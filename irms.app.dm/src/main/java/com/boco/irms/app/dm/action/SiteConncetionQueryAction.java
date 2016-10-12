package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.SiteLink;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.fiber.SearchOpticalWayRouteBOHelper;
import com.boco.transnms.server.bo.ibo.dm.ISiteLinkToSegBO;
import com.boco.transnms.server.dao.base.DaoHomeFactory;
import com.boco.transnms.server.dao.dm.DMGenericDAOX;

public class SiteConncetionQueryAction{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String current_site_cuid="";
	private String orig_site_cuid="";
	private DataObjectList sortedWireSegs=new DataObjectList();
	private Set<String> sortedWireSegCuidSet=new HashSet<String>();
	/**
	 * 根据光缆CUID获取光缆下的光缆段
	 * @param CUID
	 * @return
	 * @throws Exception 
	 */
	public List  getResourceScheme(Map<String,String> queryParam) throws Exception {
		String origCuid="";
		String destCuid="";
		String origLabelCn="";
		String destLabelCn="";
		String includeTranPoint = "";
		String excluceTranPoint = "";
		int fiberState = 0;
		int zjdNum = 0;
		List result = new ArrayList();
		if(queryParam!=null&&queryParam.size()>0){
			origCuid = queryParam.get("ORIG_DEVICE");
			destCuid = queryParam.get("DEST_DEVICE");
			origLabelCn = queryParam.get("ORIG_DEVICE_LABELCN");
			destLabelCn = queryParam.get("DEST_DEVICE_LABELCN");
			includeTranPoint = queryParam.get("INCLUDE_POINT");
			excluceTranPoint = queryParam.get("EXCLUDE_POINT");
			zjdNum = Integer.parseInt(queryParam.get("POINT_COUNT")); 
		}else{
			return result;
		}
		current_site_cuid=origCuid;
		orig_site_cuid=origCuid;
		List<DataObjectList> routes=new ArrayList<DataObjectList>();
		try{
			List<DataObjectList> routeList=getSiteLinkToSegBO().getSegsByOrigAndDestSiteCuidAndzjdNum(origCuid,destCuid,zjdNum);
			
			routes=docheckList(routeList,includeTranPoint,excluceTranPoint);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		result = constructScheme(routes);
		return result;
	}
	
	private List<Map<String,String>> constructScheme(List<DataObjectList> routeList){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		if(routeList==null||routeList.size()<1){
			return result;
		}
		
		//存储site的cuid和name的key值对
		Map<String,String> siteCuidNameMap=new HashMap<String, String>(); 
		try {
			siteCuidNameMap=getSiteLinkToSegBO().getSiteCuidNameMapByRouteList(routeList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(List route:routeList){
			current_site_cuid=orig_site_cuid;				  
			List<String> cuidList=new ArrayList<String>();
			Map<String,String> map = new HashMap<String,String>();
			//调整site_link中site的顺序，使其首位相连
			for(int i=0;i<route.size();i++){
			    GenericDO gdo=(GenericDO)route.get(i);
				String siteACuid=gdo.getAttrString(SiteLink.AttrName.siteACuid);
				String siteZCuid=gdo.getAttrString(SiteLink.AttrName.siteZCuid);
				String cuid = gdo.getAttrString(SiteLink.AttrName.cuid);
				map.put(siteACuid+siteZCuid,cuid);
				if(siteACuid.equals(current_site_cuid)){
				    cuidList.add(siteACuid);
				    cuidList.add(siteZCuid);
					current_site_cuid=siteZCuid;
				}else if(siteZCuid.equals(current_site_cuid)){
			        cuidList.add(siteZCuid);
					cuidList.add(siteACuid);
					current_site_cuid=siteACuid;
				}
			}				  
			  
		    String parentLabel="";
		    String parentCuid = "";

			for(int i=0;i<cuidList.size();i+=2){
			    parentLabel+=siteCuidNameMap.get(cuidList.get(i))+"==>";
			    parentCuid+=cuidList.get(i)+"&&";
			}
			parentLabel+=siteCuidNameMap.get(cuidList.get(cuidList.size()-1));
		    parentCuid+=cuidList.get(cuidList.size()-1);

			List<Map<String,String>> routeInfoList = new ArrayList<Map<String,String>>();
			for(int i=0;i<cuidList.size();i+=2){
				String routeID="";
				String routeLabelCn="";
				String origLabelCn=siteCuidNameMap.get(cuidList.get(i));
				String destLabelCn=siteCuidNameMap.get(cuidList.get(i+1));
				String origCuid=cuidList.get(i);
				String destCuid=cuidList.get(i+1);
				//存储sitelink的site_a_cuid 和site_z_cuid 以及cuid,用于查询光缆段
				routeID = origCuid+"&"+destCuid+"&&"+map.get(origCuid+destCuid);
				routeLabelCn =origLabelCn+"-"+destLabelCn;
				Map<String,String> routeInfo = new HashMap<String,String>();
				routeInfo.put("ID", routeID);
				routeInfo.put("LABEL_CN",routeLabelCn);
				routeInfoList.add(routeInfo);
			}
			Map schemeInfo = new HashMap();
			schemeInfo.put("ID",parentCuid);
			schemeInfo.put("LABEL_CN",parentLabel);
			schemeInfo.put("ROUTE_LIST", routeInfoList);
			result.add(schemeInfo);
	}
		
		return result;
	}
	
	/**
	 * 根据传入的起止点信息，查询optical信息，用于前天展示；
	 * @param param
	 * @return
	 */
	public List getWireSegs(String cuids, String labelCNs){
		List result = new ArrayList();
		if(cuids==null||"".equals(cuids)){
			return result;
		}
		String siteACuid = "";
		String siteZCuid = "";
		String siteAZCuid = "";
		String siteLinkCuid = "";
		String siteALabelCN = "";
		String siteZLabelCN = "";
		String[] array = cuids.split("&&");
		if(array!=null&&array.length>0){
			if(array.length==1){
				siteAZCuid=array[0];
			}else if(array.length>=2){
				siteAZCuid=array[0];
				siteLinkCuid=array[1];
			}
		}

		String[] siteCuidsArray = siteAZCuid.split("&");
		if(siteCuidsArray!=null&&siteCuidsArray.length>0){
			if(siteCuidsArray.length==1){
				siteACuid=siteCuidsArray[0];
			}else if(siteCuidsArray.length>=2){
				siteACuid=siteCuidsArray[0];
				siteZCuid=siteCuidsArray[1];
			}
		}
		
		String[] labelCNsArray = labelCNs.split("-");
		if(labelCNsArray!=null&&labelCNsArray.length>0){
			if(labelCNsArray.length==1){
				siteALabelCN=labelCNsArray[0];
			}else if(labelCNsArray.length>=2){
				siteALabelCN=labelCNsArray[0];
				siteZLabelCN=labelCNsArray[1];
			}
		}
		
		DataObjectList wireSegs = null;
		try {
			wireSegs = this.getWireSegsBySiteASiteZSiteLinkCuid(siteACuid, siteZCuid, siteLinkCuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(GenericDO gdo:wireSegs){
			String opticalID = "";
			String opticalLabelCN = "";
			String routeDescription = "";
			int fiberLevel = 0;
			
			Map wireSegInfo = new HashMap();
			opticalID = gdo.getCuid();
			opticalLabelCN = gdo.getAttrString(GenericDO.AttrName.labelCn);
			wireSegInfo.put("ID", opticalID);
			wireSegInfo.put("LABEL_CN", opticalLabelCN);
			wireSegInfo.put("ORIG_SITE", siteALabelCN);
			wireSegInfo.put("DEST_SITE", siteZLabelCN);
			result.add(wireSegInfo);
		}
		return result;
	}
	
	/**
	 * 根据起止站点和光纤状态，查询optical信息
	 * @param origCuids
	 * @param destCuids
	 * @param fiberState
	 * @return
	 * @throws Exception
	 */
	private DataObjectList getWireSegsBySiteASiteZSiteLinkCuid(String siteACuid, 
			                    String siteZCuid,String siteLinkCuid) throws Exception {
		String sql="CUID IN (SELECT RELATED_SEG_CUID FROM SITE_LINK_TO_SEG WHERE RELATED_SITE_LINK_CUID='"+siteLinkCuid+"')";
		DataObjectList wireSegs=new DataObjectList();
		DataObjectList wireSegList=new DataObjectList();
		try {
			wireSegs=getSiteLinkToSegBO().getObjectsBySql(sql, new WireSeg());
			sortedWireSegs.clear();
			sortedWireSegCuidSet.clear();
			sortWireSegs(wireSegs, siteACuid, siteACuid, siteZCuid);
			wireSegList=getSiteLinkToSegBO().convertCuidToObject(sortedWireSegs, new String[] {WireSeg.AttrName.origPointCuid,WireSeg.AttrName.destPointCuid});
			for(GenericDO gdo:wireSegList){
				Object origPointCuid=gdo.getAttrValue(WireSeg.AttrName.origPointCuid);
				if(origPointCuid instanceof GenericDO){
					GenericDO origGdo=(GenericDO)origPointCuid;
					gdo.setAttrValue(WireSeg.AttrName.origPointCuid, origGdo.getAttrString(WireSeg.AttrName.labelCn));
				}
				Object destPointCuid=gdo.getAttrValue(WireSeg.AttrName.destPointCuid);
				if(destPointCuid instanceof GenericDO){
					GenericDO destGdo=(GenericDO)destPointCuid;
					gdo.setAttrValue(WireSeg.AttrName.destPointCuid, destGdo.getAttrString(WireSeg.AttrName.labelCn));
				}			
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogHome.getLog().error("通过siteLinkCuid获取光缆段出错", e);
		}	
		return wireSegList;
		
	}
	

	private List<DataObjectList> docheckList(List<DataObjectList> routeList, 
			                       String includeTranPoint,String excludeTranPoint) {
        List<DataObjectList> returnRouteList
            =(ArrayList<DataObjectList>)((ArrayList<DataObjectList>)routeList).clone();
		for(int i=0;i<routeList.size();i++){
			 DataObjectList list=routeList.get(i);
			 Set<String> siteSet=new HashSet<String>();
			 
			 for(GenericDO gdo:list){
				 String siteACuid=gdo.getAttrString(SiteLink.AttrName.siteACuid);
				 String siteZCuid=gdo.getAttrString(SiteLink.AttrName.siteZCuid);
				 
				 siteSet.add(siteACuid);
				 siteSet.add(siteZCuid);
			 }

			if(!this.checkIncluded(siteSet, includeTranPoint)
						||!this.checkExcluded(siteSet, excludeTranPoint)){
					returnRouteList.remove(routeList.get(i));			 
		    }
	    }
		return returnRouteList;
	}


	/**
	 * 判断siteSet中是否包含cuids中的字符串
	 * 当cuids中的所有cuid对应的对象都包含在siteSet中时，返回true，否则返回false；
	 * @param siteSet
	 * @param cuids
	 * @return result
	 */
	private boolean checkIncluded(Set siteSet
			,String cuids){
		boolean result = false;
		if(siteSet==null||siteSet.size()<=0){
			return false;
		}
		if(cuids==null||"".equals(cuids)){
			return true;
		}
		
		String[] cuidArray = cuids.split(",");
		List<String> cuidList = java.util.Arrays.asList(cuidArray);
		if(siteSet.containsAll(cuidList)){
			return true;
		}
		return result;
	}

	/**
	 * 判断siteSet中是否包含cuids中的字符串
	 * 当siteSet中的所有对象不包含cuids中的任何一个值时，返回true，否则返回false；
	 * @param siteSet
	 * @param cuids
	 * @return result
	 */
	private boolean checkExcluded(Set siteSet
			,String cuids){
		boolean result = true;
		if(siteSet==null||siteSet.size()<=0
				||cuids==null||"".equals(cuids)){
			return result;
		}
		
		String[] cuidArray = cuids.split(",");
		List<String> cuidList = java.util.Arrays.asList(cuidArray);
		GenericDO gdo = null;
		
		for(String str:cuidList){
			if(siteSet.contains(str)){
				return false;
			}
		}
		return result;
	}
	
	private void sortWireSegs(DataObjectList wireSegs,String siteAcuid,String currentSiteCuid,String siteZcuid){
		
		if(currentSiteCuid.equals(siteZcuid)){
			return;
		}
		
		for(int i=0,size=wireSegs.size();i<size;i++){
			GenericDO dbo=wireSegs.get(i);
			if(dbo instanceof WireSeg){
				WireSeg wireSeg=(WireSeg)dbo;
				String cuid=wireSeg.getCuid();
			    String origPtCuid=wireSeg.getOrigPointCuid();
			    String destPtCuid=wireSeg.getDestPointCuid();
			    
			    if(!isInSortedWireSegCuidSet(cuid) && origPtCuid.equals(currentSiteCuid)){
			    	sortedWireSegCuidSet.add(cuid);
			    	sortedWireSegs.add(wireSeg);
			    	currentSiteCuid=destPtCuid;
			    	sortWireSegs(wireSegs, siteAcuid, currentSiteCuid, siteZcuid);
			    }else if(!isInSortedWireSegCuidSet(cuid) && destPtCuid.equals(currentSiteCuid)){
			    	sortedWireSegCuidSet.add(cuid);
			    	sortedWireSegs.add(wireSeg);
			    	currentSiteCuid=origPtCuid;
			    	sortWireSegs(wireSegs, siteAcuid, currentSiteCuid, siteZcuid);
			    }			    
			}
		}
		
	}
	
	private boolean isInSortedWireSegCuidSet(String cuid){
		if(sortedWireSegCuidSet.contains(cuid)){
			return true;
		}
		return false;
	}
	
	public ISiteLinkToSegBO getSiteLinkToSegBO(){
		return (ISiteLinkToSegBO)BoHomeFactory.getInstance().getBO(ISiteLinkToSegBO.class);
	}	
	
	private DMGenericDAOX getDMGenericDAOX() {
		return (DMGenericDAOX) DaoHomeFactory.getInstance().getDAO(("DMGenericDAOX"));
	}
}
