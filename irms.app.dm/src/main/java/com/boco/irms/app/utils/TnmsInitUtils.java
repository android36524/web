package com.boco.irms.app.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.lang.GenericEnum;
import com.boco.common.util.lang.TimeFormatHelper;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.irms.app.utils.CustomEnum.AccesspointLastName;
import com.boco.irms.app.utils.CustomEnum.ApointDeviceType;
//import com.boco.irms.app.utils.CustomEnum.AnPosLastName;
import com.boco.irms.app.utils.CustomEnum.BooleanType;
import com.boco.irms.app.utils.CustomEnum.BuildType;
import com.boco.irms.app.utils.CustomEnum.CanAllocateToUserType;
import com.boco.irms.app.utils.CustomEnum.CheckDate;
import com.boco.irms.app.utils.CustomEnum.CheckMonth;
import com.boco.irms.app.utils.CustomEnum.CheckType;
import com.boco.irms.app.utils.CustomEnum.ClientType;
import com.boco.irms.app.utils.CustomEnum.DMAccessMode;
import com.boco.irms.app.utils.CustomEnum.DMAccessSence;
import com.boco.irms.app.utils.CustomEnum.DMBuildType;
import com.boco.irms.app.utils.CustomEnum.DMDistrictType;
import com.boco.irms.app.utils.CustomEnum.DMProjectState;
import com.boco.irms.app.utils.CustomEnum.DMSchemeType;
import com.boco.irms.app.utils.CustomEnum.DMUserType;
import com.boco.irms.app.utils.CustomEnum.FiberCabLastName;
import com.boco.irms.app.utils.CustomEnum.FiberColor;
import com.boco.irms.app.utils.CustomEnum.FiberDpLastName;
import com.boco.irms.app.utils.CustomEnum.FiberJointLastName;
import com.boco.irms.app.utils.CustomEnum.InflexLastName;
import com.boco.irms.app.utils.CustomEnum.LifeCycleState;
import com.boco.irms.app.utils.CustomEnum.ManhleFigure;
import com.boco.irms.app.utils.CustomEnum.ManhleLastName;
import com.boco.irms.app.utils.CustomEnum.OnuAccessType;
import com.boco.irms.app.utils.CustomEnum.OnuboxDeviceType;
import com.boco.irms.app.utils.CustomEnum.OnuboxLastName;
import com.boco.irms.app.utils.CustomEnum.OpticalTaskTime;
import com.boco.irms.app.utils.CustomEnum.OpticalTaskType;
import com.boco.irms.app.utils.CustomEnum.OverLayType;
import com.boco.irms.app.utils.CustomEnum.PoleLastName;
import com.boco.irms.app.utils.CustomEnum.PosOwnershipType;
import com.boco.irms.app.utils.CustomEnum.PosRation;
import com.boco.irms.app.utils.CustomEnum.ProjectState;
import com.boco.irms.app.utils.CustomEnum.ResultType;
import com.boco.irms.app.utils.CustomEnum.SerialNumber;
import com.boco.irms.app.utils.CustomEnum.SiteType;
import com.boco.irms.app.utils.CustomEnum.StoneLastName;
import com.boco.irms.app.utils.CustomEnum.TaskCheckType;
import com.boco.irms.app.utils.CustomEnum.ZoneType;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.client.model.base.BoProxyFactory;
import com.boco.transnms.client.model.base.ImportBoManager;
import com.boco.transnms.client.model.base.XrpcUrlManager;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.OpticalCheckTask;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.cm.DeviceVendorBOHelper;
import com.boco.transnms.server.bo.helper.cm.TransElementBOHelper;
import com.boco.transnms.server.bo.helper.misc.EnumBOHelper;
import com.boco.irms.app.utils.CustomEnum.OwnershipManType;

public class TnmsInitUtils {
	private String uurl;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public String getUurl() {
		return uurl;
	}

	public void setUurl(String uurl) {
		this.uurl = uurl;
	}

	/**
	 * 初始化Xrpc客户端，可以通过Xrpc调用服务器端接口
	 * 
	 * @throws Exception
	 */
	public void initXrpcClient() throws Exception {
		try {
			if (uurl == null || uurl.trim().equals("")) {
				return;
			}
			ImportBoManager.createInstance(new String[] { "classpath*:/config/import-bo*.xml" });
			XrpcUrlManager.getInstance().setXrpcUrl("TRANSNMS_CONTEXT", uurl);
			// 加载相关配置文件，初始化引用BO对应的Xrpc代理地址
			BoProxyFactory.getInstance().loadSpringBeanFiles(new String[] { "tnms-client-proxy.xml" });
			DistrictCacheModel.getInstance();
			initEnums();
		} catch (Exception e) {
			logger.error("加载BO配置文件出错", e);
		}
	}

