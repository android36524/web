package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.common.dto.base.BoQueryContext;

public class WireLengthDetailStatGridBO extends GridTemplateProxyBO {

	private IbatisDAO ibatisSdeDAO;
	
	public void setIbatisSdeDAO(IbatisDAO ibatisSdeDAO) {
		this.ibatisSdeDAO = ibatisSdeDAO;
	}

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		/*editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}*/
		String className = editorMeta.getClassName();
		
        //拼装查询条件
		String querySql = getSql(param,className);
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
		/*editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta  : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}*/
		String className = editorMeta.getClassName();
		
        //拼装查询条件
		String countSql = "SELECT COUNT(*) FROM ( " + getSql(param,className) + " ) T";
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

	@Override
	public String getSql(GridCfg param, String className) {
		String sql =" 1 = 1";
    	String cuid = param.getCfgParams().get("cuid");
    	if  (cuid != null){
    		sql += " AND  CUID = '" + cuid + "'";
    	}
    	if(param.getQueryParams() == null || param.getQueryParams().size() == 0){
    		return sql;
    	}
    	
    	Collection<WhereQueryItem>  whereitems =  param.getQueryParams().values();
    	if(whereitems !=null && whereitems.size()>0){
			for(WhereQueryItem item : whereitems){
				if("TYPE".equals(item.getKey()) && "3".equals(item.getValue())){
					className = "DUCT_LINE_LENGTH_DET";
				}else if("CHECK_DATE".equals(item.getKey())){
					String dateValue = item.getValue().substring(0, item.getValue().lastIndexOf("-"));
					String[] dateArr = dateValue.split("-");
					int checkYear = Integer.parseInt(dateArr[0]);
					int chectMonth = Integer.parseInt(dateArr[1]);
					sql += " AND RES.CHECK_YEAR = " + checkYear + " AND RES.CHECK_MONTH = " + chectMonth;
				}else if(item.getSqlValue() != null && !item.getSqlValue().trim().equals("")){
					sql += " AND "+item.getSqlValue();
				}
			}
		}
    	if(!param.getQueryParams().containsKey("PARENT_DISTRICT_CUID")){
    		sql += " AND PARENT_DISTRICT_CUID LIKE '" + param.getAc().getRelatedDistrictCuid() + "%'";
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append("SELECT RES.CUID,")
    	  .append("(SELECT LABEL_CN FROM DISTRICT WHERE CUID = RES.PARENT_DISTRICT_CUID) AS PARENT_DISTRICT_NAME,")
		  .append("DIS.LABEL_CN AS DISTRICT_NAME,")
		  .append("RES.BRANCH_COMPANY,")
		  .append("RES.CHECK_MONTH,")
		  .append("RES.CHECK_YEAR,")
		  .append("RES.DUCT_SEG,")
		  .append("RES.POLEWAY_SEG,")
		  .append("RES.STONEWAY_SEG,RES.UP_LINE_SEG,RES.HANG_WALL_SEG, RES.ALL_SEG")
		  .append(" FROM ")
		  .append(className)
		  .append(" RES LEFT JOIN DISTRICT DIS")
		  .append(" ON RES.RELATED_DISTRICT_CUID = DIS.CUID")
		  .append(" WHERE ");
	       
    	sql = sb.toString() + sql;
    	logger.info("SQL = "+sql);
		return sql;
	}
	
	
}
