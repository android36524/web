package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.irms.app.dm.action.calllater.GridSiteRelCalcuCallLater;
/**
 * @description 综合业务区
 * @author liuchao
 * @verison 
 * @date 2015年1月9日 下午2:33:01
 */
public class MapGridAction {
	
	private static final String TRUE_TEXT = "TRUE";
	
	/**
	 * 获取县级区域下网格
	 * @param districtCuid 区域标识
	 * @return
	 */
	public List getGridXmlByDisCuid(String districtCuid){
		List gridList = null;
		try {
			String sql = "SELECT OBJECTID,CUID,GRID_NAME,GRIDNUMBER,CITY,MAINTANCE_TYPE,STATE,REMARK FROM GEO_GRID WHERE CITY  = '"+ districtCuid + "'";
			gridList = queryForList("IbatisSdeDAO",sql);
		} catch (Exception e) {
			LogHome.getLog().error("查询区域下网格出错", e);
		}
		return gridList;
	}
	
	/**
	 * 查询网格
	 * @param cuid 网格标识
	 * @return
	 */
	public List getQueryListByCuid(String cuid) {
		try {
			String sql = "SELECT G.*,(SELECT D.LABEL_CN FROM DISTRICT D WHERE D.CUID = G.CITY) AS DISTRICT_NAME FROM GEO_GRID G WHERE CUID = '" + cuid + "'";
			return queryForList("IbatisSdeDAO",sql);
		} catch (Exception e) {
			LogHome.getLog().error(e.getMessage(), e);
		}
		return null;
	}
	
	public List queryForList(String dataSource,String sql){
		List list = new ArrayList();
		try {
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
			if(StringUtils.isNotEmpty(sql)){
				list = ibatisDAO.querySql(sql);
			}
		} catch (Exception e) {
			LogHome.getLog().error(e.getMessage(),e);
		}
		return list;
	}
	
