package com.boco.irms.app.dm.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;

import com.boco.common.util.debug.LogHome;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.GenericNode;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.LineSystem;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;



@Controller
public class CalculateSystemLengthAction {
	
	 static Map<String, String> mapBO = new HashMap<String, String>();
	 static {
	        mapBO.put(DuctSeg.CLASS_NAME, DuctSegBOHelper.ActionName.modifyDuctSegsAndDuctHolesAndChildHoles);
	        mapBO.put(PolewaySeg.CLASS_NAME, PolewaySegBOHelper.ActionName.modifyPolewaySegsAndSyncCarryingCables);
	        mapBO.put(StonewaySeg.CLASS_NAME, StonewaySegBOHelper.ActionName.modifyStonewaySegs);
	        mapBO.put(UpLineSeg.CLASS_NAME, UpLineSegBOHelper.ActionName.modifyUpLineSegs);
	        mapBO.put(HangWallSeg.CLASS_NAME, HangWallSegBOHelper.ActionName.modifyHangWallSegs);

	        mapBO.put(DuctBranch.CLASS_NAME, DuctSegBOHelper.ActionName.modifyDuctSegsAndDuctHolesAndChildHoles);
	        mapBO.put(PolewayBranch.CLASS_NAME, PolewaySegBOHelper.ActionName.modifyPolewaySegsAndSyncCarryingCables);
	        mapBO.put(StonewayBranch.CLASS_NAME, StonewaySegBOHelper.ActionName.modifyStonewaySegs);

	        mapBO.put(DuctSystem.CLASS_NAME, DuctSegBOHelper.ActionName.modifyDuctSegsAndDuctHolesAndChildHoles);
	        mapBO.put(PolewaySystem.CLASS_NAME, PolewaySegBOHelper.ActionName.modifyPolewaySegsAndSyncCarryingCables);
	        mapBO.put(StonewaySystem.CLASS_NAME, StonewaySegBOHelper.ActionName.modifyStonewaySegs);
	        mapBO.put(UpLine.CLASS_NAME, UpLineSegBOHelper.ActionName.modifyUpLineSegs);
	        mapBO.put(HangWall.CLASS_NAME, HangWallSegBOHelper.ActionName.modifyHangWallSegs);

	        mapBO.put(WireSystem.CLASS_NAME, WireSegBOHelper.ActionName.modifyWireSegs);
	        mapBO.put(WireSeg.CLASS_NAME, WireSegBOHelper.ActionName.modifyWireSegs);
	 }
    //管道系统计算系统长度长度求和
	public void doCalculateSystemLength(String data) throws Exception {
		double len = 0.0;
		String cuid = " cuid ='" + data + "'";
		DataObjectList datas = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctSystemBO.getDuctSystemBySql",new BoActionContext(), cuid);
		if (datas != null && datas.size() > 0) {
			DuctSystem leng = (DuctSystem) datas.get(0);
			DataObjectList ductsegs = null;
			try {
				ductsegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid",new BoActionContext(), data);
				if (ductsegs != null && ductsegs.size() > 0) {
					for (GenericDO seg : ductsegs) {
						DuctSeg ducts = (DuctSeg) seg;
						Object length = ducts.getAttrValue("LENGTH");
						if (length instanceof Double) {
							len += Double.valueOf("" + length);
						}
					}
					leng.setAttrValue("LENGTH", len);
				}
				DataObjectList res = new DataObjectList();
				res.add(leng);
				BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),res);
				BoCmdFactory.getInstance().execBoCmd("IDuctSystemBO.getDuctSystemBySql",new BoActionContext(), cuid);
			} catch (Exception e) {
				throw new UserException(e.getMessage());
			}
		}
	}
	//计算长度重新计算
	public void modifyCalculateSystemLength(String data) {
		try {
			Map tmpMap = new HashMap();
			Map map = new HashMap();
			Map systemMap = new HashMap();
			
			DataObjectList datas = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSystemRoutePositionByCuid",new BoActionContext(), data, false);
			if(datas!=null && datas.size()>0){
				setSegsLength(datas, tmpMap);
				GenericDO res = datas.get(0);
				Map mapBySystem = TopoHelper.getListMapByAttr(datas,DuctSeg.AttrName.relatedSystemCuid);
				if (mapBySystem != null) {
					Iterator it = mapBySystem.keySet().iterator();
					while (it.hasNext()) {
						String relatedSystemCuid = (String) it.next();
						DataObjectList segs = (DataObjectList) mapBySystem.get(relatedSystemCuid);
						if (segs != null && segs.size() > 0) {
							double len = 0.0;
							for (GenericDO seg : segs) {
								if (seg != null) {
									Double length = seg.getAttrDouble(DuctSeg.AttrName.length, 0.0);
									if (length != null && length >= 0) {
										len += length;
									}
								}
							}
							// 系统长度的计算，只需四舍五入到小数点后两位。
							DecimalFormat doubleformat = new DecimalFormat(".00");
							len = Double.parseDouble(doubleformat.format(len));
							if (len >= 0) {
								GenericDO system = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), data);
								system.setAttrValue(DuctSystem.AttrName.length, len);
								datas.add(system);
							}
//							if (map.get(relatedSystemCuid) != null) {
//                                GenericNode node = (GenericNode) map.get(relatedSystemCuid);
//                                Object obj = node.getClientProperty(DuctSystem.AttrName.length);
//                                if (obj instanceof Double) { //&& (Double) obj <= 0
							
//                                }
//                            }
//							DuctSeg leng = new DuctSeg();
//							system.setAttrValue("LENGTH", len);
//							datas.add(leng);
						}
					}
				}
//				DataObjectList datass = new DataObjectList();
//				datass.add(system);
				BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),datas);
			}
