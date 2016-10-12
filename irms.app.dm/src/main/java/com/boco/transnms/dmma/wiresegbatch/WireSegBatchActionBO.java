package com.boco.transnms.dmma.wiresegbatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctBranchBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;
import com.boco.transnms.server.bo.ibo.dm.IHangWallBO;
import com.boco.transnms.server.bo.ibo.dm.IHangWallSegBO;
import com.boco.transnms.server.bo.ibo.dm.IPolewayBranchBO;
import com.boco.transnms.server.bo.ibo.dm.IPolewaySegBO;
import com.boco.transnms.server.bo.ibo.dm.IStonewayBranchBO;
import com.boco.transnms.server.bo.ibo.dm.IStonewaySegBO;
import com.boco.transnms.server.bo.ibo.dm.IUpLineBO;
import com.boco.transnms.server.bo.ibo.dm.IUpLineSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
/**
 * 右键菜单处理类
 * @author zhaodong
 *
 */
public class WireSegBatchActionBO  extends WireSegBatchActionBOAbs{
	
	public Map WireSegUpdate(HttpServletRequest request, String data) {
		List<WireSeg> wireSegs = changeViewDataToWireSegs(data,IWireSegBO.class,WireSeg.class);
		saveData(wireSegs,IWireSegBO.class,WireSeg.class);
		return new HashMap();
	}
	
