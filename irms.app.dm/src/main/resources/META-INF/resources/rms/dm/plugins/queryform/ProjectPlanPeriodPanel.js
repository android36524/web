Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.ProjectPlanPeriodPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.ProjectPlanPeriodPanel.superclass.constructor.call(this);
		var columnWidth = 80;
		
		var panel = new Ext.Panel({
			layout : 'form',
			height : 60,
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
				},  
				
			
				 ]
			}, ]
		});
		return panel;
	}
});
