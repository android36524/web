package com.boco.transnms.dmma.hitchtask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.hitch.IHitchBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.dmma.utils.DmmaEnumChangeIntf;

public class HitchTaskBO  extends XmlTemplateGridBO  implements DmmaCommonIntf<IHitchBO>{
	protected String getSQL(GridCfg arg1) {
		String sql = " select t.*,hd.hitch_longitude,hd.hitch_latitude,hd.hitch_info,hd.hitch_picture," +
				"pd.user_name as pda_user_name,tp.task_state as pda_task_state," +
				"tp.accept_time as pda_accept_time,tp.actual_end_time as pda_actual_end_time " +
				"from pda_task t,pda_device pd,task_to_pda tp,hitch_task_detail hd " +
				"where t.task_type=3 and t.cuid=tp.related_task_cuid " +
				"and hd.related_task_cuid=t.cuid "+
				"and pd.device_code=tp.related_device_code ";
		if(arg1.getCfgParams() == null){
			sql += " order by t.task_time desc";
			return sql;
		};
		String tempTaskState = arg1.getCfgParams().get("TASK_STATE");
		String tempPdaTaskState = arg1.getCfgParams().get("PDA_TASK_STATE");
		String taskName = arg1.getCfgParams().get("LABEL_CN");
		String taskArchiveTime = arg1.getCfgParams().get("ARCHIVE_TIME");
		String taskActualEndTime = arg1.getCfgParams().get("PDA_ACTUAL_END_TIME");
		String isArchive = arg1.getCfgParams().get("ISSAVE");
		if (taskName != null && !"".equals(taskName)) {
			sql += "  and t.label_cn like '%" + taskName + "%'";
		}
		if(!"全部".equals(tempTaskState)&& StringUtils.isNotEmpty(tempTaskState)){
			sql += " and t.task_state="+DuctEnum.TASK_STATE_ENUM.getValue(tempTaskState);
		}
		if(!"全部".equals(tempPdaTaskState)&& StringUtils.isNotEmpty(tempPdaTaskState)){
			sql += " and t.pda_task_state="+DuctEnum.PDA_TASK_STATE_ENUM.getValue(tempPdaTaskState);
		}
		if(taskArchiveTime!=null && !taskArchiveTime.equals("")){
			String archivetime = taskArchiveTime.replace("/", "-").split(" ")[0];
			sql += " and t.archive_time like '%"+archivetime+"%'";
		}
		if(taskActualEndTime!=null && !taskActualEndTime.equals("")){
			String actualtime = taskActualEndTime.replace("/", "-").split(" ")[0];
			sql += " and tp.actual_end_time like '%"+actualtime+"%'";
		}
		if(isArchive != null && !"".equals(isArchive) && !"全部".equals(isArchive)){
			String is_archive = isArchive;
			String issave = "0";
			if("是".equals(is_archive)){
				issave="1";
			}else{
				issave="0";
			}
			sql += " and t.issave="+issave;
		}
		
		if(arg1.getAc() != null && arg1.getAc().getRelatedDepartmentCuid() != null && StringUtils.equalsIgnoreCase(arg1.getAc().getRelatedDepartmentCuid(), String.valueOf(0))&& arg1.getAc().getRelatedDepartmentCuid().length()!=14 && arg1.getAc().getRelatedDepartmentCuid().length()!=20){
			sql += " and t.related_district_cuid like '%"+arg1.getAc().getRelatedDepartmentCuid()+"%'";
		}
		sql += " order by t.task_time desc";
		return sql;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		IHitchBO hitchbo = getBo(); 
		ITaskBO taskbo = (ITaskBO) DmmaBoFactory.getInstance().getBo();
		DataObjectList data = hitchbo.queryHitchTask(getSQL(arg1), arg0.getCurPageNum(), arg0.getPageSize());
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode(),new DmmaEnumChangeIntf() {
			@Override
			public void changEnumValue(Object key, Object value, Map element) {
				if(StringUtils.equalsIgnoreCase((String) key, "TASK_STATE")){
					if(DuctEnum.TASK_STATE_ENUM.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.TASK_STATE_ENUM.getAllEnum().get(value));
					};
				};
				if(StringUtils.equalsIgnoreCase((String) key, "PDA_TASK_STATE")){
					if(DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().get(value));
					};
				};
				if(StringUtils.equalsIgnoreCase((String) key, "PDA_TASK_STATE")){
					if(DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().containsKey(value)){
						element.put(key, DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().get(value));
					};
				};
			}
		});
		int countValue = taskbo.getResultCount(getCountSql(arg1));
		PageResult results = new PageResult<Map>(elements, countValue, arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	
	private String getCountSql(GridCfg arg1) {

		String sql = " select count(*) countvalue " +
				"from pda_task t,pda_device pd,task_to_pda tp " +
				"where t.task_type= 3 and t.cuid=tp.related_task_cuid " +
				"and pd.device_code=tp.related_device_code";
		if(arg1.getCfgParams() == null){
			sql += " order by t.task_time desc";
			return sql;
		};
		String tempTaskState = arg1.getCfgParams().get("TASK_STATE");
		String tempPdaTaskState = arg1.getCfgParams().get("PDA_TASK_STATE");
		String taskName = arg1.getCfgParams().get("LABEL_CN");
		String taskArchiveTime = arg1.getCfgParams().get("ARCHIVE_TIME");
		String taskActualEndTime = arg1.getCfgParams().get("PDA_ACTUAL_END_TIME");
		String isArchive = arg1.getCfgParams().get("ISSAVE");
		if (taskName != null && !"".equals(taskName)) {
			sql += "  and t.label_cn like '%" + taskName + "%'";
		}
		if(!"全部".equals(tempTaskState)&& StringUtils.isNotEmpty(tempTaskState)){
			sql += " and t.task_state="+DuctEnum.TASK_STATE_ENUM.getValue(tempTaskState);
		}
		if(!"全部".equals(tempPdaTaskState)&& StringUtils.isNotEmpty(tempPdaTaskState)){
			sql += " and t.pda_task_state="+DuctEnum.PDA_TASK_STATE_ENUM.getValue(tempPdaTaskState);
		}
		
		if(taskArchiveTime!=null && !taskArchiveTime.equals("")){
			String archivetime = taskArchiveTime.replace("/", "-").split(" ")[0];
			sql += " and t.archive_time like '%"+archivetime+"%'";
		}
		if(taskActualEndTime!=null && !taskActualEndTime.equals("")){
			String actualtime = taskActualEndTime.replace("/", "-").split(" ")[0];
			sql += " and tp.actual_end_time like '%"+actualtime+"%'";
		}
		
		if(isArchive != null && !"".equals(isArchive) && !"全部".equals(isArchive)){
			String is_archive = isArchive;
			String issave = "0";
			if("是".equals(is_archive)){
				issave="1";
			}else{
				issave="0";
			}
			sql += " and t.issave="+issave;
		}
		
		
		
		if(arg1.getAc() != null &&  arg1.getAc().getRelatedDepartmentCuid() != null &&  arg1.getAc().getRelatedDepartmentCuid().length()!=14 && arg1.getAc().getRelatedDepartmentCuid().length()!=20){
			sql += " and t.related_district_cuid like '%"+arg1.getAc().getRelatedDepartmentCuid()+"%'";
		}
//		sql += " order by t.issave";
		return sql;
	}
	public IHitchBO getBo() {
		return (IHitchBO) DmmaBoFactory.getInstance().getHitBo();
	}

}
