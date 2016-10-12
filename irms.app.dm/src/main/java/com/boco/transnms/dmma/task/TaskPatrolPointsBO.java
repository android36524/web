package com.boco.transnms.dmma.task;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.pojo.GridMeta;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;

public class TaskPatrolPointsBO  extends XmlTemplateGridBO  implements DmmaCommonIntf<ITaskBO>{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		DataObjectList data = new DataObjectList();
		ITaskBO taskbo = getBo();
		JSONArray jsonArray = JSONArray.fromObject(arg1.getCustType());
		for(Object jsonObj:jsonArray){
			Map jsonData = (Map) jsonObj;
			String taskCuid = (String)jsonData.get("CUID");
			String pointtype = null;
			String longilatitype = null;
			String pointname = null;
			if( arg1.getCfgParams() != null ){
				pointtype = arg1.getCfgParams().get("RESOUCE_TYPE");
				 longilatitype = arg1.getCfgParams().get("ORIGINAL");
				 pointname = arg1.getCfgParams().get("LABEL_CN");
			};
			data=doQuery(taskCuid,pointtype,longilatitype,pointname,arg0);
			data.setCountValue(taskbo.getResultCount(getCountSql(taskCuid,pointtype,longilatitype,pointname)));
		}
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode());
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	
	public DataObjectList doQuery(String taskCuid,String pointtype,String longilatitype,String pointname,PageQuery arg0) {
			ITaskBO taskbo = getBo();
			DataObjectList dbos = taskbo.getInspectPoints(getQuerySQL(taskCuid, pointtype, longilatitype, pointname), arg0.getCurPageNum(), arg0.getPageSize());
			dbos.setCountValue(taskbo.getResultCount(getCountSql(taskCuid, pointtype, longilatitype, pointname)));
			return dbos;
	
	}
	private String getCountSql(String taskCuid,String pointtype,String longilatitype,String pointname) {
		String sql = "select count(*) countvalue from pda_point_chang_loc t "
				+ " where t.related_task_cuid='" + taskCuid
				+ "' ";
		if (!"全部".equals(pointtype)) {
			sql += " and t.related_point_cuid like '%"+pointtype+"%' ";
		}
		if (!"".equals(pointname)) {
			sql += " and t.point_label_cn like '%" + pointname + "%'";
		}
		if(!"全部".equals(longilatitype) && !"原经纬度".equals(longilatitype)){
			sql += " and t.new_latitude is not null and t.new_longitude is not null and t.new_latitude !=0 and t.new_longitude !=0";
		}
		return sql;
	}

	private String getQuerySQL(String taskCuid,String pointtype,String longilatitype,String pointname) {

		String sql = " select "
				+ " t.cuid,"
				+ " t.related_task_cuid,"
				+ " t.related_point_cuid,"
				+ " t.related_device_code,"
				+ " t.original_latitude,"
				+ " t.original_longitude,"
				+ " t.new_latitude,"
				+ " t.new_longitude,"
				+ " t.point_label_cn,"
				+ " it.is_must_reach,"
				+ " it.point_state"
				+ " from pda_point_chang_loc t,"
				+ "(select distinct tem.point_cuid, tem.is_must_reach, tem.point_state"
				+ " from inspect_task_detail tem"
				+ " where tem.related_task_cuid ="
				+ "'"
				+ taskCuid
				+ "') it"
				+ " where t.related_task_cuid ="
				+ "'"
				+ taskCuid
				+ "'"
				+ " and t.related_point_cuid = it.point_cuid  ";

		if (!"全部".equals(pointtype) && pointtype != null) {
			sql += " and t.related_point_cuid like '%"
					+ pointtype+ "%' ";
		}
		if (!"".equals(pointname) && pointname != null) {
			sql += " and t.point_label_cn like '%" + pointname + "%'";
		}
		if (!"全部".equals(longilatitype) && !"原经纬度".equals(longilatitype)  && longilatitype != null) {
			sql += " and t.new_latitude is not null and t.new_longitude is not null and t.new_latitude !=0 and t.new_longitude !=0";
		}
		return sql;
	}
	
	@Override
	public ITaskBO getBo() {
		return (ITaskBO) DmmaBoFactory.getInstance().getBo();
	}

}
