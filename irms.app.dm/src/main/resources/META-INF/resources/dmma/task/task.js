
Ext.onReady(function() {
	Ext.QuickTips.init();
 GridViewAction.getGridMeta({
	 boName:"TaskBO",
	 cfgParams:{
		templateId:"DMMA.TASK.TOP.GRID" 
	 }
 }, function(data){
	  function getStore(nfields,custType){
		var fields = [];
		
		for(var i=0;i<nfields.length;i++){
			fields.push({name:nfields[i].dataIndex});
		};
		fields.push({name:"RELATED_DISTRICT_NAME"});
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
													 					boName:"TaskBO",
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
	for(var index =0;index<topData.length;index++){
		if(topData[index].dataIndex == "CUID"){
			topData[index].hidden = true;
		};
		if(topData[index].dataIndex == "LABEL_CN"){
			topData[index].width = 350;
		};
	};
	var topStoreConfig = getStore(topData,"top");
	var bottomData = data.dColumns;
	for(var index =0;index<bottomData.length;index++){
		if(bottomData[index].dataIndex == "CUID"){
			bottomData[index].hidden = true;
		};
		if(bottomData[index].dataIndex == "LABEL_CN"){
			bottomData[index].width = 390;
		};
		if(bottomData[index].dataIndex == "PTASKNAME"){
			bottomData[index].width = 350;
		};
	};
	var bottomStoreConfig	= getStore(bottomData,"bottom");										
new Ext.Viewport({
    layout: 'fit',
    items: [{
		xtype:'panel',
		layout: 'anchor',
		title:"巡线任务管理",
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
							columnWidth:1/5
						},
						items:[
							{
								xtype:'panel',
								layout:'form',
								
								items:[{
											fieldLabel:'任务名称',
											xtype:'textfield',
											name:"LABEL_CN"
									}, {						
											xtype : 'asyncombox',
											fieldLabel : '所属区域',
											name : 'RELATED_DISTRICT_CUID',
											width:150,
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
							}
								  ,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'分配PDA',
											xtype:'pdaselectwindow',
											width:185,
											name:"RELATED_PDA"
									},{
											fieldLabel:'是否有隐患',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['否','1'],['是','0']]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
											name:"ISDANGER"
									}]
							}
							,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'是否周期任务',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [['全部','9'],['否','1'],['是','0']]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
											name:"IS_CYCLE"
									},{
											fieldLabel:'周期频率',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											['全部','9'],
											['按周','1'],
											['按每个月','2'],
											['每两个月','3'],
											['按季度','4'],
											['按半年','5']
											]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
											name:"FREQUENCE_TYPE"
									}]
							},{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'按月份',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											['全部','99'],
											['1','1'],
											['2','2'],
											['3','3'],
											['4','4'],
											['5','5'],
											['6','6'],
											['7','7'],
											['8','8'],
											['9','9'],
											['10','10'],
											['11','11'],
											['12','12']
											]
											}),
											displayField:'text',
											mode: "local",
											name:"MONTHITEMS",
											value:'99'
									},{
										fieldLabel:'任务类型',
										xtype:'combo',
										valueField : 'value', 
										store: new Ext.data.SimpleStore({
										fields: ['text', 'value'],
										data : [['全部','9'],['日常巡检','1'],['验收巡检','2'],['资源核查','3']]
										}),
										displayField:'text',
										mode: "local",
										value:'9',	
										name:"TASKTYPE"
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
                        anchor: '100% 15%'
                    }, {
                        xtype: 'panel',
                        header:false,
						frame:false,
						border:false,
						layout:'fit',
						items:{
							xtype:'panel',
							 layout: 'border',
							items:[{
								 region: 'center',
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
										  store : topStoreConfig,
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
												 var oldParams = bottomStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
												bottomStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction =function(){
													return [Ext.apply(oldParams[0],{
														cfgParams:{
															TASK_CUID:grid.getStore().getAt(rowIndex).data.CUID,
															TASK_NAME:grid.getStore().getAt(rowIndex).data.LABEL_CN
														}
														})]; 
												};
												bottomStoreConfig.load();
											 }
											 
										 },
										 columns: topData,
									 }],
								 layout:'fit',
								 header:false
							}]
						},
						bbar:['->',{
							text:"导出",
							tooltip:"导出所有数据",
							handler:function(btn){
								var oldParams = topStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
								var grid = btn.ownerCt.ownerCt.items.get(0).items.get(0).items.get(0);
								grid.getSelectionModel().selectAll();
								var selectRows = grid.getSelectionModel().getSelections();
								new TaskExportPanel({fileName:"xunjianbtask",dataExport:selectRows,data:topData,height:330,column:1,width:300,baseData:oldParams}).show();
							}
						},{
							text:"选择导出",
							tooltip:"导出所有数据",
							handler:function(btn){
								var oldParams = topStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
								var grid = btn.ownerCt.ownerCt.items.get(0).items.get(0).items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								new TaskExportPanel({fileName:"xunjianbtask",dataExport:selectRows,data:topData,height:330,width:300,column:1,baseData:oldParams}).show();
							}
						},{
							text:"新增任务",
							handler:function(btn){
								new TaskAddPanel({}).show();
							}
						},{
							text:"删除",
							handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(0).items.get(0).items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								var delObj = [];
								for(var index =0;index<selectRows.length;index++){
									delObj.push(selectRows[index].data);
								}
								TaskAction.doDelete(Ext.encode(delObj),function(res){
									grid.getStore().load();
								});
								
							}
						}],
                        anchor: '100% 50%'
                    },{
						xtype:'panel',
						anchor: '100% 35%',
						layout:'fit',
						bbar:['->',{
							text:"导出",
							handler:function(btn){
								var oldParams = bottomStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
								var grid = btn.ownerCt.ownerCt.items.get(0);
								grid.getSelectionModel().selectAll();
								var selectRows = grid.getSelectionModel().getSelections();
								new TaskExportPanel({fileName:"xunjiansubtask",dataExport:selectRows,data:bottomData,height:330,width:350,column:2,baseData:oldParams}).show();
							}
						},{
							text:"选择导出",
							handler:function(){
								var oldParams = bottomStoreConfig.proxy.apiActionToHandlerMap.read.getDwrArgsFunction ();
								var grid = btn.ownerCt.ownerCt.items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								new TaskExportPanel({fileName:"xunjiansubtask",dataExport:selectRows,data:bottomData,height:330,width:350,column:2,baseData:oldParams}).show();
							}
						},{
							text:"查看巡检点",
							handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								var dataObj = [];
								for(var index =0;index<selectRows.length;index++){
									dataObj.push(selectRows[index].data);
								}
								if(selectRows.length ==  1){
									new TaskCheckPatrolPanel({data:dataObj}).show();
								}else{
									  Ext.MessageBox.alert("提示", "请选择一条记录");
								}
							}
						},{
							text:"巡线轨迹",
							handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								var dataObj = [];
								for(var index =0;index<selectRows.length;index++){
									dataObj.push(selectRows[index].data);
								}
								if(selectRows.length ==  1){
									//new TaskPatrolTracePanel({data:dataObj}).show();
									 var fields = [{
											name:"CUID"
										},{
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
																custType:Ext.encode(dataObj),
																custCode:Ext.encode(fields)
											 				}];
											 			}
											 		}
											 	}
											 }),
											baseParams : {
												start : 0,
												limit : 100000,
												totalNum : 10
											}
										}));
									
									
									var datas = new ht.List();
									checkPortalStoreConfig.each(function(rec){
										 var hdata = new ht.Data();
										 hdata.a('CUID',(rec.getData())["CUID"]);
										 hdata.a('LABEL_CN',(rec.getData())["LABEL_CN"]);
										 hdata.a('LONGITUDE',(rec.getData())["NEW_LONGITUDE"]);
										 hdata.a('LATITUDE',(rec.getData())["NEW_LATITUDE"]);
										 datas.add(hdata);
									});
									
									
									 /**
									  * 获取框架panel，向上找5层
									  */
									 var scope = window;
									 var explorer = undefined;
									 var tabPanel = undefined;
									 try {
										 for(var i = 0; i < 5; i++) {
											 if(scope.Ext) {
												 explorer = scope.Ext.getCmp("explorer_frame_panel");
												 if(explorer) {
													 break;
												 }
											 }
											 scope = scope.parent;
										 }
									  }catch(e){}
									  /**
									   * 激活地图主页,调用定位function
									   */
									  if(explorer){
									  	var childTab = explorer.centerPanel.items.items[0];
										explorer.centerPanel.activate(childTab);
										var contentWindow = explorer.el.child('iframe').dom.contentWindow;
										//contentWindow.Dms.Tools.graphicLocateOnMap(cuid);
										contentWindow.dms.Default.tpmap.locateResouce(datas);
									  }
									
								}else{
									  Ext.MessageBox.alert("提示", "请选择一条记录");
								}
							}
						},{
							text:"隐患查看",
							handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								var dataObj = [];
								for(var index =0;index<selectRows.length;index++){
									dataObj.push(selectRows[index].data);
								}
								if(selectRows.length ==  1){
									new TaskHiddenDangerPanel({data:dataObj}).show();
								}else{
									  Ext.MessageBox.alert("提示", "请选择一条记录");
								}
							}
						},{
							text:"删除子任务",
							handler:function(btn){
								var grid = btn.ownerCt.ownerCt.items.get(0);
								var selectRows = grid.getSelectionModel().getSelections();
								
								if(selectRows.length ==  1){
									TaskAction.doDeleteSubTask(Ext.encode(selectRows[0].data),function(res){
										grid.getStore().load();
									});
								}else{
									  Ext.MessageBox.alert("提示", "请选择一条记录");
								}
								
							}
						}],
						items:[{
										  xtype:'grid',
										frame:false,
										  store :bottomStoreConfig,
										 viewConfig: {
										  templates:{
												cell : new Ext.Template(
													'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
													'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">{value}</div></div>',
													'</td>'
													)
												
											},
											selectedRowClass:"x-grid3-row-detail-selected"
										   },
										 columns: bottomData,
									 }]
						
					}]
	}]
});

	//new TaskAddPanel({}).show();
	//new TaskCheckPatrolPanel({}).show();
	 
 });


	
	});
	
	
		