//			if (systemMap.size() > 0) {
//				Iterator it = systemMap.keySet().iterator();
//				while (it.hasNext()) {
//					String systemCuid = (String) it.next();
//					if (map.get(systemCuid) != null) {
//						GenericNode node = (GenericNode) map.get(systemCuid);
//						node.putClientProperty(DuctSystem.AttrName.length,systemMap.get(systemCuid));
//					}
//				}
//			}
		} catch (Exception e) {
			LogHome.getLog().info(e.getCause());
		}
	}
    //设置段的长短属性
	public static void setSegsLength(DataObjectList list, Map map) {
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				GenericDO gdo = (GenericDO) list.get(i);
				String cuid = gdo.getCuid();
				gdo.convAllObjAttrToCuid();
				if (cuid != null && !cuid.startsWith(WireSeg.CLASS_NAME)) {
					double s = 0.0;
					s = calculateSegLength(gdo);
					gdo.setAttrValue(DuctSeg.AttrName.length, s);
				}
				if (map.isEmpty()) {
					continue;
				}
				GenericNode gNode = (GenericNode) map.get(cuid);
				if (gNode != null) {
					gNode.putClientProperty(DuctSeg.AttrName.length,gdo.getAttrValue(DuctSeg.AttrName.length));
				}
			}
		}
	}
	//根据经纬度计算段的长度
    public static double calculateSegLength(GenericDO gdo) {
        double s = gdo.getAttrDouble(DuctSeg.AttrName.length, 0.0);
        double origLatitude = gdo.getAttrDouble(DMCacheObjectName.origRealLatitude, 0.0);
        double origLongitude = gdo.getAttrDouble(DMCacheObjectName.origRealLongitude, 0.0);
        double destLatitude = gdo.getAttrDouble(DMCacheObjectName.destRealLatitude, 0.0);
        double destLongitude = gdo.getAttrDouble(DMCacheObjectName.destRealLongitude, 0.0);
        if (DMHelper.isCoordAvailable(origLatitude, origLongitude) && DMHelper.isCoordAvailable(destLatitude, destLongitude)) {
            s = GraphkitUtils.getDistance(origLongitude, origLatitude, destLongitude, destLatitude);
            DecimalFormat formatter = new DecimalFormat(".00");
            String temp = formatter.format(s);
            s = Double.parseDouble(temp);
        }
        return s;
    }
    //杆路系统计算系统长度长度求和
  	public void  doPwsCalculateSystemLength(String data) throws Exception{
  		double len=0.0;
  		String cuid = " cuid ='" + data+"'";
  		DataObjectList datas=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IPolewaySystemBO.getPolewaySystemBySql",  new BoActionContext(),cuid);
  		if(datas!=null && datas.size()>0){
  		PolewaySystem leng = (PolewaySystem)datas.get(0);
  		DataObjectList ductsegs = null;
  		try {
  		    ductsegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid", new BoActionContext(),data);
			if (ductsegs != null && ductsegs.size() > 0) {
				for (GenericDO seg : ductsegs) {
					PolewaySeg ducts = (PolewaySeg) seg;
					Object length = ducts.getAttrValue("LENGTH");
					if (length instanceof Double) {
						len += Double.valueOf("" + length);
					}
				}
				leng.setAttrValue("LENGTH", len);
			}
			DataObjectList res = new DataObjectList();
  			res.add(leng);
  		    BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),res);
  			BoCmdFactory.getInstance().execBoCmd("IPolewaySystemBO.getPolewaySystemBySql",new BoActionContext(),cuid);
  		} catch (Exception e) {
  			throw new UserException(e.getMessage());
  		}
  		}
  	}
  	//标石系统计算系统长度长度求和
	public void doSwsCalculateSystemLength(String data) throws Exception {
		double len = 0.0;
		String cuid = " cuid ='" + data + "'";
		DataObjectList datas = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IStonewaySystemBO.getStonewaySystemBySql",new BoActionContext(), cuid);
		if (datas != null && datas.size() > 0) {
			StonewaySystem leng = (StonewaySystem) datas.get(0);
			DataObjectList ductsegs = null;
			try {
				ductsegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid",new BoActionContext(), data);
				if (ductsegs != null && ductsegs.size() > 0) {
					for (GenericDO seg : ductsegs) {
						StonewaySeg ducts = (StonewaySeg) seg;
						Object length = ducts.getAttrValue("LENGTH");
						if (length instanceof Double) {
							len += Double.valueOf("" + length);
						}
					}
					leng.setAttrValue("LENGTH", len);
				}
				DataObjectList res = new DataObjectList();
				res.add(leng);
				BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),res);
				BoCmdFactory.getInstance().execBoCmd("IStonewaySystemBO.getStonewaySystemBySql",new BoActionContext(), cuid);
			} catch (Exception e) {
				throw new UserException(e.getMessage());
			}
		}
	}
	// 引上系统计算系统长度长度求和
	public void doUpCalculateSystemLength(String data) throws Exception {
		double len = 0.0;
		String cuid = " cuid ='" + data + "'";
		DataObjectList datas = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IUpLineBO.getUpLinesBySql", new BoActionContext(),cuid);
		if (datas != null && datas.size() > 0) {
			UpLine leng = (UpLine) datas.get(0);
			DataObjectList ductsegs = null;
			try {
				ductsegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid",new BoActionContext(), data);
				if (ductsegs != null && ductsegs.size() > 0) {
					for (GenericDO seg : ductsegs) {
						UpLineSeg ducts = (UpLineSeg) seg;
						Object length = ducts.getAttrValue("LENGTH");
						if (length instanceof Double) {
							len += Double.valueOf("" + length);
						}
					}
					leng.setAttrValue("LENGTH", len);
				}
				DataObjectList res = new DataObjectList();
				res.add(leng);
				BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),res);
				BoCmdFactory.getInstance().execBoCmd("IUpLineBO.getUpLinesBySql", new BoActionContext(),cuid);
			} catch (Exception e) {
				throw new UserException(e.getMessage());
			}
		}
	}
	
	// 挂墙系统计算系统长度长度求和
	public void doHWCalculateSystemLength(String data) throws Exception {
		double len = 0.0;
		String cuid = " cuid ='" + data + "'";
		DataObjectList datas = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IHangWallBO.getHangWallsBySql", new BoActionContext(),cuid);
		if (datas != null && datas.size() > 0) {
			HangWall leng = (HangWall) datas.get(0);
			DataObjectList hangWallsegs = null;
			try {
				hangWallsegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid",new BoActionContext(), data);
				if (hangWallsegs != null && hangWallsegs.size() > 0) {
					for (GenericDO seg : hangWallsegs) {
						HangWallSeg hangWalls = (HangWallSeg) seg;
						Object length = hangWalls.getAttrValue("LENGTH");
						if (length instanceof Double) {
							len += Double.valueOf("" + length);
						}
					}
					leng.setAttrValue("LENGTH", len);
				}
				DataObjectList res = new DataObjectList();
				res.add(leng);
				BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.modifySystems", new BoActionContext(),res);
				BoCmdFactory.getInstance().execBoCmd("IHangWallBO.getHangWallsBySql", new BoActionContext(),cuid);
			} catch (Exception e) {
				throw new UserException(e.getMessage());
			}
		}
	}
	
	// 段计算系统长度长度求和
	public void doDMCalculateSystemLength(String data) throws Exception {
		try {
			String cuid = " cuid ='" + data + "'";
			Map map = new HashMap();
			DataObjectList dol = new DataObjectList();
			DataObjectList datas = new DataObjectList();
			String className = null;
			if (className == null) {
				className = GenericDO.parseClassNameFromCuid(data);
			}
			DataObjectList holes = BoHomeFactory.getInstance().getBO(IDuctSegBO.class).getSimpleDuctSegsBySql(new BoActionContext(),"cuid ='" + data + "'");
			DataObjectList res = new DataObjectList();
			IDuctManagerBO ductManagerBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			DataObjectList rtnList = ductManagerBO.getRoutePointsByLineSegs(new BoActionContext(), holes);
			dol.addAll(rtnList);
			setSegsLength(dol, map);
			if (className.equals(DuctSeg.CLASS_NAME)|| className.equals(PolewaySeg.CLASS_NAME)) {
				BoCmdFactory.getInstance().execBoCmd(mapBO.get(className),new BoActionContext(), dol, true);
			} else {
				BoCmdFactory.getInstance().execBoCmd(mapBO.get(className),new BoActionContext(), dol);
			}
		} catch (Exception e) {
			LogHome.getLog().info(e.getMessage());
		}
	}
}
