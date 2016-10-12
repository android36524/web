Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.MaintManagementQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.MaintManagementQueryPanel.superclass.constructor.call(this);
		
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
						fieldLabel : '系统名称',
						name : 'LABEL_CN',						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				}, {					
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
				}, {					
					layout : "form",
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
					labelWidth: labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '类型',
						name : 'TYPE',
						multSel : true,						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "MaintType"
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
					layout : "form",
					columnWidth : columnwidth,
					defaults:{
						anchor: anchor
					},
					labelWidth: labelwidth,				
					items : [ {
						xtype : 'textfield',
						fieldLabel : '操作人',
						name : 'OPERATOR',						
						queryCfg : {
							type : "string",
							relation : "like",
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : "form",
					columnWidth : columnwidth,
					defaults: {
						anchor: anchor
					},
					labelWidth: labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '状态',
						name : 'STATE',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "MaintState"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}]
			}]
		});
		return panel;
	}
});
