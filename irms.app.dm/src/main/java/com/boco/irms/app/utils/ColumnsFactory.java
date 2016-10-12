package com.boco.irms.app.utils;

import java.util.ArrayList;
import java.util.List;

import com.boco.component.grid.pojo.GridColumn;
import com.boco.component.grid.pojo.GridMeta;

public class ColumnsFactory {

	private static ColumnsFactory instance;
	private ColumnsFactory(){
		
	}
	
	public static ColumnsFactory getInstance(){
		if(instance==null){
			instance = new ColumnsFactory();
		}
		return instance;
	}
	
	public static void dealRoomGridMeta(GridMeta meta){
		if(meta ==null)
			return;
		List<GridColumn> columns= new ArrayList<GridColumn>();
		GridColumn col1 = new GridColumn();
		col1.setDataIndex("LABEL_CN");
		col1.setHeader("机房名称");
		columns.add(col1);

		GridColumn col2 = new GridColumn();
		col2.setDataIndex("LAST_LABELCN");
		col2.setHeader("修改后机房名称");
		columns.add(col2);
		
		GridColumn col3 = new GridColumn();
		col3.setDataIndex("ABBREVIATION");
		col3.setHeader("缩写");
		columns.add(col3);

		GridColumn col4 = new GridColumn();
		col4.setDataIndex("ADDRESS");
		col4.setHeader("产权");
		columns.add(col4);
		
		GridColumn col5 = new GridColumn();
		col5.setDataIndex("YEWUJIBIE");
		col5.setHeader("业务级别");
		columns.add(col5);

		GridColumn col6 = new GridColumn();
		col6.setDataIndex("LENGTH");
		col6.setHeader("长");
		columns.add(col6);
		
		GridColumn col7 = new GridColumn();
		col7.setDataIndex("WIDTH");
		col7.setHeader("宽");
		columns.add(col7);

		GridColumn col8 = new GridColumn();
		col8.setDataIndex("HEIGHT");
		col8.setHeader("高");
		columns.add(col8);
		
		GridColumn col9 = new GridColumn();
		col9.setDataIndex("CONTACTOR");
		col9.setHeader("联系人");
		columns.add(col9);
		
		GridColumn col10 = new GridColumn();
		col10.setDataIndex("CONTACTADDRESS");
		col10.setHeader("联系地址");
		columns.add(col10);

		GridColumn col11 = new GridColumn();
		col11.setDataIndex("ROOMTYPENAME");
		col11.setHeader("机房类型");
		columns.add(col11);
		
		GridColumn col12 = new GridColumn();
		col12.setDataIndex("RELATEDSPACENAME");
		col12.setHeader("所属站点");
		columns.add(col12);

		GridColumn col13 = new GridColumn();
		col13.setDataIndex("RELATEDFLOORNAME");
		col13.setHeader("所属楼层");
		columns.add(col13);
		
		GridColumn col14 = new GridColumn();
		col14.setDataIndex("YAOSHI");
		col14.setHeader("钥匙类型");
		columns.add(col14);

		GridColumn col15 = new GridColumn();
		col15.setDataIndex("WEIHUFANGSHI");
		col15.setHeader("维护方式");
		columns.add(col15);
		
		GridColumn col16 = new GridColumn();
		col16.setDataIndex("SHEBEIZHANGTAI");
		col16.setHeader("设备状态");
		columns.add(col16);

		GridColumn col17 = new GridColumn();
		col17.setDataIndex("MAINTAINDEPARTMENT");
		col17.setHeader("维护单位");
		columns.add(col17);
		
		GridColumn col18 = new GridColumn();
		col18.setDataIndex("EQUIPMENTCODE");
		col18.setHeader("固定资产编号");
		columns.add(col18);

		GridColumn col19 = new GridColumn();
		col19.setDataIndex("REMARK");
		col19.setHeader("备注");
		columns.add(col19);
		
		GridColumn col20 = new GridColumn();
		col20.setDataIndex("ROWCOUNT");
		col20.setHeader("行号");
		columns.add(col20);

		GridColumn col21 = new GridColumn();
		col21.setDataIndex("BASESTADEVICETYPE");
		col21.setHeader("基站设备类型");
		columns.add(col21);
		
		GridColumn col22 = new GridColumn();
		col22.setDataIndex("COLCOUNT");
		col22.setHeader("列号");
		columns.add(col22);

		GridColumn col23 = new GridColumn();
		col23.setDataIndex("CHUANSHUSHEBEI");
		col23.setHeader("传输主设备型号");
		columns.add(col23);
		
		GridColumn col24 = new GridColumn();
		col24.setDataIndex("ROOM");
		col24.setHeader("接入数据机房类型");
		columns.add(col24);

		GridColumn col25 = new GridColumn();
		col25.setDataIndex("ZHUANXIANDENGJI");
		col25.setHeader("专线等级");
		columns.add(col25);
		
		GridColumn col26 = new GridColumn();
		col26.setDataIndex("DEVICERELATEDPROJECT");
		col26.setHeader("新增设备所属工程");
		columns.add(col26);

		GridColumn col27 = new GridColumn();
		col27.setDataIndex("TELEPHONE");
		col27.setHeader("联系电话");
		columns.add(col27);

		GridColumn col28 = new GridColumn();
		col28.setDataIndex("MAINDEVICEMODEL");
		col28.setHeader("传输主设备类型");
		columns.add(col28);
		

		GridColumn col29 = new GridColumn();
		col29.setDataIndex("CUID");
		col29.setHeader("数据库主键");
		columns.add(col29);
		
		meta.setColumns(columns);

	}
	
