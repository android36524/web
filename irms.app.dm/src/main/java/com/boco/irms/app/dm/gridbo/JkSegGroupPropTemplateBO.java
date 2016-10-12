package com.boco.irms.app.dm.gridbo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.tpl.ResConfigurer;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.utils.exception.UserException;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.core.utils.lang.Assert;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.irms.app.utils.DmDesignHelper;

/**
 * 陕西家客全生命周期家客设备工程管理 PropTemplateBO
 * 
 * @author baiyongzhi
 */
public class JkSegGroupPropTemplateBO extends AbstractPropTemplateBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ResConfigurer resConfigurer;

	protected IbatisDAO ibatisPonDAO;

	protected IbatisDAO ibatisTnmsDAO;

	/**
	 * @param editorMeta EditorPanelMeta
	 * @return EditorPanelMeta
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		this.editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			this.editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}

		List<Map> results = JSON.parseArray(editorMeta.getParas(), Map.class);
		String cuid = (String) results.get(0).get("CUID");
		if (cuid != null) {
			try {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				String condition = " SG.CUID = '" + cuid + "'";
				String sql = this.getSql(condition);
				List<Map<String, Object>> segGroups = this.ibatisPonDAO.querySql(sql);
				if (!CollectionUtils.isEmpty(segGroups)) {
					Map<String, Object> map = segGroups.get(0);
					for (String columnName : map.keySet()) {
						Object value = map.get(columnName);
						if (columnName.equals("ACCEPTER_CUID")) {
							value = map.get("ACCEPTER");
						} else {
							value = convertObject(columnName, map.get(columnName));
						}
						map.put(columnName, value);
					}
					list.add(map);
				}

				PageResult result = new PageResult(list, 1, 1, 1);
				editorMeta.setResult(JSON.toJSONString(result.getElements()));
			} catch (Exception e) {
				this.logger.error(e.getMessage(), e);
			}
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta) {
		return null;
	}

	/**
	 * @param editorMeta EditorPanelMeta
	 * @return EditorPanelMeta
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta) {
		List<Map> result = JSON.parseArray(editorMeta.getParas(), Map.class);
		if (!CollectionUtils.isEmpty(result)) {
			boolean isUserPermissions = this.getUserPermissions(editorMeta);
			if (isUserPermissions) {
				Map<String, Object> map = result.get(0);
				String labelCn = DmDesignHelper.convertJson2String("LABEL_CN", map.get("LABEL_CN"));
				String condition = " SG.LABEL_CN = '" + labelCn + "'";
				String sql = this.getSql(condition);
				List<Map<String, Object>> segGroups = this.ibatisPonDAO.querySql(sql);
				if (!CollectionUtils.isEmpty(segGroups)) {
					throw new UserException("单位工程名称已存在，请重新输入");
				}

				String cuid = CUIDHexGenerator.getInstance().generate("SEG_GROUP");
				Date date = new Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = df.format(date);

				String userId = editorMeta.getAc().getUserId();
				String userName = editorMeta.getAc().getUserName();

				String accepterUserName = DmDesignHelper.convertJson2String("CUID", map.get("ACCEPTER_CUID"));
				List<Map<String, Object>> userMapList = this.getUserMapList(accepterUserName);
				String accepter = !CollectionUtils.isEmpty(userMapList) ? MapUtils.getString(userMapList.get(0), "TRUE_NAME", "") : "";
				String accepterCuid = !CollectionUtils.isEmpty(userMapList) ? MapUtils.getString(userMapList.get(0), "USER_NAME", "") : "";

				Record record = new Record("T_ATTEMP_SEG_GROUP");
				record.addColValue("CUID", cuid);
				record.addColValue("LABEL_CN", labelCn);
				record.addColValue("RELATED_BMCLASSTYPE_CUID", "SEG_GROUP_PRODUCT");
				record.addColValue("RELATED_PROJECT_CUID", DmDesignHelper.convertJson2String("CUID", map.get("RELATED_PROJECT_CUID")));
				record.addColValue("RELATED_DISTRICT_CUID", DmDesignHelper.convertJson2String("CUID", map.get("RELATED_DISTRICT_CUID")));
				record.addColValue("REL_SEG_GROUP_CUID", cuid);
				record.addColValue("STATUS", String.valueOf(CustomEnum.DMProjectState._design));
				record.addColValue("IS_APPLY", "1");
				record.addColValue("CREATOR", userId);
				record.addColValue("DESIGN_UNIT", userName);
				record.addColValue("DESIGN_UNIT_CUID", userId);
				record.addColValue("ACCEPTER", accepter);
				record.addColValue("ACCEPTER_CUID", accepterCuid);
				record.addColValue("REMARK", "4001");
				record.addColValue("CREATE_TIME", time);
				record.addColValue("LAST_MODIFY_TIME", time);
				this.ibatisPonDAO.insertDynamicTable(record);
			}
		} else {
			throw new UserException("当前用户不具备单位工程的设计权限.");
		}
		return editorMeta;
	}

	/**
	 * @param editorMeta EditorPanelMeta
	 * @return EditorPanelMeta
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta) {
		List<Map> result = JSON.parseArray(editorMeta.getParas(), Map.class);
		if (!CollectionUtils.isEmpty(result)) {
			boolean isUserPermissions = this.getUserPermissions(editorMeta);
			if (isUserPermissions) {
				Map<String, Object> map = result.get(0);
				String cuid = ((JSONObject) map.get("CUID")).getString("value");
				String labelCn = DmDesignHelper.convertJson2String("LABEL_CN", map.get("LABEL_CN"));
				String condition = " SG.CUID != '" + cuid + "' AND SG.LABEL_CN = '" + labelCn + "'";
				String sql = this.getSql(condition);
				List<Map<String, Object>> segGroups = this.ibatisPonDAO.querySql(sql);
				if (!CollectionUtils.isEmpty(segGroups)) {
					throw new UserException("单位工程名称已存在，请重新输入");
				}

				String status = DmDesignHelper.convertJson2String("STATUS", map.get("CUID"));
				if (!status.equals(CustomEnum.DMProjectState._maintain)) {
					Date date = new Date();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = df.format(date);
					String relatedProjectCuid = DmDesignHelper.convertJson2String("CUID", map.get("RELATED_PROJECT_CUID"));

					String accepterUserCuid = DmDesignHelper.convertJson2String("CUID", map.get("ACCEPTER_CUID"));
					List<Map<String, Object>> userMapList = this.getUserMapList(accepterUserCuid);
					String accepter = !CollectionUtils.isEmpty(userMapList) ? MapUtils.getString(userMapList.get(0), "TRUE_NAME", "") : "";
					String accepterCuid = !CollectionUtils.isEmpty(userMapList) ? MapUtils.getString(userMapList.get(0), "USER_NAME", "") : "";

					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("CUID", cuid);
					paramMap.put("LABEL_CN", labelCn);
					paramMap.put("RELATED_PROJECT_CUID", relatedProjectCuid);
					paramMap.put("RELATED_DISTRICT_CUID", DmDesignHelper.convertJson2String("CUID", map.get("RELATED_DISTRICT_CUID")));
					paramMap.put("ACCEPTER", accepter);
					paramMap.put("ACCEPTER_CUID", accepterCuid);
					paramMap.put("LAST_MODIFY_TIME", time);

					Map<String, String> tempMap = new HashMap<String, String>();
					tempMap.put("CUID", "CUID");
					paramMap.put("UPDATE_PK", tempMap);

					List<Map> paramMapList = new ArrayList<Map>();
					paramMapList.add(paramMap);
					this.ibatisPonDAO.updateDynamicTableBatch("T_ATTEMP_SEG_GROUP", paramMapList);

					this.modifyProjectNoBySegGroupCuid(cuid, relatedProjectCuid);
				} else {
					throw new UserException("只有非维护状态的单位工程允许修改.");
				}
			}
		} else {
			throw new UserException("当前用户不具备单位工程的设计权限.");
		}
		return editorMeta;
	}

	/**
	 * 获取查询拼接条件
	 * 
	 * @param condition
	 * @return String
	 */
	private String getSql(String condition) {
		String sql =
			"SELECT SG.CUID, SG.LABEL_CN, SG.RELATED_PROJECT_CUID, PM.LABEL_CN RELATED_PROJECT_NAME, PM.NO AS PROJECT_NO, SG.RELATED_DISTRICT_CUID, SG.ACCEPTER, SG.ACCEPTER_CUID, SG.STATUS"
				+ " FROM T_ATTEMP_SEG_GROUP SG, PROJECT_MANAGEMENT PM WHERE SG.RELATED_PROJECT_CUID = PM.CUID";
		if (!StringUtils.isEmpty(condition)) {
			sql += (" AND " + condition);
		}
		this.logger.info("SQL=" + sql);
		return sql;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getUserMapList(String userCuid) {
		String sql = "SELECT SU.TRUE_NAME, SU.USER_NAME FROM SYS_USER SU WHERE";
		if (userCuid.contains("SYS_USER")) {
			sql += (" SU.CUID = '" + userCuid + "'");
		} else {
			sql += (" SU.TRUE_NAME = '" + userCuid + "'");
		}
		List<Map<String, Object>> resultMapList = this.ibatisTnmsDAO.querySql(sql);
		return resultMapList;
	}

	/**
	 * 
	 * @param segGroupCuid
	 * @param relatedProjectCuid
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void modifyProjectNoBySegGroupCuid(String segGroupCuid, String relatedProjectCuid) {
		if (!StringUtils.isEmpty(relatedProjectCuid)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("RELATED_SEGGROUP_CUID", segGroupCuid);
			List<Map<String, Object>> resultMapList = this.ibatisPonDAO.getSqlMapClientTemplate().queryForList("EQUIP_DM.queryDeviceBySegGroupCuid", map);
			if (!CollectionUtils.isEmpty(resultMapList)) {
				List<Record> paramMapList = new ArrayList<Record>();
				List<Record> pkList = new ArrayList<Record>();
				for (Map<String, Object> resultMap : resultMapList) {
					resultMap.put("RELATED_PROJECT_CUID", relatedProjectCuid);
					Map<String, Record> paramMap = this.buildRecordMap(resultMap, MapUtils.getString(resultMap, "RELATED_BMCLASSTYPE_CUID"));
					paramMapList.add(paramMap.get("paramRecord"));
					pkList.add(paramMap.get("pkRecord"));
				}
				this.ibatisPonDAO.updateDynamicTableBatch(paramMapList, pkList);
			}
		}
	}

	/**
	 * 
	 * @param paramMap Map<String, Object>
	 * @param tableName
	 * @return map Map<String, Record>
	 */
	protected Map<String, Record> buildRecordMap(Map<String, Object> paramMap, String tableName) {
		Assert.notEmpty(paramMap, "参数集合不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		Record paramRecord = new Record(tableName);
		Record pkRecord = new Record(tableName);
		Iterator<String> iterator = paramMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.equals("RELATED_BMCLASSTYPE_CUID")) {
				Object object = paramMap.get(key);
				if (object != null) {
					paramRecord.addColValue(key, object);
				}
			}
			pkRecord.addColValue("CUID", MapUtils.getString(paramMap, "CUID"));
		}

		Map<String, Record> map = new HashMap<String, Record>();
		map.put("paramRecord", paramRecord);
		map.put("pkRecord", pkRecord);
		return map;
	}

	/**
	 * 
	 * @param editorMeta
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean getUserPermissions(EditorPanelMeta editorMeta) {
		String userId = editorMeta.getAc().getUserId();
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
			isUserPermissions = false;
		}
		return isUserPermissions;
	}

	protected ResConfigurer getResConfigurer() {
		return resConfigurer;
	}

	public void setResConfigurer(ResConfigurer resConfigurer) {
		this.resConfigurer = resConfigurer;
	}

	public IbatisDAO getIbatisPonDAO() {
		return ibatisPonDAO;
	}

	public void setIbatisPonDAO(IbatisDAO ibatisPonDAO) {
		this.ibatisPonDAO = ibatisPonDAO;
	}

	public IbatisDAO getIbatisTnmsDAO() {
		return ibatisTnmsDAO;
	}

	public void setIbatisTnmsDAO(IbatisDAO ibatisTnmsDAO) {
		this.ibatisTnmsDAO = ibatisTnmsDAO;
	}
}