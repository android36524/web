Ext.ns('DM.fiber');
DM.fiber.FiberManagePanel = Ext.extend(Ext.Panel,{

	layout : 'border',

	constructor : function(config){

		DM.fiber.FiberManagePanel.superclass.constructor.call(this,config);
	},

	initComponent : function(){

		this._initView();
		FDM.fiber.FiberManagePanel.superclass.initComponent.call(this);
	},

	_initView : function(){

		this.items = [this._initTreeGrid(),this._initEditor()]; 
	},

	_initEditor : function(){

		var panel = new Ext.Panel({
			region : 'east',
			width : 200
		});
		return panel;
	},

	_initTreeGrid : function(){
		var config = {};
		var panel = new Frame.tree.AsynTreeGridPanel({
			region : 'center',
			border : false,
			frame : false,
			autoScroll : false,
			expanded : false,
			autoLoad : true,
			singleSelect : true,
			checkVisible : false,
			multSel:false,
			header : false,
			checkModel: 'single',
			onlyLeafCheckable:true,
			tbar : [{text:'修改所属系统'},{text:'添加分支'}],
			columns : [{
				dataIndex : 'text',
				header : '全部分支',
				width : 200
			},{
				dataIndex : 'LABEL_CN',
				header : '名称',
				width : 200
			}],
			root : {
				id : 'root',
				cuid : 'root',
				text : 'tree',
				expanded : true,
				boName : 'DuctTreeGridBO',
				treeName : 'DUCTSEG',
				params : {
					pageCount : -1,
					templateIds : 'ROLE_ROLESELECT_LV0'
				},
				treeParams : {
					relId : this.relId,
					user : '',
					relation : this.relation,
					district : this.district
				},
				queryParams : {
				}
			},
			getSelesionNodes : function() {
				var nodeList = [];
				var node = this.getSelectionModel().getSelectedNode();
				    if (node.isLeaf()) {
						nodeList.push(node);
				    }else{
				    	msg='请选择一个叶子节点的施工队！';
				    	Ext.Msg.alert('温馨提示', msg);
				    }
				return nodeList;
			}
		});
		return panel;
	}
});