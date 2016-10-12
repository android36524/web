Ext.namespace('Frame.grid.plugins.query');
Frame.grid.plugins.query.OnuboxQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.OnuboxQueryPanel.superclass.constructor
				.call(this);
		var columnWidth = 80;
		var panel = new Ext.Panel({
			layout : 'form',
			height : 135,
			style : 'margin:5 0 0 10',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,

					items : [ {
						xtype : 'textfield',
						fieldLabel : '名称',
						name : 'LABEL_CN',

						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .3,// second column
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
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
					} ]
				}, {
					layout : 'form',
					columnWidth : .3,// second column
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,

					items : [ {
						xtype : 'textfield',
						fieldLabel : '编号',
						name : 'FIBERCAB_NO',
						
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
					columnWidth : .3,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,

					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '安装地址',
						name : 'LOCATION',

						comboxCfg : {
							cfgParams : {
								code : "DM.ROFH_FULL_ADDRESS"
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
					columnWidth : .3,// second column
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '维护方式',
						name : 'MAINT_MODE',
						multSel : true,
						minListWidth : 200,

						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMMaintMode"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .3,// second column
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,

					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '产权归属',
						name : 'OWNERSHIP',
						multSel : true,
						minListWidth : 200,

						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMCabOwnerShip"
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
