$importjs(ctx+'/dwr/interface/FiberManagerAction.js');
Ext.ns('Frame.wire');
//纤芯方向
Frame.wire.FiberDirectionPanel = Ext.extend(Ext.Panel, {
	border : true,
	layout : 'border',
	width : 600,
	height : 400,
	callBack :'',
	constructor : function(config){
		
		Frame.wire.FiberDirectionPanel.superclass.constructor.call(this,config);
	},
	initComponent : function() {

		this.fiberDirection = new Ext.form.TextField({
			fieldLabel : '纤芯方向',
			name : 'direction',
			anchor : '90%'
		});
		this.uploadForm  = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			region: 'center',
			width : 200,
	        defaults: {
	            anchor: '95%'
	        },
			items: [{
				xtype : "fieldset",
				title : "文本输入",
				defaults: {
		            anchor: '100%'
		        },
				items :[this.fiberDirection]
	        }]
		});
		
		this.items = [this.uploadForm];
		Frame.wire.FiberDirectionPanel.superclass.initComponent.call(this);
	},
	UpdateFiberInfo : function(cuids){
		var self = this;
		var flag ='';
		var text = self.fiberDirection.getValue();
		if(text==null || text==''){
			flag ='请填写纤芯方向';
			return flag;
		}
		var map = new Object();
		map.direction = text;
		map.fiberCuids = cuids;
		DWREngine.setAsync(false);//同步
		FiberManagerAction.updateFiberDirection(map,function(data){
			if(data){
				flag = data;
			}
		});
		DWREngine.setAsync(true);//异步
		return flag;
	}
	
});

