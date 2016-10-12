package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.boco.component.tree.bo.ITreePanelBO;
import com.boco.component.tree.pojo.ExtTreeNode;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;

public class FiberTreeGridBO implements ITreePanelBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public List<ExtTreeNode> loadData(ExtTreeNode node) {
		List<ExtTreeNode> results = new ArrayList<ExtTreeNode>();
        try {
    		String systemId =MapUtils.getString(node.getParams(), "cuid");
            DataObjectList segs = new DataObjectList();
            if(systemId.startsWith(WireSystem.CLASS_NAME)){
				segs = WebDMUtils.getSegInfoList(systemId);
			}
            for (int m = 0; m < segs.size(); m++) {
                GenericDO seg = (GenericDO) segs.get(m);
                WebDMUtils.setProjectState(seg);
    			ExtTreeNode segNode = new ExtTreeNode();
    			segNode.setIcon("/resources/map/WIRE_SEG.png");
    			segNode.setCuid(seg.getCuid());
    			segNode.setText(seg.getAttrString("LABEL_CN"));
    			segNode.setLeaf(false);
                if(systemId.startsWith(WireSystem.CLASS_NAME)){
                	seg.setAttrValue(WireSeg.AttrName.relatedSystemCuid,systemId);
                }
    			segNode.setData(wiresegToMap(seg));
    			List<ExtTreeNode> fiberNodes = new ArrayList<ExtTreeNode>();

                DataObjectList fiberlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFibersByWireSeg, new BoActionContext(), seg);
                if(fiberlist != null && fiberlist.size() > 0) {
                	for (int i = 0; i < fiberlist.size(); i++) {
                        GenericDO fiber = (GenericDO) fiberlist.get(i);
            			ExtTreeNode fiberNode = new ExtTreeNode();
            			fiberNode.setCuid(fiber.getCuid());
            			fiberNode.setText(fiber.getAttrLong("WIRE_NO")+"");
            			fiberNode.setIcon("/resources/topo/dm/isfixed.gif");
            			fiberNode.setData(fiberToMap(fiber));
            			fiberNode.setLeaf(true);
            			fiberNodes.add(fiberNode);
                    }
                	segNode.setChildren(fiberNodes);
                }else {
                	seg.setAttrValue(WireSeg.AttrName.fiberCount, 0L);
                	segNode.setLeaf(true);
                }

    			results.add(segNode);
            }
        }catch (Throwable ex) {
        	logger.error("查询光缆段及纤芯信息报错",ex);
        }
        return results;
		
	}
	
	
	private Map fiberToMap(GenericDO fiber){
		Map fibermap = new HashMap();
		fibermap.putAll(fiber.getObjectToMap());
		fibermap.put("SIGNAL_DIRECTION", WebDMUtils.convertValue2Enum("DMSIGNALDIRECTION", fiber.getAttrValue("SIGNAL_DIRECTION")));
		fibermap.put("FIBER_LEVEL", WebDMUtils.convertValue2Enum("DMFIBERLEVEL", fiber.getAttrValue("FIBER_LEVEL")));
		fibermap.put("OWNERSHIP", WebDMUtils.convertValue2Enum("DMOwnerShip", fiber.getAttrValue("OWNERSHIP")));
		fibermap.put("MAINT_MODE", WebDMUtils.convertValue2Enum("DMMaintMode", fiber.getAttrValue("MAINT_MODE")));
		fibermap.put("PURPOSE", WebDMUtils.convertValue2Enum("DMPurpose", fiber.getAttrValue("PURPOSE")));
		fibermap.put("USAGE_STATE", WebDMUtils.convertValue2Enum("DMSUSAGESTATE", fiber.getAttrValue("USAGE_STATE")));
		fibermap.put("ORIG_POINT_CUID", WebDMUtils.getRelatedLabelcn(fiber.getAttrValue("ORIG_POINT_CUID")));
		fibermap.put("DEST_POINT_CUID", WebDMUtils.getRelatedLabelcn(fiber.getAttrValue("DEST_POINT_CUID")));
		fibermap.put("BMCLASSTYPE", "FIBER");
		return fibermap;
	}
	
	private Map wiresegToMap(GenericDO wireseg){
		Map wiresegmap = new HashMap();
		wiresegmap.putAll(wireseg.getObjectToMap());
		wiresegmap.put("OWNERSHIP", WebDMUtils.convertValue2Enum("DMOwnerShip", wireseg.getAttrValue("OWNERSHIP")));
		wiresegmap.put("MAINT_MODE", WebDMUtils.convertValue2Enum("DMMaintMode", wireseg.getAttrValue("MAINT_MODE")));
		wiresegmap.put("PURPOSE", WebDMUtils.convertValue2Enum("DMPurpose", wireseg.getAttrValue("PURPOSE")));
		wiresegmap.put("ORIG_POINT_CUID", WebDMUtils.getRelatedLabelcn(wireseg.getAttrValue("ORIG_POINT_CUID")));
		wiresegmap.put("DEST_POINT_CUID", WebDMUtils.getRelatedLabelcn(wireseg.getAttrValue("DEST_POINT_CUID")));
		wiresegmap.put("DEST_POINT_CUID", WebDMUtils.getRelatedLabelcn(wireseg.getAttrValue("DEST_POINT_CUID")));
		wiresegmap.put("BMCLASSTYPE", "SEG");
		return wiresegmap;
	}
}
