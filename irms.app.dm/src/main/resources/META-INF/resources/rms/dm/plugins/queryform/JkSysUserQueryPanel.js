Ext.namespace('Frame.grid.plugins.query');
Frame.grid.plugins.query.JkSysUserQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.JkSysUserQueryPanel.superclass.constructor.call(this);
		var columnWidth = .3, anchor = '-20', labelWidth = 60;
		var panel = new Ext.Panel({
			layout : 'form',
			height : 100,
			style : 'margin:5 0 0 5',
			items : [{
				layout : 'column',
				items : [{
					layout : "form",
					columnWidth : columnWidth,
					defaults : {
						anchor : anchor
					},
				    labelWidth: labelWidth,
					items : [{
						xtype : 'textfield',
						fieldLabel : '用户Id',
						name : 'USER_NAME',						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					}]
				},{
					layout : "form",
					columnWidth : columnWidth,
					defaults: {
						anchor: anchor
					},
				    labelWidth : labelWidth,
					items : [{
						xtype : 'textfield',
						fieldLabel : '用户名称',
						name : 'TRUE_NAME',						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					}]
				}]
			}]
		});
		return panel;
	}
});