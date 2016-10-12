package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DrawPoint;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.dm.InterruptPoint;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;
import com.boco.transnms.server.dao.base.DaoHelper;

public class WireInterruptAction {
	
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	
	private IWireSystemBO getWireSystemBO(){
		return BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	public List getWireSystemByCuid(String cuid) throws IOException{
//		StringBuffer result = new StringBuffer();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		DataObjectList wireSystems = new DataObjectList();
		try {
			if(cuid.startsWith(Site.CLASS_NAME) || cuid.startsWith(FiberJointBox.CLASS_NAME) || cuid.startsWith(FiberDp.CLASS_NAME) || cuid.startsWith(FiberCab.CLASS_NAME)){
				String sql = WireSeg.AttrName.origPointCuid + "='" + cuid +  "' or " + WireSeg.AttrName.destPointCuid + "='" + cuid + "'";
				DataObjectList segs = (DataObjectList)getWireSegBO().getWireSegsBySql(
                        new BoActionContext(), sql);
				DataObjectList systems = getWireSystems(segs);
				if(systems != null && systems.size()>0){
					wireSystems.addAll(systems);
				}
				
			}else if(cuid.startsWith(WireSeg.CLASS_NAME)){
				WireSeg wireSeg = getWireSegBO().getWireSegByCuid(new BoActionContext(), cuid);
				String wireSystemCuid = wireSeg.getRelatedSystemCuid();
				WireSystem wireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(), wireSystemCuid);
				if(wireSystem != null){
					wireSystems.add(wireSystem);
				}
			}else if(cuid.startsWith(WireSystem.CLASS_NAME)){
				 WireSystem wireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(), cuid);
				 if(wireSystem != null){
					wireSystems.add(wireSystem);
				 }
			}
			if(wireSystems.size()>0){
				for(int i=0;i<wireSystems.size();i++){
					Map<String, String> map = new HashMap<String, String>();
					WireSystem wireSystem = (WireSystem) wireSystems.get(i);
					String wireCuid = wireSystem.getCuid();
					String labelCn = wireSystem.getLabelCn();
					
					//
					map.put("CUID", wireCuid);
					map.put("LABEL_CN", labelCn);
					list.add(map);
				}
//				String jsonString = JSON.toJSONString(wireSystems);
			}
		} catch (Throwable e) {
			
//			result = NmsUtils.getStringBufferErrJson(e.getMessage());
			LogHome.getLog().info("增加点设施失败：",e);
		}
		return list;
	}
	
    /**
     * 通过光缆段得到其所属系统
     * @param wireSegs
     * @return
     */
    private DataObjectList getWireSystems(DataObjectList wireSegs) {
        if (wireSegs == null || wireSegs.isEmpty()) return null;
        Map<String, WireSystem> systemMap = new HashMap<String, WireSystem>();
        DataObjectList wireSystems = new DataObjectList();
        for (GenericDO dto : wireSegs) {
            if (!(dto instanceof WireSeg)) continue;
            WireSeg wireSeg = (WireSeg)dto;
            String wireSystemCuid = wireSeg.getRelatedSystemCuid();
            if (!systemMap.containsKey(wireSystemCuid)) {
                WireSystem wireSystem = (WireSystem) getDuctManagerBO().getObjByCuid(new BoActionContext(),wireSystemCuid);
                wireSystems.add(wireSystem);
                systemMap.put(wireSystemCuid, wireSystem);
            }
        }
        return wireSystems;
    }
    
    /**
	 * @param wireSystemCuid 光缆系统CUID
	 * @param pointCuid 点设施CUID
	 * @return 经过此点设施且属于此光缆系统的光缆段
	 */
	public List<Map<String,String>> getWireSegsBySystemAndPoint(String wireSystemCuid,String pointCuid){
		try{
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			String sql = "SELECT CUID,LABEL_CN FROM WIRE_SEG WHERE RELATED_SYSTEM_CUID= '"+wireSystemCuid+"' AND ( ORIG_POINT_CUID ='"+pointCuid+"' OR DEST_POINT_CUID='"+pointCuid+"')";
			DataObjectList gdoList = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new WireSeg(), sql);
			if(gdoList != null && gdoList.size()>0){
				for(int i=0;i<gdoList.size();i++){
					Map<String, String> map = new HashMap<String, String>();
					GenericDO dbo = gdoList.get(i);
					String cuid = dbo.getCuid();
					String labelCn = dbo.getAttrString(WireSeg.AttrName.labelCn);
					map.put("CUID", cuid);
					map.put("LABEL_CN", labelCn);
					list.add(map);
				}
			}
			return list;
		}catch(Exception ex){
			LogHome.getLog().error(ex.getMessage(),ex);
		}
		return null;
	}
	
	/**
	 * @param systemCuid 光缆系统CUID
	 * @return 获取光缆系统的所有点设施
	 */
	public List<Map<String,String>> getSystemPointsByWireSystemCuid(String systemCuid){
		try{
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			String sql = "SELECT DEST_POINT_CUID,ORIG_POINT_CUID FROM WIRE_SEG WHERE RELATED_SYSTEM_CUID= '"+systemCuid+"'";
			DataObjectList gdoList = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new WireSeg(), sql);
			if(gdoList != null && gdoList.size()>0){
				for(int i=0;i<gdoList.size();i++){
					Map<String, String> map = new HashMap<String, String>();
					GenericDO dbo = gdoList.get(i);
					String origPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(WireSeg.AttrName.origPointCuid));
					String destPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(WireSeg.AttrName.destPointCuid));
					GenericDO origDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), origPointCuid);
					GenericDO destDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), destPointCuid);
					String origPointName = DMHelper.getRelatedCuid(origDbo.getAttrValue(WireSeg.AttrName.labelCn));
					String destPointName = DMHelper.getRelatedCuid(destDbo.getAttrValue(WireSeg.AttrName.labelCn));
					map.put("ORIG_POINT_CUID", origPointCuid);
					map.put("ORIG_POINT_NAME", origPointName);
					map.put("DEST_POINT_CUID", destPointCuid);
					map.put("DEST_POINT_NAME", destPointName);
					list.add(map);
				}
			}
			return list;
		}catch(Exception ex){
			LogHome.getLog().error(ex.getMessage(),ex);
		}
		return null;
	}

   public List getWireInterruptPoint(String wireSegCuid,String wireNo,String interruptDistance,String wirePointCuid){

		try {
			InterruptPoint interruptPoint = new InterruptPoint();
			interruptPoint.setRelatedWireSegCuid(wireSegCuid);
			interruptPoint.setWireNo(Long.parseLong(wireNo));
			interruptPoint.setInterruptDistance(Double.parseDouble(interruptDistance));
			// 获取断点，其中包括断点位置

			InterruptPoint point = getInterruptPoint(interruptPoint,wirePointCuid);

			if (point != null) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, Object> map = new HashMap<String, Object>();
				list.add(map);
				Iterator it = point.getAllAttr().keySet().iterator();

				while (it.hasNext()) {
					String key = (String) it.next();
					map.put(key, point.getAttrValue(key));
				}
				return list;
			}

		}catch(Exception e){
			LogHome.getLog().error(e.getMessage(),e);
      }
      return null;
   }
   
	/**
    * 断点查询：根据所给条件查询断点所在位置及相关基本属性
    * @param actionContext 用户信息
    * @param condition 查询所需条件（$表示必要条件）
    * $InterruptPoint.AttrName.relatedWireSystemCuid        断点所在光缆系统CUID
    * $InterruptPoint.AttrName.relatedWireSegCuid           断点所在光缆段CUID
    * InterruptPoint.AttrName.wireNo                       纤芯编号
    * $InterruptPoint.AttrName.interruptDistance            断点距查询点距离M
    * InterruptPoint.AttrName.errorDistance                断点查询允许的误差
    * $InterruptPoint.AttrName.origPointCuid                断点查询的起始点
    * $InterruptPoint.AttrName.destPointCuid                断点查询的终止点
    * 起止点和终止点确定了断点查询的方向，该数据可能与光缆段的起止点相同，亦可能相反，
    * 查询时，沿着“断点查询的起始点”向“断点查询的终止点”方向查询
    * @return
    * 一个基本属性完整的断点对象，包括断点所在的光缆信息，敷设信息，位置信息
    * 其中包含"EEROR_INFO"(List<String>)
    */
   public InterruptPoint getInterruptPoint(InterruptPoint condition,String wirePointCuid){
   	try{
   		String wireSegCuid = condition.getRelatedWireSegCuid();
   		//获取光缆段
           WireSeg wireSeg = (WireSeg) getDuctManagerBO().getObjByCuid(new BoActionContext(),wireSegCuid);
           wireSeg.setAttrValue(WireSeg.AttrName.length,getDoubleByObj(wireSeg.getAttrValue(WireSeg.AttrName.length)));
           
           //测量方向
           condition.setDirection(wirePointCuid.equals(wireSeg.getOrigPointCuid()));
           //如果断点长度超过光缆长度，并且纤芯编号>0，则循环查找下一个光缆
           if (condition.getInterruptDistance() > wireSeg.getLength() && condition.getWireNo() > 0) {
           	//获取纤芯，取第一个纤芯
               DataObjectList fibers = getFiberByNoAndSeg("" + condition.getWireNo(), wireSegCuid);
               Fiber fiber = null;
               if (fibers != null && !fibers.isEmpty() && fibers.get(0) instanceof Fiber) {
                   fiber = (Fiber) fibers.get(0);
               }
               if(fiber != null)
               {
	                String origPointCuid = condition.getDirection() ? fiber.getOrigPointCuid() : fiber.getDestPointCuid();
	
	                List fiberList = getRelatedfiberandPointByJumpFiberAndFiber(
	                        origPointCuid, null, new ArrayList(),
	                        condition.getInterruptDistance(), 0, 0, new ArrayList());
	                if (fiberList != null && !fiberList.isEmpty() && fiberList.get(0) instanceof Fiber) {
	                    fiber = (Fiber) fiberList.get(0);
	                }
	
	                condition.setRelatedWireSegCuid(fiber.getRelatedSegCuid());
	                condition.setRelatedWireSystemCuid(fiber.getRelatedSystemCuid());
	                condition.setDirection("orig".equals(fiber.getAttrValue("FONT")));
	                double interruptDistance = 0.0d;
	                Object disObj = fiber.getAttrValue("ZZLength");
	                if (disObj instanceof Double) {
	                    interruptDistance = ((Double)disObj).doubleValue();
	                }
	                condition.setInterruptDistance(interruptDistance);
               }
           }
           //获取光缆段位置
           return getInterruptPointInWireSeg(condition);
   	}catch(Exception e){
   		LogHome.getLog().error(e.getMessage(),e);
   	}
       return null;
   }
   
   public DataObjectList getFiberByNoAndSeg(String no, String seg) throws Exception{
       DataObjectList fiberslist = new DataObjectList();
       String sql = Fiber.AttrName.relatedSegCuid + "='" + seg + "' and " + Fiber.AttrName.wireNo + "=" + no + "";
       fiberslist = getDuctManagerBO().getObjectsBySql(sql,new Fiber());
       return fiberslist;
   }
   
   /**断点查询，根据纤芯，和方向点，查询纤芯或者跳纤
    * 根据fiber 和 起始点，来确定查询方向
    * @param actionContext BoActionContext
    * @param onePointCuid String
    * @param prvConnectedCuid String
    * @param retList List
    * @param index int
    * @return List    Scale测量距离   jlpoint记录测量距离减去纤芯或者跳线后剩余距离，累加形式 zuizhonglength最终剩余距离（最后一次查询时剩余的距离）
    */
   public List getRelatedfiberandPointByJumpFiberAndFiber(String onePointCuid,String prvConnectedCuid,
       ArrayList retList, double Scale, double jlpoint, double zuizhonglength, ArrayList fhlist)throws Exception {
       //首先查询jumpPair表，如果JumpPair表中有对应的数据，那么继续递归查询，如果没有，那么查询Fiber表
       String curConnectCuid = null;
       String curPointCuid = null;
       List retlist;
       boolean isfind = false;
       if (jlpoint < 0) {
           return fhlist;
       }
       DataObjectList fibers = getFiberByPointCuid(onePointCuid);
       if (fibers != null && fibers.size() > 0) {
           for (int i = 0; i < fibers.size(); i++) {
               Fiber fiber = (Fiber) fibers.get(i);
               if (fiber.getCuid() != null && !fiber.getCuid().equals(prvConnectedCuid)) {
                   fiber.setAttrValue(Fiber.AttrName.length, getDoubleByObj(fiber.getAttrValue(Fiber.AttrName.length)));
                   curConnectCuid = fiber.getCuid();
                   if (!retList.contains(curConnectCuid)) {
                       zuizhonglength = jlpoint;
                       jlpoint = Scale - fiber.getLength();
                       Scale = Scale - fiber.getLength();
                       if (jlpoint < 0) {
                           retlist = new ArrayList();
                           if ((fiber.getDestPointCuid() != null) && fiber.getDestPointCuid().trim().equals(onePointCuid)) {
                               fiber.setAttrValue("FONT", "dest");
                           } else {
                               fiber.setAttrValue("FONT", "orig");
                           }

                           fiber.setAttrValue("ZZLength", zuizhonglength);
                           fhlist.add(fiber);
                       }
                       retList.add(curConnectCuid);
                   }

                   if (onePointCuid.equals(fiber.getOrigPointCuid())) {
                       curPointCuid = fiber.getDestPointCuid();
                       isfind = true;
                   } else if (onePointCuid.equals(fiber.getDestPointCuid())) {
                       curPointCuid = fiber.getOrigPointCuid();
                       isfind = true;
                   }

                   if (isfind) {
                       if (curPointCuid == null) {
                           return retList;
                       } else {
                           if (retList.contains(curPointCuid)) {
                               LogHome.getLog().info("查找到的路径有错误," + curPointCuid + "该节点重复");
                               throw new Exception("查找到的路径有错误");
                           } else {
                               retList.add(curPointCuid);
                               getRelatedfiberandPointByJumpFiberAndFiber(curPointCuid,
                                   curConnectCuid, retList, Scale, jlpoint, zuizhonglength, fhlist);
                           }
                       }
                   }
               }
           }
       }

       //JumpFiber中没有找到，到Fiber中查询
       if (!isfind) {

           DataObjectList jumpFibers = getJumpFiberByPointCuid(onePointCuid);
           if (jumpFibers != null && jumpFibers.size() > 0) {
               for (int i = 0; i < jumpFibers.size(); i++) {
                   JumpFiber jumpFiber = (JumpFiber) jumpFibers.get(i);
                   jumpFiber.setAttrValue(JumpFiber.AttrName.length, getDoubleByObj(jumpFiber.getAttrValue(JumpFiber.AttrName.length)));

                   if (jumpFiber.getCuid() != null && !jumpFiber.getCuid().equals(prvConnectedCuid)) {
                       curConnectCuid = jumpFiber.getCuid();
                       if (!retList.contains(curConnectCuid)) {
                           zuizhonglength = jlpoint;
                           jlpoint = Scale - jumpFiber.getLength();
                           Scale = Scale - jumpFiber.getLength();
                           if (jlpoint < 0) {
                               retlist = new ArrayList();
                               if ((jumpFiber.getDestPointCuid() != null) && jumpFiber.getDestPointCuid().trim().equals(onePointCuid)) {
                                   jumpFiber.setAttrValue("FONT", "dest");
                               } else {
                                   jumpFiber.setAttrValue("FONT", "orig");
                               }

                               jumpFiber.setAttrValue("ZZLength", zuizhonglength);
                               fhlist.add(jumpFiber);
                           }
                           retList.add(curConnectCuid);
                       }

                       if (onePointCuid.equals(jumpFiber.getOrigPointCuid())) {
                           curPointCuid = jumpFiber.getDestPointCuid();
                           isfind = true;
                       } else if (onePointCuid.equals(jumpFiber.getDestPointCuid())) {
                           curPointCuid = jumpFiber.getOrigPointCuid();
                           isfind = true;
                       }

                       if (isfind) {
                           if (curPointCuid == null) {
                               return retList;
                           } else {
                               if (retList.contains(curPointCuid)) {
                                   LogHome.getLog().info("查找到的路径有错误," + curPointCuid + "该节点重复");
                                   throw new Exception("查找到的路径有错误");
                               } else {
                                   retList.add(curPointCuid);
                                   getRelatedfiberandPointByJumpFiberAndFiber(curPointCuid,
                                       curConnectCuid, retList, Scale, jlpoint, zuizhonglength, fhlist);
                               }
                           }
                       }
                   }
               }
           }

       }
       return fhlist;
   }
   
   public DataObjectList getFiberByPointCuid(String pointCuid) throws Exception {
       try {
           DataObjectList dataObjectList = new DataObjectList();
           if (pointCuid != null && pointCuid.trim().length() > 0) {
               String sql1 = Fiber.AttrName.origPointCuid + "='" + pointCuid + "'";
               DataObjectList dataObjectList1 = getDuctManagerBO().getObjectsBySql(sql1,new Fiber());//(Fiber.CLASS_NAME, sql1);
               if (dataObjectList1 != null && dataObjectList1.size()>0) {
                   dataObjectList.addAll(dataObjectList1);
               }
               String sql2 = Fiber.AttrName.destPointCuid + "='" + pointCuid + "'";
               DataObjectList dataObjectList2 =  getDuctManagerBO().getObjectsBySql(sql2,new Fiber());
               if (dataObjectList2 != null && dataObjectList2.size()>0) {
                   dataObjectList.addAll(dataObjectList2);
               }
           }
           return dataObjectList;
       } catch (Exception ex) {
           LogHome.getLog().error("根据连接端点cuid获取纤芯失败", ex);
           throw new Exception(ex);
       }
   }
   
   public DataObjectList getJumpFiberByPointCuid(String pointCuid) throws Exception {
       try {
           DataObjectList dataObjectList = new DataObjectList();
           DataObjectList dataObjectList1 = getJumpFiberByPointCuid(pointCuid, new Integer(1));
           if (dataObjectList1 != null) {
               dataObjectList.addAll(dataObjectList1);
           }
           DataObjectList dataObjectList2 = getJumpFiberByPointCuid(pointCuid, new Integer(2));
           if (dataObjectList2 != null) {
               dataObjectList.addAll(dataObjectList2);
           }
           return dataObjectList;

       } catch (Exception ex) {
           LogHome.getLog().error("根据连接终端点cuid获取跳纤失败", ex);
           throw new Exception(ex.getMessage());
       }
   }
   
   public DataObjectList getJumpFiberByPointCuid(String pointCuid, Integer pointType) throws Exception {
       try {
           return getJumpFiberByPointCuid1(pointCuid, pointType);
       } catch (Exception ex) {
           LogHome.getLog().error("根据连接终端点cuid获取跳纤失败", ex);
           throw new Exception(ex.getMessage());
       }
   }
   
   public DataObjectList getJumpFiberByPointCuid1(String pointCuid, Integer pointType) throws Exception {
       if (DaoHelper.isNotEmpty(pointCuid)) {
           String sql = "";
           if (pointType == 1) {
               sql = sql + JumpFiber.AttrName.origPointCuid + "='" + pointCuid + "'";
           } else {
               sql = sql + JumpFiber.AttrName.destPointCuid + "='" + pointCuid + "'";
           }
           return getDuctManagerBO().getObjectsBySql(sql,new JumpFiber());
       } else {
           return null;
       }
   }
   
   public InterruptPoint getInterruptPointInWireSeg(InterruptPoint condition) throws Exception {
       InterruptPoint result = (InterruptPoint) condition.deepClone();
       Object errObj = result.getAttrValue("ERROR_INFO");
       List<String> errInfo = errObj instanceof ArrayList ?
           (ArrayList)errObj : new ArrayList<String>();
       String wireSegCuid = condition.getRelatedWireSegCuid();
       boolean direction = condition.getDirection();
       double interruptDistance = condition.getInterruptDistance();
       DataObjectList wireToDuctlines = getWireToDuctlineInRoute(wireSegCuid);
       if (wireToDuctlines == null || wireToDuctlines.isEmpty()) {
           errInfo.add("光缆段没有敷设到管线段！");
       }
       Map<String, Double> wireRemainInfo = getWireRemainInfo(wireSegCuid);
       PhysicalJoin ductline = null;
       boolean ductlineLayDirection = true;
       String origRemainCuid = null;
       double origRemainToInterruptLength = 0.0d;
       String destRemainCuid = null;
       double destRemainToInterruptLength = 0.0d;
       for (int i = direction ? 0 : wireToDuctlines.size() - 1;0 <= i && i < wireToDuctlines.size();i = direction ? i + 1 : i - 1) {
           WireToDuctline w2d = (WireToDuctline)wireToDuctlines.get(i);
           String ductlineCuid = w2d.getLineSegCuid();
           w2d.setAttrValue(WireToDuctline.AttrName.direction, w2d.getAttrValue(WireToDuctline.AttrName.direction));
           boolean layDirection = w2d.getDirection() == 1;
           GenericDO curDuctline = getDuctManagerBO().getObjByCuid(new BoActionContext(),ductlineCuid);
           if(curDuctline==null){
           	return result;
           }
           curDuctline.setAttrValue(PhysicalJoin.AttrName.length, getDoubleByObj(curDuctline.getAttrValue(PhysicalJoin.AttrName.length)));
           if (!(curDuctline instanceof PhysicalJoin)) continue;
           PhysicalJoin pj = (PhysicalJoin)curDuctline;
           String layOrigPointCuid =
                   (direction && layDirection) || (!direction && !layDirection)
                   ? pj.getOrigPointCuid() : pj.getDestPointCuid();
           String layDestPointCuid =
                   (direction && layDirection) || (!direction && !layDirection)
                   ? pj.getDestPointCuid() : pj.getOrigPointCuid();
           if (wireRemainInfo.containsKey(layOrigPointCuid)) {
               interruptDistance -= wireRemainInfo.get(layOrigPointCuid);
               origRemainCuid = layOrigPointCuid;
               origRemainToInterruptLength = interruptDistance;
           }
           if (interruptDistance > pj.getLength()) {
               interruptDistance -= pj.getLength();
           } else {
               ductline = pj;
               ductlineLayDirection = layDirection;
               destRemainToInterruptLength = pj.getLength() - interruptDistance;
               if (wireRemainInfo.containsKey(layDestPointCuid)) {
                   destRemainCuid = layDestPointCuid;
               } 
               else 
               {
                   for (int j = i; 0 <= j && j < wireToDuctlines.size();
                           j = direction ? j + 1 : j - 1) {
                       w2d = (WireToDuctline) wireToDuctlines.get(j);
                       ductlineCuid = w2d.getLineSegCuid();
                       layDirection = w2d.getDirection() == 1;
                       curDuctline = getDuctManagerBO().getObjByCuid(new BoActionContext(),ductlineCuid);
                       if (!(curDuctline instanceof PhysicalJoin)) {
                           continue;
                       }
                       if(curDuctline==null){
                       	return result;
                       }
                       curDuctline.setAttrValue(PhysicalJoin.AttrName.length, getDoubleByObj(curDuctline.getAttrValue(PhysicalJoin.AttrName.length)));
                       pj = (PhysicalJoin) curDuctline;
                       layDestPointCuid =
                               (direction && layDirection) || (!direction && !layDirection)
                               ? pj.getDestPointCuid() : pj.getOrigPointCuid();
                       if (wireRemainInfo.containsKey(layDestPointCuid)) {
                           destRemainCuid = layDestPointCuid;
                           destRemainToInterruptLength += pj.getLength();
                       }
                   }
               }
               break;
           }
           if (i == (direction ? wireToDuctlines.size() - 1 : 0)) {
               errInfo.add("查询长度大于光缆段长度");
               break;
           }
       }
       if (!errInfo.isEmpty()) {
           result.setAttrValue("ERROR_INFO", errInfo);
           return result;
       }
       WireSeg wireSeg = (WireSeg) getDuctManagerBO().getObjByCuid(new BoActionContext(),wireSegCuid);
       wireSeg.setAttrValue(WireSeg.AttrName.length, getDoubleByObj(wireSeg.getAttrValue(WireSeg.AttrName.length)));
       result.setRelatedWireSystemCuid(wireSeg.getRelatedSystemCuid());
       result.setRelatedWireSegCuid(wireSegCuid);
       //取承载的2端点设施
       String origPointCuid = direction ? wireSeg.getOrigPointCuid() : wireSeg.getDestPointCuid();
       String destPointCuid = direction ? wireSeg.getDestPointCuid() : wireSeg.getOrigPointCuid();
       result.setAttrValue(InterruptPoint.AttrName.origPointCuid, getDuctManagerBO().getObjByCuid(new BoActionContext(),origPointCuid).getAttrValue(GenericDO.AttrName.labelCn));
       //距离A承载点距离
       result.setAttrValue("ORIG_POINT_POSITION", interruptDistance);
       result.setAttrValue(InterruptPoint.AttrName.destPointCuid,getDuctManagerBO().getObjByCuid(new BoActionContext(),destPointCuid).getAttrValue(GenericDO.AttrName.labelCn));
       //距离Z承载点距离
       result.setAttrValue("DEST_POINT_POSITION", ductline.getLength() - interruptDistance);
       result.setAttrValue(InterruptPoint.AttrName.laySystemCuid, getDuctManagerBO().getObjByCuid(new BoActionContext(),ductline.getRelatedSystemCuid()).getAttrValue(GenericDO.AttrName.labelCn));
       result.setAttrValue(InterruptPoint.AttrName.laySystemSegCuid, getDuctManagerBO().getObjByCuid(new BoActionContext(),ductline.getCuid()).getAttrValue(GenericDO.AttrName.labelCn));
       result.setAttrValue(InterruptPoint.AttrName.layorigPointCuid, getDuctManagerBO().getObjByCuid(new BoActionContext(),ductline.getOrigPointCuid()).getAttrValue(GenericDO.AttrName.labelCn));
       result.setAttrValue(InterruptPoint.AttrName.laydestPointCuid, getDuctManagerBO().getObjByCuid(new BoActionContext(),ductline.getDestPointCuid()).getAttrValue(GenericDO.AttrName.labelCn));
       InterruptPoint positionInfo = getInterruptPointInDuctline(ductline, interruptDistance,(direction && ductlineLayDirection) || (!direction && !ductlineLayDirection));
       result.setLongitude(positionInfo.getLongitude());
       result.setLatitude(positionInfo.getLatitude());
       if (origRemainCuid != null) {
           result.setAttrValue("ROTE_ORIG_POINT_NAME", getDuctManagerBO().getObjByCuid(new BoActionContext(),origRemainCuid).getAttrValue(GenericDO.AttrName.labelCn));
           result.setAttrValue("ROTE_ORIG_POINT_LONG", wireRemainInfo.get(origRemainCuid));
           result.setAttrValue("ORIG_POINT_LONGTH", origRemainToInterruptLength);
       }
       if (destRemainCuid != null) {
           result.setAttrValue("ROTE_DEST_POINT_NAME", getDuctManagerBO().getObjByCuid(new BoActionContext(),destRemainCuid).getAttrValue(GenericDO.AttrName.labelCn));
           result.setAttrValue("ROTE_DEST_POINT_LONG", wireRemainInfo.get(destRemainCuid));
           result.setAttrValue("DEST_POINT_LONGTH", destRemainToInterruptLength);
       }
       return result;
   }
   
   private InterruptPoint getInterruptPointInDuctline(PhysicalJoin lineSeg, double interruptDistance, boolean direction) {
       String origLayPointCuid = lineSeg.getOrigPointCuid();
       String destLayPointCuid = lineSeg.getDestPointCuid();
       GenericDO origLayPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(),origLayPointCuid);
       GenericDO destLayPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(),destLayPointCuid);
       
       origLayPoint.setAttrValue(DrawPoint.AttrName.longitude, getDoubleByObj(origLayPoint.getAttrValue(DrawPoint.AttrName.longitude)));
       origLayPoint.setAttrValue(DrawPoint.AttrName.latitude, getDoubleByObj(origLayPoint.getAttrValue(DrawPoint.AttrName.latitude)));
       destLayPoint.setAttrValue(DrawPoint.AttrName.longitude, getDoubleByObj(destLayPoint.getAttrValue(DrawPoint.AttrName.longitude)));
       destLayPoint.setAttrValue(DrawPoint.AttrName.latitude, getDoubleByObj(destLayPoint.getAttrValue(DrawPoint.AttrName.latitude)));

       double length = lineSeg.getLength();
       double origX = origLayPoint.getAttrDouble(DrawPoint.AttrName.longitude, 0.0d);
       double origY = origLayPoint.getAttrDouble(DrawPoint.AttrName.latitude, 0.0d);
       double destX = destLayPoint.getAttrDouble(DrawPoint.AttrName.longitude, 0.0d);
       double destY = destLayPoint.getAttrDouble(DrawPoint.AttrName.latitude, 0.0d);
       if (interruptDistance < 0)  {
           interruptDistance = 0;
           LogHome.getLog().error("查询长度不能小于0！");
       } else if (length < interruptDistance) {
           interruptDistance = length;
           LogHome.getLog().error("查询长度大于管线实际长度！");
       }
       double ratio = interruptDistance / length;
       if (!direction) {
           ratio = (lineSeg.getLength() - interruptDistance) / length;
       }
       double longitude = origX + (destX - origX) * ratio;
       double latitude = origY + (destY - origY) * ratio;
       InterruptPoint interruptPoint = new InterruptPoint();
       interruptPoint.setLongitude(longitude);
       interruptPoint.setLatitude(latitude);
       return interruptPoint;
   }
   
   private DataObjectList getWireToDuctlineInRoute(String wireSegCuid) {
   	DataObjectList wireToDuctlines=null;
       String sql = "SELECT " +WireToDuctline.AttrName.cuid+","+ WireToDuctline.AttrName.lineSegCuid + ", " +
               WireToDuctline.AttrName.direction + ", " + WireToDuctline.AttrName.indexInRoute +
               " FROM " + WireToDuctline.CLASS_NAME +
               " WHERE " + WireToDuctline.AttrName.wireSegCuid + " = '" + wireSegCuid + "'"/* +
               " ORDER BY " + WireToDuctline.AttrName.indexInRoute*/;
       try {
       	wireToDuctlines = getDuctManagerBO().getDMObjsBySql(new BoQueryContext(), new WireToDuctline(), sql);
           wireToDuctlines.sort(WireToDuctline.AttrName.indexInRoute, true);
       } catch(Exception ex) {
           LogHome.getLog().error("根据光缆段CUID获取光缆段下的敷设段信息出错！", ex);
       }
       return wireToDuctlines;
   }
   
   // 预留所在点CUID及该点的预留长度
   private Map<String, Double> getWireRemainInfo(String wireSegCuid) throws Exception {
       Map<String, Double> wireRemainInfo = new HashMap<String, Double>();
       //CHANGED_DM finish 预留信息应该在DM
       DataObjectList wireRemains = getWireRemainsByWireSegCuid(wireSegCuid);
       if (wireRemains != null && !wireRemains.isEmpty()) {
           for (GenericDO dto: wireRemains) {
               if (dto instanceof WireRemain) {
                   WireRemain wireRemain = (WireRemain)dto;
                   String locationCuid = wireRemain.getRelatedLocationCuid();
                   double remainLength = getDoubleByObj(WireRemain.AttrName.remainLength);
                   if (locationCuid != null && remainLength > 0) {
                       wireRemainInfo.put(locationCuid, remainLength);
                   }
               }
           }
       }
       return wireRemainInfo;
   }
   
   /**
    *根据光缆段的CUID获取该段下的所有光缆预留
    * @param actionContext BoActionContext
    * @param wireSegCUID String
    * @return DataObjectList
    * @throws Exception 
    */
   public DataObjectList getWireRemainsByWireSegCuid(String wireSegCuid) throws Exception {
       String sql = "RELATED_WIRE_SEG_CUID = '" + wireSegCuid + "'";
       return getDuctManagerBO().getObjectsBySql(sql,new WireRemain());
   }
   
   public static Double getDoubleByObj(Object obj){
		Double rtnRes=0.0;
		if(obj instanceof BigDecimal){
			rtnRes=((BigDecimal)obj).doubleValue();
		}else if(obj instanceof Double){
			rtnRes=(Double) obj;
		}
		return rtnRes;
	}
}
