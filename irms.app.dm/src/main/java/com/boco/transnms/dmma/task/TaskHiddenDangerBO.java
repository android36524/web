package com.boco.transnms.dmma.task;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.server.bo.base.BOHome;

public class TaskHiddenDangerBO extends XmlTemplateGridBO  implements DmmaCommonIntf<ITaskBO>{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		DataObjectList data = new DataObjectList();
		ITaskBO taskbo = getBo();
		JSONArray jsonArray = JSONArray.fromObject(arg1.getCustType());
		data=doQuery(arg0,arg1);
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode());
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}

	private String getQuerySQL(String taskCuid,String pointname,String pointvalue,String troblevalue) {
		String sql = "select h.*,t.label_cn,t.longitude,t.latitude from hidden_danger h," +
				"(select distinct p.related_point_cuid, p.point_label_cn as label_cn," +
				"p.original_latitude as latitude," +
				"p.original_longitude as longitude " +
				"from pda_point_chang_loc p where p.related_task_cuid='"+taskCuid+"') t " +
				"where h.related_task_cuid='"+taskCuid+"' and  h.related_point_cuid=t.related_point_cuid ";
		if (!"".equals(pointname) && StringUtils.isNotEmpty(pointname)) {
			sql += " and t.label_cn like '%" + pointname + "%'";
		}
		
		if (!"全部".equals(pointvalue)&& StringUtils.isNotEmpty(pointvalue)) {
			sql += " and h.related_point_cuid like '%"+pointvalue+"%' ";
		}
		
		if (!"全部".equals(troblevalue)&& StringUtils.isNotEmpty(troblevalue)) {
			sql += " and h.trouble_type = "+troblevalue;
		}
		return sql;
	}
	
private String getCountSql(String taskCuid,String pointname,String pointvalue,String troblevalue) {
		
		String sql = "select count(*) countvalue from hidden_danger h," +
		"(select distinct p.related_point_cuid, p.point_label_cn as label_cn," +
		"p.original_latitude as latitude," +
		"p.original_longitude as longitude " +
		"from pda_point_chang_loc p where p.related_task_cuid='"+taskCuid+"') t " +
		"where h.related_task_cuid='"+taskCuid+"' and  h.related_point_cuid=t.related_point_cuid ";
		if (!"".equals(pointname) && StringUtils.isNotEmpty(pointname)) {
			sql += " and t.label_cn like '%" + pointname + "%'";
		}
		
		if (!"全部".equals(pointvalue)&& StringUtils.isNotEmpty(pointvalue)) {
			sql += " and h.related_point_cuid like '%"+pointvalue+"%' ";
		}
		
		if (!"全部".equals(troblevalue)&& StringUtils.isNotEmpty(troblevalue)) {
			sql += " and h.trouble_type = "+troblevalue;
		}
		return sql;
	}

	public DataObjectList doQuery(PageQuery arg0, GridCfg arg1) {
		DataObjectList dbos= new DataObjectList();
		JSONArray jsonArray = JSONArray.fromObject(arg1.getCustType());
		Map jsonMap = (Map)jsonArray.get(0);
			String taskCuid = (String) jsonMap.get("CUID");
			String pointname= "";
			String pointvalue= "";
			String troblevalue = "";
			
			if(arg1.getCfgParams() != null){
				 pointname= arg1.getCfgParams().get("POINT_NAME");
				 pointvalue= arg1.getCfgParams().get("POINT_TYPE");
				 troblevalue = arg1.getCfgParams().get("TROBLE_TYPE");
			}
			ITaskBO taskbo = getBo() ;
			 dbos = taskbo.getHiddenDangerPoints(getQuerySQL(taskCuid,pointname,pointvalue,troblevalue), arg0.getCurPageNum(), arg0.getPageSize());
			dbos.setCountValue(taskbo.getResultCount(getCountSql(taskCuid,pointname,pointvalue,troblevalue)));
		return dbos;
	}
	
	@Override
	public ITaskBO getBo() {
		return (ITaskBO) DmmaBoFactory.getInstance().getBo();
	}
	

}
