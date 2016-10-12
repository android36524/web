<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html>
	<head>
		<title>交维资源设备新增流程</title>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<meta http-equiv="Access-Control-Allow-Origin" content="*">
		<script type="text/javascript"  src="${ctx}/ponMaintain/panel/DeviceMaintainPanel.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var panel = new DM.ponMaintain.common.DeviceMaintainPanel({});
				var view = new Ext.Viewport({
                    layout : 'fit',
                    items : [panel]
                });
				
			});
		</script>
	</head>
	<body>
	</body>
</html>