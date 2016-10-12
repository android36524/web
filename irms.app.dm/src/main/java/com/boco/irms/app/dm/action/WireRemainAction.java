package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;

public class WireRemainAction{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 根据光缆CUID获取光缆下的光缆段
	 * @param CUID
	 * @return
	 * @throws IOException
	 */
	public List<Map> getWireSegs(String CUID) throws IOException {
		
		String method = "IWireSegBO.getWireSegsByWireSystemCuid";// 调用的接口方法
				
		System.out.println("CUID========================"+CUID);
		// 从传输服务查询数据
		DataObjectList results = new DataObjectList();
		try {
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(), CUID);
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}
		List<Map> list = new ArrayList<Map>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("OBJECTID", gdo.getObjectNum());
				map.put("CUID", gdo.getCuid());
				map.put("LABEL_CN", gdo.getAttrString("LABEL_CN"));
				list.add(map);
			}
		}
		return list;
	}
	
	public List<Map> getRemainInfo(String segCuid){
		WireSeg wireseg = new WireSeg(segCuid);
		wireseg.setCuid(segCuid);
		List<Map> list = new ArrayList<Map>();
        try {
            DataObjectList pointlists = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.getAllPointByWireSegCUID,
                new BoActionContext(), wireseg);
            if(pointlists.size() == 0){
            	return list;
            }
            for (int i = 0; i < pointlists.size(); i++) {
                GenericDO point = pointlists.get(i); 
                if (!(point.getClassName().equals(FiberCab.CLASS_NAME) || point.getClassName().equals(FiberDp.CLASS_NAME) ||
                      point.getClassName().equals(FiberJointBox.CLASS_NAME))) {
                    Boolean blnremain = false; //是否光缆预留
                    String strLength = ""; //光缆预留长度
                    WireRemain wr = (WireRemain) point.getAttrValue("WIRE_REMAIN");
                    String remainInfo = "";
                    if (wr.getCuid() == null || wr.getCuid().trim().equals("")) {
                        blnremain = false;
                        strLength = "";
                    } else {
                        blnremain = true;
                        if (wr.getRemainLength() == 0) {
                            strLength = "";
                        } else {
                            strLength = wr.getRemainLength() + "";
                        }
                        Map map = new HashMap();
                        map.put("OBJECTID", wr.getObjectNum());
                        map.put("CUID", wr.getCuid());
                        map.put("RELATED_WIRE_SEG_CUID", wr.getRelatedWireSegCuid());
                        map.put("RELATED_DISTRICT_CUID", wr.getRelatedDistrictCuid());
                        
                        map.put("LABEL_CN", wr.getLabelCn());
                        map.put(WireRemain.AttrName.relatedLocationCuid, wr.getRelatedLocationCuid());
                        map.put(WireRemain.AttrName.remainLength, wr.getRemainLength());
                        remainInfo = JSONObject.toJSONString(map);
                    }
                    
                    Map remainMap = new HashMap();
                    remainMap.put("OBJECTID",point.getObjectNum());
                    remainMap.put("CUID",point.getCuid());
                    remainMap.put("LABEL_CN",point.getAttrString("LABEL_CN"));
                    remainMap.put("IS_REMAIN", blnremain);
                    remainMap.put("LENGTH", strLength);
                    remainMap.put("RELATED_WIRE_SEG_CUID", segCuid);
                    if (point.getClassName().equals("SITE")) {
                    	remainMap.put("RELATED_DISTRICT_CUID",(String) point.getAttrValue("RELATED_SPACE_CUID"));
                    } else {
                    	remainMap.put("RELATED_DISTRICT_CUID",(String) point.getAttrValue("RELATED_DISTRICT_CUID"));
                    }
                    remainMap.put("OWNERSHIP",point.getAttrValue("OWNERSHIP"));
                    remainMap.put("REMAIN_INFO", remainInfo);
                    list.add(remainMap);
                }
            }
        } catch (Exception ex) {
            LogHome.getLog().error("查询预留信息报错", ex);
        }
		return list;
	}
	
	public void saveWireRemain(List<Map> oldData, List<Map> newData){
        //有三种情况1:设置预留 2:修改预留 3:删除预留
        DataObjectList addlist = new DataObjectList(); //需要增加的列表
        DataObjectList modifylist = new DataObjectList(); //需要修改的列表
        DataObjectList deletelist = new DataObjectList(); //需要删除的列表
        
		Map<String,Map> oldRemainMap = new HashMap<String,Map>();
		if(oldData != null && oldData.size() > 0){
			for(Map remain: oldData){
				oldRemainMap.put(remain.get("CUID").toString(), remain);
				boolean isExist = false;
				for(Map newramin: newData){
					if(newramin.get("CUID").toString().equals(remain.get("CUID").toString())){
						isExist = true;
					}
				}
				if(isExist){
					continue;
				}
				String remaininfo = remain.get("REMAIN_INFO").toString();
				JSONObject json = JSONObject.parseObject(remaininfo);
				Map map = JSONObject.toJavaObject(json, Map.class);
			    WireRemain wr = new WireRemain();
			    wr.setRelatedLocationCuid(String.valueOf(map.get("RELATED_LOCATION_CUID")));
				wr.setLabelCn(String.valueOf(map.get("LABEL_CN")));
				wr.setCuid(String.valueOf(map.get("CUID")));
				wr.setObjectNum(Long.parseLong(String.valueOf(map.get("OBJECTID"))));
				deletelist.add(wr);
			}
		}
		
		if(newData != null && newData.size() > 0){
			for(Map remain: newData){
				Map oldremain = oldRemainMap.get(remain.get("CUID"));
				if(oldremain == null){
					    WireRemain wr = new WireRemain();
					    wr.setRelatedDistrictCuid((String.valueOf(remain.get("RELATED_DISTRICT_CUID"))));
						wr.setRelatedWireSegCuid(String.valueOf(remain.get("RELATED_WIRE_SEG_CUID")));
						wr.setRelatedLocationCuid(String.valueOf(remain.get("CUID")));
						wr.setOwnership(Long.parseLong(String.valueOf(remain.get("OWNERSHIP"))));
						wr.setRemainLength(Double.parseDouble(String.valueOf(remain.get("LENGTH"))));
						wr.setLabelCn(String.valueOf(remain.get("LABEL_CN")));
						wr.setCuid();
						addlist.add(wr);
				}else{
					String remaininfo = oldremain.get("REMAIN_INFO").toString();
					JSONObject json = JSONObject.parseObject(remaininfo);
					Map map = JSONObject.toJavaObject(json, Map.class);
				    WireRemain wr = new WireRemain();
				    wr.setRelatedDistrictCuid((String.valueOf(remain.get("RELATED_DISTRICT_CUID"))));
					wr.setRelatedWireSegCuid(String.valueOf(map.get("RELATED_WIRE_SEG_CUID")));
					wr.setRelatedLocationCuid(String.valueOf(remain.get("CUID")));
					wr.setOwnership(Long.parseLong(String.valueOf(remain.get("OWNERSHIP"))));
					wr.setRemainLength(Double.parseDouble(String.valueOf(remain.get("LENGTH"))));
					wr.setLabelCn(String.valueOf(map.get("LABEL_CN")));
					wr.setCuid(String.valueOf(map.get("CUID")));
					wr.setObjectNum(Long.parseLong(String.valueOf(map.get("OBJECTID"))));
					modifylist.add(wr);
				    System.out.println(map);	
				}
			}
		}
		

        
        try {
			BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.addWireRemainByLists,
			        new BoActionContext(), addlist, modifylist, deletelist);
		} catch (Exception e) {
			logger.error("保存预留信息报错",e);
		}
	}

	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	/**
	 * 点设施查看光缆预留信息
	 * @param pointCuid
	 * @return
	 * @throws Exception
	 */
	public List<Map> getWireRemainByPointCuid(String pointCuid) throws Exception{
		List<Map> lst = new ArrayList<Map>();

        String strLength = ""; //光缆预留长度
        String strName = ""; //光缆名称
        String strStart = ""; //光缆段开始名称
        String strEnd = ""; //光缆段结束名称

        GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), pointCuid);
        if ((gdo.getCuid()) != null) {
            String sql = WireRemain.AttrName.relatedLocationCuid + "='" + gdo.getCuid() + "'"; //获取光缆保留BO 方法
            DataObjectList wireRemains = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.getWireRemainBySql,
                new BoActionContext(), sql);
            if (wireRemains != null) {
                for (int i = 0; i < wireRemains.size(); i++) {
                	Map wireRemainMap = new HashMap();
//                    GenericNode node = new GenericNode(); // bug:10783 modify by libo 2008.9.28
                    WireRemain wr = (WireRemain) wireRemains.get(i);
                    if (wr.getCuid() == null || wr.getCuid().trim().equals("")) {
                        strLength = "";
                    } else {
                        if (wr.getRemainLength() == 0) {
                            strLength = "";
                        } else {
                            strLength = wr.getRemainLength() + "";
                        }
                        if (wr.getRelatedWireSegCuid() != null) {
                            //通过光缆段的cuid 怎样去获得 光缆段 的数据集 ???
                            String segsql = WireSeg.AttrName.cuid + "='" + wr.getRelatedWireSegCuid() + "'"; //获取光缆段BO 方法
                            DataObjectList wireSeg = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegObjectBySql,
                                new BoActionContext(), segsql);
                            if (wireSeg != null) {
                                for (int j = 0; j < wireSeg.size(); j++) {
                                    WireSeg ws = (WireSeg) wireSeg.get(j);
                                    GenericDO wsystem = null;
                                    GenericDO origPoint = null;
                                    GenericDO destPoint = null;
                                    if (ws.getAttrValue(WireSeg.AttrName.relatedSystemCuid) instanceof GenericDO) {
                                        wsystem = (GenericDO) ws.getAttrValue(WireSeg.AttrName.relatedSystemCuid);
                                    }
                                    if (ws.getAttrValue(WireSeg.AttrName.origPointCuid) instanceof GenericDO) {
                                        origPoint = (GenericDO) ws.getAttrValue(WireSeg.AttrName.origPointCuid);
                                    }
                                    if (ws.getAttrValue(WireSeg.AttrName.destPointCuid) instanceof GenericDO) {
                                        destPoint = (GenericDO) ws.getAttrValue(WireSeg.AttrName.destPointCuid);
                                    }
                                    String str = (wsystem != null ? wsystem.getAttrString(GenericDO.AttrName.labelCn) : "");
                                    String str1 = (origPoint != null ? origPoint.getAttrString(GenericDO.AttrName.labelCn) : "");
                                    String str2 = (destPoint != null ? destPoint.getAttrString(GenericDO.AttrName.labelCn) : "");
                                    strName = str; //strName +
                                    strStart = str1; // strStart +
                                    strEnd = str2; //strEnd +
                                }
                            }
                        }
                    }
                    wireRemainMap.put("WIRE_REMAIN_NAME", strName);
                    wireRemainMap.put("WIRE_REMAIN_LENGTH", strLength);
                    wireRemainMap.put("WIRE_START_NAME", strStart);
                    wireRemainMap.put("WIRE_END_NAME", strEnd);
                    lst.add(wireRemainMap);
                }
            }
        }
		return lst;
	}
	
	private IFiberJointBoxBO getFiberJointBoxBO(){
		return BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class);
	}
	
    /**
     * 接入点终端盒列表
     * @return
     */
    public List<Map> getFiberjointbox(String cuid){
    	List<Map> fiberjointBoxList = new ArrayList<Map>();
    	DataObjectList fbbList = getFiberJointBoxBO().getFiberJointBoxsByRelateLocationCuid(new BoActionContext(), cuid);
    	for(GenericDO dbo:fbbList){
    		FiberJointBox fiberjoint=null;
    		if(dbo instanceof FiberJointBox){
    			fiberjoint = (FiberJointBox) dbo;
    			long junctionType = fiberjoint.getJunctionType();
    			String labelCn = fiberjoint.getLabelCn();
    			String relatedDistrictName = "";
    			Object attrValue = fiberjoint.getAttrValue(FiberJointBox.AttrName.relatedDistrictCuid);
    			if(attrValue instanceof GenericDO){
    				relatedDistrictName = ((GenericDO) attrValue).getAttrString(FiberJointBox.AttrName.labelCn);
    			}else if(attrValue instanceof String){
    				GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), String.valueOf(attrValue));
    				if(gdo != null){
    					relatedDistrictName = gdo.getAttrString(FiberJointBox.AttrName.labelCn);
    				}
    			}
    			long kind = fiberjoint.getKind();
    			long connectType = fiberjoint.getConnectType();
    			Map map = new HashMap();
    			map.put("CUID", fiberjoint.getCuid());
    			map.put("LABEL_CN", labelCn);
    			map.put("RELATED_DISTRICT_CUID", relatedDistrictName);
    			map.put("JUNCTION_TYPE", junctionType);
    			map.put("KIND", kind);
    			map.put("CONNECT_TYPE", connectType);
    			fiberjointBoxList.add(map);
    		}
    	}
    	return fiberjointBoxList;
    }
    //终端盒增加删除列表
    public boolean deleteFBoxForAccesspoint(List<String> fiberJointBoxCuids) throws Exception {
        boolean flag = true;
        try {
            DataObjectList fiberJointBoxes = getDuctManagerBO().getGenericDOListByCuids(fiberJointBoxCuids);
            if(fiberJointBoxes != null && fiberJointBoxes.size()>0) {
                getFiberJointBoxBO().deleteRelatedLocationOfFiberJointBox(new BoActionContext(), fiberJointBoxes);
            }
        }
        catch (UserException e) {
            flag = false;
            e.printStackTrace();
        }
       return flag;
    }
    
}
