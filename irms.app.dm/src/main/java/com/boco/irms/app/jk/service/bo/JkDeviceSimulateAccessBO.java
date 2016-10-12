package com.boco.irms.app.jk.service.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.common.util.except.UserException;
import com.boco.core.utils.lang.Assert;

public class JkDeviceSimulateAccessBO extends AbstractJkDeviceBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @return
	 */
	public String apply(List<String> segGroupCuidList) {
		return null;
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
		String message = "true";
		try {
			this.getDeviceInfo(segGroupCuidList, "", "archived");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("v_cuid", segGroupCuidList.get(0));
			this.executeProcedureBatch(paramMap);
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
		Assert.hasLength(dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				this.insertDynamicTableBatch(map);
				if (this.dispatchMode.equals("2")) {
					this.dispatchMode = "1";
					map = this.convertDistrict(map, "tnms");
					this.insertDynamicTableBatch(map);
					this.dispatchMode = "2";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入家客设备相关数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("==插入家客设备相关数据时间:" + (finishTime - curTime));
	}
}