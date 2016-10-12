Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.UpLineQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.UpLineQueryPanel.superclass.constructor.call(this);

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
			            fieldLabel: '系统名称',
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
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'RELATED_SPACE_CUID',
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
			            fieldLabel: '工程状态',
			            name: 'STATE',
			            multSel : true,
						minListWidth : 200,
						width : 200,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMState" 
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
			            fieldLabel: '工程名称',
			            name: 'PROJECT',
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
						xtype : 'asyncombox',
			            fieldLabel: '系统级别',
			            name: 'SYSTEM_LEVEL',
			            multSel : true,
						minListWidth : 200,
						width : 200,
			            comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMSystemLevel" 
							}
						},
						queryCfg: {
        	    			  type: "string",
        	    			  relation:"in"
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
			            fieldLabel: '施工单位',
			            name: 'BUILDER',
			            multSel : true,
						minListWidth : 200,
						width : 200,
			            queryCfg : {
							type : "string",
							relation :'like',
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
			            fieldLabel: '产权类型',
			            name: 'OWNERSHIP',
			            multSel : true,
						minListWidth : 200,
						width : 200,
						//autoWidth: true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMOwnerShip" 
							}
						},
						queryCfg : {
							type: "string",
     	    	    	    relation: "in"
						}
					} ]
				}		
				]
			} ]
		});
		return panel;
	}
});
