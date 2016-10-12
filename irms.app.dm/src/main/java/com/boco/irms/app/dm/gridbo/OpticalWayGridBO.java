package com.boco.irms.app.dm.gridbo;

import com.alibaba.druid.util.StringUtils;

import com.boco.component.grid.pojo.GridCfg;
/**
 * 光纤管理中查看光路列表（光纤承载的光路）
 * @author wangqin
 *
 */
public class OpticalWayGridBO extends GridTemplateProxyBO {
	
	/**
     * 获取查询拼接条件
     * @param wItems
     * @param className
     * @author wangqin
     * @return
     */
	@Override
    public String getSql(GridCfg param,String className){
    	String sql =" 1=1";
    	String opticalCuid = param.getCfgParams().get("cuid");
    	if(!StringUtils.isEmpty(opticalCuid)){
    		sql = " CUID IN (SELECT OPR.RELATED_SERVICE_CUID FROM OPTICAL_ROUTE OPR ,OPTICAL_ROUTE_TO_PATH OPRTP "
    				+ "WHERE OPR.CUID = OPRTP.OPTICAL_ROUTE_CUID AND PATH_CUID = '"+opticalCuid+"')";
    	}
    	logger.info("SQL="+sql);
		return sql;
    }
}
