package com.boco.irms.app.jk.service.bo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.boco.common.util.except.UserException;
import com.boco.core.utils.lang.Assert;
import com.boco.transnms.common.dto.base.DboBlob;

public class JkDeviceChangeBO extends AbstractJkDeviceBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @return
	 */
	public String apply(List<String> segGroupCuidList) {
		Assert.notNull(segGroupCuidList, "list对象不能为空.");
		long curTime = System.currentTimeMillis();
		String message = "0";
		try {
			Map<String, Object> map = this.getDeviceInfo(segGroupCuidList, "", "apply");
			this.insertDeviceInfo(map);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==数据审核通过迁移失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("数据审核通过迁移时间:" + (finishTime - curTime));
		return message;
	}

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @param orderId
	 * @return message String
	 */
	public String archived(List<String> segGroupCuidList, String orderId) {
		Assert.notNull(segGroupCuidList, "list对象不能为空.");
		long curTime = System.currentTimeMillis();
		String message = "0";
		try {
			Map<String, Object> curMap = this.getDeviceInfo(segGroupCuidList, orderId, "archived");
			Map<String, Object> backupMap = this.getBackupData();
			Map<String, Object> map = this.screeningResourceData(curMap, backupMap);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("add", map.get("add"));
			this.insertDeviceInfo(paramMap);

			paramMap = new HashMap<String, Object>();
			paramMap.put("update", map.get("update"));
			this.updateDeviceInfo(paramMap);

			paramMap = new HashMap<String, Object>();
			paramMap.put("delete", map.get("delete"));
			this.deleteDeviceInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==数据归档迁移失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("数据归档迁移时间:" + (finishTime - curTime));
		return message;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return
	 */
	protected void insertDeviceInfo(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				this.insertDynamicTableBatch(map);
				if (this.operateType.equals("archived")) {
					if (this.dispatchMode.equals("2")) {
						this.dispatchMode = "1";
						this.insertDynamicTableBatch(map);
						this.dispatchMode = "2";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入家客设备相关数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("插入家客设备相关数据时间:" + (finishTime - curTime));
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getBackupData() {
		long curTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<String> taskCfgCuidList = new ArrayList<String>();
			taskCfgCuidList.add("DM_HB_JK_PROJECT_DEVICE_CHANGE_1_audit");
			taskCfgCuidList.add("DM_HB_JK_NO_PROJECT_DEVICE_CHANGE_1_audit");
			String taskCuid = this.getTaskCuid(taskCfgCuidList);
			String condition = "RELATED_PROJECT_CUID = '" + taskCuid + "'";
			String cuidCondition = "";
			for (String segGroupCuid : this.segGroupCuidList) {
				cuidCondition += ("'" + segGroupCuid + "',");
			}
			cuidCondition = StringUtils.isEmpty(cuidCondition) ? "" : cuidCondition.substring(0, cuidCondition.length() - 1);
			if (!StringUtils.isEmpty(cuidCondition)) {
				condition += " AND RELATED_RES_CUID IN (" + cuidCondition + ")";
				List<Map<String, Object>> resultMapList = this.iBatisDesignerDAO.querySql(this.getSql(condition, "PROJECT_TO_RES"));
				Assert.notNull(resultMapList, "日志表list对象不能为空.");
				DboBlob dboBlob = new DboBlob((byte[]) resultMapList.get(0).get("RES_PROPERTIES"));
				String resProperties = "";
				try {
					resProperties = new String(dboBlob.getBlobBytes(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				map = JSON.parseObject(resProperties, HashMap.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询家客设备相关日志数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询家客设备相关日志数据时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param map
	 * @param backupMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> screeningResourceData(Map<String, Object> curMap, Map<String, Object> backupMap) {
		long curTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iterator = backupMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.contains("CUID")) {
				if (!CollectionUtils.isEmpty(map)) {
					Map<String, Object> tempMap =
						this.screeningResourceData((List<Map<String, Object>>) curMap.get(key), (List<Map<String, Object>>) backupMap.get(key));
					map.putAll(this.appendMap(tempMap, "add"));
					map.putAll(this.appendMap(tempMap, "update"));
					map.putAll(this.appendMap(tempMap, "delete"));
				}
			}
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("家客设备相关所有数据比对时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param curMapList List<Map<String, Object>>
	 * @param backupMapList List<Map<String, Object>>
	 * @return map Map<String, Object>
	 */
	protected Map<String, Object> screeningResourceData(List<Map<String, Object>> curMapList, List<Map<String, Object>> backupMapList) {
		long curTime = System.currentTimeMillis();
		List<Map<String, Object>> tempCurMapList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempBackupMapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> backupMap : backupMapList) {
			String backupCuid = MapUtils.getString(backupMap, "CUID");
			for (Map<String, Object> curMap : curMapList) {
				String curCuid = MapUtils.getString(curMap, "CUID");
				if (backupCuid.equals(curCuid)) {
					tempCurMapList.add(curMap);
					tempBackupMapList.add(backupMap);
				}
			}
		}

		curMapList.removeAll(tempCurMapList);
		backupMapList.removeAll(tempBackupMapList);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("add", curMapList);
		map.put("update", tempCurMapList);
		map.put("delete", backupMapList);

		long finishTime = System.currentTimeMillis();
		this.logger.info("家客设备相关数据比对时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param paramMap Map<String, Object>
	 * @param key
	 * @return map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> appendMap(Map<String, Object> paramMap, String key) {
		long curTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> tempMapList = (List<Map<String, Object>>) map.get(key);
		if (!CollectionUtils.isEmpty(tempMapList)) {
			tempMapList = new ArrayList<Map<String, Object>>();
		}
		tempMapList.addAll((List<Map<String, Object>>) paramMap.get(key));
		map.put(key, tempMapList);
		long finishTime = System.currentTimeMillis();
		this.logger.info("家客设备相关数据追加时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param statementName
	 * @param object
	 */
	@SuppressWarnings({ "deprecation" })
	protected void delete(String statementName, Object object) {
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (object != null) {
				object = this.getCommonCuids(object);
				this.iBatisSynResDAO.getSqlMapClientTemplate().delete(statementName, object);
				if (this.dispatchMode.equals("2")) {
					this.iBatisTnmsDAO.getSqlMapClientTemplate().delete(statementName, object);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==删除数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("删除数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param taskCfgCuidList List<String>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getTaskCuid(List<String> taskCfgCuidList) {
		Assert.notNull(taskCfgCuidList, "任务配置list对象不能为空.");
		long curTime = System.currentTimeMillis();
		String taskCuid = "";
		List<Map<String, Object>> resultMapList = this.iBatisPonDAO.querySql(this.getSqlByTaskCuid(taskCfgCuidList));
		Assert.notNull(resultMapList, "任务表list对象不能为空.");
		for (Map<String, Object> resultMap : resultMapList) {
			taskCuid = MapUtils.getString(resultMap, "CUID");
		}
		long finishTime = System.currentTimeMillis();
		this.logger.info("获得任务CUID时间:" + (finishTime - curTime));
		return taskCuid;
	}

	/**
	 * 
	 * @param taskCfgCuidList List<String>
	 * @return
	 */
	protected String getSqlByTaskCuid(List<String> taskCfgCuidList) {
		long curTime = System.currentTimeMillis();
		String condition = "";
		for (String taskCfgCuid : taskCfgCuidList) {
			condition += ("'" + taskCfgCuid + "',");
		}
		condition = StringUtils.isEmpty(condition) ? "" : condition.substring(0, condition.length() - 1);

		String querySql = "SELECT T.CUID FROM T_ACT_TASK T WHERE T.RELATED_ORDER_CUID = '" + this.orderId + "'";
		if (!StringUtils.isEmpty(condition)) {
			querySql += (" AND T.RELATED_TASK_CFG_CUID IN (" + condition + ")");
		}
		querySql += (" ORDER BY T.CREATE_TIME DESC");
		long finishTime = System.currentTimeMillis();
		this.logger.info("获得任务查询SQL时间:" + (finishTime - curTime));
		return querySql;
	}
}