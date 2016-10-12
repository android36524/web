Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.VpQueryPanel = Ext.extend(Object,
		{
			constructor : function(grid) {
				this.grid = grid;
				Frame.grid.plugins.query.VpQueryPanel.superclass.constructor.call(this);

				var columnwidth = .3, anchor = '-20', labelwidth = 80;

				var panel = new Ext.Panel({
					layout : 'form',
					height : 100,
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
								fieldLabel : '客户名称',
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
								xtype : 'spacecombox',
								fieldLabel : '所属区域',
								name : 'RELATED_DISTRICT_CUID',
								queryType : 'DISTRICT',// 这里设置为所属区域进行查询
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
								fieldLabel : '客户性质',
								name : 'VP_PROPERTY',
								multSel : true,
								comboxCfg : {
									boName : 'EnumTemplateComboxBO',
									cfgParams : {
										code : "VpProperty"
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
								fieldLabel : '客户级别',
								name : 'VP_SERVICE_LEVEL',
								multSel : true,
								comboxCfg : {
									boName : 'EnumTemplateComboxBO',
									cfgParams : {
										code : "VpServiceLevel"
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
								fieldLabel : '客户行业',
								name : 'VP_INDUSTRY',
								multSel : true,
								comboxCfg : {
									boName : 'EnumTemplateComboxBO',
									cfgParams : {
										code : "VpIndustry"
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
								fieldLabel : '客户类型',
								name : 'VP_TYPE',
								multSel : true,
								comboxCfg : {
									boName : 'EnumTemplateComboxBO',
									cfgParams : {
										code : "VpType"
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
