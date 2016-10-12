Ext.ns('Frame.grid.edit');
$importjs(ctx+'/dwr/interface/BatchNamesAction.js');

Frame.grid.edit.BatchNamesPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		Frame.grid.edit.BatchNamesPanel.superclass.constructor.call(this,
				config);
	},
	initComponent : function() {
		this._initView();
		Frame.grid.edit.BatchNamesPanel.superclass.initComponent.call(this);
	},

	_initView : function() {
		this.items = [ this._batchName() ];
	},
	_test : function() {
		var panel = new Ext.Panel({
			region : 'center',
			width : 500
		});
		return panel;
	},
	
	_batchName : function() {
		var postfix = [];
		if (Ext.isArray(this.postfix)) {
			for (var i=0; i<this.postfix.length; i++) {
				postfix.push([this.postfix[i]]);
			}
		}else {
			postfix.push([this.postfix]);
		}
		postfix.push(['#']);
		this.batchNameGrid = new Ext.grid.PropertyGrid({
			// title: "PropertyGrid实例",
			autoWidth : true,
			autoHeight : true,
			frame : true,
			source : {
				"前缀" : "",
				"后缀" : postfix[0][0],
				"编号位数" : "000",
				"起始编号" : "1",
				"数量" : this.number
			},
			customEditors : {
				"后缀" : new Ext.grid.GridEditor(new Ext.form.ComboBox({
					editable : false,
					displayField : "after",
					mode : "local",
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
						fields : [ "after" ],
						data : postfix
					})
				})),
				"编号位数" : new Ext.grid.GridEditor(new Ext.form.ComboBox({
					editable : false,
					displayField : "number",
					mode : "local",
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
						fields : [ "number" ],
						data : [ [ "0" ], [ "00" ], [ "000" ], [ "0000" ],
								[ "00000" ] ]
					})
				})),
				
	            "数量" : new Ext.grid.GridEditor(new Ext.form.TextField({
	            	disabled:true
	            }))
			}
		});
		return this.batchNameGrid;
	},
	
	_save : function(ObjArr,callBack) {
		var scope = this;
		var jsoncode = scope.batchNameGrid.getSource();
		if(jsoncode.编号位数.length<ObjArr.length.toString().length){
			Ext.Msg.alert('温馨提示', '当前编号位数小于所选设备个数，请重新选择');
			return false;
		}
		if(jsoncode.编号位数.length<jsoncode.起始编号.toString().length){
			Ext.Msg.alert('温馨提示', '当前编号位数小于起始编号，请重新选择');
			return false;
		}
		if(jsoncode.编号位数.length<(ObjArr.length+parseInt(jsoncode.起始编号)).toString().length){
			Ext.Msg.alert('温馨提示', '当前编号位数小于起始编号和所选设备个数之和，请重新选择');
			return false;
		}
		var JsoArr=[];
		for(var i=0;i<ObjArr.length;i++){
			var Jso={
					CUID:ObjArr[i].data.CUID,
					OBJECTID:ObjArr[i].data.OBJECTID
			};
			JsoArr[i]=Jso;
		}
		var NameJson={
				PREFIX:jsoncode.前缀,
				POSTFIX:jsoncode.后缀,
				DIGIT:jsoncode.编号位数,
				STARTNUMBER:jsoncode.起始编号
		};
		BatchNamesAction.doBatchNames(JsoArr,NameJson,function(data){
			if(callBack){
				callBack.call();
			}
			return true;
		});
	},
	_namePreview : function() {
		Ext.Msg.alert('温馨提示', '名称预览');
	}
});