	public static List<String> getRoomColList(){
		List<String> result = new ArrayList<String>();
		result.add("LABEL_CN");
		result.add("LAST_LABELCN");
		result.add("ABBREVIATION");
		result.add("YEWUJIBIE");
		result.add("ADDRESS");
		result.add("ROOMTYPENAME");
		result.add("YAOSHI");
		result.add("SHEBEIZHANGTAI");
		result.add("WEIHUFANGSHI");
		result.add("MAINTAINDEPARTMENT");
		result.add("RELATEDSPACENAME");
		result.add("RELATEDFLOORNAME");
		result.add("EQUIPMENTCODE");
		result.add("LENGTH");
		result.add("WIDTH");
		result.add("HEIGHT");
		result.add("ROWCOUNT");
		result.add("COLCOUNT");
		result.add("CONTACTOR");
		result.add("TELEPHONE");
		result.add("CONTACTADDRESS");
		result.add("BASESTADEVICETYPE");
		result.add("CHUANSHUSHEBEI");
		result.add("MAINDEVICEMODEL");
		result.add("ROOM");
		result.add("ZHUANXIANDENGJI");
		result.add("DEVICERELATEDPROJECT");
		result.add("REMARK");
		result.add("CUID");
		
		return result;
	}
	

	
	public static void dealDPGridMeta(GridMeta meta){
		if(meta ==null)
			return;
		List<GridColumn> columns= new ArrayList<GridColumn>();
		GridColumn col1 = new GridColumn();
		col1.setDataIndex("RELATED_DISTRICT_CUID");
		col1.setHeader("所属区域");
		columns.add(col1);

		GridColumn col2 = new GridColumn();
		col2.setDataIndex("LABEL_CN");
		col2.setHeader("名称");
		columns.add(col2);
		
		GridColumn col40 = new GridColumn();
		col40.setDataIndex("RELATED_TEMPLATE_NAME");
		col40.setHeader("模板名称");
		columns.add(col40);
		
		GridColumn col3 = new GridColumn();
		col3.setDataIndex("OWNERSHIP");
		col3.setHeader("产权归属");
		columns.add(col3);

		GridColumn col4 = new GridColumn();
		col4.setDataIndex("PURPOSE");
		col4.setHeader("用途");
		columns.add(col4);
		
		GridColumn col5 = new GridColumn();
		col5.setDataIndex("FIBERDP_NO");
		col5.setHeader("编号");
		columns.add(col5);
		meta.setColumns(columns);

		GridColumn col6 = new GridColumn();
		col6.setDataIndex("ACCESS_SCENE");
		col6.setHeader("集客接入场景");
		columns.add(col6);
		
		GridColumn col7 = new GridColumn();
		col7.setDataIndex("LONGITUDE");
		col7.setHeader("经度");
		columns.add(col7);

		GridColumn col8 = new GridColumn();
		col8.setDataIndex("LATITUDE");
		col8.setHeader("纬度");
		columns.add(col8);
		
		GridColumn col9 = new GridColumn();
		col9.setDataIndex("MODEL");
		col9.setHeader("型号");
		columns.add(col9);
		
		GridColumn col10 = new GridColumn();
		col10.setDataIndex("LAND_HEIGHT");
		col10.setHeader("距地面高度(M)");
		columns.add(col10);

		GridColumn col11 = new GridColumn();
		col11.setDataIndex("LENGTH");
		col11.setHeader("长度(M)");
		columns.add(col11);
		
		GridColumn col12 = new GridColumn();
		col12.setDataIndex("COL_COUNT");
		col12.setHeader("列数");
		columns.add(col12);

		GridColumn col13 = new GridColumn();
		col13.setDataIndex("COL_ROW_COUNT");
		col13.setHeader("每列行数");
		columns.add(col13);
		
		GridColumn col14 = new GridColumn();
		col14.setDataIndex("TIER_COL_COUNT");
		col14.setHeader("每小排列数");
		columns.add(col14);

		GridColumn col15 = new GridColumn();
		col15.setDataIndex("TIER_ROW_COUNT");
		col15.setHeader("每小排行数");
		columns.add(col15);
		
		GridColumn col16 = new GridColumn();
		col16.setDataIndex("DESIGN_CAPACITY");
		col16.setHeader("设计容量(芯)");
		columns.add(col16);

		GridColumn col17 = new GridColumn();
		col17.setDataIndex("USED_CAPACITY");
		col17.setHeader("使用容量(芯)");
		columns.add(col17);
		
		GridColumn col18 = new GridColumn();
		col18.setDataIndex("INSTALL_CAPACITY");
		col18.setHeader("安装容量(芯)");
		columns.add(col18);

		GridColumn col19 = new GridColumn();
		col19.setDataIndex("FREE_CAPACITY");
		col19.setHeader("空闲容量(芯)");
		columns.add(col19);
		
		GridColumn col20 = new GridColumn();
		col20.setDataIndex("LABEL_DEV");
		col20.setHeader("设备标识");
		columns.add(col20);

		GridColumn col21 = new GridColumn();
		col21.setDataIndex("SEQNO");
		col21.setHeader("设备序列号");
		columns.add(col21);
		
		GridColumn col22 = new GridColumn();
		col22.setDataIndex("RELATED_VENDOR_CUID");
		col22.setHeader("设备供应商");
		columns.add(col22);

		GridColumn col23 = new GridColumn();
		col23.setDataIndex("SPECIAL_LABEL");
		col23.setHeader("厂商特征值");
		columns.add(col23);
		
		GridColumn col24 = new GridColumn();
		col24.setDataIndex("RES_OWNER");
		col24.setHeader("所有权人");
		columns.add(col24);

		GridColumn col25 = new GridColumn();
		col25.setDataIndex("USERNAME");
		col25.setHeader("使用单位");
		columns.add(col25);
		
		GridColumn col26 = new GridColumn();
		col26.setDataIndex("MAINT_DEP");
		col26.setHeader("维护单位");
		columns.add(col26);

		GridColumn col27 = new GridColumn();
		col27.setDataIndex("LOCATION");
		col27.setHeader("地址");
		columns.add(col27);

		GridColumn col28 = new GridColumn();
		col28.setDataIndex("PRESERVER");
		col28.setHeader("维护人");
		columns.add(col28);
		

		GridColumn col29 = new GridColumn();
		col29.setDataIndex("PRESERVER_PHONE");
		col29.setHeader("维护人联系电话");
		columns.add(col29);
		

		GridColumn col30 = new GridColumn();
		col30.setDataIndex("MAINT_MODE");
		col30.setHeader("维护方式");
		columns.add(col30);

		GridColumn col31 = new GridColumn();
		col31.setDataIndex("SETUP_TIME");
		col31.setHeader("入网时间");
		columns.add(col31);

		GridColumn col32 = new GridColumn();
		col32.setDataIndex("CHECK_DATE");
		col32.setHeader("核查日期");
		columns.add(col32);

		GridColumn col33 = new GridColumn();
		col33.setDataIndex("SERVICER");
		col33.setHeader("巡检人");
		columns.add(col33);

		GridColumn col34 = new GridColumn();
		col34.setDataIndex("PRESERVER_ADDR");
		col34.setHeader("维护人通信地址");
		columns.add(col34);

		GridColumn col35 = new GridColumn();
		col35.setDataIndex("IS_USAGE_STATE");
		col35.setHeader("是否正使用");
		columns.add(col35);

		GridColumn col36 = new GridColumn();
		col36.setDataIndex("USERNAME");
		col36.setHeader("建设单位");
		columns.add(col36);

		GridColumn col37 = new GridColumn();
		col37.setDataIndex("CREATTIME");
		col37.setHeader("竣工日期");
		columns.add(col37);

		GridColumn col38 = new GridColumn();
		col38.setDataIndex("REMARK");
		col38.setHeader("备注");
		columns.add(col38);

		GridColumn col39 = new GridColumn();
		col39.setDataIndex("CUID");
		col39.setHeader("数据库主键");
		columns.add(col39);
		
		GridColumn col41 = new GridColumn();
		col41.setDataIndex("IS_YJR");
		col41.setHeader("是否预覆盖接入点");
		columns.add(col41);
		
		GridColumn col42 = new GridColumn();
		col42.setDataIndex("BOSS_CODE");
		col42.setHeader("BOSS编码");
		columns.add(col42);
		
		
		
		meta.setColumns(columns);

	}
	

	
	public static List<String> getDPColList(){
		List<String> result = new ArrayList<String>();
		result.add("RELATED_DISTRICT_CUID");
		result.add("LABEL_CN");
		result.add("RELATED_TEMPLATE_NAME");
		result.add("OWNERSHIP");
		result.add("PURPOSE");
		result.add("FIBERDP_NO");
		result.add("ACCESS_SCENE");
		result.add("LONGITUDE");
		result.add("LATITUDE");
		result.add("MODEL");
		result.add("LAND_HEIGHT");
		result.add("LENGTH");
		result.add("COL_COUNT");
		result.add("COL_ROW_COUNT");
		result.add("TIER_COL_COUNT");
		result.add("TIER_ROW_COUNT");
		result.add("DESIGN_CAPACITY");
		result.add("USED_CAPACITY");
		result.add("INSTALL_CAPACITY");
		result.add("FREE_CAPACITY");
		result.add("LABEL_DEV");
		result.add("SEQNO");
		result.add("RELATED_VENDOR_CUID");
		result.add("SPECIAL_LABEL");
		result.add("RES_OWNER");
		result.add("USERNAME");
		result.add("MAINT_DEP");
		result.add("LOCATION");
		result.add("PRESERVER");
		result.add("PRESERVER_PHONE");
		result.add("MAINT_MODE");
		result.add("SETUP_TIME");
		result.add("CHECK_DATE");
		result.add("SERVICER");
		result.add("PRESERVER_ADDR");
		result.add("IS_USAGE_STATE");
		result.add("USERNAME");
		result.add("CREATTIME");
		result.add("BOSS_CODE");
		result.add("IS_YJR");
		result.add("REMARK");
		result.add("CUID");
		
		return result;
	}
	
	

	
	public static void dealCabGridMeta(GridMeta meta){
		if(meta ==null)
			return;
		List<GridColumn> columns= new ArrayList<GridColumn>();
		GridColumn col1 = new GridColumn();
		col1.setDataIndex("RELATED_DISTRICT_CUID");
		col1.setHeader("所属区域");
		columns.add(col1);

		GridColumn col2 = new GridColumn();
		col2.setDataIndex("LABEL_CN");
		col2.setHeader("名称");
		columns.add(col2);
		
		GridColumn col3 = new GridColumn();
		col3.setDataIndex("RELATED_TEMPLATE_NAME");
		col3.setHeader("模板名称");
		columns.add(col3);

		GridColumn col4 = new GridColumn();
		col4.setDataIndex("ACCESS_SCENE");
		col4.setHeader("集客接入场景");
		columns.add(col4);
		
		GridColumn col5 = new GridColumn();
		col5.setDataIndex("FIBERCAB_NO");
		col5.setHeader("编号");
		columns.add(col5);
		meta.setColumns(columns);

		GridColumn col6 = new GridColumn();
		col6.setDataIndex("OWNERSHIP");
		col6.setHeader("产权归属");
		columns.add(col6);
		
		GridColumn col9 = new GridColumn();
		col9.setDataIndex("PURPOSE");
		col9.setHeader("用途");
		columns.add(col9);
		
		GridColumn col7 = new GridColumn();
		col7.setDataIndex("LONGITUDE");
		col7.setHeader("经度");
		columns.add(col7);

		GridColumn col8 = new GridColumn();
		col8.setDataIndex("LATITUDE");
		col8.setHeader("纬度");
		columns.add(col8);
		
		GridColumn col10 = new GridColumn();
		col10.setDataIndex("MODEL");
		col10.setHeader("型号");
		columns.add(col10);

		GridColumn col16 = new GridColumn();
		col16.setDataIndex("DESIGN_CAPACITY");
		col16.setHeader("设计容量(芯)");
		columns.add(col16);

		GridColumn col17 = new GridColumn();
		col17.setDataIndex("USED_CAPACITY");
		col17.setHeader("使用容量(芯)");
		columns.add(col17);
		
		GridColumn col18 = new GridColumn();
		col18.setDataIndex("INSTALL_CAPACITY");
		col18.setHeader("安装容量(芯)");
		columns.add(col18);

		GridColumn col19 = new GridColumn();
		col19.setDataIndex("FREE_CAPACITY");
		col19.setHeader("空闲容量(芯)");
		columns.add(col19);
		
		GridColumn col11 = new GridColumn();
		col11.setDataIndex("FACE_COUNT");
		col11.setHeader("面数");
		columns.add(col11);
		
		GridColumn col12 = new GridColumn();
		col12.setDataIndex("FACE_COL_COUNT");
		col12.setHeader("每面列数");
		columns.add(col12);

		GridColumn col13 = new GridColumn();
		col13.setDataIndex("TIER_PORT_COUNT");
		col13.setHeader("每排行数");
		columns.add(col13);
		
		GridColumn col14 = new GridColumn();
		col14.setDataIndex("LABEL_DEV");
		col14.setHeader("设备标识");
		columns.add(col14);

		GridColumn col15 = new GridColumn();
		col15.setDataIndex("SEQNO");
		col15.setHeader("设备序列号");
		columns.add(col15);
		
		
		GridColumn col20 = new GridColumn();
		col20.setDataIndex("RELATED_VENDOR_CUID");
		col20.setHeader("设备供应商");
		columns.add(col20);

		GridColumn col21 = new GridColumn();
		col21.setDataIndex("SPECIAL_LABEL");
		col21.setHeader("厂商特征值");
		columns.add(col21);
		
		GridColumn col22 = new GridColumn();
		col22.setDataIndex("RES_OWNER");
		col22.setHeader("所有权人");
		columns.add(col22);

		GridColumn col23 = new GridColumn();
		col23.setDataIndex("USERNAME");
		col23.setHeader("使用单位");
		columns.add(col23);
		
		GridColumn col24 = new GridColumn();
		col24.setDataIndex("MAINT_DEP");
		col24.setHeader("维护单位");
		columns.add(col24);

		GridColumn col25 = new GridColumn();
		col25.setDataIndex("LOCATION");
		col25.setHeader("地址");
		columns.add(col25);
		
		GridColumn col26 = new GridColumn();
		col26.setDataIndex("PRESERVER");
		col26.setHeader("维护人");
		columns.add(col26);

		GridColumn col27 = new GridColumn();
		col27.setDataIndex("PRESERVER_PHONE");
		col27.setHeader("维护人联系电话");
		columns.add(col27);

		GridColumn col28 = new GridColumn();
		col28.setDataIndex("MAINT_MODE");
		col28.setHeader("维护方式");
		columns.add(col28);
		

		GridColumn col29 = new GridColumn();
		col29.setDataIndex("SETUP_TIME");
		col29.setHeader("入网时间");
		columns.add(col29);
		

		GridColumn col30 = new GridColumn();
		col30.setDataIndex("CHECK_DATE");
		col30.setHeader("核查日期");
		columns.add(col30);

		GridColumn col31 = new GridColumn();
		col31.setDataIndex("SERVICER");
		col31.setHeader("巡检人");
		columns.add(col31);

		GridColumn col32 = new GridColumn();
		col32.setDataIndex("PRESERVER_ADDR");
		col32.setHeader("维护人通信地址");
		columns.add(col32);

		GridColumn col33 = new GridColumn();
		col33.setDataIndex("IS_USAGE_STATE");
		col33.setHeader("是否正使用");
		columns.add(col33);

		GridColumn col34 = new GridColumn();
		col34.setDataIndex("USERNAME");
		col34.setHeader("建设单位");
		columns.add(col34);

		GridColumn col35 = new GridColumn();
		col35.setDataIndex("CREATTIME");
		col35.setHeader("建设日期");
		columns.add(col35);

		GridColumn col36 = new GridColumn();
		col36.setDataIndex("REMARK");
		col36.setHeader("备注");
		columns.add(col36);
		
		GridColumn col39 = new GridColumn();
		col39.setDataIndex("CUID");
		col39.setHeader("数据库主键");
		columns.add(col39);

		GridColumn col40 = new GridColumn();
		col40.setDataIndex("IS_YJR");
		col40.setHeader("是否预覆盖接入点");
		columns.add(col40);

		GridColumn col42 = new GridColumn();
		col42.setDataIndex("BOSS_CODE");
		col42.setHeader("BOSS编码");
		columns.add(col42);
		
		meta.setColumns(columns);

	}

	
	public static List<String> getCabColList(){
		List<String> result = new ArrayList<String>();
		result.add("RELATED_DISTRICT_CUID");
		result.add("LABEL_CN");
		result.add("RELATED_TEMPLATE_NAME");
		result.add("ACCESS_SCENE");
		result.add("FIBERCAB_NO");
		result.add("OWNERSHIP");
		result.add("PURPOSE");
		result.add("LONGITUDE");
		result.add("LATITUDE");
		result.add("MODEL");
		result.add("DESIGN_CAPACITY");
		result.add("USED_CAPACITY");
		result.add("INSTALL_CAPACITY");
		result.add("FREE_CAPACITY");
		result.add("FACE_COUNT");
		result.add("FACE_COL_COUNT");
		result.add("TIER_PORT_COUNT");
		result.add("LABEL_DEV");
		result.add("SEQNO");
		result.add("RELATED_VENDOR_CUID");
		result.add("SPECIAL_LABEL");
		result.add("RES_OWNER");
		result.add("USERNAME");
		result.add("MAINT_DEP");
		result.add("LOCATION");
		result.add("PRESERVER");
		result.add("PRESERVER_PHONE");
		result.add("MAINT_MODE");
		result.add("SETUP_TIME");
		result.add("CHECK_DATE");
		result.add("SERVICER");
		result.add("PRESERVER_ADDR");
		result.add("IS_USAGE_STATE");
		result.add("USERNAME");
		result.add("CREATTIME");
		result.add("BOSS_CODE");
		result.add("IS_YJR");
		result.add("REMARK");
		result.add("CUID");
		return result;
	}
	
	

