package com.boco.irms.app.dm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.url.SystemContextUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.ServiceParam;
import com.boco.transnms.common.dto.base.BoActionContext;

/**
 * 获取对应服务的url
 * @author lenovo
 *
 */
public class GetServiceParamAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public String getUrlByServerName(String serverName){
		String serverUrl = "";
		try {
			String isDebug = SystemContextUtils.getInstance().getSystemContext("DEBUG");
			if(isDebug != null && !"".equals(isDebug) && isDebug.toLowerCase().equals("true")){
				serverUrl = SystemContextUtils.getInstance().getSystemContext(serverName);
				return serverUrl;
			}
		
			ServiceParam para = (ServiceParam)BoCmdFactory.getInstance().execBoCmd("IServiceParamBO.getServiceParamByTypeAndName", new BoActionContext(), new Long(3),serverName);
			serverUrl = para.getServiceUrl();
		} catch (Exception e) {
			logger.error("查询参数失败", e);
		}
		logger.info("ServerUrl="+serverUrl);
		return serverUrl;
	}
}
