package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.OpticalToFiber;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.Ptp;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.cm.PhyOdfPortBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 * 光路完整路由
 * @author wangqin
 *
 */
public class OpticalWayRouteExportInfoGridBO extends GridTemplateProxyBO {

	@SuppressWarnings("rawtypes")
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
//		String opticalWayCuid = param.getCfgParams().get("cuid");
		String cuid = param.getCfgParams().get("cuid");
		String opticalWayCuid = cuid.split(",")[0];
		DataObjectList exportList = new DataObjectList();
		if(opticalWayCuid!=null && !"".equals(opticalWayCuid)){
			try {
				exportList = getAllDataList(opticalWayCuid);//要导出的列表数据
			} catch (Exception e) {
				LogHome.getLog().error("光路完整路由查询失败", e);
			}
		}
		//列赋值
		List list = new ArrayList();
		PageResult pageResult = new PageResult(list, 1,1, 1);
		if(exportList != null && exportList.size() > 0){
			for (int i = 0; i < exportList.size(); i++) {
				GenericDO gdo = exportList.get(i);
				Map allAttr = gdo.getAllAttr();
				list.add(allAttr);
			}
			pageResult = new PageResult(list,1,1,1);
		}
		return pageResult;
	}

	private DataObjectList getAllDataList(String opticalWayCuid) {
		DataObjectList exportList = new DataObjectList();//要导出的列表数据
		try {
             if (opticalWayCuid != null && opticalWayCuid.trim().length() > 0) {
            	 GenericDO opticalWayList = getDuctManagerBO().getObjByCuid(new BoActionContext(), opticalWayCuid);
            	 if(opticalWayList != null ){
            		 String aDevPort = "", bDevPort = "";
            		 String sql = " RELATED_OPTICAL_WAY_CUID= '" + opticalWayCuid + "'" ;
                	 DataObjectList opticalLists = getDuctManagerBO().getObjectsBySql(sql, new Optical());//光纤数据
                 	 DataObjectList linkDataList = new DataObjectList();//存放所有跳纤和光纤
    				 if (opticalLists != null && opticalLists.size() > 0) {
    					 linkDataList.addAll(opticalLists);
    					 List<String> linkCuidList = new ArrayList<String>();
    					 for (GenericDO optical : opticalLists) {
    						 DataObjectList linkRouteList = getLinkFiberRouteByLink(optical.getCuid());
    						 if (linkRouteList != null && linkRouteList.size() > 0) {
    							 for(GenericDO gdo : linkRouteList){
    								 String linkCuid = gdo.getCuid();
    								 if(!linkCuidList.contains(linkCuid)){
    									 String className = gdo.getClassName();
        								 if(className.equals(JumpFiber.CLASS_NAME) ){//|| className.equals(Fiber.CLASS_NAME)
        								 	linkDataList.add(gdo);
        								 	linkCuidList.add(linkCuid);
        								 }
    								 }
    							 }
    						 }
    					 }
    				 }
    				 //分情况
            		 String aPortCuid = opticalWayList.getAttrString(OpticalWay.AttrName.nePortCuidA);//A起点设备端口
            		 String zPortCuid = opticalWayList.getAttrString(OpticalWay.AttrName.nePortCuidZ);//Z起点设备端口
            		 if((aPortCuid == null || aPortCuid.trim().length() == 0) && (zPortCuid == null || zPortCuid.trim().length() == 0)){//两端都无端口
            			 LogHome.getLog().error("=====两端都无端口=======" + linkDataList);
            			 if(linkDataList != null && linkDataList.size() > 0){//找到独立点
            				 List<String> pointList = new ArrayList<String>();
            				 List<String> resultPoint = getResultPoint(linkDataList);//独立点
            				 LogHome.getLog().error("=====独立点1=======" + resultPoint);
            				 //找到独立点后，判断几个独立点。 2个对应一个方向，4个两个方向
            				 if(resultPoint != null && resultPoint.size() == 2){
            					 String origEnd = resultPoint.get(0);
            					 String destEnd = resultPoint.get(1);
            					 DataObjectList resultList = getResultLinkRouteByCuid(origEnd,linkDataList,destEnd, pointList);
    							 if(resultList != null && resultList.size() > 0)  {
    								 for(int j = 0; j < resultList.size(); j++){
    									 resultList.get(j).setAttrValue("DIRCTION", "发");
    									 exportList.add(resultList.get(j));
    								 }
    							 }
            				 }else if(resultPoint != null && resultPoint.size() > 3){
            					 Map<String, String> pointMap = new HashMap<String, String>();
            					 for(int i =0; i < resultPoint.size(); i++){
            						 String pointCuid = resultPoint.get(i);
            						 GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), pointCuid);
            						 if(point != null){
            							 String pointName = point.getAttrString(Manhle.AttrName.labelCn);
            							 pointMap.put(pointName, pointCuid);
            						 }
            					 }
            					 String fOrigEnd = resultPoint.get(0);
            					 String fDestEnd = resultPoint.get(1);
            					 DataObjectList fResultList = getResultLinkRouteByCuid(fOrigEnd,linkDataList,fDestEnd, pointList);
            					 LogHome.getLog().error("=====一端结果fResultList=======" + fResultList);
            					 String bDevPortName = fResultList.get(fResultList.size()-1).getAttrString("B_DEV_PORT");
            					 String pointName = bDevPortName.split("-")[1];
            					 String pointCuid = pointMap.get(pointName);
            					 //去掉已经被用的独立点
            					 resultPoint.remove(fOrigEnd);
            					 resultPoint.remove(pointCuid);
            					 LogHome.getLog().error("=====剩余独立点=======" + resultPoint);
            					 String sOrigEnd = resultPoint.get(0);
            					 String sDestEnd = resultPoint.get(1);
            					 LogHome.getLog().error("=====另一端=======" + sOrigEnd);
            					 DataObjectList sResultList = getResultLinkRouteByCuid(sOrigEnd,linkDataList,sDestEnd, pointList);
            					 //两个list 合集
    							 if((fResultList != null && fResultList.size() > 0) && (sResultList != null && sResultList.size() > 0)) {
    								 if(fResultList.size() >= sResultList.size()){
    									 for(int j=0; j < fResultList.size(); j++){
    										 fResultList.get(j).setAttrValue("DIRCTION", "发");
    										 exportList.add(fResultList.get(j));
    										 if(j < sResultList.size()){
    											 sResultList.get(j).setAttrValue("DIRCTION", "收");
    											 exportList.add(sResultList.get(j));
    										 }
    									 }
    								 }
    								 if(fResultList.size() < sResultList.size()){
    									 for(int j=0; j < sResultList.size(); j++){
    										 sResultList.get(j).setAttrValue("DIRCTION", "发");
    										 exportList.add(sResultList.get(j));
    										 if(j < fResultList.size()){
    											 fResultList.get(j).setAttrValue("DIRCTION", "收");
    											 exportList.add(fResultList.get(j));
    										 }
    									 }
    								 }
    							 }
            				 }
            				 
            			 }
            		 }else if((aPortCuid != null && !aPortCuid.equals("")) && (zPortCuid == null || zPortCuid.equals(""))){//两端A端是端口
            			 if(linkDataList != null && linkDataList.size() > 0){//找到独立点
            				 List<String> pointList = new ArrayList<String>();
            				 List<String> resultPoint = getResultPoint(linkDataList);
            				 DataObjectList aPortLineList = getResultListByLists(linkDataList, aPortCuid);
            				 linkDataList.removeAll(aPortLineList);
            				 if(aPortLineList != null && aPortLineList.size() == 1){
            					 if(resultPoint != null && resultPoint.size() > 0){
            						 String endCuid = resultPoint.get(0);
            						 DataObjectList resultList = getResultLinkRouteByCuid(aPortCuid,linkDataList,endCuid, pointList);
            						 if(resultList != null && resultList.size() > 0)  {
        								 for(int j = 0; j < resultList.size(); j++){
        									 resultList.get(j).setAttrValue("DIRCTION", "发");
        									 exportList.add(resultList.get(j));
        								 }
        							 }
            					 }
            				 }else if(aPortLineList != null && aPortLineList.size() > 1){
            					 if(resultPoint != null && resultPoint.size() > 1){
            						 String fEndCuid = resultPoint.get(0);
            						 String sEndCuid = resultPoint.get(1);
            						 String fPointCuid = "",sPointCuid ="", name ="" ;
            						 String aPortName = getPortNameAndDirecByCuid(aPortCuid);
                     				 String aSiteCuid = opticalWayList.getAttrString(OpticalWay.AttrName.siteCuidA);//若果是机房 或 接入点 ，就去找它所在的站点
                     				 String aSiteName = getSiteNameByCuid(aSiteCuid);
            						 if(!pointList.contains(aPortCuid)){
        								 pointList.add(aPortCuid);
        								 for(int i =0; i < aPortLineList.size() - 1; i ++){
                							 //一方向
                							 DataObjectList firList = new DataObjectList();
                							 String firCuid = aPortLineList.get(0).getCuid();
                							 GenericDO firGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), firCuid);
                							 if(firGdo != null){
                								 String fOrigPointCuid = firGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String fDestPointCuid = firGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO fGdo = new GenericDO();
                    							 aDevPort = aSiteName + "-" + aPortName;
                    							 fGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                								 if(fOrigPointCuid.equals(aPortCuid)){
                									 if(!fDestPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                    									 if(fDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fDestPointCuid);
                    									 }else if(fDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName +"-"+ name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = firGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                    									 }
                									 }
                									 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fDestPointCuid;
                        						 }else if(fDestPointCuid.equals(aPortCuid)){
                        							 if(!fOrigPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                        								 if(fOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fOrigPointCuid);
                    									 }else if(fOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                                       						 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" +name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = firGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fOrigPointCuid;
                        						 }
                    							 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                            						 String nameCode = getNameCodeByDuctLine(firGdo);
                            						 fGdo.setAttrValue("NAME_CODE",nameCode);
                            						 firList.add(fGdo);
                    							 }
                        						 DataObjectList fResultList = getResultLinkRouteByCuid(fPointCuid,linkDataList,fEndCuid, pointList);
                        						 firList.addAll(fResultList);
                							 }
                							 //另一方向
                							 DataObjectList secList = new DataObjectList();
                							 String secCuid = aPortLineList.get(1).getCuid();
                							 GenericDO secGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), secCuid);
                							 if(secGdo != null){
                								 String sOrigPointCuid = secGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String sDestPointCuid = secGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO sGdo = new GenericDO();
                    							 aDevPort = aSiteName + "-" + aPortName;
                    							 sGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                        						 if(sOrigPointCuid.equals(aPortCuid)){
                        							 if(!sDestPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sDestPointCuid);
                    									 }else if(sDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = secGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site  = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                            						 sPointCuid = sDestPointCuid;
                        						 }else if(sDestPointCuid.equals(aPortCuid)){
                        							 if(!sOrigPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sOrigPointCuid);
                    									 }else if(sOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = secGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 sPointCuid = sOrigPointCuid;
                        						 }
                        						 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                        							 String nameCode = getNameCodeByDuctLine(secGdo);
                        							 sGdo.setAttrValue("NAME_CODE",nameCode);
                            						 secList.add(sGdo);
                        						 }
                    							 DataObjectList sResultList = getResultLinkRouteByCuid(sPointCuid,linkDataList,sEndCuid,pointList);
                    							 secList.addAll(sResultList);
                							 }
                							 //两个list 合集
                							 if((firList != null && firList.size() > 0) && (secList != null && secList.size() > 0)) {
                								 if(firList.size() >= secList.size()){
                									 for(int j=0; j < firList.size(); j++){
                										 firList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(firList.get(j));
                										 if(j < secList.size()){
                											 secList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(secList.get(j));
                										 }
                									 }
                								 }
                								 if(firList.size() < secList.size()){
                									 for(int j=0; j < secList.size(); j++){
                										 secList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(secList.get(j));
                										 if(j < firList.size()){
                											 firList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(firList.get(j));
                										 }
                									 }
                								 }
                							 }
                						 }
        							 }
            					 }
            				 }
            			 }
            		 }else if((aPortCuid == null || aPortCuid.equals("")) && (zPortCuid != null && !zPortCuid.equals(""))){//两端Z端是端口
            			 if(linkDataList != null && linkDataList.size() > 0){//找到独立点
            				 List<String> pointList = new ArrayList<String>();
            				 List<String> resultPoint = getResultPoint(linkDataList);
            				 DataObjectList zPortLineList = getResultListByLists(linkDataList, zPortCuid);//从端口引出的跳纤或光纤
            				 linkDataList.removeAll(zPortLineList);
            				 if(zPortLineList != null && zPortLineList.size() == 1){
            					 if(resultPoint != null && resultPoint.size() > 0){
            						 String endCuid = resultPoint.get(0);
            						 DataObjectList resultList = getResultLinkRouteByCuid(zPortCuid,linkDataList,endCuid, pointList);
            						 if(resultList != null && resultList.size() > 0)  {
        								 for(int j = 0; j < resultList.size(); j++){
        									 resultList.get(j).setAttrValue("DIRCTION", "发");
        									 exportList.add(resultList.get(j));
        								 }
        							 }
            					 }
            				 }else if(zPortLineList != null && zPortLineList.size() > 1){
            					 if(resultPoint != null && resultPoint.size() > 1){
            						 String fEndCuid = resultPoint.get(0);
            						 String sEndCuid = resultPoint.get(1);
            						 
            						 String fPointCuid = "",sPointCuid ="", name ="" ;
            						 String zPortName = getPortNameAndDirecByCuid(zPortCuid);
                     				 String zSiteCuid = opticalWayList.getAttrString(OpticalWay.AttrName.siteCuidZ);//若果是机房 或 接入点 ，就去找它所在的站点
                     				 String zSiteName = getSiteNameByCuid(zSiteCuid);
            						 if(!pointList.contains(zPortCuid)){
        								 pointList.add(zPortCuid);
        								 for(int i =0; i < zPortLineList.size() - 1; i ++){
                							 //一方向
                							 DataObjectList firList = new DataObjectList();
                							 String firCuid = zPortLineList.get(0).getCuid();
                							 GenericDO firGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), firCuid);
                							 if(firGdo != null){
                								 String fOrigPointCuid = firGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String fDestPointCuid = firGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO fGdo = new GenericDO();
                    							 aDevPort = zSiteName + "-" + zPortName;
                    							 fGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                								 if(fOrigPointCuid.equals(zPortCuid)){
                									 if(!fDestPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                    									 if(fDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fDestPointCuid);
                    									 }else if(fDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = zSiteName +"-"+ name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = firGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                    									 }
                									 }
                									 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fDestPointCuid;
                        						 }else if(fDestPointCuid.equals(zPortCuid)){
                        							 if(!fOrigPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                        								 if(fOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fOrigPointCuid);
                    									 }else if(fOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                                       						 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = zSiteName + "-" +name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = firGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fOrigPointCuid;
                        						 }
                    							 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                            						 String nameCode = getNameCodeByDuctLine(firGdo);
                            						 fGdo.setAttrValue("NAME_CODE",nameCode);
                            						 firList.add(fGdo);
                    							 }
                        						 DataObjectList fResultList = getResultLinkRouteByCuid(fPointCuid,linkDataList,fEndCuid, pointList);
                        						 firList.addAll(fResultList);
                							 }
                							 //另一方向
                							 DataObjectList secList = new DataObjectList();
                							 String secCuid = zPortLineList.get(1).getCuid();
                							 GenericDO secGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), secCuid);
                							 if(secGdo != null){
                								 String sOrigPointCuid = secGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String sDestPointCuid = secGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO sGdo = new GenericDO();
                    							 aDevPort = zSiteName + "-" + zPortName;
                    							 sGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                        						 if(sOrigPointCuid.equals(zPortCuid)){
                        							 if(!sDestPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sDestPointCuid);
                    									 }else if(sDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = zSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = secGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site  = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                            						 sPointCuid = sDestPointCuid;
                        						 }else if(sDestPointCuid.equals(zPortCuid)){
                        							 if(!sOrigPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sOrigPointCuid);
                    									 }else if(sOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = zSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = secGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 sPointCuid = sOrigPointCuid;
                        						 }
                        						 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                        							 String nameCode = getNameCodeByDuctLine(secGdo);
                        							 sGdo.setAttrValue("NAME_CODE",nameCode);
                            						 secList.add(sGdo);
                        						 }
                    							 DataObjectList sResultList = getResultLinkRouteByCuid(sPointCuid,linkDataList,sEndCuid,pointList);
                    							 secList.addAll(sResultList);
                							 }
                							 //两个list 合集
                							 if((firList != null && firList.size() > 0) && (secList != null && secList.size() > 0)) {
                								 if(firList.size() >= secList.size()){
                									 for(int j=0; j < firList.size(); j++){
                										 firList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(firList.get(j));
                										 if(j < secList.size()){
                											 secList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(secList.get(j));
                										 }
                									 }
                								 }
                								 if(firList.size() < secList.size()){
                									 for(int j=0; j < secList.size(); j++){
                										 secList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(secList.get(j));
                										 if(j < firList.size()){
                											 firList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(firList.get(j));
                										 }
                									 }
                								 }
                							 }
                						 }
        							 }
            					 }
            				 }
            			 }
            		 }else if((aPortCuid != null && aPortCuid.trim().length() > 0) && (zPortCuid != null && zPortCuid.trim().length() > 0)){//两端都有端口
            			 List<String> pointList = new ArrayList<String>();
            			 String aPortName = getPortNameAndDirecByCuid(aPortCuid);
         				 String aSiteCuid = opticalWayList.getAttrString(OpticalWay.AttrName.siteCuidA);//若果是机房 或 接入点 ，就去找它所在的站点
         				 String aSiteName = getSiteNameByCuid(aSiteCuid);
        				 DataObjectList dataList = new DataObjectList();//存放 以 A端口为起止点的纤芯或跳纤
            			 if(linkDataList != null && linkDataList.size() > 0){
        					 dataList = getResultListByLists(linkDataList, aPortCuid);
        					 linkDataList.removeAll(dataList);
        					 if(dataList != null && dataList.size() > 0){
        						 if(dataList.size() > 1){//
        							 String fPointCuid = "",sPointCuid ="", name ="" ;
        							 if(!pointList.contains(aPortCuid)){
        								 pointList.add(aPortCuid);
        								 for(int i =0; i < dataList.size() - 1; i ++){
                							 //一方向
                							 DataObjectList firList = new DataObjectList();
                							 String firCuid = dataList.get(0).getCuid();
                							 GenericDO firGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), firCuid);
                							 if(firGdo != null){
                								 String fOrigPointCuid = firGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String fDestPointCuid = firGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO fGdo = new GenericDO();
                    							 aDevPort = aSiteName + "-" + aPortName;
                    							 fGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                								 if(fOrigPointCuid.equals(aPortCuid)){
                									 if(!fDestPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                    									 if(fDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fDestPointCuid);
                    									 }else if(fDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fDestPointCuid);
                        									 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName +"-"+ name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = firGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                    									 }
                									 }
                									 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fDestPointCuid;
                        						 }else if(fDestPointCuid.equals(aPortCuid)){
                        							 if(!fOrigPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                        								 if(fOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(fOrigPointCuid);
                    									 }else if(fOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                                       						 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" +name;
                                    						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = firGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 fPointCuid = fOrigPointCuid;
                        						 }
                    							 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                            						 String nameCode = getNameCodeByDuctLine(firGdo);
                            						 fGdo.setAttrValue("NAME_CODE",nameCode);
                            						 firList.add(fGdo);
                    							 }
                        						 DataObjectList fResultList = getResultLinkRouteByCuid(fPointCuid,linkDataList,zPortCuid, pointList);
                        						 firList.addAll(fResultList);
                							 }
                							 //另一方向
                							 DataObjectList secList = new DataObjectList();
                							 String secCuid = dataList.get(1).getCuid();
                							 GenericDO secGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), secCuid);
                							 if(secGdo != null){
                								 String sOrigPointCuid = secGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                        						 String sDestPointCuid = secGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                        						 
                        						 GenericDO sGdo = new GenericDO();
                    							 aDevPort = aSiteName + "-" + aPortName;
                    							 sGdo.setAttrValue("A_DEV_PORT", aDevPort);
                    							 
                        						 if(sOrigPointCuid.equals(aPortCuid)){
                        							 if(!sDestPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sDestPointCuid);
                    									 }else if(sDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sDestPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String destSiteCuid = secGdo.getAttrString(Optical.AttrName.destSiteCuid);
                                    							 GenericDO site  = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                            						 sPointCuid = sDestPointCuid;
                        						 }else if(sDestPointCuid.equals(aPortCuid)){
                        							 if(!sOrigPointCuid.startsWith(FiberJointBox.CLASS_NAME)){
                        								 if(sOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                        									 bDevPort = getGJDevPortByPointCuid(sOrigPointCuid);
                    									 }else if(sOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                        									 bDevPort = name;
                    									 }else{
                    										 name = getGFDevPortByPointCuid(sOrigPointCuid);
                                							 if(secGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                    						     bDevPort = aSiteName + "-" + name;
                                    						 }else if(secGdo.getClassName().equals(Optical.CLASS_NAME)){
                                    							 String origSiteCuid = secGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                    							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                    							 if(site != null){
                                    								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                    							 }
                                    						 }
                        								 }
                        							 }
                        							 sGdo.setAttrValue("B_DEV_PORT", bDevPort);
                        							 sPointCuid = sOrigPointCuid;
                        						 }
                        						 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
                        							 String nameCode = getNameCodeByDuctLine(secGdo);
                        							 sGdo.setAttrValue("NAME_CODE",nameCode);
                            						 secList.add(sGdo);
                        						 }
                    							 DataObjectList sResultList = getResultLinkRouteByCuid(sPointCuid,linkDataList,zPortCuid,pointList);
                    							 secList.addAll(sResultList);
                							 }
                							 //两个list 合集
                							 if((firList != null && firList.size() > 0) && (secList != null && secList.size() > 0)) {
                								 if(firList.size() >= secList.size()){
                									 for(int j=0; j < firList.size(); j++){
                										 firList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(firList.get(j));
                										 if(j < secList.size()){
                											 secList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(secList.get(j));
                										 }
                									 }
                								 }
                								 if(firList.size() < secList.size()){
                									 for(int j=0; j < secList.size(); j++){
                										 secList.get(j).setAttrValue("DIRCTION", "发");
                										 exportList.add(secList.get(j));
                										 if(j < firList.size()){
                											 firList.get(j).setAttrValue("DIRCTION", "收");
                											 exportList.add(firList.get(j));
                										 }
                									 }
                								 }
                							 }
                						 }
        							 }
        						 }else if(dataList.size() ==1){
        							 //一个方向
        							 String fPointCuid = "", name ="";
        							 DataObjectList firList = new DataObjectList();
        							 GenericDO firstGdo = dataList.get(0);
        							 if(firstGdo != null){
        								 String firCuid = firstGdo.getCuid();
        								 GenericDO firGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), firCuid);
        								 String fOrigPointCuid = firGdo.getAttrString(JumpFiber.AttrName.origPointCuid);
                						 String fDestPointCuid = firGdo.getAttrString(JumpFiber.AttrName.destPointCuid);
                						 
                						 GenericDO fGdo = new GenericDO();
            							 aDevPort = aSiteName + "-" + aPortName;
            							 fGdo.setAttrValue("A_DEV_PORT", aDevPort);
            							 if(!pointList.contains(aPortCuid)){
            								 pointList.add(aPortCuid);
            	  							 if(fOrigPointCuid.equals(aPortCuid)){
            	  								 if(!fDestPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
            	  									if(fDestPointCuid.startsWith(Fcabport.CLASS_NAME)){
                   									    bDevPort = getGJDevPortByPointCuid(fDestPointCuid);
                   								   }else if(fDestPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                   									   name = getGFDevPortByPointCuid(fDestPointCuid);
                   									   bDevPort = name;
               									   }else{
               										   name = getGFDevPortByPointCuid(fDestPointCuid);
    												   if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
    													   bDevPort = aSiteName+ "-" + name;
    												   }else if(firGdo.getClassName().equals(Fiber.CLASS_NAME)){
    													   String destSiteCuid = firGdo.getAttrString(Optical.AttrName.destSiteCuid);
    													   GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(),destSiteCuid);
    													   if(site != null){
    														   bDevPort = site.getAttrString(Manhle.AttrName.labelCn)+ "-"+ name;
    													   }
    												   }
    											   }
            	  							    }
                    							fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                    							fPointCuid = fDestPointCuid;
                    						 }else if(fDestPointCuid.equals(aPortCuid)){
                    							 if(!fOrigPointCuid.startsWith(FiberJointPoint.CLASS_NAME)){
                    								 if(fOrigPointCuid.startsWith(Fcabport.CLASS_NAME)){
                       									 bDevPort = getGJDevPortByPointCuid(fOrigPointCuid);
                   									 }else if(fOrigPointCuid.startsWith(FiberDpPort.CLASS_NAME)){
                   										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                       									 bDevPort = name;
                   									 }else{
                   										 name = getGFDevPortByPointCuid(fOrigPointCuid);
                            							 if(firGdo.getClassName().equals(JumpFiber.CLASS_NAME)){
                                						     bDevPort = aSiteName + "-" + name;
                                						 }else if(firGdo.getClassName().equals(Optical.CLASS_NAME)){
                                							 String origSiteCuid = firGdo.getAttrString(Optical.AttrName.origSiteCuid);
                                							 GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
                                							 if(site != null){
                                								 bDevPort = site.getAttrString(Manhle.AttrName.labelCn) + "-" + name ;
                                							 }
                                						 }
            	  									 }
                    							 }
                    							 fGdo.setAttrValue("B_DEV_PORT", bDevPort);
                    							 fPointCuid = fOrigPointCuid;
                    						 }
            	  							 if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
            	  								 String nameCode = getNameCodeByDuctLine(firGdo);
            	  								 fGdo.setAttrValue("NAME_CODE", nameCode);
            	  								 firList.add(fGdo);
            	  							 }
                    						 DataObjectList fResultList = getResultLinkRouteByCuid(fPointCuid,linkDataList,zPortCuid,pointList);
                    						 firList.addAll(fResultList);
            							 }
        							 }
        							 if(firList != null && firList.size() > 0)  {
        								 for(int j=0; j < firList.size(); j++){
        									 firList.get(j).setAttrValue("DIRCTION", "发");
        									 exportList.add(firList.get(j));
        								 }
        							 }
        						 }
        					 }
        				 }
            		 }
            	  }
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHome.getLog().error("查询数据失败",e);
		}
		
		if (exportList != null && exportList.size() > 0) {
			// 生成序号
			int size = exportList.size(), code = 0;
			for (int i = 0; i < size; i++) {
				GenericDO obj = exportList.get(i);
				if (code < size + 1) {
					code = code + 1;
				}
				obj.setAttrValue("NO", code);
			}
		}
		return exportList;
	}
	/**
	 * 获取独立点
	 * @param linkDataList
	 * @return
	 */
	private List<String> getResultPoint(DataObjectList linkDataList) {
		List<String> resultPoint = new ArrayList<String>();//存放独立点
		List<String> allPointList = new ArrayList<String>();//记录所有点
		for (GenericDO gdo : linkDataList) {
			GenericDO origPoint = new GenericDO(), destPoint = new GenericDO();
			String origPointCuid = "", destPointCuid = "";
			Object orig = gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
			Object dest = gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
			if (orig instanceof GenericDO) {
				origPoint = (GenericDO) gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
				origPointCuid = origPoint.getCuid();
			} else if (orig instanceof String) {
				origPointCuid = gdo.getAttrString(JumpFiber.AttrName.origPointCuid);
			}
			if (dest instanceof GenericDO) {
				destPoint = (GenericDO) gdo.getAttrValue(JumpFiber.AttrName.destPointCuid);
				destPointCuid = destPoint.getCuid();
			} else if (dest instanceof String) {
				destPointCuid = gdo.getAttrString(JumpFiber.AttrName.destPointCuid);
			}
			if(!allPointList.contains(origPointCuid)){
				allPointList.add(origPointCuid);
			}
			if(!allPointList.contains(destPointCuid)){
				allPointList.add(destPointCuid);
			}
		}
		if(allPointList != null && allPointList.size() > 0){
			for(int i = 0; i < allPointList.size(); i++){
				String pointCuid = allPointList.get(i);
				if(!resultPoint.contains(pointCuid)){
					DataObjectList resultList = getResultListByLists(linkDataList, pointCuid);
					if(resultList != null && resultList.size() == 1){
						resultPoint.add(pointCuid);
					}
				}
			}
		}
		return resultPoint;
	}

	/**
	 * 
	 * @param aPortCuid  当前点
	 * @param linkDataList 跳纤和光纤数据
	 * @param zPortCuid  结束点
	 * @param pointList  已有点
	 * @return
	 */
	private DataObjectList getResultLinkRouteByCuid(String aPortCuid,DataObjectList linkDataList, String zPortCuid, List<String> pointList) {
		DataObjectList resDataList = new DataObjectList();
        searchRouteFromLink(resDataList, aPortCuid, linkDataList,zPortCuid,pointList);
        return resDataList;
	}
	
	/**
	 * 递归传接路由点
	 * @param resDataList
	 * @param aPortCuid
	 * @param linkDataList
	 * @param zPortCuid
	 * @param pointList
	 */
	private void searchRouteFromLink(DataObjectList resDataList,String aPortCuid, DataObjectList linkDataList, String zPortCuid, List<String> pointList) {
		DataObjectList resultList = new DataObjectList();
		String aDevPort = "",bDevPort = "",aSiteName = "", bSiteName = "",aPortName = "",bPortName = "", systemName = "",portCuid = "";
		if(aPortCuid.equals(zPortCuid)){
			return;
		}
		if(aPortCuid != null && !aPortCuid.equals("")){
			if(!pointList.contains(aPortCuid)){
				pointList.add(aPortCuid);
				if(linkDataList != null && linkDataList.size() > 0){
					resultList = getResultListByLists(linkDataList, aPortCuid);
					if(resultList != null && resultList.size() > 0){
						GenericDO reGdo = resultList.get(0);
						String linkCuid= reGdo.getCuid();
						GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), linkCuid);
						String oPCuid = gdo.getAttrString(JumpFiber.AttrName.origPointCuid);
						String dPCuid = gdo.getAttrString(JumpFiber.AttrName.destPointCuid);
					    GenericDO sGdo = new GenericDO();
					    
						if(oPCuid.equals(aPortCuid)){//起点
							if(!oPCuid.startsWith(FiberJointPoint.CLASS_NAME)){
								if(oPCuid.startsWith(Fcabport.CLASS_NAME)){
									aDevPort = getGJDevPortByPointCuid(oPCuid);
								}else if(oPCuid.startsWith(FiberDpPort.CLASS_NAME)){
									aPortName = getGFDevPortByPointCuid(oPCuid);
									aDevPort = aPortName;
								}else{
									aPortName = getGFDevPortByPointCuid(oPCuid);
									if(gdo.getClassName().equals(JumpFiber.CLASS_NAME)){
										String siteCuid = gdo.getAttrString(JumpFiber.AttrName.relatedSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), siteCuid);
										if(site != null){
											aSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(aSiteName != null && aSiteName.trim().length() > 0){
											aDevPort = aSiteName + "-" + aPortName;
										}
									}else if(gdo.getClassName().equals(Optical.CLASS_NAME)){
										String origSiteCuid = gdo.getAttrString(Optical.AttrName.origSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
										if(site != null){
											aSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(aSiteName != null && aSiteName.trim().length() > 0){
											aDevPort = aSiteName + "-" + aPortName;
										}
									}
								}
							}
							if(!dPCuid.startsWith(FiberJointPoint.CLASS_NAME)){
								if(dPCuid.startsWith(Fcabport.CLASS_NAME)){
									bDevPort = getGJDevPortByPointCuid(dPCuid);
								}else if(dPCuid.startsWith(FiberDpPort.CLASS_NAME)){
									bPortName = getGFDevPortByPointCuid(dPCuid);
									bDevPort = bPortName;
								}else{
									bPortName = getGFDevPortByPointCuid(dPCuid);
									if(gdo.getClassName().equals(JumpFiber.CLASS_NAME)){
										String siteCuid = gdo.getAttrString(JumpFiber.AttrName.relatedSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), siteCuid);
										if(site != null){
											bSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(bSiteName != null && bSiteName.trim().length() > 0){
											bDevPort = bSiteName + "-" + bPortName;
										}
								    }else if(gdo.getClassName().equals(Optical.CLASS_NAME)){
										String destSiteCuid = gdo.getAttrString(Optical.AttrName.destSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
										if(site != null){
											bSiteName = site.getAttrString(Manhle.AttrName.labelCn);
										}
										if(bSiteName != null && bSiteName.trim().length() > 0){
											bDevPort = bSiteName + "-" + bPortName;
										}
									}
								}
							}
							portCuid = dPCuid;
						}else if(dPCuid.equals(aPortCuid)){//止点
							if(!dPCuid.startsWith(FiberJointPoint.CLASS_NAME)){
								if(dPCuid.startsWith(Fcabport.CLASS_NAME)){
									aDevPort = getGJDevPortByPointCuid(dPCuid);
								}else if(dPCuid.startsWith(FiberDpPort.CLASS_NAME)){
									aPortName = getGFDevPortByPointCuid(dPCuid);
									aDevPort = aPortName;
								}else{
									aPortName = getGFDevPortByPointCuid(dPCuid);
									if(gdo.getClassName().equals(JumpFiber.CLASS_NAME)){
										String siteCuid = gdo.getAttrString(JumpFiber.AttrName.relatedSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), siteCuid);
										if(site != null){
											aSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(aSiteName != null && aSiteName.trim().length() > 0){
											aDevPort = aSiteName + "-" + aPortName;
										}
									}else if(gdo.getClassName().equals(Optical.CLASS_NAME)){
										String destSiteCuid = gdo.getAttrString(Optical.AttrName.destSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), destSiteCuid);
										if(site != null){
											aSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(aSiteName != null && aSiteName.trim().length() > 0){
											aDevPort = aSiteName + "-" + aPortName;
										}
									}
								}
							}
							if(!oPCuid.startsWith(FiberJointPoint.CLASS_NAME)){
								if(oPCuid.startsWith(Fcabport.CLASS_NAME)){
									bDevPort = getGJDevPortByPointCuid(oPCuid);
								}else if(oPCuid.startsWith(FiberDpPort.CLASS_NAME)){
									bPortName = getGFDevPortByPointCuid(oPCuid);
									bDevPort = bPortName;
								}else{
									bPortName = getGFDevPortByPointCuid(oPCuid);
									if(gdo.getClassName().equals(JumpFiber.CLASS_NAME)){
										String siteCuid = gdo.getAttrString(JumpFiber.AttrName.relatedSiteCuid);
										GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), siteCuid);
										if(site != null){
											bSiteName = site.getAttrString(Site.AttrName.labelCn);
										}
										if(bSiteName != null && bSiteName.trim().length() > 0){
											bDevPort = bSiteName + "-" + bPortName;
										}
								    }else if(gdo.getClassName().equals(Optical.CLASS_NAME)){
								    	String origSiteCuid = gdo.getAttrString(Optical.AttrName.origSiteCuid);
								    	GenericDO site = getDuctManagerBO().getObjByCuid(new BoActionContext(), origSiteCuid);
										if(site != null){
											bSiteName = site.getAttrString(Manhle.AttrName.labelCn);
										}
										if(bSiteName != null && bSiteName.trim().length() > 0){
											bDevPort = bSiteName + "-" + bPortName;
										}
									}
								}
							}
							portCuid = oPCuid;
						}
						if((bDevPort != null && bDevPort.trim().length() > 0) && (aDevPort != null && aDevPort.trim().length() > 0)){
							sGdo.setAttrValue("A_DEV_PORT", aDevPort);
							sGdo.setAttrValue("B_DEV_PORT", bDevPort);
							String nameCode = getNameCodeByDuctLine(gdo);
							sGdo.setAttrValue("NAME_CODE",nameCode);
							resDataList.add(sGdo);
						}
					}
				}
			}
			linkDataList.removeAll(resultList);
			searchRouteFromLink(resDataList, portCuid, linkDataList, zPortCuid, pointList);
		}else{
			return;
		}
	}

	/**
     * 根据光纤获取纤芯
     * @param curLinkCuid
     * @return
     */
    public static DataObjectList getLinkFiberRouteByLink(String opticalCuid) {
        if (opticalCuid != null && opticalCuid.startsWith(Optical.CLASS_NAME)) {
            DataObjectList opticalToFibers = null;
            try {
                opticalToFibers = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IOpticalToFiberBO.getOpticalToFiberByCuid",
                        new BoActionContext(),
                        opticalCuid);
            } catch (Exception e) {
                LogHome.getLog().error("获取纤芯数据失败", e);
                return null;
            }
            if (opticalToFibers != null && opticalToFibers.size() > 0) {
                OpticalToFiber opticalToFiber = (OpticalToFiber) opticalToFibers.get(0);
                opticalCuid = opticalToFiber.getFiberCuid();
            }
        }
        try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(PhyOdfPortBOHelper.ActionName.getFullLinkFibersByLink,
                     new BoActionContext(),
                     opticalCuid);
        } catch (Exception e) {
            LogHome.getLog().error("查询纤芯完整路由信息出错",e);
            return null;
        }
    }
    /**
     * 获取光交接箱端子拼接
     * @param pointCuid
     * @return
     */
    private String getGJDevPortByPointCuid(String pointCuid) {
		String devPort = "";
		try {
			String name = "";
			if (pointCuid != null && pointCuid.trim().length() > 0) {
				GenericDO fcabPort = getDuctManagerBO().getObjByCuid(new BoActionContext(), pointCuid);// 光交接箱端子
				if (fcabPort != null) {
					String moduleName = "", fiberCabName = "";
					String fcabPortName = fcabPort.getAttrString(Fcabport.AttrName.labelCn);
					String fcabMoCuid = fcabPort.getAttrString(Fcabport.AttrName.relatedModuleCuid);
					String fcabCuid = fcabPort.getAttrString(Fcabport.AttrName.relatedDeviceCuid);
					GenericDO fcabModule = getDuctManagerBO().getObjByCuid(new BoActionContext(), fcabMoCuid);
					GenericDO fiberCab = getDuctManagerBO().getObjByCuid(new BoActionContext(), fcabCuid);
					if (fiberCab != null) {
						fiberCabName = fiberCab.getAttrString(FiberCab.AttrName.labelCn);
					}
					if (fcabModule != null) {
						moduleName = fcabModule.getAttrString(Fibercabmodule.AttrName.labelCn);
					}
					name = fiberCabName + "/" + moduleName + "-" + fcabPortName;
				}
				devPort = name;
			}
		} catch (UserException e) {
			e.printStackTrace();
		}
		return devPort;
	}
    /**
     * 获取当前链接数据
     * @param linkDataList
     * @param pointCuid
     * @return
     */
	private DataObjectList getResultListByLists(DataObjectList linkDataList,String pointCuid) {
		DataObjectList resultList = new DataObjectList();
		if (linkDataList != null && linkDataList.size() > 0) {
			for (GenericDO gdo : linkDataList) {
				GenericDO origPoint = new GenericDO(), destPoint = new GenericDO();
				String origPointCuid = "", destPointCuid = "";
				Object orig = gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
				Object dest = gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
				if (orig instanceof GenericDO) {
					origPoint = (GenericDO) gdo.getAttrValue(JumpFiber.AttrName.origPointCuid);
					origPointCuid = origPoint.getCuid();
				} else if (orig instanceof String) {
					origPointCuid = gdo.getAttrString(JumpFiber.AttrName.origPointCuid);
				}
				if (dest instanceof GenericDO) {
					destPoint = (GenericDO) gdo.getAttrValue(JumpFiber.AttrName.destPointCuid);
					destPointCuid = destPoint.getCuid();
				} else if (dest instanceof String) {
					destPointCuid = gdo.getAttrString(JumpFiber.AttrName.destPointCuid);
				}
				if (origPointCuid.equals(pointCuid) || destPointCuid.equals(pointCuid)) {
					resultList.add(gdo);
				}
			}
		}
		return resultList;
	}
    
	/**
	 * 获取 光分纤箱端子 拼接
	 * @param pointCuid
	 * @return
	 */
	private String getGFDevPortByPointCuid(String pointCuid) {
		String portName = "";
		if(pointCuid != null && pointCuid.trim().length() > 0){
			GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), pointCuid);
			if(point != null){
				portName = point.getAttrString(Manhle.AttrName.labelCn);
			}
		}
		return portName;
	}
	
	/**
	 * 获取 光缆系统+纤芯编号 拼接
	 * @param DuctLineGdo
	 * @return
	 */
	private String getNameCodeByDuctLine(GenericDO DuctLineGdo) {
		String nameCode = "";
		try {
			if (DuctLineGdo != null) {
				if (DuctLineGdo.getClassName().equals(JumpFiber.CLASS_NAME)) {
					nameCode = "跳纤/跳线";
				} else if (DuctLineGdo.getClassName().equals(Optical.CLASS_NAME)) {
					String fiberSql = " cuid in ( select fiber_cuid from optical_to_fiber where optical_cuid ='"
							+ DuctLineGdo.getCuid() + "' ) and usage_state = 2 ";
					DataObjectList fiberList = getDuctManagerBO().getObjectsBySql(fiberSql, new Fiber());
					if (fiberList != null && fiberList.size() > 0) {
						String systemCuid = fiberList.get(0).getAttrString(Fiber.AttrName.relatedSystemCuid);
						GenericDO system = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
						String systemName = "";
						if (system != null) {
							systemName = system.getAttrString(Manhle.AttrName.labelCn);
						}
						nameCode = systemName+ "-F"+ fiberList.get(0).getAttrValue(Fiber.AttrName.wireNo).toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nameCode;
	}
	private String getPortNameAndDirecByCuid(String portCuid) {
		String str = "";
		GenericDO ptp = getObjByCuid(portCuid);
		if(ptp != null){
			String ptpLabelCn = ptp.getAttrString(Ptp.AttrName.labelCn);
			str = ptpLabelCn; 
		}
		return str;
	}

	private String getSiteNameByCuid(String cuid) {
		String siteName = "";
		GenericDO obj = getObjByCuid(cuid);
		if(obj != null){
			String className = obj.getClassName();
			if(className.equals(Site.CLASS_NAME)){
				siteName = obj.getAttrString(Site.AttrName.labelCn);
			}else if(className.equals(Room.CLASS_NAME)){
				String relatedSiteCuid = obj.getAttrString(Room.AttrName.relatedSiteCuid);
				obj = getObjByCuid(relatedSiteCuid);
				if(obj != null){
					siteName = obj.getAttrString(Site.AttrName.labelCn);
				}
			}else if(className.equals(Accesspoint.CLASS_NAME)){
				String relatedSiteCuid = obj.getAttrString(Accesspoint.AttrName.siteCuid);
				obj = getObjByCuid(relatedSiteCuid);
				if(obj != null){
					siteName = obj.getAttrString(Site.AttrName.labelCn);
				}
			}
		}
		return siteName;
	}

	private GenericDO getObjByCuid(String cuid) {
		GenericDO object = new GenericDO();
		try {
			if(cuid != null && cuid.trim().length() > 0){
				object = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			}
		} catch (UserException e) {
			e.printStackTrace();
			LogHome.getLog().error("查询数据失败",e);
		}
		return object;
	}

	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
}
