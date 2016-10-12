Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');
$importjs(ctx+'/dwr/interface/ExportAction.js');

Frame.grid.plugins.tbar.CoverageDisignGridTbar = Ext.extend(Object,{
					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.CoverageDisignGridTbar.superclass.constructor
								.call(this);
						return [ '-', {
							xtype : 'splitbutton',
							text : '导入',
							iconCls : 'c_page_white_link',
							scope : this,
							menu : [{
								text : '预覆盖资源模板下载',
								iconCls : 'c_page_white_link',
								scope : this,
								handler : this._exports
							},{
								text : '预覆盖资源导入',
								iconCls : 'c_page_white_link',
								scope : this,
								handler : this._importOverlayResource
							}]
							
						},'-',{
							text : '转存量模板导出',
							iconCls : 'c_page_excel',
							scope : this,
							handler : this.hint
							
						} ,'-',{
							xtype : 'splitbutton',
							text : '存量导入',
							iconCls : 'c_page_excel',
							scope : this,
							menu : [{
								text : '光分纤箱导入',
								iconCls : 'c_page_excel',
								scope : this,
								handler : function(){this._importB('FIBER_DP','光分纤箱');}
							},{
								text : '光接头盒导入',
								iconCls : 'c_page_excel',
								scope : this,
								handler : function(){this._importB('FIBER_JOINT_BOX','光接头盒');}
							},{
								text : '光交接箱导入',
								iconCls : 'c_page_excel',
								scope : this,
								handler : function(){this._importB('FIBER_CAB','光交接箱');}
							},{
								text : '机房导入',
								iconCls : 'c_page_excel',
								scope : this,
								handler : function(){this._importB('ROOM','机房');}
							}]
							
						}
						];
					},
					hint:function(){
					    var scope = this;
					    Ext.Msg.alert('提示','转存量前，需导出模板后，补充必填字段.',this.exportData(scope));
					},
					exportData : function(scope) {					    	
						var sels = scope.grid.getSelectionModel().getSelections();
						if(!scope.exportPanel) {
							scope.columnDefPanel = scope.createColumnDefPanel({
								border : false
							});
							scope.exportPanel = new Ext.Panel({
								layout : 'fit',
								border : false,
								items : []
							});
							scope.exportWin = new Ext.Window({
								title : '导出数据',
								closeAction: 'hide',
								layout : 'fit',
								frame : true,
								modal : true,
								width : 330,
								height : 80,
								items : [scope.exportPanel],
								buttons : [{
									iconCls : 'c_page_excel',
									text : '导出（按查询）',
									scope : scope,
									handler : scope.exportByCondition
								},{
									iconCls : 'c_page_excel',
									text : '导出（按选择）',
									scope : scope,
									handler : scope.exportBySelected
								},{
									iconCls : 'c_door_in',
									text : '取消',
									scope : scope,
									handler : function() {
										scope.exportWin.hide();
									}
								}],
								listeners : {
									show : function(w) {
									}
								},
								bbar : new Ext.ux.StatusBar({
									textTpl : "导出完成,耗时{0}毫秒。【<a href='" + ctx + "/download.do?file={1}&fileName={2}'>点击下载</a>】",
									busyText : '数据导出中，请稍候...',
									defaultText: '选中数据：<font color="red">'+sels.length+'</font>条'
								})
							});
						}else {
							var statusBar = scope.exportWin.getBottomToolbar();
							statusBar.clearStatus();
							statusBar.setStatus('选中数据：<font color="red">'+sels.length+'</font>条');
							scope.exportWin.setHeight(80);
						}
						scope.exportPanel.sels = sels;
						scope.exportWin.show();
					},
					createColumnDefPanel : function(cfg) {
						var columns = this.grid.getColumnModel().config;
						var selData = [], seledData = [];
						Ext.each(columns, function(column, index){
							if(!Ext.isEmpty(column.header) && column.id != 'checker') {
								var d = [column.dataIndex, column.header];
								if(column.header.indexOf('red') != -1) {
									seledData.push(d);
								}else {
									selData.push(d);
								}
							}
						});
						var h = 400;
						this.selGrid = this.createSelGrid(true, selData);
						this.seledGrid = this.createSelGrid(false, seledData, true);
						this.seledGrid.on('afterrender', function(grid) {
							grid.ddrow = new Ext.dd.DropTarget(grid.container, {
								ddGroup : 'GridDD',
								copy : false,
								notifyDrop : function(dd, e, data) {
									// 选中了多少行
									var rows = data.selections;
									// 拖动到第几行
									var index = dd.getDragData(e).rowIndex;
									if (typeof(index) == "undefined") {
										return;
									}
									// 修改store
									for (var i = 0; i < rows.length; i++) {
										var rowData = rows[i];
										if (!grid.copy) {
											grid.getStore().remove(rowData);
										}
										grid.getStore().insert(index, rowData);
									}
								}
							});
						});
						var btnPanel = new Ext.Panel({
							border : false,
							width : 30,
							height : h,
							layout: {
				                type : 'vbox',
				                pack : 'center',
				                align : 'center'
				            },
				            defaults:{margins:'0 0 5 0'},
							items : [{
								xtype : 'button',
								iconCls : 'c_control_end_blue',
								scope : this,
								handler : this.onSelAll
							},{
								xtype : 'button',
								iconCls : 'c_control_fastforward_blue',
								scope : this,
								handler : this.onSel
							},{
								xtype : 'button',
								iconCls : 'c_control_rewind_blue',
								scope : this,
								handler : this.onUnSel
							},{
								xtype : 'button',
								iconCls : 'c_control_start_blue',
								scope : this,
								handler : this.onUnSelAll
							}]
						});
						this.selGrid.setHeight(h);
						this.seledGrid.setHeight(h);
						var panel = new Ext.Panel(Ext.apply({
							layout : 'fit',
							items : [{
								layout : 'column',
								items : [this.selGrid, btnPanel, this.seledGrid]
							}]
						}, cfg));
						return panel;
					},
					createSelGrid : function(isSel, data, enableDragDrop) {
						return new Ext.grid.GridPanel({
							enableDragDrop : enableDragDrop,
							border : false,
							columnWidth : .5,
							viewConfig: {
				            	forceFit:true
							},
							columns : [{
								header : isSel==true?'可选列':'导出列',
								dataIndex : 'text'
							}],
							store : new Ext.data.ArrayStore({
								fields : ['value', 'text'],
								data : data
							}),
							listeners : {
								rowdblclick : {
									scope : this,
									fn : function() {
										if(isSel == true) {
											this.onSel();
										}else {
											this.onUnSel();
										}
									}
								}
							}
						});
					},
					onSelAll : function() {
						var sels = this.selGrid.getStore().getRange();
						this.toSeled(sels);
					},
					onSel : function() {
						var sels = this.selGrid.getSelectionModel().getSelections();
						this.toSeled(sels);
					},
					onUnSel : function() {
						var sels = this.seledGrid.getSelectionModel().getSelections();
						this.toSel(sels);
					},
					onUnSelAll : function() {
						var sels = this.seledGrid.getStore().getRange();
						this.toSel(sels);
					},
					toSeled : function(datas) {
						this.seledGrid.getStore().add(datas);
						this.selGrid.getStore().remove(datas);
					},
					toSel : function(datas) {
						this.selGrid.getStore().add(datas);
						this.seledGrid.getStore().remove(datas);
					},
					exportByCondition : function() {
						var colList = this.getSeledCol();
						this.getResultFile(colList, null);
					},
					exportBySelected : function() {
						var colList = this.getSeledCol();
						var sels = this.exportPanel.sels;
						var cuidList = [];
						Ext.each(sels, function(sel){
							cuidList.push(sel.json.CUID);
						});
						if(cuidList.length > 0) {
							this.getResultFile(colList, cuidList);
						}else {
							Ext.Msg.alert('温馨提示', '请先选择要导出的记录！');
						}
					},
					getResultFile : function(colList, cuidList) {
						if(colList.length == 0){
							Ext.Msg.alert('温馨提示', '请先选择要导出的列！');
							return;
						}
						var statusBar = this.exportWin.getBottomToolbar();
						statusBar.showBusy();
						ExportAction.exportGridData(this.grid.gridCfg, colList, cuidList, function(results){
							statusBar.clearStatus({useDefaults:true});
							var time = results.seconds;
							var files = results.files;
							if(files && files.length > 0) {
								var file = files[0];
								statusBar.setStatus({
									iconCls: 'x-status-saved',
									text : String.format.apply(this, [statusBar.textTpl].concat([time, file.filePath, encodeURI(encodeURI(file.fileName))]))
								});
							}
						});
					},
					getSeledCol : function() {
					    this.onSelAll();
						var datas = this.seledGrid.getStore().getRange();
						var cols = [];
						Ext.each(datas, function(data) {
							cols.push(data.get('value'));
						});
						return cols;
					},
					
					_importB:function(_key,_title){
						var winCfg={
								width:600,
								height:200
						};
						this.filePanel=new IRMS.dm.FilePanel({
					    	 title: '导入: '+_title,
					    	 width: 100,
					    	 height:20,
					    	 inputParams : this.inputParams,
					    	 key:_key
					  });
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
					},

					
					_importB:function(_key,_title){
						var winCfg={
								width:600,
								height:200
						};
						this.filePanel=new IRMS.dm.FilePanel({
					    	 title: '导入: '+_title,
					    	 width: 100,
					    	 height:20,
					    	 inputParams : this.inputParams,
					    	 key:_key
					  });
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
					},
					_importOverlayResource:function(){
						var winCfg={
								width:600,
								height:200
						};
						this.filePanel=new IRMS.dm.FilePanel({
					    	 title: '导入',
					    	 width: 100,
					    	 height:20,
					    	 inputParams : this.inputParams,
					    	 key:'OVERLAY_RESOURCE'
					  });
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
					},
					
					_exports : function() {
						var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("预留资源模板"));
						window.open(url);
					}
});
