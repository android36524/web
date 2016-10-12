package com.boco.irms.app.utils;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twaver.PersistenceManager;
import twaver.TWaverConst;
import twaver.comm.ElementBox;

import com.boco.common.util.db.TransactionFactory;
import com.boco.common.util.db.UserTransaction;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.bean.SpringContextUtil;
import com.boco.graphkit.ext.PortNode;
import com.boco.graphkit.ext.component.TWDataBox;
import com.boco.irms.app.dm.bo.DMDataPlantBO;
import com.boco.topo.dm.rest.NeTemplateHelper;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.PropertyConst;
import com.boco.transnms.common.bussiness.consts.TemplateEnum;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.AnOnu;
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.Card;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.LineSystem;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.MicrowaveSystem;
import com.boco.transnms.common.dto.NeToCab;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.Odfport;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.OpticalFunction;
import com.boco.transnms.common.dto.OverlayResource;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.Ptp;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.SystemPara;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.common.Template;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.dm.dataimpexp.ImpExpConsts;
import com.boco.transnms.server.bo.dm.dataimpexp.ImpExpUtils;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.cm.IJumpFiberBO;
import com.boco.transnms.server.bo.ibo.cm.IPTPBO;
import com.boco.transnms.server.bo.ibo.cm.IRoomBO;
import com.boco.transnms.server.bo.ibo.cm.ISiteBO;
import com.boco.transnms.server.bo.ibo.common.ITemplateBOX;
import com.boco.transnms.server.bo.ibo.dm.IDataImportBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFcabportBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IFibercabmoduleBO;
import com.boco.transnms.server.bo.ibo.misc.ISecurityBO;
import com.boco.transnms.server.bo.ibo.system.ISystemParaBO;


/**
 * <p>Title:管线数据导入:导入各种类型的对象 </p>
 *
 * <p>Description:分别导入各种对象 </p>
 *
 * ------------------------------------------------------------*
 *          COPYRIGHT (C) 2008 BOCO Inter-Telecom INC          *
 *   CONFIDENTIAL AND PROPRIETARY. ALL RIGHTS RESERVED.        *
 *                                                             *
 *  This work contains confidential business information       *
 *  and intellectual property of BOCO  Inc, Beijing, CN.       *
 *  All rights reserved.                                       *
 * ------------------------------------------------------------*
 *
 * @author liuchunhui
 *
 * @version 1.0 2008-8-12
 */