	public static void dealJointBoxGridMeta(GridMeta meta){
		if(meta ==null)
			return;
		List<GridColumn> columns= new ArrayList<GridColumn>();
		GridColumn col1 = new GridColumn();
		col1.setDataIndex("RELATED_DISTRICT_CUID");
		col1.setHeader("所属区域");
		columns.add(col1);

		GridColumn col2 = new GridColumn();
		col2.setDataIndex("LABEL_CN");
		col2.setHeader("名称");
		columns.add(col2);
		
		GridColumn col3 = new GridColumn();
		col3.setDataIndex("ACCESS_SCENE");
		col3.setHeader("集客接入场景");
		columns.add(col3);

		GridColumn col4 = new GridColumn();
		col4.setDataIndex("JUNCTION_TYPE");
		col4.setHeader("接头形式");
		columns.add(col4);
		
		GridColumn col5 = new GridColumn();
		col5.setDataIndex("CONNECT_TYPE");
		col5.setHeader("熔接方式");
		columns.add(col5);
		meta.setColumns(columns);

		GridColumn col6 = new GridColumn();
		col6.setDataIndex("SETUP_TIME");
		col6.setHeader("投产时间");
		columns.add(col6);
		
		GridColumn col9 = new GridColumn();
		col9.setDataIndex("USERNAME");
		col9.setHeader("建设单位");
		columns.add(col9);
		
		GridColumn col7 = new GridColumn();
		col7.setDataIndex("LOCATION");
		col7.setHeader("位置描述");
		columns.add(col7);

		GridColumn col8 = new GridColumn();
		col8.setDataIndex("");
		col8.setHeader("所在设施类型");
		columns.add(col8);
		
		GridColumn col10 = new GridColumn();
		col10.setDataIndex("");
		col10.setHeader("所在设施名称");
		columns.add(col10);

		GridColumn col16 = new GridColumn();
		col16.setDataIndex("MODEL");
		col16.setHeader("型号");
		columns.add(col16);

		GridColumn col17 = new GridColumn();
		col17.setDataIndex("RELATED_VENDOR_CUID");
		col17.setHeader("生产厂家");
		columns.add(col17);
		
		GridColumn col19 = new GridColumn();
		col19.setDataIndex("OWNERSHIP");
		col19.setHeader("产权归属");
		columns.add(col19);
		
		GridColumn col11 = new GridColumn();
		col11.setDataIndex("LONGITUDE");
		col11.setHeader("经度");
		columns.add(col11);
		
		GridColumn col13 = new GridColumn();
		col13.setDataIndex("LATITUDE");
		col13.setHeader("纬度");
		columns.add(col13);

		GridColumn col15 = new GridColumn();
		col15.setDataIndex("KIND");
		col15.setHeader("光终端盒类型");
		columns.add(col15);

		GridColumn col21 = new GridColumn();
		col21.setDataIndex("BUSHING");
		col21.setHeader("套管类型");
		columns.add(col21);
		
		GridColumn col22 = new GridColumn();
		col22.setDataIndex("DESIGN_CAPACITY");
		col22.setHeader("设计容量(芯)");
		columns.add(col22);

		GridColumn col23 = new GridColumn();
		col23.setDataIndex("USED_CAPACITY");
		col23.setHeader("使用容量(芯)");
		columns.add(col23);
		
		GridColumn col24 = new GridColumn();
		col24.setDataIndex("INSTALL_CAPACITY");
		col24.setHeader("安装容量(芯)");
		columns.add(col24);

		GridColumn col25 = new GridColumn();
		col25.setDataIndex("FREE_CAPACITY");
		col25.setHeader("空闲容量(芯)");
		columns.add(col25);
		
		GridColumn col26 = new GridColumn();
		col26.setDataIndex("CAPACITY");
		col26.setHeader("接头盒容量(芯)");
		columns.add(col26);

		GridColumn col27 = new GridColumn();
		col27.setDataIndex("LABEL_DEV");
		col27.setHeader("设备标识");
		columns.add(col27);

		GridColumn col28 = new GridColumn();
		col28.setDataIndex("SEQNO");
		col28.setHeader("设备序列号");
		columns.add(col28);
		

		GridColumn col29 = new GridColumn();
		col29.setDataIndex("SPECIAL_LABEL");
		col29.setHeader("厂商特征值");
		columns.add(col29);
		

		GridColumn col30 = new GridColumn();
		col30.setDataIndex("RES_OWNER");
		col30.setHeader("所有权人");
		columns.add(col30);

		GridColumn col31 = new GridColumn();
		col31.setDataIndex("MAINT_DEP");
		col31.setHeader("维护单位");
		columns.add(col31);

		GridColumn col32 = new GridColumn();
		col32.setDataIndex("PRESERVER");
		col32.setHeader("维护人");
		columns.add(col32);

		GridColumn col33 = new GridColumn();
		col33.setDataIndex("PRESERVER_PHONE");
		col33.setHeader("维护人联系电话");
		columns.add(col33);

		GridColumn col34 = new GridColumn();
		col34.setDataIndex("PRESERVER_ADDR");
		col34.setHeader("维护人通信地址");
		columns.add(col34);

		GridColumn col35 = new GridColumn();
		col35.setDataIndex("MAINT_MODE");
		col35.setHeader("维护方式");
		columns.add(col35);

		
		GridColumn col18 = new GridColumn();
		col18.setDataIndex("IS_USAGE_STATE");
		col18.setHeader("是否正使用");
		columns.add(col18);

		GridColumn col36 = new GridColumn();
		col36.setDataIndex("CREATTIME");
		col36.setHeader("建设日期");
		columns.add(col36);
		
		GridColumn col14 = new GridColumn();
		col14.setDataIndex("REMARK");
		col14.setHeader("备注");
		columns.add(col14);
		
		
		GridColumn col20 = new GridColumn();
		col20.setDataIndex("RELATED_SITE_CUID");
		col20.setHeader("机房所属站点");
		columns.add(col20);
		
		GridColumn col12 = new GridColumn();
		col12.setDataIndex("IS_YJR");
		col12.setHeader("是否预覆盖接入点");
		columns.add(col12);
		
		GridColumn col37 = new GridColumn();
		col37.setDataIndex("BOSS_CODE");
		col37.setHeader("BOSS编码");
		columns.add(col37);

		
		GridColumn col38 = new GridColumn();
		col38.setDataIndex("VP_LABEL_CN");
		col38.setHeader("客户名称");
		columns.add(col38);

		GridColumn col39 = new GridColumn();
		col39.setDataIndex("CUID");
		col39.setHeader("数据库主键");
		columns.add(col39);
		
		meta.setColumns(columns);
	}
	
