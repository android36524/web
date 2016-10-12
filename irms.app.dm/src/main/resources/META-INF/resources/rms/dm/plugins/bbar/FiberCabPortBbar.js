Ext.ns('Frame.grid.plugins.bbar');
$importjs(ctx+'/dwr/interface/PortsManageAction.js');
$importjs(ctx+'/rms/dm/common/BatchNamesPanel.js');

Frame.grid.plugins.bbar.FiberCabPortBbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.bbar.FiberCabPortBbar.superclass.constructor.call(this);
		
		this.grid.on('changeState', this._setButtonState, this);

		return this._createToolbarButtons();
	},
	_setButtonState: function(state, states) {
		for (var i=0; i < this.grid.bottomToolbar.items.items.length; i++) {
			var button = this.grid.bottomToolbar.items.items[i];
			var enableButtons = states[state].enableButtons;
			var find = false;
			for (var j=0; j<enableButtons.length; j++) {
				if (button.text == enableButtons[j]) {
					find = true;
					break;
				}
			}

			if (find) {
				button.setDisabled(false);
			} else {
				button.setDisabled(true);
			}
		}
	},
	_createToolbarButtons : function() {
		var space = {xtype: 'tbspacer', width: 5};
		return [{xtype: 'tbfill'}, {
			text : '添加模块',
			iconCls : 'c_table_add',
			disabled: true,
			scope : this,
			handler : this._addModule
		}, space, {
			text : '删除模块',
			iconCls : 'c_table_delete',
			disabled: true,
			scope : this,
			handler : this._deleteModule
		}, space, {
			text : '修改模块',
			iconCls : 'c_table_edit',
			disabled: true,
			scope : this,
			handler : this._modifyModule
		}, space, {
			text : '按行添加端子',
			iconCls : 'c_text_padding_bottom',
			disabled: true,
			scope : this,
			handler : this._addPortByRow
//		}, space, {
//			text : '按列添加端子',
//			iconCls : 'c_text_padding_right',
//			disabled: true,
//			scope : this,
//			handler : this._addPortByColumn
//		}, space, {
//			text : '批量修改',
//			iconCls : 'c_plugin_edit',
//			disabled: true,
//			scope : this,
//			handler : this._batchModifyPort
		}, space, {
			text : '删除',
			iconCls : 'c_table_row_delete',
			disabled: true,
			scope : this,
			handler : this._deletePort
		}, space, {
			text : '批量命名',
			iconCls : 'c_text_replace',
			disabled: true,
			scope : this,
			handler : this._batchRenamePort
		} ];
	},
	isValid: function(items) {
		var validate = true;
		for (var i=0; i<items.length; i++) {
			if (items[i].isValid) {
				validate = validate && items[i].isValid();
			}
		}
		return validate;
	},
	_addModule : function() {
		var moduleName = new Ext.form.TextField({fieldLabel:'模块名称', allowBlank:false});
		var rowNumber = new Ext.form.NumberField({fieldLabel:'行号', value: 1, allowBlank:false});
		var columnNumber = new Ext.form.NumberField({fieldLabel:'列号', value: 1, allowBlank:false});
		var me = this;
		var scope = this.grid;
		
		var win = new Ext.Window({
			title : '增加模块',
			width : 270,
			resizable: false,
			hideHeaders: true,
			modal: true,
			items : [new Ext.form.FormPanel({
				layout: 'form',
				border: false,
				bodyStyle:'padding:10px;background:transparent;',
				labelWidth: 70,
				labelAlign: 'right',
				autoHeight: true,
				autoWidth: true,
				items:[
				       moduleName,
				       rowNumber,
				       columnNumber
				]
			})],
			buttons : [{
				text: '确定',
				handler: function() {
					if (!me.isValid(win.items.items[0].items.items)) {
						return;
					}
					var but = this;
					if(moduleName.getValue().trim()==""){
						Ext.Msg.alert('提示', '名称有误！');
						but.setDisabled(false);
						return;
					}
					var data = {
						'RELATED_DEVICE_CUID': scope.cuid,
						'LABEL_CN': moduleName.getValue(),
						'MODULE_ROW': rowNumber.getValue(),
						'MODULE_COL': columnNumber.getValue(),
						'scene' : scope.scene,
					    'segGroupCuid' : scope.segGroupCuid
					};
					MaskHelper.mask(scope.tree.getEl());
					//增加DWR调用逻辑
					PortsManageAction.addFiberCabModule(data, {
						callback: function(result) {
							but.setDisabled(false);
							if(Ext.isEmpty(result)) {
								Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
							}else {
								//在树图中添加模块
								scope.tree.root.reload();
								win.close();
							}
							MaskHelper.unmaskAll();
						}, 
						exceptionHandler: function exceptionHandler(exceptionString, exception){  
							but.setDisabled(false);
							MaskHelper.unmaskAll();
							Ext.Msg.alert("系统异常", exceptionString);
						}
					});
				}
			}, {
				text: '取消',
				handler: function() {
					win.close();
				}
			}]
		});
		win.show();
	},
	_deleteModule: function() {
		var scope = this.grid;
		Ext.MessageBox.confirm('注意', '您确定删除模块吗？', function(btn, text) {
			if (btn == 'yes') {
				var data = {
					'CUID': scope.tree.getSelectionModel().selNode.attributes.cuid
				};
				MaskHelper.mask(scope.tree.getEl());
				//增加DWR调用逻辑
				PortsManageAction.deleteFiberCabModule(data, {
					callback: function(result) {
						if(Ext.isEmpty(result)) {
							Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
						}
						scope.tree.root.reload();
						scope.fireEvent('metaloaded');
						MaskHelper.unmaskAll();
					}, 
					exceptionHandler: function exceptionHandler(exceptionString, exception){  
						MaskHelper.unmaskAll();
						Ext.Msg.alert("系统异常", exceptionString);
					}
				});
			}
		});
	},
	_modifyModule: function() {
		var moduleName = new Ext.form.TextField({fieldLabel:'新模块名称', allowBlank:false});
		var me = this;
		var scope = this.grid;

		var win = new Ext.Window({
			title : '修改模块',
			width : 270,
			resizable: false,
			hideHeaders: true,
			modal: true,
			items : [new Ext.form.FormPanel({
				layout: 'form',
				border: false,
				bodyStyle:'padding:10px;background:transparent;',
				labelWidth: 70,
				labelAlign: 'right',
				autoHeight: true,
				autoWidth: true,
				items:[moduleName]
			})],
			buttons : [{
				text: '确定',
				handler: function() {
					if (!me.isValid(win.items.items[0].items.items)) {
						return;
					}
					var but = this;
					if(moduleName.getValue().trim()==""){
						Ext.Msg.alert('提示', '名称有误！');
						but.setDisabled(false);
						return;
					}
					var data = {
						'RELATED_DEVICE_CUID': scope.cuid,
						'LABEL_CN': moduleName.getValue(),
						'CUID': scope.tree.getSelectionModel().selNode.attributes.cuid
					};
					MaskHelper.mask(scope.tree.getEl());
					//增加DWR调用逻辑
					PortsManageAction.modifyFiberCabModule(data, {
						callback: function(result) {
							but.setDisabled(false);
							if(Ext.isEmpty(result)) {
								Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
							}
							scope.tree.root.reload();
							MaskHelper.unmaskAll();
							win.close();
						}, 
						exceptionHandler: function exceptionHandler(exceptionString, exception){  
							but.setDisabled(false);
							MaskHelper.unmaskAll();
							Ext.Msg.alert("系统异常", exceptionString);
						}
					});
				}
			}, {
				text: '取消',
				handler: function() {
					win.close();
				}
			}]
		});
		win.show();
	},
	_addPortByRow: function() {
		var scope = this.grid;
		var prefix = new Ext.form.TextField({width : 145, fieldLabel:'前缀'});
		var suffix = new Ext.form.ComboBox({fieldLabel:'后缀',
			triggerAction: 'all',
			width : 145,
			allowBlank:false,
			forceSelection: true,
			selectOnFocus: true,
			editable: false,
		    mode: 'local',
		    store: new Ext.data.ArrayStore({
		        fields: ['text'],
		        data: [[' '],['号光交接箱端子'], ['#']]
		    }),
		    valueField: 'text',
		    displayField: 'text'
		  });

		suffix.setValue(suffix.store.getAt(0).data.text);
		var node = scope.tree.getSelectionModel().selNode;
		if (node.getDepth() == 3) {
			node = node.parentNode;
		}else if (node.getDepth() != 2) {
			return;
		}
		
		var startNumber = new Ext.form.NumberField({width : 145, fieldLabel:'起始编号', value: 1, allowBlank:false});
		var rowNumber = new Ext.form.NumberField({width : 145, fieldLabel:'行数', value: 1, allowBlank:false});
		
		var defaultColumns = node.attributes.data.PORT_COL;
		var columnNumber;
		if (defaultColumns) {
			columnNumber = new Ext.form.NumberField({
				width : 145, 
				fieldLabel:'列数', 
				value: defaultColumns,
				disabled: true
			});
		}else {
			columnNumber = new Ext.form.NumberField({
				width : 145, 
				fieldLabel:'列数', 
				value: 1
			});
		}
		var me = this;
		var scope = this.grid;

		var win = new Ext.Window({
			title : '批量增加端子',
			width : 270,
			resizable: false,
			hideHeaders: true,
			modal: true,
			items : [new Ext.form.FormPanel({
				layout: 'form',
				border: false,
				bodyStyle:'padding:10px;background:transparent;',
				labelWidth: 70,
				labelAlign: 'right',
				autoHeight: true,
				autoWidth: true,
				items:[
				       prefix,
				       suffix,
				       startNumber,
				       rowNumber,
				       columnNumber
				]
			})],
			buttons : [{
				text: '确定',
				handler: function() {
					if (!me.isValid(win.items.items[0].items.items)) {
						return;
					}
					var but = this;
					if(rowNumber.getValue()<0){
						Ext.Msg.alert('提示', '行号不能小于0！');
						but.setDisabled(false);
						return;
					}
					var data = {
						'RELATED_MODULE_CUID': node.attributes.cuid,
						'PREFIX': prefix.getValue(),
						'POSTFIX': suffix.getValue(),
						'STARTINDEX': startNumber.getValue(),
						'ADDROWS': rowNumber.getValue(),
						'ADDCOLS': columnNumber.getValue(),
						'scene' : scope.scene,
					    'segGroupCuid' : scope.segGroupCuid
					};
					MaskHelper.mask(scope.tree.getEl());
					//增加DWR调用逻辑
					PortsManageAction.addPortsByRow(data, {
						callback: function(result) {
							but.setDisabled(false);
							if(Ext.isEmpty(result)) {
								Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
							}
							scope.tree.root.reload();
							scope.fireEvent('metaloaded');
							MaskHelper.unmaskAll();
							win.close();
						}, 
						exceptionHandler: function exceptionHandler(exceptionString, exception){  
							but.setDisabled(false);
							MaskHelper.unmaskAll();
							Ext.Msg.alert("系统异常", exceptionString);
						}
					});
				}
			}, {
				text: '取消',
				handler: function() {
					win.close();
				}
			}]
		});
		win.show();
	},
	_addPortByColumn: function() {
		var prefix = new Ext.form.TextField({width : 145, fieldLabel:'前缀'});
		var suffix = new Ext.form.ComboBox({fieldLabel:'后缀',
			triggerAction: 'all',
			width : 145,
			allowBlank:false,
			forceSelection: true,
			selectOnFocus: true,
			editable: false,
		    mode: 'local',
		    store: new Ext.data.ArrayStore({
		        fields: ['text'],
		        data: [[' '],['号光交接箱端子'], ['#']]
		    }),
		    valueField: 'text',
		    displayField: 'text'
		  });

		suffix.setValue(suffix.store.getAt(0).data.text);
		var startNumber = new Ext.form.NumberField({width : 145, fieldLabel:'起始编号', value: 1, allowBlank:false});
		var columnNumber = new Ext.form.NumberField({width : 145, fieldLabel:'列数', value: 1, allowBlank:false});
		var me = this;
		var scope = this.grid;
		
		var node = scope.tree.getSelectionModel().selNode;
		if (node.getDepth() == 3) {
			node = node.parentNode;
		}else if (node.getDepth() != 2) {
			return;
		}
		
		var win = new Ext.Window({
			title : '批量增加端子',
			width : 270,
			resizable: false,
			hideHeaders: true,
			modal: true,
			items : [new Ext.form.FormPanel({
				layout: 'form',
				border: false,
				bodyStyle:'padding:10px;background:transparent;',
				labelWidth: 70,
				labelAlign: 'right',
				autoHeight: true,
				autoWidth: true,
				items:[
				       prefix,
				       suffix,
				       startNumber,
				       columnNumber
				]
			})],
			buttons : [{
				text: '确定',
				handler: function() {
					if (!me.isValid(win.items.items[0].items.items)) {
						return;
					}
					var but = this;
					but.setDisabled(true);
					var data = {
						'RELATED_MODULE_CUID': node.attributes.cuid,
						'PREFIX': prefix.getValue(),
						'POSTFIX': suffix.getValue(),
						'STARTINDEX': startNumber.getValue(),
						'ADDCOLS': columnNumber.getValue()
					};
					MaskHelper.mask(scope.tree.getEl());
					//增加DWR调用逻辑
					PortsManageAction.addPortsByColumn(data, {
						callback: function(result) {
							but.setDisabled(false);
							if(Ext.isEmpty(result)) {
								Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
							}
							scope.fireEvent('metaloaded');
							MaskHelper.unmaskAll();
							win.close();
						}, 
						exceptionHandler: function exceptionHandler(exceptionString, exception){  
							but.setDisabled(false);
							MaskHelper.unmaskAll();
							Ext.Msg.alert("系统异常", exceptionString);
						}
					});
				}
			}, {
				text: '取消',
				handler: function() {
					win.close();
				}
			}]
		});
		win.show();
	},
	_deletePort: function() {
		var scope = this.grid;
		var selections = scope.getSelectionModel().getSelections();
		if (selections.length<1) {
			return;
		}
		
		Ext.MessageBox.confirm('注意', '您确定删除所选端子吗？', function(btn, text) {
			if (btn == 'yes') {
				var datas = [];
				for (var i=0; i<selections.length; i++) {
					var data = {
						'CUID': selections[i].get("CUID")
					};
					datas.push(data);
				}
				MaskHelper.mask(scope.tree.getEl());
				//增加DWR调用逻辑
				PortsManageAction.deletePorts(datas, {
					callback: function(result) {
						if(Ext.isEmpty(result)) {
							Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
						}
						scope.fireEvent('metaloaded');
						scope.tree.root.reload();
						MaskHelper.unmaskAll();
					}, 
					exceptionHandler: function exceptionHandler(exceptionString, exception){  
						MaskHelper.unmaskAll();
						Ext.Msg.alert("系统异常", exceptionString);
					}
				});
			}
		});
	},
	_batchRenamePort: function() {
		var scope = this.grid;
		var records=scope.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length==0){
			Ext.Msg.alert('温馨提示','请选择要批量命名的行.');
			return;
		}
		this.ObjArr = [];
		for(var i=0;i<records.length;i++){
			var obj=records[i];
			if(Ext.isEmpty(obj)){
				Ext.Msg.alert('温馨提示','当前选中数据,不包含CUID字段,请检查配置');
				return;  
			}
			this.ObjArr[i] = obj;
		}
		for(var j=0;j<records.length;j++){
			var labelCn=records[j].data['LABEL_CN'];
			if(Ext.isEmpty(labelCn)){
				Ext.Msg.alert('温馨提示','当前选中数据,不包含LABEL_CN字段,请检查配置');
				return;
			}						    	
		}
		var batchNamesPanel=new Frame.grid.edit.BatchNamesPanel({postfix:[' ','号光交接箱端子','号'],number:records.length});
		var win=WindowHelper.openExtWin(batchNamesPanel,{
			title: '批量命名',
			width: 300,
			height:221,
			buttons: [{
				text: '确定',
				scope:this,
				handler: function(){
					batchNamesPanel._save(this.ObjArr,function(){
						scope.fireEvent('metaloaded');
						MaskHelper.unmaskAll();
						win.close();
						batchNamesPanel = null;
//						scope.doQuery();
					});
				}
			},{
				text: '关闭',
				handler: function() {
					win.close();
				}						    	     
			}]
		});					    
	}
});
