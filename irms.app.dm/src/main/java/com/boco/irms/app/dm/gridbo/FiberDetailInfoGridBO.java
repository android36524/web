package com.boco.irms.app.dm.gridbo;

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

public class FiberDetailInfoGridBO extends GridTemplateProxyBO{

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

		String WireSegCuid = (String)param.getCfgParams().get("CUID");
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = this.getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName(); 
		String sql = "SELECT WS.CUID,WS.LABEL_CN AS WIRE_SEG_NAME,F.WIRE_NO,OW.LABEL_CN AS OPTICAL_WAY_NAME,"
				+" OW.NE_PORT_CUID_A ,OW.NE_PORT_CUID_Z "
				+" FROM FIBER F "
				+" LEFT JOIN OPTICAL_TO_FIBER OTF ON  OTF.FIBER_CUID = F.CUID  "
				+" LEFT JOIN OPTICAL O ON OTF.OPTICAL_CUID = O.CUID "
				+" LEFT JOIN  OPTICAL_WAY OW  ON O.RELATED_OPTICAL_WAY_CUID = OW.CUID  "
				+" ,WIRE_SEG WS "
				+" WHERE F.RELATED_SEG_CUID = WS.CUID ";
			sql +=" AND WS.CUID= '"+WireSegCuid+"'";
		LogHome.getLog().info("查询语句："+sql);
		list = ibatisResDAO.querySql(sql);
		PageResult pageResult = new PageResult(list, 1,1, 1);
		String neCuid_A = "";
		String neCuid_Z = "";
		Map map = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			map = list.get(i);
			neCuid_A = map.get("NE_PORT_CUID_A")+"";
			neCuid_Z = map.get("NE_PORT_CUID_Z")+"";
            map.put("ORIG_DEVICE_NAME", getNENameByPTP(neCuid_A));
            map.put("DEST_DEVICE_NAME", getNENameByPTP(neCuid_Z));
		}
		pageResult = new PageResult(list,1,1,1);
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
		String WireSegCuid = (String)param.getCfgParams().get("CUID");
		String sqlcon = getSql(param,className);
		String sql = "SELECT COUNT(DISTINCT F.CUID)  "
		+" FROM FIBER F,WIRE_SEG WS"
		+" WHERE F.RELATED_SEG_CUID = WS.CUID ";
		sql +=" AND WS.CUID= '"+WireSegCuid+"'";
		LogHome.getLog().info("查询语句："+sql);
		sql += sqlcon;
		Integer totalNum =0;
		try {
			totalNum = ibatisResDAO.calculate(sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
	
	/**
	 * 根据端口的cuid获取网元的名称
	 * @param ptpCuid
	 * @return
	 */
	public String getNENameByPTP(String ptpCuid){
		if(ptpCuid==null||"".equals(ptpCuid)||"null".equals(ptpCuid)){
			return "";
		}
		String sql = "SELECT TE.LABEL_CN FROM PTP P,TRANS_ELEMENT TE"
				+ "  WHERE P.RELATED_NE_CUID = TE.CUID "
				+ "   AND P.CUID = '"+ptpCuid+"'";
		
		List<Map> list = new ArrayList<Map>();  
		LogHome.getLog().info("根据端口的cuid获取网元的名称:查询语句："+sql);
		list = ibatisResDAO.querySql(sql);

		if(list!=null&&list.size()>0){
			Map map = list.get(0);
			return map.get("LABEL_CN")+"";
		}
		
		return "";
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
