package com.boco.irms.app.dm.gridbo;

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
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.InterCable;
import com.boco.transnms.common.dto.LinePair;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;

public class InterLinePairTemplateBO extends FiberPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		String method = editorMeta.getRemoteMethod("query");
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
				LinePair linePair = (LinePair)BoCmdFactory.getInstance().execBoCmd(method, boaction,cuid);
				List<Map> list = new ArrayList<Map>();
				if(linePair != null ){
					linePair.setAttrValue("OBJECTID", linePair.getObjectNum());
					linePair.setAttrValue("CREATE_TIME", linePair.getCreateTime());
					linePair.setAttrValue("LAST_MODIFY_TIME", linePair.getLastModifyTime());
					linePair.setAttrValue("RELATED_SEG_CUID", linePair.getRelatedSegCuid());
					Map map = new HashMap();
					for(String columnName : linePair.getAllAttrNames()){
						Object value = linePair.getAttrValue(columnName);
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
	public EditorPanelMeta insert(EditorPanelMeta editorMeta) throws UserException {
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
				keyValueMap.put("RELATED_SYSTEM_CUID", parentNodeid.split("@")[0]);
				String interCableId = parentNodeid.split("@")[0];
				keyValueMap.remove("PARENTNODEID");
				InterCable segdata = new InterCable();
				try {
					segdata = (InterCable) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), interCableId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int num = Integer.parseInt(WebDMUtils.convertJosn2Object(NO_BATCH,value).toString());
				int maxno = getMaxWireNo(interCableId);

				for(int i=1;i<=num;i++){
					GenericDO newDbo=(GenericDO) dbo.deepClone();
					Long wireNo = new Long(maxno + i);
					newDbo.setAttrValue(LinePair.AttrName.labelCn, dbo.getAttrString("LABEL_CN") + wireNo.toString());
					
					newDbo.setCuid();
					newDbo.setAttrValue(LinePair.AttrName.relatedSegCuid, segdata.getCuid());
					newDbo.setAttrValue(LinePair.AttrName.wireNo, wireNo);
					newDbo.setAttrValue(LinePair.AttrName.origPointCuid, segdata.getOrigPointCuid()); 
					newDbo.setAttrValue(LinePair.AttrName.destPointCuid, segdata.getDestPointCuid()); 
					list.add(newDbo);
				}
			}else{
				dbo.setCuid();
				list.add(dbo);
			}
			BoActionContext bocontext = new BoActionContext();
			bocontext.setUserId(editorMeta.getAc().getUserCuid());
			bocontext.setUserName(editorMeta.getAc().getUserId());
			try {
				BoCmdFactory.getInstance().execBoCmd(method,bocontext,list);
			} catch (Exception e) {
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	
	@Override
	protected int getMaxWireNo(String interCableCuid){
		int maxno = 0;
		String sql = "select max(" + LinePair.AttrName.wireNo + ") from " + LinePair.CLASS_NAME + " where " + LinePair.AttrName.relatedSegCuid + " ='" + interCableCuid + "'";
		try {
			Long lobj = (Long) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFiberMaxWireNo, new BoActionContext(), sql);
			maxno = lobj.intValue();// 电缆中线对最大编号
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxno;
	}
}