public class ImportConverter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private  SysUser sysUser=null;
	
	public ImportConverter(SysUser user){
	    sysUser=user;
	}
    public ArrayList importManhle(Workbook workbook) throws Exception {
        return importManhle(workbook, "");
    }
    
    public IDataImportBO getDataImportBO() {
        IDataImportBO dataImportBO = (IDataImportBO)BoHomeFactory.getInstance().getBO(IDataImportBO.class);
        return dataImportBO;
    }
    
    private IJumpFiberBO getJumpFiberBO(){
		return BoHomeFactory.getInstance().getBO(IJumpFiberBO.class);
	}
    
    public IDistrictBO getDistrictBO() {
        return (IDistrictBO)BoHomeFactory.getInstance().getBO(IDistrictBO.class);
    }
    
    private ISiteBO getSiteBO() {
        return BoHomeFactory.getInstance().getBO(ISiteBO.class);
    }
    
    private ISystemParaBO getSystemParaBO(){
    	return BoHomeFactory.getInstance().getBO(ISystemParaBO.class);
    }
    
    private IFiberDpPortBO getFiberDpPortBO(){
		return BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
	}
    
    private IFiberBO getfiberBo() {
        return BoHomeFactory.getInstance().getBO(IFiberBO.class);
    }
    
    private HashSet<District> getMaxDistricts(DataObjectList districts){
    	 HashSet<District> hashSet=new HashSet<District>();
    	 if(districts!=null){
    		 String maxDistrict = "DISTRICT-00001-00000-00000-00000-00000";
    		  for(GenericDO district:districts){
    			   if(district.getCuid().length()<maxDistrict.length()){
    				   maxDistrict=district.getCuid();
    			   }
    		  }
    		  
    		  for(GenericDO gdo:districts){
    			  if(gdo.getCuid().length()==maxDistrict.length()){
    				  hashSet.add((District)gdo);
    			  }
    		  }    		  
    	 }
    	 
    	 return hashSet;
    }
    
    private HashSet<District> checkLonAndLatByUserDistricts(ArrayList districterr){
    	HashSet<District> results=new HashSet<District>();
    	
    	String userId=sysUser.getCuid();
    	
    		DataObjectList districts=null;
    		try {    			
				districts=((ISecurityBO)BoHomeFactory.getInstance().getBO(BoName.SecurityBO)).getUserDistricts(new BoActionContext(), userId);
			} catch (Throwable ex) {
				// TODO: handle exception
				LogHome.getLog().error("加载可管理区域对象出错", ex);
				throw new UserException(ex);
			}
			
			results=getMaxDistricts(districts);
			
			if(results!=null && !results.isEmpty()){				
				checkLonAndLatByMaxDistricts(districterr, results);				
			}			

    	return results;
    }
    
    private void checkLonAndLatByMaxDistricts(ArrayList districterr,HashSet<District> maxDistricts){
    	StringBuffer sb=new StringBuffer();  
    	String falseDistrictNames="";
    	for(District district:maxDistricts){    		 
    		 if(district!=null){
        		 double orig_longitude=district.getOrigLongitude();
            	 double orig_latitude=district.getOrigLatitude();
            	 double dest_longitude=district.getDestLongitude();
            	 double dest_latitude=district.getDestLatitude();
            	 //全部为0.0，则存量中区域的最大最小经纬度没有填写，跳过经纬度区间校验
            	 if(orig_longitude==0.0 &&orig_latitude==0.0 &&dest_longitude==0.0 &&dest_latitude==0.0){
            		 continue;
            	 }
            	 
            	 if(!((orig_longitude>0.0 && orig_longitude<180.0) && (orig_latitude>0.0 && orig_latitude<90.0) 
            	  && (dest_longitude>0.0 && dest_longitude<180.0) && (dest_latitude>0.0 && dest_latitude<90.0))){
            		 sb.append(district.getLabelCn()+",");
            	 }
            	 else{
            		 
            	 }        	
        	 } 
    	 } 
    	 falseDistrictNames=sb.toString();
    	 if(!"".equals(falseDistrictNames)){
    		 falseDistrictNames=falseDistrictNames.substring(0,falseDistrictNames.length()-1);
    		 districterr.add("该用户所管理的区域("+falseDistrictNames+")经纬度不正确，请重新输入区域经纬度,否则无法进行点资源经纬度的校验！");
    	 } 	     
    }

    /**
     * importManhle
     * 人手井导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importManhle(Workbook workbook, String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        ArrayList districterr=new ArrayList();
        Map manhleNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Manhle dbo = new Manhle();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "人井名称", i, err);
            dbo.setAttrValue(Manhle.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Manhle.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Manhle.AttrName.ownership, ownership);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Manhle.AttrName.longitude, longitude); //实际经度
            dbo.setAttrValue(Manhle.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Manhle.AttrName.latitude, latitude); //实际纬度
            dbo.setAttrValue(Manhle.AttrName.realLatitude, latitude); //实际纬度
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Manhle.AttrName.purpose, purpose);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Manhle.AttrName.isDangerPoint, isDangerPoint);
            String wellType = ImpExpUtils.getSheetValueByLabel(sheet, "具体类型", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellType, wellType);
            String wellKind = ImpExpUtils.getSheetValueByLabel(sheet, "人手井类型", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellKind, wellKind);
            String spec = ImpExpUtils.getSheetValueByLabel(sheet, "规格", i, err);
            dbo.setAttrValue(Manhle.AttrName.spec, spec);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err);
            dbo.setAttrValue(Manhle.AttrName.model, model);
            String struct = ImpExpUtils.getSheetValueByLabel(sheet, "结构", i, err);
            dbo.setAttrValue(Manhle.AttrName.struct, struct);
            String coverStuff = ImpExpUtils.getSheetValueByLabel(sheet, "井盖材质", i, err);
            dbo.setAttrValue(Manhle.AttrName.coverStuff, coverStuff);
            String coverBulge = ImpExpUtils.getSheetValueByLabel(sheet, "井盖凸起度", i, err);
            dbo.setAttrValue(Manhle.AttrName.coverBulge, coverBulge);
            String wellHeight = ImpExpUtils.getSheetValueByLabel(sheet, "井深(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellHeight, wellHeight);
            String netHeight = ImpExpUtils.getSheetValueByLabel(sheet, "净空高(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.netHeight, netHeight);
            String upDeep = ImpExpUtils.getSheetValueByLabel(sheet, "上覆厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.upDeep, upDeep);
            String eastWestLength = ImpExpUtils.getSheetValueByLabel(sheet, "东西长(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.eastWestLength, eastWestLength);
            String northSouthLength = ImpExpUtils.getSheetValueByLabel(sheet, "南北长(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.northSouthLength, northSouthLength);
            String wallDeep = ImpExpUtils.getSheetValueByLabel(sheet, "壁厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.wallDeep, wallDeep);
            String baseDeep = ImpExpUtils.getSheetValueByLabel(sheet, "底基厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.baseDeep, baseDeep);
            String roadCenterLength = ImpExpUtils.getSheetValueByLabel(sheet, "路中心距(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.roadCenterLength, roadCenterLength);
            String roadSideLength = ImpExpUtils.getSheetValueByLabel(sheet, "路边距(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.roadSideLength, roadSideLength);
            String sustain = ImpExpUtils.getSheetValueByLabel(sheet, "井盖承重(KG)", i, err);
            dbo.setAttrValue(Manhle.AttrName.sustain, sustain);
            String coverParam = ImpExpUtils.getSheetValueByLabel(sheet, "井盖参数", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.coverParam, coverParam);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.resOwner, resOwner);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Manhle.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Manhle.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Manhle.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Manhle.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Manhle.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
               errorsAndDatas.add(ImpExpConsts._error, err);
               return errorsAndDatas;
            }         
            
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (manhleNames.get(labelCn) != null) {
                	
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  manhleNames.get(labelCn) + "行人手井名称重复！");
                } else {
                    manhleNames.put(labelCn, Integer.valueOf(i));
                }
            }
           
            dbos.add(dbo);
        }
        
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        
        if(districterr.size()!=0){
        	districterr.add(ImpExpConsts.ERROR_INFO);
        	errorsAndDatas.add(ImpExpConsts._error,districterr);
        	return errorsAndDatas;
        }
        
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importManhle(new BoActionContext(), dbos, districts,curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
     	   for (int i = 1; i < dbos.size(); i++) {
     		   String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
     		   namelist.add(name);
     	   }
        	
         	tempErrors.add("成功导入人手井数据" + (dbos.size() - 1) + "条。");
         	tempErrors.addAll(namelist);
            return tempErrors;
        } else {
        	tempErrors.add(ImpExpConsts.ERROR_INFO);        	
            return tempErrors;
        }

    }
    public DataObjectList checkManhle(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map manhleNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Manhle dbo = new Manhle();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "人井名称", i, err);
            dbo.setAttrValue(Manhle.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Manhle.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Manhle.AttrName.ownership, ownership);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Manhle.AttrName.longitude, longitude); //实际经度
            dbo.setAttrValue(Manhle.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Manhle.AttrName.latitude, latitude); //实际纬度
            dbo.setAttrValue(Manhle.AttrName.realLatitude, latitude); //实际纬度
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Manhle.AttrName.purpose, purpose);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Manhle.AttrName.isDangerPoint, isDangerPoint);
            String wellType = ImpExpUtils.getSheetValueByLabel(sheet, "具体类型", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellType, wellType);
            String wellKind = ImpExpUtils.getSheetValueByLabel(sheet, "人手井类型", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellKind, wellKind);
            String spec = ImpExpUtils.getSheetValueByLabel(sheet, "规格", i, err);
            dbo.setAttrValue(Manhle.AttrName.spec, spec);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err);
            dbo.setAttrValue(Manhle.AttrName.model, model);
            String struct = ImpExpUtils.getSheetValueByLabel(sheet, "结构", i, err);
            dbo.setAttrValue(Manhle.AttrName.struct, struct);
            String coverStuff = ImpExpUtils.getSheetValueByLabel(sheet, "井盖材质", i, err);
            dbo.setAttrValue(Manhle.AttrName.coverStuff, coverStuff);
            String coverBulge = ImpExpUtils.getSheetValueByLabel(sheet, "井盖凸起度", i, err);
            dbo.setAttrValue(Manhle.AttrName.coverBulge, coverBulge);
            String wellHeight = ImpExpUtils.getSheetValueByLabel(sheet, "井深(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.wellHeight, wellHeight);
            String netHeight = ImpExpUtils.getSheetValueByLabel(sheet, "净空高(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.netHeight, netHeight);
            String upDeep = ImpExpUtils.getSheetValueByLabel(sheet, "上覆厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.upDeep, upDeep);
            String eastWestLength = ImpExpUtils.getSheetValueByLabel(sheet, "东西长(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.eastWestLength, eastWestLength);
            String northSouthLength = ImpExpUtils.getSheetValueByLabel(sheet, "南北长(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.northSouthLength, northSouthLength);
            String wallDeep = ImpExpUtils.getSheetValueByLabel(sheet, "壁厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.wallDeep, wallDeep);
            String baseDeep = ImpExpUtils.getSheetValueByLabel(sheet, "底基厚(CM)", i, err);
            dbo.setAttrValue(Manhle.AttrName.baseDeep, baseDeep);
            String roadCenterLength = ImpExpUtils.getSheetValueByLabel(sheet, "路中心距(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.roadCenterLength, roadCenterLength);
            String roadSideLength = ImpExpUtils.getSheetValueByLabel(sheet, "路边距(M)", i, err);
            dbo.setAttrValue(Manhle.AttrName.roadSideLength, roadSideLength);
            String sustain = ImpExpUtils.getSheetValueByLabel(sheet, "井盖承重(KG)", i, err);
            dbo.setAttrValue(Manhle.AttrName.sustain, sustain);
            String coverParam = ImpExpUtils.getSheetValueByLabel(sheet, "井盖参数", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.coverParam, coverParam);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Manhle.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Manhle.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Manhle.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Manhle.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Manhle.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
//                err.add(ImpExpConsts.ERROR_INFO);
               throw new UserException(ImpExpConsts.ERROR_INFO);
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (manhleNames.get(labelCn) != null) {
                	throw new UserException("非法数据：在excel表格中，第" + i + "行与第" +
                            manhleNames.get(labelCn) + "行人手井名称重复！");
                } else {
                    manhleNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
//            errorlist.add(ImpExpConsts.ERROR_INFO);
            throw new UserException(ImpExpConsts.ERROR_INFO);
        }
        return dbos;
        //调用服务器端接口
        
    }
    public ArrayList importManhleExt(DataObjectList dbos, String curMapCuid) throws Exception{ 
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
    	ArrayList districterr=new ArrayList();
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        if(districterr.size()!=0){
        	return districterr;
        }
        
    	ArrayList errorlist = getDataImportBO().importManhle(new BoActionContext(), dbos, districts,curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        if (tempErrors.size() == 0) {
        	tempErrors.add("成功导入人手井数据" + (dbos.size() - 1) + "条。");
            return tempErrors;
        } else {
        	tempErrors.add(ImpExpConsts.ERROR_INFO);
            return tempErrors;
        }
    }

    public ArrayList importPole(Workbook workbook) throws Exception {
        return importPole(workbook, "");
    }
    
    /**
     * ODF导入: 从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook
     * @param curMapCuid
     * @return
     * @throws Exception
     */
    public ArrayList importOdf(Workbook workbook, String curMapCuid) throws Exception{
    	Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map odfNames = new HashMap();
        
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Odf dbo = new Odf();
            String siteLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "站点名称", i, err);
            dbo.setAttrValue(Odf.AttrName.relatedSiteCuid, siteLabelCn);
            String roomLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "机房名称", i, err);
            dbo.setAttrValue(Odf.AttrName.relatedRoomCuid, roomLabelCn);
            String accessScene = ImpExpUtils.getSheetValueByLabel(sheet, "集客接入场景", i, err); //ACCESS_SCENE
            dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScene);
            String relatedAccessPoint = ImpExpUtils.getSheetValueByLabel(sheet, "所属接入点", i, err);
            dbo.setAttrValue(Odf.AttrName.relatedAccessPoint, relatedAccessPoint);
            String type = ImpExpUtils.getSheetValueByLabel(sheet, "机架类型", i, err);  
            dbo.setAttrValue("RACK_TYPE", type);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "机架名称", i, err);
            dbo.setAttrValue(Odf.AttrName.labelCn, labelCn);
            String separator= ImpExpUtils.getSheetValueByLabel(sheet, "分割符", i, err);
            dbo.setAttrValue("SEPARATOR", separator);
            String portNameRule = ImpExpUtils.getSheetValueByLabel(sheet, "端子规则", i, err);
            dbo.setAttrValue("PORT_NAME_RULE", portNameRule);
            String templateName = ImpExpUtils.getSheetValueByLabel(sheet, "模板名称", i, err);
            dbo.setAttrValue("TEMPLATE_NAME", templateName);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权归属", i, err);
            dbo.setAttrValue(Odf.AttrName.ownership, ownership);
            String modNum = ImpExpUtils.getSheetValueByLabel(sheet, "模块数量", i, err);
            dbo.setAttrValue(Odf.AttrName.modNum, modNum);
            String creator = ImpExpUtils.getSheetValueByLabel(sheet, "建设单位", i, err);
            dbo.setAttrValue(Odf.AttrName.creator, creator);
            String devicePrice = ImpExpUtils.getSheetValueByLabel(sheet, "设备价格", i, err);
            dbo.setAttrValue(Odf.AttrName.devicePrice, devicePrice);
            String serviceState = ImpExpUtils.getSheetValueByLabel(sheet, "运行状态", i, err);
            dbo.setAttrValue(Odf.AttrName.serviceState, serviceState);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "长度", i, err);
            dbo.setAttrValue(Odf.AttrName.length, length); 
            String height = ImpExpUtils.getSheetValueByLabel(sheet, "高度", i, err);
            dbo.setAttrValue(Odf.AttrName.height, height); //高度
            String width = ImpExpUtils.getSheetValueByLabel(sheet, "宽度", i, err);
            dbo.setAttrValue(Odf.AttrName.width, width);
            String vendor = ImpExpUtils.getSheetValueByLabel(sheet, "设备供应商", i, err);
            dbo.setAttrValue(Odf.AttrName.vendor, vendor);
            String preserver = ImpExpUtils.getSheetValueByLabel(sheet, "维护人", i, err);
            dbo.setAttrValue(Odf.AttrName.preserver, preserver);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "用户", i, err);
            dbo.setAttrValue(Odf.AttrName.userName, userName);
            String createtime = ImpExpUtils.getSheetValueByLabel(sheet, "建设日期", i, err);
            dbo.setAttrValue(Odf.AttrName.createtime, createtime);
            String setupTime = ImpExpUtils.getSheetValueByLabel(sheet, "投产日期", i, err);
            dbo.setAttrValue(Odf.AttrName.setupTime, setupTime);
            String equipNo = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产编号", i, err);
            dbo.setAttrValue(Odf.AttrName.equipNo, equipNo);
            String designCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "设计容量", i, err);
            dbo.setAttrValue(Odf.AttrName.designCapacity, designCapacity);
            String installCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "安装容量", i, err);
            dbo.setAttrValue(Odf.AttrName.installCapacity, installCapacity);
            String freeCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "空闲容量", i, err);
            dbo.setAttrValue(Odf.AttrName.freeCapacity, freeCapacity);
            String usedCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "使用容量", i, err);
            dbo.setAttrValue(Odf.AttrName.usedCapacity, usedCapacity);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "位置", i, err);
            dbo.setAttrValue(Odf.AttrName.location, location);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err);
            dbo.setAttrValue(Odf.AttrName.model, model);            
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Odf.AttrName.remark, remark);
            //设置device_kind的默认值为1
            dbo.setAttrValue(Odf.AttrName.deviceKind, "1");
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (odfNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                    		odfNames.get(labelCn) + "行ODF名称重复！");
                } else {
                	odfNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = (ArrayList)BoCmdFactory.getInstance().execBoCmd("IEquipDataImportBO.importDdfOdf", new BoActionContext(),dbos);
       //errorlist=getEquipDataImportBO().importDdfOdf(new BoActionContext(), dbos); 
       
       if (errorlist.size() == 0) {
           errorlist.add("成功导入接入点数据" + (dbos.size() - 1) + "条。");
           return errorlist;
       } else {
           errorlist.add("导入数据失败！请您校验数据之后重新导入。");
           return errorlist;
       }

    }
    /**
     * importPole
     * 电杆导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importPole(Workbook workbook, String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
       ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        ArrayList districterr=new ArrayList();
        Map poleNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Pole dbo = new Pole();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Pole.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "杆名", i, err);
            dbo.setAttrValue(Pole.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Pole.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Pole.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Pole.AttrName.purpose, purpose);
            String high = ImpExpUtils.getSheetValueByLabel(sheet, "高度", i, err);
            dbo.setAttrValue(Pole.AttrName.high, high);
            String poleKind = ImpExpUtils.getSheetValueByLabel(sheet, "电杆类型", i, err);
            dbo.setAttrValue(Pole.AttrName.poleKind, poleKind);
            String ringDia = ImpExpUtils.getSheetValueByLabel(sheet, "抱箍规格", i, err);
            dbo.setAttrValue(Pole.AttrName.ringDia, ringDia);
            String poleType = ImpExpUtils.getSheetValueByLabel(sheet, "杆面型式", i, err);
            dbo.setAttrValue(Pole.AttrName.poleType, poleType);
            String pullLineSpec = ImpExpUtils.getSheetValueByLabel(sheet, "拉线规格", i, err);
            dbo.setAttrValue(Pole.AttrName.pullLineSpec, pullLineSpec);
            String pullLineType = ImpExpUtils.getSheetValueByLabel(sheet, "拉线形式", i, err);
            dbo.setAttrValue(Pole.AttrName.pullLineType, pullLineType);
            String hookRadii = ImpExpUtils.getSheetValueByLabel(sheet, "挂钩半径(CM)", i, err);
            dbo.setAttrValue(Pole.AttrName.hookRadii, hookRadii);
            String isMarked = ImpExpUtils.getSheetValueByLabel(sheet, "是否有吊牌", i, err);
            dbo.setAttrValue(Pole.AttrName.isMarked, isMarked);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Pole.AttrName.longitude, longitude); //实际经度
            dbo.setAttrValue(Pole.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Pole.AttrName.latitude, latitude); //实际纬度
            dbo.setAttrValue(Pole.AttrName.realLatitude, latitude); //实际纬度
            String topDia = ImpExpUtils.getSheetValueByLabel(sheet, "顶部直径(CM)", i, err);
            dbo.setAttrValue(Pole.AttrName.topDia, topDia);
            String plywoodType = ImpExpUtils.getSheetValueByLabel(sheet, "夹板类型", i, err);
            dbo.setAttrValue(Pole.AttrName.plywoodType, plywoodType);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.resOwner, resOwner);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.maintDep, maintDep);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.checkDate, checkDate);
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Pole.AttrName.isDangerPoint, isDangerPoint);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Pole.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Pole.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Pole.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Pole.AttrName.finishDate, finishDate);
            String attachDev = ImpExpUtils.getSheetValueByLabel(sheet, "附属设备", i, err);
            dbo.setAttrValue(Pole.AttrName.attachDev, attachDev);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Pole.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
               errorsAndDatas.add(ImpExpConsts._error, err);
               return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (poleNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  poleNames.get(labelCn) + "行电杆名称重复！");
                } else {
                    poleNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        
        if(districterr.size()!=0){
        	districterr.add(ImpExpConsts.ERROR_INFO);
        	errorsAndDatas.add(ImpExpConsts._error,districterr);
        	return errorsAndDatas;
        }        
        
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
           errorsAndDatas.add(ImpExpConsts._error, errorlist);
           return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importPole(new BoActionContext(), dbos,districts,curMapCuid);
       ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
       ArrayList namelist = new ArrayList();
       if (tempErrors.size() == 0) {
    	   for (int i = 1; i < dbos.size(); i++) {
    		   String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
    		   namelist.add(name);
    	   }
    	
       	tempErrors.add("成功导入电杆数据" + (dbos.size() - 1) + "条。");
       	tempErrors.addAll(namelist);
       
           return tempErrors;
       } else {
       	tempErrors.add(ImpExpConsts.ERROR_INFO);
           return tempErrors;
       }

    }
    
    public DataObjectList checkPole(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
       ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map poleNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Pole dbo = new Pole();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Pole.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "杆名", i, err);
            dbo.setAttrValue(Pole.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Pole.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Pole.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Pole.AttrName.purpose, purpose);
            String high = ImpExpUtils.getSheetValueByLabel(sheet, "高度", i, err);
            dbo.setAttrValue(Pole.AttrName.high, high);
            String poleKind = ImpExpUtils.getSheetValueByLabel(sheet, "电杆类型", i, err);
            dbo.setAttrValue(Pole.AttrName.poleKind, poleKind);
            String ringDia = ImpExpUtils.getSheetValueByLabel(sheet, "抱箍规格", i, err);
            dbo.setAttrValue(Pole.AttrName.ringDia, ringDia);
            String poleType = ImpExpUtils.getSheetValueByLabel(sheet, "杆面型式", i, err);
            dbo.setAttrValue(Pole.AttrName.poleType, poleType);
            String pullLineSpec = ImpExpUtils.getSheetValueByLabel(sheet, "拉线规格", i, err);
            dbo.setAttrValue(Pole.AttrName.pullLineSpec, pullLineSpec);
            String pullLineType = ImpExpUtils.getSheetValueByLabel(sheet, "拉线形式", i, err);
            dbo.setAttrValue(Pole.AttrName.pullLineType, pullLineType);
            String hookRadii = ImpExpUtils.getSheetValueByLabel(sheet, "挂钩半径(CM)", i, err);
            dbo.setAttrValue(Pole.AttrName.hookRadii, hookRadii);
            String isMarked = ImpExpUtils.getSheetValueByLabel(sheet, "是否有吊牌", i, err);
            dbo.setAttrValue(Pole.AttrName.isMarked, isMarked);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Pole.AttrName.longitude, longitude); //实际经度
            dbo.setAttrValue(Pole.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Pole.AttrName.latitude, latitude); //实际纬度
            dbo.setAttrValue(Pole.AttrName.realLatitude, latitude); //实际纬度
            String topDia = ImpExpUtils.getSheetValueByLabel(sheet, "顶部直径(CM)", i, err);
            dbo.setAttrValue(Pole.AttrName.topDia, topDia);
            String plywoodType = ImpExpUtils.getSheetValueByLabel(sheet, "夹板类型", i, err);
            dbo.setAttrValue(Pole.AttrName.plywoodType, plywoodType);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.maintDep, maintDep);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.checkDate, checkDate);
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Pole.AttrName.isDangerPoint, isDangerPoint);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Pole.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Pole.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Pole.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Pole.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Pole.AttrName.finishDate, finishDate);
            String attachDev = ImpExpUtils.getSheetValueByLabel(sheet, "附属设备", i, err);
            dbo.setAttrValue(Pole.AttrName.attachDev, attachDev);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Pole.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
//                err.add(ImpExpConsts.ERROR_INFO);
                throw new UserException(ImpExpConsts.ERROR_INFO);
//               errorsAndDatas.add(ImpExpConsts._error, err);
//               return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (poleNames.get(labelCn) != null) {
                	throw new UserException("非法数据：在excel表格中，第" + i + "行与第" +
                                  poleNames.get(labelCn) + "行电杆名称重复！");
                } else {
                    poleNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            throw new UserException(ImpExpConsts.ERROR_INFO);
        }
        return dbos;

    }
    
    public ArrayList importPoleExt(DataObjectList dbos, String curMapCuid) throws Exception{
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
    	ArrayList districterr=new ArrayList();
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
    	if(districterr.size()!=0){
    		return districterr;
    	}        
        ArrayList errorlist = getDataImportBO().importPole(new BoActionContext(), dbos, districts,curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        if (tempErrors.size() == 0) {
        	tempErrors.add("成功导入电杆数据" + (dbos.size() - 1) + "条。");
            return tempErrors;
        } else {
        	tempErrors.add(ImpExpConsts.ERROR_INFO);
            return tempErrors;
        }
    }

    public ArrayList importStone(Workbook workbook) throws Exception {
        return importStone(workbook, "");
    }

    /**
     * importStone
     * 标石导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importStone(Workbook workbook, String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
       ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        ArrayList districterr=new ArrayList();
        
        Map stoneNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Stone dbo = new Stone();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Stone.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "标石名称", i, err);
            dbo.setAttrValue(Stone.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Stone.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Stone.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Stone.AttrName.purpose, purpose);
            String stoneType = ImpExpUtils.getSheetValueByLabel(sheet, "标石类型", i, err);
            dbo.setAttrValue(Stone.AttrName.stoneType, stoneType);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Stone.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(Stone.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Stone.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(Stone.AttrName.realLatitude, latitude); //实际纬度
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Stone.AttrName.isDangerPoint, isDangerPoint);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.resOwner, resOwner);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Stone.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Stone.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Stone.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Stone.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Stone.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add("导入数据失败！请您修改表格之后重新导入。");
               errorsAndDatas.add(ImpExpConsts._error, err);
               return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (stoneNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + stoneNames.get(labelCn) + "行标石名称重复！");
                } else {
                    stoneNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        
      //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        if(districterr.size()!=0){
        	districterr.add(ImpExpConsts.ERROR_INFO);
        	errorsAndDatas.add(ImpExpConsts._error,districterr);
        	return errorsAndDatas;
        }        
        
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
           errorsAndDatas.add(ImpExpConsts._error, errorlist);
           return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importStone(new BoActionContext(), dbos,districts, curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
      	   for (int i = 1; i < dbos.size(); i++) {
      		   String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
      		   namelist.add(name);
      	   }
        	tempErrors.add("成功导入标石数据" + (dbos.size() - 1) + "条。");
        	tempErrors.addAll(namelist);
            return tempErrors;
        } else {
    	    tempErrors.add(ImpExpConsts.ERROR_INFO);
            return tempErrors;
        }
    }

    public DataObjectList checkStone(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
       ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map stoneNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Stone dbo = new Stone();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Stone.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "标石名称", i, err);
            dbo.setAttrValue(Stone.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Stone.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Stone.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Stone.AttrName.purpose, purpose);
            String stoneType = ImpExpUtils.getSheetValueByLabel(sheet, "具体类型", i, err);
            dbo.setAttrValue(Stone.AttrName.stoneType, stoneType);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Stone.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(Stone.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Stone.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(Stone.AttrName.realLatitude, latitude); //实际纬度
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Stone.AttrName.isDangerPoint, isDangerPoint);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Stone.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Stone.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Stone.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Stone.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Stone.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Stone.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {

            	throw new UserException(ImpExpConsts.ERROR_INFO);
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (stoneNames.get(labelCn) != null) {
                    throw new UserException("非法数据：在excel表格中，第" + i + "行与第" +
                                  stoneNames.get(labelCn) + "行标石名称重复！");
                } else {
                    stoneNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
        	throw new UserException(ImpExpConsts.ERROR_INFO);
        }
        return dbos;

    }
    public ArrayList importStoneExt(DataObjectList dbos, String curMapCuid) throws Exception{
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
    	ArrayList districterr=new ArrayList();
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
    	if(districterr.size()!=0){
    		return districterr;
    	}
    	
    	ArrayList errorlist = getDataImportBO().importStone(new BoActionContext(), dbos, districts,curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        if (tempErrors.size() == 0) {
        	tempErrors.add("成功导入标石数据" + (dbos.size() - 1) + "条。");
            return tempErrors;
        } else {
        	tempErrors.add(ImpExpConsts.ERROR_INFO);
            return tempErrors;
        }
    }

    public ArrayList importInflexion(Workbook workbook) throws Exception {
        return importInflexion(workbook, "");
    }

    /**
     * importInflexion
     * 拐点导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importInflexion(Workbook workbook, String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList districterr=new ArrayList();
        ArrayList err = new ArrayList();
        Map inflexionNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Inflexion dbo = new Inflexion();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Inflexion.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "拐点名称", i, err);
            dbo.setAttrValue(Inflexion.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Inflexion.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Inflexion.AttrName.purpose, purpose);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(Inflexion.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(Inflexion.AttrName.realLatitude, latitude); //实际纬度
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.isDangerPoint, isDangerPoint);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Inflexion.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Inflexion.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Inflexion.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Inflexion.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add("导入数据失败！请您修改表格之后重新导入。");
               errorsAndDatas.add(ImpExpConsts._error, err);
               return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (inflexionNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  inflexionNames.get(labelCn) + "行拐点名称重复！");
                } else {
                    inflexionNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        //验证用户所在的区域经纬度是否正确
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        
        if(districterr.size()!=0){
        	districterr.add(ImpExpConsts.ERROR_INFO);
        	errorsAndDatas.add(ImpExpConsts._error,districterr);
        	return errorsAndDatas;
        }        
        
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
           errorsAndDatas.add(ImpExpConsts._error, errorlist);
           return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importInflexion(new BoActionContext(), dbos, districts,curMapCuid);
       ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
       ArrayList namelist = new ArrayList();
       if (tempErrors.size() == 0) {
           	   for (int i = 1; i < dbos.size(); i++) {
           		   String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
           		   namelist.add(name);
           	   }
              	   
       	tempErrors.add("成功导入拐点数据" + (dbos.size() - 1) + "条。");
       	tempErrors.addAll(namelist); 
           return tempErrors;
       } else {
       	tempErrors.add(ImpExpConsts.ERROR_INFO);
           return tempErrors;
       }

    }

    public DataObjectList checkInflexion(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
       ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map inflexionNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Inflexion dbo = new Inflexion();
            String relatedDistrict = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, err);
            dbo.setAttrValue(Inflexion.AttrName.relatedDistrictCuid, relatedDistrict);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "拐点名称", i, err);
            dbo.setAttrValue(Inflexion.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.location, location);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Inflexion.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Inflexion.AttrName.purpose, purpose);
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(Inflexion.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(Inflexion.AttrName.realLatitude, latitude); //实际纬度
            String isDangerPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是危险点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.isDangerPoint, isDangerPoint);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Manhle.AttrName.maintMode, maintMode);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.phoneNo, phoneNo);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.checkDate, checkDate);
            String isConnPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是接头点", i, errorlist);
            dbo.setAttrValue(Inflexion.AttrName.isConnPoint, isConnPoint);
            String isKeepPoint = ImpExpUtils.getSheetValueByLabel(sheet, "是否是预留点", i, err);
            dbo.setAttrValue(Inflexion.AttrName.isKeepPoint, isKeepPoint);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Inflexion.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Inflexion.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Inflexion.AttrName.finishDate, finishDate);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Inflexion.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {

            	throw new UserException(ImpExpConsts.ERROR_INFO);
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (inflexionNames.get(labelCn) != null) {


                    throw new UserException("非法数据：在excel表格中，第" + i + "行与第" +
                                  inflexionNames.get(labelCn) + "行拐点名称重复！");
                } else {
                    inflexionNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
        	throw new UserException(ImpExpConsts.ERROR_INFO);
        }
       return dbos;
        

    }
    public ArrayList importInflexionExt(DataObjectList dbos, String curMapCuid) throws Exception{
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
    	ArrayList districterr=new ArrayList();
        HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        if(districterr.size()!=0){
        	return districterr;
        }
    	ArrayList errorlist = getDataImportBO().importInflexion(new BoActionContext(), dbos,districts, curMapCuid);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        if (tempErrors.size() == 0) {
        	tempErrors.add("成功导入拐点数据" + (dbos.size() - 1) + "条。");
            return tempErrors;
        } else {
        	tempErrors.add(ImpExpConsts.ERROR_INFO);
            return tempErrors;
        }
    }
    /**
     * importWireremain
     * 预留点导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importWireRemain(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map wireRemainNames = new HashMap();
        ArrayList wireRemainNames_clone = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            WireRemain dbo = new WireRemain();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "预留点名称", i, err);
            dbo.setAttrValue(WireRemain.AttrName.labelCn, labelCn);
            String x = ImpExpUtils.getSheetValueByLabel(sheet, "预留点设施类型", i, err);
            dbo.setAttrValue(WireRemain.AttrName.x, x); //模型中不存在“预留点设施类型”，暂时存放在x中
            String relatedLocation = ImpExpUtils.getSheetValueByLabel(sheet, "预留点设施名称", i, err);
            dbo.setAttrValue(WireRemain.AttrName.relatedLocationCuid, relatedLocation);
            String y = ImpExpUtils.getSheetValueByLabel(sheet, "所属光缆", i, err);
            dbo.setAttrValue(WireRemain.AttrName.y, y); //模型中不存在“所属光缆”，暂时放在y中
            String relatedWireSeg = ImpExpUtils.getSheetValueByLabel(sheet, "所属光缆段", i, err);
            dbo.setAttrValue(WireRemain.AttrName.relatedWireSegCuid, relatedWireSeg);
            String remainLength = ImpExpUtils.getSheetValueByLabel(sheet, "预留长度", i, err);
            dbo.setAttrValue(WireRemain.AttrName.remainLength, remainLength);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add("导入数据失败！请您修改表格之后重新导入。");
                return err;
            }
            //验证在Excel表中是否重名的对象。
//            if ((!labelCn.equals(""))) {
//                if (wireRemainNames.get(labelCn) != null) {
//                    errorlist.add("<font color = \"red\">非法数据：在excel表格中，第" + i + "行与第" +
//                                  wireRemainNames.get(labelCn) + "行预留点名称重复！</font>");
//                } else {
//                    wireRemainNames.put(labelCn, Integer.valueOf(i));
//                }
//            }
            if(!relatedLocation.equals("") && !relatedWireSeg.equals("")){
            	String cl = relatedLocation+","+relatedWireSeg;
            	if(!wireRemainNames_clone.contains(cl)){
            		wireRemainNames_clone.add(cl);
            		dbos.add(dbo);
            	}else{
            		errorlist.add("在同一个光缆段中，所敷设的同一个点设施下只能有一个预留点！请检查预留点设施名称和所属光缆段！");
            	}
            }
            
//            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importWireRemain(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入预留点数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }

    }

    public ArrayList importFiberJointBox(Workbook workbook) throws Exception {
        return importFiberJointBox(workbook, "");
    }

    /**
     * importFiberJointBox
     * 光接头盒导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importFiberJointBox(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        boolean isOverLayResource = false;
        if(workbook.getSheet("光接头盒")!=null){
        	sheet = workbook.getSheet("光接头盒");
        	isOverLayResource = true;
        }
        int rowcount = sheet.getRows();
        int errCount = 0;
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberJointBoxNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }
            
            if(isOverLayResource){
                String errorInfo = ImportChecker.checktFiberJointBox(sheet, i, err);
                if(errorInfo!=null &&!"".equals(errorInfo)){
                	err.add(errorInfo);
                	errCount++;
                	continue;
                }
            }
            FiberJointBox dbo = new FiberJointBox();
            String relatedDistrictCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err); //RELATED_DISTRICT_CUID
            dbo.setAttrValue(FiberJointBox.AttrName.relatedDistrictCuid, relatedDistrictCuid);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, err); //LABEL_CN
            dbo.setAttrValue(FiberJointBox.AttrName.labelCn, labelCn);
            String accessScene = ImpExpUtils.getSheetValueByLabel(sheet, "集客接入场景", i, err); //ACCESS_SCENE
            dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScene);
            String junctionType = ImpExpUtils.getSheetValueByLabel(sheet, "接头形式", i, err); //JUNCTION_TYPE
            dbo.setAttrValue(FiberJointBox.AttrName.junctionType, junctionType);
            String connectType = ImpExpUtils.getSheetValueByLabel(sheet, "熔接方式", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberJointBox.AttrName.connectType, connectType);
            String setupTime = ImpExpUtils.getSheetValueByLabel(sheet, "投产时间", i, err); //SETUP_TIME
            dbo.setAttrValue(FiberJointBox.AttrName.setupTime, setupTime);
            String creator = ImpExpUtils.getSheetValueByLabel(sheet, "建设单位", i, err); //CREATOR
            dbo.setAttrValue(FiberJointBox.AttrName.creator, creator);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "位置描述", i, err); //LOCATION
            dbo.setAttrValue(FiberJointBox.AttrName.location, location);
            String relatedLocationType = ImpExpUtils.getSheetValueByLabel(sheet, "所在设施类型", i, err); //无对应关键字，暂放在SYMBOL_NAME中。
            dbo.setAttrValue(FiberJointBox.AttrName.symbolName, relatedLocationType);

            String siteLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "机房所属站点", i, err); //增加机房的所属站点RELATED_SITE_CUID
            dbo.setAttrValue(FiberJointBox.AttrName.relatedSiteCuid, siteLabelCn);

            String relatedLocationCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所在设施名称", i, err); //RELATED_LOCATION_CUID
            dbo.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid, relatedLocationCuid);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err); //MODEL
            dbo.setAttrValue(FiberJointBox.AttrName.model, model);
            String relatedVendorCuid = ImpExpUtils.getSheetValueByLabel(sheet, "生产厂家", i, err); //RELATED_VENDOR_CUID
            dbo.setAttrValue(FiberJointBox.AttrName.relatedVendorCuid, relatedVendorCuid);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权归属", i, err); //OWNERSHIP
            dbo.setAttrValue(FiberJointBox.AttrName.ownership, ownership);

            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(Inflexion.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(Inflexion.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(Inflexion.AttrName.realLatitude, latitude); //实际纬度

            dbo.setAttrValue(FiberJointBox.AttrName.latitude, latitude);
            String kind = ImpExpUtils.getSheetValueByLabel(sheet, "光终端盒类型", i, err); //KIND
            dbo.setAttrValue(FiberJointBox.AttrName.kind, kind);
            String bushing = ImpExpUtils.getSheetValueByLabel(sheet, "套管类型", i, err); //BUSHING
            dbo.setAttrValue(FiberJointBox.AttrName.bushing, bushing);
            String designCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "设计容量(芯)", i, err); //DESIGN_CAPACITY
            dbo.setAttrValue(FiberJointBox.AttrName.designCapacity, designCapacity);
            String usedCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "使用容量(芯)", i, err); //USED_CAPACITY
            dbo.setAttrValue(FiberJointBox.AttrName.usedCapacity, usedCapacity);
            String installCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "安装容量(芯)", i, err); //INSTALL_CAPACITY
            dbo.setAttrValue(FiberJointBox.AttrName.installCapacity, installCapacity);
            String freeCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "空闲容量(芯)", i, err); //FREE_CAPACITY
            dbo.setAttrValue(FiberJointBox.AttrName.freeCapacity, freeCapacity);
            String capacity = ImpExpUtils.getSheetValueByLabel(sheet, "接头盒容量(芯)", i, err); //CAPACITY
            dbo.setAttrValue(FiberJointBox.AttrName.capacity, capacity);
            String labelDev = ImpExpUtils.getSheetValueByLabel(sheet, "设备标识", i, err); //LABEL_DEV
            dbo.setAttrValue(FiberJointBox.AttrName.labelDev, labelDev);
            String seqno = ImpExpUtils.getSheetValueByLabel(sheet, "设备序列号", i, err); //SEQNO
            dbo.setAttrValue(FiberJointBox.AttrName.seqno, seqno);
            String specialLabel = ImpExpUtils.getSheetValueByLabel(sheet, "厂商特征值", i, err); //SPECIAL_LABEL
            dbo.setAttrValue(FiberJointBox.AttrName.specialLabel, specialLabel);
            String username = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err); //USERNAME
            dbo.setAttrValue(FiberJointBox.AttrName.username, username);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err); //MAINT_DEP
            dbo.setAttrValue(FiberJointBox.AttrName.maintDep, maintDep);
            String preserver = ImpExpUtils.getSheetValueByLabel(sheet, "维护人", i, err); //PRESERVER
            dbo.setAttrValue(FiberJointBox.AttrName.preserver, preserver);
            String preserverPhone = ImpExpUtils.getSheetValueByLabel(sheet, "维护人联系电话", i, err); //PRESERVER_PHONE
            dbo.setAttrValue(FiberJointBox.AttrName.preserverPhone, preserverPhone);
            String preserverAddr = ImpExpUtils.getSheetValueByLabel(sheet, "维护人通信地址", i, err); //PRESERVER_ADDR
            dbo.setAttrValue(FiberJointBox.AttrName.preserverAddr, preserverAddr);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //MAINT_MODE
            dbo.setAttrValue(FiberJointBox.AttrName.maintMode, maintMode);
            String isUsageState = ImpExpUtils.getSheetValueByLabel(sheet, "是否正使用", i, err); //IS_USAGE_STATE
            dbo.setAttrValue(FiberJointBox.AttrName.isUsageState, isUsageState);
            String creattime = ImpExpUtils.getSheetValueByLabel(sheet, "建设日期", i, err); //CREATTIME
            dbo.setAttrValue(FiberJointBox.AttrName.creattime, creattime);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //REMARK
            dbo.setAttrValue(FiberJointBox.AttrName.remark, remark);
            
            String isYjr = ImpExpUtils.getSheetValueByLabel(sheet, "是否预覆盖接入点", i, err); //IS_YJR
            dbo.setAttrValue(FiberJointBox.AttrName.isYjr, isYjr);
            String bossCode = ImpExpUtils.getSheetValueByLabel(sheet, "BOSS编码", i, err); //BOSS_CODE
            dbo.setAttrValue(FiberJointBox.AttrName.bossCode, bossCode);
            String vpLableCn = ImpExpUtils.getSheetValueByLabel(sheet, "客户名称", i, err); //VP_LABEL_CN
            dbo.setAttrValue(FiberJointBox.AttrName.vpLabelCn, vpLableCn);

            if(isOverLayResource){
                String cuid = ImpExpUtils.getSheetValueByLabel(sheet, "数据库主键", i, err);
                cuidList.add(cuid);
            }
            //验证有无缺失的属性列。
            if (err.size() != 0) {
            	err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberJointBoxNames.get(labelCn) != null) {
                	errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + fiberJointBoxNames.get(labelCn) + "光接头盒名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberJointBoxNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
    	long analyzeEndTime = System.currentTimeMillis();
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
        if(districts != null && districts.size() > 0 && !districts.isEmpty()){
        	if (errorlist.size() != 0) {
                errorlist.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, errorlist);
                return errorsAndDatas;
            }
        	//调用服务器端接口
            errorlist = bo.importFiberJointBox(new BoActionContext(), dbos,districts, curMapCuid);
        	long storeEndTime = System.currentTimeMillis();
        	if(isOverLayResource){
                dmDataPlantBO.dealHistoryOverLayResource(cuidList);
        	}
//        	long markEndTime = System.currentTimeMillis();
//            errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
//            errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
//            errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
//            errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
            ArrayList namelist = new ArrayList();
            if (errorlist.size() == 0) {
          	   for (int i = 1; i < dbos.size(); i++) {
          		   String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
          		   namelist.add(name);
          	   }
          	   errorlist.add("成功导入" + (dbos.size() - 1) + "条数据。");
          	   errorlist.addAll(namelist);
            }
        }else{
        	errorlist.add("用户管理区域值为空,无法正常导入,请处理");
        }
        return errorlist;
    }
    
    public ArrayList importPOSAndFCabRelation(Workbook workbook) throws Exception {
        return importPOSAndFCabRelation(workbook, "");
    }
    
    /**
     * 分光器与光交接箱的归属关系导入
     * @Description: 
     * @param workbook
     * @param curMapCuid
     * @return
     * @throws Exception    
     * @author Gaoxf
     */
    public ArrayList importPOSAndFCabRelation(Workbook workbook,String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }

            NeToCab dbo = new NeToCab();
            String emslabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属EMS", i, err);
            dbo.setAttrValue(NeToCab.AttrName.relatedEmsCuid, emslabelCn);

            String posLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "分光器", i, err);
            dbo.setAttrValue(NeToCab.AttrName.relatedNeCuid, posLabelCn);
            String districtLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(FiberJointBox.AttrName.relatedDistrictCuid, districtLabelCn);
            String fcabLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光交箱", i, err);
            dbo.setAttrValue(NeToCab.AttrName.relatedCabCuid, fcabLabelCn);


            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importPOSAndFCabRelation(new BoActionContext(), dbos,curMapCuid);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入分光器与光交接箱的归属关系数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }

    }
    /**
     * importLongitudeLattitude
     * 经纬度数据导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importLongitudeLattitude(Workbook workbook) throws Exception {
        ArrayList suclist = new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备        
        HashSet<District> districts=checkLonAndLatByUserDistricts(err);
        if(err.size()!=0){
        	errorlist.addAll(err);
        	errorlist.add(ImpExpConsts.ERROR_INFO);
        	return errorlist;
        }
        importLongtitudeLatitude(workbook, Site.CLASS_NAME,districts, errorlist, suclist, err);
        importLongtitudeLatitude(workbook, Manhle.CLASS_NAME, districts, errorlist, suclist, err);
        importLongtitudeLatitude(workbook, Pole.CLASS_NAME, districts, errorlist, suclist, err);
        importLongtitudeLatitude(workbook, Stone.CLASS_NAME,districts,  errorlist, suclist, err);
        importLongtitudeLatitude(workbook, Inflexion.CLASS_NAME, districts, errorlist, suclist, err);
        importLongtitudeLatitude(workbook, FiberCab.CLASS_NAME,districts,  errorlist, suclist, err);
        importLongtitudeLatitude(workbook, FiberDp.CLASS_NAME,districts,  errorlist, suclist, err);
        importLongtitudeLatitude(workbook, FiberJointBox.CLASS_NAME,districts,  errorlist, suclist, err);
        if (suclist.size() == 0) {
            err.addAll(errorlist);
            errorlist = err;
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        } else {
            suclist.addAll(err);
            suclist.addAll(errorlist);
            return suclist;
        }
    }

    /**
     * importLongtitudeLatitude
     * 选择导入的经纬度数据类型
     * @param string String
     */
    private void importLongtitudeLatitude(Workbook workbook, String type,HashSet<District> districts,
                                                 ArrayList errorlist,
                                                 ArrayList suclist,
                                                 ArrayList err) throws Exception {
        GenericDO dbo = null;
        String errInfo = "";
        if (type.equals(Site.CLASS_NAME)) {
            dbo = new Site();
            errInfo = "站点";
        } else if (type.equals(Manhle.CLASS_NAME)) {
            dbo = new Manhle();
            errInfo = "人井";
        } else if (type.equals(Pole.CLASS_NAME)) {
            dbo = new Pole();
            errInfo = "电杆";
        } else if (type.equals(Stone.CLASS_NAME)) {
            dbo = new Stone();
            errInfo = "标石";
        } else if (type.equals(Inflexion.CLASS_NAME)) {
            dbo = new Inflexion();
            errInfo = "拐点";
        } else if (type.equals(FiberCab.CLASS_NAME)) {
            dbo = new FiberCab();
            errInfo = "光交接箱";
        } else if (type.equals(FiberDp.CLASS_NAME)) {
            dbo = new FiberDp();
            errInfo = "光分纤箱";
        } else if (type.equals(FiberJointBox.CLASS_NAME)) {
            dbo = new FiberJointBox();
            errInfo = "光接头盒";
        } else {}
        Sheet sheet = workbook.getSheet(type);
        if (sheet == null) {
            errorlist.add( errInfo + "经纬度数据不存在!");
        } else {
            //modify by wanghuaiting 限制修改站点信息，此需求仅限河南    begin
        	HashMap<String,String> map=new HashMap<String,String>();
        	
            map.put("queryParaClassName", "");
            map.put("querySPName", "");
        	map.put("queryParaName", "DISTRICT_ID");
            map.put("queryParaLableCN", "");
            
        	DboCollection list=getSystemParaBO().getSystemParaByPage(new BoQueryContext(), map);
        	String para_value="";
        	String cuid="";
        	if(list!=null && list.size()>0){
        		GenericDO systemParaGdo=list.getAttrField(SystemPara.CLASS_NAME, 0);            	
            	para_value=systemParaGdo.getAttrValue("PARA_VALUE").toString();
            	cuid=getDistrictBO().getCuidByLabelCn(new BoActionContext(), "河南");
        	}        	
        	
        	if("站点".equals(errInfo) && !para_value.equals("") && (para_value.equals("河南") || para_value.equals(cuid))){
        		ArrayList errTemp = new ArrayList();
        		DataObjectList dbos = getDataFromList(sheet, dbo, errTemp, errInfo);
        		if(errTemp.size()==0){
        			if(dbos.size()>0){
        				err.add( errInfo + "信息不允许在管线系统中修改！");
        			}
        		}        		
        	}
        	else if(("站点".equals(errInfo) && !para_value.equals("") && (!(para_value.equals("河南") || para_value.equals(cuid))) ) || (!"站点".equals(errInfo))){
  	        //modify by wanghuaiting end
        		 ArrayList errTemp = new ArrayList();
                 DataObjectList dbos = getDataFromList(sheet, dbo, errTemp, errInfo);
                 if (errTemp.size() == 0) {
                     if (dbos.size() > 0) {
                         ArrayList errorTemp = getDataImportBO().importLongitudeLattitude(new BoActionContext(),districts, dbos);
                         if (errorTemp.size() == 0) {
                             suclist.add("成功导入" + errInfo + "经纬度数据" + (dbos.size() - 1) + "条。<br>");
                         } else {
                             errorlist.addAll(errorTemp);
                             errorlist.add("导入" + errInfo + "经纬度数据失败！请您校验数据之后重新导入" + errInfo + "经纬度数据。");
                         }
                     }
                 } else {
                     err.addAll(errTemp);
                     err.add("导入" + errInfo + "经纬度数据失败！请您校验数据之后重新导入" + errInfo + "经纬度数据。");
                 }
       	}            
        }
    }

    public DataObjectList getDataFromList(Sheet sheet, GenericDO typeDbo,
                                                 ArrayList err, String info) throws Exception {
        DataObjectList dbos = new DataObjectList();
        Map names = new HashMap();
        for (int i = 0; i < sheet.getRows(); i++) {
        	if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) err.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            GenericDO tempDbo = new GenericDO();
            tempDbo.setClassName(typeDbo.getClassName());
            GenericDO dbo = tempDbo.createInstanceByClassName();
            if (dbo != null) {
                dbo.setAttrValue(Manhle.AttrName.labelCn, //名称
                                 ImpExpUtils.getSheetValueByLabel(sheet, info + "名称", i, err));
                dbo.setAttrValue(Manhle.AttrName.longitude, //显示经度
                                 ImpExpUtils.getSheetValueByLabel(sheet, "显示经度", i, err));
                dbo.setAttrValue(Manhle.AttrName.latitude, //显示纬度
                                 ImpExpUtils.getSheetValueByLabel(sheet, "显示纬度", i, err));
                dbo.setAttrValue(Manhle.AttrName.realLongitude, //实际经度
                                 ImpExpUtils.getSheetValueByLabel(sheet, "实际经度", i, err));
                dbo.setAttrValue(Manhle.AttrName.realLatitude, //实际纬度
                                 ImpExpUtils.getSheetValueByLabel(sheet, "实际纬度", i, err));
                dbo.setAttrValue("IMPORT_TYPE", //导入经纬度类型
                                 ImpExpUtils.getSheetValueByLabel(sheet, "导入经纬度类型", i, err));
                dbos.add(dbo);
            }
            //如果存在列名不存在的情况，则返回null，不再进行数据读取和判断。
            if (err.size() != 0) {
                return null;
            }
            //验证在Excel表中是否重名的对象。
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, info + "名称", i, err);
            if ((!labelCn.equals(""))) {
                if (names.get(labelCn) != null) {
                    err.add("非法数据：在excel表格中，第" + i + "行与第" + names.get(labelCn) + "行" + info + "名称重复！");
                } else {
                    names.put(labelCn, Integer.valueOf(i));
                }
            }
        }      
        
        return dbos;
    }

    /**
     * importWireSystem
     * 光缆系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importWireSystem(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map wireSystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            WireSystem dbo = new WireSystem();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(WireSystem.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆系统名称", i, err);
            dbo.setAttrValue(WireSystem.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(WireSystem.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(WireSystem.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(WireSystem.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(WireSystem.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(WireSystem.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(WireSystem.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(WireSystem.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(WireSystem.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(WireSystem.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(WireSystem.AttrName.abbreviation, abbreviation);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(WireSystem.AttrName.length, length);
            String belongcom = ImpExpUtils.getSheetValueByLabel(sheet, "所属分公司", i, err);
            dbo.setAttrValue(WireSystem.AttrName.belongcom, belongcom);
            String equipmentcode = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产编号", i, err);
            dbo.setAttrValue(WireSystem.AttrName.equipmentcode, equipmentcode);
            String equipmentkindid = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产分类编码", i, err);
            dbo.setAttrValue(WireSystem.AttrName.equipmentkindid, equipmentkindid);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(WireSystem.AttrName.remark, remark);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(WireSystem.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (wireSystemNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + wireSystemNames.get(labelCn) + "行光缆系统名称重复！");
                } else {
                    wireSystemNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importWireSystem(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if(tempErrors.size()==0){
            for (int i = 1; i < dbos.size(); i++) {
            	String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
            	namelist.add(name);
            }
            tempErrors.add("成功导入光缆系统数据" + (dbos.size() - 1) + "条。");
            tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }

    /**
     * importWireSeg
     *  add by libo 2008.8.18
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importWireSeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            WireSeg dbo = new WireSeg();

            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆系统名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.relatedSystemCuid, systemlabelCn);

            String seglabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.cuid, seglabelCn);

            String typeA = ImpExpUtils.getSheetValueByLabel(sheet, "起始点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segorigPointType, typeA);

            String nameA = ImpExpUtils.getSheetValueByLabel(sheet, "起始点设施名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.origPointCuid, nameA);

//            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "起始点是否为显示点", i, err);
//            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);

            String typeZ = ImpExpUtils.getSheetValueByLabel(sheet, "终止点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segdestPointType, typeZ);

            String nameZ = ImpExpUtils.getSheetValueByLabel(sheet, "终止点设施", i, err);
            dbo.setAttrValue(WireSeg.AttrName.destPointCuid, nameZ);

//            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "终止点是否为显示点", i, err);
//            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);

            String layType = ImpExpUtils.getSheetValueByLabel(sheet, "敷设方式", i, err);
            dbo.setAttrValue(WireSeg.AttrName.layType, layType);

            String vendor = ImpExpUtils.getSheetValueByLabel(sheet, "生产厂家", i, err);
            dbo.setAttrValue(WireSeg.AttrName.vendor, vendor);

            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(WireSeg.AttrName.builder, builder);

            String btime = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(WireSeg.AttrName.buildDate, btime);

            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(WireSeg.AttrName.finishDate, finishDate);

            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
            dbo.setAttrValue(WireSeg.AttrName.checkDate, checkDate);

            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(WireSeg.AttrName.maintDep, maintDep);

            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
            dbo.setAttrValue(WireSeg.AttrName.resOwner, resOwner);

            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(WireSeg.AttrName.userName, userName);

            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(WireSeg.AttrName.remark, remark);

            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(WireSeg.AttrName.maintMode, maintMode);
            
            String networkType = ImpExpUtils.getSheetValueByLabel(sheet, "网络特性", i, err);
            dbo.setAttrValue("NETWORK_TYPE", networkType);
            
            String checkMode = ImpExpUtils.getSheetValueByLabel(sheet, "巡检方式", i, err);
            dbo.setAttrValue("CHECK_MODE", checkMode);
            
            String geoEnv = ImpExpUtils.getSheetValueByLabel(sheet, "地理环境", i, err);
            dbo.setAttrValue("GEO_ENV", geoEnv);

            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
            dbo.setAttrValue(WireSeg.AttrName.servicer, servicer);

            String phone = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
            dbo.setAttrValue(WireSeg.AttrName.phoneNo, phone);

            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(WireSeg.AttrName.purpose, purpose);

            String length = ImpExpUtils.getSheetValueByLabel(sheet, "皮长", i, err);
            dbo.setAttrValue(WireSeg.AttrName.length, length);

            String ownship = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(WireSeg.AttrName.ownership, ownship);

            String produceDate = ImpExpUtils.getSheetValueByLabel(sheet, "投产日期", i, err);
            dbo.setAttrValue(WireSeg.AttrName.produceDate, produceDate);

            String stuff = ImpExpUtils.getSheetValueByLabel(sheet, "材料", i, err);
            dbo.setAttrValue(WireSeg.AttrName.stuff, stuff);

            String fiberCount = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯数目", i, err);
            dbo.setAttrValue(WireSeg.AttrName.fiberCount, fiberCount);

            String templatename = ImpExpUtils.getSheetValueByLabel(sheet, "光缆模板名称", i, err);
            dbo.setAttrValue("TEMPLATE_NAME", templatename);

            //增加专线用途和重要性
            String specialPurpose = ImpExpUtils.getSheetValueByLabel(sheet, "专线用途", i, err);
            dbo.setAttrValue(WireSeg.AttrName.specialPurpose, specialPurpose);
            
            String olevel = ImpExpUtils.getSheetValueByLabel(sheet, "重要性", i, err);
            dbo.setAttrValue(WireSeg.AttrName.olevel, olevel);
            
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            //验证在Excel表中是否重名的对象。
//            if ((!labelCn.equals(""))) {
//                if (stonewaySystemNames.get(labelCn) != null) {
//                    errorlist.add("<font color = \"red\">非法数据：在excel表格中，第" + i + "行与第" +
//                                  stonewaySystemNames.get(labelCn) + "行标石路由系统名称重复！</font>");
//                } else {
//                    stonewaySystemNames.put(labelCn, Integer.valueOf(i));
//                }
//            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importWireSeg(new BoActionContext(), dbos);
        ArrayList namelist = new ArrayList();
        if (errorlist.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.cuid);
            	namelist.add(name);
            }
			if(namelist!=null && namelist.size()>0){
				DataObjectList segs = new DataObjectList();
				DataObjectList segDbos = queryWireSegbyName(namelist,WireSeg.CLASS_NAME);
            	if(segDbos!=null && segDbos.size()>0){
            		HashMap<String, DataObjectList> queryFiber = queryFiber(segDbos);
            		for(GenericDO dbo : segDbos){
            			String cuid = dbo.getCuid();
            			String labelCn = dbo.getAttrString(WireSeg.AttrName.labelCn);
            			DataObjectList fibers = queryFiber.get(cuid);
            			if(fibers!=null && fibers.size()>0){
            				errorlist.add(labelCn+"光缆段下已存在纤芯，不能重复导入");
            				return errorlist;
            			}else{
            				createFiber(dbo);
            			}
            		}
            	}
		}
        	errorlist.add("成功导入光缆段数据" + (dbos.size() - 1) + "条。");
        	errorlist.addAll(namelist);
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }


    /**
     * WireFiberImporter  光缆纤芯排列图谱按照模板导入
     *  add by libo 2008.8.19
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importWireFiber(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map stonewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            WireSeg dbo = new WireSeg();

            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.relatedSystemCuid, systemlabelCn);

            String seglabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.cuid, seglabelCn);

            String templatename = ImpExpUtils.getSheetValueByLabel(sheet, "光缆模版名称", i, err);
            dbo.setAttrValue("TEMPLATE_NAME", templatename);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importWireFiber(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入光缆纤芯排列图谱按照模板导入数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }


    /**
     * importDuctHole  管孔按照模板导入
     *  add by libo 2008.8.18
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importDuctHole(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map stonewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            DuctSeg dbo = new DuctSeg();

            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.relatedSystemCuid, systemlabelCn);

            String brname = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.relatedBranchCuid, brname);

            String segname = ImpExpUtils.getSheetValueByLabel(sheet, "所属管道段名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.cuid, segname);

            String tempname = ImpExpUtils.getSheetValueByLabel(sheet, "管孔模板名称", i, err);
            dbo.setAttrValue("TEMPLATE_NAME", tempname);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importDuctHole(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入管孔按照模版导入" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }


    /**
     * importWireToDuctline  光缆段的敷设信息
     *  add by libo 2008.8.18
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importWireToDuctline(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            WireToDuctline dbo = new WireToDuctline();
            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆系统名称", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.wireSystemCuid, systemlabelCn);
            String typeA = ImpExpUtils.getSheetValueByLabel(sheet, "起始点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, typeA);
            String nameA = ImpExpUtils.getSheetValueByLabel(sheet, "起始点设施名称", i, err);
            dbo.setAttrValue(WireSeg.AttrName.origPointCuid, nameA);
            String typeZ = ImpExpUtils.getSheetValueByLabel(sheet, "终止点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, typeZ);
            String nameZ = ImpExpUtils.getSheetValueByLabel(sheet, "终止点设施", i, err);
            dbo.setAttrValue(WireSeg.AttrName.destPointCuid, nameZ);
            String layductType = ImpExpUtils.getSheetValueByLabel(sheet, "敷设光缆的管线段类型", i, err);
            dbo.setAttrValue(ImpExpConsts.layDmType, layductType);
            String layductLabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "敷设光缆的管线系统名称", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.lineSystemCuid, layductLabelcn);
            String laySegorigType = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的起始点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.laySegorigType, laySegorigType);
            String laySegoriglabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的起始点设施名称", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.disPointCuid, laySegoriglabelcn);
            String laySegdestType = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的终止点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.laySegdestType, laySegdestType);
            String laySegdestlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的终止点设施名称", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.endPointCuid, laySegdestlabelcn);
//            String origPointType = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的A端设施类型", i, err);
//            dbo.setAttrValue(ImpExpConsts.segorigPointType, origPointType);
//            String alabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "敷设信息管线段的A端设施名称", i, err);
//            dbo.setAttrValue(WireToDuctline.AttrName.direction, alabelcn);//A点用来判断光缆敷设方向，故暂存在此字段。
            String indexInRoute = ImpExpUtils.getSheetValueByLabel(sheet, "管线段在光缆段敷设路由中的序号", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.indexInRoute, indexInRoute);
            String holeNo = ImpExpUtils.getSheetValueByLabel(sheet, "管孔编号", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.holeCuid, holeNo);
            String childHoleNo = ImpExpUtils.getSheetValueByLabel(sheet, "子孔编号", i, err);
            dbo.setAttrValue(WireToDuctline.AttrName.childHoleCuid, childHoleNo);
            String CarryingCableNo = ImpExpUtils.getSheetValueByLabel(sheet, "吊线编号", i, err);
            dbo.setAttrValue(ImpExpConsts.CarryingCableNo, CarryingCableNo);
            
            if(i != 0){
            //验证系统中是否已经存在相同的敷设。
	            String sql = " " + WireToDuctline.AttrName.wireSystemCuid
	                       + " in (select " + WireSystem.AttrName.cuid
	                       + " from " + WireSystem.CLASS_NAME + " where "
	                       + WireSystem.AttrName.labelCn + "='" + systemlabelCn
	                       + "') and (" + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + DuctSystem.AttrName.cuid
	                       + " from " + DuctSystem.CLASS_NAME + " where "
	                       + DuctSystem.AttrName.labelCn + "='" + layductLabelcn
	                       + "') or " + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + PolewaySystem.AttrName.cuid + " from "
	                       + PolewaySystem.CLASS_NAME + " where " + PolewaySystem.AttrName.labelCn
	                       + "='" + layductLabelcn + "') or " + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + StonewaySystem.AttrName.cuid
	                       + " from " + StonewaySystem.CLASS_NAME + " where "
	                       + StonewaySystem.AttrName.labelCn + "='" + layductLabelcn
	                       + "') or " + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + HangWall.AttrName.cuid + " from "
	                       + HangWall.CLASS_NAME + " where " + HangWall.AttrName.labelCn
	                       + "='" + layductLabelcn + "') or " + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + UpLine.AttrName.cuid + " from " + UpLine.CLASS_NAME
	                       + " where " + UpLine.AttrName.labelCn + "='" + layductLabelcn
	                       + "') or " + WireToDuctline.AttrName.lineSystemCuid
	                       + " in (select " + MicrowaveSystem.AttrName.cuid
	                       + " from " + MicrowaveSystem.CLASS_NAME + " where "
	                       + MicrowaveSystem.AttrName.labelCn + "='" + layductLabelcn
	                       + "')) and (" + WireToDuctline.AttrName.disPointCuid
	                       + " in (select " + Manhle.AttrName.cuid + " from "
	                       + Manhle.CLASS_NAME + " where " + Manhle.AttrName.labelCn
	                       + "='" + laySegoriglabelcn + "') or " + WireToDuctline.AttrName.disPointCuid
	                       + " in (select " + Pole.AttrName.cuid + " from " + Pole.CLASS_NAME
	                       + " where " + Pole.AttrName.labelCn + "='" + laySegoriglabelcn
	                       + "') or " + WireToDuctline.AttrName.disPointCuid
	                       + " in (select " + Stone.AttrName.cuid + " from " + Stone.CLASS_NAME
	                       + " where " + Stone.AttrName.labelCn + "='" + laySegoriglabelcn
	                       + "') or " + WireToDuctline.AttrName.disPointCuid
	                       + " in (select " + Inflexion.AttrName.cuid + " from "
	                       + Inflexion.CLASS_NAME + " where " + Inflexion.AttrName.labelCn
	                       + "='" + laySegoriglabelcn + "') or " + WireToDuctline.AttrName.disPointCuid
	                       + " in (select " + Site.AttrName.cuid + " from " + Site.CLASS_NAME
	                       + " where " + Site.AttrName.labelCn + "='" + laySegoriglabelcn
	                       + "')) and (" + WireToDuctline.AttrName.endPointCuid
	                       + " in (select " + Manhle.AttrName.cuid + " from "
	                       + Manhle.CLASS_NAME + " where " + Manhle.AttrName.labelCn
	                       + "='" + laySegdestlabelcn + "') or " + WireToDuctline.AttrName.endPointCuid
	                       + " in (select " + Pole.AttrName.cuid + " from " + Pole.CLASS_NAME
	                       + " where " + Pole.AttrName.labelCn + "='" + laySegdestlabelcn
	                       + "') or " + WireToDuctline.AttrName.endPointCuid
	                       + " in (select " + Stone.AttrName.cuid + " from "
	                       + Stone.CLASS_NAME + " where " + Stone.AttrName.labelCn
	                       + "='" + laySegdestlabelcn + "') or " + WireToDuctline.AttrName.endPointCuid
	                       + " in (select " + Inflexion.AttrName.cuid + " from "
	                       + Inflexion.CLASS_NAME + " where " + Inflexion.AttrName.labelCn
	                       + "='" + laySegdestlabelcn + "') or " + WireToDuctline.AttrName.endPointCuid
	                       + " in (select " + Site.AttrName.cuid + " from " + Site.CLASS_NAME
	                       + " where " + Site.AttrName.labelCn + "='" + laySegdestlabelcn + "'))";
	            
	            DataObjectList list = new DataObjectList();
	            try{
	            	list = getDuctManagerBO().getObjectsBySql(sql, new WireToDuctline());
	            }catch(Throwable e){
	            	err.add("根据sql取敷设值时发现错误，请检查数据");
	            	LogHome.getLog().error("根据sql取敷设值时发现错误，请检查数据",e);
	            }
	            if(list != null && list.size() > 0) {
	            	errorlist.add("已经做过了相同的敷设！");
	            	return errorlist;
	            }

            }
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }

            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importWireToDuctline(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入光缆段的敷设信息数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }


    private IDuctManagerBO getDuctManagerBO() {
        return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
    }
    
    /**
     * importFiber
     * add by libo 2008.8.13
     * 纤芯导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importFiber(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Fiber dbo = new Fiber();
            String wiresyslabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆系统名称", i, err);
            dbo.setAttrValue(Fiber.AttrName.relatedSystemCuid, wiresyslabelcn);
            String wireseglabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段名称", i, err);
            dbo.setAttrValue(Fiber.AttrName.relatedSegCuid, wireseglabelCn);
            String fiberno = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯编号", i, err);
            dbo.setAttrValue(Fiber.AttrName.wireNo, fiberno);
            String origdepsite = ImpExpUtils.getSheetValueByLabel(sheet, "起始设备所属站点", i, err);
            dbo.setAttrValue(Fiber.AttrName.origSiteCuid, origdepsite);
            String origdeproom = ImpExpUtils.getSheetValueByLabel(sheet, "起始设备所属机房", i, err);
            dbo.setAttrValue(ImpExpConsts.origdeproom, origdeproom);
            String origdeptype = ImpExpUtils.getSheetValueByLabel(sheet, "起始设备类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, origdeptype);
            String origeqp = ImpExpUtils.getSheetValueByLabel(sheet, "起始连接设备", i, err);
            dbo.setAttrValue(Fiber.AttrName.origEqpCuid, origeqp);
            String origdepmodel = ImpExpUtils.getSheetValueByLabel(sheet, "起始连接设备模块名称", i, err);
            dbo.setAttrValue(ImpExpConsts.origdepmodel, origdepmodel);
            String origpoint = ImpExpUtils.getSheetValueByLabel(sheet, "起始点端子", i, err);
            dbo.setAttrValue(Fiber.AttrName.origPointCuid, origpoint);
            String destdepsite = ImpExpUtils.getSheetValueByLabel(sheet, "终止设备所属站点", i, err);
            dbo.setAttrValue(Fiber.AttrName.destSiteCuid, destdepsite);
            String destdeproom = ImpExpUtils.getSheetValueByLabel(sheet, "终止设备所属机房", i, err);
            dbo.setAttrValue(ImpExpConsts.destdeproom, destdeproom);
            String destdeptype = ImpExpUtils.getSheetValueByLabel(sheet, "终止设备类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, destdeptype);
            String destEqpCuid = ImpExpUtils.getSheetValueByLabel(sheet, "终止连接设备", i, err);
            dbo.setAttrValue(Fiber.AttrName.destEqpCuid, destEqpCuid);
            String destdepmodel = ImpExpUtils.getSheetValueByLabel(sheet, "终止设备模块名称", i, err);
            dbo.setAttrValue(ImpExpConsts.destdepmodel, destdepmodel);
            String destPointCuid = ImpExpUtils.getSheetValueByLabel(sheet, "终止设备端子", i, err);
            dbo.setAttrValue(Fiber.AttrName.destPointCuid, destPointCuid);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(Fiber.AttrName.ownership, ownership);
            String usageState = ImpExpUtils.getSheetValueByLabel(sheet, "使用状态", i, err);
            dbo.setAttrValue(Fiber.AttrName.usageState, usageState);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Fiber.AttrName.purpose, purpose);
            String signalDirection = ImpExpUtils.getSheetValueByLabel(sheet, "信号方向", i, err);
            dbo.setAttrValue(Fiber.AttrName.signalDirection, signalDirection);
            String sumAttenu1310 = ImpExpUtils.getSheetValueByLabel(sheet, "1310总纤芯衰耗值", i, err);
            dbo.setAttrValue(Fiber.AttrName.sumAttenu1310, sumAttenu1310);
            String sumAttenu1550 = ImpExpUtils.getSheetValueByLabel(sheet, "1550总纤芯衰耗值", i, err);
            dbo.setAttrValue(Fiber.AttrName.sumAttenu1550, sumAttenu1550);
            String aveAttenu1310 = ImpExpUtils.getSheetValueByLabel(sheet, "1310平均衰耗值", i, err);
            dbo.setAttrValue(Fiber.AttrName.aveAttenu1310, aveAttenu1310);
            String aveAttenu1550 = ImpExpUtils.getSheetValueByLabel(sheet, "1550平均衰耗值", i, err);
            dbo.setAttrValue(Fiber.AttrName.aveAttenu1550, aveAttenu1550);
            String fiberLevel = ImpExpUtils.getSheetValueByLabel(sheet, "级别", i, err);
            dbo.setAttrValue(Fiber.AttrName.fiberLevel, fiberLevel);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "长度(M)", i, err);
            dbo.setAttrValue(Fiber.AttrName.length, length);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(Fiber.AttrName.builder, builder);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(Fiber.AttrName.buildDate, buildDate);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(Fiber.AttrName.finishDate, finishDate);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
            dbo.setAttrValue(Fiber.AttrName.checkDate, checkDate);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(Fiber.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
            dbo.setAttrValue(Fiber.AttrName.servicer, servicer);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
            dbo.setAttrValue(Fiber.AttrName.phoneNo, projectNo);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(Fiber.AttrName.userName, userName);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
            dbo.setAttrValue(Fiber.AttrName.resOwner, resOwner);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(Fiber.AttrName.remark, remark);
            String spectrum = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯色散", i, err);
            dbo.setAttrValue(Fiber.AttrName.spectrum, spectrum);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(Fiber.AttrName.maintMode, maintMode);
            String fiberColor = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯颜色", i, err);
            dbo.setAttrValue(Fiber.AttrName.fiberColor, fiberColor);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口 importFiber
        errorlist = getDataImportBO().importFiber(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入纤芯数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }

    }

    /**
     * importInterWireAndFiber
     * 中继光缆和线芯导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importInterWireAndFiber(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet("中继光缆");
        ArrayList errorlist = new ArrayList();
        if (sheet != null) {
            int rowcount = sheet.getRows();
            DataObjectList dbos = new DataObjectList();
            ArrayList err = new ArrayList();
            Map interWireNames = new HashMap();
            for (int i = 0; i < rowcount; i++) {
                if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                    if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                    break;
                }
                InterWire dbo = new InterWire();
                String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "站点", i, err);
                dbo.setAttrValue(InterWire.AttrName.relatedSiteCuid, siteName);
                String wireName = ImpExpUtils.getSheetValueByLabel(sheet, "中继光缆名称", i, err);
                dbo.setAttrValue(InterWire.AttrName.labelCn, wireName);
                String origRoomName = ImpExpUtils.getSheetValueByLabel(sheet, "A端机房", i, err);
                dbo.setAttrValue(InterWire.AttrName.origPointCuid, origRoomName);
                String destRoomName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端机房", i, err);
                dbo.setAttrValue(InterWire.AttrName.destPointCuid, destRoomName);

                //验证在Excel表中是否重名的对象。
                if ((!wireName.equals(""))) {
                    if (interWireNames.get(wireName) != null) {
                        errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + interWireNames.get(wireName) + "行中继光缆名称重复！");
                    } else {
                        interWireNames.put(wireName, Integer.valueOf(i));
                    }
                }
                dbos.add(dbo);
            }
            //调用服务器端接口
            errorlist = getDataImportBO().importInterWire(new BoActionContext(), dbos);
            List lresult = null;
            try {
                lresult = importInterFiber(workbook); //导入中继纤芯 lresult
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (lresult != null && lresult.size() > 0) {
                for (int i = 0; i < lresult.size(); i++) {
                    errorlist.add(lresult.get(i));
                }
            }
        } else {
            errorlist.add("Excel导入表格中不存在标签名称为'中继光缆'的页!");
        }
        return errorlist;
    }

    /**
     * importInterFiber
     * 中继线芯导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importInterFiber(Workbook workbook) throws Exception {
        Sheet sheet = (Sheet) workbook.getSheet("中继光缆纤芯");
        ArrayList errorlist = new ArrayList();
        if (sheet != null) {
            int rowcount = sheet.getRows();
            DataObjectList dbos = new DataObjectList();
            ArrayList err = new ArrayList();
            for (int i = 0; i < rowcount; i++) {
                if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                    if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                    break;
                }
                Fiber dbo = new Fiber();
                String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "站点", i, err);
                dbo.setAttrValue(InterWire.AttrName.relatedSiteCuid, siteName);
                String wireName = ImpExpUtils.getSheetValueByLabel(sheet, "中继光缆名称", i, err);
                dbo.setAttrValue(InterWire.AttrName.labelCn, wireName);
                String linenumber = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯编号", i, err);
                dbo.setAttrValue(Fiber.AttrName.wireNo, linenumber);
                String aRoomName = ImpExpUtils.getSheetValueByLabel(sheet, "A端ODF端子所属机房", i, err);
                dbo.setAttrValue("AROOM", aRoomName);
                String a1XdfName = ImpExpUtils.getSheetValueByLabel(sheet, "A端的ODF机架名称", i, err);
                dbo.setAttrValue(Fiber.AttrName.origEqpCuid, a1XdfName);
                String a1ModuleName = ImpExpUtils.getSheetValueByLabel(sheet, "A端的ODF模块名称", i, err);
                dbo.setAttrValue("AMODEL", a1ModuleName);
                String a1Rows = ImpExpUtils.getSheetValueByLabel(sheet, "A端的端子在模块中行号", i, err);
                dbo.setAttrValue("AROW", a1Rows);
                String a1Cols = ImpExpUtils.getSheetValueByLabel(sheet, "A端的端子在模块中列号", i, err);
                dbo.setAttrValue("ACOL", a1Cols);
                String zRoomName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端ODF端子所属机房", i, err);
                dbo.setAttrValue("ZROOM", zRoomName);
                String z1XdfName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端的ODF机架名称", i, err);
                dbo.setAttrValue(Fiber.AttrName.destEqpCuid, z1XdfName);
                String z1ModuleName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端的ODF模块名称", i, err);
                dbo.setAttrValue("ZMODEL", z1ModuleName);
                String z1Rows = ImpExpUtils.getSheetValueByLabel(sheet, "Z端的端子在模块中行号", i, err);
                dbo.setAttrValue("ZROW", z1Rows);
                String z1Cols = ImpExpUtils.getSheetValueByLabel(sheet, "Z端的端子在模块中列号", i, err);
                dbo.setAttrValue("ZCOL", z1Cols);
                String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
                dbo.setAttrValue(Fiber.AttrName.ownership, ownership);
                String usageState = ImpExpUtils.getSheetValueByLabel(sheet, "使用状态", i, err);
                dbo.setAttrValue(Fiber.AttrName.usageState, usageState);
                String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
                dbo.setAttrValue(Fiber.AttrName.purpose, purpose);
                String signalDirection = ImpExpUtils.getSheetValueByLabel(sheet, "信号方向", i, err);
                dbo.setAttrValue(Fiber.AttrName.signalDirection, signalDirection);
                String sumAttenu1310 = ImpExpUtils.getSheetValueByLabel(sheet, "1310总纤芯衰耗值", i, err);
                dbo.setAttrValue(Fiber.AttrName.sumAttenu1310, sumAttenu1310);
                String sumAttenu1550 = ImpExpUtils.getSheetValueByLabel(sheet, "1550总纤芯衰耗值", i, err);
                dbo.setAttrValue(Fiber.AttrName.sumAttenu1550, sumAttenu1550);
                String aveAttenu1310 = ImpExpUtils.getSheetValueByLabel(sheet, "1310平均衰耗值", i, err);
                dbo.setAttrValue(Fiber.AttrName.aveAttenu1310, aveAttenu1310);
                String aveAttenu1550 = ImpExpUtils.getSheetValueByLabel(sheet, "1550平均衰耗值", i, err);
                dbo.setAttrValue(Fiber.AttrName.aveAttenu1550, aveAttenu1550);
                String fiberLevel = ImpExpUtils.getSheetValueByLabel(sheet, "级别", i, err);
                dbo.setAttrValue(Fiber.AttrName.fiberLevel, fiberLevel);
                String length = ImpExpUtils.getSheetValueByLabel(sheet, "长度(M)", i, err);
                dbo.setAttrValue(Fiber.AttrName.length, length);
                String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
                dbo.setAttrValue(Fiber.AttrName.builder, builder);
                String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
                dbo.setAttrValue(Fiber.AttrName.buildDate, buildDate);
                String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
                dbo.setAttrValue(Fiber.AttrName.finishDate, finishDate);
                String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
                dbo.setAttrValue(Fiber.AttrName.checkDate, checkDate);
                String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
                dbo.setAttrValue(Fiber.AttrName.maintDep, maintDep);
                String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
                dbo.setAttrValue(Fiber.AttrName.servicer, servicer);
                String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
                dbo.setAttrValue(Fiber.AttrName.phoneNo, phoneNo);
                String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
                dbo.setAttrValue(Fiber.AttrName.userName, userName);
                String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
                dbo.setAttrValue(Fiber.AttrName.resOwner, resOwner);
                String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
                dbo.setAttrValue(Fiber.AttrName.remark, remark);
                String spectrum = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯色散", i, err);
                dbo.setAttrValue(Fiber.AttrName.spectrum, spectrum);
                String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
                dbo.setAttrValue(Fiber.AttrName.maintMode, maintMode);
                String fiberColor = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯颜色", i, err);
                dbo.setAttrValue(Fiber.AttrName.fiberColor, fiberColor);

                dbos.add(dbo);
            }
            //调用服务器端接口
            errorlist = getDataImportBO().importInterFiber(new BoActionContext(), dbos);
        } else {
            errorlist.add("Excel导入表格中不存在标签名称为'中继光缆纤芯'的页!");
        }
        return errorlist;
    }


    /**
     * importDuctSystem
     * 管道系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importDuctSystem(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map ductSystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            DuctSystem dbo = new DuctSystem();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "管道系统名称", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.abbreviation, abbreviation);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.length, length);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.remark, remark);
            String belongcom = ImpExpUtils.getSheetValueByLabel(sheet, "所属分公司", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.belongcom, belongcom);
            String equipmentcode = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产编号", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.equipmentcode, equipmentcode);
            String equipmentkindid = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产分类编码", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.equipmentkindid, equipmentkindid);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.buildDate, buildDate);
            String isSlotDuct = ImpExpUtils.getSheetValueByLabel(sheet, "是否槽道", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.isSlotDuct, isSlotDuct);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(DuctSystem.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (ductSystemNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  ductSystemNames.get(labelCn) + "行管道系统名称重复！");
                } else {
                    ductSystemNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importDuctSystem(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
        		namelist.add(name);
        	}
            tempErrors.add("成功导入管道系统数据" + (dbos.size() - 1) + "条。");
         	tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }


    /**
     * add by libo 2008.8.18
     * importDuctSeg
     * 管道段导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importDuctSeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map stonewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }

            DuctSeg dbo = new DuctSeg();
            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.relatedSystemCuid, systemlabelCn);

            if (i > 0) {
                String oldsystemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i - 1, err);
                dbo.setAttrValue("OLD_SYSTEM_LABLECN", oldsystemlabelCn);

                String oldoriglabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点名称", i - 1, err);
                dbo.setAttrValue("OLD_ORIG_LABELCN", oldoriglabelcn);

                String olddestlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点名称", i - 1, err);
                dbo.setAttrValue("OLD_DEST_LABELCN", olddestlabelcn);

                String oldtypeZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i - 1, err);
                dbo.setAttrValue("OLD_TYPE_Z", oldtypeZ);

                String oldnameZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i - 1, err);
                dbo.setAttrValue("OLD_NAME_Z", oldnameZ);
            }

            String origtype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, origtype);
            String origlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点名称", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointcuid, origlabelcn);
            String desttype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, desttype);
            String destlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点名称", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointcuid, destlabelcn);
            String typeA = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segorigPointType, typeA);
            String nameA = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.origPointCuid, nameA);
            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "A点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);
            String typeZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segdestPointType, typeZ);
            String nameZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.destPointCuid, nameZ);
            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "Z点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);
            String len = ImpExpUtils.getSheetValueByLabel(sheet, "距离", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.length, len);
            String stuff = ImpExpUtils.getSheetValueByLabel(sheet, "管孔材料", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.stuff, stuff);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.builder, builder);
            
            String multiBuilderUser = ImpExpUtils.getSheetValueByLabel(sheet, "共建单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.multiBuildUser, multiBuilderUser);
            String sharedUser = ImpExpUtils.getSheetValueByLabel(sheet, "共享单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.sharedUser, sharedUser);
            
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.finishDate, finishDate);
            String spec = ImpExpUtils.getSheetValueByLabel(sheet, "规格", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.spec, spec);
            String ownship = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.ownership, ownship);
            String sectionh = ImpExpUtils.getSheetValueByLabel(sheet, "截面高", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.sectionHeight, sectionh);
            String sectionl = ImpExpUtils.getSheetValueByLabel(sheet, "截面长", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.sectionLength, sectionl);
            String toplenA = ImpExpUtils.getSheetValueByLabel(sheet, "管顶高1", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.topHelghtA, toplenA);
            String toplenB = ImpExpUtils.getSheetValueByLabel(sheet, "管顶高2", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.topHelghtB, toplenB);
            String leftlenA = ImpExpUtils.getSheetValueByLabel(sheet, "左侧距1", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.leftDistanceA, leftlenA);
            String leftlenB = ImpExpUtils.getSheetValueByLabel(sheet, "左侧距2", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.leftDistanceB, leftlenB);

            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.maintMode, maintMode);

            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.purpose, purpose);

            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.buildDate, buildDate);

            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.resOwner, resOwner);

            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.userName, userName);

            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.maintDep, maintDep);

            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.checkDate, checkDate);

            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.servicer, servicer);

            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.phoneNo, phoneNo);

            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.remark, remark);

            String templatename = ImpExpUtils.getSheetValueByLabel(sheet, "管孔模板名称", i, err);
            dbo.setAttrValue("TEMPLATE_NAME", templatename);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importDuctSeg(new BoActionContext(), dbos);
        ArrayList namelist = new ArrayList();
        if (errorlist.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String nameA = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
    	    	String nameZ = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
    	    	String name = nameA+"--"+nameZ;
    	    	namelist.add(name);
    	    }
            errorlist.add("成功导入管道段数据" + (dbos.size() - 1) + "条。");
            errorlist.addAll(namelist); 
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }

    /**
     * importPolewaySystem
     * 杆路系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importPolewaySystem(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map polewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            PolewaySystem dbo = new PolewaySystem();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "杆路系统名称", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.abbreviation, abbreviation);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.length, length);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.remark, remark);
            String belongcom = ImpExpUtils.getSheetValueByLabel(sheet, "所属分公司", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.belongcom, belongcom);
            String equipmentcode = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产编号", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.equipmentcode, equipmentcode);
            String equipmentkindid = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产分类编码", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.equipmentkindid, equipmentkindid);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.buildDate, buildDate);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(PolewaySystem.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (polewaySystemNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  polewaySystemNames.get(labelCn) + "行杆路系统名称重复！");
                } else {
                    polewaySystemNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importPolewaySystem(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
        		namelist.add(name);
        	}
            tempErrors.add("成功导入杆路系统数据" + (dbos.size() - 1) + "条。");
            tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }


    /**
     * importPolewaySeg
     * add by libo 2008.8.12
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importPolewaySeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map stonewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            PolewaySeg dbo = new PolewaySeg();
            String systemlabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.relatedSystemCuid, systemlabelCn);

            String origtype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, origtype);

            String origlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点名称", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointcuid, origlabelcn);

            String desttype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, desttype);

            String destlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点名称", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointcuid, destlabelcn);

            String typeA = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segorigPointType, typeA);

            String nameA = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施名称", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.origPointCuid, nameA);

            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "A点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);

            String typeZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.segdestPointType, typeZ);

            String nameZ = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.destPointCuid, nameZ);

            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "Z点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);

            String len = ImpExpUtils.getSheetValueByLabel(sheet, "距离", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.length, len);

            String linetype = ImpExpUtils.getSheetValueByLabel(sheet, "拉线规格", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.pullLineType, linetype);

            String lineuser = ImpExpUtils.getSheetValueByLabel(sheet, "拉线用户", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.pullLineUser, lineuser);

            String ownship = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.ownership, ownship);

            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.builder, builder);
            
            String multiBuilderUser = ImpExpUtils.getSheetValueByLabel(sheet, "共建单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.multiBuildUser, multiBuilderUser);
            String sharedUser = ImpExpUtils.getSheetValueByLabel(sheet, "共享单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.sharedUser, sharedUser);
            

            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(PolewaySeg.AttrName.finishDate, finishDate);

            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err); //用途
            dbo.setAttrValue(PolewaySeg.AttrName.purpose, purpose);

            String maintmode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //维护方式
            dbo.setAttrValue(PolewaySeg.AttrName.maintMode, maintmode);

            String builddate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err); //施工时间
            dbo.setAttrValue(PolewaySeg.AttrName.buildDate, builddate);

            String maintdep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err); //维护单位
            dbo.setAttrValue(PolewaySeg.AttrName.maintDep, maintdep);

            String checkdate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err); //检修时间
            dbo.setAttrValue(PolewaySeg.AttrName.checkDate, checkdate);

            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err); //巡检人
            dbo.setAttrValue(PolewaySeg.AttrName.servicer, servicer);

            String phoneno = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err); //联系电话
            dbo.setAttrValue(PolewaySeg.AttrName.phoneNo, phoneno);

            String resowner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err); //所有权人
            dbo.setAttrValue(PolewaySeg.AttrName.resOwner, resowner);

            String username = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err); //使用单位
            dbo.setAttrValue(PolewaySeg.AttrName.userName, username);

            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //备注
            dbo.setAttrValue(PolewaySeg.AttrName.remark, remark);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            //验证在Excel表中是否重名的对象。
//            if ((!labelCn.equals(""))) {
//                if (stonewaySystemNames.get(labelCn) != null) {
//                    errorlist.add("<font color = \"red\">非法数据：在excel表格中，第" + i + "行与第" +
//                                  stonewaySystemNames.get(labelCn) + "行标石路由系统名称重复！</font>");
//                } else {
//                    stonewaySystemNames.put(labelCn, Integer.valueOf(i));
//                }
//            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importPolewaySeg(new BoActionContext(), dbos);
	     ArrayList namelist = new ArrayList();
	     if (errorlist.size() == 0) {
	    	 for (int i = 1; i < dbos.size(); i++) {
	    		 String nameA = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
	    		 String nameZ = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
	    		 String name = nameA+"--"+nameZ;
	    		 namelist.add(name);
	    	 }
	    	 errorlist.add("成功导入杆路段数据" + (dbos.size() - 1) + "条。");
	    	 errorlist.addAll(namelist); 
	    	 return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }

    /**
     * importStonewaySystem
     * 标石路由系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importStonewaySystem(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map stonewaySystemNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            StonewaySystem dbo = new StonewaySystem();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "标石路由名称", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.abbreviation, abbreviation);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.length, length);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.remark, remark);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.buildDate, buildDate);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(StonewaySystem.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (stonewaySystemNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  stonewaySystemNames.get(labelCn) + "行标石路由系统名称重复！");
                } else {
                    stonewaySystemNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importStonewaySystem(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
            	namelist.add(name);
        	}
            tempErrors.add("成功导入标石路由系统数据" + (dbos.size() - 1) + "条。");
        	tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }

    /**
     * importStonewayBranch
     *
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importStonewayBranch(Workbook workbook) throws Exception {
        return null;
    }

    /**
     * importStonewaySeg
     * add by libo 2008.8.12
     * 标石路由段导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importStonewaySeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
//        Map stonewaySegNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            StonewaySeg dbo = new StonewaySeg();
            String systemlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err); //    所属线路系统名称
            dbo.setAttrValue(StonewaySeg.AttrName.relatedSystemCuid, systemlabelcn);
            String branchorigtype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点类型", i, err); //    所属分支的起点类型
            dbo.setAttrValue(ImpExpConsts.origPointType, branchorigtype);
            String branchoriglabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的起点名称", i, err); //    所属分支的起点名称
            dbo.setAttrValue(ImpExpConsts.origPointcuid, branchoriglabelcn);
            String branchdesttype = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点类型", i, err); //    所属分支的终止点类型
            dbo.setAttrValue(ImpExpConsts.destPointType, branchdesttype);
            String branchdestlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "所属分支的终止点名称", i, err); //    所属分支的终止点名称
            dbo.setAttrValue(ImpExpConsts.destPointcuid, branchdestlabelcn);
            String apointtype = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施类型", i, err); //    A点设施类型
            dbo.setAttrValue(ImpExpConsts.segorigPointType, apointtype);
            String apointlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施名称", i, err); //    A点设施名称
            dbo.setAttrValue(StonewaySeg.AttrName.origPointCuid, apointlabelcn);
            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "A点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);
            String zpointtype = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i, err); //    Z点设施类型
            dbo.setAttrValue(ImpExpConsts.segdestPointType, zpointtype);
            String zpointlabelcn = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i, err); //    Z点设施名称
            dbo.setAttrValue(StonewaySeg.AttrName.destPointCuid, zpointlabelcn);
            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "Z点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "距离", i, err); //距离
            dbo.setAttrValue(StonewaySeg.AttrName.length, length);
            String protect = ImpExpUtils.getSheetValueByLabel(sheet, "保护情况", i, err); //保护情况
            dbo.setAttrValue(StonewaySeg.AttrName.dmprotectState, protect);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err); //产权
            dbo.setAttrValue(StonewaySeg.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err); //施工单位
            dbo.setAttrValue(StonewaySeg.AttrName.builder, builder);
            
            String multiBuilderUser = ImpExpUtils.getSheetValueByLabel(sheet, "共建单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.multiBuildUser, multiBuilderUser);
            String sharedUser = ImpExpUtils.getSheetValueByLabel(sheet, "共享单位", i, err);
            dbo.setAttrValue(DuctSeg.AttrName.sharedUser, sharedUser);
            
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err); //竣工时间
            dbo.setAttrValue(StonewaySeg.AttrName.finishDate, finishDate);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //维护方式
            dbo.setAttrValue(StonewaySeg.AttrName.maintMode, maintMode);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err); //用途
            dbo.setAttrValue(StonewaySeg.AttrName.purpose, purpose);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err); //施工时间
            dbo.setAttrValue(StonewaySeg.AttrName.buildDate, buildDate);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err); //检修时间
            dbo.setAttrValue(StonewaySeg.AttrName.checkDate, checkDate);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err); //维护单位
            dbo.setAttrValue(StonewaySeg.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err); //巡检人
            dbo.setAttrValue(StonewaySeg.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err); //联系电话
            dbo.setAttrValue(StonewaySeg.AttrName.phoneNo, phoneNo);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err); //使用单位
            dbo.setAttrValue(StonewaySeg.AttrName.userName, userName);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err); //所有权人
            dbo.setAttrValue(StonewaySeg.AttrName.resOwner, resOwner);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(StonewaySeg.AttrName.remark, remark);

            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            //验证在Excel表中是否重名的对象。
//            if ((!labelCn.equals(""))) {
//                if (stonewaySegNames.get(labelCn) != null) {
//                    errorlist.add("<font color = \"red\">非法数据：在excel表格中，第" + i + "行与第" +
//                                  stonewaySegNames.get(labelCn) + "行标石路由段名称重复！</font>");
//                } else {
//                    stonewaySegNames.put(labelCn, Integer.valueOf(i));
//                }
//            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importStonewaySeg(new BoActionContext(), dbos);
	     ArrayList namelist = new ArrayList();
	     if (errorlist.size() == 0) {
	    	 for (int i = 1; i < dbos.size(); i++) {
	    		 String nameA = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
	    		 String nameZ = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
	    		 String name = nameA+"--"+nameZ;
	    		 namelist.add(name);
	    	 }
	    	 errorlist.add("成功导入标石路由段数据" + (dbos.size() - 1) + "条。");
	    	 errorlist.addAll(namelist);    
	    	 return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }


    /**
     * importUpLine
     * 引上系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importUpLine(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map upLineNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            UpLine dbo = new UpLine();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(UpLine.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "引上名称", i, err);
            dbo.setAttrValue(UpLine.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(UpLine.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(UpLine.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(UpLine.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(UpLine.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(UpLine.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(UpLine.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(UpLine.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(UpLine.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(UpLine.AttrName.abbreviation, abbreviation);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(UpLine.AttrName.remark, remark);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(UpLine.AttrName.length, length);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(UpLine.AttrName.buildDate, buildDate);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(UpLine.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;

            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (upLineNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + upLineNames.get(labelCn) + "行引上系统名称重复！");
                } else {
                    upLineNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            errorsAndDatas.add(ImpExpConsts._error, errorlist);
            return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importUpLine(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
            	namelist.add(name);
        	}
            tempErrors.add("成功导入引上系统数据" + (dbos.size() - 1) + "条。");
           	tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }

    /**
     * importUplineSeg
     * 引上段导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importUplineSeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            UpLineSeg dbo = new UpLineSeg();
            String relatedSystem = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.relatedSystemCuid, relatedSystem);
            String aType = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, aType);
            String origPoint = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施名称", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.origPointCuid, origPoint);
            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "A点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);
            String zType = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, zType);
            String destPoint = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.destPointCuid, destPoint);
            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "Z点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "距离", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.length, length);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.finishDate, finishDate);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.maintMode, maintMode);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.purpose, purpose);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.buildDate, buildDate);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.checkDate, checkDate);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.phoneNo, phoneNo);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.userName, userName);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.resOwner, resOwner);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(UpLineSeg.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importUpLineSeg(new BoActionContext(), dbos);
	     ArrayList namelist = new ArrayList();
	     if (errorlist.size() == 0) {
	    	 for (int i = 1; i < dbos.size(); i++) {
	    		 String nameA = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
	    		 String nameZ = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
	    		 String name = nameA+"--"+nameZ;
	    		 namelist.add(name);
	    	 }
	    	 errorlist.add("成功导入引上段数据" + (dbos.size() - 1) + "条。");
	    	 errorlist.addAll(namelist);    
	    	 return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }

    }

    /**
     * importHangWall
     * 挂墙系统导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook 包含Excel表格中数据的Workbook。
     * @return ArrayList 错误信息列表
     */
    public ArrayList importHangWall(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map hangWallNames = new HashMap();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            HangWall dbo = new HangWall();
            String relatedSpace = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(HangWall.AttrName.relatedSpaceCuid, relatedSpace);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "挂墙名称", i, err);
            dbo.setAttrValue(HangWall.AttrName.labelCn, labelCn);
            String project = ImpExpUtils.getSheetValueByLabel(sheet, "工程名称", i, err);
            dbo.setAttrValue(HangWall.AttrName.project, project);
            String designer = ImpExpUtils.getSheetValueByLabel(sheet, "设计单位", i, err);
            dbo.setAttrValue(HangWall.AttrName.designer, designer);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(HangWall.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(HangWall.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(HangWall.AttrName.finishDate, finishDate);
            String state = ImpExpUtils.getSheetValueByLabel(sheet, "工程状态", i, err);
            dbo.setAttrValue(HangWall.AttrName.state, state);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(HangWall.AttrName.maintMode, maintMode);
            String systemLevel = ImpExpUtils.getSheetValueByLabel(sheet, "系统级别", i, err);
            dbo.setAttrValue(HangWall.AttrName.systemLevel, systemLevel);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err);
            dbo.setAttrValue(HangWall.AttrName.abbreviation, abbreviation);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(HangWall.AttrName.remark, remark);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "总长度（米）", i, err);
            dbo.setAttrValue(HangWall.AttrName.length, length);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(HangWall.AttrName.buildDate, buildDate);
            String projectNo = ImpExpUtils.getSheetValueByLabel(sheet, "工程编号", i, err);
            dbo.setAttrValue(HangWall.AttrName.projectNo, projectNo);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (hangWallNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + hangWallNames.get(labelCn) + "行挂墙系统名称重复！");
                } else {
                    hangWallNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
           errorsAndDatas.add(ImpExpConsts._error, errorlist);
           return errorsAndDatas;
        }
        //调用服务器端接口
        errorlist = getDataImportBO().importHangWall(new BoActionContext(), dbos);
        ArrayList tempErrors=(ArrayList)errorlist.get(ImpExpConsts._error);
        ArrayList namelist = new ArrayList();
        if (tempErrors.size() == 0) {
        	for (int i = 1; i < dbos.size(); i++) {
        		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
            	namelist.add(name);
        	}
            tempErrors.add("成功导入挂墙系统数据" + (dbos.size() - 1) + "条。");
            tempErrors.addAll(namelist);
            return tempErrors;
        }else{
            return tempErrors;
        }
    }

    /**
     * importHangwallSeg
     * 挂墙段导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importHangwallSeg(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            HangWallSeg dbo = new HangWallSeg();
            String relatedSystem = ImpExpUtils.getSheetValueByLabel(sheet, "所属线路系统名称", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.relatedSystemCuid, relatedSystem);
            String aType = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.origPointType, aType);
            String origPoint = ImpExpUtils.getSheetValueByLabel(sheet, "A点设施名称", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.origPointCuid, origPoint);
            String origIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "A点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.origIsDisplay, origIsDisplay);
            String zType = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施类型", i, err);
            dbo.setAttrValue(ImpExpConsts.destPointType, zType);
            String destPoint = ImpExpUtils.getSheetValueByLabel(sheet, "Z点设施名称", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.destPointCuid, destPoint);
            String destIsDisplay = ImpExpUtils.getSheetValueByLabel(sheet, "Z点是否为显示点", i, err);
            dbo.setAttrValue(ImpExpConsts.destIsDisplay, destIsDisplay);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "距离", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.length, length);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.ownership, ownership);
            String builder = ImpExpUtils.getSheetValueByLabel(sheet, "施工单位", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.builder, builder);
            String finishDate = ImpExpUtils.getSheetValueByLabel(sheet, "竣工时间", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.finishDate, finishDate);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.maintMode, maintMode);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.purpose, purpose);
            String buildDate = ImpExpUtils.getSheetValueByLabel(sheet, "施工时间", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.buildDate, buildDate);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "检修时间", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.checkDate, checkDate);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.maintDep, maintDep);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.servicer, servicer);
            String phoneNo = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.phoneNo, phoneNo);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.userName, userName);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.resOwner, resOwner);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
            dbo.setAttrValue(HangWallSeg.AttrName.remark, remark);
            //验证有无缺失的属性列。
            if (err.size() != 0) {
                err.add(ImpExpConsts.ERROR_INFO);
                return err;
            }
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        //调用服务器端接口        
        errorlist = getDataImportBO().importHangWallSeg(new BoActionContext(), dbos);
        
	     ArrayList namelist = new ArrayList();
	     if (errorlist.size() == 0) {
	    	 for (int i = 1; i < dbos.size(); i++) {
	    		 String nameA = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
	    		 String nameZ = (String) dbos.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
	    		 String name = nameA+"--"+nameZ;
	    		 namelist.add(name);
	    	 }
	    	 
	    	 errorlist.add("成功导入挂墙段数据" + (dbos.size() - 1) + "条。");
	    	 errorlist.addAll(namelist);    
	    	 return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }

    }

    public ArrayList importFiberCab(Workbook workbook) throws Exception {
        return importFiberCab(workbook, "");
    }

    /**
     * importFiberCab
     * 光交接箱导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importFiberCab(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        boolean isOverLayResource = false;
        if(workbook.getSheet("光交接箱")!=null){
        	sheet = workbook.getSheet("光交接箱");
        	isOverLayResource = true;
        }
        
        int rowcount = sheet.getRows();
        int errCount=0;
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberCabNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }
            
            if(isOverLayResource){
                String errorInfo = ImportChecker.checkFiberCab(sheet, i, err);
                if(errorInfo!=null &&!"".equals(errorInfo)){
                	err.add(errorInfo);
                	errCount++;
                	continue;
                }
            }
            FiberCab dbo = new FiberCab();
            String relatedDistrictCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err); //RELATED_DISTRICT_CUID
            dbo.setAttrValue(FiberCab.AttrName.relatedDistrictCuid, relatedDistrictCuid);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, err); //LABEL_CN
            dbo.setAttrValue(FiberCab.AttrName.labelCn, labelCn);
            String fibercabNo = ImpExpUtils.getSheetValueByLabel(sheet, "编号", i, err); //JUNCTION_TYPE
            dbo.setAttrValue(FiberCab.AttrName.fibercabNo, fibercabNo);
            String accessScene = ImpExpUtils.getSheetValueByLabel(sheet, "集客接入场景", i, err); //ACCESS_SCENE
            dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScene);

            String faceCount = ImpExpUtils.getSheetValueByLabel(sheet, "面数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberCab.AttrName.faceCount, faceCount);
            String faceColCount = ImpExpUtils.getSheetValueByLabel(sheet, "每面列数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberCab.AttrName.faceColCount, faceColCount);
            String tierPortCount = ImpExpUtils.getSheetValueByLabel(sheet, "每排行数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberCab.AttrName.tierPortCount, tierPortCount);
            String setupTime = ImpExpUtils.getSheetValueByLabel(sheet, "入网时间", i, err); //SETUP_TIME
            dbo.setAttrValue(FiberCab.AttrName.setupTime, setupTime);
            String creator = ImpExpUtils.getSheetValueByLabel(sheet, "建设单位", i, err); //CREATOR
            dbo.setAttrValue(FiberCab.AttrName.creator, creator);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地址", i, err); //LOCATION
            dbo.setAttrValue(FiberCab.AttrName.location, location);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err); //MODEL
            dbo.setAttrValue(FiberCab.AttrName.model, model);
            String relatedVendorCuid = ImpExpUtils.getSheetValueByLabel(sheet, "设备供应商", i, err); //RELATED_VENDOR_CUID
            dbo.setAttrValue(FiberCab.AttrName.relatedVendorCuid, relatedVendorCuid);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权归属", i, err); //OWNERSHIP
            dbo.setAttrValue(FiberCab.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(Manhle.AttrName.purpose, purpose);
            
//            String positionType = ImpExpUtils.getSheetValueByLabel(sheet, "归属类型", i, err); //无对应关键字，暂放在SYMBOL_NAME中。
//            dbo.setAttrValue(FiberCab.AttrName.symbolName, positionType);
//            String relatedSiteCuid = ImpExpUtils.getSheetValueByLabel(sheet, "设备所在位置", i, err);
//            dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, relatedSiteCuid);
//			
//			String dpType = ImpExpUtils.getSheetValueByLabel(sheet, "分纤点级别", i, err); //DP_TYPE
//            dbo.setAttrValue(FiberCab.AttrName.dpType, dpType);


            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(FiberCab.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(FiberCab.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(FiberCab.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(FiberCab.AttrName.realLatitude, latitude); //实际纬度

            String designCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "设计容量(芯)", i, err); //DESIGN_CAPACITY
            dbo.setAttrValue(FiberCab.AttrName.designCapacity, designCapacity);
            String usedCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "使用容量(芯)", i, err); //USED_CAPACITY
            dbo.setAttrValue(FiberCab.AttrName.usedCapacity, usedCapacity);
            String installCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "安装容量(芯)", i, err); //INSTALL_CAPACITY
            dbo.setAttrValue(FiberCab.AttrName.installCapacity, installCapacity);
            String freeCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "空闲容量(芯)", i, err); //FREE_CAPACITY
            dbo.setAttrValue(FiberCab.AttrName.freeCapacity, freeCapacity);
            String labelDev = ImpExpUtils.getSheetValueByLabel(sheet, "设备标识", i, err); //LABEL_DEV
            dbo.setAttrValue(FiberCab.AttrName.labelDev, labelDev);
            String seqno = ImpExpUtils.getSheetValueByLabel(sheet, "设备序列号", i, err); //SEQNO
            dbo.setAttrValue(FiberCab.AttrName.seqno, seqno);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(FiberCab.AttrName.username, userName);
            String specialLabel = ImpExpUtils.getSheetValueByLabel(sheet, "厂商特征值", i, err); //SPECIAL_LABEL
            dbo.setAttrValue(FiberCab.AttrName.specialLabel, specialLabel);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err); //USERNAME
            dbo.setAttrValue(FiberCab.AttrName.resOwner, resOwner);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err); //MAINT_DEP
            dbo.setAttrValue(FiberCab.AttrName.maintDep, maintDep);
            String preserver = ImpExpUtils.getSheetValueByLabel(sheet, "维护人", i, err); //PRESERVER
            dbo.setAttrValue(FiberCab.AttrName.preserver, preserver);
            String preserverPhone = ImpExpUtils.getSheetValueByLabel(sheet, "维护人联系电话", i, err); //PRESERVER_PHONE
            dbo.setAttrValue(FiberCab.AttrName.preserverPhone, preserverPhone);
            String preserverAddr = ImpExpUtils.getSheetValueByLabel(sheet, "维护人通信地址", i, err); //PRESERVER_ADDR
            dbo.setAttrValue(FiberCab.AttrName.preserverAddr, preserverAddr);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //MAINT_MODE
            dbo.setAttrValue(FiberCab.AttrName.maintMode, maintMode);
            String isUsageState = ImpExpUtils.getSheetValueByLabel(sheet, "是否正使用", i, err); //IS_USAGE_STATE
            dbo.setAttrValue(FiberCab.AttrName.isUsageState, isUsageState);
            String creattime = ImpExpUtils.getSheetValueByLabel(sheet, "建设日期", i, err); //CREATTIME
            dbo.setAttrValue(FiberCab.AttrName.creattime, creattime);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "核查日期", i, err); //CREATTIME
            dbo.setAttrValue(FiberCab.AttrName.checkDate, checkDate);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(Manhle.AttrName.servicer, servicer);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //REMARK
            dbo.setAttrValue(FiberCab.AttrName.remark, remark);
            
            String isYjr = ImpExpUtils.getSheetValueByLabel(sheet, "是否预覆盖接入点", i, err); //IS_YJR
            dbo.setAttrValue(FiberCab.AttrName.isYjr, isYjr);
            String bossCode = ImpExpUtils.getSheetValueByLabel(sheet, "BOSS编码", i, err); //BOSS_CODE
            dbo.setAttrValue(FiberCab.AttrName.bossCode, bossCode);
            String vpLableCn = ImpExpUtils.getSheetValueByLabel(sheet, "客户名称", i, err); //VP_LABEL_CN
            dbo.setAttrValue(FiberCab.AttrName.vpLabelCn, vpLableCn);
          
            String relatedTemplateName = ImpExpUtils.getSheetValueByLabel(sheet, "模板名称", i, err); //REMARK
            dbo.setAttrValue(FiberCab.AttrName.relatedTemplateName, relatedTemplateName);
            if(isOverLayResource){
                String cuid = ImpExpUtils.getSheetValueByLabel(sheet, "数据库主键", i, err);
                cuidList.add(cuid);
            }
            //验证有无缺失的属性列。
            if (err.size() != 0) {
            	err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberCabNames.get(labelCn) != null) {
                	errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  fiberCabNames.get(labelCn) + "光交接箱名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberCabNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }

    	long analyzeEndTime = System.currentTimeMillis();
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
        if(districts != null && districts.size() > 0 && !districts.isEmpty()){
        	if (errorlist.size() != 0) {
        		errorlist.add(ImpExpConsts.ERROR_INFO);
        		errorsAndDatas.add(ImpExpConsts._error, errorlist);
        		return errorsAndDatas;
        	}
        	//调用服务器端接口
            errorlist = bo.importFiberCab(new BoActionContext(), dbos, districts,curMapCuid);

        	long storeEndTime = System.currentTimeMillis();
        	if(isOverLayResource){
                dmDataPlantBO.dealHistoryOverLayResource(cuidList);
        	}
//        	long markEndTime = System.currentTimeMillis();
//            errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
//            errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
//            errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
//            errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
//            errorlist.add("数据导入成功");
//            if (err.size() != 0) {
//                errorlist.addAll(err);
//            }
            ArrayList namelist = new ArrayList();
            if (errorlist.size() == 0) {
            	for (int i = 1; i < dbos.size(); i++) {
            		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
                	namelist.add(name);
                }
            	errorlist.add("成功导入光交箱数据" + (dbos.size() - 1) + "条。");
            	errorlist.addAll(namelist);
            }
        }else{
        	errorlist.add("用户管理区域值为空,无法正常导入,请处理");
        }
        
        return errorlist;
    }

    public ArrayList importOnubox(Workbook workbook) throws Exception {
        return importOnubox(workbook, "");
    }

    /**
     * importOnubox
     * 光交接箱导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importOnubox(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        
        int rowcount = sheet.getRows();
        int errCount=0;
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberCabNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }

            FiberCab dbo = new FiberCab();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, err); //LABEL_CN
            dbo.setAttrValue(FiberCab.AttrName.labelCn, labelCn);
            String relatedDistrictCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err); //RELATED_DISTRICT_CUID
            dbo.setAttrValue(FiberCab.AttrName.relatedDistrictCuid, relatedDistrictCuid);
            String fibercabNo = ImpExpUtils.getSheetValueByLabel(sheet, "编号", i, err); //JUNCTION_TYPE
            dbo.setAttrValue(FiberCab.AttrName.fibercabNo, fibercabNo);
            
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            dbo.setAttrValue(FiberCab.AttrName.longitude, longitude); //显示经度
            dbo.setAttrValue(FiberCab.AttrName.realLongitude, longitude); //实际经度
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            dbo.setAttrValue(FiberCab.AttrName.latitude, latitude); //显示纬度
            dbo.setAttrValue(FiberCab.AttrName.realLatitude, latitude); //实际纬度

            String location = ImpExpUtils.getSheetValueByLabel(sheet, "安装地址", i, err); //LOCATION
            dbo.setAttrValue(FiberCab.AttrName.location, location);
            String setupTime = ImpExpUtils.getSheetValueByLabel(sheet, "入网时间", i, err); //SETUP_TIME
            dbo.setAttrValue(FiberCab.AttrName.setupTime, setupTime);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权归属", i, err); //OWNERSHIP
            dbo.setAttrValue(FiberCab.AttrName.ownership, ownership);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //MAINT_MODE
            dbo.setAttrValue(FiberCab.AttrName.maintMode, maintMode);
            
            String relatedSiteCuid = ImpExpUtils.getSheetValueByLabel(sheet, "综合箱归属位置", i, err);
            dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, relatedSiteCuid);
            String preserver = ImpExpUtils.getSheetValueByLabel(sheet, "维护人", i, err); //PRESERVER
            dbo.setAttrValue(FiberCab.AttrName.preserver, preserver);
            String preserverPhone = ImpExpUtils.getSheetValueByLabel(sheet, "维护人联系电话", i, err); //PRESERVER_PHONE
            dbo.setAttrValue(FiberCab.AttrName.preserverPhone, preserverPhone);
            String preserverAddr = ImpExpUtils.getSheetValueByLabel(sheet, "维护人通信地址", i, err); //PRESERVER_ADDR
            dbo.setAttrValue(FiberCab.AttrName.preserverAddr, preserverAddr);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //REMARK
            dbo.setAttrValue(FiberCab.AttrName.remark, remark);
            //设备类型：ONU综合箱
            dbo.setAttrValue(FiberCab.AttrName.deviceType, "2");
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberCabNames.get(labelCn) != null) {
                    err.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  fiberCabNames.get(labelCn) + "ONU综合箱名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberCabNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
    	long analyzeEndTime = System.currentTimeMillis();
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
    	//调用服务器端接口
        errorlist = bo.importOnubox(new BoActionContext(), dbos, districts,curMapCuid);

    	long storeEndTime = System.currentTimeMillis();

    	long markEndTime = System.currentTimeMillis();
        errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
        errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
        errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
        errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
        errorlist.add("数据导入成功");
        
        if (err.size() != 0) {
            errorlist.addAll(err);
        }
        return errorlist;
    }
    
    public ArrayList importPos(Workbook workbook) throws Exception {
        return importPos(workbook, "");
    }

    /**
     * importPos
     * 光交接箱导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importPos(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        
        int rowcount = sheet.getRows();
        int errCount=0;
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberCabNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }

            AnPos dbo = new AnPos();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "POS名称", i, err); //LABEL_CN
            dbo.setAttrValue(AnPos.AttrName.labelCn, labelCn);
            String relatedCabType = ImpExpUtils.getSheetValueByLabel(sheet, "归属设备类型", i, err); //RELATED_CAB_TYPE
            dbo.setAttrValue("RELATED_CAB_TYPE", relatedCabType);
            String relatedCabCuid = ImpExpUtils.getSheetValueByLabel(sheet, "归属设备名称", i, err); //RELATED_CAB_CUID
            dbo.setAttrValue(AnPos.AttrName.relatedCabCuid, relatedCabCuid);
            
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberCabNames.get(labelCn) != null) {
                    err.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  fiberCabNames.get(labelCn) + "ONU综合箱名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberCabNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
    	long analyzeEndTime = System.currentTimeMillis();
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
    	//调用服务器端接口
        errorlist = bo.importPos(new BoActionContext(), dbos);
    	long storeEndTime = System.currentTimeMillis();
    	long markEndTime = System.currentTimeMillis();
        errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
        errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
        errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
        errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
        errorlist.add("数据导入成功");
        
        if (err.size() != 0) {
            errorlist.addAll(err);
        }
        return errorlist;
    }
    
    public ArrayList importOnu(Workbook workbook) throws Exception {
        return importOnu(workbook, "");
    }

    /**
     * importOnu
     * 光交接箱导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importOnu(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        
        int rowcount = sheet.getRows();
        int errCount=0;
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberCabNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }

            AnOnu dbo = new AnOnu();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "ONU名称", i, err); //LABEL_CN
            dbo.setAttrValue(AnOnu.AttrName.labelCn, labelCn);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "归属设备名称", i, err); //LOCATION
            dbo.setAttrValue(AnOnu.AttrName.location, location);
            
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberCabNames.get(labelCn) != null) {
                    err.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  fiberCabNames.get(labelCn) + "ONU名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberCabNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
    	long analyzeEndTime = System.currentTimeMillis();
    	//验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
    	//调用服务器端接口
        errorlist = bo.importOnu(new BoActionContext(), dbos, districts, curMapCuid);
    	long storeEndTime = System.currentTimeMillis();
    	long markEndTime = System.currentTimeMillis();
        errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
        errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
        errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
        errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
        errorlist.add("数据导入成功");
        
        if (err.size() != 0) {
            errorlist.addAll(err);
        }
        return errorlist;
    }
    
    public ArrayList importFiberDp(Workbook workbook) throws Exception {
        return importFiberDp(workbook, "");
    }

    /**
     * importFiberDp
     * 光交接箱导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importFiberDp(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        boolean isOverLayResource = false;
        if(workbook.getSheet("光分纤箱")!=null){
        	sheet = workbook.getSheet("光分纤箱");
        	isOverLayResource = true;
        }
        int errCount = 0;
        ArrayList districterr=new ArrayList();
        int rowcount = sheet.getRows();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        DataObjectList dbos = new DataObjectList();
        ArrayList errorsAndDatas= new ArrayList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberDpNames = new HashMap();
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        //第一行是表头，后台忽略处理
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }
            if(isOverLayResource){
                String errorInfo = ImportChecker.checkFiberDP(sheet, i, err);
                if(errorInfo!=null
                		&&!"".equals(errorInfo)){
                	err.add(errorInfo);
                	errCount++;
                	continue;
                }
            }
            FiberDp dbo = new FiberDp();
            String relatedDistrictCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err); //RELATED_DISTRICT_CUID
            dbo.setAttrValue(FiberDp.AttrName.relatedDistrictCuid, relatedDistrictCuid);
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, err); //LABEL_CN
            dbo.setAttrValue(FiberDp.AttrName.labelCn, labelCn);
            String fiberDpNo = ImpExpUtils.getSheetValueByLabel(sheet, "编号", i, err); //JUNCTION_TYPE
            dbo.setAttrValue(FiberDp.AttrName.fiberdpNo, fiberDpNo);
            String accessScene = ImpExpUtils.getSheetValueByLabel(sheet, "集客接入场景", i, err); //ACCESS_SCENE
            dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScene);
            
            String dppositionType = ImpExpUtils.getSheetValueByLabel(sheet, "所在设施类型", i, err); //无对应关键字，暂放在SYMBOL_NAME中。
            dbo.setAttrValue(FiberCab.AttrName.symbolName, dppositionType);

            String relatedSiteCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所在设施名称", i, err);
            dbo.setAttrValue(FiberDp.AttrName.relatedSiteCuid, relatedSiteCuid);
            
            //杨能杰修改（2016-06-28）模板中没有此字段
//			String dpType = ImpExpUtils.getSheetValueByLabel(sheet, "分纤点级别", i, err); //DP_TYPE
//            dbo.setAttrValue(FiberDp.AttrName.dpType, dpType);
			
			
            String landHeight = ImpExpUtils.getSheetValueByLabel(sheet, "距地面高度(M)", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.landHeight, landHeight);
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "长度(M)", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.length, length);
            String colCount = ImpExpUtils.getSheetValueByLabel(sheet, "列数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.colCount, colCount);
            String colRowCount = ImpExpUtils.getSheetValueByLabel(sheet, "每列行数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.colRowCount, colRowCount);
            String tierColCount = ImpExpUtils.getSheetValueByLabel(sheet, "每小排列数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.tierColCount, tierColCount);
            String tierRowCount = ImpExpUtils.getSheetValueByLabel(sheet, "每小排行数", i, err); //CONNECT_TYPE
            dbo.setAttrValue(FiberDp.AttrName.tierRowCount, tierRowCount);
            String setupTime = ImpExpUtils.getSheetValueByLabel(sheet, "入网时间", i, err); //SETUP_TIME
            dbo.setAttrValue(FiberDp.AttrName.setupTime, setupTime);
            String creator = ImpExpUtils.getSheetValueByLabel(sheet, "建设单位", i, err); //CREATOR
            dbo.setAttrValue(FiberDp.AttrName.creator, creator);
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地址", i, err); //LOCATION
            dbo.setAttrValue(FiberDp.AttrName.location, location);
            String model = ImpExpUtils.getSheetValueByLabel(sheet, "型号", i, err); //MODEL
            dbo.setAttrValue(FiberDp.AttrName.model, model);
            String relatedVendorCuid = ImpExpUtils.getSheetValueByLabel(sheet, "设备供应商", i, err); //RELATED_VENDOR_CUID
            dbo.setAttrValue(FiberDp.AttrName.relatedVendorCuid, relatedVendorCuid);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权归属", i, err); //OWNERSHIP
            dbo.setAttrValue(FiberDp.AttrName.ownership, ownership);
            String purpose = ImpExpUtils.getSheetValueByLabel(sheet, "用途", i, err);
            dbo.setAttrValue(FiberDp.AttrName.purpose, purpose);

            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err);
            if(longitude != null && (!"".equals(longitude))) {
            	dbo.setAttrValue(FiberDp.AttrName.longitude, longitude); //显示经度
                dbo.setAttrValue(FiberDp.AttrName.realLongitude, longitude); //实际经度
            } else {
            	dbo.setAttrValue(FiberDp.AttrName.longitude, "0");
            	dbo.setAttrValue(FiberDp.AttrName.realLongitude, "0");
            }
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err);
            if(latitude != null && (!"".equals(latitude))) {
            	dbo.setAttrValue(FiberDp.AttrName.latitude, latitude); //显示纬度
                dbo.setAttrValue(FiberDp.AttrName.realLatitude, latitude); //实际纬度
            } else {
            	dbo.setAttrValue(FiberDp.AttrName.latitude, "0");
            	dbo.setAttrValue(FiberDp.AttrName.realLatitude, "0");
            }
            String designCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "设计容量(芯)", i, err); //DESIGN_CAPACITY
            dbo.setAttrValue(FiberDp.AttrName.designCapacity, designCapacity);
            String usedCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "使用容量(芯)", i, err); //USED_CAPACITY
            dbo.setAttrValue(FiberDp.AttrName.usedCapacity, usedCapacity);
            String installCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "安装容量(芯)", i, err); //INSTALL_CAPACITY
            dbo.setAttrValue(FiberDp.AttrName.installCapacity, installCapacity);
            String freeCapacity = ImpExpUtils.getSheetValueByLabel(sheet, "空闲容量(芯)", i, err); //FREE_CAPACITY
            dbo.setAttrValue(FiberDp.AttrName.freeCapacity, freeCapacity);
            String labelDev = ImpExpUtils.getSheetValueByLabel(sheet, "设备标识", i, err); //LABEL_DEV
            dbo.setAttrValue(FiberDp.AttrName.labelDev, labelDev);
            String seqno = ImpExpUtils.getSheetValueByLabel(sheet, "设备序列号", i, err); //SEQNO
            dbo.setAttrValue(FiberDp.AttrName.seqno, seqno);
            String userName = ImpExpUtils.getSheetValueByLabel(sheet, "使用单位", i, err);
            dbo.setAttrValue(FiberDp.AttrName.username, userName);
            String specialLabel = ImpExpUtils.getSheetValueByLabel(sheet, "厂商特征值", i, err); //SPECIAL_LABEL
            dbo.setAttrValue(FiberDp.AttrName.specialLabel, specialLabel);
            String resOwner = ImpExpUtils.getSheetValueByLabel(sheet, "所有权人", i, err); //USERNAME
            dbo.setAttrValue(FiberDp.AttrName.resOwner, resOwner);
            String maintDep = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err); //MAINT_DEP
            dbo.setAttrValue(FiberDp.AttrName.maintDep, maintDep);
            String preserver = ImpExpUtils.getSheetValueByLabel(sheet, "维护人", i, err); //PRESERVER
            dbo.setAttrValue(FiberDp.AttrName.preserver, preserver);
            String preserverPhone = ImpExpUtils.getSheetValueByLabel(sheet, "维护人联系电话", i, err); //PRESERVER_PHONE
            dbo.setAttrValue(FiberDp.AttrName.preserverPhone, preserverPhone);
            String preserverAddr = ImpExpUtils.getSheetValueByLabel(sheet, "维护人通信地址", i, err); //PRESERVER_ADDR
            dbo.setAttrValue(FiberDp.AttrName.preserverAddr, preserverAddr);
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err); //MAINT_MODE
            dbo.setAttrValue(FiberDp.AttrName.maintMode, maintMode);
            String isUsageState = ImpExpUtils.getSheetValueByLabel(sheet, "是否正使用", i, err); //IS_USAGE_STATE
            dbo.setAttrValue(FiberDp.AttrName.isUsageState, isUsageState);
            String creattime = ImpExpUtils.getSheetValueByLabel(sheet, "竣工日期", i, err); //CREATTIME
            dbo.setAttrValue(FiberDp.AttrName.creattime, creattime);
            String checkDate = ImpExpUtils.getSheetValueByLabel(sheet, "核查日期", i, err); //CREATTIME
            dbo.setAttrValue(FiberDp.AttrName.checkDate, checkDate);
            String servicer = ImpExpUtils.getSheetValueByLabel(sheet, "巡检人", i, errorlist);
            dbo.setAttrValue(FiberDp.AttrName.servicer, servicer);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //REMARK
            dbo.setAttrValue(FiberDp.AttrName.remark, remark);
            String relatedTemplatedName = ImpExpUtils.getSheetValueByLabel(sheet, "模板名称", i, err); //REMARK
            dbo.setAttrValue(FiberDp.AttrName.relatedTemplateName, relatedTemplatedName);
            
            String isYjr = ImpExpUtils.getSheetValueByLabel(sheet, "是否预覆盖接入点", i, err); //IS_YJR
            dbo.setAttrValue(FiberDp.AttrName.isYjr, isYjr);
            String bossCode = ImpExpUtils.getSheetValueByLabel(sheet, "BOSS编码", i, err); //BOSS_CODE
            dbo.setAttrValue(FiberDp.AttrName.bossCode, bossCode);
            String vpLableCn = ImpExpUtils.getSheetValueByLabel(sheet, "客户名称", i, err); //VP_LABEL_CN
            dbo.setAttrValue(FiberDp.AttrName.vpLabelCn, vpLableCn);
            if(isOverLayResource){
                String cuid = ImpExpUtils.getSheetValueByLabel(sheet, "数据库主键", i, err);
                cuidList.add(cuid);
            }
        	//验证有无缺失的属性列。
            if (err.size() != 0) {
            	err.add(ImpExpConsts.ERROR_INFO);
                errorsAndDatas.add(ImpExpConsts._error, err);
                return errorsAndDatas;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberDpNames.get(labelCn) != null) {
                	errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
                                  fiberDpNames.get(labelCn) + "光分纤箱名称重复！");
                    errCount++;
                    continue;
                } else {
                    fiberDpNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
    	long analyzeEndTime = System.currentTimeMillis();
        //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
    	HashSet<District> districts=checkLonAndLatByUserDistricts(districterr);
        if(districterr.size()!=0){
        	districterr.add(ImpExpConsts.ERROR_INFO);
        	errorsAndDatas.add(ImpExpConsts._error,districterr);
        	return errorsAndDatas;
        }
//        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
//        if(districts != null && districts.size() > 0 && !districts.isEmpty()){
//        	if (errorlist.size() != 0) {
//        		errorlist.add(ImpExpConsts.ERROR_INFO);
//        		errorsAndDatas.add(ImpExpConsts._error, errorlist);
//        		return errorsAndDatas;
//        	}
        	//调用服务器端接口
            errorlist = bo.importFiberDp(new BoActionContext(), dbos, districts,curMapCuid);
            logger.info("====返回数据=====" + errorlist);
        	long storeEndTime = System.currentTimeMillis();
        	if(isOverLayResource){
                dmDataPlantBO.dealHistoryOverLayResource(cuidList);
        	}
            ArrayList namelist = new ArrayList();
            List templateNamesList = new ArrayList();
            Map<String,String> templateNamesMap = new HashMap<String,String>();
            if (errorlist.size() == 0) {
            	for (int i = 1; i < dbos.size(); i++) {
            		String name = (String) dbos.get(i).getAttrValue(Pole.AttrName.labelCn);
                	namelist.add(name);
                	String templateName =  (String) dbos.get(i).getAttrValue(FiberDp.AttrName.relatedTemplateName);
                	templateNamesList.add(templateName);
                	templateNamesMap.put(name, templateName);
                }
            	errorlist.add("成功导入光分纤箱数据" + (dbos.size() - 1) + "条。");
            	errorlist.addAll(namelist);
            	DataObjectList portsList = new DataObjectList();
            	//生成光分纤箱端子
            	if(templateNamesMap != null && templateNamesMap.size() > 0){
            		logger.info("====模板名城数据=====" + templateNamesMap);
            		for (String dpName : templateNamesMap.keySet()) {
            			String tempName = templateNamesMap.get(dpName);
            			String sql1 = Pole.AttrName.labelCn + " ='" + dpName + "'";
            			DataObjectList fiberDpsList = getDuctManagerBO().getObjectsBySql(sql1, new FiberDp());
            			logger.info("====分纤箱数据=====" + fiberDpsList);
            			String sql = Template.AttrName.Name + " ='" + tempName + "'";
            			DataObjectList list = BoHomeFactory.getInstance().getBO(ITemplateBOX.class).getTemplatesBySql(new BoActionContext(),sql);
            			logger.info("====模板数据=====" + list);
            			if(list != null && list.size() > 0 && fiberDpsList != null && fiberDpsList.size() > 0){
            				Template template = (Template) list.get(0);
            				String fiberDpCuid = fiberDpsList.get(0).getAttrString(FiberDp.AttrName.cuid);
            				portsList = getTemplatePicJson(template, template.getPic(), fiberDpCuid);
            				logger.info("====端子数据=====" + portsList);
            				if(portsList != null && portsList.size() > 0){
            		        	getFiberDpPortBO().addFiberDpPorts(new BoActionContext(), portsList);
            		        }
            			}
            		}
            	}
            }
//        }else{
//        	errorlist.add("管理区域值为空,无法正常导入,请处理");
//        }
        return errorlist;
    }
    
    private DataObjectList getTemplatePicJson(Template template,DboBlob bolb, String fiberDpCuid){
    	DataObjectList portsList = new DataObjectList();
		TWDataBox box = new TWDataBox();
		ElementBox elBox = new ElementBox();
		elBox.setClient("time", new Date().getTime());
		try{
			String templateXml = new String(bolb.getBlobBytes(),TWaverConst.DEFAULT_ENCODING).trim();
			String xml = replaceXml(templateXml);
			System.out.println(xml);
			box.putClientProperty(PropertyConst.TEMPLATE_DATA_PIC_FLAG, null);
		    box.putClientProperty(PropertyConst.TEMPLATE_SLOT_PAIR_FLAG, null);
			if(template.getType() != TemplateEnum.TEMPLATE_TYPE._ne && StringUtils.isNotBlank(xml)){
				PersistenceManager.readByXML(box, xml, null);
			}else{
				 DboBlob dataBlob = getTemplateDataPic(template);
				 box.clear();
                 NeTemplateHelper.makeGraphByData(bolb, dataBlob, box, new ArrayList()); 
			}
			List elements = box.getAllElements();
			portsList = getPortsList(elements, elBox, fiberDpCuid);
		}catch(Exception e){
			e.printStackTrace();
			LogHome.getLog().error(e);
		}
		return portsList;
	}
    
    private DataObjectList getPortsList(List elements, ElementBox elBox, String fiberDpCuid) {
    	DataObjectList portsList = new DataObjectList();
		for(int i = 0; i < elements.size(); i++){
			twaver.Element portElement = (twaver.Element)elements.get(i);
			if(portElement instanceof PortNode){
				GenericDO portDto = ((PortNode) portElement).getNodeValue();
				String id = portDto.getCuid();
				
				long numInRow = portDto.getAttrLong(Odfport.AttrName.numInMrow);
				long numInCol = portDto.getAttrLong(Odfport.AttrName.numInMcol);
				String labelCn = portDto.getAttrString(Odfport.AttrName.labelCn);
				
    			FiberDpPort fDport = new FiberDpPort();
				fDport.setCuid();
				fDport.setLabelCn(labelCn);
				fDport.setNumInMcol(numInCol);
				fDport.setNumInMrow(numInRow);
				fDport.setRelatedDeviceCuid(fiberDpCuid);
				portsList.add(fDport); 
			}
		}
		return portsList;
	}
    
    public DboBlob getTemplateDataPic(Template template) throws Exception {
    	ITemplateBOX bo = BoHomeFactory.getInstance().getBO(ITemplateBOX.class);
        DboBlob dboBlob = bo.getTemplateDataPic(new BoActionContext(), template);
        dboBlob = replaceBlob(dboBlob);
        return dboBlob;
    }
    
    private DboBlob replaceBlob(DboBlob dboBlob) throws UnsupportedEncodingException {
    	  if(dboBlob == null)
    		  return null;
          String templateXml = new String(dboBlob.getBlobBytes(), TWaverConst.DEFAULT_ENCODING).trim();
          String xml = replaceXml(templateXml);
          if (!xml.equals(templateXml)) {
              dboBlob = new DboBlob(xml.getBytes(TWaverConst.DEFAULT_ENCODING));
          }
          return dboBlob;
      }
    
    private String replaceXml(String xml) {
        if (xml.indexOf("com.boco.transnms.client.view.area.AreaConst$ElementTypeEnum") >= 0) {
            xml = xml.replaceAll("com.boco.transnms.client.view.area.AreaConst\\$ElementTypeEnum",
                                 "com.boco.topo.NmsClientConsts\\$ElementTypeEnum");
        }else if(xml.indexOf("com.boco.transnms.client.NmsClientConsts$ElementTypeEnum")>=0){
        	 xml = xml.replaceAll("com.boco.transnms.client.NmsClientConsts\\$ElementTypeEnum",
                     "com.boco.topo.NmsClientConsts\\$ElementTypeEnum");
        }
        return xml;
    }
    
    private DataObjectList getGenericDOByName(String name, String className) {
    	DataObjectList list = new DataObjectList();
    	try {
			GenericDO  gdo = new GenericDO();
			if(className.equals(FiberDp.CLASS_NAME)){
				gdo = new FiberDp();
			}else if(className.equals(Template.CLASS_NAME)){
				gdo = new Template();
			}
			String sql = Pole.AttrName.labelCn + " ='" + name + "'";
			list = getDuctManagerBO().getObjectsBySql(sql, gdo);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
	}
	/**
     * addImportOpticalFunction
     * 导入光缆衰耗数据
     * @param workbook Workbook
     * @return ArrayList  错误信息列表
     */
    public ArrayList addImportOpticalFunction(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map miscrackNames = new HashMap();
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            OpticalFunction dbo = new OpticalFunction();

            String functionTesters1 = ImpExpUtils.getSheetValueByLabel(sheet, "测试衰耗人员一", i, err); //RELATED_SITE_CUID
            dbo.setAttrValue(OpticalFunction.AttrName.functionTesters1, functionTesters1);

            String functionValue1 = ImpExpUtils.getSheetValueByLabel(sheet, "衰耗值一", i, err); //RELATED_ROOM_CUID
            dbo.setAttrValue(OpticalFunction.AttrName.functionValue1, functionValue1);

            String functionTime1 = ImpExpUtils.getSheetValueByLabel(sheet, "测试时间一", i, err); //LABEL_CN
            dbo.setAttrValue(OpticalFunction.AttrName.functionTime1, functionTime1);

            String opticalType = ImpExpUtils.getSheetValueByLabel(sheet, "光纤类型", i, err); // OWNERSHIP
            dbo.setAttrValue(OpticalFunction.AttrName.opticalType, opticalType);

            String origEqpCuid = ImpExpUtils.getSheetValueByLabel(sheet, "起点设备", i, err); //VENDOR
            dbo.setAttrValue(Optical.AttrName.origEqpCuid, origEqpCuid);

            String origPointCuid = ImpExpUtils.getSheetValueByLabel(sheet, "起点端子", i, err); //MODEL
            dbo.setAttrValue(Optical.AttrName.origPointCuid, origPointCuid);

            String destEqpCuid = ImpExpUtils.getSheetValueByLabel(sheet, "终点设备", i, err); //MODEL
            dbo.setAttrValue(Optical.AttrName.destEqpCuid, destEqpCuid);

            String destPointCuid = ImpExpUtils.getSheetValueByLabel(sheet, "终点端子", i, err); //LENGTH
            dbo.setAttrValue(Optical.AttrName.destPointCuid, destPointCuid);

            String opticRoute = ImpExpUtils.getSheetValueByLabel(sheet, "路由信息", i, err); //HEIGTH
            dbo.setAttrValue(Optical.AttrName.opticRoute, opticRoute);

            if (err.size() != 0) {
                errorlist.add("导入数据失败,请您修改表格之后重新导入。");
                return err;
            }
            //验证在Excel表中是否重名的对象。
            /*if ((!labelCn.equals(""))) {
                if (miscrackNames.get(labelCn) != null) {
                    errorlist.add("<font color = \"red\">非法数据：在excel表格中，第" + i + "行与第" +
                                  miscrackNames.get(labelCn) + "综合机架名称重复！</font>");
                } else {
                    miscrackNames.put(labelCn, Integer.valueOf(i));
                }
            }*/
            dbos.add(dbo);
        }
        if (errorlist.size() != 0) {
            errorlist.add("导入数据失败！请您校验数据之后重新导入。");
            return errorlist;
        }
        //调用服务器端接口
        errorlist = bo.modifyImportOpticalFunction(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功修改光纤衰耗数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add("导入数据失败！请您校验数据之后重新导入。");
            return errorlist;
        }
    }
    public ArrayList generateSystemsByExcel(List <Map<String,Workbook>> list, String curMapCuid,String districtCuid,boolean isConnected) throws Exception {
    	UserTransaction trx = TransactionFactory.getInstance().createTransaction();
    	ArrayList errAndDataList=new ArrayList();
    	errAndDataList.add(ImpExpConsts._error,new ArrayList());
    	errAndDataList.add(ImpExpConsts._data,new ArrayList());
    	try{
    		trx.begin();
    		if(list!=null&&list.size()>0){
    			List <GenericDO>dataList=new ArrayList<GenericDO>();
    			List <DataObjectList>pointList=new ArrayList<DataObjectList>();
    			ArrayList dataRtnList=null;
            	for(int i=0;i<list.size();i++){
            		Map<String,Workbook> map=list.get(i);
            		String key=(String) map.keySet().toArray()[0];
            		Workbook workbook=map.get(key);
            		ArrayList tempList=null;
            		GenericDO segTemplate=null;
            		GenericDO branchTemplate=null;
            		GenericDO systemTemplate=null;
            		if(key.startsWith(Manhle.CLASS_NAME)){
            			 tempList=importManhle(workbook, curMapCuid);
            			 segTemplate=new DuctSeg();
            			 branchTemplate=new DuctBranch();
            			 systemTemplate=new DuctSystem();
            		}else if(key.startsWith(Pole.CLASS_NAME)){
            			 tempList=importPole(workbook, curMapCuid);
            			 segTemplate=new PolewaySeg();
            			 branchTemplate=new PolewayBranch();
            			 systemTemplate=new PolewaySystem();
            		}else if(key.startsWith(Stone.CLASS_NAME)){
            			 tempList=importStone(workbook, curMapCuid);
            			 segTemplate=new StonewaySeg();
            			 branchTemplate=new StonewayBranch();
            			 systemTemplate=new StonewaySystem();
            		}else if(key.startsWith(Inflexion.CLASS_NAME)){
            			 tempList=importInflexion(workbook, curMapCuid);
            			 segTemplate=new HangWallSeg();
//            			 branchTemplate=new PolewayBranch();
            			 systemTemplate=new HangWall();
            		}
            		if(tempList!=null&&tempList.size()>0){
            			DataObjectList dol = (DataObjectList) tempList.get(ImpExpConsts._data);	
            			ArrayList err = (ArrayList) tempList.get(ImpExpConsts._error);
            			
            			ArrayList errList=(ArrayList) errAndDataList.get(ImpExpConsts._error);
            			if(errList==null){
            				errList=new ArrayList();
            				errAndDataList.add(ImpExpConsts._error,errList);
            			}
            			errList.addAll(err);
            			
            			if(err!=null&&err.size()>0){
            				if(!err.toString().contains("成功导入")){
                				
                				throw new UserException("导入路由点数据出错！");
            				}
            			}
            			GenericDO system=null;
        				if(dol!=null&&dol.size()>0){
        					try{
            					 system=addsegs(dol,segTemplate,branchTemplate,systemTemplate,districtCuid);
            					pointList.add(dol);
            					system.setAttrValue("ROUTE_POINTS", dol);
            					dataList.add(system);    
            				
        					}catch(Exception ex){
        						LogHome.getLog().error(ex.getMessage(),ex);
        						throw new UserException("根据点生成线设施出错！");
        					}

        				}
        				
        				 dataRtnList=(ArrayList) errAndDataList.get(ImpExpConsts._data);
            			if(dataRtnList==null){
            				dataRtnList=new ArrayList();
            				errAndDataList.add(ImpExpConsts._data,dataRtnList);
            			}
            			if(system!=null){
                			dataRtnList.add(system);	
            			}
        			}
            	}
            	if(isConnected){
            		if(pointList.size()>1){
            			GenericDO origPoint=null;
            			GenericDO destPoint=null;
            			for(int i=0;i<=pointList.size()-2;i++){
            				
            				DataObjectList beforeList=pointList.get(i);
            				if(beforeList!=null&&beforeList.size()>1){
            					origPoint=beforeList.get(beforeList.size()-1);
            				}else{
            					origPoint=null;
            					destPoint=null;
            					continue;
            				}
            				DataObjectList afterList=pointList.get(i+1);
            				if(afterList!=null&&afterList.size()>1){
            					destPoint=afterList.get(0);
            				}else{
            					origPoint=null;
            					destPoint=null;
            					continue;
            				}
            			}
            			if(origPoint!=null&&destPoint!=null){
            				DataObjectList dol=new DataObjectList();
            				dol.add(origPoint);
            				dol.add(destPoint);
            				GenericDO segTemplate=new UpLineSeg();
            				GenericDO systemTemplate=new UpLine();
            				GenericDO system=addsegs(dol,segTemplate,null,systemTemplate,districtCuid);
            				pointList.add(dol);
        					system.setAttrValue("ROUTE_POINTS", dol);
        					dataList.add(system);
        					if(system!=null){
                    			dataRtnList.add(system);	
                			}
            			}
            		}
            	}
            }

    		trx.commit();
    		return errAndDataList;
    	}catch(Exception ex){
    		trx.rollback();
    		ArrayList tmpList=(ArrayList) errAndDataList.get(ImpExpConsts._error);
    		tmpList.add("导入路由点出错");
    		return errAndDataList;
    	}
    }
    //CLIENT_TODO  含有调用不到的Bo  暂时注释
    public ArrayList generateSystemsByExcelExt(List <Map<String,DataObjectList>> list, String curMapCuid,String districtCuid,ArrayList newPointCuids,ArrayList modifyPointCuids,boolean isConnected) throws Exception {
//    	UserTransaction trx = TransactionFactory.getInstance().createTransaction();
    	ArrayList errAndDataList=new ArrayList();
    	errAndDataList.add(ImpExpConsts._error,new ArrayList());
    	errAndDataList.add(ImpExpConsts._data,new ArrayList());
    	try{
//    		trx.begin();
    		if(list!=null&&list.size()>0){
//    			List <GenericDO>dataList=new ArrayList<GenericDO>();
    			List <DataObjectList>pointList=new ArrayList<DataObjectList>();
    			ArrayList dataRtnList=null;
            	for(int i=0;i<list.size();i++){
            		Map<String,DataObjectList> map=list.get(i);
            		String key=(String) map.keySet().toArray()[0];
            		DataObjectList dol=map.get(key);
//            		ArrayList tempList=null;
            		GenericDO segTemplate=null;
            		GenericDO branchTemplate=null;
            		GenericDO systemTemplate=null;
            		String sql=null;
            		IDuctManagerBO bo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
            		GenericDO pointTempalte=null;
            		GenericDO firstNumDto=null;
            		if(dol!=null&&dol.size()>2){
               		    firstNumDto=dol.get(0);
                		if(key.startsWith(Manhle.CLASS_NAME)){
                		 pointTempalte=new Manhle();
               			 segTemplate=new DuctSeg();
               			 branchTemplate=new DuctBranch();
               			 systemTemplate=new DuctSystem();

               		}else if(key.startsWith(Pole.CLASS_NAME)){
               			
               			pointTempalte=new Pole();
               			 segTemplate=new PolewaySeg();
               			 branchTemplate=new PolewayBranch();
               			 systemTemplate=new PolewaySystem();
               		}else if(key.startsWith(Stone.CLASS_NAME)){
               			pointTempalte=new Stone();
               			 segTemplate=new StonewaySeg();
               			 branchTemplate=new StonewayBranch();
               			 systemTemplate=new StonewaySystem();
               		}else if(key.startsWith(Inflexion.CLASS_NAME)){
               			pointTempalte=new Inflexion();
               			 segTemplate=new HangWallSeg();
//               			 branchTemplate=new PolewayBranch();
               			 systemTemplate=new HangWall();
               		}else{
               			continue;
               		}	
            		}else{
            			continue;
            		}
            			
            			

            			
            			dataRtnList=(ArrayList) errAndDataList.get(ImpExpConsts._data);
            			if(dataRtnList==null){
            				dataRtnList=new ArrayList();
            				errAndDataList.add(ImpExpConsts._data,dataRtnList);
            			}
            			DataObjectList newPointList=new DataObjectList();
        				if(dol!=null&&dol.size()>0){
        					try{
        						String pointSql="SELECT " + Manhle.AttrName.cuid + ","+Manhle.AttrName.labelCn+" FROM " + pointTempalte.getClassName() + " WHERE " +Manhle.AttrName.labelCn +" IN(";
        						for (int j = 1; j < dol.size(); j++) {
                                    GenericDO dto = (GenericDO) dol.get(j);
                                    pointSql = pointSql + "'" + dto.getAttrValue(Manhle.AttrName.labelCn) + "',";
                                }
        						pointSql = pointSql.substring(0, pointSql.length() - 1) + ")";
        						
        						DataObjectList existedPoints=bo.getDMObjsBySql(new BoQueryContext(), pointTempalte, pointSql);
        						Map pointMap=new HashMap();
        						TopoHelper.putListToMap(existedPoints, pointMap);
        						
                       			String ductSegSql1 = "SELECT " + DuctSeg.AttrName.origPointCuid + ","+ DuctSeg.AttrName.destPointCuid + ","+DuctSeg.AttrName.cuid+","+DuctSeg.AttrName.relatedBranchCuid+","+DuctSeg.AttrName.relatedSystemCuid+","+DuctSeg.AttrName.labelCn+" FROM " + segTemplate.getClassName() + " WHERE " + DuctSeg.AttrName.origPointCuid +" IN(";
                                String ductSegSql2 = "SELECT " + DuctSeg.AttrName.origPointCuid + ","+ DuctSeg.AttrName.destPointCuid + ","+DuctSeg.AttrName.cuid+","+DuctSeg.AttrName.relatedBranchCuid+","+DuctSeg.AttrName.relatedSystemCuid+","+DuctSeg.AttrName.labelCn+" FROM " + segTemplate.getClassName() + " WHERE " + DuctSeg.AttrName.destPointCuid +" IN(";
                    			Map <String,GenericDO> segMap=new HashMap<String,GenericDO> ();
                    			DataObjectList newPointsWithCuid=new DataObjectList();
                                if(existedPoints!=null&&existedPoints.size()>0){
                                	newPointsWithCuid.addAll(existedPoints);
                                    for (int j = 0; j < existedPoints.size(); j++) {
                                        GenericDO dto = (GenericDO) existedPoints.get(j);
                                        ductSegSql1 = ductSegSql1 + "'" + dto.getCuid() + "',";
                                        ductSegSql2 = ductSegSql2 + "'" + dto.getCuid() + "',";
                                    }
                                    String ductSegSql = ductSegSql1.substring(0, ductSegSql1.length() - 1) + ") UNION " +ductSegSql2.substring(0, ductSegSql2.length() - 1) + ")";
                        			DataObjectList segs=bo.getDMObjsBySql(new BoQueryContext(), segTemplate, ductSegSql);
                        			if(segs!=null&&segs.size()>0){
                        				for(GenericDO seg:segs){
                        					String origPointCuid=(String) seg.getAttrValue(DuctSeg.AttrName.origPointCuid);
                        					String destPointCuid=(String) seg.getAttrValue(DuctSeg.AttrName.destPointCuid);
                        					String origPoint=null;
                        					String destPoint=null;
                        					if(pointMap.get(origPointCuid)!=null){
                        						origPoint=(String) ((GenericDO)pointMap.get(origPointCuid)).getAttrValue(Manhle.AttrName.labelCn);
                        					}
                        					if(pointMap.get(destPointCuid)!=null){
                        						 destPoint=(String) ((GenericDO)pointMap.get(destPointCuid)).getAttrValue(Manhle.AttrName.labelCn);
                        					}

                        					if(origPoint!=null&&destPoint!=null){
                            					segMap.put(origPoint+"_"+destPoint, seg);
                            					segMap.put(destPoint+"_"+origPoint, seg);	
                        					}
                        				}
                        			}	
                                }
        						ArrayList tempList=null;
                                
                                
                                
                				DataObjectList newPoints=new DataObjectList();
                				GenericDO system=null;
                    			for(int j=1;j<dol.size()-1;j++){
                    				GenericDO orig=dol.get(j);
                    				GenericDO dest=dol.get(j+1);
                    				GenericDO tmpSeg=segMap.get(orig.getAttrValue(DuctSeg.AttrName.labelCn)+"_"+dest.getAttrValue(DuctSeg.AttrName.labelCn));
//                    				newPoints.add(orig);

                    				if(tmpSeg!=null){
                    					if(newPoints.size()>1){
                    						newPoints.add(0, firstNumDto);

                    						if(key.startsWith(Manhle.CLASS_NAME)){
                    							tempList=importManhleExt(newPoints, curMapCuid);
                    	               		}else if(key.startsWith(Pole.CLASS_NAME)){
                    	               			tempList=importPoleExt(newPoints, curMapCuid);
                    	               		}else if(key.startsWith(Stone.CLASS_NAME)){
                    	               			tempList=importStoneExt(newPoints, curMapCuid);
                    	               		}else if(key.startsWith(Inflexion.CLASS_NAME)){
                    	               			tempList=importInflexionExt(newPoints, curMapCuid);
                    	           		}
                    						if(tempList!=null&&tempList.size()>0){
                    							
//                    							newPointsWithCuid.addAll(tempList);
                        						DataObjectList points = (DataObjectList) tempList.get(ImpExpConsts._data);	
                        						newPointsWithCuid.addAll(points);
                                    			ArrayList err = (ArrayList) tempList.get(ImpExpConsts._error);
                                    			
                                    			ArrayList errList=(ArrayList) errAndDataList.get(ImpExpConsts._error);
                                    			if(errList==null){
                                    				errList=new ArrayList();
                                    				errAndDataList.add(ImpExpConsts._error,errList);
                                    			}
                                    			errList.addAll(err);
                                    			
                                    			if(err!=null&&err.size()>0){
                                    				if(!err.toString().contains("成功导入")){
                                        				
                                        				throw new UserException("导入路由点数据出错！");
                                    				}
                                    			}
//                                    			if(points!=null&&points.size()>0){
//                                    				for(GenericDO dto:points){
//                            							if (!dto.getAttrValue(ImpExpRow.modify_flag).equals(ImpExpRow.modify_flag)){
//                            								if(!newPointCuids.contains(dto.getCuid())){
//                            									newPointCuids.add(dto.getCuid());
//                            								}	
//                            							}else{
//                            								if(!modifyPointCuids.contains(dto.getCuid())){
//                            									modifyPointCuids.add(dto.getCuid());
//                            								}	
//                            							}
//                        							}
//                                    			}
                        						GenericDO newSystem=addsegs(points,segTemplate,branchTemplate,systemTemplate,districtCuid);
                        						DataObjectList newSystems=new DataObjectList();
                        						newSystems.add(newSystem);
                        						DataObjectList elements =DMHelper.getSegListBySystem(newSystems);
                        						if(elements!=null&&elements.size()>0){
                        							for(GenericDO gdo:elements){
                        								String segCuid=gdo.getCuid();
                        								if(!newPointCuids.contains(segCuid)){
                        									newPointCuids.add(segCuid);
                        								}
                        							}
                        						}
//                        						dataList.add(newSystem);
                        						dataRtnList.add(newSystem);                    							
                    						}

                    					}
                    					newPoints.clear();
                    					
                    					String branchCuid=(String) tmpSeg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid);
                    					String systemCuid=(String) tmpSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid);
                    					system=bo.getObjByCuid(new BoActionContext(), systemCuid);
                    					DataObjectList segList=new DataObjectList();
                    					segList.add(tmpSeg);
                    					if(branchCuid!=null&&branchCuid.length()>0){
                    						GenericDO branch=bo.getObjByCuid(new BoActionContext(), branchCuid);
                    						DataObjectList branchs=new DataObjectList();
                    						TopoHelper.setChildren(branch, segList);
                    						branchs.add(branch);
                    						TopoHelper.setChildren(system, branchs);
                    					}else{
                    						TopoHelper.setChildren(system, segList);
                    					}
//                    					dataList.add(system);
                    					dataRtnList.add(system);
                    				}else{
                    					List origList=newPoints.getObjectByAttr(Manhle.AttrName.labelCn, orig.getAttrValue(Manhle.AttrName.labelCn));
                    					if(origList.isEmpty()){
                    						newPoints.add(orig);
                    					}
                    					List destList=newPoints.getObjectByAttr(Manhle.AttrName.labelCn, dest.getAttrValue(Manhle.AttrName.labelCn));
                    					if(destList.isEmpty()){
                    						newPoints.add(dest);
                    					}
                    				} 
                    				
                    			}
                    			if(newPoints.size()>1){
            						newPoints.add(0, firstNumDto);
            						if(key.startsWith(Manhle.CLASS_NAME)){
            							tempList=importManhleExt(newPoints, curMapCuid);
            	               		}else if(key.startsWith(Pole.CLASS_NAME)){
            	               			tempList=importPoleExt(newPoints, curMapCuid);
            	               		}else if(key.startsWith(Stone.CLASS_NAME)){
            	               			tempList=importStoneExt(newPoints, curMapCuid);
            	               		}else if(key.startsWith(Inflexion.CLASS_NAME)){
            	               			tempList=importInflexionExt(newPoints, curMapCuid);
            	           		}
            						if(tempList!=null&&tempList.size()>0){
//            							newPointsWithCuid.addAll(tempList);
                						DataObjectList points = (DataObjectList) tempList.get(ImpExpConsts._data);	
                						newPointsWithCuid.addAll(points);
                            			ArrayList err = (ArrayList) tempList.get(ImpExpConsts._error);
                            			
                            			ArrayList errList=(ArrayList) errAndDataList.get(ImpExpConsts._error);
                            			if(errList==null){
                            				errList=new ArrayList();
                            				errAndDataList.add(ImpExpConsts._error,errList);
                            			}
                            			errList.addAll(err);
                            			
                            			if(err!=null&&err.size()>0){
                            				if(!err.toString().contains("成功导入")){
                                				
                                				throw new UserException("导入路由点数据出错！");
                            				}
                            			}
                						GenericDO newSystem=addsegs(points,segTemplate,branchTemplate,systemTemplate,districtCuid);
                						DataObjectList newSystems=new DataObjectList();
                						newSystems.add(newSystem);
                						DataObjectList elements =DMHelper.getSegListBySystem(newSystems);
                						if(elements!=null&&elements.size()>0){
                							for(GenericDO gdo:elements){
                								String segCuid=gdo.getCuid();
                								if(!newPointCuids.contains(segCuid)){
                									newPointCuids.add(segCuid);
                								}
                							}
                						}
//                						dataList.add(newSystem);
                						dataRtnList.add(newSystem);                    							
            						}

            					}
            					newPoints.clear();
            					
//            					String branchCuid=(String) tmpSeg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid);
//            					String systemCuid=(String) tmpSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid);
//            					system=bo.getObjByCuid(new BoActionContext(), systemCuid);
//            					DataObjectList segList=new DataObjectList();
//            					segList.add(tmpSeg);
//            					if(branchCuid==null||branchCuid.length()<1){
//            						GenericDO branch=bo.getObjByCuid(new BoActionContext(), branchCuid);
//            						DataObjectList branchs=new DataObjectList();
//            						TopoHelper.setChildren(branch, segList);
//            						branchs.add(branch);
//            						TopoHelper.setChildren(system, branchs);
//            					}else{
//            						TopoHelper.setChildren(system, segList);
//            					}
////            					dataList.add(system);
//            					dataRtnList.add(system);
            					
            					
                				Map tmpmap=new HashMap();
                				TopoHelper.putListToMapByAttr(newPointsWithCuid, tmpmap, Manhle.AttrName.labelCn);
                				for(int k=1;k<dol.size();k++){
                					GenericDO dto=dol.get(k);
                					if(dto.getCuid()==null||dto.getCuid().length()<1){
                						DataObjectList tmps=(DataObjectList) tmpmap.get(dto.getAttrValue(Manhle.AttrName.labelCn));
                						if(tmps!=null&&tmps.size()>0){
                							dto.setCuid(tmps.get(0).getCuid());
                							if(tmps.get(0).getCuid()!=null&&pointMap.get(tmps.get(0).getCuid())!=null){
                								if(!modifyPointCuids.contains(dto.getCuid())){
                    								modifyPointCuids.add(dto.getCuid());
                    							}	
                							}else{
                								if(!newPointCuids.contains(dto.getCuid())){
                    								newPointCuids.add(dto.getCuid());
                    							}	
                							}
                						}
                					}

                				}
                    			dol.remove(0);
            					pointList.add(dol);  
            				
        					}catch(Exception ex){
        						LogHome.getLog().error(ex.getMessage(),ex);
        						throw new UserException("根据点生成线设施出错！");
        					}

        				}
            	}
            	if(isConnected){
            		if(pointList.size()>1){
            			GenericDO origPoint=null;
            			GenericDO destPoint=null;
            			for(int i=0;i<=pointList.size()-2;i++){
            				
            				DataObjectList beforeList=pointList.get(i);
            				if(beforeList!=null&&beforeList.size()>1){
            					origPoint=beforeList.get(beforeList.size()-1);
            				}else{
            					origPoint=null;
            					destPoint=null;
            					continue;
            				}
            				DataObjectList afterList=pointList.get(i+1);
            				if(afterList!=null&&afterList.size()>1){
            					destPoint=afterList.get(0);
            				}else{
            					origPoint=null;
            					destPoint=null;
            					continue;
            				}
            			}
            			if(origPoint!=null&&destPoint!=null){
            				DataObjectList dol=new DataObjectList();
            				dol.add(origPoint);
            				dol.add(destPoint);
            				GenericDO segTemplate=new UpLineSeg();
            				GenericDO systemTemplate=new UpLine();
            				GenericDO system=addsegs(dol,segTemplate,null,systemTemplate,districtCuid);
            				DataObjectList newSystems=new DataObjectList();
    						newSystems.add(system);
    						DataObjectList elements =DMHelper.getSegListBySystem(newSystems);
    						if(elements!=null&&elements.size()>0){
    							for(GenericDO gdo:elements){
    								String segCuid=gdo.getCuid();
    								if(!newPointCuids.contains(segCuid)){
    									newPointCuids.add(segCuid);
    								}
    							}
    						}
            				pointList.add(dol);
        					system.setAttrValue("ROUTE_POINTS", dol);
//        					dataList.add(system);
        					if(system!=null){
                    			dataRtnList.add(system);	
                			}
            			}
            		}
            	}
            }

