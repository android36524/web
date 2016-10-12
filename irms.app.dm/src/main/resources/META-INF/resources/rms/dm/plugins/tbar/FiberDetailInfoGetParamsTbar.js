Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/dwr/interface/PortsManageAction.js');
$importjs(ctx+'/rms/dm/common/BatchNamesPanel.js');

Frame.grid.plugins.tbar.FiberDetailInfoGetParamsTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		this.grid.gridCfg.cfgParams['CUID']=grid.CUID;
		Frame.grid.plugins.tbar.FiberDetailInfoGetParamsTbar.superclass.constructor.call(this);
		return null;
	}
});
