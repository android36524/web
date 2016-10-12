var ProjectAddTaskPanel = Ext.extend(Ext.Window ,{
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
								fieldLabel: "任务名称",
								id:"TASK_NAME"
							}, {
								fieldLabel: "工程名称",
								id:"PROJECT_NAME"
							},{
								fieldLabel: "备注",
								xtype:"textarea",
								id:"REMARK"
							}]
						},{
							title: "任务时间设置",
							xtype: "fieldset",
							collapsible: true,
							items: [
							new Ext.Panel({
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
														id:"START_TIME"
												},{
														fieldLabel:'结束时间',
														id:"END_TIME"
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
												labelSeparator: ":",
												labelWidth: 25,
												width: 80
											},
											defaultType: "datefield",
											items:[{
														fieldLabel:'每周(星期)'
												},{
														fieldLabel:'每月(日期)'
												},{
														fieldLabel:'派发时间'
												},{
														fieldLabel:'任务时长(天)'
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
														fieldLabel:'每两月(月份)'
												},{
														fieldLabel:'每季度(月份)'
												},{
														fieldLabel:'每半年(月份)'
												},{
														fieldLabel:'日期选择(月份)'
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
					   header:false,
					   width:350,
					   girdData:[],
					   region: 'east',
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
					   items:[{
							title: "指派PDA列表",
							xtype: "fieldset",
							height:300,
							  width:350,
							collapsible: true,
						
							items: [{
										  xtype:'grid',
										  frame:true,
										  width:350,
										  height:235,
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
										
										 columns: [
											{header: "PDA编码",  sortable: true, dataIndex: 'DEVICE_CODE'},
											{header: "用户",  sortable: true,dataIndex: 'USER_NAME'},
											{header: "设备状态",  sortable: true,dataIndex: 'DEVICE_STATE'},
											{header: "归属设备组",  sortable: true,dataIndex: 'GROUPNAME'},
											{header: "备注",  sortable: true,dataIndex: 'REMARK'},
											{header: "手机号码",  sortable: true,dataIndex: 'PHONENUMBER'}
										],
									 }]
						}
						   
					   ]
					}
			];
		ProjectAddTaskPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		ProjectAddTaskPanel.superclass.initComponent.call(this);
	},
	buttons:[{
		text:"保存任务",
		handler:function(btn){
			var paramObj = {};
			paramObj.TASK_NAME = Ext.getCmp("TASK_NAME").getValue();
			paramObj.PROJECT_NAME = Ext.getCmp("PROJECT_NAME").getValue();
			paramObj.REMARK = Ext.getCmp("REMARK").getValue();
			paramObj.START_TIME = Ext.getCmp("START_TIME").el.dom.value;
			paramObj.END_TIME = Ext.getCmp("END_TIME").el.dom.value;
			var pdaData = [];
			var store = Ext.getCmp("PDA_GRID").getStore();
			var count = store.getCount();
			for (var i = 0; i < count; i++) {
				var record = store.getAt(i);
				pdaData.push(record.data);
			}
			paramObj.pdaData = Ext.encode(pdaData);
			ProjectTaskAction.addProjectTask(Ext.encode(paramObj),function(resault){
				Ext.getCmp("PROJECT_TASK_GRID").getStore().load();
			});
			btn.ownerCt.ownerCt.close();
		}
	},{
		text:"取消",
		handler:function(btn){
			btn.ownerCt.ownerCt.close();
		}
	}],
	buttonAlign:"right",
	height:360
	
	
});