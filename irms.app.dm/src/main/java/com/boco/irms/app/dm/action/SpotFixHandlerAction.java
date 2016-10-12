package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberJointPointBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;

/**
 * ------------------------------------------------------------*
 *          COPYRIGHT (C) 2006 BOCO Inter-Telecom INC          *
 *   CONFIDENTIAL AND PROPRIETARY. ALL RIGHTS RESERVED.        *
 *                                                             *
 *  This work contains confidential business information       *
 *  and intellectual property of BOCO  Inc, Beijing, CN.       *
 *  All rights reserved.                                       *
 * ------------------------------------------------------------*
 *
 * SpotFixHandlerAction
 *-------------------------------------------------------------/
 /**
  *Revision Information:
  *
  *@version 1.0 2015年5月5日 release(lizongyu)
  *2015年5月5日
  */
public class SpotFixHandlerAction {
    
    /** @Description  :根据光缆段查纤芯
     * @Author       :lizongyu
     * @CreateDate   :2015年5月5日
     * @param segs
     * @return
     * @throws Exception
    */
	List<String> pointsCannotUse = new ArrayList<String>();
    //FIBER_JOINT_BOX[objectId=89209741414404, createTime=2011-11-09 10:27:49, , lastModifyTime=2011-11-09 10:27:49, , SEQNO<String>=, CUID<String>=FIBER_JOINT_BOX-8ad7fda3336ca6420133862719540ca2, LABEL_DEV<String>=, CAPACITY<String>=, PRESERVER_ADDR<String>=, REMARK<String>=, CREATOR<String>=gxzhangsonglin, VP_LABEL_CN<String>=, CONNECT_TYPE<Long>=1, MAINT_DEP<String>=, RELATED_PROJECT_CUID<String>=, OWNERSHIP<Long>=1, LATITUDE<Double>=34.498, RELATED_TEMPLATE_NAME<String>=, RELATED_VENDOR_CUID<String>=, RES_OWNER<String>=, BOSS_CODE<String>=, RELATED_MAINT_CUID<String>=, OBJECT_TYPE_CODE<Long>=4004, LABEL_CN<String>=洛阳市宜阳古村基站引入杆路066号杆-接头盒1, IS_USAGE_STATE<Boolean>=false, PRESERVER<String>=, KIND<Long>=1, RELATED_ROOM_CUID<String>=, SPECIAL_LABEL<String>=, REAL_LONGITUDE<Double>=111.80383, EQUIPMENTCODE<String>=, PRESERVER_PHONE<String>=, PRO_NAME<String>=, IS_YJR<Long>=0, MAINT_MODE<Long>=1, SYMBOL_NAME<String>=, RELATED_LOCATION_CUID<Pole>=POLE[, CUID<String>=POLE-402881e82bffc03c012c00da85840a53, POLE_KIND<Long>=1, PULL_LINE_TYPE<Long>=1, HIGH<Long>=1, MAINT_MODE<Long>=1, IS_KEEP_POINT<Boolean>=false, POLE_TYPE<Long>=1, OWNERSHIP<Long>=1, RING_DIA<Long>=1, PLYWOOD_TYPE<Long>=1, IS_CONN_POINT<Boolean>=false, PURPOSE<Long>=1, IS_DANGER_POINT<Boolean>=false, OBJECT_TYPE_CODE<Long>=15002, LABEL_CN<String>=洛阳市宜阳古村基站引入杆路066号杆], LOCATION<String>=, MODEL<String>=, JUNCTION_TYPE<Long>=1, BUSHING<String>=, USERNAME<String>=, LONGITUDE<Double>=111.80383, RELATED_DISTRICT_CUID<DummyDO>=洛阳, RELATED_SITE_CUID<String>=, REAL_LATITUDE<Double>=34.498]
    public Map getFibersBySegs(DataObjectList segs) throws Exception {
        IFiberBO fiberBO = (IFiberBO) BoHomeFactory.getInstance().getBO(IFiberBO.class);
        Map fibers = (Map) fiberBO.getFibersByWireSegs(new BoActionContext(), segs);
        return fibers;
    }
    
