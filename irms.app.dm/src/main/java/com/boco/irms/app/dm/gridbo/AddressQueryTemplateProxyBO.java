package com.boco.irms.app.dm.gridbo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class AddressQueryTemplateProxyBO extends GridTemplateProxyBO {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Map<String,EditorColumnMeta> editorColumnMetaMap  = new HashMap<String,EditorColumnMeta>();
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();
		
		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
        //拼装查询条件
		Map params = getParams(param,className);
		//从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext((queryParam.getCurPageNum()-1)*queryParam.getPageSize(), queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		DataObjectList results = new DataObjectList();
		try {
			IbatisDAO designerDao = (IbatisDAO)SpringContextUtil.getBean("IbatisDesignerDAO");
			String sqlName = "RofhAddress.queryRofhAddress";
			if(className.equals("QUERYONUPOS"))
			{
				sqlName = "RofhAddress.queryOnuPos";
			}
			int minSize = (queryParam.getCurPageNum()-1)*queryParam.getPageSize() + 1;
			int maxSize = queryParam.getCurPageNum()*queryParam.getPageSize();
			params.put("minSize", minSize);
			params.put("maxSize", maxSize);
			List<Map<String, Object>> result = designerDao.getSqlMapClientTemplate().queryForList(sqlName, params);
			for(Map<String, Object> map : result)
			{
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				GenericDO gdo = new GenericDO();
				gdo.setCuid(cuid);
				gdo.setAttrValue("LABEL_CN", labelCn);
				results.add(gdo);
			}
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		
		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0, queryParam.getCurPageNum(), queryParam.getPageSize());
		if(results != null && results.size()>0){
			for(int i=0;i<results.size();i++){
				GenericDO gdo = results.get(i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map map = new HashMap();
				for(String columnName : columnMap.keySet()){
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName,value));
				}
				list.add(map);
			}
			pageResult = new PageResult(list, results.getCountValue(), queryParam
					.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}
	
    private DataObjectList getResultByPage(DataObjectList results, PageQuery queryParam)
    {
    	DataObjectList returnList = new DataObjectList();
    	int start = (queryParam.getCurPageNum()-1)*queryParam.getPageSize() - 1;
    	int end = start + queryParam.getPageSize();
    	if(start < results.size())
    	{
    		for(int i = 0; i < results.size(); i++)
    		{
    			if(i <= end)
    			{
    				returnList.add(results.get(i));
    			}
    		}
    	}
    	return returnList;
    }
	
	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		//String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();
        //拼装查询条件
		Map params = getParams(param,className);
		
		//从传输服务查询数据
		Integer totalNum =0;
		try {
			IbatisDAO designerDao = (IbatisDAO)SpringContextUtil.getBean("IbatisDesignerDAO");
			String sqlName = "RofhAddress.queryCountRofhAddress";
			if(className.equals("QUERYONUPOS"))
			{
				sqlName = "RofhAddress.queryCountOnuPos";
			}
			Object result = designerDao.getSqlMapClientTemplate().queryForObject(sqlName, params);
			if(result != null)
				totalNum = (Integer) result;
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=RofhAddress.queryRofhAddress",e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
	
    public Object convertObject(String columnName, Object value) {
 	   Object obj = value;
		if("RELATED_DISTRICT_CUID".equals(columnName) || "RELATED_SPACE_CUID".equals(columnName)
				|| "DISTRICT_CUID".equals(columnName)){
			if(value instanceof String){
				District dist = DistrictCacheModel.getInstance().getDistrictByCUID(String.valueOf(value));
				if(dist != null){
					Map map = new HashMap();
					map.put("CUID", value);
					map.put("LABEL_CN", dist.getLabelCn());
					obj = map;
				}
			}else if (value instanceof GenericDO) {
				obj = gdo2Map(((GenericDO)value));
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
	    				obj = gdo2Map(((GenericDO)value));
	    	        }
	    			
	    			if(value instanceof String){
 	    				if(columnName.equals("RELATED_SYSTEM_CUID") || columnName.equals("RELATED_PROJECT_CUID")
 	    						|| columnName.equals("RELATED_MAINT_CUID")||columnName.equals("GROUP_CUID")){
							if(!StringUtils.isEmpty(value.toString())){
								String relatedValue = AbstractPropTemplateBO.getLabelcnByCuid(value.toString()); 
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
					obj = gdo2Map(((GenericDO)value));
		        }
				if(columnMeta!=null && "date".equals(columnMeta.getXtype())){
					obj=formatDate(value);
				}
	    	}
		}
    	return obj;
    }    
    
    
    private Object formatDate(Object value){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Object obj=null;		
		if(value==null){
			obj=null;
		}
		else{
			obj=sdf.format(value);
		}		
		return obj;
    }
    /**
     * 转换GenericDO 为map
     * @param gdo
     * @return
     * @author gaoxf at 2014年3月17日 下午10:29:48
     */
    private Map gdo2Map(GenericDO gdo){
		Map map = new HashMap();
		map.put("CUID", gdo.getCuid());
		map.put("LABEL_CN", gdo.getAttrValue("LABEL_CN"));
		return map;
    }
    
    /**
     * 获取查询拼接条件
     * @param wItems
     * @param className
     * @author wangqin
     * @return
     */
    public Map getParams(GridCfg param,String className){
    	Map map = new HashMap();
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return map;
    	}
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				if(item.getSqlValue() != null && !item.getSqlValue().trim().equals("")){
					map.put(item.getKey(), item.getValue());
				}
				
			}
		}
		return map;
    }
    
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
}
