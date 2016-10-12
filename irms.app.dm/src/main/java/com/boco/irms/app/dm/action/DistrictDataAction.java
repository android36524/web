package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.spring.SysProperty;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.DataObjectList;

public class DistrictDataAction {

	private static Map districtTree;
	
	private static Map<String,String> districtShape = new HashMap<String,String>();
	
	@SuppressWarnings("rawtypes")
	public String getAllData(String labelName) throws IOException {
			StringBuffer result = new StringBuffer();
//			List<Map> list = new ArrayList<Map>();
//			list = DistrictCacheModel.getInstance().getAllDistricts();		
			DataObjectList dtoAll=DistrictCacheModel.getInstance().allDistricts;
			String districtCuid = SysProperty.getInstance().getValue("district", "DISTRICT-00001");
			result.append("{\"district\":[");
			for (int i=0;i<dtoAll.size();i++){
				String cuid=dtoAll.get(i).getAttrString(District.AttrName.cuid);
				String labelCn=dtoAll.get(i).getAttrString(District.AttrName.labelCn);
				if(cuid.indexOf(districtCuid) != -1){
					result.append("{");
					result.append("\"CUID\":").append("\""+cuid+"\",");
					result.append("\"LABEL_CN\":").append("\""+labelCn+"\"");
					result.append("}");
					result.append(",");
				}
			}
			result.deleteCharAt(result.length()-1);
			result.append("]}");
		return result.toString();
	}
	
	public String getDistrictShape(String districtCuid){
		String shape = districtShape.get(districtCuid);
		if(shape == null)
		{
			IbatisDAO sde = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			String districtTable = districtCuid.length() == 26?"REGION":"COUNTY";
			String sql = "SELECT SDE.ST_ASTEXT(SHAPE) AS SHAPE FROM "+districtTable+" WHERE RELATED_DISTRICT_CUID = '"+districtCuid+"'";
			List list = sde.querySql(sql);
			if(list != null && list.size() > 0)
			{
				shape =  (String)((Map)list.get(0)).get("SHAPE");
				districtShape.put(districtCuid, shape);
			}
		}
		return shape;
	}
	
	public Map getDistrictTree(){
		if(districtTree != null){
			return districtTree;
		}
		
		IbatisDAO ibatisSdeDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
		String province = SysProperty.getInstance().getValue("district");
		String sql = "SELECT *  FROM DISTRICT A WHERE LENGTH(A.CUID) <= 32 START WITH CUID = '" + province +
				"' CONNECT BY PRIOR  A.CUID = A.RELATED_SPACE_CUID ";
		
		List list = ibatisSdeDAO.querySql(sql);
		Map root = new HashMap();
		if(list != null && list.size() > 0)
		{
			root = (Map)list.get(0);
			buildDistrictTree(root,list);
		}
		districtTree = root;
		return root;
	}
	
	private void buildDistrictTree(Map root,List list)
	{
		String cuid = (String)root.get("CUID");
		List children = new ArrayList();
		for(int i = 0;i < list.size();i++)
		{
			Map map = (Map)list.get(i);
			String parentCuid = (String)map.get("RELATED_SPACE_CUID");
			if(cuid.equals(parentCuid))
				children.add(map);
		}
		root.put("children", children);
		for(int i=0;i<children.size();i++)
		{
			buildDistrictTree((Map)children.get(i),list);
		}
	}
}
