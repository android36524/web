package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.boco.common.util.debug.LogHome;
import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.FcabportBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberDpPortBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberJointPointBOHelper;
import com.boco.transnms.server.bo.helper.dm.FibercabmoduleBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointPointBO;

/**
 * 
 * @author wanghuaiting
 *
 */

public class PortsManageAction {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String RESULTS="OK";
	
	private IFiberDpPortBO getFiberDpPortBO(){
		return BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
	}
	
	private IFiberJointPointBO getFiberJointPointBO(){
		return BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class);
	}
	
	/**
	 * 添加光交接箱模块
	 * @param fCabModule
	 * @throws UserException
	 */
	public String addFiberCabModule(Map map) throws UserException{		
		//获取map的值
		String fibercabCuid=map.get("RELATED_DEVICE_CUID").toString();    //光交接箱CUID
		String labelCn=map.get("LABEL_CN").toString();     //添加模块的名称
		long moduleRow=Long.valueOf(map.get(Fibercabmodule.AttrName.moduleRow).toString());   //添加模块的所在行
		long moduleCol=Long.valueOf(map.get(Fibercabmodule.AttrName.moduleCol).toString());   //添加模块的所在列

		String sql=" RELATED_DEVICE_CUID='"+fibercabCuid+"'";
		DataObjectList fibercabModules=getFiberCabModuleBySql(sql);

		if(fibercabModules==null){
			return null;
		}			
		//判断新创建的fiberCabModule的名称是否已经存在
		checkModuleName(fibercabModules, labelCn);
		//判断新创建的fiberCabModule的行、列号
		checkModuleRowAndCol(fibercabModules, moduleRow, moduleCol);
		//保存模块
		save(fibercabCuid,labelCn,moduleRow,moduleCol);
		String scene = (String)map.get("scene");
		String segGroupCuid = (String)map.get("segGroupCuid");
		try{
			if("erratum".equals(scene)){//勘误场景，补录资源需要和单位工程关联
				if(StringUtils.isNotEmpty(fibercabCuid)){
					DataObjectList fibercabs = new DataObjectList();
					FiberCab ddo = new FiberCab();
					ddo.setCuid(fibercabCuid);
					ddo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
					fibercabs.add(ddo);
					BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.createSegGroupToReses",new BoActionContext(),fibercabs);
				}
			}
		}catch(Exception ex){
			LogHome.getLog().error("勘误场景，补录资源入关联表报错："+ex.getMessage());
			throw new UserException("勘误场景，补录资源入关联表报错："+ex.getMessage());
		}
		return RESULTS;
	}
	
	private DataObjectList getFiberCabModuleBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFibercabmoduleBO.getFibercabmoduleBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光交接箱模块失败",e);
			throw new UserException("通过sql查询光交接箱模块失败");
		}
		return list;
	}

	private DataObjectList getFiberJointPointBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberJointPointBO.getFiberJointPointBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光交接箱模块失败",e);
			throw new UserException("通过sql查询光交接箱模块失败");
		}
		return list;
	}
	
	private DataObjectList getFiberJointBoxBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberJointBoxBO.getFiberJointBoxBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光交接箱模块失败",e);
			throw new UserException("通过sql查询光交接箱模块失败");
		}
		return list;
	}
	
	private DataObjectList getFiberDpBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberDpBO.getFiberDpBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光交接箱模块失败",e);
			throw new UserException("通过sql查询光交接箱模块失败");
		}
		return list;
	}

	private DataObjectList getFiberCabBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberCabBO.getFiberCabBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光交接箱模块失败",e);
			throw new UserException("通过sql查询光交接箱模块失败");
		}
		return list;
	}
	
	private void checkModuleName(DataObjectList fibercabModules,String labelCn) throws UserException{
		 String name=new String();
		 for(GenericDO gdo:fibercabModules){	
			 name=gdo.getAttrString("LABEL_CN");
			 if(name.equals(labelCn)){
				 throw new UserException("新创建的光交接箱模块名称已经存在");
			 }
		 }
	}
	
	private void checkModuleRowAndCol(DataObjectList fibercabModules,long moduleRow,long moduleCol) throws UserException{
		
		if(moduleRow<1L || moduleCol<1L){
			throw new UserException("新创建的光交接箱模块行列均不能为0");
		}
		
		for(GenericDO gdo:fibercabModules){
			Fibercabmodule fcabModule=(Fibercabmodule)gdo;
			long tempModuleRow=fcabModule.getModuleRow();
			long tempModuleCol=fcabModule.getModuleCol();
			
			if(tempModuleRow==moduleRow && tempModuleCol==moduleCol){
				throw new UserException("新创建的光交接箱模块所在行列数已经被使用");
			}
		}
	}
	
	private Fibercabmodule save(String fibercabCuid,String labelCn,long moduleRow,long moduleCol) throws UserException{
		 Fibercabmodule fibercabmodule = new Fibercabmodule();
		 fibercabmodule.setCuid();
		 fibercabmodule.setLabelCn(labelCn);
         fibercabmodule.setModuleRow(moduleRow);
         fibercabmodule.setModuleCol(moduleCol);
         fibercabmodule.setAttrValue(Fibercabmodule.AttrName.relatedDeviceCuid, fibercabCuid);
         Fibercabmodule addFibercabmodule=null;
         try {
        	 addFibercabmodule=(Fibercabmodule)BoCmdFactory.getInstance().execBoCmd(FibercabmoduleBOHelper.ActionName.addFibercabmodule,
                     new BoActionContext(), fibercabmodule);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("添加模块失败",e);
			throw new UserException("添加模块失败");
		}
        return addFibercabmodule;
	}
    /**
     * 修改光交接箱模块
     * @param fCabModule
     */
	public String modifyFiberCabModule(Map map) throws UserException{
		 //获取map中的值
		 String fibercabCuid=map.get("RELATED_DEVICE_CUID").toString();    //模块所在的光交接箱的CUID
		 String moduleCuid=map.get("CUID").toString();                     //模块的CUID
		 String labelCn=map.get("LABEL_CN").toString();                    //要修改的模块的名称
		 
		 String sql=" RELATED_DEVICE_CUID='"+fibercabCuid+"'";
		 DataObjectList fibercabModules=getFiberCabModuleBySql(sql);
			
		 if(fibercabModules==null){
			return null;
		 }
			
		 //判断新创建的fiberCabModule的名称是否已经存在
		 checkModuleName(fibercabModules, labelCn);
		 //修改模块
		 Fibercabmodule modifyFiberCabModule=modifyMoudle(fibercabModules,moduleCuid,labelCn);		 
		 
		 return RESULTS;
	}
	
	private Fibercabmodule modifyMoudle(DataObjectList fibercabModules,String moduleCuid,String labelCn) throws UserException{		
		Fibercabmodule fibercabmodule=null;		
		for(GenericDO gdo:fibercabModules){
			fibercabmodule=(Fibercabmodule)gdo;
			if(moduleCuid.equals(fibercabmodule.getCuid())){
				break;
			}
		}		
	    fibercabmodule.setLabelCn(labelCn);
	    
	    Fibercabmodule modifyFiberCabModule=null;
	    try {
	    	modifyFiberCabModule=(Fibercabmodule)BoCmdFactory.getInstance().execBoCmd(FibercabmoduleBOHelper.ActionName.modifyFibercabmodule, new BoActionContext(), fibercabmodule);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("修改光交接箱模块失败",e);
			throw new UserException("修改光交接箱模块失败");
		}	
	    return modifyFiberCabModule;
	}
	
    /**
     * 删除光交接箱模块
     * @param list
     */
	public String deleteFiberCabModule(Map map) throws UserException{
		//获取map的值
		String moduleCuid=map.get("CUID").toString();       //模块的CUID
		GenericDO gdo = null;
		try{
			if(StringUtils.isNotEmpty(moduleCuid)){
				gdo = (GenericDO)BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), moduleCuid);
			}
		}catch(Exception ex){
			logger.error("查询模块出错：",ex);
			throw new UserException("查询模块出错:"+ex.getMessage());
		}
		
		//删除module
		DataObjectList deleteList=deleteModule(moduleCuid);
