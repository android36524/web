var HitchTaskQueryPanel = Ext.extend(Ext.Window ,{
	title:"故障查询",
	width:800,
	modal:true,
	maximizable :true,
	constructor : function(configer) {
		 configer.layout="fit";
		 configer.items=[
				   {
					 xtype:'panel',
				     bodyStyle :'overflow-x:hidden;overflow-y:scroll',
					 header:false,
					  items:[{
							title: "查询条件",
							xtype: "fieldset",
							bodyPadding: 5,
							collapsible: true,
							items: [new HitchTaskQueryPropertyPanel({
								title:'基本属性',
								isAllEidte:true,
								id:"BASE_PANEL_PROPERTY_GRID",
								name:"JBSX",
								customEditors:{
									"光缆名称":new Ext.grid.GridEditor(new HitchTaskWireWindow({
											pid:"WIRE_GRIDEDITOR"
										}),{id:"WIRE_GRIDEDITOR"}),
									"光交设备": new Ext.grid.GridEditor(new Ext.form.ComboBox({
											fieldLabel:'是否归档',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											]
											}),
											displayField:'text',
											mode: "local",
											value:'9',	
											listeners :{
												select:function( combo, record,  index ){
													HitchTaskAction.initWireSegEnum(Ext.encode({
														wireSystemCuid:Ext.getCmp("BASE_PANEL_PROPERTY_GRID").name,
														pointDeviceCuid:record.data.value
													}),function(res){
														Ext.getCmp("CLFX").getStore().loadData(res._DATA);
													})
														Ext.getCmp("ISSAVE_GRIDEITOR").setValue(record.data.text);
														Ext.getCmp("ISSAVE_GRIDEITOR").completeEdit();
														combo.name=record.data.value;
														Ext.getCmp("ISSAVE_GRIDEITOR").allowBlur = true;
												} 
											},
											id:"ISSAVE"
										}),{id:"ISSAVE_GRIDEITOR"}),
									"测量方向": new Ext.grid.GridEditor(new Ext.form.ComboBox({
											fieldLabel:'测量方向',
											xtype:'combo',
											valueField : 'value', 
											store: new Ext.data.SimpleStore({
											fields: ['text', 'value'],
											data : [
											]
											}),
											listeners :{
												select:function( combo, record,  index ){
														combo.name=record.data.value;
													    Ext.getCmp("CLFX_GRIDEITOR").setValue(record.data.text);
														Ext.getCmp("CLFX_GRIDEITOR").completeEdit();
														Ext.getCmp("CLFX_GRIDEITOR").allowBlur = true;
												} 
											},
											displayField:'text',
											mode: "local",
											value:'9',	
											id:"CLFX"
										}),{
											id:"CLFX_GRIDEITOR"
										})  
								},
							source: {
								"光缆名称": "",
								"光交设备": "",
								"测量方向": "",
								"测量距离(M)": 100.0,
								"误差(M)": 15.0
							}
					  })]},{
							title: "断点信息",
							xtype: "fieldset",
							collapsible: true,
							items: [new HitchTaskQueryPropertyPanel({
								title:'系统属性',
							source: {
								"设备1名称": "",
								"距离断点位置": 0,
								"设备2名称": "",
								"距离断点位置":0,
								"经度":0,
								"维度": 0
							}
					  }),new HitchTaskQueryPropertyPanel({
						    title:'业务属性',
							source: {
								"所属线路系统": "",
								"所属线路段": "",
								"线路段起点": "",
								"线路段终点": ""
							}
					  })
								]
						},{
							title: "预留信息",
							xtype: "fieldset",
							collapsible: true,
							items: [new HitchTaskQueryPropertyPanel({
							title:"系统属性",
							source: {
								"预留1名称": "",
								"预留1长度": 0,
								"距离断点长度": 0,
								"预留2名称": "",
								"预留2长度": 0,
								"距离断点长度":0
							}
					  })
								]
						}]
					}
					
			];
		HitchTaskQueryPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		HitchTaskQueryPanel.superclass.initComponent.call(this);
	},
	buttons:[{
		text:"查询断点",
		handler:function(btn){
			var basicPropertyGrid = btn.ownerCt.ownerCt.items.get(0).items.get(0).items.get(0);
			console.dir(basicPropertyGrid.getSource());
			console.log(Ext.getCmp("BASE_PANEL_PROPERTY_GRID").name);
			var duandianPropertyGrid = btn.ownerCt.ownerCt.items.get(0).items.get(1).items.get(0);
			var bussPropertyGrid = btn.ownerCt.ownerCt.items.get(0).items.get(1).items.get(1);
			var sysPropertyGrid = btn.ownerCt.ownerCt.items.get(0).items.get(2).items.get(0);
			var paramData = {
				relatedWireSystemCuid:Ext.getCmp("BASE_PANEL_PROPERTY_GRID").name,
				relatedWireSegCuid:Ext.getCmp("ISSAVE").getName(),
				direction:Ext.getCmp("CLFX").getName(),
				cljl:basicPropertyGrid.getSource()['测量距离(M)']
			};
			HitchTaskAction.queryInterrupt(Ext.encode(paramData),function(res){
				if(res.SUCCESS == false){
					 Ext.MessageBox.show({
						title: '提示',
						msg: res.MSG,
						buttons: Ext.MessageBox.OK,
						icon: Ext.MessageBox.ERROR
					});
				}else{
					duandianPropertyGrid.setSource({
									"设备1名称": "",
									"距离断点位置": res.DATA.ORIG_POINT_POSITION,
									"设备2名称": "",
									"距离断点位置":res.DATA.ORIG_POINT_POSITION,
									"经度":res.DATA.LONGITUDE,
									"维度": res.DATA.LATITUDE
					});
					
					bussPropertyGrid.setSource({
								"所属线路系统": "",
								"所属线路段": "",
								"线路段起点": res.DATA.ROTE_ORIG_POINT_LONG,
								"线路段终点": res.DATA.ROTE_DEST_POINT_LONG
					});
					sysPropertyGrid.setSource({
								"预留1名称": "",
								"预留1长度": 0,
								"距离断点长度": res.DATA.ORIG_POINT_LONGTH,
								"预留2名称": "",
								"预留2长度": 0,
								"距离断点长度":res.DATA.DEST_POINT_LONGTH
					});
				}
					
			});
			/*basicPropertyGrid.setSource({
				"(name)": "My Object",
				"Created": new Date(Date.parse('10/15/2006')),  // 日期类型 date type
				"Available": false,  // 布尔类型 boolean type
				"Version": .01,      // 小数类型 decimal type
				"Description": "一个测试对象  A test object"
			});
			duandianPropertyGrid.setSource({
				"(name)": "My Object",
				"Created": new Date(Date.parse('10/15/2006')),  // 日期类型 date type
				"Available": false,  // 布尔类型 boolean type
				"Version": .01,      // 小数类型 decimal type
				"Description": "一个测试对象  A test object"
			});
			bussPropertyGrid.setSource({
				"(name)": "My Object",
				"Created": new Date(Date.parse('10/15/2006')),  // 日期类型 date type
				"Available": false,  // 布尔类型 boolean type
				"Version": .01,      // 小数类型 decimal type
				"Description": "一个测试对象  A test object"
			});
			sysPropertyGrid.setSource({
				"(name)": "My Object",
				"Created": new Date(Date.parse('10/15/2006')),  // 日期类型 date type
				"Available": false,  // 布尔类型 boolean type
				"Version": .01,      // 小数类型 decimal type
				"Description": "一个测试对象  A test object"
			});*/
		}
	},{
		text:"确认",
		handler:function(btn){
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