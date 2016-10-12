/**
 * 交维资源设备新增/变更/删除流程的统一页面入口，
 * 组装包括：上方查询面板，下方资源维护tab页，底部操作按钮
 */
Ext.ns('DM.ponMaintain.common');
$importjs(ctx + "/ponMaintain/panel/ConditionPanel.js");
$importjs(ctx + "/ponMaintain/panel/DeviceTabPanel.js");

DM.ponMaintain.common.DeviceMaintainPanel = Ext.extend(Ext.Panel, {
	border : false,
	id : "ponMaintainPanel",
	layout : 'fit',
	title  : '交维资源设备管理',
	param : null,
	constructor : function(config) {
		DM.ponMaintain.common.DeviceMaintainPanel.superclass.constructor.call(this, config);
	},

	initComponent : function() {
		this.initView();
		DM.ponMaintain.common.DeviceMaintainPanel.superclass.initComponent.call(this);
	},

	initView : function () {
		// 上方的查询面板，用于描述单位工程等信息
		var conditionPanel = new DM.ponMaintain.common.ConditionPanel(this);

		// 资源维护TAB页，此页面根据参数循环调用irms.app.rms.pon中的资源页面
		var resourceTab = new DM.ponMaintain.common.DeviceTabPanel({
			inputParams : { segGroupCuid : cuid },
			deviceTypes : 'OLT,ONU,POSE,ADDRESS,COVER'
		});

		var northPanel = new Ext.Panel({
			collapseFirst : true,
	        region : 'north',
			border : false,
			bodyBorder : false,
	        collapsible : true,
			monitorResize : true,
	        title : '单位工程信息',
	        autoHeight : true,
	        items : [conditionPanel]
	    });
		
		var centerPanel = new Ext.Panel({
			id : "centerPanel",
			layout :'fit',
			region : 'center',
			border : false,
			bodyBorder : false,
			items : [resourceTab]
		});
		
		// 组装整体面板
		var mainPanel = new Ext.Panel({
			layout: 'border',
			region : 'center',
			frame : true,
			shim:true, 
			border : false,
			bodyBorder : false,
			id:'mainPanel',
			items:[northPanel,centerPanel]
		});
		
		this.items = [mainPanel];
	},
	
	setDefaultValue : function () {
	},
	
	commit : function () {
	},
	
	cancel : function () {
	}

});
