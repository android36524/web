package com.boco.irms.app.dm.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.dao.IbatisDAO;

public class DMDataPlantBO {

	private IbatisDAO ibatisResDAO;
	public DMDataPlantBO(){
		
	}
	
	public void setIbatisResDAO(IbatisDAO _ibatisResDAO){
		ibatisResDAO = _ibatisResDAO;
	}
	
	public void dealHistoryOverLayResource(List<String> cuidList){
		if(cuidList==null||cuidList.size()<1){
			return;
		}
		String cuids = "";
		for(int i=0;i<cuidList.size();i++){
			if("".equals(cuids)){
				cuids = cuidList.get(i);
			}else{
				cuids =cuids+","+ cuidList.get(i);
			}
			
			if(i>1&&i%500==0){
				moveOverLayResourceToHistory(cuids);
				deleteOverLayResource(cuids);
				cuids="";
			}
		}
		moveOverLayResourceToHistory(cuids);
		deleteOverLayResource(cuids);
	}
	

	public void moveOverLayResourceToHistory(String cuids){
		if(cuids==null||"".equals(cuids)){
			return;
		}
		cuids = cuids.replace(",", "','");
		String sql = "INSERT INTO HIS_OVERLAY_RESOURCE SELECT ORE.*,'' FROM OVERLAY_RESOURCE ORE WHERE CUID IN ('"
				+cuids+ "')";
		this.ibatisResDAO.deleteSql(sql);
	}
	

	public void deleteOverLayResource(String cuids){
		if(cuids==null||"".equals(cuids)){
			return;
		}
		cuids = cuids.replace(",", "','");
		String delSql = "DELETE FROM OVERLAY_RESOURCE WHERE CUID IN ('"
				+cuids+ "')";
		this.ibatisResDAO.deleteSql(delSql);
	}
	
	public Map<String,String> getSiteInfo(String label_cn){
		Map<String,String> result = new HashMap<String,String>();
		if(label_cn==null)
			return result;
		String sqlStr = "SELECT CUID,LABEL_CN,RELATED_SPACE_CUID FROM SITE WHERE LABEL_CN = '"+label_cn+"'";
		LogHome.getLog().info("查询语句："+sqlStr);
		List list = this.ibatisResDAO.querySql(sqlStr); 
		if(list!=null&&list.size()>0){
			Map map = (Map)list.get(0);
			result.put("SITE_CUID", (String)map.get("CUID")) ;
			result.put("SPACE_CUID", (String)map.get("RELATED_SPACE_CUID")) ;
		}
		
		return result;
	}
}
