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
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;

public class PosListGridBO extends GridTemplateProxyBO {
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {

		// 获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();

		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
		// 拼装查询条件
		String sql = getSql(param,className);
		// 从传输服务查询数据
		DataObjectList results = new DataObjectList();
		try {
			 GenericDO gdo = new GenericDO();
			 gdo.setClassName("AN_POS");
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, sql, gdo);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}

		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0,queryParam.getCurPageNum(), queryParam.getPageSize());
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
//				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
//				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
//				gdo.setAttrValue("LAST_MODIFY_TIME",
//						gdo.getLastModifyTime());
				Map map = new HashMap();
				for (String columnName : columnMap.keySet()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName, value));
				}
				list.add(map);
			}
			pageResult = new PageResult(list, results.getCountValue(),
					queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
		
	}

    public String getSql(GridCfg param,String className){
    	String relatedCabCuid = param.getCfgParams().get("cuid");
    	String sql = "RELATED_CAB_CUID='" + relatedCabCuid + "'";
		return sql;
    }

	}
