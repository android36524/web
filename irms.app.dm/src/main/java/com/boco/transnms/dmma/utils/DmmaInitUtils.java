package com.boco.transnms.dmma.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.debug.LogHome;
import com.boco.transnms.server.bo.base.BOHome;

public class DmmaInitUtils {
	private String uurl;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public String getUurl() {
		return uurl;
	}

	public void setUurl(String uurl) {
		this.uurl = uurl;
	}

	/**
	 * 初始化Xrpc客户端，可以通过Xrpc调用服务器端接口
	 * 
	 * @throws Exception
	 */
	public void initXrpcClient() throws Exception {
		try {
			loadServerURL();
			BOHome.initRemoteBO();
		} catch (Exception e) {
			LogHome.getLog().error("初始化服务端BO失败", e);
		}
	}



	/**
	 * 读取服务器地址
	 * 
	 * @throws IOException
	 */
	private void loadServerURL() throws IOException {
		BOHome.serverUrlMap.put("DMMA_SERVER", uurl);
	}
}