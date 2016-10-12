package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.dm.gridbo.AbstractPropTemplateBO;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 * 光接头盒设备管理
 * @author wangqin
 *
 */
public class OpticalWayAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 根据光路查询其经过的光缆段
	 * @param 
	 * @return 
	 */
	public Set<String> getWireSegCuidsByOptiWayId(String opticalWayCuid){
		String method = "IFiberBO.getWiresByOpticalWayCuid";
		DataObjectList wireSegLists = new DataObjectList();
	    
		OpticalWay opticalWay=getOpticalWayByCuid(opticalWayCuid);		  
		if(opticalWay==null){
			  return null ;
		}
		//记录段CUID
		 Set<String>  segsSet = new HashSet<String>();
		try {
			 wireSegLists = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method,new BoActionContext(), opticalWay);
			 
			 if(wireSegLists != null && wireSegLists.size() != 0){
				 //获取光缆段CUID
				 for(GenericDO gdo : wireSegLists){
					 WireSeg seg =  (WireSeg)gdo;
					 segsSet.add(seg.getCuid());
				 }
			 }
		} catch (Exception e) {
			logger.error("获取光缆段CUID失败",e);
			throw new UserException(e);
		}
		
		return segsSet;
	}

	private OpticalWay getOpticalWayByCuid(String opticalWayCuid) {
		String method = "IOpticalWayBO.getOpticalWayByCuid";
		OpticalWay opticalWay = null;
		try {
			opticalWay = (OpticalWay) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),opticalWayCuid);
		} catch (Exception e) {
			logger.error("通过cuid查询光路失败",e);
			throw new UserException("通过cuid查询光路失败");
		}
		return opticalWay;
	}
	
	/**
	 * 删除光纤、光路数据
	 * @param resList
	 * @throws UserException
	 */
	public void doDeleteOpOrOpWays(List<Map> resList) throws UserException{
		if(resList==null || resList.size()==0){
			return ;
		}
		String classId = resList.get(0).get("CUID").toString();
		String tableName = classId.split("-")[0];
		DataObjectList list=new DataObjectList();
		for(Map map : resList){
			String cuid=map.get("CUID").toString();
			String className=GenericDO.parseClassNameFromCuid(cuid);
			GenericDO gdo=WebDMUtils.createInstanceByClassName(className, map);
			list.add(gdo);
		}
		
		try {
			if(tableName.equals("OPTICAL")){
				BoCmdFactory.getInstance().execBoCmd("IOpticalBO.deleteOpticals", new BoActionContext(),list);
			}
			if(tableName.equals("OPTICAL_WAY")){
				BoCmdFactory.getInstance().execBoCmd("IOpticalWayBO.deleteOpticalWayVectors", new BoActionContext(),list);
			}
		} catch (Exception e) {
			logger.error("删除失败",e);
			throw new UserException("删除资源数据失败");
		}
	}

}
