Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.JkSegGroupQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.JkSegGroupQueryPanel.superclass.constructor.call(this);
		var columnWidth = 80;
		var panel = new Ext.Panel({
			layout : 'form',
			height : 30,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-5'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '单位工程名称',
						name : 'SG.LABEL_CN',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'	
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-5'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'SG.RELATED_DISTRICT_CUID',
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
				},{
					layout : 'form',
					columnWidth : .3,
					defaults : {
						anchor : '-5'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '所属项目编号',
						name : 'PROJECT_NO',
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'	
						}
					}]
				}]
			}]
		});
		return panel;
	}
});