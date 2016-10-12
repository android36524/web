package com.boco.irms.app.dm.action;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.OpticalCheckTask;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IOpticalCheckBO;

public class CheckTaskNameAction {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String lookTaskNameIsHave(String taskName,Map list) throws UserException{	
		String result="true";
		try{
			Boolean bool=(Boolean) BoCmdFactory.getInstance().execBoCmd("IOpticalCheckBO.lookTaskNameIsHave",taskName);
			if(false==bool){
				GenericDO dbo = new GenericDO();
	    		int num=list.get("CUID").toString().indexOf("-");
	    		String classname=list.get("CUID").toString().substring(0,num);
	    		dbo.setClassName(classname);
	    		dbo = dbo.createInstanceByClassName();
	    		dbo.setObjectNum(Long.parseLong((String) list.get("OBJECTID")));
	    		dbo.setAttrValue("CUID", list.get("CUID"));
	    		DataObjectList allFiberList = new DataObjectList();
	    		if(classname.equals(WireSystem.CLASS_NAME)){
	    			WireSystem wireSystem = (WireSystem) dbo;
	    			DataObjectList fiberlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFibersByWireSystem,new BoActionContext(), wireSystem);
					for (int j = 0; j < fiberlist.size(); j++) {
						Fiber fiber = (Fiber) fiberlist.get(j);
						long usageState = fiber.getUsageState();
						//如果为坏纤或者不可用纤，那么就不做处理
						if (usageState != DuctEnum.DMSUSAGESTATE._bad && usageState != DuctEnum.DMSUSAGESTATE._notuse) {
							allFiberList.add(fiber);
						}
					}
	    		} else if (classname.equals(Fiber.CLASS_NAME)) {
					Fiber fiber = (Fiber) dbo;
					long usageState = fiber.getUsageState();
					//如果为坏纤或者不可用纤，那么就不做处理
					if (usageState != DuctEnum.DMSUSAGESTATE._bad && usageState != DuctEnum.DMSUSAGESTATE._notuse) {
						allFiberList.add(fiber);
					}
				} else if (classname.equals(WireSeg.CLASS_NAME)) {
					WireSeg wireSeg = (WireSeg) dbo;
					DataObjectList fiberlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFibersByWireSeg,new BoActionContext(), wireSeg);
					for (int j = 0; j < fiberlist.size(); j++) {
						Fiber fiber = (Fiber) fiberlist.get(j);
						long usageState = fiber.getUsageState();
						//如果为坏纤或者不可用纤，那么就不做处理
						if (usageState != DuctEnum.DMSUSAGESTATE._bad && usageState != DuctEnum.DMSUSAGESTATE._notuse) {
							allFiberList.add(fiber);
						}
					}
				}
	    		OpticalCheckTask oct = new OpticalCheckTask();
				String districtCuid = (String)list.get("RELATED_DISTRICT_CUID");
				if(districtCuid == null)
					districtCuid = "DISTRICT-00001";
				oct.setAttrValue(OpticalCheckTask.AttrName.createUser,"ADMIN");
				oct.setAttrValue(OpticalCheckTask.AttrName.taskName, taskName);// 任务名称
				oct.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 0L);
				oct.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle, 1L);// 纤芯
																				// 光缆段
																				// 光缆
																				// 光纤
				oct.setAttrValue(OpticalCheckTask.AttrName.taskStartWay, 1L);// 立即延时
				oct.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue, 1L);// 激活挂起
				oct.setAttrValue(OpticalCheckTask.AttrName.taskState, 2L);// 立即
				oct.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid, districtCuid);// 默认区域
				try {
					IOpticalCheckBO checkBo = (IOpticalCheckBO) BoHomeFactory.getInstance().getBO(IOpticalCheckBO.class);
					checkBo.insertDataNowDo(oct);
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new UserException(e.getMessage());
				}
				BoCmdFactory.getInstance().execBoCmd("IMakeOpticalFiberBO.setOpticalCheckOptimization",new BoActionContext(),allFiberList,oct);
				result="核查结束";
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			throw new UserException(e.getMessage());
		}
		return result;
	}
}
