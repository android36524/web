package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.common.Template;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.common.ITemplateBO;
import com.boco.transnms.server.bo.ibo.common.ITemplateBOX;
import com.boco.transnms.server.bo.ibo.common.ITemplateGroupBO;

public class TemplateViewAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("rawtypes")
	public List getAllTemplateGroupsAndTemplates() throws IOException {
		
		ArrayList list = new ArrayList();
		
		ITemplateGroupBO templateGroupBo = BoHomeFactory.getInstance().getBO(ITemplateGroupBO.class);
		DataObjectList templateGroups = templateGroupBo.getAllTemplateGroups(new BoActionContext());
		
//		ITemplateBO templateBo = BoHomeFactory.getInstance().getBO(ITemplateBO.class);
		ITemplateBOX templateBo = BoHomeFactory.getInstance().getBO(ITemplateBOX.class);
		DataObjectList templates = templateBo.getTemplatesBySql(new BoActionContext(), " 1=1 ");
		
		list.add(templateGroups);
		list.add(templates);
		
		return list;
	}

	public String getTemplatePic(String cuid){
		String json = "";
//		ITemplateBO templateBo = BoHomeFactory.getInstance().getBO(ITemplateBO.class);
		ITemplateBOX templateBo = BoHomeFactory.getInstance().getBO(ITemplateBOX.class);
		Template template = new Template();
		template.setCuid(cuid);
		DboBlob b = templateBo.getTemplatePic(new BoActionContext(), template);
		return json;
	}
}
