Ext.ns('Frame.grid');

Frame.grid.formPamel_cable = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config.layout = 'border';
		Frame.grid.formPamel_cable.superclass.constructor.call(this, config);
	},

	initComponent : function() {
		this._initItems();
		Frame.grid.formPamel_cable.superclass.initComponent.call(this);
	},

	_initItems : function() {
		// 树结构
		var tree = new Ext.tree.TreePanel({
			title : "光缆资源",
			useArrows : true,// 否使用箭头样式
			autoScroll : true,// 滚动条
			animate : true,// 展开,收缩动画
			rootVisible : true,// 根节点否见
			lines : false,// 禁止显示树虚线
			root : new Ext.tree.AsyncTreeNode({
				text : "光缆信息",
				children : [ {} ]
			})

		});

		this.treePanel = new Ext.Panel({
			region : 'west',
			width : 300,
			height : 400,
			layout : 'form',
			border : true,
			//renderTo : Ext.getBody(),
			items : [ tree ]
		});
		
		// 列表
		this.rightPanel = new Ext.grid.GridPanel({
			width : 500,
			height : 400,
			border : true,
			frame : false,
			region : 'center',

			store : new Ext.data.Store({
				autoDestroy : true,
				reader : new Ext.data.DataReader({
					fields : [ {
						name : 'LABEL_CN'
					}, {
						name : 'MAKE_FLAG'
					} ]
				})
			}),
			colModel : new Ext.grid.ColumnModel({
				defaults : {
					width : 265,
					shortable : false
				},
				columns : [ {
					id : 'LABEL_CN',
					header : '点设施名称',
					dataIndex : 'LABEL_CN'
				}, {// 枚举类型
					id : 'MAKE_FLAG',
					header : '预留光缆长度',
					dataIndex : 'MAKE_FLAG',
					editor : new Ext.form.TextField({
						allowBalank : false
					})
				} ]
			})
		});
		this.items = [ this.treePanel,this.rightPanel];
	}
});