	public static List<String> getJointBoxColList() {
		List<String> result = new ArrayList<String>();

		result.add("RELATED_DISTRICT_CUID");
		result.add("LABEL_CN");
		result.add("ACCESS_SCENE");
		result.add("JUNCTION_TYPE");
		result.add("CONNECT_TYPE");
		result.add("SETUP_TIME");
		result.add("USERNAME");
		result.add("LOCATION");
		result.add("");
		result.add("");
		result.add("MODEL");
		result.add("RELATED_VENDOR_CUID");
		result.add("OWNERSHIP");
		result.add("LONGITUDE");
		result.add("LATITUDE");
		result.add("KIND");
		result.add("BUSHING");
		result.add("DESIGN_CAPACITY");
		result.add("USED_CAPACITY");
		result.add("INSTALL_CAPACITY");
		result.add("FREE_CAPACITY");
		result.add("CAPACITY");
		result.add("LABEL_DEV");
		result.add("SEQNO");
		result.add("SPECIAL_LABEL");
		result.add("RES_OWNER");
		result.add("MAINT_DEP");
		result.add("PRESERVER");
		result.add("PRESERVER_PHONE");
		result.add("PRESERVER_ADDR");
		result.add("MAINT_MODE");
		result.add("IS_USAGE_STATE");
		result.add("CREATTIME");
		result.add("REMARK");
		result.add("RELATED_SITE_CUID");
		result.add("IS_YJR");
		result.add("BOSS_CODE");
		result.add("VP_LABEL_CN");
		result.add("CUID");

		return result;
	}
	
	
}
