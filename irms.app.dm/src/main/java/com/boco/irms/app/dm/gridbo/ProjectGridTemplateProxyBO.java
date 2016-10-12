package com.boco.irms.app.dm.gridbo;

import java.util.Collection;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;

public class ProjectGridTemplateProxyBO extends GridTemplateProxyBO {

	@Override
	public String getSql(GridCfg param,String className){
    	String sql = " RELATED_PROJECT_CUID IS NULL";
    	String cuid = param.getCfgParams().get("cuid");
    	if  (cuid != null){
    		sql += " AND  CUID = ' "+ cuid + "'";
    	}
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
	
	
}
