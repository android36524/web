package com.boco.irms.app.dm.gridbo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.topo.dm.utils.SortManager;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 *  光缆系统接头盒明细
 * @author wangqin
 *
 */
public class FiberJointBoxExportInfoGridBO extends WireSystemDirctionInfoGridBO {
	
	@SuppressWarnings("rawtypes")
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String cuid = param.getCfgParams().get("cuid");
		String wireSystemCuid = cuid.split(",")[0];
		Integer no = Integer.valueOf(cuid.split(",")[1].toString());
		DataObjectList resultList = getAllDataList(wireSystemCuid);
		Map<Object, Object> map = new HashMap<Object, Object>();
		if(resultList != null && resultList.size() > 0){
			for(GenericDO gdo : resultList){
				map.put(gdo.getAttrValue("NO"), gdo.getAttrValue("DIRCTION"));
			}
		}
		String dirction = map.get(no).toString();
		
		DataObjectList exportList = new DataObjectList();
		if(wireSystemCuid!=null && !"".equals(wireSystemCuid)){
			try {
				exportList = getAllExDataList(wireSystemCuid,dirction);//要导出的列表数据
			} catch (Exception e) {
				LogHome.getLog().error("光缆系统路由明细查询失败", e);
			}
		}
		//列赋值
		List list = new ArrayList();
		PageResult pageResult = new PageResult(list, 1,1, 1);
		if (exportList != null && exportList.size() > 0) {
			for (int i = 0; i < exportList.size(); i++) {
				GenericDO gdo = exportList.get(i);
				Date date = gdo.getLastModifyTime();//AttrDate("LAST_MODIFY_TIME");
				if(date != null){
					Timestamp time = new Timestamp(date.getTime());
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
					String lastModifyTime = df.format(time);
					gdo.setAttrValue("LAST_MODIFY_TIME", lastModifyTime);
				}
				Map allAttr = gdo.getAllAttr();
				list.add(allAttr);
			}
			pageResult = new PageResult(list,1,1,1);
		}
		return pageResult;
	}
	
	private DataObjectList getAllExDataList(String wireSystemCuid, String dirction) {
		DataObjectList exportList = new DataObjectList();//要导出的列表数据
		try {
			Map<String,String> cuidToNameMap = new HashMap<String,String>();
			if(wireSystemCuid!=null && !"".equals(wireSystemCuid)){
				DataObjectList wireSegList = getWireSegsListByCuid(wireSystemCuid);//排序后的光缆段list
			    if(wireSegList != null && wireSegList.size() > 0){
			    	List<String> cuidList = new ArrayList<String>();//所有起止点
			    	for(int i = 0; i < wireSegList.size(); i++){
			    		GenericDO wireSeg = wireSegList.get(i);
			    		 String origPointCuid = wireSeg.getAttrString(WireSeg.AttrName.origPointCuid);
			    		 String destPointCuid = wireSeg.getAttrString(WireSeg.AttrName.destPointCuid);
			    		 if(!cuidList.contains(origPointCuid)){
			    			 if(origPointCuid != null && !origPointCuid.equals("")){
						    	 GenericDO origPoint =  getDuctManagerBO().getObjByCuid(new BoActionContext(), origPointCuid);
						    	 if(origPoint != null){
						    		 String origPointName = origPoint.getAttrString(Manhle.AttrName.labelCn);
						    		 cuidToNameMap.put(origPointName, origPointCuid);
						    	 }
						     }
			    			 cuidList.add(origPointCuid);
			    		 }
					     if(!cuidList.contains(destPointCuid)){
					    	 if(destPointCuid != null && destPointCuid.trim().length() > 0){
						    	 GenericDO destPoint =  getDuctManagerBO().getObjByCuid(new BoActionContext(), destPointCuid);
						    	 if(destPoint != null){
						    		 String destPointName = destPoint.getAttrString(Manhle.AttrName.labelCn);
						    		 cuidToNameMap.put(destPointName, destPointCuid);
						    	 }
						     }
					     }
					     cuidList.add(destPointCuid);
			    	}
					List<String> resultPoint = new ArrayList<String>();//分支点
		        	//分支点
	    			if(cuidList != null && cuidList.size() > 0){
	    				for(int i = 0; i < cuidList.size(); i++){
	    					String pointCuid = cuidList.get(i);
	    					if(!resultPoint.contains(pointCuid)){
	    						DataObjectList resultList = getResultListByLists(wireSegList, pointCuid);
	    						if(resultList != null && resultList.size() == 3){
	    							resultPoint.add(pointCuid);
	    						}
	    					}
	    				}
	    			}
	    			List<String> nodeList = new ArrayList<String>();
	    			DataObjectList wireSegsList = new DataObjectList();
	    			if(resultPoint != null && resultPoint.size() == 0){
	    				if(wireSegList != null && wireSegList.size() > 0){
	    					SortManager sm = new SortManager(wireSegList);
	    		            sm.sort();
	    		            List nodeAllList = sm.getAllNodeList();//存放所有起止点（无重复）
	    		            if(nodeAllList != null && nodeAllList.size() > 0){
	    		            	String origCuid = wireSegList.get(0).getAttrString(WireSeg.AttrName.origPointCuid);
	    		            	String destCuid = wireSegList.get(0).getAttrString(WireSeg.AttrName.destPointCuid);
	    		            	String firstCuid = (String) nodeAllList.get(0);
	    		            	if(firstCuid.equals(origCuid) || firstCuid.equals(destCuid)){
	    		            		nodeList.addAll(nodeAllList);
	    		            	}else{
	    		            		for(int i = nodeAllList.size()-1; i >=0 ; i--){
	    		                		nodeList.add((String) nodeAllList.get(i));
	    		                	}
	    		            	}
	    		            }
	    		            wireSegsList.addAll(wireSegList);
	    				}
	    			}else if(resultPoint != null && resultPoint.size() == 1){
	    				String overPoCuid = resultPoint.get(0);
	    				String firstPointName = dirction.split("->")[0];
						String secondPointName = dirction.split("->")[1];
						
						String beginPoCuid = cuidToNameMap.get(firstPointName);//方向起
						List<String> pointList = new ArrayList<String>();//存放经过点
						Map beginMap = getPointsByCuid(beginPoCuid,wireSegList,overPoCuid, pointList);
						List beginPsList = (List) beginMap.get("points");
						DataObjectList beginSegs = (DataObjectList) beginMap.get("segs");
						
						String endPoCuid = cuidToNameMap.get(secondPointName);//方向止
						List<String> endpoList = new ArrayList<String>();//止方向存放经过点
						Map endMap = getPointsByCuid(endPoCuid,wireSegList,overPoCuid, endpoList);
						List endPsList = (List) endMap.get("points");
						DataObjectList endSegs = (DataObjectList) endMap.get("segs");
						//经过点
						if(beginPsList != null && beginPsList.size() > 0){
							nodeList.addAll(beginPsList);
						}
						nodeList.add(overPoCuid);
						if(endPsList != null && endPsList.size() > 0){
							for(int i = endPsList.size()-1; i >=0 ; i--){
		                		nodeList.add((String) endPsList.get(i));
		                	}
						}
						//经过段
						if(beginSegs != null && beginSegs.size() > 0){
							wireSegsList.addAll(beginSegs);
						}
						if(endSegs != null && endSegs.size() > 0){
							for(int i = endSegs.size()-1; i >=0 ; i--){
								wireSegsList.add(endSegs.get(i));
		                	}
						}
	    			}
	    			//调用的计算结果方法
		            exportList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.getFiberBoxExportList",new BoActionContext(),wireSegsList,wireSystemCuid,nodeList);
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHome.getLog().error("查询数据失败",e);
		}
		return exportList;
	}

	private Map getPointsByCuid(String startPoCuid,DataObjectList wireSegList, String overPoCuid, List<String> pointList) throws Exception {
        Map pointSegsMap = new HashMap();
		DataObjectList wireSegsList = new DataObjectList();
		searchRouteFromLink(wireSegsList,pointList, startPoCuid, wireSegList,overPoCuid);
		pointSegsMap.put("segs", wireSegsList);
		pointSegsMap.put("points", pointList);
        return pointSegsMap;
	}
	
	private void searchRouteFromLink(DataObjectList wireSegsList, List<String> pointList,String startPoCuid, DataObjectList wireSegList, String overPoCuid) throws Exception {
		String portCuid = "";
		if(startPoCuid.equals(overPoCuid)){
			return;
		}
		if(startPoCuid != null && !startPoCuid.equals("")){
			if(!pointList.contains(startPoCuid)){
				pointList.add(startPoCuid);
				if(wireSegList != null && wireSegList.size() > 0){
					//找点的顺序  一起点为顺序找到段，
					String segCuids = "";
					for (GenericDO gdo : wireSegList) {
						String segCuid = gdo.getCuid();
						if(segCuid != null && !segCuid.equals("")){
							segCuids = segCuids + "'" + segCuid + "',";
 						}
					}
					segCuids = segCuids.substring(0,segCuids.length()-1);
					
					String sql = "(" + WireSeg.AttrName.origPointCuid + " ='" + startPoCuid + "' or " +  WireSeg.AttrName.destPointCuid + " ='" 
					              + startPoCuid + "') and " + WireSeg.AttrName.cuid + " in (" + segCuids + ")";
					
					DataObjectList segList = getDuctManagerBO().getObjectsBySql(sql, new WireSeg());
					if(segList != null && segList.size() > 0){
						GenericDO gdo = segList.get(0);
						String oPCuid = gdo.getAttrString(WireSeg.AttrName.origPointCuid);
						String dPCuid = gdo.getAttrString(WireSeg.AttrName.destPointCuid);
						if(oPCuid.equals(startPoCuid)){//起点
							portCuid = dPCuid;
						}else if(dPCuid.equals(startPoCuid)){
							portCuid = oPCuid;
						}
					}
					for(GenericDO gdo : wireSegList){
						String cuid = gdo.getCuid();
						if(cuid != null && cuid.equals(segList.get(0).getCuid())){
							wireSegList.remove(gdo);
							wireSegsList.add(gdo);
							break;
						}
					}
					searchRouteFromLink(wireSegsList, pointList, portCuid, wireSegList, overPoCuid);
				}
			}
		}else{
			return;
		}
	}
	
    private DataObjectList getResultListByLists(DataObjectList wireSegList,String pointCuid) {
		DataObjectList resultList = new DataObjectList();
		if (wireSegList != null && wireSegList.size() > 0) {
			for (GenericDO gdo : wireSegList) {
				String origPointCuid = gdo.getAttrString(WireSeg.AttrName.origPointCuid);
			    String destPointCuid = gdo.getAttrString(WireSeg.AttrName.destPointCuid);
				if (origPointCuid.equals(pointCuid) || destPointCuid.equals(pointCuid)) {
					resultList.add(gdo);
				}
			}
		}
		return resultList;
	}
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
}
