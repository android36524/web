package com.boco.irms.app.dm.gridbo;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class FiberInterPropTemplateBO extends FiberPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta) throws UserException {
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();		
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			if(StringUtils.isEmpty(dbo.getAttrValue("WIRE_NO"))){
				Object value  = keyValueMap.get(NO_BATCH);
				JSONObject parentNode = (JSONObject)keyValueMap.get("PARENTNODEID");
				if(parentNode == null){
					throw new UserException("为获取到所属系统CUID");
				}
				String parentNodeid = String.valueOf(parentNode.get("value"));
				keyValueMap.put("RELATED_SYSTEM_CUID", parentNodeid.split("@")[0]);
				String interFiberId = parentNodeid.split("@")[0];
				keyValueMap.remove("PARENTNODEID");
				InterWire segdata = new InterWire();
				try {
					segdata = (InterWire) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), interFiberId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int num = Integer.parseInt(WebDMUtils.convertJosn2Object(NO_BATCH,value).toString());
				int maxno = getMaxWireNo(interFiberId);

				for(int i=1;i<=num;i++){
					GenericDO newDbo=(GenericDO) dbo.deepClone();
					Long wireNo = new Long(maxno + i);
					newDbo.setAttrValue(Fiber.AttrName.labelCn, wireNo.toString());
					
					newDbo.setCuid();
					newDbo.setAttrValue(Fiber.AttrName.relatedSegCuid, segdata.getCuid());
					newDbo.setAttrValue(Fiber.AttrName.wireNo, wireNo);
					newDbo.setAttrValue(Fiber.AttrName.length, segdata.getLength()); // 长度
					newDbo.setAttrValue(Fiber.AttrName.origSiteCuid, segdata.getOrigPointCuid()); // A端设备
					newDbo.setAttrValue(Fiber.AttrName.destSiteCuid, segdata.getDestPointCuid()); // Z端设备
					long icolor = dbo.getAttrLong("FIBER_COLOR");
					if(icolor == 0)
						icolor = Color.WHITE.getRGB();
					newDbo.setAttrValue(Fiber.AttrName.fiberColor, icolor); // 加颜色
					list.add(newDbo);
				}
			}else{
				dbo.setCuid();
				list.add(dbo);
			}
			try {
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
}
