Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx + '/dwr/interface/GetServiceParamAction.js');

Frame.grid.plugins.tbar.DmRoomGridTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.DmRoomGridTbar.superclass.constructor
				.call(this);
		return ['-', {
					text : '机房平面图',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._method1
				}];
	},
	_method1 : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要一条记录.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var url = ctx+ "/topo/index.do?code=RoomSectionTopo&resId="+ cuid + "&resType=ROOM&clientType=html5";
		FrameHelper.openUrl(url,'机房平面图-'+records[0].data['LABEL_CN']);
/*		GetServiceParamAction.getUrlByServerName("TOPO", function(data) {
					var url = data
							+ "/topo/index.do?code=RoomSectionTopo&resId="
							+ cuid + "&resType=ROOM&clientType=html5";
					var win = new Ext.Window({
								title : '机房平面图',
								maximizable : true,
								width : window.screen.availWidth * 0.80,
								height : window.screen.availHeight * 0.75,
								isTopContainer : true,
								modal : true,
								resizable : false,
								contentEl : Ext.DomHelper.append(document.body,
										{
											tag : 'iframe',
											style : "border 0px none;scrollbar:true",
											src : url,
											height : "100%",
											width : "100%"
										})
							});
					win.show();
				});*/
	}

});
