package com.boco.irms.app.dm.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.spring.SysProperty;

/**
 * @author Administrator
 * 资源勘察服务
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class DmResExplorerAction {

	//根据用户名模糊查询用户(irms3.5)
	public List getUsersByName(String name){
		String sql = "select customer_name name,customer_address addr,longitude,latitude from T_RC_CUSTOMER where customer_name like '%" + name + "%'";
		List list = new ArrayList();
		if(sql != ""){
			list = this.irmsQueryForList(sql,0,1000);
			addSNToList(list);	
		}
		return list;
	}
	//查询结果添加SN序号
	private void addSNToList(List list){
		if(list != null && list.size() > 0){
			for(int j = 0; j < list.size(); j++){
				if(list.get(j) instanceof HashMap){
					HashMap map = (HashMap)list.get(j);
					map.put("SN", j+1);
				}
			}
		}
	}
	private List irmsQueryForList(String sql,int skipResults,int maxResults){
		List aryList = new ArrayList();
		try{
			IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			List list = ibatisDAO.querySql(sql, skipResults, maxResults);
			Iterator it = list.iterator();
			while(it.hasNext()){
				Map map = (HashMap) it.next();
				map = this.parseHashMap_Key_toUpperCase(map);
				aryList.add(map);
			}
		}catch(Exception ex){
			LogHome.getLog().error(ex.getMessage(),ex);
		}
		return aryList;
	}	
	
	/**
	 * 集客勘查
	 * @param datas 勘查资源
	 * @return
	 */
	public List<Map<String,Object>> dealResExplore(List<Map<String,String>> datas){
		long start = System.currentTimeMillis();
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Map<String,Object>> resultMap = new HashMap<String,Map<String,Object>>();
		List<Map<String,Object>> tempResult = new ArrayList<Map<String,Object>>();
		List<String> accessScenes = new ArrayList<String>();
		List<String> rooms = new ArrayList<String>();
		Map<String,List> sceneRooms = new HashMap<String,List>();
		Map<String,Map<String,Object>> sceneMinCost = new HashMap<String,Map<String,Object>>();
		Map<String,Object> minCost = new HashMap<String,Object>();
		Map<String,Map<String,Object>> room2Device = new HashMap<String,Map<String,Object>>();
		List<Map<String,Object>> allSceneDatas = new ArrayList<Map<String,Object>>();
		
		List<String> sceneEmum = new ArrayList<String>();
		sceneEmum.add("综合业务机房");
		sceneEmum.add("基站机房");
		sceneEmum.add("客户机房");
		sceneEmum.add("预覆盖");
		
		Map<String,Object> summationMap = new HashMap<String,Object>();
		summationMap.put("ACCESS_SCENE", "存量资源合计");
		summationMap.put("ACCESSPOINT_NUM", 0);
		summationMap.put("FREE_PORT_NUM", 0);
		summationMap.put("FREE_FIBER_NUM", 0);
		summationMap.put("OPTIMAL_ACCESSPOINT", "");
		summationMap.put("FORECAST_COST", 0);
		summationMap.put("FORECAST_TIME", 0);
		summationMap.put("DATA", new ArrayList<Map<String,Object>>());
		
		
		//预估工期与预估费用默认值
		Map<String,Double> defaultValue = new HashMap<String,Double>();
		defaultValue.put("WIRE_COST_PER_KM", converToDouble(SysProperty.getInstance().getValue("WIRE_COST_PER_KM")));
		defaultValue.put("CONSTRUCTION_PERIOD", converToDouble(SysProperty.getInstance().getValue("CONSTRUCTION_PERIOD")));
		defaultValue.put("DISTANCE_RATE", converToDouble(SysProperty.getInstance().getValue("DISTANCE_RATE")));
		defaultValue.put("INSTALLATION_COST", converToDouble(SysProperty.getInstance().getValue("INSTALLATION_COST")));
		defaultValue.put("SDH_DEVICE_COST", converToDouble(SysProperty.getInstance().getValue("SDH_DEVICE_COST")));
		defaultValue.put("PTN_DEVICE_COST", converToDouble(SysProperty.getInstance().getValue("PTN_DEVICE_COST")));
		
		Map<String,List<String>> type2cuidMap = new HashMap<String,List<String>>();
		Map<String,Double> cuid2distanceMap = new HashMap<String,Double>();
		
		for(Map<String,String> map : datas){
			String cuid = (String) map.get("CUID");
			String type = (String) map.get("TYPE");
			String distance = (String)map.get("DISTANCE");
			
			List<String> item = type2cuidMap.get(type);
			if(item == null){
				item = new ArrayList<String>();
			}
			item.add(cuid);
			type2cuidMap.put(type, item);
			
			cuid2distanceMap.put(cuid, converToDouble(distance));
		}
		long startSiteSearch = System.currentTimeMillis();
		System.out.println("查询站点内资源时间毫秒:"+(startSiteSearch - start));
		//站点内资源
		List sites = type2cuidMap.get("SITE");			
		if(sites != null && sites.size() > 0)
		{
			//查询odf，综合机架，终端盒
			List<Map<String,Object>> list = getAccessPointsBySites(sites);
			if(list != null && list.size() > 0){
				for(Map<String,Object> accessPoint : list){
					String accCuid = (String) accessPoint.get("CUID");
					String siteCuid = (String)accessPoint.get("RELATED_SITE_CUID");
					String resType = accCuid.split("-")[0];
					Double distance = cuid2distanceMap.get(siteCuid);
					
					List<String> item = type2cuidMap.get(resType);						
					if(item == null)
						item = new ArrayList<String>();
					item.add(accCuid);
					type2cuidMap.put(resType, item);						
					cuid2distanceMap.put(accCuid, distance);			
				}
			}
		}
		long endSiteSearch = System.currentTimeMillis();
		System.out.println("查询站点内资源时间毫秒:"+(endSiteSearch - startSiteSearch));
		
		for(Map.Entry<String, List<String>> entry : type2cuidMap.entrySet())
		{
			String type = entry.getKey();
			List cuids = entry.getValue();
			
			if(!"SITE".equals(type))
			{
				List<Map<String,Object>> accessList = getWireSegsByAccesspoint(cuids,type,cuid2distanceMap,defaultValue);
				//setAccessWayAndFreePort(accessList,room2Device);
				tempResult.addAll(accessList);
			}
		}
		long endResSearch = System.currentTimeMillis();
		System.out.println("资源查询时间毫秒:"+(endResSearch - endSiteSearch));
		
		//分场景计算
		for(Map<String,Object> map : tempResult){
			String scene = map.get("ACCESS_SCENE").toString();
			if(StringUtils.isNotBlank(scene)){
				scene = scene.trim();
				if(accessScenes.contains(scene)){
					Map<String,Object> sceneMap = resultMap.get(scene);
					
					//计算可接入点数
					if(map.get("RELATED_ROOM_CUID") == null){
						sceneMap.put("ACCESSPOINT_NUM", Integer.parseInt(sceneMap.get("ACCESSPOINT_NUM").toString())+1);
					}else{
						String roomCuid = map.get("RELATED_ROOM_CUID").toString();
						List sceneRoom = sceneRooms.get(scene);
						if(!sceneRoom.contains(roomCuid)){
							sceneMap.put("ACCESSPOINT_NUM", Integer.parseInt(sceneMap.get("ACCESSPOINT_NUM").toString())+1);
						}
					}
					
					//计算空闲端口
					BigDecimal sceneFreePortNum =new BigDecimal(sceneMap.get("FREE_PORT_NUM").toString());
					BigDecimal freePortNum = new BigDecimal(map.get("FREE_PORT_NUM").toString());
					sceneMap.put("FREE_PORT_NUM", sceneFreePortNum.add(freePortNum));
					//计算空闲纤芯
					BigDecimal sceneFreeFiberNum = new BigDecimal(sceneMap.get("FREE_FIBER_NUM").toString());
					BigDecimal freeFiberNum = new BigDecimal(map.get("FREE_FIBER_NUM").toString());
					sceneMap.put("FREE_FIBER_NUM", sceneFreeFiberNum.add(freeFiberNum));
					
					//计算预估费用
					Map<String,Object> tempScene = sceneMinCost.get(scene);
					Double minForcastCost = Double.valueOf(tempScene.get("FORECAST_COST").toString());
					if(minForcastCost > Double.valueOf(map.get("FORECAST_COST").toString())){
						sceneMinCost.put(scene, map);
					}
					
					List sceneDatas = (List) sceneMap.get("DATA");
					sceneDatas.add(map);
					
				}else{
					accessScenes.add(scene);
					Map<String,Object> sceneMap = new HashMap<String,Object>();
					sceneMap.put("ACCESS_SCENE", scene);
					sceneMap.put("ACCESSPOINT_NUM", 1);
					sceneMap.put("FREE_PORT_NUM", new BigDecimal(map.get("FREE_PORT_NUM").toString()));
					sceneMap.put("FREE_FIBER_NUM", new BigDecimal(map.get("FREE_FIBER_NUM").toString()));
					sceneMap.put("OPTIMAL_ACCESSPOINT", "");
					sceneMap.put("FORECAST_COST", Double.valueOf(map.get("FORECAST_COST").toString()));
					sceneMap.put("FORECAST_TIME", 0);
					
					List sceneDatas = new ArrayList();
					sceneDatas.add(map);
					allSceneDatas.addAll(sceneDatas);
					sceneMap.put("DATA", sceneDatas);
					
					List sceneRoom = new ArrayList();
					if(map.get("RELATED_ROOM_CUID") != null){
						sceneRoom.add(map.get("RELATED_ROOM_CUID").toString());
					}
					sceneRooms.put(scene, sceneRoom);
					sceneMinCost.put(scene, map);
					
					resultMap.put(scene, sceneMap);
				}
			}
		}
		
		//获取建议最优接入点、预估费用、预估工期
		for(String key : sceneMinCost.keySet()){
			Map<String,Object> minCostMap = sceneMinCost.get(key);
			Map<String,Object> sceneMap = resultMap.get(key);
			if(sceneMap != null){
				String type = minCostMap.get("TYPE").toString();
				if("ODF".equals(type) || "MISCRACK".equals(type) || "FIBER_JOINT_BOX".equals(type)){
					sceneMap.put("OPTIMAL_ACCESSPOINT", minCostMap.get("ROOM_NAME"));
				}else{
					sceneMap.put("OPTIMAL_ACCESSPOINT", minCostMap.get("LABEL_CN"));
				}
				sceneMap.put("FORECAST_COST", minCostMap.get("FORECAST_COST"));
				sceneMap.put("FORECAST_TIME", minCostMap.get("FORECAST_TIME"));
				
				int allAccesspointNum = Integer.parseInt(summationMap.get("ACCESSPOINT_NUM").toString());
				int accesspontNum = Integer.parseInt(sceneMap.get("ACCESSPOINT_NUM").toString());
				summationMap.put("ACCESSPOINT_NUM", allAccesspointNum+accesspontNum);
				
				BigDecimal allfreePortNum = new BigDecimal(summationMap.get("FREE_PORT_NUM").toString());
				BigDecimal freePortNum = new BigDecimal(sceneMap.get("FREE_PORT_NUM").toString());
				summationMap.put("FREE_PORT_NUM", allfreePortNum.add(freePortNum)); 
				
				BigDecimal allfreeFiberNum = new BigDecimal(summationMap.get("FREE_FIBER_NUM").toString());
				BigDecimal freeFiberNum = new BigDecimal(sceneMap.get("FREE_FIBER_NUM").toString());
				summationMap.put("FREE_FIBER_NUM", allfreeFiberNum.add(freeFiberNum)); 
				
				if(minCost.get("FORECAST_COST") == null){
					minCost = sceneMap;
				}else{
					Double minForeCast = Double.valueOf(minCost.get("FORECAST_COST").toString());
					Double sceneCost = Double.valueOf(sceneMap.get("FORECAST_COST").toString());
					if(sceneCost < minForeCast){
						minCost = sceneMap;
					}
				}
				
				List sceneDatas = (List) sceneMap.get("DATA");
				allSceneDatas = (List) summationMap.get("DATA");
				allSceneDatas.addAll(sceneDatas);
			}
			result.add(sceneMap);
		}
		long startFreePort = System.currentTimeMillis();
		System.out.println("接入方式空闲端口开始毫秒:"+(startFreePort - endResSearch));
		//获取接入点明细的接入方式、空闲端口
		setAccessWayAndFreePort(allSceneDatas,room2Device);
		long endFreePort = System.currentTimeMillis();
		System.out.println("接入方式空闲端口结束毫秒:"+(endFreePort - startFreePort));
		
		summationMap.put("OPTIMAL_ACCESSPOINT", minCost.get("OPTIMAL_ACCESSPOINT"));
		summationMap.put("FORECAST_COST", minCost.get("FORECAST_COST"));
		summationMap.put("FORECAST_TIME", minCost.get("FORECAST_TIME"));
		
		System.out.println("所有资源类型：" + sceneEmum.toString());
		System.out.println("已有资源类型：" + accessScenes.toString());
		
		for(int i = 0; i < sceneEmum.size(); i++){
			if(accessScenes.indexOf(sceneEmum.get(i)) == -1){
				System.out.println("缺少资源类型：" + sceneEmum.get(i));
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ACCESS_SCENE", sceneEmum.get(i));
				map.put("ACCESSPOINT_NUM", 0);
				map.put("FREE_PORT_NUM", 0);
				map.put("FREE_FIBER_NUM", 0);
				map.put("OPTIMAL_ACCESSPOINT", "");
				map.put("FORECAST_COST", 0);
				map.put("FORECAST_TIME", 0);
				map.put("DATA", new ArrayList<Map<String,Object>>());
				result.add(map);
			}
		}
		
		result.add(summationMap);
		long end = System.currentTimeMillis();
		System.out.println("后台总时间毫秒:"+(end - start));
		
		return result; 
	}
	
	/**
	 * 设置接入点接入方式和空闲端口
	 * @param accessList 机房内集客场景接入点
	 * @param room2Device 机房内设备类型
	 */
	private void setAccessWayAndFreePort(List accessList,Map room2Device){
		Set<String> roomset = new HashSet<String>();
		for(int i = 0; i < accessList.size(); i++){
			Map acc = (Map)accessList.get(i);
			if(acc.get("RELATED_ROOM_CUID") != null)
			{
				roomset.add(acc.get("RELATED_ROOM_CUID").toString());
			}
		}
		Iterator<String> ite = roomset.iterator();
		StringBuffer temp = new StringBuffer();
		int k = 0;
		while(ite.hasNext())
		{
			temp.append("'"+ite.next()+"'");
			if(k < roomset.size() -1)
				temp.append(",");
			k++;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ")
		  .append("decode(ne.signal_type,'1','SDH','2','PDH','3','WDM','4','SDH微波','5','混合','6','未知','7','PDH微波','8','PTN','9','PON','10','OLP') signal_type,")
		  .append("p.port_type,count(p.port_type) as port_type_count,ne.related_room_cuid ")
		  .append("from trans_element ne,ptp p ")
		  .append("where ne.cuid = p.related_ne_cuid ")
		  .append("and p.port_state = 1 ")
		  .append("and ne.related_room_cuid in ( ")
		  .append(temp)
		  .append(") group by signal_type,port_type,related_room_cuid ");
		//获取接入点明细的接入方式、空闲端口
		long startFreePort = System.currentTimeMillis();
		List<Map<String,Object>> neList = this.queryForList(sql.toString());
		long endFreePort = System.currentTimeMillis();
		System.out.println("查询机房接入毫秒:"+(endFreePort - startFreePort));
		if(neList != null && neList.size() > 0){		
			for(Map<String,Object> map : neList){
				String roomCuid = (String) map.get("RELATED_ROOM_CUID");
				Map<String,Map<String,Object>> neMap = (Map<String,Map<String,Object>>)room2Device.get(roomCuid);
				if(neMap == null)
				{
					neMap = new HashMap<String,Map<String,Object>>();
					room2Device.put(roomCuid, neMap);
				}
				String signalType = (String) map.get("SIGNAL_TYPE");
				if(signalType != null){
					if(neMap.get(signalType) != null){
						Map<String,Object> portMap = neMap.get(signalType);
						Object port = map.get("PORT_TYPE");
						if(port != null){
							Long portType = port instanceof Long ? (Long) port:((BigDecimal) port).longValue();
							if(portType.intValue() == 1){  //电口
								portMap.put("电口",portMap.get("电口") == null?1:Integer.parseInt(portMap.get("电口").toString())+Integer.parseInt(map.get("PORT_TYPE_COUNT").toString()));
							}else if(portType.intValue() == 2){  //光口
								portMap.put("光口",portMap.get("光口") == null?1:Integer.parseInt(portMap.get("光口").toString())+Integer.parseInt(map.get("PORT_TYPE_COUNT").toString()));
							}
						}
					}else{
						Object port = map.get("PORT_TYPE");
						Map<String,Object> portMap = new HashMap<String,Object>();
						if(port != null){
							Long portType = port instanceof Long ? (Long) port:((BigDecimal) port).longValue();
							if(portType.intValue() == 1){  //电口
								portMap.put("电口", Integer.parseInt(map.get("PORT_TYPE_COUNT").toString()));
							}else if(portType.intValue() == 2){  //光口
								portMap.put("光口", Integer.parseInt(map.get("PORT_TYPE_COUNT").toString()));
							}
						}
						neMap.put(signalType, portMap);
					}
				}
			}
		}
			
		for(int i = 0; i < accessList.size(); i++){
			Map acc = (Map)accessList.get(i);
			if(acc.get("RELATED_ROOM_CUID") != null){
				String roomCuid = (String) acc.get("RELATED_ROOM_CUID");
				if(room2Device.get(roomCuid) != null){
					//设置接入方式和空闲端口
					Map<String,Map<String,Object>> map = (Map<String, Map<String, Object>>) room2Device.get(roomCuid);
					
					Set<String> set = map.keySet();
					String accessWay = "";
					for(String equipType : set){
						accessWay += equipType + "/";
					}
					accessWay = accessWay.substring(0, accessWay.length()-1);
					acc.put("ACCESS_WAY",accessWay);
					
					String freePort = "";
					for(String signalType : map.keySet()){
						Map<String,Object> portInfo = map.get(signalType);
						String equipFreePort = "(";
						for(String portType : portInfo.keySet()){
							equipFreePort += portType + " " + portInfo.get(portType) + ",";
						}
						equipFreePort = equipFreePort.substring(0, equipFreePort.length()-1) + ")";
						freePort += equipFreePort + "/";
					}
					freePort = freePort.substring(0,freePort.length()-1);
					acc.put("FREE_PORT_NUM", freePort);
				}
			}
		}
	}
	
	
	private List<Map<String,Object>> getWireSegsByAccesspoint(List<String> cuids,String tableName,Map<String,Double> distances,Map<String,Double> defaultValue){
		long start = System.currentTimeMillis();
		StringBuffer temp = new StringBuffer();
		for(int i = 0 ; i < cuids.size() ; i++)
		{
			String cuid = cuids.get(i);
			temp.append("'"+cuid+"'");
			if(i < cuids.size() -1)
				temp.append(",");
		}
		
		Double wireCost =  defaultValue.get("WIRE_COST_PER_KM");
		Double constructPeriod =  defaultValue.get("CONSTRUCTION_PERIOD");
		Double distanceRate =  defaultValue.get("DISTANCE_RATE");
		Double installCost =  defaultValue.get("INSTALLATION_COST");
		Double sdhCost =  defaultValue.get("SDH_DEVICE_COST");
		Double ptnCost =  defaultValue.get("PTN_DEVICE_COST");
		
		StringBuffer sb = new StringBuffer();
		Boolean flag = false;
		if("ODF".equals(tableName) || "MISCRACK".equals(tableName) || "FIBER_JOINT_BOX".equals(tableName)){
			flag = true;
			
			sb.append("SELECT T.CUID,T.LABEL_CN,T.RELATED_ROOM_CUID,R.LABEL_CN AS ROOM_NAME,'")
			.append(tableName)
			.append("' AS TYPE,")
			.append("CASE  ")                            
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_CAB%' THEN (SELECT LABEL_CN FROM FIBER_CAB C WHERE C.CUID = WS.ORIG_POINT_CUID) ")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_DP%' THEN (SELECT LABEL_CN FROM FIBER_DP C WHERE C.CUID = WS.ORIG_POINT_CUID) ")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_JOINT_BOX%' THEN (SELECT LABEL_CN FROM FIBER_JOINT_BOX C WHERE C.CUID = WS.ORIG_POINT_CUID) ")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'SITE%' THEN (SELECT LABEL_CN FROM SITE C WHERE C.CUID = WS.ORIG_POINT_CUID) ")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'ACCESSPOINT%' THEN (SELECT LABEL_CN FROM ACCESSPOINT C WHERE C.CUID = WS.ORIG_POINT_CUID) ")
			.append("END ORIG_POINT_NAME, ")
			.append("CASE ")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_CAB%' THEN (SELECT LABEL_CN FROM FIBER_CAB C WHERE C.CUID = WS.DEST_POINT_CUID) ")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_DP%' THEN (SELECT LABEL_CN FROM FIBER_DP C WHERE C.CUID = WS.DEST_POINT_CUID) ")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_JOINT_BOX%' THEN (SELECT LABEL_CN FROM FIBER_JOINT_BOX C WHERE C.CUID = WS.DEST_POINT_CUID) ")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'SITE%' THEN (SELECT LABEL_CN FROM SITE C WHERE C.CUID = WS.DEST_POINT_CUID) ")     
			.append("WHEN WS.DEST_POINT_CUID LIKE 'ACCESSPOINT%' THEN (SELECT LABEL_CN FROM ACCESSPOINT C WHERE C.CUID = WS.DEST_POINT_CUID) ")
			.append("END DEST_POINT_NAME,")
			.append("(SELECT COUNT(*) FROM FIBER F WHERE F.RELATED_SEG_CUID = WS.CUID AND F.USAGE_STATE = 1) FREE_FIBER_NUM,")
			.append("(SELECT COUNT(*) FROM PTP P, TRANS_ELEMENT NE WHERE P.PORT_STATE = 1 AND P.RELATED_NE_CUID = NE.CUID AND NE.RELATED_ROOM_CUID = R.CUID) AS FREE_PORT_NUM,")
			.append("DECODE(T.ACCESS_SCENE,0,'未知',1,'综合业务机房',2,'基站机房',3,'客户机房',4,'预覆盖',5,'其他','未知') AS ACCESS_SCENE")
			.append(" FROM ")
			.append(tableName)
			.append(" T left join ROOM r on T.RELATED_ROOM_CUID = R.CUID")
			.append(" left join WIRE_SEG WS on ( WS.ORIG_POINT_CUID = R.RELATED_SITE_CUID  or WS.DEST_POINT_CUID = R.RELATED_SITE_CUID)")
			.append(" where T.CUID in (")
			.append(temp)
			.append(")"); 
		}else{
			sb.append("SELECT T.CUID,T.LABEL_CN,WS.CUID AS RELATED_SEG_CUID,'")
			.append(tableName)
			.append("' AS TYPE,")
			.append("CASE ")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_CAB%' THEN (SELECT LABEL_CN FROM FIBER_CAB C WHERE C.CUID = WS.ORIG_POINT_CUID)")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_DP%' THEN (SELECT LABEL_CN FROM FIBER_DP C WHERE C.CUID = WS.ORIG_POINT_CUID)")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'FIBER_JOINT_BOX%' THEN (SELECT LABEL_CN FROM FIBER_JOINT_BOX C WHERE C.CUID = WS.ORIG_POINT_CUID)")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'SITE%' THEN (SELECT LABEL_CN FROM SITE C WHERE C.CUID = WS.ORIG_POINT_CUID)")
			.append("WHEN WS.ORIG_POINT_CUID LIKE 'ACCESSPOINT%' THEN (SELECT LABEL_CN FROM ACCESSPOINT C WHERE C.CUID = WS.ORIG_POINT_CUID)  ")
			.append("END ORIG_POINT_NAME,")
			.append("CASE ")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_CAB%' THEN (SELECT LABEL_CN FROM FIBER_CAB C WHERE C.CUID = WS.DEST_POINT_CUID)")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_DP%' THEN (SELECT LABEL_CN FROM FIBER_DP C WHERE C.CUID = WS.DEST_POINT_CUID)")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'FIBER_JOINT_BOX%' THEN (SELECT LABEL_CN FROM FIBER_JOINT_BOX C WHERE C.CUID = WS.DEST_POINT_CUID)")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'SITE%' THEN (SELECT LABEL_CN FROM SITE C WHERE C.CUID = WS.DEST_POINT_CUID)")
			.append("WHEN WS.DEST_POINT_CUID LIKE 'ACCESSPOINT%' THEN (SELECT LABEL_CN FROM ACCESSPOINT C WHERE C.CUID = WS.DEST_POINT_CUID)")
			.append("END DEST_POINT_NAME,")
			.append("DECODE(T.ACCESS_SCENE,0,'未知',1,'综合业务机房',2,'基站机房',3,'客户机房',4,'预覆盖',5,'其他','未知') AS ACCESS_SCENE,")
			.append("(SELECT COUNT(*) FROM FIBER F WHERE F.RELATED_SEG_CUID = WS.CUID AND F.USAGE_STATE = 1) FREE_FIBER_NUM,")
			.append("0 AS FREE_PORT_NUM,")
			.append("'' AS ACCESS_WAY")
			.append(" FROM ")
			.append(tableName)
			.append(" T left join WIRE_SEG WS on (WS.ORIG_POINT_CUID = T.CUID ")
			.append("OR WS.DEST_POINT_CUID = T.CUID) where T.CUID in (")                                             
			.append(temp)
			.append(")");
		}
		
