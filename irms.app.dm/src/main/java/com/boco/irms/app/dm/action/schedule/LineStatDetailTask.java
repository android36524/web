package com.boco.irms.app.dm.action.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.id.CUIDHexGenerator;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.spring.SysProperty;
import com.boco.irms.app.dm.action.multithread.ITaskThreadPool;
import com.boco.irms.app.dm.action.multithread.IThreadPoolTaskHandler;
import com.boco.irms.app.dm.action.multithread.ThreadPoolUtils;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
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
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.dao.base.DaoHelper;

//根据所属分公司进行光缆以及管线长度的统计
public class LineStatDetailTask extends JobWorking {
	
	private int year = 2015;
	private int month = 1;
	private Map <String,String> disMap = new HashMap<String,String>();
	private Map <String,String> disLabelMap = new HashMap<String,String>();
	
	//["未知","省际","省内","本地骨干","本地汇聚","本地接入"]
	private int[] systemLevel = {0,1,2,3,4,5}; 
	
	//["未知","自建","共建","合建","附挂/附穿","租用","购买","置换"]
	private int[] ownership = {0,1,2,3,4,5,6,7};
	
	@Override
	public void processJob() {
		//根据当前时间得到对应的月份
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		month = calendar.get(Calendar.MONTH)+1;
		year = calendar.get(Calendar.YEAR);
		
		//根据核查月份删除数据
		deleteData(month);
		//取得区域的父子关系
		//取得所有区域的CUID
		String district = SysProperty.getInstance().getValue("district","DISTRICT-00001");
		String disSql = "SELECT CUID,LABEL_CN,RELATED_SPACE_CUID FROM DISTRICT WHERE RELATED_SPACE_CUID LIKE '" + district + "%'" ;
		List<Map<String, Object>> districts = getListByParams("IbatisTransDAO", disSql);
		for(Map<String, Object> map : districts){
			String disCuid = (String) map.get(District.AttrName.cuid);
			String parentCuid = (String) map.get(District.AttrName.relatedSpaceCuid);
			String labelCn = (String) map.get(District.AttrName.labelCn);
			disMap.put(disCuid, parentCuid);
			disLabelMap.put(disCuid, labelCn);
		}
		//调用增加任务的接口
		ThreadPoolUtils.synExecuteThreadPool(new IThreadPoolTaskHandler<List>() {

			public void addTasks(final ITaskThreadPool<List> taskThreadPool) {
				try {
					String district = SysProperty.getInstance().getValue("district","DISTRICT-00001");
					String sql = "SELECT CUID FROM DISTRICT WHERE CUID LIKE '" + district + "%'";
					List<Map<String, Object>> list = getListByParams("IbatisSdeDAO", sql);
					if(list != null && list.size() > 0){
						LogHome.getLog().info("list:     "+list);
						for(Map map:list){
							String cuid = (String) map.get("CUID");
							List task = new ArrayList();
							task.add(cuid);
							taskThreadPool.addTask(task);
							LogHome.getLog().info("地区:     "+cuid);
						}
					}
				} catch (Exception e) {
					LogHome.getLog().error(e, e);
				}
			}

			public void doTask(List task) {
				try {
					LogHome.getLog().info("doTask:     size():"+task.size());
					if(task != null && task.size() > 0){		
						String districtCuid = (String) task.get(0);
						if(isStatByDis(districtCuid)){
							LogHome.getLog().info("开始统计正常的"+districtCuid);
							//正常的
							doAnalyseWire(districtCuid,null);
							LogHome.getLog().info("开始统计代维的"+districtCuid);
							//代维的
							doAnalyseWire(districtCuid,new HashMap<String,Integer>());
							LogHome.getLog().info("开始统计管道的"+districtCuid);
							//管道的
							doAnalyseDuct(districtCuid);
						}
					}									
				} catch (Exception e) {
					LogHome.getLog().error(e.getMessage(), e);
				}
				
			}
			
		});
		Date finishDate = new Date();
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = d.format(finishDate);
		LogHome.getLog().info("按区域核查结束,当前时间是      "+timeStr);
	}
	
