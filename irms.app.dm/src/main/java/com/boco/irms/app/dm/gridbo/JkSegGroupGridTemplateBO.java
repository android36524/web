package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.ibatis.vo.ResultMap;
import com.boco.irms.app.utils.CustomEnum;

/**
 * 陕西家客全生命周期家客设备工程管理 GridTemplateBO
 * 
 * @author baiyongzhi
 */
public class JkSegGroupGridTemplateBO extends GridTemplateProxyBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private IbatisDAO ibatisPonDAO;

	/**
	 * @param queryParam PageQuery
	 * @param param GridCfg
	 * @return PageResult
	 */
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
		List<ResultMap<String, Object>> segGroups = this.ibatisPonDAO.querySql(sql);
		if (!CollectionUtils.isEmpty(segGroups)) {
			for (int i = 0; i < segGroups.size(); i++) {
				if (i >= (queryParam.getCurPageNum() - 1) * queryParam.getPageSize() && i < queryParam.getCurPageNum() * queryParam.getPageSize()) {
					Map<String, Object> map = segGroups.get(i);
					for (String columnName : columnMap.keySet()) {
						map.put(columnName, convertObject(columnName, map.get(columnName)));
					}

					Map<String, Object> projectMap = new HashMap<String, Object>();
					projectMap.put("CUID", map.get("RELATED_PROJECT_CUID"));
					projectMap.put("LABEL_CN", map.get("RELATED_PROJECT_NAME"));
					map.put("RELATED_PROJECT_CUID", projectMap);
					list.add(map);
				}
			}
			pageResult = new PageResult(list, list.size(), queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}

	/**
	 * @param queryParam PageQuery
	 * @param param GridCfg
	 * @return PageResult
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String sql = this.getSql(param);
		List<Map<String, Object>> segGroups = this.ibatisPonDAO.querySql(sql);
		PageResult page = new PageResult(null, segGroups.size(), queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}

	/**
	 * 获取查询拼接条件
	 * 
	 * @param param GridCfgs
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	private String getSql(GridCfg param) {
		String userId = param.getAc().getUserId();
		boolean isValidatePermissions = true;
		boolean isUserPermissions = true;
		String userPermissionsSql =
			"SELECT DISTINCT PR.* FROM T_SYS_P_ROLE_REL PRR, T_SYS_P_ROLE PR WHERE PRR.RELATED_ROLE_ID = PR.CUID AND PRR.CODE = 'USER' AND PRR.RELATED_REL_ID = '"
				+ userId + "' AND PR.RES_TYPE IN ('6', '7')";
		List<Map<String, Object>> resultMapList = this.ibatisPonDAO.querySql(userPermissionsSql);
		if (!CollectionUtils.isEmpty(resultMapList)) {
			String resType = MapUtils.getString(resultMapList.get(0), "RES_TYPE", "");
			if (resType.equals("6")) {
				isUserPermissions = true;
			} else if (resType.equals("7")) {
				isUserPermissions = false;
			}
		} else {
			isValidatePermissions = false;
		}

		String sql =
			"SELECT SG.CUID, SG.LABEL_CN, SG.RELATED_PROJECT_CUID, PM.LABEL_CN RELATED_PROJECT_NAME, PM.NO AS PROJECT_NO,"
				+ " SG.RELATED_DISTRICT_CUID, SG.DESIGN_UNIT, SG.ACCEPTER, SG.ACCEPTER_CUID, SG.STATUS, SG.CREATOR, SG.IS_APPLY, SG.CREATE_TIME"
				+ " FROM T_ATTEMP_SEG_GROUP SG, PROJECT_MANAGEMENT PM WHERE SG.RELATED_PROJECT_CUID = PM.CUID";
		if (isValidatePermissions) {
			if (isUserPermissions) {
				sql += (" AND SG.CREATOR = '" + userId + "' AND SG.STATUS != '" + CustomEnum.DMProjectState._approval + "'");
			} else {
				sql += (" AND SG.ACCEPTER_CUID = '" + userId + "' AND SG.STATUS != '" + CustomEnum.DMProjectState._design + "'");
			}
		}
		if (param.getQueryParams() != null && param.getQueryParams().size() > 0) {
			Collection<WhereQueryItem> whereItems = param.getQueryParams().values();
			if (whereItems != null && whereItems.size() > 0) {
				for (WhereQueryItem item : whereItems) {
					if (item.getSqlValue() != null && !item.getSqlValue().trim().equals("")) {
						sql += " AND " + item.getSqlValue();
					}
				}
			}
		}
		sql += " ORDER BY SG.CREATE_TIME DESC";
		this.logger.info("SQL=" + sql);
		return sql;
	}

	public IbatisDAO getIbatisPonDAO() {
		return ibatisPonDAO;
	}

	public void setIbatisPonDAO(IbatisDAO ibatisPonDAO) {
		this.ibatisPonDAO = ibatisPonDAO;
	}
}