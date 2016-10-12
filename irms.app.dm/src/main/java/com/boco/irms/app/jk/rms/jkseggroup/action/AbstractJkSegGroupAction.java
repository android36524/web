package com.boco.irms.app.jk.rms.jkseggroup.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boco.core.bean.SpringContextUtil;
import com.boco.irms.app.jk.base.action.DmBaseAction;
import com.boco.irms.app.jk.rms.jkseggroup.bo.AbstractJkSegGroupBO;
import com.boco.irms.app.jk.service.bo.AbstractJkDeviceBO;
import com.boco.irms.app.utils.CustomEnum;

public abstract class AbstractJkSegGroupAction extends DmBaseAction {
	/**
	 * 
	 * @param request
	 * @param response
	 * @param formMapStr
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public void submit(HttpServletRequest request, HttpServletResponse response, @RequestParam
	String formMapStr) {
		Map<String, Object> formMap = JSON.parseObject(formMapStr, HashMap.class);
		String segGroupCuid = MapUtils.getString(formMap, "segGroupCuid");
		String result = "true";
		try {
			result = this.getJkSegGroupBO().validator(segGroupCuid);
			if (result.equals("true")) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("CUID", MapUtils.getString(formMap, "segGroupCuid"));
				paramMap.put("IS_APPLY", "0");
				paramMap.put("STATUS", String.valueOf(CustomEnum.DMProjectState._approval));
				this.getJkSegGroupBO().submit(paramMap, "T_ATTEMP_SEG_GROUP");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		this.write(response, result);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param formMapStr
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "migrateData", method = RequestMethod.POST)
	public void migrateData(HttpServletRequest request, HttpServletResponse response, @RequestParam
	String formMapStr) {
		Map<String, Object> formMap = JSON.parseObject(formMapStr, HashMap.class);
		String segGroupCuid = MapUtils.getString(formMap, "segGroupCuid");
		String result = "true";
		try {
			List<String> segGroupCuidList = new ArrayList<String>();
			segGroupCuidList.add(segGroupCuid);
			result = this.getAbstractJkDeviceBO().archived(segGroupCuidList, "");
			if (result.equals("true")) {
				boolean flag = MapUtils.getBoolean(formMap, "flag");
				result = this.getJkSegGroupBO().archived(segGroupCuid, "T_ATTEMP_SEG_GROUP", "T_SF_SEG_GROUP", flag);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		this.write(response, result);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param formMapStr
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "validator", method = RequestMethod.POST)
	public void validator(HttpServletRequest request, HttpServletResponse response, @RequestParam
	String formMapStr) {
		String result = "true";
		Map<String, Object> formMap = JSON.parseObject(formMapStr, HashMap.class);
		String serviceListJson = MapUtils.getString(formMap, "serviceList");
		List<String> serviceList = JSONObject.parseArray(serviceListJson, String.class);
		try {
			Map<String, Object> map = this.getAbstractJkDeviceBO().getDeviceInfo(serviceList, "", "archived");
			if (CollectionUtils.isEmpty(map)) {
				result = "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		this.write(response, result);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param formMapStr
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "reject", method = RequestMethod.POST)
	public void reject(HttpServletRequest request, HttpServletResponse response, @RequestParam
	String formMapStr) {
		Map<String, Object> formMap = JSON.parseObject(formMapStr, HashMap.class);
		String result = "true";
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("CUID", MapUtils.getString(formMap, "segGroupCuid"));
			paramMap.put("IS_APPLY", "0");
			paramMap.put("STATUS", String.valueOf(CustomEnum.DMProjectState._design));
			this.getJkSegGroupBO().submit(paramMap, "T_ATTEMP_SEG_GROUP");
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		this.write(response, result);
	}

	protected abstract AbstractJkSegGroupBO getJkSegGroupBO();

	protected AbstractJkDeviceBO getAbstractJkDeviceBO() {
		return (AbstractJkDeviceBO) SpringContextUtil.getBean("JkDeviceSimulateAccessBO");
	}
}