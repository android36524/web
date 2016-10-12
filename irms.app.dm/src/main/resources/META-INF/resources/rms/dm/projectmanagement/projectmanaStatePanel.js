Ext.ns('DM.form');
$importjs(ctx+'/dwr/interface/ProjectManageAction.js');

DM.form.formPamel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		DM.form.formPamel.superclass.constructor.call(this,config);
	},
	
	initComponent : function() {
		this._initView();
		DM.form.formPamel.superclass.initComponent.call(this);
	},

	_initView : function() {
		this.items = [ this._batchState() ];
	},
	
	_batchState : function() {
		/*this.gridPanel = new Ext.grid.PropertyGrid({
			layout : 'form',
		    height : 130,
		    frame : false,
		    source : {
				"说明" : ""
			}
		});*/
		this.formPanel = new Ext.form.FormPanel({
			 bodyStyle : "padding:5 5 5 5",
			 border : false,
			 items : [
			 	{
                    title: "说明信息",
                    xtype: "fieldset",
                    collapsible: false,
                    defaultType: "textarea",
                    items: [{
                        name : 'description',
                        width : '100%',
                        hideLabel : true
                    }]
                }
			 ]
		});
		return this.formPanel;
	},
	_save : function(records) {
		var scope = this;
		//var jsoncode = scope.gridPanel.getSource();
		var desctiption = scope.formPanel.getForm().findField('description').getValue();
		var ValueJson={
				DESCRIPTION:desctiption
		};
		var prObjArr = {};
		for(var i=0;i<records.length;i++){
			var data=records[i].json;
			for(var key in data){
				var value=data[key];
				if(value!=null){
					if(typeof value=='object' && !Ext.isDate(value)){
						prObjArr[key]= (value.CUID==null)?null:value.CUID;										
				    }
					else if(typeof value=='object' && Ext.isDate(value)){
						prObjArr[key]=value.dateFormat('Y-m-d h:i:s');
					}
					else{
						prObjArr[key]=value;
					}	
				}
				else{
					prObjArr[key]=value;
				}															
		    }
		 }
		ProjectManageAction.updateProjManaState(prObjArr,ValueJson,function(data){});		
		
	},
	_saveDe : function(records){
		var scope = this;
		//var jsoncode = scope.gridPanel.getSource();
		var desctiption = scope.formPanel.getForm().findField('description').getValue();
		var ValueJson={
				DESCRIPTION:desctiption
		};
		var prObjArr = {};
		for(var i=0;i<records.length;i++){
			var data=records[i].json;
			for(var key in data){
				var value=data[key];
				if(value!=null){
					if(typeof value=='object' && !Ext.isDate(value)){
						prObjArr[key]= (value.CUID==null)?null:value.CUID;										
				    }
					else if(typeof value=='object' && Ext.isDate(value)){
						prObjArr[key]=value.dateFormat('Y-m-d h:i:s');
					}
					else{
						prObjArr[key]=value;
					}	
				}
				else{
					prObjArr[key]=value;
				}															
		    }
		 }
		ProjectManageAction.updateProjManaStateDesign(prObjArr,ValueJson,function(data){
			
		});
	},
	_saveSubProject : function(records,isApproved){
		var scope = this;
		//var jsoncode = scope.gridPanel.getSource();
		var desctiption = scope.formPanel.getForm().findField('description').getValue();
		var ValueJson={
				DESCRIPTION:desctiption,
				ISAPPROVED:isApproved
		};
		var prObjArr = {};
		for(var i=0;i<records.length;i++){
			var data=records[i].json;
			for(var key in data){
				var value=data[key];
				if(value!=null){
					if(typeof value=='object' && !Ext.isDate(value)){
						prObjArr[key]= (value.CUID==null)?null:value.CUID;										
				    }
					else if(typeof value=='object' && Ext.isDate(value)){
						prObjArr[key]=value.dateFormat('Y-m-d h:i:s');
					}
					else{
						prObjArr[key]=value;
					}	
				}
				else{
					prObjArr[key]=value;
				}															
		    }
		 }
		ProjectManageAction.updateSubProjectState(prObjArr,ValueJson,function(data){});		
	}
	
});