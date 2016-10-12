package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.PdaDevice;
import com.boco.transnms.common.dto.PdaGroup;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class PdaGroupBO extends AbstractPropTemplateBO{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IbatisDAO ibatisDAO;

	public void setIbatisDAO(IbatisDAO ibatisDAO) {
		this.ibatisDAO = ibatisDAO;
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getPdaGroupData(String name, String districtCuid) {
		String sql = "SELECT P.*,D.LABEL_CN AS SPACENAME FROM PDA_GROUP P,DISTRICT D WHERE D.CUID=P.RELATED_DISTRICT_CUID";
		if (!name.equals("") && name != null) {
			sql = sql + " AND P.LABEL_CN LIKE '%" + name + "%'";
		}
		if (!districtCuid.equals("") && districtCuid != null) {
			sql = sql + " AND SUBSTR(P.RELATED_DISTRICT_CUID,1,26)='"
					+ districtCuid + "'";
		}
		List<Map> list = ibatisDAO.querySql(sql);
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getPdaMemberData(String cuid) throws Exception {
		String sql = "SELECT P.*,P2.LABEL_CN FROM PDA_DEVICE P,PDA_GROUP P2 WHERE P.GROUP_CUID=P2.CUID AND P.GROUP_CUID='"
				+ cuid + "'";
		List<Map> list = ibatisDAO.querySql(sql);
//		String sql = "GROUP_CUID ='"+cuid+"'";
//		DboCollection result =  (DboCollection) BoCmdFactory.getInstance().execBoCmd("IPdaDeviceBO.queryPda", new BoQueryContext(),sql);
//		DataObjectList list = new DataObjectList();
//		for(int i=0;i<result.size();i++){
//			list.add(result.getAttrField(PdaDevice.CLASS_NAME,i));
//		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getPdaMemberDataByName(String name) {
		String sql = "SELECT P.*,D.LABEL_CN AS DISTRICTNAME,P2.LABEL_CN AS GROUPNAME FROM PDA_DEVICE P,PDA_GROUP P2,DISTRICT D WHERE P2.CUID(+)=P.GROUP_CUID AND SUBSTR(P.RELATED_DISTRICT_CUID,1,26)=D.CUID(+)";
		if (name != null && name.length() > 0) {
			sql += " AND P.USER_NAME LIKE '%" + name + "%'";
		}
		List<Map> list = ibatisDAO.querySql(sql);
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String insertPdaGroup(String name, String districtName){
		if (name != null && name.length() > 0 && districtName != null
				&& districtName.length() > 0) {
			String sql = "SELECT CUID FROM DISTRICT D WHERE D.LABEL_CN='"
					+ districtName + "'";
			List<Map> list = ibatisDAO.querySql(sql);
			String districtCuid = (String) list.get(0).get("CUID");
			sql = "SELECT 1 FROM PDA_GROUP P WHERE P.LABEL_CN = '"+name+"'";
			list = ibatisDAO.querySql(sql);
			if(list.size()>0){
				return "名称重复，请重新填写";
			}
			Map map = new HashMap();
			String cuid = UUID.randomUUID().toString();
			map.put("CUID", "PDA_GROUP-"+cuid);
			map.put("LABEL_CN", name);
			map.put("RELATED_DISTRICT_CUID", districtCuid);
			try {
				ibatisDAO.insertDynamicTable("PDA_GROUP", map);
				return "添加成功";
			} catch (Exception e) {
				throw new UserException(e);
			}
		} else {
			return "必填字段请填写数据";
		}
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean updatePdaGroup(String cuid, String name, String districtName) {
		if (name != null && name.length() > 0 && districtName != null
				&& districtName.length() > 0) {
			String sql = "SELECT CUID FROM DISTRICT D WHERE D.LABEL_CN='"
					+ districtName + "'";
			List<Map> list = ibatisDAO.querySql(sql);
			String districtCuid = (String) list.get(0).get("CUID");
			String updateSql = "UPDATE PDA_GROUP P SET P.LABEL_CN='" + name
					+ "',p.RELATED_DISTRICT_CUID='" + districtCuid
					+ "' where p.cuid='" + cuid + "'";
			ibatisDAO.updateSql(updateSql);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean deletePdaGroup(String cuid) {
		if(cuid!=null&&cuid.length()>0){
			String sql = "SELECT CUID FROM PDA_DEVICE P WHERE P.GROUP_CUID='"
					+ cuid + "'";
			List<Map> list = ibatisDAO.querySql(sql);
			if (list.size() > 0) {
				return false;
			} else {
				String deleteSql = "delete from pda_group p where p.cuid='" + cuid
						+ "'";
				ibatisDAO.deleteSql(deleteSql);
				return true;
			}
		}else{
			return false;
		}
	}

	public boolean deleteFromGroup(String cuid, String groupCuid) {
		if (cuid != null && cuid.length() > 0) {
			String updateSql = "UPDATE PDA_DEVICE P SET P.GROUP_CUID='" + groupCuid
					+ "' WHERE P.CUID ='" + cuid+ "'";
			ibatisDAO.updateSql(updateSql);
			return true;
		} else {
			return false;
		}
	}

	public boolean insertIntoGroup(String cuid, String groupCuid) throws Exception {
		if (cuid != null && cuid.length() > 0 && groupCuid != null
				&& groupCuid.length() > 0) {
//			String updateSql = "UPDATE PDA_DEVICE P SET P.GROUP_CUID='" + groupCuid
//					+ "' WHERE P.CUID ='" + cuid+ "'";
//			ibatisDAO.updateSql(updateSql);
			GenericDO dbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			PdaDevice pda = (PdaDevice)dbo;
			pda.setGroupCuid(groupCuid);
			DataObjectList list = new DataObjectList();	
			list.add(pda);
			BoCmdFactory.getInstance().execBoCmd("IPdaDeviceBO.modifyPdaDevices",new BoActionContext(),list);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();		
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			dbo.setCuid();
			list.add(dbo);
			try {
				PdaGroup pdag = (PdaGroup) dbo;
				String sql = "LABEL_CN ='"+pdag.getLabelCn()+"' AND RELATED_DISTRICT_CUID ='"+pdag.getRelatedDistrictCuid()+"'";
				DataObjectList pdags = getDuctManagerBO().getObjectsBySql(sql, new PdaGroup());
				if(pdags.size()>0){
					throw new UserException("名称重复");
				}
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				BoCmdFactory.getInstance().execBoCmd(method, bocontext,list);
			} catch (Exception e) { 
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.removeAttr("LAST_MODIFY_TIME");
			list.add(dbo);
			BoActionContext bocontext = new BoActionContext();
			bocontext.setUserId(editorMeta.getAc().getUserCuid());
			bocontext.setUserName(editorMeta.getAc().getUserName());
			try {
				PdaGroup pdag = (PdaGroup) dbo;
				String sql = "LABEL_CN ='"+pdag.getLabelCn()+"' AND RELATED_DISTRICT_CUID ='"+pdag.getRelatedDistrictCuid()+"'";
				DataObjectList pdags = getDuctManagerBO().getObjectsBySql(sql, new PdaGroup());
				if(pdags.size()>0){
					throw new UserException("名称重复");
				}
				BoCmdFactory.getInstance().execBoCmd(method,bocontext,list);
			} catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
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
				dbo.setClassName(className);
				list.add(dbo);
			}
			try {
				BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),list);
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
}
