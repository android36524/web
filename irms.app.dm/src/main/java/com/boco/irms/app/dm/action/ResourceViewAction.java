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
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class ResourceViewAction extends XmlTemplateGridBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, EditorColumnMeta> editorColumnMetaMap = new HashMap<String, EditorColumnMeta>();
	/**
	 * 工程管理中查看关联资源实现
	 * 根据工程Id获得该工程下的关联资源
	 * @param CUID
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public List getResourceLists(String CUID) throws IOException {
		String method = "IProjectManagementBO.getResourceByProjectCuid";// 调用查询关联资源接口方法
		Map<String, String> columnMap = getColumnMap();
		editorColumnMetaMap.clear();
		
		// 从传输服务查询数据
		DataObjectList results = new DataObjectList();
		try {
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(), CUID);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}
		List<Map> list = new ArrayList<Map>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map<String, Object> map = new HashMap<String, Object>();
				for (String columnName : columnMap.values()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName,value );
				}
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 封装列表map
	 * @return
	 */
	public Map<String, String> getColumnMap() {
		Map<String, String> columnMap = new HashMap<String, String>();
		columnMap.put("0", "LABEL_CN");
		columnMap.put("1", "TYPE");
		return columnMap;
	}
}
