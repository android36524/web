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
<script src="${ctx}/map/map-inc.js"></script>
<%@ include file="/cmp_include/form.jsp"%>
<script src="${ctx}/jslib/tp/topo-all.js"></script>
<link rel="stylesheet" href="${ctx}/jslib/tp/tp-map.css">
<script	src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"></script>
<script	src="${ctx}/dwr/interface/GridViewAction.js"></script>
<script>
	htconfig = {
		Default : {
			toolTipDelay : 100,
			toolTipContinual : true,
			toolTipLabelFont : '16px arial, sans-serif'
		}
	};
</script>

<script type="text/javascript">
	tp.plugin.combo = {};
	var isTouchable = "ontouchend" in document;
	
	$(document).ready(function(){
		seajs.use('tp_component',function(){
			var dialogGridPanel = new tp.component.DiaLogGridPanel({
				bmclassid : 'IRMS.RMS.MANHLE',
				boname : 'GridTemplateProxyBO',
				plugins : {
					queryform : 'rms/dm/example/queryform/QueryForm'
				},
				listeners : {
					select : function(data){
						console.log(data);
					}
				}
			});
			dialogGridPanel.show();
		});
	});
</script>
</head>
<body>
</body>
</html>
