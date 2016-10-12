Ext.namespace('Frame.grid.plugins.query');

//月度光缆长度减量统计
Frame.grid.plugins.query.WireLengthDecrementStatQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.WireLengthDecrementStatQueryPanel.superclass.constructor.call(this);
		var columnwidth = .3, anchor = '-20', labelwidth = 80;

		var panel = new Ext.Panel({
			layout : 'form',
			height : 130,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column', // first row
				items : [ {
					layout : 'form', // first column
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '核查月份',
						name : 'CHECK_MONTH',
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "CheckMonth"
							}
						},
						queryCfg : {
							type : "number",
							relation : '='
						}
					} ]
				}, {
					layout : 'form', // second column
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '统计区域',
						name : 'RELATED_DISTRICT_CUID',
						/*queryType:'DISTRICT',
						queryCfg : {
							type : "string",
							relation : '='
						}*/
						comboxCfg : {
							cfgParams : {
								code : "DMDF.DISTRICT"
							}
						},
						queryCfg : {
							type : "string",
							relation : '=',
							blurMatch : 'right'
						}
					} ]
				}]
			} ]
		});
		return panel;
	}
});
