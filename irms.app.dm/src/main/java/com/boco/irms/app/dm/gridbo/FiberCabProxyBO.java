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
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;

public class FiberCabProxyBO extends GridTemplateProxyBO {

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
		
		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
        //拼装查询条件
		String sql = getSql(param,className);
		sql += " AND ("+FiberCab.AttrName.deviceType+" = "+CustomEnum.OnuboxDeviceType.FIBERCAB+" or "+FiberCab.AttrName.deviceType+" is null) ";
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
					map.put(columnName, convertObject(columnName,value));
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
        //拼装查询条件
		String sqlcon = getSql(param,className);
		sqlcon += " AND ("+FiberCab.AttrName.deviceType+" = "+CustomEnum.OnuboxDeviceType.FIBERCAB+" or "+FiberCab.AttrName.deviceType+" is null) ";
		
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
	
}
