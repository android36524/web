package com.boco.irms.app.dm.gridbo;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 *  光缆系统路由明细
 * @author wangqin
 *
 */
public class WireRouteExportInfoGridBO extends GridTemplateProxyBO {

	@SuppressWarnings("rawtypes")
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String wireSystemCuid = param.getCfgParams().get("cuid");
		DataObjectList exportList = new DataObjectList();
		if(wireSystemCuid!=null && !"".equals(wireSystemCuid)){
			try {
				exportList = getAllDataList(wireSystemCuid);//要导出的列表数据
			} catch (Exception e) {
				LogHome.getLog().error("光缆系统路由明细查询失败", e);
			}
		}
		//列赋值
		List list = new ArrayList();
		PageResult pageResult = new PageResult(list, 1,1, 1);
		DataObjectList resultExportList = new DataObjectList();
		if(exportList != null && exportList.size() > 0){
			DataObjectList countList = new DataObjectList();
			GenericDO gdo = new GenericDO();
			double length = 0L;
			for(GenericDO dgo : exportList){
				Date date = dgo.getLastModifyTime();//AttrDate("LAST_MODIFY_TIME");
				if(date != null){
					Timestamp time = new Timestamp(date.getTime());
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
					String lastModifyTime = df.format(time);
					dgo.setAttrValue("LAST_MODIFY_TIME", lastModifyTime);
				}
				if(dgo.getAttrValue("LENGTH") != null){
					length = length + Double.parseDouble(dgo.getAttrValue("LENGTH").toString());
				}
			}
			DecimalFormat formater = new DecimalFormat("#0.##");
			String len = formater.format(length);
			length = Double.parseDouble(len);
			
			gdo.setAttrValue("NO", "总计");
			gdo.setAttrValue("LENGTH", length);
			countList.add(gdo);
			resultExportList.addAll(exportList);
			resultExportList.addAll(countList);

		}
		if (resultExportList != null && resultExportList.size() > 0) {
			for (int i = 0; i < resultExportList.size(); i++) {
				GenericDO gdo = resultExportList.get(i);
				Map allAttr = gdo.getAllAttr();
				list.add(allAttr);
			}
			pageResult = new PageResult(list,1,1,1);
		}
		
