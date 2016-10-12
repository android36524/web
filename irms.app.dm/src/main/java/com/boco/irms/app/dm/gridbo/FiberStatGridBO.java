package com.boco.irms.app.dm.gridbo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class FiberStatGridBO extends GridTemplateProxyBO{

	private IbatisDAO ibatisResDAO;

	public void setIbatisResDAO(IbatisDAO _ibatisResDAO) {
		this.ibatisResDAO = _ibatisResDAO;
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
					if(item.getKey()!=null&&"ORIG_DEST_CUID".equals(item.getKey())){
						sql += " AND (WS.ORIG_POINT_CUID = '"+item.getValue()+"' OR WS.DEST_POINT_CUID = '"+item.getValue()+"')";
					}else{
						sql += " AND "+item.getSqlValue();
					}
				}
			}
		}
    	logger.info("SQL="+sql);
		return sql;
    }
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		//列赋值
		List<Map> list = new ArrayList<Map>();  
		DecimalFormat format = new DecimalFormat("0.00");
		String name = this.getTemplateId(param);
		String queryCon = this.getSql(param, "");
		String sql = "select T.*,"+
			"	       case"+
			"	         when T.FIBER_COUNT > 0 then"+
			"	          round(T.FIBER_COUNT_USED / T.FIBER_COUNT, 2)"+
			"	         when T.FIBER_COUNT = 0 then"+
			"	          0"+
			"	       end as RATE_USED"+
			"	  from (SELECT WS.CUID,"+
			"	               WS.LABEL_CN as WIRE_SEG_NAME,"+
			"	               WSYS.SYSTEM_LEVEL,"+
			"	               WSYS.RELATED_SPACE_CUID,"+
			"	               WS.RELATED_SYSTEM_CUID,"+
			"	               WS.LENGTH,"+
			"	               WS.ORIG_POINT_CUID AS ORIG_JOINT_NAME,"+
			"	               WS.DEST_POINT_CUID AS DEST_JOINT_NAME,"+
