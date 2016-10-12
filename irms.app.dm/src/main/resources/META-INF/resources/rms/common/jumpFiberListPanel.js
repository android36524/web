Ext.ns("IRMS.dm.common");
$importjs(ctx + '/dwr/interface/jumpFiberRemarkAction.js');
$importjs(ctx + '/rms/grid/EditorGridPanel.js');
IRMS.dm.common.jumpFiberListPanel = Ext.extend(Ext.Panel, {
	layout : 'form',
	allowBlank : true,
	width : 700,
	height : 350,
	constructor : function(config) {
		this.cuid = config.cuid;
		IRMS.dm.common.jumpFiberListPanel.superclass.constructor.call(this,config);
	},
	initComponent : function() {
		this._initView();
		IRMS.dm.common.jumpFiberListPanel.superclass.initComponent.call(this);
	},
	listeners: {  
        'afterrender': function() {  
        	this.queryData();
        }
    }, 
	_initView : function() {
		var self = this;
		var record=Ext.data.Record.create([ 
            {name:'JUMP_FIBER_CUID',mapping:0},  
	        {name:'LABEL_CN',mapping:1}, 
	        {name:'REMARK',mapping:2}
	    ]);
		this.winGrid=new Ext.grid.EditorGridPanel({  
			height : 280,
			viewConfig: { 
		          forceFit: true 
		    },
	        store:new Ext.data.Store({  
	              reader:new Ext.data.ArrayReader({id:0},record)
	        }),  
	        columns:[
	            {header:'跳纤CUID',hidden:true,dataIndex:'JUMP_FIBER_CUID'},    
	            {header:'名称',width:.7,dataIndex:'LABEL_CN'},  
	            {header:'别名',width:.3,dataIndex:'REMARK',  
	                 editor:new Ext.form.TextField({ })}
	        ]
		});
		this.winPanel = new Ext.form.FormPanel({
			height : 70,
			layout : 'form',
			buttonAlign : 'right',
			frame : true,
			scope : this,
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .45,
					defaults : {
						anchor : '-20'
					},
					labelWidth : 50,
					items : [ {
						xtype : 'textfield',
						name : 'LABEL_CN',
						fieldLabel : '名称',
						width : 50
					} ]
				}, {
					layout : 'form',
					columnWidth : .45,
					defaults : {
						anchor : '-20'
					},
					labelWidth : 50,
					items : [ {
						xtype : 'textfield',
						name : 'REMARK',
						fieldLabel : '别名',
						width : 50
					} ]
				}]
			}],
			buttons : [ {
				text : '查询',
				disabled : this.readOnly,
				iconCls : 'c_find',
				scope : self,
				handler : this.queryData
			}, {
				text : '重置',
				disabled : this.readOnly,
				iconCls : 'c_arrow_rotate_anticlockwise',
				scope : self,
				handler : this.clearQueryData
			} ]
		});
		this.items = [this.winPanel,this.winGrid];
		this.buttons =[{
			text : '保存',
			disabled : this.readOnly,
			scope : self,
			handler : this.savedata
		}];
	},
	
	queryData : function() {
		var labelCn = this.winPanel.getForm().findField("LABEL_CN").getValue();
		var remark = this.winPanel.getForm().findField("REMARK").getValue();
		this.winGrid.getStore().removeAll();
		var self = this;
		self.winGrid.getEl().mask('查询中，请稍后！');
		jumpFiberRemarkAction.getJumpFIberNames(this.cuid,labelCn,remark,{
			callback : function(data) {
					if (data) {
						for (var i = 0; i < data.length; i++) {
							var record = new Ext.data.Record({
								JUMP_FIBER_CUID : data[i].JUMP_FIBER_CUID,
								LABEL_CN : data[i].LABEL_CN,
								REMARK : data[i].REMARK
							});
							self.winGrid.getStore().insert(i, record);
						}
					}
					self.winGrid.getEl().unmask();
			}, 
			exceptionHandler: function exceptionHandler(exceptionString, exception){  
				self.winGrid.getEl().unmask();
				Ext.Msg.alert("系统异常", exceptionString);
			}
		});
	},
	clearQueryData : function() {
		this.winPanel.getForm().reset();
	},
	savedata : function() {
		var self = this;
		self.winGrid.getEl().mask('正在保存，请稍后！');
		var modifiedRecords=this.winGrid.getStore().getModifiedRecords();
		if(modifiedRecords.length==0){
			win.hide();
			return;
		}
		var info =new Array();
		for(var i=0;i<modifiedRecords.length;i++){
			info.push(modifiedRecords[i].data);
		}
		jumpFiberRemarkAction.saveJumpFIberNames(info,{
			callback : function(data){
				if (data=='success') {
					Ext.Msg.alert("操作提示", "保存成功！");
				}
				self.winGrid.getEl().unmask();
			}, 
			exceptionHandler: function exceptionHandler(exceptionString, exception){  
				self.winGrid.getEl().unmask();
				Ext.Msg.alert("系统异常", exceptionString);
			}
		});
	},
	closeWindow : function() {
		this.win.close();
	}
});