package com.boco.transnms.dmma.hitchtask;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boco.core.bean.SpringContextUtil;

public class HitchTaskAction {
	
	public Map viewHitchPicture(HttpServletRequest request,String data) {
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.viewHitchPicture(request, data);
	}
	public Map doDelete(HttpServletRequest request,String data) {
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.doDelete(request, data);
	}
	public Map saveResource(HttpServletRequest request,String data) {
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.saveResource(request, data);
	}
	public Map saveTask(HttpServletRequest request,String data) {
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.saveTask(request, data);
	}
	public Map queryInterrupt(HttpServletRequest request,String data) {
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.queryInterrupt(request, data);
	}
	public  Map  getFirstWireSegInfo(HttpServletRequest request,String data){
			HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
			return hitchTaskActionBO.getFirstWireSegInfo(request, data);
	}
	public  Map  initWireSegEnum(HttpServletRequest request,String data){
		HitchTaskActionBO hitchTaskActionBO = (HitchTaskActionBO) SpringContextUtil.getBean("HitchTaskActionBO");
		return hitchTaskActionBO.initWireSegEnum(request, data);
	}
}
