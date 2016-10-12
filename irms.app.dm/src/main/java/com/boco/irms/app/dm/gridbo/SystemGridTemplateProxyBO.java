package com.boco.irms.app.dm.gridbo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class SystemGridTemplateProxyBO extends GridTemplateProxyBO {
	
	private static Map<String,String> systemSegMap = new HashMap<String,String>();
	static{
		systemSegMap.put(WireSystem.CLASS_NAME,WireSeg.CLASS_NAME);
		systemSegMap.put(DuctSystem.CLASS_NAME,DuctSeg.CLASS_NAME);
		systemSegMap.put(PolewaySystem.CLASS_NAME,PolewaySeg.CLASS_NAME);
		systemSegMap.put(StonewaySystem.CLASS_NAME,StonewaySeg.CLASS_NAME);
		systemSegMap.put(UpLine.CLASS_NAME,UpLineSeg.CLASS_NAME);
		systemSegMap.put(HangWall.CLASS_NAME,HangWallSeg.CLASS_NAME);
	}
	
    /**
     * 获取查询拼接条件
     * @param wItems
     * @param className
     * @return
     */	
    public String getSql(GridCfg param,String className){
    	String sql = "1=1";
    	
    	try{
    		String user_cuid = param.getAc().getUserCuid();
    		GenericDO sysUser = getDuctManagerBO().getObjByCuid(new BoActionContext(), user_cuid);
    		String user_district_cuid = sysUser.getAttrString("RELATED_DISTRICT_CUID");
    		if(user_district_cuid != null){
    			//规划期数不需要所属空间
    			if(!className.equals("PROJECT_PLAN_PERIOD"))
    			{
    				sql += " AND  RELATED_SPACE_CUID like'"+user_district_cuid+"%'";    			
    			}
    		}
    	}catch(Exception e)
    	{}
    	
    	String cuid=param.getCfgParams().get("cuid");

    	if  (cuid !=null){
    		sql += " AND  cuid='"+cuid+"'";
    	}
    	
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return sql;
    	}
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				if(DuctSeg.AttrName.purpose.equals(item.getKey())){
					   sql +=" AND (exists (( select 1 from "+systemSegMap.get(className)+" where ("+className+".CUID="+systemSegMap.get(className)+".RELATED_SYSTEM_CUID) and "+systemSegMap.get(className)+"."+item.getSqlValue()+" )))";
					}else if(WireSeg.AttrName.olevel.equals(item.getKey())){
						sql +=" AND cuid in ( select related_system_cuid from WIRE_SEG where "+ item.getSqlValue() +")";		
					}else{
						sql+= " AND "+item.getSqlValue();
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
