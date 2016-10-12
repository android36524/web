Dms.initToolPane = (function() {
	createEditPanel = function() {
		var config = tp.Default.BaseToolConfig;
		config.items.push({
			id: 'statist',
			image: 'tool_calculate',
			toggle: true,
			name: '统计',
			action: function() {
				var panel = new tp.widget.AreaStatisticsPanel(Dms.Default.tpmap);
				panel.show();
			}
		});
		var panel = new tp.widget.ToolPanel(config);
		return panel.getPanelView();
	}, createDrawPanel = function() {
		var config = tp.Default.DrawToolConfig,
			panel = new tp.widget.ToolPanel(config);
		return panel.getPanelView();
	}, createOperatePane = function() {
		var mainSplit = new ht.widget.SplitView(createTable(), createOperatePanel(), 'vertical', 0.3);
		return mainSplit;
	}, createFloatOperatePane = function() {
		var config = tp.Default.FloatToolConfig,
			panel = new tp.widget.FloatToolPanel(config);
		return panel.getPanelView();
	},  createTable = function() {
		var dataModel = new ht.DataModel();
		var tablePane = new ht.widget.TablePane(dataModel), columnModel = tablePane
				.getColumnModel(), table = tablePane.getTableView(), column = new ht.Column();
		tablePane.getTableHeader().getView().className = "tableHeader";

		table.drawRowBackground = function(g, data, selected, x, y, width,
				height) { // 重写此方法实现斑马线效果
			var self = this, checkMode = self.isCheckMode(), index = table
					.getRowIndex(data), color = "white";
			if (index % 2 === 0) {// 偶数行的颜色
				color = "rgb(244, 251, 251)";
			}
			if (data === this._hoverData) {// hover颜色
				color = "rgb(252, 248, 227)";
			}
			if ((data === self._focusData && checkMode) || selected
					&& !checkMode) {// 选中的颜色
				color = this.getSelectBackground(data);
			}
			g.fillStyle = color;
			g.beginPath();
			g.rect(x, y, width, height);
			g.fill();
		};

		column.setName("type");
		column.setDisplayName("类型");
		column.setAccessType('attr');
		columnModel.add(column);

		column = new ht.Column();
		column.setName("labelcn");
		column.setDisplayName("名称");
		column.setAccessType('attr');
		columnModel.add(column);

		var n = new ht.Node();
		n.a('type', '光缆');
		n.a('labelcn', '武汉测试光缆');
		dataModel.add(n);
		return tablePane;
	}, createOperatePanel = function() {
		var config = tp.Default.OperateToolConfig,
			panel = new tp.widget.FloatToolPanel(config);
		return panel.getPanelView();
	};

	return function() {
		var panelGroup= new ht.widget.PanelGroup({
            hGap: 10,
            vGap: 10
        });
		// 初始化查询窗口
		var map = Dms.Default.tpmap;
		var layerPanel = new tp.widget.LayerPanel(map);// 图层控制面板
		var queryPanel = new tp.widget.ResouceQueryPanel();
		queryPanel = queryPanel.getMainPanel();// createQueryPanel()
		var editGv = createEditPanel(), 
		drawGv = createDrawPanel(), 
		treeView = layerPanel.getTreeView(), 
		qPanel = queryPanel, 
		panel = Dms.Default.toolPanel = new ht.widget.Panel(
				{
					title : "工具箱",
					width : 160,
					resizeMode:"w",
					exclusive : true,
					// titleBackground: "red",
					titleColor : "yellow",
					id : 1,
					// content:"<input type='button' value='add panel'
					// onclick='addPanel()'>",
					minimizable : true,
					narrowWhenCollapse : true,
					expand : true,
					items : [ {
						id : 2,
						buttons : [ "independentSwitch", "toggle" ],
						title : "基本工具",
						contentHeight : 240,
						expand : true,
						content : editGv
					}, {
						id : 3,
						buttons : [ "independentSwitch", "toggle" ],
						title : "编辑工具",
						expand : false,
						contentHeight : 300,
						content : drawGv
					},
					{
						id : 4,
						buttons : [ "independentSwitch", "toggle" ],
						title : "图层控制",
						// titleBackground: "rgb(52, 151, 218)",
						expand : false,
						contentHeight : 360,
						content : treeView.getView()
					} ]
				});
		panel.addEventListener(function(e) {
			if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
					(e.kind === "endToggle" && panel._config.expand === true)){
				operateqPanel.invalidate();
				editGv.invalidate();
				drawGv.invalidate();
				treeView.invalidate();
			}
		});
		panel.setPosition(10, 10);
		document.body.appendChild(panel.getView());
		// return id++;
		var tpmap = Dms.Default.tpmap; 
		tpmap.setResourceLayerObject(layerPanel.getResourceLayerObject());
		// 第二个资源查询面板

		var panel2 = Dms.Default.queryPanel = new ht.widget.Panel({
			title : "资源查询",
			width : 260,
			exclusive : true,
			// titleBackground: "red",
			titleColor : "yellow",
			id : 5,
			// content:"<input type='button' value='add panel'
			// onclick='addPanel()'>",
			minimizable : true,
			expand : false,
			narrowWhenCollapse : true,
			contentHeight : 500,
			// titleBackground: "rgb(52, 151, 218)",
			content : qPanel
		});
		// panel2.setPosition(10, 160);
		panel2.addEventListener(function(e) {
			if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
					(e.kind === "endToggle" && panel2._config.expand === true)){
						qPanel.invalidate();
						// table.getView().style.width =
						// tablePanel._config.width + "px";
						// table.getView().style.height =
						// tablePanel._config.contentHeight + "px";
						// table.iv();
					}
				});
		panel2.setPosition(470, 10);
		document.body.appendChild(panel2.getView());

		var operateqPanel = createOperatePane(), panel3 = new ht.widget.Panel({
			title : "功能操作",
			width : 260,
			exclusive : true,
			titleColor : "yellow",
			id : 6,
			minimizable : true,
			minimize : true,
			expand : false,
			narrowWhenCollapse : true,
			contentHeight : 360,
			content : operateqPanel
		});
		panel3.addEventListener(function(e) {
			if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
					(e.kind === "endToggle" && panel3._config.expand === true)){
						operateqPanel.invalidate();
					}
				});
		// panel3.setPosition(10, 120);
		//panel3.setPosition(190, 10);
		panel3.setPositionRelativeTo("rightTop");
		panel3.setPosition(0, 80);
		//document.body.appendChild(panel3.getView()); 暂时先屏蔽掉

		var floatOperatePanel = createFloatOperatePane(), 
		panel4 = new ht.widget.Panel(
				{
					title : "动态操作",
					width : 190,
					exclusive : true,
					titleColor : "yellow",
					id : 7,
					minimizable : true,
					minimize : true,
					expand : false,
					narrowWhenCollapse : true,
					contentHeight : 210,
					content : floatOperatePanel
				});
		panel4.addEventListener(function(e) {
					if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
							(e.kind === "endToggle" && panel4._config.expand === true)){
						floatOperatePanel.invalidate();
					}
				});
//		panel4.setPosition(1200, 120);
		panel4.setPositionRelativeTo("rightBottom");
		panel4.setPosition(0, 180);
		//document.body.appendChild(panel4.getView());暂时先屏蔽掉

		// panel.minimize(true);
		// panel2.minimize(true);
		// panel3.minimize(true);
		panelGroup.add(panel);
        panelGroup.add(panel2);
        panelGroup.add(panel3);
        //panelGroup.add(panel4);
        panelGroup.setLeftTopPanels(panel, panel2, "h");
//        Dms.Default.floatOperatePanel = panel4;
	};

})();