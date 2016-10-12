package com.boco.irms.app.jk.rms.jkseggroup.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boco.core.bean.SpringContextUtil;
import com.boco.irms.app.jk.rms.jkseggroup.bo.AbstractJkSegGroupBO;

@Controller
@RequestMapping(value = "/JkSnSegGroupAction")
public class JkSnSegGroupAction extends AbstractJkSegGroupAction {
	protected AbstractJkSegGroupBO getJkSegGroupBO() {
		return (AbstractJkSegGroupBO) SpringContextUtil.getBean("JkSnSegGroupBO");
	}
}