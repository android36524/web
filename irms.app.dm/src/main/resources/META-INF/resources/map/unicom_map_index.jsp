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
<link rel="stylesheet" href="${ctx}/map/dms-map.css">
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
<script src="${ctx}/map/dms-tools.js"></script>
<script src="${ctx}/jslib/tp/tp-map-inc.js"></script>
<script src="${ctx}/map/dms-map.js"></script>
<script src="${ctx}/map/dms-mapbounds.js"></script>
<script src="${ctx}/commons/utils/SynDataHelper.js"></script>
<!-- <script type="text/javascript" src="${ctx}/dwr/interface/GridViewAction.js"></script> -->
<script src="${ctx}/map/GridServiceDictLoader.js"></script>

<script src="${ctx}/map/dms-map-contextmenu.js"></script>

<script src="${ctx}/dwr/interface/MapConfigAction.js"></script>
<script src="${ctx}/map/unicom-view.js"></script>
<style>
    html, body {
        padding: 0px;
        margin: 0px;                
    } 
    .main {
        margin: 0px;
        padding: 0px;
        position: absolute;
        top: 0px;
        bottom: 0px;
        left: 0px;
        right: 0px;
    }
</style> 
<%-- <script src="${ctx}/map/jquery-plugins.js"></script> --%>
<script type="text/javascript">
	var isTouchable = "ontouchend" in document;
	$(document).ready(function(){
		//var graphView = Dms.Default.graphView = new ht.graph.GraphView(),
			//读取图层配置文件
		var url = ctx + "/map/mapconfig.json";
		$.getJSON(url, {}, function(data) {
			//Dms.initMap(data,graphView);
			//Dms.initToolbar(graphView);
			//Dms.initToolPane(graphView);
			Dms.Default.Unicom.initView(data);
		});
	    $(window).bind("beforeunload",function(){
	    	var msg;
	    	/*
	    	var msg = '您确定要退出该界面么！';
	    	var e = e || window.event;
			if (e) {
				e.returnValue = msg;
			}*/
			//Dms.MapBounds.saveMapBound();
			return msg;
	    });
	});
</script>
</head>
<body>
<!--
	<div id="map"></div>
	-->
</body>
</html>