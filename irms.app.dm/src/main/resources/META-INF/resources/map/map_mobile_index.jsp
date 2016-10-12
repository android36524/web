<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<%@ include file="/commons/common.jsp"%>
<!--%@ include file="/commons/dwr.jsp"%-->
<!-- %@ include file="/commons/ext.jsp"% -->
<!-- %@ include file="/cmp_include/form.jsp"%-->
<%
	String cuid = (String)request.getParameter("cuid");
%>
<link rel="stylesheet" href="${ctx}/jslib/tp/tp-map.css">
<!-- <script src="${ctx}/jsp/framework/FlexEvent.js"></script>-->
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
<script src="${ctx}/map/map-mobile-inc.js"></script>
<script src="${ctx}/map/dms-init.js"></script>
<script src="${ctx}/map/dms-components.js"></script>
<script src="${ctx}/map/dms-querypanel.js"></script>
<script src="${ctx}/map/dms-tools.js"></script>
<script src="${ctx}/map/dms-wiretoductline.js"></script>
<script src="${ctx}/map/dms-map.js"></script>
<script src="${ctx}/map/dms-mapbounds.js"></script>
<script src="${ctx}/commons/utils/SynDataHelper.js"></script>
<script src="${ctx}/map/dms-toolpanel.js"></script>
<script src="${ctx}/map/dms-map-contextmenu.js"></script>
<script src="${ctx}/dwr/interface/MapConfigAction.js"></script>
<script type="text/javascript">
	var isTouchable = "ontouchend" in document;
	$(document).ready(function(){
		var callback = function(){
			var tpmap = Dms.Default.tpmap;
			var graphView = tpmap.getGraphView();
			//选中对象时调用外部app
			graphView.dm().addDataModelChangeListener(function(e){
				if(e.kind === 'add'){
					var data = e.data;
					var cuid = data.getId();
					var name = data.getName();
					try{
						//仅处理站点
						if(cuid.indexOf('SITE-')==0){
							window.js2java.runApp(cuid, name);
						}
					} catch(err) {
						console.error("错误描述: " + err.message);
					}
				}else if(e.kind === 'remove'||e.kind === 'clear'){
					
				}
			});
	    	var cuid = '<%=cuid%>';	  
	    	var className=cuid.split('-')[0];
	    	if(cuid && className){
	    		var locateSql="CUID='"+cuid+"'" ;
		    	tpmap.locateByCondition(className,locateSql,true);
			}
		};
		//读取图层配置文件
		MapConfigAction.getMapConfig(function(data){
			Dms.initMap(data, callback);
			
		});
	    $(window).bind("beforeunload",function(){
	    	var msg;
			Dms.MapBounds.saveMapBound();
			return msg;
	    });
	});
</script>
</head>
</html>