	protected <T extends GenericDO> Map saveData(List<T> datas, Class<?> class1, Class<?> class2){
		BoActionContext boActionContext = new  BoActionContext();
		if(datas != null){
			if(StringUtils.equalsIgnoreCase("IWireSegBO", class1.getSimpleName())){
				IWireSegBO  wireSegBO =BoHomeFactory.getInstance().getBO(IWireSegBO.class);
				for(T mapData:datas){
					WireSeg wireseg = (WireSeg) mapData;
					wireSegBO.modifyWireSeg(boActionContext, wireseg );
				}
			}
			if(StringUtils.equalsIgnoreCase("IPolewayBranchBO", class1.getSimpleName())){
				IPolewayBranchBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IPolewayBranchBO.class);
				for(T mapData:datas){
					PolewayBranch polewayBranch = (PolewayBranch) mapData;
					polewayBranchBO.modifyPolewayBranch(boActionContext, polewayBranch );
				}
			}
			if(StringUtils.equalsIgnoreCase("IPolewaySegBO", class1.getSimpleName())){
				IPolewaySegBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IPolewaySegBO.class);
				for(T mapData:datas){
					PolewaySeg polewaySeg = (PolewaySeg) mapData;
					polewayBranchBO.modifyPolewaySeg(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IUpLineSegBO", class1.getSimpleName())){
				IUpLineSegBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IUpLineSegBO.class);
				for(T mapData:datas){
					UpLineSeg polewaySeg = (UpLineSeg) mapData;
					polewayBranchBO.modifyUpLineSeg(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IUpLineBO", class1.getSimpleName())){
				IUpLineBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IUpLineBO.class);
				for(T mapData:datas){
					UpLine polewaySeg = (UpLine) mapData;
					polewayBranchBO.modifyUpLine(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IHangWallSegBO", class1.getSimpleName())){
				IHangWallSegBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IHangWallSegBO.class);
				for(T mapData:datas){
					HangWallSeg polewaySeg = (HangWallSeg) mapData;
					polewayBranchBO.modifyHangWallSeg(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IHangWallBO", class1.getSimpleName())){
				IHangWallBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IHangWallBO.class);
				for(T mapData:datas){
					HangWall polewaySeg = (HangWall) mapData;
					polewayBranchBO.modifyHangWall(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IStonewayBranchBO", class1.getSimpleName())){
				IStonewayBranchBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IStonewayBranchBO.class);
				for(T mapData:datas){
					StonewayBranch polewaySeg = (StonewayBranch) mapData;
					polewayBranchBO.modifyStonewayBranch(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IStonewaySegBO", class1.getSimpleName())){
				IStonewaySegBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IStonewaySegBO.class);
				for(T mapData:datas){
					StonewaySeg polewaySeg = (StonewaySeg) mapData;
					polewayBranchBO.modifyStonewaySeg(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IDuctSegBO", class1.getSimpleName())){
				IDuctSegBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IDuctSegBO.class);
				for(T mapData:datas){
					DuctSeg polewaySeg = (DuctSeg) mapData;
					polewayBranchBO.modifyDuctSeg(boActionContext, polewaySeg);
				}
			}
			if(StringUtils.equalsIgnoreCase("IDuctBranchBO", class1.getSimpleName())){
				IDuctBranchBO  polewayBranchBO =BoHomeFactory.getInstance().getBO(IDuctBranchBO.class);
				for(T mapData:datas){
					DuctBranch polewaySeg = (DuctBranch) mapData;
					polewayBranchBO.modifyDuctBranch(boActionContext, polewaySeg);
				}
			}
		}
		return new HashMap();
	}

	public Map PolewayBranchUpdate(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
		for(int index=0;index<jsonArray.size();index++){
			Map jsonData = (Map) jsonArray.get(index);
			String cuid = (String) jsonData.get("CUID");
			String clsName = cuid.split("-")[0];
			if(StringUtils.equalsIgnoreCase("POLEWAY_SEG", clsName)){
				List<PolewaySeg> polewayBranchs = changeViewDataToWireSegs(data,IPolewaySegBO.class,PolewaySeg.class);
				saveData(polewayBranchs,IPolewaySegBO.class,PolewaySeg.class);
			}else if(StringUtils.equalsIgnoreCase("POLEWAY_BRANCH", clsName)){
				List<PolewayBranch> polewayBranchs = changeViewDataToWireSegs(data,IPolewayBranchBO.class,PolewayBranch.class);
				saveData(polewayBranchs,IPolewayBranchBO.class,PolewayBranch.class);
			}
		}
		return new HashMap();
	}
	public <T extends GenericDO> T  CopyDbRecordValue(String clsName, Object wireSegBO,String cuid) {
		T wireSeg = null; 
		if(wireSegBO == null){
			 wireSegBO =BoHomeFactory.getInstance().getBO(clsName);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IPolewaySegBO.class.getSimpleName())){
			 IPolewaySegBO  wireSegBO2 = (IPolewaySegBO)wireSegBO;
			 if(!StringUtils.equalsIgnoreCase("POLEWAY_SEG", cuid.split("-")[0])){
				 return null;
			 }
			  wireSeg = (T) wireSegBO2.getPolewaySegBySql(new BoActionContext(), " CUID = '"+cuid+"'").get(0);
			
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IWireSegBO.class.getSimpleName())){
			 IWireSegBO  wireSegBO2 = (IWireSegBO)wireSegBO;
			  wireSeg = (T) wireSegBO2.getWireSegByCuid(new BoActionContext(), cuid);
			
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IPolewayBranchBO.class.getSimpleName())){
			IPolewayBranchBO  wireSegBO2 = (IPolewayBranchBO)wireSegBO;
			 if(!StringUtils.equalsIgnoreCase("POLEWAY_BRANCH", cuid.split("-")[0])){
				 return null;
			 }
			 wireSeg = (T) wireSegBO2.getPolewayBranchBySql(new BoActionContext(), " CUID = '"+cuid+"'").get(0);
			 
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IDuctBranchBO.class.getSimpleName())){
			IDuctBranchBO  wireSegBO2 = (IDuctBranchBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getDuctBranchBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
			
		}
		
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IStonewayBranchBO.class.getSimpleName())){
			IStonewayBranchBO  wireSegBO2 = (IStonewayBranchBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getStonewayBranchBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IStonewaySegBO.class.getSimpleName())){
			IStonewaySegBO  wireSegBO2 = (IStonewaySegBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getStonewaySegBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IUpLineBO.class.getSimpleName())){
			IUpLineBO  wireSegBO2 = (IUpLineBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getUpLinesBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
			
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IUpLineSegBO.class.getSimpleName())){
			IUpLineSegBO  wireSegBO2 = (IUpLineSegBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getSegmentsBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IHangWallBO.class.getSimpleName())){
			IHangWallBO  wireSegBO2 = (IHangWallBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getHangWallsBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IHangWallSegBO.class.getSimpleName())){
			IHangWallSegBO  wireSegBO2 = (IHangWallSegBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getSegmentsBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IDuctSegBO.class.getSimpleName())){
			IDuctSegBO  wireSegBO2 = (IDuctSegBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getDuctSegBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		if(wireSegBO != null && StringUtils.equalsIgnoreCase(clsName, IDuctBranchBO.class.getSimpleName())){
			IDuctBranchBO  wireSegBO2 = (IDuctBranchBO)wireSegBO;
			 wireSeg = (T) wireSegBO2.getDuctBranchBySql(new BoActionContext(), " CUID ='"+cuid+"'").get(0);
		}
		return wireSeg;
	}

	public Map DuctBranchUpdate(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
		for(int index=0;index<jsonArray.size();index++){
			Map jsonData = (Map) jsonArray.get(index);
			String cuid = (String) jsonData.get("CUID");
			String clsName = cuid.split("-")[0];
			if(StringUtils.equalsIgnoreCase("DUCT_SEG", clsName)){
				 List<DuctSeg> ductBranchs = changeViewDataToWireSegs(data,IDuctSegBO.class,DuctSeg.class);
			     saveData(ductBranchs,IDuctSegBO.class,DuctSeg.class);
			}else if(StringUtils.equalsIgnoreCase("DUCT_BRANCH", clsName)){
				 List<DuctBranch> ductBranchs = changeViewDataToWireSegs(data,IDuctBranchBO.class,DuctBranch.class);
			     saveData(ductBranchs,IDuctBranchBO.class,DuctBranch.class);
			}
		}
		return new HashMap();
	}

	public Map StonewayBranchUpdate(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
		for(int index=0;index<jsonArray.size();index++){
			Map jsonData = (Map) jsonArray.get(index);
			String cuid = (String) jsonData.get("CUID");
			String clsName = cuid.split("-")[0];
			if(StringUtils.equalsIgnoreCase("STONEWAY_SEG", clsName)){
				List<StonewaySeg> upLineSegs = changeViewDataToWireSegs(data,IStonewaySegBO.class,StonewaySeg.class);
				saveData(upLineSegs,IStonewaySegBO.class,StonewaySeg.class);
			}else if(StringUtils.equalsIgnoreCase("STONEWAY_BRANCH", clsName)){
				List<StonewayBranch> stonewayBranchs = changeViewDataToWireSegs(data,IStonewayBranchBO.class,StonewayBranch.class);
				saveData(stonewayBranchs,IStonewayBranchBO.class,StonewayBranch.class);
			}
		}
		return new HashMap();
	}

	public Map UpLineUpdate(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
		for(int index=0;index<jsonArray.size();index++){
			Map jsonData = (Map) jsonArray.get(index);
			String cuid = (String) jsonData.get("CUID");
			String clsName = cuid.split("-")[0];
			if(StringUtils.equalsIgnoreCase("UP_LINE_SEG", clsName)){
				List<UpLineSeg> upLineSegs = changeViewDataToWireSegs(data,IUpLineSegBO.class,UpLineSeg.class);
				saveData(upLineSegs,IUpLineSegBO.class,UpLineSeg.class);
			}else if(StringUtils.equalsIgnoreCase("UP_LINE_BRANCH", clsName)){
				List<UpLine> upLines = changeViewDataToWireSegs(data,IUpLineBO.class,UpLine.class);
				saveData(upLines,IUpLineBO.class,UpLine.class);
			}
		}
		return new HashMap();
	}

	public Map HangWallUpdate(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
		for(int index=0;index<jsonArray.size();index++){
			Map jsonData = (Map) jsonArray.get(index);
			String cuid = (String) jsonData.get("CUID");
			String clsName = cuid.split("-")[0];
			if(StringUtils.equalsIgnoreCase("HANG_WALL_SEG", clsName)){
				List<HangWallSeg> hangWalls = changeViewDataToWireSegs(data,IHangWallSegBO.class, HangWallSeg.class);
				saveData(hangWalls,IHangWallSegBO.class, HangWallSeg.class);
			}else if(StringUtils.equalsIgnoreCase("HANG_WALL_BRANCH", clsName)){
				List<HangWall> hangWalls = changeViewDataToWireSegs(data,IHangWallBO.class, HangWall.class);
				saveData(hangWalls,IHangWallBO.class, HangWall.class);
			}
		}
		return new HashMap();
	}
	
	
}
