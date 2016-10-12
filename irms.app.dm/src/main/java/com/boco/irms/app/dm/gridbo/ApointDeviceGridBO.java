package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class ApointDeviceGridBO extends GridTemplateProxyBO {

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String relatedAccesspointCuid = "";
		if(param.getQueryParams().get("accesspointCuid") != null)
			relatedAccesspointCuid = param.getQueryParams().get("accesspointCuid").getValue();
		String labelCn = "";
		if(param.getQueryParams().get("LABEL_CN") != null)
			labelCn = param.getQueryParams().get("LABEL_CN").getValue();
		String relatedDeviceCuid = "";
		if(param.getQueryParams().get("RELATED_DEVICE_CUID") != null)
			relatedDeviceCuid = param.getQueryParams().get("RELATED_DEVICE_CUID").getValue();
		
		// 获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		String method = editorMeta.getRemoteMethod("query");
		String className = editorMeta.getClassName();

		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
		// 拼装查询条件
		String sql = "RELATED_SITE_CUID='" + relatedAccesspointCuid + "'";

		// 从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext(
				(queryParam.getCurPageNum() - 1) * queryParam.getPageSize(),
				queryParam.getPageSize(), false);
		querycon.setUserId("SYS_USER-0");
		DataObjectList devices =  null;
		try {
			sql = "SELECT * FROM (SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 1 as DEVICE_TYPE FROM fiber_cab WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 2 as DEVICE_TYPE FROM fiber_dp WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 3 as DEVICE_TYPE FROM fiber_joint_box WHERE RELATED_LOCATION_CUID='" + relatedAccesspointCuid + "' union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 4 as DEVICE_TYPE FROM an_pos WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 5 as DEVICE_TYPE FROM an_onu WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 6 as DEVICE_TYPE FROM odf WHERE " + sql + ") WHERE 1=1";
			if(StringUtils.isNotBlank(labelCn))
			{
				sql += " AND UPPER(LABEL_CN) like UPPER("+labelCn+") ";
			}
			if(StringUtils.isNotBlank(relatedDeviceCuid))
			{
				sql += " AND DEVICE_TYPE ="+relatedDeviceCuid+" ";
			}
			try{
				Class[] classType = new Class[] {String.class, String.class, String.class, String.class, String.class, Integer.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				devices = ductmanagerBo.getDatasBySql(sql, classType);
				for(GenericDO gdo : devices)
				{
					gdo.setAttrValue("CUID", gdo.getAttrString("1"));
					gdo.setAttrValue("LABEL_CN", gdo.getAttrString("2"));
					gdo.setAttrValue("OBJECTID", gdo.getAttrString("3"));
					gdo.setAttrValue("CREATE_TIME", gdo.getAttrString("4"));
					gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getAttrString("5"));
					gdo.setAttrValue("DEVICE_TYPE", gdo.getAttrInt("6"));
				}
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}

		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0,
				queryParam.getCurPageNum(), queryParam.getPageSize());
		if (devices != null && devices.size() > 0) {
			for (int i = 0; i < devices.size(); i++) {
				GenericDO gdo = devices.get(i);
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map map = new HashMap();
				for (String columnName : columnMap.keySet()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName, value));
				}
				list.add(map);
			}
			pageResult = new PageResult(list, devices.getCountValue(),
					queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}

	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String relatedAccesspointCuid = "";
		if(param.getQueryParams().get("accesspointCuid") != null)
			relatedAccesspointCuid = param.getQueryParams().get("accesspointCuid").getValue();
		String labelCn = "";
		if(param.getQueryParams().get("LABEL_CN") != null)
			labelCn = param.getQueryParams().get("LABEL_CN").getValue();
		String relatedDeviceCuid = "";
		if(param.getQueryParams().get("RELATED_DEVICE_CUID") != null)
			relatedDeviceCuid = param.getQueryParams().get("RELATED_DEVICE_CUID").getValue();
		
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String method = editorMeta.getRemoteMethod("query");
		// 拼装查询条件
		String sql = "RELATED_SITE_CUID='" + relatedAccesspointCuid + "'";

		// 从传输服务查询数据
		BoQueryContext querycon = new BoQueryContext(
				(queryParam.getCurPageNum() - 1) * queryParam.getPageSize(),
				queryParam.getPageSize(), false);
		querycon.setUserId("SYS_USER-0");
		Integer totalNum = 0;
		try {
			sql = "SELECT * FROM (SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 1 as DEVICE_TYPE FROM fiber_cab WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 2 as DEVICE_TYPE FROM fiber_dp WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 3 as DEVICE_TYPE FROM fiber_joint_box WHERE RELATED_LOCATION_CUID='" + relatedAccesspointCuid + "' union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 4 as DEVICE_TYPE FROM an_pos WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 5 as DEVICE_TYPE FROM an_onu WHERE " + sql + " union " +
			"SELECT CUID, LABEL_CN, OBJECTID, CREATE_TIME, LAST_MODIFY_TIME, 6 as DEVICE_TYPE FROM odf WHERE " + sql + ") WHERE 1=1";
			if(StringUtils.isNotBlank(labelCn))
			{
				sql += " AND UPPER(LABEL_CN) like UPPER("+labelCn+") ";
			}
			if(StringUtils.isNotBlank(relatedDeviceCuid))
			{
				sql += " AND DEVICE_TYPE ="+relatedDeviceCuid+" ";
			}
			try{
				Class[] classType = new Class[] {String.class, String.class, String.class, String.class, String.class, Integer.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				DataObjectList devices = ductmanagerBo.getDatasBySql(sql, classType);
				totalNum = devices.size();
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
}