	private void initEnums() {
		try {
			if (EnumTypeManager.getInstance() == null) {
				EnumTypeManager.createInstance(new String[] { "tnms-client-enum.xml" });
			}
			EnumTypeManager.getInstance().registerEnumTypes("BooleanType", new BooleanType());
			EnumTypeManager.getInstance().registerEnumTypes("ManhleFigure", new ManhleFigure());
			EnumTypeManager.getInstance().registerEnumTypes("DMProjectState", new DMProjectState());
			EnumTypeManager.getInstance().registerEnumTypes("ProjectState", new ProjectState());
			EnumTypeManager.getInstance().registerEnumTypes("SerialNumber", new SerialNumber());
			EnumTypeManager.getInstance().registerEnumTypes(Manhle.CLASS_NAME + "LastName", new ManhleLastName());
			EnumTypeManager.getInstance().registerEnumTypes(Pole.CLASS_NAME + "LastName", new PoleLastName());
			EnumTypeManager.getInstance().registerEnumTypes(Inflexion.CLASS_NAME + "LastName", new InflexLastName());
			EnumTypeManager.getInstance().registerEnumTypes(FiberCab.CLASS_NAME + "LastName", new FiberCabLastName());
			EnumTypeManager.getInstance().registerEnumTypes(FiberJointBox.CLASS_NAME + "LastName", new FiberJointLastName());
			EnumTypeManager.getInstance().registerEnumTypes(FiberDp.CLASS_NAME + "LastName", new FiberDpLastName());
			EnumTypeManager.getInstance().registerEnumTypes(Stone.CLASS_NAME + "LastName", new StoneLastName());
			EnumTypeManager.getInstance().registerEnumTypes(Accesspoint.CLASS_NAME + "LastName", new AccesspointLastName());
//			EnumTypeManager.getInstance().registerEnumTypes(AnPos.CLASS_NAME + "LastName", new AnPosLastName());
			EnumTypeManager.getInstance().registerEnumTypes("FiberColor", new FiberColor());
			EnumTypeManager.getInstance().registerEnumTypes("CheckMonth", new CheckMonth());
			EnumTypeManager.getInstance().registerEnumTypes("CheckType", new CheckType());
			EnumTypeManager.getInstance().registerEnumTypes("ResultType", new ResultType());
			EnumTypeManager.getInstance().registerEnumTypes("DMAccessSence", new DMAccessSence());
			EnumTypeManager.getInstance().registerEnumTypes("OverLayType", new OverLayType());
			EnumTypeManager.getInstance().registerEnumTypes("LifeCycleState", new LifeCycleState());
			getDuctSegStuffEnum();
			EnumTypeManager.getInstance().registerEnumTypes("OpticalTaskType", new OpticalTaskType());
			EnumTypeManager.getInstance().registerEnumTypes("OpticalTaskTime", new OpticalTaskTime());
			EnumTypeManager.getInstance().registerEnumTypes("CheckDate", new CheckDate());
			EnumTypeManager.getInstance().registerEnumTypes("TaskCheckType", new TaskCheckType());
			EnumTypeManager.getInstance().registerEnumTypes("DMSchemeType", new DMSchemeType());
			EnumTypeManager.getInstance().registerEnumTypes("SiteType", new SiteType());
			EnumTypeManager.getInstance().registerEnumTypes("OnuboxDeviceType", new OnuboxDeviceType());
			EnumTypeManager.getInstance().registerEnumTypes("ONUBOXLastName", new OnuboxLastName());
			EnumTypeManager.getInstance().registerEnumTypes("ApointDeviceType", new ApointDeviceType());
			EnumTypeManager.getInstance().registerEnumTypes("OnuAccessType", new OnuAccessType());
			EnumTypeManager.getInstance().registerEnumTypes("ZoneType", new ZoneType());
			EnumTypeManager.getInstance().registerEnumTypes("ClientType", new ClientType());
			EnumTypeManager.getInstance().registerEnumTypes("BuildType", new BuildType());
			EnumTypeManager.getInstance().registerEnumTypes("BooleanType", new BooleanType());
			
			//地图POS编辑时使用
			EnumTypeManager.getInstance().registerEnumTypes("PosOwnershipType", new PosOwnershipType());
			EnumTypeManager.getInstance().registerEnumTypes("CanAllocateToUserType", new CanAllocateToUserType());
			EnumTypeManager.getInstance().registerEnumTypes("DMBuildType", new DMBuildType());
			EnumTypeManager.getInstance().registerEnumTypes("DMDistrictType", new DMDistrictType());
			EnumTypeManager.getInstance().registerEnumTypes("DMAccessMode", new DMAccessMode());
			EnumTypeManager.getInstance().registerEnumTypes("DMUserType", new DMUserType());
			EnumTypeManager.getInstance().registerEnumTypes("PosRationType", new PosRation());
			EnumTypeManager.getInstance().registerEnumTypes("OwnershipManType", new OwnershipManType());
			initDeviceVendor();
		} catch (Exception e) {
			logger.error("加载枚举出错", e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void initDeviceVendor() {
		try {
			Map enumTypes = EnumTypeManager.getInstance().getRegisterEnumType("DeviceVendor");
			if (enumTypes == null) {
				DataObjectList dbos =
					(DataObjectList) BoCmdFactory.getInstance().execBoCmd(DeviceVendorBOHelper.ActionName.getAllDeviceVendor, new BoActionContext());
				GenericEnum<String> genericEnum = new GenericEnum<String>();
				for (int i = 0; i < dbos.size(); i++) {
					GenericDO dbo = dbos.get(i);
					genericEnum.putEnum(dbo.getCuid(), dbo.getAttrValue(GenericDO.AttrName.labelCn).toString());
				}
				EnumTypeManager.getInstance().registerEnumTypes("DeviceVendor", genericEnum);
			}
		} catch (Exception ex) {
			logger.error("加载枚举DeviceVendor出错", ex);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void getDuctSegStuffEnum() {
		try {
			Map enumTypes = EnumTypeManager.getInstance().getRegisterEnumType("DUCT_SEG_STUFF");
			if (enumTypes == null) {
				GenericEnum stuffEnum = (GenericEnum) BoCmdFactory.getInstance().execBoCmd(EnumBOHelper.ActionName.getEnum, "DUCT_SEG_STUFF");
				EnumTypeManager.getInstance().registerEnumTypes("DUCT_SEG_STUFF", stuffEnum);
			}
		} catch (Exception ex) {
			logger.error("加载枚举DeviceVendor出错", ex);
		}
	}

	protected void initNeModel() {
		try {
			Map enumTypes = EnumTypeManager.getInstance().getRegisterEnumType("NeModeType");
			if (enumTypes == null) {
				DataObjectList dbos =
					(DataObjectList) BoCmdFactory.getInstance().execBoCmd(TransElementBOHelper.ActionName.getAllNeModel, new BoActionContext());
				GenericEnum<String> genericEnum = new GenericEnum<String>();
				for (int i = 0; i < dbos.size(); i++) {
					GenericDO dbo = dbos.get(i);
					String attrKey = dbo.getAttrString("1");
					String attrValue = dbo.getAttrString("2");
					genericEnum.putEnum(attrKey,attrValue);
				}
				EnumTypeManager.getInstance().registerEnumTypes("NeModeType", genericEnum);
			}
		} catch (Exception ex) {
			logger.error("加载枚举NeModeType出错", ex);
		}
	}
	
	public static void main(String[] args) throws Exception {
		TnmsInitUtils ss = new TnmsInitUtils();
		ss.setUurl("http://localhost:18150/dm/xrpcproxyservlet");
		ss.initXrpcClient();
		testMothed();
	}

	public static void testMothed() {
		try {
			OpticalCheckTask oct = new OpticalCheckTask();
			oct.setAttrValue(OpticalCheckTask.AttrName.createUser, "admin");
			// 任务名称
			oct.setAttrValue(OpticalCheckTask.AttrName.taskName, "admin-0112");
			// 2代表是光路核查
			oct.setAttrValue(OpticalCheckTask.AttrName.taskType, 2);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 0L);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle, 4L);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskStartWay, 1L);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue, 1L);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskState, 2L);
			oct.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid, "DISTRICT-00001");
			Date date = new Date();
			Timestamp timestamp = TimeFormatHelper.getFormatTimestamp(date, "yyyy-MM-dd HH:mm:ss");
			oct.setAttrValue(OpticalCheckTask.AttrName.createtime, timestamp);
			oct.setAttrValue(OpticalCheckTask.AttrName.taskWorkTime, timestamp);

			OpticalWay ow =
				(OpticalWay) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid", new BoActionContext(),
					"OPTICAL_WAY-8a9e2d10482556d401484363a5226a21");
			BoCmdFactory.getInstance().execBoCmd("IMakeOpticalWayBO.updateOpticalWayRoute", oct, ow);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}