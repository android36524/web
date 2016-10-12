Ext.onReady(function() {
		
	Ext.QuickTips.init();
	GridViewAction.getGridMeta({
	 boName:"ProjectTaskBO",
	 cfgParams:{
		templateId:"DMMA.PROJECTTASK.GRID" 
	 }
 }, function(data){
	  function getStore(nfields,custType){
		var fields = [];
		
		for(var i=0;i<nfields.length;i++){
			fields.push({name:nfields[i].dataIndex});
		};
		return new Ext.data.Store(Ext.apply({
												autoLoad : true,
												remoteSort : false,
												reader : new Ext.data.JsonReader({
													root : 'list',
													totalProperty : 'totalCount',
													fields : fields
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
													 					boName:"ProjectTaskBO",
																		custType:custType,
																		custCode:Ext.encode(fields)
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
	};
	var topData = data.columns;
	var topStoreConfig = getStore(topData,"top");
 	new Ext.Viewport({
    layout: 'border',
    items: [{
                        xtype: 'form',
                       layout:"column",
						title:'资源采集任务管理',
						height:80,
						border:false,
						defaults:{ //在这里同一定义item中属性，否则需要各个指明
							xtype:'textfield',
							labelAlign:'right',
							border:false,
							labelWidth:80,
							columnWidth:1/5
						},
						items:[
							{
								xtype:'panel',
								layout:'form',
								
								items:[{
											fieldLabel:'任务名称',
											xtype:'textfield',
											name:"LABEL_CN",
											width:185
									},{
											fieldLabel:'完成情况',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['未开始','1'],['开始','0'],['结束','0']]
											}),
											displayField:'text',
											mode: "local",
											name:"TASK_STATE",
											value:'9'
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'任务状态',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['未接受','1'],['接受','0'],['正在处理','2'],['完成','3']]
											}),
											displayField:'text',
											mode: "local",
											name:"PDA_TASK_STATE",
											value:'9'
									},{
											fieldLabel:'归档时间',
											xtype:'datetimefield',
											name:'ARCHIVE_TIME',
											width:185
									}]
							}
							,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'实际结束时间',
											xtype:'datetimefield',
											name:'PDA_ACTUAL_END_TIME',
											width:185
									},{
											fieldLabel:'是否归档',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											['全部','9'],
											['否','1'],
											['是','2']
											]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
											name:"ISSAVE"
									}]
							},{
								xtype:'panel',
								layout:'form',
								style:"margin-left:100px;",
								items:[{
											text:'查询',
											width:50,
											height:25,
											xtype:'button',
											handler:function(btn){
												var queryData = btn.ownerCt.ownerCt.getForm().getValues();
												var oldParams = topStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
												topStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
													return [Ext.apply(oldParams[0],{
														cfgParams:queryData
														})]; 
												};
												topStoreConfig.load();
											}
									},{
											text:'清除',
											style:"margin-top:5px;",
											width:50,
											height:25,
											xtype:'button',
											handler:function(btn){
											btn.ownerCt.ownerCt.getForm().reset();
											}
									}]
							}
						  ],
                        region: 'north'
                    },{
		
								 region: 'center',
								 bbar:['->',{
									 text:"查看新建的点资源",
									 handler:function(btn){
										var selectionDatas = Ext.getCmp("PROJECT_TASK_GRID").getSelectionModel().getSelections();
										 if(selectionDatas.length > 0){
											new ProjectViewSourcePanel({RELATED_TASK_CUID:selectionDatas[0].data.CUID}).show();
										 }
										
									 }
								 },{
									 text:"新增工程任务",
									 handler:function(btn){
										 new ProjectAddTaskPanel({}).show();
									 }
								 },{
									 text:"删除",
									 handler:function(){
										var selectionDatas = Ext.getCmp("PROJECT_TASK_GRID").getSelectionModel().getSelections();
										var delObjects= [];
										for(var i =0;i<selectionDatas.length;i++){
											delObjects.push(selectionDatas[i].data);
											
										}
										ProjectTaskAction.doDelete(Ext.encode(delObjects),function(resault){
											 Ext.getCmp("PROJECT_TASK_GRID").getStore().load();
										});
										
									 }
								 },{
									 text:"归档",
									 handler:function(){
										 var selectionDatas = Ext.getCmp("PROJECT_TASK_GRID").getSelectionModel().getSelections();
										var delObjects= [];
										for(var i =0;i<selectionDatas.length;i++){
											delObjects.push(selectionDatas[i].data);
											
										}
										ProjectTaskAction.saveResource(Ext.encode(delObjects),function(resault){
											 Ext.getCmp("PROJECT_TASK_GRID").getStore().load();
										});
									 }
								 }],
								 tbar:new Ext.PagingToolbar({
										    pageSize: 20,
											store: topStoreConfig,
											displayInfo: true,
											displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
											emptyMsg: "没有数据"
										}),
								 items:[{
										  xtype:'grid',
										  frame:false,
										  store :topStoreConfig,
										  id:"PROJECT_TASK_GRID",
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
										 columns:topData,
									 }],
								 layout:'fit',
								 header:false
	}]
	});
 })
	


});