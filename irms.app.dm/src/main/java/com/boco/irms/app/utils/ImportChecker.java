package com.boco.irms.app.utils;

import java.util.ArrayList;

import jxl.Sheet;

import com.boco.transnms.server.bo.dm.dataimpexp.ImpExpUtils;

public class ImportChecker {

	public ImportChecker(){
		
	}
	
	public static String checkFiberDP(Sheet sheet,int index,ArrayList errList){
		if(sheet==null){
			return "sheet页有问题，请确认导入文件是否为空！";
		}
		if (ImpExpUtils.isEmptyRow(sheet, index)) { // 判断是否为空行
			return "导入表格第一页，第一行为空，不符合导入表格要求！";
		}
		String errorInfo = "";
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"所属区域", index, errList))) {
			errorInfo += "所属区域 为空；";
		} // RELATED_DISTRICT_CUID
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"名称", index, errList))) {
			errorInfo += "名称 为空；";
		} // LABEL_CN
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"编号", index, errList))) {
			errorInfo += "编号 为空；";
		} // JUNCTION_TYPE
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"入网时间", index, errList))) {
			errorInfo += "入网时间 为空；";
		} // SETUP_TIME
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"建设单位", index, errList))) {
			errorInfo += "建设单位 为空；";
		} // CREATOR
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"地址", index, errList))) {
			errorInfo += "地址 为空；";
		} // LOCATION
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"设备供应商", index, errList))) {
			errorInfo += "设备供应商 为空；";
		} // RELATED_VENDOR_CUID
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"产权归属", index, errList))) {
			errorInfo += "产权归属 为空；";
		} // OWNERSHIP
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"用途", index, errList))) {
			errorInfo += "用途 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"经度", index, errList))) {
			errorInfo += "经度 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"纬度", index, errList))) {
			errorInfo += "纬度 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"设备标识", index, errList))) {
			errorInfo += "设备标识 为空；";
		} // LABEL_DEV
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"设备序列号", index, errList))) {
			errorInfo += "设备序列号 为空；";
		} // SEQNO
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"使用单位", index, errList))) {
			errorInfo += "使用单位 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"厂商特征值", index, errList))) {
			errorInfo += "厂商特征值 为空；";
		} // SPECIAL_LABEL
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"所有权人", index, errList))) {
			errorInfo += "所有权人 为空；";
		} // USERNAME
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护单位", index, errList))) {
			errorInfo += "维护单位 为空；";
		} // MAINT_DEP
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护人", index, errList))) {
			errorInfo += "维护人 为空；";
		} // PRESERVER
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护人联系电话", index, errList))) {
			errorInfo += "维护人联系电话 为空；";
		} // PRESERVER_PHONE
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护人通信地址", index, errList))) {
			errorInfo += "维护人通信地址 为空；";
		} // PRESERVER_ADDR
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护方式", index, errList))) {
			errorInfo += "维护方式 为空；";
		} // MAINT_MODE
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"是否正使用", index, errList))) {
			errorInfo += "是否正使用 为空；";
		} // IS_USAGE_STATE
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"核查日期", index, errList))) {
			errorInfo += "核查日期 为空；";
		} // CREATTIME
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"巡检人", index, errList))) {
			errorInfo += "巡检人 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"备注", index, errList))) {
			errorInfo += "备注 为空；";
		} // REMARK
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"模板名称", index, errList))) {
			errorInfo += "模板名称 为空；";
		} // REMARK
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"是否预覆盖接入点", index, errList))) {
			errorInfo += "是否预覆盖接入点 为空；";
		} // IS_YJR
		
		if(!ImpExpUtils.isEmpty(errorInfo)){
			errorInfo = "第 "+(index+1)+"行数据:"+errorInfo;
		}
		return errorInfo;
	}
	

	
	public static String checkFiberCab(Sheet sheet,int index,ArrayList errList){
		if(sheet==null){
			return "sheet页有问题，请确认导入文件是否为空！";
		}
		if (ImpExpUtils.isEmptyRow(sheet, index)) { // 判断是否为空行
			return "导入表格第一页，第一行为空，不符合导入表格要求！";
		}
		String errorInfo = "";
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"所属区域", index, errList))) {
			errorInfo += "所属区域 为空；";
		} // RELATED_DISTRICT_CUID
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"名称", index, errList))) {
			errorInfo += "名称 为空；";
		} // LABEL_CN
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"用途", index, errList))) {
			errorInfo += "用途 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"经度", index, errList))) {
			errorInfo += "经度 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"纬度", index, errList))) {
			errorInfo += "纬度 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护方式", index, errList))) {
			errorInfo += "维护方式 为空；";
		} // MAINT_MODE
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"数据库主键", index, errList))) {
			errorInfo += "数据库主键 为空；";
		} 

		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"模板名称", index, errList))) {
			errorInfo += "模板名称 为空；";
		} 
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"集客接入场景", index, errList))) {
			errorInfo += "集客接入场景 为空；";
		} 
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"产权归属", index, errList))) {
			errorInfo += "产权归属 为空；";
		} 

		if(!ImpExpUtils.isEmpty(errorInfo)){
			errorInfo = "第 "+(index+1)+"行数据:"+errorInfo;
		}
		return errorInfo;
	}
	
	
	public static String checktFiberJointBox(Sheet sheet,int index,ArrayList errList){
		if(sheet==null){
			return "sheet页有问题，请确认导入文件是否为空！";
		}
		if (ImpExpUtils.isEmptyRow(sheet, index)) { // 判断是否为空行
			return "导入表格第一页，第一行为空，不符合导入表格要求！";
		}
		String errorInfo = "";
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"所属区域", index, errList))) {
			errorInfo += "所属区域 为空；";
		} // RELATED_DISTRICT_CUID
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"名称", index, errList))) {
			errorInfo += "名称 为空；";
		} // LABEL_CN
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"经度", index, errList))) {
			errorInfo += "经度 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"纬度", index, errList))) {
			errorInfo += "纬度 为空；";
		}
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"数据库主键", index, errList))) {
			errorInfo += "数据库主键 为空；";
		} 

		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"集客接入场景", index, errList))) {
			errorInfo += "集客接入场景 为空；";
		} 

		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"维护方式", index, errList))) {
			errorInfo += "维护方式 为空；";
		} // MAINT_MODE
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"是否预覆盖接入点", index, errList))) {
			errorInfo += "是否预覆盖接入点 为空；";
		} 
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"产权归属", index, errList))) {
			errorInfo += "产权归属 为空；";
		} 

		if(!ImpExpUtils.isEmpty(errorInfo)){
			errorInfo = "第 "+(index+1)+"行数据:"+errorInfo;
		}
		return errorInfo;
	}
	
	

	
	public static String checktRoom(Sheet sheet,int index,ArrayList errList){
		if(sheet==null){
			return "sheet页有问题，请确认导入文件是否为空！";
		}
		if (ImpExpUtils.isEmptyRow(sheet, index)) { // 判断是否为空行
			return "导入表格第一页，第一行为空，不符合导入表格要求！";
		}
		String errorInfo = "";
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"修改后机房名称", index, errList))) {
			errorInfo += "修改后机房名称 为空；";
		} // RELATED_DISTRICT_CUID
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"机房名称", index, errList))) {
			errorInfo += "机房名称 为空；";
		} // LABEL_CN
		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"数据库主键", index, errList))) {
			errorInfo += "数据库主键 为空；";
		} 

		
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"机房类型", index, errList))) {
			errorInfo += "机房类型 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"业务级别", index, errList))) {
			errorInfo += "业务级别 为空；";
		}
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"所属站点", index, errList))) {
			errorInfo += "所属站点 为空；";
		} 

		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"产权", index, errList))) {
			errorInfo += "产权 为空；";
		} 
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"钥匙类型", index, errList))) {
			errorInfo += "钥匙类型 为空；";
		} 
		if (ImpExpUtils.isEmpty(ImpExpUtils.getSheetValueByLabel(sheet,
				"设备状态", index, errList))) {
			errorInfo += "设备状态 为空；";
		} 

		if(!ImpExpUtils.isEmpty(errorInfo)){
			errorInfo = "第 "+(index+1)+"行数据:"+errorInfo;
		}
		return errorInfo;
	}
}