//		System.out.println(tableName+"->"+sb.toString());
		List<Map<String,Object>> result =  this.queryForList(sb.toString());
		
		for(Map<String,Object> map : result)
		{
			String cuid = (String)map.get("CUID");			
			if(cuid != null)
			{
				double distance = distances.get(cuid);
				double time = (distance / 1000) * distanceRate * constructPeriod;
				double cost = (distance / 1000) * distanceRate * wireCost
						+ sdhCost + installCost + (sdhCost + installCost) * 0.15;
				
				map.put("FORECAST_TIME", time);
				map.put("FORECAST_COST", cost);
				map.put("ACCESS_DISTANCE", distance);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(tableName+"时间毫秒:"+(end - start));
		return result;
	}
	
	
	private double converToDouble(String str)
	{
		double result = 0d;
		try{
			result = Double.parseDouble(str);
		}catch(Exception e)
		{}
		return result;
	}
	
	//精度保证在小数点后2位
	private String getAccuracy2(double number){
		DecimalFormat    df   = new DecimalFormat("#########.00");
		return df.format(number);
	}
	
	/**
	 * 获取机房内可作为集客接入场景资源（ODF、综合机架、终端盒）
	 * @param cuid 站点标识
	 * @return
	 */
	private List<Map<String,Object>> getAccessPointsBySites(List<String> cuids){
		StringBuffer temp = new StringBuffer();
		for(String cuid : cuids)
		{
			temp.append("'"+cuid+"'").append(",");
		}
		temp.append("'000'");
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUID,RELATED_SITE_CUID FROM ODF ")
		.append("WHERE RELATED_SITE_CUID in (")
		.append(temp)
		.append(") UNION ")
		.append("SELECT CUID,RELATED_SITE_CUID FROM MISCRACK ")
		.append("WHERE RELATED_SITE_CUID in (")
		.append(temp)
		.append(") UNION  ")
		.append("SELECT CUID,RELATED_SITE_CUID FROM FIBER_JOINT_BOX ")
		.append("WHERE RELATED_SITE_CUID in (")
		.append(temp)
		.append(") AND KIND = 2");
		System.out.println(sb.toString());
		return this.queryForList(sb.toString());
	}
	private ArrayList queryForList(String sql){
		return queryForList(null,sql);
	}
	private Map parseHashMap_Key_toUpperCase(Map map){
		Map<String,Object> m = new HashMap<String,Object>(); 
		boolean bol = false;
		for (Object obj : map.keySet()) {
			if(obj instanceof String){
				if(this.parseString(obj).length()<1) continue;
				bol = true;
				String key = obj.toString();
				if(!key.equals(key.toUpperCase())){
					m.put(key.toUpperCase(),map.get(key));
				}else{ 
					m.put(key,map.get(key));
				}
			}
		}
		if(!bol) return map;else return m;
	}
	private String  parseString(Object obj) {
		try {
			return obj.toString();
		} catch (Exception ex) {
			return "";
		}
	}
	private ArrayList queryForList(String dataBase,String sql){
		long a = System.currentTimeMillis();
		ArrayList aryList = new ArrayList();
		IbatisDAO ibatisDAO = (IbatisDAO)SpringContextUtil.getBean(dataBase!=null?dataBase : "IbatisTransDAO");
		long c = System.currentTimeMillis();
		System.out.println("时间1:"+(c-a));
		List list =ibatisDAO.querySql(sql);
		Iterator it = list.iterator();
		while(it.hasNext()){
			Map map = (HashMap) it.next();
			map = this.parseHashMap_Key_toUpperCase(map);
			aryList.add(map);
		}
		long b = System.currentTimeMillis();
		System.out.println("时间2:"+(b-a));
		return aryList;
	}
	/**
	 * 获取机房内空闲传输设备端口信息
	 * @param roomCuid
	 * @return
	 */
	public List<Map<String,Object>> getPortDetail(String roomCuid){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT NE.CUID,NE.LABEL_CN AS DEVICE_NAME,P.LABEL_CN AS PORT_NAME,")
		  .append("DECODE(P.PORT_TYPE,'0','未知','1','电口','2','光口','3','MSTP口','4','逻辑口','5','中频口','6','射频口','7','IMA口','8','LAG口','9','GPON口','10','EPON口') PORT_TYPE,")
		  .append("DECODE(NE.SIGNAL_TYPE,'1','SDH','2','PDH','3','WDM','4','SDH微波','5','混合','6','未知','7','PDH微波','8','PTN','9','PON','10','OLP') ACCESS_WAY ")
		  .append("FROM TRANS_ELEMENT NE,PTP P ")
		  .append("WHERE NE.CUID = P.RELATED_NE_CUID ")
		  .append("AND NE.RELATED_ROOM_CUID = '")
		  .append(roomCuid)
          .append("' AND P.PORT_STATE = 1 ");
		return this.queryForList(sb.toString());
	}
}
