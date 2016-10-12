
<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/cmp_include/form.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<%@ include file="/cmp_include/tree.jsp"%>
		<script type="text/javascript" src="${ctx}/rms/dm/wire/fiber-treegrid-editor.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = getGridCfgByCode();
				var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);
				cfg.hasQuery = false;
				cfg.canBatch = false;
				cfg.canDel = false;
				var grid = new DM.fiber.TreeEditorGrid(Ext.apply({
					treeLevelBoName: ['IRMS.RMS.WIRE_SEG','IRMS.RMS.FIBER']
				}, cfg));
				grid.getAddButton().hidden=true;
				grid.getAddBatch().disable();
				var view = new Ext.Viewport({
                    layout : 'fit',
                    items : [grid]
                });
			});
		</script>
	</head>
	<body>
	</body>
</html>