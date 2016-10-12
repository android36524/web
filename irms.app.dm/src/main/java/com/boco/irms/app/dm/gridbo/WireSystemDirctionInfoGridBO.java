package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collections;
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
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 *  光缆系统方向
 * @author wangqin
 *
 */
public class WireSystemDirctionInfoGridBO extends GridTemplateProxyBO {
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String wireSystemCuid = param.getCfgParams().get("cuid");
		DataObjectList resultLists = new DataObjectList();
		if(wireSystemCuid!=null && !"".equals(wireSystemCuid)){
			try {
				resultLists = getAllDataList(wireSystemCuid);//要导出的列表数据
			} catch (Exception e) {
				LogHome.getLog().error("光缆系统路由明细查询失败", e);
			}
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		PageResult pageResult = new PageResult(list, 1,1, 1);
		
		if (resultLists != null && resultLists.size() > 0) {
			for (int i = 0; i < resultLists.size(); i++) {
				GenericDO gdo = resultLists.get(i);
				Map allAttr = gdo.getAllAttr();
				list.add(allAttr);
			}
			pageResult = new PageResult(list,1,1,1);
		}
		return pageResult;
	}
	
	protected DataObjectList getAllDataList(String wireSystemCuid) {
		DataObjectList resultLists = new DataObjectList();
		try {
			List<String> dirctionNoteNames = new ArrayList<String>();//方向名称
			List<String> dirctionNoteCuid = new ArrayList<String>();//方向点
			List<String> allPointList = new ArrayList<String>();//所有起止点
			if (wireSystemCuid != null && !"".equals(wireSystemCuid)) {
				// 获取光缆下 光缆段 并 排序
				DataObjectList wireSegList = getWireSegsListByCuid(wireSystemCuid);
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
				}
				
	        	List<String> resultPoint = new ArrayList<String>();//独立点
	        	//独立点
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
    			dirctionNoteCuid.addAll(resultPoint);

    			if(dirctionNoteCuid != null && dirctionNoteCuid.size() >0){
    				for(int i = 0; i < dirctionNoteCuid.size(); i++){
    					String noteCuid = dirctionNoteCuid.get(i);
    					GenericDO note = getDuctManagerBO().getObjByCuid(new BoActionContext(), noteCuid);
    					if(note != null){
    						String dirctionNoteName = note.getAttrString(Manhle.AttrName.labelCn);
    						dirctionNoteNames.add(dirctionNoteName);
    					}
    				}
    			}
	        	//串接方向
				List<String> ditctionList = new ArrayList<String>();
				if (dirctionNoteNames != null && dirctionNoteNames.size() > 1) {
					for (int i = 0; i < dirctionNoteNames.size() - 1; i++) {
						for (int j = i + 1; j < dirctionNoteNames.size(); j++) {
							String ditction = dirctionNoteNames.get(i) + "->"+ dirctionNoteNames.get(j);
							ditctionList.add(ditction);
						}
					}
				}
				DataObjectList resultList = new DataObjectList();
				if (ditctionList != null && ditctionList.size() > 0) {
					for (int i = 0; i < ditctionList.size(); i++) {
						GenericDO dirct = new GenericDO();
						dirct.setAttrValue("DIRCTION", ditctionList.get(i));
						resultList.add(dirct);
					}
				}
				if (resultList != null && resultList.size() > 0) {
					// 生成序号
					int size = resultList.size(), code = 0;
					for (int i = 0; i < size; i++) {
						GenericDO obj = resultList.get(i);
						if (code < size + 1) {
							code = code + 1;
						}
						obj.setAttrValue("NO", code);
						resultLists.add(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHome.getLog().error("查询数据失败", e);
		}
		return resultLists;
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
	protected DataObjectList getWireSegsListByCuid(String wireSystemCuid) {
		DataObjectList wireSegList = new DataObjectList();//排序后的光缆段list
		try {
			//获取光缆下  光缆段 并  排序 
			Map map = (Map) BoCmdFactory.getInstance().execBoCmd(
		            DuctManagerBOHelper.ActionName.getSegsAndPointsBySystemCuid, new BoActionContext(), wireSystemCuid);
			DataObjectList wireSegLists = (DataObjectList) map.get("segsList");
			SortManager sortManager=new SortManager(wireSegLists);
			sortManager.sort();
			List<DataObjectList> lists=sortManager.getLinkList();
			
			if(lists != null && lists.size() > 0){
				for(DataObjectList dol:lists){
					GenericDO dto=dol.get(0);
					boolean dirc=sortManager.getDirection(dto.getCuid());
					if(!dirc){
			    		Collections.reverse(dol);
					}
					wireSegList.addAll(dol);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wireSegList;
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
}
