Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.UpNeQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.UpNeQueryPanel.superclass.constructor.call(this);
		var columnwidth = .3, anchor = '-20', labelwidth = 80;

		var panel = new Ext.Panel({
			layout : 'form',
			height : 80,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '设备名称',
						name : 'LABEL_CN',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '设备类型',
						name : 'NE_TYPE',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				} ]
			} ]
		});
		return panel;
	}
});