//		String scene = map.get("scene").toString();
//		String segGroupCuid = map.get("segGroupCuid").toString();
//		try{
//			if("erratum".equals(scene)){//勘误场景，删除补录资源需要和单位工程关联
//				if(gdo!=null){
//					DataObjectList resList = new DataObjectList();
//					GenericDO resgdo = new GenericDO();
//					resgdo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
//					resgdo.setAttrValue("CUID", gdo.getAttrValue(Fcabport.AttrName.relatedDeviceCuid));
//					resList.add(gdo);
//					BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.deleteSegGroupToReses",new BoActionContext(),resList);
//				}
//			}
//		}catch(Exception ex){
//			logger.error("勘误场景，删除补录资源关联关系报错："+ex.getMessage());
//			throw new UserException("勘误场景，删除补录资源关联关系报错："+ex.getMessage());
//		}
		return RESULTS;      
	}
    
	private DataObjectList deleteModule(String moduleCuid) throws UserException{
		String sql=" CUID='"+moduleCuid+"'";
		DataObjectList list=getFiberCabModuleBySql(sql);		
		if(list==null){
			return null;
		}
		DataObjectList deleteList=new DataObjectList();
		try {
			deleteList=(DataObjectList)BoCmdFactory.getInstance().execBoCmd(
					"IFibercabmoduleBO.deleteFibercabmodules",
					new BoActionContext(), list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("删除光交接箱模块失败",e);
			throw new UserException("删除光交接箱模块失败");
		}
        
		return deleteList;
		
	}	

	public String deletePorts(List<Map<String,String>> list) throws UserException{
		if(list==null || list.size()==0){
			return null;
		}
		//获取选中的端子
		DataObjectList portLists = getSelectedPorts(list);
		//检查端子状态
		checkPortServerState(portLists);
		//删除端子
		delete(portLists);
//		Map map = list.get(0);
//		String scene = map.get("scene").toString();
//		String segGroupCuid = map.get("segGroupCuid").toString();
//		try{
//			if("erratum".equals(scene)){//勘误场景，删除补录资源需要和单位工程关联
//				if(portLists!=null && portLists.size()>0){
//					DataObjectList resList = new DataObjectList();
//					for(GenericDO ddo : portLists){
//						GenericDO gdo = new GenericDO();
//						gdo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
//						gdo.setAttrValue("CUID", ddo.getAttrValue(Fcabport.AttrName.relatedDeviceCuid));
//						if(!resList.contains(gdo)){
//							resList.add(gdo);
//						}
//					}
//					BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.deleteSegGroupToReses",new BoActionContext(),resList);
//				}
//			}
//		}catch(Exception ex){
//			logger.error("勘误场景，删除补录资源关联关系报错："+ex.getMessage());
//			throw new UserException("勘误场景，删除补录资源关联关系报错："+ex.getMessage());
//		}
		return RESULTS;
	}
    
	private DataObjectList getSelectedPorts(List<Map<String, String>> list) throws UserException {
		DataObjectList portLists=new DataObjectList();
		List<String> cuids=new ArrayList<String>();		
		for(Map<String,String> map:list){
			cuids.add(map.get("CUID"));
		}	
		
		String portCuid=cuids.get(0);
		int size=cuids.size();
		
		StringBuffer sql=new StringBuffer();
		if(size==1){
			sql.append(" CUID='"+cuids.get(0)+"'");
			DataObjectList tempList =new DataObjectList();
			if(portCuid.startsWith(Fcabport.CLASS_NAME)){
				tempList= getFcabPortBySql(sql.toString());
			}
			else if(portCuid.startsWith(FiberDpPort.CLASS_NAME)){
				tempList=getFiberDpPortBySql(sql.toString());
			}
			else if(portCuid.startsWith(FiberJointPoint.CLASS_NAME)){
				tempList=getFiberJointPointBySql(sql.toString());
			}
			portLists.addAll(tempList);
			sql.setLength(0);
		}
		else if(size>1){
			 for(int i=1;i<=size;i++){
				 if(i%50!=0){
					 sql.append(" CUID='"+cuids.get(i-1)+"' OR ");
				 }
				 if(i%50==0){
					 sql.append(" CUID='"+cuids.get(i-1)+"' OR ");
					 DataObjectList tempList =new DataObjectList();
					 //tempList= getFcabPortBySql(sql.substring(0, sql.length()-4));
					 if(portCuid.startsWith(Fcabport.CLASS_NAME)){
							tempList= getFcabPortBySql(sql.substring(0, sql.length()-4));
					 }
					 else if(portCuid.startsWith(FiberDpPort.CLASS_NAME)){
							tempList=getFiberDpPortBySql(sql.substring(0, sql.length()-4));
					 }
					 else if(portCuid.startsWith(FiberJointPoint.CLASS_NAME)){
							tempList=getFiberJointPointBySql(sql.substring(0, sql.length()-4));
					 }
					 portLists.addAll(tempList);
					 sql.setLength(0);
				 }				 
			 }
			 if(size%50!=0){
				 DataObjectList tempList =new DataObjectList();
				 if(portCuid.startsWith(Fcabport.CLASS_NAME)){
						tempList= getFcabPortBySql(sql.substring(0, sql.length()-4));
				 }
				 else if(portCuid.startsWith(FiberDpPort.CLASS_NAME)){
						tempList=getFiberDpPortBySql(sql.substring(0, sql.length()-4));
				 }
				 else if(portCuid.startsWith(FiberJointPoint.CLASS_NAME)){
						tempList=getFiberJointPointBySql(sql.substring(0, sql.length()-4));
				 }
				 portLists.addAll(tempList);
				 sql.setLength(0);
			 }			 
		}
		return portLists;
	}

//	private DataObjectList getSelectedPorts(List<Map<String, String>> list) throws UserException{
//		DataObjectList portLists=new DataObjectList();
//		
//		List<String> cuids=new ArrayList<String>();		
//		for(Map<String,String> map:list){
//			cuids.add(map.get("CUID"));
//		}		
//		String portCuid=cuids.get(0);	
//		
//		if(portCuid.startsWith(Fcabport.CLASS_NAME)){	
//			DataObjectList fcabPortList=getFcabPortBySql(" CUID='"+portCuid+"'");
//			
//			if(fcabPortList==null || fcabPortList.size()==0){
//				return null;
//			}		
//			Fcabport fcabPort =(Fcabport)fcabPortList.get(0);
//			DataObjectList fcplist = getFcabPortBySql(" RELATED_DEVICE_CUID='"+fcabPort.getRelatedDeviceCuid()+"'");
//
//			for(GenericDO gdo:fcabPortList){
//				if(cuids.contains(gdo.getCuid())){
//					portLists.add(gdo);
//				}
//			}
//		}
//		else if(portCuid.startsWith(FiberDpPort.CLASS_NAME)){
//			DataObjectList tempList=getFiberDpPortBySql(" CUID='"+portCuid+"'");
//			if(tempList!=null && tempList.size()>0){
//				 FiberDpPort fiberDpPort=(FiberDpPort)tempList.get(0);
//				 DataObjectList fiberDpPortList=getFiberDpPortBySql(" RELATED_DEVICE_CUID='"+fiberDpPort.getRelatedDeviceCuid()+"'");
//				 
//				 for(GenericDO gdo: fiberDpPortList){
//					 if(cuids.contains(gdo.getCuid())){
//						 portLists.add(gdo);
//					 }
//				 }	 
//			}		
//		}
//		else if(portCuid.startsWith(FiberJointPoint.CLASS_NAME)){
//			DataObjectList tempList=getFiberJointPointBySql(" CUID='"+portCuid+"'");
//			if(tempList!=null && tempList.size()>0){
//				FiberJointPoint fiberJointPoint=(FiberJointPoint)tempList.get(0);
//				DataObjectList fiberJointPointList=getFiberJointPointBySql(" RELATED_DEVICE_CUID='"+fiberJointPoint.getRelatedDeviceCuid()+"'");
//				
//				for(GenericDO gdo:fiberJointPointList){
//					if(cuids.contains(gdo.getCuid())){
//						portLists.add(gdo);
//					}
//				}
//			}
//		}
//		
//		return portLists;
//	}
	
	private DataObjectList getFcabPortBySql(String sql) throws UserException{
		DataObjectList list=new DataObjectList();
		try {
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFcabportBO.getFcabportBySql",new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过SQL查询光交接箱端子失败",e);
			throw new UserException("通过SQL查询光交接箱端子失败");
		}
		return list;
    }
	
	private void delete(DataObjectList portLists) throws UserException{
		GenericDO gdo=(GenericDO)portLists.get(0);
		String cuid=gdo.getCuid();
		
		try {
		     if(cuid.startsWith(Fcabport.CLASS_NAME)){
				 BoCmdFactory.getInstance().execBoCmd(FcabportBOHelper.ActionName.deleteFcabportsWithModule, new BoActionContext(), portLists);
			 }
		     else if(cuid.startsWith(FiberDpPort.CLASS_NAME)){
		    	 BoCmdFactory.getInstance().execBoCmd(FiberDpPortBOHelper.ActionName.deleteFiberDpPorts, new BoActionContext(), portLists);
		     }
		     else if(cuid.startsWith(FiberJointPoint.CLASS_NAME)){
		    	 BoCmdFactory.getInstance().execBoCmd(FiberJointPointBOHelper.ActionName.deleteFiberJointPoints, new BoActionContext(), portLists);
		     }
		   
		 } catch (Exception e) {
			// TODO: handle exception
			logger.error("删除端子失败",e);
			throw new UserException("删除端子失败:"+e.getMessage());
		}		
	}
	
	private void checkPortServerState(DataObjectList portLists) throws UserException{
		//端子业务状态的判断；1-空闲,2-占用,3-预占,4-坏
		long serverState=0L;
		String labelCn=new String();
		for(int i=0;i<portLists.size();i++){
			GenericDO gdo=(GenericDO)portLists.get(i);
			serverState=Long.valueOf(gdo.getAttrValue("SERVICE_STATE").toString());
			labelCn=gdo.getAttrString("LABEL_CN");
			if(serverState==2L){
				throw new UserException("端子["+labelCn+"]被预占,不能删除");
			}
			else if(serverState==3L){
				throw new UserException("端子["+labelCn+"]被占用,不能删除");
			}
		}
	}
	
    public String addPortsByColumn(Map map) throws UserException{
    	DataObjectList allList = new DataObjectList();
	    String relatedModuleCuid=null;
	    String relatedDeviceCuid=null;
	    String relatedDistrictCuid=null;
	    long portRow=0L;
	    long portCol=0L;
		//获取map的值
	    if(map.containsKey("RELATED_MODULE_CUID")){   //处理光交接箱模块端子
	    	relatedModuleCuid=map.get("RELATED_MODULE_CUID").toString();       //端子所在的模块的CUID		
			String fcmSql=" CUID='"+relatedModuleCuid+"'";
			DataObjectList fibercabModulelist=getFiberCabModuleBySql(fcmSql);        
			Fibercabmodule fibercabModule=(Fibercabmodule)fibercabModulelist.get(0);
			portRow=fibercabModule.getPortRow();
			portCol=fibercabModule.getPortCol();
			relatedDeviceCuid=fibercabModule.getRelatedDeviceCuid();       //端子所在的交接箱的CUID		
			String fcbSql=" CUID='"+relatedDeviceCuid+"'";
			DataObjectList fibercabList=getFiberCabBySql(fcbSql);		
			FiberCab fiberCab=(FiberCab)fibercabList.get(0);		
			relatedDistrictCuid = getRelatedDistrictCuid(fiberCab);
			allList.addAll(fibercabList);
	    }
	    else if(map.containsKey("CUID")){
	    	relatedDeviceCuid=map.get("CUID").toString();
	    	if(relatedDeviceCuid.startsWith(FiberDp.CLASS_NAME)){   //处理光分纤箱端子
	    		String sql=" CUID='"+relatedDeviceCuid+"'";
				DataObjectList fiberDpList=getFiberDpBySql(sql);
				FiberDp fiberDp=(FiberDp)fiberDpList.get(0);
				relatedDistrictCuid=getRelatedDistrictCuid(fiberDp);
				Map<String,Long> maxRowAndColMap=getMaxRowAndCol(fiberDp);
				portRow=maxRowAndColMap.get("MAXROW");
				portCol=maxRowAndColMap.get("MAXCOL");
				allList.addAll(fiberDpList);
	    	}
	    	else if(relatedDeviceCuid.startsWith(FiberJointBox.CLASS_NAME)){  //处理光接头盒焊点
	    		String sql=" CUID='"+relatedDeviceCuid+"'";
	    		DataObjectList fiberJointBoxList=getFiberJointBoxBySql(sql);
	    		FiberJointBox fiberJointBox=(FiberJointBox)fiberJointBoxList.get(0);
	    		relatedDistrictCuid=getRelatedDistrictCuid(fiberJointBox);
	    		Map<String,Long> maxRowAndColMap=getMaxRowAndCol(fiberJointBox);
				portRow=maxRowAndColMap.get("MAXROW");
				portCol=maxRowAndColMap.get("MAXCOL");
				allList.addAll(fiberJointBoxList);
	    	}
	    }
		
		String prefix=map.get("PREFIX").toString();                                //前缀
		String postfix=map.get("POSTFIX").toString();                              //后缀
		long startIndex=Long.valueOf(map.get("STARTINDEX").toString());            //起始编号
		long rows=portRow;                                                         //当前模块的端子的最大行
		long addcols=Long.valueOf(map.get("ADDCOLS").toString());                  //端子增加的列数
		long cols=portCol;                                                         //当前模块的端子的最大列

		long count=0L;		
		DataObjectList list=new DataObjectList();
		GenericDO gdo=null;
		
		for(long row=1;row<=rows;row++){
			for (long col = cols + 1; col <= cols+addcols; col++) {
	            long index = startIndex + (count++);	            
	            gdo=createPort(relatedModuleCuid, relatedDeviceCuid, relatedDistrictCuid, prefix, postfix, row, col, index);
	            if(gdo!=null){
	            	list.add(gdo);
	            }	            
	        }
		}	
        savePorts(list);
        String scene = (String)map.get("scene");
		String segGroupCuid = (String)map.get("segGroupCuid");
		try{
			if("erratum".equals(scene)){//勘误场景，补录资源需要和单位工程关联
				if(allList!=null && allList.size()>0){
					for(GenericDO ddo : allList){
						ddo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
					}
					BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.createSegGroupToReses",new BoActionContext(),allList);
				}
			}
		}catch(Exception ex){
			LogHome.getLog().error("勘误场景，补录资源入关联表报错："+ex.getMessage());
			throw new UserException("勘误场景，补录资源入关联表报错："+ex.getMessage());
		}
        return RESULTS;
  }
	
	private String getRelatedDistrictCuid(GenericDO device) {
		String relatedDistrictCuid="";		
		Object relatedDistrict=device.getAttrValue(FiberCab.AttrName.relatedDistrictCuid);
		if(relatedDistrict instanceof GenericDO){
			relatedDistrictCuid=((GenericDO) relatedDistrict).getCuid();
		}
		else if(relatedDistrict instanceof String){
			relatedDistrictCuid=relatedDistrict.toString();
		}
		return relatedDistrictCuid;
	}
	
	/**
	 * 按行添加端子
	 * @param list
	 * @throws UserException
	 */	
	public String addPortsByRow(Map map) throws UserException{
		//获取map的值
		DataObjectList allList = new DataObjectList();
		String relatedDeviceCuid=null;
		String relatedDistrictCuid=null;
		String relatedModuleCuid=null;
		long portRow=0L;
		
		if(map.containsKey("RELATED_MODULE_CUID")){  //处理光交接箱模块端子
			relatedModuleCuid=map.get("RELATED_MODULE_CUID").toString();      //端子所在的模块的CUID		
			String fcmSql=" CUID='"+relatedModuleCuid+"'";
			DataObjectList fibercabModulelist=getFiberCabModuleBySql(fcmSql);        
			Fibercabmodule fibercabModule=(Fibercabmodule)fibercabModulelist.get(0);			
			portRow=fibercabModule.getPortRow(); 			
			relatedDeviceCuid=fibercabModule.getRelatedDeviceCuid();       //端子所在的交接箱的CUID
			
			String fcbSql=" CUID='"+relatedDeviceCuid+"'";
			DataObjectList fibercabList=getFiberCabBySql(fcbSql);		
			FiberCab fiberCab=(FiberCab)fibercabList.get(0);		
			relatedDistrictCuid = getRelatedDistrictCuid(fiberCab);
			allList.addAll(fibercabList);
		}
		else if(map.containsKey("CUID")){
			relatedDeviceCuid=map.get("CUID").toString();
			if(relatedDeviceCuid.startsWith(FiberDp.CLASS_NAME)){   //处理光分纤箱端子
				String sql=" CUID='"+relatedDeviceCuid+"'";
				DataObjectList fiberDpList=getFiberDpBySql(sql);
				FiberDp fiberDp=(FiberDp)fiberDpList.get(0);
				relatedDistrictCuid=getRelatedDistrictCuid(fiberDp);
				Map<String,Long> maxRowAndColMap=getMaxRowAndCol(fiberDp);
				portRow=maxRowAndColMap.get("MAXROW");
				allList.addAll(fiberDpList);
			}
			else if(relatedDeviceCuid.startsWith(FiberJointBox.CLASS_NAME)){  //处理光接头盒焊点
				String sql=" CUID='"+relatedDeviceCuid+"'";
				DataObjectList fiberJointBoxList=getFiberJointBoxBySql(sql);
				FiberJointBox fiberJointBox=(FiberJointBox)fiberJointBoxList.get(0);
				relatedDistrictCuid=getRelatedDistrictCuid(fiberJointBox);
				Map<String,Long> maxRowAndColMap=getMaxRowAndCol(fiberJointBox);
				portRow=maxRowAndColMap.get("MAXROW");
				allList.addAll(fiberJointBoxList);
			}
			
		}

		String prefix=map.get("PREFIX").toString();                                //前缀
		String postfix=map.get("POSTFIX").toString();                              //后缀
		long startIndex=Integer.valueOf(map.get("STARTINDEX").toString());          //起始编号
		long rows=portRow;                                                        //当前模块的端子的最大行
		long addrows=Long.valueOf(map.get("ADDROWS").toString());                //新增的行数
		long cols=Long.valueOf(map.get("ADDCOLS").toString());                     //当前模块的端子的最大列
        
		
		DataObjectList list=new DataObjectList();
		GenericDO gdo=null;
		long count=0L;
		for (long i = rows + 1; i <= rows + addrows; i++) {
            for (long j = 1; j <= cols; j++) {
            	long index = startIndex + (count++);                
                gdo=createPort(relatedModuleCuid, relatedDeviceCuid, relatedDistrictCuid, prefix, postfix, i, j, index);
                if(gdo!=null){
                	list.add(gdo);
                }
            }
        }
		savePorts(list);
		String scene = (String)map.get("scene");
		String segGroupCuid = (String)map.get("segGroupCuid");
		try{
			if("erratum".equals(scene)){//勘误场景，补录资源需要和单位工程关联
				if(allList!=null && allList.size()>0){
					for(GenericDO ddo : allList){
						ddo.setAttrValue("RELATED_SEG_GROUP_CUID", segGroupCuid);
					}
					BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.createSegGroupToReses",new BoActionContext(),allList);
				}
			}
		}catch(Exception ex){
			LogHome.getLog().error("勘误场景，补录资源入关联表报错："+ex.getMessage());
		}
		return RESULTS;
	}
    
	/**
	 * 获取光分纤箱、光接头盒的最大行、最大列
	 * @param device
	 * @return
	 * @throws UserException
	 */
	private Map<String,Long> getMaxRowAndCol(GenericDO device) throws UserException{
		String sql=new String();
		sql=" RELATED_DEVICE_CUID='"+device.getCuid()+"'";
		DataObjectList portList=new DataObjectList();
		if(device instanceof FiberDp){
			portList=getFiberDpPortBySql(sql);
		}
		else if(device instanceof FiberJointBox){
			portList=getFiberJointPointBySql(sql);
		}
		long maxRow=0L;
		long maxCol=0L;
		for(GenericDO gdo:portList){
			if(gdo instanceof FiberDpPort){
				FiberDpPort fiberDpPort=(FiberDpPort)gdo;
				if(fiberDpPort.getNumInMrow()>maxRow){
					maxRow=fiberDpPort.getNumInMrow();
				}
				if(fiberDpPort.getNumInMcol()>maxCol){
					maxCol=fiberDpPort.getNumInMcol();
				}
			}
			else if(gdo instanceof FiberJointPoint){
				FiberJointPoint fiberJointPoint=(FiberJointPoint)gdo;
				if(fiberJointPoint.getNumInMrow()>maxRow){
					maxRow=fiberJointPoint.getNumInMrow();
				}
				if(fiberJointPoint.getNumInMcol()>maxCol){
					maxCol=fiberJointPoint.getNumInMcol();
				}
			}
		}		
		Map<String,Long> map=new HashMap<String, Long>();		
		map.put("MAXROW", maxRow);
		map.put("MAXCOL", maxCol);		
		return map;
	}
	
	private DataObjectList getFiberDpPortBySql(String sql) throws UserException{
        DataObjectList list=new DataObjectList();
		try {			
			list=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberDpPortBO.getFiberDpPortBySql",new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("按行添加端子失败", e);
			throw new UserException("按行添加端子失败");
		}
		return list;
	}

	private void savePorts(DataObjectList list) throws UserException{
		if(list==null || list.size()==0){
			return;
		}
		try {
			GenericDO gdo=list.get(0);
			if(gdo instanceof Fcabport){
				BoCmdFactory.getInstance().execBoCmd("IFcabportBO.addFcabportsWithModule",new BoActionContext(),list);
			}
			else if(gdo instanceof FiberDpPort){
				BoCmdFactory.getInstance().execBoCmd(FiberDpPortBOHelper.ActionName.addFiberDpPorts,
                        new BoActionContext(), list);
			}
			else if(gdo instanceof FiberJointPoint){
				BoCmdFactory.getInstance().execBoCmd(FiberJointPointBOHelper.ActionName.addFiberJointPoints,
                        new BoActionContext(), list);				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("按行添加端子失败", e);
			throw new UserException("按行添加端子失败:"+e.getMessage());
		}
				
	}
	/**
	 * 创建添加的端子
	 * @param relatedModuleCuid
	 * @param relatedDeviceCuid
	 * @param relatedDistrictCuid
	 * @param prefix
	 * @param postfix
	 * @param row
	 * @param col
	 * @param index
	 * @return
	 */
	private GenericDO createPort(String relatedModuleCuid,
			String relatedDeviceCuid, String relatedDistrictCuid,
			String prefix, String postfix, long row, long col, long index) {
		 if(relatedDeviceCuid.startsWith(FiberCab.CLASS_NAME)){
			 Fcabport fcabport = new Fcabport();
			 fcabport.setCuid();
			 fcabport.setIsConnected(false);
			 fcabport.setIsConnectedToFiber(false);
			 fcabport.setAttrValue(Fcabport.AttrName.labelCn, createNewName(prefix, index, postfix));
			 fcabport.setNumInMrow(row);
			 fcabport.setNumInMcol(col);
			 fcabport.setAttrValue("RELATED_MODULE_CUID", relatedModuleCuid);
			 fcabport.setAttrValue("RELATED_DEVICE_CUID", relatedDeviceCuid);
	         fcabport.setAttrValue("RELATED_DISTRICT_CUID",relatedDistrictCuid);
			 fcabport.setServiceState(1);
			 return fcabport;
		 }
		 else if(relatedDeviceCuid.startsWith(FiberDp.CLASS_NAME)){
			 FiberDpPort fiberDpPort = new FiberDpPort();
			 fiberDpPort.setCuid();
             fiberDpPort.setIsConnected(false);
             fiberDpPort.setIsConnectedToFiber(false);
             fiberDpPort.setAttrValue(FiberDpPort.AttrName.labelCn, createNewName(prefix, index, postfix));
             fiberDpPort.setNumInMrow(row);
             fiberDpPort.setNumInMcol(col);
             fiberDpPort.setAttrValue("RELATED_DEVICE_CUID", relatedDeviceCuid);
             fiberDpPort.setAttrValue("RELATED_DISTRICT_CUID", relatedDistrictCuid);
             fiberDpPort.setServiceState(1);
             return fiberDpPort;
		 }
		 else if(relatedDeviceCuid.startsWith(FiberJointBox.CLASS_NAME)){
			 FiberJointPoint fiberJointPoint = new FiberJointPoint();
			 fiberJointPoint.setCuid();
             fiberJointPoint.setIsConnected(false);
             fiberJointPoint.setIsConnectedToFiber(false);
             fiberJointPoint.setAttrValue(FiberJointPoint.AttrName.labelCn, createNewName(prefix, index, postfix));
             fiberJointPoint.setNumInMrow(row);
             fiberJointPoint.setNumInMcol(col);
             fiberJointPoint.setAttrValue("RELATED_DEVICE_CUID", relatedDeviceCuid);
             fiberJointPoint.setAttrValue("RELATED_DISTRICT_CUID", relatedDistrictCuid);
             fiberJointPoint.setServiceState(1);
             return fiberJointPoint;
		 }		 
		 return null;
	}	
	
	private String createNewName(String prefix,long index,String postfix){
		return prefix + index + postfix;
	}	

	/**
	 * 端子管理 查看关联信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map> getLinkFiber(List<Map<String,String>> list) throws Exception{
		List<Map> lst = new ArrayList<Map>();
		for(Map<String,String> map : list){
			for(Map.Entry<String,String> entry : map.entrySet()){
				String portCuid = entry.getValue();
				Map portMap = new HashMap();
				if(portCuid.startsWith(FiberDpPort.CLASS_NAME)){
					FiberDpPort port = getFiberDpPortBO().getFiberDpPortByCuid(new BoActionContext(), portCuid);
					portMap = getdpLinkFiber(port);
				}else if(portCuid.startsWith(FiberJointPoint.CLASS_NAME)){
					FiberJointPoint port = getFiberJointPointBO().getFiberJointPointByCuid(new BoActionContext(), portCuid);
					portMap = getdpLinkFiber(port);
				}
				if(portMap.size()>0){
					lst.add(portMap);
				}
			}
		}
		return lst;
	}
	
    /**
     * 光分纤箱端子管理 查看关联信息
     * @param portNode
     */
    private Map getdpLinkFiber(GenericDO port) throws Exception {
    	
    	Map fdpportMap = new HashMap();
        String relatedwire = "RELATED_WIRESYSTEM"; //所属光缆
        String relatedwireseg = "RELATED_WIRESEG"; //所属光缆段 RELATED_WIRESEG
        String fiberno = "FIBERNO"; //纤芯号 FIBERNO
        String destdevtype = "DEST_DEV_TYPE"; //目标设施类型 DEST_DEV_TYPE
        String destdevno = "DEST_DEV_NO"; //目标设施编码 DEST_DEV_NO
        String destdevpointno = "DEST_DEV_PORTNO"; //目标设施端子号DEST_DEV_PORTNO
        String wireFiberCount = "WIRE_FIBER_COUNT"; //光缆总芯数
        String fiberUseCount = "FIBER_USE_COUNT"; //安装芯数
        String usageState = "USAGE_STATE"; //使用芯号(纤芯状态)
        String ustate ="";
        String relSys = "";
        String relseg = "";
        String fiberInfo = "";
        String destPortLabelCn = "";
        String destDevLabelCn = "";
        String destDevTypeName = "";
        Long AwirefiberCount = 0L;
        Long AfiberUseCount = 0L;
        String OLdrelatedSystemCuid = "";
        try {
//        	FiberDpPort port = getFiberDpPortBO().getFiberDpPortByCuid(new BoActionContext(), portCuid);
            if ((port.getObjectNum()) != 0) {
                DataObjectList fibers = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getConnectedFibers,
                    new BoActionContext(),port.getObjectNum());
                if (fibers != null && fibers.size() > 0) {
                    for (int i = 0; i < fibers.size(); i++) {
                        Fiber fiber = (Fiber) fibers.get(i);
                        GenericDO wsystem = null;
                        GenericDO origSite = null;
                        GenericDO destSite = null;
                        if (fiber.getAttrValue(Fiber.AttrName.relatedSystemCuid) instanceof GenericDO) {
                            wsystem = (GenericDO) fiber.getAttrValue(Fiber.AttrName.relatedSystemCuid);
                        }
                        if (fiber.getAttrValue(Fiber.AttrName.origSiteCuid) instanceof GenericDO) {
                            origSite = (GenericDO) fiber.getAttrValue(Fiber.AttrName.origSiteCuid);
                        }
                        if (fiber.getAttrValue(Fiber.AttrName.destSiteCuid) instanceof GenericDO) {
                            destSite = (GenericDO) fiber.getAttrValue(Fiber.AttrName.destSiteCuid);
                        }

                        String str1 = (origSite != null ? origSite.getAttrString(GenericDO.AttrName.labelCn) : "") + "--" +
                                      (destSite != null ? destSite.getAttrString(GenericDO.AttrName.labelCn) : "");
                        if(StringUtils.isNotEmpty(str1) && relseg.indexOf(str1) < 0)
                        	relseg = relseg +" "+ str1;

                        String strrelsys = (wsystem != null ? wsystem.getAttrString(GenericDO.AttrName.labelCn) : "未知光缆");
                        if(StringUtils.isNotEmpty(strrelsys) && relSys.indexOf(strrelsys) < 0)
                        	relSys = relSys +" "+ strrelsys;

                        String str = fiber.getWireNo() + "号纤芯";
                        if(StringUtils.isNotEmpty(str) && fiberInfo.indexOf(str) < 0)
                        	fiberInfo = fiberInfo +" "+ str;
                        
                        Long fiberCount = 0L;
                        Long lfiberUseCount = 0L;
                        if (wsystem != null && origSite != null && destSite != null) {
                        	String relatedDeviceCuid = DMHelper.getRelatedCuid(port.getAttrValue(FiberDpPort.AttrName.relatedDeviceCuid));
                            if ((origSite.getCuid().equals(relatedDeviceCuid) ||
                                 destSite.getCuid().equals(relatedDeviceCuid)) &&
                                !wsystem.getCuid().equals(OLdrelatedSystemCuid)) {
                                String sql = "select count(*) from FIBER where  RELATED_SYSTEM_CUID ='" + wsystem.getCuid() + "'";
                                fiberCount = (Long) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFiberCount, new BoActionContext(), sql);

                                String sqlfuc = "select count(*) from FIBER where (USAGE_STATE =2 or USAGE_STATE =3) and RELATED_SYSTEM_CUID ='" + wsystem.getCuid() + "'";
                                lfiberUseCount = (Long) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFiberCount, new BoActionContext(),sqlfuc);
                            }
                            OLdrelatedSystemCuid = wsystem.getCuid();
                            AwirefiberCount = AwirefiberCount + fiberCount;
                            AfiberUseCount = AfiberUseCount + lfiberUseCount;
                        }
                        GenericDO destPoint = new GenericDO();
                        if (fiber.getAttrValue(Fiber.AttrName.destPointCuid) != null && fiber.getAttrValue(Fiber.AttrName.destPointCuid) instanceof GenericDO) {
                            destPoint = (GenericDO) fiber.getAttrValue(Fiber.AttrName.destPointCuid);
                            if (DMHelper.getRelatedCuid(destPoint).equals(port.getCuid())) {
                                //对端端子对象
                                Object obj = fiber.getAttrValue(Fiber.AttrName.origPointCuid);
                                if ((obj != null) && (obj != null) && (!obj.toString().trim().equals(""))) {
                                    GenericDO destPort = (GenericDO) fiber.getAttrValue(Fiber.AttrName.origPointCuid);
                                    //对端设备对象
                                    GenericDO destDev = (GenericDO) fiber.getAttrValue(Fiber.AttrName.origEqpCuid);

                                    String str2 = (destPort != null ? (String) destPort.getAttrValue(GenericDO.AttrName.labelCn) : "");
                                    String str3 = (destDev != null ? (String) destDev.getAttrValue(GenericDO.AttrName.labelCn) : "");
                                    String str4 = (destDev != null ? (String) getDeviceTypeName(destDev) : "");
                                    //对端端子编码
                                    destPortLabelCn = destPortLabelCn + str2;
                                    //对端设备名称
                                    destDevLabelCn = destDevLabelCn + str3;
                                    //对端设备类型
                                    destDevTypeName = destDevTypeName + str4;
                                }
                            }
                        }
                        GenericDO origPoint = new GenericDO();
                        if (fiber.getAttrValue(Fiber.AttrName.origPointCuid) != null && fiber.getAttrValue(Fiber.AttrName.origPointCuid) instanceof GenericDO) {
                            origPoint = (GenericDO) fiber.getAttrValue(Fiber.AttrName.origPointCuid);
                            if (DMHelper.getRelatedCuid(origPoint).equals(port.getCuid())) {
                                //对端端子对象
                                Object obj = fiber.getAttrValue(Fiber.AttrName.destPointCuid);
                                if (((obj != null) && obj != null) && (!obj.toString().trim().equals(""))) {
                                    GenericDO destPort = (GenericDO) fiber.getAttrValue(Fiber.AttrName.destPointCuid);
                                    //对端设备对象
                                    GenericDO destDev = (GenericDO) fiber.getAttrValue(Fiber.AttrName.destEqpCuid);
                                    String str2 = (destPort != null ? (String) destPort.getAttrValue(GenericDO.AttrName.labelCn) : "");
                                    String str3 = (destDev != null ? (String) destDev.getAttrValue(GenericDO.AttrName.labelCn) : "");
                                    String str4 = (destDev != null ? (String) getDeviceTypeName(destDev) : "");
                                    //对端端子编码
                                    destPortLabelCn = destPortLabelCn + str2;
                                    //对端设备名称
                                    destDevLabelCn = destDevLabelCn + str3;
                                    //对端设备类型
                                    destDevTypeName = destDevTypeName + str4;
                                }
                            }
                        }
                        ustate = String.valueOf(fiber.getUsageState());
//                        portNode.putClientProperty(usageState, fiber.getUsageState());
                    }
                    fdpportMap.put(usageState, ustate);
                    fdpportMap.put(relatedwire, relSys);
                    fdpportMap.put(relatedwireseg, relseg);
                    fdpportMap.put(fiberno, fiberInfo);
                    fdpportMap.put(destdevpointno, destPortLabelCn);
                    fdpportMap.put(destdevno, destDevLabelCn);
                    fdpportMap.put(destdevtype, destDevTypeName);
                    fdpportMap.put(wireFiberCount, AwirefiberCount);
                    fdpportMap.put(fiberUseCount, AfiberUseCount);
                    fdpportMap.put("PORT_CUID", port.getCuid());
                }
            }
        } catch (Exception ex) {
        	logger.error("根据端子得到相关信息出错！", ex);
			throw new UserException("根据端子得到相关信息出错！");
        }
        return fdpportMap;
    }
    
    private String getDeviceTypeName(GenericDO dbo) {
        String devTypeName = "";
        if (dbo.getClassName().equalsIgnoreCase(Odf.CLASS_NAME)) {
            devTypeName = "ODF";
        } else if (dbo.getClassName().equalsIgnoreCase(FiberCab.CLASS_NAME)) {
            devTypeName = "光交接箱";
        } else if (dbo.getClassName().equalsIgnoreCase(FiberDp.CLASS_NAME)) {
            devTypeName = "光分纤箱";
        } else if (dbo.getClassName().equalsIgnoreCase(FiberJointBox.CLASS_NAME)) {
            devTypeName = "接头盒";
        }
        return devTypeName;
    }
}
