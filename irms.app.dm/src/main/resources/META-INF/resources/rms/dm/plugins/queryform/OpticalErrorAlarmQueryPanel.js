Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.OpticalErrorAlarmQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.OpticalErrorAlarmQueryPanel.superclass.constructor
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
						anchor : '-20'
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'RELATED_CITY_CUID',
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
				},{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype:'dmcombox',
					    fieldLabel:'EMS',
					    name:'RELATED_EMS_CUID',
					    templateId: 'service_dict_dm.DM_EMS',
					    queryCfg:{
					    	  type:"string",
					    	  relation:"="
					    } } ]
				},{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : '-20'
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '告警设备',
			            name: 'EMS_ALM_DEVINFO',
			            width : 200,
			            queryCfg : {
							type : "string",
							relation :'like',
							blurMatch : 'both'
						}
					} ]
				},{
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'datefield',
						fieldLabel : '告警时间-从',
						name : 'NEALARM_TIME',
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
				}, {
					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'datefield',
						fieldLabel : '告警时间-到',
						name : 'NEALARM_TIME',
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
				} ,{

					layout : 'form',
					columnWidth : columnwidth,
					defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '设备类型',
						name : 'VALUE',
					      multSel:true,
					      minListWidth:200,
					      comboxCfg :{
					    	  boName:'EnumTemplateComboxBO',
					    	  cfgParams:{
					    		  code:"NeSingeType"
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
		