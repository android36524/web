function changerDisabled(num){
	
			if(num == 1){
				Ext.getCmp("cycmz").enable();
			}else{
				Ext.getCmp("cycmz").disable();
			};
}
var TaskAddPanel = Ext.extend(Ext.Window ,{
	title:"任务详细",
	width:800,
	modal:true,
	constructor : function(configer) {
		
		 configer.layout="border";
		 configer.items=[
				   {
					  xtype:'panel',
					 header:false,
					  items:[{
							title: "基本信息",
							xtype: "fieldset",
							bodyPadding: 5,
							collapsible: true,
							defaults: {
								labelSeparator: "：",
								labelWidth: 65,
								width: 175
							},
							defaultType: "textfield",
							items: [{
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
								name:"TASK_TYPE"
						},{
								fieldLabel: "任务名称",
								id:"TASK_NAME"
							}, {
								fieldLabel: "备注",
								xtype:"textarea",
								id:"REMARK"
							}]
						},{
							title: "指派PDA列表",
							xtype: "fieldset",
							layout:'fit',
							height:200,
							girdData:[],
							collapsible: true,
							bbar:['->',{
								  text:"指派PDA",
								  handler:function(btn){
									  
									     function beforeClose(data){
										  var girdData = [];
										 girdData.push([data[0].data.DEVICE_CODE,
														data[0].data.USER_NAME,
														data[0].data.DEVICE_STATE,
														data[0].data.GROUPNAME,
														data[0].data.REMARK,
														data[0].data.PHONENUMBER
										]);
										Ext.getCmp("PDA_GRID").getStore().loadData(girdData);
										 btn.ownerCt.ownerCt.girdData = girdData;
									  }
									  new ProjectTaskAddPdaPanel({beforeClose:beforeClose}).show();
								
								  }
								},{
								  text:"移除PDA",
								  handler:function(btn){
									   var girdData = [];
										var selectionDatas = Ext.getCmp("PDA_GRID").getSelectionModel().getSelections();
										 if(selectionDatas.length > 0){
											 var cuidStr = "";
											 for(var index=0;index<selectionDatas.length;index++){
													var selectData = selectionDatas[index];
													cuidStr = cuidStr+selectData.data.DEVICE_CODE;
											}
											var storeData = Ext.getCmp("PDA_GRID").getStore();
											var totalCount =  Ext.getCmp("PDA_GRID").getStore().getTotalCount();
											for(var index = 0;index < totalCount;index++ ){
												var rec = storeData.getAt(index);
												if(-1 == cuidStr.lastIndexOf(rec.data.DEVICE_CODE)){
														girdData.push([rec.data.DEVICE_CODE,
														rec.data.USER_NAME,
														rec.data.DEVICE_STATE,
														rec.data.GROUPNAME,
														rec.data.REMARK,
														rec.data.PHONENUMBER
														]);		
												
												}
										}
										storeData.removeAll();	
										storeData.loadData(girdData);
										btn.ownerCt.ownerCt.girdData = girdData;
								  }
								  }
								}],
							items: [{
										  xtype:'grid',
										  frame:true,
										   id:"PDA_GRID",
										  store : new Ext.data.ArrayStore({
											    autoLoad:true,
														fields : [
														{name:'DEVICE_CODE'}
														, {name:'USER_NAME'}
														,{name:'DEVICE_STATE'}
														, {name:'GROUPNAME'}
														, {name:'REMARK'}
														, {name:'PHONENUMBER'}],
														data : [
															
														]
												}),
										 viewConfig: {
										    fit:true,
											forceFit: true
										   },
										 columns: [
											{header: "PDA编码",  sortable: true, dataIndex: 'DEVICE_CODE'},
											{header: "用户",  sortable: true,dataIndex: 'USER_NAME'},
											{header: "设备状态",  sortable: true,dataIndex: 'DEVICE_STATE'},
											{header: "归属设备组",  sortable: true,dataIndex: 'GROUPNAME'},
											{header: "备注",  sortable: true,dataIndex: 'REMARK'},
											{header: "手机号码",  sortable: true,dataIndex: 'PHONENUMBER'}
										],
									 }]
						},{
							title: "任务周期",
							xtype: "fieldset",
							collapsible: true,
							items: [
							new Ext.Panel({
								id:"LAYOUT_CARD_ID",
								header:false,
								layout:'card',
								activeItem: 0,
								bodyStyle: 'padding:15px',
								defaults: {
								   border:false
								},
								tbar: [
									{
										text: '即时任务',
										handler:function(btn){
											btn.ownerCt.ownerCt.layout.setActiveItem("jsrw");
										}
									},
									'->',
									{
										text: '周期任务',
										handler:function(btn){
											btn.ownerCt.ownerCt.layout.setActiveItem("zqrw");
										}
									}
								],
								items: [{
									id: 'jsrw',
									xtype:"form",
									layout:'column',
									items:[{
											xtype:'panel',
											layout:'form',
											columnWidth:1,
											border:false,
											collapsible: false,
											header:false,
											defaults: {
												labelSeparator: "：",
												labelWidth: 65,
												width: 175
											},
											defaultType: "datetimefield",
											items:[{
														fieldLabel:'开始时间',
														id:"fact_start_time"
												},{
														fieldLabel:'结束时间',
														id:"fact_end_time"
												}]
										}]
								},{
									id: 'zqrw',
									xtype:"form",
									layout:'column',
									items:[{
											xtype:'panel',
											layout:'form',
											columnWidth:1/2,
											border:false,
											collapsible: false,
											header:false,
											defaults: {
												labelWidth: 50,
												width: 80
											},
											defaultType: "datefield",
											items:[{
														fieldLabel:'<input type="radio" name="zqrw_radio" value="1"  onclick ="changerDisabled(\'1\')"/> 每周(星期)',
														xtype:'combo',
														valueField : 'value', 
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['星期一','1'],
														['星期二','2'],
														['星期三','3'],
														['星期四','4'],
														['星期五','5'],
														['星期六','6'],
														['星期日','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														
														id:"cycmz"
												},{
														fieldLabel:'<input type="radio" name="zqrw_radio" value="2"  onclick ="changerDisabled(\'2\')" /> 每月(日期)',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														id:'cycmy'
												},{
														fieldLabel:'派发时间',
														xtype:"textfield",
														disabled:true,
														value:'00:00',
														id:'cycpfsj'
												},{
														fieldLabel:'任务时长(天)',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'7',
														id:'cycrwsc'
												}]
										},{
											xtype:'panel',
											layout:'form',
											columnWidth:1/2,
											border:false,
											collapsible: false,
											header:false,
											defaults: {
												labelSeparator: ":",
												labelWidth: 25,
												width: 80
											},
											defaultType: "datefield",
											items:[{
														fieldLabel:'<input type="radio" name="zqrw_radio" value="3" onclick ="changerDisabled(\'3\')" />每两月(月份)',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														id:'cycrm2z'
												},{
														fieldLabel:'<input type="radio" name="zqrw_radio" value="4"  onclick ="changerDisabled(\'4\')" />每季度(月份)',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														id:'cycrmjd'
												},{
														fieldLabel:'<input type="radio" name="zqrw_radio" value="5"  onclick ="changerDisabled(\'5\')"/>每半年(月份)',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														id:'cycrmbn'
												},{
														fieldLabel:'日期选择',
														xtype:'combo',
														valueField : 'value', 
														disabled:true,
														store: new Ext.data.SimpleStore({
														fields: ['text', 'value'],
														data : [
														['1','1'],
														['2','2'],
														['3','3'],
														['4','4'],
														['5','5'],
														['6','6'],
														['7','7']
														]
														}),
														displayField:'text',
														mode: "local",
														value:'1',
														id:'cycrqxz'
												}]
										}]
								}]
							})
								
								]
						}],
					  region: 'center'
					},
					{
					   xtype:'panel',
					   width:350,
					   header:false,
					   region: 'east',
					   items:[
						   {
							title: "关联工程",
							xtype: "fieldset",
							bodyPadding: 5,
							collapsible: true,
							layout:"column",
							defaultType: "textfield",
							items: [
							{
										  xtype:'grid',
										  frame:false,
										  viewConfig: {
											     fit:true,
												 forceFit:true,
													  templates:{
														cell : new Ext.Template(
															'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
															'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">{value}</div></div>',
															'</td>'
															)
														
													},
													selectedRowClass:"x-grid3-row-data-selected"
												   },
										  store : new Ext.data.ArrayStore({
											    autoLoad:true,
														fields : [
														'CUID'
														, 'NAME'],
														data : [
															
														]
												}),
										 height:70,
										 hideHeaders :true,
										 id:"RIGHT_GRID_DATA",
										 border:true,
										 columnWidth:4/5,
										 columns: [
											{header: "资源编号",  sortable: true, dataIndex: 'CUID',hidden:true},
											{header: "资源名称",  sortable: true,dataIndex: 'NAME'}
										],
									 },{
											xtype:'panel',
											layout:'form',
											columnWidth:1/5,
											border:false,
											collapsible: false,
											style:"margin-left:10px;",
											header:false,
											defaultType: "button",
											items:[{
														text:'选择',
														menu:[{
															text:"杆路系统",
															handler:function(btn){
																btn.ownerCt.ownerCt.setText("杆路系统");
																 GridViewAction.getGridMeta({
																 boName:"TaskPolewaySystemBO",
																cfgParams:{
																	templateId:"IRMS.RMS.WIRE_SYSTEM"
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
																																	custCode:Ext.encode(fields),
																																	boName:"TaskPolewaySystemBO",
																																	 cfgParams:{
																																			templateId:"IRMS.RMS.WIRE_SYSTEM"
																																		 }
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
																	if(topData[index].dataIndex == "OBJECTID" 
																	|| topData[index].dataIndex == "CUID"
																	|| topData[index].dataIndex == "RELATED_SPACE_CUID"
																	){
																		topData[index].hidden=true;
																	};
																};
																var topStoreConfig = getStore(topData,"top");
																new TaskWireWindow({typeTitle:"杆路系统",purpose:"杆路",topData:topData,topStoreConfig:topStoreConfig}).show();
															 });
																
															}
														},{
															text:"管道系统",
															handler:function(btn){
																btn.ownerCt.ownerCt.setText("管道系统");
																GridViewAction.getGridMeta({
																 boName:"TaskDuctSystemBO",
																 cfgParams:{
																	templateId:"IRMS.RMS.WIRE_SYSTEM"
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
																																	boName:"TaskDuctSystemBO",
																																	custCode:Ext.encode(fields),
																																	 cfgParams:{
																																			templateId:"IRMS.RMS.WIRE_SYSTEM"
																																		 }
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
																	if(topData[index].dataIndex == "OBJECTID" || topData[index].dataIndex == "CUID"
																		|| topData[index].dataIndex == "RELATED_SPACE_CUID"){
																		topData[index].hidden=true;
																	};
																};
																var topStoreConfig = getStore(topData,"top");
																new TaskWireWindow({typeTitle:"管道系统",purpose:"管道",topData:topData,topStoreConfig:topStoreConfig}).show();
															 });
															}
														},{
															text:"标石系统",
															handler:function(btn){
																btn.ownerCt.ownerCt.setText("标石系统");
																GridViewAction.getGridMeta({
																 boName:"TaskStonewaySystemBO",
																 cfgParams:{
																	templateId:"IRMS.RMS.WIRE_SYSTEM"
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
																																	boName:"TaskStonewaySystemBO",
																																	custCode:Ext.encode(fields),
																																	 cfgParams:{
																																			templateId:"IRMS.RMS.WIRE_SYSTEM"
																																		 }
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
																	if(topData[index].dataIndex == "OBJECTID" || topData[index].dataIndex == "CUID"
																		|| topData[index].dataIndex == "RELATED_SPACE_CUID"){
																		topData[index].hidden=true;
																	};
																};
																var topStoreConfig = getStore(topData,"top");
																new TaskWireWindow({typeTitle:"标石系统",purpose:"标石",topData:topData,topStoreConfig:topStoreConfig}).show();
															 });
															}
														}],
														height:30
												},{
														style:"margin-top:10px;",
														text:'删除',
														handler:function(btn){
															 var girdData = [];
																var selectionDatas = Ext.getCmp("RIGHT_GRID_DATA").getSelectionModel().getSelections();
																 if(selectionDatas.length > 0){
																	 var cuidStr = "";
																	 for(var index=0;index<selectionDatas.length;index++){
																			var selectData = selectionDatas[index];
																			cuidStr = cuidStr+selectData.data.CUID;
																	}
																	var storeData = Ext.getCmp("RIGHT_GRID_DATA").getStore();
																	var totalCount =  Ext.getCmp("RIGHT_GRID_DATA").getStore().getTotalCount();
																	for(var index = 0;index < totalCount;index++ ){
																		var rec = storeData.getAt(index);
																		if(-1 == cuidStr.lastIndexOf(rec.data.CUID)){
																				girdData.push([
																				rec.data.CUID,
																				rec.data.NAME
																				]);		
																		
																		}
																}
																storeData.removeAll();	
																storeData.loadData(girdData);
														}
														},
														height:30
												}]
										}]
						}, {
							title: "必到点列表",
							xtype: "fieldset",
							bodyPadding: 5,
							collapsible: true,
							items:[{
											xtype:'panel',
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
														fieldLabel:'资源名称',
														name:"LABEL_CN"
												}]
										},{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "textfield",
											items:[{
													fieldLabel:'所属区域',
													name:"DISTRICT"
												}]
										},{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "textfield",
											items:[{
														fieldLabel:'设备名称',
														name:"LABEL_CN"
												}]
										}]
										},{
										  xtype:'grid',
										  frame:true,
										  height:250,
										  tbar:['->',{
											   text:"查询",
											   handler:function(btn){
												   
											   }
											  }],
										  store : new Ext.data.ArrayStore({
											    autoLoad:true,
														fields : [
														'CUID',
														'RESOUCE_TYPE'
														, 'LABEL_CN'
														, 'DISTRICT'
														, 'USER_NAME'],
														data : [
															
														]
												}),
										 viewConfig: {
										    fit:true,
											forceFit: true
										   },
										 id:"RESOURCE_POINTS_ID", 
										 columns: [
										     new Ext.grid.CheckboxSelectionModel(),       
											{header: "编号",  sortable: true, dataIndex: 'CUID'},
											{header: "资源类型",  sortable: true, dataIndex: 'RESOUCE_TYPE'},
											{header: "资源名称",  sortable: true,dataIndex: 'LABEL_CN'},
											{header: "所属区域",  sortable: true,dataIndex: 'DISTRICT'},
											{header: "手机用户",  sortable: true,dataIndex: 'USER_NAME'}
										],
									 },{
											xtype:'panel',
											layout:'column',
											border:false,
											style:"margin-top:10px;",
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
														fieldLabel:'巡线长度',
														name:"xunxianchangdu",
														id:"xunxianchangdu_id",
														disabled:true
														
												}]
										},{
											xtype:'panel',
											layout:'form',
											border:false,
											collapsible: false,
											header:false,
											defaultType: "button",
											items:[{
													text:'获取巡线点',
													handler:function(btn){
														var data = [];
															var totalCount2 =  Ext.getCmp("RIGHT_GRID_DATA").getStore().getTotalCount();
															for(var index = 0;index < totalCount2;index++ ){
																	data.push({
																		CUID:Ext.getCmp("RIGHT_GRID_DATA").getStore().getAt(index).data.CUID,
																		LABEL_CN:Ext.getCmp("RIGHT_GRID_DATA").getStore().getAt(index).data.NAME
																	});							
															};
														TaskAction.getMustPoints(Ext.encode(data),function(res){
															Ext.getCmp("xunxianchangdu_id").setValue(res.MSG);
															if(res.DATA.length > 0){
																var dataPoints = [];
																for(var index =0;index<res.DATA.length;index++){
																	dataPoints.push([
																	res.DATA[index].CUID,
																	res.DATA[index].RESOUCE_TYPE,
																	res.DATA[index].LABEL_CN,
																	res.DATA[index].DISTRICT,
																	res.DATA[index].USER_NAME
																	]);
																}
																Ext.getCmp("RESOURCE_POINTS_ID").loadData(dataPoints);
															}
														});
													}
												}]
										}]
										}]
						}
						   
					   ]
					}
			];
		TaskAddPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		TaskAddPanel.superclass.initComponent.call(this);
	},
	buttons:[{
		text:"保存",
		handler:function(btn){
			var FREQUENCE_TYPE=0;
			var arrayObjRadio=document.getElementsByName("zqrw_radio");
				for(var pos=0;pos<=arrayObjRadio.length-1;pos++){
					if(arrayObjRadio[pos].checked==true){
						FREQUENCE_TYPE = arrayObjRadio[pos].value;
					}
				}
			
			var data={
				IS_CYCLE:Ext.getCmp("LAYOUT_CARD_ID").layout.activeItem.id == 'jsrw'?0:1,
				TASK_NAME:Ext.getCmp("TASK_NAME").getValue(),
				REMARK:Ext.getCmp("REMARK").getValue(),
				TASK_TYPE:Ext.getCmp("TASK_TYPE").getValue(),
				PDADATAS:[],
				RES_PROJECTDATAS:[],
				FACTTIME:{
					fact_start_time:Ext.getCmp("fact_start_time").getValue(),
					fact_end_time:Ext.getCmp("fact_end_time").getValue()
				},
				FREQUENCE_TYPE:FREQUENCE_TYPE,
				TASK_TIME:Ext.getCmp("cycpfsj").getValue(),
				DURATION:Ext.getCmp("cycrwsc").getValue(),
				CYCLETIME:{
					cycmz:Ext.getCmp("cycmz").getValue(),
					cycmy:Ext.getCmp("cycmy").getValue(),
					cycpfsj:Ext.getCmp("cycpfsj").getValue(),
					cycrwsc:Ext.getCmp("cycrwsc").getValue(),
					cycrm2z:Ext.getCmp("cycrm2z").getValue(),
					cycrmjd:Ext.getCmp("cycrmjd").getValue(),
					cycrmbn:Ext.getCmp("cycrmbn").getValue(),
					cycrqxz:Ext.getCmp("cycrqxz").getValue()
				},
				POINTS:[]
			};
			var totalCount =  Ext.getCmp("PDA_GRID").getStore().getTotalCount();
			for(var index = 0;index < totalCount;index++ ){
						var rec =  Ext.getCmp("PDA_GRID").getStore().getAt(index);
					data.PDADATAS.push(rec.data);							
			};
			var totalCount2 =  Ext.getCmp("RIGHT_GRID_DATA").getStore().getTotalCount();
			for(var index = 0;index < totalCount2;index++ ){
					data.RES_PROJECTDATAS.push(Ext.getCmp("RIGHT_GRID_DATA").getStore().getAt(index).data);							
			};
			var totalCount3 =  Ext.getCmp("RESOURCE_POINTS_ID").getSelectionModel().getSelections();
			for(var index = 0;index < totalCount3;index++ ){
					data.POINTS.push(totalCount3[index].data);							
			};
			TaskAction.saveTask(Ext.encode({data}),function(res){
				    
					btn.ownerCt.ownerCt.close();
			});
			
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