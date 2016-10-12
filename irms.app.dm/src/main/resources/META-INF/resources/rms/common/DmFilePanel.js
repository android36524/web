Ext.ns("IRMS.dm.common");
$importcss(ctx + "/jslib/ext/ux/fileuploadfield/css/fileuploadfield.css");
$importjs(ctx + "/jslib/ext/ux/fileuploadfield/FileUploadField.js");

IRMS.dm.common.FilePanel = Ext.extend(Ext.Panel, {
	title : (Ext.isEmpty(this.title) ? '附件管理' : this.title),
	layout : 'border',
	allowBlank : true,
	constructor : function(config){
		if (!config.inputParams) {
			config.inputParams = {
				readOnly : true,
				preview : false
			};
		}
		IRMS.dm.common.FilePanel.superclass.constructor.call(this, config);
		this.on('afterrender', function() {
			this.setCmpReadOnly((this.type == null || this.relatedServiceCuid == null) || this.readOnly);
		}, this);
	},

	initComponent : function() {
		this._initItems();
		IRMS.dm.common.FilePanel.superclass.initComponent.call(this);
	},

	_initItems : function() {
		var scope = this;
		this.items = [];

		this.fileUploadField = new Ext.ux.form.FileUploadField({
			emptyText : '选择一个文件',
			name : 'file',
			fieldLabel : '',
			buttonCfg : {
				iconCls : 'c_image_add'
			}
		});

		this.fileUploadPanel = new Ext.Panel({
			flex : 1,
			border : false,
			layout : 'fit',
			items : [this.fileUploadField]
		});

		this.buttonBar = new Ext.Panel({
			flex : 1,
			border : false,
			layout : 'column',
			items : [{
				xtype : 'button',
				text : '上传',
				iconCls : 'c_page_white_get',
				scope : this,
				handler : this.onBtnImportClick
			},{
				xtype : 'button',
				text : '删除',
				code : 'delete',
				iconCls : 'c_page_white_delete',
				scope : this,
				handler : this.onBtnDeleteClick
			},{
				xtype : 'button',
				text : '下载',
				iconCls : 'c_page_white_put',
				scope : this,
				code : 'download',
				handler : this.onBtnDownloadClick
			}]
		});

		this.grid = new Frame.grid.DataGridPanel({
			region : 'south',
			enableContextMenu : true,
			height : 264,
			width : 400,
			layout : 'fit',
			loadData : true,
			hasPageBar : true,
			hasBbar : true,
			gridCfg : {
				cfgParams: {
					templateId : Ext.isEmpty(this.templateId) ? 'DM.DESIGNER.FILE.GRID' : this.templateId
				},
				keyParams : {
					relatedServiceCuid : {
						key : 'relatedServiceCuid',
						value : this.relatedServiceCuid
					},
					type : {
						key : 'type',
						value : this.type
					}
				}
			},
			viewConfig : {
				forceFit : true
            },
            listeners : {
            	'rowclick' : function(grid, rowIndex, e) {
            		if (scope.preview) {
	            		var records = grid.getSelectionModel().getSelections();
	            		var addre = records[0].data['ATTACH_ADDRESS'];
	            		var address = addre.replace(/\\/g, "/");
	            		var url = ctx + "/dm/readImg.do?file=" + address;
	            		scope.image.update('<img width="300" height="268" src="' + url + '" />');
            		}
            	}
            }
		});

		this.uploadForm  = new Ext.FormPanel({
			region : 'center',
			style : 'border : 1px solid #a9bfd3; borderWidth : 1px 1px 0 1px;padding : 2',
			fileUpload : true,
			height : 28,
			width : 400,
			layout : 'hbox',
			cls: 'x-panel-mc',
			defaults : {margins : '0 0 0 5'},
			items : [this.fileUploadPanel, this.buttonBar]
		});

		this.left = new Ext.Panel({
			region : 'center',
			items : [this.uploadForm, this.grid]
		});

		this.image = new Ext.Panel({
			title : '图片预览',
			region : 'east',
			border : false,
			width : 300,
			height : 320,
			html : '<div style="height:268px;width:300px;text-align:center;line-height:240px;"><font size="5">[图片预览]</font></div>'
		});

		if (this.preview) {
			this.items = [this.left, this.image];
		} else {
			this.items = [this.uploadForm, this.grid];
		}
	},

	onBtnImportClick : function() {
		var scope = this;
		var filePath = this.fileUploadField.getValue();
		if (Ext.isEmpty(filePath)) {
			Ext.Msg.show({
				title : "错误",
				msg : "请选择要导入的文件！",
				buttons : Ext.Msg.OK,
				minWidth : 300,
				icon : Ext.Msg.ERROR
			});
		} else {
			if (this.preview) {
				var datas = this.grid.getStore().getRange();
				if (!Ext.isEmpty(datas) && datas.length >= 5) {
					Ext.Msg.show({
						title : "错误",
						msg : "只能上传五张图片！",
						buttons : Ext.Msg.OK,
						minWidth : 300,
						icon : Ext.Msg.ERROR
					});
					return;
				}
				
				var tempFilePath = filePath.toLowerCase();
				if (tempFilePath.indexOf("jpeg") == -1 && tempFilePath.indexOf("jpg") == -1 && tempFilePath.indexOf("gif") == -1 && tempFilePath.indexOf("png") == -1) {
					Ext.Msg.show({
						title : "错误",
						msg : "只能上传jpeg,jpg,gif,png格式的图片！",
						buttons : Ext.Msg.OK,
						minWidth : 300,
						icon : Ext.Msg.ERROR
					});
					return;
				}
			}
			this.importData();
		}
	},

	importData : function() {
		var type = this.type;
		var validateSize = (this.preview == true ? false : true);
		var relatedServiceCuid = this.relatedServiceCuid
		var scope = this;
		MaskHelper.mask(this.getEl(), '附件上传中，请稍候...');
		this.uploadForm.getForm().submit({
			scope : this,
            url : ctx + '/dm/fileImport.do',
            params : {
            	type : type,
            	relatedServiceCuid : relatedServiceCuid,
            	validateSize : validateSize
            },
            method : 'POST',
            success : function(form, action) {
                if (action.result.success) {
					scope.grid.doQuery();
                }
                Frame.MaskHelper.unmask(scope.getEl());
            },
            failure : function(form, action) {
                Ext.Msg.show({
					title : "提示",
					msg : action.result.msg,
					buttons : Ext.Msg.OK,
					minWidth : 300,
					icon : Ext.Msg.ERROR
				});
				scope.grid.doQuery();
				Frame.MaskHelper.unmask(scope.getEl());
            }
        });
	},

	onBtnDownloadClick : function() {
		var sel = this.grid.getSelectionModel().getSelected();
		var data = [];
		if (Ext.isEmpty(sel)) {
			Ext.Msg.show({
				title : "错误",
				msg : "请选择要下载的文件！",
				buttons : Ext.Msg.OK,
				minWidth : 300,
				icon : Ext.Msg.ERROR
			});
		} else {
		    var addre = sel.json.ATTACH_ADDRESS;
			var address = addre.replace(/\\/g, "/");
			var fileName = encodeURI(sel.json.FILENAME);
			var url = ctx + "/dm/download.do?file=" + address + "&fileName=" + fileName;
			window.open(url);
		}
	},

	onBtnDeleteClick : function() {
		var sel = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(sel)) {
			Ext.Msg.show({
				title : "错误",
				msg : "请选择要删除的文件！",
				buttons : Ext.Msg.OK,
				minWidth : 300,
				icon : Ext.Msg.ERROR
			});
		} else {
			var scope = this;
			var sel = this.grid.getSelectionModel().getSelections();
			Ext.Msg.confirm("温馨提示", "是否删除附件？", function(btn) {
				if (btn == 'yes') {
					var data = [];
					for (var i = 0; i < sel.length; i++) {
						var record = sel[i];
						var id = record.json.CUID;
						var address = record.json.ATTACH_ADDRESS;
						
						data.push({
							id : id,
							address : address
						});
					}

					IRMS.ViewHelper.request.call(this, {
						url : ctx + '/dm/deleteWf.do',
						method : 'POST',
						params : {
							formListStr : Ext.encode(data)
						},
						async : false,
						success : function() {
							scope.grid.doQuery();
						},
						failure : function() {
							Ext.Msg.alert("操作提示", "操作失败，请确认信息后重新提交");
						}
					});
				}
			});
		}
	},

	setCmpReadOnly : function(readOnly) {
		if (readOnly || this.readOnly) {
			this.buttonBar.items.each(function(item) {
				if(item.code != 'download') {
					item.hide();
				}
			});
			this.fileUploadPanel.hide();
		} else {
			this.buttonBar.items.each(function(item) {
				if (item.code != 'download') {
					item.show();
				}
			});
			this.fileUploadPanel.show();
		}
		this.doLayout();
	},

	setValues : function(type, relatedServiceCuid, address) {
		this.type = type;
		this.relatedServiceCuid = relatedServiceCuid;
		this.address = address;
		if (this.type != null && this.relatedServiceCuid != null) {
			this.setCmpReadOnly(false);
		}
	},

	refreshData : function(inputParams, type, relatedServiceCuid, address) {
		if (inputParams) {
			this.inputParams = inputParams;
		}
		this.grid.gridCfg.keyParams = {
			type : {
				key : 'type',
				value : this.type
			},
			relatedServiceCuid : {
				key : 'relatedServiceCuid',
				value : this.relatedServiceCuid
			},
			address : {
				key : 'address',
				value : this.address
			}
		};
		this.grid.doQuery();
	}
});