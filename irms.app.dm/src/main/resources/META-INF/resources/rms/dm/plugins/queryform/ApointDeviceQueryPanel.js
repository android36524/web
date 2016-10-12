Ext.namespace('Frame.grid.plugins.query');
Frame.grid.plugins.query.ApointDeviceQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.ApointDeviceQueryPanel.superclass.constructor.call(this);
		
		var columnwidth=.3 ,anchor='-20',labelwidth=80 ;
		
		var panel = new Ext.Panel({
			layout : 'form',
			height : 130,
			style : 'margin:5 0 0 5',
			items : [{
				layout : 'column',
				items : [ {					
					layout : "form",
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
				    labelWidth: labelwidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '名称',
						name : 'LABEL_CN',						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				},{
					layout: 'form',
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
					labelWidth: labelwidth,
					items: [{
						xtype : 'asyncombox',
						fieldLabel : '类型',
						name : 'RELATED_DEVICE_CUID',
						comboxCfg:{
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code:"ApointDeviceType"
							}
						},
						queryCfg : {
							type : "number",
							relation : "="
						}
					}]
				}]
			}]
		});
		return panel;
	}
});
