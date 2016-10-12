package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class FCabGridTemplateProxyBO extends GridTemplateProxyBO {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
        //获取列名
		String name = this.getTemplateId(param);				
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();

        //拼装查询条件
		String relatedDeviceCuid=new String();
		if(param.getQueryParams()!=null){
			Collection<WhereQueryItem> whereitems=param.getQueryParams().values();
			if(whereitems!=null && whereitems.size()>0){
				for(WhereQueryItem item: whereitems){							
					if(item.getSqlValue().contains("CUID"));
					relatedDeviceCuid=item.getSqlValue().substring(item.getSqlValue().indexOf("'")+1,item.getSqlValue().lastIndexOf("'"));
				}
			}				
		}
		//获取光交接箱端子
		String portSql=" 1=1 AND RELATED_DEVICE_CUID ='"+relatedDeviceCuid.trim()+"' ORDER BY NUM_IN_MROW,NUM_IN_MCOL ";
		DataObjectList fcabPortList=getPortListBySql(relatedDeviceCuid.trim(),portSql);
		//获取光交接箱模块
		if(relatedDeviceCuid.trim().startsWith(FiberCab.CLASS_NAME)){
			String fibercabModuleSql=" 1=1 AND RELATED_DEVICE_CUID='"+relatedDeviceCuid.trim()+"'";
			DataObjectList fibercabModuleList=getFibercabModuleBySql(fibercabModuleSql);
			//将交接箱端子中的模块的CUID转换成对象
			convertModuleCuidToObject(fcabPortList,fibercabModuleList);
		}
		

		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0, fcabPortList.size(), 1);
		if(fcabPortList != null && fcabPortList.size()>0){
			for(int i=0;i<fcabPortList.size();i++){
				GenericDO gdo=fcabPortList.get(i);
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
			pageResult = new PageResult(list, fcabPortList.size(), fcabPortList.size(), 1);
		}
		return pageResult;
	}
	private DataObjectList getPortListBySql(String relatedDeviceCuid,String portSql){
		DataObjectList results=new DataObjectList();
		try {
			  if(relatedDeviceCuid.startsWith(FiberCab.CLASS_NAME)){
				  results = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFcabportBO.getFcabportBySql", new BoActionContext(),portSql);
			  }
			  else if(relatedDeviceCuid.startsWith(FiberDp.CLASS_NAME)){
				  results = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberDpPortBO.getFiberDpPortBySql", new BoActionContext(),portSql);
			  }
			  else if(relatedDeviceCuid.startsWith(FiberJointBox.CLASS_NAME)){
				  results = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFiberJointPointBO.getFiberJointPointBySql", new BoActionContext(),portSql);
			  }
				
			} catch (Exception e) {
				logger.error("通过sql获取光交接箱端子失败",e);
				return null;
		}
		return results;
	}

	private DataObjectList getFibercabModuleBySql(String sql) {
		DataObjectList fibercabmoduleList=new DataObjectList();
		try {
			fibercabmoduleList=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFibercabmoduleBO.getFibercabmoduleBySql", new BoActionContext(),sql);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过sql获取光交接箱模块失败",e);
			return null;
		}
		return fibercabmoduleList;
	}
	
	private DataObjectList getFcabPortListBySql(String sql){
		DataObjectList results=new DataObjectList();
		try {
				results = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IFcabportBO.getFcabportBySql", new BoActionContext(),sql);
			} catch (Exception e) {
				logger.error("通过sql获取光交接箱端子失败",e);
		}
		return results;
	}
	
	private void convertModuleCuidToObject(DataObjectList fcabPortList,DataObjectList fibercabModuleList){
		 for(GenericDO fcabPort: fcabPortList){
			 String relatedModuleCuid=fcabPort.getAttrString(Fcabport.AttrName.relatedModuleCuid);
			 for(GenericDO fibercabmodule: fibercabModuleList){
				 if(fibercabmodule.getCuid().equals(relatedModuleCuid)){
					 fcabPort.setAttrValue(Fcabport.AttrName.relatedModuleCuid,fibercabmodule);
				 }
			 }
		 }
	}

}
