package com.boco.irms.app.dm.gridbo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.topo.ITopoMapBO;

public class FiberPropTemplateBO extends PointPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		String method = editorMeta.getRemoteMethod("query");
		String className=editorMeta.getClassName();
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
	
		List<Map> results = JSON.parseArray(editorMeta.getParas(),Map.class);
		String cuid = (String) results.get(0).get("CUID");
		if(cuid != null){
			try {
				BoActionContext boaction = new BoActionContext();
				boaction.setUserId("SYS_USER-0");
				Fiber fiber = (Fiber)BoCmdFactory.getInstance().execBoCmd(method, boaction,cuid);
				DataObjectList fiberList = new DataObjectList();
				fiberList.add(fiber);
				
				fiberList = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class).getDboCuidObjs(null,fiberList, new String[] {Fiber.AttrName.origPointCuid,Fiber.AttrName.destPointCuid});
				
				List<Map> list = new ArrayList<Map>();
				
				if(fiber != null ){	
					fiber =(Fiber) fiberList.get(0);
					fiber.setAttrValue("OBJECTID", fiber.getObjectNum());
					fiber.setAttrValue("CREATE_TIME", fiber.getCreateTime());
					fiber.setAttrValue("LAST_MODIFY_TIME", fiber.getLastModifyTime());
					
					Map map = new HashMap();
					for(String columnName : fiber.getAllAttrNames()){
						Object value = fiber.getAttrValue(columnName);
						map.put(columnName, convertObject(columnName,value));
					}
					list.add(map);
				}		
				PageResult result = new PageResult(list, 1, 1, 1);
				editorMeta.setResult(JSON.toJSONString(result.getElements()));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}        
		}
		return editorMeta;
	}
	
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)throws UserException {
	    BoActionContext actionContext = ActionContextUtil.getActionContext();
        logger.debug("context:::insert:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();		
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			if(StringUtils.isEmpty(dbo.getAttrValue("WIRE_NO"))){
				Object value  = keyValueMap.get(NO_BATCH);
				JSONObject parentNode = (JSONObject)keyValueMap.get("PARENTNODEID");
				if(parentNode == null){
					throw new UserException("为获取到所属系统CUID");
				}
				String parentNodeid = String.valueOf(parentNode.get("value"));
				keyValueMap.remove("PARENTNODEID");
				String[] cuid = parentNodeid.split("@");
				String relatedSystemCuid = null;
				String relatedSegCuid = "";
				if(cuid.length > 0){
					if(cuid.length > 1){
						relatedSystemCuid = cuid[0];
						relatedSegCuid = cuid[1];
					}else{
						throw new UserException("需要先建立光缆段，在建立纤芯");
					}
				}
				/*String relatedSystemCuid = parentNodeid.split("@")[0];
				String relatedSegCuid = parentNodeid.split("@")[1];*/
				keyValueMap.put("RELATED_SYSTEM_CUID", relatedSystemCuid);
				WireSeg segdata = new WireSeg();
				WireSystem wiresystem = new WireSystem();
				int num = Integer.parseInt(WebDMUtils.convertJosn2Object(NO_BATCH,value).toString());
				int maxno = getMaxWireNo(relatedSegCuid);
				try {
					segdata = (WireSeg) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",actionContext, relatedSegCuid);
					
					//修改光缆段纤芯数
					long fiberCount = segdata.getFiberCount();
					segdata.setFiberCount(num+fiberCount);
					DataObjectList segList = new DataObjectList();	
					segList.add(segdata);
					BoCmdFactory.getInstance().execBoCmd("IWireSegBO.modifyWireSegs",actionContext,segList);
					
					wiresystem = (WireSystem) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",actionContext, relatedSystemCuid);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}

				for(int i=1;i<=num;i++){
					GenericDO newDbo=(GenericDO) dbo.deepClone();
					Long wireNo = new Long(maxno + i);
					newDbo.setAttrValue(Fiber.AttrName.labelCn, wireNo.toString());
					
					newDbo.setCuid();
					newDbo.setAttrValue(Fiber.AttrName.relatedSystemCuid, segdata.getRelatedSystemCuid());
					newDbo.setAttrValue(Fiber.AttrName.fiberLevel, wiresystem.getSystemLevel());
					newDbo.setAttrValue(Fiber.AttrName.ownership, segdata.getOwnership());
					newDbo.setAttrValue(Fiber.AttrName.relatedSegCuid, segdata.getCuid());
					newDbo.setAttrValue(Fiber.AttrName.destSiteCuid, segdata.getDestPointCuid());
					newDbo.setAttrValue(Fiber.AttrName.origSiteCuid, segdata.getOrigPointCuid());
					newDbo.setAttrValue(Fiber.AttrName.destEqpCuid, segdata.getDestPointCuid());
					newDbo.setAttrValue(Fiber.AttrName.origEqpCuid, segdata.getOrigPointCuid());
					newDbo.setAttrValue(Fiber.AttrName.destPointCuid, "");
					newDbo.setAttrValue(Fiber.AttrName.origPointCuid, "");
					newDbo.setAttrValue(Fiber.AttrName.wireNo, wireNo);
					newDbo.setAttrValue(Fiber.AttrName.length, segdata.getLength()); // 长度
					long icolor = (Color.WHITE).getRGB();
					if(dbo.getAttrLong("FIBER_COLOR")!=0){
						icolor = dbo.getAttrLong("FIBER_COLOR");
					}
					newDbo.setAttrValue(Fiber.AttrName.fiberColor, icolor); // 加颜色
					list.add(newDbo);
				}
			}else{
				dbo.setCuid();
				list.add(dbo);
			}
			try {
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);
			} catch (Exception e) {
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	protected int getMaxWireNo(String wiresegcuid){
		int maxno = 0;
		String sql = "select max(" + Fiber.AttrName.wireNo + ") from " + Fiber.CLASS_NAME + " where " + Fiber.AttrName.relatedSegCuid + " ='" + wiresegcuid + "'";
		try {
			Long lobj = (Long) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFiberMaxWireNo, new BoActionContext(), sql);
			maxno = lobj.intValue();// 光缆段中 纤芯最大编号
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return maxno;
	}
}
