var TaskWireWindow = Ext.extend(Ext.Window ,{
	  title: '光缆管理',
	width:800,
	maximized :true,
	maximizable :true,
	modal:true,
	constructor : function(configer) {
		 configer.layout="fit";
		 this.title= '<div align="center">'+configer.typeTitle+'</div>';
		this.items = [
				{
              header:false,
            closable:true,
			height:650,
			width:900,
			modal:true,
	 layout: 'fit',
    items: [{
		xtype:'panel',
		layout: 'anchor',
		header:false,
        items: [{
                        xtype: 'form',
                        header:false,
						layout:"column",
						border:false,
						style:"padding-top:10px;",
						defaults:{ //在这里同一定义item中属性，否则需要各个指明
							xtype:'textfield',
							labelAlign:'right',
							border:false,
							labelWidth:80,
							width:300
						},
						id:"PROJECT_QUERY_DATA",
						items:[
							{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'系统名称',
											xtype:'textfield',
											name:"LABEL_CN"
									},{
											fieldLabel:'工程名称',
											xtype:'textfield',
											name:"PROJECT"
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'所属区域',
											xtype : 'asyncombox',
											name:"RELATED_SPACE_CUID",
											comboxCfg : {
												cfgParams : {
													code : "DMDF.DISTRICT"
												}
											}
									},{
											fieldLabel:'产权类型',
											xtype : 'enumcombox',
											name:"OWNERSHIP",
											width:210,
											comboxCfg:{
												boName : 'EnumComboxBO',
												cfgParams : {
													code:"OwnerShip"
												}
											}
									}]
							}
							,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'系统级别',
											xtype : 'enumcombox',
											name:"SYSTEM_LEVEL",
											width:150,
											comboxCfg:{
												boName : 'EnumComboxBO',
												cfgParams : {
													code:"DMSystemLevel"
												}
											}
									},{
											fieldLabel:'维护方式',
											xtype:'combo',
											valueField : 'value',
											name:"MAINT_MODE", 
											width:150,
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['自维','1'],['代维','2'],['未知','0']]
											}),
											displayField:'text',
											mode: "local",
											value:'9'
											
									}]
							},{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'施工单位',
											xtype:'textfield',
											name:"BUILDER" 
									},{
											fieldLabel:configer.purpose+'段用途',
											xtype:'combo',
											valueField : 'value', 
											width:150,
											name:"PURPOSE" ,
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											['全部','9'],
											['未知','0'],
											['自用','1'],
											['出租','2'],
											['共享','3'],
											['共享我方资源','4']
											]
											}),
											displayField:'text',
											mode: "local",
											value:'9'
									}]
							}
						  ],
						  buttons:[{
											text:'查询',
											xtype:'button',
											handler:function(btn){
												var oldParams = Ext.getCmp("DETAIL_GRID_DATA").getStore().proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
												Ext.getCmp("DETAIL_GRID_DATA").getStore().proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
													return [Ext.apply(oldParams[0],{
														cfgParams:{
															queryData:Ext.encode(Ext.getCmp("PROJECT_QUERY_DATA").getForm().getValues())
														}
														})]; 
												};
												 Ext.getCmp("DETAIL_GRID_DATA").getStore().load();
											}
									}],
						 buttonAlign:"center",
                        anchor: '100% 20%'
                    }, {
                        xtype: 'panel',
                        header:false,
						layout:'fit',
						items:{
							xtype:'panel',
							 layout: 'border',
									items:[{
										 region: 'center',
										 tbar:new Ext.PagingToolbar({
													pageSize: 20,
													store: configer.topStoreConfig,
													displayInfo: true,
													displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
													emptyMsg: "没有数据"
												}),
										 items:[{
												  xtype:'grid',
												  frame:true,
												  store : configer.topStoreConfig,
												  id:"DETAIL_GRID_DATA",
												  viewConfig: {
													  templates:{
														cell : new Ext.Template(
															'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
															'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">{value}</div></div>',
															'</td>'
															)
														
													},
													selectedRowClass:"x-grid3-row-data-selected"
												   },
												 columns:configer.topData,
											 }],
										 layout:'fit',
										 header:false
									}]
								},
								anchor: '100% 80%'
							}]
			}],
			
			}
			];
		TaskWireWindow.superclass.constructor.call(this, configer);
 
	},
	initComponent:function() {
		TaskWireWindow.superclass.initComponent.call(this);
	},
	buttons:[{
				text:"选中",
				handler:function(btn,event){
					var rightGridStore = Ext.getCmp("RIGHT_GRID_DATA").getStore();
					var selGridSelects = Ext.getCmp("DETAIL_GRID_DATA").getSelectionModel().getSelections();
					var dataObjs = [];
					for(var index=0;index<selGridSelects.length;index++){
						dataObjs.push([selGridSelects[index].data.CUID,selGridSelects[index].data.LABEL_CN]);
					}
					rightGridStore.loadData(dataObjs);
					btn.ownerCt.ownerCt.close();
				}
			},{
				text:"关闭",
				handler:function(btn){
					btn.ownerCt.ownerCt.close();
				}
			}],
	buttonAlign:"right",
	height:600
	
	
});