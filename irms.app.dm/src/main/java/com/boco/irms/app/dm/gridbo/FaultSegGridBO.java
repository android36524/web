package com.boco.irms.app.dm.gridbo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.common.dto.base.BoQueryContext;

public class FaultSegGridBO extends GridTemplateProxyBO {

	private IbatisDAO ibatisSdeDAO;
	
	public void setIbatisSdeDAO(IbatisDAO ibatisSdeDAO) {
		this.ibatisSdeDAO = ibatisSdeDAO;
	}

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		
		//拼装查询条件
		String[] sqlTemplate = editorMeta.getSql().trim().split("@");
		String querySql = getSql(param,sqlTemplate);
		logger.info("querySql:"+querySql);
		
		String cuid = param.getCfgParams().get("cuid");
		int resultType = getResultType(cuid);
		
		//从空间库查询数据
		List<Map> list = new ArrayList<Map>();
		try {
			list = ibatisSdeDAO.querySql(querySql);
			if(resultType != 0){
				addResultType(list,resultType);
			}
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
		
        //拼装查询条件
		String[] sqlTemplate = editorMeta.getSql().trim().split("@");
		String countSql = "SELECT COUNT(*) FROM ( " + getSql(param,sqlTemplate) + " ) T";
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

	
	private String getSql(GridCfg param,String[] sqlTemplate){
		String cuid = param.getCfgParams().get("cuid");
		String className = cuid.split("-")[0];
		String whereSql = " WHERE RELATED_" + className + "='" + cuid + "'";
		if("WIRE_LENGTH_STAT".equals(className)){
			return sqlTemplate[0] + whereSql;
		}else if("DUCTLINE_LENGTH_STAT".equals(className)){
			return sqlTemplate[1] + whereSql;
		}
		return null;
	}
	
	/**
	 * 添加result_type，区分正常问题段与代维问题段
	 * @param list	问题段集合
	 * @param resultType 1.正常  2.代维
	 * @return
	 */
	private List<Map<String,Object>> addResultType(List list,int resultType){
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				if(list.get(i) instanceof HashMap){
					Map map = (HashMap)list.get(i);
					map.put("RESULT_TYPE", resultType);
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取结果类型
	 * @param cuid 
	 * @return
	 */
	private int getResultType(String cuid){
		String tableName = cuid.split("-")[0];
		String sql = "SELECT RESULT_TYPE FROM " + tableName + " WHERE CUID = '" + cuid + "'";
		try {
			List list = ibatisSdeDAO.querySql(sql);
			if(list != null && list.size() > 0){
				Map map = (Map) list.get(0);
				BigDecimal resultType = (BigDecimal) map.get("RESULT_TYPE");
				return resultType.intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