		return pageResult;
	}

	private DataObjectList getAllDataList(String wireSystemCuid) {
		DataObjectList exportList = new DataObjectList();//要导出的列表数据
		try {
			//获取光缆下  光缆段 并  排序 
//			DataObjectList wireSegsList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IWireSegBO.getWireSegsByWireSystemCuid",new BoActionContext(),wireSystemCuid); 
			Map map = (Map) BoCmdFactory.getInstance().execBoCmd(
		            DuctManagerBOHelper.ActionName.getSegsAndPointsBySystemCuid, new BoActionContext(), wireSystemCuid);
	        DataObjectList wireSegLists = (DataObjectList) map.get("segsList");
			SortManager sortManager=new SortManager(wireSegLists);
			sortManager.sort();
			List<DataObjectList> list=sortManager.getLinkList();
			DataObjectList wireSegList=new DataObjectList();//排序后的光缆段list
			if(list != null && list.size() > 0){
				for(DataObjectList dol:list){
					GenericDO dto=dol.get(0);
			    	boolean dirction=sortManager.getDirection(dto.getCuid());
			    	if(!dirction){
			        	Collections.reverse(dol);	
			    	}
			    	wireSegList.addAll(dol);
			    }
			}
			List<String> allPointList = new ArrayList<String>();//所有起止点
			if(wireSegList != null && wireSegList.size() > 0){
				for(GenericDO gdo : wireSegList){
					String origPointCuid = gdo.getAttrString(WireSeg.AttrName.origPointCuid);
					String destPointCuid = gdo.getAttrString(WireSeg.AttrName.destPointCuid);
					if(!allPointList.contains(origPointCuid)){
						allPointList.add(origPointCuid);
					}
					if(!allPointList.contains(destPointCuid)){
						allPointList.add(destPointCuid);
					}
				}
				List<String> resultPoint = new ArrayList<String>();//独立点
    			if(allPointList != null && allPointList.size() > 0){
    				for(int i = 0; i < allPointList.size(); i++){
    					String pointCuid = allPointList.get(i);
    					if(!resultPoint.contains(pointCuid)){
    						DataObjectList resultList = getResultListByLists(wireSegList, pointCuid);
    						if(resultList != null && resultList.size() == 1){
    							resultPoint.add(pointCuid);
    						}
    					}
    				}
    			}
    			List<String> barchPoint = new ArrayList<String>();//分支点
	        	//分支点
    			if(allPointList != null && allPointList.size() > 0){
    				for(int i = 0; i < allPointList.size(); i++){
    					String pointCuid = allPointList.get(i);
    					if(!barchPoint.contains(pointCuid)){
    						DataObjectList resultList = getResultListByLists(wireSegList, pointCuid);
    						if(resultList != null && resultList.size() == 3){
    							barchPoint.add(pointCuid);
    						}
    					}
    				}
    			}
    			List<String> nodeList = new ArrayList<String>();
    			DataObjectList wireSegsList = new DataObjectList();
				if((resultPoint != null && resultPoint.size() == 3) && (barchPoint != null && barchPoint.size() == 1)){
					String overPoCuid = barchPoint.get(0);
					
					String firstPoCuid = resultPoint.get(0);
					String secondPoCuid = resultPoint.get(1);
					String thirdPoCuid = resultPoint.get(2);
					List<String> pointList = new ArrayList<String>();//存放经过点
					Map firstMap = getPointsByCuid(firstPoCuid,wireSegList,overPoCuid, pointList);
					List firstPsList = (List) firstMap.get("points");
					DataObjectList firstSegs = (DataObjectList) firstMap.get("segs");
					
					List<String> bPointList = new ArrayList<String>();//中方向存放经过点
					Map secondMap = getPointsByCuid(secondPoCuid,wireSegList,overPoCuid, bPointList);
					List secondPsList = (List) secondMap.get("points");
					DataObjectList secondSegs = (DataObjectList) secondMap.get("segs"); 
					
					List<String> cPointList = new ArrayList<String>();//止方向存放经过点
					Map thirdMap = getPointsByCuid(thirdPoCuid,wireSegList,overPoCuid, cPointList);
					List thirdPsList = (List) thirdMap.get("points");
					DataObjectList thirdSegs = (DataObjectList) thirdMap.get("segs");
					//经过点
					if(firstPsList != null && firstPsList.size() > 0){
						nodeList.addAll(firstPsList);
					}
					nodeList.add(overPoCuid);
					if(secondPsList != null && secondPsList.size() > 0){
						for(int i = secondPsList.size()-1; i >=0 ; i--){
	                		nodeList.add((String) secondPsList.get(i));
	                	}
					}
					if(thirdPsList != null && thirdPsList.size() > 0){
						for(int i = thirdPsList.size()-1; i >=0 ; i--){
	                		nodeList.add((String) thirdPsList.get(i));
	                	}
					}
					//经过段
					if(firstSegs != null && firstSegs.size() > 0){
						wireSegsList.addAll(firstSegs);
					}
					if(secondSegs != null && secondSegs.size() > 0){
						for(int i = secondSegs.size()-1; i >=0 ; i--){
							wireSegsList.add(secondSegs.get(i));
	                	}
					}
					if(thirdSegs != null && thirdSegs.size() > 0){
						for(int i = thirdSegs.size()-1; i >=0 ; i--){
							wireSegsList.add(thirdSegs.get(i));
	                	}
					}
				}else if(resultPoint != null && resultPoint.size() == 2){
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
				exportList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.getRouteExportList",new BoActionContext(),wireSegsList,nodeList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHome.getLog().error("查询数据失败",e);
		}
		
		return exportList;
	}
	
    /**
     * 找到以pointCuid为端点的光缆段
     * @param wireSegList
     * @param pointCuid
     * @return
     */
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
    private Map getPointsByCuid(String startPoCuid,DataObjectList wireSegList, String overPoCuid, List<String> pointList) throws Exception {
    	Map pointSegsMap = new HashMap();
		DataObjectList wireSegsList = new DataObjectList();
    	searchRouteFromLink(wireSegsList,pointList, startPoCuid, wireSegList,overPoCuid);
    	pointSegsMap.put("segs", wireSegsList);
		pointSegsMap.put("points", pointList);
        return pointSegsMap;
	}
	
	private void searchRouteFromLink(DataObjectList wireSegsList, List<String> pointList, String startPoCuid, DataObjectList wireSegList, String overPoCuid) throws Exception {
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
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
}
