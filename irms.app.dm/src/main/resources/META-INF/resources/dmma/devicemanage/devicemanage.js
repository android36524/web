Ext.onReady(function() {
	
	Ext.QuickTips.init();
new Ext.Viewport({
    layout: 'fit',
    items: [{
		xtype:'panel',
		layout: 'anchor',
		title:"PDA设备管理",
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
											fieldLabel:'设备编码',
											xtype:'textfield',
											name:"aaa"
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'设备用户',
											xtype:'textfield',
											name:"bbb"
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
								style:"margin-left:20px;",
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
								 tbar:new Ext.PagingToolbar({
										    pageSize: 2,
											store: {},
											displayInfo: true,
											displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
											emptyMsg: "没有数据"
										}),
								 items:[{
										  xtype:'grid',
										  frame:true,
										  store : new Ext.data.ArrayStore({
											    autoLoad:true,
														fields : [
														'company3'
														, 'price3'
														, 'change3'
														, 'pctChange3'
														, 'lastChange3'
														, 'lastChange23'],
														data : [
															[1, '未归档工单',1, '未归档工单',1, '未归档工单']
														]
												}),
										 viewConfig: {
										    fit:true,
											selectedRowClass:"x-grid3-row-data-selected",
											forceFit: true
										   },
										 columns: [
											{header: "PDA编码",  sortable: true, dataIndex: 'company3'},
											{header: "用户",  sortable: true,dataIndex: 'price3'},
											{header: "设备状态",  sortable: true, dataIndex: 'change3'},
											{header: "归属设备组",  sortable: true, dataIndex: 'pctChange3'},
											{header: "备注",  sortable: true,  dataIndex: 'lastChange3'},
											{header: "手机号码",sortable: true,  dataIndex: 'lastChange23'}
										],
									 }],
								 layout:'fit',
								 header:false
							},{
								  width:350,
								  region: 'east',
								  tbar:[{
									  text:'分组显示',
									  width:30,
									  handler : function(){
											store.clearGrouping();
										}
									},{
										text:'排序属性',
									  width:30
									},{
										text:'显示属性描述',
									  width:30
									},{
										xtype:'textfield',
										width:190
									}],
									items:[{
									  xtype:"panel",
									  height:200,
									  title:"明细",
									  region: 'south'
									},new Ext.grid.GridPanel({
									    region: 'center',
										store: new Ext.data.GroupingStore({
										reader:  new Ext.data.ArrayReader({}, [
											   {name: 'company'},
											   {name: 'price', type: 'float'},
											   {name: 'industry'}
											]),
													data: [['3m Co',71.72,0.02,0.03,'4/2 12:00am', 'Manufacturing'],
											['Alcoa Inc',29.01,0.42,1.47,'4/1 12:00am', 'Manufacturing'],
											['Altria Group Inc',83.81,0.28,0.34,'4/3 12:00am', 'Manufacturing'],
											['American Express Company',52.55,0.01,0.02,'4/8 12:00am', 'Finance'],
											['American International Group, Inc.',64.13,0.31,0.49,'4/1 12:00am', 'Services'],
											['AT&T Inc.',31.61,-0.48,-1.54,'4/8 12:00am', 'Services'],
											['Boeing Co.',75.43,0.53,0.71,'4/8 12:00am', 'Manufacturing']],
										sortInfo:{field: 'company', direction: "ASC"},
										groupField:'industry'
									}),
									columns: [
										{header: "Company", width: 60, sortable: true, dataIndex: 'company',menuDisabled :true,renderer: function(value, metaData, record, rowIndex, colIndex, store) {
											return value;
										}},
										{header: "Price", width: 20, sortable: true, dataIndex: 'price',menuDisabled :true},
										{header: "Industry", width: 20, sortable: true, dataIndex: 'industry',hidden:true,menuDisabled :true}
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
						bbar:['->',{
							text:"导出",
							tooltip:"导出所有数据"
						},{
							text:"选择导出",
							tooltip:"导出所有数据"
						},{
							text:"添加(A)"
						},{
							text:"修改(M)"
						},{
							text:"删除(D)"
						},{
							xtype: 'tbspacer',
							width:200
						},{
							text:"选中(S)"
						},{
							text:"保存(S)"
						},{
							text:"取消(C)"
						}],
                        anchor: '100% 90%'
                    }]
	}]
});



	
	});
	
	
		