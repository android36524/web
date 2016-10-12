package com.boco.irms.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.db.DbType;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.MaintManagement;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.MicrowaveLineSeg;
import com.boco.transnms.common.dto.MicrowaveSystem;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.ProjectManagement;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireDisplaySeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;

public class WebDMUtils {

	
    public static boolean isSystemClassName(String className) {
        return  DuctSystem.CLASS_NAME.equals(className) ||
            PolewaySystem.CLASS_NAME.equals(className) || StonewaySystem.CLASS_NAME.equals(className) ||
            HangWall.CLASS_NAME.equals(className) || UpLine.CLASS_NAME.equals(className);
    }
   
    
    public static DataObjectList getSegInfoList( String systemCuid) throws Exception {

        Map map = getSegsAndPointsBySystemCuid(systemCuid);
        DataObjectList segs = (DataObjectList) map.get("segsList");
        DataObjectList points = (DataObjectList) map.get("pointsList");
        DMHelper.setSegsPoints(segs, points);
        return segs;
    }
    
    public static Map  getSegsAndPointsBySystemCuid(String systemCuid) throws Exception {
        return (Map) BoCmdFactory.getInstance().execBoCmd(
            DuctManagerBOHelper.ActionName.getSegsAndPointsBySystemCuid, new BoActionContext(), systemCuid);
    }
    
