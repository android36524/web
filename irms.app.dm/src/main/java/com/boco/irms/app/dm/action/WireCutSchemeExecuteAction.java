package com.boco.irms.app.dm.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.gis.rest.DmDesignerTools;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.CutoverTask;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.ICutoverSchemeBO;
import com.boco.transnms.server.bo.ibo.dm.ICutoverTaskBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.ITempCutoverWireSegBO;


@Controller()
@Service("WireCutSchemeExecuteAction")
@RequestMapping("/WireCutSchemeExecuteAction")
public class WireCutSchemeExecuteAction {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/doGetWireFinishCut/{cuid}", method = RequestMethod.GET)
	public void doGetWireFinishCut(@PathVariable String cuid,HttpServletRequest request,HttpServletResponse response){
		response.setCharacterEncoding("UTF-8");
		DmDesignerTools.setActionContext(new ServiceActionContext(request));
		BoActionContext context = DmDesignerTools.getActionContext();
		
		try{
				GenericDO dbo = getDuctManagerBO().getObjByCuid(context, cuid);
//				network.pushInteractionMode(new InteractionMode(new InputHandler[]{}),true);
				//执行割接更新相关信息
				//1.执行割接的条件判断，光缆设备不能为空，是成端割接。状态为设计好的任务。
				if(dbo!=null && dbo instanceof CutoverTask){
					//CutoverTask cutoverTask = (CutoverTask)dbo;
					CutoverTask cutoverTask = (CutoverTask)getDuctManagerBO().getObjByCuid(context, dbo.getCuid());
					long state = (Long) cutoverTask.getAttrValue(CutoverTask.AttrName.cutoverState);
					if(cutoverTask.getAttrValue(CutoverTask.AttrName.relatedWireSegCuid)==null ||cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid)==null ){
						response.getWriter().print("{\"s\":\"割接的光缆段和割接设备不存在！\"}");
						return;
					}
					if(DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign !=state){//不是设计好的方案
						response.getWriter().print("{\"s\":\"请选择设计好的方案执行割接!\"}");
						return;
					}
					String result =executeWireCutScheme(context,cutoverTask);
					response.getWriter().print("{\"s\":\""+result+"\"}");
					return;
				}
		}catch(Throwable ex){
			ex.printStackTrace();
			logger.info("割接任务执行失败!",ex);
		}
	}
	
	 private String executeWireCutScheme(BoActionContext actionContext,CutoverTask cutoverTask){
		 	try {
		 		long type = cutoverTask.getCutoverType();
		 		if(type==1){   //成端割接
		 			DataObjectList fiberCutList = getCutoverSchemeBO().getCutOverSchemeByTaskCuid(new BoQueryContext(), cutoverTask.getCuid());
			 		if(fiberCutList==null || fiberCutList.size()==0){
			 			return "未设计完成，不可进行执行割接!";
			 		}
			 		cutoverTask = getCutoverTaskBO().executeCutoverTask(actionContext,cutoverTask,fiberCutList);
		 		}else{   //中间光缆割接
		 			//判断是否有临时光缆段存在，不存在则设计未完成，不可进行执行割接
		 			DataObjectList tempWireSegs= getTempCutoverWireSegBO().getTempCutoverWireSegByTaskCuid(new BoQueryContext(), cutoverTask.getCuid());
		 			if(tempWireSegs==null || tempWireSegs.size()==0){//存在临时光缆段，设计完成。
		 				return "未设计完成，不可进行执行割接!";
		 			}
		 			//执行割接：
		 			cutoverTask = getCutoverTaskBO().executeWireSegCutoverTask(actionContext, cutoverTask, tempWireSegs);
		 			String relatedDeviceCuid=cutoverTask.getRelatedDeviceCuid().trim();
		 			String deviceStr=new String();
		 			if(relatedDeviceCuid==null || relatedDeviceCuid.equals("")){
		 				deviceStr="";
		 			}
		 			else if(relatedDeviceCuid.length()>0 && relatedDeviceCuid.contains(",")){
		 				String[] relatedDevices=relatedDeviceCuid.split(",");
		 				List list=getDuctManagerBO().getObjsByCuid(actionContext, relatedDevices);
		 				for(int i=0;i<list.size();i++){
		 					GenericDO dbo=(GenericDO)list.get(i);
		 					deviceStr+=dbo.getAttrString(FiberJointBox.AttrName.labelCn)+",";
		 				}
		 				deviceStr=deviceStr.substring(0,deviceStr.length()-1);
		 			}else if(relatedDeviceCuid.length()>0 && !relatedDeviceCuid.contains(",")){
		 				GenericDO gdo=getDuctManagerBO().getObjByCuid(actionContext, relatedDeviceCuid);
		 				deviceStr+=gdo.getAttrString(FiberJointBox.AttrName.labelCn);
		 			}
		 			
		 			cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, deviceStr);		 			
		 		}
		 		
		 		long state =(Long) cutoverTask.getAttrValue(CutoverTask.AttrName.cutoverState);
		 		if(DuctEnum.WIRE_CUT_STATE_ENUM._cutsuccess==state){
		 			return "割接成功!";
		 		}else{
		 			return "割接失败!";
		 		}
			} catch (Exception e) {
				logger.info("割接任务执行失败!",e);
				e.printStackTrace();
			}
			return null;
	 }
	 
	 private IDuctManagerBO getDuctManagerBO() {
			return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		}
	 
	 private ICutoverTaskBO getCutoverTaskBO() {
			return BoHomeFactory.getInstance().getBO(ICutoverTaskBO.class);
		}
	 
	 private ICutoverSchemeBO getCutoverSchemeBO() {
			return BoHomeFactory.getInstance().getBO(ICutoverSchemeBO.class);
		}
	 
	 private ITempCutoverWireSegBO getTempCutoverWireSegBO() {
			return BoHomeFactory.getInstance().getBO(ITempCutoverWireSegBO.class);
		}
}
