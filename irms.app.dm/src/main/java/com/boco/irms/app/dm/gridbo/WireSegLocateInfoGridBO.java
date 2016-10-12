package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;

public class WireSegLocateInfoGridBO extends GridTemplateProxyBO{

	private IbatisDAO ibatisResDAO;

	public void setIbatisResDAO(IbatisDAO _ibatisResDAO) {
		this.ibatisResDAO = _ibatisResDAO;
	} 
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		DataObjectList exportList = new DataObjectList();
		String port_a = (String)param.getCfgParams().get("RELATED_PORT_A");
		String port_z = (String)param.getCfgParams().get("RELATED_PORT_Z");
		String transSysName = (String)param.getCfgParams().get("TRANSSYS_NAME");
		//列赋值
		List<Map> list = new ArrayList<Map>();
		String sql =" SELECT WS.CUID,WS.OBJECTID,OW.LABEL_CN AS OPTICAL_WAY_NAME "
         +",DOCUIDTONAME(OW.RELATED_SYSTEM_CUID) AS TRANS_SYSTEM,WSYS.LABEL_CN AS WIRE_SYSTEM  "
         +",WS.LABEL_CN AS WIRE_SEG_NAME,F.WIRE_NO,RA.LABEL_CN AS ROOM_A "
         +",RZ.LABEL_CN AS ROOM_Z,DOCUIDTONAME(F.ORIG_POINT_CUID) AS POINT_A "
         +",DOCUIDTONAME(F.DEST_POINT_CUID) AS POINT_Z  "
    	 +"FROM OPTICAL_WAY OW,OPTICAL_ROUTE OROUTE,OPTICAL_ROUTE_TO_PATH ORTP "
	     +",OPTICAL_TO_FIBER OTF,FIBER F,OPTICAL O,WIRE_SEG WS,WIRE_SYSTEM WSYS  "
    	 +",PTP PA,PTP PZ,ROOM RA,ROOM RZ  "
    	+"WHERE OW.CUID = OROUTE.RELATED_SERVICE_CUID "
        +"  AND OROUTE.CUID = ORTP.OPTICAL_ROUTE_CUID "
	    +"  AND ORTP.PATH_CUID = O.CUID "
	    +"  AND O.CUID = OTF.OPTICAL_CUID "
	    +"  AND OTF.FIBER_CUID = F.CUID "
	    +"  AND OTF.WIRE_SEG_CUID = WS.CUID "
	    +"  AND OTF.WIRE_SYSTEM_CUID = WSYS.CUID "
	    +"  AND O.ORIG_ROOM_CUID = RA.CUID "
	    +"  AND O.DEST_ROOM_CUID = RZ.CUID "
	    +"  AND OW.NE_PORT_CUID_A = PA.CUID "
	    +"  AND OW.NE_PORT_CUID_Z = PZ.CUID "
	    +"  AND PA.LABEL_CN= '"+port_a+"' "
	    +"  AND PZ.LABEL_CN ='"+port_z+"'"
	    +" UNION "
	    +"SELECT WS.CUID,WS.OBJECTID,OW.LABEL_CN AS OPTICAL_WAY_NAME "
        +",DOCUIDTONAME(OW.RELATED_SYSTEM_CUID) AS TRANS_SYSTEM,WSYS.LABEL_CN AS WIRE_SYSTEM  "
    	+",WS.LABEL_CN AS WIRE_SEG_NAME,F.WIRE_NO,RA.LABEL_CN AS ROOM_A "
    	+" ,RZ.LABEL_CN AS ROOM_Z,DOCUIDTONAME(F.ORIG_POINT_CUID) AS POINT_A "
    	+",DOCUIDTONAME(F.DEST_POINT_CUID) AS POINT_Z "
    	+" FROM OPTICAL_WAY OW,OPTICAL_ROUTE OROUTE,OPTICAL_ROUTE_TO_PATH ORTP "
	    +",OPTICAL_TO_FIBER OTF,FIBER F,OPTICAL O,WIRE_SEG WS,WIRE_SYSTEM WSYS  "
    	+" ,PTP PA,PTP PZ,ROOM RA,ROOM RZ  "
    	+"WHERE OW.CUID = OROUTE.RELATED_SERVICE_CUID "
        +"  AND OROUTE.CUID = ORTP.OPTICAL_ROUTE_CUID "
	    +"  AND ORTP.PATH_CUID = O.CUID "
	    +"  AND O.CUID = OTF.OPTICAL_CUID "
	    +"  AND OTF.FIBER_CUID = F.CUID "
	    +"  AND OTF.WIRE_SEG_CUID = WS.CUID "
	    +"  AND OTF.WIRE_SYSTEM_CUID = WSYS.CUID "
	    +"  AND O.ORIG_ROOM_CUID = RA.CUID "
	    +"  AND O.DEST_ROOM_CUID = RZ.CUID "
	    +"  AND OW.NE_PORT_CUID_A = PA.CUID "
	    +"  AND OW.NE_PORT_CUID_Z = PZ.CUID "
	    +"  AND PA.LABEL_CN= '"+port_z+"' "
	    +"  AND PZ.LABEL_CN ='"+port_a+"'"
	    +" ORDER BY WIRE_SEG_NAME ASC ";

		
		LogHome.getLog().info("获取预警光缆段的查询语句："+sql);
		list = ibatisResDAO.querySql("SELECT * FROM ("+sql+")");
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