	/**
	 * 获取网格下的所有已绑定站点
	 * @param gridId
	 * @return
	 */
	public List findSiteListByGrid(String gridId)
	{
		try{
			String sql = "SELECT MD.RELATED_RIGHT_CUID,S.CUID,S.LABEL_CN FROM T_MD_RES_RES MD,GEO_SITE S WHERE MD.RELATED_LEFT_CUID='"+gridId+"' AND MD.RELATED_RIGHT_CUID = S.CUID";
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			return ibatisDAO.querySql(sql);
		}catch(Exception e)
		{
			LogHome.getLog().error(e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 获取网格图形下的所有站点
	 * @param gridId
	 * @return
	 */
	public List findSiteListByGridShape(String gridId)
	{
		try{
			String sql = "SELECT SITE.CUID, SITE.LABEL_CN, 'SITE' AS RELATED_BMCLASSTYPE_CUID,SDE.ST_ASTEXT(SITE.SHAPE) AS SHAPE,GRID.CUID AS GRID_ID "+
						 " FROM GEO_SITE SITE, GEO_GRID GRID "+
						 " WHERE GRID.CUID='"+gridId+"' AND SDE.ST_WITHIN(SITE.SHAPE,GRID.SHAPE) = 1";
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			return ibatisDAO.querySql(sql);
		}catch(Exception e)
		{
			LogHome.getLog().error(e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 绑定站点与网格
	 * @param gridId
	 * @param list
	 * @return
	 */
	public String bindGridSite(String gridId,String siteCuids)
	{
		try{
			List<Map> list = new ArrayList<Map>();
			String [] sites = siteCuids.split(",");
			for(String site : sites){
				Map map = new HashMap();
				map.put("RELATED_LEFT_CUID",gridId);
				map.put("RELATED_L_TYPE_CUID","GRID");
				map.put("RELATED_RIGHT_CUID",site);
				map.put("RELATED_R_TYPE_CUID","SITE");
				map.put("RELATED_RELATION_CUID","GRID@SITE0");
				map.put("CUID",gridId+"_"+site);
				
				list.add(map);
			}
			IbatisDAO sde = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			//IbatisDAO irms = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			//判断是否重叠
			String intersectSql = "SELECT U.OBJECTID,U.GRID_NAME  FROM GEO_GRID U, GEO_GRID R WHERE SDE.ST_DISJOINT(R.SHAPE, U.SHAPE) = 0 AND SDE.ST_TOUCHES(R.SHAPE, U.SHAPE)=0 AND U.OBJECTID<>R.objectid AND U.STATE=1 AND R.CUID ='"+gridId+"'";  
			List<Map<String,Object>> il = sde.querySql(intersectSql);
			if(il!=null&&il.size() > 0){
				String errorMessage = "";
				for(Map<String,Object> m : il){
					errorMessage += m.get("GRID_NAME")+" ";
				}
				errorMessage += "与本网格重叠，请修改后再试！";
				return errorMessage;
			}else{
				//先删除关联站点
				String sql = "DELETE FROM T_MD_RES_RES WHERE RELATED_LEFT_CUID = '"+gridId+"' AND RELATED_RELATION_CUID='GRID@SITE0'";
				sde.deleteSql(sql);
				//再绑定站点			
				sde.insertDynamicTableBatch("T_MD_RES_RES", list);
			}
		}catch(Exception e)
		{
			return e.getMessage();
		}
		return TRUE_TEXT;
	}
	
	/**
	 * 绑定资源与网格
	 * @param gridId
	 * @param list
	 * @return
	 */
	public String bindGridResource(String gridId,String resData)
	{
		try{
			List<Map> resList = new ArrayList<Map>();
			JSONArray datas = (JSONArray)JSONArray.parse(resData);
			for(int i=0; i<datas.size(); i++){
				Map res = new HashMap();
				Map map = (Map) datas.get(i);
				res.put("RELATED_LEFT_CUID",gridId);
				res.put("RELATED_L_TYPE_CUID","GRID");
				res.put("RELATED_RIGHT_CUID",map.get("CUID").toString());
				res.put("RELATED_R_TYPE_CUID",map.get("RES_TYPE").toString());
				
				String relationStr = "";
				if("SITE".equals(map.get("RES_TYPE"))){
					relationStr = "GRID@SITE0";
				}else if("FIBER_CAB".equals(map.get("RES_TYPE"))){
					relationStr = "GRID@FIBER_CAB0";
				}else if("FIBER_DP".equals(map.get("RES_TYPE"))){
					relationStr = "GRID@FIBER_DP0";
				}
				
				res.put("RELATED_RELATION_CUID",relationStr);
				res.put("CUID",gridId+"_"+map.get("CUID").toString());
				
				resList.add(res);
			}
			
			IbatisDAO sde = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			//IbatisDAO irms = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			//判断是否重叠
			String intersectSql = "SELECT U.OBJECTID,U.GRID_NAME  FROM GEO_GRID U, GEO_GRID R WHERE SDE.ST_DISJOINT(R.SHAPE, U.SHAPE) = 0 AND SDE.ST_TOUCHES(R.SHAPE, U.SHAPE)=0 AND U.OBJECTID<>R.objectid AND U.STATE=1 AND R.CUID ='"+gridId+"'";  
			List<Map<String,Object>> tempList = sde.querySql(intersectSql);
			if(tempList!= null && tempList.size() > 0){
				String errorMessage = "";
				for(Map<String,Object> m : tempList){
					errorMessage += m.get("GRID_NAME")+" ";
				}
				errorMessage += "与本网格重叠，请修改后再试！";
				return errorMessage;
			}else{
				//先删除关联资源
				String sql = "DELETE FROM T_MD_RES_RES WHERE RELATED_LEFT_CUID = '"+gridId+"'";
				sde.deleteSql(sql);
				//再绑定资源			
				sde.insertDynamicTableBatch("T_MD_RES_RES", resList);
			}
		}catch(Exception e)
		{
			return e.getMessage();
		}
		return TRUE_TEXT;
	}
	
	/**
	 * 更新网格
	 * @param map
	 * @return
	 */
	public String updateGrid(Map map)
	{
		Map result = checkGridNameAndCode(map);
		Object retValue = result.get("RESULT");
		if(retValue != null){
			return retValue.toString();
		}
		result.put("RESULT", TRUE_TEXT);
		//1.在SDE中更新
		Map pk = new HashMap();
		try{ 
			map.remove("SHAPE");
			map.remove("SHAPE.AREA");
			map.remove("SHAPE.LEN");
			map.remove("DISTRICT_NAME");
			pk.put("OBJECTID", map.get("OBJECTID"));
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.updateDynamicTable("GEO_GRID",map,pk);
		}catch(Exception e){
			e.printStackTrace();
			result.put("RESULT", "网格自相交，请修改后再试！");
		}
		
		
		//2.在IRMS中更新
		/*try{
			pk.clear();
			map.put("CUID", "GRID-"+map.get("OBJECTID"));
			map.remove("OBJECTID");
			map.remove("SHAPE");
			pk.put("CUID",  "GRID-"+map.get("OBJECTID"));
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			ibatisDAO.updateDynamicTable("GRID",map,pk);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		return result.get("RESULT").toString(); 
	}
	
	/**
	 * 检测网格编号和名称不能重复
	 * @param map
	 * @return
	 */
	private Map checkGridNameAndCode(Map map) {
		Map result = new HashMap();
		String sql = "SELECT OBJECTID,GRIDNUMBER,GRID_NAME FROM GEO_GRID" ;
		if(map.get("OBJECTID") != null){
			sql += " WHERE OBJECTID <> " + map.get("OBJECTID");
		}
		List<Map<String, Object>> list = queryForList("IbatisSdeDAO",sql);
		for (Map<String, Object> map2 : list) {
			if (map.get("GRIDNUMBER")!=null && map.get("GRIDNUMBER").equals(map2.get("GRIDNUMBER"))) {
				result.put("RESULT", "网格编号已存在！");
			}
			if(map.get("GRID_NAME")!=null && map.get("GRID_NAME").equals(map2.get("GRID_NAME"))) {
				result.put("RESULT", "网格名称已存在！");
			}
		}
		return result;
	}
	
	/**
	 * 删除网格
	 * @param cuid
	 * @return
	 */
	public String deleteGrid(String cuid)
	{
		IbatisDAO sde = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
		//IbatisDAO irms = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
		//1.在SDE中删除Grid
		try{
			String sql = "DELETE FROM GEO_GRID WHERE CUID = '"+cuid+"'";
			sde.deleteSql(sql);
		}catch(Exception e)
		{
			return e.getMessage();
		}
		//2.在IRMS中删除Grid
		/*try{
			String irmsSql="DELETE FROM GRID WHERE CUID = '"+cuid+"'";
			irms.deleteSql(irmsSql);
		}catch(Exception e)
		{
			e.printStackTrace();
		}*/
		//3.在SDE中删除Grid和站点的关联关系
		try{
			String siteSql = "DELETE FROM T_MD_RES_RES WHERE RELATED_LEFT_CUID = '"+cuid+"' AND RELATED_RELATION_CUID='GRID@SITE0'";
			sde.deleteSql(siteSql);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return TRUE_TEXT;
	}
	
	/**
	 * 切分网格
	 * @param gridCuid 网格标识
	 * @param geometries 切分后网格图形
	 * @return
	 */
	public String splitGrid(String gridCuid,List geometries)
	{
		try{
			Map map = getGrid(gridCuid);
			if(geometries.size() > 1)
			{
				//1.更新网格信息
				String shape = geometries.get(0).toString();
				Map updateInfo = new HashMap();
				updateInfo.put("OBJECTID", map.get("OBJECTID"));
				updateInfo.put("GRID_NAME", map.get("GRID_NAME")+"_0");
				updateInfo.put("GRIDNUMBER", map.get("GRIDNUMBER")!=null?map.get("GRIDNUMBER")+"_0":map.get("OBJECTID"));
				String result = updateGrid(updateInfo);
				if(!TRUE_TEXT.equals(result))
					return result;
				//2.更新网格形状
				Map updateShape = new HashMap();
				updateShape.put("CUID", map.get("CUID"));
				updateShape.put("SHAPE", shape);
				//切分不判断是否重合
				result = updateGeometry(updateShape,false);
				if(!TRUE_TEXT.equals(result))
					return result;
				//3.新增其他网格
				for(int i=1;i<geometries.size();i++)
				{
					Map insert = new HashMap();
					insert.putAll(map);
					insert.remove("OBJECTID");
					insert.remove("CUID");
					insert.put("SHAPE", geometries.get(i).toString());
					insert.put("GRID_NAME", map.get("GRID_NAME")+"_"+i);
					insert.put("GRIDNUMBER", map.get("GRIDNUMBER")!=null?map.get("GRIDNUMBER")+"_"+i:map.get("OBJECTID"));
					
					String insertResult = insertGridIntoSde(insert);
					if(!TRUE_TEXT.equals(insertResult)){
						return insertResult;
					}
					
					//insertGridIntoIrms(insert);
					//异步建立站点与网格关联
					calculateRelation(insert.get("CUID").toString());
				}
			}
		}catch(Exception e)
		{
			LogHome.getLog().error(e.getMessage(),e);
			return e.getMessage();
		}
		return TRUE_TEXT;
	}
	
	/**
	 * 更新网格Geometry
	 * @param map
	 * @param checkOverlay
	 * @return
	 */
	public String updateGeometry(Map map,boolean checkOverlay)
	{
		try{
			Map pk = new HashMap();
			pk.put("CUID", map.get("CUID"));
			String shape = transferShapeString(map.get("SHAPE").toString());
			map.put("SHAPE","sql:sde.ST_PolyFromText('"+shape+"',"+getSRIDByTable("GEO_GRID")+")");		
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.updateDynamicTable("GEO_GRID", map, pk);
		}catch(Exception e){
			e.printStackTrace();
			return "此网格自相交，请重新绘制！";
		}

		//异步建立站点与网格关联
		calculateRelation(map.get("CUID").toString());
		return TRUE_TEXT;
	}
	
	private String transferShapeString(String shapeString){
		String shape = "";
		JSONObject shapeJson = JSON.parseObject(shapeString);
		JSONArray rings = (JSONArray)shapeJson.get("rings");
		
		if(rings.size() ==  1){
			JSONArray pointArr = (JSONArray)rings.get(0);
			for(int i = 0; i < pointArr.size(); i++){
				JSONArray arr = (JSONArray) pointArr.get(i);
				shape += " " + arr.get(0).toString() + " " + arr.get(1).toString() + ",";
			}
			shape  = "POLYGON ((" + shape.substring(0, shape.length()-1) +"))";
			return shape;
		}else{
			for(int i = 0; i < rings.size() ; i++){
				JSONArray pointArr = (JSONArray) rings.get(i);
				String ringStr = "";
				for(int j = 0; j < pointArr.size(); i++){
					JSONArray arr = (JSONArray) pointArr.get(i);
					ringStr += " " + arr.get(0).toString() + " " + arr.get(1).toString() + ",";
				}
				shape += "((" + ringStr.substring(0, ringStr.length()-1)+ ")),";
			}
			shape = "MULTIPOLYGON (" + shape.substring(0, shape.length()-1) + ")";
			return shape;
		}
	}
	
	private int getSRIDByTable(String tableName){
		String sql="SELECT SRID FROM SDE.ST_GEOMETRY_COLUMNS WHERE TABLE_NAME = '"+tableName+"' ";
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
		List<Map<String, Object>> executeSql = ibatisDAO.querySql(sql);
		if(executeSql!=null&&executeSql.size()>0){
			Map<String, Object> map = executeSql.get(0);
			return ((Number) map.get("SRID")).intValue();
		}
		return 1;
	}
	
	//插入IRMS库
	private void insertGridIntoIrms(Map map) {
		try{
			map.remove("OBJECTID");
			map.remove("SHAPE");
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			ibatisDAO.insertDynamicTable("GRID",map);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//插入SDE库
	private String insertGridIntoSde(Map map) throws Exception {
		if(map.get("OBJECTID") == null )
		{
			int objectId = getSeq("SEQ_GEO_GRID");
			map.put("OBJECTID", objectId);
			map.put("CUID", "GRID-"+objectId);
		}
		if(map.get("SHAPE") !=null )
		{
			String shape = transferShapeString(map.get("SHAPE").toString());
			map.put("SHAPE","sql:sde.ST_PolyFromText('"+shape+"',"+getSRIDByTable("GEO_GRID")+")");	
		}
		try {
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.insertDynamicTable("GEO_GRID", map);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return TRUE_TEXT;
	}
	
	private  int getSeq(String seqName) throws Exception{
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
		return ibatisDAO.getSeq(seqName);
	}
	
	
	private  Map getGrid(String gridCuid) throws Exception{
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
		List<Map> list = ibatisDAO.querySql("select * from GEO_GRID where cuid='"+gridCuid+"'");
		if(list != null && list.size() > 0)
		{
			return list.get(0);
		}
		return null; 
	}
	
	private void calculateRelation(String gridCuid){
		new GridSiteRelCalcuCallLater(gridCuid).execute();
	}
		
	/**
	 * 导入行政边界
	 * @param districtCuid 地区id
	 * @param labelCn 区县名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> importRegionShape(String districtCuid,String labelCn)
	{
		Map<String,Object> retValue = new HashMap<String,Object>();
		//1.根据名称查询区县边界
		String sql = "SELECT OBJECTID FROM COUNTY WHERE LABEL_CN = '" + labelCn + "'";
		List<Map<String,Object>> districtlist  = queryForList("IbatisSdeDAO",sql);
		if(districtlist == null || districtlist.size() == 0)
		{
			retValue.put("result", "没有找到"+labelCn+"边界信息!");
			return retValue;
		}
		//2.插入行政边界
		String disObjectid = districtlist.get(0).get("OBJECTID").toString();
		String gridCuid = null;
		
		//2.1.行政边界是否与其他网格重叠
		String intersectSql = "SELECT G.OBJECTID,G.GRID_NAME FROM GEO_GRID G WHERE SDE.ST_DISJOINT((SELECT SHAPE FROM COUNTY WHERE OBJECTID = " + disObjectid + "), G.SHAPE) = 0  AND G.CUID <> '" + gridCuid + "'";  
		String errorMessage = "";
		List<Map<String,Object>> list = queryForList("IbatisSdeDAO",intersectSql);
		if(list.size() > 0){
			for(Map<String,Object> m : list){
				errorMessage += m.get("GRID_NAME")+" ";
			}
			errorMessage = errorMessage.substring(0, errorMessage.length()-1);
			errorMessage += "与本业务区重叠，请修改后再试！";
			retValue.put("result", errorMessage);
			return retValue;
		}
		//2.2 新增网格
		try
		{
			Map grid = new HashMap();
			int objectid =  getSeq("SEQ_GEO_GRID");
			grid.put("OBJECTID", objectid);
			gridCuid = "GRID-"+grid.get("OBJECTID");
			grid.put("CUID",gridCuid);
			grid.put("GRID_NAME", labelCn+"_边界");
			grid.put("CITY", districtCuid);
			grid.put("STATE", "1");
			String result =  addGrid(grid);
			if(!TRUE_TEXT.equals(result))
			{
				retValue.put("result", result);
				return retValue;
			}
			grid.put("OBJECTID", objectid);
			retValue.put("grid", grid);
		}catch(Exception e)
		{
			retValue.put("result", e.getMessage());
			return retValue;
		}
		//2.3.更新网格边界
		try{
			String updateSql = "UPDATE GEO_GRID SET SHAPE = (SELECT SHAPE FROM COUNTY WHERE OBJECTID = " + disObjectid + ") WHERE CUID='" + gridCuid + "'";
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.updateSql(updateSql);
		}catch(Exception e)
		{
			retValue.put("result", e.getMessage());
			return retValue;
		}
		retValue.put("result", TRUE_TEXT);
		
		//3.异步建立站点与网格关联
		calculateRelation(gridCuid);
		return retValue;
	}
		
	/**
	 * 新增网格
	 * @param map
	 * @return
	 */
	public String addGrid(Map map){
		Map result = checkGridNameAndCode(map);
		Object retValue = result.get("RESULT");
		if(retValue != null){
			return retValue.toString();
		}
		result.put("RESULT", TRUE_TEXT);
		
		try {
			String insertResult = insertGridIntoSde(map);
			if(!TRUE_TEXT.equals(insertResult)){
				return insertResult;
			}
			//insertGridIntoIrms(map);
			//异步建立站点与网格关联
			calculateRelation(map.get("CUID").toString());
		} catch (Exception e) {
			LogHome.getLog().error(e.getMessage(),e);
		}
		
		return result.get("RESULT").toString(); 
	}
		
	/**
	 * 替换行政边界
	 * @param cuid 网格标识
	 * @return
	 */
	public String replaceRegionShape(String cuid)
	{
		//1.根据网格ID查询网格所属区县名称
		String sql = "SELECT OBJECTID,CITY FROM GEO_GRID WHERE CUID = '" + cuid + "'";
		List<Map<String,Object>> list  = queryForList("IbatisSdeDAO",sql);
		if(list == null || list.size() == 0)
		{
			return "此业务区已不存在!";
		}
		String city = list.get(0).get("CITY").toString();
		//2.根据县区ID获取县区的行政边界
		sql = "SELECT C.OBJECTID FROM COUNTY C, DISTRICT D WHERE D.LABEL_CN = C.LABEL_CN AND D.CUID = '" + city + "'";
		list = queryForList("IbatisSdeDAO",sql);
		if(list == null || list.size() == 0)
		{
			return "没有找到业务区所属区域边界信息!";
		}
		String objectid = list.get(0).get("OBJECTID").toString();
		//3.行政边界是否与其他网格重叠
		String intersectSql = "SELECT G.OBJECTID,G.GRID_NAME FROM GEO_GRID G WHERE SDE.ST_DISJOINT((SELECT SHAPE FROM COUNTY WHERE OBJECTID = " + objectid + "), G.SHAPE) = 0  AND G.STATE=1 AND G.CUID <> '" + cuid + "'";  
		String errorMessage = "";
		List<Map<String,Object>> gridList = queryForList("IbatisSdeDAO",intersectSql);
		if(gridList.size() > 0){
			for(Map<String,Object> m : gridList){
				errorMessage += m.get("GRID_NAME")+" ";
			}
			errorMessage += "与本业务区重叠，请修改后再试！";
			return errorMessage;
		}
		//4.更新网格边界
		try{
			String updateSql = "UPDATE GEO_GRID SET SHAPE = (SELECT SHAPE FROM COUNTY WHERE OBJECTID = " + objectid + ") WHERE CUID = '" + cuid + "'";
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.updateSql(updateSql);
		}catch(Exception e)
		{
			return e.getMessage();
		}
		//异步建立站点与网格关联
		calculateRelation(cuid);
		return TRUE_TEXT;
	}
		
	/**
	 * 合并网格
	 * @return
	 */
	public String combineGrid(String cuid1,String cuid2)
	{
		try{
			//1.更新第一个网格
			String updateSql = "UPDATE GEO_GRID SET SHAPE = (SELECT SDE.ST_UNION(A.SHAPE, B.SHAPE) "+
							"  FROM GEO_GRID A, GEO_GRID B "+
							" WHERE A.CUID = '"+cuid1+"' "+
							"   AND B.CUID = '"+cuid2+"') WHERE CUID='"+cuid1+"'";
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			ibatisDAO.updateSql(updateSql);
		}catch(Exception e)
		{
			return e.getMessage();
		}
		//2.删除第二个网格
		//异步建立站点与网格关联
		calculateRelation(cuid1);
		return deleteGrid(cuid2);
	}
	
	
	/**
	 * 获取网格图形下的资源（站点、光交、光分)
	 * @param gridId
	 * @return
	 */
	public Map<String,Object> findResourceByGridShape(String gridId){
		Map<String,Object> resultMap = new HashMap<String,Object>(); 
		resultMap.put("resultList", new ArrayList());
		resultMap.put("siteCount", 0);
		resultMap.put("cabCount", 0);
		resultMap.put("dpCount", 0);
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SITE.CUID,SITE.LABEL_CN,'SITE' AS RES_TYPE,")
			.append("SDE.ST_ASTEXT(SITE.SHAPE) AS SHAPE,GRID.CUID AS GRID_ID ")
			.append("FROM GEO_SITE SITE, GEO_GRID GRID ")
			.append("WHERE GRID.CUID = '")
			.append(gridId)
			.append("' AND SDE.ST_WITHIN(SITE.SHAPE, GRID.SHAPE) = 1 ")
			.append("UNION ALL ")
			.append("SELECT CAB.CUID,CAB.LABEL_CN,'FIBER_CAB' AS RES_TYPE,")
			.append("SDE.ST_ASTEXT(CAB.SHAPE) AS SHAPE,")
			.append("GRID.CUID AS GRID_ID ")
			.append("FROM GEO_FIBER_CAB CAB, GEO_GRID GRID ")
			.append("WHERE GRID.CUID = '")
			.append(gridId)
			.append("' AND SDE.ST_WITHIN(CAB.SHAPE, GRID.SHAPE) = 1 ")
			.append("UNION ALL ")
			.append("SELECT DP.CUID,DP.LABEL_CN,'FIBER_DP' AS RES_TYPE,")
			.append("SDE.ST_ASTEXT(DP.SHAPE) AS SHAPE,")
			.append("GRID.CUID AS GRID_ID ")
			.append("FROM GEO_FIBER_DP DP, GEO_GRID GRID ")
			.append("WHERE GRID.CUID = '")
			.append(gridId)
			.append("' AND SDE.ST_WITHIN(DP.SHAPE, GRID.SHAPE) = 1 ");

			List<Map<String,Object>> list = queryForList("IbatisSdeDAO",sb.toString());
			if(list != null && list.size() > 0){
				resultMap.put("resultList", list);
				Long siteCount = 0L;
				Long cabCount = 0L;
				Long dpCount = 0L;
				for(Map<String,Object> map : list){
					String resType = map.get("RES_TYPE").toString();
					if("SITE".equals(resType)){
						siteCount++;
					}else if("FIBER_CAB".equals(resType)){
						cabCount++;
					}else if("FIBER_DP".equals(resType)){
						dpCount++;
					}
				}
				resultMap.put("siteCount",siteCount);
				resultMap.put("cabCount",cabCount);
				resultMap.put("dpCount",dpCount);
				
				return resultMap;
			}
		}catch(Exception e)
		{
			LogHome.getLog().error(e.getMessage(),e);
		}
		return resultMap;
	}
	
	
}
