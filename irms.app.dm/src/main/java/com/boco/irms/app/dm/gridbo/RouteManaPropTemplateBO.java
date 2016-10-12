package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
/**
 * 路由管理功能BO
 * @author wangqin
 *
 */
public class RouteManaPropTemplateBO extends AbstractPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		String method = editorMeta.getRemoteMethod("query");
		String className=editorMeta.getClassName();
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
	
		List<Map> results = JSON.parseArray(editorMeta.getParas(),Map.class);
		String cuid = (String) results.get(0).get("CUID");
		if(cuid != null){
			try {
				String sql = "CUID='"+cuid+"'";
				DataObjectList col = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),sql);
				List<Map> list = new ArrayList<Map>();
				
				if(col != null && col.size() > 0){
					//list.add(col.getAttrField(className, 0).getObjectToMap());
					GenericDO  gdo = col.get(0);				
					gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
					gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
					gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
					
					Map map = new HashMap();
					for(String columnName : gdo.getAllAttrNames()){
						Object value = gdo.getAttrValue(columnName);
						map.put(columnName, convertObject(columnName,value));
					}
					list.add(map);
				}		
				PageResult result = new PageResult(list, 1, 1, 1);
				editorMeta.setResult(JSON.toJSONString(result.getElements()));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return editorMeta;
	}
	
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::insert:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.setCuid();
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				BoCmdFactory.getInstance().execBoCmd(method,actionContext,dbo);
			} catch (Exception e) {
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta) throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::update:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.removeAttr("LAST_MODIFY_TIME");
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				if(dbo instanceof DuctBranch || dbo instanceof StonewayBranch || dbo instanceof PolewayBranch){
					 BoCmdFactory.getInstance().execBoCmd(method, bocontext,dbo,"");
				}
				else{
					GenericDO oldSeg = bo.getObjByCuid(actionContext, dbo.getCuid());
					Map<String, Object> oldAttrsMap = getAttrsMap(oldSeg);
	    			//此处方法暂时不变，还是先修改
					BoCmdFactory.getInstance().execBoCmd(method, actionContext,dbo);
					//广西数据质量管控需求
					updatePointsBySeg(actionContext,dbo,oldAttrsMap);
				}				
			} catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	/**
	 * 封装数据，用于判断是否要调用后台数据
	 * @param dbo 需要修改的数据
	 * @return
	 */
	private Map<String,Object> getAttrsMap(GenericDO dbo){
		Map<String,Object> attrMap = new HashMap<String,Object>();
		//产权
		Object ownerShip = dbo.getAttrValue(DuctSeg.AttrName.ownership);
		attrMap.put(DuctSeg.AttrName.ownership, ownerShip);
		//用途
		Object purpose = dbo.getAttrValue(DuctSeg.AttrName.purpose);
		attrMap.put(DuctSeg.AttrName.purpose, purpose);
		//维护方式
		Object maintMode = dbo.getAttrValue(DuctSeg.AttrName.maintMode);
		attrMap.put(DuctSeg.AttrName.maintMode, maintMode);
		//所有权人
		Object resOwner = dbo.getAttrValue(DuctSeg.AttrName.resOwner);
		attrMap.put(DuctSeg.AttrName.resOwner, resOwner);
		//使用单位
		Object userName = dbo.getAttrValue(DuctSeg.AttrName.userName);
		attrMap.put(DuctSeg.AttrName.userName, userName);
		//维护单位
		Object maintDep = dbo.getAttrValue(DuctSeg.AttrName.maintDep);
		attrMap.put(DuctSeg.AttrName.maintDep, maintDep);
		//巡检人
		Object servicer = dbo.getAttrValue(DuctSeg.AttrName.servicer);
		attrMap.put(DuctSeg.AttrName.servicer, servicer);
		//联系电话
		Object phoneNo = dbo.getAttrValue(DuctSeg.AttrName.phoneNo);
		attrMap.put(DuctSeg.AttrName.phoneNo, phoneNo);
		//检修时间
		Object checkDate = dbo.getAttrValue(DuctSeg.AttrName.checkDate);
		attrMap.put(DuctSeg.AttrName.checkDate, checkDate);
		//施工单位
		Object builder = dbo.getAttrValue(DuctSeg.AttrName.builder);
		attrMap.put(DuctSeg.AttrName.builder, builder);
		//施工时间
		Object buildDate = dbo.getAttrValue(DuctSeg.AttrName.buildDate);
		attrMap.put(DuctSeg.AttrName.buildDate, buildDate);
		//竣工时间
		Object finishDate = dbo.getAttrValue(DuctSeg.AttrName.finishDate);
		attrMap.put(DuctSeg.AttrName.finishDate, finishDate);
		return attrMap;
	}
	//在修改段时，相关的点等需要跟着修改
	/**
	 * DUCT_SEG:产权\用途\维护方式\所有权人\使用单位\维护单位\巡检人\联系电话\检修时间\施工单位\施工时间\竣工时间---需要修改两头起止点人手井、管孔、子孔，属性相同
	 * POLE_WAY_SEG:产权\用途\维护方式\所有权人\使用单位\维护单位\巡检人\联系电话\检修时间\施工单位\施工时间\竣工时间----需要修改杆路段的起止点的相同属性
	 * UP_LINE_SEG:同上
	 * HANG_WALL_SEG:同上
	 * 管道段、杆路段要判断的属性一致，故封装一下
	 * @throws Exception 
	 * 
	 */
	private void updatePointsBySeg(BoActionContext actionContext,GenericDO segDbo,Map<String, Object> oldAttrsMap) throws Exception{
		//segDbo为要修改的值，需要先查库，从库里得到原来判断，如果上面属性跟segDbo有不同，再调用修改方法，反之不调用
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		Map<String,Object> modifyAttrsMap = getAttrsMap(segDbo);
		boolean flag = WebDMUtils.getIsSameAttrs(modifyAttrsMap, oldAttrsMap);

		if(flag){
			//修改段两端的点
			String origPointCuid = DMHelper.getRelatedCuid(segDbo.getAttrValue(DuctSeg.AttrName.origPointCuid));
			String destPointCuid = DMHelper.getRelatedCuid(segDbo.getAttrValue(DuctSeg.AttrName.destPointCuid));
			List<String> lst = new ArrayList<String>();
			if(null != origPointCuid){
				lst.add(origPointCuid);
			}
			
			if(null != destPointCuid){
				lst.add(destPointCuid);
			}
			
			DataObjectList pointList = bo.getGenericDOListByCuids(lst);
			if(null != pointList && pointList.size()>0){
				//站点和接入点不修改
				for (int i = pointList.size() - 1; i >= 0; i--) {
					GenericDO point = pointList.get(i);
					String pCname = point.getClassName();
					if (pCname.equals(Site.CLASS_NAME) || pCname.equals(Accesspoint.CLASS_NAME)) {
						pointList.remove(i);
					}
				}
				Map<String, DataObjectList> dboMap = getSameTypeListMapByClassName(pointList);
				if(dboMap.size()>0){
					for(Map.Entry<String, DataObjectList> entry : dboMap.entrySet()){
						//String key = entry.getKey();
						//承载段起止点有无接入点和站点，如果有这两个，后面需要过滤？？？
						DataObjectList value = entry.getValue();
						getModifyDbo(modifyAttrsMap,value);
						bo.updateDatasByCuidOrObjectId(actionContext, value, true, false);
					}
				}
			}
		}
	}
	
	/**
	 * 按类型分数据类型
	 * @param objList
	 * @return
	 */
	private Map<String, DataObjectList> getSameTypeListMapByClassName(DataObjectList objList) {
		Map<String, DataObjectList> mapCuids = new HashMap<String, DataObjectList>();
		for (GenericDO dbo : objList) {
			String className = dbo.getClassName();
			DataObjectList list = mapCuids.get(className);
			if (list == null) {
				list = new DataObjectList();
				mapCuids.put(className, list);
			}
			list.add(dbo);
		}
		return mapCuids;
	}
	/**
	 * 修改数据封装
	 * @param map
	 * @param list
	 */
	private void getModifyDbo(Map<String,Object> map,DataObjectList list){
		for(GenericDO dbo : list){
			for(Map.Entry entry : map.entrySet()){
				String key = String.valueOf(entry.getKey());
				Object value = entry.getValue();
				if(null != value){
					dbo.setAttrValue(key, value);
				}
			}
		}
	}
	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::delete:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("delete");	
		JSONArray  arr = JSON.parseArray(editorMeta.getParas());
		if(arr==null || arr.size() == 0){
			return null;
		}
		List<Map> result = arr.getObject(0, List.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();
			for(Map keyValueMap : result){
				GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
				list.add(dbo);
			}
			try {
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);			
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
		
}
