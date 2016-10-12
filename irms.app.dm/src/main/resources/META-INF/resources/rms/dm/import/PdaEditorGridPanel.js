Ext.namespace('IRMS.dm');
$importjs(ctx + '/dwr/interface/PdaGroupAction.js');
$importjs(ctx + '/cmp_res/grid/EditorGridPanel.js');
IRMS.dm.EditorGridPanel = Ext.extend(Ext.Panel, {
	layout : 'border',
	constructor : function(config) {
		this._initView(config);
		IRMS.dm.EditorGridPanel.superclass.constructor.call(this, config);
	},
	initComponent : function() {
		IRMS.dm.EditorGridPanel.superclass.initComponent.call(this);
	},

	_initView : function(config) {
		this.grid = new Frame.grid.ResEditorGridPanel(Ext.apply({
		}, config));
		var items = new Array();
		//下部分
		var cm2 = new Ext.grid.ColumnModel([ {
			header : 'PDA编码',
			dataIndex : 'pdaCode',
			width : 200,
			sortable : true
		}, {
			header : '用户',
			dataIndex : 'userName',
			width : 200,
			sortable : true
		}, {
			header : '设备状态',
			dataIndex : 'deviceStatus',
			width : 200,
			sortable : true
		}, {
			header : '归属设备组',
			dataIndex : 'relatedGroup',
			width : 200,
			sortable : true
		}, {
			header : '备注',
			dataIndex : 'remark',
			width : 230,
			sortable : true
		}, {
			header : 'CUID',
			dataIndex : 'CUID',
			width : 250,
			sortable : true,
			hidden : true
		} ]);
		var store2 = new Ext.data.Store({});
		this.grid2 = new Ext.grid.EditorGridPanel({
			autoScroll : true,
			scope : this,
			height : 225,
			store : store2,
			cm : cm2,
			sm : new Ext.grid.RowSelectionModel({}),
			bbar : [ {
				text : '选中',
				iconCls : 'c_drive_add',
				scope : this,
				handler : this.choose
			}, {
				text : '添加入组',
				iconCls : 'c_drive_edit',
				scope : this,
				handler : this.insertGroup
			}, {
				text : '从组删除',
				iconCls : 'c_drive_edit',
				scope : this,
				handler : this.delfromGroup
			}, {
				text : '关闭',
				iconCls : 'c_drive_edit',
				scope : this,
				handler : this.close
			}]
		});
		var self = this;
		Ext.apply(this.grid, {region: 'center',height:400});
		Ext.apply(this.grid2, {region: 'south',height:200});
		/*this.grid.grid.rowClickListener = function(grid,rowIndex,e){
				var datas = grid.getSelectionModel().getSelections();
				if(datas.length == 0){
					return;
				}
				this.maintainPanel.setCanUpdate();
				if(this.maintainPanel.batchEdit==true){
					this.maintainPanel.changeSignalMeta();
				}
				this.maintainPanel.refreshGridData(datas[0].data,datas);
				self.loadGrid2();
		}*/
		this.grid.grid.on('rowclick', function(grid,rowIndex,e){
			var datas = grid.getSelectionModel().getSelections();
			self.loadGrid2(datas[0].data.CUID);
		}, this);
//		this.grid.grid.addListener('rowdblclick',self.loadGrid2(),this);
//		Ext.apply(this.grid.items.items[0], {layout: null,region : 'north',height:200});
//		Ext.apply(this.grid.items.items[1], {region : 'east',height:200});
//		Ext.apply(this.grid.items.items[2], {region : 'center',height:400});
		items.push(this.grid);
		items.push(this.grid2);
		this.items = items;
	},
	delfromGroup : function() {
		var self = this;
		var selModel = this.grid2.getSelectionModel();
		if (selModel.hasSelection()) {
			Ext.Msg.confirm('提示信息','确认删除吗？',function(fn){
				if(fn=='yes'){
					var userCuid = selModel.getSelected().data.CUID;
					var pdaGroupData = self.grid.grid.getSelectionModel().getSelected().data;
					var cuid = pdaGroupData.CUID;
					PdaGroupAction.deleteFromGroup(userCuid,'', function(data) {
						if (data) {
							Ext.Msg.alert("操作提示", "删除成功！");
							self.loadGrid2(cuid);
						} else {
							Ext.Msg.alert("操作提示", "删除失败！");
						}
					});
				}else{
					return;
				}
			});
		} else {
			Ext.Msg.alert("提示", "请选择一条记录");
		}
	},
	insertGroup : function() {
		var self = this;
		var cuid="";
		if(self.grid.grid.getSelectionModel().getSelected()!=undefined){
			var pdaGroupData = self.grid.grid.getSelectionModel().getSelected().data;
			cuid = pdaGroupData.CUID;
		}
		if (cuid == ""||cuid==undefined) {
			Ext.Msg.alert("操作提示", "请先选择设备组");
			return;
		}
		this.store4 = new Ext.data.Store({});
		this.cm4 = new Ext.grid.ColumnModel([ {
			header : 'PDA编码',
			dataIndex : 'DEVICE_CODE',
			width : 100,
			sortable : true
		}, {
			header : '用户',
			dataIndex : 'USER_NAME',
			width : 100,
			sortable : true
		}, {
			header : '电话号码',
			dataIndex : 'PHONENUMBER',
			width : 100,
			sortable : true
		}, {
			header : '设备状态',
			dataIndex : 'DEVICE_STATE',
			width : 100,
			sortable : true
		}, {
			header : '归属设备组',
			dataIndex : 'GROUPNAME',
			width : 100,
			sortable : true
		}, {
			header : '所属区域',
			dataIndex : 'DISTRICTNAME',
			width : 100,
			sortable : true
		}, {
			header : '备注',
			dataIndex : 'REMARK',
			width : 200,
			sortable : true
		}, {
			header : 'CUID',
			dataIndex : 'CUID',
			width : 200,
			sortable : true,
			hidden : true
		} ]);
		this.winPanel = new Ext.form.FormPanel({
			height : 70,
			layout : 'form',
			buttonAlign : 'right',
			frame : true,
			scope : this,
			items : [ {
				xtype : 'textfield',
				name : 'PDA_USER_NAME',
				fieldLabel : '用户名',
				width : 250
			} ],
			buttons : [ {
				text : '查询',
				disabled : this.readOnly,
				iconCls : 'c_find',
				scope : self,
				handler : this.queryPdaUser
			}, {
				text : '重置',
				disabled : this.readOnly,
				iconCls : 'c_arrow_rotate_anticlockwise',
				scope : self,
				handler : this.clearQueryData
			} ]
		});

		this.winGrid = new Ext.grid.GridPanel({
			frame : false,
			autoScroll : true,
			scope : this,
			height : 380,
			store : this.store4,
			cm : this.cm4,
			sm : new Ext.grid.RowSelectionModel({})
		});
		this.win = new Ext.Window({
			title : '添加入组',
			layout : 'anchor',
			width : 800,
			height : 500,
			frame : true,
			scope : this,
			modal : true,
			items : [ this.winPanel, this.winGrid ],
			buttons : [ {
				text : '确定',
				scope : self,
				handler : this.confirmPda
			}, {
				text : '取消',
				scope : self,
				handler : this.closeWindow
			} ],
		});
		this.win.show();
	},
	
	queryPdaUser : function() {
		var userName = this.winPanel.getForm().findField("PDA_USER_NAME")
				.getValue();
		this.winGrid.getStore().removeAll();
		var self = this;
		PdaGroupAction.getPdaMemberDataByName(userName, function(data) {
			if (data) {
				for (var i = 0; i < data.length; i++) {
					var record = new Ext.data.Record({
						USER_NAME : data[i].USER_NAME,
						CUID : data[i].CUID,
						DEVICE_CODE : data[i].DEVICE_CODE,
						DEVICE_STATE : data[i].DEVICE_STATE,
						GROUPNAME : data[i].GROUPNAME,
						PHONENUMBER : data[i].PHONENUMBER,
						DISTRICTNAME : data[i].DISTRICTNAME,
						REMARK : data[i].REMARK,
					});
					self.winGrid.getStore().insert(i, record);
				}
			} else {
				Ext.Msg.alert("操作提示", "获取信息失败！");
			}
		});
	},
	clearQueryData : function() {
		this.winPanel.getForm().reset();
	},
	confirmPda : function() {
		var self = this;
		var selModel = this.winGrid.getSelectionModel();
		if (selModel.hasSelection()) {
			var userCuid = selModel.getSelected().data.CUID;
			var pdaGroupData = self.grid.grid.getSelectionModel().getSelected().data;
			var cuid = pdaGroupData.CUID;
			PdaGroupAction.insertIntoGroup(userCuid,cuid, function(data) {
				if (data) {
					Ext.Msg.alert("操作提示", "添加成功！");
					self.queryPdaUser();
					self.loadGrid2(cuid);
				} else {
					Ext.Msg.alert("操作提示", "添加失败！");
				}
			});
		} else {
			Ext.Msg.alert("提示", "请选择一条记录");
		}
	},
	closeWindow : function() {
		this.win.close();
	},
	
	loadGrid2 : function(cuid) {
		var self = this;
		if(cuid!=undefined){
			// 加载pda用户grid2数据
			PdaGroupAction.getPdaMemberData(cuid, function(data) {
				self.grid2.getStore().removeAll();
				if (data) {
					for (var i = 0; i < data.length; i++) {
						var record = new Ext.data.Record({
							pdaCode : data[i].DEVICE_CODE,
							userName : data[i].USER_NAME,
							deviceStatus : data[i].DEVICE_STATE,
							relatedGroup : data[i].LABEL_CN,
							CUID : data[i].CUID,
							remark : data[i].REMARK
						});
						self.grid2.getStore().insert(i, record);
					}
				} 
//				else {
//					Ext.Msg.alert("操作提示", "获取信息失败！");
//				}
			});
		}
	},
});