	public static void setProjectState(GenericDO dbo) {
        try {
            if (dbo != null) {
            	Object obj = dbo.getAttrValue(Pole.AttrName.relatedProjectCuid);
                if (obj instanceof GenericDO) {
                    dbo.setAttrValue(Pole.AttrName.projectState, ((GenericDO) (obj)).getAttrValue(ProjectManagement.AttrName.state));
                } else {
                    dbo.removeAttr(Pole.AttrName.projectState);
                }
                
                Object obj1 = dbo.getAttrValue(Pole.AttrName.relatedMaintCuid);
                if (obj1 instanceof GenericDO) {
                    dbo.setAttrValue(Pole.AttrName.maintState, ((GenericDO) (obj1)).getAttrValue(MaintManagement.AttrName.state));
                } else {
                    dbo.removeAttr(Pole.AttrName.maintState);
                }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
	
	/**
	 * 取对象的名称
	 * @param obj
	 * @return
	 */
    public static String getRelatedLabelcn(Object obj) {
        if (obj instanceof GenericDO) {
            return ((GenericDO) obj).getAttrString("LABEL_CN");
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj != null) {
            return (String) obj.toString();
        } else {
            return null;
        }
    }
    
    /**
     * 根据枚举类型，枚举值找到对应的显示名称
     * @param code
     * @param value
     * @return
     */
	public static Object convertValue2Enum(String code, Object value) {
		Object enumValue = value;
		if (value instanceof Boolean) {
			value = (Boolean) value ? 1L : 0L;
		}
		Object[] gcEnum = EnumTypeManager.getInstance().getEnumTypes(code);
		if (gcEnum != null) {
			for (Object oEnum : gcEnum) {
				EnumType etype = (EnumType) oEnum;
				if (!StringUtils.isEmpty(value.toString())) {
					try{
						if (Long.parseLong((etype.value).toString()) == Long.parseLong(String.valueOf(value))) {
							enumValue = etype.dispalyName;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		return enumValue;
	}
    

	/**
	 * 根据classname或cuid获取资源名称
	 * @param value
	 * @return
	 */
    public static String getLabelCnByClassNameOrCuid(String value) {
        if (value != null && value.trim().length() > 0) {
            if (value.equals(DuctSystem.CLASS_NAME) || value.startsWith(DuctSystem.CLASS_NAME)) {
                return "管道系统";
            } else if (value.equals(PolewaySystem.CLASS_NAME) || value.startsWith(PolewaySystem.CLASS_NAME)) {
                return "杆路系统";
            } else if (value.equals(StonewaySystem.CLASS_NAME) || value.startsWith(StonewaySystem.CLASS_NAME)) {
                return "标石路由";
            } else if (value.equals(UpLine.CLASS_NAME)|| value.startsWith(UpLine.CLASS_NAME)) {
                return "引上系统";
            } else if (value.equals(HangWall.CLASS_NAME)|| value.startsWith(HangWall.CLASS_NAME)) {
                return "挂墙系统";
            } else if (value.equals(WireSystem.CLASS_NAME)|| value.startsWith(WireSystem.CLASS_NAME)) {
                return "光缆系统";
            } else if (value.equals(MicrowaveSystem.CLASS_NAME)|| value.startsWith(MicrowaveSystem.CLASS_NAME)) {
                return "微波系统";
            } else if (value.equals(Site.CLASS_NAME)|| value.startsWith(Site.CLASS_NAME)) {
                return "站点";
            }else if (value.equals(DuctSeg.CLASS_NAME)|| value.startsWith(DuctSeg.CLASS_NAME)) {
                return "管道段";
            } else if (value.equals(PolewaySeg.CLASS_NAME)|| value.startsWith(PolewaySeg.CLASS_NAME)) {
                return "杆路段";
            } else if (value.equals(StonewaySeg.CLASS_NAME)|| value.startsWith(StonewaySeg.CLASS_NAME)) {
                return "标石路由段";
            } else if (value.equals(UpLineSeg.CLASS_NAME)|| value.startsWith(UpLineSeg.CLASS_NAME)) {
                return "引上段";
            } else if (value.equals(HangWallSeg.CLASS_NAME)|| value.startsWith(HangWallSeg.CLASS_NAME)) {
                return "挂墙段";
            } else if (value.equals(WireSeg.CLASS_NAME)|| value.startsWith(WireSeg.CLASS_NAME)) {
                return "光缆段";
            } else if (value.equals(MicrowaveLineSeg.CLASS_NAME)|| value.startsWith(MicrowaveLineSeg.CLASS_NAME)) {
                return "微波段";
            } else if (value.equals(Manhle.CLASS_NAME)|| value.startsWith(Manhle.CLASS_NAME)) {
                return "人手井";
            } else if (value.equals(Pole.CLASS_NAME)|| value.startsWith(Pole.CLASS_NAME)) {
                return "电杆";
            } else if (value.equals(Stone.CLASS_NAME)|| value.startsWith(Stone.CLASS_NAME)) {
                return "标石";
            } else if (value.equals(FiberDp.CLASS_NAME)|| value.startsWith(FiberDp.CLASS_NAME)) {
                return "分纤箱";
            } else if (value.equals(FiberCab.CLASS_NAME)|| value.startsWith(FiberCab.CLASS_NAME)) {
                return "交接箱";
            } else if (value.equals(Accesspoint.CLASS_NAME)|| value.startsWith(Accesspoint.CLASS_NAME)) {
                return "接入点";
            } else if (value.equals(FiberJointBox.CLASS_NAME)|| value.startsWith(FiberJointBox.CLASS_NAME)) {
                return "接头盒";
            }else if (value.equals(Inflexion.CLASS_NAME)|| value.startsWith(Inflexion.CLASS_NAME)) {
                return "拐点";
            }
        }
        return null;
    }

    /**
     * Map对象转换成GenericDO
     * @param className
     * @param keyValueMap
     * @return
     */
	public static GenericDO createInstanceByClassName(String className,Map keyValueMap){
		GenericDO dbo = new GenericDO();
		dbo.setClassName(className);		
		dbo = dbo.createInstanceByClassName();
		Class fieldType = null;
		for(String key: dbo.getAllAttrNames()){
			String columnName = String.valueOf(key);
			fieldType = dbo.getAttrType(columnName);
			Object value = keyValueMap.get(key);
			value = convertJosn2Object(columnName,value);	
			if(containAttr(dbo,columnName)&& fieldType != null && !"".equals(String.valueOf(value))){			
				if (fieldType == boolean.class || fieldType == Boolean.class) {
					   if( value == null){
						   value = false;
					   }else if(value instanceof Integer){
						   value = ((Integer)value==0?false:true);
					   }else if(value instanceof Long){
						   value = ((Long)value==0?false:true);
					   }
					   dbo.setAttrValue(columnName, value);
				} else if (fieldType == long.class || fieldType == Long.class) {
					if( value == null){
						   value = 0L;
					   }else{
						   dbo.setAttrValue(columnName, Long.parseLong("" + value));
					   }
				} else if (fieldType == double.class || fieldType == Double.class || fieldType == float.class
						|| fieldType == Float.class) {
					if( value == null){
						   value = 0.0;
					   }else{
						   dbo.setAttrValue(columnName, Double.parseDouble("" + value));
					   }
				} else if (fieldType == String.class) {
					if( value != null){
						dbo.setAttrValue(columnName, String.valueOf(value));
				    }
				} else if (fieldType == java.sql.Timestamp.class) {
						Date date = null;
						//此处时间不能自动赋值
						if( value == null){
							date = new Date();
					    }else if(value instanceof String){
							SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								date = formatDate.parse(String.valueOf(value));
							} catch (ParseException e) {
								System.out.println("日期转换错误");
							}
						}else {
							date = new Date(Long.parseLong("" + value));
						}
						if(value != null){
							dbo.setAttrValue(columnName, new java.sql.Timestamp(date.getTime()));
						}
				} 
			}
		}
		dbo.setObjectNum(dbo.getAttrLong("OBJECTID"));
		dbo.clearUnknowAttrs();
		
		return dbo;
	}
	
	public static  Object convertJosn2Object(String colName,Object value){
		String strValue = String.valueOf(value);
		if(value != null&&strValue.contains("{")&& strValue.contains("}")){			
			List<Map> result = JSON.parseArray("["+strValue+"]",Map.class);
			if(result != null && result.size() > 0){
				Map keyValueMap = result.get(0);
				Object keyObj = keyValueMap.get("value");
				if(keyObj instanceof JSONObject){
					if(keyObj != null){
						Map valueMap  = JSON.parseObject(keyObj.toString(),Map.class);
						keyObj = valueMap.get("CUID");
					}else{
						keyObj = keyValueMap.get("CUID");
					}
				}else if(keyObj == null){
					keyObj = keyValueMap.get("CUID");
				}
				
				return keyObj;

			}
		}
		return value;
	}
	
	private static boolean containAttr(GenericDO dbo, String columnName){
		for(String key: dbo.getAllAttrNames()){
			 if(key.equals(columnName)){
				 return true;
			 }
			}
		return false;
		
	}
	
   /**
    * 同步光缆路由
 * @param wireseg
 * @param isPrompt
 * @param result
 */
@SuppressWarnings("all")
public static void syschWireSegDisplayRoute(WireSeg wireseg,boolean isPrompt) throws Exception{
    	if(wireseg==null)
    		return;
        String wireBranchCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
        String wireSystemCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
        GenericDO branch = null;
        try {
            branch = (WireBranch) BoCmdFactory.getInstance().execBoCmd(WireBranchBOHelper.ActionName.getWireBranchByCuid,
                new BoActionContext(), wireBranchCuid);
        } catch (Exception ex) {
        	throw new UserException("读取光缆分支出错"+ex.getMessage());
        }

        if (branch == null) {
            throw new UserException("光缆段所在光缆分支为空,生成显示路由失败!");
        }

        //根据seg关系得到所有敷设信息
        DataObjectList wiretoductlines = new DataObjectList();
        DataObjectList wiredisplayseglists = new DataObjectList();
        List<String> deletePresetPoints=new ArrayList<String>();
        //根据关系得到所有敷设信息,可能是多种类型,对不同类型进行不同的处理
        try {
            wiretoductlines = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg,
                new BoActionContext(), wireseg);
        } catch (Exception ex) {
        	throw new UserException("读取光缆敷设信息对象出错");
        }

        wiretoductlines.sort(WireToDuctline.AttrName.indexInRoute, true); //用 INDEX_IN_ROUTE =1,2,3  进行排序
        try {

            List<String> pointCuidList = DMHelper.getPointCuidListByWireToDuctlines(wiretoductlines);
            String origPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.destPointCuid));

            List<String> displayPoints = getDisplayPointByWireBranch(DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid)));
            if(displayPoints==null){
                displayPoints=new ArrayList();
            }

            boolean isReset = false;
            if (displayPoints.size() > 0) {
                for (int i = 0; i < displayPoints.size(); i++) {
                    String pointCuid = displayPoints.get(i);
                    if (pointCuid.startsWith(PresetPoint.CLASS_NAME)) {
                        isReset = true;
                        if(!pointCuidList.contains(pointCuid)){
                            deletePresetPoints.add(pointCuid);
                        }
                    }
                }
            }

            if (pointCuidList.size() == 0) {
                pointCuidList = displayPoints;

                if (!isReset) {
                    pointCuidList = new ArrayList<String>();
                }
            }
            if (!pointCuidList.contains(origPointCuid)) {
                pointCuidList.add(0, origPointCuid);
            }
            if (!pointCuidList.contains(destPointCuid)) {
                pointCuidList.add(destPointCuid);
            }
            String[] pointCuidArray = new String[pointCuidList.size()];
            pointCuidList.toArray(pointCuidArray);
            DataObjectList points = NmsUtils.getObjectsByCuids(pointCuidArray);

            for (int j = 0; j < points.size() - 1; j++) {
                WireDisplaySeg wiredisplayseg = new WireDisplaySeg();
                String labelcn = "";
                if (points.get(j) != null) {
                    labelcn = "" + points.get(j).getAttrString("LABEL_CN");
                }
                if (points.get(j + 1) != null) {
                    labelcn += "--" + points.get(j + 1).getAttrString("LABEL_CN");
                }
                // "具体路由"
                wiredisplayseg.setLabelCn(labelcn);
                wiredisplayseg.setIndexInBranch(j + 1);
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.origPointCuid, points.get(j));
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.destPointCuid, points.get(j + 1));
                wiredisplayseg.setRelatedSystemCuid(wireSystemCuid);
                wiredisplayseg.setRelatedBranchCuid(wireBranchCuid);

                wiredisplayseglists.add(wiredisplayseg);
            }
        } catch (Exception e) {
        	throw new UserException("处理光缆显示路由出错！");
        }
        DMHelper.clearDto(wiredisplayseglists);
        //删除旧显示路由 具体路由代替 到服务器
        //删除数据 条件 根据WireSeg中的RELATED_BRANCH_CUID 和 RELATED_SYSTEM_CUID 的
        DataObjectList wiresegdisplay = new DataObjectList();
        if (wiredisplayseglists.size() > 0) {
            try {
                wiresegdisplay = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                    WireDisplaySegBOHelper.ActionName.deleteAndAddWireDisplaySegs,
                    new BoActionContext(), wireseg, wiredisplayseglists);
            } catch (Exception ex) {
            	throw new UserException("删除显示路由时增加具体路由信息对象出错");
            	//删除显示路由,增加具体路由信息对象出错
            }

            branch.setAttrValue(DMCacheObjectName.SystemDisplaySegChildren, wiresegdisplay);
            
            try {
            	IWireSegBO bo = (IWireSegBO) BoHomeFactory.getInstance().getBO(IWireSegBO.class);
            	bo.modifyLayOrDeleteRelationWireSeg(new BoActionContext(), wireseg);
    		} catch (Exception e) {
    			throw new UserException("修改光缆段出错！");
    		}
           
            WireSystem wireSystem = new WireSystem();
            DataObjectList refreshList = new DataObjectList();
            wireSystem.setCuid(wireSystemCuid);
            refreshList.add(wireSystem);
            //arcgis版本中不做处理，此处如果要处理老版本，需要加上判断
//	            DMGisUtils.getDmMap().refreshSystems((DataObjectList) refreshList);
            try {
                if (deletePresetPoints.size() > 0) {
                    String[] tmpPointCuids = new String[deletePresetPoints.size()];
                    for (int i = 0; i < deletePresetPoints.size(); i++) {
                        tmpPointCuids[i] = deletePresetPoints.get(i);
                    }
                    DataObjectList list = NmsUtils.getObjectsByCuids(tmpPointCuids);
                   deletePointObjects(list);
                }
            } catch (Exception ex) {
            	throw new UserException("删除预置点出错!");
            }
        }
    }
	
