Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.ProjectManagementQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.ProjectManagementQueryPanel.superclass.constructor.call(this);
		
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
						xtype : 'asyncombox',
						fieldLabel : '工程状态',
						name : 'STATE',
						multSel : true,						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMProjectState"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
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
				}]
			}]
		});
		return panel;
	}
});
