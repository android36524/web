Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/map/dms-map-deleteResource.js');
$importjs(ctx + '/rms/dm/wire/WireSegDeleteView.js');
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx+'/jslib/tp/topo-all.js');
$importjs(ctx+'/jslib/tp/tp-form.js');
$importjs(ctx+'/map/dms-tools.js');
$importjs(ctx+'/map/dms-utils.js');
$importjs(ctx+'/map/dms-wiretoductline.js');
$importjs(ctx+'/map/dms-sectionPic.js');
$importjs(ctx+'/map/dms-map-contextmenu.js');

$importjs(ctx+'/jslib/tp/component/gridpanel');
$importjs(ctx+'/jslib/tp/component/dialoggridpanel.js');
$importjs(ctx+'/map/dms-ductline.js');
$importjs(ctx + "/jsp_space/maintain/districts/query/tbar/FiberLinksMainView.js");


var accesspointCuid = Frame.grid.BaseGridPanel.initParamsByUrl(getGridCfgByCode()).cuid;
Ext.ns('Frame.combo');

Frame.combo.ResDmCombo = Ext.extend(Frame.combo.PopCombo, {
	anchor : '100%',
	popUrl : ctx + '/cmp_res/grid/EditorGridPanel.jsp',
	popParams : {
		hasMaintan : false,
		code:'service_dict_dm.'
	},
	constructor : function(config) {
		this.popParams.code = config.templateId;
		config.comboxCfg = {
				boName : 'ComboxTemplateComboxBO',
				url : this.popUrl + Ext.urlEncode(this.popParams, '?')
		};
		config.wincfg = {winArgs: 'left=200,top=100,width='+(screen.availWidth-400)+',height='+(screen.availHeight-200)};
		Frame.combo.ResDmCombo.superclass.constructor.call(this,config);
	},
	initComponent : function() {
		Frame.combo.ResDmCombo.superclass.initComponent.call(this);
	},
	 onTrigger2Click: function(i) {
		if (this.disabled == true) {
            return false;
        }
        if (this.fireEvent("beforeopen", this) !== false) {
            if (this.comboxCfg) {
                if (this.comboxCfg.url) {
                    var c = this.comboxCfg.url;
                    if (!this.wincfg.winArgs) {
                        var b = Ext.getBody().getWidth() - 50;
                        var f = Ext.getBody().getHeight() - 50;
                        this.wincfg.winArgs = "dialogWidth=" + b + "px;dialogHeight=" + f + "px;";
                    }
                    if (this.comboxCfg.urlArgs) {
                        c = UrlHelper.replaceUrlArguments(c, this.comboxCfg.urlArgs);
                    }
                    if(window.ActiveXObject){
	                    var d = window.showModalDialog(c, "selectRecord", this.wincfg.winArgs);     
	                    if (!Ext.isEmpty(d)) {
							this.setValue(d);
							var g = this.getStore().query(this.valueField, this.getValue(), false);
							var a = this.getStore().find(this.valueField, this.getValue(), 0, false);
							if (g && g.getCount() > 0) {
								this.fireEvent("select", this, g.get(0), a);
							}
		                }
	                 }else{
	                	 var scope = this;
	                	 window.setShowModalDialogValue = function(value){
	                		 var d = value;
	                		 if (!Ext.isEmpty(d)) {
	                			 scope.setValue(d);
	 							var g = scope.getStore().query(scope.valueField, scope.getValue(), false);
	 							var a = scope.getStore().find(scope.valueField, scope.getValue(), 0, false);
	 							if (g && g.getCount() > 0) {
	 								scope.fireEvent("select", scope, g.get(0), a);
	 							}
	 		                }
	                	 }
	                	 var b = window.screen.width - 150;
	                	 var f = window.screen.height - 150;
	                	 var winOption = "location=no,top=25,left=75,height="+f+",width="+b;
	                	 window.open(c+"&s_accesspointCuid="+accesspointCuid+"", "selectRecord", winOption);
	                 }
                    if (!Ext.isEmpty(d)) {
                        this.setValue(d);
                        var g = this.getStore().query(this.valueField, this.getValue(), false);
                        var a = this.getStore().find(this.valueField, this.getValue(), 0, false);
                        if (g && g.getCount() > 0) {
                            this.fireEvent("select", this, g.get(0), a);
                        }
                    }
                } else {
                    if (this.comboxCfg.panel) {
                        this.wincfg.winPanel = WindowHelper.openExtWin(this.comboxCfg.panel, this.wincfg.winArgs);
                    }
                }
            } else {
                return false;
            }
        }
    },
	setValue : function(v) {
		if(!Ext.isEmpty(v) && Ext.isArray(v)) {
			v = v[0].data;
			var obj = {};
			obj[this.valueField] = v.CUID;
			obj[this.displayField] = v.LABEL_CN;
			obj[this.dataField] = v;
			Frame.combo.ResDmCombo.superclass.setValue.call(this, obj);
		}else {
			Frame.combo.ResDmCombo.superclass.setValue.call(this, v);
		}
	}
});

