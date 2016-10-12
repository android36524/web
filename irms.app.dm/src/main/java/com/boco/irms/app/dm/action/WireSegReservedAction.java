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

public class WireSegReservedAction extends XmlTemplateGridBO{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, EditorColumnMeta> editorColumnMetaMap = new HashMap<String, EditorColumnMeta>();
	
	/**
	 * 根据光缆CUID获取光缆下的光缆段
	 * @param CUID
	 * @return
	 * @throws IOException
	 */
	public List<Object> getWireSegLists(String CUID) throws IOException {
		
		String method = "IWireSegBO.getWireSegsByWireSystemCuid";// 调用的接口方法
		editorColumnMetaMap.clear();
				
		System.out.println("CUID========================"+CUID);
		// 从传输服务查询数据
		DataObjectList results = new DataObjectList();
		try {
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(), CUID);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}
		List<Map> list = new ArrayList<Map>();
		List<Object> list1 =new ArrayList<Object>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				list1.add(gdo);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("", gdo);
//				for (String columnName : columnMap.values()) {
//					Object value = gdo.getAttrValue(columnName);
//					map.put(columnName, convertObject(columnName, value));
//				}
				list.add(map);
			}
		}
		return list1;
	}
}
