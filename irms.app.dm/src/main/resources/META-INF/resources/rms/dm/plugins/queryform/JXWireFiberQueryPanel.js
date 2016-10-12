Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.JXWireFiberQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.JXWireFiberQueryPanel.superclass.constructor
				.call(this);

		var columnwidth = .3, anchor = '-20', labelwidth = 80;

		var panel = new Ext.Panel({
			layout : 'form',
			height : 130,
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
						xtype : 'menucombox',
						fieldLabel : '起点',
						name : 'ORIG_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱 |DM_FIBER_DP=光分纤箱|DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
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
						xtype : 'menucombox',
						fieldLabel : '终点',
						name : 'DEST_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱 |DM_FIBER_DP=光分纤箱|DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
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
						xtype : 'dmcombox',
						fieldLabel : '光缆系统',
						name : 'RELATED_SYSTEM_CUID',					
						templateId : 'service_dict_dm.DM_WIRE_SYSTEM',
						queryCfg : {
							type : "string",
							relation : "="
						}
					} ]
				}]
			}, {
				layout : 'column',
				items : [  {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '产权',
						name : 'OWNERSHIP',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMOwnerShip"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				} ,{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '专线用途',
						name : 'SPECIAL_PURPOSE',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMSpecialPurpose"
							}
						},
						queryCfg : {
							type : "string",
							relation : 'in'
						}
					} ]
				},  {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '光缆段名称',
						name : 'LABEL_CN',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				} ]
			}, {
				layout : 'column',
				items : [  {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '重要性',
						name : 'OLEVEL',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMWireSegOlevel"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
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
						xtype : 'asyncombox',
						fieldLabel : '用途',
						name : 'PURPOSE',
						multSel : true,
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
			} ]
		});
		return panel;
	}
});
		