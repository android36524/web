Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.FiberStatQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.FiberStatQueryPanel.superclass.constructor.call(this);

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
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'WSYS.RELATED_SPACE_CUID',
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
						xtype : 'dmcombox',
						fieldLabel : '光缆系统',
						name : 'WS.RELATED_SYSTEM_CUID',					
						templateId : 'service_dict_dm.DM_WIRE_SYSTEM',
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
						xtype : 'menucombox',
						fieldLabel : '起点',
						name : 'WS.ORIG_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱|DM_FIBER_DP=光分纤箱 |DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点|DM_PON=POS'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
						}
					}]
				},{

					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [{
						xtype : 'menucombox',
						fieldLabel : '止点',
						name : 'WS.DEST_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱|DM_FIBER_DP=光分纤箱 |DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点|DM_PON=POS'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
						}
					}  ]
				
				}]
			} ]
		});
		return panel;
	}
});
