package com.boco.transnms.dmma.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





























import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;

import com.boco.component.grid.bo.IGridBO;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.pojo.GridMeta;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.client.model.base.IBoCommand;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoCmdContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.dmma.utils.DmmaEnumChangeIntf;
import com.boco.transnms.dmma.utils.DmmaUtils;
import com.boco.transnms.server.bo.base.BOHome;
import com.boco.transnms.server.bo.base.SeviceException;



public class TaskBO extends XmlTemplateGridBO  implements DmmaCommonIntf<ITaskBO>{
	
	
	protected String getSQL(String taskName,String related_district,
			String is_cycle,String is_danger,String frequencestr
			,String monthstr,String related_device_code,String districtCuid,String tasktype) {
		String sql = 
				"select * from (select distinct t.cuid,t.label_cn,(select label_cn from district where cuid = t.related_district_cuid) as related_district_cuid,t.task_type,"
			+ "t.is_cycle,t.frequence_type,t.frequence,t.duration,t.task_time,t.remark,t.sendmonth "
			+ "from pda_p_task t,pda_task p,task_to_pda tp where t.cuid=p.related_p_cuid "
			+ "and p.cuid=tp.related_task_cuid ";
		if (StringUtils.isNotEmpty(StringUtils.trim(tasktype))) {
			sql += "  and t.task_type = '%" + tasktype + "%'";
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(taskName))) {
			sql += "  and t.label_cn like '%" + taskName + "%'";
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(related_district))) {
			sql += "   and t.related_district_cuid = '"+related_district+"'";
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(is_cycle)) && !StringUtils.equalsIgnoreCase(is_cycle, "全部")){
			sql += " and t.is_cycle="+is_cycle;
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(is_danger)) && !StringUtils.equalsIgnoreCase(is_danger, "全部")){
			if("是".equals(is_danger)){
				sql += " and (select count(hd.cuid) hdcount from hidden_danger hd where hd.related_task_cuid=p.cuid)>0";
			}else{
				sql += " and (select count(hd.cuid) hdcount from hidden_danger hd where hd.related_task_cuid=p.cuid)=0";
			}
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(frequencestr)) && !StringUtils.equalsIgnoreCase(frequencestr, "全部")){
			sql +=" and t.frequence_type="+frequencestr;
		}
		
		if(StringUtils.isNotEmpty(StringUtils.trim(monthstr)) && !StringUtils.equalsIgnoreCase(monthstr, "全部") ){
			sql +=" and t.task_time like '%"+monthstr+"%'";
		}
		
		if (StringUtils.isNotEmpty(StringUtils.trim(related_device_code))) {
			sql += "   and  tp.related_device_code = '"+related_device_code+"'";
		}
		sql+=" union ";
		sql += " select distinct pt.cuid,pt.label_cn,pt.related_district_cuid,pt.task_type,"
			+ "pt.is_cycle,pt.frequence_type,pt.frequence,pt.duration,pt.task_time,pt.remark,pt.sendmonth "
			+ "from pda_p_task pt,temp_task_to_pda ttp where pt.cuid=ttp.related_task_cuid ";
	
		if (StringUtils.isNotEmpty(StringUtils.trim(tasktype))) {
			sql += "  and pt.task_type = '%" + tasktype + "%'";
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(taskName))) {
			sql += "  and pt.label_cn like '%" + taskName + "%'";
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(related_district))) {
			sql += "   and  pt.related_district_cuid '"+related_district+"'";
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(is_cycle)) && !StringUtils.equalsIgnoreCase(is_cycle, "全部") ){
			sql += " and pt.is_cycle="+is_cycle;
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(related_device_code))) {
			sql += "   and  ttp.related_device_code = '"+related_device_code+"'";
		}
		
		if(StringUtils.isNotEmpty(StringUtils.trim(frequencestr))  && !StringUtils.equalsIgnoreCase(frequencestr, "全部")){
			sql +=" and pt.frequence_type="+frequencestr;
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(monthstr)) && !StringUtils.equalsIgnoreCase(monthstr, "全部")){
			sql +=" and pt.task_time like '%"+monthstr+"%'";
		}
		sql+=") tep";
		if(districtCuid != null && districtCuid.length()!=14 && districtCuid.length()!=20){
			sql += "  where tep.related_district_cuid like  '%"+districtCuid+"%'";
		}
		return sql;
	}
	
	private String getSubTaskSqlByPTaskCuid(String pTaskCuid){
		String sql = "select pt.*,(select label_cn from district where cuid = pt.related_district_cuid) as related_district_cuid,"
				+ "tp.task_state as pda_task_state,tp.accept_time as pda_accept_time," +
		"tp.action_time as pda_action_time, tp.actual_end_time,pd.user_name as pda_user_name " +
		"from pda_task pt, task_to_pda tp, pda_p_task ppt, pda_device pd " +
		"where tp.related_task_cuid = pt.cuid " +
		"and tp.related_device_code = pd.device_code " +
		"and pt.related_p_cuid = ppt.cuid and ppt.cuid = '"+pTaskCuid+"' " +
		"order by pt.task_time";
	   return sql;
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		ITaskBO taskbo = getBo(); 
		DataObjectList data = new DataObjectList();
		if(StringUtils.equalsIgnoreCase(arg1.getCustType(),"bottom")){
			if(arg1.getCfgParams() != null && arg1.getCfgParams().get("TASK_CUID") != null){
				data= taskbo.querySubTask(getSubTaskSqlByPTaskCuid(arg1.getCfgParams().get("TASK_CUID")));
				DataObjectList data2 = new DataObjectList();
				for(Object  dataObj:data){
					GenericDO dataMap = (GenericDO) dataObj;
					dataMap.getAllAttr().put("PTASKNAME", arg1.getCfgParams().get("TASK_NAME"));
					data2.add(dataMap);
				}
			}
		
		}else if(StringUtils.equalsIgnoreCase(arg1.getCustType(),"top")){
			Map cfgData = arg1.getCfgParams();
			
			if(cfgData == null){
				data = taskbo.queryTask(getSQL("", "", "", "", "", "",  "",arg1.getAc().getRelatedDistrictCuid(), ""), arg0.getCurPageNum(),
						arg0.getPageSize());
				data.setCountValue(taskbo.getResultCount(getCountSql("", "", "", "", "", "",  "",arg1.getAc().getRelatedDistrictCuid())));
			}else{
				data = taskbo.queryTask(getSQL((String)cfgData.get("LABEL_CN"), (String)cfgData.get("RELATED_DISTRICT_CUID"), (String)cfgData.get("IS_CYCLE"), (String)cfgData.get("ISDANGER"), (String)cfgData.get("FREQUENCE_TYPE"), (String)cfgData.get("MONTHITEMS"),  (String)cfgData.get("RELATED_PDA"),arg1.getAc().getRelatedDistrictCuid(), ""), arg0.getCurPageNum(),
						arg0.getPageSize());
				data.setCountValue(taskbo.getResultCount(getCountSql((String)cfgData.get("LABEL_CN"), (String)cfgData.get("RELATED_DISTRICT_CUID"), (String)cfgData.get("IS_CYCLE"), (String)cfgData.get("ISDANGER"), (String)cfgData.get("FREQUENCE_TYPE"), (String)cfgData.get("MONTHITEMS"),  (String)cfgData.get("RELATED_PDA"),arg1.getAc().getRelatedDistrictCuid())));
			}
		}else{
			PageResult pageResult = new PageResult<Map>(new ArrayList<Map>() , 0, 1, 1);
			return pageResult;
		}
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode(),new DmmaEnumChangeIntf() {
			@Override
			public void changEnumValue(Object key, Object value, Map element) {
				if(StringUtils.equalsIgnoreCase((String) key, "IS_CYCLE")){
					if(DuctEnum.CYCLE_ENUM.getAllEnum().containsKey(value)){
						element.put("IS_CYCLE", DuctEnum.CYCLE_ENUM.getAllEnum().get(value));
					};
				};
				if(StringUtils.equalsIgnoreCase((String) key, "TASK_STATE")){
					if(DuctEnum.TASK_STATE_ENUM.getAllEnum().containsKey(value)){
						element.put("TASK_STATE", DuctEnum.TASK_STATE_ENUM.getAllEnum().get(value));
					}
				};
				if(StringUtils.equalsIgnoreCase((String) key, "PDA_TASK_STATE")){
					if(DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().containsKey(value)){
						element.put("PDA_TASK_STATE", DuctEnum.PDA_TASK_STATE_ENUM.getAllEnum().get(value));
					}
				}
				
			}
		} );
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	private String getCountSql(String taskName,String related_district,
			String is_cycle,String is_danger,String frequencestr
			,String monthstr,String related_device_code,String districtCuid) {

		String sql = "select count(*) countvalue from (select distinct t.cuid,t.label_cn,t.related_district_cuid,t.task_type,"
				+ "t.is_cycle,t.frequence_type,t.frequence,t.duration,t.task_time,t.remark "
				+ "from pda_p_task t,pda_task p,task_to_pda tp where t.cuid=p.related_p_cuid "
				+ "and p.cuid=tp.related_task_cuid ";
	
			if (StringUtils.isNotEmpty(StringUtils.trim(taskName))) {
				sql += "  and t.label_cn like '%" + taskName + "%'";
			}
			if (StringUtils.isNotEmpty(StringUtils.trim(related_district))) {
				sql += "   and t.related_district_cuid = '"+related_district+"'";
			}
			if(StringUtils.isNotEmpty(StringUtils.trim(is_cycle)) && !StringUtils.equalsIgnoreCase(is_cycle, "全部")){
				sql += " and t.is_cycle="+is_cycle;
			}
			if(StringUtils.isNotEmpty(StringUtils.trim(is_danger)) && !StringUtils.equalsIgnoreCase(is_danger, "全部")){
				if("是".equals(is_danger)){
					sql += " and (select count(hd.cuid) hdcount from hidden_danger hd where hd.related_task_cuid=p.cuid)>0";
				}else{
					sql += " and (select count(hd.cuid) hdcount from hidden_danger hd where hd.related_task_cuid=p.cuid)=0";
				}
			}
			if(StringUtils.isNotEmpty(StringUtils.trim(frequencestr)) && !StringUtils.equalsIgnoreCase(frequencestr, "全部")){
				sql +=" and t.frequence_type="+frequencestr;
			}
			
			if(StringUtils.isNotEmpty(StringUtils.trim(monthstr)) && !StringUtils.equalsIgnoreCase(monthstr, "全部") ){
				sql +=" and t.task_time like '%"+monthstr+"%'";
			}
			
			if (StringUtils.isNotEmpty(StringUtils.trim(related_device_code))) {
				sql += "   and  tp.related_device_code = '"+related_device_code+"'";
			}
		sql+=" union ";
		sql += " select distinct pt.cuid,pt.label_cn,pt.related_district_cuid,pt.task_type,"
			+ "pt.is_cycle,pt.frequence_type,pt.frequence,pt.duration,pt.task_time,pt.remark "
			+ "from pda_p_task pt,temp_task_to_pda ttp where pt.cuid=ttp.related_task_cuid ";


		if (StringUtils.isNotEmpty(StringUtils.trim(taskName))) {
			sql += "  and pt.label_cn like '%" + taskName + "%'";
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(related_district))) {
			sql += "   and  pt.related_district_cuid '"+related_district+"'";
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(is_cycle)) && !StringUtils.equalsIgnoreCase(is_cycle, "全部") ){
			sql += " and pt.is_cycle="+is_cycle;
		}
		if (StringUtils.isNotEmpty(StringUtils.trim(related_device_code))) {
			sql += "   and  ttp.related_device_code = '"+related_device_code+"'";
		}
		
		if(StringUtils.isNotEmpty(StringUtils.trim(frequencestr))  && !StringUtils.equalsIgnoreCase(frequencestr, "全部")){
			sql +=" and pt.frequence_type="+frequencestr;
		}
		if(StringUtils.isNotEmpty(StringUtils.trim(monthstr)) && !StringUtils.equalsIgnoreCase(monthstr, "全部")){
			sql +=" and pt.task_time like '%"+monthstr+"%'";
		}
		sql+=") tep";
		if(districtCuid != null && districtCuid.length()!=14 && districtCuid.length()!=20){
			sql += "  where tep.related_district_cuid like  '%"+districtCuid+"%'";
		}
		return sql;
	}
	@Override
	public GridMeta getGridMeta(GridCfg arg0) {
		GridMeta gridMeta = super.getGridMeta(arg0);
		arg0.getCfgParams().put("templateId", "DMMA.TASK.BOTTOM.GRID");
		gridMeta.setdColumns(super.getGridMeta(arg0).getColumns());
		return gridMeta;
	}

	@Override
	public PageResult getGridPageInfo(PageQuery arg0, GridCfg arg1) {
		return super.getGridPageInfo(arg0, arg1);
	}


	@Override
	public ITaskBO getBo() {
		return (ITaskBO) DmmaBoFactory.getInstance().getBo();
	}

}
