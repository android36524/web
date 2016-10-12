var ProjectTaskAddPdaPanel = Ext.extend(Ext.Window ,{
	title:"PDA设备组管理",
	width:800,
	modal:true,
	constructor : function(configer) {
		this.beforeClose = configer.beforeClose;
		 configer.layout="fit";
		 configer.items= [{
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
							width:260
						},
						items:[
							{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'设备组名',
											xtype:'textfield',
											name:"aaa"
									}]
							}
								 
							,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'所属区域',
											xtype:'textfield',
											name:"ccc"
									}]
							},{
								xtype:'panel',
								layout:'form',
								items:[{
											text:'查询',
											xtype:'button'
									}]
							}
						  ],
                        anchor: '100% 10%'
                    }, {
                        xtype: 'panel',
                        header:false,
						layout:'fit',
						items:{
							xtype:'panel',
							 layout: 'border',
							items:[{
								 region: 'center',
								items:[{
										  xtype:'grid',
										  frame:true,
										   listeners :{
											 rowclick:function( grid ,  rowIndex,  e ) {
												var oldParams = Ext.getCmp("DEVICE_DETAILS_GRID").getStore().proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
												Ext.getCmp("DEVICE_DETAILS_GRID").getStore().proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
													return [Ext.apply(oldParams[0],{
														cfgParams:{
															DEVICE_GROUP_CUID:grid.getStore().getAt(rowIndex).data.CUID
														}
														})]; 
												};
												 Ext.getCmp("DEVICE_DETAILS_GRID").getStore().load();
												 Ext.getCmp("DEVICE_DETAIL_PROPERTY").getStore().loadData([
													['名称',grid.getStore().getAt(rowIndex).data.RELATED_DISTRICT_CUID,"区域"],
												    ['名称',grid.getStore().getAt(rowIndex).data.LABEL_CN,"基本属性"]
												 ]);
											 }
											 
										 },
										  store : new Ext.data.Store(Ext.apply({
												autoLoad : true,
												remoteSort : false,
												reader : new Ext.data.JsonReader({
													root : 'list',
													totalProperty : 'totalCount',
													fields : [{
														name:"CUID"
													},{
														name:"LABEL_CN"
													},{
														name:"RELATED_DISTRICT_CUID"
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
													 					boName:"DeviceGroupBO",
																		custCode:Ext.encode([{
														name:"CUID"
													},{
														name:"LABEL_CN"
													},{
														name:"RELATED_DISTRICT_CUID"
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
												})),
										 viewConfig: {
										    fit:true,
											selectedRowClass:"x-grid3-row-data-selected",
											forceFit: true
										   },
										 columns: [
										 	{header: "编号",  sortable: true, dataIndex: 'CUID',hidden:true},
											{header: "名称",  sortable: true, dataIndex: 'LABEL_CN'},
											{header: "所属区域",  sortable: true,dataIndex: 'RELATED_DISTRICT_CUID'}
										],
									 }],
								 layout:'fit',
								 header:false
							},{
								  width:350,
								  region: 'east',
								  items:[new Ext.grid.GridPanel({
									    region: 'center',
										id:"DEVICE_DETAIL_PROPERTY",
										store: new Ext.data.GroupingStore({
										reader:  new Ext.data.ArrayReader({}, [
											   {name: 'LABEL_CN'},
											   {name: 'LABEL_VALUE'},
											   {name: 'LABEL_TYPE'}
											]),
											data: [
											
											],
										groupField:'LABEL_TYPE'
									}),
									columns: [
										{header: "属性", width: 60, sortable: true, dataIndex: 'LABEL_CN',menuDisabled :true,renderer: function(value, metaData, record, rowIndex, colIndex, store) {
											return value;
										}},
										{header: "数值", width: 20, sortable: true, dataIndex: 'LABEL_VALUE',menuDisabled :true},
										{header: "类别", width: 20, sortable: true, dataIndex: 'LABEL_TYPE',hidden:true,menuDisabled :true}
									],
									view: new Ext.grid.GroupingView({
										forceFit:true,
										selectedRowClass:"x-grid3-row-detail-selected"
									}),
									frame:true,
									header:false
								})],
								  layout:'border',
								  header:false
							}]
						},
						anchor: '100% 50%'
                    },{
						xtype:'panel',
						anchor: '100% 40%',
						layout:'fit',
						items:[{
										  xtype:'grid',
										  frame:true,
										  id:"DEVICE_DETAILS_GRID",
										  store :new Ext.data.Store(Ext.apply({
												autoLoad : true,
												remoteSort : false,
												reader : new Ext.data.JsonReader({
													root : 'list',
													totalProperty : 'totalCount',
													fields : [{
														name:"CUID"
													},{
														name:"DEVICE_CODE"
													},{
														name:"USER_NAME"
													},{
														name:"DEVICE_STATE"
													},{
														name:"GROUPNAME"
													},{
														name:"REMARK"
													},{
														name:"PHONENUMBER"
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
													 					boName:"DeviceBO",
																		custCode:Ext.encode([{
														name:"CUID"
													},{
														name:"DEVICE_CODE"
													},{
														name:"USER_NAME"
													},{
														name:"DEVICE_STATE"
													},{
														name:"GROUPNAME"
													},{
														name:"REMARK"
													},{
														name:"PHONENUMBER"
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
												})),
										 viewConfig: {
										    fit:true,
											forceFit: true
										   },
										 columns: [
										 	{header: "编号",  sortable: true, dataIndex: 'CUID',hidden:true},
											{header: "PDA编码",  sortable: true, dataIndex: 'DEVICE_CODE'},
											{header: "用户",  sortable: true,dataIndex: 'USER_NAME'},
											{header: "设备状态",  sortable: true,dataIndex: 'DEVICE_STATE'},
											{header: "归属设备组",  sortable: true,dataIndex: 'GROUPNAME'},
											{header: "备注",  sortable: true,dataIndex: 'REMARK'},
											{header: "手机号码",  sortable: true,dataIndex: 'PHONENUMBER'}
										],
									 }]
						
					}]
	}]
		ProjectTaskAddPdaPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		ProjectTaskAddPdaPanel.superclass.initComponent.call(this);
	},
	buttons:[{
		text:"选中",
		handler:function(btn){
			btn.ownerCt.ownerCt.beforeClose(Ext.getCmp("DEVICE_DETAILS_GRID").getSelectionModel().getSelections());
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