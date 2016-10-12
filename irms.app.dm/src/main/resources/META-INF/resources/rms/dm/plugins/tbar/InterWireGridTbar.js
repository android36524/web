Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx + '/rms/dm/optical/opticalPanel.js');
$importjs(ctx + '/dwr/interface/OpticalViewAction.js');
$importjs(ctx + '/dwr/interface/GenerateOpticalAction.js');
$importjs(ctx+'/rms/common/FilePanel.js');

Frame.grid.plugins.tbar.InterWireGridTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.InterWireGridTbar.superclass.constructor
				.call(this);
		return [ '-', {
			text : '楼内纤芯管理',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._fiberManage
		}, '-', {
			text : '生成光纤',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._methodBuildOptical
		}, '-', {
			text : '查看光纤列表',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._getOpticalLists
		},'-',{
			xtype : 'splitbutton',
			text : '导入',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '楼内光缆导入',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._import
			},{
				text : '模板下载',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._export
			}]
		}];
	},
	/*
	 * 楼内纤芯管理
	 */
	_fiberManage : function() {

		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
			Ext.Msg.alert('温馨提示', '请选择楼内光缆管理数据.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var destPoint=records[0].data['DEST_POINT_CUID'];
		var origPoint=records[0].data['ORIG_POINT_CUID'];
		var url = '/rms/dm/wire/interfibermanapanel.jsp?code=service_dict_dm.DM_FIBER_INTER&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
				labelCn +destPoint+origPoint+ '楼内纤芯管理');
	},
	/*
	 * 生成光纤
	 */
	_methodBuildOptical : function() {
		var records=this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records)||records.length==0 || records.length>1){
			Ext.Msg.alert('温馨提示', '请选择单条记录.');
			return;
		}
		ObjArr = [];
	    for(var i=0;i<records.length;i++){
	    	var obj=records[i];
	    	var labelCn=records[i].data['LABEL_CN'];
	    	if(Ext.isEmpty(obj) || Ext.isEmpty(labelCn)){
	    		Ext.Msg.alert('温馨提示','当前选中数据,不包含必需字段,请检查配置');
	    		return;
	    	}
	    	ObjArr[i] = obj;
	    };
	    var JsonArr=[];
		for(var i=0;i<ObjArr.length;i++){
			var Json={
					CUID:ObjArr[i].data.CUID,
					OBJECTID:ObjArr[i].data.OBJECTID,
					LABEL_CN:ObjArr[i].data.LABEL_CN
			};
			JsonArr[i]=Json;
		};
		Ext.MessageBox.show({
			title:'注意！',
			msg:'选择此条记录生成光纤',
			buttons:{yes:'生成光纤',cancel:'取消'},
			 fn:function(btn){
				 if(btn =="yes"){
					 GenerateOpticalAction.doBuildOptical(JsonArr);
					 Ext.Msg.alert('温馨提示','生成光纤成功');
				 }
			 }
		});
	},
	/*
	 * 查看光纤列表实现
	 */
	_getOpticalLists : function() {
		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
			Ext.Msg.alert('温馨提示', '请选择一条光缆系统数据操作。');
			return;
		} else {
			this.formPanel = new Ext.form.formPamel({});
			var win = WindowHelper.openExtWin(this.formPanel, {
				title : '查看光纤列表',
				width : 850,
				height : 500,
				frame : true,
				buttons : [ {
					text : '关闭',
					handler : function() {
						win.close();
					}
				} ]
			});

			// 与后台数据交互
			var scope = this.formPanel;
			var recordss = new Array();
			var WireSystemCUID = records[0].data.CUID;
			MaskHelper.mask(scope.getEl(),"读取中,请稍候...");
			OpticalViewAction.getOpticalLists(WireSystemCUID, function(data) {
				for (var i = 0; i < data.length; i++) {
					var gridData = data[i];
					var record = new Ext.data.Record({
						LABEL_CN : gridData.LABEL_CN,
						MAKE_FLAG : gridData.MAKE_FLAG,
						ORIG_SITE_CUID : gridData.ORIG_SITE_CUID,
						ORIG_ROOM_CUID : gridData.ORIG_ROOM_CUID,
						ORIG_EQP_CUID : gridData.ORIG_EQP_CUID,
						ORIG_POINT_CUID : gridData.ORIG_POINT_CUID,
						DEST_SITE_CUID : gridData.DEST_SITE_CUID,
						DEST_ROOM_CUID : gridData.DEST_ROOM_CUID,
						DEST_EQP_CUID : gridData.DEST_EQP_CUID,
						DEST_POINT_CUID : gridData.DEST_POINT_CUID,
						ROUTE_DESCIPTION : gridData.ROUTE_DESCIPTION,
						OWNERSHIP : gridData.OWNERSHIP,
						FIBER_LEVEL : gridData.FIBER_LEVEL,
						FIBER_STATE : gridData.FIBER_STATE,
						USER_NAME : gridData.USER_NAME,
						FIBER_TYPE : gridData.FIBER_TYPE,
						FIBER_DIR : gridData.FIBER_DIR,
						PURPOSE : gridData.PURPOSE,
						LENGTH : gridData.LENGTH,
						SUM_ATTENU_1310 : gridData.SUM_ATTENU_1310,
						SUM_ATTENU_1550 : gridData.SUM_ATTENU_1550,
						APPLY_TYPE : gridData.APPLY_TYPE
					});
					recordss.push(record);
				}
				var store = scope.getStore();
				store.add(recordss);
				MaskHelper.unmask(scope.getEl());
			});
		}
	},
	
	_import:function(){
		var winCfg={
				width:600,
				height:200
		};
		this.filePanel=new IRMS.dm.FilePanel({
	    	 title: '导入',
	    	 width: 100,
	    	 height:20,
	    	 inputParams : this.inputParams,
	    	 key:'INTER_WIRE'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	
	_export:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("楼内光缆导入"));
		window.open(url);
	}
	
});