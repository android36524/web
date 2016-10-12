package com.boco.irms.app.dm.gridbo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.bo.IEditorPanelBO;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;

public abstract class AbstractPropTemplateBO implements IEditorPanelBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public Map<String,EditorColumnMeta> editorColumnMetaMap  = new HashMap<String,EditorColumnMeta>();

	protected String FIRST_NAME_BATCH="FIRST_NAME_BATCH";
	protected String LAST_NAME_BATCH="LAST_NAME_BATCH";
	protected String FIGURE_BATCH="FIGURE_BATCH";
	protected String START_NO_BATCH="START_NO_BATCH";
	protected String NO_BATCH="NO_BATCH";
	@Override
	public EditorPanelMeta getEditorMeta(EditorPanelMeta arg0) {
		return null;
	}
	
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
					
					Map map = new HashMap();
					for(Object columnName : gdo.getAllAttr().keySet()){
						String colName = columnName.toString();
						Object value = gdo.getAttrValue(colName);
						map.put(colName, convertObject(colName,value));
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
	public abstract EditorPanelMeta insert(EditorPanelMeta editorMeta)throws UserException;
	

	@Override
	public abstract EditorPanelMeta update(EditorPanelMeta editorMeta) throws UserException;
	@Override
	public abstract EditorPanelMeta delete(EditorPanelMeta editorMeta) throws UserException ;
	

    public Object convertObject(String columnName, Object value) {
  	   Object obj = value;
 		if("RELATED_DISTRICT_CUID".equals(columnName) || "RELATED_SPACE_CUID".equals(columnName)||"DISTRICT_CUID".equals(columnName) || "SITE_CUID_A".equals(columnName)|| "SITE_CUID_Z".equals(columnName)){
 			if(value instanceof String){
 				District dist = DistrictCacheModel.getInstance().getDistrictByCUID(String.valueOf(value));
 				if(dist != null){
 					Map map = new HashMap();
 					map.put("CUID", value);
 					map.put("LABEL_CN", dist.getLabelCn());
 					obj = map;
 				}
 			}else if (value instanceof GenericDO) {
 				Map map = new HashMap();
 				map.put("CUID", ((GenericDO)value).getCuid());
 				map.put("LABEL_CN", ((GenericDO)value).getAttrValue("LABEL_CN"));
 				obj = map;
 	        } 
 		}else{
 	    	EditorColumnMeta  columnMeta = editorColumnMetaMap.get(columnName);	    	
 	    	if(value != null && columnMeta != null&& columnMeta.getXtype() != null && columnMeta.getCode() != null){
 	    		String editor = columnMeta.getXtype();
 	    		String  code = columnMeta.getCode();
 	    		if("enumbox".equals(editor)|| "spacecombox".equals(editor)){
    				if(value instanceof Boolean){
    					value = (Boolean)value? 1L:0L;
    				}
	    			Object[]  gcEnum = EnumTypeManager.getInstance().getEnumTypes(code);
	    			if(gcEnum != null && !StringUtils.isEmpty(value.toString())){
	    				Map  enumMap = new HashMap();
	    				String valueStr = String.valueOf(value);
	    				String labelcn = "";
		    			for(Object oEnum : gcEnum){
		    				EnumType etype = (EnumType)oEnum;		    					    					
		    				String[] attrValue = valueStr.split(",");    					
		    				for(String val : attrValue){
		    					if("DeviceVendor".equals(code)){
		    						 if((etype.value).toString().equals(String.valueOf(val))){
				    						labelcn += ","+etype.dispalyName;
					    				}
		    					}else if(Long.parseLong((etype.value).toString())== Long.parseLong(String.valueOf(val))){
		    						labelcn += ","+etype.dispalyName;
			    				}
		    				}
		    			}
    					enumMap.put("CUID", valueStr);
    					enumMap.put("LABEL_CN", labelcn.length()==0? labelcn:labelcn.substring(1));
    	    			obj = enumMap;
	    			}
 	    		}else{
 	    			if (value instanceof GenericDO) {
 	    				Map map = new HashMap();
 	    				map.put("CUID", ((GenericDO)value).getCuid());
 	    				map.put("LABEL_CN", ((GenericDO)value).getAttrValue("LABEL_CN"));
 	    				obj = map;
 	    	        }
 	    			if(value instanceof String){
 	    				if(columnName.equals("RELATED_SYSTEM_CUID") || columnName.equals("RELATED_PROJECT_CUID")
 	    						|| columnName.equals("RELATED_MAINT_CUID")){
							if(!StringUtils.isEmpty(value.toString())){
								String relatedValue = getLabelcnByCuid(value.toString()); 
								Map map = new HashMap();
			 					map.put("CUID", value);
			 					map.put("LABEL_CN", relatedValue);
			 					obj = map;
							}
						}
 	    			}
 	    		}
 	    	}else{
 				if (value instanceof GenericDO) {
 					Map map = new HashMap();
 					map.put("CUID", ((GenericDO)value).getCuid());
 					map.put("LABEL_CN", ((GenericDO)value).getAttrValue("LABEL_CN"));
 					obj = map;
 		        } 
 	    	}
 		}
     	return obj;
     } 
    /**
     * 通过CUID获取名称
     * @param cuid
     * @return
     */
    public static String getLabelcnByCuid(String cuid){
		String name = cuid;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), cuid);
			if(gdo != null){
				name = gdo.getAttrString("LABEL_CN");
				if(!StringUtils.isEmpty(name)){
					name = gdo.getAttrString("LABEL_CN");
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
    
    /**
     * 判断施工时间不能大于竣工时间
     * @param dbo
     * @return
     */
    public boolean compareBuildAndFinshDate(GenericDO dbo){
    	boolean flag=true;
    	Object buildValue = dbo.getAttrValue(DuctSystem.AttrName.buildDate);
    	Object finishValue = dbo.getAttrValue(DuctSystem.AttrName.finishDate);
    	if(buildValue != null && finishValue != null){
    		Timestamp buildDate = (Timestamp) buildValue;
            Timestamp finishDate = (Timestamp) finishValue;
            if(buildDate != null && finishDate != null){
            	if(buildDate.after(finishDate)){
            		flag=false;
            	}
            }
    	}
    	return flag;
    }
    public void getSpecialPurposeAndOlevel(long systemLevel,long specialPurpose,long olevel) throws UserException{

    	if(systemLevel == 5L){//系统级别是本地接入
    		if(specialPurpose==0L){//如果专线用途是未知，重要性只能是本地一级或者二级
    			if(olevel !=4L && olevel !=5L){
    				//提示
    				throw new UserException("系统级别是本地接入，专线用途是未知时，重要性只能是本地二级或本地三级!");
    			}
    		}else if(specialPurpose==1L || specialPurpose==3L){//如果专线用途是集客，重要性只能是金牌、银牌、铜牌、标准
    			if(olevel !=6L && olevel !=7L && olevel !=8L && olevel !=9L){
    				//提示
    				throw new UserException("系统级别是本地接入，专线用途是集客或者集客家客时，重要性只能是金牌、银牌、铜牌、标准!");
    			}
    		}else if(specialPurpose==2L){//如果专线用途是家客，重要性只能是未知
    			if(olevel !=0L){
    				//提示
    				throw new UserException("系统级别是本地接入，专线用途是家客时，重要性只能是未知!");
    			}
    		}
    	}else{//系统级别是非本地接入，专线用途只能是未知
    		if(specialPurpose != 0L){
    			//提示
    			throw new UserException("系统级别是非本地接入时，专线用途只能是未知!");
    		}else{
    			if(systemLevel==0L){//如果系统级别是未知，重要性也只能是未知
    				if(olevel !=0L){
    					//提示
    					throw new UserException("系统级别是未知时，重要性只能是未知!");
    				}
    			}else if(systemLevel==1L){//如果系统级别是省际，重要性也只能是一干
    				if(olevel !=1L){
    					//提示
    					throw new UserException("系统级别是省际时，重要性只能是一干!");
    				}
    			}else if(systemLevel==2L){//如果系统级别是省内，重要性也只能是二干
    				if(olevel !=2L){
    					//提示
    					throw new UserException("系统级别是省际时，重要性只能是二干!");
    				}
    			}else if(systemLevel==3L || systemLevel==4L){//如果系统级别是本地骨干、本地汇聚，重要性也只能是本地一级
    				if(olevel !=3L){
    					//提示
    					throw new UserException("系统级别是本地骨干、本地汇聚时，重要性只能是本地一级!");
    				}
    			}
    		}
    	}
    
    }
    
}
