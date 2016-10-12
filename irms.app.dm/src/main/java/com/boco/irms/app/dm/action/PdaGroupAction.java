package com.boco.irms.app.dm.action;

import java.util.List;

import com.boco.core.bean.SpringContextUtil;
import com.boco.irms.app.dm.gridbo.PdaGroupBO;
import com.boco.transnms.common.dto.base.DataObjectList;


public class PdaGroupAction {
//    @SuppressWarnings("rawtypes")
//	@RequestMapping(value="getPdaGroupData", method=RequestMethod.POST)
//	public List getPdaGroupData(String name,String districtCuid){
//		List pdaGroupData = getPdaGroupBO().getPdaGroupData(name,districtCuid);
//		return pdaGroupData;
//	}
    
    @SuppressWarnings("rawtypes")
	public List getPdaMemberData(String cuid) throws Exception{
    	List pdaMemberData = getPdaGroupBO().getPdaMemberData(cuid);
		return pdaMemberData;
	}
    @SuppressWarnings("rawtypes")
	public List getPdaMemberDataByName(String name){
		List pdaMemberData = getPdaGroupBO().getPdaMemberDataByName(name);
		return pdaMemberData;
	}
	
//    public String insertPdaGroup(String name,String districtName) throws Exception{
//    	String result=getPdaGroupBO().insertPdaGroup(name,districtName);
//    	return result;
//    }
	public PdaGroupBO getPdaGroupBO(){
		return (PdaGroupBO)SpringContextUtil.getBean("PdaGroupBO");
	}
	
//	public boolean updatePdaGroup(String cuid,String name,String districtName) throws Exception{
//    	boolean result=false;
//    	result=getPdaGroupBO().updatePdaGroup(cuid,name,districtName);
//    	return result;
//	}
	
//	public boolean deletePdaGroup(String cuid){
//		boolean result=false;
//		result=getPdaGroupBO().deletePdaGroup(cuid);
//		return result;
//	}
	
	public boolean deleteFromGroup(String cuid,String groupCuid){
		boolean result=false;
		result=getPdaGroupBO().deleteFromGroup(cuid,groupCuid);
		return result;
	}
	
	public boolean insertIntoGroup(String cuid,String groupCuid) throws Exception{
		boolean result=false;
		result=getPdaGroupBO().insertIntoGroup(cuid,groupCuid);
		return result;
	}
}