    /** @Description  :根据接头盒得到焊点
     * @Author       :lizongyu
     * @CreateDate   :2015年5月5日
     * @param gdo
     * @return
     * @throws Exception
    */
    public DataObjectList getFiberJointPointsByJointBox(String cuid) throws Exception {
        //List<Map<String,String>> lst = new ArrayList<Map<String,String>>();
        GenericDO dbo = new GenericDO();
        dbo.setCuid(cuid);
        DataObjectList result =  (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                FiberJointPointBOHelper.ActionName.getFiberJointPointByJoinBoxCuid, new BoActionContext(), cuid);
        /*if(result != null && result.size()>0) {
            for(GenericDO dto : result) {
                Map<String,String> map = SetDboMap(dto);
                lst.add(map);
            }
        }*/
       return result;
    }
    
    /** @Description  :根据起止点得到光缆段
     * @Author       :lizongyu
     * @CreateDate   :2015年5月5日
     * @param gdo
     * @return
     * @throws Exception
    */
    public List<Map<String,String>> getWireSegsByPoint(String cuid) throws Exception {
        String sql = " " + WireSeg.AttrName.origPointCuid + "='" + cuid + "' or "
                + WireSeg.AttrName.destPointCuid + "='" + cuid + "'";
        DataObjectList result = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegsBySql,
                new BoActionContext(), sql);
        List<Map<String,String>> lst = new ArrayList<Map<String,String>>();
        if(result != null && result.size()>0) {
            for(GenericDO dbo : result) {
                Map<String,String> map = SetDboMap(dbo);
                lst.add(map);
            }
        }
        return lst;
    }
    
    private Map<String,String> SetDboMap(GenericDO dbo){
        Map<String,String> map = new HashMap<String,String>();
        String cuid = dbo.getCuid();
        String labelCn = dbo.getAttrString(Fiber.AttrName.labelCn);
        map.put("CUID", cuid);
        map.put("LABEL_CN", labelCn);
        return map;
    }
    //批量接续
    public boolean batchConn(String jointBoxCuid, String aSegCuid, String bSegCuid,
            String connNumStr, String aStartStr, String bStartStr, int index) throws Exception {

        DataObjectList points = getFiberJointPointsByJointBox(jointBoxCuid);
        boolean discriminateConn = true;
        try {
            discriminateConn = discriminateConn(jointBoxCuid, aSegCuid, bSegCuid, points, connNumStr, aStartStr,
                    bStartStr, index);
        }
        catch (Exception e) {
            discriminateConn = false;
            throw new UserException(e.getMessage());
        }
        return discriminateConn;
    }
    //分歧接续
    public boolean discriminateConn(String jointBoxCuid, String aSegCuid, String bSegCuid,
            String connNumStr, String aStartStr, String bStartStr) throws Exception {

        DataObjectList points = getFiberJointPointsByJointBox(jointBoxCuid);
        boolean discriminateConn = true;
        try {
            discriminateConn = discriminateConn(jointBoxCuid, aSegCuid, bSegCuid, points, connNumStr, aStartStr,
                    bStartStr, -1);
        }
        catch (Exception e) {
            discriminateConn = false;
            throw new UserException(e.getMessage());
        }
        return discriminateConn;
    }

    //分歧接续
    public boolean discriminateConn(String jointBoxCuid, String aSegCuid, String bSegCuid, DataObjectList points,
            String connNumStr, String aStartStr, String bStartStr, int index) throws Exception {
        //pointsCannotUse改成私有
//        List<String> pointsCannotUse = new ArrayList<String>();
        //throw new UserException("dfsfds");
        //纤芯改为查询
        WireSeg awsseg = new WireSeg();
        awsseg.setCuid(aSegCuid);

        WireSeg zwsseg = new WireSeg();
        zwsseg.setCuid(bSegCuid);

        DataObjectList segs = new DataObjectList();
        segs.add(awsseg);
        segs.add(zwsseg);
        Map fibers = getFibersBySegs(segs);
        
        List afibers = (List) fibers.get(aSegCuid);
        List bfibers = (List) fibers.get(bSegCuid);
        List afs = new ArrayList();
        List bfs = new ArrayList();
        
        afs.addAll(afibers);
        bfs.addAll(bfibers);
        if (noFiber(afs)) {
            throw new UserException(index != -1 ? "A" + index + "光缆段下没纤芯" : "A光缆段下没纤芯");
            //MessagePane.showErrorMessage(index != -1 ? "A" + index + "光缆段下没纤芯" : "A光缆段下没纤芯");
            //return false;
        }
        if (noFiber(bfs)) {
            throw new UserException(index != -1 ? "B" + index + "光缆段下没纤芯" : "B光缆段下没纤芯");
            //MessagePane.showErrorMessage(index != -1 ? "B" + index + "光缆段下没纤芯" : "B光缆段下没纤芯");
            //return false;
        }
        int connNum = 0;
        int aStart = 0;
        int bStart = 0;
        try {
            connNum = Integer.parseInt(connNumStr);
            aStart = Integer.parseInt(aStartStr);
            bStart = Integer.parseInt(bStartStr);
        }
        catch (Exception ex) {
            throw new UserException(index != -1 ? "接续芯数，A" + index + "B" + index + "起始芯序都必须填入一个整数" : "接续芯数，AB起始芯序都必须填入一个整数");
            //MessagePane.showErrorMessage(index != -1 ? "接续芯数，A" + index + "B" + index + "起始芯序都必须填入一个整数" : "接续芯数，AB起始芯序都必须填入一个整数");
            //return false;
        }
        if (connNum <= 0 || aStart <= 0 || bStart <= 0) {
            throw new UserException(index != -1 ? "接续芯数,A" + index + "B" + index + "起始芯序都必须大于0" : "接续芯数,AB起始芯序都必须大于0");
            //MessagePane.showErrorMessage(index != -1 ? "接续芯数,A" + index + "B" + index + "起始芯序都必须大于0" : "接续芯数,AB起始芯序都必须大于0");
            //return false;
        }
        if (!points.isEmpty()) {
            try {
                IFiberBO fiberBO = (IFiberBO) BoHomeFactory.getInstance().getBO(IFiberBO.class);
                pointsCannotUse = fiberBO.PointsCannotUse(new BoActionContext(), points, new String[] { aSegCuid,
                        bSegCuid });
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        for (int i = afs.size() - 1; i >= 0; i--) {
            GenericDO af = (GenericDO) afs.get(i);
            if (!canConnect(pointsCannotUse,af, points)) {
                afs.remove(i);
            }
        }
        for (int i = bfs.size() - 1; i >= 0; i--) {
            GenericDO bf = (GenericDO) bfs.get(i);
            if (!canConnect(pointsCannotUse,bf, points)) {
                bfs.remove(i);
            }
        }
        if (afs.size() > 0) {
            for (int i = afs.size() - 1; i >= 0; i--) {
                GenericDO af = (GenericDO) afs.get(i);
                if (af.getAttrLong(Fiber.AttrName.wireNo) < aStart) {
                    afs.remove(i);
                }
            }
        }
        if (bfs.size() > 0) {
            for (int i = bfs.size() - 1; i >= 0; i--) {
                GenericDO bf = (GenericDO) bfs.get(i);
                if (bf.getAttrLong(Fiber.AttrName.wireNo) < bStart) {
                    bfs.remove(i);
                }
            }
        }
        if (afs.size() < connNum && bfs.size() < connNum) {
            throw new UserException(index != -1 ? "A" + index + "B" + index + "光缆段可接续纤芯数都小于接续芯数" : "AB光缆段可接续纤芯数都小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "A" + index + "B" + index + "光缆段可接续纤芯数都小于接续芯数" : "AB光缆段可接续纤芯数都小于接续芯数");
            //return false;
        }
        if (afs.size() < connNum) {
            throw new UserException(index != -1 ? "A" + index + "缆段可接续纤芯数小于接续芯数" : "A光缆段可接续纤芯数小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "A" + index + "缆段可接续纤芯数小于接续芯数" : "A光缆段可接续纤芯数小于接续芯数");
            //return false;
        }
        if (bfs.size() < connNum) {
            throw new UserException(index != -1 ? "B" + index + "缆段可接续纤芯数小于接续芯数" : "B光缆段可接续纤芯数小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "B" + index + "缆段可接续纤芯数小于接续芯数" : "B光缆段可接续纤芯数小于接续芯数");
            //return false;
        }
        //用于自动添加焊点时为焊点命名，避免与已有焊点的名称重复。
        int maxIndex = points.size();
        for (int i = points.size() - 1; i >= 0; i--) {
            String pointCuid = ((GenericDO) points.get(i)).getCuid();
            if (!canPointUse(pointsCannotUse,pointCuid)) {
                points.remove(i);
            }
        }

        //错位接续判断
        findOffsetConn(afs, bfs, points, jointBoxCuid);

        if (afs.size() < connNum && bfs.size() < connNum) {
            throw new UserException(index != -1 ? "A" + index + "B" + index + "光缆段可接续纤芯数都小于接续芯数" : "AB光缆段可接续纤芯数都小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "A" + index + "B" + index + "光缆段可接续纤芯数都小于接续芯数" : "AB光缆段可接续纤芯数都小于接续芯数");
            //return false;
        }
        if (afs.size() < connNum) {
            throw new UserException(index != -1 ? "A" + index + "缆段可接续纤芯数小于接续芯数" : "A光缆段可接续纤芯数小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "A" + index + "缆段可接续纤芯数小于接续芯数" : "A光缆段可接续纤芯数小于接续芯数");
            //return false;
        }
        if (bfs.size() < connNum) {
            throw new UserException(index != -1 ? "B" + index + "缆段可接续纤芯数小于接续芯数" : "B光缆段可接续纤芯数小于接续芯数");
            //MessagePane.showErrorMessage(index != -1 ? "B" + index + "缆段可接续纤芯数小于接续芯数" : "B光缆段可接续纤芯数小于接续芯数");
            //return false;
        }
        //开始接续
        if (afs.size() > 0 && bfs.size() > 0) {
            return doConnect(jointBoxCuid, afs, bfs, connNum, maxIndex, points);
        }
        else {
            throw new UserException("没有需要接续的纤芯，不进行接续");
            //MessagePane.showErrorMessage("没有需要接续的纤芯，不进行接续");
            //return false;
        }
    }
    
    //是否有纤芯
    private boolean noFiber(List fs) {
        return null == fs || fs.size() == 0;
    }
    
    //执行接续
    private boolean doConnect(String jointBoxCuid, List afs, List bfs, int len, int maxIndex, DataObjectList points) {
        //添加新焊点
        if (points.size() < len) {
            DataObjectList newList = new DataObjectList();
            for (int i = 0; i < len - points.size(); i++) {
                maxIndex++;
                FiberJointPoint newPoint = addPoint(maxIndex, jointBoxCuid);
                newList.add(newPoint);
            }
            try {
                newList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                        FiberJointPointBOHelper.ActionName.addFiberJointPoints, new BoActionContext(), newList);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            points.addAll(newList);
        }

        //过滤已连接单边纤芯的焊点
        filterConnectedToFiberPoints(points);

        //开始连接
        DataObjectList nfs = new DataObjectList();
        DataObjectList nps = new DataObjectList();
        int length = afs.size() < bfs.size() ? (afs.size() < len ? afs.size() : len) : (bfs.size() < len ? bfs.size()
                : len);
        for (int i = 0; i < length; i++) {
            GenericDO af = (GenericDO) afs.get(i);
            GenericDO bf = (GenericDO) bfs.get(i);
            connect(nfs, nps, points, af, bf, jointBoxCuid);
        }
        try {
            BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.addFibersConnectToPoint,
                    new BoActionContext(), nfs, nps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    //纤芯可否接续
    private boolean canConnect(List<String> pointsCannotUse,GenericDO gdo, List points) {
        if ((null == gdo.getAttrString(Fiber.AttrName.origPointCuid) || "".equals(gdo.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null == gdo.getAttrString(Fiber.AttrName.destPointCuid) || "".equals(gdo.getAttrString(Fiber.AttrName.destPointCuid)))) {
            return true;
        }
        else if ((null != gdo.getAttrString(Fiber.AttrName.origPointCuid) && !"".equals(gdo.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null == gdo.getAttrString(Fiber.AttrName.destPointCuid) || "".equals(gdo.getAttrString(Fiber.AttrName.destPointCuid)))) {
            String attrString = gdo.getAttrString(Fiber.AttrName.origPointCuid);
            if (isBelongJointBox(attrString, points)) {
                if (canPointUse(pointsCannotUse,attrString)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else if ((null == gdo.getAttrString(Fiber.AttrName.origPointCuid) || "".equals(gdo.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null != gdo.getAttrString(Fiber.AttrName.destPointCuid) && !"".equals(gdo.getAttrString(Fiber.AttrName.destPointCuid)))) {
            String attrString = gdo.getAttrString(Fiber.AttrName.destPointCuid);
            if (isBelongJointBox(gdo.getAttrString(Fiber.AttrName.destPointCuid), points)) {
                if (canPointUse(pointsCannotUse,attrString)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else {
            if (isBelongJointBox(gdo.getAttrString(Fiber.AttrName.origPointCuid), points)) {
                if (canPointUse(pointsCannotUse,gdo.getAttrString(Fiber.AttrName.origPointCuid))) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (isBelongJointBox(gdo.getAttrString(Fiber.AttrName.destPointCuid), points)) {
                if (canPointUse(pointsCannotUse,gdo.getAttrString(Fiber.AttrName.destPointCuid))) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
    }
    
    //焊点是否属于当前接头盒
    private boolean isBelongJointBox(String pointCuid, List points) {
        for (int i = 0; i < points.size(); i++) {
            GenericDO point = (GenericDO) points.get(i);
            if (point.getCuid().equals(pointCuid)) {
                return true;
            }
        }
        return false;
    }
    
    //焊点是否可使
    private boolean canPointUse(List<String> pointsCannotUse,String pointCuid) {
        if (pointsCannotUse.contains(pointCuid)) {
            return false;
        }
        return true;
    }
    
    //准备接续数据
    private final void connect(DataObjectList nfs, DataObjectList nps, List points, GenericDO af, GenericDO bf,
            String jointBoxCuid) {
        GenericDO point = null;
        if (!points.isEmpty()) {
            point = (GenericDO) points.get(0);
        }
        else {
            point = new GenericDO();
        }
        GenericDO naf = getConnectPort(af, point, jointBoxCuid);
        GenericDO nbf = getConnectPort(bf, point, jointBoxCuid);
        if (naf != null && nbf != null) {
            nfs.add(naf);
            nfs.add(nbf);
            point.setAttrValue(FiberJointPoint.AttrName.isConnectedToFiber, true);
            nps.add(point);
            points.remove(0);
        }
        else if (null == naf && nbf != null) {
            nbf = getOtherSide(af, nbf, jointBoxCuid);
            if (nbf != null) {
                nfs.add(nbf);
            }
        }
        else if (naf != null && null == nbf) {
            naf = getOtherSide(bf, naf, jointBoxCuid);
            if (naf != null) {
                nfs.add(naf);
            }
        }
    }
    
    //错位接续判断
    private void findOffsetConn(List afs, List bfs, DataObjectList points, String jointBoxCuid) {
        int len = afs.size() < bfs.size() ? afs.size() : bfs.size();
        for (int i = len - 1; i >= 0; i--) {
            GenericDO af = (GenericDO) afs.get(i);
            GenericDO bf = (GenericDO) bfs.get(i);
            Object aosc = af.getAttrValue(Fiber.AttrName.origSiteCuid);
            Object bosc = bf.getAttrValue(Fiber.AttrName.origSiteCuid);
            Object adsc = af.getAttrValue(Fiber.AttrName.destSiteCuid);
            Object bdsc = bf.getAttrValue(Fiber.AttrName.destSiteCuid);
            Object aopc = af.getAttrValue(Fiber.AttrName.origPointCuid);
            Object bopc = bf.getAttrValue(Fiber.AttrName.origPointCuid);
            Object adpc = af.getAttrValue(Fiber.AttrName.destPointCuid);
            Object bdpc = bf.getAttrValue(Fiber.AttrName.destPointCuid);
            if (jointBoxCuid.equals(aosc) && jointBoxCuid.equals(bosc)) {
                if ((aopc != null && !"".equals(aopc)) && (bopc != null && !"".equals(bopc))) {
                    removeOffsetPoint(points, aopc, bopc);
                    afs.remove(i);
                    bfs.remove(i);
                }
            }
            else if (jointBoxCuid.equals(aosc) && jointBoxCuid.equals(bdsc)) {
                if ((aopc != null && !"".equals(aopc)) && (bdpc != null && !"".equals(bdpc))) {
                    removeOffsetPoint(points, aopc, bdpc);
                    afs.remove(i);
                    bfs.remove(i);
                }
            }
            else if (jointBoxCuid.equals(adsc) && jointBoxCuid.equals(bosc)) {
                if ((adpc != null && !"".equals(adpc)) && (bopc != null && !"".equals(bopc))) {
                    removeOffsetPoint(points, adpc, bopc);
                    afs.remove(i);
                    bfs.remove(i);
                }
            }
            else if (jointBoxCuid.equals(adsc) && jointBoxCuid.equals(bdsc)) {
                if ((adpc != null && !"".equals(adpc)) && (bdpc != null && !"".equals(bdpc))) {
                    removeOffsetPoint(points, adpc, bdpc);
                    afs.remove(i);
                    bfs.remove(i);
                }
            }
        }
    }
    
    //移除错位接续关联的焊点
    private final void removeOffsetPoint(DataObjectList points, Object apc, Object bpc) {
        int removeCount = 0;
        for (int j = points.size() - 1; j >= 0; j--) {
            if (((GenericDO) points.get(j)).getCuid().equals(apc)) {
                points.remove(j);
                removeCount++;
                if (2 == removeCount) {
                    break;
                }
            }
            else if (((GenericDO) points.get(j)).getCuid().equals(bpc)) {
                points.remove(j);
                removeCount++;
                if (2 == removeCount) {
                    break;
                }
            }
        }
    }
    
    //确定接续端子，返回待接续对象
    private GenericDO getConnectPort(GenericDO f, GenericDO point, String jointBoxCuid) {
        String origEqpCuid = f.getAttrString(Fiber.AttrName.origEqpCuid) != null
                && !"".equals(f.getAttrString(Fiber.AttrName.origEqpCuid)) ? f.getAttrString(Fiber.AttrName.origEqpCuid) : f.getAttrString(Fiber.AttrName.origSiteCuid);
        String destEqpCuid = f.getAttrString(Fiber.AttrName.destEqpCuid) != null
                && !"".equals(f.getAttrString(Fiber.AttrName.origEqpCuid)) ? f.getAttrString(Fiber.AttrName.destEqpCuid) : f.getAttrString(Fiber.AttrName.destSiteCuid);
        if ((null == f.getAttrString(Fiber.AttrName.origPointCuid) || "".equals(f.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null == f.getAttrString(Fiber.AttrName.destPointCuid) || "".equals(f.getAttrString(Fiber.AttrName.destPointCuid)))) {
            if (jointBoxCuid.equals(origEqpCuid)) {
                f.setAttrValue(Fiber.AttrName.origPointCuid, point.getCuid());
                return f;
            }
            else {
                f.setAttrValue(Fiber.AttrName.destPointCuid, point.getCuid());
                return f;
            }
        }
        else if ((null != f.getAttrString(Fiber.AttrName.origPointCuid) && !"".equals(f.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null == f.getAttrString(Fiber.AttrName.destPointCuid) || "".equals(f.getAttrString(Fiber.AttrName.destPointCuid)))) {
            if (jointBoxCuid.equals(origEqpCuid)) {
                return null;
            }
            else {
                f.setAttrValue(Fiber.AttrName.destPointCuid, point.getCuid());
                return f;
            }
        }
        else if ((null == f.getAttrString(Fiber.AttrName.origPointCuid) || "".equals(f.getAttrString(Fiber.AttrName.origPointCuid)))
                && (null != f.getAttrString(Fiber.AttrName.destPointCuid) && !"".equals(f.getAttrString(Fiber.AttrName.destPointCuid)))) {
            if (jointBoxCuid.equals(destEqpCuid)) {
                return null;
            }
            else {
                f.setAttrValue(Fiber.AttrName.origPointCuid, point.getCuid());
                return f;
            }
        }
        else {
            return null;
        }
    }
    
    //在一条纤芯成端的情况下，确定另一条纤芯的接续端子
    private final GenericDO getOtherSide(GenericDO fiber, GenericDO otherFiber, String jointBoxCuid) {
        Object fiberOrigEqpCuid = fiber.getAttrValue(Fiber.AttrName.origEqpCuid) != null && !"".equals(fiber.getAttrValue(Fiber.AttrName.origEqpCuid)) ? 
                fiber.getAttrValue(Fiber.AttrName.origEqpCuid) : fiber.getAttrValue(Fiber.AttrName.origSiteCuid);
        Object fiberDestEqpCuid = fiber.getAttrValue(Fiber.AttrName.destEqpCuid) != null && !"".equals(fiber.getAttrValue(Fiber.AttrName.destEqpCuid)) ? 
                fiber.getAttrValue(Fiber.AttrName.destEqpCuid) : fiber.getAttrValue(Fiber.AttrName.destSiteCuid);
        Object otherFiberOrigEqpCuid = otherFiber.getAttrValue(Fiber.AttrName.origEqpCuid) != null && !"".equals(otherFiber.getAttrValue(Fiber.AttrName.origEqpCuid)) ? 
                otherFiber.getAttrValue(Fiber.AttrName.origEqpCuid) : otherFiber.getAttrValue(Fiber.AttrName.origSiteCuid);
        Object otherFiberDestEqpCuid = otherFiber.getAttrValue(Fiber.AttrName.destEqpCuid) != null && !"".equals(otherFiber.getAttrValue(Fiber.AttrName.destEqpCuid)) ? 
                otherFiber.getAttrValue(Fiber.AttrName.destEqpCuid) : otherFiber.getAttrValue(Fiber.AttrName.destSiteCuid);
        if(jointBoxCuid.equals(fiberOrigEqpCuid) && jointBoxCuid.equals(otherFiberOrigEqpCuid)) {
                otherFiber.setAttrValue(Fiber.AttrName.origPointCuid, fiber.getAttrValue(Fiber.AttrName.origPointCuid));
                return otherFiber;
        } else if(jointBoxCuid.equals(fiberOrigEqpCuid) && jointBoxCuid.equals(otherFiberDestEqpCuid)) {
                otherFiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber.getAttrValue(Fiber.AttrName.origPointCuid));
                return otherFiber;
        } else if(jointBoxCuid.equals(fiberDestEqpCuid) && jointBoxCuid.equals(otherFiberOrigEqpCuid)) {
                otherFiber.setAttrValue(Fiber.AttrName.origPointCuid, fiber.getAttrValue(Fiber.AttrName.destPointCuid));
                return otherFiber;
        } else if(jointBoxCuid.equals(fiberDestEqpCuid) && jointBoxCuid.equals(otherFiberDestEqpCuid)) {
                otherFiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber.getAttrValue(Fiber.AttrName.destPointCuid));
                return otherFiber;
        }
        return null;
    }
    
    //添加新焊点
    private FiberJointPoint addPoint(int maxIndex, String jointBoxCuid) {
        FiberJointPoint newPoint = new FiberJointPoint();
        newPoint.setLabelCn("1_" + maxIndex + "_auto");
        newPoint.setAttrValue(FiberJointPoint.AttrName.serviceState, DuctEnum.DMServiceState._empty);
        newPoint.setAttrValue(FiberJointPoint.AttrName.isConnectedToFiber, false);
        newPoint.setAttrValue(FiberJointPoint.AttrName.relatedDeviceCuid, jointBoxCuid);
        return newPoint;
    }
    
    //过滤已经连接了一根A或B光缆段中的纤芯的焊点
    private void filterConnectedToFiberPoints(List points) {
        for (int i = points.size() - 1; i >= 0; i--) {
            GenericDO point = (GenericDO) points.get(i);
            boolean isConnectToFiber = point.getAttrBool(FiberJointPoint.AttrName.isConnectedToFiber);
            if (isConnectToFiber) {
                points.remove(i);
            }
        }
    }
   //直通接续
    public boolean directConn(String jointBoxCuid, String aSegCuid, String bSegCuid) throws Exception{
    	
    	DataObjectList points = getFiberJointPointsByJointBox(jointBoxCuid);
    	boolean directConn = true;
    	try{
    		doOneDirectConnect(jointBoxCuid, aSegCuid, bSegCuid, points);   		
    	}
    	catch (Exception e){
    		directConn = false;
    		throw new UserException(e.getMessage());
    	}
    	return directConn;
    }

	private boolean doOneDirectConnect(String jointBoxCuid, String aSegCuid,
			String bSegCuid, DataObjectList points) {
		
		List<String> pointsCannotUse = new ArrayList<String>();
		WireSeg awsseg = new WireSeg();
		awsseg.setCuid(aSegCuid);
		
		WireSeg zwsseg = new WireSeg();
		zwsseg.setCuid(bSegCuid);
		
		DataObjectList segs = new DataObjectList();
		segs.add(awsseg);
		segs.add(zwsseg);
		Map fibers=null;
		try {
			fibers = getFibersBySegs(segs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List afibers = (List)fibers.get(aSegCuid);
		List bfibers = (List)fibers.get(bSegCuid);
		List afs = new ArrayList();
		List bfs = new ArrayList();
		afs.addAll(afibers);
		bfs.addAll(bfibers);
		if(noFiber(afs)){
			throw new UserException("A光缆段下没纤芯");
		}
		if(noFiber(bfs)){
			throw new UserException("B光缆段下没纤芯");
		}
		if(!isTotalEquals(afs,bfs)){
			throw new UserException("AB光缆段的纤芯总数不相等，无法进行直通接续");
		}
		try{
			pointsCannotUse = (List<String>) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.PointsCannotUse, new BoActionContext(), points, new String[]{aSegCuid, bSegCuid});
		}catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
		for (int i=0; i<afs.size(); i++){
			GenericDO af = (GenericDO)afs.get(i);
			GenericDO bf = (GenericDO)bfs.get(i);
			if(!isSame(af,bf,points)){
				throw new UserException("AB对等位置的纤芯可否接续状态不一致");				
			}
		}
		for(int i=afs.size()-1; i>=0; i--) {
			GenericDO af = (GenericDO)afs.get(i);
			if(!canConnect(pointsCannotUse,af, points)) {
				afs.remove(i);
				bfs.remove(i);
			}
		}
		int maxIndex = points.size();//用于自动添加焊点时为焊点命名，避免与已有焊点的名称重复。
		for(int i=points.size()-1; i>=0; i--) {
			String pointCuid = ((GenericDO)points.get(i)).getCuid();
			if(!canPointUse(pointsCannotUse,pointCuid)) {
				points.remove(i);
			}
		}
		//错位接续判断
		findOffsetConn(afs, bfs, points, jointBoxCuid);
		//开始接续
		if(afs.size() > 0) {
			return doConnect(jointBoxCuid, afs, bfs, afs.size(), maxIndex, points);
		} else {
			throw new UserException("没有需要接续的纤芯，不进行接续");
		}
}
    
  //被接续光缆段的纤芯总数是否相等
    private boolean isTotalEquals(List afs, List bfs) {

    	return afs.size() == bfs.size();
    }
  //AB纤芯可否接续是否一致
    private boolean isSame(GenericDO af, GenericDO bf, List points) {
    
		return canConnect(pointsCannotUse,af, points) && canConnect(pointsCannotUse,bf, points) ? true: (!canConnect(pointsCannotUse, af, points) && !canConnect(pointsCannotUse, bf, points));
    }
    
    
}
