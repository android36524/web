package com.boco.irms.app.jk.service.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.utils.lang.Assert;

public class JkDevicePrecoverBO extends AbstractJkDeviceBO {
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
		long curTime = System.currentTimeMillis();
		String message = "true";
		try {
			Map<String, Object> map = this.getDeviceInfo(segGroupCuidList, "", "archived");
			this.insertDeviceInfo(map);

			Map<String, Object> sdeMap = this.getDeviceInfoWithSde(map);
			this.insertDynamicTableBatchWithSde(sdeMap);
			this.deleteDeviceInfoWithSde(sdeMap);

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

	/**
	 * 
	 * @param map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public void insertDynamicTableBatch(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			boolean flag = true;
			List<Record> records = new ArrayList<Record>();
			Iterator<String> iterator = map.keySet().iterator();
			for (; iterator.hasNext();) {
				String key = iterator.next();
				if (!key.contains("CUID")) {
					if (this.dispatchMode.equals("2")) {
						if (key.equals("T_SPACE_STANDARD_ADDRESS") || key.equals("BUSINESS_COMMUNITY") || key.equals("T_ROFH_FULL_ADDRESS")) {
							flag = true;
						} else {
							flag = false;
						}
					}

					if (flag) {
						List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get(key);
						for (Map<String, Object> resultMap : resultMapList) {
							String bmClassId = MapUtils.getString(resultMap, "bmClassId");
							Date time = new Date();
							resultMap.put("OBJECTID", this.getObjectId(bmClassId));
							resultMap.put("CREATE_TIME", time);
							resultMap.put("LAST_MODIFY_TIME", time);
							Record record = this.buildRecord(resultMap, bmClassId);
							records.add(record);
						}
					}
				}
			}

			if (this.operateType.equals("archived")) {
				if (this.dispatchMode.equals("2")) {
					this.iBatisTnmsDAO.insertDynamicTableBatch(records);
				} else {
					this.iBatisSynResDAO.insertDynamicTableBatch(records);
				}
			} else {
				this.iBatisDesignerDAO.insertDynamicTableBatch(records);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入模式" + this.dispatchMode + "库失败==" + e.getMessage());
			this.rollbackFlag = "1";
			throw new UserException(e.getMessage());
		} finally {
			this.deleteDeviceInfo(map);
			this.rollbackFlag = "0";
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("==插入模式" + this.dispatchMode + "库时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	protected void deleteDeviceInfo(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				List<String> communityCuidList = (List<String>) map.get("BUSINESS_COMMUNITY_CUID");
				List<String> addressCuidList = (List<String>) map.get("T_ROFH_FULL_ADDRESS_CUID");
				List<String> standardAddCuidList = (List<String>) map.get("T_SPACE_STANDARD_ADDRESS_CUID");
				List<String> coverCuidList = (List<String>) map.get("GPON_COVER_CUID");
				List<String> logicLinkCuidList = (List<String>) map.get("T_LOGIC_LINK_CUID");
				List<String> topoLinkCuidList = (List<String>) map.get("PON_TOPO_LINK_CUID");
				List<String> ptpCuidList = (List<String>) map.get("PTP_CUID");
				List<String> cardCuidList = (List<String>) map.get("CARD_CUID");
				List<String> onuCuidList = (List<String>) map.get("AN_ONU_CUID");
				List<String> posCuidList = (List<String>) map.get("AN_POS_CUID");
				List<String> oltCuidList = (List<String>) map.get("TRANS_ELEMENT_CUID");

				this.delete("EQUIP_DM.deleteBusCommunityByCuids", communityCuidList);
				this.delete("EQUIP_DM.deleteAddressByCuids", addressCuidList);
				this.delete("EQUIP_DM.deleteStandardAddByCuids", standardAddCuidList);

				if (this.dispatchMode.equals("1")) {
					this.delete("EQUIP_DM.deleteCoverByCuids", coverCuidList);
					this.delete("EQUIP_DM.deleteLogicLinkByCuids", logicLinkCuidList);
					this.delete("EQUIP_DM.deleteTopoLinkByCuids", topoLinkCuidList);
					this.delete("EQUIP_DM.deletePtpByCuids", ptpCuidList);
					this.delete("EQUIP_DM.deleteCardByCuids", cardCuidList);
					this.delete("EQUIP_DM.deleteOnuByCuids", onuCuidList);
					this.delete("EQUIP_DM.deletePosByCuids", posCuidList);
					this.delete("EQUIP_DM.deleteOltByCuids", oltCuidList);
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