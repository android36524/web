Ext.ns("Frame.grid.plugins.event");
$importjs(ctx + "/dwr/interface/WireSegBatchAction.js");
var oldCellData=[];
var oldCellValueData=[];
var oldCellSureCancel = false;
var filterDataIndex = "DEST_POINT_CUID,ORIG_POINT_CUID,LABEL_CN,text";
var oldHeaderData=[];
var oldHeaderSureCancel = false;
Frame.grid.plugins.event.DMSystemGridEvent = Ext.extend(Object, {
	_treeLevelBoNameCfg: {},
	constructor: function(grid){
		this.grid = grid;
		var columns = this.grid.columns;
		this.grid.onContextMenu=function(e){
			var selDatas = [];

			if("undefined" == (typeof grid.getRootNode().childNodes[0])){
				return;
			}
				selDatas.push(grid.getRootNode().childNodes[0].attributes.data);	
			var childNodes = grid.getRootNode().childNodes[0].childNodes;
			for( var i =0;i<childNodes.length;++i){  
                 var record = grid.getNodeById(childNodes[i].id);
				 
				
				 selDatas.push(childNodes[i].attributes.data);
             }  
				
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
		var oldSelDatas =  grid.getSelectionModel().getSelectedNodes();
		
		var colName = etarget.target.textContent;
		function getDataIndex(text){
			var dataIndex ="";
			for(var index =0;index<columns.length;index++){
				if(columns[index].header == text){
					dataIndex = columns[index].dataIndex;
					break;
				};
			}
			return dataIndex;
		};
		var colIndex = getDataIndex(colName);
		
		if(filterDataIndex.lastIndexOf(colIndex) != -1){
			return;
		};
		
	  
		function getCustomRenderers(oldHeaderData,value){
			
			return   {
						"备注": function(v){
							if("undefined" == (typeof Ext.getCmp("REMARK_HEADER_ID").getEl())){
								return v;
							}
							return Ext.getCmp("REMARK_HEADER_ID").getEl().dom.value;
						}
					}
		}
	
		function getCustomEditors(oldHeaderData,value){
		
			return {"备注":new Ext.grid.GridEditor(new Ext.form.TextField({
							id:"REMARK_HEADER_ID",
							name : "REMARK_ID",
							fieldLabel : "备注"
						}))
				};
		}
		 
		
		
		var record = {};
		
		etarget.target.innerHTML = "<div id='"+colIndex+"'><font color=\"blue\" >"+oldInnerHtml+"</font></div>";
		oldHeaderData.push({
			key:colIndex,
			value:oldInnerHtml,
			colIndex:colIndex,
			value:"",
			colName:colName
		});
		
		var sourceStrData = function(oldHeaderData){
			var str = "{";
			for(var index=0;index<oldHeaderData.length;index++){
				str=str+"\""+oldHeaderData[index].colName+"\":\""+oldHeaderData[index].value+"\",";
			}
			if(oldHeaderData.length >0){
				str = str.substr(0,str.length-1);
			}
			str = str+"}";
			return str;
		}
		function resetColor(id,oldhtml){
			
			document.getElementById(id).innerHTML =oldhtml;
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
										
  										    for(var index2=0;index2<oldHeaderData.length;index2++){
													dataValue.push({
														CUID:selDatas[index].CUID,
														key:oldHeaderData[index2].colIndex,
														value:oldHeaderData[index2].oldInnerHtml,
														colIndex:oldHeaderData[index2].colIndex,
														value:"",
														colName:oldHeaderData[index2].colName
													});
											}
											
										
											
										};
										var param = getGridCfgByCode();
										var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);	
										if(cfg.key == "DUCT_BRANCH"){
											WireSegBatchAction.DuctBranchUpdate(Ext.encode({
													type:"header",
													selDatas:Ext.encode(dataValue),
													data:Ext.encode(dataValue),
													value:Ext.encode({
														REMARK:Ext.getCmp("REMARK_HEADER_ID").getValue()
													})
												}),function(btn){
													oldHeaderSureCancel = true;
													resetColor(colIndex,oldInnerHtml);
													contextmenu.hide();
													 grid.reloadNode();
												});
										}else if(cfg.key == "POLEWAY_BRANCH"){
											WireSegBatchAction.PolewayBranchUpdate(Ext.encode({
											type:"header",
											selDatas:Ext.encode(dataValue),
											data:Ext.encode(dataValue),
											value:Ext.encode({
												REMARK:Ext.getCmp("REMARK_HEADER_ID").getValue()
											})
										}),function(btn){
											oldHeaderSureCancel = true;
											resetColor(colIndex,oldInnerHtml);
											contextmenu.hide();
											 grid.reloadNode();
										});
										}else if(cfg.key == "STONEWAY_BRANCH"){
											WireSegBatchAction.StonewayBranchUpdate(Ext.encode({
											type:"header",
											selDatas:Ext.encode(dataValue),
											data:Ext.encode(dataValue),
											value:Ext.encode({
												REMARK:Ext.getCmp("REMARK_HEADER_ID").getValue()
											})
										}),function(btn){
											oldHeaderSureCancel = true;
											resetColor(colIndex,oldInnerHtml);
											contextmenu.hide();
											 grid.reloadNode();
										});
										}else if(cfg.key == "UP_LINE"){
											WireSegBatchAction.UpLineUpdate(Ext.encode({
											type:"header",
											selDatas:Ext.encode(dataValue),
											data:Ext.encode(dataValue),
											value:Ext.encode({
												REMARK:Ext.getCmp("REMARK_HEADER_ID").getValue()
											})
										}),function(btn){
											oldHeaderSureCancel = true;
											resetColor(colIndex,oldInnerHtml);
											contextmenu.hide();
											 grid.reloadNode();
										});
										}else if(cfg.key == "HANG_WALL"){
											WireSegBatchAction.HangWallUpdate(Ext.encode({
											type:"header",
											selDatas:Ext.encode(dataValue),
											data:Ext.encode(dataValue),
											value:Ext.encode({
												REMARK:Ext.getCmp("REMARK_HEADER_ID").getValue()
											})
										}),function(btn){
											oldHeaderSureCancel = true;
											resetColor(colIndex,oldInnerHtml);
											contextmenu.hide();
											 grid.reloadNode();
										});
										};									
										
										
									}
								},{
									text:"取消",
									iconCls :'c_cancel',
									handler:function(btn){
										oldHeaderSureCancel = true;
										resetColor(colIndex,oldInnerHtml);
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
		Frame.grid.plugins.event.DMSystemGridEvent.superclass.constructor.call(this);
		return {
			scope : this,
			rowdblclick : this.onRowDblClick,
			contextmenu:function( node, e){
				e.preventDefault();
			    var dataIndex = "undefined";
				var headrValue = "undefined";
				if("undefined"  != (typeof e.target.attributes['dataIndex']) ){
					  dataIndex = e.target.attributes['dataIndex'].nodeValue;
				};
				if("undefined"  == dataIndex  && e.target.innerHTML.indexOf("dataindex") != -1 ){
					  dataIndex = e.target.innerHTML.split("dataindex=")[1].split("headrindex=")[0].replace(/\"|\s/ig,"");
				};
				if(dataIndex == "undefined"){
					return;
				};
				var cellData  = e.target.textContent;
				
			
				if("undefined"  != (typeof e.target.attributes['headrIndex']) ){
					  headrValue = e.target.attributes['headrIndex'].nodeValue;
				};
				if("undefined"  == headrValue  && e.target.innerHTML.indexOf("headrindex") != -1 ){
					  headrValue = e.target.innerHTML.split("headrindex=")[1].split("class=")[0].replace(/\"|\s/ig,"");
				};
				if(headrValue == "undefined"){
					return;
				};
				
				var colName = eval("columns["+headrValue+"].header");
				 if(filterDataIndex.lastIndexOf(dataIndex) != -1){
					return;
				}
				
				var record = node.attributes;
	  
		
				var oldInnerHtml = e.target.innerHTML;
				var cellId = node.attributes.CUID;
				e.target.innerHTML = "<div id='"+cellId+"' style=\"background:#BEBEBE;\"><font color=\"blue\" >"+oldInnerHtml+"</font></div>";
				oldCellData.push({
					key:cellId,
					value:oldInnerHtml
				});
				oldCellValueData.push({
					recordData:record.data,
					CUID:record.data.CUID,
					colName:colName,
					colValue:cellData,
					dataIndex:dataIndex
				});
				  
				function getCustomRenderers(oldHeaderData,value){
					
					return  {
						"备注": function(v){
							if("undefined" == (typeof Ext.getCmp("REMARK_CELL_ID").getEl())){
								return v;
							}
							return Ext.getCmp("REMARK_CELL_ID").getEl().dom.value;
						}
					}
				}
			
				function getCustomEditors(oldHeaderData,value){
				return {
						"备注":new Ext.grid.GridEditor(new Ext.form.TextField({
							id:"REMARK_CELL_ID",
							name : "REMARK_ID",
							fieldLabel : "备注"
						}))
					};
					
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
						str=str+"\""+oldCellValueData[index].colName+"\":\""+oldCellValueData[index].colValue+"\",";
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
																if("undefined" !=  typeof(Ext.getCmp("REMARK_CELL_ID").getValue())){
																		
																		var param = getGridCfgByCode();
																		var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);	
																		if(cfg.key == "DUCT_BRANCH"){
																			WireSegBatchAction.DuctBranchUpdate(Ext.encode({
																			type:"cell",
																			data:Ext.encode(oldCellValueData),
																			value:Ext.encode({
																			REMARK:Ext.getCmp("REMARK_CELL_ID").getValue()
																		})
																		}),function(btn){
																			oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			 grid.reloadNode();
																		});
																		}else if(cfg.key == "POLEWAY_BRANCH"){
																			WireSegBatchAction.PolewayBranchUpdate(Ext.encode({
																			type:"cell",
																			data:Ext.encode(oldCellValueData),
																			value:Ext.encode({
																			REMARK:Ext.getCmp("REMARK_CELL_ID").getValue()
																		})
																		}),function(btn){
																			oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			 grid.reloadNode();
																		});
																		}else if(cfg.key == "STONEWAY_BRANCH"){
																			WireSegBatchAction.StonewayBranchUpdate(Ext.encode({
																			type:"cell",
																			data:Ext.encode(oldCellValueData),
																			value:Ext.encode({
																			REMARK:Ext.getCmp("REMARK_CELL_ID").getValue()
																		})
																		}),function(btn){
																			oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			 grid.reloadNode();
																		});
																		}else if(cfg.key == "UP_LINE"){
																			WireSegBatchAction.UpLineUpdate(Ext.encode({
																			type:"cell",
																			data:Ext.encode(oldCellValueData),
																			value:Ext.encode({
																			REMARK:Ext.getCmp("REMARK_CELL_ID").getValue()
																		})
																		}),function(btn){
																			oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			 grid.reloadNode();
																		});
																		}else if(cfg.key == "HANG_WALL"){
																			WireSegBatchAction.HangWallUpdate(Ext.encode({
																			type:"cell",
																			data:Ext.encode(oldCellValueData),
																			value:Ext.encode({
																			REMARK:Ext.getCmp("REMARK_CELL_ID").getValue()
																		})
																		}),function(btn){
																			oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			 grid.reloadNode();
																		});
																		};	
																		
																}else{
																		    oldCellSureCancel = true;
																			resetColor(cellId,oldInnerHtml);
																			contextmenu.hide();
																			
																};
																
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
				
				
				
				
			},
			click:this.onRowclick
		};
	},
	onRowDblClick : function(grid, rowIndex, e) {
	},
	onRowclick	:	function(node, e){
		
		if(!node){
			return;
		}
		this.grid.maintainPanel.setCanUpdate();
		var level = node.getDepth()-1;
		var classname = this.grid.gridConfig.treeLevelBoName[level];
		if (this._treeLevelBoNameCfg && this._treeLevelBoNameCfg[classname]) {
			this.grid.maintainPanel.propertyMeta = this._treeLevelBoNameCfg[classname];

		}else {
			var editorMeta = {
					cuid : classname
				};
			var scope = this;
			DWREngine.setAsync(false);
			EditorPanelAction.getEditorMeta(editorMeta,function(result){
				if(result) {
					scope.grid.maintainPanel.propertyMeta = result;
					scope._treeLevelBoNameCfg[classname] = result;
				}
			});
			DWREngine.setAsync(true);
		}
		this.grid.maintainPanel._buildCustomEdit();
		this.grid.maintainPanel._buildCustomRender();
		this.grid.maintainPanel.refreshGridData(node.attributes,node.attributes);
//		refreshData(Ext.encode([node.attributes]));
	}
});