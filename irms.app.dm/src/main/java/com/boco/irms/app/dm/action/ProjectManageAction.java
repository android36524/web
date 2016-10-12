package com.boco.irms.app.dm.action;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.remoting.exchange.Request;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.dm.gridbo.ProjectGridTemplateProxyBO;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.ProjectManagement;
import com.boco.transnms.common.dto.ProjectProgress;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class ProjectManageAction {	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String RESULT="OK";
	
	private static final Map<String,GenericDO> map=new HashMap<String, GenericDO>();
	static{
		 map.put(WireSystem.CLASS_NAME, new WireSeg());
		 map.put(DuctSystem.CLASS_NAME, new DuctSeg());
		 map.put(PolewaySystem.CLASS_NAME, new PolewaySeg());
		 map.put(StonewaySystem.CLASS_NAME, new StonewaySeg());
		 map.put(UpLine.CLASS_NAME, new UpLineSeg());
		 map.put(HangWall.CLASS_NAME, new HangWallSeg());
	}
	
	public String deleteRelatedProject(List<Map> resList) throws UserException {
		if(resList==null || resList.size()==0){
			return null;
		}
		DataObjectList resources=new DataObjectList();
		for(Map map : resList){
			String cuid=map.get("CUID").toString();
			String className=GenericDO.parseClassNameFromCuid(cuid);
			
			GenericDO gdo=createInstanceByClassName(className, map);
			resources.add(gdo);
		}
		try {
			IDuctManagerBO ductManagerBO = (IDuctManagerBO)BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			ductManagerBO.deleteRelatedProject(new BoActionContext(),resources);			
		} catch (Exception e) {
			logger.error("删除资源的所属工程失败",e);
			throw new UserException("删除资源的所属工程失败");
		}		
		return RESULT;
	}
	
	public String updateRelatedProject(Map projMap,List<Map> resList) throws UserException {		
		  if(resList==null || resList.size()==0){
			  return null;
		  }
		  String projectCuid=projMap.get("CUID").toString();
		  ProjectManagement projectManagement=getProjectManagementByCuid(projectCuid);		  
		  if(projectManagement==null){
			  return null;
		  }			  
		  DataObjectList resources=new DataObjectList();		  
		  String sysCuid=resList.get(0).get("CUID").toString();		  
		  if(isSystem(sysCuid)){
			  List<String> sysCuids=new ArrayList<String>();
			  for(Map map:resList){
				  sysCuids.add(map.get("CUID").toString());
			  }			  
			  resources.addAll(getSegsBySystemCuids(sysCuids));			  
			  for(GenericDO seg: resources){
				  seg.setAttrValue(WireSeg.AttrName.relatedProjectCuid, projectCuid);
			  }
		  }
		  else {
			  for(Map map:resList){
				  String cuid=map.get("CUID").toString();	 
				  String className=GenericDO.parseClassNameFromCuid(cuid);
				  GenericDO gdo=createInstanceByClassName(className, map);
				  gdo.setAttrValue(Manhle.AttrName.relatedProjectCuid, projectCuid);
				  gdo.removeAttr("LAST_MODIFY_TIME");
				  resources.add(gdo);
			  }
		  }			  
		  try {			  
				IDuctManagerBO ductManagerBO = (IDuctManagerBO)BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				ductManagerBO.updateRelatedMag(new BoActionContext(), projectManagement, resources, DMHelper.Method.ADD);
		  } catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException("修改失败");
		  }
		  return RESULT;
	}
    
	private DataObjectList getSegsBySystemCuids(List<String> sysCuids) throws UserException{
		 DataObjectList segs=new DataObjectList();
		 String sysCuid=sysCuids.get(0);
		 String className=GenericDO.parseClassNameFromCuid(sysCuid);
		 int size=sysCuids.size();
		 		 
		 StringBuffer sql=new StringBuffer();
			if(size==1){
				sql.append(" RELATED_SYSTEM_CUID='"+sysCuids.get(0)+"'");
				DataObjectList tempList =new DataObjectList();				
				tempList=getSegsBySql(sql.toString(), map.get(className));
				segs.addAll(tempList);
				sql.setLength(0);
			}
			else if(size>1){
				 for(int i=1;i<=size;i++){
					 if(i%50!=0){
						 sql.append(" RELATED_SYSTEM_CUID='"+sysCuids.get(i-1)+"' OR ");
					 }
					 if(i%50==0){
						 sql.append(" RELATED_SYSTEM_CUID='"+sysCuids.get(i-1)+"' OR ");
						 DataObjectList tempList =new DataObjectList();
						 tempList=getSegsBySql(sql.substring(0, sql.length()-4), map.get(className));
						 
						 segs.addAll(tempList);
						 sql.setLength(0);
					 }				 
				 }
				 if(size%50!=0){
					 DataObjectList tempList =new DataObjectList();
					 tempList=getSegsBySql(sql.substring(0, sql.length()-4), map.get(className));					
					 segs.addAll(tempList);
					 sql.setLength(0);
				 }			 
			}			
			return segs;		 
	}	
	
	private DataObjectList getSegsBySql(String sql,GenericDO seg) throws UserException{
		DataObjectList segs=new DataObjectList();
		try {
			IDuctManagerBO ductManagerBO = (IDuctManagerBO)BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			segs=ductManagerBO.getObjectsBySql(sql,seg);
		} catch (Exception e) {
			logger.error("通过sql查询段失败",e);
			throw new UserException("通过sql查询段失败");
		}
		return segs;
	}	
	private boolean isSystem(String cuid){
		return cuid.startsWith(WireSystem.CLASS_NAME) || cuid.startsWith(DuctSystem.CLASS_NAME) || cuid.startsWith(StonewaySystem.CLASS_NAME)
			 || cuid.startsWith(PolewaySystem.CLASS_NAME) || cuid.startsWith(UpLine.CLASS_NAME) || cuid.startsWith(HangWall.CLASS_NAME);
	}
	private ProjectManagement getProjectManagementByCuid(String projectCuid){		
		ProjectManagement projectManagement=null;
		try {
			projectManagement=(ProjectManagement)BoCmdFactory.getInstance().execBoCmd("IProjectManagementBO.getProjectManagementByCuid", new BoActionContext(),projectCuid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过cuid查询工程失败",e);
			throw new UserException("通过cuid查询工程失败");
		}
		return projectManagement;		
	}	
	public GenericDO createInstanceByClassName(String className,Map keyValueMap){
		  GenericDO dbo=new GenericDO();
		  dbo.setClassName(className);
		  dbo=dbo.createInstanceByClassName();
		  
		  Set keySet=keyValueMap.keySet();
		  Iterator iterator=keySet.iterator();
		  
		  Class fieldType=null;
		  while(iterator.hasNext()){
			  String columnName=iterator.next().toString();
			  fieldType = dbo.getAttrType(columnName);
			  Object value=keyValueMap.get(columnName);
			  
			  if(containAttr(dbo, columnName) && fieldType!=null && !"".equals(String.valueOf(value))){
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
						if( value == null){
							   value = new Date();
						   }else if(value instanceof String){
							SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								date = formatDate.parse(String.valueOf(value));
							} catch (ParseException e) {
								logger.error("日期转换错误",e);
							}
						}else {
							date = new Date(Long.parseLong("" + value));
						}
						dbo.setAttrValue(columnName, date);
				}
			  }
		  }		  
		  
		  dbo.setObjectNum(dbo.getAttrLong("OBJECTID"));
		  dbo.clearUnknowAttrs();			
	      return dbo;
	}	
	private  boolean containAttr(GenericDO dbo, String columnName){
		for(String key: dbo.getAllAttrNames()){
			 if(key.equals(columnName)){
				 return true;
			 }
		}
		return false;		
	}
	
	/**
	 *  工程管理状态流转  
	 * @param prObjArr
	 * @param ValueJson
	 * @throws UserException
	 */
	public void updateProjManaState(Map prObjArr,Map ValueJson) throws UserException{
		String method1 = "IProjectManagementBO.modifyBuildToWaitingMaint";
		String method2 = "IProjectManagementBO.modifyWaitingMaintToMaint";

		String projectCuid=prObjArr.get("CUID").toString();
		String projClassName=GenericDO.parseClassNameFromCuid(projectCuid);
		GenericDO management=createInstanceByClassName(projClassName,prObjArr);
		  
		String descValue = "";
		if(ValueJson.equals(null) || ValueJson.size()!=0 || ValueJson==null){
			descValue = ValueJson.get("DESCRIPTION").toString();
		}else{
			descValue="";
		}
		
		String stateValue = prObjArr.get("STATE").toString();
		if(stateValue.equals("1")){ //设计->工程
			try {
				BoCmdFactory.getInstance().execBoCmd(method1, new BoActionContext(),management,descValue);
			} catch (Exception e) {
				logger.error("修改工程状态失败",e);
				e.printStackTrace();
			}
		}else if(stateValue.equals("2")){ //工程->在网
			try {
	    		BoCmdFactory.getInstance().execBoCmd(method2, new BoActionContext(),management,descValue);
			} catch (Exception e) {
				logger.error("修改工程状态失败",e);
				throw new UserException(e);
			}
		}
		
	}
	
	/**
	 * 工程管理状态流转  工程->设计
	 * @param prObjArr
	 * @param ValueJson
	 * @throws UserException
	 */
	public void updateProjManaStateDesign(Map prObjArr,Map ValueJson) throws UserException{
		String method = "IProjectManagementBO.modifyWaitingMaintToBuild";
		String projectCuid=prObjArr.get("CUID").toString();
		String projClassName=GenericDO.parseClassNameFromCuid(projectCuid);
		GenericDO management=createInstanceByClassName(projClassName,prObjArr);
		  
		String descValue = "";
		if(ValueJson.equals(null) || ValueJson.size()!=0 || ValueJson==null){
			descValue = ValueJson.get("DESCRIPTION").toString();
		}else{
			descValue="";
		}
		
		try {
    		BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),management,descValue);
		} catch (Exception e) {
			logger.error("修改工程状态失败",e);
			throw new UserException(e);
		}
	}
	
	/**
	 * 工程/项目状态流转
	 * @param prObjArr
	 * @param ValueJson
	 * @throws UserException
	 */
	public void updateSubProjectState(Map projectArr,Map ValueJson) throws UserException{
		String method1 = "IProjectManagementBO.approvalSubProject";
		String method2 = "IProjectManagementBO.modifySubProjectState";
		
		String projectCuid = projectArr.get("CUID").toString();
		String projClassName = GenericDO.parseClassNameFromCuid(projectCuid);
		GenericDO management = createInstanceByClassName(projClassName,projectArr);
		String description = ValueJson.get("DESCRIPTION") == null ? "" : ValueJson.get("DESCRIPTION").toString();
		Boolean isApproved = false;
		if("true".equals(ValueJson.get("ISAPPROVED"))){
			isApproved = true;
		}
		WebContext webContext = WebContextFactory.get();
		ServiceActionContext ac = new ServiceActionContext(webContext.getHttpServletRequest());
		BoActionContext context = new BoActionContext();
		context.setUserId(ac.getUserCuid());
		context.setUserName(ac.getUserId());
		
		String stateValue = projectArr.get("STATE").toString();
		if(stateValue.equals("5")){ // 审批工程/项目
			try {
				BoCmdFactory.getInstance().execBoCmd(method1, context,management,description,isApproved);
			} catch (Exception e) {
				logger.error("修改工程/项目状态失败",e);
				throw new UserException(e);
			}
		}else{ //提交审批
			try {
	    		BoCmdFactory.getInstance().execBoCmd(method2, context,management,description);
			} catch (Exception e) {
				logger.error("修改工程/项目状态失败",e);
				throw new UserException(e);
			}
		}
	}
	
	/**
	 * 查询工程/项目进展信息
	 * @param projectCuid 工程/项目标识
	 * @return
	 */
	public List<Map<String,Object>> getProjectProgressList(String projectCuid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			String method = "IProjectManagementBO.getProgressesByProjectCuid";
//			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IProjectManagementBO.getProjectManagementByCuid",new BoActionContext(), projectCuid);
//			if(gdo != null){
//				projectName = gdo.getAttrString("LABEL_CN");
//			}
			DataObjectList results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),projectCuid);
			
			if (results != null && results.size() > 0) {
				for(GenericDO gdo : results){
					Map<String, Object> map = new HashMap<String, Object>();
					GenericDO projectManagement = (GenericDO) gdo.getAttrValue(ProjectProgress.AttrName.relatedProjectCuid);
					Long state = Long.parseLong(projectManagement.getAttrValue("STATE").toString());
					String stateValue = getStateValue(state);
					map.put("CUID",projectManagement.getAttrValue("CUID"));
					map.put("LABEL_CN", projectManagement.getAttrValue("LABEL_CN"));
					map.put("NO", projectManagement.getAttrValue("NO"));
					map.put("OPERATOR", gdo.getAttrValue("OPERATOR"));
					map.put("OPERATION_NAME", stateValue);
					map.put("DESCRIPTION", projectManagement.getAttrValue("REMARK"));
					Timestamp ts = (Timestamp)gdo.getAttrValue("OPERATION_DATE");
					map.put("OPERATION_DATE",ts.toString());
					list.add(map);
				}
			}
		} catch (Exception e) {
			logger.error("查询工程/项目进展信息报错",e);
		}
		return list;
	}
	
	public String getStateValue(Long state){
		String value = "";
		if(state == 1){
			value = "设计";
		}else if(state == 2){
			value = "施工/在建";
		}else if(state == 3){
			value = "竣工";
		}else if(state == 5){
			value = "维护";
		}else if(state == 10){
			value = "立项";
		}
		return value;
	}
	
}
