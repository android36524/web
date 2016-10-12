package com.boco.irms.app.utils;

import com.boco.common.util.lang.GenericEnum;

public class CustomEnum {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class BooleanType extends GenericEnum {
		private static final long serialVersionUID = -8778301743010741854L;

		public static final long _false = 0;
		public static final long _true = 1;

		protected BooleanType() {
			super.putEnum(_false, "否");
			super.putEnum(_true, "是");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class ManhleLastName extends GenericEnum {
		private static final long serialVersionUID = 8365328957155323327L;

		public static final String _en = "#";
		public static final String _ch = "号人手井";

		protected ManhleLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号人手井");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class PoleLastName extends GenericEnum {
		private static final long serialVersionUID = -6039095916914605531L;

		public static final String _en = "#";
		public static final String _ch = "号电杆";

		protected PoleLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号电杆");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class InflexLastName extends GenericEnum {
		private static final long serialVersionUID = -6708033302839468876L;

		public static final String _en = "#";
		public static final String _ch = "号拐点";

		protected InflexLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号拐点");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class AccesspointLastName extends GenericEnum {
		private static final long serialVersionUID = 1379285047379247286L;

		public static final String _en = "#";
		public static final String _ch = "号接入点";

		protected AccesspointLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号接入点");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class FiberCabLastName extends GenericEnum {
		private static final long serialVersionUID = 880274053235844224L;

		public static final String _en = "#";
		public static final String _ch = "号光交接箱";

		protected FiberCabLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号光交接箱");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class FiberJointLastName extends GenericEnum {
		private static final long serialVersionUID = 894874929838807901L;

		public static final String _en = "#";
		public static final String _ch = "号光接头盒";

		protected FiberJointLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号光接头盒");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class FiberDpLastName extends GenericEnum {
		private static final long serialVersionUID = -1045372237434611414L;

		public static final String _en = "#";
		public static final String _ch = "号光分纤箱";

		protected FiberDpLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号光分纤箱");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class StoneLastName extends GenericEnum {
		private static final long serialVersionUID = 4915960828843437330L;

		public static final String _en = "#";
		public static final String _ch = "号标石";

		protected StoneLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号标石");
		}
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static class AnPosLastName extends GenericEnum {
//		private static final long serialVersionUID = 4915960828843437330L;
//
//		public static final String _en = "#";
//		public static final String _ch = "号分光器";
//
//		protected AnPosLastName() {
//			super.putEnum(_en, "#");
//			super.putEnum(_ch, "号分光器");
//		}
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class ManhleFigure extends GenericEnum {
		private static final long serialVersionUID = -7150454361795508477L;

		public static final String _0 = "0";
		public static final String _00 = "00";
		public static final String _000 = "000";
		public static final String _0000 = "0000";
		public static final String _00000 = "00000";

		protected ManhleFigure() {
			super.putEnum(_0, "0");
			super.putEnum(_00, "00");
			super.putEnum(_000, "000");
			super.putEnum(_0000, "0000");
			super.putEnum(_00000, "00000");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class SerialNumber extends GenericEnum {
		private static final long serialVersionUID = 3529256762804367854L;

		public static final long _0 = 1;
		public static final long _00 = 2;
		public static final long _000 = 3;
		public static final long _0000 = 4;
		public static final long _00000 = 5;

		protected SerialNumber() {
			super.putEnum(_0, "0");
			super.putEnum(_00, "00");
			super.putEnum(_000, "000");
			super.putEnum(_0000, "0000");
			super.putEnum(_00000, "00000");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMProjectState extends GenericEnum {
		private static final long serialVersionUID = 8586424652784512301L;

		public static final long _design = 1;
		public static final long _construction = 2;
		public static final long _completion = 3;
		public static final long _cancel = 4;
		public static final long _maintain = 5;
		public static final long _approval = 6;
		public static final long _to_plan = 7;
		public static final long _plan = 8;
		public static final long _designDDM = 9;
		public static final long _apply = 10;

		DMProjectState() {
			super.putEnum(_design, "设计");
			super.putEnum(_construction, "施工/在建");
			super.putEnum(_completion, "竣工");
			super.putEnum(_cancel, "作废");
			super.putEnum(_maintain, "维护");
			super.putEnum(_approval, "待审批");
			super.putEnum(_to_plan, "待规划");
			super.putEnum(_plan, "规划");
			super.putEnum(_designDDM, "补录");
			super.putEnum(_apply, "立项");
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class ProjectState extends GenericEnum {
		private static final long serialVersionUID = 7132179627582606016L;

		public static final long _unkown = 0;
		public static final long _design = 1;
		public static final long _construction = 2;
		public static final long _completion = 3;
		public static final long _maintain = 5;
		public static final long _changAudit = 6;
		public static final long _acceptance = 7;
		public static final long _approval = 9;

		ProjectState() {
			super.putEnum(_unkown, "待设计");
			super.putEnum(_design, "设计");
			super.putEnum(_construction, "施工");
			super.putEnum(_completion, "竣工");
			super.putEnum(_maintain, "维护");
			super.putEnum(_changAudit, "变更审核");// 6
			super.putEnum(_acceptance, "验收");// 7
			super.putEnum(_approval, "待审批");// 9
		}
	}

	// 任务类型
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class TaskCheckType extends GenericEnum {
		private static final long serialVersionUID = 7818264579506670806L;

		public static long opticalCheck = 1;
		public static long opticalWayCheck = 2;

		protected TaskCheckType() {
			super.putEnum(opticalCheck, "光纤核查");
			super.putEnum(opticalWayCheck, "光路核查");
		}
	}

	// 纤芯颜色
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class FiberColor extends GenericEnum {
		private static final long serialVersionUID = -1709446812464052516L;

		public static long blue = -16776961;
		public static long orange = -14336;
		public static long green = -16711936;
		public static long brown = -5952982;
		public static long gray = -8355712;
		public static long white = -1;
		public static long red = -65536;
		public static long black = -16777216;
		public static long yellow = -256;
		public static long purple = -8388480;
		public static long pink = -20561;
		public static long cyan = -12525360;

		protected FiberColor() {
			super.putEnum(blue, "蓝");
			super.putEnum(orange, "橙");
			super.putEnum(green, "绿");
			super.putEnum(brown, "棕");
			super.putEnum(gray, "灰");
			super.putEnum(white, "白");
			super.putEnum(red, "红");
			super.putEnum(black, "黑");
			super.putEnum(yellow, "黄");
			super.putEnum(purple, "紫");
			super.putEnum(pink, "粉红");
			super.putEnum(cyan, "青绿");
		}
	}

	// 核查月份
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class CheckMonth extends GenericEnum {
		private static final long serialVersionUID = 7607236066212339800L;

		public static final long _1 = 1;
		public static final long _2 = 2;
		public static final long _3 = 3;
		public static final long _4 = 4;
		public static final long _5 = 5;
		public static final long _6 = 6;
		public static final long _7 = 7;
		public static final long _8 = 8;
		public static final long _9 = 9;
		public static final long _10 = 10;
		public static final long _11 = 11;
		public static final long _12 = 12;

		protected CheckMonth() {
			super.putEnum(_1, "1");
			super.putEnum(_2, "2");
			super.putEnum(_3, "3");
			super.putEnum(_4, "4");
			super.putEnum(_5, "5");
			super.putEnum(_6, "6");
			super.putEnum(_7, "7");
			super.putEnum(_8, "8");
			super.putEnum(_9, "9");
			super.putEnum(_10, "10");
			super.putEnum(_11, "11");
			super.putEnum(_12, "12");
		}
	}

	// 区域细分光缆皮长统计： 统计类型
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class CheckType extends GenericEnum {
		private static final long serialVersionUID = 6160899530132965685L;

		public static final long wireLength = 1;
		public static final long substituteLength = 2;
		public static final long ductLineLength = 3;

		protected CheckType() {
			super.putEnum(wireLength, "光缆皮长长度");
			super.putEnum(substituteLength, "代维统计长度");
			super.putEnum(ductLineLength, "承载物段长度");
		}
	}

	// 统计类型
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class ResultType extends GenericEnum {
		private static final long serialVersionUID = 4629142067469886501L;

		public static final long normalLength = 1;
		public static final long substituteLength = 2;

		protected ResultType() {
			super.putEnum(normalLength, "正常长度");
			super.putEnum(substituteLength, "代维考核长度");
		}
	}

	// 集客接入场景 枚举（0：未知，1：综合业务机房，2：基站机房，3：客户机房，4：预覆盖，5：其他）
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMAccessSence extends GenericEnum {
		private static final long serialVersionUID = 1800311443252630756L;

		public static final long _businessRoom = 1;
		public static final long _baseRoom = 2;
		public static final long _customerRoom = 3;
		public static final long _preCoating = 4;
		public static final long _other = 5;

		DMAccessSence() {
			super.putEnum(_businessRoom, "综合业务机房");
			super.putEnum(_baseRoom, "基站机房");
			super.putEnum(_customerRoom, "客户机房");
			super.putEnum(_preCoating, "预覆盖");
			super.putEnum(_other, "其他");
		}
	}

	// 资源类型 枚举 机房/接头盒/交接箱/光分纤箱
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class OverLayType extends GenericEnum {
		private static final long serialVersionUID = 4965197386621647292L;

		public static final long _room = 1;
		public static final long _fiberJointBox = 2;
		public static final long _fiberCab = 3;
		public static final long _fiberDp = 4;

		OverLayType() {
			super.putEnum(_room, "机房");
			super.putEnum(_fiberJointBox, "接头盒");
			super.putEnum(_fiberCab, "交接箱");
			super.putEnum(_fiberDp, "光分纤箱");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class LifeCycleState extends GenericEnum {
		private static final long serialVersionUID = -860227592231249578L;

		public static final long _project = 1;
		public static final long _offical = 2;

		protected LifeCycleState() {
			super.putEnum(_project, "工程");
			super.putEnum(_offical, "现网");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class OpticalTaskType extends GenericEnum {
		private static final long serialVersionUID = 1905512335892254989L;

		public static final String opticalTaskCheck = "1";
		public static final String opticalTaskWayCheck = "2";

		protected OpticalTaskType() {
			super.putEnum(opticalTaskCheck, "光纤核查明细");
			super.putEnum(opticalTaskWayCheck, "光路核查明细");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class OpticalTaskTime extends GenericEnum {
		private static final long serialVersionUID = -5830701511461348721L;

		public static final String taskTime1 = "1:00";
		public static final String taskTime2 = "2:00";
		public static final String taskTime3 = "3:00";
		public static final String taskTime4 = "4:00";

		protected OpticalTaskTime() {
			super.putEnum(taskTime1, "1:00");
			super.putEnum(taskTime2, "2:00");
			super.putEnum(taskTime3, "3:00");
			super.putEnum(taskTime4, "4:00");
		}
	}

	// 核查月号
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class CheckDate extends GenericEnum {
		private static final long serialVersionUID = -3081658042971614744L;

		public static final long _Date1 = 1;
		public static final long _Date2 = 2;
		public static final long _Date3 = 3;
		public static final long _Date4 = 4;
		public static final long _Date5 = 5;
		public static final long _Date6 = 6;
		public static final long _Date7 = 7;
		public static final long _Date8 = 8;
		public static final long _Date9 = 9;
		public static final long _Date10 = 10;
		public static final long _Date11 = 11;
		public static final long _Date12 = 12;
		public static final long _Date13 = 13;
		public static final long _Date14 = 14;
		public static final long _Date15 = 15;
		public static final long _Date16 = 16;
		public static final long _Date17 = 17;
		public static final long _Date18 = 18;
		public static final long _Date19 = 19;
		public static final long _Date20 = 20;
		public static final long _Date21 = 21;
		public static final long _Date22 = 22;
		public static final long _Date23 = 23;
		public static final long _Date24 = 24;
		public static final long _Date25 = 25;
		public static final long _Date26 = 26;
		public static final long _Date27 = 27;
		public static final long _Date28 = 28;
		public static final long _Date29 = 29;
		public static final long _Date30 = 30;
		public static final long _Date31 = 31;

		protected CheckDate() {
			super.putEnum(_Date1, "1号");
			super.putEnum(_Date2, "2号");
			super.putEnum(_Date3, "3号");
			super.putEnum(_Date4, "4号");
			super.putEnum(_Date5, "5号");
			super.putEnum(_Date6, "6号");
			super.putEnum(_Date7, "7号");
			super.putEnum(_Date8, "8号");
			super.putEnum(_Date9, "9号");
			super.putEnum(_Date10, "10号");
			super.putEnum(_Date11, "11号");
			super.putEnum(_Date12, "12号");
			super.putEnum(_Date13, "13号");
			super.putEnum(_Date14, "14号");
			super.putEnum(_Date15, "15号");
			super.putEnum(_Date16, "16号");
			super.putEnum(_Date17, "17号");
			super.putEnum(_Date18, "18号");
			super.putEnum(_Date19, "19号");
			super.putEnum(_Date20, "20号");
			super.putEnum(_Date21, "21号");
			super.putEnum(_Date22, "22号");
			super.putEnum(_Date23, "23号");
			super.putEnum(_Date24, "24号");
			super.putEnum(_Date25, "25号");
			super.putEnum(_Date26, "26号");
			super.putEnum(_Date27, "27号");
			super.putEnum(_Date28, "28号");
			super.putEnum(_Date29, "29号");
			super.putEnum(_Date30, "30号");
			super.putEnum(_Date31, "31号");
		}
	}
	
	// PROJECT_MANAGEMENT表SCHEME_TYPE的枚举值,暂时保存数据来源
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMSchemeType extends GenericEnum {
		private static final long serialVersionUID = 1162319613058162351L;

		public static final long _unKnown = 0;
		public static final long _process = 99;
		public static final long _input = 100;
		public static final long _interface = 101;
		public static final long _subProject = 102;
		public static final long _simulateProject = 103;

		DMSchemeType() {
			super.putEnum(_unKnown, "未知");
			super.putEnum(_process, "流程录入");
			super.putEnum(_input, "手工录入");
			super.putEnum(_interface, "接口透传");
			super.putEnum(_subProject, "子项目");
			super.putEnum(_simulateProject, "模拟项目");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class SiteType extends GenericEnum {
    	public static final long _all = -1;
    	public static final long _mainSite = 1;
    	public static final long _converSite = 2;
    	public static final long _accessSite = 3;
    	public static final long _coreSite = 4;
    	public static final long _userSite = 5;
    	public static final long _otherSite = 6;
    	public static final long _resourcePoint = 7;
    	public static final long _non = 0;
    	SiteType(){
    		super.putEnum(_all, "全部");
    		super.putEnum(_mainSite, "骨干站点");
    		super.putEnum(_converSite, "汇聚站点");
    		super.putEnum(_accessSite, "接入站点");
    		super.putEnum(_coreSite, "核心站点");
    		super.putEnum(_userSite, "用户站点");
    		super.putEnum(_otherSite, "其他站点");
    		super.putEnum(_resourcePoint, "资源点");
    		super.putEnum(_non, "未知");
    	}
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class OnuboxDeviceType extends GenericEnum {
    	public static final long FIBERCAB = 1;
    	public static final long ONUBOX = 2;
    	OnuboxDeviceType(){
    		super.putEnum(FIBERCAB, "光交接箱");
    		super.putEnum(ONUBOX, "ONU综合箱");
    	}
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class OnuboxLastName extends GenericEnum {
		private static final long serialVersionUID = 1L;
		
		public static final String _en = "#";
		public static final String _ch = "号ONU综合箱";

		protected OnuboxLastName() {
			super.putEnum(_en, "#");
			super.putEnum(_ch, "号ONU综合箱");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class ApointDeviceType extends GenericEnum {
		private static final long serialVersionUID = 1L;

		public static final long FIBERCAB = 1;
		public static final long FIBERDP = 2;
		public static final long FIBERJOINT = 3;
		public static final long POS = 4;
		public static final long ONU = 5;
		public static final long ODF = 6;

		protected ApointDeviceType() {
			super.putEnum(FIBERCAB, "光交接箱");
			super.putEnum(FIBERDP, "光分纤箱");
			super.putEnum(FIBERJOINT, "终端盒");
			super.putEnum(POS, "POS");
			super.putEnum(ONU, "ONU");
			super.putEnum(ODF, "ODF");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMBuildType extends GenericEnum {

		private static final long serialVersionUID = 3391060049439340557L;
		
		public static final long CMCC = 1;
		public static final long WCDMA = 2;
		public static final long CATV = 3;
		public static final long COMBINED = 4;
		public static final long OTHER = 5;
		
		DMBuildType() {
			super.putEnum(CMCC, "移动");
			super.putEnum(WCDMA, "联通");
			super.putEnum(CATV, "广电");
			super.putEnum(COMBINED, "合建");
			super.putEnum(OTHER, "其他");
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMDistrictType extends GenericEnum {

		private static final long serialVersionUID = -7292595603633457765L;
		public static final long CITY = 1;
		public static final long TOWN = 2;
		public static final long VILLAGE = 3;
		
		DMDistrictType() {
			super.putEnum(CITY, "城市");
			super.putEnum(TOWN, "乡镇");
			super.putEnum(VILLAGE, "农村");
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMAccessMode extends GenericEnum {

		private static final long serialVersionUID = -3421958004317909309L;
		public static final long FTTB = 1;
		public static final long FTTH = 2;
		
		DMAccessMode() {
			super.putEnum(FTTB, "FTTB");
			super.putEnum(FTTH, "FTTH");
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class DMUserType extends GenericEnum {
		private static final long serialVersionUID = 7426253287539922249L;
		public static final long HOME = 1;
		public static final long SCHOOL = 2;
		public static final long SHOP = 3;
		public static final long OFFICE = 4;
		
		DMUserType() {
			super.putEnum(HOME, "家庭");
			super.putEnum(SCHOOL, "学校");
			super.putEnum(SHOP, "商铺");
			super.putEnum(OFFICE, "写字楼");
		}
	}
	@SuppressWarnings("rawtypes")
	public static class OnuAccessType extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final long GC = 1;
		public static final long FC = 2;
		public static final long SHARE = 3;
		public static final long OTHER = 4;
		
		@SuppressWarnings("unchecked")
		protected OnuAccessType(){
			super.putEnum(GC, "集客");
			super.putEnum(FC, "家客");
			super.putEnum(SHARE, "集客/家客公用");
			super.putEnum(OTHER, "其他");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class PosOwnershipType extends GenericEnum {
		private static final long serialVersionUID = 1L;

		public static final long SELFBUILD = 1;
		public static final long TOGETHERBUILD = 3;

		protected PosOwnershipType() {
			super.putEnum(SELFBUILD, "自建");
			super.putEnum(TOGETHERBUILD, "合作");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class CanAllocateToUserType extends GenericEnum {
		private static final long serialVersionUID = 1L;

		public static final long ISTRUE = 1;
		public static final long ISFALSE = 0;

		protected CanAllocateToUserType() {
			super.putEnum(ISTRUE, "是");
			super.putEnum(ISFALSE, "否");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class ZoneType extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final long CITY = 1;
		public static final long TOWN = 2;
		public static final long COUNTY = 3;
		
		@SuppressWarnings("unchecked")
		protected ZoneType(){
			super.putEnum(CITY,"城市");
			super.putEnum(TOWN, "乡镇");
			super.putEnum(COUNTY, "农村");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class ClientType extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final long FAMILIY = 1;
		public static final long SCHOOL = 2;
		public static final long STREET = 3;
		public static final long WORKBUILDING = 4;
		
		@SuppressWarnings("unchecked")
		protected ClientType(){
			super.putEnum(FAMILIY,"家庭");
			super.putEnum(SCHOOL, "学校");
			super.putEnum(STREET, "商铺");
			super.putEnum(WORKBUILDING, "写字楼");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class BuildType extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final long CMCC = 1;
		public static final long CMCT = 2;
		public static final long CTT = 3;
		public static final long BUILDT = 4;
		public static final long OTHERT = 5;
		
		@SuppressWarnings("unchecked")
		protected BuildType(){
			super.putEnum(CMCC,"移动");
			super.putEnum(CMCT, "广电");
			super.putEnum(CTT, "铁通");
			super.putEnum(BUILDT, "合建");
			super.putEnum(OTHERT, "其他合作方式");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class PosRation extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final String PosRation4 = "1:4";
		public static final String PosRation8 = "1:8";
		public static final String PosRation16 = "1:16";
		public static final String PosRation32 = "1:32";
		public static final String PosRation64 = "1:64";
		
		@SuppressWarnings("unchecked")
		protected PosRation(){
			super.putEnum(PosRation4, "1:4");
			super.putEnum(PosRation8, "1:8");
			super.putEnum(PosRation16, "1:16");
			super.putEnum(PosRation32, "1:32");
			super.putEnum(PosRation64, "1:64");
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public static class OwnershipManType extends GenericEnum{
		private static final long serialVersionUID = 1L;
		public static final long TYPE1 = 1;
		public static final long TYPE2 = 2;
		public static final long TYPE3 = 3;
		public static final long TYPE4 = 4;
		public static final long TYPE5 = 5;
		public static final long TYPE6 = 6;
		public static final long TYPE7 = 7;
		public static final long TYPE8 = 8;
		public static final long TYPE9 = 9;
		
		@SuppressWarnings("unchecked")
		protected OwnershipManType(){
			super.putEnum(TYPE1,"移动");
			super.putEnum(TYPE2, "联通");
			super.putEnum(TYPE3, "电信");
			super.putEnum(TYPE4, "铁通");
			super.putEnum(TYPE5, "广电");
			super.putEnum(TYPE6, "政府");
			super.putEnum(TYPE7, "客户自有");
			super.putEnum(TYPE8, "物业");
			super.putEnum(TYPE9, "其它");
		}
	}
}