$importjs(ctx + "/dwr/interface/WireSegBatchAction.js");
Ext.ns("Frame.grid.plugins.event");
var oldCellData=[];
var oldHeaderData=[];
var oldHeaderSureCancel = false;
var oldCellValueData=[];
var oldHeaderSureCancel = false;
var oldCellSureCancel = false;
var filterDataIndex = "LABEL_CN,ORIG_POINT_CUID,DEST_POINT_CUID";
var contentMenuFactory ={
	createCustomRenderers:{
		enumcombox:function(id,text,type,code){
			var render = function(v){
					if("undefined" == (typeof Ext.getCmp(id+"_"+type+"_id").getEl())){
						return v;
					}
					return Ext.getCmp(id+"_"+type+"_id").getEl().dom.value;
				};
			return 	render;
		},
		dmcombox:function(id,text,type,templateId){
			var render = function(v){
					if("undefined" == (typeof Ext.getCmp(id+"_"+type+"_id").getEl())){
						return v;
					}
					return Ext.getCmp(id+"_"+type+"_id").getEl().dom.value;
				};
			return 	render;
		},
		text:function(id,text,type,templateId){
			var render = function(v){
					return v;
				};
			return 	render;
		},
		longs:function(id,text,type,templateId){
			var render = function(v){
					return v;
				};
			return 	render;
		},
		doubles:function(id,text,type,templateId){
			var render = function(v){
					return v;
				};
			return 	render;
		},
		date:function(id,text,type,templateId){
			var render = function(v){
						if(typeof v == 'string' && !Ext.isEmpty(v)){
							var dt = new Date(v);
							return dt.format('Y-m-d H:i:s');
						} else if(typeof v == 'number'){
							var dt = new Date(parseInt(v));
							return dt.format('Y-m-d H:i:s');
						} else if(Ext.isDate(v)){
							return v.dateFormat('Y-m-d H:i:s');
						 }
						return v;
					};
			return 	render;
		}
		
	},
	createCustomEditors:{
		enumcombox:function(id,text,type,code){
			var comp = Ext.create({
					xtype : 'enumcombox',
					fieldLabel :text,
					code : code,
					getParentZIndex:function(){
						return 99999999;
					},
					name : id,
					id:id+"_"+type+"_id"
				});
			comp.comboxCfg = {
					boName : 'EnumTemplateComboxBO',
					cfgParams : {
						code : code
					}
				};
			comp.queryCfg = {
					type : "string",
					relation : "in"
				};
			return new Ext.grid.GridEditor(comp);
		},
		dmcombox:function(id,text,type,templateId){
			 return new Ext.grid.GridEditor(Ext.create({
					xtype : 'dmcombox',
					name : id,
					id:id+"_"+type+"_id",
					fieldLabel :text,
					templateId :templateId
				}));
		},
		text:function(id,text,type){
			return new Ext.grid.GridEditor(new Ext.form.TextField({
							name : id,
							id:id+"_"+type+"_id",
							fieldLabel :text
						})
					);
		},
		longs:function(id,text,type){
			return new Ext.grid.GridEditor(Ext.create({
					xtype: 'numberfield',
					minValue : Number.NEGATIVE_INFINITY,
					maxValue : Number.MAX_VALUE,
					decimalPrecision: 0 ,
					name : id,
					id:id+"_"+type+"_id",
					fieldLabel :text
					}));
		},
		doubles:function(id,text,type){
			return new Ext.grid.GridEditor(Ext.create({
				xtype: 'numberfield',
				allowBlank: true,
				minValue:  Number.NEGATIVE_INFINITY,
				maxValue:  Number.MAX_VALUE,
				decimalPrecision: 6,
				name : id,
				id:id+"_"+type+"_id",
				fieldLabel :text			
				}))
		},
		date:function(id,text,type){
		      return new Ext.grid.GridEditor( Ext.create({
								xtype : 'datetimefield',
								name : id,
								id:id+"_"+type+"_id",
								fieldLabel :text,
								format : 'Y-m-d H:i:s'
							})
				);
		}
	},
	data : {
		
		enumcombox : [ {
			id : "PROJECT_STATE",
			text : "工程状态",
			code : "DMProjectState"
		}, {
			id : "LAY_TYPE",
			text : "敷设方式",
			code : "DMLayType"
		}, {
			id : "FIBER_TYPE",
			text : "纤芯类型",
			code : "DMFiberType"
		},
		{
			id : "OWNERSHIP",
			text : "产权",
			code : "DMOwnerShip"
		},{
			id : "PURPOSE",
			text : "用途",
			code : "DMPurpose"
		},{
			id : "MAINT_MODE",
			text : "维护方式",
			code : "DMMaintMode"
		},{
			id : "SPECIAL_PURPOSE",
			text : "专线用途",
			code : "DMSpecialPurpose"
		},{
			id : "OLEVEL",
			text : "重要性",
			code : "DMWireSegOlevel"
		},{
			id : "NETWORK_TYPE",
			text : "网络特性",
			code : "DMNetworkType"
		},{
			id : "CHECK_MODE",
			text : "巡检方式",
			code : "DMCheckMode"
		},{
			id : "GEO_ENV",
			text : "地理环境",
			code : "DMGeoEnv"
		},{
			id : "MAINT_STATE",
			text : "维护作业状态",
			code : "MaintState"
		},{
			id : "DATA_STATE",
			text : "数据状态",
			code : "DataStateEnum"
		}],
		dmcombox : [ {
			id : "RELATED_PROJECT_CUID",
			text : "所属工程",
			templateId : "service_dict_dm.DM_PROJECT_MANAGEMENT"
		}, {
			id : "RELATED_SYSTEM_CUID",
			text : "所属光缆",
			templateId : "service_dict_dm.DM_WIRE_SYSTEM"
		}, {
			id : "RELATED_MAINT_CUID",
			text : "所属维护作业",
			templateId : "service_dict_dm.DM_MAINT_MANAGEMENT"
		} ],
		longs : [ {
			id : "FIBER_COUNT",
			text : "纤芯个数（芯）"
		}],
		doubles : [ {
			id : "DIA",
			text : "光缆直径（CM）"
		},
		{
			id : "LENGTH",
			text : "总长度（M）"
		},],
		dates : [ {
			id : "FINISH_DATE",
			text : "竣工时间"
		},
		{
			id : "PRODUCE_DATE",
			text : "生产时间"
		},
		{
			id : "BUILD_DATE",
			text : "施工时间"
		},
		{
			id : "FINISH_DATE",
			text : "竣工时间"
		},
		{
			id : "CHECK_DATE",
			text : "检修时间"
		},
		{
			id : "CREATE_TIME",
			text : "录入时间"
		},
		{
			id : "LAST_MODIFY_TIME",
			text : "最后修改时间"
		}],
		text : [ {
			id : "REMARK",
			text : "备注",
		}, {
			id : "STUFF",
			text : "材料",
		},

		{
			id : "VENDOR",
			text : "生产厂商",
		},
		{
			id : "RELATED_PATH_CUID",
			text : "所属路由",
		},
		{
			id : "RELETED_TAMPLETE_NAME",
			text : "模板名称",
		},
		
		{
			id : "WIRE_TYPE",
			text : "光缆类型",
		},
		{
			id : "LABEL_CN",
			text : "名称",
		},
		{
			id : "BUILDER",
			text : "施工单位",
		},
		{
			id : "MAINT_DEP",
			text : "维护单位",
		},
		{
			id : "SERVICER",
			text : "巡检人",
		},
		{
			id : "PHONE_NO",
			text : "联系电话",
		},
		{
			id : "RES_OWNER",
			text : "产权单位",
		},
		{
			id : "USER_NAME",
			text : "使用单位",
		},
		{
			id : "DATA_PROBLEM",
			text : "数据问题描述",
		},
		{
			id : "PRO_NAME",
			text : "工程名称",
		},
		{
			id : "CREATOR",
			text : "录入人",
		}]
	},
	getValues:function(type){
			var me = this;
		var data ={};
		for(var index=0;index<me.data.text.length;index++){
			var text = me.data.text[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		for(var index=0;index<me.data.dates.length;index++){
			var text = me.data.dates[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		for(var index=0;index<me.data.doubles.length;index++){
			var text = me.data.doubles[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		for(var index=0;index<me.data.longs.length;index++){
			var text = me.data.longs[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		for(var index=0;index<me.data.dmcombox.length;index++){
			var text = me.data.dmcombox[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		for(var index=0;index<me.data.enumcombox.length;index++){
			var text = me.data.enumcombox[index];
			eval("data[\""+text.id+"\"]=\""+Ext.getCmp(text.id+"_"+type+"_id").getValue()+"\"");
		}
		return data;
	},
	getCustomRenderers:function(type){
			var me = this;
		var data ={};
		for(var index=0;index<me.data.text.length;index++){
			var text = me.data.text[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.text(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.dates.length;index++){
			var text = me.data.dates[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.date(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.doubles.length;index++){
			var text = me.data.doubles[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.doubles(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.longs.length;index++){
			var text = me.data.longs[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.longs(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.dmcombox.length;index++){
			var text = me.data.dmcombox[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.dmcombox(\""+text.id+"\",\""+text.text+"\",\""+type+"\",\""+text.templateId+"\")");
		}
		for(var index=0;index<me.data.enumcombox.length;index++){
			var text = me.data.enumcombox[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomRenderers.enumcombox(\""+text.id+"\",\""+text.text+"\",\""+type+"\",\""+text.code+"\")");
		}
		return data;
	},
	getCustomEditors:function(type){
			var me = this;
		var data ={};
		for(var index=0;index<me.data.text.length;index++){
			var text = me.data.text[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.text(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.dates.length;index++){
			var text = me.data.dates[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.date(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.doubles.length;index++){
			var text = me.data.doubles[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.doubles(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.longs.length;index++){
			var text = me.data.longs[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.longs(\""+text.id+"\",\""+text.text+"\",\""+type+"\")");
		}
		for(var index=0;index<me.data.dmcombox.length;index++){
			var text = me.data.dmcombox[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.dmcombox(\""+text.id+"\",\""+text.text+"\",\""+type+"\",\""+text.templateId+"\")");
		}
		for(var index=0;index<me.data.enumcombox.length;index++){
			var text = me.data.enumcombox[index];
			eval("data[\""+text.text+"\"]="+"contentMenuFactory.createCustomEditors.enumcombox(\""+text.id+"\",\""+text.text+"\",\""+type+"\",\""+text.code+"\")");
		}
		return data;
	}
}
Frame.grid.plugins.event.WireSystemContentMenuEvent = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.event.WireSystemContentMenuEvent.superclass.constructor.call(this);
		var evts = {};
				evts['rowcontextmenu'] = {
				scope : this,
				fn : this.onContextmenu
			};
			evts['cellcontextmenu'] = {
				scope : this,
				fn : this.onCellContextmenu
			};
			evts['headercontextmenu'] = {
				scope : this,
				fn : this.onHeaderContextmenu
			};
			evts['rowclick'] = {
				scope : this,
				fn : this.onRowclick
			};
		return evts;
	},
	onRowclick : function(grid, rowIndex, e) {
			
		},
	onContextmenu: function(grid, rowIndex, e) {
		
		
	},
	onHeaderContextmenu:function(grid, columnIndex, e) {
			if(Ext.getCmp("cell_right_menu")){
			oldCellSureCancel = true;
			Ext.getCmp("cell_right_menu").hide();
				
		};
		oldHeaderSureCancel = false;
		for(var index=0;index<oldCellData.length;index++){
				document.getElementById(oldCellData[index].key).style="";
				document.getElementById(oldCellData[index].key).innerHTML =oldCellData[index].value;
			}
			oldCellData = [];
			oldCellValueData=[];
			grid.getSelectionModel().clearSelections();
		e.preventDefault();
		var etarget = e;
		var oldInnerHtml = etarget.target.innerHTML;
		var oldSelDatas =  grid.getSelectionModel().getSelections();
		var colName = grid.getColumnModel().getColumnHeader(columnIndex);
		var colIndex = grid.getColumnModel().getDataIndex(columnIndex);
		if(filterDataIndex.lastIndexOf(colIndex) != -1){
			return;
		};
		
	    
		function getCustomRenderers(oldHeaderData,value){
			
			return contentMenuFactory.getCustomRenderers("header");
		}
	
		function getCustomEditors(oldHeaderData,value){
				return contentMenuFactory.getCustomEditors("header");
		}
		 
		grid.getSelectionModel().clearSelections();
		grid.getSelectionModel().selectAll();
		var selDatas = grid.getSelectionModel().getSelections();
		
		var record = grid.getStore().getAt(columnIndex);
		
		etarget.target.innerHTML = "<div id='"+colIndex+"'><font color=\"blue\" >"+oldInnerHtml+"</font></div>";
		oldHeaderData.push({
			key:colIndex,
			value:oldInnerHtml,
			colIndex:colIndex,
			colName:colName
		});
		
		var sourceStrData = function(oldHeaderData){
			var str = "{";
			for(var index=0;index<oldHeaderData.length;index++){
				str=str+"\""+oldHeaderData[index].colName+"\":\""+""+"\",";
			}
			if(oldHeaderData.length >0){
				str = str.substr(0,str.length-1);
			}
			str = str+"}";
			return str;
		}
		function resetColor(id,oldhtml){
			for(var index=0;index<oldHeaderData.length;index++){
				document.getElementById(oldHeaderData[index].key).innerHTML =oldHeaderData[index].value;
			}
			oldHeaderData = [];
		};
		var contextmenu =  {};
		if(Ext.getCmp("header_right_menu")){
			 contextmenu =  Ext.getCmp("header_right_menu");
		}else{
			contextmenu = new Ext.menu.Menu({
				id:"header_right_menu",
				listeners :{
					beforeshow :function(){
						
					},
					beforehide:function(){
						if(oldHeaderSureCancel){
							return true;
						}
						return false;
					},
					hide:function(menu){
					    if(oldHeaderSureCancel){
							resetColor(colIndex,oldInnerHtml);
						}
						oldHeaderSureCancel = false;
					}
				},
                shadow : 'frame',  
                items:[  
					new Ext.grid.PropertyGrid({
								header:false,
								hideHeaders :true,
								customEditors :getCustomEditors(oldHeaderData,""),
								customRenderers:getCustomRenderers(oldHeaderData,""),
								viewConfig: {
										    fit:true,
											selectedRowClass:"x-grid3-row-data-selected",
											templates:{
												cell : new Ext.XTemplate(
													'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
													'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">'+
													'<tpl if="this.isBold(id) == true">',  
														'<font style="font-weight:bold;" color=\"#2828FF\"  >{value}</font>'+
													'</tpl>',
													'<tpl if="this.isBold(id) == false">',  
														'<font  color=\"#2828FF\"  >{value}</font>'+
													'</tpl>',
													'</div></div>',
													'</td>',  {
																compiled: true,
																disableFormats: true,
																isBold: function(name){
																	return name == 'name';
																}
															}
													)
												
											},
											forceFit: true
										   },
								autoHeight:true,
								bbar:[new Ext.form.Label({
									text :selDatas.length+"行可修改"
								}),'->',{
									iconCls :'c_script_save',
									text:"确定",
									handler:function(btn){
										var dataValue=[];
										for(var index =0;index<selDatas.length;index++){
											dataValue.push({
												CUID:selDatas[index].data.CUID
											});
										}
										
										WireSegBatchAction.WireSegUpdate(Ext.encode({
											type:"header",
											data:Ext.encode(oldHeaderData),
											selDatas:Ext.encode(dataValue),
											value:Ext.encode(contentMenuFactory.getValues("header"))
										}),function(btn){
											oldHeaderSureCancel = true;
											resetColor(colIndex,oldInnerHtml);
											grid.getSelectionModel().clearSelections();
											for(var index=0;index<oldSelDatas.length;index++ ){
													grid.getSelectionModel().selectRow(grid.getStore().indexOfId(oldSelDatas[index].id)); 
												}
											contextmenu.hide();
											grid.getStore().load();
											
										});
										
									}
								},{
									text:"取消",
									iconCls :'c_cancel',
									handler:function(btn){
										oldHeaderSureCancel = true;
										resetColor(colIndex,oldInnerHtml);
										grid.getSelectionModel().clearSelections();
										for(var index=0;index<oldSelDatas.length;index++ ){
												grid.getSelectionModel().selectRow(grid.getStore().indexOfId(oldSelDatas[index].id)); 
											}
										contextmenu.hide();
									}
								}],
								width: 300,
								source:Ext.decode( sourceStrData(oldHeaderData)) 
							})
                ]  
            });
		};
		
		contextmenu.items.get(0).setSource( Ext.decode( sourceStrData(oldHeaderData))); 
		if(contextmenu) {
			var x = e.getPageX(), y = e.getPageY();
			contextmenu.showAt([x, y]);
		}
	},
	onCellContextmenu : function(grid, rowIndex,cellIndex, e) {
		if(Ext.getCmp("header_right_menu")){
			oldHeaderSureCancel = true;
			Ext.getCmp("header_right_menu").hide();
			
		};
		oldCellSureCancel = false;
		for(var index=0;index<oldHeaderData.length;index++){
				document.getElementById(oldHeaderData[index].key).innerHTML =oldHeaderData[index].value;
			}
			oldHeaderData = [];
		e.preventDefault();
		var record = grid.getStore().getAt(rowIndex);
	    var dataIndex = grid.getColumnModel().getDataIndex(cellIndex);
		var colName = grid.getColumnModel().getColumnHeader(cellIndex);
        var cellData = record.get(dataIndex);
        if(filterDataIndex.lastIndexOf(dataIndex) != -1){
			return;
		}
		
		var oldInnerHtml = e.target.innerHTML;
		var cellId=dataIndex+"-"+rowIndex+"-"+cellIndex;
		e.target.innerHTML = "<div id='"+cellId+"' style=\"background:#BEBEBE;\"><font color=\"blue\" >"+oldInnerHtml+"</font></div>";
		oldCellData.push({
			key:cellId,
			value:oldInnerHtml
		});
		oldCellValueData.push({
			recordData:record.data,
			CUID:record.data.CUID,
			colName:colName,
			dataIndex:dataIndex,
			rowIndex:rowIndex,
			cellIndex:cellIndex
		});
		   function createEnumCombox(fieldLabel,code,name){
			var comp = Ext.create({
					xtype : 'enumcombox',
					fieldLabel :fieldLabel,
					code : code,
					getParentZIndex:function(){
						return 99999999;
					},
					name : name,
					id:name+"_CELL_ID"
				});
			comp.comboxCfg = {
					boName : 'EnumTemplateComboxBO',
					cfgParams : {
						code : code
					}
				};
			comp.queryCfg = {
					type : "string",
					relation : "in"
				};
			return comp;
		}
	    ;
		function getCustomRenderers(oldHeaderData,value){
			
			return contentMenuFactory.getCustomRenderers("cell");
		}
	
		function getCustomEditors(oldHeaderData,value){
			return contentMenuFactory.getCustomEditors("cell");
			
		}
		var sourceStrDataLength = function(oldCellValueData){
			var str = "";
			var countValue =0;
			for(var index=0;index<oldCellValueData.length;index++){
				if(str.lastIndexOf(oldCellValueData[index].recordData.CUID) == -1){
					str=str+oldCellValueData[index].recordData.CUID+",";
					countValue = countValue+1;
				};
			}
			return countValue;  
		 } ;
		 
		  var sourceStrData = function(oldCellValueData){
			var str = "{";
			for(var index=0;index<oldCellValueData.length;index++){
				str=str+"\""+oldCellValueData[index].colName+"\":\""+""+"\",";
			}
			if(oldCellValueData.length >0){
				str = str.substr(0,str.length-1);
			}
			str = str+"}";
			return str;  
		  } ; 
		function resetColor(id,oldhtml){
			if(id != ""){
					document.getElementById(id).innerHTML =oldhtml;
			}
			for(var index=0;index<oldCellData.length;index++){
				document.getElementById(oldCellData[index].key).style="";
				document.getElementById(oldCellData[index].key).innerHTML =oldCellData[index].value;
			}
			oldCellData = [];
			oldCellValueData=[];
			grid.getSelectionModel().clearSelections();
		}
		var tpl = new Ext.XTemplate(
													'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
													'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">'+
													'<tpl if="this.isBold(id) == true">',  
														'<font style="font-weight:bold;" color=\"#2828FF\"  >{value}</font>'+
													'</tpl>',
													'<tpl if="this.isBold(id) == false">',  
														'<font  color=\"#2828FF\"  >{value}</font>'+
													'</tpl>',
													'</div></div>',
													'</td>',  {
																compiled: true,
																disableFormats: true,
																isBold: function(name){
																	return name == 'name';
																}
															}
													);
	
	var contextmenu =  {};
		if(Ext.getCmp("cell_right_menu")){
			 contextmenu =  Ext.getCmp("cell_right_menu");
		}else{
		 contextmenu =  new Ext.menu.Menu({  
                shadow : 'frame', 
				id:"cell_right_menu",
				listeners :{
										beforeshow :function(){
											
										},
										beforehide:function(){
											if(oldCellSureCancel){
												return true;
											}
											return false;
										},
										hide:function(menu){
											if(oldCellSureCancel){
												resetColor(cellId,oldInnerHtml);
											}
											oldCellSureCancel = false;
										}
									},
                items:[  
					new Ext.grid.PropertyGrid({
								header:false,
								hideHeaders :true,
								customRenderers:getCustomRenderers(oldCellData,""),
								customEditors :getCustomEditors(oldCellData,""),
								
								viewConfig: {
										    fit:true,
											selectedRowClass:"x-grid3-row-data-selected",
											templates:{
												cell : tpl
												
											},
											forceFit: true
										   },
								autoHeight: true,
								bbar:[new Ext.form.Label({
									id:"countValueNum",
									text :sourceStrDataLength(oldCellValueData)+"行被选中"
								}),'->',{
									text:"确定",
									iconCls :'c_script_save',
									handler:function(btn){
										WireSegBatchAction.WireSegUpdate(Ext.encode({
											type:"cell",
											data:Ext.encode(oldCellValueData),
											value:Ext.encode({
											RELATED_MAINT_CUID:Ext.getCmp("RELATED_MAINT_CUID_CELL_ID").getValue(),
											RELATED_PROJECT_CUID:Ext.getCmp("RELATED_PROJECT_CUID_CELL_ID").getValue(),
											RELATED_SYSTEM_CUID:Ext.getCmp("RELATED_SYSTEM_CUID_CELL_ID").getValue(),
											LAY_TYPE:Ext.getCmp("LAY_TYPE_CELL_ID").getValue(),
											FIBER_TYPE:Ext.getCmp("FIBER_TYPE_CELL_ID").getValue(),
											OWNERSHIP:Ext.getCmp("OWNERSHIP_CELL_ID").getValue(),
											PURPOSE:Ext.getCmp("PURPOSE_CELL_ID").getValue(),
											MAINT_MODE:Ext.getCmp("MAINT_MODE_CELL_ID").getValue(),
											SPECIAL_PURPOSE:Ext.getCmp("SPECIAL_PURPOSE_CELL_ID").getValue(),
											OLEVEL:Ext.getCmp("OLEVEL_CELL_ID").getValue(),
											NETWORK_TYPE:Ext.getCmp("NETWORK_TYPE_CELL_ID").getValue(),
											CHECK_MODE:Ext.getCmp("CHECK_MODE_CELL_ID").getValue(),
											GEO_ENV:Ext.getCmp("GEO_ENV_CELL_ID").getValue(),
											PROJECT_STATE:Ext.getCmp("PROJECT_STATE_CELL_ID").getValue(),
											MAINT_STATE:Ext.getCmp("MAINT_STATE_CELL_ID").getValue(),
											DATA_STATE:Ext.getCmp("DATA_STATE_CELL_ID").getValue()
										})
										}),function(btn){
											oldCellSureCancel = true;
											resetColor(cellId,oldInnerHtml);
											contextmenu.hide();
											grid.getStore().load();
										});
									}
								},{
									text:"取消",
									iconCls :'c_cancel',
									handler:function(btn){
										oldCellSureCancel = true;
										resetColor(cellId,oldInnerHtml);
										contextmenu.hide();
									}
								}],
								width: 300,
								source: Ext.decode( sourceStrData(oldCellValueData)) 
							})
                ]  
            });  
		}	;
		Ext.getCmp("countValueNum").setText(sourceStrDataLength(oldCellValueData)+"行被选中");
		contextmenu.items.get(0).setSource( Ext.decode( sourceStrData(oldCellValueData)));
			
		if(contextmenu) {
			var x = e.getPageX(), y = e.getPageY();
			contextmenu.showAt([x, y]);
		}
	}
})