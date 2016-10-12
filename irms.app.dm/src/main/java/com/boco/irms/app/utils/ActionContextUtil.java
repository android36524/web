package com.boco.irms.app.utils;

import com.boco.transnms.common.dto.base.BoActionContext;

public class ActionContextUtil {
	private static ThreadLocal<BoActionContext> contextLocal;

	public static BoActionContext getActionContext() {
		return contextLocal.get();
	}

	public static ThreadLocal<BoActionContext> getContextLocal() {
		return contextLocal;
	}

	public static void setContextLocal(ThreadLocal<BoActionContext> contextLocal) {
		ActionContextUtil.contextLocal = contextLocal;
	}
}
