Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.WireTroubleQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.WireTroubleQueryPanel.superclass.constructor.call(this);

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
						fieldLabel : '隐患名称',
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
						xtype : 'menucombox',
						fieldLabel : '起始端点',
						name : 'ORIG_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱|DM_FIBER_DP=光分纤箱 |DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点|DM_PON=POS|DM_POLE=电杆|DM_STONE=标石|DM_MANHLE=人手井|DM_INFLEXION=拐点'
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
						fieldLabel : '终止端点',
						name : 'DEST_POINT_CUID',					
						comboxCfg : {
							cfgParams : {
								code : 'DM_SITE=站点|DM_FIBER_CAB=光交接箱|DM_FIBER_DP=光分纤箱 |DM_FIBER_JOINT_BOX=光接头盒|DM_ACCESSPOINT=接入点|DM_PON=POS|DM_POLE=电杆|DM_STONE=标石|DM_MANHLE=人手井|DM_INFLEXION=拐点'
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
				}]
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
						xtype : 'textfield',
						fieldLabel : '单位名称',
						name : 'BELONGCOM',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					}]
				},{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '系统级别',
						name : 'TROUBLE_LEVEL',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "TroubleSystemLevel"
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
						fieldLabel : '状态',
						name : 'STATE',
						multSel : true,
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "WireTroubleState"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}]
			} ]
		});
		return panel;
	}
});
		