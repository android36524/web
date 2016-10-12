package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.ProjectManagement;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class ProjectManagementTemplateProxyBO extends GridTemplateProxyBO {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());	
    
	private static final Map<String,GenericDO> map=new HashMap<String,GenericDO>();
	 static {
		 map.put("IRMS.RMS.MANHLE",new Manhle());
		 map.put("IRMS.RMS.POLE",new Pole());
		 map.put("IRMS.RMS.STONE",new Stone());
		 map.put("IRMS.RMS.INFLEXION",new Inflexion());
		 map.put("IRMS.RMS.FIBER_CAB",new FiberCab());
		 map.put("IRMS.RMS.FIBER_DP",new FiberDp());
		 map.put("IRMS.RMS.FIBERJOINTBOX",new FiberJointBox());
		 map.put("IRMS.RMS.WIRE_SEG",new WireSeg());
		 map.put("IRMS.RMS.DUCT_SEG",new DuctSeg());
		 map.put("IRMS.RMS.STONEWAY_SEG",new StonewaySeg());
		 map.put("IRMS.RMS.POLEWAY_SEG",new PolewaySeg());
		 map.put("IRMS.RMS.UPLINESEG",new UpLineSeg());
		 map.put("IRMS.RMS.HANG_WALL_SEG",new HangWallSeg());
		 map.put("IRMS.RMS.ONUBOX",new FiberCab());
		 map.put("IRMS.RMS.INTERWIRE_APOINT",new InterWire());
	 }
	
	private static final Map<String,String> classBoMap=new HashMap<String, String>();
	static {
		classBoMap.put(DuctSeg.CLASS_NAME, DuctSegBOHelper.ActionName.getDuctSegBySql);
		classBoMap.put(StonewaySeg.CLASS_NAME,StonewaySegBOHelper.ActionName.getStonewaySegBySql);
		classBoMap.put(PolewaySeg.CLASS_NAME,PolewaySegBOHelper.ActionName.getPolewaySegBySql);
		classBoMap.put(UpLineSeg.CLASS_NAME,UpLineSegBOHelper.ActionName.getSegmentsBySql);
		classBoMap.put(HangWallSeg.CLASS_NAME,HangWallSegBOHelper.ActionName.getSegmentsBySql);
	}
	@Override
	public PageResult getGridData(PageQuery queryParam,GridCfg param){
		//获取列名
		String name = this.getTemplateId(param);				
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();   
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();
		
		String projectCuid=new String();
		projectCuid=param.getCfgParams().get("cuid");
		DataObjectList resources=getResourcesByProjectCuid(map.get(name), projectCuid);
		if(resources==null){
			return null;
		}		
		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0, resources.size(), 1);
		if(resources.size()>0){
			for(GenericDO gdo: resources){
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				Map map = new HashMap();
				for(String columnName : columnMap.keySet()){
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName,value));
				}
				list.add(map);
			}
			pageResult = new PageResult(list, resources.size(), resources.size(), 1);
		}

		return pageResult;		
	}
	
	private DataObjectList getResourcesByProjectCuid(GenericDO gdo,String projectCuid){
		DataObjectList resources=new DataObjectList();
		DboCollection collection=new DboCollection();
		
	    ProjectManagement projectManagement=getProjectManagementByCuid(projectCuid);
		
		String sql=" RELATED_PROJECT_CUID='"+projectCuid+"'";
		try {			
			if(gdo instanceof WireSeg){
				collection=(DboCollection)BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegBySql, new BoQueryContext(),sql);
				resources=convertCollectionToList(collection);
			}
			else if(gdo instanceof DuctSeg || gdo instanceof StonewaySeg || gdo instanceof PolewaySeg
					|| gdo instanceof UpLineSeg || gdo instanceof HangWallSeg){
				resources=(DataObjectList)BoCmdFactory.getInstance().execBoCmd(classBoMap.get(gdo.getClassName()), new BoActionContext(),sql);
			}
			else{
				IDuctManagerBO ductManagerBO = (IDuctManagerBO)BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				resources=ductManagerBO.getObjectsBySql(sql,gdo);
			}			
		} catch (Exception e) {
				logger.error("通过sql查询所属工程下的资源失败",e);
				return null;
		}
		
		for(GenericDO dbo: resources){
			dbo.setAttrValue(Manhle.AttrName.relatedProjectCuid, projectManagement);
		}

		return resources;
	}
	
	private ProjectManagement getProjectManagementByCuid(String projectCuid){		
		ProjectManagement projectManagement=null;
		try {
			projectManagement=(ProjectManagement)BoCmdFactory.getInstance().execBoCmd("IProjectManagementBO.getProjectManagementByCuid", new BoActionContext(),projectCuid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过cuid查询工程失败",e);
			return null;
		}
		return projectManagement;
		
	}
	
	private DataObjectList convertCollectionToList(DboCollection collection){
		DataObjectList list=new DataObjectList();
		if(collection==null || collection.size()==0){
			return list;
		}
		for(int i=0;i<collection.size();i++){
			list.add(collection.getAttrField(WireSeg.CLASS_NAME, i));
		}		
		return list;
	}

}
