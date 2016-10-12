package com.boco.irms.app.dm.action.schedule;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.TransElement;
import com.boco.transnms.server.dao.base.DaoHelper;

public class StatHelper {
	private static Map<String ,String > disLabelMap;
	
	public static String separate="_";
	
	public static Double getDoubleByObj(Object obj){
		Double rtnRes = 0.0;
		if(obj instanceof BigDecimal){
			rtnRes =((BigDecimal)obj).doubleValue();
		}else if(obj instanceof Double){
			rtnRes = (Double) obj;
		}
		return rtnRes;
	}
	
	public static Long getLongByObj(Object obj){
		Long rtnRes = 0L;
		if(obj instanceof BigDecimal){
			rtnRes = ((BigDecimal)obj).longValue();
		}else if(obj instanceof Long){
			rtnRes = (Long) obj;
		}
		return rtnRes;
	}
	
	public static Integer getIntByObj(Object obj){
		Integer rtnRes=0;
		if(obj instanceof BigDecimal){
			rtnRes=((BigDecimal)obj).intValue();
		}else if(obj instanceof Integer){
			rtnRes=(Integer) obj;
		}
		return rtnRes;
	}
	
	public static Integer getIntByString(Object obj){
		Integer rtnRes = 0;
		if(obj instanceof BigDecimal){
			rtnRes = ((BigDecimal)obj).intValue();
		}else if(obj instanceof Integer){
			rtnRes = (Integer) obj;
		}else
		{
			try{
				rtnRes = Integer.valueOf(obj.toString());
			}catch(Exception e){}
		}
		return rtnRes;
	}
	
	public static void convertTypeByCuid(List<Map>list,String attr,String newAttr){
		if(list != null && list.size() > 0){
			for(Map map : list){
				String cuid = (String) map.get(attr);
				if(StringUtils.isNotEmpty(cuid)){
					map.put(newAttr, getResTypeByCuid(cuid));
				}
			}
		}
	}
	
	public static String getResTypeByCuid(String cuid){
		if(cuid.indexOf(Site.CLASS_NAME) > -1){
			return "站点";
		}else if(cuid.indexOf(Manhle.CLASS_NAME) > -1){
			return "人手井";
		}else if(cuid.indexOf(Pole.CLASS_NAME) > -1){
			return "电杆";
		}else if(cuid.indexOf(Stone.CLASS_NAME) > -1){
			return "标石";
		}else if(cuid.indexOf(Pole.CLASS_NAME) > -1){
			return "电杆";
		}else if(cuid.indexOf(Inflexion.CLASS_NAME) > -1){
			return "拐点";
		}else if(cuid.indexOf(FiberCab.CLASS_NAME) > -1){
			return "交接箱";
		}else if(cuid.indexOf(FiberDp.CLASS_NAME) > -1){
			return "分纤箱";
		}else if(cuid.indexOf(FiberJointBox.CLASS_NAME) > -1){
			return "接头盒";
		}else if(cuid.indexOf(Accesspoint.CLASS_NAME) > -1){
			return "接入点";
		}else if(cuid.indexOf(TransElement.CLASS_NAME) > -1){
			return "POS";
		}
		return null;
	}
	
	public static Map <String,String> getDisLabelMap(){
		if(disLabelMap == null){
			disLabelMap = getDistrictCuidToName();
		}
		return disLabelMap;
	}
	
	private static Map <String,String> getDistrictCuidToName(){
		Map <String,String> disLabelMap = new HashMap<String,String>();
		String disSql = "SELECT CUID,RELATED_SPACE_CUID,LABEL_CN FROM DISTRICT";
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisTransDAO");
		List<Map<String, Object>> districts = ibatisDAO.querySql(disSql);
		for(Map<String, Object> map : districts){
			String disCuid = (String) map.get(District.AttrName.cuid);
			String parentCuid = (String) map.get(District.AttrName.relatedSpaceCuid);
			String labelCn =(String) map.get(District.AttrName.labelCn);
			disLabelMap.put(disCuid, labelCn);
		}
		return disLabelMap;
	}

	public static double format(double data,String pattern){
    	DecimalFormat format = new DecimalFormat(pattern);
    	String temp = format.format(data);
    	return Double.parseDouble(temp);
    }
	    
    public static double format(double data){
    	String pattern = ".000";
    	DecimalFormat format = new DecimalFormat(pattern);
    	String temp = format.format(data);
    	return Double.parseDouble(temp);
    }
    
    public static double formatExt(double data){
    	data = data/1000;
    	String pattern = ".000";
    	DecimalFormat format = new DecimalFormat(pattern);
    	String temp = format.format(data);
    	return Double.parseDouble(temp);
    }
    
    //根据经纬度计算段的长度(单位M)
    public static double calculateLength(double origLongitude,double origLatitude,double destLongitude,double destLatitude) {
    	double length = 0.0;
        if (DMHelper.isCoordAvailable(origLatitude,origLongitude) && DMHelper.isCoordAvailable(destLatitude,destLongitude)) {
            length = GraphkitUtils.getDistance(origLongitude, origLatitude, destLongitude, destLatitude);
            DecimalFormat formatter = new DecimalFormat(".000");
            String temp = formatter.format(length);
            length = Double.parseDouble(temp);
        }
        return length;
    }
    
    
    //根据经纬度计算段的长度(单位公里)
    public static double calculateLengthExt(double origLongitude,double origLatitude,double destLongitude,double destLatitude) {
    	double length = 0.0;
        if (DMHelper.isCoordAvailable( origLatitude,origLongitude) && DMHelper.isCoordAvailable(destLatitude,destLongitude)) {
            length = GraphkitUtils.getDistance(origLongitude, origLatitude, destLongitude, destLatitude);
            length = length/1000;
            DecimalFormat formatter = new DecimalFormat(".000");
            String temp = formatter.format(length);
            length = Double.parseDouble(temp);
        }
        return length;
    }
}
