package com.boco.irms.app.jk.rms.jkseggroup.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.utils.lang.Assert;
import com.boco.irms.app.jk.common.bo.JkCommonBO;
import com.boco.irms.app.jk.service.bo.AbstractJkDeviceBO;
import com.boco.irms.app.utils.CustomEnum;

public abstract class AbstractJkSegGroupBO extends JkCommonBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected AbstractJkDeviceBO abstractJkDeviceBO;

	/**
	 * 
	 * @param resultList List<Map<String, Object>>
	 * @param tableName
	 */
	public void insertDynamicTableBatch(List<Map<String, Object>> resultList, String tableName) {
		Assert.notEmpty(resultList, "参数集合不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			List<Record> records = new ArrayList<Record>();
			for (Map<String, Object> resultMap : resultList) {
				Record record = this.buildRecord(resultMap, tableName);
				records.add(record);
			}
			this.iBatisPonDAO.insertDynamicTableBatch(records);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("插入数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param cuid
	 * @param tableName
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querySql(String cuid, String tableName) {
		Assert.hasLength(cuid, "CUID不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		long curTime = System.currentTimeMillis();
		List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
		try {
			String condition = "CUID = '" + cuid + "'";
			resultMapList = this.iBatisPonDAO.querySql(this.getSql(condition, tableName));
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询数据时间:" + (finishTime - curTime));
		return resultMapList;
	}

	/**
	 * 
	 * @param segGroupCuid
	 * @param tableName
	 * @return int
	 */
	public void update(String segGroupCuid, String tableName) {
		Assert.hasLength(segGroupCuid, "单位工程CUID不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("CUID", segGroupCuid);
			paramMap.put("STATUS", String.valueOf(CustomEnum.DMProjectState._maintain));

			Map<String, Record> map = this.buildRecordMap(paramMap, tableName);
			List<Record> paramMapList = new ArrayList<Record>();
			List<Record> pkList = new ArrayList<Record>();
			paramMapList.add(map.get("paramRecord"));
			pkList.add(map.get("pkRecord"));
			this.iBatisPonDAO.updateDynamicTableBatch(paramMapList, pkList);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==更新数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("更新数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param paramMap
	 * @param tableName
	 */
	public void submit(Map<String, Object> paramMap, String tableName) {
		Assert.notEmpty(paramMap, "集合对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			String cuid = MapUtils.getString(paramMap, "CUID");
			List<Map<String, Object>> resultList = this.querySql(cuid, tableName);
			if (!CollectionUtils.isEmpty(resultList)) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Map<String, Object> resultMap = resultList.get(0);
				paramMap.put("CUID", resultMap.get("CUID"));
				paramMap.put("IS_APPLY", paramMap.get("IS_APPLY"));
				paramMap.put("STATUS", paramMap.get("STATUS"));
				paramMap.put("LAST_MODIFY_TIME", df.format(Calendar.getInstance().getTime()));

				Map<String, Record> map = this.buildRecordMap(paramMap, tableName);
				List<Record> paramMapList = new ArrayList<Record>();
				List<Record> pkList = new ArrayList<Record>();
				paramMapList.add(map.get("paramRecord"));
				pkList.add(map.get("pkRecord"));
				this.iBatisPonDAO.updateDynamicTableBatch(paramMapList, pkList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==提交数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("提交数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param segGroupCuid
	 * @param oldTableName
	 * @param newTableName
	 * @param flag
	 * @return String
	 */
	public String archived(String segGroupCuid, String oldTableName, String newTableName, boolean flag) {
		Assert.hasLength(segGroupCuid, "单位工程CUID不能为空.");
		Assert.hasLength(oldTableName, "临时表名不能为空.");
		Assert.hasLength(newTableName, "迁移表名不能为空.");
		long curTime = System.currentTimeMillis();
		String result = "true";
		try {
			if (flag) {
				List<Map<String, Object>> resultList = this.querySql(segGroupCuid, oldTableName);
				if (!CollectionUtils.isEmpty(resultList)) {
					this.insertDynamicTableBatch(resultList, newTableName);
				}
			}
		} catch (Exception e) {
			result = "false";
			e.printStackTrace();
			this.logger.info("==数据迁移失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("数据迁移时间:" + (finishTime - curTime));
		return result;
	}

	/**
	 * 
	 * @param segGroupCuid
	 * @return String
	 */
	public String validator(String segGroupCuid) {
		Assert.hasLength(segGroupCuid, "CUID不能为空.");
		String result = "true";
		try {
			List<String> segGroupCuidList = new ArrayList<String>();
			segGroupCuidList.add(segGroupCuid);
			Map<String, Object> map = this.abstractJkDeviceBO.getDeviceInfo(segGroupCuidList, "", "archived");
			if (CollectionUtils.isEmpty(map)) {
				result = "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==获得家客设备失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}
		return result;
	}

	public AbstractJkDeviceBO getAbstractJkDeviceBO() {
		return abstractJkDeviceBO;
	}

	public void setAbstractJkDeviceBO(AbstractJkDeviceBO abstractJkDeviceBO) {
		this.abstractJkDeviceBO = abstractJkDeviceBO;
	}
}