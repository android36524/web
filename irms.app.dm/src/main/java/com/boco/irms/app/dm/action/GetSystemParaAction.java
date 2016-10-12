package com.boco.irms.app.dm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.SystemPara;
import com.boco.transnms.common.dto.base.BoActionContext;

/**
 * 获取系统参数值
 * @author gaoxf
 *
 */
public class GetSystemParaAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public String getValueByParaName(String paraClassName,String paraName){
		String paraValue = "";
		try {
			SystemPara para = (SystemPara)BoCmdFactory.getInstance().execBoCmd("ISystemParaBO.getSystemPara", new BoActionContext(), paraClassName,paraName);
			paraValue = para == null? "":para.getParaValue();
		} catch (Exception e) {
			logger.error("查询参数失败", e);
		}
		return paraValue;
	}
}
