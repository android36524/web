<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
<%@ include file="/commons/common.jsp"%>
<%@ include file="/commons/ext.jsp"%>
<%@ include file="/commons/dwr.jsp"%>
<script type="text/javascript" src="${ctx}/jsp/framework/FlexEvent.js"></script>
<style>
body { margin: 0px; overflow:hidden; }
body {-moz-user-select:none; -khtml-user-select:none; user-select:none;}
</style>
<title>Index</title>
<link rel="stylesheet" href="${ctx}/js/topo.css">
		<script type="text/javascript" src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"> </script>
		<script type="text/javascript" src="${ctx}/jslib/tp/topo-all.js"> </script>
		<script type="text/javascript" src="${ctx}/jslib/tp/topo-integ.js"> </script>

<script src="${ctx}/map/map-inc.js"></script>
<script src="${ctx}/map/dms-init.js"></script>
<script src="${ctx}/map/dms-components.js"></script>
<script src="${ctx}/map/dms-querypanel.js"></script>
<script src="${ctx}/map/dms-tools.js"></script>
<script src="${ctx}/map/dms-tools-ux.js"></script>
<script src="${ctx}/map/dms-sectionPic.js"></script>
<script src="${ctx}/map/dms-conf-layername.js"></script>
<script src="${ctx}/map/dms-map.js"></script>
<script src="${ctx}/map/dms-toolpanel.js"></script>
<script src="${ctx}/map/dms-map-inc.js"></script>
<script src="${ctx}/commons/utils/SynDataHelper.js"></script>
<script src="${ctx}/map/dms-toolpanel.js"></script>
<script src="${ctx}/map/dms-map-contextmenu.js"></script>
<script src="${ctx}/map/dms-querySiteConnctionAndAnalyse.js"></script>
<script src="${ctx}/map/dms-util-selectDeviceByTypeComp.js"></script>
<script src="${ctx}/jslib/tp/topo-all.js"></script>
<script src="${ctx}/jslib/tp/tp-form.js"></script>
<script src="${ctx}/jslib/ht/ht-historymanager.js"></script>


<script type="text/javascript">

	$(document).ready(function(){
		var graphView = tp.utils.createSiteConnectionQueryPanel();
		
		//菜单+2D拓扑组件
		window.addEventListener('resize', function (e) {
			graphView.invalidate();
	    }, false);
		//显示
		var view = graphView.getView();
	    view.className = 'topo';
	    document.querySelector(".main").appendChild(view);
	});
</script>
</head>
<body>
	<div class="main"></div>
</body>
</html>
