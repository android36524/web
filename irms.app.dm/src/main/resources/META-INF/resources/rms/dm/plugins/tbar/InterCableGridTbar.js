Ext.ns('Frame.grid.plugins.tbar');

Frame.grid.plugins.tbar.InterCableGridTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.InterCableGridTbar.superclass.constructor
				.call(this);
		return [ '-', {
			text : '楼内线对管理',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._interLinePairManage
		}];
	},

	_interLinePairManage : function() {
		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择光缆系统数据.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单条光缆系统的纤芯管理.');
			return;
		}
		var url = '/rms/dm/wire/linepairmanapanel.jsp?code=service_dict_dm.DM_LINE_PAIR&hasQuery=false';
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var objectId=records[0].data['OBJECTID'];
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含CUID字段,请检查配置');
			return;
		}
		if (Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含LABEL_CN字段,请检查配置');
			return;
		}
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
				labelCn + '楼内线对管理');
	}

});