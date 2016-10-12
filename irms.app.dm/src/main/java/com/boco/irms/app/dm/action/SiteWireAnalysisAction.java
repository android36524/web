package com.boco.irms.app.dm.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

/**
 * @author Administrator
 * 通达站点光缆分析
 */
public class SiteWireAnalysisAction {
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	/**
	 * 通达站点光缆分析入口
	 * @param cuid
	 * @return
	 * @throws Exception 
	 */
	public Map getSiteWireAnalysis(String cuid) throws Exception{

		String tnmsSql="SELECT LABEL_CN FROM SITE WHERE CUID = '"+ cuid +"'";
		DataObjectList siteList = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new Site(), tnmsSql);
		if(siteList != null && siteList.size()>0){

			String tnmsCuid=cuid;
			Map<String, Object> result=new HashMap<String, Object>();
			//通达站点光缆信息					
			List siteWireListResult=new ArrayList<Map<String, Object>>();
			Map<String, Object> pointMap=new HashMap<String, Object>();	
			//光缆信息
			Map<String, Object> wireMap=new HashMap<String, Object>();
			List wireListResult=new ArrayList<Map<String, Object>>();		
												
			siteWireAnalysis(tnmsCuid,tnmsCuid,pointMap,wireMap,null,null,null);
			for(Map.Entry entry:pointMap.entrySet()){
				List pointList=(ArrayList) entry.getValue();
				List wireList=(ArrayList) wireMap.get(entry.getKey());

				String origCuid=pointList.get(0).toString();
				String destCuid=pointList.get(pointList.size()-1).toString();

				String origsql="SELECT LABEL_CN,LONGITUDE,LATITUDE FROM SITE WHERE CUID='"+origCuid+"'";
				String destsql="SELECT LABEL_CN,LONGITUDE,LATITUDE FROM SITE WHERE CUID='"+destCuid+"'";
				DataObjectList origlist = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new Site(), origsql);
				DataObjectList destlist = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new Site(), destsql);
				
