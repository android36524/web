package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.ibatis.vo.ResultMap;
import com.boco.core.spring.SysProperty;

public class JkSysUserGridTemplateProxyBO extends GridTemplateProxyBO {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IbatisDAO ibatisPonDAO;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		// 获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		this.editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			this.editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		GridTplConfig gridTpl = this.getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
		// 拼装查询条件
		String sql = this.getSql(param);
		this.logger.info("查询SQL=" + sql);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		PageResult pageResult = new PageResult(list, 0, queryParam.getCurPageNum(), queryParam.getPageSize());
		List<Map<String, Object>> resultMapList = this.IbatisDAO.querySql(sql);
		if (!CollectionUtils.isEmpty(resultMapList)) {
			for (int i = 0; i < resultMapList.size(); i++) {
				if (i >= (queryParam.getCurPageNum() - 1) * queryParam.getPageSize() && i < queryParam.getCurPageNum() * queryParam.getPageSize()) {
					Map<String, Object> map = resultMapList.get(i);
					for (String columnName : columnMap.keySet()) {
						map.put(columnName, convertObject(columnName, map.get(columnName)));
					}
					list.add(map);
				}
			}
			pageResult = new PageResult(list, list.size(), queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String sql = this.getSql(param);
		logger.info("查询SQL=" + sql);
		List<ResultMap<String, String>> sysUsersList = this.IbatisDAO.querySql(sql);
		PageResult page = new PageResult(null, sysUsersList.size(), queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}

	/**
	 * 获取查询拼接条件
	 * 
	 * @param param GridCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getSql(GridCfg param) {
		String condition = "";
		String provinceCuid = SysProperty.getInstance().getValue("district");
		String roleRelSql =
			"SELECT PRR.RELATED_REL_ID FROM T_SYS_P_ROLE_REL PRR, T_SYS_P_ROLE PR WHERE PRR.RELATED_ROLE_ID = PR.CUID AND PRR.CODE = 'USER'"
				+ " AND PR.RES_TYPE = '7' AND PR.RELATED_DISTRICT_CUID LIKE '" + provinceCuid + "%'";
		List<Map<String, Object>> resultMapList = this.ibatisPonDAO.querySql(roleRelSql);
		if (!CollectionUtils.isEmpty(resultMapList)) {
			for (Map<String, Object> resultMap : resultMapList) {
				String relatedRelId = MapUtils.getString(resultMap, "RELATED_REL_ID", "");
				if (!StringUtils.isEmpty(relatedRelId)) {
					condition += ("'" + relatedRelId + "',");
				}
			}
			condition = StringUtils.isEmpty(condition) ? "" : condition.substring(0, condition.length() - 1);
		}

		String sql =
			"SELECT SU.CUID, SU.USER_NAME, SU.TRUE_NAME AS LABEL_CN, SU.RELATED_DISTRICT_CUID FROM SYS_USER SU WHERE SU.RELATED_DISTRICT_CUID LIKE '"
				+ provinceCuid + "%'";
		if (!StringUtils.isEmpty(condition)) {
			sql += (" AND SU.USER_NAME IN (" + condition + ")");
		}
		if (param.getQueryParams() == null || param.getQueryParams().size() == 0) {
			return sql;
		}
		Collection<WhereQueryItem> whereItems = param.getQueryParams().values();
		if (whereItems != null && whereItems.size() > 0) {
			for (WhereQueryItem item : whereItems) {
				if (item.getSqlValue() != null && !item.getSqlValue().trim().equals("")) {
					sql += " AND " + item.getSqlValue();
				}
			}
		}
		this.logger.info("SQL=" + sql);
		return sql;
	}

	public IbatisDAO getIbatisDAO() {
		return this.IbatisDAO;
	}

	public void setIbatisDAO(IbatisDAO ibatisDAO) {
		this.IbatisDAO = ibatisDAO;
	}

	public IbatisDAO getIbatisPonDAO() {
		return ibatisPonDAO;
	}

	public void setIbatisPonDAO(IbatisDAO ibatisPonDAO) {
		this.ibatisPonDAO = ibatisPonDAO;
	}
}