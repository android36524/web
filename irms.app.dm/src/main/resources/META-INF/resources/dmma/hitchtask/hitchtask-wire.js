var HitchTaskWireWindow = Ext.extend(Ext.form.TriggerField,  {
    selfWindow:"",
    initComponent : function(){
		 HitchTaskWireWindow.superclass.initComponent.call(this);
  },
	onDestroy : function(){
		Ext.destroy(this.selfWindow);
        HitchTaskWireWindow.onDestroy.call(this);
    },

    onTriggerClick : function(){
		var me =this;
	 GridViewAction.getGridMeta({
	 boName:"SystemGridTemplateProxyBO",
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
													 					boName:"SystemGridTemplateProxyBO",
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
	for(var index=0;index<topData.length;index++){
		topData[index].renderer=function(value, cellmeta, record, rowIndex, columnIndex, store){
				if(value instanceof Object){
					return value.LABEL_CN;
				}else{
						return value;
			}
		}
	}
	var topStoreConfig = getStore(topData,"top");
	
	   Ext.getCmp(me.pid).allowBlur = false;
		me.selfWindow = new Ext.Window({
            title: '光缆管理',
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
							border:false,
						},
						id:"PROJECT_QUERY_DATA",
						items:[
							{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'系统名称',
											xtype:'textfield',
											width:100,
											name:"LABEL_CN"
									},{
											fieldLabel:'工程名称',
											xtype:'textfield',
											width:100,
											name:"PROJECT"
									}]
							}
								  ,{
								xtype:'panel',
								layout:'form',
								items:[{
											fieldLabel:'所属区域',
											xtype : 'asyncombox',
											width:100,
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
											width:100,
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
											width:100,
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
											width:100,
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
											width:100,
											name:"BUILDER" 
									},{
											fieldLabel:'段用途',
											xtype:'combo',
											valueField : 'value', 
											width:100,
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
															templateId:"IRMS.RMS.WIRE_SYSTEM",
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
													store: topStoreConfig,
													displayInfo: true,
													displayMsg: '当前 第{0}-{1} 条 / 共 {2} 条',
													emptyMsg: "没有数据"
												}),
										 items:[{
												  xtype:'grid',
												  frame:true,
												  store : topStoreConfig,
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
												 columns:topData,
											 }],
										 layout:'fit',
										 header:false
									}]
								},
								anchor: '100% 80%'
							}]
			}],
			buttons:[{
				text:"选中",
				handler:function(btn,event){
					var selectionDatas = Ext.getCmp("DETAIL_GRID_DATA").getSelectionModel().getSelections();
					 if(selectionDatas.length == 1){
							me.setValue(selectionDatas[0].data.LABEL_CN);
							Ext.getCmp("BASE_PANEL_PROPERTY_GRID").name=selectionDatas[0].data.CUID;
							HitchTaskAction.getFirstWireSegInfo(selectionDatas[0].data.CUID,function(res){
								Ext.getCmp("ISSAVE").getStore().loadData(res._DATA);
							});
					};
			    	me.selfWindow.close();
					Ext.getCmp(me.pid).completeEdit();
					Ext.getCmp(me.pid).allowBlur = true;
				}
			},{
				text:"关闭",
				handler:function(btn,event){
			    	me.selfWindow.close();
					Ext.getCmp(me.pid).completeEdit();
					Ext.getCmp(me.pid).allowBlur = true;
				}
			}]
			});
			me.selfWindow.show();
	});
	
		
    }
});
Ext.reg('HitchTaskWireWindow', HitchTaskWireWindow);