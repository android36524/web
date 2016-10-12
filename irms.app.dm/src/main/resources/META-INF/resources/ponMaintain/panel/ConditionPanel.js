/**
 * 交维资源设备新增/变更/删除流程的统一页面的上方查询面板
 */
Ext.ns('DM.ponMaintain.common');
DM.ponMaintain.common.ConditionPanel = Ext.extend(Ext.Panel, {
	border : false,
	id:"ConditionPanel",
	title  : '',
	height : 80,
	param : null,
	constructor : function(config) {
		this.param = config;
		DM.ponMaintain.common.ConditionPanel.superclass.constructor.call(this, config);
	},

	initComponent : function() {
		this.initView();
		DM.ponMaintain.common.ConditionPanel.superclass.initComponent.call(this);
	},
	
	initView : function () {
		
	},
	
	setDefaultValue : function () {
	}

});
