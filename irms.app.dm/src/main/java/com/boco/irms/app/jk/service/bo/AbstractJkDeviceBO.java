package com.boco.irms.app.jk.service.bo;

import java.io.Reader;
import java.sql.Clob;
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
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.lang.Assert;
import com.boco.irms.app.jk.common.bo.JkCommonBO;
import com.boco.sf.rms.base.CommonCuids;

public abstract class AbstractJkDeviceBO extends JkCommonBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 单位工程CUID的list
	 */
	protected List<String> segGroupCuidList;

	/**
	 * 订单号
	 */
	protected String orderId;

	/**
	 * 模式类型 开通和调度采用 模式一 模式二
	 */
	protected String dispatchMode;

	/**
	 * 操作类型
	 */
	protected String operateType;

	/**
	 * 是否删除设计库PON数据 1是2否
	 */
	protected String ponDeleteFlag;

	/**
	 * 是否校验PON资源link数据 1是2否
	 */
	protected String ponLinkValidator;

	/**
	 * 省份区域CUID
	 */
	protected String provinceCuid;

	/**
	 * 项目批复类型 0未知1批复项目2虚拟项目
	 */
	protected long auditFlag;

	/**
	 * 回滚标识 0不回滚1回滚
	 */
	protected String rollbackFlag = "0";

	public static Map<String, String> tableCnNameMap = new HashMap<String, String>();
	
	static {
		tableCnNameMap.put("CARD", "板卡");
		tableCnNameMap.put("PTP", "端口");
		tableCnNameMap.put("PON_TOPO_LINK", "链路");
		tableCnNameMap.put("T_LOGIC_LINK", "链路");
		tableCnNameMap.put("GPON_COVER", "覆盖地址");
		tableCnNameMap.put("T_ROFH_FULL_ADDRESS", "标准地址");
	}

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @return
	 */
	public abstract String apply(List<String> segGroupCuidList);

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @param orderId
	 * @return
	 */
	public abstract String archived(List<String> segGroupCuidList, String orderId);

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return
	 */
	protected abstract void insertDeviceInfo(Map<String, Object> map);

	/**
	 * 
	 * @param segGroupCuid
	 * @param orderId
	 * @param operateType
	 */
	protected void init(List<String> segGroupCuidList, String orderId, String operateType) {
		this.segGroupCuidList = segGroupCuidList;
		this.orderId = orderId;
		this.operateType = operateType;
		this.dispatchMode = SysProperty.getInstance().getValue("dispatchMode");
		this.dispatchMode = StringUtils.isEmpty(this.dispatchMode) ? "1" : this.dispatchMode;
		this.ponDeleteFlag = SysProperty.getInstance().getValue("ponDeleteFlag");
		this.ponDeleteFlag = StringUtils.isEmpty(this.ponDeleteFlag) ? "2" : this.ponDeleteFlag;
		this.ponLinkValidator = SysProperty.getInstance().getValue("ponLinkValidator");
		this.ponLinkValidator = StringUtils.isEmpty(this.ponLinkValidator) ? "1" : this.ponLinkValidator;
		this.provinceCuid = SysProperty.getInstance().getValue("district");
	}

	/**
	 * 
	 * @return map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getDeviceCuid() {
		Assert.notNull(this.segGroupCuidList, "list对象不能为空.");
		Map<String, Object> map = new HashMap<String, Object>();
		long curTime = System.currentTimeMillis();
		try {
			String cuidCondition = "";
			for (String segGroupCuid : this.segGroupCuidList) {
				cuidCondition += ("'" + segGroupCuid + "',");
			}
			cuidCondition = StringUtils.isEmpty(cuidCondition) ? "" : cuidCondition.substring(0, cuidCondition.length() - 1);
			if (!StringUtils.isEmpty(cuidCondition)) {
				String condition = "RELATED_SEGGROUP_CUID IN (" + cuidCondition + ")";
				List<Map<String, Object>> resultMapList = this.iBatisDesignerDAO.querySql(this.getSql(condition, "SEGGROUP_TO_RES"));
				Assert.notNull(resultMapList, "关系表list对象不能为空.");
				for (Map<String, Object> resultMap : resultMapList) {
					String relatedBmClassTypeCuid = MapUtils.getString(resultMap, "RELATED_BMCLASSTYPE_CUID");
					String relatedResCuid = MapUtils.getString(resultMap, "RELATED_RES_CUID");
					String key = relatedBmClassTypeCuid + "_CUID";
					List<String> deviceCuidList = (List<String>) map.get(key);
					if (CollectionUtils.isEmpty(deviceCuidList)) {
						deviceCuidList = new ArrayList<String>();
					}
					deviceCuidList.add(relatedResCuid);
					map.put(key, deviceCuidList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==获得家客设备CUID失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("获得家客设备CUID时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param statementName
	 * @param tableName
	 * @param object Object
	 * @return map Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	protected Map<String, Object> getResourceInfo(String statementName, String tableName, Object object) {
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		Assert.hasLength(this.operateType, "操作类型不能为空.");
		long curTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (object != null) {
				object = this.getCommonCuids(object);
				List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
				if (this.operateType.equals("archived")) {
					resultMapList = this.iBatisPonDAO.getSqlMapClientTemplate().queryForList(statementName, object);
				} else if (this.operateType.equals("apply")) {
					resultMapList = this.iBatisSynResDAO.getSqlMapClientTemplate().queryForList(statementName, object);
				} else {
					resultMapList = this.iBatisSdeDesignDAO.getSqlMapClientTemplate().queryForList(statementName, object);
				}

				Assert.notNull(resultMapList, "查询结果集list对象不能为空.");
				List<String> cuidList = new ArrayList<String>();
				for (Map<String, Object> resultMap : resultMapList) {
					cuidList.add(MapUtils.getString(resultMap, "CUID"));
					if (!tableName.contains("GEO")) {
						resultMap.put("bmClassId", tableName);
					}
				}

				map.put(tableName, resultMapList);
				map.put(tableName + "_CUID", cuidList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询" + tableName + "数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询" + tableName + "数据时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param coverMapList List<Map<String, Object>>
	 * @return map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getAddressInfo(List<Map<String, Object>> coverMapList) {
		Assert.notNull(coverMapList, "list对象不能为空.");
		long curTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!CollectionUtils.isEmpty(coverMapList)) {
				List<String> cuidList = new ArrayList<String>();
				for (Map<String, Object> coverMap : coverMapList) {
					String standardAddrCuid = MapUtils.getString(coverMap, "STANDARD_ADDR");
					if (!cuidList.contains(standardAddrCuid)) {
						cuidList.add(standardAddrCuid);
					}
				}

				Map<String, Object> addressMap = this.getResourceInfo("EQUIP_DM.queryFullAddressByCuids", "T_ROFH_FULL_ADDRESS", cuidList);
				this.validator(addressMap, "T_ROFH_FULL_ADDRESS", "CUID", cuidList);
				this.validator(addressMap, "T_ROFH_FULL_ADDRESS", "CUID", (List<String>) map.get("T_ROFH_FULL_ADDRESS_CUID"));
				if (this.provinceCuid.equals("DISTRICT-00001-00022")) {
					this.getProjectAuditFlag();
					this.validator((List<Map<String, Object>>) addressMap.get("T_ROFH_FULL_ADDRESS"));
				}
				map.putAll(addressMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询标准地址失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询标准地址时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param segGroupCuidList List<String>
	 * @param orderId
	 * @param operateType
	 * @return map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDeviceInfo(List<String> segGroupCuidList, String orderId, String operateType) {
		Assert.notNull(segGroupCuidList, "list对象不能为空.");
		Assert.hasLength(operateType, "操作类型不能为空.");
		long curTime = System.currentTimeMillis();
		this.init(segGroupCuidList, orderId, operateType);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = this.getDeviceCuid();
			Assert.notNull(map, "该单位工程未录入资源.");
			List<String> onuCuidList = (List<String>) map.get("AN_ONU_CUID");
			map.putAll(this.getResourceInfo("EQUIP_DM.queryOnuByCuids", "AN_ONU", onuCuidList));

			List<String> oltCuidList = (List<String>) map.get("AN_OLT_CUID");
			map.putAll(this.getResourceInfo("EQUIP_DM.queryOltByCuids", "TRANS_ELEMENT", oltCuidList));

			List<String> posCuidList = (List<String>) map.get("AN_POS_CUID");
			map.putAll(this.getResourceInfo("EQUIP_DM.queryPosByCuids", "AN_POS", posCuidList));

			if (this.provinceCuid.equals("DISTRICT-00001-00022")) {
				List<String> addressCuidList = (List<String>) map.get("T_SPACE_STANDARD_ADDRESS_CUID");
				map.putAll(this.getResourceInfo("EQUIP_DM.queryAddressByCuids", "T_SPACE_STANDARD_ADDRESS", addressCuidList));

				List<String> communityCuidList = (List<String>) map.get("BUSINESS_COMMUNITY_CUID");
				map.putAll(this.getResourceInfo("EQUIP_DM.queryBusCommunityByCuids", "BUSINESS_COMMUNITY", communityCuidList));
			}

			List<String> cuidList = new ArrayList<String>();
			List<String> origNeCuidList = new ArrayList<String>();
			if (!CollectionUtils.isEmpty(onuCuidList)) {
				cuidList.addAll(onuCuidList);
				origNeCuidList.addAll(onuCuidList);
			}
			if (!CollectionUtils.isEmpty(oltCuidList)) {
				cuidList.addAll(oltCuidList);
				if (!this.provinceCuid.equals("DISTRICT-00001-00022")) {
					origNeCuidList.addAll(oltCuidList);
				}
			}
			if (!CollectionUtils.isEmpty(posCuidList)) {
				cuidList.addAll(posCuidList);
				if (this.provinceCuid.equals("DISTRICT-00001-00022")) {
					origNeCuidList.addAll(this.filterFirstPos(map));
				} else {
					origNeCuidList.addAll(posCuidList);
				}
			}

			Map<String, Object> cardMap = this.getResourceInfo("EQUIP_DM.queryCardByCuids", "CARD", cuidList);
			this.validator(cardMap, "CARD", "RELATED_DEVICE_CUID", cuidList);
			map.putAll(cardMap);

			Map<String, Object> ptpMap = this.getResourceInfo("EQUIP_DM.queryPtpByCuids", "PTP", cuidList);
			this.validator(ptpMap, "PTP", "RELATED_NE_CUID", cuidList);
			map.putAll(ptpMap);

			Map<String, Object> linkMap = this.getResourceInfo("EQUIP_DM.queryTopoLinkByCuids", "PON_TOPO_LINK", origNeCuidList);
			if (this.ponLinkValidator.equals("1") && !this.provinceCuid.equals("DISTRICT-00001-00022")) {
				this.validator(linkMap, "PON_TOPO_LINK", "ORIG_NE_CUID", origNeCuidList);
			}
			map.putAll(linkMap);

			Map<String, Object> oltLinkMap = this.getResourceInfo("EQUIP_DM.queryLogicLinkByCuids", "T_LOGIC_LINK", oltCuidList);
			if (this.ponLinkValidator.equals("1") && !this.provinceCuid.equals("DISTRICT-00001-00022")) {
				this.validator(oltLinkMap, "T_LOGIC_LINK", "RELATED_ORIG_LOGIC_CUID", oltCuidList);
			}
			map.putAll(oltLinkMap);

			Map<String, Object> coverMap = this.getResourceInfo("EQUIP_DM.queryCoverByCuids", "GPON_COVER", origNeCuidList);
			this.validator(coverMap, "GPON_COVER", "RELATED_NE_CUID", origNeCuidList);
			map.putAll(coverMap);

			map.putAll(this.getAddressInfo((List<Map<String, Object>>) coverMap.get("GPON_COVER")));
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询家客设备相关数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询家客设备相关数据时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return sdeMap Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDeviceInfoWithSde(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		Map<String, Object> sdeMap = new HashMap<String, Object>();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				this.operateType = "archivedSde";
				List<String> communityCuidList = (List<String>) map.get("BUSINESS_COMMUNITY_CUID");
				sdeMap.putAll(this.getResourceInfo("EQUIP_DM_GEO.queryGeoGridCommunityByCuids", "GEO_GRID_COMMUNITY", communityCuidList));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==查询家客设计SDE库数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("查询家客设计SDE库数据时间:" + (finishTime - curTime));
		return sdeMap;
	}

	/**
	 * 根据ClassName获取ObjectId
	 * 
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected long getObjectId(String tableName) {
		long curTime = System.currentTimeMillis();
		long objectId = 0L;
		try {
			if (this.dispatchMode.equals("2")) {
				if (this.operateType.equals("archived")) {
					objectId = (Long) this.iBatisTnmsDAO.getSqlMapClientTemplate().queryForObject("COMMON_OBJECT.getObjectID", tableName);
				} else {
					objectId = (Long) this.iBatisSdeDAO.getSqlMapClientTemplate().queryForObject("COMMON_OBJECT.getObjectID", tableName);
				}
			} else {
				objectId = (Long) this.iBatisSynResDAO.getSqlMapClientTemplate().queryForObject("COMMON_OBJECT.getObjectID", tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==" + tableName + "的objectId生成失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}
		long finishTime = System.currentTimeMillis();
		this.logger.info(tableName + "的objectId生成时间:" + (finishTime - curTime));
		return objectId;
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
			List<Record> records = new ArrayList<Record>();
			Iterator<String> iterator = map.keySet().iterator();
			for (; iterator.hasNext();) {
				String key = iterator.next();
				if (!key.contains("CUID")) {
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
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertDynamicTableBatchWithSde(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			List<Map> resultMapList = (List<Map>) map.get("GEO_GRID_COMMUNITY");
			if (!CollectionUtils.isEmpty(resultMapList)) {
				for (Map<String, Object> resultMap : resultMapList) {
					resultMap.put("OBJECTID", this.getObjectId("GEO_GRID_COMMUNITY"));
					String shape = this.tranformToString(resultMap.get("SHAPE"));
					resultMap.put("SHAPE", "sql:sde.ST_PolyFromText('" + shape + "'," + this.getSRIDByTable("GEO_GRID_COMMUNITY") + ")");
				}
				this.iBatisSdeDAO.insertDynamicTableBatch("GEO_GRID_COMMUNITY", resultMapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==插入正式SDE库失败==" + e.getMessage());
			this.rollbackFlag = "1";
			throw new UserException(e.getMessage());
		} finally {
			this.deleteDeviceInfoWithSde(map);
			this.rollbackFlag = "0";
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("插入正式SDE库时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	protected void deleteDeviceInfoWithSde(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				List<String> communityCuidList = (List<String>) map.get("GEO_GRID_COMMUNITY_CUID");
				this.deleteWithSde("EQUIP_DM_GEO.deleteGeoGridCommunityByCuids", communityCuidList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==删除SDE库数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("删除SDE库数据时间:" + (finishTime - curTime));
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
				this.delete("EQUIP_DM.deleteCoverByCuids", coverCuidList);
				this.delete("EQUIP_DM.deleteLogicLinkByCuids", logicLinkCuidList);
				this.delete("EQUIP_DM.deleteTopoLinkByCuids", topoLinkCuidList);
				this.delete("EQUIP_DM.deletePtpByCuids", ptpCuidList);
				this.delete("EQUIP_DM.deleteCardByCuids", cardCuidList);
				this.delete("EQUIP_DM.deleteOnuByCuids", onuCuidList);
				this.delete("EQUIP_DM.deletePosByCuids", posCuidList);
				this.delete("EQUIP_DM.deleteOltByCuids", oltCuidList);
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
	 * @param statementName
	 * @param object
	 */
	@SuppressWarnings({ "deprecation" })
	protected void delete(String statementName, Object object) {
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		Assert.hasLength(this.ponDeleteFlag, "是否删除设计库PON数据不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (object != null) {
				object = this.getCommonCuids(object);
				if (this.ponDeleteFlag.equals("1")) {
					this.iBatisPonDAO.getSqlMapClientTemplate().delete(statementName, object);
				}

				if (this.rollbackFlag.equals("1")) {
					this.iBatisSynResDAO.getSqlMapClientTemplate().delete(statementName, object);
					this.iBatisTnmsDAO.getSqlMapClientTemplate().delete(statementName, object);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==删除模式" + this.dispatchMode + "库数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("删除模式" + this.dispatchMode + "库数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param statementName
	 * @param object
	 */
	@SuppressWarnings({ "deprecation" })
	protected void deleteWithSde(String statementName, Object object) {
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		Assert.hasLength(this.ponDeleteFlag, "是否删除设计库PON数据不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (object != null) {
				object = this.getCommonCuids(object);
				if (this.ponDeleteFlag.equals("1")) {
					this.iBatisSdeDesignDAO.getSqlMapClientTemplate().delete(statementName, object);
				}

				if (this.rollbackFlag.equals("1")) {
					this.iBatisSdeDAO.getSqlMapClientTemplate().delete(statementName, object);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==删除SDE库数据失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("删除SDE库数据时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param map
	 * @param tableName
	 * @param columnName
	 * @param cuidList
	 */
	@SuppressWarnings("unchecked")
	protected void validator(Map<String, Object> map, String tableName, String columnName, List<String> cuidList) {
		long curTime = System.currentTimeMillis();
		if (!CollectionUtils.isEmpty(cuidList)) {
			List<String> resultList = (List<String>) ((ArrayList<String>) cuidList).clone();
			List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get(tableName);
			if (!CollectionUtils.isEmpty(resultMapList)) {
				for (Map<String, Object> resultMap : resultMapList) {
					resultList.remove(MapUtils.getString(resultMap, columnName));
				}

				if (!CollectionUtils.isEmpty(resultList)) {
					this.logger.info("==校验设备关联" + tableName + "不存在==" + JSONObject.toJSONString(resultList));
					throw new UserException("设备关联" + tableCnNameMap.get(tableName) + "不存在");
				}
			} else {
				if (!tableName.equals("T_LOGIC_LINK")) {
					if ((tableName.equals("PON_TOPO_LINK") && !this.provinceCuid.equals("DISTRICT-00001-00022")) || !tableName.equals("PON_TOPO_LINK")) {
						this.logger.info("==校验设备关联" + tableName + "不存在==");
						throw new UserException("设备关联" + tableCnNameMap.get(tableName) + "不存在");
					}
				}
			}
		}
		long finishTime = System.currentTimeMillis();
		this.logger.info("校验设备关联" + tableCnNameMap.get(tableName) + "时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param addressMapList List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	protected void validator(List<Map<String, Object>> addressMapList) {
		long curTime = System.currentTimeMillis();
			if (this.auditFlag == 1L) {
				Map<String, Object> busCommunityMap = this.getResourceInfo("EQUIP_DM.queryBusCommunityBySegGroupCuids", "BUSINESS_COMMUNITY", this.segGroupCuidList);
				List<Map<String, Object>> communityMapList = (List<Map<String, Object>>) busCommunityMap.get("BUSINESS_COMMUNITY");
				Assert.notNull(communityMapList, "小区list对象不能为空.");
				List<String> communityNameList = new ArrayList<String>();
				for (Map<String, Object> communityMap : communityMapList) {
					String communityName = MapUtils.getString(communityMap, "LABEL_CN", "");
					if (!communityNameList.contains(communityName)) {
						communityNameList.add(communityName);
					}
				}
				this.logger.info("==指定小区范围==" + JSONObject.toJSONString(communityNameList));

				for (Map<String, Object> addressMap : addressMapList) {
					String tempCommunityName = "";
					String addressName = MapUtils.getString(addressMap, "LABEL_CN", "");
					String tempAddressName = addressName.replace("|", ";");
					String[] addressNameArray = tempAddressName.split(";");
					for (int i = 0; i < 6; i++) {
						tempCommunityName += addressNameArray[i];
					}

					if (!communityNameList.contains(tempCommunityName)) {
						this.logger.info("==校验标准地址【" + addressName + "】不属于指定小区==");
						throw new UserException("==校验标准地址【" + addressName + "】不属于指定小区==");
					}
				}
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("校验标准地址属于指定小区时间:" + (finishTime - curTime));
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void getProjectAuditFlag() {
		long curTime = System.currentTimeMillis();
		String sql =
			"SELECT PM.CUID, PM.AUDIT_FLAG FROM T_ATTEMP_SEG_GROUP SG, PROJECT_MANAGEMENT PM WHERE SG.RELATED_PROJECT_CUID = PM.CUID";
		String condition = "";
		for (String segGroupCuid : this.segGroupCuidList) {
			condition += ("'" + segGroupCuid + "',");
		}
		condition = StringUtils.isEmpty(condition) ? "" : condition.substring(0, condition.length() - 1);
		if (!StringUtils.isEmpty(condition)) {
			sql += (" AND SG.CUID IN (" + condition + ")");
		}
		List<Map<String, Object>> resultMapList = this.iBatisPonDAO.querySql(sql);
		if (!CollectionUtils.isEmpty(resultMapList)) {
			this.auditFlag = MapUtils.getLongValue(resultMapList.get(0), "AUDIT_FLAG", 2L);
		}
		long finishTime = System.currentTimeMillis();
		this.logger.info("获得项目批复类型时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object getCommonCuids(Object object) {
		if (object instanceof List) {
			List<String> cuidList = (List<String>) object;
			CommonCuids cuids = new CommonCuids();
			cuids.setCuidList(cuidList);
			object = cuids;
		}
		return object;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return
	 */
	protected void updateDeviceInfo(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		Assert.hasLength(this.dispatchMode, "模式标识不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			if (!CollectionUtils.isEmpty(map)) {
				if (this.operateType.equals("archived")) {
					if (this.dispatchMode.equals("2")) {
						this.dispatchMode = "1";
						this.updateDynamicTableBatch(map);
						this.dispatchMode = "2";
					}
				}
				this.updateDynamicTableBatch(map);
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
	 * @param map Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public void updateDynamicTableBatch(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			List<Record> paramMapList = new ArrayList<Record>();
			List<Record> pkList = new ArrayList<Record>();
			Iterator<String> iterator = map.keySet().iterator();
			for (; iterator.hasNext();) {
				String key = iterator.next();
				if (!key.contains("CUID")) {
					List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get(key);
					for (Map<String, Object> resultMap : resultMapList) {
						resultMap.put("LAST_MODIFY_TIME", new Date());
						Map<String, Record> paramMap = this.buildRecordMap(resultMap, MapUtils.getString(resultMap, "RELATED_BMCLASSTYPE_CUID"));
						paramMapList.add(paramMap.get("paramRecord"));
						pkList.add(paramMap.get("pkRecord"));
					}
				}
			}

			if (this.operateType.equals("archived")) {
				if (this.dispatchMode.equals("2")) {
					this.iBatisTnmsDAO.updateDynamicTableBatch(paramMapList, pkList);
				} else {
					this.iBatisSynResDAO.updateDynamicTableBatch(paramMapList, pkList);
				}
			} else {
				this.iBatisDesignerDAO.updateDynamicTableBatch(paramMapList, pkList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==更新模式" + this.dispatchMode + "库失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("更新模式" + this.dispatchMode + "库时间:" + (finishTime - curTime));
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @param type tnms 传输区域转综资  irms 综资区域转传输
	 * @return Map<String, Object>
	 */
	protected Map<String, Object> convertDistrict(Map<String, Object> map, String type) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();

		List<String> districtCuids = this.getDistrict(map);
		Map<String, String> districtCuidMap = this.getDistrict(districtCuids, type.toLowerCase());
		map = this.convertDistrict(map, districtCuidMap);

		long finishTime = System.currentTimeMillis();
		this.logger.info("转换综资地市时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return List<String>
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getDistrict(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		List<String> districtCuids = new ArrayList<String>();
		Iterator<String> iterator = map.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.contains("CUID")) {
				List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get(key);
				if (!CollectionUtils.isEmpty(resultMapList)) {
					for (Map<String, Object> resultMap : resultMapList) {
						String districtCuid = MapUtils.getString(resultMap, "RELATED_DISTRICT_CUID", "");
						if (!districtCuids.contains(districtCuid) && districtCuid.length() >= 26) {
							districtCuids.add(districtCuid);
						}

						String cuid = MapUtils.getString(resultMap, "CUID", "");
						if (!districtCuids.contains(cuid) && cuid.contains("DISTRICT")) {
							districtCuids.add(cuid);
						}

						String relatedAddressCuid = MapUtils.getString(resultMap, "RELATED_ADDRESS_CUID", "");
						if (!districtCuids.contains(relatedAddressCuid) && relatedAddressCuid.contains("DISTRICT")) {
							districtCuids.add(relatedAddressCuid);
						}
					}
				}
			}
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("获得传输地市时间:" + (finishTime - curTime));
		return districtCuids;
	}

	/**
	 * 
	 * @param districtCuids List<String>
	 * @param source 数据来源 tnms传输 irms综资
	 * @return Map<String, String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected Map<String, String> getDistrict(List<String> districtCuids, String source) {
		Assert.notNull(districtCuids, "list对象不能为空.");
		long curTime = System.currentTimeMillis();
		Map<String, String> districtCuidMap = new HashMap<String, String>();
		try {
			if (!CollectionUtils.isEmpty(districtCuids)) {
				CommonCuids commonCuids = (CommonCuids) this.getCommonCuids(districtCuids);
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(source.toLowerCase() + "CommonCuids", commonCuids);
				List<Map<String, Object>> maplist = this.iBatisPonDAO.getSqlMapClientTemplate().queryForList("EQUIP_DM.queryDistrictByCuids", param);
				if (!CollectionUtils.isEmpty(maplist)) {
					for (Map<String, Object> tempMap : maplist) {
						String tnmsDistrictCuid = MapUtils.getString(tempMap, "TNMS_CUID", "");
						String irmsDistrictCuid = MapUtils.getString(tempMap, "IRMS_CUID", "");
						districtCuidMap.put(tnmsDistrictCuid, irmsDistrictCuid);
						districtCuidMap.put(irmsDistrictCuid, tnmsDistrictCuid);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==获得综资和传输地市关联关系失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("获得综资和传输地市关联关系时间:" + (finishTime - curTime));
		return districtCuidMap;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @param districtCuidMap Map<String, String>
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> convertDistrict(Map<String, Object> map, Map<String, String> districtCuidMap) {
		Assert.notNull(map, "map对象不能为空.");
		Assert.notNull(districtCuidMap, "综资和传输地市map对象不能为空.");
		long curTime = System.currentTimeMillis();
		Iterator<String> iterator = map.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.contains("CUID")) {
				List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get(key);
				for (int i = 0; i < resultMapList.size(); i++) {
					Map<String, Object> resultMap = resultMapList.get(i);
					String districtCuid = MapUtils.getString(resultMap, "RELATED_DISTRICT_CUID", "");
					if (districtCuid.length() >= 26) {
						resultMap.put("RELATED_DISTRICT_CUID", MapUtils.getString(districtCuidMap, districtCuid, ""));
					}
					resultMapList.set(i, resultMap);
				}
				map.put(key, resultMapList);
			}
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("转换综资地市时间:" + (finishTime - curTime));
		return map;
	}

	/**
	 * 
	 * @param map Map<String, Object>
	 * @return List<String>
	 */
	@SuppressWarnings("unchecked")
	protected List<String> filterFirstPos(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		List<String> cuidList = new ArrayList<String>();
		List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) map.get("AN_POS");
		if (!CollectionUtils.isEmpty(resultMapList)) {
			for (Map<String, Object> resultMap : resultMapList) {
				String cuid = MapUtils.getString(resultMap, "CUID");
				String posType = MapUtils.getString(resultMap, "POS_TYPE", "2");
				if (posType.equals("2") && !cuidList.contains(cuid)) {
					cuidList.add(cuid);
				}
			}
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("过滤一级分光器时间:" + (finishTime - curTime));
		return cuidList;
	}

	private String tranformToString(Object clob) {
		if (clob instanceof Clob) {
			String result = null;
			Reader inStream = null;
			try {
				inStream = ((Clob) clob).getCharacterStream();
				char[] c = new char[(int) ((Clob) clob).length()];
				inStream.read(c);
				// data是读出并需要返回的数据，类型是String
				result = new String(c);
				inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (inStream != null) {
						inStream.close();
					}
				} catch (Exception ex) {
				}
			}
			return result;
		} else {
			return clob.toString();
		}
	}

	@SuppressWarnings("unchecked")
	protected int getSRIDByTable(String tableName) {
		String sql = "select SRID from SDE.ST_GEOMETRY_COLUMNS where table_name = '" + tableName + "' ";
		List<Map<String, Object>> executeSql = this.iBatisSdeDAO.querySql(sql);
		if (executeSql != null && executeSql.size() > 0) {
			Map<String, Object> map = executeSql.get(0);
			return ((Number) map.get("SRID")).intValue();
		}
		return 1;
	}

	/**
	 * 批量执行存储过程
	 * 
	 * @param map Map<String, Object>
	 */
	public void executeProcedureBatch(Map<String, Object> map) {
		Assert.notNull(map, "map对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			this.executeProcedure(this.iBatisSynResDAO, "EQUIP_DM_ARCHIVED.archivedStandardAddress", map);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==批量执行存储过程失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}

		long finishTime = System.currentTimeMillis();
		this.logger.info("批量执行存储过程时间:" + (finishTime - curTime));
	}

	/**
	 * 执行存储过程
	 * 
	 * @param iBatisDAO
	 * @param statementName
	 * @param object
	 */
	protected void executeProcedure(IbatisDAO iBatisDAO, String statementName, Object object) {
		Assert.notNull(iBatisDAO, "IbatisDAO对象不能为空.");
		Assert.hasLength(statementName, "调用方法名称不能为空.");
		Assert.notNull(object, "object对象不能为空.");
		long curTime = System.currentTimeMillis();
		try {
			iBatisDAO.getSqlMapClient().queryForObject(statementName, object);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("==执行存储过程失败==" + e.getMessage());
			throw new UserException(e.getMessage());
		}
		long finishTime = System.currentTimeMillis();
		this.logger.info("执行存储过程时间:" + (finishTime - curTime));
	}
}