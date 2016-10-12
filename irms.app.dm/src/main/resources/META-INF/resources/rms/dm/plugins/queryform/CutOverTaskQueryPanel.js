Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.CutOverTaskQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.CutOverTaskQueryPanel.superclass.constructor.call(this);
		
		var columnwidth=.3 ,anchor='-20',labelwidth=80 ;
		
		var panel = new Ext.Panel({
			layout : 'form',
			height : 130,
			style : 'margin:5 0 0 5',
			items : [{
				layout : 'column',
				items : [ {					
					layout : "form",
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
				    labelWidth: labelwidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '割接工单名称',
						name : 'LABEL_CN',						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				},{
					layout: 'form',
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
					labelWidth: labelwidth,
					items: [{
						xtype : 'menucombox',
						fieldLabel : '割接设备',
						name : 'RELATED_DEVICE_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_ODF=ODF配线架|DM_MISCRACK=综合机架|DM_FIBER_JOINT_BOX=光接头盒'
							}
						},
						queryCfg : {
							type : "string",
							relation : "="
						}
					
					}]
				},{					
					layout : "form",
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
					labelWidth: labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '割接场景',
						name : 'CUTOVER_TYPE',
						multSel : true,						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "WireCutTypeEnum"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}]
			},{
				layout : 'column',
				items : [{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'dmcombox',
						fieldLabel : '光缆系统',
						name : 'RELATED_WIRE_SYSTEM_CUID',					
						templateId : 'service_dict_dm.DM_WIRE_SYSTEM',
						queryCfg : {
							type : "string",
							relation : "="
						}
					} ]
				},{					
					layout : "form",
					columnWidth : columnwidth,
					defaults:{
						anchor: anchor
					},
					labelWidth: labelwidth,				
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '割接状态',
						name : 'CUTOVER_STATE',
						multSel : true,						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "WireCutStateEnum"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				},{					
				    layout:"form",
				    columnWidth:columnwidth,
				    defaults: {
				    	anchor:anchor
				    },
				    labelWidth: labelwidth,
				    items:[{  
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
				    }]					
				}]
			}]
		});
		return panel;
	}
});
