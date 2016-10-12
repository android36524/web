package com.boco.irms.app.dm.gridbo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.boco.common.util.lang.TimeFormatHelper;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.db.DbType;
import com.boco.core.utils.db.SqlHelper;
import com.boco.irms.app.dm.action.schedule.StatHelper;
import com.boco.irms.app.dm.action.schedule.TimeHelper;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.common.dto.base.BoQueryContext;

public class WireDecrementInfoGridBO extends GridTemplateProxyBO {

	private IbatisDAO ibatisTransDAO;
	
	private IbatisDAO ibatisSdeDAO;
	
	public void setIbatisSdeDAO(IbatisDAO ibatisSdeDAO) {
		this.ibatisSdeDAO = ibatisSdeDAO;
	}
	

	public void setIbatisTransDAO(IbatisDAO ibatisTransDAO) {
		this.ibatisTransDAO = ibatisTransDAO;
	}



	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName();
		
		//拼装查询条件
		String[] sqlTemplate = editorMeta.getSql().trim().split("@");
		String querySql = getSql(param,sqlTemplate).trim();
		logger.info("querySql:"+querySql);
		//从传输库查询数据
		List<Map> list = new ArrayList<Map>();
		try {
			list = ibatisTransDAO.querySql(querySql);
			StatHelper.convertTypeByCuid(list, "ORIG_POINT_CUID", "ORIG_POINT_TYPE");
			StatHelper.convertTypeByCuid(list, "DEST_POINT_CUID", "DEST_POINT_TYPE");
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult(list,1,1,1);
		return pageResult;
	}

	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName();
		
        //拼装查询条件
		String[] sqlTemplate = editorMeta.getSql().trim().split("@");
		String countSql = "SELECT COUNT(*) FROM ( " + getSql(param,sqlTemplate) + " ) T".trim();
		logger.info("countSql:"+countSql);
		BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		Integer totalNum = 0;
		try {
			totalNum = ibatisTransDAO.calculate(countSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult(null,totalNum,queryParam.getCurPageNum(), queryParam.getPageSize());
		return pageResult;
	}

	
	private String getSql(GridCfg param,String[] sqlTemplate){
		if(sqlTemplate.length == 1){
			String cuid = param.getCfgParams().get("cuid");
			String tableName = cuid.split("-")[0];
			Map<String,Object> paramMap = null;
			if(cuid != null){
				try {
					String sql = "SELECT RELATED_DISTRICT_CUID,CHECK_MONTH FROM " + tableName + " WHERE CUID = '" + cuid + "'";
					List<Map> list = ibatisSdeDAO.querySql(sql);
					if(list != null && list.size() > 0){
						paramMap = list.get(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(paramMap != null){
				BigDecimal month = (BigDecimal) paramMap.get("CHECK_MONTH");
				Date firstDayOfCurrMon = TimeHelper.getFirstDayOfMonth(0, month.intValue());
				Date firstDayOfNextMon = TimeHelper.getFirstDayOfMonth(1, month.intValue());
				String timeSql = "";
				try {
					DbType dbType = WebDMUtils.getDbType();
					String fromTime = SqlHelper.getDate(dbType, new java.sql.Date(firstDayOfCurrMon.getTime()));
					String toTime = SqlHelper.getDate(dbType, new java.sql.Date(firstDayOfNextMon.getTime()));
					timeSql = " RES.CREATE_TIME >= " + fromTime + " AND RES.CREATE_TIME < " + toTime;
					
					String createTimeSql = "";
					if(dbType == DbType.DB_TYPE_ORACLE){
						createTimeSql = "TO_CHAR(RES.CREATE_TIME,'yyyy-mm-dd hh24:mi:ss') AS CREATE_TIME ";
					}else if(dbType == DbType.DB_TYPE_INFORMIX){
						createTimeSql = "TO_CHAR(RES.CREATE_TIME,'%Y-%m-%d  %H:%M:%S') AS CREATE_TIME ";
					}
					
					sqlTemplate[0] = sqlTemplate[0].replaceAll("RES.CREATE_TIME",createTimeSql);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				paramMap.put("CHECK_MONTH", timeSql);
				
				for(Map.Entry entry : paramMap.entrySet()){
					sqlTemplate[0] = sqlTemplate[0].replaceAll("\\$" + entry.getKey() + "\\$", "" + entry.getValue());
				}
				return sqlTemplate[0];
			}
		}
		return null;
	}
	
}
