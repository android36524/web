Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.OpticalQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.OpticalQueryPanel.superclass.constructor
				.call(this);
		var columnwidth = .3, anchor = '-20', labelwidth = 100;

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
						xtype : 'textfield',
						fieldLabel : '光纤名称',
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
						xtype : 'menucombox',
						fieldLabel : '起端设备',
						name : 'ORIG_EQP_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_ROOM=机房|DM_ODF=ODF配线架|DM_MISCRACK=综合机架|DM_FIBER_CAB=光交接箱 |DM_FIBER_DP=光分纤箱|DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点'
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
						xtype : 'textfield',
						fieldLabel : '关联业务信息',
						name : 'BUSINESS_INFO',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				} ]
			}, {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '级别',
						name : 'FIBER_LEVEL',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMFIBERLEVEL"
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
						xtype : 'menucombox',
						fieldLabel : '止端设备',
						name : 'DEST_EQP_CUID',
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_ROOM=机房|DM_ODF=ODF配线架|DM_MISCRACK=综合机架|DM_FIBER_CAB=光交接箱 |DM_FIBER_DP=光分纤箱|DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点'
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
						xtype : 'textfield',
						fieldLabel : '用户',
						name : 'USER_NAME',
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
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '产权归属',
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
				}, {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '核查状态',
						name : 'MAKE_FLAG',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "PathMakeFlag"
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