	private void deleteData(int month){
		String sqlDelFault = "DELETE FROM DUCT_LINE_LENGTH_DET WHERE CHECK_MONTH = " + month;
		String sqlDelRes = "DELETE FROM WIRE_LENGTH_DET WHERE CHECK_MONTH = " + month;
		deleteObject("IbatisSdeDAO", sqlDelFault);
		deleteObject("IbatisSdeDAO", sqlDelRes);
	}
	
	//只有是县级区域才进行统计
	private boolean isStatByDis(String districtCuid){
		if(districtCuid != null && districtCuid.length() > 0){
			String [] strs = districtCuid.split("-");
			if(strs.length == 5){
				return true;
			}
		}
		return false;
	}
	
	private void doAnalyseWire(String districtCuid,Map<String,Integer> cacheMap){
		Map <String,Map<String ,Object>> resulstMap = new HashMap<String,Map<String ,Object>>();
		String wireSysSql = "SELECT CUID,LABEL_CN,SYSTEM_LEVEL FROM WIRE_SYSTEM WHERE RELATED_SPACE_CUID = '" + districtCuid + "'";
		List<Map<String, Object>> wireSystems = getListByParams("IbatisTransDAO", wireSysSql);
		if(wireSystems !=null && wireSystems.size() > 0){
//			
			//经过的管道的预留长度
			double remainCrossDuct = 0.0;
			//经过的杆路的预留长度
			double remainCrossPole = 0.0;
			//经过的直埋的预留长度
			double remainCrossStone = 0.0;
			for(Map wireSystem : wireSystems){
				String wireSystemCuid = (String) wireSystem.get(GenericDO.AttrName.cuid);
				Integer systemLevel = StatHelper.getIntByObj(wireSystem.get(WireSystem.AttrName.systemLevel));
				String wireSegSql = "SELECT CUID FROM WIRE_SEG WHERE RELATED_SYSTEM_CUID = '" + wireSystemCuid + "'";
				List<Map<String, Object>> wireSegs = getListByParams("IbatisTransDAO", wireSegSql);
				if(wireSegs != null && wireSegs.size() > 0){
					for(Map wireSeg : wireSegs){
//						//存放有问题的光缆的map

						String wireSegCuid=(String) wireSeg.get(GenericDO.AttrName.cuid);

						String wireToDuctLineSql="SELECT LINE_SEG_CUID FROM WIRE_TO_DUCTLINE WHERE WIRE_SEG_CUID='"+wireSegCuid+"'";
						List<Map<String, Object>>wtdls = getListByParams("IbatisTransDAO", wireToDuctLineSql);
						if(wtdls==null||wtdls.isEmpty()){
							continue;
						}
						//一条光缆经过的承载段的长度
						for(Map pointCuidMap:wtdls){
							String lineSegCuid=(String) pointCuidMap.get("LINE_SEG_CUID");
							String className=GenericDO.parseClassNameFromCuid(lineSegCuid);
							String sql="SELECT LENGTH FROM "+className+" WHERE CUID='"+lineSegCuid+"'";
							List<Map<String, Object>> list = getListByParams("IbatisTransDAO", sql);
							Map<String, Object>lineMap=null;
							if(list!=null&&list.size()>0){
								lineMap=list.get(0);
							}else{
								continue;
							}
							double lineLength=StatHelper.getDoubleByObj(lineMap.get(DuctSeg.AttrName.length));
							
							if(cacheMap!=null){
								lineLength = getLengthExt(lineLength, lineSegCuid, cacheMap);
							}
							String gridSql = "select GRID_NAME from T_GRID_TO_RES where RES_CUID = '"+lineSegCuid+"'";
							List<Map<String, Object>> grids = getListByParams("IbatisTransDAO", gridSql);

//							String remark=(String) lineMap.get("REMARK");
//							if(remark==null||remark.trim().length()<1){
//								String disLabel=this.disLabelMap.get(districtCuid);
//								remark=disLabel+"分公司";
//							}
//							String []remarks=remark.split(",");
							for(Map<String, Object> grid : grids){
								String gridName = (String)grid.get("GRID_NAME");
								
								if(cacheMap == null){
									//光缆皮长统计：按照光缆系统级别区分
									if(systemLevel != null){
										Map<String,Object> res = resulstMap.get(gridName+"_"+systemLevel.intValue());
										if(res == null){
											res = new HashMap <String,Object>();
											resulstMap.put(gridName+"_"+systemLevel.intValue(), res);
											String cuid = CUIDHexGenerator.getInstance().generate("WIRE_LENGTH_DET");
											res.put(GenericDO.AttrName.cuid, cuid);
											res.put("RELATED_DISTRICT_CUID", districtCuid);
											res.put("PARENT_DISTRICT_CUID", disMap.get(districtCuid));
											res.put("BRANCH_COMPANY", gridName);
											res.put("CREATE_TIME", new Date());
											res.put("CHECK_MONTH", this.month);
											res.put("CHECK_YEAR", this.year);
											res.put("TYPE", 1);
											res.put("SYSTEM_LEVEL", systemLevel.intValue());
										}
										addLengthByType(lineLength, res,className);
										addLengthByType(lineLength, res,"ALL_SEG");
									}
								}else{
									//代维长度统计： 不按照光缆系统级别区分
									Map<String,Object> res = resulstMap.get(gridName);
									if(res == null){
										res = new HashMap <String,Object>();
										resulstMap.put(gridName, res);
										String cuid = CUIDHexGenerator.getInstance().generate("WIRE_LENGTH_DET");
										res.put(GenericDO.AttrName.cuid, cuid);
										res.put("RELATED_DISTRICT_CUID", districtCuid);
										res.put("PARENT_DISTRICT_CUID", disMap.get(districtCuid));
										res.put("BRANCH_COMPANY", gridName);
										res.put("CREATE_TIME", new Date());
										res.put("CHECK_MONTH", this.month);
										res.put("CHECK_YEAR", this.year);
										res.put("TYPE", 2);
									}
									addLengthByType(lineLength, res,className);
									addLengthByType(lineLength, res,"ALL_SEG");
								}
								
							}
						}
						
//						String wireRemainSql="SELECT REMAIN_LENGTH,RELATED_LOCATION_CUID FROM WIRE_REMAIN WHERE RELATED_WIRE_SEG_CUID='"+wireSegCuid+"'";;
//						List<Map<String, Object>>wireRems = getListByParams("IbatisTransDAO", wireRemainSql);
//						if(wireRems!=null){
//							for(Map remain:wireRems){
//								Double tmpLength=StatHelper.getDoubleByObj(remain.get(WireRemain.AttrName.remainLength));
//								if(tmpLength==null){
//									tmpLength=0.0;
//								}
//								String pointCuid=(String) remain.get(WireRemain.AttrName.relatedLocationCuid);
//								if(DaoHelper.isNotEmpty(pointCuid)){
//									String className=GenericDO.parseClassNameFromCuid(pointCuid);
//									if(Pole.CLASS_NAME.equals(className)){
//										remainCrossPole+=tmpLength;
//									}else if(Stone.CLASS_NAME.equals(className)){
//										remainCrossStone+=tmpLength;
//									}else{
//										remainCrossDuct+=tmpLength;
//									}
//								}
//							}
//						}
					}
				}
			}
			if(!resulstMap.isEmpty()){
//				Map<String ,Object> addRemainMap = (Map<String, Object>) resulstMap.values().toArray()[0];
//				addLengthByType(remainCrossDuct, addRemainMap,DuctSeg.CLASS_NAME);
//				addLengthByType(remainCrossPole, addRemainMap,PolewaySeg.CLASS_NAME);
//				addLengthByType(remainCrossStone, addRemainMap,StonewaySeg.CLASS_NAME);
//				addLengthByType((remainCrossDuct+remainCrossPole+remainCrossStone), addRemainMap,"ALL_SEG");
				
				//光缆皮长长度统计：如果某县区某一个系统级别的光缆不存在，则长度都默认0
				Map <String,Map<String ,Object>> resMap = new HashMap<String,Map<String ,Object>>();
				if(cacheMap == null){
					Iterator<Map<String, Object>> iter = resulstMap.values().iterator();
					while(iter.hasNext()){
						Map <String, Object> map = iter.next();
						String branchCompany = (String) map.get("BRANCH_COMPANY");
						String disCuid = (String) map.get("RELATED_DISTRICT_CUID");
						for(int i = 0; i < systemLevel.length; i++){
							Map<String,Object> res = resulstMap.get(branchCompany+"_"+systemLevel[i]);
							if(res == null){
								res = new HashMap <String,Object>();
								resMap.put(branchCompany+"_"+systemLevel[i], res);
								String cuid = CUIDHexGenerator.getInstance().generate("WIRE_LENGTH_DET");
								res.put(GenericDO.AttrName.cuid, cuid);
								res.put("RELATED_DISTRICT_CUID", disCuid);
								res.put("PARENT_DISTRICT_CUID", disMap.get(disCuid));
								res.put("BRANCH_COMPANY", branchCompany);
								res.put("CREATE_TIME", new Date());
								res.put("CHECK_MONTH", this.month);
								res.put("CHECK_YEAR", this.year);
								res.put("TYPE", 1);
								res.put("SYSTEM_LEVEL", systemLevel[i]);
								res.put(DuctSeg.CLASS_NAME, 0.0);
								res.put(PolewaySeg.CLASS_NAME, 0.0);
								res.put(StonewaySeg.CLASS_NAME, 0.0);
								res.put(UpLineSeg.CLASS_NAME, 0.0);
								res.put(HangWallSeg.CLASS_NAME, 0.0);
								res.put("ALL_SEG", 0.0);
							}
						}
					}
					
					for(Map.Entry entry : resMap.entrySet()){
						Map<String,Object> map = (Map<String, Object>) entry.getValue();
						resulstMap.put((String)entry.getKey(), map);
					}
					
				}
				
				
				Iterator<Map<String, Object>> it = resulstMap.values().iterator();
				while(it.hasNext()){
					Map <String, Object> map = it.next();
					formatByKey(map,DuctSeg.CLASS_NAME);
					formatByKey(map,PolewaySeg.CLASS_NAME);
					formatByKey(map,StonewaySeg.CLASS_NAME);
					formatByKey(map,HangWallSeg.CLASS_NAME);
					formatByKey(map,UpLineSeg.CLASS_NAME);
					formatByKey(map,"ALL_SEG");
					insertObject("IbatisSdeDAO", "WIRE_LENGTH_DET", map);
				}
			}
		}

	}
	
