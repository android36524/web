$importjs(ctx + "/jsp/framework/NavTree.js");
Ext.ns('Frame.grid');

Frame.grid.PortTreePanel = Ext.extend(Ext.Panel, {
	border : false,
	store : new Ext.data.JsonStore({
		root : 'rows',
		autoLoad : false,
		fields : ['CUID']
	}),
	constructor : function(config) {
		config.gridConfig = Ext.applyIf({
			loadData : false
		}, config);
		Frame.grid.PortTreePanel.superclass.constructor.call(this,config);
	},
	
	initComponent : function() {
		this.layout = 'border';
		this.items = [];
		this._buildTreePanel();
		this._buildGridPanel();
		Frame.grid.PortTreePanel.superclass.initComponent.call(this);
		this.on('afterlayout', this._mask);
	},
	_mask : function() {
		MaskHelper.mask(this.getEl());
	},
	_buildTreePanel : function() {
		this.tree = new Frame.tree.AsynTreePanel({
			region: 'west',
			frame : false,
			header : false,
			border : true,
			split : true,
			width : 200,
			collapsible : false,
			autoLoad : false,
			animate: false,
			rootVisible : false,
			singleExpand : false,
			root : {
				id : 'root',
				cuid : this.cuid,
				expanded : true,
				boName : this.boName
			}
		});
		this.tree.on("click", function(node, event) {
			this._filterGridData(node);
			this.grid.fireEvent('changeState', 'level' + node.getDepth(), this.states);
		}, this);
		this.tree.expandAll();
		this.items.push(this.tree);
	},
	_buildGridPanel : function(){
		this.grid = new Frame.grid.DataGridPanel(Ext.applyIf({
			region : 'center',
			disableSystemBar : true,
			viewConfig : {
				forceFit: true
			}
		}, this.gridConfig));
		this.items.push(this.grid);
		this.grid.on('rowclick', function(grid, rowIndex, e){
			this.grid.fireEvent('changeState', 'row', this.states);
		}, this);
		this.grid.on('metaloaded', this._loadGridData, this);
		this.grid.tree = this.tree;
	},
	_filterGridData : function(node) {
		var filterColumns = [];
		var curnode = node;
		while (true) {
			if (!this.states['level' + curnode.getDepth()]) {
				break;
			}
			var levelFilterColumn = this.states['level' + curnode.getDepth()].levelFilterColumn;
			if (levelFilterColumn) {
				var filter = {
					property : levelFilterColumn,
					value : curnode.attributes.cuid
				};
				filterColumns.push(filter);
				curnode = curnode.parentNode;
			} else {
				break;
			}
		}
		this.grid.store.filterBy(function(record) {
			var r = true;
			for (var i=0; i<filterColumns.length; i++) {
				var data = record.get(filterColumns[i].property);
				if (data && data.CUID) {
					r = r && data.CUID == filterColumns[i].value;
				}else {
					r = r && data == filterColumns[i].value;
				}
			}
			return r;
		}, this);
	},
	_loadGridData : function() {
		this.un('afterlayout', this._mask);
		MaskHelper.mask(this.getEl());
		if (this.cuid) {
			var baseParams = {
				start : 0,
				limit : 1000
			};
			
			var gridCfg = Ext.apply(this.gridCfg, {
				queryParams : {
					CUID : {
						key : 'CUID',
						value : this.cuid
					}
				}
			});
			
			var scope = this;
			GridViewAction.getGridData(baseParams, gridCfg, function(result) {
				if(Ext.isEmpty(result)) {
					Ext.Msg.alert('系统异常', '无法获取表格定义，请检查表格配置！');
				}else {
					scope.grid.store.loadData(result);
					scope.grid.fireEvent('changeState', 'level2', scope.states);
					var node = scope.tree.getSelectionModel().selNode;
					if (node) {
						scope.tree.fireEvent('click', node);
					}
				}
				MaskHelper.unmask(scope.getEl());
			});
		}
	}
});