				Map<String, Object> m=new HashMap<String, Object>();
				if(origlist!=null&&origlist.size()>0){
					GenericDO origPointDbo = origlist.get(0);
					
					String origSiteName=origPointDbo.getAttrString(Site.AttrName.labelCn);						
//					String longitude = String.valueOf(origPointDbo.getAttrValue(Site.AttrName.longitude));
//					String latitude = String.valueOf(origPointDbo.getAttrValue(Site.AttrName.latitude));
					
					Object longitude = origPointDbo.getAttrValue(Site.AttrName.longitude);
					Object latitude = origPointDbo.getAttrValue(Site.AttrName.latitude);
					
					if(longitude instanceof BigDecimal && latitude instanceof BigDecimal){
						longitude = (BigDecimal)longitude;
						latitude = (BigDecimal)latitude;
					}else if(longitude instanceof Double && latitude instanceof Double){
						longitude = (Double)longitude;
						latitude = (Double)latitude;
					}
						
					
//					BigDecimal longitude = (BigDecimal)tempmap.get("LONGITUDE");
//					BigDecimal latitude = (BigDecimal)tempmap.get("LATITUDE");
					m.put("ORIG_SITE", origSiteName);
					m.put("ORIG_LONGITUDE", longitude);
					m.put("ORIG_LATITUDE", latitude);
				}else{
					m.put("ORIG_SITE", "");
					m.put("ORIG_LONGITUDE", null);
					m.put("ORIG_LATITUDE", null);
				}
				if(destlist!=null&&destlist.size()>0){
					GenericDO destPointDbo = destlist.get(0);
					String destSiteName=destPointDbo.getAttrString(Site.AttrName.labelCn);						
//					String longitude = String.valueOf(destPointDbo.getAttrValue(Site.AttrName.longitude));
//					String latitude = String.valueOf(destPointDbo.getAttrValue(Site.AttrName.latitude));
					
					Object longitude = destPointDbo.getAttrValue(Site.AttrName.longitude);
					Object latitude = destPointDbo.getAttrValue(Site.AttrName.latitude);
					
					if(longitude instanceof BigDecimal && latitude instanceof BigDecimal){
						longitude = (BigDecimal)longitude;
						latitude = (BigDecimal)latitude;
					}else if(longitude instanceof Double && latitude instanceof Double){
						longitude = (Double)longitude;
						latitude = (Double)latitude;
					}
					
					m.put("DEST_SITE", destSiteName);
					m.put("DEST_LONGITUDE", longitude);
					m.put("DEST_LATITUDE", latitude);							
				}else{
					m.put("DEST_SITE", "");
					m.put("DEST_LONGITUDE", null);
					m.put("DEST_LATITUDE", null);
				}												
				m.put("MARK", entry.getKey().toString());
				m.put("COUNT", (pointList.size()-1));
				siteWireListResult.add(m);
			} 
			for(Map.Entry entry:wireMap.entrySet()){
				List wireList=(ArrayList) entry.getValue();
				for(int i=0;i<wireList.size();i++){
//					Map map=(Map) wireList.get(i);
					GenericDO dbo = (GenericDO) wireList.get(i);
					String origCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(WireSeg.AttrName.origPointCuid));
					String destCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(WireSeg.AttrName.destPointCuid));
					
					String origName=origCuid.split("-")[0];						
					String destName=destCuid.split("-")[0];
					String wireCuid=dbo.getCuid();
					String sql="SELECT '"+entry.getKey().toString()+"' AS MARK, " +
							           "W.CUID AS CUID, "+
								       "W.LABEL_CN AS LABEL_CN, "+
								       "W.FIBER_COUNT AS FIBER_COUNT, "+
								       "S.LABEL_CN AS SYSTEM_NAME, "+
								       "W.ORIG_POINT_CUID, "+
								       "W.DEST_POINT_CUID, "+
								       "(SELECT LABEL_CN "+
								          "FROM "+origName+
								         " WHERE CUID = '"+origCuid+"') AS ORIG_NAME, "+
								       "(SELECT LABEL_CN "+
								         "FROM "+destName +
								         " WHERE CUID = '"+destCuid+"') AS DEST_NAME "+
								  "FROM WIRE_SEG W "+
								  "JOIN WIRE_SYSTEM S "+
								    "ON W.RELATED_SYSTEM_CUID = S.CUID "+
								 "WHERE W.CUID = '"+wireCuid+"'";
