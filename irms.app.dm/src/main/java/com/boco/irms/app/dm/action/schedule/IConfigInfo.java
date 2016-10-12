package com.boco.irms.app.dm.action.schedule;

import java.util.List;
import java.util.Map;

/**
 * @author chengwenfeng
 * 取得配置文件或其他数据源的数据
 * @param <T>
 */
public interface IConfigInfo <T>{

	public List<Map<String,String>> getTaskInfo(T t);
	
}
