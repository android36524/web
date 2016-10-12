package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.tree.bo.ITreePanelBO;
import com.boco.component.tree.pojo.ExtTreeNode;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.server.bo.helper.dm.FiberCabBOHelper;

public class PortTreeGridBO implements ITreePanelBO{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	@Override
	public List<ExtTreeNode> loadData(ExtTreeNode node){ 
       
		List<ExtTreeNode> result=new ArrayList<ExtTreeNode>();

		FiberCab fibercab = getFiberCabByCuid(node);

		ExtTreeNode rootNode = getRootNode(fibercab);

		Map<Fibercabmodule, DataObjectList> modulesAndPorts = getModuleAndPortByFiberCab(fibercab);
		if (modulesAndPorts != null) {
			rootNode.setChildren(getModuleList(modulesAndPorts));
			result.add(rootNode);
		}
        return result;
	}

	private List<ExtTreeNode> getModuleList(
			Map<Fibercabmodule, DataObjectList> modulesAndPorts) {
		Map<String, Fibercabmodule> treeDataMap = sortModule(modulesAndPorts);           

		Iterator<String> it = treeDataMap.keySet().iterator();
		List<ExtTreeNode> moduleChildren = new ArrayList<ExtTreeNode> ();
		while(it.hasNext()){
			String FibercabmoduleName = it.next();
			Fibercabmodule module = treeDataMap.get(FibercabmoduleName);

			ExtTreeNode moduleNode = newModuleNode(FibercabmoduleName,
					module);
			moduleChildren.add(moduleNode);

			DataObjectList list = modulesAndPorts.get(module);
			moduleNode.setChildren(getPortList(module.getCuid(), list));
		}
		return moduleChildren;
	}

	private Map<String, Fibercabmodule> sortModule(Map<Fibercabmodule, DataObjectList> modulesAndPorts) {
		// HashMap没有排序,先让到TreeMap中,顺序确定,然后在读取 有序的数据
		Map<String, Fibercabmodule> treeDataMap = new TreeMap<String, Fibercabmodule>();
		Iterator<Fibercabmodule> it = modulesAndPorts.keySet().iterator();
		while (it.hasNext()) {
			Fibercabmodule module = it.next();
			if (module != null) {
				treeDataMap.put(module.getLabelCn(), module);
			}
		}
		return treeDataMap;
	}

	private ExtTreeNode newModuleNode(String FibercabmoduleName,
			Fibercabmodule module) {
		ExtTreeNode node=new ExtTreeNode();
		node.setText(FibercabmoduleName);
		node.setCuid(module.getCuid());
		node.setData(module.getObjectToMap());
		node.setIconCls("c_layout");
		node.setExpanded(true);
		node.setLeaf(false);
		node.setSystem(Fibercabmodule.CLASS_NAME);
		return node;
	}

	private Map<Fibercabmodule, DataObjectList> getModuleAndPortByFiberCab(
			FiberCab fibercab) {
		Map<Fibercabmodule, DataObjectList> dataMap = new HashMap<Fibercabmodule, DataObjectList>();
        try {
        	dataMap = (Map<Fibercabmodule, DataObjectList>) BoCmdFactory.getInstance().execBoCmd(FiberCabBOHelper.ActionName.getFiberCabModAndPortWithCheck,
    				new BoActionContext(), fibercab);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过光交接箱查询模块和端子失败",e);
		}
		return dataMap;
	}

	private FiberCab getFiberCabByCuid(ExtTreeNode node) {
		String sql=" CUID='"+node.getCuid()+"'";
		DataObjectList fiberCabList = null;
		try {
			fiberCabList=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberCabBO.getFiberCabBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			logger.error("通过CUID获取光交接箱失败", e);
			return null;
		}
		
		if(fiberCabList==null || fiberCabList.size()==0){
			return null;
		}
		
		FiberCab fibercab=(FiberCab)fiberCabList.get(0);
		return fibercab;
	}

	private ExtTreeNode getRootNode(FiberCab fibercab) {
		ExtTreeNode node=new ExtTreeNode();
		node.setText(fibercab.getLabelCn());
		node.setCuid(fibercab.getCuid());
		node.setExpanded(true);
		node.setIconCls("c_table");
		node.setLeaf(false);
		node.setSystem(FiberCab.CLASS_NAME);
		return node;
	}

	private List<ExtTreeNode> getPortList(String moduleCuid, DataObjectList list) {
		List<ExtTreeNode> children = new ArrayList<ExtTreeNode>();
		Map<Object, DataObjectList> map = TopoHelper.getListMapByAttr(list, Fcabport.AttrName.relatedModuleCuid);
		DataObjectList relatedPorts = (DataObjectList) map.get(moduleCuid);
		if(relatedPorts!=null){	
			Map<Object, DataObjectList> rowMap = null;
			try {
				rowMap = TopoHelper.getListMapByAttr(relatedPorts, Fcabport.AttrName.numInMrow);
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			if (rowMap != null) {
				Iterator<Object> itRow = rowMap.keySet().iterator();
				while (itRow.hasNext()) {
					Long row = (Long) itRow.next();
					ExtTreeNode rowNode = newRowNode(row);
					children.add(rowNode);
				}
			}			
		}
		return children;
	}

	private ExtTreeNode newRowNode(Long row) {
		ExtTreeNode node = new ExtTreeNode();
		node.setText("第"+row+"行");
		node.setCuid(row.toString());
		node.setIconCls("c_application_tile_vertical");
		node.setLeaf(true);
		node.setSystem("row");
		return node;
	}

}
