Ext.ns('Frame.grid.plugins.bbar');
$importjs(ctx+'/dwr/interface/PortsManageAction.js');
$importjs(ctx+'/rms/dm/common/BatchNamesPanel.js');

Frame.grid.plugins.bbar.FiberTerminalPointBbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.bbar.FiberTerminalPointBbar.superclass.constructor.call(this);
		
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
			text : '批量命名',
			iconCls : 'c_text_replace',
			disabled: true,
			scope : this,
			handler : this._batchRenamePort
		}, space, {
			text : '按行添加',
			iconCls : 'c_text_padding_bottom',
			disabled: true,
			scope : this,
			handler : this._addPortByRow
		}, space, {
			text : '删除',
			iconCls : 'c_table_row_delete',
			disabled: true,
			scope : this,
			handler : this._deletePort
		}, space, {
			text : '关联信息',
			iconCls : 'c_table_row_Link',
			disabled: true,
			scope : this,
			handler : this._queryLink
		}];
	},
	
	//批量命名
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
		
		var batchNamesPanel=new Frame.grid.edit.BatchNamesPanel({postfix:['号终端盒端子'],number:records.length});
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
						scope.doQuery();
					});
				}
			},{
				text: '关闭',
				handler: function() {
					win.close();
				}						    	     
			}]
		});					    
	},
	
	//按行添加
	_addPortByRow: function() {
		var scope = this.grid;
		var prefix = new Ext.form.TextField({width : 145, fieldLabel:'前缀'});
		var suffix = new Ext.form.ComboBox({fieldLabel:'后缀',
			triggerAction: 'all',
			width : 145,
			forceSelection: true,
			selectOnFocus: true,
			editable: false,
		    mode: 'local',
		    store: new Ext.data.ArrayStore({
		        fields: ['text'],
		        data: [['号终端盒端子'], ['#']]
		    }),
		    valueField: 'text',
		    displayField: 'text'
		  });

		suffix.setValue(suffix.store.getAt(0).data.text);
		
		var node = scope.tree.getSelectionModel().selNode;
		
		if(node==null){
			node=scope.tree.root.childNodes[0];
		}
		
		if (node!=null && node.getDepth() == 2) {
			node = node.parentNode;
		}

		var startNumber = new Ext.form.NumberField({width : 145, fieldLabel:'起始编号', value: 1,allowBlank:false});
		var rowNumber = new Ext.form.NumberField({width : 145, fieldLabel:'行数', value: 1,allowBlank: false});
		
		var defaultColumns = node.attributes.data.PORT_COL;
		
		var columnNumber;
		if (defaultColumns) {
			columnNumber = new Ext.form.NumberField({
				width : 145, 
				fieldLabel:'列数', 
				value: defaultColumns,
				disabled: true,
				allowBlank:false
			});
		}else {
			columnNumber = new Ext.form.NumberField({
				width : 145, 
				fieldLabel:'列数', 
				value: 1,
				allowBlank:false
			});
		}
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
					var but = this;
					but.setDisabled(true);
					var data = {
						'CUID': node.attributes.cuid,
						'PREFIX': prefix.getValue(),
						'POSTFIX': suffix.getValue(),
						'STARTINDEX': startNumber.getValue(),
						'ADDROWS': rowNumber.getValue(),
						'ADDCOLS': columnNumber.getValue(),
						'scene' : scope.scene,
					    'segGroupCuid' : scope.segGroupCuid
					};
					//增加DWR调用逻辑
					PortsManageAction.addPortsByRow(data, {
						callback: function(result) {
							but.setDisabled(false);
							if(Ext.isEmpty(result)) {
								Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
							}
							scope.tree.root.reload();
							scope.fireEvent('metaloaded');
							win.close();
						}, 
						exceptionHandler: function exceptionHandler(exceptionString, exception){  
							but.setDisabled(false);
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
	
	//删除
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
				//增加DWR调用逻辑
				PortsManageAction.deletePorts(datas, {
					callback: function(result) {
						if(Ext.isEmpty(result)) {
							Ext.Msg.alert('系统异常', '调用服务器功能发生异常！');
						}
						scope.fireEvent('metaloaded');
					}, 
					exceptionHandler: function exceptionHandler(exceptionString, exception){  
						Ext.Msg.alert("系统异常", exceptionString);
					}
				});
			}
		});
	},
	
	//关联关系
	_queryLink : function(){
		var scope = this.grid;
		var selections = scope.getSelectionModel().getSelections();
		if (selections.length<1) {
			return;
		}
		scope.ports = [];
		var datas = [];
		for (var i=0; i<selections.length; i++) {
			var portCuid = {"CUID":selections[i].get("CUID")};
			datas.push(portCuid);
			//如果传map类型，后面action一定要写范式，如Map<String,String>
			scope.ports.push(selections[i]);
		}
		
		PortsManageAction.getLinkFiber(datas, {
			callback: function(results) {
				if(results){
					for(var i=0;i<results.length;i++){
						var result = results[i];
						var pid = result.PORT_CUID;
						var relatedSystem = result.RELATED_WIRESYSTEM;
						var relatedSeg = result.RELATED_WIRESEG;
						var fiberNO = result.FIBERNO;
						for(var j=0;j<scope.ports.length;j++){
							var port = scope.ports[j],
								cid = scope.ports[j].get('CUID');
							if(cid == pid){
								port.set('RELATED_WIRESYSTEM',relatedSystem);
								port.set('RELATED_WIRESEG',relatedSeg);
								port.set('FIBERNO',fiberNO);
								break;
							}
						}
					}
				}
			}, 
			exceptionHandler: function exceptionHandler(exceptionString, exception){  
				Ext.Msg.alert("系统异常", exceptionString);
			}
		});
	}
});
