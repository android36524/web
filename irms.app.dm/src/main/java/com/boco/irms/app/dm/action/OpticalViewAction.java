package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;

public class OpticalViewAction extends XmlTemplateGridBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, EditorColumnMeta> editorColumnMetaMap = new HashMap<String, EditorColumnMeta>();

	@SuppressWarnings("rawtypes")
	public List getOpticalLists(String CUID) throws IOException {
		String method = "IOpticalBO.getOpticalBySelectSqlExt";// 调用查询光纤接口方法
		Map<String, String> columnMap = getColumnMap();
		editorColumnMetaMap.clear();
		
		String sql = " CUID IN ( SELECT OPTICAL_CUID FROM OPTICAL_TO_FIBER WHERE WIRE_SYSTEM_CUID ='"+ CUID + "')";
		
		// 从传输服务查询数据
		DboCollection results = new DboCollection();
		try {
			results = (DboCollection) BoCmdFactory.getInstance().execBoCmd(method, new BoQueryContext(), sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}
		List<Map> list = new ArrayList<Map>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.getAttrField(com.boco.transnms.common.dto.Optical.CLASS_NAME, i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map<String, Object> map = new HashMap<String, Object>();
				for (String columnName : columnMap.values()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName, value));
				}
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 根据字段类型转化值
	 * 
	 * @param columnName
	 * @param value
	 * @return
	 */
	public Object convertObject(String columnName, Object value) {
		Object obj = value;
		if ("RELATED_DISTRICT_CUID".equals(columnName) || "RELATED_SPACE_CUID".equals(columnName)) {
			if (value instanceof String) {
				District dist = DistrictCacheModel.getInstance().getDistrictByCUID(String.valueOf(value));
				if (dist != null) {
					obj = dist.getLabelCn();
				}
			} else if (value instanceof GenericDO) {
				obj = ((GenericDO) value).getAttrValue("LABEL_CN");
			}
		} else {
			Map<String, String> enumMap = getEnumMap();
			for (String columnName_ : enumMap.keySet()) {
				if (value instanceof Boolean) {
					value = (Boolean) value ? 1L : 0L;
				}
				if (value != null && columnName_.equals(columnName)) {
					String code = enumMap.get(columnName_);
					Object[] gcEnum = EnumTypeManager.getInstance().getEnumTypes(code);
					for (Object oEnum : gcEnum) {
						EnumType etype = (EnumType) oEnum;
						if (((Long) etype.value).longValue() == Long.parseLong(String.valueOf(value))) {
							obj = etype.dispalyName;
						} else {
							if (value instanceof GenericDO) {
								obj = ((GenericDO) value).getAttrValue("LABEL_CN");
							}
						}
					}
				} else {
					if (value instanceof GenericDO) {
						obj = ((GenericDO) value).getAttrValue("LABEL_CN");
					}
				}
			}
		}
		return obj;
	}

	/**
	 * 封装列表map
	 * 
	 * @return
	 */
	public Map<String, String> getColumnMap() {
		Map<String, String> columnMap = new HashMap<String, String>();
		columnMap.put("0", "LABEL_CN");
		columnMap.put("1", "MAKE_FLAG");
		columnMap.put("2", "ORIG_SITE_CUID");
		columnMap.put("3", "ORIG_ROOM_CUID");
		columnMap.put("4", "ORIG_EQP_CUID");
		columnMap.put("5", "ORIG_POINT_CUID");
		columnMap.put("6", "DEST_SITE_CUID");
		columnMap.put("7", "DEST_ROOM_CUID");
		columnMap.put("8", "DEST_EQP_CUID");
		columnMap.put("9", "DEST_POINT_CUID");
		columnMap.put("10", "ROUTE_DESCIPTION");
		columnMap.put("11", "OWNERSHIP");
		columnMap.put("12", "FIBER_LEVEL");
		columnMap.put("13", "FIBER_STATE");
		columnMap.put("14", "USER_NAME");
		columnMap.put("15", "FIBER_TYPE");
		columnMap.put("16", "FIBER_DIR");
		columnMap.put("17", "PURPOSE");
		columnMap.put("18", "LENGTH");
		columnMap.put("19", "SUM_ATTENU_1310");
		columnMap.put("20", "SUM_ATTENU_1550");
		columnMap.put("21", "APPLY_TYPE");
		return columnMap;
	}
	
	/**
	 * 封装枚举字段
	 * @return
	 */
	public Map<String, String> getEnumMap() {
		Map<String, String> enumMap = new HashMap<String, String>();
		enumMap.put("MAKE_FLAG", "PathMakeFlag");
		enumMap.put("OWNERSHIP", "DMOwnerShip");
		enumMap.put("FIBER_LEVEL", "DMFIBERLEVEL");
		enumMap.put("FIBER_STATE", "DMSUSAGESTATE");
		enumMap.put("FIBER_TYPE", "DMFiberType");
		enumMap.put("FIBER_DIR", "DMSIGNALDIRECTION");
		enumMap.put("PURPOSE", "DMPurpose");
		// enumMap.put("APPLY_TYPE", "");
		return enumMap;
	}
}
