var TaskExportPanel = Ext.extend(Ext.Window ,{
	title:"Excel表格导出",
	width:300,
	modal:true,
	baseData:{},
	style:"padding-top:10px;",
	constructor : function(configer) {
		 configer.layout="form";
		 var topData = configer.data;
		 var boxLabels = [];
		 this.dataExport = configer.dataExport;
		 this.baseData = configer.baseData;
		 this.fileName = configer.fileName;
		 for(var i =0;i<topData.length;i++ ){
			 boxLabels.push({boxLabel:topData[i].header, name:topData[i].dataIndex});
		 }
		 configer.items=[
				    {
					   xtype:'textfield',
					   value:'巡线任务管理',
					   disabled:true,
					   fieldLabel:"Excel页名"
					   
				   }, {
					    xtype: 'checkboxgroup',     
						fieldLabel: '选择导出列',     
						border:true,
						itemCls: 'x-check-group-alt',     
						columns: configer.column,     
						autoHeight :true,
						items:boxLabels     
					   
				   }
				   ];
		TaskExportPanel.superclass.constructor.call(this, configer);
	},
	initComponent:function() {
		TaskExportPanel.superclass.initComponent.call(this);
	},
	buttons:['->',{
		text:"确定",
		handler:function(btn){
			var checkColumns = btn.ownerCt.ownerCt.items.get(1).getValue();
			var names ="";
			var dataIndexs = "";
			for(var i=0;i<checkColumns.length;i++){
				var checkColumn = checkColumns[i];
				names = names+","+checkColumn.name;
				dataIndexs =dataIndexs+","+ checkColumn.boxLabel;
				
			};
			var dataExport = [];
			for(var ii =0;ii<btn.ownerCt.ownerCt.dataExport.length;ii++){
				dataExport.push(btn.ownerCt.ownerCt.dataExport[ii].data)
			};
			GridViewAction.exportGridData({
					exportBoName:"TaskExportBO",
					boName:"TaskBO",
					custType:btn.ownerCt.ownerCt.baseData[0].custType,
					custCode:btn.ownerCt.ownerCt.baseData[0].custCode,
					cfgParams:{
						keys:names,
						dataExport:Ext.encode(dataExport),
						fileName:btn.ownerCt.ownerCt.fileName,
						sheetName:"巡检任务管理",
						values:dataIndexs
					},
					queryParams:{}
					}
				, [], [], function(results){
					window.open (encodeURI(encodeURI(ctx+results.files[0].filePath)),results.files[0].fileName,'height=100,width=400,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no') 
					btn.ownerCt.ownerCt.close();
			});
		}
	},{
		text:"取消",
		handler:function(btn){
			btn.ownerCt.ownerCt.close();
		}
	}],
	buttonAlign:"right",
	height:200
})