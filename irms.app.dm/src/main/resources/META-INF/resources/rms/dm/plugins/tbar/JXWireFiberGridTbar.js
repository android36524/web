Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/dwr/interface/OpticalViewAction.js');
$importjs(ctx + "/rms/dm/wire/WireRemainView.js");
$importjs(ctx + '/rms/dm/optical/opticalPanel.js');


/*
 *
 */
Frame.grid.plugins.tbar.JXWireFiberGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.JXWireFiberGridTbar.superclass.constructor.call(this);
		return [  '-',  {
							text : '纤芯管理',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._fiberManage
						}, '-', {
							text : '预留信息',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._wireRemainView
						}, '-', {
							text : '查询光纤列表',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._getOpticalLists
						}];
	},
	_fiberManage : function() {
		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择光缆系统数据.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单条数据的纤芯管理.');
			return;
		}
		var url = '/rms/dm/wire/fibermanagepanel.jsp?x=x&type=SYSTEM&key=DUCT_BRANCH';
		var cuid = records[0].data['RELATED_SYSTEM_CUID'].CUID;
		var labelCn = records[0].data['LABEL_CN'];
		var objectId=records[0].data['OBJECTID'];
		if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
			return;
		}
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
				labelCn + '路由管理');
	},
	_wireRemainView : function() {
		var records=this.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		var cuid=records[0].data['RELATED_SYSTEM_CUID'].CUID;
		var labelcn=records[0].data['LABEL_CN'];
		var wireRemainPanel = new Frame.wire.WireRemainPanel({
			cuid : cuid,
			labelcn : labelcn
		});
		
		var scope = this;
		var win = WindowHelper.openExtWin(wireRemainPanel, {
			title : '光缆预留设置',
			width : 600,
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
	
	_getOpticalLists : function() {
		var records = this.getSelectionModel().getSelections();
		
		if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
			Ext.Msg.alert('温馨提示', '请选择一条光缆系统数据操作。');
		} else {
			this.formPanel = new Ext.form.formPamel({});
			var win = WindowHelper.openExtWin(this.formPanel, {
				title : '查看光纤列表',
				width : 850,
				height : 400,
				frame : true,
				buttons : [ {
					text : '关闭',
					handler : function() {
						win.close();
					}
				} ]
			});
			
			// 与后台数据交互
			var scope=this.formPanel;
			var recordss = new Array();
			var WireSystemCUID = records[0].data.CUID;
			MaskHelper.mask(scope.getEl(),"读取中,请稍候...");
			OpticalViewAction.getOpticalLists(WireSystemCUID,function(data){
				for(var i=0;i<data.length;i++){
					var gridData =data[i];
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
	}
});