	private void formatByKey(Map <String, Object>map,String key){
		Double length = StatHelper.getDoubleByObj(map.get(key));
		length = StatHelper.formatExt(length);
		map.put(key, length);
	}
	
	private void doAnalyseDuct(String districtCuid){
		Map <String,Map<String ,Object>>resulstMap=new HashMap <String,Map<String ,Object>>();
		this.doAnalyseDuctByType(districtCuid, DuctSystem.CLASS_NAME, DuctSeg.CLASS_NAME, DuctSeg.CLASS_NAME, resulstMap);
		this.doAnalyseDuctByType(districtCuid, PolewaySystem.CLASS_NAME, PolewaySeg.CLASS_NAME, PolewaySeg.CLASS_NAME, resulstMap);
		this.doAnalyseDuctByType(districtCuid, StonewaySystem.CLASS_NAME, StonewaySeg.CLASS_NAME, StonewaySeg.CLASS_NAME, resulstMap);
		this.doAnalyseDuctByType(districtCuid, UpLine.CLASS_NAME, UpLineSeg.CLASS_NAME, UpLineSeg.CLASS_NAME, resulstMap);
		this.doAnalyseDuctByType(districtCuid, HangWall.CLASS_NAME, HangWallSeg.CLASS_NAME, HangWallSeg.CLASS_NAME, resulstMap);
		
		//承载段长度统计：如果某县区某一个产权的承载段不存在，则长度都默认0
		Map <String,Map<String ,Object>> resMap = new HashMap<String,Map<String ,Object>>();
		Iterator<Map<String, Object>> iter = resulstMap.values().iterator();
		while(iter.hasNext()){
			Map <String, Object> map = iter.next();
			String branchCompany = (String) map.get("BRANCH_COMPANY");
			String disCuid = (String) map.get("RELATED_DISTRICT_CUID");
			for(int i = 0; i < ownership.length; i++){
				Map<String,Object> res = resulstMap.get(branchCompany+"_"+ownership[i]);
				if(res == null){
					res = new HashMap <String,Object>();
					resMap.put(branchCompany+"_"+ownership[i], res);
					String cuid = CUIDHexGenerator.getInstance().generate("DUCT_LINE_LENGTH_DET");
					res.put(GenericDO.AttrName.cuid, cuid);
					res.put("RELATED_DISTRICT_CUID", disCuid);
					res.put("PARENT_DISTRICT_CUID", disMap.get(disCuid));
					res.put("BRANCH_COMPANY", branchCompany);
					res.put("CREATE_TIME", new Date());
					res.put("CHECK_MONTH", this.month);
					res.put("CHECK_YEAR", this.year);
					res.put("OWNERSHIP", ownership[i]);
					res.put(DuctSeg.CLASS_NAME, 0.0);
					res.put(PolewaySeg.CLASS_NAME, 0.0);
					res.put(StonewaySeg.CLASS_NAME, 0.0);
					res.put(UpLineSeg.CLASS_NAME, 0.0);
					res.put(HangWallSeg.CLASS_NAME, 0.0);
					res.put("ALL_SEG", 0.0);
				}
			}
		}
		
		for(Map.Entry entry : resMap.entrySet()){
			Map<String,Object> map = (Map<String, Object>) entry.getValue();
			resulstMap.put((String)entry.getKey(), map);
		}
	
		if(!resulstMap.isEmpty()){
			Iterator<Map<String, Object>> it=resulstMap.values().iterator();
			while(it.hasNext()){
				Map <String, Object>map=it.next();
				formatByKey(map,DuctSeg.CLASS_NAME);
				formatByKey(map,PolewaySeg.CLASS_NAME);
				formatByKey(map,StonewaySeg.CLASS_NAME);
				formatByKey(map,HangWallSeg.CLASS_NAME);
				formatByKey(map,UpLineSeg.CLASS_NAME);
				formatByKey(map,"ALL_SEG");
				insertObject("IbatisSdeDAO", "DUCT_LINE_LENGTH_DET", map);
			}
		}
	}
	private void doAnalyseDuctByType(String districtCuid,String systemTable,String segTable,String attrName,Map <String,Map<String ,Object>>resulstMap){
		String wireSysSql="SELECT CUID FROM "+systemTable+" WHERE RELATED_SPACE_CUID='"+districtCuid+"'";
		List<Map<String, Object>>lineSystems = getListByParams("IbatisTransDAO", wireSysSql);
		if(lineSystems!=null&&lineSystems.size()>0){
			for(Map lineSystem:lineSystems){
				String lineSystemCuid=(String) lineSystem.get(GenericDO.AttrName.cuid);
				String segSql="SELECT CUID,LENGTH,OWNERSHIP FROM "+segTable+" WHERE RELATED_SYSTEM_CUID='"+lineSystemCuid+"'";
				List<Map<String, Object>>segs = getListByParams("IbatisTransDAO", segSql);
				if(segs!=null&&segs.size()>0){
					
					for(Map seg:segs){
						Integer ownership = StatHelper.getIntByObj(seg.get(DuctSeg.AttrName.ownership));
						Double length=StatHelper.getDoubleByObj(seg.get(DuctSeg.AttrName.length));
						String lineCuid = (String)seg.get(DuctSeg.AttrName.cuid);
						if(length==null||length<=0){
							continue;
						}
//						String remark=(String) seg.get(DuctSeg.AttrName.remark);
//						if(remark==null||remark.trim().length()<1){
//							String disLabel=this.disLabelMap.get(districtCuid);
//							remark=disLabel+"分公司";
//						}
//						String []remarks=remark.split(",");
						String gridSql = "select GRID_NAME from T_GRID_TO_RES where RES_CUID = '"+lineCuid+"'";
						List<Map<String, Object>> grids = getListByParams("IbatisTransDAO", gridSql);
						for(Map<String, Object> grid : grids){
							String gridName = (String)grid.get("GRID_NAME");
							if(ownership != null){
								//承载长度统计：按照产权细分
								Map <String,Object> res = resulstMap.get(gridName+"_"+ownership.toString());
								if(res == null){
									res=new HashMap <String,Object>();
									resulstMap.put(gridName+"_"+ownership.toString(), res);
									String cuid = CUIDHexGenerator.getInstance().generate("DUCT_LINE_LENGTH_DET");
									res.put(GenericDO.AttrName.cuid, cuid);
									res.put("RELATED_DISTRICT_CUID", districtCuid);
									res.put("PARENT_DISTRICT_CUID", disMap.get(districtCuid));
									res.put("BRANCH_COMPANY", gridName);
									res.put("CREATE_TIME", new Date());
									res.put("CHECK_MONTH", this.month);
									res.put("CHECK_YEAR", this.year);
									res.put("OWNERSHIP", ownership.intValue());
									res.put(DuctSeg.CLASS_NAME, 0.0);
									res.put(PolewaySeg.CLASS_NAME, 0.0);
									res.put(StonewaySeg.CLASS_NAME, 0.0);
									res.put(UpLineSeg.CLASS_NAME, 0.0);
									res.put(HangWallSeg.CLASS_NAME, 0.0);
									res.put("ALL_SEG", 0.0);
									
								}
//								length=DMHelper.formatExt(length);
								addLengthByType(length, res,attrName);
								addLengthByType(length, res,"ALL_SEG");	
							}
						}
					}
				}
			}
		}
	}
	
