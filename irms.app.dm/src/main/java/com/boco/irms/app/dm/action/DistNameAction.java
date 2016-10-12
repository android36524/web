package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.misc.ISecurityBO;

public class DistNameAction {
	 
	private IDistrictBO getDistrictBO() {
		return BoHomeFactory.getInstance().getBO(IDistrictBO.class);
	}

	private ISecurityBO getSecurityBO(){
    	return (ISecurityBO)BoHomeFactory.getInstance().getBO(ISecurityBO.class);
    }
	
	public String distNameByDistCuid(String cuid){
		String dists=getDistrictBO().getNameByEquipCuid(new BoActionContext(), cuid);
	    return dists;
	}
	
	@SuppressWarnings("all")
	public Map getRunTimeDistrict() throws Exception{
		Map map = new HashMap();
		try {
			District systemDistrict = getSecurityBO().getSystemDistrict(new BoActionContext());
			if(systemDistrict != null){
				String cuid = systemDistrict.getCuid();
				String labelCn = systemDistrict.getLabelCn();
				map = new HashMap();
				map.put("CUID", cuid);
				map.put("LABEL_CN", labelCn);
			}
		} catch (Exception e) {
			throw new UserException("取得运行区域出错！"+e.getMessage());
		}
		return map;
	}
	
	public List<String> getCountyByCityCuid(String cityCuid){
		String sql = " related_space_cuid='"+cityCuid+"'";
		DataObjectList districts = getDistrictBO().getDistrictBySql(new BoActionContext(), sql);
		List<String> districtNames = new ArrayList<String>();
		if(districts!=null && districts.size()>0){
			for(GenericDO gdo:districts){
				District district = (District)gdo;
				districtNames.add(district.getLabelCn());
			}
		}
		return districtNames;
	}
}
