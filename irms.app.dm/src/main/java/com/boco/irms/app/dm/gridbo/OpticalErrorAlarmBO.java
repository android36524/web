package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class OpticalErrorAlarmBO extends GridTemplateProxyBO{

	private IbatisDAO ibatisAlarmDAO;

	public void setIbatisAlarmDAO(IbatisDAO _ibatisAlarmDAO) {
		this.ibatisAlarmDAO = _ibatisAlarmDAO;
	} 
	
	/**
	 * 拼装查询条件
	 * @param param GridCfg
	 * @param className String
	 */
	@Override	
    public String getSql(GridCfg param,String className){
		String sql ="";
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return sql;
    	}
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				if(item.getSqlValue() != null && !item.getSqlValue().trim().equals("")){
					sql += " AND "+item.getSqlValue();
				}
			}
		}
    	logger.info("SQL="+sql);
		return sql;
    }
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		DataObjectList exportList = new DataObjectList();
		//列赋值
		List<Map> list = new ArrayList<Map>();  

		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = this.getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName(); 
		String queryCon = this.getSql(param, className);
		String sql = "SELECT SUBSTR(CA.ALM_DEVINFO,0,INSTR(CA.ALM_DEVINFO,'<-->')-1) AS RELATED_PORT_A, "
			       +" REPLACE(REPLACE(REPLACE(SUBSTR(CA.ALM_DEVINFO,INSTR(CA.ALM_DEVINFO,'<-->')+4),'单收无光'),'互收无光'),'单链中断') AS RELATED_PORT_Z, "
			       +" CA.OBJECTID,CA.CUID,CA.CITY_NAME,TO_CHAR(CA.NEALARM_TIME,'yyyy-mm-dd hh:mi:ss') AS NEALARM_TIME,"
			       +" CA.ALARM_SEVERITY_NAME,CA.ALARM_NAME,CA.EMS_NAME,ENUM.NAME AS NE_MODEL, "
			       +" CA.ALM_DEVINFO "
			       +" FROM CURRENT_ALARM CA "
			       + " LEFT JOIN T_SYS_ENUM_VALUE ENUM  ON ENUM.RELATED_ENUM_TYPE_CUID = 'SIGNAL_TYPE'  "
			       + " AND CA.NE_SIGNAL_TYPE = ENUM.VALUE"
			       +" WHERE  CA.DERIVATIVE_TYPE = 2 "
			       +"  AND (INSTR(CA.ALARM_NAME, '单链中断')>0 OR INSTR(CA.ALARM_NAME, '无光')>0) "
			       + queryCon;
		if (queryParam == null) {
			queryParam = new PageQuery(0, 15);
		}
		if (StringUtils.isNotBlank(queryParam.getSort())) {
			String dir = queryParam.getDir();
			if (StringUtils.isBlank(dir)) {
				dir = "ASC";
			}
			sql = "SELECT * FROM (" + sql + ") ORDER BY " + queryParam.getSort() + " " + dir;
		}
		LogHome.getLog().info("查询语句："+sql);
		list = ibatisAlarmDAO.querySql(sql);
		PageResult pageResult = new PageResult(list, 1,1, 1);
		if(exportList != null && exportList.size() > 0){
			for (int i = 0; i < exportList.size(); i++) {
				GenericDO gdo = exportList.get(i);
				Map allAttr = gdo.getAllAttr();
				list.add(allAttr);
			}
			pageResult = new PageResult(list,1,1,1);
		}
		return pageResult;
	}
	
	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String method = "";
		//获取列名
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName();
        //拼装查询条件
		String sqlcon = getSql(param,className);
		
		String sql = "SELECT COUNT(*) "
			       +" FROM CURRENT_ALARM CA "
			       +" WHERE  CA.DERIVATIVE_TYPE = 2 "
			       +"  AND (INSTR(CA.ALARM_NAME, '无光')>0 OR INSTR(CA.ALARM_NAME, '单链中断')>0)";
		BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		sql += sqlcon;
		Integer totalNum =0;
		try {
			totalNum = ibatisAlarmDAO.calculate(sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
	
}
