package com.boco.irms.app.dm.action.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.db.DbConnManager;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.id.CUIDHexGenerator;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.db.DbType;
import com.boco.core.utils.db.SqlHelper;
import com.boco.irms.app.dm.action.multithread.ITaskThreadPool;
import com.boco.irms.app.dm.action.multithread.IThreadPoolTaskHandler;
import com.boco.irms.app.dm.action.multithread.ThreadPoolUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.dao.base.DaoHomeFactory;

/**
 * @description 光缆及承载长度统计
 * @author liuchao
 * @verison 
 * @date 2015年1月27日 下午5:53:51
 */
public class WireStatTask extends JobWorking {

	private String fromTime;
	private String toTime;
	
	@Override
	public void processJob() {
		initData();
		Date  firstDayOfCurrMon = TimeHelper.getFirstDayOfMonth(0,StatConst.taskBeginTime);
		Date firstDayOfNextMon = TimeHelper.getFirstDayOfMonth(1,StatConst.taskBeginTime);
		try {
			DbType dbType = WebDMUtils.getDbType();
			fromTime = SqlHelper.getDate(dbType, new java.sql.Date(firstDayOfCurrMon.getTime()));
			toTime = SqlHelper.getDate(dbType, new java.sql.Date(firstDayOfNextMon.getTime()));
		} catch (Exception e1) {
			LogHome.getLog().error(e1.getMessage(),e1);
		}

		//调用增加任务的接口
		ThreadPoolUtils.synExecuteThreadPool(new IThreadPoolTaskHandler<List>() {

			public void addTasks(final ITaskThreadPool<List> taskThreadPool) {
				try {
					String district = SysProperty.getInstance().getValue("district","DISTRICT-00001");
					String sql = "SELECT CUID FROM DISTRICT WHERE CUID LIKE '" + district + "%'";
//					String sqlString="SELECT CUID FROM DISTRICT where cuid like 'DISTRICT-00001-00008-00010%'";
//					String sqlString="SELECT CUID FROM DISTRICT where cuid like 'DISTRICT-00001-00008-00007-00002%'";
					List<Map<String, Object>> list = getListByParams("IbatisSdeDAO", sql);
					if(list != null && list.size() > 0){
						for(Map map:list){
							String cuid = (String) map.get("CUID");
							List task = new ArrayList();
							task.add(cuid);
							taskThreadPool.addTask(task);
						}
					}
				} catch (Exception e) {
					LogHome.getLog().error(e, e);
				}
			}

			public void doTask(List task) {
				try {
					if(task != null && task.size() > 0){		
						String districtCuid = (String) task.get(0);
						if(isStatByDis(districtCuid)){
							doStat(districtCuid);
						}
					}									
				} catch (Exception e) {
					LogHome.getLog().error(e.getMessage(), e);
				}
				
			}
			
		});
		
		Date date = new Date();
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = d.format(date);
		LogHome.getLog().info("按区域核查结束,当前时间是      "+timeStr);
	}
	
	/**
	 * 初始化统计数据
	 */
	private void initData() {
		Date fireDate = new Date();
		StatConst.taskBeginTime = fireDate;
		Calendar curTime = Calendar.getInstance();
		curTime.setTime(fireDate);
		StatConst.checkMonth = curTime.get(Calendar.MONTH)+1;
		//删除对应的月份数据
		String sqlDelWireFault = "DELETE FROM WIRE_SEG_FAULT WHERE CHECK_MONTH = " + StatConst.checkMonth;
		
		String sqlDelWireRes = "DELETE FROM WIRE_LENGTH_STAT WHERE CHECK_MONTH = " + StatConst.checkMonth;
		
		String sqlDelDuctFault = "DELETE FROM DUCTLINE_SEG_FAULT WHERE CHECK_MONTH = " + StatConst.checkMonth;
		
		String sqlDelDuctRes = "DELETE FROM DUCTLINE_LENGTH_STAT WHERE CHECK_MONTH = " + StatConst.checkMonth;
		
		String sqlDelWireDetail = "";
		
		String sqlDelDuctLineDetail = "";
		
		//详细信息保留两个月的数据
		if(StatConst.checkMonth == 1){
			sqlDelWireDetail = "DELETE FROM WIRE_DETAIL WHERE CHECK_MONTH < 12";	
			sqlDelDuctLineDetail = "DELETE FROM DUCTLINE_DETAIL WHERE CHECK_MONTH < 12";
		}else{
			sqlDelWireDetail = "DELETE FROM WIRE_DETAIL WHERE CHECK_MONTH != " + (StatConst.checkMonth-1);
			sqlDelDuctLineDetail = "DELETE FROM DUCTLINE_DETAIL WHERE CHECK_MONTH != "+(StatConst.checkMonth-1);
		}
		try
		{
			if(StatConst.isStatAddDelWireOfHenan){
				String sql = "DELETE FROM DEL_WIRE_LENGTH_STAT WHERE CHECK_MONTH = " + StatConst.checkMonth;
				deleteObject("IbatisSdeDAO", sql);
			}
			deleteObject("IbatisSdeDAO", sqlDelWireFault);
			deleteObject("IbatisSdeDAO", sqlDelWireRes);
			deleteObject("IbatisSdeDAO", sqlDelDuctFault);
			deleteObject("IbatisSdeDAO", sqlDelDuctRes);
			deleteObject("IbatisSdeDAO", sqlDelWireDetail);
			deleteObject("IbatisSdeDAO", sqlDelDuctLineDetail);
		}
		catch(Exception e)
		{
			LogHome.getLog().error("删除统计数据表出错", e);
		}
	}
	