Ext.reg("dmcombox", Frame.combo.ResDmCombo);
Frame.grid.BasePropertyGridPanel = Ext.extend(Frame.grid.EditorGrid,{
	option:'INSERT',
	canEdit : false,
	batchEdit:false,
	parentNodeId:'',
	constructor : function(config){
		Frame.grid.BasePropertyGridPanel.superclass.constructor.call(this,config);
		parentNodeId = config.parentNodeId;
	},
	
	initComponent : function(){
		this._buildButton();
		this.initMeta();
		Frame.grid.BasePropertyGridPanel.superclass.initComponent.call(this);
		this.on('beforeedit',function(e){
			var d = e.record.data;
			if(!this.canEdit){
				return false;
			}
			if(!d.editable){
				return false;
			}
		});
		this.addEvents(
				/**
				 * 开始新增、修改时触发后响应事件
				 */
			'beginEdit',
			/**
			 * 在保存按钮触发后响应事件
			 */
			'aftersubmit'
		);
	},
	listeners :{
	  beginEdit:function(cell){
	    
	  }
	},
	initMeta : function(templateId){
		if(templateId != null){
			this.editorId = templateId;
		}
		var editorMeta = {
			cuid : this.editorId
		};
		var scope = this;
		EditorPanelAction.getEditorMeta(editorMeta,function(result){
			scope._setPropertyMeta(result);
			scope._buildCustomEdit();
			scope._buildCustomRender();
			scope.refreshData.call(scope);
		});
	},
	
	_buildCustomRender : function(){
	
		var editorMeta = this.getPropertyMeta();
		var data = {};
		var columnMetas = editorMeta.editorColumnMetas;
		Ext.each(columnMetas,function(columnMeta){
			var component = Frame.util.EditorBuilder.buildRender(columnMeta);
			if(component){
				data[columnMeta.labelCn] = component;
			}
		});
		this.customRenderers = data;
		
	},
	
	_buildCustomEdit : function(){

		var editorMeta = this.getPropertyMeta();
		var data = {};
        var relatedPlugins = {}
		var columnMetas = editorMeta.editorColumnMetas;
		var s = this;
		Ext.each(columnMetas,function(columnMeta){
			var component = Frame.util.EditorBuilder.buildEditor(columnMeta,s);
			if(component){
				data[columnMeta.labelCn] = new Frame.grid.GridEditor(component);
                relatedPlugins[columnMeta.cuid] = component;
			}
		});

        this.customEditors = data;

        this.relatedPlugins = relatedPlugins;

        var scope = this;

        Ext.each(columnMetas,function(columnMeta){
            var component = scope.relatedPlugins[columnMeta.cuid];
            if(component){
                if(columnMeta.cuid && typeof tp === 'object' && tp.grid && tp.grid.plugins && tp.grid.plugins.combobox){
                    var pkg = tp.grid.plugins.combobox;
                    if(pkg[columnMeta.cuid]){
                        component.on('select',pkg[columnMeta.cuid],scope);
                    }
                }
            }
        });
	},
	
	_setPropertyMeta : function(meta){
		this.propertyMeta = meta;
	},
	
	setCanUpdate : function(){
		this.saveButton.disable();
		this.cancelButton.disable();
		this.modifyButton.enable();
		this.addButton.enable();
		this.removeButton.enable();
		this.addBatch.enable();
	},
	
	_buildButton : function(){
		this.addButton = new Ext.Button({
			text : '添加',
			iconCls : 'c_add',
			hidden: this.canAdd == null ? false : !this.canAdd,
			handler : function(){
				this.saveButton.enable();
				this.cancelButton.enable();
				this.addButton.disable();
				this.modifyButton.disable();
				this.removeButton.disable();
				this.addBatch.disable();
				this.canEdit = true;
				this.fireEvent('beginEdit',this);
			},
			scope : this
		});
		
		this.modifyButton = new Ext.Button({
			text : '修改',
			iconCls : 'c_script_edit',
			hidden: this.canMod == null ? false : !this.canMod,
			disabled : true,
			handler : function(){
				this.saveButton.enable();
				this.cancelButton.enable();
				this.addButton.disable();
				this.modifyButton.disable();
				this.removeButton.disable();
				this.addBatch.disable();
				this.canEdit = true;
				this.fireEvent('beginEdit',this);
			},
			scope : this
		});
		
		this.removeButton = new Ext.Button({
			text : '删除',
			iconCls : 'c_delete',
			hidden: this.canDel == null ? false : !this.canDel,
			disabled : true,
			handler : function(){
				var params = new Array();
				if(this.gridDatas == null || this.gridDatas.length == 0){
					Ext.Msg.alert("温馨提示","至少选择一条记录进行删除.");
					return;
				}
				
				Ext.each(this.gridDatas,function(param){
					params.push(param.data);
				});
				var editorMeta = this.buildMetaData(params);
				if(editorMeta.className == 'DUCT_SYSTEM'||editorMeta.className == 'POLEWAY_SYSTEM'
				||editorMeta.className == 'STONEWAY_SYSTEM'||editorMeta.className == 'HANG_WALL'
				||editorMeta.className == 'UP_LINE'||editorMeta.className == 'WIRE_SYSTEM'){
					Ext.MessageBox.show({
						title:'温馨提示',
						msg: '是否删除线设施及其关联点设施 <br />是：删除线设施及其关联点设施<br />否：仅删除线设施<br />取消：取消删除操作',
						buttons: Ext.MessageBox.YESNOCANCEL,
						fn: function(btn){
							var grid = this;
							if(btn == 'yes'){								
								editorMeta.sql='true';
								EditorPanelAction.remove(editorMeta,function(data){
									var result = data.result;
									grid.fireEvent('aftersubmit',grid,result);
									Ext.Msg.alert("删除成功.");
								});
							}else if(btn == 'no'){
								editorMeta.sql='false';
								EditorPanelAction.remove(editorMeta,function(data){
									var result = data.result;
									grid.fireEvent('aftersubmit',grid,result);
									Ext.Msg.alert("删除成功.");
								});
							}else{
								return;
							}
						},
						scope : this,
						icon: Ext.MessageBox.QUESTION
					});
				}else{
					Ext.Msg.confirm("温馨提示","当前有"+params.length+"条记录准备删除，请确认.",function(btn){
						if(btn == 'yes'){
							var scope = this;
							EditorPanelAction.remove(editorMeta,function(data){
								var result = data.result;
								scope.fireEvent('aftersubmit',scope,result);
								Ext.Msg.alert("删除成功.");
							});
						}else{
							return;
						}
					},this);
				}
				
		        
				
				this.canEdit = false;
			},
			scope : this
		});
		
		this.saveButton = new Ext.Button({
			text : '保存',
			disabled : true,
			hidden: this.canSave == null ? false : !this.canSave,
			iconCls : 'c_script_save',
			handler : function(){
				var scope = this;
				var source = this.getSource();//前台传入数据
				var keyMap = this.getMetaKeyMap();//没操作前字段属性
				var data = {};
				var nullMsg = '';
				for(var k in keyMap){
					data[k] = source[k] == null ? '' : source[k];
					if(k != "OBJECTID" && k != "CUID" && keyMap[k].nullable){
						if(Ext.isObject(source[k])){
							if("value" in source[k]){
								if(Ext.isEmpty(source[k].value)){
								   nullMsg += '【'+keyMap[k].labelCn+'】';
								}
							}else{
								if(Ext.isEmpty(source[k].LABEL_CN)){
								   nullMsg += '【'+keyMap[k].labelCn+'】';
								}
							}
						}else{
							if(Ext.isEmpty(source[k])){
								nullMsg += '【'+keyMap[k].labelCn+'】';
							}
						}
					}
				}
				if(!Ext.isEmpty(nullMsg)){
					Ext.Msg.alert('温馨提示','当前必填字段'+nullMsg+'为空,请检查.');
					return;
				}
				this.saveButton.disable();
				this.cancelButton.disable();
				this.addButton.enable();
				this.modifyButton.enable();
				this.removeButton.enable();
				this.addBatch.enable();
				data = Ext.apply(source,{CUID : {name : 'CUID',value : this.CUID}});
				data = Ext.apply(source,{OBJECTID : {name : 'OBJECTID',value : this.OBJECTID}});
				data = Ext.apply(source,{PARENTNODEID : {name:'PARENTNODEID',value:this.parentNodeId}});
				var editorMeta = this.buildMetaData(data);
				
				var action = null;
				if(this.option == 'UPDATE'){
					action = 'update';
				}else if(this.option == 'INSERT'){
					action = 'insert';
				}
				EditorPanelAction.save(editorMeta,action,function(data){
					var result = data.result;
					scope.fireEvent('aftersubmit',scope,result);
				});
				
				if(this.batchEdit==true){
					this.changeSignalMeta();
				}
				this.canEdit = false;
			},
			scope : this
		});
		
		this.cancelButton = new Ext.Button({
			text : '取消',
			disabled : true,
			hidden: this.canCancel == null ? false : !this.canCancel,
			iconCls : 'c_cancel',
			handler : function(){
				this.saveButton.disable();
				this.cancelButton.disable();
				this.addButton.enable();
				this.modifyButton.enable();
				this.removeButton.enable();
				this.addBatch.enable();
				if(this.batchEdit==true){
					this.changeSignalMeta();
				}
				this.canEdit = false;
			},
			scope : this
		});
		this.addBatch = new Ext.Button({
			text : '批量新增',
			disabled : false,
			iconCls : 'c_add',
			hidden: this.canBatch == null ? true : !this.canBatch,
			handler : function(){
				this.saveButton.enable();
				this.cancelButton.enable();
				this.addButton.disable();
				this.modifyButton.disable();
				this.removeButton.disable();
				this.addBatch.disable();
				this.canEdit = true;
				this.fireEvent('beginEdit',this);
				this.changeBatchMeta();
			},
			scope : this
		});
		this.bbar = [this.addButton,this.modifyButton,this.removeButton,this.addBatch,'->',this.saveButton,this.cancelButton];
	},
	changeSignalMeta:function(){
		this.batchEdit=false;
		var editorColumnMetas=[];  
		for(var i=0;i<this.propertyMeta.editorColumnMetas.length;i++){
			if(this.propertyMeta.editorColumnMetas[i].cuid!='FIRST_NAME_BATCH'&&this.propertyMeta.editorColumnMetas[i].cuid!='LAST_NAME_BATCH'
				&&this.propertyMeta.editorColumnMetas[i].cuid!='START_NO_BATCH'&&this.propertyMeta.editorColumnMetas[i].cuid!='NO_BATCH'
					&&this.propertyMeta.editorColumnMetas[i].cuid!='FIGURE_BATCH'){
				editorColumnMetas.push(this.propertyMeta.editorColumnMetas[i]);
			}else{
				var labelCn={
						categoryName: "系统属性",
						code: null,
						cuid: "LABEL_CN",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "名称",
						name: "labelCn",
						nullable: true,
						type: "string",
						value: undefined,
						width: 100,
						xtype: "string"
				};
				editorColumnMetas.push(labelCn);
			}
		}
		this.propertyMeta.editorColumnMetas=editorColumnMetas;
		this._buildCustomEdit();
		this._buildCustomRender();
		this.setSource(this.sinStore);
	},
	
	getDataBySource : function(){
		
		var source = this.getSource();
		var data = {};
		for(var k in source){
			data[k] = source[k].value;
		}
		
		return data;
	},
	
	changeBatchMeta:function(){
		this.batchEdit=true;
		var editorColumnMetas=[]; 
		this.sinStore=this.getSource();
		var batStore=[];
		for(var i=0;i<this.propertyMeta.editorColumnMetas.length;i++){
			if(this.propertyMeta.editorColumnMetas[i].cuid!='LABEL_CN'){
				editorColumnMetas.push(this.propertyMeta.editorColumnMetas[i]);
			}else{
				var firstName={
						categoryName: "系统属性",
						code: null,
						cuid: "FIRST_NAME_BATCH",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "前缀",
						name: "前缀",
						nullable: true,
						type: "string",
						value: undefined,
						width: 100,
						xtype: "string"
				};
				editorColumnMetas.push(firstName);
				var lastName={
						categoryName: "系统属性",
						code: this.propertyMeta.className+"LastName",
						cuid: "LAST_NAME_BATCH",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "后缀",
						name: "后缀",
						nullable: true,
						type: "enumbox",
						value: undefined,
						width: 100,
						xtype: "enumbox"
				};
				editorColumnMetas.push(lastName);
				var figure={
						categoryName: "系统属性",
						code: "ManhleFigure",
						cuid: "FIGURE_BATCH",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "编号位数",
						name: "编号位数",
						nullable: true,
						type: "enumbox",
						value: undefined,
						width: 100,
						xtype: "enumbox"
				};
				editorColumnMetas.push(figure);
				var start={
						categoryName: "系统属性",
						code: null,
						cuid: "START_NO_BATCH",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "起始编号",
						name: "起始编号",
						nullable: true,
						type: "long",
						value: undefined,
						width: 100,
						xtype: "long"
				};
				editorColumnMetas.push(start);
				var no={
						categoryName: "系统属性",
						code: null,
						cuid: "NO_BATCH",
						customEditor: null,
						customRender: null,
						editable: true,
						group: "系统属性",
						hidden: false,
						labelCn: "数量",
						name: "数量",
						nullable: true,
						type: "long",
						value: undefined,
						width: 100,
						xtype: "long"
				};
				editorColumnMetas.push(no);
			}
		}
		this.propertyMeta.editorColumnMetas=editorColumnMetas;
		this._buildCustomEdit();
		this._buildCustomRender();
		var array = new Array();
		array.push(this.getDataBySource());
		this.refreshData(array);
	},
	getMetaKeyMap : function(){

		var editorMeta = this.getPropertyMeta();//读取xml文件
		var editorColumnMetas = editorMeta.editorColumnMetas;//获取xml配置中字段的属性
		var data = {};
		Ext.each(editorColumnMetas,function(meta){//循环xml中配置的字段
			data[meta.cuid] = meta;
		});
		return data;
	},
	refreshData : function(result){
		var me = this;
		var resultData = {};
		var datas = [];
		if(result != null && typeof result != 'object'){
			datas = Ext.decode(result);
		}else{
			datas = result;
		}
		if(datas == null || datas.length == 0){
			data = {};
		}else{
			data = datas[0];
		}

		var keyMap = this.getMetaKeyMap();
		
		for(var k in keyMap){
			if(keyMap[k] == null || keyMap[k].cuid == 'CUID'){
				this.CUID = data[k];
				continue;
			}
			if(keyMap[k] == null || keyMap[k].cuid == 'OBJECTID'){
				this.OBJECTID = data[k];
				continue;
			}
			if(k == "ORIG_POINT_CUID"){
			  if("undefined" == (typeof me.gridDatas) || "undefined" == (typeof me.gridDatas[0])){
			    resultData[k] = Ext.apply(keyMap[k],{
					name :  keyMap[k].labelCn,
					value : data[k],
					group : keyMap[k].categoryName,
					type : keyMap[k].xtype,
					nullable : keyMap[k].nullable
				});
			  }else{
				 
				 
				 resultData[k] = Ext.apply(keyMap[k],{
					name :  keyMap[k].labelCn,
					value : data[k],
					group : keyMap[k].categoryName,
					type : keyMap[k].xtype,
					nullable : keyMap[k].nullable
				});
			  }
			}else{
				resultData[k] = Ext.apply(keyMap[k],{
					name :  keyMap[k].labelCn,
					value : data[k],
					group : keyMap[k].categoryName,
					type : keyMap[k].xtype,
					nullable : keyMap[k].nullable
				});
			}
		}
		this.setSource(resultData);
	},
	getPropertyMeta : function(){
		
		return this.propertyMeta;
	},
	
	buildMetaData : function(datas){
		var params = new Array();
		if(typeof datas == 'array'){
			Ext.each(datas,function(data){
				params.push(data);
			});
		}else{
			params.push(datas);
		}
		
		var editorMeta = {};
		editorMeta = this.getPropertyMeta();
		editorMeta = Ext.apply(editorMeta,{
			paras : Ext.encode(params)
		});
		return editorMeta;
	},
	
	refreshGridData : function(gridData,datas){
		
		this.gridDatas = datas;
		var editorMeta = this.buildMetaData(gridData);
	    var scope = this;
		MaskHelper.mask(scope.getEl(),"读取中,请稍候...");
		EditorPanelAction.getEditorData(editorMeta,function(data){
			var result = data.result;
			scope.refreshData(result);
			MaskHelper.unmask(scope.getEl());
		});
	}
	
});
Frame.grid.plugins.tbar.InterwireApointGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.InterwireApointGridTbar.superclass.constructor.call(this);
		this.grid.on('click',function(node){
			var records=this.grid.getSelectionModel().getSelections();
			Ext.each(this.changeButtonArray,function(button){
				if(Ext.isEmpty(records) || records.length==0 || records.length>1){
					button.disable();
			    }else{
			    	button.enable();
			    }
			});
		},this);
		
		var fiberManageButton = new Ext.Button({
			text : '层间纤芯管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._fiberView
		});
		var fiberRelatedButton = new Ext.Button({
			text : '纤芯关联',
			disabled : true,
			iconCls : 'c_page_white_magnify',
			scope : this,
			handler : this.fiberRelated
		});
		this.changeButtonArray = new Array();
		this.changeButtonArray.push(fiberManageButton);
		this.changeButtonArray.push(fiberRelatedButton);
		return [fiberManageButton,'-',fiberRelatedButton
//		        ,'-',{
//					xtype : 'splitbutton',
//					text : '导入',
//					iconCls : 'c_page_white_link',
//					scope : this,
//					menu : [{
//						text : '层间光缆导入',
//						iconCls : 'c_page_white_link',
//						scope : this,
//						handler : this._import
//					},{
//						text : '模板下载',
//						iconCls : 'c_page_white_link',
//						scope : this,
//						handler : this._export
//					}]
//				}
		];
	},
	
	fiberRelated : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
	    var cuid=records[0].data['CUID'];
		var name = records[0].data['LABEL_CN'];
		var fiberLinkPanel = new Frame.op.FiberLinksPanel({
			cuid : cuid,
			name : name,
			bmClassId : 'INTER_WIRE'
		});//FiberLinksMainView.js
		
		var scope = this;
		var win = WindowHelper.openExtWin(fiberLinkPanel, {
			title : '纤芯关联',
			width : 800,
			height : 450,
			buttons : [{
				text : '关闭',
				scope : this,
				handler : function() {
					win.hide();
				}
			}]
		});
	},
	_fiberView:function(){
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
			Ext.Msg.alert('温馨提示', '请选择一条记录.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var url = '/rms/dm/wire/interfibermanapanel.jsp?code=service_dict_dm.DM_FIBER_INTERWIRE&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
				labelCn + '纤芯管理');
	}
//	_import:function(){
//		var winCfg={
//				width:600,
//				height:200
//		};
//		this.filePanel=new IRMS.dm.FilePanel({
//	    	 title: '导入',
//	    	 width: 100,
//	    	 height:20,
//	    	 inputParams : this.inputParams,
//	    	 key:'INTER_WIRE'
//		});
//		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
//	},
//	_export:function(){
//		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("层间光缆"));
//		window.open(url);
//	}
});
