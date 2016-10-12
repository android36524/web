package com.boco.transnms.dmma.wiresegbatch;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boco.core.bean.SpringContextUtil;

public class WireSegBatchAction {
 
	public Map WireSegUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.WireSegUpdate(request,data);
	}
	
	public Map PolewayBranchUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.PolewayBranchUpdate(request,data);
	}
	
	public Map DuctBranchUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.DuctBranchUpdate(request,data);
	}
	
	public Map StonewayBranchUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.StonewayBranchUpdate(request,data);
	}
	
	public Map UpLineUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.UpLineUpdate(request,data);
	}
	
	public Map HangWallUpdate(HttpServletRequest request,String data) {
		WireSegBatchActionBO wireSegBatchActionBO = (WireSegBatchActionBO) SpringContextUtil.getBean("WireSegBatchActionBO");
		return wireSegBatchActionBO.HangWallUpdate(request,data);
	}
}
