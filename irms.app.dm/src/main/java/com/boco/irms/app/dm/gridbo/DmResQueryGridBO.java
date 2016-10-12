package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.component.tpl.grid.pojo.GridTplConfig;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class DmResQueryGridBO extends GridTemplateProxyBO {

	@Override
	public PageResult getGridData(PageQuery queryParam, GridCfg param) {
		String name = this.getTemplateId(param);
		EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
		String sql = editorMeta.getSql();
		String className = editorMeta.getClassName();
		String labelCn=param.getQueryParams().get("LABEL_CN").getValue();
		// 获取列名
		editorColumnMetaMap.clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
		String method = editorMeta.getRemoteMethod("query");

		GridTplConfig gridTpl = getResConfigurer().getGridTpl(name);
		Map<String, String> columnMap = gridTpl.getColumnNames();

		// 从传输服务查询数据
		DataObjectList results = new DataObjectList();
		BoQueryContext querycon = new BoQueryContext((queryParam.getCurPageNum()-1)*queryParam.getPageSize(), queryParam.getPageSize(),false);
		try {
			DboCollection dboCollection = getDatasBysql(className, labelCn,sql,querycon);
			if(null != dboCollection && dboCollection.size()>0) {
			    for(int i=0;i<dboCollection.size();i++) {
			        GenericDO gdo = dboCollection.getAttrField(className, i);
			        results.add(gdo);
			    }
			}
		} catch (Exception e) {
			logger.error("从传输服务查询数据失败,方法名称=" + method, e);
		}

		List<Map> list = new ArrayList<Map>();
		PageResult pageResult = new PageResult(list, 0,queryParam.getCurPageNum(), queryParam.getPageSize());
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				Map map = new HashMap();
				for (String columnName : columnMap.keySet()) {
					Object value = gdo.getAttrValue(columnName);
					map.put(columnName, convertObject(columnName, value));
				}
				list.add(map);
			}
			pageResult = new PageResult(list, results.getCountValue(),queryParam.getCurPageNum(), queryParam.getPageSize());
		}
		return pageResult;
	}
	
	private IDuctManagerBO getDuctManagerBO() {
	    return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	private DboCollection getDatasBysql(String className,String labelCn,String sqlstr,BoQueryContext querycon) {
	    DboCollection points = null;
	    try {
	    	String sql = "";
	    	if(!StringUtils.isEmpty(sqlstr.trim())){
	    		sql =  sqlstr+" and "+ Manhle.AttrName.labelCn + " LIKE '"+ labelCn +"'";
	    	}else{
	    		sql =  Manhle.AttrName.labelCn + " LIKE '"+ labelCn +"'";
	    	}
            //查询设计库数据
            points = getDuctManagerBO().getDataBySql(querycon, sql, className);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	    return points;
	}
    
	@Override
    public PageResult getGridPageInfo(PageQuery queryParam, GridCfg param) {
       
        String method = "IDuctManagerBO.getPointOfSystemCountBySql";
        //获取列名
        String name = this.getTemplateId(param);
        EditorPanelMeta editorMeta = getResConfigurer().getEditorMeta(name);
        String sqlstr = editorMeta.getSql();
        //String method = editorMeta.getRemoteMethod("query");
        String className = editorMeta.getClassName();
        //拼装查询条件
        
        String sql = "SELECT COUNT(*) FROM "+className;
        if(!StringUtils.isEmpty(sqlstr.trim())){
        	sql  = sql+" WHERE "+sqlstr;
        }
        //从传输服务查询数据
        BoQueryContext querycon = new BoQueryContext(queryParam.getCurPageNum(),queryParam.getPageSize(),false);
        querycon.setUserId(param.getAc().getUserCuid());
        Integer totalNum =0;
        try {
            totalNum = (Integer)BoCmdFactory.getInstance().execBoCmd(method, querycon,sql);
        } catch (Exception e) {
            logger.error("从传输服务查询数据失败,方法名称="+method,e);
        }
        PageResult page = new PageResult(null, totalNum, queryParam.getCurPageNum(), queryParam.getPageSize());
        return page;
    }
	   
	}
