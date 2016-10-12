package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.common.dto.base.BoQueryContext;

public class WireLengthDecrementStatGridBO extends GridTemplateProxyBO {

	private IbatisDAO ibatisSdeDAO;
	
	public void setIbatisSdeDAO(IbatisDAO ibatisSdeDAO) {
		this.ibatisSdeDAO = ibatisSdeDAO;
	}

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName();
		
		//拼装查询条件
		String sqlTemplate = editorMeta.getSql().trim();
		String querySql = getSql(param,sqlTemplate).trim();
		//从空间库查询数据
		List<Map> list = new ArrayList<Map>();
		try {
			list = ibatisSdeDAO.querySql(querySql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult(list,1,1,1);
		return pageResult;
	}

	@Override
	public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String className = editorMeta.getClassName();
		
        //拼装查询条件
		String sqlTemplate = editorMeta.getSql().trim();
		String countSql = "SELECT COUNT(*) FROM ( " + getSql(param,sqlTemplate) + " ) T".trim();
		logger.info("countSql:"+countSql);
		BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
		querycon.setUserId(param.getAc().getUserCuid());
		Integer totalNum = 0;
		try {
			totalNum = ibatisSdeDAO.calculate(countSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult(null,totalNum,queryParam.getCurPageNum(), queryParam.getPageSize());
		return pageResult;
	}

	
	public String getSql(GridCfg param,String sqlTemplate){
		Map<String,WhereQueryItem> queryParams = param.getQueryParams();
		
		if(!queryParams.containsKey("RELATED_DISTRICT_CUID")){
			WhereQueryItem item = new WhereQueryItem("RELATED_DISTRICT_CUID",param.getAc().getRelatedDistrictCuid()+"%");
			item.setRelation("LIKE");
			item.setType("string");
			queryParams.put("RELATED_DISTRICT_CUID", item);
		}
		
		if(!queryParams.containsKey("CHECK_MONTH")){
			WhereQueryItem item = new WhereQueryItem("CHECK_MONTH", "1,12");
			item.setRelation("BETWEEN");
			item.setType("number");
			queryParams.put("CHECK_MONTH", item);
		}
		
		Collection<WhereQueryItem>  whereitems =  queryParams.values();
		if(whereitems != null && whereitems.size() > 0){
			for(WhereQueryItem item : whereitems){
					sqlTemplate = sqlTemplate.replaceAll("\\$"+item.getKey()+"\\$", ""+item.getSqlValue());
			}
		}
		return sqlTemplate;
	}
	
	
}
