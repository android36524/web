Ext.namespace('Frame.grid.plugins.query');

//区域细分光缆皮长统计
Frame.grid.plugins.query.WireLengthStatQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.WireLengthStatQueryPanel.superclass.constructor.call(this);
		var columnwidth = .5, anchor = '-20', labelwidth = 80;

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
			},{
				layout : 'column', // first row
				items : [{
					layout : 'form', // thrid column
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'radiogroup',
						fieldLabel : '统计类型',
						items: [  
			                    { boxLabel: '正常长度', name: 'RESULT_TYPE', inputValue: '1' ,id: 'radiogroup', checked : true},   
			                    { boxLabel: '代维考核长度', name: 'RESULT_TYPE', inputValue: '2' }
			                ]
					} ]
				}]
			} ]
		});
		return panel;
	}
});
