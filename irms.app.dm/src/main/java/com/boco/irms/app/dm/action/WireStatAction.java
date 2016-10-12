package com.boco.irms.app.dm.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.boco.irms.app.dm.action.schedule.WireStatTask;

public class WireStatAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Map<String,Object> checkFaultSeg(String faultSegCuid,boolean isWire,boolean isSub){
		if(StringUtils.isNotEmpty(faultSegCuid)){
			return new WireStatTask().doCheckSeg(faultSegCuid, isWire, isSub);
		}
		return null;
	}
	
}