	private void addLengthByType(double tmpDuctLength, Map<String, Object> res,String key) {
		if(res.get(key) == null){
			res.put(key, tmpDuctLength);
		}else{
			Double length = StatHelper.getDoubleByObj( res.get(key));
			length += tmpDuctLength;
			res.put(key, length);
		}
	}
	
	private double getLengthByCuid(String cuid){
		String className = GenericDO.parseClassNameFromCuid(cuid);
		String sql = "SELECT LENGTH FROM " + className + " WHERE CUID = '" + cuid + "'";
		List<Map<String, Object>> list = getListByParams("IbatisTransDAO", sql);
		if(list != null && list.size() > 0){
			Double length = StatHelper.getDoubleByObj(list.get(0).get(DuctSeg.AttrName.length));
			if(length != null){
				return length;
			}
		}
		
		return 0.0;
	}
	
	private double getLengthExt(double length,String segCuid,Map<String,Integer> lineMap){
		double subLength = length;
		Integer count = lineMap.get(segCuid);
		if(count == null){
			count = 0;
		}else if(1 <= count && count <= 3){
			subLength = length * 0.1;
		}else{
			subLength = 0.0;
		}
		count++;
		lineMap.put(segCuid, count);
		return subLength;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> getListByParams(String dataSource, String sql){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
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
}
