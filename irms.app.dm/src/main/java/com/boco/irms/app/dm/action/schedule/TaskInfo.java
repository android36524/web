package com.boco.irms.app.dm.action.schedule;

import com.boco.irms.app.dm.action.schedule.IConfigInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author chengwenfeng
 * 取得前台传入的任务信息
 *
 */
public class TaskInfo implements IConfigInfo<Map<String,String>> {

	public List<Map<String, String>> getTaskInfo(Map<String, String> t) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		list.add(t);
		return list;
	}

}
