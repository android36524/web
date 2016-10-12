<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/cmp_include/form.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<%@ include file="/cmp_include/tree.jsp"%>
		<script type="text/javascript" src="${ctx}/rms/dm/optical/opticalway-grid-editor.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = getGridCfgByCode();
				var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);
				if(cfg.cuid){
					cfg.gridCfg.cfgParams.cuid = cfg.cuid;
					cfg.gridCfg.cfgParams.labelCn = cfg.labelCn;
				}
				cfg.hasBbar =true;
				var grid = new Frame.grid.ResEditorGridPanel(Ext.apply({
				}, cfg));
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