//    		trx.commit();
    		return errAndDataList;
    	}catch(Exception ex){
//    		trx.rollback();
    		ArrayList tmpList=(ArrayList) errAndDataList.get(ImpExpConsts._error);
    		tmpList.add("导入路由点出错");
    		return errAndDataList;
    	}
    }
    private GenericDO addsegs(DataObjectList points,GenericDO segTemplate,GenericDO branchTemplate,GenericDO systemTemp,String districtCuid) throws Exception {
    	try{
        	if(systemTemp!=null&&segTemplate!=null&&points!=null&&points.size()>1){
        		GenericDO systemTemplate=DMHelper.getTemplateDoByClassName(systemTemp.getClassName());
        		GenericDO firstPoint=points.get(0);
        		GenericDO lastPoint=points.get(points.size()-1);
        		String name=""+firstPoint.getAttrValue(GenericDO.AttrName.labelCn)+"-"+lastPoint.getAttrValue(GenericDO.AttrName.labelCn);
        		systemTemplate.setAttrValue(GenericDO.AttrName.labelCn, name);
        		
        	
        		systemTemplate.setAttrValue(LineSystem.AttrName.relatedSpaceCuid, districtCuid);
        		IDuctManagerBO bo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
        		String sql=DuctSystem.AttrName.labelCn+"='"+name+"'";
        		DboCollection dbos=bo.getDataBySql(new BoQueryContext(), sql, systemTemp.getClassName());
        		if(dbos!=null&&dbos.size()>0){
        			Timestamp date=new Timestamp(new Date().getTime());
        			name=name+date;
        			systemTemplate.setAttrValue(GenericDO.AttrName.labelCn, name);
        		}

        		GenericDO system=bo.createDMDO(new BoActionContext(),systemTemplate);
        		GenericDO branch=null;
        		if(branchTemplate!=null){
        			branchTemplate.setAttrValue(GenericDO.AttrName.labelCn, name);
        			branchTemplate.setAttrValue(DuctBranch.AttrName.relatedSystemCuid, system.getCuid());
        			branchTemplate.setAttrValue(DuctBranch.AttrName.origPointCuid, firstPoint.getCuid());
        			branchTemplate.setAttrValue(DuctBranch.AttrName.destPointCuid, lastPoint.getCuid());
        			branch=bo.createDMDO(new BoActionContext(), branchTemplate);
        			DataObjectList branchs=new DataObjectList();
        			branchs.add(branch);
        			TopoHelper.setChildren(system, branchs);
        		}
        		DataObjectList segs=modifyRoute(system,branch,points);
        		if(branch!=null){
        			TopoHelper.setChildren(branch, segs);
        		}else{
        			TopoHelper.setChildren(system, segs);
        		}
        		return system;
        	}	
        	return null;
    	}catch(Exception ex){
    		LogHome.getLog().error("ImportConverter.addsegs出错",ex);
    		throw new UserException(ex.getMessage());	
    	}
    }
    private DataObjectList modifyRoute(GenericDO system,GenericDO branch,DataObjectList points) throws Exception {
        DataObjectList list = new DataObjectList();
        if (branch != null && DMHelper.isBranchClassName(branch.getClassName())) {
            list.add(branch);
        } else {
            list.add(system);
        }
        GenericDO segtemplate=DMHelper.getSegTemplateBySystem(system.getClassName());
        DataObjectList segtemplalist = new DataObjectList();
        segtemplalist.add(segtemplate);
        DataObjectList allsegs;
        try {
        	for(GenericDO point:points){
        		point.setAttrValue("DISPLAY_POINT", true);
        	}
        	IDuctManagerBO bo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
        	List rlist =bo.saveDbosAndRoute(new BoActionContext(), list, points, segtemplalist);
            
            return (DataObjectList) rlist.get(0);
        } catch (Exception ex) {
        	LogHome.getLog().error("ImportConverter.modifyRoute出错",ex);
        	throw new UserException(ex.getMessage());
        }
    }
    public ArrayList importAccesspoint(Workbook workbook) throws Exception {
        return importAccesspoint(workbook, "");
    }
    public ArrayList importAccesspoint(Workbook workbook, String curMapCuid) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map accesspointNames = new HashMap();
        IDataImportBO bo = (IDataImportBO) BoHomeFactory.getInstance().getBO(BoName.DataImportBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            Accesspoint dbo = new Accesspoint();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.labelCn, labelCn);
            
            String vpCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属客户", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.vpCuid, vpCuid);
            
            String vpnCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属VPN", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.vpnCuid, vpnCuid);
            
            String districtCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, err);
            dbo.setAttrValue(Accesspoint.AttrName.districtCuid, districtCuid);
            
            String siteCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属站点", i, err);
            dbo.setAttrValue(Accesspoint.AttrName.siteCuid, siteCuid);
            
            String roomCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属机房", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.roomCuid, roomCuid);
            
            String longitude = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.longitude, longitude);
            
            String latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.latitude, latitude);
            
            String location = ImpExpUtils.getSheetValueByLabel(sheet, "地址", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.location, location);
            
            String vpContact = ImpExpUtils.getSheetValueByLabel(sheet, "客户联系人", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.vpContact, vpContact);
            
            String vpPhone = ImpExpUtils.getSheetValueByLabel(sheet, "客户联系电话", i, err);
            dbo.setAttrValue(Accesspoint.AttrName.vpPhone, vpPhone);
            
            String maintainMan = ImpExpUtils.getSheetValueByLabel(sheet, "维护经理", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.maintainMan, maintainMan);
            
            String maintainPhone = ImpExpUtils.getSheetValueByLabel(sheet, "维护经理电话", i, err); 
            dbo.setAttrValue(Accesspoint.AttrName.maintainPhone, maintainPhone);
            //add  by 肖顺梅   2012-10-15 接入点增加预覆盖功能
            String isYjr = ImpExpUtils.getSheetValueByLabel(sheet, "是否预覆盖接入点", i, err); //IS_YJR
            dbo.setAttrValue(Accesspoint.AttrName.isYjr, isYjr);
            String bossCode = ImpExpUtils.getSheetValueByLabel(sheet, "BOSS编码", i, err); //BOSS_CODE
            dbo.setAttrValue(Accesspoint.AttrName.bossCode, bossCode);
            String vpLableCn = ImpExpUtils.getSheetValueByLabel(sheet, "客户名称", i, err); //VP_LABEL_CN
            dbo.setAttrValue(Accesspoint.AttrName.vpLabelCn, vpLableCn);
            //end add  by  肖顺梅

            if (err.size() != 0) {
                errorlist.add("导入数据失败,请您修改表格之后重新导入。");
                return err;
            }
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (accesspointNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + accesspointNames.get(labelCn) + "资源点名称重复！");
                } else {
                	accesspointNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }
        
      //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
        
        if (errorlist.size() != 0) {
            errorlist.add("导入数据失败！请您校验数据之后重新导入。");
            return errorlist;
        }
        //调用服务器端接口
        errorlist = bo.importAccesspoint(new BoActionContext(), dbos,districts, curMapCuid);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入接入点数据" + (dbos.size() - 1) + "条。");
            return errorlist;
        } else {
            errorlist.add("导入数据失败！请您校验数据之后重新导入。");
            return errorlist;
        }
    }
    
    /*****************************cs新增*********************************/
    
    //导入DDF(ODF)端子和端子的跳线(跳纤)
    public ArrayList importXdfJump(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList jumpPairdbos = new DataObjectList();
        DataObjectList jumpFiberdbos = new DataObjectList();
        DataObjectList jumpPairOrigPointdbos = new DataObjectList();
        DataObjectList jumpPairDestPointdbos = new DataObjectList();
        DataObjectList jumpFiberOrigPointdbos = new DataObjectList();
        DataObjectList jumpFiberDestPointdbos = new DataObjectList();
        ArrayList jumpPairLines = new ArrayList();
        ArrayList jumpFiberLines = new ArrayList();
        Map allXdps = new HashMap();

        ArrayList errorlist = new ArrayList();

        ArrayList errorlists = new ArrayList();
        //加载全部DDF
//        Map allDdfsMap = new HashMap();

        //加载全部ODF
//        Map allOdfsMap = new HashMap();

        //加载全部综合机架
//        Map allMiscracksMap = new HashMap();

//        SiteBO siteBO = (SiteBO) BoHomeFactory.getInstance().getBO(BoName.SiteBO);
//        IRoomBO roombo = (IRoomBO) BoHomeFactory.getInstance().getBO(IRoomBO.class);
        int rowno = 0;
        for (int i = 1; i < rowcount; i++) {
           
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            rowno = i + 1;
            LogHome.getLog().info("开始验证第" + i + "/" + rowcount + "行关系!");
            int t = errorlist.size();
            String districtName = ImpExpUtils.getSheetValueByLabel(sheet, "区域", i, errorlist);
            String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "站点", i, errorlist);
            String roomName = ImpExpUtils.getSheetValueByLabel(sheet, "机房", i, errorlist);
            String aXdfType = ImpExpUtils.getSheetValueByLabel(sheet, "A端配线架类型", i, errorlist);
            String aXdfName = ImpExpUtils.getSheetValueByLabel(sheet, "A端配线架名称", i, errorlist);
            String aModuleName = ImpExpUtils.getSheetValueByLabel(sheet, "A端模块名称", i, errorlist);
            String aRows = ImpExpUtils.getSheetValueByLabel(sheet, "A端端子所在行号", i, errorlist);
            String aCols = ImpExpUtils.getSheetValueByLabel(sheet, "A端端子所在列号", i, errorlist);
            String zXdfType = ImpExpUtils.getSheetValueByLabel(sheet, "Z端配线架类型", i, errorlist);
            String zXdfName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端配线架名称", i, errorlist);
            String zModuleName = ImpExpUtils.getSheetValueByLabel(sheet, "Z端模块名称", i, errorlist);
            String zRows = ImpExpUtils.getSheetValueByLabel(sheet, "Z端端子所在行号", i, errorlist);
            String zCols = ImpExpUtils.getSheetValueByLabel(sheet, "Z端端子所在列号", i, errorlist);
            String isFixed = ImpExpUtils.getSheetValueByLabel(sheet, "是否固定连接", i, errorlist);

            if (errorlist.size() > t) {
                break;
            } else {
                t = errorlist.size();
            }
            if (districtName == null || (districtName != null && districtName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,区域不能为空!");
            }
            if (siteName == null || (siteName != null && siteName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,站点不能为空!");
            }
            if (roomName == null || (roomName != null && roomName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,机房不能为空!");
            }
            if (aXdfType == null ||
                (aXdfType != null && !aXdfType.trim().equals("DDF") && !aXdfType.trim().equals("ODF")) &&
                !aXdfType.trim().equals("综合机架")) {
                errorlist.add("非法数据: 第 " + rowno + " 行, A端配线架类型填写错误!");
            }
            if (aXdfName == null || (aXdfName != null && aXdfName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,A端配线架名称不能为空!");
            }
            if (aModuleName == null || (aModuleName != null && aModuleName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,A端模块名称不能为空!");
            }
            if (aRows == null || (aRows != null && aRows.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,A端端子所在行号不能为空!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(aRows);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,A端端子所在行号数值转换错误!");
                }
            }
            if (aCols == null || (aCols != null && aCols.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,A端端子所在列号不能为空!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(aCols);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,A端端子所在列号数值转换错误!");
                }
            }
            if (zXdfType == null ||
                (zXdfType != null && !zXdfType.trim().equals("DDF") && !zXdfType.trim().equals("ODF")) &&
                !zXdfType.trim().equals("综合机架")) {
                errorlist.add("非法数据: 第 " + rowno + " 行, Z端配线架类型填写错误!");
            }
            if (zXdfName == null || (zXdfName != null && zXdfName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,Z端配线架名称不能为空!");
            }
            if (zModuleName == null || (zModuleName != null && zModuleName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,Z端模块名称不能为空!");
            }
            if (zRows == null || (zRows != null && zRows.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,Z端端子所在行号不能为空!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(zRows);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,Z端端子所在行号数值转换错误!");
                }
            }
            if (zCols == null || (zCols != null && zCols.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,Z端端子所在列号不能为空!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(zCols);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,Z端端子所在列号数值转换错误!");
                }
            }
            if (aXdfType != null && !aXdfType.trim().equals("综合机架") && zXdfType != null && !zXdfType.trim().equals("综合机架") &&
                !aXdfType.trim().equals(zXdfType.trim())) {
                errorlist.add("非法数据: 第 " + rowno + " 行, A、Z两端配线架类型不一致!");
            }
            if (isFixed == null || (isFixed != null && !isFixed.trim().equals("是") && !isFixed.trim().equals("否"))) {
                errorlist.add("非法数据: 第 " + rowno + " 行, 是否固定连接填写错误!");
            }
            String curxdp = "";
            Integer findrow = 0;
            curxdp = siteName + "-" + roomName + "-" + aXdfType + "-" + aXdfName + "-" + aModuleName + "-" + aRows + "-" + aCols + "-" + isFixed;
            findrow = (Integer) allXdps.get(curxdp);
            if (findrow != null && findrow > 0) {
                errorlist.add("非法数据: 第 " + rowno + " 行, A端配线架端子与第" + findrow + "行配线架端子重复!");
            } else {
                allXdps.put(curxdp, rowno);
            }
            curxdp = siteName + "-" + roomName + "-" + zXdfType + "-" + zXdfName + "-" + zModuleName + "-" + zRows + "-" + zCols + "-" + isFixed;
            findrow = (Integer) allXdps.get(curxdp);
            if (findrow != null && findrow > 0) {
                errorlist.add("非法数据: 第 " + rowno + " 行, Z端配线架端子与第" + findrow + "行配线架端子重复!");
            } else {
                allXdps.put(curxdp, rowno);
            }

            if (errorlist.size() == t) {
            	Map maps = getDataImportBO().getXdfJump(districtName, siteName, roomName, aXdfType, aXdfName, aModuleName, aRows, aCols, zXdfType, zXdfName, zModuleName, zRows, zCols, isFixed, rowno, errorlist);
            	
            	errorlist = (ArrayList) maps.get("error");
            	
            	DataObjectList jumpPairs = (DataObjectList) maps.get("jumppair");
                DataObjectList jumpPairOrig = (DataObjectList) maps.get("jumppaireorig");
                DataObjectList jumpPairDest = (DataObjectList) maps.get("jumppairdest");
                ArrayList jumpPairLine = (ArrayList) maps.get("jumppairline");
                 
                 
                DataObjectList jumpFibers = (DataObjectList) maps.get("jumpfiber");
                DataObjectList jumpFiberOrig = (DataObjectList) maps.get("jumpfiberorig");
                DataObjectList jumpFiberDest = (DataObjectList) maps.get("jumpfiberdest");
                ArrayList jumpFiberLine = (ArrayList) maps.get("jumpfiberline");
                
                jumpPairdbos.addAll(jumpPairs);
                jumpPairOrigPointdbos.addAll(jumpPairOrig);
                jumpPairDestPointdbos.addAll(jumpPairDest);
                jumpPairLines.addAll(jumpPairLine);
                
                jumpFiberdbos.addAll(jumpFibers);
                jumpFiberOrigPointdbos.addAll(jumpFiberOrig);
                jumpFiberDestPointdbos.addAll(jumpFiberDest);
                jumpFiberLines.addAll(jumpFiberLine);
                
            	errorlists.addAll(errorlist);
            	errorlist.clear();
            }
        }
        if (rowno > 1) {
            if (jumpPairLines.size() > 0) {

                List templist = getDataImportBO().addJumpPairsFromExcel(new BoActionContext(), jumpPairdbos, jumpPairOrigPointdbos, jumpPairDestPointdbos, jumpPairLines);
                if (templist.size() > 0) {
                    for (int i = 0; i < templist.size(); i++) {
                    	errorlists.add(templist.get(i));
                    }
                }
            }
            if (jumpFiberLines.size() > 0) {

                List templist = getDataImportBO().addJumpFibersFromExcel(new BoActionContext(), jumpFiberdbos, jumpFiberOrigPointdbos, jumpFiberDestPointdbos, jumpFiberLines);
                if (templist.size() > 0) {
                    for (int i = 0; i < templist.size(); i++) {
                    	errorlists.add(templist.get(i));
                    }
                }
            }
        } else {
        	errorlists.add("Excel中没有数据!");
        }
        return errorlists;
    }
    
    //导入ODF端子和光缆纤芯连接
    public ArrayList importODFPortFiberConnect(Workbook workbook) throws Exception {
        List errorlist = new ArrayList();
        ArrayList errorlists = new ArrayList();

        BoActionContext actionContext = new BoActionContext();

        District district = null;
        Site site = null;
        Room room = null;
        Long r = 0L;
        Long c = 0L;

        Map<String, List<Integer>> fiberMap = new HashMap<String, List<Integer>>(); //缓存EXCEL中定义过的纤芯
        Map<String, List<Integer>> roomodfodmportMap = new HashMap<String, List<Integer>>(); //缓存EXCEL中定义过的端子
        Map<String, Integer> conMap = new HashMap<String, Integer>(); //缓存EXCEL中定义过的关系

        Map<String, WireSystem> wsMap = new HashMap<String, WireSystem>(); //缓存查过的光缆
        Map<String, Map<String, DataObjectList>> wrOMap = new HashMap<String, Map<String, DataObjectList>>(); //缓存起始端是站点的纤芯
        Map<String, Map<String, DataObjectList>> wrDMap = new HashMap<String, Map<String, DataObjectList>>(); //缓存终止端是站点的纤芯
        
        DataObjectList odfportList = new DataObjectList(); //存放通过校验后的odf端子
        DataObjectList fiberList = new DataObjectList(); //存放通过校验后的纤芯
        //构建WireSystem WireSeg 缓存
        List mapsList = getDataImportBO().creatWireSystemAndWireSegCache();
        wsMap = (Map) mapsList.get(0);
        wrOMap = (Map) mapsList.get(1);
        wrDMap = (Map) mapsList.get(2);
        
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        //在EXCEL 中校验
        for (int i = 1; i < rowcount; i++) {
        	Map listmaps = new HashMap();
            //剔除空行
    	    if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            int t = errorlist.size(); //0
            //读取数据，校验数据列是否存在
            String districtName = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, errorlist);
            String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "所属站点", i, errorlist);
            String roomName = ImpExpUtils.getSheetValueByLabel(sheet, "所属机房", i, errorlist);
            String rackType = ImpExpUtils.getSheetValueByLabel(sheet, "设备类型", i, errorlist);
            String rackName = ImpExpUtils.getSheetValueByLabel(sheet, "机架名称", i, errorlist);
            String ODMName = ImpExpUtils.getSheetValueByLabel(sheet, "ODM名称", i, errorlist);
            String portName = ImpExpUtils.getSheetValueByLabel(sheet, "端子名称", i, errorlist);
            String rowNum = ImpExpUtils.getSheetValueByLabel(sheet, "端子所在行号", i, errorlist);
            String colNum = ImpExpUtils.getSheetValueByLabel(sheet, "端子所在列号", i, errorlist);
            String wireName = ImpExpUtils.getSheetValueByLabel(sheet, "光缆名称", i, errorlist);
            String wireSegName = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段名称", i, errorlist);
            String OrigName = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段起点名称", i, errorlist);
            String DestName = ImpExpUtils.getSheetValueByLabel(sheet, "光缆段终点名称", i, errorlist);
            String wireNO = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯编号", i, errorlist);
            //数据列不存在
            if (errorlist.size() > 0) {
                errorlists.addAll(errorlist);
                break;
            }

            String disCuid = getDistrictBO().getCuidByLabelCn(new BoActionContext(), districtName);
            district = null;
            site = null;
            room = null;
            //数据非空、所属空间、数据类型校验
            if (districtName == null || (districtName != null && districtName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,所属区域不能为空!");
            } else {
            	district = getDistrictBO().getDistrictByCuid(actionContext,disCuid);
//                district = DistrictModel.getInstance().getDistrictByLabelCn(districtName);
                if (district == null) {
                    errorlist.add("非法数据: 第 " + i + " 行, 区域 '" + districtName + "' 不存在!");
                }
            }
            if (siteName == null || (siteName != null && siteName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,所属站点不能为空!");
            } else {
                site = (Site)getSiteBO().getSiteByName(new BoActionContext(), siteName).get(0);//SiteModel.getInstance().getSiteByLabelCn(siteName);
                if (site == null) {
                    errorlist.add("非法数据: 第 " + i + " 行, 站点 '" + siteName + "' 不存在!");
                } else if (district != null) {
                    if (!site.getRelatedSpaceCuid().equals(district.getCuid())) {
                        errorlist.add("非法数据: 第 " + i + " 行, 站点 '" + siteName + "' 的所属区域不是区域 '" + districtName + "' ！");
                    }
                }
            }
            if (roomName == null || (roomName != null && roomName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,所属机房不能为空!");
            } else {
                if (site != null) {
                    room = ImpExpUtils.getRoomByRoomNameAndSiteCuid(roomName, site.getCuid());
                }
                if (room == null) {
                    errorlist.add("非法数据: 第 " + i + " 行, 机房 '" + roomName + "' 不存在!");
                } else {
                    String roomCuid = room.getRelatedSpaceCuid();
                    if (site != null) {
                        if (!roomCuid.equals(site.getCuid())) {
                            errorlist.add("非法数据: 第 " + i + " 行, 机房 '" + roomName + "' 的所属站点不是站点 '" + siteName + "' ！");
                        }
                    }
                }
            }
            if (rackType == null) {
                errorlist.add("非法数据: 第 " + i + " 行, 设备类型不能为空!");
            }
            if (rackType != null && !rackType.trim().equals("ODF") && !rackType.trim().equals("综合机架")) {
                errorlist.add("非法数据: 第 " + i + " 行, 设备类型填写错误!");
            }
            if (rackName == null || (rackName != null && rackName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,ODF架名称不能为空!");
            }
            if (ODMName == null || (ODMName != null && ODMName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,ODM名称不能为空!");
            }
            if (rowNum == null || (rowNum != null && rowNum.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,端子所在行号不能为空!");
            } else {
                try {
                    r = Long.valueOf(rowNum.trim());
                } catch (Exception e) {
                    errorlist.add("非法数据: 第 " + i + " 行, 端子所在行号数值转换错误!");
                }
            }
            if (colNum == null || (colNum != null && colNum.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,端子所在列号不能为空!");
            } else {
                try {
                    c = Long.valueOf(colNum.trim());
                } catch (Exception e) {
                    errorlist.add("非法数据: 第 " + i + " 行, 端子所在列号数值转换错误!");
                }
            }
            if (wireName == null || (wireName != null && wireName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,光缆名称不能为空!");
            }
            if (OrigName == null || (OrigName != null && OrigName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,光缆段起点名称不能为空!");
            }
            if (DestName == null || (DestName != null && DestName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,光缆段终点名称不能为空!");
            }
            if (OrigName != null && !OrigName.equals(siteName) && DestName != null && !DestName.equals(siteName)) {
                errorlist.add("非法数据: 第 " + i + " 行,光缆段起止点名称至少要有一个和 '所属站点' 相同!");
            }
            if (wireNO == null || (wireNO != null && wireNO.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + i + " 行,纤芯编号不能为空!");
            } else {
                try {
                    int w = Integer.valueOf(wireNO.trim());
                } catch (Exception e) {
                    errorlist.add("非法数据: 第 " + i + " 行, 纤芯编号数值转换错误!");
                }
            }

            //excel中端子、纤芯唯一性校验
            String portStr = roomName + rackName + ODMName + rowNum + colNum; //roomName 已经包含了对区域站点的校验
            String fiberStr = wireName + OrigName + DestName + wireNO;
            String conStr = portStr + "@" + fiberStr;
            if (roomodfodmportMap.containsKey(portStr)) {
                List<Integer> indexList = roomodfodmportMap.get(portStr);
                if (indexList.size() >= 2) {
                    errorlist.add("非法数据: 第 " + i + " 行ODF端子和EXCEL中第 " + indexList.get(0) + "行 和 第" + indexList.get(1) + " 行ODF端子定义重复!");
                } else {
                    indexList.add(i);
                    roomodfodmportMap.put(portStr, indexList);
                }
            } else {
                List<Integer> indexList = new ArrayList<Integer>();
                indexList.add(i);
                roomodfodmportMap.put(portStr, indexList);
            }
            if (fiberMap.containsKey(fiberStr)) {
                List<Integer> indexList = fiberMap.get(fiberStr);
                if (indexList.size() >= 2) {
                    errorlist.add("非法数据: 第 " + i + " 行纤芯和EXCEL中第 " + indexList.get(0) + "行 和 第" + indexList.get(1) + " 行纤芯定义重复!");
                } else {
                    indexList.add(i);
                    fiberMap.put(fiberStr, indexList);
                }

            } else {
                List<Integer> indexList = new ArrayList<Integer>();
                indexList.add(i);
                fiberMap.put(fiberStr, indexList);
            }

            if (conMap.containsKey(conStr)) {
                errorlist.add("非法数据: 第 " + i + " 行连接关系和第 " + conMap.get(conStr) + " 行连接关系重复!");
            } else {
                conMap.put(conStr, i);
            }

            if (errorlist.size() == 0) {
            	listmaps = getDataImportBO().getODFPortFiberConnect(i, site.getCuid(), errorlists,room.getCuid(), roomName, rackType, rackName, ODMName, wireName, wireSegName, OrigName, DestName, siteName, portName, wireNO, r, c,wsMap,wrOMap,wrDMap);
            }
            if(listmaps != null && listmaps.size()>0){
            	 errorlist = (List) listmaps.get("error");
                 DataObjectList fibers = (DataObjectList) listmaps.get("fiber");
                 DataObjectList odfports = (DataObjectList) listmaps.get("odfport");
                 
                 errorlists.addAll(errorlist);
                 fiberList.addAll(fibers);
                 odfportList.addAll(odfports);
                 errorlist.clear();
            }else{
            	errorlists.addAll(errorlist);
            }
        }

        if (errorlists.size() == 0) {

//        	((IFiberBO) getPhyOdfPortBO()).modifyFibers(new BoActionContext(), fiberList);
        	
        	getDataImportBO().modifyFibers(ActionContextUtil.getActionContext(), fiberList);
        	getDataImportBO().modifyPhyODFPorts(ActionContextUtil.getActionContext(), odfportList);
            errorlists.add("成功导入: " + odfportList.size() + " 条数据！");
        }
        //清除缓存
        fiberMap.clear();
        roomodfodmportMap.clear();
        conMap.clear();
        wsMap.clear();
//        wrOMapTemp.clear();
//        wrDMapTemp.clear();
        wrOMap.clear();
        wrDMap.clear();

        return errorlists;
    }
    //架端子光纤链接属性(备注信息)
    public ArrayList importDdfPortRemark(Workbook workbook) throws Exception{

        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        Map allDdps = new HashMap();
        int importcount = 0;
        
        DataObjectList lists = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        //加载全部ODF
        Map allOdfsMap = new HashMap();
        /*IBoCommand cmd = BoCmdFactory.getInstance().createBoCmd(PhyOdfBOHelper.ActionName.getAllOdfs,
            new Object[] {new BoActionContext()});*/
        
//        getPhyOdfBO().getAllOdfs(new BoActionContext());

        //加载全部综合机架
        Map allMiscracksMap = new HashMap();
        int rowno = 0;
        for (int i = 1; i < rowcount; i++) {
        	DataObjectList list = new DataObjectList();
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                break;
            }
            rowno = i + 1;
            LogHome.getLog().info("开始验证第" + i + "/" + rowcount + "行记录!");
            int t = errorlist.size();
            String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "站点", i, errorlist);
            String roomName = ImpExpUtils.getSheetValueByLabel(sheet, "机房", i, errorlist);
            String xdfName = ImpExpUtils.getSheetValueByLabel(sheet, "配线架名称", i, errorlist);
            String moduleName = ImpExpUtils.getSheetValueByLabel(sheet, "配线架模块名称", i, errorlist);
            String rows = ImpExpUtils.getSheetValueByLabel(sheet, "端子在模块中行号", i, errorlist);
            String cols = ImpExpUtils.getSheetValueByLabel(sheet, "端子在模块中列号", i, errorlist);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, errorlist);
            if (errorlist.size() > t) {
                break;
            } else {
                t = errorlist.size();
            }
            //验证数据合法性
            if (siteName == null || (siteName != null && siteName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,站点不能为空,此条不予导入!");
            }
            if (roomName == null || (roomName != null && roomName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,机房不能为空,此条不予导入!");
            }
            if (xdfName == null || (xdfName != null && xdfName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,配线架名称不能为空,此条不予导入!");
            }
            if (moduleName == null || (moduleName != null && moduleName.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,模块名称不能为空,此条不予导入!");
            }
            if (rows == null || (rows != null && rows.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,端子所在行号不能为空,此条不予导入!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(rows);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,端子所在行号数值转换错误,此条不予导入!");
                }
            }
            if (cols == null || (cols != null && cols.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,端子所在列号不能为空,此条不予导入!");
            } else {
                int temp = 0;
                try {
                    temp = Integer.valueOf(cols);
                } catch (Exception e) {
                    temp = 0;
                }
                if (temp == 0) {
                    errorlist.add("非法数据: 第 " + rowno + " 行,端子所在列号数值转换错误,此条不予导入!");
                }
            }
            if (remark == null || (remark != null && remark.trim().length() == 0)) {
                errorlist.add("非法数据: 第 " + rowno + " 行,备注不能为空,此条不予导入!");
            }

            String curddp = siteName + "-" + roomName + "-" + xdfName + "-" + moduleName + "-" + rows + "-" + cols;
            Integer findrow = (Integer) allDdps.get(curddp);
            if (findrow != null && findrow > 0) {
                errorlist.add("非法数据: 第 " + rowno + " 行, 配线架端子与第" + findrow + "行配线架端子重复,此条不予导入!");
            } else {
                allDdps.put(curddp, rowno);
            }
            //验证数据有效性
            if (errorlist.size() == t) {
            	Map maplists = getDataImportBO().getOdpRemark(siteName, roomName, xdfName, moduleName, rowno, rows, cols, remark);
            	
            	ArrayList listerror = (ArrayList) maplists.get("error");
            	list = (DataObjectList) maplists.get("maplist");
            	errorlist.addAll(listerror);
            	lists.addAll(list);
            }
        }
        if (rowno > 1) {
        	getDataImportBO().modifyPhyODFPorts(new BoActionContext(), lists);
            errorlist.add("导入了" + lists.size() + "条数据!");
        } else {
            errorlist.add("Excel中没有数据!");
        }
        return errorlist;
    }
    
    
    /**
     * 接头盒纤芯接续
     * @Description: 
     * @param workbook
     * @return
     * @throws Exception    
     * @author Gaoxf
     */
    public ArrayList importFBoxFiberConn(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheet(0);
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        if(!ImpExpUtils.isEmptyRow(sheet, 0)&& !ImpExpUtils.isEmptyRow(sheet, 1)){
        for (int i = 2; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                break;
            }

            FiberJointBox box = new FiberJointBox();
            String aWireSystemLablecn = getCellValue(sheet, 0, i, errorlist,false,"A端光缆系统名称");
            box.setAttrValue("A_WIRE_SYSTEM", aWireSystemLablecn);
            
            String aWireSegLablecn = getCellValue(sheet, 1, i, errorlist,false,"A端光缆段名称");
            box.setAttrValue("A_WIRE_SEG", aWireSegLablecn);
            
            String aWireNo = getCellValue(sheet, 2, i, errorlist,false,"A端纤芯编号");
            box.setAttrValue("A_WIRE_NO", aWireNo);
            
            String districtLabelCn = getCellValue(sheet, 3, i, errorlist,false,"区域");
            box.setAttrValue(FiberJointBox.AttrName.relatedDistrictCuid, districtLabelCn);
            
            String boxlabelCn = getCellValue(sheet, 4, i, errorlist,false,"接头盒名称");
            box.setAttrValue(FiberJointBox.AttrName.labelCn, boxlabelCn);
            
            String zWireSystemLablecn = getCellValue(sheet, 5, i, errorlist,false,"Z端光缆系统名称");
            box.setAttrValue("Z_WIRE_SYSTEM", zWireSystemLablecn);
            
            String zWireSegLablecn = getCellValue(sheet, 6, i, errorlist,false,"Z端光缆段名称");
            box.setAttrValue("Z_WIRE_SEG", zWireSegLablecn);
            
            String zWireNo = getCellValue(sheet, 7, i, errorlist,false,"Z端纤芯编号");
            box.setAttrValue("Z_WIRE_NO", zWireNo);
            
            dbos.add(box);
        }
        }else{
        	errorlist.add("导入表格第一页，第一行和第二行不允许为空，不符合导入表格要求！");
        }
        if (errorlist.size() != 0) {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
        errorlist = getDataImportBO().importFBoxFiberConn(new BoActionContext(), dbos);
        if (errorlist.size() == 0) {
            errorlist.add("成功导入接头盒纤芯接续数据" + dbos.size() + "条。");
            return errorlist;
        } else {
            errorlist.add(ImpExpConsts.ERROR_INFO);
            return errorlist;
        }
    }

    /**
     * 根据行列取值
     * @Description: 
     * @param sheet
     * @param column
     * @param row
     * @param errs
     * @param isNullable 单元格值是否允许为空
     * @param columnName 
     * @return    
     * @author Gaoxf
     */
    public String getCellValue(Sheet sheet,int column,int row,List errs,boolean isNullable,String columnName){
        String value = sheet.getCell(column, row).getContents().trim();
        if(!isNullable){
        	if("".equals(value)){
        		errs.add("第"+row+"行'"+columnName+"'为空！");
        	}
        }
        return value;
    }
    /**
     * @param workbook
     * @author hongwei
     * @return
     * @throws Exception
     */
	public ArrayList importJumpFiberPosConn(Workbook workbook) throws Exception {
		DataObjectList ports = new DataObjectList();
		DataObjectList jumpfibers = new DataObjectList();
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		HashMap<String, String> args = new HashMap<String, String>();
		Sheet sheet = workbook.getSheet(0);
		ArrayList errorlists = new ArrayList();
		int rowcount = sheet.getRows();
		DataObjectList dbos = new DataObjectList();
		ArrayList errorlist = new ArrayList();
		if (!ImpExpUtils.isEmptyRow(sheet, 0)
				&& !ImpExpUtils.isEmptyRow(sheet, 1)) {
			for (int i = 1; i < rowcount; i++) {
				if (ImpExpUtils.isEmptyRow(sheet, i)) { // 判断是否为空行
					errorlist.add("导入表格第一页，第一行和第二行不允许为空，不符合导入表格要求！");
					break;
				}
				GenericDO gdo = new GenericDO();

				String aDevicename = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端设备名称", i, errorlist);
				String aDeviceType = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端设备类型", i, errorlist);
				String aModel = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端Model/card", i, errorlist);
				args.put("aModel", aModel);
				String aPortName = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端端子/端口名", i, errorlist);
				String aPortRow = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端端子/端口行", i, errorlist);
				args.put("aPortRow", aPortRow);
				String aPortCol = ImpExpUtils.getSheetValueByLabel(sheet,
						"A端端子/端口列", i, errorlist);
				args.put("aPortCol", aPortCol);
				String bDevicename = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端设备名称", i, errorlist);
				String bDeviceType = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端设备类型", i, errorlist);
				String bModel = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端Model/card", i, errorlist);
				args.put("bModel", bModel);
				String bPortName = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端端子/端口名", i, errorlist);
				String bPortRow = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端端子/端口行", i, errorlist);
				args.put("bPortRow", bPortRow);
				String bPortCol = ImpExpUtils.getSheetValueByLabel(sheet,
						"B端端子/端口列", i, errorlist);
				args.put("bPortCol", bPortCol);
				String connType = ImpExpUtils.getSheetValueByLabel(sheet,
						"连接方式", i, errorlist);

				if (errorlist.size() > 0) {
					errorlists.addAll(errorlist);
					break;
				}
				int rowno = i + 1;
				if (aDevicename == null
						|| (aDevicename != null && aDevicename.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno + " 行,A端设备名称不能为空,此条不予导入!");
				} else {
					args.put("aDevicename", aDevicename);
				}
				if (aDeviceType == null
						|| (aDeviceType != null && aDeviceType.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno + " 行,A端设备类型不能为空,此条不予导入!");
				} else {
					args.put("aDeviceType", aDeviceType);
				}
				if (aPortName == null
						|| (aPortName != null && aPortName.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno
							+ " 行,A端端子/端口名不能为空,此条不予导入!");
				} else {
					args.put("aPortName", aPortName);
				}
				
				if (bDevicename == null
						|| (bDevicename != null && bDevicename.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno + " 行,B端设备名称不能为空,此条不予导入!");
				} else {
					args.put("bDevicename", bDevicename);
				}
				if (bDeviceType == null|| (bDeviceType != null && bDeviceType.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno + " 行,B端设备类型不能为空,此条不予导入!");
				} else {
					args.put("bDeviceType", bDeviceType);
				}
				if (bPortName == null
						|| (bPortName != null && bPortName.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno
							+ " 行,B端端子/端口名不能为空,此条不予导入!");
				} else {
					args.put("bPortName", bPortName);
				}
				
				if (connType == null
						|| (connType != null && connType.trim().length() == 0)) {
					errorlist.add("非法数据: 第 " + rowno + " 行,连接方式不能为空,此条不予导入!");
				} else {
					args.put("connType", connType);
				}
                check(errorlist, ports, jumpfibers, args,map);
			}
		}
		if (errorlist.size() != 0) {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
		try {
			getJumpFiberBO().connectPortsWithJumpFiber(new BoActionContext(),ports,null, jumpfibers);
		} catch (Exception e) {
			e.printStackTrace();
			errorlist.add(e);
		}
		if (errorlist.size() == 0) {
			errorlist.add("成功导入光交接箱、分纤箱跳纤关联数据" + dbos.size() + "条。");
			return errorlist;
		} else {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
	}
    
    private void check(ArrayList errorlist,DataObjectList ports,DataObjectList jumpfibers,Map<String,String> args,Map<String,List<String>> map) throws Exception{
    	/**
    	 * flag 1 代表起点 2代表终点
    	 */
    	JumpFiber jumpfiber = new JumpFiber();
		if (args.get("aDeviceType").equals("光分纤箱")) {
			checkfiberdp(args, ports, errorlist, jumpfiber, map,1);
    	}else if(args.get("aDeviceType").equals("光交接箱")){
    		checkfiebrcab(args, ports, errorlist, jumpfiber, map, 1);
    	}else if(args.get("aDeviceType").equals("POS")){
    		checkpos(args, ports, errorlist, jumpfiber, map, 1);
    	}
		
		if (args.get("bDeviceType").equals("光分纤箱")) {
			checkfiberdp(args, ports, errorlist, jumpfiber, map,2);
    	}else if(args.get("bDeviceType").equals("光交接箱")){
    		checkfiebrcab(args, ports, errorlist, jumpfiber, map, 2);
    	}else if(args.get("bDeviceType").equals("POS")){
    		checkpos(args, ports, errorlist, jumpfiber, map, 2);
    	}
		
		if(args.get("connType").equals("固定连接")){
			jumpfiber.setAttrValue(JumpFiber.AttrName.isFixed, true);
		}else{
			jumpfiber.setAttrValue(JumpFiber.AttrName.isFixed, false);
		}
		
		jumpfiber.setAttrValue(JumpFiber.AttrName.labelCn, args.get("aPortName") + "=>" + args.get("bPortName"));
		jumpfibers.add(jumpfiber);
    	
    }
    
    private void checkfiberdp(Map<String,String> args,DataObjectList ports,ArrayList errorlist,JumpFiber jumpfiber,Map<String,List<String>> map,int flag) throws Exception{
    	FiberDp dp = new FiberDp();
    	if(flag == 1)
		     dp.setAttrValue(FiberDp.AttrName.labelCn, args.get("aDevicename"));
    	if(flag == 2)
    		 dp.setAttrValue(FiberDp.AttrName.labelCn, args.get("bDevicename"));
		String sql = "select CUID from FIBER_DP where LABEL_CN = '" +  dp.getLabelCn() +"'";
		Class[] cla = new Class[]{String.class};
		DataObjectList fiberdpcuid = getDuctManagerBO().getDatasBySql(sql, cla);
		if (fiberdpcuid != null && fiberdpcuid.size()>0) {
			String cuid = fiberdpcuid.get(0).getAttrString("1");
			FiberDpPort port = new FiberDpPort();
			if(flag == 1){
				port.setAttrValue(FiberCab.AttrName.labelCn,
						args.get("aPortName"));
			}
			if(flag == 2){
				port.setAttrValue(FiberCab.AttrName.labelCn,
						args.get("bPortName"));
			}
			port.setAttrValue(FiberDpPort.AttrName.relatedDeviceCuid,
					cuid);
			sql = FiberDpPort.AttrName.labelCn + "= '" + port.getLabelCn() + "'" + " and " + FiberDpPort.AttrName.relatedDeviceCuid + "= '" + cuid +"'";
			IFiberDpPortBO iFiberDpPortBO = BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
			DataObjectList portsbysql = iFiberDpPortBO.getFiberDpPortBySql(ActionContextUtil.getActionContext(), sql);
			if (portsbysql != null && portsbysql.size() > 0) {
				port = (FiberDpPort) portsbysql.get(0);
				String colNo = null;
				if(flag == 1)
				    colNo = args.get("aPortCol");
				else
					colNo = args.get("bPortCol");
				if (colNo != null && !"".equals(colNo)) {
					try {
						long numInCol = Long.parseLong(colNo);
						if (numInCol != port
								.getAttrLong(FiberDpPort.AttrName.numInMcol)) {
							errorlist.add("端子所在列号和端子不一致!");
						}
					} catch (Exception e) {
						LogHome.getLog().error(e);
						errorlist.add("端子所在列号要为整数!");
					}
				} else {
					errorlist.add("端子所在列号不能为空!");
				}
				String rowNo = null;
				if(flag == 1)
				    rowNo =  args.get("aPortRow");
				else
					rowNo =  args.get("bPortRow");
				if (rowNo != null && !"".equals(rowNo)) {
					try {
						long numInrow = Long.parseLong(rowNo);
						if (numInrow != port
								.getAttrLong(FiberDpPort.AttrName.numInMrow)) {
							errorlist.add("端子所在列号和端子不一致!");
						}
					} catch (Exception e) {
						LogHome.getLog().error(e);
						errorlist.add("端子所在行号要为整数!");
					}
				} else {
					errorlist.add("端子所在行号不能为空!");
				}
				if (!checkDuplicate(dp.getCuid(), port.getCuid(), map)) {
					errorlist.add(args.get("aPortName") +"or"+args.get("bPortName")+ "'重复导入");
				}

				if ((Boolean) (port
						.getAttrValue(Fcabport.AttrName.isConnected)) == true) {
					errorlist.add(args.get("aPortName") +"or"+args.get("bPortName")+ "'已连接跳纤");
				} else {
					port.setAttrValue(Fcabport.AttrName.isConnected, true);
					ports.add(port);
					if(flag == 1){
						jumpfiber.setAttrValue(
								JumpFiber.AttrName.origPointCuid,
								port.getCuid());
						jumpfiber
						.setAttrValue(
								JumpFiber.AttrName.origEqpCuid,
								port.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
					}
					if(flag == 2){
						jumpfiber.setAttrValue(
								JumpFiber.AttrName.destPointCuid,
								port.getCuid());
						jumpfiber
						.setAttrValue(
								JumpFiber.AttrName.destEqpCuid,
								port.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
					}
				}
			} else {
				errorlist.add(args.get("aPortName") +"or"+args.get("bPortName") + "'不存在!");
			}
		}else{
			errorlist.add(args.get("aDevicename")+"or"+args.get("bDevicename") + "'不存在!");
		}
    }
    
    private void checkfiebrcab(Map<String,String> args,DataObjectList ports,ArrayList errorlist,JumpFiber jumpfiber,Map<String,List<String>> map,int flag) throws Exception{
    	String labelcn = null;
    	String model = null;
    	String port = null;
    	String portcol = null;
    	String portrow = null;
    	if(flag == 1){
    		labelcn = args.get("aDevicename");
    		port = args.get("aPortName");
    		model = args.get("aModel");
    		portcol = args.get("aPortCol");
    		portrow = args.get("aPortRow");
    	}
    	if(flag == 2){
    		labelcn = args.get("aDevicename");
    		port = args.get("bPortName");
    		model = args.get("bModel");
    		portcol = args.get("bPortCol");
    		portrow = args.get("bPortRow");
    	}
    	
    	String sql = "select CUID from FIBER_CAB where LABEL_CN = '" +  labelcn +"'";
		Class[] cla = new Class[]{String.class};
		DataObjectList fibercabcuid = getDuctManagerBO().getDatasBySql(sql, cla);
    	if(fibercabcuid != null && fibercabcuid.size()>0){
    		String cuid = fibercabcuid.get(0).getAttrString("1");
    		Fibercabmodule fibercabmodule = new Fibercabmodule();
    		if(model != null && !"".equals(model)){
    			IFibercabmoduleBO iFibercabmoduleBO = BoHomeFactory.getInstance().getBO(IFibercabmoduleBO.class);
    			sql = Fibercabmodule.AttrName.labelCn + " = '" + model + "' and " + Fibercabmodule.AttrName.relatedDeviceCuid + " = '" + cuid + "'";
    			DataObjectList models = iFibercabmoduleBO.getFibercabmoduleBySql(ActionContextUtil.getActionContext(), sql);
    			if(models != null && models.size() > 0){
    				fibercabmodule = (Fibercabmodule) models.get(0);
    				Fcabport fcabport = new Fcabport();
    				fcabport.setAttrValue(Fcabport.AttrName.labelCn, port);
    				fcabport.setAttrValue(Fcabport.AttrName.relatedModuleCuid, fibercabmodule.getCuid());
    				IFcabportBO iFcabportBO = BoHomeFactory.getInstance().getBO(IFcabportBO.class);
    				sql = Fcabport.AttrName.labelCn + " = '" + port + "' and " + Fcabport.AttrName.relatedModuleCuid + " = '" + fibercabmodule.getCuid() +"'";
    				DataObjectList fcabportBySql = iFcabportBO.getFcabportBySql(ActionContextUtil.getActionContext(), sql);
    				if(fcabportBySql != null && fcabportBySql.size() > 0){
    					fcabport = (Fcabport) fcabportBySql.get(0);
    					try {
							Long rowNo = Long.parseLong(portrow);
							if(rowNo != fcabport.getAttrLong(Fcabport.AttrName.numInMrow)){
								errorlist.add("FIBERCAB:"+labelcn+"的model："+model+"的"+port+"行号与端子不一致");
							}
							Long colNo = Long.parseLong(portcol);
							if(colNo != fcabport.getAttrLong(Fcabport.AttrName.numInMcol)){
								errorlist.add("FIBERCAB:"+labelcn+"的model："+model+"的"+port+"列号与端子不一致");
							}
							if(!checkDuplicate(fibercabmodule.getCuid(), fcabport.getCuid(), map)){
								errorlist.add("FIBERCAB:"+labelcn+"的model："+model+"的"+port+"重复导入");
							}
							if((Boolean)(fcabport.getAttrValue(Fcabport.AttrName.isConnected)) == true){
								errorlist.add("FIBERCAB:"+labelcn+"的model："+model+"的"+port+ "'已连接跳纤");
							}else{
								fcabport.setAttrValue(Fcabport.AttrName.isConnected,true);
								ports.add(fcabport);
								if(flag == 1){
									jumpfiber.setAttrValue(JumpFiber.AttrName.origPointCuid, fcabport.getCuid());
									jumpfiber.setAttrValue(JumpFiber.AttrName.origEqpCuid, fcabport.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
								}
								if(flag == 2){
									jumpfiber.setAttrValue(JumpFiber.AttrName.destPointCuid, fcabport.getCuid());
									jumpfiber.setAttrValue(JumpFiber.AttrName.destEqpCuid, fcabport.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
								}
							}
						} catch (Exception e) {
							errorlist.add("端子行列号必须为整数");
						}
    				}else{
    					errorlist.add("FIBERCAB:"+labelcn+"的"+port+"不存在");
    				}
    			}else{
    				errorlist.add("FIBERCAB:"+labelcn+"model不存在");
    			}
    		}else{
    			errorlist.add("FIBERCAB:"+labelcn+"model不能为空");
    		}
    	}else{
    		errorlist.add(labelcn+"不存在");
    	}
    }
    
    private void checkpos(Map<String,String> args,DataObjectList ports,ArrayList errorlist,JumpFiber jumpfiber,Map<String,List<String>> map,int flag) throws Exception{
    
    	String labelcn = null;
    	String model = null;
    	String port = null;
    	String portcol = null;
    	String portrow = null;
    	AnPos anpos = new AnPos();
    	if(flag == 1){
    		labelcn = args.get("aDevicename");
    		port = args.get("aPortName");
    		model = args.get("aModel");
    		portcol = args.get("aPortCol");
    		portrow = args.get("aPortRow");
    	}
    	if(flag == 2){
    		labelcn = args.get("aDevicename");
    		port = args.get("bPortName");
    		model = args.get("bModel");
    		portcol = args.get("bPortCol");
    		portrow = args.get("bPortRow");
    	}
    	
    	anpos.setAttrValue(AnPos.AttrName.labelCn, labelcn);
    	String sql = "select cuid from an_pos where " + AnPos.AttrName.labelCn + "  =  '" + labelcn +"'";
    	DataObjectList datasBySql = getDuctManagerBO().getDatasBySql(sql, new Class[]{String.class});
    	if(datasBySql != null && datasBySql.size() > 0){
    		String cuid = datasBySql.get(0).getAttrString("1");
    		Card card = new Card();
    		card.setAttrValue(Card.AttrName.labelCn, model);
    		card.setAttrValue(Card.AttrName.relatedDeviceCuid, anpos.getCuid());
    		sql = "select cuid from card where " + Card.AttrName.relatedDeviceCuid + " = '" +cuid+"' and "+Card.AttrName.labelCn+" = '"+ model+"'";
    		DataObjectList modelcuid = getDuctManagerBO().getDatasBySql(sql, new Class[]{String.class});
    		if(modelcuid != null && modelcuid.size() > 0){
    			cuid = modelcuid.get(0).getAttrString("1");
    			Ptp ptp = new Ptp();
    			IPTPBO iptpbo = BoHomeFactory.getInstance().getBO(IPTPBO.class);
    			sql = "select * from ptp where "+ Ptp.AttrName.relatedCardCuid+" = '" + cuid + "' and "+ Ptp.AttrName.labelCn +" = '" + port+"'";
    			DataObjectList ptpcuids = getDuctManagerBO().getDatasBySql(sql, new Class[]{String.class});
    			if(ptpcuids != null && ptpcuids.size() > 0){
    				cuid = ptpcuids.get(0).getAttrString("1");
    				ptp = iptpbo.getPTPByCuid(ActionContextUtil.getActionContext(), cuid);
    				if((Boolean)(ptp.getAttrValue(Ptp.AttrName.isConnState)) == true){
    					errorlist.add("POS::"+labelcn+"的card："+model+"的端口："+port+"已经跳纤");
    				}
    				if(!checkDuplicate(card.getCuid(), ptp.getCuid(), map)){
    					errorlist.add("POS::"+labelcn+"的card："+model+"的端口："+port+"重复导入");
    				}
    				ptp.setAttrValue(Ptp.AttrName.isConnState, true);
    				ports.add(ptp);
    				if(flag == 1){
						jumpfiber.setAttrValue(JumpFiber.AttrName.origPointCuid, ptp.getCuid());
						jumpfiber.setAttrValue(JumpFiber.AttrName.origEqpCuid, anpos.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
					}
					if(flag == 2){
						jumpfiber.setAttrValue(JumpFiber.AttrName.destPointCuid, ptp.getCuid());
						jumpfiber.setAttrValue(JumpFiber.AttrName.destEqpCuid, anpos.getAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid));
					}
    			}else{
    				errorlist.add("POS::"+labelcn+"的card："+model+"的端口："+port+"不存在");
    			}
    		}else{
    			errorlist.add("POS::"+labelcn+"的card："+model+"不存在");
    		}
    	}else{
    		errorlist.add("POS::"+labelcn+"不存在");
    	}
    }
    
    private boolean checkDuplicate(String parentCuid, String childCuid,Map<String,List<String>> fibersConnectedMap) {
		if (fibersConnectedMap.containsKey(parentCuid)) {
			List<String> fibercuidLs = fibersConnectedMap.get(parentCuid);
			if (fibercuidLs.contains(childCuid)) {
				return false;
			} else {
				fibercuidLs.add(childCuid);
				return true;
			}
		} else {
			List<String> fibercuidLs = new ArrayList<String>();
			fibercuidLs.add(childCuid);
			fibersConnectedMap.put(parentCuid, fibercuidLs);
			return true;
		}
	}

	/**
	 * 跳纤关联
	 * @param workbook
	 * @return
	 * @throws Exception
	 */
	public ArrayList importJumpFiberConn(Workbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet(0);
		int rowcount = sheet.getRows();
		DataObjectList dbos = new DataObjectList();
		ArrayList errorlist = new ArrayList();
		ArrayList err = new ArrayList();
		if (!ImpExpUtils.isEmptyRow(sheet, 0) && !ImpExpUtils.isEmptyRow(sheet, 1)) {
			for (int i = 2; i < rowcount; i++) {
				if (ImpExpUtils.isEmptyRow(sheet, i)) { // 判断是否为空行
					break;
				}
				GenericDO gdo = new GenericDO();

				String pointType = getCellValue(sheet, 0, i, errorlist,false,"类型");
				gdo.setAttrValue("DEVICE_TYPE", pointType);

				String labelCn = getCellValue(sheet, 1, i, errorlist,false,"对象名称");
				gdo.setAttrValue(FiberJointBox.AttrName.labelCn, labelCn);

				String a_moduleLabelcn = getCellValue(sheet, 2, i, errorlist,false,"模块名称");
				gdo.setAttrValue("A_MODULE_LABEL_CN", a_moduleLabelcn);

				String a_rowNo = getCellValue(sheet, 3, i, errorlist,false,"端子所在行号");
				gdo.setAttrValue("A_ROW_NO", a_rowNo);

				String a_colNo = getCellValue(sheet, 4, i, errorlist,false,"端子所在列号");
				gdo.setAttrValue("A_COL_NO", a_colNo);

				String a_portLabelcn = getCellValue(sheet, 5, i, errorlist,false,"端子名称");
				gdo.setAttrValue("A_PORT_LABEL_CN", a_portLabelcn);
				
				String connectType = getCellValue(sheet, 6, i, errorlist,false,"连接方式");
				gdo.setAttrValue("CONNECT_TYPE", connectType);
				
				String z_moduleLabelcn = getCellValue(sheet, 7, i, errorlist,false,"模块名称");
				gdo.setAttrValue("Z_MODULE_LABEL_CN", z_moduleLabelcn);

				String z_rowNo = getCellValue(sheet, 8, i, errorlist,false,"端子所在行号");
				gdo.setAttrValue("Z_ROW_NO", z_rowNo);

				String z_colNo = getCellValue(sheet, 9, i, errorlist,false,"端子所在列号");
				gdo.setAttrValue("Z_COL_NO", z_colNo);

				String z_portLabelcn = getCellValue(sheet, 10, i, errorlist,false,"端子名称");
				gdo.setAttrValue("Z_PORT_LABEL_CN", z_portLabelcn);
	            
				// 验证有无缺失的属性列。
				if (err.size() != 0) {
					err.add(ImpExpConsts.ERROR_INFO);
					return err;
				}
				dbos.add(gdo);
			}
		} else {
			errorlist.add("导入表格第一页，第一行和第二行不允许为空，不符合导入表格要求！");
		}
		if (errorlist.size() != 0) {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
		errorlist = getDataImportBO().importJumpFiberConn(new BoActionContext(), dbos);
		if (errorlist.size() == 0) {
			errorlist.add("成功导入光交接箱、分纤箱跳纤关联数据" + dbos.size()+ "条。");
			return errorlist;
		} else {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
	}
	
	
	public ArrayList importRoom(Workbook workbook) throws Exception {
        return importRoom(workbook, "");
    }

    /**
     * importFiberJointBox
     * 光接头盒导入:从Excel表格中读出数据，将数据发送到服务器端进行导入。
     * @param workbook Workbook
     * @return ArrayList
     */
    public ArrayList importRoom(Workbook workbook, String curMapCuid) throws Exception {
    	long startTime = System.currentTimeMillis();
        Sheet sheet = workbook.getSheet(0);
        if(workbook.getSheet("机房")!=null){
        	sheet = workbook.getSheet("机房");
        }
        int errCount = 0;
        int rowcount = sheet.getRows();
        DataObjectList dbos = new DataObjectList();
        ArrayList errorlist = new ArrayList();
        ArrayList err = new ArrayList();
        Map fiberRoomNames = new HashMap();
        List<String> cuidList = new ArrayList<String>();
        DMDataPlantBO dmDataPlantBO = (DMDataPlantBO)  SpringContextUtil.getBean("DMDataPlantBO");
        IRoomBO bo = (IRoomBO) BoHomeFactory.getInstance().getBO(BoName.RoomBO);
        for (int i = 0; i < rowcount; i++) {
            if (ImpExpUtils.isEmptyRow(sheet, i)) { //判断是否为空行
                if (i == 0) errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
                errCount = rowcount;
                break;
            }
            if(i==0){//第一行是列头信息，跳过
            	continue;
            }
            String errorInfo = ImportChecker.checktRoom(sheet, i, err);
            if(errorInfo!=null
            		&&!"".equals(errorInfo)){
            	err.add(errorInfo);
            	errCount++;
            	continue;
            }
            Room dbo = new Room();
            String labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "机房名称", i, err); 
            dbo.setAttrValue(Room.AttrName.labelCn, labelCn);
            labelCn = ImpExpUtils.getSheetValueByLabel(sheet, "修改后机房名称", i, err); 
            dbo.setAttrValue(Room.AttrName.labelCn, labelCn);
            String abbreviation = ImpExpUtils.getSheetValueByLabel(sheet, "缩写", i, err); 
            dbo.setAttrValue(Room.AttrName.abbreviation, abbreviation);
            String ownership = ImpExpUtils.getSheetValueByLabel(sheet, "产权", i, err); 
         
            try{
                Object ownershipInt = new RoomEnum.Ownership().getValue(ownership);
                dbo.setAttrValue(Room.AttrName.ownership, ownershipInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String serviceLevel = ImpExpUtils.getSheetValueByLabel(sheet, "业务级别", i, err); //CONNECT_TYPE
            try{
                Object serviceLevelInt = new RoomEnum.SeviceLevel().getValue(serviceLevel);
                dbo.setAttrValue(Room.AttrName.serviceLevel, serviceLevelInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String length = ImpExpUtils.getSheetValueByLabel(sheet, "长", i, err); //CONNECT_TYPE
            dbo.setAttrValue(Room.AttrName.length, length);
            String width = ImpExpUtils.getSheetValueByLabel(sheet, "宽", i, err); //CONNECT_TYPE
            dbo.setAttrValue(Room.AttrName.width, width);
            String height = ImpExpUtils.getSheetValueByLabel(sheet, "高", i, err); //CONNECT_TYPE
            dbo.setAttrValue(Room.AttrName.height, height);
            String contactor = ImpExpUtils.getSheetValueByLabel(sheet, "联系人", i, err); //CONNECT_TYPE
            dbo.setAttrValue(Room.AttrName.contactor, contactor);
            String telephone = ImpExpUtils.getSheetValueByLabel(sheet, "联系电话", i, err); //SETUP_TIME
            dbo.setAttrValue(Room.AttrName.telephone, telephone);
            String contactAddress = ImpExpUtils.getSheetValueByLabel(sheet, "联系地址", i, err); //CREATOR
            dbo.setAttrValue(Room.AttrName.contactAddress, contactAddress);
            String type = ImpExpUtils.getSheetValueByLabel(sheet, "机房类型", i, err); 
            try{
                Object typeInt = new RoomEnum.RoomType().getValue(type);
                dbo.setAttrValue(Room.AttrName.roomType, typeInt.toString() );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String siteLabelCn = ImpExpUtils.getSheetValueByLabel(sheet, "所属站点", i, err); 
            Map map = dmDataPlantBO.getSiteInfo(siteLabelCn);
            dbo.setAttrValue(Room.AttrName.relatedSiteCuid, map.get("SITE_CUID"));
            dbo.setAttrValue(Room.AttrName.relatedSpaceCuid, map.get("SPACE_CUID"));
            String relatedFloorCuid = ImpExpUtils.getSheetValueByLabel(sheet, "所属楼层", i, err); 
            dbo.setAttrValue(Room.AttrName.relatedFloorCuid, relatedFloorCuid);
            String keyType = ImpExpUtils.getSheetValueByLabel(sheet, "钥匙类型", i, err); 

            try{
                Object keyTypeInt =  new RoomEnum.keyType().getValue(keyType);
                dbo.setAttrValue(Room.AttrName.keyType, keyTypeInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String maintMode = ImpExpUtils.getSheetValueByLabel(sheet, "维护方式", i, err);
            try{
                Object maintModeInt =  new RoomEnum.MaintMode().getValue(maintMode);
                dbo.setAttrValue(Room.AttrName.maintMode, maintModeInt );
            }catch(Exception e){
            	e.printStackTrace();
            }

            String status = ImpExpUtils.getSheetValueByLabel(sheet, "设备状态", i, err);
            try{
                Object statusInt =   new RoomEnum.State().getValue(status);
                dbo.setAttrValue(Room.AttrName.status, statusInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String maintaindepartment = ImpExpUtils.getSheetValueByLabel(sheet, "维护单位", i, err);
            dbo.setAttrValue(Room.AttrName.maintaindepartment, maintaindepartment); //显示纬度

            String equipmentcode = ImpExpUtils.getSheetValueByLabel(sheet, "固定资产编号", i, err); //DESIGN_CAPACITY
            dbo.setAttrValue(Room.AttrName.equipmentcode, equipmentcode);
            String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err); //USED_CAPACITY
            dbo.setAttrValue(Room.AttrName.remark, remark);
            String rowCount = ImpExpUtils.getSheetValueByLabel(sheet, "行号", i, err); //INSTALL_CAPACITY
            dbo.setAttrValue(Room.AttrName.rowCount, rowCount);
            String colCount = ImpExpUtils.getSheetValueByLabel(sheet, "列号", i, err); //FREE_CAPACITY
            dbo.setAttrValue(Room.AttrName.colCount, colCount);
            String baseStaDeviceType = ImpExpUtils.getSheetValueByLabel(sheet, "基站设备类型", i, err); //LABEL_DEV
            dbo.setAttrValue(Room.AttrName.baseStaDeviceType, baseStaDeviceType);
            String mainDeviceType = ImpExpUtils.getSheetValueByLabel(sheet, "传输主设备类型", i, err); //SEQNO
            dbo.setAttrValue(Room.AttrName.mainDeviceType, mainDeviceType);
            String mainDeviceModel = ImpExpUtils.getSheetValueByLabel(sheet, "传输主设备型号", i, err);
            try{
                Object mainDeviceModelInt =   new RoomEnum.MainDeviceType().getValue(mainDeviceModel);
                dbo.setAttrValue(Room.AttrName.mainDeviceModel, mainDeviceModelInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String roomType = ImpExpUtils.getSheetValueByLabel(sheet, "接入数据机房类型", i, err); //SPECIAL_LABEL
            try{
                Object roomTypeInt = new RoomEnum.ReceiveRoomType().getValue(roomType);
                dbo.setAttrValue(Room.AttrName.receiveRoomType, roomTypeInt.toString() );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String specialLineLevel = ImpExpUtils.getSheetValueByLabel(sheet, "专线等级", i, err); //USERNAME
            try{
                Object specialLineLevelInt =  new RoomEnum.SpecialLineLevel().getValue(specialLineLevel);
                dbo.setAttrValue(Room.AttrName.specialLineLevel, specialLineLevelInt );
            }catch(Exception e){
            	e.printStackTrace();
            }
            String deviceRelatedProject = ImpExpUtils.getSheetValueByLabel(sheet, "新增设备所属工程", i, err); //MAINT_DEP
            dbo.setAttrValue(Room.AttrName.deviceRelatedProject, deviceRelatedProject);
            String cuid = ImpExpUtils.getSheetValueByLabel(sheet, "数据库主键", i, err);
            cuidList.add(cuid);
            //验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (fiberRoomNames.get(labelCn) != null) {
                    err.add("非法数据：在excel表格中，第" + i + "行与第" +
                    		fiberRoomNames.get(labelCn) + "机房名称重复！");
                    errCount++;
                    continue;
                } else {
                	fiberRoomNames.put(labelCn, Integer.valueOf(i));
                }
            }
            dbos.add(dbo);
        }

    	long analyzeEndTime = System.currentTimeMillis();
      //验证用户所在的区域经纬度是否正确，为点资源经纬度的校验做准备
        HashSet<District> districts=checkLonAndLatByUserDistricts(errorlist);
        
        //调用服务器端接口
        BoActionContext actionContext = new BoActionContext();
        for(int i=0;i<dbos.size();i++){
        	Room room = (Room)dbos.get(i);
        	try{
            	bo.addRoom(actionContext, room);
        	}catch(UserException e){
        		e.printStackTrace();
        	}
        }

    	long storeEndTime = System.currentTimeMillis();
        dmDataPlantBO.dealHistoryOverLayResource(cuidList);

    	long markEndTime = System.currentTimeMillis();

//        errorlist.add("总共识别数据" + (rowcount-1) + "行 "); 
//        errorlist.add("有效数据"+(dbos.size())+"行  ");
//        errorlist.add("错误数据 "+errCount+"行");
//        errorlist.add("\n");
        errorlist.add("总共耗时("+(markEndTime-startTime)/1000+"秒)");
        errorlist.add("数据抽取耗时("+(analyzeEndTime-startTime)/1000+"秒)");
        errorlist.add("数据翻译耗时("+(storeEndTime-analyzeEndTime)/1000+"秒)");
        errorlist.add("结果标记耗时("+(markEndTime-storeEndTime)/1000+"秒)");
        if (err.size() != 0) {
            errorlist.addAll(err);
        }
        
        return errorlist;
    
    }



	public ArrayList importOverlayResource(Workbook workbook)throws Exception{
			Sheet sheet = workbook.getSheet(0);
	        int rowcount = sheet.getRows();
	        DataObjectList dbos = new DataObjectList();
	        ArrayList errorlist = new ArrayList();
	        int correctRow=0;
	        int errorRow=0;
	        Map<String,Integer> duplicate = new HashMap<String,Integer>();
	        if (!ImpExpUtils.isEmptyRow(sheet, 0) && !ImpExpUtils.isEmptyRow(sheet, 1)) {	        	
	        	for (int i = 0; i < rowcount; i++) {
	        		ArrayList<String> rowError=new ArrayList<String>();
	        		if (ImpExpUtils.isEmptyRow(sheet, i)) { // 判断是否为空行
	        			String desc="行"+i+"为空行";
	        			rowError.add(desc);
	        			errorlist.add(desc);
	        			continue;
					}	        		
	        		GenericDO gdo = new GenericDO();

	        		if(i==0){
	                    String labelCn  = ImpExpUtils.getSheetValueByLabel(sheet, "名称", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.labelCn,labelCn );
	                    String district  = ImpExpUtils.getSheetValueByLabel(sheet, "所属区域", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.relatedDistrictCuid,district );
	                    String projectPlanPeriod  = ImpExpUtils.getSheetValueByLabel(sheet, "规划期数", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.projectPlanPeriod, projectPlanPeriod);
	                    String longitude  = ImpExpUtils.getSheetValueByLabel(sheet, "经度", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.longitude, longitude);
	                    String  latitude = ImpExpUtils.getSheetValueByLabel(sheet, "纬度", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.latitude,latitude );
	                    String resType  = ImpExpUtils.getSheetValueByLabel(sheet, "资源类型", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.resType,resType );
	                    String fiberCount  = ImpExpUtils.getSheetValueByLabel(sheet, "已规划纤芯数", i, errorlist);
	                    gdo.setAttrValue(OverlayResource.AttrName.fiberCount, fiberCount);
	        			dbos.add(gdo);
	                    continue;
	        		}
	        		String labelCn = getCellValue(sheet, 0, i, errorlist,false,"名称");
	        		if(StringUtils.isEmpty(labelCn)){
	        			rowError.add("名称列要求必填");
	        			continue;
	        		}else{
	        			gdo.setAttrValue(OverlayResource.AttrName.labelCn, labelCn);	
	        		}
						        						
					String districtName = getCellValue(sheet, 1, i, errorlist,false,"所属区域");
					if(StringUtils.isEmpty(districtName)){
						String desc="区域列要求必填";
						rowError.add(desc);
						errorlist.add(desc);	
	        			continue;					
					}else{
						String disCuid = getDistrictBO().getCuidByLabelCn(new BoActionContext(), districtName);
						if(StringUtils.isEmpty(disCuid)){
						   String desc=districtName+"系统中不存在";
						   rowError.add(desc);	
						   errorlist.add(desc);
		        			continue;						   
						}else{
						   gdo.setAttrValue(OverlayResource.AttrName.relatedDistrictCuid, districtName);
						}
					}
					
					String projectPlanPeriod=getCellValue(sheet, 2, i, errorlist,false,"规划期数");
	        		if(StringUtils.isEmpty(projectPlanPeriod)){
	        			String desc="规划期数要求必填";
	        			rowError.add(desc);	
						errorlist.add(desc);	
	        			continue;
	        		}else{
	        			gdo.setAttrValue(OverlayResource.AttrName.projectPlanPeriod, projectPlanPeriod);
	        		}
	        		
	        		String longitude=getCellValue(sheet, 3, i, errorlist,false,"经度").trim();
	        		String latitude=getCellValue(sheet, 4, i, errorlist,false,"纬度").trim();
	        		if(StringUtils.isEmpty(longitude)||StringUtils.isEmpty(latitude)){
	        			String desc="经维度要求都必填";
	        			rowError.add(desc);	
						errorlist.add(desc);	
	        			continue;
	        		}else{
	        			if(isNum(longitude)&&isNum(latitude)){
	        				if(bitNum(longitude,3)&&bitNum(latitude,3)){
	        					gdo.setAttrValue(OverlayResource.AttrName.longitude, longitude);
	        					gdo.setAttrValue(OverlayResource.AttrName.latitude, latitude);
	        				}else{
	        					String desc="经维度要求小数点后为6位";
			        			rowError.add(desc);	
								errorlist.add(desc);
			        			continue;
	        				}
	        				
	        			}else{
	        				String desc="经维度要求都必为数字";
		        			rowError.add(desc);	
							errorlist.add(desc);
		        			continue;
	        			}
	        			
	        		}
	        		
	        		
	        		String resType=getCellValue(sheet, 5, i, errorlist,false,"资源类型");
	        		if(StringUtils.isEmpty(resType)){
	        			String desc="资源类型要求必填";
	        			rowError.add(desc);	
						errorlist.add(desc);	
	        			continue;
	        		}else{
	        			String type="";
	        			if(resType.equals("机房")) {type="1";}
	        			else if(resType.equals("光交箱"))   { type="4";}
	        			else if(resType.equals("交接箱"))   { type="3";}
	        			else if (resType.equals("接头盒"))  {  type="2";}
	        			
	        			if(StringUtils.isEmpty(type)){
	        				String desc="资源类型必须是机房，接头盒，交接箱，光分纤箱枚举之一";
		        			rowError.add(desc);	
							errorlist.add(desc);	
		        			continue;
	        			}else{
	        				gdo.setAttrValue(OverlayResource.AttrName.resType, resType);
	        			}	        				        			
	        		}
	        		
	        		String fiberCount=getCellValue(sheet, 6, i, errorlist,false,"已规划纤芯数");
	        		if(fiberCount.trim().length()>0){
			        		if(isNum(fiberCount)&&bitNum(fiberCount,0)){
			        			gdo.setAttrValue(OverlayResource.AttrName.fiberCount, fiberCount);
			        		}else{
			        			String desc="已规划纤芯数必须为整型";
			        			rowError.add(desc);	
								errorlist.add(desc);
			        			continue;
			        		}
	        		}
	        		
	                //验证在Excel表中是否重名的对象。
	                if (!"".equals(labelCn)&&!"".equals(resType)) {
	                	
	                    if (duplicate.get(labelCn+resType) != null) {
	                    	
	                        errorlist.add("非法数据：在excel表格中，第" + i + "行与第" +
	                        		duplicate.get(labelCn+resType) + "行人手井名称重复！");
	                    } else {
	                    	duplicate.put(labelCn, Integer.valueOf(i));
	                    }
	                }
	                
	                
	        		if(rowError.size()>0){
	        			errorRow++;	        			
	        			String errRowDesc="";
	        			for(String s:rowError){
	        				errRowDesc=errRowDesc+","+s;
	        			}
	        	        LogHome.getLog().info("第 "+i+"行有错误信息："+errRowDesc);
	        		}else{
	        			correctRow++;
	        			dbos.add(gdo);
	        		}
	        	}
	        	
	        }else{
	        	errorlist.add("导入表格第一页，第一行和第二行不允许为空，不符合导入表格要求！");
	        }
	        LogHome.getLog().info("调用导入预覆盖资源服务开始");
	        LogHome.getLog().info("合格数据："+(dbos.size()-1)+" 条");
	        ArrayList ls = (ArrayList)BoCmdFactory.getInstance().execBoCmd("IDataImportBO.importOverLayResource",new BoActionContext(), dbos);
	        errorlist.addAll(ls);//存入数据库
		    LogHome.getLog().info("调用导入预覆盖资源服务结束");
	        workbook.close();	          
	        ArrayList restDesc=new ArrayList();
	           String rest=MessageFormat.format("导入结果{0}", correctRow);
	           restDesc.add(MessageFormat.format("共识别数据({0})行", rowcount-1));//去掉列头信息
	           restDesc.add(MessageFormat.format("有效数据({0})行", correctRow));
	           restDesc.add(MessageFormat.format("错误数据({0})行", rowcount-1-correctRow));
	           restDesc.addAll(errorlist);
	           return restDesc;
	           			
	}

	/**
	 * 判断小数点后的位数
	 * @param value 数值
	 * @param num   位数
	 * @return
	 */
	private boolean bitNum(String value,int num){
		   if(num<0){
			   throw new RuntimeException("位数条件应大于等于0");
		   }		   
		   String locValue=value.trim();
		   String[] strs=locValue.split("\\.");		   
		   if(num==0){
			   if(strs.length!=1){
				   return false;
			   }else {
				   return true;
			   }
		   }		   
		   if(strs.length!=2){
			   return false;
		   }else{
			   String  part=strs[1];
			   if(part.length()==num){
				   return true;
			   }else {
				   return false;
			   }
		   }
	}
	 /**
	  * 判断判断是否为数字
	  * @param str
	  * @return
	  */
	 private boolean isNum(String str){
			return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
     }
	 
	 public static void main(String[] args){
		 ImportConverter obj = new ImportConverter(null);
		 try {
			obj.importOverlayResource(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 public HashMap<String, DataObjectList> queryFiber(DataObjectList segDbos) throws Exception{
		 HashMap<String, DataObjectList> segFibers = new HashMap<String, DataObjectList>();
			for(GenericDO dbo : segDbos){
	    		String cuid = dbo.getCuid();
	    		String labelCn = dbo.getAttrString(WireSeg.AttrName.labelCn);
	    		String sql = Fiber.AttrName.relatedSegCuid + "='" + cuid +"'";
	    		DataObjectList fibers = getDuctManagerBO().getObjectsBySql(sql, new Fiber());
	    		if(fibers!=null && fibers.size()>0){
	    			segFibers.put(cuid, fibers);
	    		}
	    	}
		return segFibers;
	 }
	 
	 public void createFiber(GenericDO dbo) throws Exception{
	   	 DataObjectList addFibers = new DataObjectList();
	   	 List cuidlist = new ArrayList();
	   	 
	   	 long fiberCount=0L;
	   	 
	   	 String cuid = dbo.getCuid();
	   	 fiberCount = dbo.getAttrLong(WireSeg.AttrName.fiberCount);
	   	 String systemCuid = dbo.getAttrString(WireSeg.AttrName.relatedSystemCuid);
	   	 String destPointCuid = dbo.getAttrString(WireSeg.AttrName.destPointCuid);
	   	 String origPointCuid = dbo.getAttrString(WireSeg.AttrName.origPointCuid);
	   	 double length = dbo.getAttrDouble(WireSeg.AttrName.length);
	   	 GenericDO system = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
	   	 long systemLevel = system.getAttrLong(WireSystem.AttrName.systemLevel);
	   	 long ownership = dbo.getAttrLong(WireSeg.AttrName.ownership);
	   	 for (long fiberNo = 0; fiberNo < fiberCount; fiberNo++) {
	   		 Fiber fiber = new Fiber();
	   	     Long fNo = new Long(fiberNo + 1);
	   	     fiber.setAttrValue(Fiber.AttrName.labelCn, fNo.toString());
	   	     fiber.setAttrValue(Fiber.AttrName.relatedSystemCuid, systemCuid);
	   	     fiber.setAttrValue(Fiber.AttrName.relatedSegCuid, cuid);
	   	     fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destPointCuid);
	   	     fiber.setAttrValue(Fiber.AttrName.origSiteCuid, origPointCuid);
	   	     fiber.setAttrValue(Fiber.AttrName.destEqpCuid, destPointCuid);
	   	     fiber.setAttrValue(Fiber.AttrName.origEqpCuid, origPointCuid);
	   	     fiber.setAttrValue(Fiber.AttrName.wireNo, fNo);
	   	     DecimalFormat df = new DecimalFormat("#.00");
	   	     fiber.setAttrValue(Fiber.AttrName.length, df.format(length));
	   	     fiber.setFiberLevel(systemLevel);
	   	     long color = (Color.WHITE).getRGB();
	   	     fiber.setAttrValue(Fiber.AttrName.fiberColor, color);
	   	     //加默认值
	   	     fiber.setAttrValue(Fiber.AttrName.signalDirection, new Long(1));
	   	     fiber.setAttrValue(Fiber.AttrName.ownership, ownership);
	   	     fiber.setAttrValue(Fiber.AttrName.maintMode, new Long(1));
	   	     fiber.setAttrValue(Fiber.AttrName.purpose, new Long(1));
	   	     fiber.setAttrValue(Fiber.AttrName.usageState, new Long(1));
	   	     fiber.setAttrValue(Fiber.AttrName.signalDirection, new Long(1));
	   	     fiber.setAttrValue(Fiber.AttrName.aveAttenu1310, new Double(0.0));
	   	     fiber.setAttrValue(Fiber.AttrName.sumAttenu1310, new Double(0.0));
	   	     fiber.setAttrValue(Fiber.AttrName.sumAttenu1550, new Double(0.0));
	   	     fiber.setAttrValue(Fiber.AttrName.aveAttenu1550, new Double(0.0));

	   	     addFibers.add(fiber);
	   	 }
	   	 if (addFibers.size() > 0) {
	   		 DataObjectList fibers = getDuctManagerBO().createDMDOs(new BoActionContext(), addFibers);
	       	 LogHome.getLog().info(fibers +"纤芯已经生成 ");
	   	 }
	 }
	 
	 public DataObjectList queryWireSegbyName(ArrayList namelist, String className) throws Exception{
		 DataObjectList dbos= new DataObjectList();
		 String sql = "";
		 for(int i=0;i<namelist.size();i++){
			 String name = (String) namelist.get(i);
			 sql = WireSeg.AttrName.labelCn + " = '"+ name +"' ";
			 DataObjectList segs = new DataObjectList();
			 if(className.equals(WireSeg.CLASS_NAME)){
				 segs = getDuctManagerBO().getObjectsBySql(sql, new WireSeg());
			 }else if(className.equals(InterWire.CLASS_NAME)){
				 segs = getDuctManagerBO().getObjectsBySql(sql, new InterWire());
			 }
			 if(segs != null && segs.size() > 0){
				 GenericDO wireSeg = segs.get(0);
				 dbos.add(wireSeg);
			 }
		 }
		 return dbos;
	 }

	 /**
	  * 楼内光缆导入
	  * @param workbook
	  * @return
	  * @throws Exception
	  */
	public ArrayList importIntertWire(Workbook workbook) throws Exception {
		LogHome.getLog().info("===进入层间光缆导入方法===");
		Sheet sheet = workbook.getSheet(0);
		int rowcount = sheet.getRows();
		DataObjectList dbos = new DataObjectList();
		ArrayList errorlist = new ArrayList();
		ArrayList err = new ArrayList();
		Map interWireNames = new HashMap();
		for (int i = 0; i < rowcount; i++) {
			if (ImpExpUtils.isEmptyRow(sheet, i)) { // 判断是否为空行
				if (i == 0)
					errorlist.add("导入表格第一页，第一行为空，不符合导入表格要求！");
				break;
			}
			InterWire dbo = new InterWire();
			String labelCn = ImpExpUtils.getSheetValueByLabel(sheet,"楼内光缆名称", i, err);
			dbo.setAttrValue(InterWire.AttrName.labelCn, labelCn);

			String cableModel = ImpExpUtils.getSheetValueByLabel(sheet,"型号", i, err);
			dbo.setAttrValue(InterWire.AttrName.cableModel, cableModel);

			String siteName = ImpExpUtils.getSheetValueByLabel(sheet, "所属站点", i, err);
            dbo.setAttrValue(InterWire.AttrName.relatedSiteCuid, siteName);
            
			String origPointCuid = ImpExpUtils.getSheetValueByLabel(sheet, "A端机房", i, err);
			dbo.setAttrValue(InterWire.AttrName.origPointCuid, origPointCuid);

			String destPointCuid = ImpExpUtils.getSheetValueByLabel(sheet, "Z端机房", i, err);
			dbo.setAttrValue(InterWire.AttrName.destPointCuid, destPointCuid);

			String numWire = ImpExpUtils.getSheetValueByLabel(sheet, "纤芯数", i, err);
			dbo.setAttrValue(InterWire.AttrName.numWire, numWire);

			String length = ImpExpUtils.getSheetValueByLabel(sheet, "皮长", i, err);
			dbo.setAttrValue(InterWire.AttrName.length, length);
			
			String remark = ImpExpUtils.getSheetValueByLabel(sheet, "备注", i, err);
			dbo.setAttrValue(InterWire.AttrName.remark, remark);

			// 验证有无缺失的属性列。
			if (err.size() != 0) {
				err.add(ImpExpConsts.ERROR_INFO);
				return err;
			}
			//验证在Excel表中是否重名的对象。
            if ((!labelCn.equals(""))) {
                if (interWireNames.get(labelCn) != null) {
                    errorlist.add("非法数据：在excel表格中，第" + i + "行与第" + interWireNames.get(labelCn) + "行中继光缆名称重复！");
                } else {
                    interWireNames.put(labelCn, Integer.valueOf(i));
                }
            }
			dbos.add(dbo);
		}
		LogHome.getLog().info("===层间光缆导入数据===" + dbos.size());
		if (errorlist.size() != 0) {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
		// 调用服务器端接口
		errorlist = getDataImportBO().importInterWire(new BoActionContext(), dbos);
		LogHome.getLog().info("===层间光缆导入返回结果===" + dbos + errorlist.size() + "==" + errorlist);
		ArrayList namelist = new ArrayList();
		if (errorlist.size() == 0) {
			LogHome.getLog().info("===增加光纤数据===");
			for (int i = 1; i < dbos.size(); i++) {
				String name = (String) dbos.get(i).getAttrValue(InterWire.AttrName.labelCn);
				namelist.add(name);
			}
			if (namelist != null && namelist.size() > 0) {
				DataObjectList segs = new DataObjectList();
				DataObjectList segDbos = queryWireSegbyName(namelist, InterWire.CLASS_NAME);
				LogHome.getLog().info("===导入的层间光缆数据===");
				if (segDbos != null && segDbos.size() > 0) {
					HashMap<String, DataObjectList> queryFiber = queryFiber(segDbos);
					for (GenericDO dbo : segDbos) {
						String cuid = dbo.getCuid();
						String labelCn = dbo.getAttrString(InterWire.AttrName.labelCn);
						DataObjectList fibers = queryFiber.get(cuid);
						if (fibers != null && fibers.size() > 0) {
							errorlist.add(labelCn + "楼内光缆下已存在纤芯，不能重复导入");
							return errorlist;
						}
					}
					createFibers(segDbos);
				}
			}
			errorlist.add("成功导入楼内光缆数据" + (dbos.size() - 1) + "条。");
			errorlist.addAll(namelist);
			return errorlist;
		} else {
			errorlist.add(ImpExpConsts.ERROR_INFO);
			return errorlist;
		}
	}
	
	/**
	 * 导入层间光缆时 生成光纤
	 * @param segDbos
	 * @throws Exception
	 */
	public void createFibers(DataObjectList segDbos) throws Exception {
		LogHome.getLog().info("===进入导入层间光缆时生成光纤数据方法===" + segDbos);
		DataObjectList addFibers = new DataObjectList();
		if (segDbos != null && segDbos.size() > 0) {
			for (GenericDO dbo : segDbos) {
				long fiberCount = 0L;

				String cuid = dbo.getCuid();
				fiberCount = dbo.getAttrLong(InterWire.AttrName.numWire);
				String destPointCuid = dbo.getAttrString(InterWire.AttrName.destPointCuid);
				String origPointCuid = dbo.getAttrString(InterWire.AttrName.origPointCuid);
				double length = dbo.getAttrDouble(InterWire.AttrName.length);
				long ownership = dbo.getAttrLong(InterWire.AttrName.ownership);

				for (long fiberNo = 0; fiberNo < fiberCount; fiberNo++) {
					Fiber fiber = new Fiber();
					Long fNo = new Long(fiberNo + 1);
					fiber.setAttrValue(Fiber.AttrName.labelCn, fNo.toString());
					fiber.setAttrValue(Fiber.AttrName.relatedSystemCuid, cuid);
					fiber.setAttrValue(Fiber.AttrName.relatedSegCuid, cuid);
					fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destPointCuid);
					fiber.setAttrValue(Fiber.AttrName.origSiteCuid, origPointCuid);
					fiber.setAttrValue(Fiber.AttrName.destEqpCuid, destPointCuid);
					fiber.setAttrValue(Fiber.AttrName.origEqpCuid, origPointCuid);
					fiber.setAttrValue(Fiber.AttrName.wireNo, fNo);
					DecimalFormat df = new DecimalFormat("#.00");
					fiber.setAttrValue(Fiber.AttrName.length, df.format(length));
					fiber.setFiberLevel(new Long(0));
					long color = (Color.WHITE).getRGB();
					fiber.setAttrValue(Fiber.AttrName.fiberColor, color);
					// 加默认值
					fiber.setAttrValue(Fiber.AttrName.signalDirection, new Long(1));
					fiber.setAttrValue(Fiber.AttrName.ownership, ownership);
					fiber.setAttrValue(Fiber.AttrName.maintMode, new Long(1));
					fiber.setAttrValue(Fiber.AttrName.purpose, new Long(1));
					fiber.setAttrValue(Fiber.AttrName.usageState, new Long(1));
					fiber.setAttrValue(Fiber.AttrName.signalDirection, new Long(1));
					fiber.setAttrValue(Fiber.AttrName.aveAttenu1310, new Double(0.0));
					fiber.setAttrValue(Fiber.AttrName.sumAttenu1310, new Double(0.0));
					fiber.setAttrValue(Fiber.AttrName.sumAttenu1550, new Double(0.0));
					fiber.setAttrValue(Fiber.AttrName.aveAttenu1550, new Double(0.0));

					addFibers.add(fiber);
				}
			}
		}
		LogHome.getLog().info("===要增加的光纤数据===" + addFibers.size());
		if (addFibers.size() > 0) {
			DataObjectList fibers = getDuctManagerBO().createDMDOs(new BoActionContext(), addFibers);
			LogHome.getLog().info(fibers.size() + "纤芯已经生成 ");
		}
	}
}
