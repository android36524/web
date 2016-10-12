package com.boco.irms.app.dm.gridbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.core.spring.SysProperty;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class DistrictCacheModel {
	
	private static DistrictCacheModel instance;
	
	private Map<String, District> map = new HashMap<String,District>();
	
	public DataObjectList allDistricts = new DataObjectList();
	
    public DistrictCacheModel(DataObjectList list) {
        allDistricts = list;
        list2map();
	}

	public static DistrictCacheModel getInstance() {
		if(instance == null){
			synchronized(DistrictCacheModel.class){
				String systemDistrictCuid = SysProperty.getInstance().getValue("district");
				String sql = " CUID LIKE '"+systemDistrictCuid+"%' ORDER BY LABEL_CN";
				try {
					DataObjectList  districts = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IDistrictBO.getDistrictBySql", new BoActionContext(),sql);
					instance = new DistrictCacheModel(districts);
				} catch (Exception e) {}
			}
		}
		return instance;
    }
    
    private void list2map(){
    	for(GenericDO gdo :allDistricts){
    		map.put(gdo.getCuid(), (District)gdo);
    	}
    }
    public District getDistrictByCUID(String cuid){
    	return map.get(cuid);
    }
    
    public List  getAllDistricts(){
    	return allDistricts;
    }
    
    public DataObjectList getChildDistrictByCuid(final String cuid) throws Exception {
        DataObjectList resList = new DataObjectList();
		DataObjectList list = new DataObjectList();
		for (GenericDO gdo : allDistricts) {
			Object attrValue = gdo.getAttrValue(District.AttrName.relatedSpaceCuid);
			if (attrValue != null && cuid != null && attrValue.toString().contains(cuid))
				list.add(gdo);
		}
        resList.addAll(list);
        District district = getDistrictByCUID(cuid);
        resList.add(0, district);
        return resList;
    }

}
