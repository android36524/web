Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.InflexionQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.InflexionQueryPanel.superclass.constructor.call(this);

		var columnWidth = 80;
		var panel = new Ext.Panel({
			layout : 'form',
			height : 135,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '拐点名称',
			            name: 'LABEL_CN',
			            width : 200,
			            queryCfg : {
							type : "string",
							relation :'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '维护单位',
			            name: 'MAINT_DEP',
						multSel : true,
						minListWidth : 200,
						width : 200,
						queryCfg : {
							type : "string",
							relation : "like",
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '所有权人',
			            name: 'RES_OWNER',
						multSel : true,
						minListWidth : 200,
						width : 200,
						
						queryCfg : {
							type : "string",
							relation : "like",
							blurMatch : 'both'
						}
					} ]
				} ]
			}, {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
			            fieldLabel: '维护方式',
			            name: 'MAINT_MODE',
						multSel : true,
						minListWidth : 200,
						width : 200,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMMaintMode" 
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'RELATED_DISTRICT_CUID',
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
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
			            fieldLabel: '用途',
			            name: 'PURPOSE',
						multSel : true,
						minListWidth : 200,
						width : 200,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMPurpose" 
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
			            }
					} ]
				} ]
			}, {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '产权所属',
			            name: 'OWNERSHIP',
			            width : 200,
			            
			            queryCfg : {
							type : "string",
							relation :'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '地址',
			            name: 'LOCATION',
			            width : 200,
			            queryCfg : {
							type : "string",
							relation :'like',
							blurMatch : 'both'
						}
					} ]
				} ]
			} ]
		});
		return panel;
	}
});
