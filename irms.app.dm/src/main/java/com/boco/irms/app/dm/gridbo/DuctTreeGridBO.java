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
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class DuctTreeGridBO implements ITreePanelBO{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    	
	private static final Map<String,String> map=new HashMap<String, String>();
	static {
		map.put(DuctBranch.CLASS_NAME, "IDuctSegBO.getDuctSegBySql");
		map.put(PolewayBranch.CLASS_NAME, "IPolewaySegBO.getPolewaySegBySql");
		map.put(StonewayBranch.CLASS_NAME, "IStonewaySegBO.getStonewaySegBySql");
		map.put(UpLine.CLASS_NAME, "IUpLineSegBO.getSegmentsBySql");
		map.put(HangWall.CLASS_NAME, "IHangWallSegBO.getSegmentsBySql");
	};
	
	@Override
	public List<ExtTreeNode> loadData(ExtTreeNode node) {
		String systemCuid = MapUtils.getString(node.getParams(), "cuid");
		String systemLabelcn = getLabelcnByCuid(systemCuid);
		DataObjectList dataObjectList = this.executeCmdReturnDataObjectList("IDuctManagerBO.getBranchsBySystemCuid",systemCuid);
		Map map = this.executeCmdReturnMap("IDuctManagerBO.getSegsAndPointsBySystemCuid", MapUtils.getString(node.getParams(), "cuid"));
		DataObjectList segDataObjectList = (DataObjectList)map.get("segsList");
		DataObjectList pointDataObjectList = (DataObjectList)map.get("pointsList");
		
		Map<String,ExtTreeNode> parentNodeMap = new HashMap<String,ExtTreeNode>();
		
		for(GenericDO dbo : dataObjectList){
			ExtTreeNode n = new ExtTreeNode();
			n.setCuid(dbo.getAttrString("CUID"));
			n.setText(dbo.getAttrString("LABEL_CN"));
			dbo.setAttrValue("RELATED_SYSTEM_CUID", systemLabelcn);
			String origId = dbo.getAttrString("ORIG_POINT_CUID");
			String destId = dbo.getAttrString("DEST_POINT_CUID");
			//调用bo转换连接起点
			if (!StringUtils.isEmpty(origId)) {
				List<GenericDO> list=pointDataObjectList.getObjectByCuid(origId);
				String origName=new String();
				if(list!=null  && list.size()>0){
					origName=list.get(0).getAttrString("LABEL_CN");					
				}				
				dbo.setAttrValue("ORIG_POINT_CUID", origName);
			}
			//调用bo转换连接终点
			if(!StringUtils.isEmpty(destId)){
				List<GenericDO> list=pointDataObjectList.getObjectByCuid(destId);
				String destName=new String();
				if(list!=null && list.size()>0){
					destName=list.get(0).getAttrString("LABEL_CN");
				}
				dbo.setAttrValue("DEST_POINT_CUID", destName);
			}
			
			n.setExpanded(true);
			n.setLeaf(false);
			n.setTreeName("DUCTSEG");
			dbo.setAttrValue("BMCLASSTYPE", "BRANCH");
			n.setData(dbo.getObjectToMap());
			parentNodeMap.put(n.getCuid(), n);
		}
		
		for(GenericDO dbo : segDataObjectList){
			ExtTreeNode n = new ExtTreeNode();
			String origId = dbo.getAttrString("ORIG_POINT_CUID");
			String destId = dbo.getAttrString("DEST_POINT_CUID");
			//调用bo转换连接起点
			if (!StringUtils.isEmpty(origId)) {
				List<GenericDO> list=pointDataObjectList.getObjectByCuid(origId);
				String origName=new String();
				if(list!=null  && list.size()>0){
					origName=list.get(0).getAttrString("LABEL_CN");
				}
				dbo.setAttrValue("ORIG_POINT_CUID", origName);
			}
			//调用bo转换连接终点
			if(!StringUtils.isEmpty(destId)){
				List<GenericDO> list=pointDataObjectList.getObjectByCuid(destId);
				String destName=new String();
				if(list!=null  && list.size()>0){
					destName=list.get(0).getAttrString("LABEL_CN");
				}
				dbo.setAttrValue("DEST_POINT_CUID", destName);
			}
			n.setCuid(dbo.getAttrString("CUID"));
			n.setText(dbo.getAttrString("LABEL_CN"));
			n.setLeaf(true);
			n.setTreeName("DUCTSEG");
			n.setData(dbo.getObjectToMap());
			String branchCuid = dbo.getAttrString("RELATED_BRANCH_CUID");
			if(parentNodeMap.containsKey(branchCuid)){
				parentNodeMap.get(branchCuid).addChild(n);
			}else{
				parentNodeMap.put(n.getCuid(), n);
			}
		}
		List<ExtTreeNode> results = new ArrayList<ExtTreeNode>();
		for(Map.Entry<String, ExtTreeNode> it : parentNodeMap.entrySet()){
			results.add(it.getValue());
		}
		return results;
	}
	
	/**
	 * 获取显示路由点
	 * @param branchCuid
	 * @return
	 */
	public  static DataObjectList getRoutePointsByBranchCuid(String branchCuid){
		DataObjectList routenodes = new DataObjectList();
		String sql = " RELATED_BRANCH_CUID='"+branchCuid+"'";
		DataObjectList segs = null;
		try {
			    String key=branchCuid.substring(0, branchCuid.indexOf("-"));
			    if((!key.equals(UpLine.CLASS_NAME)) && (!key.equals(HangWall.CLASS_NAME)) && map.containsKey(key)){
			    	segs = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(map.get(key), new BoActionContext(),sql);
			    }
			    else if((key.equals(UpLine.CLASS_NAME) || key.equals(HangWall.CLASS_NAME)) && map.containsKey(key)){
					sql=" RELATED_SYSTEM_CUID='"+branchCuid+"'";
					segs=(DataObjectList)BoCmdFactory.getInstance().execBoCmd(map.get(key), new BoActionContext(),sql);			
			    }
				
			} catch (Exception e) {
                  e.printStackTrace();
		}
		
        if (segs != null) {
        	segs.sort("INDEX_IN_BRANCH", true);
        }
        
        DataObjectList res = DMHelper.getSegsPoints(segs);
        routenodes = res;
        /**
         * 判断是否是显示路由时,只返回是显示路由点的点设施的CUID,不再返回显示路由段
         * 对象.这样可以减少不必要的网络传输时间.
         **/
		List<String> pointCuidList = null;
		try {
			pointCuidList = (List<String>)BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getDisplayPointsByRelatedCuid", new BoActionContext(),branchCuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Map<String, String> pointCuidMap = new HashMap<String, String>();
        List<String> presetPointCuidList = new ArrayList<String>();
        for (Object obj : pointCuidList) {
            if (obj instanceof String) {
                String str = (String) obj;
                pointCuidMap.put(str, str);
                if (str.startsWith(PresetPoint.CLASS_NAME)) {
                    presetPointCuidList.add(str);
                }
            }
        }
        //处理显示路由点
        for (int i = 0; i < routenodes.size(); i++) {
    	    GenericDO gdo = routenodes.get(i);
            if (pointCuidMap.get(gdo.getCuid()) != null) {
            	gdo.setAttrValue("ISSELECTED", true);
            }
            gdo.setAttrValue("CLASS_TYPE", WebDMUtils.getLabelCnByClassNameOrCuid(gdo.getCuid()));
        }
        if (presetPointCuidList.size() < 1) {
            return routenodes;
        }
        String[] presetPointCuid = new String[presetPointCuidList.size()];
        for (int i = 0; i < presetPointCuid.length; i++) {
            presetPointCuid[i] = presetPointCuidList.get(i);
        }
        DataObjectList presetPointList = null;
        try {
			presetPointList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjsByCuid", new BoActionContext(),presetPointCuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for (int i = 0; i < presetPointList.size(); i++) {
            GenericDO point = presetPointList.get(i);
            if (point == null) {
                continue;
            }
            point.setAttrValue("ISSELECTED", true);
            routenodes.add(getPointIndex(routenodes,getPrePointCuid(pointCuidList, point.getCuid())), point);
        }
        
		return routenodes;
	}
	
    private static int getPointIndex(DataObjectList routenodes,String id) {
        int index = 1;
        for (GenericDO o : routenodes) {
            if (o.getCuid().equals(id)) {
                break;
            }
            index++;
        }
        return index;
    }
    
    private static String getPrePointCuid(List<String> allPointCuid, String cuid) {
        String pre = null;
        for (int i = 0; i < allPointCuid.size(); i++) {
            if (allPointCuid.get(i).equals(cuid)) {
                if (i != 0) {
                    pre = allPointCuid.get(i - 1);
                }
                break;
            }
        }
        return pre;
    }

	private DataObjectList executeCmdReturnDataObjectList(String method,String sql){
		//从传输服务查询数据
		BoActionContext actionContext = new BoActionContext();
		actionContext.setUserId("SYS_USER-0");
		DataObjectList  results = new DataObjectList();
		try {
			results = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(method, actionContext,sql);
		} catch (Exception e) {
			throw new UserException(e);
		}
		return results;
	}
	
	private Map executeCmdReturnMap(String method,String sql){
		//从传输服务查询数据
		BoActionContext actionContext = new BoActionContext();
		actionContext.setUserId("SYS_USER-0");
		Map  results = new HashMap();
		try {
			results = (Map)BoCmdFactory.getInstance().execBoCmd(method, actionContext,sql);
		} catch (Exception e) {
			throw new UserException(e);
		}
		return results;
	}
	
	/**
	 * 通过id值获取其对应的名称
	 * @param id
	 * @return
	 * author wangqin
	 */
	public String getLabelcnByCuid(String cuid){
		String name = cuid;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), cuid);
			if(gdo != null){
				name = gdo.getAttrString("LABEL_CN");
				if(!StringUtils.isEmpty(name)){
					name = gdo.getAttrString("LABEL_CN");
				}
			}	
		} catch (Exception e) {
			logger.error("转换失败");
		}
	
		return name;
	}
	
}
