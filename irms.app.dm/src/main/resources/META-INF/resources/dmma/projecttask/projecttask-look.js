var ProjectViewSourcePanel = Ext.extend(Ext.Window ,{
	title:"采集点列表",
	width:800,
	modal:true,
	constructor : function(configer) {
		 configer.layout="border";
		 var bottomStore = new Ext.data.Store(Ext.apply({
												autoLoad : true,
												remoteSort : false,
												reader : new Ext.data.JsonReader({
													root : 'list',
													totalProperty : 'totalCount',
													fields : [{
														name:"POINTNAME"
													},{
														name:"POINTTYPE"
													},{
														name:"LONGITUDE"
													},{
														name:"LATITUDE"
													},{
														name:"OWNERSHIP"
													},{
														name:"PURPOSE"
													},{
														name:"CREATETIME"
													}]
												}),
												paramNames : {
													limit : 20
												}
											},{
												 proxy : new Ext.ux.data.DwrProxy({
													 	apiActionToHandlerMap : {
													 		read : {
													 			dwrFunction : GridViewAction.getGridData,
													 			getDwrArgsFunction : function(result) {
													 				return [{
													 					boName:"GatherPointsBO",
																		cfgParams:{
																			RELATED_TASK_CUID:configer.RELATED_TASK_CUID
																		},
																		custCode:Ext.encode( [{
														name:"POINTNAME"
													},{
														name:"POINTTYPE"
													},{
														name:"LONGITUDE"
													},{
														name:"LATITUDE"
													},{
														name:"OWNERSHIP"
													},{
														name:"PURPOSE"
													},{
														name:"CREATETIME"
													}])
													 				}];
													 			}
													 		}
													 	}
													 }),
													baseParams : {
														start : 0,
														limit : 20
													}
												}));
		 configer.items=[
				   {
										 xtype:'grid',
										  layout:'fit',
										    region: 'center',
										  frame:true,
										   viewConfig: {
										    fit:true,
											selectedRowClass:"x-grid3-row-data-selected",
											templates:{
												cell : new Ext.Template(
													'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
													'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">{value}</div></div>',
													'</td>'
													)
												
											},
											forceFit: true
										   },
										    tbar:new Ext.PagingToolbar({
										    pageSize: 20,
											store: bottomStore,
											displayInfo: true,
											displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
											emptyMsg: "没有数据"
										}),
										  store :bottomStore ,
										columns: [
											{header: "资源点名称",  sortable: true, dataIndex: 'POINTNAME'},
											{header: "点设施类型",  sortable: true,dataIndex: 'POINTTYPE'},
											{header: "经度",  sortable: true,dataIndex: 'LONGITUDE'},
											{header: "纬度",  sortable: true,dataIndex: 'LATITUDE'},
											{header: "产权",  sortable: true,dataIndex: 'OWNERSHIP'},
											{header: "用途",  sortable: true,dataIndex: 'PURPOSE'},
											{header: "采集时间",  sortable: true,dataIndex: 'CREATETIME'}
										]
					},
					{
					   xtype:'panel',
					   width:350,
					   height:60,
					   header:false,
					   region: 'north',
					   items:[{
											xtype:'panel',
											  style:"margin-top:10px;",
											layout:'column',
											border:false,
											collapsible: false,
											header:false,
											items:[{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "textfield",
											items:[{
														fieldLabel:'点设施类型',
														xtype:'combo',
														valueField : 'value', 
														width:150,
														name:"POINTTYPE" ,
														id:"POINTTYPE_ID",
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['站点','1'],
														['人井','2'],
														['电杆','3'],
														['标石','4'],
														['拐点','5'],
														['交接箱','6'],
														['分纤箱','7'],
														['接头盒','8'],
														['接入点','9']
														]
														}),
														displayField:'text',
														mode: "local"
												}]
										},{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "textfield",
											items:[{
													fieldLabel:'名称' ,
													id:"POINTNAME_ID",
													name:"POINTNAME"
												}]
										},{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "button",
											items:[{
														text:'查询',
														handler:function(btn){
															var queryData ={
																		RELATED_TASK_CUID:configer.RELATED_TASK_CUID,
																		POINTNAME:Ext.getCmp("POINTNAME_ID").getValue(),
																		POINTTYPE:Ext.getCmp("POINTTYPE_ID").getValue()
																	};
																	console.dir(queryData);
															var oldParams = bottomStore.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
															bottomStore.proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
																return [Ext.apply(oldParams[0],{
																	cfgParams:queryData
																	})]; 
															};
															bottomStore.load();
														}
												}]
										}]
										} 
					   ]
					}
			];
		ProjectViewSourcePanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		ProjectViewSourcePanel.superclass.initComponent.call(this);
	},
	buttons:[{
		text:"关闭",
		handler:function(btn){
			btn.ownerCt.ownerCt.close();
		}
	}],
	buttonAlign:"right",
	height:600
	
	
});