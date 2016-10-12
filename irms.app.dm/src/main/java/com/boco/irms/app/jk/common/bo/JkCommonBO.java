package com.boco.irms.app.jk.common.bo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.utils.lang.Assert;

public class JkCommonBO {
	public Logger logger = LoggerFactory.getLogger(this.getClass());

	public IbatisDAO iBatisSynResDAO;

	public IbatisDAO iBatisDesignerDAO;

	public IbatisDAO iBatisTnmsDAO;

	public IbatisDAO iBatisPonDAO;

	public IbatisDAO iBatisSdeDesignDAO;

	public IbatisDAO iBatisSdeDAO;

	/**
	 * 
	 * @param resultMap
	 * @param tableName
	 * @return record Record
	 */
	protected Record buildRecord(Map<String, Object> resultMap, String tableName) {
		Record record = new Record(tableName);
		Iterator<String> iterator = resultMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.equals("bmClassId")) {
				Object object = resultMap.get(key);
				if (object != null) {
					record.addColValue(key, object);
				}
			}
		}
		return record;
	}

	/**
	 * 
	 * @param resultList
	 * @param tableName
	 */
	public void insertDynamicTableBatch(List<Map<String, Object>> resultList, String tableName) {

	}

	/**
	 * 
	 * @param condition
	 * @param tableName
	 * @return sql String
	 */
	protected String getSql(String condition, String tableName) {
		String sql = "SELECT * FROM " + tableName;
		if (!StringUtils.isEmpty(condition)) {
			sql += (" WHERE " + condition);
		}
		return sql;
	}

	/**
	 * 
	 * @param paramMap Map<String, Object>
	 * @param tableName
	 * @return map Map<String, Record>
	 */
	protected Map<String, Record> buildRecordMap(Map<String, Object> paramMap, String tableName) {
		Assert.notEmpty(paramMap, "参数集合不能为空.");
		Assert.hasLength(tableName, "表名不能为空.");
		Record paramRecord = new Record(tableName);
		Record pkRecord = new Record(tableName);
		Iterator<String> iterator = paramMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			if (!key.equals("bmClassId")) {
				Object object = paramMap.get(key);
				if (object != null) {
					paramRecord.addColValue(key, object);
				}
			}
			pkRecord.addColValue("CUID", MapUtils.getString(paramMap, "CUID"));
		}

		Map<String, Record> map = new HashMap<String, Record>();
		map.put("paramRecord", paramRecord);
		map.put("pkRecord", pkRecord);
		return map;
	}

	public IbatisDAO getiBatisSynResDAO() {
		return iBatisSynResDAO;
	}

	public void setiBatisSynResDAO(IbatisDAO iBatisSynResDAO) {
		this.iBatisSynResDAO = iBatisSynResDAO;
	}

	public IbatisDAO getiBatisDesignerDAO() {
		return iBatisDesignerDAO;
	}

	public void setiBatisDesignerDAO(IbatisDAO iBatisDesignerDAO) {
		this.iBatisDesignerDAO = iBatisDesignerDAO;
	}

	public IbatisDAO getiBatisTnmsDAO() {
		return iBatisTnmsDAO;
	}

	public void setiBatisTnmsDAO(IbatisDAO iBatisTnmsDAO) {
		this.iBatisTnmsDAO = iBatisTnmsDAO;
	}

	public IbatisDAO getiBatisPonDAO() {
		return iBatisPonDAO;
	}

	public void setiBatisPonDAO(IbatisDAO iBatisPonDAO) {
		this.iBatisPonDAO = iBatisPonDAO;
	}

	public IbatisDAO getiBatisSdeDAO() {
		return iBatisSdeDAO;
	}

	public void setiBatisSdeDAO(IbatisDAO iBatisSdeDAO) {
		this.iBatisSdeDAO = iBatisSdeDAO;
	}

	public IbatisDAO getiBatisSdeDesignDAO() {
		return iBatisSdeDesignDAO;
	}

	public void setiBatisSdeDesignDAO(IbatisDAO iBatisSdeDesignDAO) {
		this.iBatisSdeDesignDAO = iBatisSdeDesignDAO;
	}
}