package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.DMNameUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class OnuboxPropTemplateBO extends AbstractPropTemplateBO {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		String method = editorMeta.getRemoteMethod("query");
		String className=editorMeta.getClassName();
		className = repalceClassName(className);
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
	
		List<Map> results = JSON.parseArray(editorMeta.getParas(),Map.class);
		String cuid = (String) results.get(0).get("CUID");
		if(cuid != null){
			try {
				String sql = "CUID='"+cuid+"'";
				BoQueryContext boquery = new BoQueryContext();
				boquery.setUserId(editorMeta.getAc().getUserCuid());
				DboCollection col = (DboCollection)BoCmdFactory.getInstance().execBoCmd(method, boquery,sql);
				List<Map> list = new ArrayList<Map>();
				
				if(col != null && col.size() > 0){
					//list.add(col.getAttrField(className, 0).getObjectToMap());
					GenericDO  gdo = col.getAttrField(className, 0);				
					gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
					gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
					gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
					
					Map locationMap = new HashMap();
					sql = "SELECT cuid, label_cn from T_ROFH_FULL_ADDRESS";
					Class[] colClassType = new Class[] {String.class, String.class};
					try {
						DataObjectList dataObjectList = getDuctManagerBO().getDatasBySql(sql, colClassType);
						if(dataObjectList!=null && dataObjectList.size()>0){
							logger.info("查到的网格关联资源的记录数:"+dataObjectList.size());
							for(GenericDO locationGdo : dataObjectList){
								locationMap.put(locationGdo.getAttrString("1"), locationGdo.getAttrString("2"));
							}
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					
					Map map = new HashMap();
					for(Object columnName : gdo.getAllAttr().keySet()){
						String colName = columnName.toString();
						Object value = gdo.getAttrValue(colName);
						if(columnName.equals("LOCATION"))
						{
							if(value instanceof String)
							{
								Map<String, String> dataMap = new HashMap<String, String>();
								dataMap.put("CUID", value.toString());
								dataMap.put("LABEL_CN", locationMap.get(value).toString());
								map.put(columnName, dataMap);
							}
						}
						else
						{
							map.put(colName, convertObject(colName,value));
						}
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
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		className = repalceClassName(className);
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			if(StringUtils.isEmpty(dbo.getAttrValue("LABEL_CN"))){
				Object value = keyValueMap.get(FIRST_NAME_BATCH);
				String firstName = WebDMUtils.convertJosn2Object(FIRST_NAME_BATCH,value).toString();
				
				value = keyValueMap.get(LAST_NAME_BATCH);
				String lastName = WebDMUtils.convertJosn2Object(LAST_NAME_BATCH,value).toString();
				
				value = keyValueMap.get(FIGURE_BATCH);
				String figure = WebDMUtils.convertJosn2Object(FIGURE_BATCH,value).toString();
				
				value = keyValueMap.get(START_NO_BATCH);
				int start = Integer.parseInt(WebDMUtils.convertJosn2Object(START_NO_BATCH,value).toString());
				
				value = keyValueMap.get(NO_BATCH);
				int num = Integer.parseInt(WebDMUtils.convertJosn2Object(NO_BATCH,value).toString());
				List<String> labelCn=DMNameUtils.getDMDataName(firstName, lastName, figure, start, num);
				for(int i=0;i<labelCn.size();i++){
					GenericDO newDbo=(GenericDO) dbo.deepClone();
					newDbo.setCuid();
					newDbo.setAttrValue("LABEL_CN", labelCn.get(i));
					newDbo.setAttrValue(FiberCab.AttrName.deviceType, 2);
					list.add(newDbo);
				}
			}else{
				GenericDO newDbo=(GenericDO) dbo.deepClone();
				newDbo.setCuid();
				newDbo.setAttrValue(FiberCab.AttrName.deviceType, 2);
				list.add(newDbo);
			}
			try {
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				BoCmdFactory.getInstance().execBoCmd(method, bocontext,list);
			} catch (Exception e) {
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}



	

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		className = repalceClassName(className);
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.removeAttr("LAST_MODIFY_TIME");
			list.add(dbo);
			BoActionContext bocontext = new BoActionContext();
			bocontext.setUserId(editorMeta.getAc().getUserCuid());
			bocontext.setUserName(editorMeta.getAc().getUserId());
			try {
				BoCmdFactory.getInstance().execBoCmd(method,bocontext,list);
			} catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		className = repalceClassName(className);
		String method = editorMeta.getRemoteMethod("delete");	
		JSONArray  arr = JSON.parseArray(editorMeta.getParas());
		if(arr==null || arr.size() == 0){
			return null;
		}
		List<Map> result = arr.getObject(0, List.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();
			for(Map keyValueMap : result){
				GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
				list.add(dbo);
			}
			try {
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				BoCmdFactory.getInstance().execBoCmd(method, bocontext,list);
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	/**
	 * 转换className（两个设备共用一个pojo，为区分前台批量增加的后缀，其中一个设置为虚假className，后台调用时转换）
	 * @param className
	 * @return
	 */
	private String repalceClassName(String className)
	{
		if(className.equals("ONUBOX"))
		{
			return  "FIBER_CAB";
		}
		return className;
	}
	
}
