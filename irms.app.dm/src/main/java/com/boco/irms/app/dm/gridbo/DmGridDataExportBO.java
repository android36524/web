package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class DmGridDataExportBO extends GridTemplateProxyBO {

	@Override
//		String relatedSystemCuid = param.getCfgParams().get("cuid");
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		// 获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
//		String className = editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("query");

		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
	
		// 拼装查询条件
		ArrayList systemcuids = getSystemCuids(param);

		// 从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext((queryParam.getCurPageNum() - 1) * queryParam.getPageSize(),
				queryParam.getPageSize(), false);
		querycon.setUserId("SYS_USER-0");
		DataObjectList results = new DataObjectList();
		try {
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),systemcuids);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}

		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0,queryParam.getCurPageNum(), queryParam.getPageSize());
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map map = new HashMap();
				for (String columnName : columnMap.keySet()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, value);
				}
				list.add(map);
			}
			pageResult = new PageResult(list, results.getCountValue(),
					queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}

	
	public ArrayList getSystemCuids(GridCfg param){
		ArrayList systemCuids = new ArrayList();
    	String cuidV = "";
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				cuidV = item.getValue();
			}
		}
    	for(String systemcuid: cuidV.split(",")){
    		systemCuids.add(systemcuid.replace("'", ""));
    	}
		return systemCuids;
    }

	}
