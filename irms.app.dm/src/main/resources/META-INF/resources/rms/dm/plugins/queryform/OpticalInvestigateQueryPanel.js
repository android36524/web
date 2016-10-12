Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.OpticalInvestigateQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.OpticalInvestigateQueryPanel.superclass.constructor
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
						xtype : 'textfield',
						fieldLabel : '任务名称',
						name : 'TASK_NAME',
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
						xtype : 'asyncombox',
						fieldLabel : '任务状态',
						name : 'TASK_STATE',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "CheckTaskState"
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
						xtype : 'textfield',
						fieldLabel : '创建用户',
						name : 'CREATE_USER',
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
						xtype : 'datefield',
						fieldLabel : '创建时间-从',
						name : 'CREATE_TIME',
						format : 'Y-m-d',
						id : 'begin',
						queryCfg : {
							type : "date",
							relation : "between"
						},listeners:{
    						'select':function(){
    							if(Ext.getCmp("end").getValue()!=null&&Ext.getCmp("end").getValue()!=""&&Ext.getCmp("begin").getValue()>Ext.getCmp("end").getValue()){
	    							Ext.Msg.alert("提示","截止日期不能在起始日期前，请重新选择");
	    							Ext.getCmp("begin").setValue("");
    							}
    						}
						}
					} ]
				} , {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'datefield',
						fieldLabel : '创建时间-到',
						name : 'CREATE_TIME',
						format : 'Y-m-d',
						id : 'end',
						queryCfg : {
							type : "date",
							relation : "between"
						},listeners:{
    						'select':function(){
    							if(Ext.getCmp("begin").getValue()!=null&&Ext.getCmp("begin").getValue()!=""&&Ext.getCmp("begin").getValue()>Ext.getCmp("end").getValue()){
	    							Ext.Msg.alert("提示","截止日期不能在起始日期前，请重新选择");
	    							Ext.getCmp("end").setValue("");
    							}
    						}
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
						fieldLabel : '任务类型',
						name : 'task_Type',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "CheckTaskType"
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
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {						
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'RELATED_WIRE_SYSTEM_CUID',
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
				}]
			} ]
		});
		return panel;
	}
});
		