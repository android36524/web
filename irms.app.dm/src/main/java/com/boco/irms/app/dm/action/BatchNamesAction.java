package com.boco.irms.app.dm.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FiberCabBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberDpBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberJointBoxBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class BatchNamesAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	static Map<String, String> mapBO = new HashMap<String, String>();
	static {
		mapBO.put("MANHLE", "IManhleBO.modifyManhles");
		mapBO.put("POLE", "IPoleBO.modifyPoles");
		mapBO.put("STONE", "IStoneBO.modifyStones");
		mapBO.put("INFLEXION", "IInflexionBO.modifyInflexions");
		mapBO.put("FIBER_CAB", "IFiberCabBO.modifyFiberCabs");
		mapBO.put("FIBER_DP", "IFiberDpBO.modifyFiberDps");
		mapBO.put("FIBER_JOINT_BOX", "IFiberJointBoxBO.modifyFiberJointBoxs");
		mapBO.put(Fcabport.CLASS_NAME, "IFcabportBO.modifyFcabports");
		mapBO.put(FiberDpPort.CLASS_NAME, "IFiberDpPortBO.modifyFiberDpPorts");
		mapBO.put(FiberJointPoint.CLASS_NAME,
				"IFiberJointPointBO.modifyFiberJointPoints");

	}

	private IDuctManagerBO getDuctManagerBO() {
        return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
    }
	
	public void doBatchNames(Map[] list, Map name) throws UserException {
		DataObjectList lists = new DataObjectList();
		String classname = new String();
		// 拼接修改后的名称
		String aftername[] = new String[list.length];
		int STARTNUMBER = Integer.parseInt((String) name.get("STARTNUMBER"));
		int number = name.get("DIGIT").toString().length()
				- name.get("STARTNUMBER").toString().length();
		String numstr = "";
		for (int i = 0; i < number; i++) {
			numstr += "0";
		}
		for (int i = 0; i < list.length; i++) {
			aftername[i] = (String) name.get("PREFIX") + numstr + STARTNUMBER
					+ (String) name.get("POSTFIX");			
			if((STARTNUMBER+1+"").length()>(STARTNUMBER+"").length()){
				numstr = numstr.substring(1);
			}
			STARTNUMBER++;
			GenericDO dbo = new GenericDO();
			int num = list[i].get("CUID").toString().indexOf("-");
			classname = list[i].get("CUID").toString().substring(0, num);
			dbo.setClassName(classname);
			dbo = dbo.createInstanceByClassName();
			dbo.setObjectNum(Long.parseLong((String) list[i].get("OBJECTID")));
			dbo.setAttrValue("CUID", list[i].get("CUID"));
			dbo.setAttrValue("LABEL_CN", aftername[i]);
			if(classname != null && !classname.equals("") && classname.equals(FiberJointBox.CLASS_NAME)){
				String cuid = list[i].get("CUID").toString();
				GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
				if(gdo != null){
					dbo.setAttrValue(FiberJointBox.AttrName.kind, gdo.getAttrValue(FiberJointBox.AttrName.kind));
				}
			}
			
			lists.add(dbo);
		}
		try {
			BoCmdFactory.getInstance().execBoCmd(mapBO.get(classname),
					new BoActionContext(), lists);
		} catch (Exception e) {
			logger.error("修改失败", e);
			throw new UserException(e);
		}

	}

	/**
	 * 接入点上增加终端盒
	 * 
	 * @param points
	 * @param name
	 * @throws UserException
	 */
	public boolean accessPontAddFiberJointBox(List<Map> points, Map addMap)
			throws UserException {
		boolean isSuccess = false;
		DataObjectList list = new DataObjectList();
		String classname = FiberJointBox.CLASS_NAME;
		Map point = points.get(0);
		String accesspointName = String.valueOf(point.get("LABEL_CN"));
		String city = String.valueOf(point.get("CITY_LABEL"));
		String relatedDistrictCuid = null;
		if(StringUtils.isNotBlank(city))
		{
			try {
				DataObjectList dl = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IDistrictBO.getDistrictBySql",new BoActionContext(), " LABEL_CN = '"+city+"'");
				if(dl.size() > 0)
				{
					GenericDO gdo = dl.get(0);
					relatedDistrictCuid = gdo.getCuid();
				}
			} catch (Exception e) {
			}
		}
		int STARTNUMBER = Integer.parseInt((String) addMap.get("STARTNUMBER"));
		int number = addMap.get("DIGIT").toString().length()
				- addMap.get("STARTNUMBER").toString().length();

		String numstr = "";
		for (int i = 0; i < number; i++) {
			numstr += "0";
		}
		int count = Integer.valueOf(addMap.get("numCount").toString());
		// 拼接修改后的名称
		for (int i = 0; i < count; i++) {
			String name = accesspointName + (String) addMap.get("PREFIX") + numstr + STARTNUMBER
					+ (String) addMap.get("POSTFIX");
			STARTNUMBER++;
			GenericDO dbo = new GenericDO();

			dbo.setClassName(classname);
			dbo = dbo.createInstanceByClassName();
			dbo.setCuid();
			dbo.setAttrValue(FiberJointBox.AttrName.longitude,
					point.get("LONGITUDE"));
			dbo.setAttrValue(FiberJointBox.AttrName.latitude,
					point.get("LATITUDE"));
			dbo.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid,
					point.get("CUID"));
			dbo.setAttrValue("LABEL_CN", name);
			dbo.setAttrValue("KIND", "2");
			if(StringUtils.isNotBlank(relatedDistrictCuid))
			{
				dbo.setAttrValue(FiberJointBox.AttrName.relatedDistrictCuid, relatedDistrictCuid);
			}
			list.add(dbo);
		}
		try {
			BoCmdFactory.getInstance().execBoCmd(
					FiberJointBoxBOHelper.ActionName.addFiberJointBoxs,
					new BoActionContext(), list);
			isSuccess = true;
		} catch (Exception e) {
			logger.error(accesspointName + " 增加接头盒失败！", e);
			throw new UserException(e);
		}
		return isSuccess;
	}
	/*
	 * 接入点内增加光交接箱  liuyumiao
	 * 
	 */
	public boolean accessPontAddFiberCab(List<Map> points, Map addMap)
			throws UserException{

		boolean isSuccess = false;
		DataObjectList list = new DataObjectList();
		String classname = FiberCab.CLASS_NAME;
		Map point = points.get(0);
		String accesspointName = String.valueOf(point.get("LABEL_CN"));
		String city = String.valueOf(point.get("CITY_LABEL"));
		String relatedDistrictCuid = null;
		if(StringUtils.isNotBlank(city))
		{
			try {
				DataObjectList dl = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IDistrictBO.getDistrictBySql",new BoActionContext(), " LABEL_CN = '"+city+"'");
				if(dl.size() > 0)
				{
					GenericDO gdo = dl.get(0);
					relatedDistrictCuid = gdo.getCuid();
				}
			} catch (Exception e) {
			}
		}
		int STARTNUMBER = Integer.parseInt((String) addMap.get("STARTNUMBER"));
		int number = addMap.get("DIGIT").toString().length()
				- addMap.get("STARTNUMBER").toString().length();

		String numstr = "";
		for (int i = 0; i < number; i++) {
			numstr += "0";
		}
		int count = Integer.valueOf(addMap.get("numCount").toString());
		// 拼接修改后的名称
		for (int i = 0; i < count; i++) {
			String name = accesspointName + (String) addMap.get("PREFIX") + numstr + STARTNUMBER
					+ (String) addMap.get("POSTFIX");
			STARTNUMBER++;
			GenericDO dbo = new GenericDO();

			dbo.setClassName(classname);
			dbo = dbo.createInstanceByClassName();
			dbo.setCuid();
			dbo.setAttrValue(FiberCab.AttrName.longitude,
					point.get("LONGITUDE"));
			dbo.setAttrValue(FiberCab.AttrName.latitude,
					point.get("LATITUDE"));
			dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid,
					point.get("CUID"));
			dbo.setAttrValue("LABEL_CN", name);
			if(StringUtils.isNotBlank(relatedDistrictCuid))
			{
				dbo.setAttrValue(FiberCab.AttrName.relatedDistrictCuid, relatedDistrictCuid);
			}
			list.add(dbo);
		}
		try {
			BoCmdFactory.getInstance().execBoCmd(
					FiberCabBOHelper.ActionName.addFiberCabs,
					new BoActionContext(), list);
			isSuccess = true;
		} catch (Exception e) {
			logger.error(accesspointName + " 增加交接箱失败！", e);
			throw new UserException(e);
		}
		return isSuccess;
	}
	/*
	 * 接入点内增加光分纤箱  liuyumiao
	 * 
	 */
	public boolean accessPontAddFiberDp(List<Map> points, Map addMap)
			throws UserException{
		boolean isSuccess = false;
		DataObjectList list = new DataObjectList();
		String classname = FiberDp.CLASS_NAME;
		Map point = points.get(0);
		String accesspointName = String.valueOf(point.get("LABEL_CN"));
		String city = String.valueOf(point.get("CITY_LABEL"));
		String relatedDistrictCuid = null;
		if(StringUtils.isNotBlank(city))
		{
			try {
				DataObjectList dl = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IDistrictBO.getDistrictBySql",new BoActionContext(), " LABEL_CN = '"+city+"'");
				if(dl.size() > 0)
				{
					GenericDO gdo = dl.get(0);
					relatedDistrictCuid = gdo.getCuid();
				}
			} catch (Exception e) {
			}
		}
		int STARTNUMBER = Integer.parseInt((String) addMap.get("STARTNUMBER"));
		int number = addMap.get("DIGIT").toString().length()
				- addMap.get("STARTNUMBER").toString().length();

		String numstr = "";
		for (int i = 0; i < number; i++) {
			numstr += "0";
		}
		int count = Integer.valueOf(addMap.get("numCount").toString());
		// 拼接修改后的名称
		for (int i = 0; i < count; i++) {
			String name = accesspointName + (String) addMap.get("PREFIX") + numstr + STARTNUMBER
					+ (String) addMap.get("POSTFIX");
			STARTNUMBER++;
			GenericDO dbo = new GenericDO();

			dbo.setClassName(classname);
			dbo = dbo.createInstanceByClassName();
			dbo.setCuid();
			dbo.setAttrValue(FiberDp.AttrName.longitude,
					point.get("LONGITUDE"));
			dbo.setAttrValue(FiberDp.AttrName.latitude,
					point.get("LATITUDE"));
			dbo.setAttrValue(FiberDp.AttrName.relatedSiteCuid,
					point.get("CUID"));
			dbo.setAttrValue("LABEL_CN", name);
			if(StringUtils.isNotBlank(relatedDistrictCuid))
			{
				dbo.setAttrValue(FiberDp.AttrName.relatedDistrictCuid, relatedDistrictCuid);
			}
			list.add(dbo);
		}
		try {
			BoCmdFactory.getInstance().execBoCmd(
					FiberDpBOHelper.ActionName.addFiberDps,
					new BoActionContext(), list);
			isSuccess = true;
		} catch (Exception e) {
			logger.error(accesspointName + " 增加光分纤箱失败！", e);
			throw new UserException(e);
		}
		return isSuccess;
	}
	
}
