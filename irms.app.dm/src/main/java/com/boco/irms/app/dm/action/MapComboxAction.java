package com.boco.irms.app.dm.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;

/**
 * @description 地图下拉框
 * @author liuchao
 * @verison 
 * @date 2015年1月12日 下午2:41:47
 */
public class MapComboxAction {
	
	public Map getEnumValue(String database,String sql){
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(database);
		List list = ibatisDAO.querySql(sql, 0, 20);
		List labels = new ArrayList();
		List values = new ArrayList();
		
		for(int i=0;i<list.size();i++)
		{
			Map m = (Map)list.get(i);
			if(m.get("LABEL") !=null && m.get("VALUE") != null)
			{
				labels.add(m.get("LABEL"));
				values.add(m.get("VALUE"));
			}
		}
		Map result = new HashMap();
		result.put("labels", labels);
		result.put("values", values);
		return result;
	}
}
