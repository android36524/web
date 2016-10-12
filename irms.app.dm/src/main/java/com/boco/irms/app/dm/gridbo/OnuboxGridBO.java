package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class OnuboxGridBO extends GridTemplateProxyBO {

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();
		className = repalceClassName(className);
		
		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
        //拼装查询条件
		String sql = getSql(param,className);
		sql += " AND "+FiberCab.AttrName.deviceType+" = "+CustomEnum.OnuboxDeviceType.ONUBOX+" ";
		logger.info("查询SQL="+sql);
		//从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext((queryParam.getCurPageNum()-1)*queryParam.getPageSize(), queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		DboCollection  results = new DboCollection();
		try {
			results = (DboCollection)BoCmdFactory.getInstance().execBoCmd(method, querycon,sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		
		Map locationMap = new HashMap();
		sql = "SELECT cuid, label_cn from T_ROFH_FULL_ADDRESS";
		Class[] colClassType = new Class[] {String.class, String.class};
		try {
			DataObjectList dataObjectList = getDuctManagerBO().getDatasBySql(sql, colClassType);
			if(dataObjectList!=null && dataObjectList.size()>0){
				logger.info("查到的网格关联资源的记录数:"+dataObjectList.size());
				for(GenericDO gdo : dataObjectList){
					locationMap.put(gdo.getAttrString("1"), gdo.getAttrString("2"));
				}
			}
		} catch (Exception e) {
		}
		
		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0, queryParam.getCurPageNum(), queryParam.getPageSize());
		if(results != null && results.size()>0){
			for(int i=0;i<results.size();i++){
				GenericDO  gdo = results.getAttrField(className, i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map map = new HashMap();
				for(String columnName : columnMap.keySet()){
					Object value = gdo.getAttrValue(columnName);
					if(columnName.equals("LOCATION"))
					{
						if(value instanceof String && locationMap.get(value) != null)
						{
							Map<String, String> dataMap = new HashMap<String, String>();
							dataMap.put("CUID", value.toString());
							dataMap.put("LABEL_CN", locationMap.get(value).toString());
							map.put(columnName, dataMap);
						}
					}
					else
					{
						map.put(columnName, convertObject(columnName,value));
					}
				}
				list.add(map);
			}
			pageResult = new PageResult(list, results.getCountValue(), queryParam
					.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}
	
	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String method = "IDuctManagerBO.getPointOfSystemCountBySql";
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		//String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();
		className = repalceClassName(className);
        //拼装查询条件
		String sqlcon = getSql(param,className);
		sqlcon += " AND "+FiberCab.AttrName.deviceType+" = "+CustomEnum.OnuboxDeviceType.ONUBOX+" ";
		
		String sql = "SELECT COUNT(*) FROM "+className+" WHERE "+sqlcon;
		//从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		Integer totalNum =0;
		try {
			totalNum = (Integer)BoCmdFactory.getInstance().execBoCmd(method, querycon,sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
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
