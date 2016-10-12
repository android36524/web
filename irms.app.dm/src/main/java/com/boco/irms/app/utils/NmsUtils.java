package com.boco.irms.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.boco.common.util.debug.LogHome;
import com.boco.graphkit.base.IFilter;
import com.boco.graphkit.ext.IViewElement;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class NmsUtils {
	
    public static DataObjectList getGenericDOList(List list) {
        return getGenericDOList(list, (IFilter)null);
    }

    public static DataObjectList getGenericDOList(List list, IFilter filter) {

        DataObjectList doList = new DataObjectList();
        for (Object o : list) {
            if (o instanceof IViewElement) {

                if (filter != null && !filter.isEnable(o)) {
                    continue;
                }
                GenericDO gdo = ((IViewElement) o).getNodeValue();
                doList.add(gdo);
            }
        }
        return doList;
    }

    public static DataObjectList getGenericDOList(List list, String ...types) {
        final Map<String, String> map = new HashMap<String, String>();
        for (String str : types) {
            map.put(str, str);
        }
        return getGenericDOList(list, new IFilter() {
            public boolean isEnable(Object o) {
                if (o instanceof IViewElement) {
                    GenericDO gdo = ((IViewElement) o).getNodeValue();
                    return map.get(gdo.getClassName()) != null;
                }
                return false;
            }

        });

    }
    
    /**
     * @Description  :得到 attrName 属性对应的值列表
     */
    public static List getList(DataObjectList dols, String attrName) {
        return getList(dols, attrName, true);
    }

    public static List getList(DataObjectList dols, String attrName, boolean isNullable) {
        List list = new ArrayList();
        if(dols==null) {
            return list;
        }
        for (GenericDO gdo : dols) {
            Object key = gdo.getAttrValue(attrName);
            if (!isNullable) {
                if (key == null) {
                    continue;
                }
            }
            list.add(key);
        }
        return list;
    }

    public static List getList(DataObjectList dols, String attrName, IFilter filter) {
        List list = new ArrayList();
        for (GenericDO gdo : dols) {
            Object key = gdo.getAttrValue(attrName);
            if (filter.isEnable(key)) {
                list.add(key);
            }

        }
        return list;
    }
    
    public static Map getCommonMap(Map map1, Map map2) {
        Map map = new HashMap();
        for (Object key : map1.keySet()) {
            if (map2.containsKey(key)) {
                map.put(key, map1.get(key));
            }
        }
        return map;
    }
    
	/**
	 * 属性封装成json
	 * @param map
	 * @return
	 */
	public static String getJsonByAttrNameAndValue(Map<String,String> map){
		String res = "";
		if(!map.isEmpty()){
			Iterator iterator = map.entrySet().iterator();
			res += "{";
			while(iterator.hasNext()){
				Map.Entry entry = (Entry) iterator.next();
				String key = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				res += "\""+key+"\":\""+value+"\",";
			}
			res = res.substring(0,res.length()-1);
			res += "}";
		}
		return res;
	}
	
	 /**
     * 往前台抛异常提示信息
     * @param err
     * @return
     */
    public static StringBuffer getStringBufferErrJson(String err){
    	StringBuffer sb = new StringBuffer();
    	sb.append("{\"error\":\""+err+"\"}");
    	return sb;
    }
    
	public static DataObjectList getObjectsByCuids(String[] cuids) {
		try {
			DataObjectList resList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					"IRelationPropertyLoadBO.getObjsByCuid",new BoActionContext(), cuids);
			DataObjectList dataList = new DataObjectList();
			for (GenericDO gdo : resList) {
				if (gdo == null) {
					continue;
				}
				if (gdo.getClass() == GenericDO.class) {
					GenericDO cloneDbo = gdo.createInstanceByClassName();
					gdo.copyTo(cloneDbo);
					dataList.add(cloneDbo);
				} else {
					dataList.add(gdo);
				}
			}
			return dataList;
		} catch (Exception ex) {
			 LogHome.getLog().info("getObjsByCuid得到对象出错" +ex.getMessage());
		}
		return null;
	}
}
