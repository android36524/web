/**
 * ------------------------------------------------------------*
 *          COPYRIGHT (C) 2009 BOCO Inter-Telecom INC          *
 *   CONFIDENTIAL AND PROPRIETARY. ALL RIGHTS RESERVED.        *
 *                                                             *
 *  This work contains confidential business information       *
 *  and intellectual property of BOCO  Inc, Beijing, CN.       *
 *  All rights reserved.                                       *
 * ------------------------------------------------------------*
 *
 * com.boco.transnms.client.view.dm.system.wire.move.MoveWireRouteHelper
 */
package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.irms.app.utils.NmsUtils;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.dm.dataimpexp.ImpExpConsts;
import com.boco.transnms.server.bo.dm.dataimpexp.ImportConverter;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class MoveWireRouteHelper {

    public static GenericDO getRelatedGenericDO(Object obj) {
        GenericDO related = null;
        if (obj instanceof GenericDO) {
            related = (GenericDO) obj;
        } else if (obj instanceof String) {
            related = getDuctManagerBO().getObjByCuid(new BoActionContext(), obj.toString());
        }
        return related;
    }

    public static DataObjectList getDuctLinesBySelection(GenericDO selection) {
        DataObjectList ductLines = new DataObjectList();
        if (selection == null) 
            return ductLines;
        else if (selection instanceof PhysicalJoin) {
            ductLines.add(selection);
            return ductLines;
        }
        try {
            ductLines = getDuctManagerBO().getSegsBySystemCuid(new BoActionContext(), selection.getCuid());
        } catch (Exception e) {
        	LogHome.getLog().error(e.getMessage(),e);
        }
        return ductLines;
    }

    public static DataObjectList getWireSegByDuctLine(List<String> ductLineCuids) {
        DataObjectList wireSegs = new DataObjectList();
        try {
            wireSegs = getDuctManagerBO().getWireSegsByDuctSegCuids(new BoActionContext(), (ArrayList)ductLineCuids);
        } catch (Exception e) {
        	LogHome.getLog().error(e.getMessage(),e);
        }
        return wireSegs;
    }

    public static void doMoveWireRoute(List<String> wireSegCuids,
            List<String> oldLayingOutCuids, DataObjectList newLayingOutObjs) {
        try {
            getDuctManagerBO().moveWireSegRoute(new BoActionContext(),
                    (ArrayList)wireSegCuids,
                    (ArrayList)oldLayingOutCuids, newLayingOutObjs);
        } catch (Exception e) {
        	LogHome.getLog().error(e.getMessage(),e);
            throw new UserException();
        }
    }

    private static IDuctManagerBO getDuctManagerBO() {
        return (IDuctManagerBO)BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
    }
    public static ArrayList getWireSegByDuctLineExt(List <String>ductLineCuids) {
  	ArrayList aList=null;
      try {
      	ArrayList list=new ArrayList();
      	list.addAll(ductLineCuids);
          aList = getDuctManagerBO().getWireSegsByDuctSegs(new BoActionContext(), list);
          
      } catch (Exception e) {
    	  LogHome.getLog().error(e.getMessage(),e);
      }
      return aList;
  }
  public static DataObjectList doMoveWireRouteExt(DataObjectList wireSegs, List<String> wireSystemCuids,
          List<String> jointBoxCuids,List<String> oldLayingOutCuids,List oldSystemCuids, DataObjectList newLayingOutObjs,boolean isDeleteRoute,
          List <Map<String,DataObjectList>> fileList,String curMapCuid,String districtCuid,Boolean isConnected,Boolean isImport) {
      try {
    	  
    	  DataObjectList rtnWireSegs=new DataObjectList();
    	  
		  DataObjectList systems=new DataObjectList();
    	  if(isImport){
    		  ArrayList newPointCuids=new ArrayList();
          	  ArrayList modifyPointCuids=new ArrayList();
          	  ArrayList rtnList=ImportConverter.generateSystemsByExcelExt(fileList,curMapCuid,districtCuid,newPointCuids,modifyPointCuids,true);
          	  ArrayList err = (ArrayList) rtnList.get(ImpExpConsts._error);
			if(err!=null&&err.size()>0){
				if(err.toString().contains("导入路由点出错")){
					throw new UserException("ImportConverter.generateSystemsByExcel出错");
				}
			}

			if (rtnList.size() > 1) {
				Object obj = rtnList.get(ImpExpConsts._data);
				if (obj instanceof ArrayList) {
					ArrayList tmpList=(ArrayList) obj;
					if(tmpList!=null&&tmpList.size()>0){
						for(int i=0;i<tmpList.size();i++){
							GenericDO dto=(GenericDO) tmpList.get(i);
							if(dto!=null){
								systems.add(dto);
							}
						}
					}
				}
			}
    	  }
    	  
          DataObjectList list=getDuctManagerBO().moveWireSegRoute(new BoActionContext(),wireSegs, (ArrayList)oldLayingOutCuids, newLayingOutObjs,new Boolean(isDeleteRoute),isImport,systems);
          
          
          DataObjectList refreshSystems=new DataObjectList();
          if(wireSegs!=null&&wireSegs.size()>0){
    		DataObjectList newSystems=new DataObjectList();
    		if(oldSystemCuids.size()>0){
    			newSystems=NmsUtils.getObjectsByCuids((String[]) oldSystemCuids.toArray(new String [oldSystemCuids.size()]));
    			if(newSystems!=null&&newSystems.size()>0){
    				refreshSystems.addAll(newSystems);
    			}
    		}
    		if(list!=null&&list.size()>0){
    			DataObjectList addCacheSystems=new DataObjectList();
    			DataObjectList deletedPoints=new DataObjectList();
    			for(GenericDO dto:list){
    				if(DMHelper.isSystemClassName(dto.getClassName())){
    					refreshSystems.add(dto);
    					addCacheSystems.add(dto);
    				}else if(DMHelper.isPointClassName(dto.getClassName())){
    					deletedPoints.add(dto);
    				}else if(dto.getClassName().equals(WireSeg.CLASS_NAME)){
    					rtnWireSegs.add(dto);
    				}
    			}
//    			if(addCacheSystems.size()>0){
//    				DMGisUtils.getDmMap().addCacheSystems(addCacheSystems);			
//    			}
//    			DMGisUtils.getDmMap().refreshSystems(refreshSystems);
    			
    			ArrayList pointCuids=new ArrayList();
    			if(deletedPoints.size()>0){
    				pointCuids.addAll(deletedPoints.getCuidList());
    			}
    			if(jointBoxCuids!=null&&jointBoxCuids.size()>0){
    				pointCuids.addAll(jointBoxCuids);
    			}
    			if(pointCuids.size()>0){
    				DataObjectList points=NmsUtils.getObjectsByCuids((String[]) pointCuids.toArray(new String [pointCuids.size()]));
    				if(points!=null&&points.size()>0){
        				DataObjectList rtnList=getDuctManagerBO().deletePoints(new BoActionContext(), points);
//        				DMGisUtils.getDmMap().removePointsFromCache(rtnList);
    				}
    			}

    		}else{
//    			DMGisUtils.getDmMap().refreshSystems(refreshSystems);
    		}
    		if(wireSystemCuids!=null&&wireSystemCuids.size()>0){
//    			DataObjectList wireSystems=ClientUtils.getObjectsByCuids((String[]) wireSystemCuids.toArray(new String [wireSystemCuids.size()]));
//    			DataObjectList wireSystems=NmsUtils.getObjectsByCuids((String[]) wireSystemCuids.toArray(new String [wireSystemCuids.size()]));
//				DMGisUtils.getDmMap().refreshSystems(wireSystems);
    		}
    	}
          return rtnWireSegs;
          
      } catch (Exception e) {
          LogHome.getLog().error(e.getMessage(),e);
          throw new UserException();
      }
  }
}
