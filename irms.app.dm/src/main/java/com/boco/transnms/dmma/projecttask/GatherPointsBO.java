package com.boco.transnms.dmma.projecttask;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.project.ICreateResourceBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.dmma.utils.DmmaEnumChangeIntf;

public class GatherPointsBO extends XmlTemplateGridBO  implements DmmaCommonIntf<ICreateResourceBO>{
	protected String getSQL( GridCfg arg1) {
		String where = " where 1 =2 ";
		String sql = "select * from t_pda_newwork_point_temp t ";
		if(arg1 != null && arg1.getCfgParams() != null && StringUtils.isNotEmpty(arg1.getCfgParams().get("RELATED_TASK_CUID"))){
			where = "  where t.taskcuid='"+arg1.getCfgParams().get("RELATED_TASK_CUID")+"'";
			if(!"全部".equals(arg1.getCfgParams().get("POINTTYPE")) && StringUtils.isNotEmpty(arg1.getCfgParams().get("POINTTYPE"))){
				where += " and t.pointtype= '"+arg1.getCfgParams().get("POINTTYPE")+"'";
			}
			if(!"".equals(arg1.getCfgParams().get("POINTNAME")) && StringUtils.isNotEmpty(arg1.getCfgParams().get("POINTNAME"))){
				where+=" and t.pointname like '%"+arg1.getCfgParams().get("POINTNAME")+"%'";
			}
		}
		sql = sql +where;
		sql += " order by t.point_index";
		return sql;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		ICreateResourceBO taskbo = getBo(); 
		DataObjectList data = taskbo.getGatherPoints(getSQL(arg1), arg0.getCurPageNum(), arg0.getPageSize());
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode(),new DmmaEnumChangeIntf() {
			
			@Override
			public void changEnumValue(Object key, Object value, Map element) {
				if(StringUtils.equalsIgnoreCase((String) key, "POINTTYPE")){
					if(DuctEnum.POINT_TYPE.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.POINT_TYPE.getAllEnum().get(value));
					};
				};
				if(StringUtils.equalsIgnoreCase((String) key, "OwnerShip")){
					if(DuctEnum.DM_OWNER_SHIP.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_OWNER_SHIP.getAllEnum().get(value));
					};
				};
				if(StringUtils.equalsIgnoreCase((String) key, "Purpose")){
					if(DuctEnum.DM_PURPOSE.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.DM_PURPOSE.getAllEnum().get(value));
					};
				};
				
			}
		});
		ITaskBO taskbo2 = (ITaskBO) DmmaBoFactory.getInstance().getBo();
		int countValue = taskbo2.getResultCount(getCountSql(arg1));
		
		PageResult results = new PageResult<Map>(elements, countValue, arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	

	public ICreateResourceBO getBo() {
		return (ICreateResourceBO) DmmaBoFactory.getInstance().getICreateResourceBO();
	}
	
	private String getCountSql(GridCfg arg1) {
		String where = " where 1 =2 ";
		String sql = "select count(*) countvalue from t_pda_newwork_point_temp t ";
		if(arg1 != null && arg1.getCfgParams() != null && StringUtils.isNotEmpty(arg1.getCfgParams().get("RELATED_TASK_CUID"))){
			where = "  where t.taskcuid='"+arg1.getCfgParams().get("RELATED_TASK_CUID")+"'";
			if(!"全部".equals(arg1.getCfgParams().get("POINTTYPE")) && StringUtils.isNotEmpty(arg1.getCfgParams().get("POINTTYPE"))){
				where += " and t.pointtype= '"+arg1.getCfgParams().get("POINTTYPE")+"'";
			}
			if(!"".equals(arg1.getCfgParams().get("POINTNAME")) && StringUtils.isNotEmpty(arg1.getCfgParams().get("POINTNAME"))){
				where+=" and t.pointname like '%"+arg1.getCfgParams().get("POINTNAME")+"%'";
			}
		}
		sql = sql +where;
//		sql += " order by t.point_index";
		return sql;
	}
	
}