		DataObjectList exportList = new DataObjectList();
		String port_a = (String)param.getCfgParams().get("RELATED_PORT_A");
		String port_z = (String)param.getCfgParams().get("RELATED_PORT_Z");
		String transSysName = (String)param.getCfgParams().get("TRANSSYS_NAME");
		//列赋值
		List<Map> list = new ArrayList<Map>();
		String sql =" SELECT WS.CUID,WS.OBJECTID,OW.LABEL_CN AS OPTICAL_WAY_NAME "
		         +",DOCUIDTONAME(OW.RELATED_SYSTEM_CUID) AS TRANS_SYSTEM,WSYS.LABEL_CN AS WIRE_SYSTEM  "
		         +",WS.LABEL_CN AS WIRE_SEG_NAME,F.WIRE_NO,RA.LABEL_CN AS ROOM_A "
		         +",RZ.LABEL_CN AS ROOM_Z,DOCUIDTONAME(F.ORIG_POINT_CUID) AS POINT_A "
		         +",DOCUIDTONAME(F.DEST_POINT_CUID) AS POINT_Z  "
		    	 +"FROM OPTICAL_WAY OW,OPTICAL_ROUTE OROUTE,OPTICAL_ROUTE_TO_PATH ORTP "
			     +",OPTICAL_TO_FIBER OTF,FIBER F,OPTICAL O,WIRE_SEG WS,WIRE_SYSTEM WSYS  "
		    	 +",PTP PA,PTP PZ,ROOM RA,ROOM RZ  "
		    	+"WHERE OW.CUID = OROUTE.RELATED_SERVICE_CUID "
		        +"  AND OROUTE.CUID = ORTP.OPTICAL_ROUTE_CUID "
			    +"  AND ORTP.PATH_CUID = O.CUID "
			    +"  AND O.CUID = OTF.OPTICAL_CUID "
			    +"  AND OTF.FIBER_CUID = F.CUID "
			    +"  AND OTF.WIRE_SEG_CUID = WS.CUID "
			    +"  AND OTF.WIRE_SYSTEM_CUID = WSYS.CUID "
			    +"  AND O.ORIG_ROOM_CUID = RA.CUID "
			    +"  AND O.DEST_ROOM_CUID = RZ.CUID "
			    +"  AND OW.NE_PORT_CUID_A = PA.CUID "
			    +"  AND OW.NE_PORT_CUID_Z = PZ.CUID "
			    +"  AND PA.LABEL_CN= '"+port_a+"' "
			    +"  AND PZ.LABEL_CN ='"+port_z+"'"
			    +" UNION "
			    +"SELECT WS.CUID,WS.OBJECTID,OW.LABEL_CN AS OPTICAL_WAY_NAME "
		        +",DOCUIDTONAME(OW.RELATED_SYSTEM_CUID) AS TRANS_SYSTEM,WSYS.LABEL_CN AS WIRE_SYSTEM  "
		    	+",WS.LABEL_CN AS WIRE_SEG_NAME,F.WIRE_NO,RA.LABEL_CN AS ROOM_A "
		    	+" ,RZ.LABEL_CN AS ROOM_Z,DOCUIDTONAME(F.ORIG_POINT_CUID) AS POINT_A "
		    	+",DOCUIDTONAME(F.DEST_POINT_CUID) AS POINT_Z "
		    	+" FROM OPTICAL_WAY OW,OPTICAL_ROUTE OROUTE,OPTICAL_ROUTE_TO_PATH ORTP "
			    +",OPTICAL_TO_FIBER OTF,FIBER F,OPTICAL O,WIRE_SEG WS,WIRE_SYSTEM WSYS  "
		    	+" ,PTP PA,PTP PZ,ROOM RA,ROOM RZ  "
		    	+"WHERE OW.CUID = OROUTE.RELATED_SERVICE_CUID "
		        +"  AND OROUTE.CUID = ORTP.OPTICAL_ROUTE_CUID "
			    +"  AND ORTP.PATH_CUID = O.CUID "
			    +"  AND O.CUID = OTF.OPTICAL_CUID "
			    +"  AND OTF.FIBER_CUID = F.CUID "
			    +"  AND OTF.WIRE_SEG_CUID = WS.CUID "
			    +"  AND OTF.WIRE_SYSTEM_CUID = WSYS.CUID "
			    +"  AND O.ORIG_ROOM_CUID = RA.CUID "
			    +"  AND O.DEST_ROOM_CUID = RZ.CUID "
			    +"  AND OW.NE_PORT_CUID_A = PA.CUID "
			    +"  AND OW.NE_PORT_CUID_Z = PZ.CUID "
			    +"  AND PA.LABEL_CN= '"+port_z+"' "
			    +"  AND PZ.LABEL_CN ='"+port_a+"'";
		
		sql = "SELECT COUNT(*) FROM (" +sql+") ";
		BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		Integer totalNum =0;
		try {
			totalNum = ibatisResDAO.calculate(sql);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称="+method,e);
		}
		PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
		return page;
	}
	
	
}
