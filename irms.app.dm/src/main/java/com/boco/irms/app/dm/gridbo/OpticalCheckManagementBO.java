package com.boco.irms.app.dm.gridbo;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class OpticalCheckManagementBO extends ProjectPropTemplateBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
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
		String cuid=(String) result.get(0).get("CUID");
		try {
			BoCmdFactory.getInstance().execBoCmd(method,cuid);			
		} catch (Exception e) {
			logger.error("删除失败",e);
			throw new UserException(e);
		}
		return editorMeta;
	}
	
}
