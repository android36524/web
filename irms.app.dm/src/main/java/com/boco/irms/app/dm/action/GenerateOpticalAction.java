package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IMakeOpticalFiberBO;

public class GenerateOpticalAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/*
	 * WireSystem，光缆系统，生成光纤
	 */
	public boolean doDealOptical(Map list) throws UserException{
		GenericDO dbo = new GenericDO();
		int num=list.get("CUID").toString().indexOf("-");
		String classname=list.get("CUID").toString().substring(0,num);
		dbo.setClassName(classname);
		dbo = dbo.createInstanceByClassName();
		dbo.setObjectNum(Long.parseLong((String) list.get("OBJECTID")));
		dbo.setAttrValue("CUID", list.get("CUID"));
		DataObjectList fiberList = new DataObjectList();
		StringBuffer sb = new StringBuffer();
		if (classname.equals(Fiber.CLASS_NAME)) {
			Fiber fiber = (Fiber) dbo;
				if(!fiberList.getCuidList().contains(dbo.getCuid())){
					fiberList.add(dbo);
				}
		} else if (classname.equals(WireSeg.CLASS_NAME)) {
			WireSeg wireSeg = (WireSeg) dbo;
			DataObjectList fiberlist = null;
			try {
				fiberlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFibersByWireSeg, new BoActionContext(), wireSeg);
			} catch (Exception e) {
				logger.error("生成光纤失败",e);
				throw new UserException("生成光纤失败");
			}
			for (int j = 0; j < fiberlist.size(); j++) {
				Fiber fiber = (Fiber) fiberlist.get(j);
				if(!fiberList.getCuidList().contains(fiber.getCuid())){
					fiberList.add(fiber);
				}
			}
		}else if(classname.equals(WireSystem.CLASS_NAME)) {
			WireSystem wiresystem = (WireSystem)dbo;
			String relatedWireSystemCuid = wiresystem.getCuid();
			sb.append(" RELATED_SYSTEM_CUID='");
			sb.append(relatedWireSystemCuid);
			sb.append("' OR ");
		}
		String sql = sb.toString().substring(0,sb.toString().length()-3);
		boolean bool=false;
		if(classname.equals(WireSystem.CLASS_NAME)){
			getMakeOpticalFiberBO().dealOptical(new BoActionContext(),dbo.getCuid());
			bool=true;
		}else{
			getMakeOpticalFiberBO().dealOptical(new BoActionContext(),fiberList);
			bool=true;
		}
		return bool;
	}
	
	private IMakeOpticalFiberBO getMakeOpticalFiberBO(){
		return BoHomeFactory.getInstance().getBO(IMakeOpticalFiberBO.class);
	}
	
	/*
	 * inter_wire，楼内管理
	 */
	public void doBuildOptical(Map[] maps) throws UserException{
		DataObjectList dol=new DataObjectList(); 
		GenericDO gdo=new GenericDO();
		gdo.setObjectNum(Long.parseLong((String) maps[0].get("OBJECTID")));
		gdo.setAttrValue("CUID",maps[0].get("CUID"));
		gdo.setAttrValue("LABEL_CN",maps[0].get("LABEL_CN"));
		dol.add(gdo);
		try{
			BoCmdFactory.getInstance().execBoCmd("IOpticalCheckBO.addOpticalCheckTaskBySystem", new BoActionContext(),dol,new Long(3),"光纤生成-楼内光缆"+maps[0].get("LABEL_CN"));
		}catch(Exception e){
			logger.error("楼内光缆管理生成光纤失败",e);
			throw new UserException("楼内光缆管理生成光纤失败");
		}
    }
	
	/*
	 * OpticalSystem,光线管理，重新生成光纤
	 */
	public void doRegenerateOpticals(String cuids) throws UserException{
		String[] tempStringArray=cuids.split("&&");
		List opticalCuids=new ArrayList();
		Collections.addAll(opticalCuids, tempStringArray);
		try{
			BoCmdFactory.getInstance().execBoCmd("IOpticalCheckBO.regenerateOpticals", new BoActionContext(),opticalCuids);
		}catch(Exception e){
			logger.error("重新生成光纤失败",e);
			throw new UserException("重新生成光纤失败");
		}
    }
}
