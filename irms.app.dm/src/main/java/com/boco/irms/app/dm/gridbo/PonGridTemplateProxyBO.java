package com.boco.irms.app.dm.gridbo;

import java.util.Collection;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;

public class PonGridTemplateProxyBO extends GridTemplateProxyBO{

	/**
     * 获取查询sql条件,区分了分光器的查询条件
     * @param className
     * @author wangqin
     * @return
     */
	@Override	
    public String getSql(GridCfg param,String className){
    	String sql = " 1=1 AND CONFIG_TYPE in ('3')";
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return sql;
    	}
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				sql += " AND "+item.getSqlValue();
			}
		}
    	logger.info("SQL="+sql);
		return sql;
    }
}