//					List<Map<String, String>> list=getListByParams(DAOProxy.DATASOURCE_TNMS,sql);
					Class[] colClassType = new Class[] {String.class, String.class,String.class, String.class,String.class, String.class,String.class, String.class,String.class};
					DataObjectList list = getDuctManagerBO().getDatasBySql(sql, colClassType);
					List lst = new ArrayList();
					if(list != null && list.size()>0){
						for(GenericDO dto : list){
							Map<String, Object> dmap=new HashMap<String, Object>();
							String cid = dto.getAttrString("1");
							String wCid = dto.getAttrString("2");
							String labelCn = dto.getAttrString("3");
							String fiberCount = dto.getAttrString("4");
							String systemLabelCn = dto.getAttrString("5");
							String origPointCuid = dto.getAttrString("6");
							String destPointCuid = dto.getAttrString("7");
							String origPointName = dto.getAttrString("8");
							String destPointName = dto.getAttrString("9");
							dmap.put("WIRE_SEG_CUID", wCid);
							dmap.put("WIRE_SEG_LABEL_CN", labelCn);
							dmap.put("FIBER_COUNT", fiberCount);
							dmap.put("SYSTEM_LABEL_CN", systemLabelCn);
							dmap.put("ORIG_POINT_CUID", origPointCuid);
							dmap.put("DEST_POINT_CUID", destPointCuid);
							dmap.put("ORIG_POINT_NAME", origPointName);
							dmap.put("DEST_POINT_NAME", destPointName);
							dmap.put("CID", cid);
							lst.add(dmap);
						}
					}
					wireListResult.addAll(lst);
				}
			}
			result.put("wirelist", wireListResult);
			result.put("siteWirelist", siteWireListResult);
			return result;
		}
		
		return null;
	}
	
	/**
	 * 通达站点光缆分析
	 * @param cuid
	 * @param siteCuid
	 * @param wirelist
	 * @param siteWirelist
	 * @param wireCuid
	 * @param count
	 * @throws Exception 
	 */
	public void siteWireAnalysis(String cuid,String siteCuid,Map<String, Object> pointMap,
			Map<String, Object> wireMap,ArrayList pointList,ArrayList wireList,String wireCuid) throws Exception{
//		String sql="SELECT CUID, LABEL_CN , DEST_POINT_CUID , ORIG_POINT_CUID "+
//				  "FROM WIRE_SEG "+
//				 "WHERE (DEST_POINT_CUID = '"+cuid+"' "+ 
//				    "OR ORIG_POINT_CUID = '"+cuid+"')";
		String sql=" DEST_POINT_CUID = '"+cuid+"' OR ORIG_POINT_CUID = '"+cuid+"'";
		if(wireCuid!=null){
			sql+=" and CUID!='"+wireCuid+"'";
		}else{
		}
		DataObjectList list = getDuctManagerBO().getObjectsBySql(sql, new WireSeg());
		if(list != null && list.size()>0){
			for(GenericDO wireSegDbo:list){
				String wsCuid = wireSegDbo.getCuid();
				String origCuid=DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.origPointCuid));
				String destCuid=DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.destPointCuid));

				 if(pointList==null||wireList==null){
					 pointList = new ArrayList<String>();
					 wireList = new ArrayList<Object>();	
					 pointList.add(siteCuid);
				 }
				if(!destCuid.equals(cuid)){		
					if(!pointList.contains(destCuid)&&!wireList.contains(wireSegDbo)){
						pointList.add(destCuid);
						wireList.add(wireSegDbo);
					}else{
						continue;
					}						
					if(!"SITE".equals(destCuid.split("-")[0])){//如果不是站点则继续往下找
						siteWireAnalysis(destCuid,siteCuid,pointMap,wireMap,pointList,wireList,wsCuid);
					}else{
						List pList = new ArrayList(pointList.size());
						for(int i=0;i<pointList.size();i++){
							pList.add(new Object());
						}				
						//临时备份
						Collections.copy(pList, pointList);
						pointMap.put(wireList.hashCode()+"", pList);
						List wList = new ArrayList(wireList.size());
						for(int i=0;i<wireList.size();i++){
							wList.add(new Object());
						}							
						Collections.copy(wList,wireList);
						wireMap.put(wireList.hashCode()+"", wList);

						pointList.remove(destCuid);
						wireList.remove(wireSegDbo);
					}
				}else if(!origCuid.equals(cuid)){
					if(!pointList.contains(origCuid)&&!wireList.contains(wireSegDbo)){
						pointList.add(origCuid);
						wireList.add(wireSegDbo);
					}else{
						continue;
					}					
					if(!"SITE".equals(origCuid.split("-")[0])){//如果不是站点则继续往下找
						siteWireAnalysis(origCuid,siteCuid,pointMap,wireMap,pointList,wireList,wsCuid);
					}else{						
						List pList = new ArrayList(pointList.size());
						for(int i=0;i<pointList.size();i++){
							pList.add(new Object());
						}							
						Collections.copy(pList, pointList);
						pointMap.put(wireList.hashCode()+"", pList);
						List wList = new ArrayList(wireList.size());
						for(int i=0;i<wireList.size();i++){
							wList.add(new Object());
						}							
						Collections.copy(wList,wireList);
						wireMap.put(wireList.hashCode()+"", wList);
						pointList.remove(origCuid);
						wireList.remove(wireSegDbo);
					}
				}
			}
		}
	}
}
