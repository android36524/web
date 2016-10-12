package com.boco.irms.app.dm.action.schedule;
import java.util.Date;

/**
 * @description 统计常量
 * @author liuchao
 * @verison 
 * @date 2015年1月28日 上午9:16:58
 */
public class StatConst {
	
	public static final String DATASOURCE_TNMS = "TNMS";
	
	public static final String DATASOURCE_SDE = "SDE";
	
	public static final String DATASOURCE_IRMS = "IRMS";
	
	public static FaultThreshold faultThreshold;
	
	public static boolean isWireStatByDis = false;
	
	public static boolean isStatWireRemain = false;
	
	public static boolean isThresholdByDis = false;
	
	public static Date taskBeginTime = null;
	
	public static String wireStatCronExpression = "";
	
	public static String ductStatCronExpression = "";
	
	public static String taskName = "WireStatTaskName";
	
	public static String triggerName = "WireStatTriggerName";
	
	public static String outOfLength = "路由长度越界";
	
	public static String outOfRatio = "长度比值超标";
	
	public static String noLay = "光缆未敷设";
	
	public static String noFiber = "未添加纤芯";
	
	public static String notEntireFiber = "纤芯未上架";
	
	public static String numDiff = "纤芯数和实际不一致";
	
	public static String multiLay = "反复敷设";
	
	public static String rebuild = "被删除重建";
	
	public static int checkMonth = 1;
	
	//河南统计月度光缆增量&减量
	public static boolean isStatAddDelWireOfHenan = false;
	
	//河南根据所属公司进行长度统计
	public static boolean isWireStatOfHenan = false;
	
	//湖北隐患点统计启动时间表达式
	//public static String troubleCronExpression = "";
	
	//湖北网格颜色渲染
	//public static Boolean isGridColorRenderOfHB = false;
	
	//湖北网格颜色渲染启动时间表达式
	//public static String gridRenderExpression = "";
}
