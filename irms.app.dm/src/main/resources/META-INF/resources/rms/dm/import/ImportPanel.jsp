<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/cmp_include/form.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<script type="text/javascript" src="${ctx}/rms/dm/import/ImportPanel.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var panel = new IRMS.dm.ImportPanel();
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