var TaskHiddenDangerPanel = Ext.extend(Ext.Window ,{
	title:"<div  style='text-align:center;' >隐患查看</div>",
	width:800,
	modal:true,
	style:"padding-top:10px;",
	constructor : function(configer) {
		 configer.layout="border";
		 var fields = [								{
														name:"IMAGE_LINK"
													},{
														name:"RESOUCE_TYPE"
													},{
														name:"LABEL_CN"
													},{
														name:"TROUBLE_TYPE"
													},{
														name:"TROUBLE_DETAIL"
													},{
														name:"LONGITUDE"
													},{
														name:"LATITUDE"
													},{
														name:"UPLOAD_HIDDEN_TIME"
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
													 					boName:"TaskHiddenDangerBO",
																		custType:Ext.encode(configer.data),
																		custCode:Ext.encode(fields)
													 				}];
													 			}
													 		}
													 	}
													 }),
													baseParams : {
														start : 0,
														limit : 20,
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
											fieldLabel:'点设施名称',
											width:100,
											name:"POINT_NAME",
											id:"POINT_NAME_ID",
											xtype:'textfield'
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
														fieldLabel:'点设施类型',
														xtype:'combo',
														valueField : 'value', 
														width:100,
														name:"POINT_TYPE" ,
														id:"POINT_TYPE_ID",
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
							} ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											fieldLabel:'隐患类型',
											width:100,
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['线路公共隐患','1'],['管道隐患','2'],['直埋隐患','3'],['架空隐患','4']]
											}),
											displayField:'text',
											name:"TROBLE_TYPE",
											id:"TROBLE_TYPE_ID",
											mode: "local",
											value:'9',	
									}]
							} ,{
								xtype:'panel',
								layout:'form',
								style:"margin-top:10px",
								items:[{
											text:'查询',
											xtype:'button',
											handler:function(btn){
												 var oldParams = checkPortalStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
												checkPortalStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
													return [Ext.apply(oldParams[0],{
														cfgParams:{
															POINT_NAME:Ext.getCmp("POINT_NAME_ID").getValue(),
															POINT_TYPE:Ext.getCmp("POINT_TYPE_ID").getValue(),
															TROBLE_TYPE:Ext.getCmp("TROBLE_TYPE_ID").getValue()
														}
														})]; 
												};
												checkPortalStoreConfig.load();
											}
									}]
							}
					   ],
					   header:false				   
				   }, {
								 region: 'center',
								 tbar:new Ext.PagingToolbar({
										    pageSize: 20,
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
											{header: "图片路径", sortable: true, dataIndex: 'IMAGE_LINK'},
											{header: "巡检点类型", sortable: true, dataIndex: 'RESOUCE_TYPE'},
											{header: "点设施名称", sortable: true, dataIndex: 'LABEL_CN'},
											{header: "隐患类型", sortable: true, dataIndex: 'TROUBLE_TYPE'},
											{header: "隐患详细", sortable: true, dataIndex: 'TROUBLE_DETAIL'},
											{header: "经度", sortable: true, dataIndex: 'LONGITUDE'},
											{header: "纬度", sortable: true, dataIndex: 'LATITUDE'},
											{header: "图片上传时间", sortable: true, dataIndex: 'UPLOAD_HIDDEN_TIME'},
											{header: "备注", sortable: true, dataIndex: 'REMARK'}
											]
									 }],
								 layout:'fit',
								 header:false
							}
				   ];
		TaskHiddenDangerPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		TaskHiddenDangerPanel.superclass.initComponent.call(this);
	},
	buttonAlign:"center",
	buttons:[{
		text:"查看隐患图片",
		handler:function(btn){
			var grid = btn.ownerCt.ownerCt.items.get(1).items.get(0);
			var selectRows = grid.getSelectionModel().getSelections();
			if(selectRows.length == 1){
					if("undefined" == typeof selectRows[0].data.IMAGE_LINK){
														Ext.Msg.alert("提示","没有故障图片");
														return ;
													}else{
													new HitchTaskPicPanel({data:{
													id:"img_1",
													src:ctx+selectRows[0].data.IMAGE_LINK,
													}}).show(); 
													}
			}else{
				Ext.Msg.alert("提示","请选择一条数据!");
			}
		}
	
	},{
		text:"定位隐患点"
	},{
		text:"导出",
		handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(1).items.get(0);
								grid.getSelectionModel().selectAll();
								var selectRows = grid.getSelectionModel().getSelections();
								if(selectRows.length > 0){
										new TaskExportPanel({fileName:"xunjiandian",dataExport:selectRows,data:topData,height:330,column:1,width:300,baseData:oldParams}).show();
								}
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