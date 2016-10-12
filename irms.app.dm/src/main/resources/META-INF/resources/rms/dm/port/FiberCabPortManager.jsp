<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<%@ include file="/cmp_include/tree.jsp"%>
		<script type="text/javascript" src="${ctx}/rms/dm/port/PortManager.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = getGridCfgByCode();
				var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);
				
				cfg.states = {
						//点击树图一级节点状态名，不能改变
						'level1' : {
							//bbar中按钮的文字
							enableButtons : ['添加模块']
						},
						//点击树图二级节点状态名，不能改变
						'level2' : {
							enableButtons : ['添加模块','删除模块','修改模块','按行添加端子','按列添加端子','批量修改'],
							levelFilterColumn : 'RELATED_MODULE_CUID'
						},
						//点击树图三级节点状态名，不能改变
						'level3' : {
							enableButtons : ['按行添加端子','按列添加端子','批量修改'],
							levelFilterColumn : 'NUM_IN_MROW'
						},
						//点击grid状态名，不能改变
						'row' : {
							enableButtons : ['按行添加端子','按列添加端子','修改','批量修改','删除','批量命名']
						}
				};
				
				Ext.apply(cfg, {
					boName: 'PortTreeGridBO'
				});
				
				var grid = new Frame.grid.PortTreePanel(cfg);
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