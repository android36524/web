package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.SeggroupToRes;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class GenerateCuidAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String[] relatedAttributes = new String[]{"RELATED_DISTRICT_CUID",
		"RELATED_SPACE_CUID","DISTRICT_CUID","SITE_CUID_A","SITE_CUID_Z","ORIG_POINT_CUID","DEST_POINT_CUID"};
	
	/**
	 * 根据CLASSNAME生成CUID
	 * @param className
	 * @param count 生成CUID的个数
	 * @return
	 */
	public String[] getCuidsByClassName(String className,int count) throws UserException{
		String[] cuids = new String[count];
		try{
			for(int i=0; i< count; i++ ){
				GenericDO gdo = new GenericDO();
				gdo.setClassName(className);
				gdo = gdo.createInstanceByClassName();
				gdo.setCuid();
				cuids[i] = gdo.getCuid();
			}

		}catch(Exception e){
			logger.error("生成CUID出错",e);
			throw new UserException("传入的ClassName不正确，无法生成对应的CUID。");
		}
		return cuids;
	}
	
	/**
	 *  判断是不是设计库数据
	 * 调用IDuctManagerBO接口查询
	 * 因为利旧关系，先查资源库，如果资源库中有的话，就不能修改
	 * 在app-dm中就是查现网库
	 * @param cuid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("all")
	public Map getSplitPointDatas(String cuid) throws Exception {
		Map map = new HashMap();
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd(
					"IDuctManagerBO.getObjByCuid", new BoActionContext(), cuid);
			if(gdo != null){
				String cid = gdo.getCuid();
				String origPointCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(DuctSeg.AttrName.origPointCuid));
				String destPointCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(DuctSeg.AttrName.destPointCuid));
				GenericDO origPointDbo = (GenericDO) BoCmdFactory.getInstance().execBoCmd(
						"IDuctManagerBO.getObjByCuid", new BoActionContext(), origPointCuid);
				if(origPointDbo != null){
					String origName = origPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
					String origLongi = String.valueOf(origPointDbo.getAttrValue(Manhle.AttrName.longitude));
					String origLati = String.valueOf(origPointDbo.getAttrValue(Manhle.AttrName.latitude));
					String relatedDistrictCuid = "";
					String className = origPointDbo.getCuid().split("-")[0];
					if(className.equals(Accesspoint.CLASS_NAME)){
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Accesspoint.AttrName.districtCuid));
					}else if(className.equals(Site.CLASS_NAME)){
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Site.AttrName.relatedSpaceCuid));
					}else{
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Manhle.AttrName.relatedDistrictCuid));
					}
					map.put("ORIG_POINT_CUID", origPointDbo.getCuid());
					map.put("ORIG_POINT_NAME", origName);
					map.put("ORIG_POINT_LONGITUDE", origLongi);
					map.put("ORIG_POINT_LATITUDE", origLati);
					map.put("ORIG_POINT_DISTRICT", relatedDistrictCuid);
				}
				GenericDO destPointDbo = (GenericDO) BoCmdFactory.getInstance().execBoCmd(
					"IDuctManagerBO.getObjByCuid", new BoActionContext(), destPointCuid);
				
				if (destPointDbo != null) {
					String destName = destPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
					String destLongi = String.valueOf(destPointDbo.getAttrValue(Manhle.AttrName.longitude));
					String destLati = String.valueOf(destPointDbo.getAttrValue(Manhle.AttrName.latitude));

					String relatedDistrictCuid = "";
					String className = destPointDbo.getCuid().split("-")[0];
					if (className.equals(Accesspoint.CLASS_NAME)) {
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Accesspoint.AttrName.districtCuid));
					} else if (className.equals(Site.CLASS_NAME)) {
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Site.AttrName.relatedSpaceCuid));
					} else {
						relatedDistrictCuid = DMHelper.getRelatedCuid(origPointDbo.getAttrValue(Manhle.AttrName.relatedDistrictCuid));
					}
					
					map.put("DEST_POINT_CUID", destPointDbo.getCuid());
					map.put("DEST_POINT_NAME", destName);
					map.put("DEST_POINT_LONGITUDE", destLongi);
					map.put("DEST_POINT_LATITUDE", destLati);
					map.put("DEST_POINT_DISTRICT", relatedDistrictCuid);
				}
			}
		} catch (Exception e) {
			throw new UserException(e.getMessage());
		}
		return map;
	}
	public List<String> getAllSegsCuidByBranchCuid(String branchCuid) throws Exception{
		String sql = PolewaySeg.AttrName.relatedBranchCuid + " = '" + branchCuid +"' ";
	   	String className = branchCuid.split("-")[0];
	   	DataObjectList allSegsList = new DataObjectList();
	   	if(className.equals(PolewayBranch.CLASS_NAME)){
	   		allSegsList =  getDuctManagerBO().getObjectsBySql(sql, new PolewaySeg());
	   	}
	   	if(className.equals(StonewayBranch.CLASS_NAME)){
	   		allSegsList =  getDuctManagerBO().getObjectsBySql(sql, new StonewaySeg());
	   	}
	   	if(className.equals(DuctBranch.CLASS_NAME)){
	   		allSegsList =  getDuctManagerBO().getObjectsBySql(sql, new DuctSeg());
	   	}
	   	List<String> segsCuidList = new ArrayList<String>();
	   	if(allSegsList != null && allSegsList.size() > 0){
	   		for(GenericDO seg : allSegsList){
	   			String segCuid = seg.getCuid();
	   			segsCuidList.add(segCuid);
	   		}
	   	}
		return segsCuidList;
	}
	
	@SuppressWarnings("all")
	public List getPointsBySegCuid(String segCuid){
	   	List<Map> list = new ArrayList();
	   	GenericDO seg = getDuctManagerBO().getObjByCuid(new BoActionContext(), segCuid);
	   	if(seg != null){
	   		String origCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.origPointCuid));
	    	GenericDO orig = getDuctManagerBO().getObjByCuid(new BoActionContext(), origCuid);
	    	String destCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.destPointCuid));
	    	GenericDO dest = getDuctManagerBO().getObjByCuid(new BoActionContext(), destCuid);
	    	Map mapOrig = getPointAttr(orig);
	    	Map mapDest = getPointAttr(dest);
	    	list.add(mapOrig);
	    	list.add(mapDest);
	   	}
		return list;
	}
	
	@SuppressWarnings("all")
	private Map getPointAttr(GenericDO point){
	    Map map = new HashMap();
	    map.put(Manhle.AttrName.cuid, point.getCuid());
		map.put(Manhle.AttrName.labelCn, DMHelper.getRelatedCuid(point.getAttrValue(Manhle.AttrName.labelCn)));
		map.put(Manhle.AttrName.latitude, DMHelper.getRelatedCuid(point.getAttrValue(Manhle.AttrName.latitude)));
		map.put(Manhle.AttrName.longitude, DMHelper.getRelatedCuid(point.getAttrValue(Manhle.AttrName.longitude)));
	    return map;
	}
	
	private IDuctManagerBO getDuctManagerBO() {
		return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	    
	/**
	 * 
	 * 根据光缆段CUID得到起止点
	 */
	public List<Map> getOrigAndDestPointByWireSegCuid(String cuid, String attrName) throws IOException {
		List<Map> list = new ArrayList<Map>();
		try {
			if (cuid != null && !cuid.equals("")) {
				GenericDO objByCuid = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
				String pointCuid = "";
				if (objByCuid != null) {
					Map map = new HashMap();
					if (WireSeg.AttrName.origPointCuid.equals(attrName.trim())) {
						// 左端
						pointCuid = DMHelper.getRelatedCuid(objByCuid.getAttrValue(WireSeg.AttrName.origPointCuid));
					} else {
						pointCuid = DMHelper.getRelatedCuid(objByCuid.getAttrValue(WireSeg.AttrName.destPointCuid));
					}
					if (!"".equals(pointCuid.trim())) {
						map.put("POINT_CUID", pointCuid);
						list.add(map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String doCompareName(String param, String inputNum) { // 批量命名后的名称对比查看
		DecimalFormat df = new DecimalFormat(param + ".#");
		String format = df.format(Integer.parseInt(inputNum));
		return format;
	}
	
	/**
	 * 批量修改资源列表中数据
	 * @param resList
	 */
	public String updateResAttr(HttpServletRequest request,List<Map<String,String>> resList){
		String message = "修改成功";
		try {
			if(resList != null && resList.size() > 0){
				DataObjectList list = new DataObjectList();
				for(int i = 0; i < resList.size(); i++){
					Map<String, String> resMap = resList.get(i);
					String labelCn = MapUtils.getString(resMap,"LABEL_CN"," ");
					String cuid = MapUtils.getString(resMap,"CUID"," ");
					String bmClassId = resMap.get("bmClassId");
					if(DMHelper.isPointClassName(bmClassId)){
						GenericDO point = getPointsNewAttrs(resMap, bmClassId);
						DataObjectList checkList = getDuctManagerBO().getDatasBySql("select cuid,label_cn from "+bmClassId+ "  where label_cn = '" + labelCn +"'", new Class[]{String.class,String.class});
						if(checkList.size()>0){
							String sqlCuid = checkList.get(0).getAttrString("1");
							String sqlLabelCn = checkList.get(0).getAttrString("2");							
							if(!sqlCuid.equals(cuid)&&sqlLabelCn.equals(labelCn)){
								throw new UserException("已存在同名"+bmClassId+"!");
							}
						}
						list.add(point);
					}
					if(DMHelper.isSegClassName(bmClassId)){
						GenericDO seg = getSegsNewAttrs(resMap, bmClassId);
						DataObjectList checkList = getDuctManagerBO().getDatasBySql("select cuid,label_cn from "+bmClassId+ "  where label_cn = '" + labelCn +"'", new Class[]{String.class,String.class});
						if(checkList.size()>0){
							String sqlCuid = checkList.get(0).getAttrString("1");
							String sqlLabelCn = checkList.get(0).getAttrString("2");							
							if(!sqlCuid.equals(cuid)&&sqlLabelCn.equals(labelCn)){
								throw new UserException("已存在同名"+bmClassId+"!");
							}
						}
						list.add(seg);
					}
				}
				getDuctManagerBO().updateDDMDuctLine(new BoActionContext(), list);
			}
		} catch (Exception e) {
			logger.info("批量修改资源失败。",e);
			message = "修改失败："+e.getMessage();
		} finally {
			return message;
		}
	}
	
	private GenericDO transListToGenericDO(Map<String,String> map) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException{
		GenericDO genericDO = new GenericDO();
		genericDO.setClassName(map.get("bmClassId"));
		if(map.containsKey("CUID") && map.get("CUID")!=null){
			genericDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), map.get("CUID"));
		}else{
			genericDO.setCuid();
		}
		for(String key:map.keySet()){
			 if(!"bmClassId".equals(key) && !"CUID".equals(key)){
			     String value = map.get(key);
				 genericDO.setAttrValue(key, value);
			 }
		}
		return genericDO;
	}
	
	/**
	 * 批量修改资源列表中数据
	 * @param resList
	 */
	public void saveEditResAttr(List<Map<String,String>> resList,String isAdd,String segGroupCuid){
		try {
			if(resList != null && resList.size() > 0){
				DataObjectList list = new DataObjectList();
				DataObjectList seggroups = new DataObjectList();
				for(int i = 0; i < resList.size(); i++){
					Map<String, String> resMap = resList.get(i);
					String bmClassId = resMap.get("bmClassId");
					if("true".equals(isAdd)){
						GenericDO gdo = transListToGenericDO(resMap);
						if(!StringUtils.isEmpty(segGroupCuid)){
							SeggroupToRes seggroupToRes = new SeggroupToRes();
							seggroupToRes.setCuid();
							seggroupToRes.setRelatedResCuid(gdo.getCuid());
							seggroupToRes.setRelatedSeggroupCuid(segGroupCuid);
							seggroupToRes.setRelatedBmclasstypeCuid(gdo.getClassName());
							seggroupToRes.setResType(1L);
							seggroupToRes.setProjectState(1L);
							seggroups.add(seggroupToRes);
						}
						list.add(gdo);
					}else{
						if(DMHelper.isPointClassName(bmClassId)){
							GenericDO point = getPointsNewAttrs(resMap, bmClassId);
							list.add(point);
						}else if(DMHelper.isSegClassName(bmClassId)){
							GenericDO seg = getSegsNewAttrs(resMap, bmClassId);
							list.add(seg);
						}else{
							GenericDO gdo = transListToGenericDO(resMap);
							list.add(gdo);
						}
					}
					
				}
				if("true".equals(isAdd)){
					getDuctManagerBO().addDDMDuctLine(new BoActionContext(), list);
					getDuctManagerBO().addDDMDuctLine(new BoActionContext(), seggroups);
				}else{
					getDuctManagerBO().updateDDMDuctLine(new BoActionContext(), list);
				}
				
			}
		} catch (Exception e) {
			logger.info("批量修改资源失败。",e);
		}
	}
	
	/**
	 * 段资源赋值新属性
	 * @param resMap
	 * @param bmClassId
	 * @return
	 */
	private GenericDO getSegsNewAttrs(Map<String, String> resMap, String bmClassId) {

		GenericDO gdo = new GenericDO();
		try {
			String cuid = resMap.get("CUID");
			if(cuid!= null && !cuid.equals("")){
				gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
				if(gdo !=null){
					String labelCn = resMap.get("LABEL_CN");
					if(labelCn != null && !labelCn.equals("") && !labelCn.equals("undefined")){
						gdo.setAttrValue(WireSeg.AttrName.labelCn, labelCn);//名称
					}
					gdo.setAttrValue(WireSeg.AttrName.ownership,resMap.get("OWNERSHIP"));//产权
					gdo.setAttrValue(WireSeg.AttrName.purpose,resMap.get("PURPOSE"));//用途
					gdo.setAttrValue(WireSeg.AttrName.remark, resMap.get("REMARK"));//备注
					gdo.setAttrValue(WireSeg.AttrName.resOwner,resMap.get("RES_OWNER"));//产权单位
					gdo.setAttrValue(WireSeg.AttrName.userName,resMap.get("USER_NAME"));//使用单位
					String length = resMap.get("LENGTH");
					if(length != null && !length.equals("") && !length.equals("undefined")) {
						gdo.setAttrValue(WireSeg.AttrName.length, resMap.get("LENGTH"));//长度
					}else{
						gdo.setAttrValue(WireSeg.AttrName.length, 0);//长度
					}
					
					gdo.setAttrValue(WireSeg.AttrName.maintDep, resMap.get("MAINT_DEP"));//维护单位
					
					if(bmClassId.equals(WireSeg.CLASS_NAME)){//光缆
						gdo.setAttrValue(WireSeg.AttrName.fiberCount,resMap.get("FIBER_COUNT"));//纤芯总数
						gdo.setAttrValue(WireSeg.AttrName.vendor,resMap.get("VENDOR"));//具体类型
						gdo.setAttrValue(WireSeg.AttrName.wireType,resMap.get("WIRE_TYPE"));//光缆类型
						gdo.setAttrValue(WireSeg.AttrName.proName,resMap.get("PRO_NAME"));//工程名称
					}
					if(bmClassId.equals(DuctSeg.CLASS_NAME)){//管道
						gdo.setAttrValue(DuctSeg.AttrName.ductSegType,resMap.get("DUCT_SEG_TYPE"));//管道段类型
						gdo.setAttrValue(DuctSeg.AttrName.depth,resMap.get("DEPTH"));//深度
						gdo.setAttrValue(DuctSeg.AttrName.multiBuildUser,resMap.get("MULTI_BUILD_USER"));//共建单位
						gdo.setAttrValue(DuctSeg.AttrName.sharedUser,resMap.get("SHARED_USER"));//共享单位
					}
					if(bmClassId.equals(PolewaySeg.CLASS_NAME)){//杆路
						gdo.setAttrValue(PolewaySeg.AttrName.height,resMap.get("HEIGHT"));//高度
						gdo.setAttrValue(PolewaySeg.AttrName.multiBuildUser,resMap.get("MULTI_BUILD_USER"));//共建单位
						gdo.setAttrValue(PolewaySeg.AttrName.sharedUser,resMap.get("SHARED_USER"));//共享单位
					}
					if(bmClassId.equals(StonewaySeg.CLASS_NAME)){//直埋
						gdo.setAttrValue(StonewaySeg.AttrName.proName,resMap.get("PRO_NAME"));//工程名称
						gdo.setAttrValue(StonewaySeg.AttrName.depth,resMap.get("DEPTH "));//深度
						gdo.setAttrValue(StonewaySeg.AttrName.multiBuildUser,resMap.get("MULTI_BUILD_USER"));//共建单位
						gdo.setAttrValue(StonewaySeg.AttrName.sharedUser,resMap.get("SHARED_USER"));//共享单位
					}
					if(bmClassId.equals(HangWallSeg.CLASS_NAME)){//挂墙
						gdo.setAttrValue(HangWallSeg.AttrName.height,resMap.get("HEIGHT"));//高度
						gdo.setAttrValue(HangWallSeg.AttrName.proName,resMap.get("PRO_NAME"));//工程名称
					}
				}
			}
		} catch (com.boco.common.util.except.UserException e) {
			logger.info("批量修改点资源属性失败",e);
		}
		return gdo;
	}

	/**
	 * 点资源赋值新属性
	 * @param resMap
	 * @param bmClassId
	 * @return
	 */
	private GenericDO getPointsNewAttrs(Map<String, String> resMap, String bmClassId) {
		GenericDO gdo = new GenericDO();
		try {
			String cuid = resMap.get("CUID");
			if(cuid!= null && !cuid.equals("")){
				gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
				if(gdo !=null){
					gdo.setAttrValue(Stone.AttrName.labelCn, resMap.get("LABEL_CN"));//名称
					gdo.setAttrValue(Stone.AttrName.maintDep, resMap.get("MAINT_DEP"));//维护单位
					gdo.setAttrValue(Stone.AttrName.resOwner,resMap.get("RES_OWNER"));//产权单位
					gdo.setAttrValue(Stone.AttrName.username,resMap.get("USERNAME"));//使用单位
					gdo.setAttrValue(Stone.AttrName.ownership,resMap.get("OWNERSHIP"));//产权
					gdo.setAttrValue(Stone.AttrName.purpose,resMap.get("PURPOSE"));//用途
					gdo.setAttrValue(Stone.AttrName.remark, resMap.get("REMARK"));//备注
					if(bmClassId.equals(Manhle.CLASS_NAME)){//人井
						gdo.setAttrValue(Manhle.AttrName.wellKind,resMap.get("WELL_KIND"));//人手井类型
						gdo.setAttrValue(Manhle.AttrName.wellType,resMap.get("WELL_TYPE"));//具体类型
						gdo.setAttrValue(Manhle.AttrName.model,resMap.get("MODEL"));//型号
					}
					if(bmClassId.equals(Stone.CLASS_NAME)){//标石
						gdo.setAttrValue(Stone.AttrName.stoneType,resMap.get("STONE_TYPE"));//标石类型
					}
					if(bmClassId.equals(FiberCab.CLASS_NAME)){//光交接箱     
						gdo.setAttrValue(FiberCab.AttrName.fibercabNo, resMap.get("FIBERCAB_NO"));//编号
						gdo.setAttrValue(FiberCab.AttrName.model, resMap.get("MODEL"));//型号
						gdo.setAttrValue(FiberCab.AttrName.faceCount, resMap.get("FACE_COUNT"));//面数
						gdo.setAttrValue(FiberCab.AttrName.faceColCount, resMap.get("FACE_COL_COUNT"));//每面列数
						gdo.setAttrValue(FiberCab.AttrName.tierPortCount, resMap.get("TIER_PORT_COUNT"));//每排行数
						gdo.setAttrValue(FiberCab.AttrName.relatedVendorCuid, resMap.get("RELATED_VENDOR_CUID"));//设备供应商
					}
					if(bmClassId.equals(FiberDp.CLASS_NAME)){//光分纤箱
						gdo.setAttrValue(FiberDp.AttrName.fiberdpNo, resMap.get("FIBERDP_NO"));//编号
						gdo.setAttrValue(FiberDp.AttrName.model, resMap.get("MODEL"));//型号
						gdo.setAttrValue(FiberDp.AttrName.colCount, resMap.get("COL_COUNT"));//列数
						gdo.setAttrValue(FiberDp.AttrName.colRowCount, resMap.get("COL_ROW_COUNT"));//每列行数
						gdo.setAttrValue(FiberDp.AttrName.tierColCount, resMap.get("TIER_COL_COUNT"));//每小排列数
						gdo.setAttrValue(FiberDp.AttrName.tierRowCount, resMap.get("TIER_ROW_COUNT"));//每小排行数
					}
					if(bmClassId.equals(FiberJointBox.CLASS_NAME)){//光接头盒
						gdo.setAttrValue(FiberJointBox.AttrName.kind, resMap.get("KIND"));//设备类型
						gdo.setAttrValue(FiberJointBox.AttrName.junctionType, resMap.get("JUNCTION_TYPE"));//接头形式
						gdo.setAttrValue(FiberJointBox.AttrName.capacity, resMap.get("CAPACITY"));//接头盒容量
						gdo.setAttrValue(FiberJointBox.AttrName.relatedVendorCuid, resMap.get("RELATED_VENDOR_CUID"));//设备供应商
					}
					if(bmClassId.equals(Site.CLASS_NAME)){//站点
						gdo.setAttrValue(Site.AttrName.alias, resMap.get("ALIAS"));//别名
						gdo.setAttrValue(Site.AttrName.abbreviation, resMap.get("ABBREVIATION"));//缩写
						gdo.setAttrValue(Site.AttrName.serviceLevel, resMap.get("SERVICE_LEVEL"));//网元业务级别
						gdo.setAttrValue(Site.AttrName.siteType, resMap.get("SITE_TYPE"));//站点类型
						gdo.setAttrValue(Site.AttrName.location, resMap.get("LOCATION"));//位置
						gdo.setAttrValue(Site.AttrName.realLongitude, resMap.get("REAL_LONGITUDE"));//实际经度
						gdo.setAttrValue(Site.AttrName.realLatitude, resMap.get("REAL_LATITUDE"));//实际纬度
						gdo.setAttrValue(Site.AttrName.contactor, resMap.get("CONTACTOR"));//联系人
						gdo.setAttrValue(Site.AttrName.contactAddress, resMap.get("CONTACT_ADDRESS"));//联系地址
						gdo.setAttrValue(Site.AttrName.telephone, resMap.get("TELEPHONE"));//联系电话
					}
				}
			}
		} catch (com.boco.common.util.except.UserException e) {
			logger.info("批量修改点资源属性失败",e);
		}
		return gdo;
	}
	
	/**
	 * 查询资源数据
	 * 
	 * @param cuids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getObjsByCuidsAndType(List<String> cuids) {
		Map result = new HashMap();
		try {
			if (cuids != null && cuids.size() > 0) {
				GenericDO genericdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), cuids.get(0));
				if (genericdo != null) {
					genericdo.setAttrValue("isDesignRes", true);
					convertObject(genericdo);
					result.put("resObject", genericdo.getObjectToMap());
					result.put("isDesignRes", true);
				}
			}
		} catch (Exception e) {
			logger.error("查询数据失败", e);
		}
		return result;
	}

	/**
	 * 转换 关联属性
	 * 
	 * @param gdo
	 */
	private void convertObject(GenericDO gdo) {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		for (String key : gdo.getAllAttrNames()) {
			Object value = gdo.getAttrValue(key);
			if (ArrayUtils.contains(relatedAttributes, key)) {
				if ("ORIG_POINT_CUID".equals(key) || "DEST_POINT_CUID".equals(key)) {
					tempMap.put(key + "_NAME", getLabelCnByCuid(value.toString()));
				} else {
					District dist = DistrictCacheModel.getInstance().getDistrictByCUID(String.valueOf(value));
					if (dist != null) {
						tempMap.put(key + "_NAME", dist.getLabelCn());
					}
				}
			}
		}
		for (String key : tempMap.keySet()) {
			gdo.setAttrValue(key, tempMap.get(key));
		}
	}
	
	/**
	 * 通过cuid获取名称
	 * 
	 * @param cuid
	 * @return
	 */
	public String getLabelCnByCuid(String cuid) {
		String name = cuid;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid", new BoActionContext(), cuid);
			if (gdo != null) {
				name = gdo.getAttrString("LABEL_CN");
				if (!StringUtils.isEmpty(name)) {
					name = gdo.getAttrString("LABEL_CN");
				}
			}
		} catch (Exception e) {
			logger.error("转换失败");
		}
		return name;
	}
	
}
