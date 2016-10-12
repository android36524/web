/**
 * 交维资源设备新增/变更/删除流程的统一页面的下方资源维护tab页
 */
Ext.ns('DM.ponMaintain.common');
DM.ponMaintain.common.DeviceTabPanel = Ext.extend(Ext.TabPanel, {
	border : false,
	activeTab : 0,
	id : "DeviceTabPanel",
	title : '工单信息',

	constructor : function(config) {
		// 用户名
		this.userId = Ext.isEmpty(ac.userId) ? 'liminfeng' : ac.userId;
		// 单位工程CUID
		this.segGroupCuid = !Ext.isEmpty(config.inputParams.segGroupCuid) ? config.inputParams.segGroupCuid : '';
		// PON管理中的页面templeteId
		this.codes = !Ext.isEmpty(config.codes) ? config.codes : null;
		// 与codes对应
		this.menuIds = !Ext.isEmpty(config.menuIds) ? config.menuIds : null;
		this.source = !Ext.isEmpty(config.source) ? config.source : 'IRMS';
		this.tbarBtns = !Ext.isEmpty(config.tbarBtns) ? config.tbarBtns : null;
		// 项目编号projectNo
		this.projectNo = !Ext.isEmpty(config.inputParams.projectNo) ? config.inputParams.projectNo : null;
		// 是否创建关系
		this.isCreateRel = !Ext.isEmpty(config.isCreateRel) ? config.isCreateRel : 'false';
		DM.ponMaintain.common.DeviceTabPanel.superclass.constructor.call(this, config);
	},

	initComponent : function() {
		DM.ponMaintain.common.DeviceTabPanel.superclass.initComponent.call(this);
		this.initView();
	},

	initView : function () {
		if (Ext.isEmpty(this.codes)) {
			Ext.Msg.alert("操作提示", "参数信息出错");
		}
		var templeteIds = this.codes.split(",");
		var templeteMenus = this.menuIds.split(",");
		for (var i = 0; i < templeteIds.length; i++) {
			this.addInitTab('deviceTab_'+ i, this.getTabUrl(templeteIds[i], templeteMenus[i]), this.getTabTitle(templeteIds[i]));
		}
	},

	setDefaultValue : function () {
	},

	// 新增单个tab页的方法；
	addInitTab : function(tabId, url, title) {
		var newTab = new Ext.Panel({
			title : title,
			id : tabId,
			closable : false,
			autoScroll : true,
			html : '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'
		});
		this.add(newTab);
	},
	
	getTabUrl : function(code, menu) {
		var url = ctx + "/ponMaintain/error.jsp";
		if (!Ext.isEmpty(code)) {
			url = ctx + '/cmp_res/grid/ResGridPanel.jsp?code='+ code + '&menuId='+ menu + '&userId='+ this.userId + '&segGroupCuid='+ this.segGroupCuid + '&source=' + this.source + '&tbarBtns=' + this.tbarBtns + '&projectNo=' + this.projectNo;
		}
		return url;
	},

	getTabTitle : function(code) {
		var title = "错误页面";
		if (!Ext.isEmpty(code)) {
			if (code.indexOf("ONU") != -1) {
				title = 'ONU管理';
			} else if (code.indexOf("OLT") != -1) {
				title = 'OLT管理';
			} else if (code.indexOf("POS") != -1) {
				title = 'POS管理';
			} else if (code.indexOf("COVER") != -1) {
				title = '覆盖范围管理';
			} else if (code.indexOf("ADDRESS") != -1) {
				title = '标准地址管理';
			} else if (code.indexOf("SVLAN") != -1) {
				title = 'SVLAN管理';
			} else {
				title = code;
			}
		}
		return title;
	}
});