Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/dwr/interface/PortsManageAction.js');
$importjs(ctx+'/rms/dm/common/BatchNamesPanel.js');

Frame.grid.plugins.tbar.WireSegAlarmTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		this.grid.gridCfg.cfgParams['RELATED_PORT_A']=grid.RELATED_PORT_A;
		this.grid.gridCfg.cfgParams['RELATED_PORT_Z']=grid.RELATED_PORT_Z;
		Frame.grid.plugins.tbar.WireSegAlarmTbar.superclass.constructor.call(this);
		return this._createToolbarButtons();
	},
	_createToolbarButtons : function() {
		var space = {xtype: 'tbspacer', width: 5};
		return [{
			text : '定位',
			iconCls : 'c_page_white_link',
			disabled: false,
			scope : this,
			handler : this._doLocate
		},{xtype: 'tbfill'}];
	},
	_doLocate : function(){
		var records = this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length == 0){
	    	Ext.Msg.alert('温馨提示','请选择一条记录.');
	    	return;
	    }
	    
	    var cuid = records[0].data['CUID'];
		
		var scope = window;
		var explorer = undefined;
		for(var i = 0; i < 5; i++) {
			if(scope.Ext) {
				explorer = scope.Ext.getCmp("explorer_frame_panel");
				if(explorer) {
					explorer.windowScope = scope;
					break;
				}
			}
			scope = scope.parent;
		}
		
		if(explorer){
			var indexTab = explorer.centerPanel.items.items[0];
			explorer.centerPanel.activate(indexTab);
			var contentWindow = explorer.el.child('iframe').dom.contentWindow;
			contentWindow.Dms.Tools.graphicLocateOnMap(cuid);
		}
	}
});
