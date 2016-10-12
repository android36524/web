package com.boco.irms.app.dm.action.schedule;

import java.beans.XMLDecoder;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
/**
 * @description 光缆统计阀值
 * @author liuchao
 * @verison 
 * @date 2015年1月28日 上午8:51:23
 */
public class FaultThreshold {
	
	private static FaultThreshold ft;
	//默认的阈值 
	private Map<String,Double> defaultThreshold = new HashMap<String,Double>();
	//各个区域对应的阈值
	private Map<String,Map<String,Double>> disThreshold = new HashMap<String,Map<String,Double>>();
	
	public static FaultThreshold getInstance(){
		if(ft == null){
			return new FaultThreshold();
		}
		return ft;
	}
	
	public static void loadCfgFile(InputStream stream){
		ft = (FaultThreshold) new XMLDecoder(stream).readObject();
	}

	public Map<String, Double> getDefaultThreshold() {
		return defaultThreshold;
	}

	public void setDefaultThreshold(Map<String, Double> defaultThreshold) {
		this.defaultThreshold  =  defaultThreshold;
	}

	public Map<String, Map<String, Double>> getDisThreshold() {
		return disThreshold;
	}

	public void setDisThreshold(Map<String, Map<String, Double>> disThreshold) {
		this.disThreshold  =  disThreshold;
	}
	
}
