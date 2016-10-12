<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<%@ include file="/commons/common.jsp"%>
<%@ include file="/commons/dwr.jsp"%>
<%@ include file="/commons/ext.jsp"%>
<%@ include file="/cmp_include/form.jsp"%>
<link rel="stylesheet" href="${ctx}/jslib/tp/tp-map.css">
<script src="${ctx}/jsp/framework/FlexEvent.js"></script>
<script	src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"></script>
<script>
	htconfig = {
		Default : {
			toolTipDelay : 100,
			toolTipContinual : true,
			toolTipLabelFont : '16px arial, sans-serif'
		}
	};
</script>
<script src="${ctx}/map/map-inc.js"></script>
<script src="${ctx}/map/dms-init.js"></script>
<script src="${ctx}/map/dms-components.js"></script>
<script src="${ctx}/map/dms-querypanel.js"></script>
<script src="${ctx}/map/dms-tools.js"></script>
<script src="${ctx}/map/dms-wiretoductline.js"></script>
<script src="${ctx}/map/dms-map.js"></script>
<script src="${ctx}/map/dms-mapbounds.js"></script>
<script src="${ctx}/commons/utils/SynDataHelper.js"></script>
<!-- <script type="text/javascript" src="${ctx}/dwr/interface/GridViewAction.js"></script> -->
<script src="${ctx}/map/dms-toolpanel.js"></script>
<script src="${ctx}/map/dms-map-contextmenu.js"></script>
<%-- <script src="${ctx}/map/jquery-plugins.js"></script> --%>
<script type="text/javascript">

	var isTouchable = "ontouchend" in document;
	var geojson;
	$(document).ready(function(){
		var callback = function(){
			//加载保存的位置
			var tpmap = Dms.Default.tpmap
	        Dms.MapBounds.loadMapBound(tpmap.getMap());
		};
		var url = ctx + "/map/mapconfig.json";
		$.getJSON(url, {}, function(data) {
			Dms.initMap(data, callback);
			Dms.initToolPane();
		});
		
	    $(window).bind("beforeunload",function(){
	    	var msg;
	    	/*
	    	var msg = '您确定要退出该界面么！';
	    	var e = e || window.event;
			if (e) {
				e.returnValue = msg;
			}*/
			Dms.MapBounds.saveMapBound();
			return msg;
	    });
	});
	function style(feature) {
	    return {
	        fillColor: '#800026',//getColor(feature.properties.density),
	        weight: 2,
	        opacity: 1,
	        color: 'white',
	        dashArray: '3',
	        fillOpacity: 0.7
	    };
	}
	function highlightFeature(e) {
	    var layer = e.target;

	    layer.setStyle({
	        weight: 5,
	        color: '#666',
	        dashArray: '',
	        fillOpacity: 0.7
	    });

	    if (!L.Browser.ie && !L.Browser.opera) {
	        layer.bringToFront();
	    }
	};
	function resetHighlight(e) {
	    geojson.resetStyle(e.target);
	};
	function zoomToFeature(e) {
	    map.fitBounds(e.target.getBounds());
	};
	function onEachFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: zoomToFeature
	    });
	};
</script>
</head>
<body>
<!-- 	<div id="map"></div> -->
</body>
</html>
