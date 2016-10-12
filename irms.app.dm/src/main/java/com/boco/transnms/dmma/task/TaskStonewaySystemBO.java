package com.boco.transnms.dmma.task;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.dmma.utils.DmmaEnumChangeIntf;

public class TaskStonewaySystemBO extends XmlTemplateGridBO  implements DmmaCommonIntf<ITaskBO>{

	@Override
	public ITaskBO getBo() {
		return (ITaskBO) DmmaBoFactory.getInstance().getBo();
	}
	
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		ITaskBO taskbo = getBo();
		DataObjectList data = taskbo.getSelectSystem(getSQL(arg1)+"&&true", arg0.getCurPageNum(), arg0.getPageSize());
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode(),new DmmaEnumChangeIntf() {
			
			@Override
			public void changEnumValue(Object key, Object value, Map element) {
				if(StringUtils.equalsIgnoreCase((String) key, "OWNERSHIP")){
					if(DuctEnum.DM_CAB_OWNER_SHIP.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_CAB_OWNER_SHIP.getAllEnum().get(value));
					}
				};
				if(StringUtils.equalsIgnoreCase((String) key, "SYSTEM_LEVEL")){
					if(DuctEnum.DM_SYSTEM_LEVEL.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_SYSTEM_LEVEL.getAllEnum().get(value));
					}
				};
				if(StringUtils.equalsIgnoreCase((String) key, "MAINT_MODE")){
					if(DuctEnum.DM_MAINT_MODE.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_MAINT_MODE.getAllEnum().get(value));
					}
				};
				if(StringUtils.equalsIgnoreCase((String) key, "STATE")){
					if(DuctEnum.DM_STATE.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_STATE.getAllEnum().get(value));
					}
				}
			}
		});
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		results.setTotalCount(taskbo.getResultCount(getCountSql(arg1)));
		return results;
		
	}
	protected String getSQL( GridCfg arg1) {
	      String sql = "SELECT * FROM STONEWAY_SYSTEM WHERE 1=1 ";
	      if(arg1 != null && arg1.getCfgParams() != null && arg1.getCfgParams().get("queryData") != null){
	    	  JSONObject jsonObj = JSONObject.fromObject( arg1.getCfgParams().get("queryData") );
	    	  String  labelcn = jsonObj.getString("LABEL_CN");
	    	  String  project = jsonObj.getString("PROJECT");
	    	  String  ownership = jsonObj.getString("OWNERSHIP");
	    	  String systemLevel = jsonObj.getString("SYSTEM_LEVEL");
	    	  String mainMode = jsonObj.getString("MAINT_MODE");
	    	  if (StringUtils.isNotEmpty(labelcn)) {
	              sql += " AND LABEL_CN like '%" + labelcn + "%' ";
	          };
	          if (StringUtils.isNotEmpty(project)) {
	              sql += " AND  PROJECT like '%" + project + "%' ";
	          };
	          if (StringUtils.isNotEmpty(ownership)) {
	              sql += " AND OWNERSHIP=" + ownership + " ";
	          }
	          if (StringUtils.isNotEmpty(systemLevel)) {
	              sql += " SYSTEM_LEVEL=" + systemLevel + " ";
	          }
	          if (StringUtils.isNotEmpty(mainMode)) {
	              sql += " AND  MAINT_MODE=" + mainMode + " ";
	          }
	      }
	      return sql;
	  }
		
		
		
		private String getCountSql( GridCfg arg1){
			 String sql = " SELECT COUNT(*) COUNTVALUE FROM STONEWAY_SYSTEM WHERE 1=1 ";  
			  if(arg1 != null && arg1.getCfgParams() != null && arg1.getCfgParams().get("queryData") != null){
		    	  JSONObject jsonObj = JSONObject.fromObject( arg1.getCfgParams().get("queryData") );
		    	  String  labelcn = jsonObj.getString("LABEL_CN");
		    	  String  project = jsonObj.getString("PROJECT");
		    	  String  ownership = jsonObj.getString("OWNERSHIP");
		    	  String systemLevel = jsonObj.getString("SYSTEM_LEVEL");
		    	  String mainMode = jsonObj.getString("MAINT_MODE");
		    	  if (StringUtils.isNotEmpty(labelcn)) {
		              sql += " AND LABEL_CN like '%" + labelcn + "%' ";
		          };
		          if (StringUtils.isNotEmpty(project)) {
		              sql += " AND  PROJECT like '%" + project + "%' ";
		          };
		          if (StringUtils.isNotEmpty(ownership)) {
		              sql += " AND OWNERSHIP=" + ownership + " ";
		          }
		          if (StringUtils.isNotEmpty(systemLevel)) {
		              sql += " SYSTEM_LEVEL=" + systemLevel + " ";
		          }
		          if (StringUtils.isNotEmpty(mainMode)) {
		              sql += " AND  MAINT_MODE=" + mainMode + " ";
		          }
		      }
				return sql;
		}

}
