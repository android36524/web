package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.tree.bo.ITreePanelBO;
import com.boco.component.tree.pojo.ExtTreeNode;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.FiberDpBOHelper;

public class FiberDpPortTreeGridBO implements ITreePanelBO{

	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	public List<ExtTreeNode> loadData(ExtTreeNode node){       
		List<ExtTreeNode> result=new ArrayList<ExtTreeNode>();
		FiberDp fiberDp = getFiberDpByCuid(node);
		Map<Object,DataObjectList> rowMap = getFiberDpPortByFiberDp(fiberDp);		
		Long maxCol=getMaxCol(rowMap);	
		ExtTreeNode rootNode = getRootNode(fiberDp,maxCol);		
		List<ExtTreeNode> children = new ArrayList<ExtTreeNode>();
		 if (rowMap != null) {
             Iterator itRow = rowMap.keySet().iterator();
             while (itRow.hasNext()) {
                 Long row = (Long) itRow.next();                 
                 
                 ExtTreeNode rowNode = newRowNode(row);
                 children.add(rowNode);
             }
         }		
		rootNode.setChildren(children);		
		result.add(rootNode);		
        return result;
	}

	private Long getMaxCol(Map<Object, DataObjectList> rowMap) {
		Long maxCol=0L;
		if(rowMap==null){
			return maxCol;
		}
		Iterator itRow = rowMap.keySet().iterator();
		while(itRow.hasNext()){
			Long row = (Long) itRow.next();
			DataObjectList list=rowMap.get(row);                 
	        for(GenericDO port:list){
		       	 FiberDpPort fiberDpPort=(FiberDpPort)port;
		       	 long numInMcol=fiberDpPort.getNumInMcol();
		       	 if(numInMcol>maxCol){
		       		 maxCol=numInMcol;
		       	 }
	        }
		}
		
		return maxCol;
	}

	private Map<Object,DataObjectList> getFiberDpPortByFiberDp(FiberDp fiberDp){
		DataObjectList list = new DataObjectList();
        try {
            list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberDpBOHelper.ActionName.getFiberDpPortInDpWithRC,
                new BoActionContext(), fiberDp);
        } catch (Exception ex) {
            logger.error("通过光分纤箱获取端子失败",ex);
            return null;
        }        
        
        Map<Object,DataObjectList> rowMap = TopoHelper.getListMapByAttr(list, FiberDpPort.AttrName.numInMrow);
        
        return rowMap;
	}
	
	private FiberDp getFiberDpByCuid(ExtTreeNode node){
		String sql=" CUID='"+node.getCuid()+"'";
		DataObjectList list=new DataObjectList();
		try {
			list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IFiberDpBO.getFiberDpBySql",
	                new BoActionContext(), sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql查询光分纤箱失败",e);
			return null;
		}
        
        if(list==null || list.size()==0){
			return null;
		}
		
        FiberDp fiberDp=(FiberDp)list.get(0);
		return fiberDp;
	}

	
	private ExtTreeNode getRootNode(FiberDp fiberDp,Long maxCol) {
		ExtTreeNode node=new ExtTreeNode();
		node.setText(fiberDp.getLabelCn());
		node.setCuid(fiberDp.getCuid());
		node.setExpanded(true);
		Map<String,Long> map=new HashMap<String, Long>();
		map.put("PORT_COL", maxCol);
		node.setData(map);
		node.setIconCls("c_table");
		node.setLeaf(false);
		node.setSystem(FiberDp.CLASS_NAME);
		return node;
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
