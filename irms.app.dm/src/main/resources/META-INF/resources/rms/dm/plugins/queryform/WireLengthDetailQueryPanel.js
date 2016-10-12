Ext.namespace('Frame.grid.plugins.query');

//区域细分光缆皮长统计
Frame.grid.plugins.query.WireLengthDetailQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.WireLengthDetailQueryPanel.superclass.constructor.call(this);
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
						xtype : 'datefield',
						fieldLabel : '核查月份',
						name : 'CHECK_DATE',
						editable : false,
						format : 'Y-m',
						queryCfg : {
							type : "date",
							relation : "between"
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
						name : 'PARENT_DISTRICT_CUID',
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
			} ,{
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
						//allowBlank : false,
						//name : 'TYPE',
						items: [  
			                    { boxLabel: '光缆皮长统计', name: 'TYPE', inputValue: '1' ,id: 'radiogroup', checked : true},   
			                    { boxLabel: '代维统计长度', name: 'TYPE', inputValue: '2' },  
			                    { boxLabel: '承载物段长度', name: 'TYPE', inputValue: '3' }
			                ]
//						comboxCfg : {
//							boName : 'EnumTemplateComboxBO',
//							cfgParams : {
//								code : "CheckType"
//							}
//						},
//						queryCfg : {
//							type : "string",
//							relation : '='
//						}
					} ]
				}]
			}]
		});
		return panel;
	}
});
