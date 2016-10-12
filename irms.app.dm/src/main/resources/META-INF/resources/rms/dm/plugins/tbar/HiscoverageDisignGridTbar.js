Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');

Frame.grid.plugins.tbar.HiscoverageDisignGridTbar = Ext.extend(Object,{
					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.HiscoverageDisignGridTbar.superclass.constructor
								.call(this);
						return [ '-',{
							text : '导出',
							iconCls : 'c_page_white_link',
							scope : this,
							handler : this._exports
						} 
						];
					},
					_exports : function() {},
					
					
					
					
					
					
				
});