	/**
	 * 只有是县级或是市级的区域才进行统计
	 * @param districtCuid
	 * @return
	 */
	public static boolean isStatByDis(String districtCuid){
		if(districtCuid != null && districtCuid.length() > 0){
			String[] strs = districtCuid.split("-");
			if(strs.length >= 4 && strs.length <= 5){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 统计光缆及承载长度
	 * @param districtCuid
	 * @throws Exception
	 */
	public  void doStat(String districtCuid) throws Exception{
		if(districtCuid != null && districtCuid.length() > 0){
			doWireStat(districtCuid);			
			doDuctlineStat(districtCuid);
		}
	}
	
	/**
	 * 统计光缆长度
	 * @param districtCuid
	 */
	private void doWireStat(String districtCuid) {
		
		//市级统计包含县级区域
		String wireSysSql = "SELECT LABEL_CN,CUID,SYSTEM_LEVEL FROM WIRE_SYSTEM WHERE RELATED_SPACE_CUID like '" + districtCuid + "%'";
		List<Map<String, Object>> wireSystems = getListByParams("IbatisTransDAO", wireSysSql);
		Map <String,Object> wireMap = initWireMap(districtCuid,"WIRE_LENGTH_STAT",1,1);//正常
		Map <String,Object> subWireMap = initWireMap(districtCuid,"WIRE_LENGTH_STAT",1,2);//代维
		List<Map<String, Object>> wireDetailList = new ArrayList<Map<String,Object>>();
		
		if(wireSystems != null && wireSystems.size() > 0){				
			for(Map wireSystem : wireSystems){
				String wireSystemCuid = (String) wireSystem.get(GenericDO.AttrName.cuid);
				String wireSegSql = "SELECT LABEL_CN,CUID,LAY_TYPE,LENGTH,FIBER_COUNT,ORIG_POINT_CUID,DEST_POINT_CUID FROM WIRE_SEG WHERE RELATED_SYSTEM_CUID = '" + wireSystemCuid + "'";
				List<Map<String, Object>> wireSegs = getListByParams("IbatisTransDAO", wireSegSql);
				String wireSystemLabelCn =(String) wireSystem.get(GenericDO.AttrName.labelCn);
				int systemLevel = StatHelper.getIntByString(wireSystem.get("SYSTEM_LEVEL"));
				this.doAnalyseWire(districtCuid, wireMap,subWireMap,wireDetailList,wireSystemCuid, wireSystemLabelCn, wireSegs, 1,systemLevel);					
			}								
		}
		
		doInsertObject(wireMap,"WIRE_LENGTH_STAT",true);
		doInsertObject(subWireMap,"WIRE_LENGTH_STAT",true);
		doInsertDetailObject(wireDetailList, "WIRE_DETAIL");
		
		//河南特殊处理，增加光缆增量及减量统计
		if(StatConst.isStatAddDelWireOfHenan){
			//需要加上时间条件，数据类型不同，暂时不加				
			String relatedSystemCuids = "SELECT RELATED_SYSTEM_CUID,SYSTEM_LABEL_CN FROM OPERATED_WIRE WHERE RELATED_SPACE_CUID = '" + 
					districtCuid + "' AND TYPE = 1 AND CREATE_TIME >= " + fromTime + " AND CREATE_TIME < " + toTime;
			List<Map<String, Object>> relatedSysCuids = getListByParams("IbatisTransDAO", relatedSystemCuids);
			
			if(relatedSysCuids != null && relatedSysCuids.size() > 0){					
				//2是代表新增光缆的统计
				Map <String,Object> wireMapHn = initWireMap(districtCuid,"WIRE_LENGTH_STAT",2,1);
				Map <String,Object> subWireMapHn = initWireMap(districtCuid,"WIRE_LENGTH_STAT",2,2);
				List<Map<String, Object>> wireDetailListHn = new ArrayList<Map<String,Object>>();
//					Map <String,Object>subWireDetailMap=initWireDetailMap(districtCuid,"WIRE_DUCTLINE_DETAIL",2);//代维
				for(Map relatedSysCuid : relatedSysCuids){
					String wireSystemCuid = (String) relatedSysCuid.get(WireSeg.AttrName.relatedSystemCuid);
					String systemSql = "SELECT CUID,SYSTEM_LEVEL FROM WIRE_SYSTEM WHERE CUID = '" + wireSystemCuid + "'";
					List<Map<String, Object>> systems = getListByParams("IbatisTransDAO", systemSql);
					int sysLevel = 0;
					if(systems != null && systems.size()>0){
						 sysLevel = StatHelper.getIntByString(systems.get(0).get("SYSTEM_LEVEL"));
					}else{
					}
					String wireSystemLabelCn=(String) relatedSysCuid.get("SYSTEM_LABEL_CN");
					String wireSegSql="SELECT LABEL_CN,CUID,LAY_TYPE,LENGTH,FIBER_COUNT,ORIG_POINT_CUID,DEST_POINT_CUID FROM WIRE_SEG " +
							"WHERE CUID IN(SELECT WIRE_SEG_CUID FROM OPERATED_WIRE WHERE RELATED_SYSTEM_CUID IN ('"+wireSystemCuid+"') AND TYPE=1 AND CREATE_TIME>="+fromTime+" AND CREATE_TIME<="+toTime+")";
					List<Map<String, Object>>wireSegs=getListByParams("IbatisTransDAO", wireSegSql);
					this.doAnalyseWire(districtCuid, wireMapHn,subWireMapHn,wireDetailListHn,wireSystemCuid, wireSystemLabelCn, wireSegs, 2,sysLevel);
				}
				doInsertObject(wireMapHn,"WIRE_LENGTH_STAT",true);
				doInsertObject(subWireMapHn,"WIRE_LENGTH_STAT",true);
				doInsertDetailObject(wireDetailListHn, "WIRE_DETAIL");
			}
			//删除的光缆的统计
			dealDelWire(districtCuid);				
		}
		
	}
	
	/**
	 * 统计承载设施长度
	 * @param districtCuid
	 */
	private void doDuctlineStat(String districtCuid) {
		//区域统计
		Map <String,Object>lineMap=initWireMap(districtCuid,"DUCTLINE_LENGTH_STAT",null,1);
		lineMap.put("isInsert", false);
		List<Map<String, Object>> ductLineDetailList=new ArrayList<Map<String,Object>>();
		Map <String,Object>subLineMap=initWireMap(districtCuid,"DUCTLINE_LENGTH_STAT",null,2);						
		doDuctLineStatByType(districtCuid,ductLineDetailList,DuctSystem.CLASS_NAME,DuctSeg.CLASS_NAME,lineMap,subLineMap);
		doDuctLineStatByType(districtCuid,ductLineDetailList,PolewaySystem.CLASS_NAME,PolewaySeg.CLASS_NAME,lineMap,subLineMap);
		doDuctLineStatByType(districtCuid,ductLineDetailList,StonewaySystem.CLASS_NAME,StonewaySeg.CLASS_NAME,lineMap,subLineMap);
		doDuctLineStatByType(districtCuid,ductLineDetailList,UpLine.CLASS_NAME,UpLineSeg.CLASS_NAME,lineMap,subLineMap);
		doDuctLineStatByType(districtCuid,ductLineDetailList,HangWall.CLASS_NAME,HangWallSeg.CLASS_NAME,lineMap,subLineMap);	
		if((Boolean) lineMap.get("isInsert")){
			lineMap.remove("isInsert");
			doInsertObject(lineMap,"DUCTLINE_LENGTH_STAT",false);
			doInsertObject(subLineMap,"DUCTLINE_LENGTH_STAT",false);	
			doInsertDetailObject(ductLineDetailList, "DUCTLINE_DETAIL");
		}
	}
	
	private void doDuctLineStatByType(String districtCuid,List<Map<String, Object>> ductLineDetailList,String sytemClassName,String segName,Map <String,Object>lineMap,Map <String,Object>subLineMap) {
		String LineSysSql="SELECT LABEL_CN,CUID,SYSTEM_LEVEL FROM "+sytemClassName+" WHERE RELATED_SPACE_CUID like'"+districtCuid+"%'";
		List<Map<String, Object>>lineSystems=getListByParams("IbatisTransDAO", LineSysSql);
		if(lineSystems!=null&&lineSystems.size()>0){
			lineMap.put("isInsert",true);
			for(Map lineSystem:lineSystems){					
				String lineSystemCuid=(String) lineSystem.get(GenericDO.AttrName.cuid);
				String lineSegSql="";
				if("UP_LINE_SEG".equals(segName)||"HANG_WALL_SEG".equals(segName)){
					 lineSegSql="SELECT LABEL_CN,CUID,ORIG_POINT_CUID,DEST_POINT_CUID,RELATED_SYSTEM_CUID AS RELATED_BRANCH FROM "+segName+" WHERE RELATED_SYSTEM_CUID='"+lineSystemCuid+"'";					   
				}else{
					 lineSegSql="SELECT LABEL_CN,CUID,ORIG_POINT_CUID,DEST_POINT_CUID,RELATED_BRANCH_CUID AS RELATED_BRANCH FROM "+segName+" WHERE RELATED_SYSTEM_CUID='"+lineSystemCuid+"' ORDER BY RELATED_BRANCH_CUID,INDEX_IN_BRANCH";
				}
				List<Map<String, Object>>lineSegs=getListByParams("IbatisTransDAO", lineSegSql);
				String systemLabelCn=(String) lineSystem.get(GenericDO.AttrName.labelCn);
				int systemLevel = StatHelper.getIntByString(lineSystem.get("SYSTEM_LEVEL"));		
				this.doAnalyseLineSegs(districtCuid,ductLineDetailList, lineMap, subLineMap,systemLabelCn, lineSegs,systemLevel);
			}
		}
	}
	
	private void dealDelWire(String districtCuid) {
		//删除的光缆段做特殊处理  中的人也需要加上时间限制
		String deleteWireSql="SELECT LENGTH,WIRE_SEG_CUID FROM OPERATED_WIRE WHERE RELATED_SPACE_CUID ='"+districtCuid+"' AND TYPE=2 AND CREATE_TIME>="+fromTime+" AND CREATE_TIME<="+toTime;
		List<Map<String, Object>>deleteWires=getListByParams("IbatisTransDAO", deleteWireSql);
		if(deleteWires!=null&&deleteWires.size()>0){
			Map<String, Object> dataMap=new HashMap<String, Object>();				
			double length=0.0;
			for(Map<String, Object>map:deleteWires){
				length=length+StatHelper.getDoubleByObj(map.get("LENGTH"));	
			}
			String cuid = CUIDHexGenerator.getInstance().generate("DEL_WIRE_LENGTH_STAT");
			dataMap.put("CUID", cuid);
			dataMap.put("RELATED_DISTRICT_CUID", districtCuid);
			dataMap.put("LENGTH", length);
			this.format("LENGTH", dataMap);
			dataMap.put("START_TIME", StatConst.taskBeginTime);
			dataMap.put("FINISH_TIME", StatConst.taskBeginTime);
			Date date=new Date();
			dataMap.put("CREATE_TIME", date);
			dataMap.put("CHECK_MONTH", StatConst.checkMonth);
			
			insertObject("IbatisSdeDAO", "DEL_WIRE_LENGTH_STAT", dataMap);
		}
	}
	
	/**
	 * 数据库批量插入详细信息
	 * */
	private void doInsertDetailObject(List<Map<String, Object>> wireDetailList,String className) {			
		for (Map<String, Object> map  : wireDetailList) {
			if("WIRE_DETAIL".equals(className)){
				format("REMAIN",map);
			}
			format("LENGTH",map);				
		}
		insertObjectBatch("IbatisSdeDAO", className, wireDetailList);
	}
	
	private void doInsertObject(Map<String, Object> dataMap,String className,boolean isWire) {			
		double trunkAllSeg=StatHelper.getDoubleByObj(dataMap.get("TRUNK_DUCT_SEG"))+StatHelper.getDoubleByObj(dataMap.get("TRUNK_POLEWAY_SEG"))
				+StatHelper.getDoubleByObj(dataMap.get("TRUNK_STONEWAY_SEG"))+StatHelper.getDoubleByObj(dataMap.get("TRUNK_HANG_WALL_SEG"))+StatHelper.getDoubleByObj(dataMap.get("TRUNK_UP_LINE_SEG"));
		double localAllSeg=StatHelper.getDoubleByObj(dataMap.get("LOCAL_DUCT_SEG"))+StatHelper.getDoubleByObj(dataMap.get("LOCAL_POLEWAY_SEG"))
				+StatHelper.getDoubleByObj(dataMap.get("LOCAL_STONEWAY_SEG"))+StatHelper.getDoubleByObj(dataMap.get("LOCAL_UP_LINE_SEG"))+StatHelper.getDoubleByObj(dataMap.get("LOCAL_HANG_WALL_SEG"));
		double allSeg=trunkAllSeg+localAllSeg;			
		dataMap.put("TRUNK_ALL_SEG", trunkAllSeg);
		dataMap.put("LOCAL_ALL_SEG", localAllSeg);
					
		//干线
		format("TRUNK_DUCT_SEG",dataMap);
		format("TRUNK_POLEWAY_SEG",dataMap);
		format("TRUNK_STONEWAY_SEG",dataMap);
		format("TRUNK_UP_LINE_SEG",dataMap);
		format("TRUNK_HANG_WALL_SEG",dataMap);
		format("TRUNK_ALL_SEG",dataMap);			
		//本地
		format("LOCAL_DUCT_SEG",dataMap);
		format("LOCAL_POLEWAY_SEG",dataMap);
		format("LOCAL_STONEWAY_SEG",dataMap);
		format("LOCAL_UP_LINE_SEG",dataMap);
		format("LOCAL_HANG_WALL_SEG",dataMap);
		format("LOCAL_ALL_SEG",dataMap);	
		//总计

		if(isWire){			
			format("TRUNK_WIRE_REMAIN",dataMap);
			format("LOCAL_WIRE_REMAIN",dataMap);
			dataMap.put("ALL_REMAIN", (StatHelper.getDoubleByObj(dataMap.get("TRUNK_WIRE_REMAIN"))+StatHelper.getDoubleByObj(dataMap.get("LOCAL_WIRE_REMAIN"))));

			dataMap.put("ALL_SEG", allSeg);	
			format("ALL_SEG",dataMap);
			//光缆总长度=干程总长度+预留总长度
			dataMap.put("ALL_LENGTH",(StatHelper.getDoubleByObj(dataMap.get("ALL_SEG"))+StatHelper.getDoubleByObj(dataMap.get("ALL_REMAIN"))));					
		}else{
			//承载总长度=干程总长度
			dataMap.put("ALL_LENGTH", allSeg);
			format("ALL_LENGTH",dataMap);
		}						
		Date date=new Date();
		dataMap.put("CREATE_TIME", date);
		try {
			insertObject("IbatisSdeDAO", className, dataMap);	
		} catch (Exception e) {
			LogHome.getLog().error(e.getMessage(),e);
		}
	}		
	
	private Map<String, Object> initWireMap(String districtCuid,String className,Integer type,int resultType) {
		Map<String, Object> dataMap=new HashMap<String, Object>();
		String cuid = CUIDHexGenerator.getInstance().generate(className);
		dataMap.put("CUID", cuid);
		dataMap.put("RELATED_DISTRICT_CUID", districtCuid);
		//主干网
		dataMap.put("TRUNK_DUCT_SEG", 0.0d);
		dataMap.put("TRUNK_POLEWAY_SEG", 0.0d);
		dataMap.put("TRUNK_STONEWAY_SEG", 0.0d);
		dataMap.put("TRUNK_UP_LINE_SEG", 0.0d);
		dataMap.put("TRUNK_HANG_WALL_SEG", 0.0d);
		dataMap.put("TRUNK_ALL_SEG", 0.0d);
		//本地网
		dataMap.put("LOCAL_DUCT_SEG", 0.0d);
		dataMap.put("LOCAL_POLEWAY_SEG", 0.0d);
		dataMap.put("LOCAL_STONEWAY_SEG", 0.0d);
		dataMap.put("LOCAL_UP_LINE_SEG", 0.0d);
		dataMap.put("LOCAL_HANG_WALL_SEG", 0.0d);
		dataMap.put("LOCAL_ALL_SEG", 0.0d);
							
		dataMap.put("ALL_LENGTH", 0.0d);			 
		if("WIRE_LENGTH_STAT".equals(className)){
			dataMap.put("ALL_SEG", 0.0d);	
			dataMap.put("TRUNK_WIRE_REMAIN", 0.0d);
			dataMap.put("LOCAL_WIRE_REMAIN", 0.0d);
			dataMap.put("ALL_REMAIN", 0.0d);
			dataMap.put("TYPE", type);
//				dataMap.put("RELATE_WIRE_SEG", "");
		}

		dataMap.put("FAULT_SEG_COUNT", 0);
		dataMap.put("RESULT_TYPE", resultType);
		dataMap.put("CHECK_MONTH", StatConst.checkMonth);
		return dataMap;
	}
	
	private void format(String key,Map<String, Object> dataMap) {
		double data = StatHelper.getDoubleByObj( dataMap.get(key));
		data = StatHelper.formatExt(data);
		dataMap.put(key, data);
	}
	
	private void formatExt(String key,Map<String, Object> dataMap) {
		double data = StatHelper.getDoubleByObj( dataMap.get(key));
		data = StatHelper.format(data);
		dataMap.put(key, data);
	} 
	
	private void doAnalyseWire(String districtCuid, Map<String, Object> wireMap, Map<String, Object> subWireMap,
			List<Map<String, Object>>wireDetailList,String wireSystemCuid,String wireSystemLabelCn,List<Map<String, Object>>wireSegs,int type,int systemLevel) {			
			if(wireSegs!=null&&wireSegs.size()>0){
				for(Map wireSeg:wireSegs){
					String wireSegCuid=(String) wireSeg.get(GenericDO.AttrName.cuid);
					//先分析是不是问题段，如果是问题段就不统计在长度表中
					String wireToDuctLineSql="SELECT DIS_POINT_CUID,END_POINT_CUID,LINE_SEG_CUID,LINE_SYSTEM_CUID,LINE_BRANCH_CUID AS LINE_BRANCH FROM WIRE_TO_DUCTLINE WHERE WIRE_SEG_CUID='"+wireSegCuid+"' ORDER BY LINE_BRANCH_CUID, INDEX_IN_ROUTE";											
					List<Map<String, Object>>wtdls=getListByParams("IbatisTransDAO", wireToDuctLineSql);
					
					String wireRemainSql="SELECT REMAIN_LENGTH,RELATED_WIRE_SEG_CUID,RELATED_LOCATION_CUID FROM WIRE_REMAIN WHERE RELATED_WIRE_SEG_CUID='"+wireSegCuid+"'";;
					List<Map<String, Object>>wireRems=getListByParams("IbatisTransDAO", wireRemainSql);
					List<Map<String, Object>>rebuildMap=null;
					if(type==2){
						String origPointCuid=(String) wireSeg.get(WireSeg.AttrName.origPointCuid);
						String destPointCuid=(String) wireSeg.get(WireSeg.AttrName.destPointCuid);
						String rebuildSql="SELECT WIRE_SEG_CUID FROM OPERATED_WIRE WHERE TYPE=2 AND ((ORIG_POINT_CUID='"+origPointCuid+"' AND DEST_POINT_CUID='"+destPointCuid+"') OR " +
								"(ORIG_POINT_CUID='"+destPointCuid+"' AND DEST_POINT_CUID='"+origPointCuid+"'))"+" AND CREATE_TIME>="+fromTime+" AND CREATE_TIME<"+toTime;;
						rebuildMap=getListByParams("IbatisTransDAO", rebuildSql);
					}
					String fiberSql="SELECT CUID,ORIG_POINT_CUID,DEST_POINT_CUID FROM FIBER WHERE RELATED_SEG_CUID='"+wireSegCuid+"'";
					List<Map<String, Object>>fiberMap=getListByParams("IbatisTransDAO", fiberSql);
					
					Map<String,List<Map<String, Object>>> paraMap=new HashMap<String,List<Map<String, Object>>>();
					
					paraMap.put(WireToDuctline.CLASS_NAME, wtdls);
					paraMap.put(WireRemain.CLASS_NAME, wireRems);
					paraMap.put(Fiber.CLASS_NAME, fiberMap);
					paraMap.put("REBUILD", rebuildMap);
					doAnalyseWireSeg(districtCuid, wireMap,wireDetailList,false,wireSystemLabelCn, type, wireSeg,paraMap,systemLevel);
					doAnalyseWireSeg(districtCuid, subWireMap,wireDetailList,true,wireSystemLabelCn, type, wireSeg,paraMap,systemLevel);
				}
			}
	}
	
	/**
	 * 分析承载段
	 * */		
	private void doAnalyseLineSegs(String districtCuid,List<Map<String, Object>> ductLineDetailList,Map<String, Object> lineMap,
			Map<String, Object> subLineMap, String systemLabelCn,List<Map<String, Object>>lineSegs,int systemLevel) {		
		//存放有问题段的map
		Map <String,Object>faultMap=new HashMap<String,Object>();
		String lineCuidString=(String) lineMap.get(GenericDO.AttrName.cuid);
		String subLineCuidString=(String) subLineMap.get(GenericDO.AttrName.cuid);
		//承载段描述
		String description="";
		//承载段描述起始点名称				
		String origName="";
		//同一个分支下的正常承载段长度
		double branchLength=0.0;
		//同一个分支下的代维承载段长度
		double subBranchLength=0.0;
		//所属分支
		String lineBranchCuid="";
		String tmpResType="";
		Boolean fault=true;
		for(int j = 0; j < lineSegs.size(); j++){
		    Map lineSeg=lineSegs.get(j);
			//问题描述集合
			Set <String>faultDesSet=new HashSet<String>();
			double length=getSegLengthByPoints(lineSeg,"ORIG_POINT_CUID","DEST_POINT_CUID");
							
			String lineSegCuid=(String) lineSeg.get(GenericDO.AttrName.cuid);
			String lineSegLabel=(String) lineSeg.get(DuctSeg.AttrName.labelCn);				
			//与数据库中的字段对应
			String className=GenericDO.parseClassNameFromCuid(lineSegCuid);
			String relatedClassName="";
			if(systemLevel == 1 || systemLevel==2){//主干网
				relatedClassName="TRUNK_"+className;
			}else if(systemLevel == 3 || systemLevel==4 || systemLevel == 5){//本地网
				relatedClassName="LOCAL_"+className;
			}else{
				continue;
			}
			
			Double existLen=StatHelper.getDoubleByObj( lineMap.get(relatedClassName));
			Double subExistLen=StatHelper.getDoubleByObj( subLineMap.get(relatedClassName));
			
			//对代维的特殊处理
			Double subLength=0.0;
			Integer wireCount = getLayCount(lineSegCuid);
			subLength = getSubLength(length, wireCount);
			fault=true;
			checkOutOfLength(length,className,districtCuid,faultDesSet,StatConst.isThresholdByDis);	
			if(faultDesSet.size()>0){
				Integer lineCount=(Integer) lineMap.get("FAULT_SEG_COUNT");
				lineCount++;
				lineMap.put("FAULT_SEG_COUNT", lineCount);
				
				Integer subLineCount=(Integer) subLineMap.get("FAULT_SEG_COUNT");
				subLineCount++;
				subLineMap.put("FAULT_SEG_COUNT", subLineCount);
				
				faultMap.put("RELATED_DUCTLINE_LENGTH_STAT",lineCuidString);
				this.setFaultSeg(districtCuid, systemLabelCn, faultMap, faultDesSet, lineSegCuid, lineSegLabel,"DUCTLINE_SEG_FAULT");
				
				faultMap.put("RELATED_DUCTLINE_LENGTH_STAT",subLineCuidString);
				this.setFaultSeg(districtCuid, systemLabelCn, faultMap, faultDesSet, lineSegCuid, lineSegLabel,"DUCTLINE_SEG_FAULT");
			}else{
				if(existLen==null){
					lineMap.put(relatedClassName, length);					
				}else{
					lineMap.put(relatedClassName, existLen+length);
				}					
				if(subExistLen==null){
					subLineMap.put(relatedClassName, subLength);
				}else{
					subLineMap.put(relatedClassName, subExistLen+subLength);
				}										
			}	
			//县级才统计详细信息
			if(districtCuid.length()==32){
				//承载段起始点资源名称									
				String relatedBranch=lineSeg.get("RELATED_BRANCH")==null?"":lineSeg.get("RELATED_BRANCH").toString();
				String pointName=getNameByPoint(lineSeg.get("ORIG_POINT_CUID").toString());
				if(j==0&&origName!=null){						
					origName=pointName;
				}	
				if(lineSegs.size()==1){//如果只有一段						
	    			if(faultDesSet.size()==0){
	    				description=origName+"——"+getNameByPoint(lineSeg.get("DEST_POINT_CUID").toString());
		    			branchLength+=length;	
		    			subBranchLength+=subLength;		
		    			tmpResType=relatedClassName;
	    				recordDetailMap(ductLineDetailList, null, lineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, branchLength, 0.0, tmpResType, false,false);
						recordDetailMap(ductLineDetailList, null, subLineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, subBranchLength, 0.0, tmpResType, false,true);	
	    			}		    						    	
				}else if(j!=0&&lineBranchCuid.equals(relatedBranch)&&faultDesSet.size()>0&&j==(lineSegs.size()-1)){//如果是集合中最后一段承载段出现错误
					description=origName+"——"+getNameByPoint(lineSeg.get("DEST_POINT_CUID").toString());
					recordDetailMap(ductLineDetailList, null, lineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, branchLength, 0.0, tmpResType, false,false);
					recordDetailMap(ductLineDetailList, null, subLineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, subBranchLength, 0.0, tmpResType, false,true);
				}else if(j!=0&&lineBranchCuid.equals(relatedBranch)&&faultDesSet.size()>0&&!relatedBranch.equals(lineSegs.get(j+1).get("RELATED_BRANCH").toString())){//如果是分支中最后一段承载段出现错误
					description=origName+"——"+getNameByPoint(lineSeg.get("DEST_POINT_CUID").toString());
					recordDetailMap(ductLineDetailList, null, lineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, branchLength, 0.0, tmpResType, false,false);
					recordDetailMap(ductLineDetailList, null, subLineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, subBranchLength, 0.0, tmpResType, false,true);
					//记录完一个分支的承载，初始化参数
					description="";
					origName=getNameByPoint(lineSeg.get("DEST_POINT_CUID").toString());
					branchLength=0.0;
					subBranchLength=0.0;
				}else if(j!=0&&(!lineBranchCuid.equals(relatedBranch)||j==(lineSegs.size()-1))){					
					if(j==(lineSegs.size()-1)){							
						description=origName+"——"+getNameByPoint(lineSeg.get("DEST_POINT_CUID").toString());
					}else{
						description=origName+"——"+pointName;//起始点名称--终点名称
					}
					if(j==(lineSegs.size()-1)&&faultDesSet.size()==0){
						branchLength+=length;
						subBranchLength+=subLength;
					}
					recordDetailMap(ductLineDetailList, null,lineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, branchLength, 0.0, tmpResType, false,false);
					recordDetailMap(ductLineDetailList, null,subLineMap.get("CUID").toString(), districtCuid,lineSegCuid, lineSegLabel, systemLabelCn, description, subBranchLength, 0.0, tmpResType, false,true);
					//记录完一个分支的承载，初始化参数
					description="";
					origName=pointName;
					branchLength=0.0;
					subBranchLength=0.0;
				}
				tmpResType=relatedClassName;
				if(faultDesSet.size()==0){
					branchLength+=length;
					subBranchLength+=subLength;
				}					
				lineBranchCuid=lineSeg.get("RELATED_BRANCH").toString();				
			}
		}

	}
	
	private Double getSubLength(double length, Integer wireCount) {
		Double subLength;
		if(wireCount==0||wireCount==1){
			subLength=length;
		}else if(wireCount<=5){
			subLength=length+length*(wireCount-1)*0.1;
		}else{
			subLength=length+length*(5-1)*0.1;
		}
		return subLength;
	}
	
	private Integer getLayCount(String lineSegCuid) {
		String wtdlSql="SELECT count(DISTINCT WIRE_SEG_CUID) as COUNT FROM WIRE_TO_DUCTLINE WHERE "+WireToDuctline.AttrName.lineSegCuid+"='"+lineSegCuid+"'" ;
		List<Map<String, Object>> wtdlList=getListByParams("IbatisTransDAO", wtdlSql);
		Integer wireCount=StatHelper.getIntByObj(wtdlList.get(0).get("COUNT"));
		return wireCount;
	}

	/**分析光缆段*/
	private void doAnalyseWireSeg(String districtCuid,Map<String, Object> dataMap,List<Map<String, Object>>wireDetailList,boolean isSub, String wireSystemLabelCn,
			int type, Map wireSeg,Map<String,List<Map<String, Object>>>paraMap,int systemLevel) {			
		List<Map<String, Object>>wtdls=paraMap.get(WireToDuctline.CLASS_NAME);
		List<Map<String, Object>>wireRems=paraMap.get(WireRemain.CLASS_NAME);
		List<Map<String, Object>>rebuildMap=paraMap.get("REBUILD");
		List<Map<String, Object>>fiberMap=paraMap.get(Fiber.CLASS_NAME);			
		String wireStatCuid=(String) dataMap.get(GenericDO.AttrName.cuid);
		//存放有问题的光缆的map
		Map <String,Object>faultMap=new HashMap<String,Object>();
		//问题描述集合
		Set <String>faultDesSet=new HashSet<String>();
//			一条干线光缆段经过的管道的长度
		double tmpTrunkDuctLength=0.0;
		//一条干线光缆段经过的杆路的长度
		double tmpTrunkPolewayLength=0.0;
		//一条干线光缆段经过的直埋的长度
		double tmpTrunkStonewayLength=0.0;
		//一条干线光缆段经过的引上的长度
		double tmpTrunkUplineLength=0.0;
		//一条干线光缆段经过的挂墙的长度
		double tmpTrunkHangwallLength=0.0;
		//计算干线预留长度
		double tmpTrunkWireRemainLength=0.0;	
		
		double tmpLocalDuctLength=0.0;
		//一条本地光缆段经过的杆路的长度
		double tmpLocalPolewayLength=0.0;
		//一条本地光缆段经过的直埋的长度
		double tmpLocalStonewayLength=0.0;
		//一条本地光缆段经过的引上的长度
		double tmpLocalUplineLength=0.0;
		//一条本地光缆段经过的挂墙的长度
		double tmpLocalHangwallLength=0.0;

		//计算本地预留长度
		double tmpLocalWireRemainLength=0.0;
		
		//总预留
		double tmpAllRemain=0.0;		
		//光缆承载段描述	
		String description="";
		//光缆承载段描述起始点名称				
		String disName="";
				
		String wireSegCuid=(String) wireSeg.get(GenericDO.AttrName.cuid);
		String wireSegName=(String) wireSeg.get(GenericDO.AttrName.labelCn);
		Double wireSegLength=StatHelper.getDoubleByObj(wireSeg.get("LENGTH"));

		long fiberCount=StatHelper.getLongByObj( wireSeg.get("FIBER_COUNT"));
		
		//先分析是不是问题段，如果是问题段就不统计在长度表中			
		if(wtdls==null||wtdls.isEmpty()){
			faultDesSet.add(StatConst.noLay);
		}
		//一条光缆经过的承载段的长度
		double ductLineLength=0.0;
		//所属分支
		String lineBranchCuid="";
		//同一个分支下的承载段长度
		double branchLength=0.0;
		String tmpResType="";
		//循环所有承载段
		for(int j = 0; j < wtdls.size(); j++){
			Map pointCuidMap=wtdls.get(j);
			//承载段长度	
			double length=getSegLengthByPoints(pointCuidMap,"DIS_POINT_CUID","END_POINT_CUID");					
			String lineSegCuid=(String) pointCuidMap.get("LINE_SEG_CUID");//段ID
			String className=GenericDO.parseClassNameFromCuid(lineSegCuid);			
			String resType="";
			ductLineLength+=length;
			//检查段是否超过了定义的阈值
			checkOutOfLength(length,className,districtCuid,faultDesSet,StatConst.isThresholdByDis);	
			if(systemLevel == 1 || systemLevel == 2){//干线
				resType="TRUNK_"+className;
				if(isSub){
					int count =this.getLayCount(lineSegCuid);
					//光缆代维取管道代维的平均值，zhl
					length = count > 0 ? this.getSubLength(length, count)/count : 0;
				}
				if(DuctSeg.CLASS_NAME.equals(className)){//管道段
					tmpTrunkDuctLength+=length;								
				}else if(PolewaySeg.CLASS_NAME.equals(className)){//杆路段
					tmpTrunkPolewayLength+=length;
				}else if(StonewaySeg.CLASS_NAME.equals(className)){//标石段
					tmpTrunkStonewayLength+=length;
				}else if(UpLineSeg.CLASS_NAME.equals(className)){//引上段
					tmpTrunkUplineLength+=length;
				}else if(HangWallSeg.CLASS_NAME.equals(className)){//挂墙段
					tmpTrunkHangwallLength+=length;
				}
			}else if(systemLevel == 3 || systemLevel == 4 || systemLevel == 5){//本地	
				resType="LOCAL_"+className;
				if(isSub){
					int count =this.getLayCount(lineSegCuid);
					//光缆代维取管道代维的平均值，zhl
					length = count > 0 ? this.getSubLength(length, count)/count : 0;
				}
				if(DuctSeg.CLASS_NAME.equals(className)){//管道段
					tmpLocalDuctLength+=length;
				}else if(PolewaySeg.CLASS_NAME.equals(className)){//杆路段
					tmpLocalPolewayLength+=length;
				}else if(StonewaySeg.CLASS_NAME.equals(className)){//标石段
					tmpLocalStonewayLength+=length;
				}else if(UpLineSeg.CLASS_NAME.equals(className)){//引上段
					tmpLocalUplineLength+=length;
				}else if(HangWallSeg.CLASS_NAME.equals(className)){//挂墙段
					tmpLocalHangwallLength+=length;
				}					
			}else{
				continue;
			}
			//县级才统计详细信息
			if(districtCuid.length()==32){
				//光缆承载段起始点资源名称				
				String pointName=getNameByPoint(pointCuidMap.get("DIS_POINT_CUID").toString());									
				if(j==0){
					disName=pointName;
				}					
				String tmpLineBranchCuid=pointCuidMap.get("LINE_BRANCH")==null?pointCuidMap.get("LINE_SYSTEM_CUID").toString():pointCuidMap.get("LINE_BRANCH").toString();	
				//挂墙、引上没有所属分支，根据所属光缆系统							    	
		    	if(j!=0&&((!lineBranchCuid.equals(tmpLineBranchCuid)||j==(wtdls.size()-1)))){
		    		// 记录详细信息  起始点名称--终点名称
		    		if(j==(wtdls.size()-1)){
		    			description=disName+"——"+getNameByPoint(pointCuidMap.get("END_POINT_CUID").toString());
		    			branchLength+=length;
		    			tmpResType=resType;
		    		}else{
		    			description=disName+"——"+pointName;
		    		}						
		    		if(faultDesSet.size()==0){						
		    			recordDetailMap(wireDetailList, null, dataMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, branchLength, 0.0, tmpResType,true,isSub);										
		    		}
					//记录完一个分支的承载，初始化参数						
					description="";
					disName=pointName;
					branchLength=0.0;
		    	}else if(wtdls.size()==1){//如果只有一段
	    			description=disName+"——"+getNameByPoint(pointCuidMap.get("END_POINT_CUID").toString());
	    			branchLength+=length;			
	    			tmpResType=resType;
		    		if(faultDesSet.size()==0){						
		    			recordDetailMap(wireDetailList,  null,dataMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, branchLength, 0.0, tmpResType,true,isSub);										
		    		}
		    	}
		    	tmpResType=resType;
		    	branchLength+=length;
		    	lineBranchCuid=tmpLineBranchCuid;				
			}								
		}
		//预留信息
		if(StatConst.isStatWireRemain){
			Map<String, Double> remainMap = calculateRemain(wireRems);				
			String resType="";			
			if(systemLevel == 1 || systemLevel == 2){//干线
				resType="TRUNK_WIRE_REMAIN";
				if (remainMap.get("ALL_LENGTH")!=null) {
					tmpTrunkWireRemainLength+=remainMap.get("ALL_LENGTH");
				}
			}else if(systemLevel == 3 || systemLevel == 4 || systemLevel == 5){//本地
				resType="LOCAL_WIRE_REMAIN";
				if (remainMap.get("ALL_LENGTH")!=null) {
					tmpLocalWireRemainLength+=remainMap.get("ALL_LENGTH");
				}
			}
			tmpAllRemain=tmpTrunkWireRemainLength+tmpLocalWireRemainLength;
			//计算预留的详细信息	
			if(districtCuid.length()==32){
				calculateRemainDetail(wireDetailList, null,districtCuid, wireSystemLabelCn,wireSegCuid, wireSegName, resType,dataMap.get("CUID").toString(), wireRems,isSub);
			}				
		}	
		//检查长度
		checkOutOfRatio(wireSegLength+tmpAllRemain,ductLineLength+tmpAllRemain,faultDesSet);
		//河南的数据做特殊处理
		if(StatConst.isStatAddDelWireOfHenan){
			isFaultWithFiber(faultDesSet, fiberMap, fiberCount);				
			isMultiLay(faultDesSet, wtdls);
			//只有是新增加的光缆段才会判断是否
			if(type==2){
				if(rebuildMap!=null&&rebuildMap.size()>0){
					faultDesSet.add(StatConst.rebuild);
				}	
			}
		}
		//检查问题后，如果该光缆段还存在问题,则该光缆段详细描述不保存
		if(districtCuid.length()==32&&faultDesSet.size()>0){
			for (int i = 0; i < wireDetailList.size(); i++) {
				Map wireDetail =wireDetailList.get(i);
				if(wireDetail.get("RELATED_WIRESEG_CUID").toString().equals(wireSegCuid)){
					wireDetailList.remove(wireDetail);
					i--;
				}else{}
			}
		}else{}		
		
		//如果faultDesSet中有数据，就认为这条光缆是问题数据，不统计在光缆长度中，但是要录入在问题表中
		if(faultDesSet.size()>0){
			int faultSegCount=(Integer) dataMap.get("FAULT_SEG_COUNT");
			faultSegCount++;
			dataMap.put("FAULT_SEG_COUNT",faultSegCount);
			faultMap.put("RELATED_WIRE_LENGTH_STAT", wireStatCuid);	
			setFaultSeg(districtCuid,  wireSystemLabelCn,faultMap, faultDesSet, wireSegCuid, wireSegName,"WIRE_SEG_FAULT");
		}else{
			//主干网
			putNewValueDouble("TRUNK_DUCT_SEG",dataMap, tmpTrunkDuctLength);
			putNewValueDouble("TRUNK_POLEWAY_SEG",dataMap, tmpTrunkPolewayLength);
			putNewValueDouble("TRUNK_STONEWAY_SEG",dataMap, tmpTrunkStonewayLength);
		    putNewValueDouble("TRUNK_UP_LINE_SEG",dataMap, tmpTrunkUplineLength);
			putNewValueDouble("TRUNK_HANG_WALL_SEG",dataMap, tmpTrunkHangwallLength);
			putNewValueDouble("TRUNK_WIRE_REMAIN",dataMap, tmpTrunkWireRemainLength);
			//本地网
			putNewValueDouble("LOCAL_DUCT_SEG",dataMap, tmpLocalDuctLength);
			putNewValueDouble("LOCAL_POLEWAY_SEG",dataMap, tmpLocalPolewayLength);
			putNewValueDouble("LOCAL_STONEWAY_SEG",dataMap, tmpLocalStonewayLength);
			putNewValueDouble("LOCAL_UP_LINE_SEG",dataMap, tmpLocalUplineLength);
			putNewValueDouble("LOCAL_HANG_WALL_SEG",dataMap, tmpLocalHangwallLength);
			putNewValueDouble("LOCAL_WIRE_REMAIN",dataMap, tmpLocalWireRemainLength);

			//总计
//				putNewValueDouble("ALL_REMAIN",dataMap, tmpAllRemain);		
		}
	}
	/**
	 * 记录详细信息
	 * */
	private void recordDetailMap(List<Map<String, Object>>detailList,String checkMonth,String statCuid,String districtCuid,
			String segCuid,String segName,String systemName,String description,double length,double remain,String resType,Boolean isWire,Boolean isSub) {
		Map<String, Object> detailMap=new HashMap<String, Object>();
		String cuid="";
		if(isWire){
			cuid = CUIDHexGenerator.getInstance().generate("WIRE_DETAIL");
			detailMap.put("RELATED_WIRE_STAT", statCuid);				
			detailMap.put("RELATED_WIRESEG_CUID", segCuid);
			detailMap.put("REMAIN", remain);
		}else{
			cuid = CUIDHexGenerator.getInstance().generate("DUCTLINE_DETAIL");
			detailMap.put("RELATED_DUCTLINE_STAT", statCuid);
			detailMap.put("RELATED_DUCTSEG_CUID", segCuid);
		}
		if(isSub){
			detailMap.put("ISSUB", 2);
		}else{
			detailMap.put("ISSUB", 1);
		}
		detailMap.put("CUID", cuid);
		detailMap.put("RELATED_DISTRICT_CUID", districtCuid);						
		detailMap.put("SYSTEM_NAME", systemName);
		detailMap.put("SEG_NAME", segName);
		detailMap.put("LENGTH", length);			
		detailMap.put("DESCRIPTION", description);
		detailMap.put("RESTYPE", resType);
		//是否是核减操作
		if(checkMonth!=null){
			detailMap.put("CHECK_MONTH", checkMonth);
		}else{
			detailMap.put("CHECK_MONTH", StatConst.checkMonth);
		}
		
		detailList.add(detailMap);
	}
	/**
	 * 计算预留的详细信息
	 * */
	private void calculateRemainDetail(List<Map<String, Object>>wireDetailList,String checkMonth, String districtCuid,String wireSystemLabelCn,String wireSegCuid,String wireSegName,
			String resType,String statWireCuid,List<Map<String, Object>>wireRems,Boolean isSub) {		
		if(wireRems!=null){
			Double remainLength=0.0;
			for(Map remain:wireRems){
				Double remainLengthTmp=StatHelper.getDoubleByObj(remain.get("REMAIN_LENGTH"));
				if(remainLength==null){
					remainLength=0.0;
				}	
				remainLength+=remainLengthTmp;
			}
			recordDetailMap(wireDetailList,checkMonth,statWireCuid, districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, "", 0.0, remainLength, resType, true,isSub);
		}
	}
	/**
	 * 获取资源点名称
	 * */
	private String getNameByPoint(String pointCuid){
		if(StringUtils.isNotEmpty(pointCuid)){
			String className = GenericDO.parseClassNameFromCuid(pointCuid);
			String sql = "SELECT LABEL_CN FROM " + className + " WHERE CUID = '" + pointCuid + "'";
			List<Map<String, Object>> results = getListByParams("IbatisTransDAO", sql);
			if(results != null && !results.isEmpty()){					
				return results.get(0).get("LABEL_CN").toString();
			}else{
				return null;
			}
		}
		return null;
	}
	
	private void isMultiLay(Set<String> faultDesSet,
			List<Map<String, Object>> wtdls) {
		if(wtdls!=null){
			List multiCuids=new ArrayList();
			for(Map<String, Object> map:wtdls){
				String lineSegCuid=(String) map.get("LINE_SEG_CUID");
				if(multiCuids.contains(lineSegCuid)){
					faultDesSet.add(StatConst.multiLay);
				}
				multiCuids.add(lineSegCuid);
			}
		}
	}
	private Map<String,Double> calculateRemain(List<Map<String, Object>>wireRems) {
		Map <String,Double>map=new HashMap<String, Double>();
		double tmpWireRemainLength=0.0;
		
		if(wireRems!=null){
			for(Map remain:wireRems){
				Double tmpLength=StatHelper.getDoubleByObj(remain.get("REMAIN_LENGTH"));
				String wireCuid=(String) remain.get(WireRemain.AttrName.relatedWireSegCuid);
				String locatedPointCuid=(String) remain.get(WireRemain.AttrName.relatedLocationCuid);
				String locatedPointClassName=GenericDO.parseClassNameFromCuid(locatedPointCuid);
				if(tmpLength==null){
					tmpLength=0.0;
				}
				//暂时不考虑预留的代维问题
//					if(lineMap!=null){
//						Integer count=lineMap.get(wireCuid);
//						if(count==null){
//							count=0;
//						}else if(1<=count&&count<=4){//如果有多于一条的光缆经过该预留，大于2-5条的预留长度是原有长度的10%，都与5条的不计
//							tmpLength=tmpLength*0.1;
//						}else{
//							tmpLength=0.0;
//						}
//						count++;
//						lineMap.put(wireCuid, count);
//					}
				this.getRemainMapByLocType(map, locatedPointClassName, tmpLength);
				tmpWireRemainLength+=tmpLength;
			}
		}
		map.put("ALL_LENGTH", tmpWireRemainLength);
		return map;
	}
	
	private void getRemainMapByLocType(Map<String,Double> map,String className,double remainLen){
		String segClassName="";
		if(Pole.CLASS_NAME.equals(className)){
			segClassName=PolewaySeg.CLASS_NAME;
		}else if(Stone.CLASS_NAME.equals(className)){
			segClassName=StonewaySeg.CLASS_NAME;

		}else{
			segClassName=DuctSeg.CLASS_NAME;
		} 
		
		Double ductSegLen=map.get(segClassName);
		if(ductSegLen==null){
			map.put(segClassName, remainLen);
		}else{
			map.put(segClassName, remainLen+ductSegLen);
		}			
	}
	private void isFaultWithFiber(Set<String> faultDesSet,
			List<Map<String, Object>>fiberMap, long fiberCount) {

		if(fiberMap!=null&&!fiberMap.isEmpty()){
			for(Map<String, Object> map:fiberMap){
				Object orig=map.get("ORIG_POINT_CUID");
				if(orig==null||orig.toString().length()<1){
					faultDesSet.add(StatConst.notEntireFiber);
					break;
				}
				Object dest=map.get("DEST_POINT_CUID");
				if(dest==null||dest.toString().length()<1){
					faultDesSet.add(StatConst.notEntireFiber);
					break;
				}
			}
		}else{
			faultDesSet.add(StatConst.noFiber);
		}
		
		if(fiberMap==null){
			fiberMap=new ArrayList();
		}
		if(fiberCount!=fiberMap.size()){
			faultDesSet.add(StatConst.numDiff);
		}
	}
	private void setFaultSeg(String districtCuid,String wireSystemLabelCn,
			Map<String, Object> faultMap,
			Set<String> faultDesSet, String wireSegCuid, String wireSegName,String tableName) {
		String cuid = CUIDHexGenerator.getInstance().generate(tableName);
		faultMap.put("CUID", cuid);
		faultMap.put("SEG_CUID", wireSegCuid);
		faultMap.put("SEG_NAME", wireSegName);
		faultMap.put("SYSTEM_NAME", wireSystemLabelCn);
		faultMap.put("RELATED_DISTRICT_CUID", districtCuid);

		faultMap.put("CREATE_TIME", new Date());
		faultMap.put("CHECK_MONTH", StatConst.checkMonth);
		faultMap.put("CHECK_STATE", 1);
		String faultDes="";
		for(String des:faultDesSet){
			faultDes+=des+",";
		}
		faultDes=faultDes.substring(0, faultDes.length()-1);
		faultMap.put("FAULT_DESCRIPTION", faultDes);
		insertObject("IbatisSdeDAO", tableName, faultMap);
	}
	
	
	private void putNewValueDouble(String key,Map<String, Object> dataMap,double tmpWireRemainLength) {
		double wireRemainLength=StatHelper.getDoubleByObj(dataMap.get(key));
		wireRemainLength+=tmpWireRemainLength;
		dataMap.put(key, wireRemainLength);
	}
	//根据CUID得到点的经纬度
	private Map getLLByCuid(String pointCuid){
		if(StringUtils.isNotEmpty(pointCuid)){
			String className = GenericDO.parseClassNameFromCuid(pointCuid);
			String sql = "SELECT LONGITUDE,LATITUDE FROM " + className + " WHERE CUID = '" + pointCuid + "'";
			List<Map<String, Object>> results = getListByParams("IbatisTransDAO", sql);
			if(results != null && !results.isEmpty()){
				return results.get(0);
			}	
		}
		return new HashMap();
	}
	//检查段是否超过了定义的阈值
	private void checkOutOfLength(double length,String className,String districtCuid,Set set,boolean isThresholdByDis){
		Map<String,Double> threshold=null;
		if(isThresholdByDis){
			Map<String, Map<String, Double>> defaultThreshold=StatConst.faultThreshold.getDisThreshold();
			threshold=defaultThreshold.get(districtCuid);
		}else{
			threshold=StatConst.faultThreshold.getDefaultThreshold();
		}
		if(threshold==null){
			threshold=StatConst.faultThreshold.getDefaultThreshold();
		}
		Double ductThreshold=threshold.get(className);
		if(ductThreshold==null){
			return;
		}
		if(length>ductThreshold){
			set.add(StatConst.outOfLength);
		}	
	}
	
	//检查长度比值超标：输入长度比计算长度超长20%，或者计算长度大于输入长度，如果系统开启了预留长度统计，则计算长度指：按经纬度计算的长度+预留长度
		private void checkOutOfRatio(double wireLength,double ductLineLength,Set set){
//				if(ductLineLength>wireLength){
//					set.add(StatConst.outOfRatio);
//					return;
//				}
			if(ductLineLength==0){
				return;
			}else if((Math.abs(wireLength-ductLineLength))/ductLineLength>0.2){
				set.add(StatConst.outOfRatio);
				return;
			}
		}
		
		
		private double getSegLengthByPoints(Map pointCuidMap,String origAttr,String destAttr){
			
			double disPointLongitude=0.0;
			double disPointLatitude=0.0;
			double endPointLongitude=0.0;
			double endPointLatitude=0.0;
			try{
				String disPointCuid=(String) pointCuidMap.get(origAttr);
				Map disPointLL=getLLByCuid(disPointCuid);
				
				disPointLongitude=StatHelper.getDoubleByObj(disPointLL.get("LONGITUDE"));
				disPointLatitude=StatHelper.getDoubleByObj(disPointLL.get("LATITUDE"));
				if(!DMHelper.isCoordAvailable(disPointLatitude,disPointLongitude)){
					LogHome.getLog().error(disPointCuid+"---根据点的CUID取得的经纬度错误");
					return 0;
				}
				
				String endPointCuid=(String) pointCuidMap.get(destAttr);
				Map endPointLL=getLLByCuid(endPointCuid);
				
				endPointLongitude=StatHelper.getDoubleByObj(endPointLL.get("LONGITUDE"));
				endPointLatitude=StatHelper.getDoubleByObj(endPointLL.get("LATITUDE"));
				if(!DMHelper.isCoordAvailable(endPointLatitude,endPointLongitude)){
					LogHome.getLog().error(endPointCuid+"---根据点的CUID取得的经纬度错误");
					return 0;
				}

			}catch(Exception ex){
				LogHome.getLog().error(ex.getMessage(),ex);
				return 0;
			}
			double length = StatHelper.calculateLength(disPointLongitude, disPointLatitude, endPointLongitude, endPointLatitude);
			return length;
		}
		//暂时不用这个方法
		private void doCheck(String wireFaultCuid) {
			//先分析是不是问题段，如果是问题段就不统计在长度表中
			String wireFaultSql="SELECT * FROM FAULT_WIRE_SEG WHERE CUID='"+wireFaultCuid+"'";
			List<Map<String, Object>>fautlList = getListByParams("IbatisTransDAO", wireFaultSql);
			

			if(fautlList.size()>0){
				Map faultMap=fautlList.get(0);
				String wireSegCuid=(String) faultMap.get("SEG_CUID");
				String relatedStatCuid=(String) fautlList.get(0).get("RELATED_WIRE_LENGTH_STAT");

				String wireStatSql="SELECT * FROM WIRE_LENGTH_DIS WHERE CUID='"+relatedStatCuid+"'";
				List<Map<String, Object>>wireStatList=getListByParams("IbatisSdeDAO", wireStatSql);
				Map<String, Object> statMap=wireStatList.get(0);
				
				//先分析是不是问题段，如果是问题段就不统计在长度表中
				String wireToDuctLineSql="SELECT DIS_POINT_CUID,END_POINT_CUID,LINE_SEG_CUID FROM WIRE_TO_DUCTLINE WHERE WIRE_SEG_CUID='"+wireSegCuid+"'";
				List<Map<String, Object>>wtdls=getListByParams("IbatisTransDAO", wireToDuctLineSql);
				
				String wireRemainSql="SELECT REMAIN_LENGTH,RELATED_WIRE_SEG_CUID,RELATED_LOCATION_CUID FROM WIRE_REMAIN WHERE RELATED_WIRE_SEG_CUID='"+wireSegCuid+"'";;
				List<Map<String, Object>>wireRems=getListByParams("IbatisTransDAO", wireRemainSql);
				
				
				double ductLineLength=0.0;
//					一条光缆段经过的管道的长度
				double tmpDuctLength=0.0;
				//一条光缆段经过的杆路的长度
				double tmpPolewayLength=0.0;
				//一条光缆段经过的直埋的长度
				double tmpStonewayLength=0.0;
				//一条光缆段经过的引上的长度
				double tmpUplineLength=0.0;
				//一条光缆段经过的挂墙的长度
				double tmpHangwallLength=0.0;
				//计算预留长度
				double tmpWireRemainLength=0.0;
				for(Map pointCuidMap:wtdls){
					double length=getSegLengthByPoints(pointCuidMap,"DIS_POINT_CUID","END_POINT_CUID");
					ductLineLength+=length;
					String lineSegCuid=(String) pointCuidMap.get("LINE_SEG_CUID");
					
					String className=GenericDO.parseClassNameFromCuid(lineSegCuid);
					if(DuctSeg.CLASS_NAME.equals(className)){
						tmpDuctLength+=length;
					}else if(PolewaySeg.CLASS_NAME.equals(className)){
						tmpPolewayLength+=length;
					}else if(StonewaySeg.CLASS_NAME.equals(className)){
						tmpStonewayLength+=length;
					}else if(UpLineSeg.CLASS_NAME.equals(className)){
						tmpUplineLength+=length;
					}else if(HangWallSeg.CLASS_NAME.equals(className)){
						tmpHangwallLength+=length;
					}
					
				}	
				
				if(StatConst.isStatWireRemain){
					Map<String, Double> remainMap = calculateRemain(wireRems);
					if (remainMap.get(DuctSeg.CLASS_NAME)!=null) {
						tmpDuctLength+=remainMap.get(DuctSeg.CLASS_NAME);
					}
					if (remainMap.get(PolewaySeg.CLASS_NAME)!=null) {
						tmpPolewayLength+=remainMap.get(PolewaySeg.CLASS_NAME);
					}
					if (remainMap.get(StonewaySeg.CLASS_NAME)!=null) {
						tmpStonewayLength+=remainMap.get(StonewaySeg.CLASS_NAME);
					}
					if (remainMap.get("ALL_LENGTH")!=null) {
						tmpWireRemainLength+=remainMap.get("ALL_LENGTH");
					}
				}
				Map map=wireStatList.get(0);
				Map updateMap=new HashMap();
				updateMap.put("RELATED_DUCT_SEG", StatHelper.getDoubleByObj(statMap.get("RELATED_DUCT_SEG"))+tmpDuctLength);
				updateMap.put("RELATED_POLEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("RELATED_POLEWAY_SEG"))+tmpPolewayLength);
				updateMap.put("RELATED_STONEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("RELATED_STONEWAY_SEG"))+tmpStonewayLength);
				updateMap.put("RELATED_UP_LINE_SEG", StatHelper.getDoubleByObj(statMap.get("RELATED_UP_LINE_SEG"))+tmpUplineLength);
				updateMap.put("RELATED_HANG_WALL_SEG", StatHelper.getDoubleByObj(statMap.get("RELATED_HANG_WALL_SEG"))+tmpHangwallLength);
				updateMap.put("WIRE_REMAIN", StatHelper.getDoubleByObj(statMap.get("WIRE_REMAIN"))+tmpWireRemainLength);

				updateMap.put("ALL_SEG", StatHelper.getDoubleByObj(statMap.get("ALL_SEG"))+ductLineLength);
				
				Map pkMap=new HashMap();
				pkMap.put("CUID", statMap.get("CUID"));
				updateObject("IbatisSdeDAO", "WIRE_LENGTH_DIS", updateMap, pkMap);
				
				Map updateFaultMap=new HashMap();
				updateFaultMap.put("CHECK_STATE", 2);
				Map pkFaulMap=new HashMap();
				pkMap.put("CUID", faultMap.get("CUID"));
				updateObject("IbatisSdeDAO", "FAULT_WIRE_SEG", updateMap, pkMap);

			}
			
		}
		public Map<String,Object> doCheckSeg(String faultSegCuid,boolean isWire,boolean isSub) {
			try{
				//先分析是不是问题段，如果是问题段就不统计在长度表中
				String faultTableClassName=GenericDO.parseClassNameFromCuid(faultSegCuid);
				String wireFaultSql="SELECT * FROM "+faultTableClassName+" WHERE CUID='"+faultSegCuid+"'";
				List<Map<String, Object>>fautlList=getListByParams("IbatisSdeDAO", wireFaultSql);	
				//同时更新区域代维信息和正常信息
				Map<String,Object> updateMap=new HashMap<String,Object>();
				Map<String,Object> subUpdateMap=new HashMap<String,Object>();
				if(fautlList.size()>0){
					Map faultMap=fautlList.get(0);
					String wireSegCuid=(String) faultMap.get("SEG_CUID");						
					String relatedStatCuid=null;	
					String subRelatedStatCuid=null;	
					String checkMonth= fautlList.get(0).get("CHECK_MONTH").toString();
					String relatedDistrict=fautlList.get(0).get("RELATED_DISTRICT_CUID").toString();
					if(isWire){
						//例如：根据光缆段所在区域正常信息查找该光缆段所在区域代维信息
						relatedStatCuid=(String) fautlList.get(0).get("RELATED_WIRE_LENGTH_STAT");							
						String subWireFaultSql="SELECT RELATED_WIRE_LENGTH_STAT " +
								"FROM "+faultTableClassName+" WHERE CHECK_MONTH="+checkMonth+" " +
										"AND SEG_CUID='"+wireSegCuid+"' " +
										"AND RELATED_DISTRICT_CUID='"+relatedDistrict+"' " +
										"AND RELATED_WIRE_LENGTH_STAT!='"+relatedStatCuid+"'";
						List<Map<String, Object>>subFautlList=getListByParams("IbatisSdeDAO", subWireFaultSql);
						if(subFautlList.size()>0){
							subRelatedStatCuid=subFautlList.get(0).get("RELATED_WIRE_LENGTH_STAT").toString();		
						}
					}else{
						relatedStatCuid=(String) fautlList.get(0).get("RELATED_DUCTLINE_LENGTH_STAT");
						String subDuctFaultSql="SELECT RELATED_DUCTLINE_LENGTH_STAT " +
								"FROM "+faultTableClassName+" WHERE CHECK_MONTH="+checkMonth+" " +
										"AND RELATED_DISTRICT_CUID='"+relatedDistrict+"' " +
										"AND SEG_CUID='"+wireSegCuid+"' " +
										"AND RELATED_DUCTLINE_LENGTH_STAT!='"+relatedStatCuid+"'";
						List<Map<String, Object>>subFautlList=getListByParams("IbatisSdeDAO", subDuctFaultSql);
						if(subFautlList.size()>0){
							subRelatedStatCuid=subFautlList.get(0).get("RELATED_DUCTLINE_LENGTH_STAT").toString();									
						}
					}
					
					String lengthTableName=GenericDO.parseClassNameFromCuid(relatedStatCuid);
					String wireStatSql="SELECT * FROM "+lengthTableName+" WHERE CUID='"+relatedStatCuid+"'";											
					List<Map<String, Object>>wireStatList=getListByParams("IbatisSdeDAO", wireStatSql);						
					Map<String, Object> statMap=wireStatList.get(0);
					Map<String,Object> pkMap=new HashMap<String,Object>();
					pkMap.put("CUID", statMap.get("CUID"));
					updateMap.put("CUID", statMap.get("CUID"));
					
					List<Map<String, Object>>subWireStatList=null;
					Map<String, Object> subStatMap=null;
					Map<String,Object> subPkMap=new HashMap<String,Object>();
					if(subRelatedStatCuid!=null){
						String subWireStatSql="SELECT * FROM "+lengthTableName+" WHERE CUID='"+subRelatedStatCuid+"'";
						subWireStatList=getListByParams("IbatisSdeDAO", subWireStatSql);
						if(subWireStatList.size()>0){		
							 subStatMap=subWireStatList.get(0);
							 subPkMap=new HashMap<String,Object>();
							 subPkMap.put("CUID", subStatMap.get("CUID"));
							 subUpdateMap.put("CUID", subStatMap.get("CUID"));								 
						}
					}
					if(isWire){	
						String sqlsys="SELECT B.LABEL_CN AS SYSTEM_NAME,B.RELATED_SPACE_CUID,B.SYSTEM_LEVEL,A.LABEL_CN AS WIRE_SEG_NAME FROM  WIRE_SEG A JOIN WIRE_SYSTEM B ON A.RELATED_SYSTEM_CUID=B.CUID WHERE A.CUID='"+wireSegCuid+"'";
						List<Map<String, Object>>wireMapList=getListByParams("IbatisTransDAO", sqlsys);
						if(wireMapList.size()>0){
							doCheckAnalyseWireSeg(statMap, isSub, checkMonth,wireSegCuid, 1, lengthTableName,pkMap,wireMapList.get(0),updateMap);																
							if(subStatMap!=null){
								doCheckAnalyseWireSeg(subStatMap, !isSub, checkMonth,wireSegCuid, 1, lengthTableName,subPkMap,wireMapList.get(0),subUpdateMap);
							}								
						}								
					}else{							
						String segClassName=GenericDO.parseClassNameFromCuid(wireSegCuid);
						String wireToDuctLineSql="SELECT ORIG_POINT_CUID,DEST_POINT_CUID FROM "+segClassName+" WHERE CUID='"+wireSegCuid+"'";
						List<Map<String, Object>>segs=getListByParams("IbatisTransDAO", wireToDuctLineSql);
						if(segs.size()>0){
							Map tmpMap=segs.get(0);
							double length=getSegLengthByPoints(tmpMap,"ORIG_POINT_CUID","DEST_POINT_CUID");
							String systemTable="";
							if(segClassName.equals(DuctSeg.CLASS_NAME)){
								systemTable=DuctSystem.CLASS_NAME;
							}else if(segClassName.equals(PolewaySeg.CLASS_NAME)){
								systemTable=PolewaySystem.CLASS_NAME;
							}else if(segClassName.equals(StonewaySeg.CLASS_NAME)){
								systemTable=StonewaySystem.CLASS_NAME;
							}else if(segClassName.equals(UpLineSeg.CLASS_NAME)){
								systemTable=UpLine.CLASS_NAME;
							}else if(segClassName.equals(HangWallSeg.CLASS_NAME)){
								systemTable=HangWall.CLASS_NAME;
							}
							Double subLength=0.0;
							Integer wireCount = getLayCount(wireSegCuid);
							subLength = getSubLength(length, wireCount);
							String sqlsys="SELECT B.LABEL_CN AS SEG_NAME,A.LABEL_CN AS SYSTEM_NAME,A.SYSTEM_LEVEL,A.RELATED_SPACE_CUID FROM  "+systemTable+"  A JOIN "+segClassName+" B ON B.RELATED_SYSTEM_CUID=A.CUID WHERE B.CUID='"+wireSegCuid+"'";
							List<Map<String, Object>>sysLevelList=getListByParams("IbatisTransDAO", sqlsys);
							int systemLevel=Integer.parseInt(sysLevelList.get(0).get("SYSTEM_LEVEL").toString());
							String wireSegName=sysLevelList.get(0).get("SEG_NAME").toString();
							String wireSystemLabelCn=sysLevelList.get(0).get("SYSTEM_NAME").toString();
							String districtCuid=sysLevelList.get(0).get("RELATED_SPACE_CUID").toString();
							String key="";
							if(systemLevel == 1 || systemLevel==2){//主干网
								key="TRUNK_"+segClassName;
							}else if(systemLevel == 3 || systemLevel==4 || systemLevel == 5){//本地网
								key="LOCAL_"+segClassName;
							}
							List<Map<String, Object>> detailList=new ArrayList<Map<String,Object>>();
							
							String origName=getNameByPoint(tmpMap.get("ORIG_POINT_CUID").toString());
							String destName=getNameByPoint(tmpMap.get("DEST_POINT_CUID").toString());
							String description=origName+"——"+destName;
							
							recordDetailMap(detailList,checkMonth,statMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, length, 0.0, key,false,isSub);																												
							updateMap.put(key, StatHelper.getDoubleByObj(statMap.get(key)) + StatHelper.formatExt(length));								
							formatExt(key,updateMap);
													
							updateObject("IbatisSdeDAO", lengthTableName, updateMap, pkMap);
							if(subStatMap!=null){
								recordDetailMap(detailList,checkMonth,subStatMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, subLength, 0.0, key,false,!isSub);
								subUpdateMap.put(key, StatHelper.getDoubleByObj(subStatMap.get(key)) + StatHelper.formatExt(subLength));
								formatExt(key,subUpdateMap);
								updateObject("IbatisSdeDAO", lengthTableName, subUpdateMap, subPkMap);
							}
							doInsertDetailObject(detailList, "DUCTLINE_DETAIL");
						}
					}
					
					Map<Object,Object> faultPKMap=new HashMap<Object,Object>();
					Map<String,Object> updateFaultMap=new HashMap<String,Object>();
					updateFaultMap.put("CHECK_STATE", 2);
					updateFaultMap.put("CUID", faultMap.get("CUID"));
					faultPKMap.put("CUID", faultMap.get("CUID"));
					updateObject("IbatisSdeDAO", faultTableClassName, updateFaultMap, faultPKMap);
				}
				return updateMap;
			}catch(Exception ex){
				LogHome.getLog().error(ex.getMessage(),ex);
				return null;
			}				
		}
		private void doCheckAnalyseWireSeg(Map<String, Object> statMap,boolean isSub,String checkMonth, String wireSegCuid,
				int type, String lengthTableName, Map<String,Object> pkMap,Map<String, Object> wireMap,Map<String,Object> updateMap) {
			int systemLevel=Integer.parseInt(wireMap.get("SYSTEM_LEVEL").toString());
			String wireSegName=wireMap.get("WIRE_SEG_NAME").toString();
			String wireSystemLabelCn=wireMap.get("SYSTEM_NAME").toString();
			String districtCuid=wireMap.get("RELATED_SPACE_CUID").toString();
			//先分析是不是问题段，如果是问题段就不统计在长度表中
			String wireToDuctLineSql="SELECT DIS_POINT_CUID,END_POINT_CUID,LINE_SEG_CUID,LINE_SYSTEM_CUID,LINE_BRANCH_CUID AS LINE_BRANCH FROM WIRE_TO_DUCTLINE WHERE WIRE_SEG_CUID='"+wireSegCuid+"' ORDER BY LINE_BRANCH_CUID, INDEX_IN_ROUTE";
			List<Map<String, Object>>wtdls=getListByParams("IbatisTransDAO", wireToDuctLineSql);
			
			String wireRemainSql="SELECT REMAIN_LENGTH,RELATED_WIRE_SEG_CUID FROM WIRE_REMAIN WHERE RELATED_WIRE_SEG_CUID='"+wireSegCuid+"'";;
			List<Map<String, Object>>wireRems=getListByParams("IbatisTransDAO", wireRemainSql);
			
			//一条干线光缆段经过的管道的长度
			double tmpTrunkDuctLength=0.0;
			//一条干线光缆段经过的杆路的长度
			double tmpTrunkPolewayLength=0.0;
			//一条干线光缆段经过的直埋的长度
			double tmpTrunkStonewayLength=0.0;
			//一条干线光缆段经过的引上的长度
			double tmpTrunkUplineLength=0.0;
			//一条干线光缆段经过的挂墙的长度
			double tmpTrunkHangwallLength=0.0;
			//计算干线预留长度
			double tmpTrunkWireRemainLength=0.0;	
			
			double tmpLocalDuctLength=0.0;
			//一条本地光缆段经过的杆路的长度
			double tmpLocalPolewayLength=0.0;
			//一条本地光缆段经过的直埋的长度
			double tmpLocalStonewayLength=0.0;
			//一条本地光缆段经过的引上的长度
			double tmpLocalUplineLength=0.0;
			//一条本地光缆段经过的挂墙的长度
			double tmpLocalHangwallLength=0.0;

			//计算本地预留长度
			double tmpLocalWireRemainLength=0.0;
			
			//总预留
			double tmpAllRemain=0.0;		
			
			//一条光缆经过的承载段的长度
			double ductLineLength=0.0;
			
			//光缆承载段描述	
			String description="";
			//光缆承载段描述起始点名称				
			String disName="";
			//循环所有承载段
			//所属分支
			String lineBranchCuid="";
			//同一个分支下的承载段长度
			double branchLength=0.0;
			List<Map<String, Object>> detailList=new ArrayList<Map<String,Object>>();
			String tmpResType="";
			for(int j = 0; j < wtdls.size(); j++){
				Map pointCuidMap=wtdls.get(j);
				//承载段长度	
				String resType="";
				double length=getSegLengthByPoints(pointCuidMap,"DIS_POINT_CUID","END_POINT_CUID");
				ductLineLength+=length;
				String lineSegCuid=(String) pointCuidMap.get("LINE_SEG_CUID");
				
				String className=GenericDO.parseClassNameFromCuid(lineSegCuid);
				if(systemLevel == 1 || systemLevel == 2){//干线
					resType="TRUNK_"+className;
					if(isSub){
						int count =this.getLayCount(lineSegCuid);
						//光缆代维取管道代维的平均值，zhl
						length = count > 0 ? this.getSubLength(length, count)/count : 0;
					}
					if(DuctSeg.CLASS_NAME.equals(className)){//管道段
						tmpTrunkDuctLength+=length;								
					}else if(PolewaySeg.CLASS_NAME.equals(className)){//杆路段
						tmpTrunkPolewayLength+=length;
					}else if(StonewaySeg.CLASS_NAME.equals(className)){//标石段
						tmpTrunkStonewayLength+=length;
					}else if(UpLineSeg.CLASS_NAME.equals(className)){//引上段
						tmpTrunkUplineLength+=length;
					}else if(HangWallSeg.CLASS_NAME.equals(className)){//挂墙段
						tmpTrunkHangwallLength+=length;
					}
				}else if(systemLevel == 3 || systemLevel == 4 || systemLevel == 5){//本地	
					resType="LOCAL_"+className;
					if(isSub){
						int count =this.getLayCount(lineSegCuid);
						//光缆代维取管道代维的平均值，zhl
						length = count > 0 ? this.getSubLength(length, count)/count : 0;
					}
					if(DuctSeg.CLASS_NAME.equals(className)){//管道段
						tmpLocalDuctLength+=length;
					}else if(PolewaySeg.CLASS_NAME.equals(className)){//杆路段
						tmpLocalPolewayLength+=length;
					}else if(StonewaySeg.CLASS_NAME.equals(className)){//标石段
						tmpLocalStonewayLength+=length;
					}else if(UpLineSeg.CLASS_NAME.equals(className)){//引上段
						tmpLocalUplineLength+=length;
					}else if(HangWallSeg.CLASS_NAME.equals(className)){//挂墙段
						tmpLocalHangwallLength+=length;
					}					
				}			
				if(districtCuid.length()==32){
					String pointName=getNameByPoint(pointCuidMap.get("DIS_POINT_CUID").toString());									
					if(j==0){
						disName=pointName;
					}					
					String tmpLineBranchCuid=pointCuidMap.get("LINE_BRANCH")==null?pointCuidMap.get("LINE_SYSTEM_CUID").toString():pointCuidMap.get("LINE_BRANCH").toString();	
					//挂墙、引上没有所属分支，根据所属光缆系统							    	
			    	if(j!=0&&((!lineBranchCuid.equals(tmpLineBranchCuid)||j==(wtdls.size()-1)))){
			    		// 记录详细信息  起始点名称--终点名称
			    		if(j==(wtdls.size()-1)){
			    			description=disName+"——"+getNameByPoint(pointCuidMap.get("END_POINT_CUID").toString());
			    			branchLength+=length;
			    			tmpResType=resType;
			    		}else{
			    			description=disName+"——"+pointName;
			    		}										
			    		recordDetailMap(detailList,checkMonth, statMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, branchLength, 0.0, tmpResType,true,isSub);										
						//记录完一个分支的承载，初始化参数						
						description="";
						disName=pointName;
						branchLength=0.0;
			    	}else if(wtdls.size()==1){//如果只有一段
			    			description=disName+"——"+getNameByPoint(pointCuidMap.get("END_POINT_CUID").toString());
			    			branchLength+=length;			
			    			tmpResType=resType;			
			    			recordDetailMap(detailList,checkMonth,statMap.get("CUID").toString(), districtCuid, wireSegCuid,wireSegName, wireSystemLabelCn, description, branchLength, 0.0, tmpResType,true,isSub);										
			    	}
			    	tmpResType=resType;
			    	branchLength+=length;
			    	lineBranchCuid=tmpLineBranchCuid;	
				}
			}
			//预留信息
			if(StatConst.isStatWireRemain){
				Map<String, Double> remainMap = calculateRemain(wireRems);				
				String resType="";			
				if(systemLevel == 1 || systemLevel == 2){//干线
					resType="TRUNK_WIRE_REMAIN";
					if (remainMap.get("ALL_LENGTH")!=null) {
						tmpTrunkWireRemainLength+=remainMap.get("ALL_LENGTH");
					}
				}else if(systemLevel == 3 || systemLevel == 4 || systemLevel == 5){//本地
					resType="LOCAL_WIRE_REMAIN";
					if (remainMap.get("ALL_LENGTH")!=null) {
						tmpLocalWireRemainLength+=remainMap.get("ALL_LENGTH");
					}
				}
				tmpAllRemain=tmpTrunkWireRemainLength+tmpLocalWireRemainLength;	
				calculateRemainDetail(detailList,checkMonth,districtCuid, wireSystemLabelCn,wireSegCuid, wireSegName, resType,statMap.get("CUID").toString(), wireRems,isSub);
			}
			double localAllSeg=tmpLocalDuctLength+tmpLocalPolewayLength+tmpLocalStonewayLength+tmpLocalUplineLength+tmpLocalHangwallLength;
			double trunkAllSeg=tmpTrunkDuctLength+tmpTrunkPolewayLength+tmpTrunkStonewayLength+tmpTrunkUplineLength+tmpTrunkHangwallLength;
			double allSeg=localAllSeg+trunkAllSeg;
			double alllength=tmpAllRemain+allSeg;
			updateMap.put("LOCAL_DUCT_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_DUCT_SEG")) + StatHelper.formatExt(tmpLocalDuctLength));
			updateMap.put("LOCAL_POLEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_POLEWAY_SEG")) + StatHelper.formatExt(tmpLocalPolewayLength));
			updateMap.put("LOCAL_STONEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_STONEWAY_SEG")) + StatHelper.formatExt(tmpLocalStonewayLength));
			updateMap.put("LOCAL_UP_LINE_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_UP_LINE_SEG")) + StatHelper.formatExt(tmpLocalUplineLength));
			updateMap.put("LOCAL_HANG_WALL_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_HANG_WALL_SEG")) + StatHelper.formatExt(tmpLocalHangwallLength));
			updateMap.put("LOCAL_WIRE_REMAIN", StatHelper.getDoubleByObj(statMap.get("LOCAL_WIRE_REMAIN")) + StatHelper.formatExt(tmpLocalWireRemainLength));
			updateMap.put("LOCAL_ALL_SEG", StatHelper.getDoubleByObj(statMap.get("LOCAL_ALL_SEG")) + StatHelper.formatExt(localAllSeg));
			
			updateMap.put("TRUNK_DUCT_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_DUCT_SEG")) + StatHelper.formatExt(tmpTrunkDuctLength));
			updateMap.put("TRUNK_POLEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_POLEWAY_SEG")) + StatHelper.formatExt(tmpTrunkPolewayLength));
			updateMap.put("TRUNK_STONEWAY_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_STONEWAY_SEG")) + StatHelper.formatExt(tmpTrunkStonewayLength));
			updateMap.put("TRUNK_UP_LINE_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_UP_LINE_SEG")) + StatHelper.formatExt(tmpTrunkUplineLength));
			updateMap.put("TRUNK_HANG_WALL_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_HANG_WALL_SEG")) + StatHelper.formatExt(tmpTrunkHangwallLength));
			updateMap.put("TRUNK_WIRE_REMAIN", StatHelper.getDoubleByObj(statMap.get("TRUNK_WIRE_REMAIN")) + StatHelper.formatExt(tmpTrunkWireRemainLength));
			updateMap.put("TRUNK_ALL_SEG", StatHelper.getDoubleByObj(statMap.get("TRUNK_ALL_SEG")) + StatHelper.formatExt(trunkAllSeg));

			updateMap.put("ALL_SEG", StatHelper.getDoubleByObj(statMap.get("ALL_SEG")) + StatHelper.formatExt(allSeg));
			updateMap.put("ALL_REMAIN", StatHelper.getDoubleByObj(statMap.get("ALL_REMAIN")) + StatHelper.formatExt(tmpAllRemain));
			updateMap.put("ALL_LENGTH", StatHelper.getDoubleByObj(statMap.get("ALL_LENGTH")) + StatHelper.formatExt(alllength));
			
			
			formatExt("LOCAL_DUCT_SEG",updateMap);
			formatExt("LOCAL_POLEWAY_SEG",updateMap);
			formatExt("LOCAL_STONEWAY_SEG",updateMap);
			formatExt("LOCAL_UP_LINE_SEG",updateMap);
			formatExt("LOCAL_HANG_WALL_SEG",updateMap);
			formatExt("LOCAL_WIRE_REMAIN",updateMap);
			formatExt("LOCAL_ALL_SEG",updateMap);
			
			formatExt("TRUNK_DUCT_SEG",updateMap);
			formatExt("TRUNK_POLEWAY_SEG",updateMap);
			formatExt("TRUNK_STONEWAY_SEG",updateMap);
			formatExt("TRUNK_UP_LINE_SEG",updateMap);
			formatExt("TRUNK_HANG_WALL_SEG",updateMap);
			formatExt("TRUNK_WIRE_REMAIN",updateMap);
			formatExt("TRUNK_ALL_SEG",updateMap);
			
			formatExt("ALL_SEG",updateMap);
			formatExt("ALL_REMAIN",updateMap);
			formatExt("ALL_LENGTH",updateMap);
			doInsertDetailObject(detailList, "WIRE_DETAIL");
			updateObject("IbatisSdeDAO", lengthTableName, updateMap, pkMap);				
		}
		private List getListByParams(String dataSource, String sql){
			List list = new ArrayList();
			try {
				if(StringUtils.isNotEmpty(sql)){
					IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
					list = ibatisDAO.querySql(sql);
				}
			} catch (Exception e) {
				LogHome.getLog().error("查询数据库出错",e);
			}
			return list;
		}
		
		private void insertObject(String dataSource,String tableName,Map dataMap){
			try{
				IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
				ibatisDAO.insertDynamicTable(tableName, dataMap);
			}catch(Exception e)
			{
				LogHome.getLog().error("新增数据出错", e);
			}
		}
		
		private void updateObject(String dataSource,String tableName,Map updateMap,Map pkMap){
			try{
				IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
				ibatisDAO.updateDynamicTable(tableName, updateMap, pkMap);
				
			}catch(Exception e)
			{
				LogHome.getLog().error("修改数据出错", e);
			}
		}
		
		private void deleteObject(String dataSource,String sql){
			try{
				IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
				ibatisDAO.deleteSql(sql);
			}catch(Exception e)
			{
				LogHome.getLog().error("删除数据出错", e);
			}
		}
		
		private void insertObjectBatch(String dataSource,String tableName,List list){
			try {
				IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataSource);
				ibatisDAO.insertDynamicTableBatch(tableName, list);
			} catch (Exception e) {
				LogHome.getLog().error("批量新增数据出错", e);
			}
		}
	}
