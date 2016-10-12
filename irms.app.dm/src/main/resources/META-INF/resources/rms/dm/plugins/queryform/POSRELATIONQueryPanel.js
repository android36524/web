Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.POSRELATIONQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.POSRELATIONQueryPanel.superclass.constructor.call(this);
		var columnWidth = 80;
		
		var panel = new Ext.Panel({
			layout : 'form',
			height : 135,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
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
				}, {
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {						
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'DISHI2',
						comboxCfg : {
							cfgParams : {
								code : "DMDF.DISTRICT"
							}
						},
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'right'								
						}
					} ]
				} ]
			} ]
		});
		return panel;
	}
});
