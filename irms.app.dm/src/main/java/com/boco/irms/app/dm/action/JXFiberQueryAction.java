package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.fiber.SearchOpticalWayRouteBOHelper;

public class JXFiberQueryAction{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 根据光缆CUID获取光缆下的光缆段
	 * @param CUID
	 * @return
	 * @throws Exception 
	 */
	public List  getResourceScheme(Map<String,String> queryParam) throws Exception {
		String actionName = SearchOpticalWayRouteBOHelper.ActionName.getOpticalZjjPointByFiberState;
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
			fiberState = Integer.parseInt(queryParam.get("FIBER_STATUS"));
			zjdNum = Integer.parseInt(queryParam.get("POINT_COUNT")); 
		}else{
			return result;
		}
		ArrayList dboss = new ArrayList();
		if (zjdNum == 0) {
			DataObjectList opticals = 
					getOpticalByOrigDestPoint(origCuid,destCuid,fiberState);
			if (opticals != null && opticals.size() > 0) {
				Map schemeInfo = new HashMap();
				schemeInfo.put("ID",origCuid+"&&"+destCuid);
				schemeInfo.put("LABEL_CN",origLabelCn+"==>"+destLabelCn);
				List<Map<String,String>> routeList = new ArrayList<Map<String,String>>();
				Map<String,String> routeInfo = new HashMap<String,String>();
				routeInfo.put("ID", origCuid+"&"+destCuid);
				routeInfo.put("LABEL_CN",origLabelCn+"-"+destLabelCn);
				routeList.add(routeInfo);
				schemeInfo.put("ROUTE_LIST", routeList);
				result.add(schemeInfo);
			}
		} else {
			if (fiberState != 1) {
				dboss = (ArrayList) BoCmdFactory.getInstance().execBoCmd(
						actionName, new BoActionContext(), origCuid, destCuid,
						1, 1L, zjdNum, 0L);
			} else {
				dboss = (ArrayList) BoCmdFactory.getInstance().execBoCmd(
						actionName, new BoActionContext(), origCuid, destCuid,
						1, 1L, zjdNum, new Long(fiberState));
			}
			dboss = docheckList(dboss, includeTranPoint, excluceTranPoint, zjdNum);
			result = constructScheme(dboss);
		}
		return result;
	}
	
	private List<Map<String,String>> constructScheme(List<List> dbos){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		if(dbos==null||dbos.size()<1){
			return result;
		}
		
		for(List points:dbos){
			String labelCn="";
			String id="";
			for(int i=0;i<points.size();i++){
				GenericDO point=(GenericDO) points.get(i);
				labelCn+=point.getAttrString(GenericDO.AttrName.labelCn);
				id +=point.getAttrString(GenericDO.AttrName.cuid);
				if(i!=points.size()-1){
					labelCn+="==>";
					id +="&&";
				}
			}

			List<Map<String,String>> routeList = new ArrayList<Map<String,String>>();
			for(int i=0;i<points.size()-1;i++){
				String routeID="";
				String routeLabelCn="";
				GenericDO pointOrig=(GenericDO) points.get(i);
				GenericDO pointDest=(GenericDO) points.get(i+1);
				routeID = pointOrig.getCuid()+"&"+pointDest.getCuid();
				routeLabelCn = pointOrig.getAttrString(GenericDO.AttrName.labelCn)+"-"
				                          +pointDest.getAttrString(GenericDO.AttrName.labelCn);
				Map<String,String> routeInfo = new HashMap<String,String>();
				routeInfo.put("ID", routeID);
				routeInfo.put("LABEL_CN",routeLabelCn);
				routeList.add(routeInfo);
			}
			Map schemeInfo = new HashMap();
			schemeInfo.put("ID",id);
			schemeInfo.put("LABEL_CN",labelCn);
			schemeInfo.put("ROUTE_LIST", routeList);
			result.add(schemeInfo);
	}
		
		return result;
	}
	
	/**
	 * 根据传入的起止点信息，查询optical信息，用于前天展示；
	 * @param param
	 * @return
	 */
	public List getOpticals(String cuids, String labelCNs){
		List result = new ArrayList();
		if(cuids==null||"".equals(cuids)){
			return result;
		}
		String origCuid = "";
		String destCuid = "";
		String origLabelCN = "";
		String destLabelCN = "";
		String[] cuidsArray = cuids.split("&");
		if(cuidsArray!=null&&cuidsArray.length>0){
			if(cuidsArray.length==1){
				origCuid=cuidsArray[0];
			}else if(cuidsArray.length>=2){
				origCuid=cuidsArray[0];
				destCuid=cuidsArray[1];
			}
		}
		String[] labelCNsArray = labelCNs.split("-");
		if(labelCNsArray!=null&&labelCNsArray.length>0){
			if(labelCNsArray.length==1){
				origLabelCN=labelCNsArray[0];
			}else if(labelCNsArray.length>=2){
				origLabelCN=labelCNsArray[0];
				destLabelCN=labelCNsArray[1];
			}
		}
		
		DataObjectList opticals = null;
		try {
			opticals = this.getOpticalByOrigDestPoint(origCuid, destCuid, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(GenericDO gdo:opticals){
			String opticalID = "";
			String opticalLabelCN = "";
			String routeDescription = "";
			int fiberLevel = 0;
			
			Map opticalInfo = new HashMap();
			opticalID = gdo.getCuid();
			opticalLabelCN = gdo.getAttrString(GenericDO.AttrName.labelCn);
			routeDescription = gdo.getAttrString("ROUTE_DESCIPTION");
			fiberLevel = (int)gdo.getAttrLong("FIBER_LEVEL");
			opticalInfo.put("ID", opticalID);
			opticalInfo.put("LABEL_CN", opticalLabelCN);
			opticalInfo.put("ORIG_SITE", origLabelCN);
			opticalInfo.put("DEST_SITE", destLabelCN);
			opticalInfo.put("ROUTE_DESCRIPTION", routeDescription);
			opticalInfo.put("FIBER_LEVEL", WebDMUtils.convertValue2Enum("DMFIBERLEVEL", fiberLevel));
			result.add(opticalInfo);
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
	private DataObjectList getOpticalByOrigDestPoint(String origCuids, 
			                    String destCuids,int fiberState) throws Exception {
		DataObjectList returnList=new DataObjectList();
		if(origCuids!=null&&origCuids.length()>-1){
			origCuids = origCuids.replace(",", "','");
		}
		if(destCuids!=null&&destCuids.length()>-1){
			destCuids = destCuids.replace(",", "','");
		}
		String sql="";
		if(fiberState!=-1){
			sql="SELECT * FROM "+Optical.CLASS_NAME+
					" WHERE "+Optical.AttrName.origSiteCuid+" IN ('"+origCuids+"') "+
					"  AND "+Optical.AttrName.destSiteCuid+" IN ('"+destCuids+"') "+
					"  AND  "+Optical.AttrName.fiberState+"="+fiberState
						+" UNION "+
					"SELECT * FROM "+Optical.CLASS_NAME+
					" WHERE "+Optical.AttrName.destSiteCuid+" IN ('"+origCuids+"')"+
					"   AND "+Optical.AttrName.origSiteCuid+" IN ('"+destCuids+"')" +
					"   AND "+Optical.AttrName.fiberState+"="+fiberState;
		}else{
			sql="SELECT * FROM "+Optical.CLASS_NAME+
				" WHERE "+Optical.AttrName.origSiteCuid+" IN ('"+origCuids+"') "+
				"  AND "+Optical.AttrName.destSiteCuid+" IN ('"+destCuids+"') "
					+" UNION "+
				"SELECT * FROM "+Optical.CLASS_NAME+
				" WHERE "+Optical.AttrName.destSiteCuid+" IN ('"+origCuids+"')"+
				"   AND "+Optical.AttrName.origSiteCuid+" IN ('"+destCuids+"')" ;
		}
		
			DboCollection opticalColl=(DboCollection) BoCmdFactory.getInstance()
					.execBoCmd("IOpticalBO.getDboOpticalBySelectSql",
					new BoQueryContext(), sql);
			for(int i=0;i<opticalColl.size();i++){
				returnList.add(opticalColl.getAttrRow(i).get(Optical.CLASS_NAME));
			}
		return returnList;
	}
	

	private ArrayList docheckList(ArrayList dboss, 
			String includeTranPoint,String excludeTranPoint, int zjdNum) {
		ArrayList returnList=(ArrayList) dboss.clone();
		for(int i=0;i<dboss.size();i++){
			ArrayList tempList=(ArrayList) dboss.get(i);
			if(tempList.size()!=(zjdNum+2)){
				returnList.remove(dboss.get(i));
			}else{
				DataObjectList tempGdoList=new DataObjectList();
				for(Object o:tempList){
					if(o instanceof GenericDO){
						GenericDO gdo=(GenericDO) o;
						tempGdoList.add(gdo);
					}
				}
				if(!this.checkIncluded(tempGdoList, includeTranPoint)
						||!this.checkExcluded(tempGdoList, excludeTranPoint)){
					returnList.remove(dboss.get(i));
				}
			}
		}
		return returnList;
	}


	/**
	 * 判断gdoList中是否包含cuids中的字符串
	 * 当cuids中的所有cuid对应的对象都包含在gdoList中时，返回true，否则返回false；
	 * @param gdoList
	 * @param cuids
	 * @return result
	 */
	private boolean checkIncluded(DataObjectList gdoList
			,String cuids){
		boolean result = false;
		if(gdoList==null||gdoList.size()<=0){
			return false;
		}
		if(cuids==null||"".equals(cuids)){
			return true;
		}
		
		String[] cuidArray = cuids.split(",");
		List<String> cuidList = java.util.Arrays.asList(cuidArray);
		GenericDO gdo = null;
		List<String> allCuidList = new ArrayList<String>();
		for(Object obj:gdoList){
			gdo = (GenericDO)obj;
			allCuidList.add(gdo.getCuid());
		}
		
		if(allCuidList.containsAll(cuidList)){
			return true;
		}
		return result;
	}

	/**
	 * 判断gdoList中是否包含cuids中的字符串
	 * 当gdoList中的所有对象不包含cuids中的任何一个值时，返回true，否则返回false；
	 * @param gdoList
	 * @param cuids
	 * @return result
	 */
	private boolean checkExcluded(DataObjectList gdoList
			,String cuids){
		boolean result = true;
		if(gdoList==null||gdoList.size()<=0
				||cuids==null||"".equals(cuids)){
			return result;
		}
		
		String[] cuidArray = cuids.split(",");
		List<String> cuidList = java.util.Arrays.asList(cuidArray);
		GenericDO gdo = null;
		List<String> allCuidList = new ArrayList<String>();
		for(Object obj:gdoList){
			gdo = (GenericDO)obj;
			allCuidList.add(gdo.getCuid());
		}
		
		for(String str:cuidList){
			if(allCuidList.contains(str)){
				return false;
			}
		}
		return result;
	}
}
