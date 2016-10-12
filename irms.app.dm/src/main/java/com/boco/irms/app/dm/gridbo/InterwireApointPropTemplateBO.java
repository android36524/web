package com.boco.irms.app.dm.gridbo;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class InterwireApointPropTemplateBO extends AbstractPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){	
			Map keyValueMap = result.get(0);
			JSONObject parentNode = (JSONObject)keyValueMap.get("PARENTNODEID");
			if(parentNode == null){
				throw new UserException("为获取到所属系统CUID");
			}
			if(parentNode != null && parentNode.get("value") != null)
				keyValueMap.put("RELATED_SITE_CUID", parentNode.get("value"));
			keyValueMap.remove("PARENTNODEID");
			GenericDO interWire = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			interWire.setCuid();
			interWire.setObjectId("");
			String siteName = AbstractPropTemplateBO.getLabelcnByCuid(interWire.getAttrString("RELATED_SITE_CUID"));
			String aPoint = AbstractPropTemplateBO.getLabelcnByCuid(interWire.getAttrString("ORIG_POINT_CUID"));
			String zPoint = AbstractPropTemplateBO.getLabelcnByCuid(interWire.getAttrString("DEST_POINT_CUID"));
			String newLabelCn = siteName + aPoint + "-" + zPoint + interWire.getAttrLong("NUM_WIRE") + "芯层间光缆";
			interWire.setAttrValue("LABEL_CN", newLabelCn);
			BoActionContext bocontext = new BoActionContext();
			bocontext.setUserName(editorMeta.getAc().getUserId());
			try {
				BoCmdFactory.getInstance().execBoCmd(method,bocontext,(InterWire) interWire);
			} catch (Exception e) {
				logger.error("光缆段添加失败",e);
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
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			dbo.removeAttr("LAST_MODIFY_TIME");
			try {
				InterWire interWire = (InterWire)dbo;
				
				String siteName = "";
				Class[] classType = new Class[] {String.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				try {
					DataObjectList devices = ductmanagerBo.getDatasBySql("SELECT LABEL_CN FROM accesspoint WHERE cuid in "
							+ "(select related_site_cuid from inter_wire where cuid ='" + interWire.getCuid() + "')", classType);
					for(GenericDO deviceGdo : devices)
					{
						siteName = deviceGdo.getAttrString("1");
					}
				} catch (Exception e) {
				}
				
				String aPoint = getLabelcnByTranscuid(interWire.getAttrString("ORIG_POINT_CUID"));
				String zPoint = getLabelcnByTranscuid(interWire.getAttrString("DEST_POINT_CUID"));
				String newLabelCn = siteName + aPoint + "-" + zPoint + interWire.getAttrLong("NUM_WIRE") + "芯层间光缆";
				interWire.setAttrValue("LABEL_CN", newLabelCn);
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				BoCmdFactory.getInstance().execBoCmd(method, bocontext, interWire);
			} catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	private String getLabelcnByTranscuid(String cuid)
	{
		if(cuid.startsWith("TRANS_ELEMENT"))
		{
			String sql = "SELECT LABEL_CN FROM AN_ONU WHERE CUID='" + cuid + "'"
					+ " UNION SELECT LABEL_CN FROM AN_POS WHERE CUID='" + cuid + "'"
					+ " UNION SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID='" + cuid + "'";
			//从传输服务查询数据
			DataObjectList  results = new DataObjectList();
			try {
				Class[] classType = new Class[] {String.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				results = ductmanagerBo.getDatasBySql(sql, classType);
			} catch (Exception e) {
				logger.error("从传输服务查询数据失败,方法名称=update",e);
			}
			String returnName = cuid;
			if(results != null && results.size() > 0)
			{
				returnName = results.get(0).getAttrString("1");
			}
			return returnName;
		}
		else
		{
			return AbstractPropTemplateBO.getLabelcnByCuid(cuid);
		}
	}
	
	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("delete");	
		JSONArray arr = JSON.parseArray(editorMeta.getParas());
		if(arr==null || arr.size() == 0){
			return null;
		}
		List<Map> result = arr.getObject(0, List.class);
		if(result != null && result.size() > 0){
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			try {
				BoActionContext bocontext = new BoActionContext();
				bocontext.setUserId(editorMeta.getAc().getUserCuid());
				bocontext.setUserName(editorMeta.getAc().getUserId());
				BoCmdFactory.getInstance().execBoCmd(method, bocontext,(InterWire)dbo);
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}



	
}
