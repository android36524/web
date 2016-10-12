
<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/cmp_include/form.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<%@ include file="/cmp_include/tree.jsp"%>
		<script type="text/javascript" src="${ctx}/rms/dm/ductseg/tree-editor.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = getGridCfgByCode();
				var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);
				cfg.hasQuery = false;//屏蔽查询面板
				var grid = new Frame.grid.TreeEditorGrid(Ext.apply({
					treeLevelBoName: ['IRMS.RMS.STONEWAY_BRANCH','IRMS.RMS.STONEWAY_SEG']
				}, cfg));
				grid.getAddButton().hidden=true;
				grid.getDeleteButton().hidden=true;
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