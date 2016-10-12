package com.boco.irms.app.jk.service.bo;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.common.util.except.UserException;
import com.boco.core.utils.lang.Assert;

public class JkDeviceBackoutBO extends AbstractJkDeviceBO {
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
	 * @return
	 */
	public String archived(List<String> segGroupCuidList, String orderId) {
		Assert.notNull(segGroupCuidList, "list对象不能为空.");
		long curTime = System.currentTimeMillis();
		String message = "0";
		try {
			Map<String, Object> map = this.getDeviceInfo(segGroupCuidList, orderId, "archived");
			this.deleteDeviceInfo(map);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入家客设备相关数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("==插入家客设备相关数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param statementName
	 * @param dispatchMode
	 * @param object
	 */
	@SuppressWarnings({ "deprecation" })
	protected void delete(String statementName, Object object) {
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			object = this.getCommonCuids(object);
			if (this.dispatchMode.equals("2")) {
				this.iBatisTnmsDAO.getSqlMapClientTemplate().delete(statementName, object);
				if (this.rollbackFlag.equals("1")) {
					this.iBatisSynResDAO.getSqlMapClientTemplate().delete(statementName, object);
				}
			} else {
				this.iBatisSynResDAO.getSqlMapClientTemplate().delete(statementName, object);
				if (this.rollbackFlag.equals("1")) {
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
}