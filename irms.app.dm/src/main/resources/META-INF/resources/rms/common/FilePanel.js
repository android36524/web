Ext.ns("IRMS.dm");

$importcss(ctx + "/jslib/ext/ux/fileuploadfield/css/fileuploadfield.css");
$importjs(ctx + "/jslib/ext/ux/fileuploadfield/FileUploadField.js");
//$importjs(ctx+'/dwr/interface/DMImportAction.js');


IRMS.dm.FilePanel = Ext.extend(Ext.Panel,{
	
	title : '导入',
	border : false,
	layout: 'anchor',
	allowBlank : true,
	constructor : function(config){
		
		if(!config.inputParams){
			config.inputParams = {
				sheetId : undefined,
				taskId : undefined,
				readOnly : true
			};
		}
		IRMS.dm.FilePanel.superclass.constructor.call(this,config);
	},
	
	initComponent : function(){
		this._initItems();
		IRMS.dm.FilePanel.superclass.initComponent.call(this);
	},
	
	_initItems : function(){
		this.items = [];
		
		this.fileUploadField = new Ext.ux.form.FileUploadField({
			emptyText : '选择一个文件',
			name : 'file',
			fieldLabel : '',
			buttonText : '',
			buttonCfg : {
				iconCls : 'c_image_add'
			}
		});
		
		this.fileUploadPanel = new Ext.Panel({
			flex:1,
			border : false,
			layout : 'fit',
			items : [this.fileUploadField]
		});
		
		this.buttonBar = new Ext.Panel({
			flex : 1,
			border : false,
			layout : 'column',
			defaults : {margins:'0 0 0 5'},
			items : [{
				xtype : 'button',
				text : '导入',
				iconCls : 'c_page_white_get',
				scope : this,
				handler  : this.onBtnImportClick
			}]
		});
		this.areaText = new Ext.form.TextArea({
			fieldLabel : '导入结果',
			anchor : '100%',
			height : 130,
			width:1000
			
		});
		this.uploadForm  = new Ext.FormPanel({
			style : 'border : 1px solid #a9bfd3; borderWidth : 1px 1px 0 1px;padding : 2',
			fileUpload : true,
			height : 28,
			layout : 'hbox',
			cls: 'x-panel-mc',
			items : [this.fileUploadPanel, this.buttonBar]
		}, {
		});
		
		this.items.push(this.uploadForm);
		this.items.push(this.areaText);
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
		}else {
			this.importData();
		}
	},
	importData : function() {
		MaskHelper.mask(this.getEl(), '正在解锁，请稍候...');
		this.uploadForm.getForm().submit({
			scope : this,
			url : ctx + '/dm/import.do?key='+this.key,
			success : function(form, action) {
				this.areaText.setValue(Ext.decode(action.response.responseText).msg);
				MaskHelper.unmask(this.getEl());
            },
            failure : function(form, action) {
            	this.areaText.setValue(Ext.decode(action.response.responseText).msg);
            	MaskHelper.unmask(this.getEl());
            }
		});
	}
	
	
});