package com.boco.irms.app.dm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceTestServerAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public void sayHello(String str){
		logger.info("调用成功！信息内容："+str);
	}

}
