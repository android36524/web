Ext.ns('Frame.grid.edit');
$importjs(ctx+'/dwr/interface/BatchNamesAction.js');

Frame.grid.edit.AccesspointAddFiberDpPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		Frame.grid.edit.AccesspointAddFiberDpPanel.superclass.constructor.call(this,config);
	},
	initComponent : function() {
		this._initView();
		Frame.grid.edit.AccesspointAddFiberDpPanel.superclass.initComponent.call(this);
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
				}))
			}
		});
		return this.batchNameGrid;
	},
	
	_save : function(ObjArr) {
		var scope = this;
		var jsoncode = scope.batchNameGrid.getSource();
		var JsoArr=[];
		
		if(jsoncode.前缀==""){
			alert('前缀为空，请填写后点确定保存');
			return;
		}
		
		for(var i=0;i<ObjArr.length;i++){
			var Jso={
					CUID:ObjArr[i].data.CUID,
					LABEL_CN:ObjArr[i].data.LABEL_CN,
					LONGITUDE:ObjArr[i].data.LONGITUDE,
					LATITUDE:ObjArr[i].data.LATITUDE,
					CITY_LABEL:ObjArr[i].data.CITY_LABEL,
			};
			JsoArr[i]=Jso;
		}
		
		
		
		var NameJson={
				PREFIX:jsoncode.前缀,
				POSTFIX:jsoncode.后缀,
				DIGIT:jsoncode.编号位数,
				STARTNUMBER:jsoncode.起始编号,
				numCount : jsoncode.数量
		};
		

		
		BatchNamesAction.accessPontAddFiberDp(JsoArr,NameJson,function(data){	
			if(data){
				Ext.Msg.alert('温馨提示', '光分纤箱增加成功！');
			}
		});
		
	},
	_namePreview : function() {
		Ext.Msg.alert('温馨提示', '名称预览');
	}
});