//			"				   DOCUIDTONAME(WS.ORIG_POINT_CUID) AS ORIG_JOINT_NAME,"+	
//			"				   DOCUIDTONAME(WS.DEST_POINT_CUID) AS DEST_JOINT_NAME,"+
			"	               WSYS.RELATED_SPACE_CUID AS RELATED_DISTRICT_CUID,"+
			"	               WSYS.STATE AS STATE,"+
			"	               (SELECT COUNT(*)"+
			"	                  FROM FIBER F"+
			"	                 WHERE F.RELATED_SEG_CUID = WS.CUID"+
			"	                   AND (F.USAGE_STATE = 2 OR (F.ORIG_POINT_CUID IN (select CUID FROM ODFPORT where IS_CONNECTED=1) OR F.DEST_POINT_CUID IN (select CUID FROM ODFPORT where IS_CONNECTED=1)))) FIBER_COUNT_USED,"+
			"	               (SELECT COUNT(*)"+
			"	                  FROM FIBER F"+
			"	                 WHERE F.RELATED_SEG_CUID = WS.CUID) FIBER_COUNT"+
			"	          FROM WIRE_SEG WS, WIRE_SYSTEM WSYS"+
			"	         WHERE WS.RELATED_SYSTEM_CUID = WSYS.CUID "+queryCon+") T";
		//sql = "SELECT * FROM ("+sql+")";
		LogHome.getLog().info("查询语句："+sql);
		list = ibatisResDAO.querySql(sql, queryParam.getStartPos()-1, queryParam.getPageSize());
		PageResult pageResult = new PageResult(list, 1,1, 1);
		double used = 0;
		double all = 0;
		double rate_used = 0;
		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				used = Double.parseDouble(map.get("FIBER_COUNT_USED")+"");
				all = Double.parseDouble(map.get("FIBER_COUNT")+"");
				if(all>0){
					rate_used = used/all;
				}else
					rate_used = 0;
				map.put("RATE_USED", format.format(rate_used*100));
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
        //拼装查询条件
		String sqlcon = getSql(param,"");
		
		String sql = "SELECT COUNT(*) "
			       +" FROM WIRE_SEG WS,WIRE_SYSTEM WSYS "
			       +" WHERE  WS.RELATED_SYSTEM_CUID = WSYS.CUID  ";
		sql += sqlcon;
		Integer totalNum =0;
		try {
			LogHome.getLog().info("统计页面信息的查询语句："+sql);
			totalNum = ibatisResDAO.calculate(sql);
		} catch (Exception e) {
            e.printStackTrace();
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
	
	public static void main(String[] args){

		String sql = "SELECT CASE "
			       +" WHEN INSTR(CA.ALM_DEVINFO,'单收')>0 "
			       +" THEN SUBSTR(CA.ALM_DEVINFO,0,INSTR(CA.ALM_DEVINFO,'单收')-1)  "
			       +" ELSE SUBSTR(CA.ALM_DEVINFO,0,INSTR(CA.ALM_DEVINFO,'互收')-1) END AS RELATED_PORT_A, "
			       +" CASE  "
			       +"  WHEN INSTR(CA.ALM_DEVINFO,'单收')>0 "
			       +" THEN SUBSTR(CA.ALM_DEVINFO,INSTR(CA.ALM_DEVINFO,'单收')+2,INSTR(CA.ALM_DEVINFO,'无光') - INSTR(CA.ALM_DEVINFO,'单收')-2) "
			       +" ELSE SUBSTR(CA.ALM_DEVINFO,INSTR(CA.ALM_DEVINFO,'互收')+2,INSTR(CA.ALM_DEVINFO,'无光') - INSTR(CA.ALM_DEVINFO,'互收')-2) END AS RELATED_PORT_Z, "
			       +" CA.OBJECTID,CA.CUID,CA.CITY_NAME,TO_CHAR(CA.NEALARM_TIME,'yyyy-mm-dd hh:mi:ss') AS NEALARM_TIME,CA.ALARM_SEVERITY_NAME,"
			       + " CA.ALARM_NAME,CA.EMS_NAME,ENUM.NAME AS NE_MODEL, CA.ALM_DEVINFO "
			       +" FROM CURRENT_ALARM CA "
			       + " LEFT JOIN T_SYS_ENUM_VALUE ENUM  ON ENUM.RELATED_ENUM_TYPE_CUID = 'SIGNAL_TYPE'  "
			       + "                             AND CA.NE_SIGNAL_TYPE = ENUM.VALUE"
			       +" WHERE  CA.DERIVATIVE_TYPE = 2 "
			       +"  AND INSTR(CA.ALARM_NAME, '无光')>0";
		sql +=" UNION ";
		sql = "";
		sql +=" SELECT SUBSTR(CA.ALM_DEVINFO,0,INSTR(CA.ALM_DEVINFO,'<-->')-1) AS RELATED_PORT_A, "
			       +" SUBSTR(CA.ALM_DEVINFO,INSTR(CA.ALM_DEVINFO,'<-->')+4,INSTR(CA.ALM_DEVINFO,'单链中断') - INSTR(CA.ALM_DEVINFO,'<-->')-4) AS RELATED_PORT_Z, "
			       +" CA.OBJECTID,CA.CUID,CA.CITY_NAME,TO_CHAR(CA.NEALARM_TIME,'yyyy-mm-dd hh:mi:ss') AS NEALARM_TIME,"
			       +" CA.ALARM_SEVERITY_NAME,CA.ALARM_NAME,CA.EMS_NAME,ENUM.NAME AS NE_MODEL, "
			       +" CA.ALM_DEVINFO "
			       +" FROM CURRENT_ALARM CA "
			       + " LEFT JOIN T_SYS_ENUM_VALUE ENUM  ON ENUM.RELATED_ENUM_TYPE_CUID = 'SIGNAL_TYPE'  "
			       + "                             AND CA.NE_SIGNAL_TYPE = ENUM.VALUE"
			       +" WHERE  CA.DERIVATIVE_TYPE = 2 "
			       +"  AND (INSTR(CA.ALARM_NAME, '单链中断')>0 OR  INSTR(CA.ALARM_NAME, '无光')>0)";
		LogHome.getLog().info("查询语句："+sql);
	}

}
