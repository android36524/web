package com.boco.irms.app.dm.gridbo;

import java.util.Collection;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class OpticalWayGridTemplateProxyBO extends GridTemplateProxyBO{

	/**
     * 获取查询sql条件,增加区域查询条件
     * @param className
     * @return
     */
	@Override	
    public String getSql(GridCfg param,String className){
		String sql =" 1= 1";
		
		String user_cuid = param.getAc().getUserCuid();
    	GenericDO sysUser = getDuctManagerBO().getObjByCuid(new BoActionContext(), user_cuid);
    	String user_district_cuid = sysUser.getAttrString("RELATED_DISTRICT_CUID");
    	if(user_district_cuid != null){
    		sql += " AND  SITE_DISTRICT_A like'"+user_district_cuid+"%'" + " AND  SITE_DISTRICT_Z like'"+user_district_cuid+"%'";
    	}
		
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return sql;
    	}
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				if("RELATED_DISTRICT_CUID".equals(item.getKey())){
					sql += " AND (SITE_DISTRICT_A LIKE '"+item.getValue()+"' OR SITE_DISTRICT_Z LIKE '"+item.getValue()+"' )";
				}else{
					sql += " AND "+item.getSqlValue();
				}
			}
		}
    	logger.info("SQL="+sql);
		return sql;
    }
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
}
