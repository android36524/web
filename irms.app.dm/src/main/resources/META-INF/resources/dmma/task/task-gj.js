var TaskPatrolTracePanel = Ext.extend(Ext.Window ,{
	title:"巡检点列表",
	width:800,
	modal:true,
	style:"padding-top:10px;",
	constructor : function(configer) {
		 configer.layout="border";
		 var fields = [{
														name:"RESOUCE_TYPE"
													},{
														name:"LABEL_CN"
													},{
														name:"POINT_STATE"
													},{
														name:"ORIGINAL_LONGITUDE"
													},{
														name:"ORIGINAL_LATITUDE"
													},{
														name:"NEW_LONGITUDE"
													},{
														name:"NEW_LATITUDE"
													},{
														name:"REMARK"
													}];
		var checkPortalStoreConfig = new Ext.data.Store(Ext.apply({
												autoLoad : true,
												remoteSort : false,
												reader : new Ext.data.JsonReader({
													root : 'list',
													totalProperty : 'totalCount',
													fields : fields
												}),
												paramNames : {
													limit : 'limit',
													start : 'start',
													dir : 'dir',
													sort : 'sort',
													total : 'totalNum',
													count : 'count'
												}
											},{
												 proxy : new Ext.ux.data.DwrProxy({
													 	apiActionToHandlerMap : {
													 		read : {
													 			dwrFunction : GridViewAction.getGridData,
													 			getDwrArgsFunction : function(result) {
													 				return [{
													 					boName:"TaskPatrolPointsBO",
																		custType:Ext.encode(configer.data),
																		custCode:Ext.encode(fields)
													 				}];
													 			}
													 		}
													 	}
													 }),
													baseParams : {
														start : 0,
														limit : 5,
														totalNum : 10
													}
												}));
		 configer.items=[
				   {
					   region: 'north',
					   layout:"column",
					   height:60,
					   border:false,
					   defaults:{ //在这里同一定义item中属性，否则需要各个指明
								xtype:'textfield',
								labelAlign:'right',
								border:false,
								labelWidth:80,
								columnWidth:1/4
								},
					   items:[
					   {
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											fieldLabel:'点设施类型',
											width:100,
											xtype:'textfield'
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											fieldLabel:'经纬度类型',
											width:100,
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['原经纬度','1'],['修改经纬度','0']]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
									}]
							} ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											fieldLabel:'名称',
											width:100,
											xtype:'textfield'
									}]
							} ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											text:'查询',
											xtype:'button'
									}]
							}
					   ],
					   header:false				   
				   }, {
								 region: 'center',
								 tbar:new Ext.PagingToolbar({
										    pageSize: 2,
											store: checkPortalStoreConfig,
											displayInfo: true,
											displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
											emptyMsg: "没有数据"
										}),
								 items:[{
										  xtype:'grid',
										  frame:false,
										  store : checkPortalStoreConfig,
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
										 listeners :{
											 rowclick:function( grid ,  rowIndex,  e ) {
												
											 }
											 
										 },
										 columns: [
											{header: "巡检点类型", sortable: true, dataIndex: 'RESOUCE_TYPE'},
											{header: "点设施名称", sortable: true, dataIndex: 'LABEL_CN'},
											{header: "是否到达", sortable: true, dataIndex: 'POINT_STATE'},
											{header: "原经度", sortable: true, dataIndex: 'ORIGINAL_LONGITUDE'},
											{header: "原纬度", sortable: true, dataIndex: 'ORIGINAL_LATITUDE'},
											{header: "修改经度", sortable: true, dataIndex: 'NEW_LONGITUDE'},
											{header: "修改纬度", sortable: true, dataIndex: 'NEW_LATITUDE'},
											{header: "备注", sortable: true, dataIndex: 'REMARK'}
											]
									 }],
								 layout:'fit',
								 header:false
							}
				   ];
		TaskPatrolTracePanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		TaskPatrolTracePanel.superclass.initComponent.call(this);
	},
	buttons:['->',{
		text:"同步经纬度"
	},{
		text:"导出",
		handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(1).items.get(0);
								grid.getSelectionModel().selectAll();
								var selectRows = grid.getSelectionModel().getSelections();
								new TaskExportPanel({fileName:"xunjiandian",dataExport:selectRows,data:topData,height:330,column:1,width:300,baseData:oldParams}).show();
		}
	},{
		text:"取消",
		handler:function(btn){
			btn.ownerCt.ownerCt.close();
		}
	}],
	buttonAlign:"right",
	height:600
})