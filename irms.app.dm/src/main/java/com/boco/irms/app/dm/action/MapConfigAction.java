package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.core.spring.SysProperty;

/**
 * @author zhl
 * 读取图层配置
 */
public class MapConfigAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getMapConfig() throws Exception
	{
		Map config = new HashMap();
		Map map = new HashMap();
		
		String initCenterLat = SysProperty.getInstance().getValue("map.initCenter.lat");
		String initCenterLng = SysProperty.getInstance().getValue("map.initCenter.lng");
		//全图起止经纬度
		String viewEntireOrigLat = SysProperty.getInstance().getValue("map.viewEntire.orig_lat");
		String viewEntireOrigLng = SysProperty.getInstance().getValue("map.viewEntire.orig_lng");
		String viewEntireDestLat = SysProperty.getInstance().getValue("map.viewEntire.dest_lat");
		String viewEntireDestLng = SysProperty.getInstance().getValue("map.viewEntire.dest_lng");
		
		
		String initZoomLevel = SysProperty.getInstance().getValue("map.initZoomLevel");
		String geometryService = SysProperty.getInstance().getValue("map.geometryService");
		String offsetX = SysProperty.getInstance().getValue("map.offsetX");
		String offsetY = SysProperty.getInstance().getValue("map.offsetY");
		if (offsetX==null){offsetX="0";};
		if (offsetY==null){offsetY="0";};
		
		String exportOffsetX = SysProperty.getInstance().getValue("map.exportOffsetX");
		String exportOffsetY = SysProperty.getInstance().getValue("map.exportOffsetY");
		if (exportOffsetX==null){exportOffsetX="0";};
		if (exportOffsetY==null){exportOffsetY="0";};
		
		Map initCenter = new HashMap();
		initCenter.put("lat", initCenterLat);
		initCenter.put("lng", initCenterLng);
		
		Map viewEntire = new HashMap();
		viewEntire.put("origLat", viewEntireOrigLat);
		viewEntire.put("origLng", viewEntireOrigLng);
		viewEntire.put("destLat", viewEntireDestLat);
		viewEntire.put("destLng", viewEntireDestLng);
		
		map.put("viewEntire", viewEntire);
		map.put("initCenter", initCenter);
		map.put("initZoomLevel", initZoomLevel);
		map.put("geometryService", geometryService);
		map.put("offsetX", offsetX);
		map.put("offsetY", offsetY);
		map.put("exportOffsetX", exportOffsetX);
		map.put("exportOffsetY", exportOffsetY);
		//底图数
		String basemapSize = SysProperty.getInstance().getValue("map.basemap.size");
		Integer bSize = Integer.valueOf(basemapSize);
		List baseMapList = new ArrayList();
		for(int i = 0;i < bSize; i++)
		{
			Map baseMap = new HashMap();
			baseMap.put("label", SysProperty.getInstance().getValue("map.basemaps["+i+"].label"));
			baseMap.put("type", SysProperty.getInstance().getValue("map.basemaps["+i+"].type"));
			baseMap.put("visible", SysProperty.getInstance().getValue("map.basemaps["+i+"].visible"));
			baseMap.put("alpha", SysProperty.getInstance().getValue("map.basemaps["+i+"].alpha"));
			baseMap.put("url", SysProperty.getInstance().getValue("map.basemaps["+i+"].url"));
			baseMap.put("searchFields", SysProperty.getInstance().getValue("map.basemaps["+i+"].searchFields"));
			baseMap.put("searchLayers", SysProperty.getInstance().getValue("map.basemaps["+i+"].searchLayers"));
			baseMap.put("cadBackLayerIds", SysProperty.getInstance().getValue("map.basemaps["+i+"].cadBackLayerIds"));//cad 导出的背景图层
			//湖北的背景图切片服务和矢量图层服务是分开的，所以在此增加一个参数，
			baseMap.put("identifyUrl", SysProperty.getInstance().getValue("map.basemaps["+i+"].identifyUrl"));//cad 导出的背景图层
			baseMapList.add(baseMap);
		}		
		map.put("basemaps", baseMapList);
		
		//动态图层数
		String reslayerSize = SysProperty.getInstance().getValue("map.reslayer.size");
		Integer rSize = Integer.valueOf(reslayerSize);
		List reslayerList = new ArrayList();
		for(int i = 0 ; i < rSize ; i++)
		{
			Map resLayer = new HashMap();
			resLayer.put("id", SysProperty.getInstance().getValue("map.reslayers["+i+"].id"));
			resLayer.put("label", SysProperty.getInstance().getValue("map.reslayers["+i+"].label"));
			resLayer.put("type", SysProperty.getInstance().getValue("map.reslayers["+i+"].type"));
			resLayer.put("visible", SysProperty.getInstance().getValue("map.reslayers["+i+"].visible"));
			resLayer.put("alpha", SysProperty.getInstance().getValue("map.reslayers["+i+"].alpha"));
			resLayer.put("count", SysProperty.getInstance().getValue("map.reslayers["+i+"].count"));
			resLayer.put("url", SysProperty.getInstance().getValue("map.reslayers["+i+"].url"));
			resLayer.put("resid", SysProperty.getInstance().getValue("map.reslayers["+i+"].resid"));
			reslayerList.add(resLayer);
		}
		map.put("reslayers", reslayerList);
		
		config.put("map", map);
		return config;
	}
}