@SuppressWarnings("all")
public static List getDisplayPointByWireBranch(String branchCuid) {
    DataObjectList displays = null;
    try {
        displays = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireDisplaySegBOHelper.ActionName.getDisplaySegByBranch, new BoActionContext(),
            new WireBranch(branchCuid));
    } catch (Exception ex) {
        LogHome.getLog().error("getDisplayPointByWireBranch出错",ex);
    };
    if (displays == null || displays.isEmpty()) return null;
    List<String> pointCuids = new ArrayList<String>();
    for (GenericDO dto : displays) {
        if (!(dto instanceof WireDisplaySeg)) continue;
        WireDisplaySeg display = (WireDisplaySeg) dto;
        if (!pointCuids.contains(display.getOrigPointCuid())) {
            pointCuids.add(display.getOrigPointCuid());
        }
        if (!pointCuids.contains(display.getDestPointCuid())) {
            pointCuids.add(display.getDestPointCuid());
        }
    }
    return pointCuids;
}
	    
private static void deletePointObjects(DataObjectList list) throws Exception {
    if (list != null && list.size() > 0) {
        try {
            //从服务器删除
            BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLocatePoints, new BoActionContext(), list);
            //从内存删除及地图表删除,以下是非arcgis版本用到，注释
//	               List strCuids= list.getCuidList();
//	                for (int i = 0; i < list.size(); i++) {
//	                	DMGisUtils.getDmMap().removeCachePoint(list.get(i));
//	                    LocatePointCacheModel.getInstance().removeElement(list.get(i));
 //
//	                }
//	                List allList = LocatePointCacheModel.getInstance().getElements();
 //
//	                if (allList != null && allList.size() > 0) {
//	                    List tmpList=new ArrayList();
//	                    for (int i = 0; i < allList.size(); i++) {
//	                        GenericDO dto=(GenericDO) allList.get(i);
//	                        String cuid=dto.getCuid();
//	                        if(cuid!=null&&strCuids.contains(cuid)){
//	                            tmpList.add(dto);
//	                        }
//	                    }
//	                    if(tmpList.size()>0){
//	                        allList.removeAll(tmpList);
//	                    }
//	                }

        } catch (Exception ex) {
            LogHome.getLog().error("删除地图显示点出错!", ex);
        }
    }
}

	public static InputStream getInputStream(String path) throws IOException{
		if(path.contains(":")){
			return getInputStream(path,null);
		}
		return getInputStream(path, WebDMUtils.class);
	}
	
	public static InputStream getInputStream(String path, Class class1) throws IOException{
		File file = new File(path);
		if(file.exists()){
			return new FileInputStream(file);
		}
		
		URL url = null;
		if(class1 == null){
			url = new URL(path);
		}else{
			url = class1.getResource(path);
		}
		
		if(url == null){
			return null;
		}
		
		URLConnection urlConn = url.openConnection();
		return urlConn.getInputStream();
	}

	public static DbType getDbType(){
		String url = SysProperty.getInstance().getValue("tnms.jdbc.url");
		if(url.indexOf("oracle") > -1){
			return DbType.DB_TYPE_ORACLE;
		}else if(url.indexOf("informix") > -1){
			return DbType.DB_TYPE_INFORMIX;
		}
		return null;
	}
	
	public static boolean getIsSameAttrs(Map<String,Object> attrsMap,Map<String, Object> oldAttrsMap){
		boolean flag = false;
		for(Map.Entry<String,Object> modifyEntry : attrsMap.entrySet()){
			String modifyKey = modifyEntry.getKey();
			Object value2 = modifyEntry.getValue();
			String modifyValue = "";
			if(value2 != null){
				modifyValue = String.valueOf(modifyEntry.getValue());
			}
			for(Map.Entry<String,Object> oldEntry : oldAttrsMap.entrySet()){
				String key = String.valueOf(oldEntry.getKey());
				Object value3 = oldEntry.getValue();
				String value = "";
				if(value3 != null){
					value = String.valueOf(oldEntry.getValue());
				}
				if(key.equals(modifyKey)){
					if(!value.equals(modifyValue)){
						//值有不同时，需要修改
						flag = true;
						return flag;
					}else{
						break;
					}
				}
			}
		}
		return flag;
	}
}
