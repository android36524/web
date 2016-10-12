package com.boco.gis.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import twaver.Generator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.NmsUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.bussiness.consts.DuctEnum.DMSystemLevel;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.DMHelperX;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.CarryingCable;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.SystemPara;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireDisplaySeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.AccessPointBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctChildHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctSystemBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberCabBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberDpBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberJointBoxBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.InflexionBOHelper;
import com.boco.transnms.server.bo.helper.dm.ManhleBOHelper;
import com.boco.transnms.server.bo.helper.dm.PoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewayBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySystemBOHelper;
import com.boco.transnms.server.bo.helper.dm.StoneBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySystemBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSystemBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.dm.ICarryingCableBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctSystemBO;
import com.boco.transnms.server.bo.ibo.dm.IPolewaySegBO;
import com.boco.transnms.server.bo.ibo.dm.ITempCutoverWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.ITempWireToDuctlineBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireToDuctLineBO;

@Controller()
@Service("MapRestService")
@RequestMapping("/MapRestService")
public class MapRestService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Map<String,GenericDO> systemMap = new HashMap<String, GenericDO>();
	private static Map<String,String> adddlineTypeMap = new HashMap<String,String>();
	private static Map<String,String> adddSystemTypeMap = new HashMap<String,String>();
	private static Map<String,String> addmap = new HashMap<String,String>();
	static{
		addmap.put(FiberCab.CLASS_NAME, FiberCabBOHelper.ActionName.addFiberCabs);
		addmap.put(FiberDp.CLASS_NAME, FiberDpBOHelper.ActionName.addFiberDps);
		addmap.put(FiberJointBox.CLASS_NAME, FiberJointBoxBOHelper.ActionName.addFiberJointBoxs);
		addmap.put(Manhle.CLASS_NAME, ManhleBOHelper.ActionName.addManhles);
		addmap.put(Pole.CLASS_NAME, PoleBOHelper.ActionName.addPoles);
		addmap.put(Stone.CLASS_NAME, StoneBOHelper.ActionName.addStones);
		addmap.put(Inflexion.CLASS_NAME, InflexionBOHelper.ActionName.addInflexions);
		addmap.put(Accesspoint.CLASS_NAME, AccessPointBOHelper.ActionName.addAccesspoints);
		addmap.put(AnPos.CLASS_NAME,ManhleBOHelper.ActionName.addManhles);
		
		addmap.put(DuctSeg.CLASS_NAME, DuctSegBOHelper.ActionName.addDuctSegsWithSectionPic);
		addmap.put(PolewaySeg.CLASS_NAME, PolewaySegBOHelper.ActionName.addPolewaySegs);
		addmap.put(StonewaySeg.CLASS_NAME, StonewaySegBOHelper.ActionName.addStonewaySegs);
		addmap.put(UpLineSeg.CLASS_NAME, UpLineSegBOHelper.ActionName.addUpLineSegs);
		addmap.put(HangWallSeg.CLASS_NAME, HangWallSegBOHelper.ActionName.addHangWallSegs);
		addmap.put(WireSeg.CLASS_NAME, WireSegBOHelper.ActionName.addWireSegs);
		
		addmap.put(DuctBranch.CLASS_NAME, DuctBranchBOHelper.ActionName.addDuctBranch);
		addmap.put(PolewayBranch.CLASS_NAME, PolewayBranchBOHelper.ActionName.addPolewayBranch);
		addmap.put(StonewayBranch.CLASS_NAME, StonewayBranchBOHelper.ActionName.addStonewayBranch);
		addmap.put(WireBranch.CLASS_NAME, WireBranchBOHelper.ActionName.addWireBranches);
		
		addmap.put(DuctSystem.CLASS_NAME, DuctSystemBOHelper.ActionName.addDuctSystem);
		addmap.put(PolewaySystem.CLASS_NAME, PolewaySystemBOHelper.ActionName.addPolewaySystem);
		addmap.put(StonewaySystem.CLASS_NAME, StonewaySystemBOHelper.ActionName.addStonewaySystem);
		addmap.put(UpLine.CLASS_NAME, UpLineBOHelper.ActionName.addUpLine);
		addmap.put(HangWall.CLASS_NAME, HangWallBOHelper.ActionName.addHangWall);
		addmap.put(WireSystem.CLASS_NAME, WireSystemBOHelper.ActionName.addWireSystem);
		
		systemMap.put(WireSystem.CLASS_NAME, new WireSystem());
		systemMap.put(DuctSystem.CLASS_NAME, new DuctSystem());
		systemMap.put(PolewaySystem.CLASS_NAME, new PolewaySystem());
		systemMap.put(StonewaySystem.CLASS_NAME, new StonewaySystem());
		systemMap.put(UpLine.CLASS_NAME, new UpLine());
		systemMap.put(HangWall.CLASS_NAME, new HangWall());
		
		adddlineTypeMap.put(DuctSeg.CLASS_NAME, "管道分支");
		adddlineTypeMap.put(PolewaySeg.CLASS_NAME, "杆路分支");
		adddlineTypeMap.put(StonewaySeg.CLASS_NAME, "标石路由分支");
		
		adddSystemTypeMap.put(WireSystem.CLASS_NAME, "光缆");
		adddSystemTypeMap.put(DuctSystem.CLASS_NAME, "管道");
		adddSystemTypeMap.put(PolewaySystem.CLASS_NAME, "杆路");
		adddSystemTypeMap.put(StonewaySystem.CLASS_NAME, "标石路由");
		adddSystemTypeMap.put(UpLine.CLASS_NAME, "引上");
		adddSystemTypeMap.put(HangWall.CLASS_NAME, "挂墙");
		
	}
	
	private IDistrictBO getDistrictBO(){
		return BoHomeFactory.getInstance().getBO(IDistrictBO.class);
	}

	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	
	private IPolewaySegBO getPolewaySegBO(){
		return BoHomeFactory.getInstance().getBO(IPolewaySegBO.class);
	}
	
	private IDuctSegBO getDuctSegBO(){
		return BoHomeFactory.getInstance().getBO(IDuctSegBO.class);
	}
	
	private IDuctSystemBO getDuctSystemBO(){
		return BoHomeFactory.getInstance().getBO(IDuctSystemBO.class);
	}
	
	private ICarryingCableBO getCarryingCableBO(){
		return BoHomeFactory.getInstance().getBO(ICarryingCableBO.class);
	}
	
	private District getDistrictBySystemPara(){
		District district = null;
		try {
			String sql = " PARA_NAME = 'DISTRICT_ID'";
			DataObjectList objectsBySql = getDuctManagerBO().getObjectsBySql(sql, new SystemPara());
			if(objectsBySql != null && objectsBySql.size()>0){
				GenericDO genericDO = objectsBySql.get(0);
				String districtId = DMHelperX.getRelatedCuid(genericDO.getAttrValue(SystemPara.AttrName.paraValue));
				 if (districtId.indexOf(District.CLASS_NAME) != -1) {
			        //如果用户填写的是区域CUID
					 district = getDistrictBO().getDistrictByCuid(new BoActionContext(), districtId);
			     } else {
			        //如果用户填写的是区域名称
			    	String disSql = " LABEL_CN ="+"'"+districtId+"'";
			        DataObjectList districtBySql = getDistrictBO().getDistrictBySql(new BoActionContext(), disSql);
			        if(districtBySql != null && districtBySql.size()>0){
			        	district = (District) districtBySql.get(0);
			        }
			     }
			}
		}catch (Throwable e) {
			logger.error("查询区域报错",e);
		}
		return district;
	}
	
	/**
	 * 地图上增加点
	 * @param bmClassId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/addPoints/{bmClassId}/{scene}/{segGroupCuid}", method = RequestMethod.POST)
	public void addPoints(@PathVariable String bmClassId,@PathVariable String scene,
			@PathVariable String segGroupCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			DataObjectList points = setPointGenericDoAttr(request, bmClassId);
			BoActionContext context = ActionContextUtil.getActionContext();
			logger.debug("addPoints----context===uerid:"+context.getUserId()+":::username:::"+context.getUserName());
			DataObjectList pointss = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(addmap.get(bmClassId), context, points);
			if("erratum".equals(scene)){//勘误场景，补录资源需要和单位工程关联
				for(GenericDO gdo : points){
					gdo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
				}
				getDuctManagerBO().createSegGroupToReses(context,points);
			}
			result.append("{\"success\":\"增加点设施成功\"}");
		} catch (Throwable e) {
			result = NmsUtils.getStringBufferErrJson(e.getMessage());
			LogHome.getLog().info(e.getMessage(),e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	/**
	 * 地图上增加线
	 * @param bmClassId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/addDuctLines/{bmClassId}/{scene}/{segGroupCuid}", method = RequestMethod.POST)
	public void addDuctLines(@PathVariable String bmClassId,@PathVariable String scene,
			@PathVariable String segGroupCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			/*ServiceActionContext ac = new ServiceActionContext(request);
			BoActionContext context = new BoActionContext();
			context.setUserId(ac.getUserId());
			context.setUserName(ac.getUserId());*/			
			
			BoActionContext context = ActionContextUtil.getActionContext();
			logger.debug("context:::"+context.getUserId()+"======"+context.getUserName());
			Map<String, Object> gdosMap = setDuctLineGenericDoAttr(request, bmClassId);
			DataObjectList points = (DataObjectList) gdosMap.get("points");
			DataObjectList lines = (DataObjectList) gdosMap.get("lines");
			Object branchDbo = gdosMap.get("branch");
			//如果是光缆段，需要单独处理
			GenericDO systemDbo = (GenericDO) gdosMap.get("systemDbo");
			
			Map<String, DataObjectList> cuidMap = filterPoints(points);
			for(String tableName:cuidMap.keySet()){
				DataObjectList pointsList = cuidMap.get(tableName);
				if(pointsList != null && pointsList.size()>0){
					String cName = pointsList.get(0).getClassName();
					BoCmdFactory.getInstance().execBoCmd(addmap.get(cName), context, pointsList);
				}
			}
			String attrValue = systemDbo.getAttrString("ISOLD");
			if(bmClassId.equals(WireSeg.CLASS_NAME)){
				DataObjectList wireSystemList = new DataObjectList();
				if(attrValue ==null || !attrValue.trim().equals("0")){
					wireSystemList.add(systemDbo);
				}
				DataObjectList branchList = new DataObjectList();
				if(branchDbo instanceof DataObjectList){
					branchList = (DataObjectList) branchDbo;
				}
//				if(branchList.size()>0){
//					//暂时用这个
//					for(GenericDO branch:branchList){
//						getWireBranchBO().addWireBranch(new BoActionContext(), (WireBranch) branch);
//					}
//				}
//				GenericDO branch = ((DataObjectList) branchDbo).get(0);
				
//				getWireSegBO().addWireSegs(new BoActionContext(), lines);
				// DataObjectList systemList, DataObjectList wireBranchList, DataObjectList segList, DataObjectList disSegList,DataObjectList endPointList, String mapCuid
				getWireSegBO().addWireSegs(context, wireSystemList, branchList, lines, new DataObjectList(), new DataObjectList(), null);
//				getWireSegBO().addWireSegs(new BoActionContext(),wireSystemList, branchList, lines, new DataObjectList(), new DataObjectList(), null);
			}else{
				if(branchDbo != null){
					if(branchDbo instanceof GenericDO){
						BoCmdFactory.getInstance().execBoCmd(addmap.get(((GenericDO)branchDbo).getClassName()),context, branchDbo);
					}
				}
				if(systemDbo != null && (attrValue ==null || !attrValue.trim().equals("0"))){
					BoCmdFactory.getInstance().execBoCmd(addmap.get(systemDbo.getClassName()), context, systemDbo);
				}
				if(lines != null && lines.size()>0){
					BoCmdFactory.getInstance().execBoCmd(addmap.get(bmClassId), context, lines);
				}
			}
			if("erratum".equals(scene)){//勘误场景，补录资源需要和单位工程关联
				if(lines!=null && lines.size()>0){
					for(GenericDO gdo : lines){
						gdo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
					}
					getDuctManagerBO().createSegGroupToReses(context, lines);
				}
			}
			result.append("{\"success\":\"增加线设施成功\"}");
		} catch (Throwable e) {
			result = NmsUtils.getStringBufferErrJson(e.getMessage());
			LogHome.getLog().error("增加线设施失败：",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	/**
	 * 过滤同类型点（暂未过滤地图上原来的已有点）
	 * 2014-3-19增加过滤地图上已有点,新增加双击后，已有点应该不能移动
	 * @param points
	 * @return
	 */
	private Map<String, DataObjectList> filterPoints(DataObjectList points){
		Map<String, DataObjectList> mapCuids = new HashMap<String, DataObjectList>();
		for(GenericDO dbo:points){
			String cuid = dbo.getCuid();
			String pointClassName = parseClassNameFromCuid(cuid);
			DataObjectList list = mapCuids.get(pointClassName);
			if(list == null){
				list = new DataObjectList();
				mapCuids.put(pointClassName, list);
			}
			String isOld = dbo.getAttrString("ISOLD");
			if(isOld == null || !isOld.equals("0")){//0代表地图上已有点
				list.add(dbo);
			}
		}
		return mapCuids;
	}
	
	/**
	 * 地图上增加线设施时，组装点、线数据
	 * @param request
	 * @param bmClassId
	 * @param kind
	 * @return
	 */
	private Map<String,Object> setDuctLineGenericDoAttr(HttpServletRequest request,String bmClassId){
		Map<String,Object> gdoMap = new HashMap<String,Object>();
		DataObjectList ductLineList = new DataObjectList();
		
		DataObjectList pointList = new DataObjectList();
		Map<String,GenericDO> pointNameMap = new HashMap<String,GenericDO>();
		//点入库前需要根据名称或者前台设置的id来判断重复值
		String parameter = request.getParameter("ductlines");
		JSONArray obj = JSONArray.parseArray(parameter);
		int count = obj.size();

		//分支的两个点
		String origPCuid ="";
		String destPCuid ="";
		JSONObject origObj = (JSONObject) obj.get(0);
		String districtCuid = origObj.getString(Manhle.AttrName.relatedDistrictCuid);
		String relatedSystemLabelCn = origObj.getString("RELATED_SYSTEM_LABEL_CN");
//		String relatedSystemCuid=DMHelperX.getRelatedCuid(origObj.get(DuctSeg.AttrName.relatedSystemCuid));
		String relatedProjectCuid=DMHelperX.getRelatedCuid(origObj.get(DuctSeg.AttrName.relatedProjectCuid));
//		String origName = String.valueOf(origObj.get("ORIG_POINT_NAME"));
//		JSONObject destObj = (JSONObject) obj.get(count-1);
//		String destName = String.valueOf(destObj.get("DEST_POINT_NAME"));
//		String branchName = origName+"--"+destName;
		String branchName = String.valueOf(origObj.get("BRANCH_NAME"));
		String specialPurpose = null,
		       olevel = null;
		if(bmClassId.equals(WireSeg.CLASS_NAME)) {
		    specialPurpose = origObj.getString("SPECIAL_PURPOSE");
	        olevel  = origObj.getString("OLEVEL");
		}
		
		for(int i=0;i<count;i++){
			JSONObject jsonObject = (JSONObject) obj.get(i);
			String labelCN = String.valueOf(jsonObject.get(DuctSeg.AttrName.labelCn));
			String origPointName = String.valueOf(jsonObject.get("ORIG_POINT_NAME"));
			String origPointType = String.valueOf(jsonObject.get("ORIG_POINT_TYPE"));
			String origIsOld = String.valueOf(jsonObject.get("ORIG_POINT_ISOLD"));
			String origPointCuid = String.valueOf(jsonObject.get("ORIG_POINT_CUID"));
			Object origkind =  jsonObject.get("ORIG_POINT_KIND");
			if(i==0){
				origPCuid = origPointCuid;
			}
			double origPointLongit = parseDouble(jsonObject.get("ORIG_POINT_LONGITUDE"));
			double origPointLati = parseDouble(jsonObject.get("ORIG_POINT_LATITUDE"));
			
			GenericDO origPointDbo = null;
			GenericDO pODbo = pointNameMap.get(origPointName);
			if(pODbo == null){
				origPointDbo = getGenericDbo(origPointType);
				if(origPointDbo != null && origIsOld.equals("0")){
					origPointDbo.setAttrValue("ISOLD", origIsOld);
					origPointDbo.setCuid(origPointCuid);
				}else{
					origPointDbo.setCuid();
				}
				origPointDbo.setAttrValue(Manhle.AttrName.labelCn, origPointName);
				origPointDbo.setAttrValue(Manhle.AttrName.longitude, origPointLongit);
				origPointDbo.setAttrValue(Manhle.AttrName.latitude, origPointLati);
				if(origPointDbo instanceof Accesspoint){
					origPointDbo.setAttrValue(Accesspoint.AttrName.districtCuid, districtCuid);
				}else{
					origPointDbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, districtCuid);
				}
				
				if(!pointNameMap.containsKey(origPointName)){
					pointNameMap.put(origPointName, origPointDbo);
				}
				origPointDbo.setAttrValue(DuctSeg.AttrName.relatedProjectCuid, relatedProjectCuid);
				if(null != origkind) {
				    origPointDbo.setAttrValue(FiberJointBox.AttrName.kind, origkind);
                }
			}else{
				origPointDbo = pODbo;
			}
			//分支名称取第一个点和最后一个点的名字组合起来
//			branchName = String.valueOf(jsonObject.get("BRANCH_NAME"));
			String destPointName = String.valueOf(jsonObject.get("DEST_POINT_NAME"));
//			if(i ==0){
//				branchName = origPointName + "--" + destPointName;
//			}
			String destPointType = String.valueOf(jsonObject.get("DEST_POINT_TYPE"));
			String destIsOld = String.valueOf(jsonObject.get("DEST_POINT_ISOLD"));
			String destPointCuid = String.valueOf(jsonObject.get("DEST_POINT_CUID"));
			Object destkind =  jsonObject.get("DEST_POINT_KIND");
			if(i==(count-1)){
				destPCuid = destPointCuid;
			}
			double destPointLongi = parseDouble(jsonObject.get("DEST_POINT_LONGITUDE"));
			double destPointLati = parseDouble(jsonObject.get("DEST_POINT_LATITUDE"));
			
			GenericDO destPointDbo = null;
			GenericDO pDDbo = pointNameMap.get(destPointName);
			if(pDDbo == null){
				destPointDbo = getGenericDbo(destPointType);
				if(destIsOld != null && destIsOld.equals("0")){
					destPointDbo.setAttrValue("ISOLD", origIsOld);
					destPointDbo.setCuid(destPointCuid);
				}else{
					destPointDbo.setCuid();
				}
				destPointDbo.setAttrValue(Manhle.AttrName.labelCn, destPointName);
				destPointDbo.setAttrValue(Manhle.AttrName.longitude, destPointLongi);
				destPointDbo.setAttrValue(Manhle.AttrName.latitude, destPointLati);
				if(destIsOld != null && destIsOld.equals("0")){
					destPointDbo.setAttrValue("ISOLD", destIsOld);
				}
				if(destPointDbo instanceof Accesspoint){
					destPointDbo.setAttrValue(Accesspoint.AttrName.districtCuid, districtCuid);
				}else{
					destPointDbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, districtCuid);
				}
				destPointDbo.setAttrValue(DuctSeg.AttrName.relatedProjectCuid, relatedProjectCuid);
				if(null != destkind) {
				    destPointDbo.setAttrValue(FiberJointBox.AttrName.kind, destkind);
				}
				if(!pointNameMap.containsKey(destPointName)){
					pointNameMap.put(destPointName, destPointDbo);
				}
			}else{
				destPointDbo = pDDbo;
			}
			
			int indexInBranch = parseInt(jsonObject.get("INDEX_IN_BRANCH"));
			
			GenericDO dbo = getGenericDbo(bmClassId);
			dbo.setCuid();
			dbo.setAttrValue(DuctSeg.AttrName.relatedProjectCuid, relatedProjectCuid);
			dbo.setAttrValue(DuctSeg.AttrName.labelCn, labelCN);
			dbo.setAttrValue(DuctSeg.AttrName.origPointCuid, origPointDbo.getCuid());
			dbo.setAttrValue(DuctSeg.AttrName.destPointCuid, destPointDbo.getCuid());
			dbo.setAttrValue(DuctSeg.AttrName.indexInBranch, indexInBranch);
			dbo.setAttrValue(DuctSeg.AttrName.direction, "1");
			if(null != specialPurpose) {
			    dbo.setAttrValue(WireSeg.AttrName.specialPurpose, specialPurpose);
			}
			if(null != olevel) {
			    dbo.setAttrValue(WireSeg.AttrName.olevel, olevel);
			}
			
			Object segLength = jsonObject.get(DuctSeg.AttrName.length);
			if(null != segLength) {
			    dbo.setAttrValue(DuctSeg.AttrName.length, segLength);
			}
			ductLineList.add(dbo);
		}
		GenericDO systemDbo = getSystemBySegType(bmClassId);
		systemDbo.setAttrValue(DuctSystem.AttrName.relatedSpaceCuid, districtCuid);
		systemDbo = doSystem(systemDbo,relatedSystemLabelCn);
		
		gdoMap.put("systemDbo", systemDbo);
		GenericDO branchDbo = getBranchBySegType(bmClassId);
		if(branchDbo != null){
			if (systemDbo instanceof UpLine || systemDbo instanceof HangWall) {
				branchDbo.setCuid(systemDbo.getCuid());
	        }else{
	        	branchDbo.setCuid();
	        }
			branchDbo.setAttrValue(DuctBranch.AttrName.relatedSystemCuid, systemDbo.getCuid());
			branchDbo.setAttrValue(DuctBranch.AttrName.labelCn, branchName);
			branchDbo.setAttrValue(DuctBranch.AttrName.origPointCuid, origPCuid);
			branchDbo.setAttrValue(DuctBranch.AttrName.destPointCuid, destPCuid);
		      
			gdoMap.put("branch", branchDbo);
		}
		
		
		if(bmClassId.equals(WireSeg.CLASS_NAME)){
			DataObjectList wireBranchList = doWireBranch(ductLineList,systemDbo);
			gdoMap.put("branch", wireBranchList);
		}else{
			doLineSegs(bmClassId,ductLineList,branchDbo,systemDbo,branchName);
			
		}
		
		if(!pointNameMap.isEmpty()){
			Iterator it = pointNameMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Entry) it.next();
				GenericDO value = (GenericDO) entry.getValue();
				pointList.add(value);
			}
		}
		gdoMap.put("points", pointList);
		gdoMap.put("lines", ductLineList);
		return gdoMap;
	}
	
	/**
	 * 给段设置所属分支和所属系统
	 * @param bmClassId
	 * @param lineSegs
	 * @param branchDbo
	 * @param systemDbo
	 */
	private void doLineSegs(String bmClassId,DataObjectList lineSegs,GenericDO branchDbo,GenericDO systemDbo,String branchName){
		String systemCuid = systemDbo.getCuid();
		String className = lineSegs.get(0).getClassName();
		if(!bmClassId.equals(UpLineSeg.CLASS_NAME) && !bmClassId.equals(HangWallSeg.CLASS_NAME)){
			String branchCuid = branchDbo.getCuid();
			branchDbo.setAttrValue(WireBranch.AttrName.labelCn,  branchName + adddlineTypeMap.get(className));
			for(GenericDO segDbo:lineSegs){
				segDbo.setAttrValue(DuctSeg.AttrName.relatedBranchCuid, branchCuid);
				segDbo.setAttrValue(DuctSeg.AttrName.relatedSystemCuid, systemCuid);
			}
		}else{
			for(GenericDO segDbo:lineSegs){
				segDbo.setAttrValue(DuctSeg.AttrName.relatedSystemCuid, systemCuid);
			}
		}
	}
	/**
	 * 根据段类型得到所属系统类型
	 * @param bmClassId
	 * @return
	 */
	private GenericDO getSystemBySegType(String bmClassId){
		GenericDO systemDbo = null;
		if(bmClassId.equals(DuctSeg.CLASS_NAME)){
			systemDbo = new DuctSystem();
		}else if(bmClassId.equals(PolewaySeg.CLASS_NAME)){
			systemDbo = new PolewaySystem();
		}else if(bmClassId.equals(StonewaySeg.CLASS_NAME)){
			systemDbo = new StonewaySystem();
		}else if(bmClassId.equals(UpLineSeg.CLASS_NAME)){
			systemDbo = new UpLine();
		}else if(bmClassId.equals(HangWallSeg.CLASS_NAME)){
			systemDbo = new HangWall();
		}else if(bmClassId.equals(WireSeg.CLASS_NAME)){
			systemDbo = new WireSystem();
		}
		return systemDbo;
	}
	
	/**
	 * 根据段类型得到分支类型
	 * @param bmClassId
	 * @return
	 */
	private GenericDO getBranchBySegType(String bmClassId){
		GenericDO branch = null;
		if(bmClassId.equals(DuctSeg.CLASS_NAME)){
			branch = new DuctBranch();
		}else if(bmClassId.equals(PolewaySeg.CLASS_NAME)){
			branch = new PolewayBranch();
		}else if(bmClassId.equals(StonewaySeg.CLASS_NAME)){
			branch = new StonewayBranch();
		}/*else if(bmClassId.equals(WireSeg.CLASS_NAME)){
			branch = new WireBranch();
		}*/
		return branch;
	}
	
    /**
     * 处理系统
     * @param systemDbo
     * @param origName
     * @param destName
     */
    private GenericDO doSystem(GenericDO systemDbo,String segName) {

    	String className = systemDbo.getClassName();
		// PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.model.dm.DMCacheModel.java30")
		// 光缆
		String systemLabelCn = segName/* + adddSystemTypeMap.get(className)*/;
		// 得到光缆系统的名称
		// 根据名称查询光缆系统
		String sql = "label_cn = '" + systemLabelCn + "'";
		DataObjectList list = getDuctManagerBO().getSimpleDMDObySql(new BoActionContext(), sql, systemMap.get(className));
		if (list != null && !list.isEmpty()) {
			systemDbo = list.get(0);
			systemDbo.setAttrValue("ISOLD", "0");
		} else {
			systemDbo.setCuid();
			systemDbo.setAttrValue(DuctSystem.AttrName.labelCn, systemLabelCn);
			// 现在没有用户，区域暂时用默认
			// wireSystem.setRelatedSpaceCuid(UserSecurityModel.getInstance().getCurrentUserDistrictCuid());
			systemDbo.setAttrValue(DuctSystem.AttrName.systemLevel, DMSystemLevel._localconnect);
		}
		return systemDbo;
    }
    
    /**
     * 处理光缆分支
     * @param segList
     * @return
     */
    private DataObjectList doWireBranch(DataObjectList segList,GenericDO systemDbo) {
        DataObjectList branchList = new DataObjectList();
        for (GenericDO dto : segList) {
            if (dto instanceof WireSeg) {
                WireSeg wireSeg = (WireSeg) dto;
                WireBranch wireBranch = new WireBranch();
                wireBranch.setCuid();
                wireBranch.setLabelCn(wireSeg.getLabelCn());
                wireBranch.setRelatedSystemCuid(systemDbo.getCuid());
                wireBranch.setOrigPointCuid(wireSeg.getOrigPointCuid());
                wireBranch.setDestPointCuid(wireSeg.getDestPointCuid());
                branchList.add(wireBranch);
                wireSeg.setRelatedBranchCuid(wireBranch.getCuid());
                wireSeg.setRelatedSystemCuid(systemDbo.getCuid());
            }
        }
        return branchList;
    }
	/**
	 * 根据bmClassId生成一个新对象
	 * @param bmClassId
	 * @return
	 */
	private GenericDO getGenericDbo(String bmClassId){
		GenericDO dbo = null;
		if(bmClassId.equals(WireSeg.CLASS_NAME)){
			dbo = new WireSeg();
		}else if(bmClassId.equals(DuctSeg.CLASS_NAME)){
			dbo = new DuctSeg();
		}else if(bmClassId.equals(PolewaySeg.CLASS_NAME)){
			dbo = new PolewaySeg();
		}else if(bmClassId.equals(StonewaySeg.CLASS_NAME)){
			dbo = new StonewaySeg();
		}else if(bmClassId.equals(UpLineSeg.CLASS_NAME)){
			dbo = new UpLineSeg();
		}else if(bmClassId.equals(HangWallSeg.CLASS_NAME)){
			dbo = new HangWallSeg();
		}else if(bmClassId.equals(Manhle.CLASS_NAME)){
			dbo = new Manhle();
		}else if(bmClassId.equals(Pole.CLASS_NAME)){
			dbo = new Pole();
		}else if(bmClassId.equals(Stone.CLASS_NAME)){
			dbo = new Stone();
		}else if(bmClassId.equals(Inflexion.CLASS_NAME)){
			dbo = new Inflexion();
		}else if(bmClassId.equals(FiberJointBox.CLASS_NAME)){
			dbo = new FiberJointBox();
		}else if(bmClassId.equals(FiberCab.CLASS_NAME)){
			dbo = new FiberCab();
		}else if(bmClassId.equals(FiberDp.CLASS_NAME)){
			dbo = new FiberDp();
		}else if(bmClassId.equals(Accesspoint.CLASS_NAME)){
			dbo = new Accesspoint();
			dbo.setAttrValue(Accesspoint.AttrName.siteCuid, " ");
			dbo.setAttrValue(Accesspoint.AttrName.vpCuid, " ");
			dbo.setAttrValue(Accesspoint.AttrName.vpnCuid, " ");
		}else if(bmClassId.equals(AnPos.CLASS_NAME)){
			dbo = new AnPos();
		}
		return dbo;
	}
	private double parseDouble(Object obj){
		if(obj == null){
			return 0;
		}
		double objdb = Double.parseDouble(String.valueOf(obj));
		return objdb;
	}
	
	private int parseInt(Object obj){
		if(obj == null){
			return 0;
		}
		int intdb = Integer.parseInt(String.valueOf(obj));
		return intdb;
	}

	/**
	 * 将要修改点类型属性值放到原有数据中，以便进行修改
	 * @param request
	 * @param dbo
	 */
	private DataObjectList setPointGenericDoAttr(HttpServletRequest request,String bmClassId){
		/*District distict = getDistrictBySystemPara();
		String districtCuid = "";
		if(distict != null){
			districtCuid = distict.getCuid();
		}*/
		DataObjectList pointList = new DataObjectList();
		String parameter = request.getParameter("points");
		JSONArray obj = JSONArray.parseArray(parameter);
		for(int i=0;i<obj.size();i++){
			JSONObject jsonObject = (JSONObject) obj.get(i);
			String labelCN = String.valueOf(jsonObject.get(Manhle.AttrName.labelCn));
			String longitude = String.valueOf(jsonObject.get(Manhle.AttrName.longitude));
			String latitude = String.valueOf(jsonObject.get(Manhle.AttrName.latitude));
			String districtCuid = String.valueOf(jsonObject.get(Manhle.AttrName.relatedDistrictCuid));
			GenericDO dbo = getGenericDbo(bmClassId);
			dbo.setCuid();
			dbo.setAttrValue(Manhle.AttrName.labelCn, labelCN);
			dbo.setAttrValue(Manhle.AttrName.longitude, longitude);
			dbo.setAttrValue(Manhle.AttrName.latitude, latitude);
			if(dbo instanceof Accesspoint){
				dbo.setAttrValue(Accesspoint.AttrName.districtCuid, districtCuid);
			}else{
				dbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, districtCuid);
			}
			if(bmClassId.equals(FiberJointBox.CLASS_NAME)){
			    Object kind = jsonObject.get(FiberJointBox.AttrName.kind);
			    if(null != kind) {
			        dbo.setAttrValue(FiberJointBox.AttrName.kind, kind);
			    }
			}
			dbo.clearUnknowAttrs();
			pointList.add(dbo);
		}
		return pointList;
	}

	private String parseClassNameFromCuid(String cuid) {
		String className = null;
		if (cuid != null && !cuid.equals("")) {
			String cuids[] = cuid.split("-");
			if (cuids.length > 1)
				className = cuids[0];
		}
		return className;
	}

	@RequestMapping(value = "/getSegsAndWire2duct/{systemCuid}/{wireSegCuid}/{indexInRouteBegin}/{indexInRouteEnd}", method = RequestMethod.POST)
	public void getSegsAndWire2duct(@PathVariable String systemCuid,@PathVariable String wireSegCuid,@PathVariable String indexInRouteBegin,@PathVariable String indexInRouteEnd,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			GenericDO lineSystem = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
			
			DataObjectList w2dLists = null;
			//根据关系得到所有敷设信息,可能是多种类型,对不同类型进行不同的处理
			try {
			    w2dLists = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
			        WireToDuctLineBOHelper.ActionName.getDuctLineByWireSegAndRSysCuid, new BoQueryContext(),
			        wireSegCuid, systemCuid,indexInRouteBegin,indexInRouteEnd);
			    loadOrigAndDestPoint(w2dLists, lineSystem,wireSegCuid,indexInRouteBegin,indexInRouteEnd);
			    refreshWireToDuctlineProp(w2dLists);
			} catch (Exception ex) {
				result = NmsUtils.getStringBufferErrJson(ex.getMessage());
				LogHome.getLog().error("获得具体路由数据失败：",ex);
			}
			w2dLists.sort(WireToDuctline.AttrName.indexInRoute, true);
			GenericDO ductSystemDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
			DataObjectList branchList = new DataObjectList();
			DataObjectList segList = new DataObjectList();
			if(systemCuid.startsWith(UpLine.CLASS_NAME) || systemCuid.startsWith(HangWall.CLASS_NAME)){
				ductSystemDbo.setAttrValue("DataChildren", segList);
			}else{
				ductSystemDbo.setAttrValue("DataChildren", branchList);
			}
			
			for(GenericDO wireToDuctLine:w2dLists){
				
				GenericDO origPoint = (GenericDO) wireToDuctLine.getAttrValue(WireToDuctline.AttrName.disPointCuid);
				GenericDO origPointDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), origPoint.getCuid());
				GenericDO destPoint = (GenericDO) wireToDuctLine.getAttrValue(WireToDuctline.AttrName.endPointCuid);
				GenericDO destPointDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), destPoint.getCuid());
				
				String lineSegCuid = DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.lineSegCuid));
				String lineSystemCuid = ductSystemDbo.getCuid();//DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.lineSystemCuid));
				String wireSystemCuid = DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.wireSystemCuid));
				String wsCuid = DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.wireSegCuid));
				String indexInRoute = DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.indexInRoute));
				
				if(!systemCuid.startsWith(UpLine.CLASS_NAME) && !systemCuid.startsWith(HangWall.CLASS_NAME)){
					GenericDO lineBranch = (GenericDO) wireToDuctLine.getAttrValue(WireToDuctline.AttrName.lineBranchCuid);
					GenericDO lineBranchDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineBranch.getCuid());
					String branchRelatedSysCuid = DMHelper.getRelatedCuid(lineBranchDbo.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
					if(branchRelatedSysCuid.equals(lineSystemCuid)){
						if(!branchList.getCuidList().contains(lineBranchDbo.getCuid())){
							branchList.add(lineBranchDbo);
						}
					}
				}

				String direction = DMHelper.getRelatedCuid(wireToDuctLine.getAttrValue(WireToDuctline.AttrName.direction));
				GenericDO lineSegDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineSegCuid);
				segList.add(lineSegDbo);
				String holeCuid = "",holeLabelCn = "",childHoleCuid = "",childHoleLabelCn = "",carringCuid = "",carryingLabelCn = "";
				GenericDO reDbo = setDuctChildHole(wireToDuctLine);
				holeCuid = reDbo.getAttrString("HOLE_CUID")==null?"":reDbo.getAttrString("HOLE_CUID");
				holeLabelCn = reDbo.getAttrString("HOLE_NUM")==null?"":reDbo.getAttrString("HOLE_NUM");
				childHoleCuid = reDbo.getAttrString("CHILD_HOLE_CUID")==null?"":reDbo.getAttrString("CHILD_HOLE_CUID");
				childHoleLabelCn = reDbo.getAttrString("CHILD_HOLE_NUM")==null?"":reDbo.getAttrString("CHILD_HOLE_NUM");
				carringCuid = reDbo.getAttrString("CARRYING_CUID")==null?"":reDbo.getAttrString("CARRYING_CUID");
				carryingLabelCn = reDbo.getAttrString("CARRYING_NUM")==null?"":reDbo.getAttrString("CARRYING_NUM");
				lineSegDbo.setAttrValue(WireToDuctline.AttrName.holeCuid, holeCuid);
				lineSegDbo.setAttrValue(WireToDuctline.AttrName.childHoleCuid, childHoleCuid);
				lineSegDbo.setAttrValue("CARRYING_CUID", carringCuid);
				lineSegDbo.setAttrValue("HOLE_NUM", holeLabelCn);
				lineSegDbo.setAttrValue("CHILD_HOLE_NUM", childHoleLabelCn);
				lineSegDbo.setAttrValue("CARRYING_NUM", carryingLabelCn);
				//bs管线的bug,起止点的正反向、翻转有问题，此处为了展示特殊处理
				//根据方向设置起止点、名称
				if("1".equals(direction))
				{
					lineSegDbo.setAttrValue(DuctSeg.AttrName.origPointCuid,origPointDbo);
					lineSegDbo.setAttrValue(DuctSeg.AttrName.destPointCuid,destPointDbo);
					lineSegDbo.setAttrValue(DuctSeg.AttrName.labelCn, origPointDbo.getAttrValue("LABEL_CN") + "--" + destPointDbo.getAttrValue("LABEL_CN"));
				}
				else
				{
					lineSegDbo.setAttrValue(DuctSeg.AttrName.origPointCuid,destPointDbo);
					lineSegDbo.setAttrValue(DuctSeg.AttrName.destPointCuid,origPointDbo);
					lineSegDbo.setAttrValue(DuctSeg.AttrName.labelCn, destPointDbo.getAttrValue("LABEL_CN") + "--" + origPointDbo.getAttrValue("LABEL_CN"));
				}
				lineSegDbo.setAttrValue(DuctSeg.AttrName.direction,direction);
				lineSegDbo.setAttrValue(WireToDuctline.AttrName.wireSystemCuid, wireSystemCuid);
				lineSegDbo.setAttrValue(WireToDuctline.AttrName.wireSegCuid, wsCuid);
				lineSegDbo.setAttrValue(WireToDuctline.AttrName.indexInRoute, indexInRoute);
			}
			if(segList.size()>0){
				if(!systemCuid.startsWith(UpLine.CLASS_NAME) && !systemCuid.startsWith(HangWall.CLASS_NAME)){
					for(GenericDO branch:branchList){
						DataObjectList ductSegList = new DataObjectList();
						String branchCuid = branch.getCuid();
						branch.setAttrValue("DataChildren", ductSegList);
						for(GenericDO seg:segList){
				    		String relatedBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
				    		if(relatedBranchCuid.equals(branchCuid)){
				    			ductSegList.add(seg);
				    		}
				    	}
						ductSegList.sort(WireToDuctline.AttrName.indexInRoute, true);
					}
				}
			}
			
			result.append("{\"systemList\":[");
			String systemLabelCn = ductSystemDbo.getAttrString(WireSystem.AttrName.labelCn);
			if(ductSystemDbo != null){
				String sysClassName = systemCuid.split("-")[0];
				result.append("{");
				result.append("\"CUID\":").append("\""+systemCuid+"\",");
				result.append("\"SYS_CLASS_NAME\":").append("\""+sysClassName+"\",");
				result.append("\"LABEL_CN\":").append("\""+systemLabelCn+"\"");
				if(sysClassName.equals(UpLine.CLASS_NAME) || sysClassName.equals(HangWall.CLASS_NAME)){
					DataObjectList segs = (DataObjectList) ductSystemDbo.getAttrValue("DataChildren");
					if(segs !=null && segs.size()>0){
						result.append(",\"segList\":[");
						
						if(segs != null && segs.size()>0){
							segs.sort(DuctSeg.AttrName.indexInBranch, true);
							for(int k=0;k<segs.size();k++){
								GenericDO seg = segs.get(k);
								String segCuid = seg.getCuid();
								String segClassName = segCuid.split("-")[0];
								String segLabelCn = seg.getAttrString(WireSeg.AttrName.labelCn);
								
								GenericDO origDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
								GenericDO destDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
								String origPointCuid = origDbo.getCuid();
								String destPointCuid = destDbo.getCuid();
								String origPointName = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
								String destPointName = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
								
								String direction = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.direction));
								
								String lineSystemCuid = systemCuid;//DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
								
//								String ductHoleNum=DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.holeCuid));
//								String ductChildHoleNum=DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.childHoleCuid));
								
								String wireSysemCuid = DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.wireSystemCuid));
								String holeCuid = "",childHoleCuid = "",ductHoleNum = "",childHoleNum = "",carryingCuid = "",carryingNum = "";
								if(segCuid.startsWith(DuctSeg.CLASS_NAME)){
									holeCuid = String.valueOf(seg.getAttrValue(WireToDuctline.AttrName.holeCuid));
									childHoleCuid = String.valueOf(seg.getAttrValue(WireToDuctline.AttrName.childHoleCuid));
									ductHoleNum = String.valueOf(seg.getAttrValue("HOLE_NUM"));
									childHoleNum = String.valueOf(seg.getAttrValue("CHILD_HOLE_NUM"));
								}else if(segCuid.startsWith(PolewaySeg.CLASS_NAME)){
									carryingCuid = String.valueOf(seg.getAttrValue("CARRYING_CUID"));
									carryingNum = String.valueOf(seg.getAttrValue("CARRYING_NUM"));
								}
								String indexInBranch = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.indexInBranch));
								Map<String, String> map = new HashMap<String, String>();
								map.put("SEG_CUID", segCuid);
								map.put("SEG_CLASS_NAME", segClassName);
								map.put("ORIG_POINT_NAME", origPointName);
								map.put("DEST_POINT_NAME", destPointName);
								map.put("HOLE_CUID", holeCuid);
								map.put("HOLE_NUM", ductHoleNum);
								map.put("CHILD_HOLE_CUID", childHoleCuid);
								map.put("CHILD_HOLE_NUM", childHoleNum);
								map.put("CARRYING_CUID", carryingCuid);
								map.put("CARRYING_NUM", carryingNum);
								
								map.put("DIRECTION", direction);
								map.put("LINE_SYSTEM_CUID", lineSystemCuid);
								map.put("LINE_SEG_CUID", segCuid);
								map.put("ORIG_POINT_CUID", origPointCuid);
								map.put("DEST_POINT_CUID", destPointCuid);
								map.put("WIRE_SEG_CUID", wireSegCuid);
								map.put("WIRE_SYSTEM_CUID", wireSysemCuid);
								map.put("SEG_LABEL_CN", segLabelCn);
								
								map.put("INDEX_IN_BRANCH", indexInBranch);
								
								String res = NmsUtils.getJsonByAttrNameAndValue(map);
								result.append(res);

								if(k < segs.size()-1)
									result.append(",");
							}
						}
						result.append("]");
					}
				}else{
					DataObjectList branchs = (DataObjectList) ductSystemDbo.getAttrValue("DataChildren");
					if(branchs != null && branchs.size()>0){
						result.append(",\"branchList\":[");
						for(int i=0;i<branchs.size();i++){
							GenericDO branch = branchs.get(i);
							String branchCuid = branch.getCuid();
							String branchClassName = branchCuid.split("-")[0];
							String branchName = branch.getAttrString(WireSystem.AttrName.labelCn);
							result.append("{");
							result.append("\"BRANCH_CUID\":").append("\""+branchCuid+"\",");
							result.append("\"BRANCH_CLASS_NAME\":").append("\""+branchClassName+"\",");
							result.append("\"BRANCH_LABEL_CN\":").append("\""+branchName+"\"");
							DataObjectList segs = (DataObjectList) branch.getAttrValue("DataChildren");
							if(segs !=null && segs.size()>0){
								result.append(",\"segList\":[");
								
								if(segs != null && segs.size()>0){
									segs.sort(DuctSeg.AttrName.indexInBranch, true);
									for(int k=0;k<segs.size();k++){
										GenericDO seg = segs.get(k);
										String segCuid = seg.getCuid();
										String segClassName = segCuid.split("-")[0];
										String segLabelCn = seg.getAttrString(WireSeg.AttrName.labelCn);
										
										GenericDO origDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
										GenericDO destDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
										String origPointCuid = origDbo.getCuid();
										String destPointCuid = destDbo.getCuid();
										String origPointName = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
										String destPointName = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
										
										String direction = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.direction));
										
										String lineSystemCuid = systemCuid;//DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
										String lineBranchCuid = branchCuid;//DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
										
										String wireSysemCuid = DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.wireSystemCuid));
										String holeCuid = "",childHoleCuid = "",ductHoleNum = "",childHoleNum = "",carryingCuid = "",carryingNum = "";
										if(segCuid.startsWith(DuctSeg.CLASS_NAME)){
											holeCuid = String.valueOf(seg.getAttrValue(WireToDuctline.AttrName.holeCuid));
											childHoleCuid = String.valueOf(seg.getAttrValue(WireToDuctline.AttrName.childHoleCuid));
											ductHoleNum = String.valueOf(seg.getAttrValue("HOLE_NUM"));
											childHoleNum = String.valueOf(seg.getAttrValue("CHILD_HOLE_NUM"));
										}else if(segCuid.startsWith(PolewaySeg.CLASS_NAME)){
											carryingCuid = String.valueOf(seg.getAttrValue("CARRYING_CUID"));
											carryingNum = String.valueOf(seg.getAttrValue("CARRYING_NUM"));
										}
										String indexInBranch = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.indexInBranch));
										Map<String, String> map = new HashMap<String, String>();
										map.put("SEG_CUID", segCuid);
										map.put("SEG_CLASS_NAME", segClassName);
										map.put("ORIG_POINT_NAME", origPointName);
										map.put("DEST_POINT_NAME", destPointName);
										map.put("HOLE_CUID", holeCuid);
										map.put("HOLE_NUM", ductHoleNum);
										map.put("CHILD_HOLE_CUID", childHoleCuid);
										map.put("CHILD_HOLE_NUM", childHoleNum);
										map.put("CARRYING_CUID", carryingCuid);
										map.put("CARRYING_NUM", carryingNum);
										map.put("DIRECTION", direction);
										
										map.put("LINE_SYSTEM_CUID", lineSystemCuid);
										map.put("LINE_BRANCH_CUID", lineBranchCuid);
										map.put("LINE_SEG_CUID", segCuid);
										map.put("ORIG_POINT_CUID", origPointCuid);
										map.put("DEST_POINT_CUID", destPointCuid);
										map.put("WIRE_SEG_CUID", wireSegCuid);
										map.put("WIRE_SYSTEM_CUID", wireSysemCuid);
										map.put("SEG_LABEL_CN", segLabelCn);
										
										map.put("INDEX_IN_BRANCH", indexInBranch);
										
										String res = NmsUtils.getJsonByAttrNameAndValue(map);
										result.append(res);
										
										if(k < segs.size()-1)
											result.append(",");
									}
								}
								result.append("]");
							}
							result.append("}");
							if(i < branchs.size()-1)
								result.append(",");
						}
						result.append("]");
					}
				}
				result.append("}");
			}
			result.append("]}");
		} catch (UserException e) {
			result = NmsUtils.getStringBufferErrJson(e.getMessage());
			LogHome.getLog().error("获得具体路由数据失败：",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	/**
	 * 设置敷设信息的管孔子孔信息
	 * @param wireToDuctLine
	 */
	private GenericDO setDuctChildHole(GenericDO dbo){
		GenericDO returnDbo = new GenericDO();
		String holeCuid = "",holeLabelCn = "",childHoleCuid = "",childHoleLabelCn = "",carryingCuid = "",carryingLabelCn = "";
		Object ductHoleDbo = dbo.getAttrValue(WireToDuctline.AttrName.holeCuid);
		Object childHoleDbo = dbo.getAttrValue(WireToDuctline.AttrName.childHoleCuid);
//		Object carryingDbo = dbo.getAttrValue("CARRYING_CUID");
		if(ductHoleDbo != null){
			GenericDO gdo = null;
			if(ductHoleDbo instanceof String && !ductHoleDbo.toString().trim().equals("")){
				gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), String.valueOf(ductHoleDbo));
			}else if(ductHoleDbo instanceof GenericDO){
				gdo = (GenericDO) ductHoleDbo;
			}
			if(gdo != null){
				holeCuid = gdo.getCuid();
				if (gdo instanceof DuctHole) {
		            if (new Long(DuctEnum.DTRECTION_TYPE._rdtrection).equals(gdo.getAttrValue(WireToDuctline.AttrName.direction))){
		            	holeLabelCn = gdo.getAttrString(DuctHole.AttrName.destNo);
			            returnDbo.setAttrValue("HOLE_NUM", holeLabelCn);
		            }else{
		            	holeLabelCn = gdo.getAttrString(DuctHole.AttrName.origNo);
			            returnDbo.setAttrValue("HOLE_NUM", holeLabelCn);
		            }
		            returnDbo.setAttrValue("HOLE_CUID", holeCuid);
		        }else if (gdo instanceof CarryingCable) {
		        	returnDbo.setAttrValue("CARRYING_CUID", holeCuid);
		        	holeLabelCn = String.valueOf(gdo.getAttrValue(CarryingCable.AttrName.innerNo));
		            returnDbo.setAttrValue("CARRYING_NUM", holeLabelCn);
		        }
				
			}
		}
		if(childHoleDbo != null){
			GenericDO gdo = null;
			if(childHoleDbo instanceof String && !childHoleDbo.toString().trim().equals("")){
				gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), String.valueOf(childHoleDbo));
			}else if(childHoleDbo instanceof GenericDO){
				gdo = (GenericDO) childHoleDbo;
			}
			if(gdo != null){
				if(gdo instanceof DuctChildHole){
					childHoleCuid = gdo.getCuid();
					childHoleLabelCn = ((DuctChildHole) gdo).getDuctChildHoldNum();//((DuctChildHole) gdo).getLabelCn();
					returnDbo.setAttrValue("CHILD_HOLE_CUID", childHoleCuid);
					returnDbo.setAttrValue("CHILD_HOLE_NUM", childHoleLabelCn);
				}
			}
		}
