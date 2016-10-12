Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.OpticalWayQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.OpticalWayQueryPanel.superclass.constructor.call(this);

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
			            fieldLabel: '光路名称',
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
						xtype : 'menucombox',
						fieldLabel : '起始点',
						name : 'SITE_CUID_A',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_ROOM=机房|DM_ACCESSPOINT=接入点'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
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
						xtype : 'menucombox',
						fieldLabel : '终止点',
						name : 'SITE_CUID_Z',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_ROOM=机房|DM_ACCESSPOINT=接入点'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
						}
					} ]				
				}]
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
			            fieldLabel: '核查状态',
			            name: 'MAKE_FLAG',
						multSel : true,
						minListWidth : 200,
						width : 200,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "OpticalWayMakeFlag" 
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
						xtype : 'textfield',
			            fieldLabel: '调单文号',
			            name: 'DISPATCH_NAME',
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
			            fieldLabel: '传输业务',
			            name: 'BUSINESS_INFO',
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
			            fieldLabel: '业务类型',
			            name: 'EXT_TYPE',
			            width : 200,
			            comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "ExtType" 
							}
						},
			            queryCfg : {
							type : "string",
							relation :'in'
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
				} ]
			} ]
		});
		return panel;
	}
});