//		if(carryingDbo != null){
//			GenericDO gdo = null;
//			if(carryingDbo instanceof String && !carryingDbo.toString().trim().equals("")){
//				gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), String.valueOf(carryingDbo));
//			}else if(carryingDbo instanceof GenericDO){
//				gdo = (GenericDO) carryingDbo;
//			}
//			if(gdo != null){
//				if(gdo instanceof CarryingCable){
//					carryingCuid = gdo.getCuid();
//					carryingLabelCn = String.valueOf(((CarryingCable) gdo).getInnerNo());
//					returnDbo.setAttrValue("CARRYING_CUID", carryingCuid);
//					returnDbo.setAttrValue("CARRYING_NUM", carryingLabelCn);
//				}
//			}
//		}
		return returnDbo;
	}
	
    private void loadOrigAndDestPoint(DataObjectList segs, GenericDO lineSystem,String segCuid,String indexInRouteBegin,String indexInRouteEnd) {
        try {
            DataObjectList rpointsList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getPointsByWireToDuctLine, new BoActionContext(),
                segCuid, lineSystem.getCuid(),indexInRouteBegin,indexInRouteEnd);
            Map<String, GenericDO> pointMap = new HashMap<String, GenericDO>();

            TopoHelper.putListToMap(rpointsList, pointMap);
            for (GenericDO seg : segs) {
                String cuid = DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.disPointCuid));
                GenericDO origPoint = pointMap.get(cuid);
                if (origPoint != null) {
                    seg.setAttrValue(WireToDuctline.AttrName.disPointCuid, origPoint);
                }
                cuid = DMHelper.getRelatedCuid(seg.getAttrValue(WireToDuctline.AttrName.endPointCuid));
                GenericDO destPoint = pointMap.get(cuid);
                if (destPoint != null) {
                    seg.setAttrValue(WireToDuctline.AttrName.endPointCuid, destPoint);
                }
                if (origPoint != null && destPoint != null) {
                    String name = origPoint.getAttrString(GenericDO.AttrName.labelCn) + "--"
                                  + destPoint.getAttrString(GenericDO.AttrName.labelCn);
                    seg.setAttrValue(GenericDO.AttrName.labelCn, name);
                } else {
                    seg.setAttrValue(GenericDO.AttrName.labelCn, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void refreshWireToDuctlineProp(DataObjectList w2dLists) {
        for (GenericDO gdo : w2dLists) {
            refreshWireToDuctlineProp(gdo);
        }
    }

    public static void refreshWireToDuctlineProp(GenericDO gdo) {
        Object origPoint = null;
        Object destPoint = null;
        if (new Long(DuctEnum.DTRECTION_TYPE._rdtrection).equals(gdo.getAttrValue(WireToDuctline.AttrName.direction))) {
            origPoint = gdo.getAttrValue(WireToDuctline.AttrName.endPointCuid);
            destPoint = gdo.getAttrValue(WireToDuctline.AttrName.disPointCuid);
        }
        else {
            origPoint = gdo.getAttrValue(WireToDuctline.AttrName.disPointCuid);
            destPoint = gdo.getAttrValue(WireToDuctline.AttrName.endPointCuid);
        }

        gdo.setAttrValue(WireSeg.AttrName.origPointCuid, origPoint);
        gdo.setAttrValue(WireSeg.AttrName.destPointCuid, destPoint);
        if (origPoint instanceof GenericDO && destPoint instanceof GenericDO) {
            gdo.setAttrValue(GenericDO.AttrName.labelCn, ((GenericDO) origPoint)
                    .getAttrValue(GenericDO.AttrName.labelCn)
                    + "--" + ((GenericDO) destPoint).getAttrValue(GenericDO.AttrName.labelCn));
        }

//        Object o = gdo.getAttrValue(WireToDuctline.AttrName.holeCuid);
//        if (o instanceof DuctHole) {
//            if (new Long(DuctEnum.DTRECTION_TYPE._rdtrection).equals(gdo.getAttrValue(WireToDuctline.AttrName.direction)))
//                ((DuctHole) o).setLabelCn(((DuctHole) o).getDestNo());
//            else
//                ((DuctHole) o).setLabelCn(((DuctHole) o).getOrigNo());
//            gdo.setAttrValue("HOLENO", ((DuctHole) o));
//        }
//        else if (o instanceof CarryingCable) {
//            gdo.setAttrValue("INNERNO", o);
//        }
//        o = gdo.getAttrValue(WireToDuctline.AttrName.childHoleCuid);
//        if (o instanceof DuctChildHole) {
//            gdo.setAttrValue("CHILDHOLENO", o);
//        }
    }
    
	@RequestMapping(value = "/getWireSegsRoute/{wireSegCuid}", method = RequestMethod.POST)
	public void getWireSegsRoute(@PathVariable String wireSegCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			
			DataObjectList systems = new DataObjectList();
//			GenericDO wireSegDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
//			String relatedSystemCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
			systems = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
			            WireToDuctLineBOHelper.ActionName.getLineSystemAndIndexByWireSegCuid, new BoQueryContext(),
			            wireSegCuid);
			if(wireSegCuid.startsWith("TEMP")){
				String oldSystemCuid = "";
				String sql = "select LINE_SYSTEM_CUID,INDEX_IN_ROUTE from TEMP_WIRE_TO_DUCTLINE where WIRE_SEG_CUID = '" +wireSegCuid+ "' order by INDEX_IN_ROUTE";
				Class[] cla = new Class[]{String.class,String.class};
				DataObjectList lineCuids = getDuctManagerBO().getDatasBySql(sql, cla);
				GenericDO resDbo = null;
				for(GenericDO gdo : lineCuids){
					String syscuid = gdo.getAttrString("1");
					int sysIndexInRoute = Integer.parseInt(gdo.getAttrString("2"));
					if (!oldSystemCuid.equals(syscuid.trim()))
					    resDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), syscuid);
					resDbo.setAttrValue("indexInRouteEnd", sysIndexInRoute);
					if (!oldSystemCuid.equals(syscuid.trim())) {
                        if (!oldSystemCuid.equals(syscuid.trim())) {
                            resDbo.setAttrValue("indexInRouteBegin", sysIndexInRoute);
                            systems.add(resDbo);
                        }
                    }
                    oldSystemCuid = syscuid;
                }
			}
			result.append("{\"systemList\":[");

			for(int i=0;i<systems.size();i++){
				GenericDO sysDbo = systems.get(i);
				String systemCuid = sysDbo.getCuid();
				String systemName = sysDbo.getAttrString(WireSystem.AttrName.labelCn);
				String indexInRouteBegin = String.valueOf(sysDbo.getAttrValue("indexInRouteBegin"));
				String indexInRouteEnd = String.valueOf(sysDbo.getAttrValue("indexInRouteEnd"));
				String systemClassName = systemCuid.split("-")[0];
				result.append("{");
				result.append("\"CUID\":").append("\""+systemCuid+"\",");
				result.append("\"CLASS_NAME\":").append("\""+systemClassName+"\",");
				result.append("\"indexInRouteBegin\":").append("\""+indexInRouteBegin+"\",");
				result.append("\"indexInRouteEnd\":").append("\""+indexInRouteEnd+"\",");
				result.append("\"LABEL_CN\":").append("\""+systemName+"\"");
				
				result.append("}");
				if(i < systems.size()-1)
					result.append(",");
			}
			result.append("]}");
		
		} catch (UserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	/**
	 * 修改光缆段具体路由
	 * @param systemCuid 选择的非光缆管线系统cuid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/getWireSegRoute/{systemCuid}/{wireSegCuid}", method = RequestMethod.POST)
	public void getWireSegRoute(@PathVariable String systemCuid,@PathVariable String wireSegCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
		    GenericDO wireSeg = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
		    String wsCuid = wireSeg.getCuid();
		    String wireSysemCuid = DMHelper.getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
		    
		    GenericDO systemDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
			String systemClassName = parseClassNameFromCuid(systemCuid);
            DataObjectList branchs = getBranchsBySystemCuid(systemCuid);
            Map sesAndPointsMap = getSegsAndPointsBySystemCuid(systemCuid);
            DataObjectList segs = (DataObjectList) sesAndPointsMap.get("segsList");
            DataObjectList points = (DataObjectList) sesAndPointsMap.get("pointsList");
            
            DMHelper.setSegsPoints(segs, points);
	        DMHelper.setSegsPoints(branchs, points);
	        segs.sort(DuctSeg.AttrName.indexInBranch, true);
	        
	        result.append("{\"systemList\":[");
	        String systemLabelCn = systemDbo.getAttrString(WireSystem.AttrName.labelCn);
	        if(systemDbo != null){
	        	String sysClassName = systemCuid.split("-")[0];
	        	result.append("{");
				result.append("\"CUID\":").append("\""+systemCuid+"\",");
				result.append("\"SYS_CLASS_NAME\":").append("\""+sysClassName+"\",");
				result.append("\"LABEL_CN\":").append("\""+systemLabelCn+"\"");
				
	        	if(sysClassName.equals(UpLine.CLASS_NAME) || sysClassName.equals(HangWall.CLASS_NAME)){
					if(segs !=null && segs.size()>0){
						result.append(",\"segList\":[");

						for(int k=0;k<segs.size();k++){
							GenericDO seg = segs.get(k);
							String segCuid = seg.getCuid();
							String segClassName = segCuid.split("-")[0];
							String segLabelCn = seg.getAttrString(WireSeg.AttrName.labelCn);
							GenericDO origDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
							GenericDO destDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
							String origPointName = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
							String destPointName = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
							String origPointCuid= origDbo.getCuid();
							String destPointCuid = destDbo.getCuid();
							String direction = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.direction));
							String lineSystemCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
							String lineBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
							//管孔子孔现在在此方法中暂时无用
							String holeCuid = "";
							String ductHoleNum = "";
							String childHoleCuid = "";
							String ductChildHoleNum = "";
							String carryingNum = "";
							
							Map<String,String> map = new HashMap<String,String>();
							map.put("SEG_CUID", segCuid);
							map.put("SEG_CLASS_NAME", segClassName);
							map.put("ORIG_POINT_NAME", origPointName);
							map.put("DEST_POINT_NAME", destPointName);
							map.put("HOLE_CUID", holeCuid);
							map.put("HOLE_NUM", ductHoleNum);
							map.put("CHILD_HOLE_CUID", childHoleCuid);
							map.put("CHILD_HOLE_NUM", ductChildHoleNum);
							map.put("CARRYING_NUM", carryingNum);
							map.put("DIRECTION", direction);
							map.put("LINE_SYSTEM_CUID", lineSystemCuid);
							map.put("LINE_BRANCH_CUID", lineBranchCuid);
							map.put("LINE_SEG_CUID", segCuid);
							map.put("ORIG_POINT_CUID", origPointCuid);
							map.put("DEST_POINT_CUID", destPointCuid);
							map.put("WIRE_SEG_CUID", wsCuid);
							map.put("WIRE_SYSTEM_CUID", wireSysemCuid);
							map.put("SEG_LABEL_CN", segLabelCn);
							
							
							String res = NmsUtils.getJsonByAttrNameAndValue(map);
							result.append(res);
							if(k < segs.size()-1)
								result.append(",");
						}
						result.append("]");
					}
	        	}
				
				if(branchs != null && branchs.size()>0){
					result.append(",\"branchList\":[");
					for(int i=0;i<branchs.size();i++){
						GenericDO branch = branchs.get(i);
						String branchCuid = branch.getCuid();
						String branchClassName = branchCuid.split("-")[0];
						String branchName = branch.getAttrString(WireSystem.AttrName.labelCn);
						result.append("{");
						result.append("\"BRANCH_CUID\":").append("\""+branchCuid+"\",");
						result.append("\"BRANCH_CLASS_NAME\":").append("\""+branchClassName+"\",");
						result.append("\"BRANCH_LABEL_CN\":").append("\""+branchName+"\"");
						if(segs !=null && segs.size()>0){
							result.append(",\"segList\":[");
							DataObjectList segList = new DataObjectList();
							for(int j=0;j<segs.size();j++){
								GenericDO seg = segs.get(j);
								String relatedBranchCuid = DMHelperX.getRelatedCuid(seg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
								if(relatedBranchCuid.equals(branchCuid)){
									segList.add(seg);
								}
							}
							if(segList != null && segList.size()>0){
								for(int k=0;k<segList.size();k++){
									GenericDO seg = segList.get(k);
									String segCuid = seg.getCuid();
									String segClassName = segCuid.split("-")[0];
									String segLabelCn = seg.getAttrString(WireSeg.AttrName.labelCn);
									GenericDO origDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
									GenericDO destDbo = (GenericDO) seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
									String origPointName = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
									String destPointName = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
									String origPointCuid= origDbo.getCuid();
									String destPointCuid = destDbo.getCuid();
									String direction = String.valueOf(seg.getAttrValue(DuctSeg.AttrName.direction));
									String lineSystemCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
									String lineBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
									String holeCuid = "";
									String ductHoleNum="";
									String childHoleCuid = "";
									String ductChildHoleNum="";
									String carryingNum="";
									Map<String,String> map = new HashMap<String,String>();
									map.put("SEG_CUID", segCuid);
									map.put("SEG_CLASS_NAME", segClassName);
									map.put("ORIG_POINT_NAME", origPointName);
									map.put("DEST_POINT_NAME", destPointName);
									map.put("HOLE_CUID", holeCuid);
									map.put("HOLE_NUM", ductHoleNum);
									map.put("CHILD_HOLE_CUID", childHoleCuid);
									map.put("CHILD_HOLE_NUM", ductChildHoleNum);
									map.put("CARRYING_NUM", carryingNum);
									map.put("DIRECTION", direction);
									map.put("LINE_SYSTEM_CUID", lineSystemCuid);
									map.put("LINE_BRANCH_CUID", lineBranchCuid);
									map.put("LINE_SEG_CUID", segCuid);
									map.put("ORIG_POINT_CUID", origPointCuid);
									map.put("DEST_POINT_CUID", destPointCuid);
									map.put("WIRE_SEG_CUID", wsCuid);
									map.put("WIRE_SYSTEM_CUID", wireSysemCuid);
									map.put("SEG_LABEL_CN", segLabelCn);
									
									String res = NmsUtils.getJsonByAttrNameAndValue(map);
									result.append(res);
									
									if(k < segList.size()-1)
										result.append(",");
								}
							}
							result.append("]");
						}
						result.append("}");
						if(i < branchs.size()-1)
							result.append(",");
					}
					result.append("]");
				}
				result.append("}");
	        }
	        result.append("]}");
//            refreshSystemTree(box, branchs, segs, points, systemNode);

		} catch (Throwable e) {
			result = NmsUtils.getStringBufferErrJson(e.getMessage());
			LogHome.getLog().error("获得具体路由数据失败：",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	@RequestMapping(value = "/modifyGlayWireTuDuctLine/{wireSegCuid}", method = RequestMethod.POST)
	public void modifyGlayWireTuDuctLine(@PathVariable String wireSegCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			String parameter = request.getParameter("wireductlinelist");
			JSONObject jsonobj = (JSONObject) JSONArray.parse(parameter);
			JSONArray segsArray = (JSONArray) jsonobj.get("segList");
			DataObjectList wtds = new DataObjectList();
			GenericDO wireSegDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
			String wireSystemCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
			
			DataObjectList systems = new DataObjectList();
			Map<String,List<JSONObject>> systemMap = new HashMap<String,List<JSONObject>>();
			for(int i=0;i<segsArray.size();i++){
				JSONObject sysObject = (JSONObject) segsArray.get(i);
				String cuid = String.valueOf(sysObject.get("CUID"));
				String className = cuid.split("-")[0];
				if(DMHelper.isLineSystemClassName(className)){
					GenericDO system = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
					String indexInRouteBegin = String.valueOf(sysObject.get("indexInRouteBegin"));
					String indexInRouteEnd = String.valueOf(sysObject.get("indexInRouteEnd"));
					system.setAttrValue("indexInRouteBegin", Long.parseLong(indexInRouteBegin));
					system.setAttrValue("indexInRouteEnd", Long.parseLong(indexInRouteEnd));
					if(!systems.getCuidList().contains(cuid)){
						systems.add(system);
					}
				}else{
					String relatedSystemCuid = String.valueOf(sysObject.get("LINE_SYSTEM_CUID"));
					List<JSONObject> list = systemMap.get(relatedSystemCuid);
					if (list == null) {
						list = new ArrayList<JSONObject>();
						systemMap.put(relatedSystemCuid, list);
					}
					list.add(sysObject);
				}
			}
			
			for(String relatedSystemCuid:systemMap.keySet()){
				List<JSONObject> list = systemMap.get(relatedSystemCuid);
				
				if(list != null && list.size()>0){
					GenericDO system = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedSystemCuid);
					if(systems.getCuidList().contains(relatedSystemCuid)){
						//如果列表中选择的分支所属系统和当前选择的是一个系统，需要将原来的敷设信息先加上
						system = systems.getObjectByCuid(relatedSystemCuid).get(0);
						String indexInRouteBegin = String.valueOf(system.getAttrLong("indexInRouteBegin"));
						String indexInRouteEnd = String.valueOf(system.getAttrLong("indexInRouteEnd"));
						DataObjectList w2dLists = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
						        WireToDuctLineBOHelper.ActionName.getDuctLineByWireSegAndRSysCuid, new BoQueryContext(),
						        wireSegCuid, relatedSystemCuid,indexInRouteBegin,indexInRouteEnd);
						w2dLists.sort(WireToDuctline.AttrName.indexInRoute, true);
						TopoHelper.getChildren(system).addAll(w2dLists);
					}else{
						systems.add(system);
					}
					wtds = getWire2DuctLines(list,relatedSystemCuid,wireSegCuid,wireSystemCuid,relatedSystemCuid);
					TopoHelper.getChildren(system).addAll(wtds);
				}
			}
			
			BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.modifyWireToDuctLines,
			         new BoActionContext(), wireSegDbo, systems);
			getWireSegBO().modifyLayOrDeleteRelationWireSeg(new BoActionContext(), (WireSeg) wireSegDbo);
        	result.append("{\"success\":\"光缆修改完成!\"}");
//			syschWireSegDisplayRoute((WireSeg) wireSegDbo,true,result);
		}catch (Exception e) {
			result = NmsUtils.getStringBufferErrJson("光缆修改失败!");
			LogHome.getLog().info("光缆修改失败：",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	/**
	 * 修改光缆段具体路由
	 * @param wireSegCuid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/modifyWireTuDuctLine/{wireSegCuid}", method = RequestMethod.POST)
	public void modifyWireTuDuctLine(@PathVariable String wireSegCuid,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		StringBuffer result = new StringBuffer();
		try {
			GenericDO wireSeg = getDuctManagerBO().getObjByCuid(
					new BoActionContext(), wireSegCuid);
			DataObjectList systemList = new DataObjectList();

			String parameter = request.getParameter("wireductlinelist");
			JSONObject jsonobj = (JSONObject) JSONArray.parse(parameter);
			JSONArray sysArray = (JSONArray) jsonobj.get("systemList");
			if (sysArray.size() > 0){
				for (int i = 0; i < sysArray.size(); i++) {
					JSONObject sysObject = (JSONObject) sysArray.get(i);
					String scuid = String.valueOf(sysObject
							.get(Manhle.AttrName.cuid));
					GenericDO ductSystemDbo = getDuctManagerBO().getObjByCuid(
							new BoActionContext(), scuid);
					systemList.add(ductSystemDbo);
					String indexInRouteBegin = null;
					if (sysObject.get("indexInRouteBegin") != null
							&& !sysObject.get("indexInRouteBegin").equals(
									"null")) {
						indexInRouteBegin = String.valueOf(sysObject
								.get("indexInRouteBegin"));
						ductSystemDbo.setAttrValue("indexInRouteBegin",
								Long.parseLong(indexInRouteBegin));
					}
					String indexInRouteEnd = null;
					if (sysObject.get("indexInRouteEnd") != null
							&& !sysObject.get("indexInRouteEnd").equals("null")) {
						indexInRouteEnd = String.valueOf(sysObject
								.get("indexInRouteEnd"));
						ductSystemDbo.setAttrValue("indexInRouteEnd",
								Long.parseLong(indexInRouteEnd));
					}
					String sysClassName = scuid.split("-")[0];
					if (sysClassName.equals(UpLine.CLASS_NAME)
							|| sysClassName.equals(HangWall.CLASS_NAME)) {
						DataObjectList wtdAllclones = new DataObjectList();
						JSONArray segsArray = (JSONArray) sysObject
								.get("segList");
						DataObjectList wtdclones = new DataObjectList();
						if (null != segsArray) {
							for (int p = 0; p < segsArray.size(); p++) {
								JSONObject jsonObject = (JSONObject) segsArray
										.get(p);
								String labelCN = String.valueOf(jsonObject
										.get(Manhle.AttrName.labelCn));
								String ductLineCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.ductlineCuid));
								String lineSegCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.lineSegCuid));
								String lineSystemCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.lineSystemCuid));
								// String lineBranchCuid =
								// String.valueOf(jsonObject.get(WireToDuctline.AttrName.lineBranchCuid));
								String wsCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.wireSegCuid));
								String wireSystemCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.wireSystemCuid));
								String direction = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.direction));
								String disPointCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.disPointCuid));
								String endPointCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.endPointCuid));
								String holeCuid = String.valueOf(jsonObject
										.get(WireToDuctline.AttrName.holeCuid));
								String childHoleCuid = String
										.valueOf(jsonObject
												.get(WireToDuctline.AttrName.childHoleCuid));
								String carryingCuid = String.valueOf(jsonObject
										.get("CARRYING_CUID"));
								if (lineSegCuid.startsWith(DuctSeg.CLASS_NAME)) {
									if (holeCuid != null
											&& !holeCuid.trim().equals("")) {
										ductLineCuid = holeCuid;
									}
									if (childHoleCuid != null
											&& !childHoleCuid.trim().equals("")) {
										ductLineCuid = childHoleCuid;
									}
								} else if (lineSegCuid
										.startsWith(PolewaySeg.CLASS_NAME)) {
									if (carryingCuid != null
											&& !carryingCuid.trim().equals("")) {
										ductLineCuid = carryingCuid;
									}
								}
								
								WireToDuctline wireToDuctline = new WireToDuctline();
								wireToDuctline.setCuid();
								wireToDuctline.setLabelCn(labelCN);
								wireToDuctline.setDuctlineCuid(ductLineCuid);
								wireToDuctline.setLineSegCuid(lineSegCuid);
								// wireToDuctline.setLineBranchCuid(lineBranchCuid);
								wireToDuctline
								.setLineSystemCuid(lineSystemCuid);
								wireToDuctline.setWireSegCuid(wsCuid);
								wireToDuctline
								.setWireSystemCuid(wireSystemCuid);
								wireToDuctline.setDirection(Long
										.valueOf(direction));
								wireToDuctline.setDisPointCuid(disPointCuid);
								wireToDuctline.setEndPointCuid(endPointCuid);
								// 是吊线的情况下，需要将吊线id设置到holecuid
								if (ductLineCuid
										.startsWith(CarryingCable.CLASS_NAME)) {
									wireToDuctline.setHoleCuid(carryingCuid);
								} else {
									wireToDuctline.setHoleCuid(holeCuid);
								}
								wireToDuctline.setChildHoleCuid(childHoleCuid);
								wtdclones.add(wireToDuctline);
							}
						}
						wtdAllclones.addAll(wtdclones);
						ductSystemDbo
						.setAttrValue("DataChildren", wtdAllclones);
					} else {
						JSONArray branchsArray = (JSONArray) sysObject
								.get("branchList");
						if (branchsArray != null && branchsArray.size() > 0) {
							DataObjectList wtdAllclones = new DataObjectList();
							for (int k = 0; k < branchsArray.size(); k++) {
								JSONObject branchObject = (JSONObject) branchsArray
										.get(k);
								JSONArray segsArray = (JSONArray) branchObject
										.get("segList");
								DataObjectList wtdclones = new DataObjectList();
								if (null != segsArray) {
									for (int p = 0; p < segsArray.size(); p++) {
										JSONObject jsonObject = (JSONObject) segsArray
												.get(p);
										String labelCN = String
												.valueOf(jsonObject
														.get(Manhle.AttrName.labelCn));
										String ductLineCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.ductlineCuid));
										String lineSegCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.lineSegCuid));
										String lineSystemCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.lineSystemCuid));
										String lineBranchCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.lineBranchCuid));
										String wsCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.wireSegCuid));
										String wireSystemCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.wireSystemCuid));
										String direction = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.direction));
										String disPointCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.disPointCuid));
										String endPointCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.endPointCuid));
										String holeCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.holeCuid));
										String childHoleCuid = String
												.valueOf(jsonObject
														.get(WireToDuctline.AttrName.childHoleCuid));
										String carryingCuid = String
												.valueOf(jsonObject
														.get("CARRYING_CUID"));
										if (lineSegCuid
												.startsWith(DuctSeg.CLASS_NAME)) {
											if (holeCuid != null
													&& !holeCuid.trim().equals(
															"")) {
												ductLineCuid = holeCuid;
											}
											if (childHoleCuid != null
													&& !childHoleCuid.trim()
													.equals("")) {
												ductLineCuid = childHoleCuid;
											}
										} else if (lineSegCuid
												.startsWith(PolewaySeg.CLASS_NAME)) {
											if (carryingCuid != null
													&& !carryingCuid.trim()
													.equals("")) {
												ductLineCuid = carryingCuid;
											}
										}
										WireToDuctline wireToDuctline = new WireToDuctline();
										wireToDuctline.setCuid();
										wireToDuctline.setLabelCn(labelCN);
										wireToDuctline
										.setDuctlineCuid(ductLineCuid);
										wireToDuctline
										.setLineSegCuid(lineSegCuid);
										wireToDuctline
										.setLineBranchCuid(lineBranchCuid);
										wireToDuctline
										.setLineSystemCuid(lineSystemCuid);
										wireToDuctline.setWireSegCuid(wsCuid);
										wireToDuctline
										.setWireSystemCuid(wireSystemCuid);
										wireToDuctline.setDirection(Long
												.valueOf(direction));
										//bs管线的bug,起止点的正反向、翻转有问题，此处为了展示特殊处理
										//以管道段的起止点作为具体路由的起止，不能随意切换
										String sql = " select " + DuctSeg.AttrName.origPointCuid + "," + DuctSeg.AttrName.destPointCuid
												+ " from " + DuctSeg.CLASS_NAME + " where cuid='" + ductLineCuid + "' union "
												+ " select " + PolewaySeg.AttrName.origPointCuid + "," + PolewaySeg.AttrName.destPointCuid
												+ " from " + PolewaySeg.CLASS_NAME + " where cuid='" + ductLineCuid + "' union "
												+ " select " + StonewaySeg.AttrName.origPointCuid + "," + StonewaySeg.AttrName.destPointCuid
												+ " from " + StonewaySeg.CLASS_NAME + " where cuid='" + ductLineCuid + "' union "
												+ " select " + UpLineSeg.AttrName.origPointCuid + "," + UpLineSeg.AttrName.destPointCuid
												+ " from " + UpLineSeg.CLASS_NAME + " where cuid='" + ductLineCuid + "' union "
												+ " select " + HangWallSeg.AttrName.origPointCuid + "," + HangWallSeg.AttrName.destPointCuid
												+ " from " + HangWallSeg.CLASS_NAME + " where cuid='" + ductLineCuid + "'";
										Class[] classArr = new Class[]{String.class, String.class};
										DataObjectList oldDataList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getDatasBySql", sql, classArr);
										if(oldDataList != null && oldDataList.size() > 0)
										{
											for(GenericDO oldData : oldDataList)
											{
												wireToDuctline.setDisPointCuid(oldData.getAttrString("1"));
												wireToDuctline.setEndPointCuid(oldData.getAttrString("2"));
											}
										}
										else
										{
											wireToDuctline.setDisPointCuid(disPointCuid);
											wireToDuctline.setEndPointCuid(endPointCuid);
										}
										// 是吊线的情况下，需要将吊线id设置到holecuid
										if (ductLineCuid
												.startsWith(CarryingCable.CLASS_NAME)) {
											wireToDuctline
											.setHoleCuid(carryingCuid);
										} else {
											wireToDuctline
											.setHoleCuid(holeCuid);
										}
										wireToDuctline
										.setChildHoleCuid(childHoleCuid);
										wtdclones.add(wireToDuctline);
									}
								}
								wtdAllclones.addAll(wtdclones);
							}
							ductSystemDbo.setAttrValue("DataChildren",
									wtdAllclones);
						}
					}
					
				}
			}

			BoCmdFactory.getInstance().execBoCmd(
					DuctManagerBOHelper.ActionName.modifyWireToDuctLines,
					new BoActionContext(), wireSeg, systemList);

			// syschWireSegDisplayRoute((WireSeg) wireSeg,true,result);
			getWireSegBO().modifyLayOrDeleteRelationWireSeg(
					new BoActionContext(), (WireSeg) wireSeg);
			result.append("{\"success\":\"光缆修改完成!\"}");
		} catch (Exception e) {
			result = NmsUtils.getStringBufferErrJson("光缆修改失败!");
			LogHome.getLog().info("光缆修改失败：", e);
		} finally {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	 public DataObjectList getBranchsBySystemCuid(String systemCuid) throws Exception {
	        return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
	                DuctManagerBOHelper.ActionName.getBranchsBySystemCuid, new BoActionContext(), systemCuid);
	 }
	
	 public Map getSegsAndPointsBySystemCuid(String systemCuid) throws Exception {
	        return (Map) BoCmdFactory.getInstance().execBoCmd(
	            DuctManagerBOHelper.ActionName.getSegsAndPointsBySystemCuid, new BoActionContext(), systemCuid);
	}
	 
	/**
	 * 拆除承载
	 * @param cuid 可以是光缆段的cuid，也可以是光缆系统 的cuid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/doDeleteLayedRelation/{cuid}", method = RequestMethod.POST)
	public void doDeleteLayedRelation(@PathVariable String cuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLayedRelation, new BoActionContext(), cuid);
			GenericDO dbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			if(dbo instanceof WireSeg){
				syschWireSegDisplayRoute((WireSeg) dbo,false,result);
				if(!(result.indexOf("success") > 0)){
                    result.append("{\"success\":\"光缆段拆除承载完成!\"}");                	
                }
			}else if(dbo instanceof WireSystem){

                DataObjectList list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegsByWireSystemCuid,
                    new BoActionContext(), dbo.getCuid());
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        WireSeg wireSeg = (WireSeg) list.get(i);
                        syschWireSegDisplayRoute(wireSeg, false,result);
                    }
                }
                if(!(result.indexOf("success") > 0)){
                    result.append("{\"success\":\"光缆段拆除承载完成!\"}");                	
                }
			}
			
		}catch(Exception e){
			result = NmsUtils.getStringBufferErrJson("光缆段拆除承载出错!"+e.getMessage());
			LogHome.getLog().info("光缆段拆除承载出错!",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
   private void syschWireSegDisplayRoute(WireSeg wireseg,boolean isPrompt,StringBuffer result) {
    	if(wireseg==null)
    		return;
        String wireBranchCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
        String wireSystemCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
        GenericDO branch = null;
        try {
            branch = (WireBranch) BoCmdFactory.getInstance().execBoCmd(WireBranchBOHelper.ActionName.getWireBranchByCuid,
                new BoActionContext(), wireBranchCuid);
        } catch (Exception ex) {
            LogHome.getLog().info("读取光缆分支出错"+ex.getMessage());
        }

        if (branch == null) {
            if (isPrompt) {
            	//光缆段所在光缆分支为空,生成显示路由失败
                result = NmsUtils.getStringBufferErrJson("光缆段所在光缆分支为空,生成显示路由失败!");
            }
            return;
        }

        //根据seg关系得到所有敷设信息
        DataObjectList wiretoductlines = new DataObjectList();
        DataObjectList wiredisplayseglists = new DataObjectList();
        List<String> deletePresetPoints=new ArrayList<String>();
        //根据关系得到所有敷设信息,可能是多种类型,对不同类型进行不同的处理
        try {
            wiretoductlines = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg,
                new BoActionContext(), wireseg);
        } catch (Exception ex) {
            LogHome.getLog().info("读取光缆敷设信息对象出错" + ex.getMessage());
        }
       
        wiretoductlines.sort(WireToDuctline.AttrName.indexInRoute, true); //用 INDEX_IN_ROUTE =1,2,3  进行排序
        try {

            List<String> pointCuidList = DMHelper.getPointCuidListByWireToDuctlines(wiretoductlines);
            String origPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.destPointCuid));

            List<String> displayPoints = getDisplayPointByWireBranch(DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid)));
            if(displayPoints==null){
                displayPoints=new ArrayList();
            }

            boolean isReset = false;
            if (displayPoints.size() > 0) {
                for (int i = 0; i < displayPoints.size(); i++) {
                    String pointCuid = displayPoints.get(i);
                    if (pointCuid.startsWith(PresetPoint.CLASS_NAME)) {
                        isReset = true;
                        if(!pointCuidList.contains(pointCuid)){
                            deletePresetPoints.add(pointCuid);
                        }
                    }
                }
            }

            if (pointCuidList.size() == 0) {
                pointCuidList = displayPoints;

                if (!isReset) {
                    pointCuidList = new ArrayList<String>();
                }
            }
            if (!pointCuidList.contains(origPointCuid)) {
                pointCuidList.add(0, origPointCuid);
            }
            if (!pointCuidList.contains(destPointCuid)) {
                pointCuidList.add(destPointCuid);
            }
            String[] pointCuidArray = new String[pointCuidList.size()];
            pointCuidList.toArray(pointCuidArray);
            DataObjectList points = getObjectsByCuids(pointCuidArray);

            for (int j = 0; j < points.size() - 1; j++) {
                WireDisplaySeg wiredisplayseg = new WireDisplaySeg();
                String labelcn = "";
                if (points.get(j) != null) {
                    labelcn = "" + points.get(j).getAttrString("LABEL_CN");
                }
                if (points.get(j + 1) != null) {
                    labelcn += "--" + points.get(j + 1).getAttrString("LABEL_CN");
                }
                // "具体路由"
                wiredisplayseg.setLabelCn(labelcn);
                wiredisplayseg.setIndexInBranch(j + 1);
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.origPointCuid, points.get(j));
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.destPointCuid, points.get(j + 1));
                wiredisplayseg.setRelatedSystemCuid(wireSystemCuid);
                wiredisplayseg.setRelatedBranchCuid(wireBranchCuid);

                wiredisplayseglists.add(wiredisplayseg);
            }
        } catch (Exception e) {
            LogHome.getLog().error(e.getMessage(), e);
        }
        DMHelper.clearDto(wiredisplayseglists);
        //删除旧显示路由 具体路由代替 到服务器
        //删除数据 条件 根据WireSeg中的RELATED_BRANCH_CUID 和 RELATED_SYSTEM_CUID 的
        DataObjectList wiresegdisplay = new DataObjectList();
        if (wiredisplayseglists.size() > 0) {
            try {
                wiresegdisplay = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                    WireDisplaySegBOHelper.ActionName.deleteAndAddWireDisplaySegs,
                    new BoActionContext(), wireseg, wiredisplayseglists);
            } catch (Exception ex) {
            	//删除显示路由,增加具体路由信息对象出错
                LogHome.getLog().info("删除显示路由时增加具体路由信息对象出错" + ex.getMessage());
            }

            branch.setAttrValue(DMCacheObjectName.SystemDisplaySegChildren, wiresegdisplay);
            
            try {
            	IWireSegBO bo = (IWireSegBO) BoHomeFactory.getInstance().getBO(IWireSegBO.class);
            	bo.modifyLayOrDeleteRelationWireSeg(new BoActionContext(), wireseg);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
           
            WireSystem wireSystem = new WireSystem();
            DataObjectList refreshList = new DataObjectList();
            wireSystem.setCuid(wireSystemCuid);
            refreshList.add(wireSystem);
            //arcgis版本中不做处理，此处如果要处理老版本，需要加上判断
//            DMGisUtils.getDmMap().refreshSystems((DataObjectList) refreshList);
            try {
                if (deletePresetPoints.size() > 0) {
                    String[] tmpPointCuids = new String[deletePresetPoints.size()];
                    for (int i = 0; i < deletePresetPoints.size(); i++) {
                        tmpPointCuids[i] = deletePresetPoints.get(i);
                    }
                    DataObjectList list = getObjectsByCuids(tmpPointCuids);
                   deletePointObjects(list);
                }
            } catch (Exception ex) {
            	result = NmsUtils.getStringBufferErrJson("删除预置点出错!");
                LogHome.getLog().info("删除预置点出错", ex);
            }
        }
    }
	 
   private List getDisplayPointByWireBranch(String branchCuid) {
       DataObjectList displays = null;
       try {
           displays = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireDisplaySegBOHelper.ActionName.getDisplaySegByBranch, new BoActionContext(),
               new WireBranch(branchCuid));
       } catch (Exception ex) {
           LogHome.getLog().error("getDisplayPointByWireBranch出错",ex);
       };
       if (displays == null || displays.isEmpty()) return null;
       List<String> pointCuids = new ArrayList<String>();
       for (GenericDO dto : displays) {
           if (!(dto instanceof WireDisplaySeg)) continue;
           WireDisplaySeg display = (WireDisplaySeg) dto;
           if (!pointCuids.contains(display.getOrigPointCuid())) {
               pointCuids.add(display.getOrigPointCuid());
           }
           if (!pointCuids.contains(display.getDestPointCuid())) {
               pointCuids.add(display.getDestPointCuid());
           }
       }
       return pointCuids;
   }
   
   private DataObjectList getObjectsByCuids(String[] cuids) {
       try {
           DataObjectList resList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getObjsByCuid,
               new BoActionContext(), cuids);
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
       } catch (Exception ex1) {
           ex1.printStackTrace();
       }
       return null;
   }
   
   private void deletePointObjects(DataObjectList list) throws Exception {
       if (list != null && list.size() > 0) {
           try {
               //从服务器删除
               BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLocatePoints, new BoActionContext(), list);
               //从内存删除及地图表删除,以下是非arcgis版本用到，注释
//              List strCuids= list.getCuidList();
//               for (int i = 0; i < list.size(); i++) {
//               	DMGisUtils.getDmMap().removeCachePoint(list.get(i));
//                   LocatePointCacheModel.getInstance().removeElement(list.get(i));
//
//               }
//               List allList = LocatePointCacheModel.getInstance().getElements();
//
//               if (allList != null && allList.size() > 0) {
//                   List tmpList=new ArrayList();
//                   for (int i = 0; i < allList.size(); i++) {
//                       GenericDO dto=(GenericDO) allList.get(i);
//                       String cuid=dto.getCuid();
//                       if(cuid!=null&&strCuids.contains(cuid)){
//                           tmpList.add(dto);
//                       }
//                   }
//                   if(tmpList.size()>0){
//                       allList.removeAll(tmpList);
//                   }
//               }

           } catch (Exception ex) {
               LogHome.getLog().error("删除地图显示点出错!", ex);
           }
       }
   }
   
	/**
	 * 管线设施拆除敷设功能，加载光缆列表方法
	 * @param cuid 当前选择的线设施cuid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/getWireSegByRelatedDuct/{cuid}", method = RequestMethod.POST)
	public void getWireSegByRelatedDuct(@PathVariable String cuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			DataObjectList resList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getWireSegByRelatedDuct, new BoActionContext(),cuid);
			if(resList != null && resList.size()>0){
				result.append("{\"wireSegList\":[");
				for(int i=0;i<resList.size();i++){
					GenericDO wireSegDbo = resList.get(i);
					String segCuid = wireSegDbo.getCuid();
					String wireSegName = wireSegDbo.getAttrString("WIRE_SEG");
					String wireSystemName = wireSegDbo.getAttrString("WIRE_SYSTEM");
					Map<String,String> map = new HashMap<String,String>();
					map.put("CUID", segCuid);
					map.put("WIRE_SEG_NAME", wireSegName);
					map.put("WIRE_SYSTEM_NAME", wireSystemName);
					String res = NmsUtils.getJsonByAttrNameAndValue(map);
					result.append(res);
//					result.append("{");
//					result.append("\"CUID\":").append("\""+segCuid+"\",");
//					result.append("\"WIRE_SEG_NAME\":").append("\""+wireSegName+"\",");
//					result.append("\"WIRE_SYSTEM_NAME\":").append("\""+wireSystemName+"\"");
//					result.append("}");
					if(i<resList.size()-1){
						result.append(",");
					}
				}
				result.append("]}");
			}
		} catch (Exception e) {
			LogHome.getLog().info("加载光缆数据出错!",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
   
	/**
	 * 管线设施拆除承载删除选择的光缆
	 * @param cuid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/deleteDuctlineRelation/{cuids}/{lineSegCuid}", method = RequestMethod.POST)
	public void deleteDuctlineRelation(@PathVariable String cuids,@PathVariable String lineSegCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineSegCuid);
			String wireSegCuids[] = cuids.split(",");

            if (wireSegCuids != null && wireSegCuids.length > 0) {
                BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLayRelation, new BoActionContext(), gdo.getCuid(),
                    wireSegCuids);
            }
            for(int i=0;i<wireSegCuids.length;i++){
            	String cuid = wireSegCuids[i];
            	sysWireSegDisplayRoute(cuid, false,result);
            }
            if(!(result.indexOf("success") > 0)){
                result.append("{\"success\":\"拆除承载成功\"}");            	
            }
		} catch (Exception e) {
			result = NmsUtils.getStringBufferErrJson("拆除承载失败");
			LogHome.getLog().info("加载光缆数据出错!",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	@RequestMapping(value = "/getHoleAndChildHoleOrCarryingCable/{systemCuid}/{columnName}", method = RequestMethod.POST)
	public void getHoleAndChildHoleOrCarryingCable(@PathVariable String systemCuid,@PathVariable String columnName,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		Map<Object, DataObjectList> childHoleMap = new HashMap<Object, DataObjectList>();
	    Map<Object, DataObjectList> holesMap = new HashMap<Object, DataObjectList>();
	    Map<Object, DataObjectList> carryingCablesMap = new HashMap<Object, DataObjectList>();
	    Map<Object, DataObjectList> wireToDuctlineMap = new HashMap<Object, DataObjectList>();
	    String systemClassName = parseClassNameFromCuid(systemCuid);
		try {
          if (DuctSystem.CLASS_NAME.equals(systemClassName)) {
        	  Map map = getDuctSystemBO().getResourcesBySystem(new BoActionContext(),systemCuid);
	          if (map != null) {
	              DataObjectList childHoles = (DataObjectList) map.get(DuctChildHole.CLASS_NAME);
	              DataObjectList holes = (DataObjectList) map.get(DuctHole.CLASS_NAME);
	              DataObjectList wireToDuctlines = (DataObjectList) map.get(WireToDuctline.CLASS_NAME);
	              childHoleMap = TopoHelper.getListMapByAttr(childHoles, DuctChildHole.AttrName.relatedHoleCuid);
	              holesMap = TopoHelper.getListMapByAttr(holes, DuctHole.AttrName.relatedSegCuid);
	              wireToDuctlineMap = TopoHelper.getListMapByAttr(wireToDuctlines, WireToDuctline.AttrName.childHoleCuid);
	          }
	      } else if (PolewaySystem.CLASS_NAME.equals(systemClassName)) {
	    	  DataObjectList carryingCables = getCarryingCableBO().getCarryingCableBySystem(new BoActionContext(),systemCuid);
	          carryingCablesMap = TopoHelper.getListMapByAttr(carryingCables, CarryingCable.AttrName.relatedSegCuid); ;
	      }
		
          String holeColumnName = "HOLE_NUM";
          String crrayColumnName = "CARRYING_NUM";
          String childColumnName = "CHILD_HOLE_NUM";
          DataObjectList nos = null;
          String origNo = "--";

          List selectList = new ArrayList();
//          List selectList = ((TWTreeTable) tTable).getDataBox().getSelectionModel().getAllSelectedElement();
//          Object[] objs = selectList.toArray();
//          GenericNode node = (GenericNode) selectList.toArray()[0];
//          GenericDO dbo = node.getNodeValue();
          String lineSystemCuid = systemCuid;
          //selectList需要将wiretoductline封装成一个list
          //对比所有管孔信息,得到所选管道段共有的管孔
//          if (objs.length > 0) {}
          String parameter = request.getParameter("segs");
          JSONObject jsonobj = (JSONObject) JSONArray.parse(parameter);
          JSONArray sysArray = (JSONArray) jsonobj.get("systemList");
          
          JSONObject sysObject = (JSONObject) sysArray.get(0);
          String scuid = String.valueOf(sysObject.get(Manhle.AttrName.cuid));
          JSONArray segsArray = (JSONArray) sysObject.get("segList");
          DataObjectList dataList = new DataObjectList();//NmsUtils.getGenericDOList(selectList, (IFilter)null);
          for(int p=0;p<segsArray.size();p++){
        	JSONObject segObject = (JSONObject) segsArray.get(p);
        	String cuid = segObject.getString("SEG_CUID");
        	String holeCuid = segObject.getString("HOLE_CUID");
        	String holeNum = segObject.getString("HOLE_NUM");
        	String childHoleCuid = segObject.getString("CHILD_HOLE_CUID");
        	String ChildHoleNum = segObject.getString("CHILD_HOLE_NUM");
        	String carryingCuid = segObject.getString("CARRYING_CUID");
        	String carryingNum = segObject.getString("CARRYING_NUM");
			GenericDO dbo = new GenericDO();
			dbo.setCuid(cuid);
			dbo.setAttrValue(WireToDuctline.AttrName.lineSegCuid, cuid);
			dbo.setAttrValue("HOLE_CUID", holeCuid);
			dbo.setAttrValue("HOLE_NUM", holeNum);
			dbo.setAttrValue("CHILD_HOLE_CUID", childHoleCuid);
			dbo.setAttrValue("CHILD_HOLE_NUM", ChildHoleNum);
			dbo.setAttrValue("CARRYING_CUID", carryingCuid);
			dbo.setAttrValue("CARRYING_NUM", carryingNum);
			if(!dataList.getCuidList().contains(cuid)){
				dataList.add(dbo);
			}
          }

          try { //管孔
//              DataObjectList dataList = NmsUtils.getGenericDOList(selectList, (IFilter)null);
              if (childColumnName.equals(columnName)) {
                  List list = NmsUtils.getList(dataList, "HOLE_NUM");//HOLENO
                  DataObjectList holeList = new DataObjectList();
                  holeList.addAll(list);
                  nos = getChildHoles(dataList,childHoleMap);
//                  TopoHelper.getListMapByAttr(nos, DuctChildHole.AttrName.cuid);
              } else if (holeColumnName.equals(columnName) || crrayColumnName.equals(columnName)) {
//                  Object o = dbo.getAttrValue(WireToDuctline.AttrName.lineSystemCuid);
//                  String cuid = DMHelper.getRelatedCuid(o);
                  if (lineSystemCuid != null && lineSystemCuid.startsWith(PolewaySystem.CLASS_NAME)) {
                      nos = getCarringCables(dataList,carryingCablesMap);
                  } else {
                      nos = getDuctHoles(dataList,holesMap);
                  }
              } else {
//                  return null;
              }
          } catch (Exception e1) {
              e1.printStackTrace();
          }
          if (nos == null || nos.isEmpty()) {
//              return ;
          }
          //构造菜单
          result.append("{\"holeList\":[");
          for(int i=0;i<nos.size();i++){
        	 GenericDO holeDbo = nos.get(i);
        	 String cuid = holeDbo.getCuid();
        	 String orig_No = "";
        	 String dest_No = "";
        	 if(cuid.startsWith(DuctHole.CLASS_NAME)){
        		 orig_No = holeDbo.getAttrString("ORIG_NO");
            	 dest_No = holeDbo.getAttrString("DEST_NO");
        	 }else if(cuid.startsWith(DuctChildHole.CLASS_NAME)){
        		 String ductChildHoleNum = String.valueOf(holeDbo.getAttrValue(DuctChildHole.AttrName.ductChildHoldNum));
        		 orig_No = ductChildHoleNum;
            	 dest_No = ductChildHoleNum;
        	 }else if(cuid.startsWith(CarryingCable.CLASS_NAME)){
        		 String innerNo = String.valueOf(holeDbo.getAttrValue(CarryingCable.AttrName.innerNo));
        		 orig_No = innerNo;
            	 dest_No = innerNo;
        	 }
        	 String relatedSegCuid = holeDbo.getAttrString("RELATED_SEG_CUID");
        	 Map<String,String> map = new HashMap<String,String>();
        	 map.put("CUID", cuid);
        	 map.put("ORIG_NO", orig_No);
        	 map.put("DEST_NO", dest_No);
        	 map.put("RELATED_SEG_CUID", relatedSegCuid);
        	 String attr = NmsUtils.getJsonByAttrNameAndValue(map);
        	 result.append(attr);
        	 
        	 if(i<nos.size()-1){
        		 result.append(",");
        	 }
          }
          result.append("]}");
		} catch (UserException e) {
			result.append("{\"error:\":\"查询管孔出错!\"}");
			e.printStackTrace();
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	private DataObjectList getSameDO(DataObjectList segs, String attrName, Map<Object, DataObjectList> doMap, final String keyAttrName) {
        DataObjectList list = getSameDO(segs, attrName, doMap, new Generator() {
            public Object generate(Object arg0) {
                if (arg0 instanceof GenericDO) {
                    return ((GenericDO) arg0).getAttrValue(keyAttrName);
                } else {
                    return null;
                }
            }
        });
        list.sort(keyAttrName, true);
        return list;
    }

    private DataObjectList getSameDO(DataObjectList segs, String attrName, Map<Object, DataObjectList> doMap, Generator keyGenerator) {
        DataObjectList rList = new DataObjectList();
        Map<Object, GenericDO> maps = new HashMap<Object, GenericDO>();
        if (segs.isEmpty()) {
            return rList;
        }
        Object id = DMHelper.getRelatedCuid(segs.get(0).getAttrValue(attrName));
        DataObjectList list = null;
        if (attrName.equals("LINE_SEG_CUID")) { //管孔
            list = doMap.get(id);
            if (list == null || list.isEmpty()) {
                return rList;
            }
            for (GenericDO gdo : list) {
                Object key = keyGenerator.generate(gdo);
                maps.put(key, gdo);
            }
        } else { //  bug:10674 屏蔽 坏孔的情况
//            WireToDuctline wt = (WireToDuctline) segs.get(0);
        	GenericDO wt = (GenericDO) segs.get(0);
            String holecuid = "";
            if (wt.getAttrValue(WireToDuctline.AttrName.holeCuid) instanceof String) {
                holecuid = (String) wt.getAttrValue(WireToDuctline.AttrName.holeCuid);
            } else if (wt.getAttrValue(WireToDuctline.AttrName.holeCuid) instanceof DuctHole) {
                DuctHole hole = (DuctHole) wt.getAttrValue(WireToDuctline.AttrName.holeCuid);
                holecuid = hole.getCuid();
            }
            String cuid = DuctChildHole.AttrName.relatedHoleCuid + " = '" + holecuid + "'";
            try {
                list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    DuctChildHoleBOHelper.ActionName.getDuctChildHoleBySql, new BoActionContext(), cuid);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (list == null || list.isEmpty()) {
                return rList;
            }
            for (GenericDO gdo : list) {
                if (!gdo.getAttrValue(DuctChildHole.AttrName.usageState).equals(DuctEnum.ChildHoleState._bad)) {
                    Object key = keyGenerator.generate(gdo);
                    maps.put(key, gdo);
                }
            }
        }

        if (maps.isEmpty()) {
            return rList;
        }
        for (int i = 1; i < segs.size(); i++) {
            GenericDO seg = segs.get(i);
            id = DMHelper.getRelatedCuid(seg.getAttrValue(attrName));
            list = doMap.get(id);
            if (list == null || list.isEmpty()) {
                return rList;
            }
            Map keyMap = new HashMap();
            for (GenericDO gdo : list) {
                Object key = keyGenerator.generate(gdo);
                keyMap.put(key, gdo);
            }
            maps = NmsUtils.getCommonMap(maps, keyMap);
        }
        rList.addAll(maps.values());
        return rList;
    }

    private DataObjectList getCarringCables(DataObjectList segs,Map carryingCablesMap) throws Exception {
        return getSameDO(segs, WireToDuctline.AttrName.lineSegCuid, carryingCablesMap, CarryingCable.AttrName.innerNo);
    }

    private DataObjectList getChildHoles(DataObjectList segs,Map childHoleMap) throws Exception {
        return getSameDO(segs, WireToDuctline.AttrName.holeCuid, childHoleMap, DuctChildHole.AttrName.ductChildHoldNum);
    }

    private DataObjectList getDuctHoles(DataObjectList segs,Map holesMap) throws Exception {
        return getSameDO(segs, WireToDuctline.AttrName.lineSegCuid, holesMap, DuctHole.AttrName.origNo);
    }

    private void sysWireSegDisplayRoute(String wireSegCuid,boolean isPrompt,StringBuffer result) {
        WireSeg wireSeg = new WireSeg();
        try {
            wireSeg = (WireSeg) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegByCuid,
                new BoActionContext(), wireSegCuid);
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage(), ex);
        }
        syschWireSegDisplayRoute(wireSeg, isPrompt,result);
    }
    
    
	/**
	 * 图形化光缆自动敷设
	 * @param wireSegCuid 当前选择的光缆段cuid
	 * @param lineCuid 当前选择的分支、系统、段cuid
	 * @param direction 0：正向:1：反向
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/graphWireLayToNewDuctLine/{wireSegCuid}/{lineCuid}/{direction}", method = RequestMethod.POST)
	public void graphWireLayToNewDuctLine(@PathVariable String wireSegCuid,@PathVariable String lineCuid,@PathVariable String direction,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			//当前选择的分支或段
			GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineCuid);
			WireSeg wireSeg = (WireSeg) getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
			if (direction.equals("0")) {//0代表正向
			    doLayWireSeg(gdo,true,wireSeg,result);
			} else {
			    doLayWireSeg(gdo,false,wireSeg,result);
			}
			result.append("{\"success\":\"光缆段敷设成功\"}");
		} catch (UserException e) {
			result = NmsUtils.getStringBufferErrJson("光缆段敷设失败");
			LogHome.getLog().info("光缆段敷设失败",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	@RequestMapping(value = "/getGlayLayWireSelectLine/{lineCuid}", method = RequestMethod.POST)
	public void getGlayLayWireSelectLine(@PathVariable String lineCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try{
			String className = lineCuid.split("-")[0];
			boolean segClassName = DMHelper.isSegClassName(className);
			if(segClassName){
				result = NmsUtils.getStringBufferErrJson("请选择分支或系统");
			}else{
				setSystemOrBranch(lineCuid,result);
			}
		}catch(Exception ex){
			result = NmsUtils.getStringBufferErrJson("选择数据失败!");
			LogHome.getLog().info("选择数据失败!",ex);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	private void setSystemOrBranch(String lineCuid,StringBuffer result){
		String className = lineCuid.split("-")[0];
		boolean branchClassName = DMHelper.isBranchClassName(className);
		GenericDO selectBranchDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineCuid);
		if(branchClassName){
			String systemCuid = DMHelper.getRelatedCuid(selectBranchDO.getAttrValue(DuctBranch.AttrName.relatedSystemCuid));
			setLineBranch(systemCuid,lineCuid,result);
		}else{
			setLineSystem(lineCuid,result);
		}
		
	}
    private void setLineBranch(String systemCuid,String branchCuid,StringBuffer result) {
    	GenericDO selectSystemDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
    	GenericDO selectBranchDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), branchCuid);
        
        String systemName = selectSystemDO.getAttrString(GenericDO.AttrName.labelCn);
        String branchName = selectBranchDO.getAttrString(GenericDO.AttrName.labelCn);
        DataObjectList lineSegs = new DataObjectList();

        DataObjectList dbPoints = null;
        try {
            lineSegs = getSegsBySystemCuid(branchCuid);
        } catch (Exception ex) {
        }
        try {
            dbPoints = getPointsBySystemCuid(branchCuid);
        } catch (Exception ex) {
        }
        if (lineSegs != null) {
            lineSegs.sort(DuctSeg.AttrName.indexInBranch, true);
            DMHelper.setSegsPoints(lineSegs, dbPoints);
            DataObjectList points = DMHelper.getSegsPoints(lineSegs);
            
            result.append("{\"selectList\":[");
            result.append("{");
			result.append("\"SYSTEM_CUID\":").append("\""+systemCuid+"\",");
			result.append("\"SYSTEM_NAME\":").append("\""+systemName+"\",");
			result.append("\"BRANCH_CUID\":").append("\""+branchCuid+"\",");
			result.append("\"BRANCH_NAME\":").append("\""+branchName+"\"");
			result.append(",\"pointList\":[");
            for (int i=0;i<points.size();i++) {
            	GenericDO point = points.get(i);
            	Map<String, String> map = new HashMap<String, String>();
            	String pointCuid = point.getCuid();
            	String pointLabelCn = point.getAttrString(DuctSeg.AttrName.labelCn);
            	map.put("POINT_CUID", pointCuid);
            	map.put("POINT_NAME", pointLabelCn);
            	String res = NmsUtils.getJsonByAttrNameAndValue(map);
				result.append(res);
				if(i < points.size()-1){
					result.append(",");
				}
            }
            result.append("]");
            result.append(",\"lineSegList\":[");
            for(int i=0;i<lineSegs.size();i++){
            	GenericDO segDbo = lineSegs.get(i);
            	String segCuid = segDbo.getCuid(),
            		seglabelCn = segDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	GenericDO origPointDbo = (GenericDO) segDbo.getAttrValue(DuctSeg.AttrName.origPointCuid);
            	String origPointCuid = origPointDbo.getCuid(),
            		origPointName = origPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	
            	GenericDO destPointDbo = (GenericDO) segDbo.getAttrValue(DuctSeg.AttrName.destPointCuid);
            	String destPointCuid = destPointDbo.getCuid(),
                		destPointName = destPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	
            	String direction = String.valueOf(segDbo.getAttrValue(DuctSeg.AttrName.direction));
            	Map<String, String> map = new HashMap<String, String>();
            	map.put("ORIG_POINT_CUID", origPointCuid);
            	map.put("ORIG_POINT_NAME", origPointName);
            	map.put("DEST_POINT_CUID", destPointCuid);
            	map.put("DEST_POINT_NAME", destPointName);
            	map.put("DIRECTION", direction);
            	
            	map.put("SYSTEM_CUID", systemCuid);
            	map.put("BRANCH_CUID", branchCuid);
            	map.put("SEG_CUID", segCuid);
            	map.put("SEG_NAME", seglabelCn);
            	String res = NmsUtils.getJsonByAttrNameAndValue(map);
            	result.append(res);
            	if(i<lineSegs.size()-1){
            		result.append(",");
            	}
            }
            result.append("]");
            result.append("}]}");
        }

    }

    public void setLineSystem(String systemCuid,StringBuffer result) {
    	DataObjectList lineSegs = new DataObjectList();
    	GenericDO selectSystemDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
        
        String systemName = selectSystemDO.getAttrString(GenericDO.AttrName.labelCn);

        DataObjectList dbPoints = null;
        try {
            lineSegs = getSegsBySystemCuid(systemCuid);
        } catch (Exception ex) {
        }
        try {
            dbPoints = getPointsBySystemCuid(systemCuid);
        } catch (Exception ex) {
        }
        if (lineSegs != null) {
            lineSegs.sort(DuctSeg.AttrName.indexInBranch, true);
            DMHelper.setSegsPoints(lineSegs, dbPoints);
            DataObjectList points = DMHelper.getSegsPoints(lineSegs);
            
            result.append("{\"selectList\":[");
            result.append("{");
			result.append("\"SYSTEM_CUID\":").append("\""+systemCuid+"\",");
			result.append("\"SYSTEM_NAME\":").append("\""+systemName+"\"");
//			result.append("\"BRANCH_CUID\":").append("\""+branchCuid+"\",");
//			result.append("\"BRANCH_NAME\":").append("\""+branchName+"\"");
			result.append(",\"pointList\":[");
            for (int i=0;i<points.size();i++) {
            	GenericDO point = points.get(i);
            	Map<String, String> map = new HashMap<String, String>();
            	String pointCuid = point.getCuid();
            	String pointLabelCn = point.getAttrString(DuctSeg.AttrName.labelCn);
            	map.put("POINT_CUID", pointCuid);
            	map.put("POINT_NAME", pointLabelCn);
            	String res = NmsUtils.getJsonByAttrNameAndValue(map);;
				result.append(res);
				if(i < points.size()-1){
					result.append(",");
				}
            }
            result.append("]");
            result.append(",\"lineSegList\":[");
            for(int i=0;i<lineSegs.size();i++){
            	GenericDO segDbo = lineSegs.get(i);
            	String segCuid = segDbo.getCuid(),
            		seglabelCn = segDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	GenericDO origPointDbo = (GenericDO) segDbo.getAttrValue(DuctSeg.AttrName.origPointCuid);
            	String origPointCuid = origPointDbo.getCuid(),
            		origPointName = origPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	
            	String branchCuid = DMHelper.getRelatedCuid(segDbo.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
            			
            	GenericDO destPointDbo = (GenericDO) segDbo.getAttrValue(DuctSeg.AttrName.destPointCuid);
            	String destPointCuid = destPointDbo.getCuid(),
                		destPointName = destPointDbo.getAttrString(DuctSeg.AttrName.labelCn);
            	
            	String direction = String.valueOf(segDbo.getAttrValue(DuctSeg.AttrName.direction));
            	Map<String, String> map = new HashMap<String, String>();
            	map.put("ORIG_POINT_CUID", origPointCuid);
            	map.put("ORIG_POINT_NAME", origPointName);
            	map.put("DEST_POINT_CUID", destPointCuid);
            	map.put("DEST_POINT_NAME", destPointName);
            	map.put("DIRECTION", direction);
            	
            	map.put("SYSTEM_CUID", systemCuid);
            	map.put("BRANCH_CUID", branchCuid);
            	map.put("SEG_CUID", segCuid);
            	map.put("SEG_NAME", seglabelCn);
            	String res = NmsUtils.getJsonByAttrNameAndValue(map);
            	result.append(res);
            	if(i<lineSegs.size()-1){
            		result.append(",");
            	}
            }
            result.append("]");
            result.append("}]}");
        }

    }
    
    private void doLayWireSeg(GenericDO lineGdo,boolean isForward,WireSeg wireSeg,StringBuffer result) {
        if (lineGdo instanceof UpLine
            || lineGdo instanceof HangWall
            || lineGdo instanceof DuctBranch
            || lineGdo instanceof PolewayBranch
            || lineGdo instanceof StonewayBranch) {
            automaticLayWireSegToSystemOrBranch(wireSeg, lineGdo.getCuid(), isForward,result);

        } else if (lineGdo instanceof DuctSeg
                   || lineGdo instanceof PolewaySeg
                   || lineGdo instanceof StonewaySeg
                   || lineGdo instanceof UpLineSeg
                   || lineGdo instanceof HangWallSeg) {
            automaticLayWireSegToSeg(wireSeg, lineGdo, isForward,result);
        }
    }
    

    //将光缆段自动化敷设到管线段（敷设优先级级别 空闲子孔>空闲管孔>段)
    //WireSeg wireSeg 要敷设的光缆段
    //DataObjectList segs 要敷设到得管线段顺序列表
    //boolean isForwardDirection 敷设方向
    public void automaticLayWireSegToSeg(WireSeg wireSeg, GenericDO seg, boolean isForwardDirection,StringBuffer result) {
        WireToDuctline wtd = getNewWireToDuctline(wireSeg, seg, isForwardDirection);
        DataObjectList wireToDuctlines = new DataObjectList();
        wireToDuctlines.add(wtd);
        //暂时屏蔽  2012-6-12
        //modify by xuwb 返回光纤对象供方法syschWireSegDisplayRoute使用
        wireSeg = addWireToDuctlinesToDB(wireToDuctlines, wireSeg);
        //addWireToDuctlinesToDB(wireToDuctlines, wireSeg);
        syschWireSegDisplayRoute(wireSeg,true,result);
    }
    
    public void automaticLayWireSegToSystemOrBranch(WireSeg wireSeg, String cuid, boolean isForwardDirection,StringBuffer result) {
        DataObjectList segList = null;
        try {
            segList = getSegsBySystemCuid(cuid);
            segList.sort(DuctSeg.AttrName.indexInBranch, isForwardDirection);
        } catch (Exception ex) {
//            LogHome.getLog().error(PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.graph.GDMUtils.java1"), ex);//PropertyMessage
            return;
        }

        if (segList == null || segList.size() == 0) {
        	//获取分支或者系统下面的段为空,无法完成敷设！
//            MessagePane.showErrorMessage(PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.graph.GDMUtils.java2"));
            return;
        }

        DataObjectList wireToDuctlines = new DataObjectList();
        for (int i = 0; i < segList.size(); i++) {
            GenericDO seg = segList.get(i);
            WireToDuctline wtd = getNewWireToDuctline(wireSeg, seg, isForwardDirection);
            wireToDuctlines.add(wtd);
        }
        addWireToDuctlinesToDB(wireToDuctlines, wireSeg);
        syschWireSegDisplayRoute(wireSeg,true,result);
    }
    
    //通过要敷设的光缆段和要敷设到得管线段生成敷设关系。
    //WireSeg wireSeg 要敷设的光缆段
    //GenericDO seg 要敷设到的管线段
    //boolean isForwardDirection 敷设方向

    private WireToDuctline getNewWireToDuctline(WireSeg wireSeg, GenericDO seg, boolean isForwardDirection) {

        WireToDuctline wtd = new WireToDuctline();
        if (isForwardDirection) {
            wtd.setDirection(1);
        } else {
            wtd.setDirection(2);
        }

        wtd.setCuid();
        wtd.setWireSegCuid(wireSeg.getCuid());
        String wireSystemCuid = DMHelper.getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
        wtd.setWireSystemCuid(wireSystemCuid);
        wtd.setLineSegCuid(seg.getCuid());
        String relatedSystemCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));

        String origPointCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.origPointCuid));
        String destPointCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.destPointCuid));
        wtd.setLineSystemCuid(relatedSystemCuid);
        wtd.setDisPointCuid(origPointCuid);
        wtd.setEndPointCuid(destPointCuid);

        setWireToDuctLineLabelCnAttr(wtd, seg);
        String segClassName = seg.getClassName();

        if (segClassName.equals(DuctSeg.CLASS_NAME) || segClassName.equals(PolewaySeg.CLASS_NAME) || segClassName.equals(StonewaySeg.CLASS_NAME)) {
            String relatedBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
            wtd.setLineBranchCuid(relatedBranchCuid);
        }

        if (segClassName.equals(DuctSeg.CLASS_NAME)) {
            GenericDO holeGdo = getDuctSegBO().getFreeAndSmallestNumberHoleByDuctSegCuid(new BoActionContext(), seg.getCuid(), isForwardDirection);
            if (holeGdo instanceof DuctChildHole) {
                DuctChildHole dch = (DuctChildHole) holeGdo;
                wtd.setChildHoleCuid(dch.getCuid());
                wtd.setHoleCuid(dch.getRelatedHoleCuid());
                wtd.setDuctlineCuid(dch.getCuid());
            } else if (holeGdo instanceof DuctHole) {
                DuctHole dh = (DuctHole) holeGdo;
                wtd.setHoleCuid(dh.getCuid());
                wtd.setDuctlineCuid(dh.getCuid());
            } else if (holeGdo instanceof DuctSeg) {
                DuctSeg ds = (DuctSeg) holeGdo;
                wtd.setDuctlineCuid(ds.getCuid());
            }
        } else if (segClassName.equals(PolewaySeg.CLASS_NAME)) {
            GenericDO carryingCableGdo = getPolewaySegBO().getSmallestNumberCarryingCableByPolewaySegCuid(new BoActionContext(), seg.getCuid());
            if (carryingCableGdo instanceof CarryingCable) {
                CarryingCable cc = (CarryingCable) carryingCableGdo;
                wtd.setHoleCuid(cc.getCuid());
                wtd.setDuctlineCuid(cc.getCuid());

            } else if (carryingCableGdo instanceof PolewaySeg) {
                PolewaySeg ps = (PolewaySeg) carryingCableGdo;
                wtd.setDuctlineCuid(ps.getCuid());
            }
        } else {
            wtd.setDuctlineCuid(seg.getCuid());
        }
        return wtd;
    }
    
    private void setWireToDuctLineLabelCnAttr(WireToDuctline wtd, GenericDO seg) {
        Object origPoint = seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
        Object destPoint = seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
        String origPointLabelCn = getLabelCn(origPoint);
        String destPointLabelCn = getLabelCn(destPoint);
        wtd.setLabelCn(origPointLabelCn + "--" + destPointLabelCn);
    }
    private String getLabelCn(Object origPoint) {
        if (!(origPoint instanceof GenericDO)) {
            return "";
        }
        GenericDO gdoPoint = (GenericDO) origPoint;
        return gdoPoint.getAttrString(GenericDO.AttrName.labelCn);
    }
    
    public WireSeg addWireToDuctlinesToDB(DataObjectList wireToDuctlines, WireSeg wireSeg) {
        try {

            DataObjectList systemList = getSystemList(wireToDuctlines);

            DataObjectList fromDBwtdlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getDMResouceByAttr,
                new BoActionContext(), WireToDuctline.CLASS_NAME, WireToDuctline.AttrName.wireSegCuid, wireSeg.getCuid());

            DataObjectList saveToDBSystemList = new DataObjectList();
            if (fromDBwtdlist != null && fromDBwtdlist.size() > 0) {
                fromDBwtdlist.sort(WireToDuctline.AttrName.indexInRoute, true);
                GenericDO system = new GenericDO();
                TopoHelper.getChildren(system).addAll(fromDBwtdlist);
                saveToDBSystemList.add(system);
            }

            saveToDBSystemList.addAll(systemList);

            //下面这个调用的方法systemList必须还要原来的敷设关系，如果不含有则此方法会把原来的敷设关系删除。
            BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.modifyWireToDuctLines,
                                                 new BoActionContext(), wireSeg, saveToDBSystemList);
    		  
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return wireSeg;
    }
    
    private DataObjectList getSystemList(DataObjectList wireToDuctlines) {
        DataObjectList systemList = new DataObjectList();
        GenericDO newsystem = new GenericDO();
        systemList.add(newsystem);
        TopoHelper.getChildren(newsystem).addAll(wireToDuctlines);
        return systemList;
    }
    
    private DataObjectList getSegsBySystemCuid(String systemCuid) throws Exception {
        return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                DuctManagerBOHelper.ActionName.getSegsBySystemCuid, new BoActionContext(), systemCuid);
    }
    
    private DataObjectList getPointsBySystemCuid(String systemCuid) throws Exception {
        return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                DuctManagerBOHelper.ActionName.getPointsBySystemCuid, new BoActionContext(), systemCuid);
    }
    
    /**
     * 图形化承载光缆(手动)
     * @param lineCuid
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/getGlaySelectDuctLine/{lineCuid}", method = RequestMethod.POST)
	public void getGlaySelectDuctLine(@PathVariable String lineCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try{
			String className = lineCuid.split("-")[0];
			boolean segClassName = DMHelper.isSegClassName(className);
			if(segClassName){
				GenericDO selectSegDO = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineCuid);
				String systemCuid = DMHelper.getRelatedCuid(selectSegDO.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
				String branchCuid = DMHelper.getRelatedCuid(selectSegDO.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));			
				setLineBranch(systemCuid,branchCuid,result);
			}else{
				setSystemOrBranch(lineCuid,result);
			}
		}catch(Exception ex){
			result = NmsUtils.getStringBufferErrJson("选择数据失败!");
			LogHome.getLog().info("选择数据失败!",ex);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	@RequestMapping(value = "/modifyGlayDuctLineWire/{wireSegCuid}", method = RequestMethod.POST)
	public void modifyGlayDuctLineWire(@PathVariable String wireSegCuid,HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			String parameter = request.getParameter("wireductlinelist");
			JSONObject jsonobj = (JSONObject) JSONArray.parse(parameter);
			JSONArray segsArray = (JSONArray) jsonobj.get("segList");
			DataObjectList wtds = new DataObjectList();
			GenericDO wireSegDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
			String wireSystemCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
			
			DataObjectList systems = new DataObjectList();
			Map<String,List<JSONObject>> systemMap = new HashMap<String,List<JSONObject>>();
			for(int i=0;i<segsArray.size();i++){
				JSONObject sysObject = (JSONObject) segsArray.get(i);
				String cuid = String.valueOf(sysObject.get("CUID"));
				String className = cuid.split("-")[0];
				if(DMHelper.isLineSegClassName(className)){
					String relatedSystemCuid = String.valueOf(sysObject.get("LINE_SYSTEM_CUID"));
					List<JSONObject> list = systemMap.get(relatedSystemCuid);
					if (list == null) {
						list = new ArrayList<JSONObject>();
						systemMap.put(relatedSystemCuid, list);
					}
					list.add(sysObject);
				}
			}
			
			for(String relatedSystemCuid:systemMap.keySet()){
				List<JSONObject> list = systemMap.get(relatedSystemCuid);
				
				if(list != null && list.size()>0){
					GenericDO system = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedSystemCuid);
					if(!systems.getCuidList().contains(relatedSystemCuid)){
						//如果列表中选择的分支所属系统和当前选择的是一个系统，需要将原来的敷设信息先加上
						String sql = WireToDuctline.AttrName.lineSystemCuid + " = '" + relatedSystemCuid + "'";
						DataObjectList w2dLists = getDuctManagerBO().getObjectsBySql(sql, new WireToDuctline());
						w2dLists.sort(WireToDuctline.AttrName.indexInRoute, true);
						TopoHelper.getChildren(system).addAll(w2dLists);
						wtds = getWire2DuctLines(list,relatedSystemCuid,wireSegCuid,wireSystemCuid,relatedSystemCuid);
						systems.add(system);
						TopoHelper.getChildren(system).addAll(wtds);
					}
					
				}
			}
			BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.modifyWireToDuctLines,
			         new BoActionContext(), wireSegDbo, systems);
			getWireSegBO().modifyLayOrDeleteRelationWireSeg(new BoActionContext(), (WireSeg) wireSegDbo);
        	result.append("{\"success\":\"光缆修改完成!\"}");
		}catch (Exception e) {
			result = NmsUtils.getStringBufferErrJson("光缆修改失败!");
			LogHome.getLog().info("光缆修改失败：",e);
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}

	private DataObjectList getWire2DuctLines(List<JSONObject> list, String relatedSystemCuid, String wireSegCuid, String wireSystemCuid, String relatedSystemCuid2) {
		DataObjectList wtds = new DataObjectList();
		try {
			if(list != null && list.size() > 0){
				for(int i=0;i<list.size();i++){
					JSONObject segsObject = (JSONObject) list.get(i);
					String lineSegCuid = String.valueOf(segsObject.get("CUID"));
					String systemName=relatedSystemCuid.split("-")[0];
					
					String origPointCuid = String.valueOf(segsObject.get(DuctSeg.AttrName.origPointCuid));
				    String destPointCuid = String.valueOf(segsObject.get(DuctSeg.AttrName.destPointCuid));
				    String direction = String.valueOf(segsObject.get("DIRECTION"));
				    WireToDuctline wto = new WireToDuctline();
				    wto.setCuid();
				    wto.setWireSegCuid(wireSegCuid);
				    wto.setWireSystemCuid(wireSystemCuid);
				    wto.setLineSegCuid(lineSegCuid);
				    
				    wto.setLineSystemCuid(relatedSystemCuid);
				    wto.setDisPointCuid(origPointCuid);
				    wto.setEndPointCuid(destPointCuid);
				    
				    if (systemName.equals(DuctSystem.CLASS_NAME) || systemName.equals(PolewaySystem.CLASS_NAME) || systemName.equals(StonewaySystem.CLASS_NAME)) {
				       String relatedBranchCuid= String.valueOf(segsObject.get("LINE_BRANCH_CUID"));//DMHelper.getRelatedCuid(lineSegDbo.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
				       wto.setLineBranchCuid(relatedBranchCuid);
				    }
				    
				    String ductChildHoleCuid = String.valueOf(segsObject.get("CHILD_HOLE_CUID"));//DMHelper.getRelatedCuid(lineSegDbo.getAttrValue("CHILDHOLENO"));
				    String ductHoleCuid = String.valueOf(segsObject.get("HOLE_CUID"));//DMHelper.getRelatedCuid(lineSegDbo.getAttrValue("HOLENO"));
				    String CarryingCableCuid = String.valueOf(segsObject.get("CARRYING_CUID"));//DMHelper.getRelatedCuid(seg.getAttrValue("INNERNO"));
				    if (systemName.equals(DuctSystem.CLASS_NAME)) {
				        if (ductChildHoleCuid != null && !ductChildHoleCuid.trim().equals("")) {
				            wto.setChildHoleCuid(ductChildHoleCuid);
				            wto.setHoleCuid(ductHoleCuid);
				            wto.setDuctlineCuid(ductChildHoleCuid);
				        } else {
				            if (ductHoleCuid != null && !ductHoleCuid.trim().equals("")) {
				                wto.setHoleCuid(ductHoleCuid);
				                wto.setDuctlineCuid(ductHoleCuid);
				            } else {
				                wto.setDuctlineCuid(lineSegCuid);
				            }
				        }
				    } else if (systemName.equals(PolewaySystem.CLASS_NAME)) {
				        if (CarryingCableCuid != null && !CarryingCableCuid.trim().equals("")) {
				            wto.setHoleCuid(CarryingCableCuid);
				            wto.setDuctlineCuid(CarryingCableCuid);
				        } else {
				            wto.setDuctlineCuid(lineSegCuid);
				        }
				    } else {
				        wto.setDuctlineCuid(lineSegCuid);
				    }
				    wto.setDirection(Long.parseLong(direction));
				    wtds.add(wto);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return wtds;
	}
    
    private ITempCutoverWireSegBO getTempCutoverWireSegBO(){
		 return BoHomeFactory.getInstance().getBO(ITempCutoverWireSegBO.class);
	}
    
	private ITempWireToDuctlineBO getTempWireToDuctlineBO(){
		 return BoHomeFactory.getInstance().getBO(ITempWireToDuctlineBO.class);
	}
    
}
