Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/dwr/interface/GenerateOpticalAction.js');
$importjs(ctx+'/dwr/interface/OpticalViewAction.js');
$importjs(ctx+'/dwr/interface/CheckTaskNameAction.js');
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx + "/rms/dm/wire/WireRemainView.js");
$importjs(ctx + "/rms/dm/ductseg/WirePassListPanelExt.js");
$importjs(ctx+'/dwr/interface/GetSystemParaAction.js');
$importjs(ctx+'/map/dms-map-deleteResource.js');
$importjs(ctx + '/rms/dm/wire/WireSystemDeleteView.js');
$importjs(ctx + '/rms/dm/plugins/tbar/DmGridDataExport.js');
$importcss(ctx + '/jslib/ext/ux/statusbar/css/statusbar.css');
$importjs(ctx + "/jslib/ext/ux/statusbar/StatusBar.js");
$importjs(ctx + "/dwr/interface/PropertyGridViewAction.js");
$importjs(ctx + '/rms/dm/optical/opticalPanel.js');
$importjs(ctx + "/rms/common/DmFilePanel.js");

/*
 * 光缆系统管理的生成光纤按钮的功能及实现
 *光缆系统管理的功能按钮及其实现
 */
Frame.grid.plugins.tbar.WireSystemGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.WireSystemGridTbar.superclass.constructor.call(this);
		var isShowCAD = true;
		DWREngine.setAsync(false);
		GetSystemParaAction.getValueByParaName("TNMS_RUN_TIME_CFG","IS_SHOW_CAD",function(result){
				isShowCAD = (result=="1"?false:true);
		});
		DWREngine.setAsync(true);
		return [  '-', {
				text : '定位',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._doMapLocate
			},'-',  {
				text : '路由管理',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._routeManage
			}, '-', {
				text : '纤芯管理',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._fiberManage
			}, '-', {
				text : '纤芯接续图',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._fiberlinkview
			}, '-', {
				text : '预留信息',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._wireRemainView
			}, '-', {
				text : '删除',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this.deleteWireSystem
			},'-',{
				text : '导出CAD图纸',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				hidden: isShowCAD,
				handler : this._exportCAD
			}, '-',{
				xtype : 'splitbutton',
				text : '导入',
				iconCls : 'c_page_white_link',
				scope : this,
				menu : [{
					text : '光缆系统导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._import
				},{
					text : '光缆系统模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._export
				},'-', {
					text : '光缆段导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._importseg
				},{
					text : '光缆段模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._exportseg
				},'-', {
					text : '光缆纤芯排列图谱按照导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._import1
				},{
					text : '光缆纤芯排列图谱按照模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._export1
				},'-', {
					text : '光缆段的敷设信息导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._import2
				},{
					text : '光缆段的敷设信息模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._export2
				},'-', {
					text : '预留点导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._import3
				},{
					text : '预留点模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._export3
				},'-', {
					text : '纤芯上架链接管理导入',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._import4
				},{
					text : '纤芯上架链接管理模板下载',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._export4
				}]
			},'-',{
				xtype : 'splitbutton',
				text : '更多功能',
				iconCls : 'c_page_white_link',
				scope : this,
				menu : [{								
					text : '光缆路由图',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._routeview
				},'-',{
					text : '生成光纤',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._methodGenerateFiber
				//目前未实现，后续迁移
//							}, '-', {   
//								text : '计算长度',
//								iconCls : 'c_page_white_link',
//								scope : this.grid,
//								handler : this._method2
				},{
					text : '查看光纤列表',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._getOpticalLists
				} ,'-', {
					text : '设置核查任务',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._methodCheckTaskNameAction
				},'-', {
					text : '光缆系统路由明细',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._wireRouteLists
				},'-', {
					text : '光缆系统接头盒明细',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._fiberJointBoxLists
				},'-',{
					text : '承载对象查询',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._methodwirePass
				},'-',{
					text : '导出光缆段',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this.exportFile
				}, '-', {
					text : '附件管理',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._uploadAttachment 
				}]
			}];
		},
		_doMapLocate : function(){
			var records=this.grid.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0 || records.length > 1){
		    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
		    	return;
		    }
			 var cuid=records[0].data['CUID'];
			 var labelCn = records[0].data['LABEL_CN'];
			 var hdata = new ht.Data();
			 hdata.a('CUID',cuid);
			 hdata.a('LABEL_CN',labelCn);
			 var datas = new ht.List();
			 datas.add(hdata);
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
				contentWindow.dms.Default.tpmap.locateResouce(datas);
			  }
		},
		_exportCAD:function(){
			
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0) {
				Ext.Msg.alert('温馨提示', '请选择数据.');
				return;
			}
			if (records.length > 1) {
				Ext.Msg.alert('温馨提示', '目前只支持单条数据的CAD导出.');
				return;
			}
			var cuid = records[0].data['CUID'];
			var url = ctx+"/exportcad.do?cuid="+cuid;
			window.open(url);
		},
		_import1:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'FIBER'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_export1:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("光缆纤芯排列图谱按照模板导入"));
			window.open(url);
		},
		
		_import2:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'WIRE_TO_DUCTLINE'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_export2:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("光缆段的敷设信息"));
			window.open(url);
		},
		
		_import3:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'WIRE_REMAIN'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_export3:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("预留点"));
			window.open(url);
		},
		
		_import4:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'FIBER'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_export4:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("纤芯上架链接管理"));
			window.open(url);
		},
		
		_import:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'WIRE_SYSTEM'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_export:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("光缆系统"));
			window.open(url);
		},
	
		_importseg:function(){
			var winCfg={
					width:600,
					height:200
			};
			this.filePanel=new IRMS.dm.FilePanel({
		    	 title: '导入',
		    	 width: 100,
		    	 height:20,
		    	 inputParams : this.inputParams,
		    	 key:'WIRE_SEG'
		  });
			var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		},
		_exportseg:function(){
			var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("光缆段导入"));
			window.open(url);
		},
		_fiberlinkview : function() {
			 var records=this.getSelectionModel().getSelections();
			    if(Ext.isEmpty(records) || records.length==0){
			    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
			    	return;
			    }
				 var cuid=records[0].data['CUID'];		
				  var url = ctx+"/topo/index.do?code=WireSystemLinkSectionTopo&resId="+cuid+"&resType=WIRE_SYSTEM&clientType=html5";
				 
				 FrameHelper.openUrl(url,'纤芯接续图-'+records[0].data['LABEL_CN']);	
		},
		
		_routeview : function() {
			 var records=this.getSelectionModel().getSelections();
			    if(Ext.isEmpty(records) || records.length==0){
			    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
			    	return;
			    }
				 var cuid=records[0].data['CUID'];
				  var url = ctx+"/topo/index.do?code=WireRouteSectionTopo&resId="+cuid+"&resType=WIRE_SYSTEM&clientType=html5";
				 
				 FrameHelper.openUrl(url,'光缆路由图-'+records[0].data['LABEL_CN']);				 
		},
		/*
		 * 设置核查任务
		 */
		_methodCheckTaskNameAction : function() {
			var records=this.getSelectionModel().getSelections();
			if(Ext.isEmpty(records)||records.length==0){
				Ext.Msg.alert('温馨提示', '请选择一条记录.');
				return;
			}
			if(records.length>1){
				Ext.Msg.alert('温馨提示', '目前只支持单条记录生成.');
				return;
			};
			Ext.MessageBox.show({
				title:'注意！',
				msg:'您确认要核查吗',
				buttons:{yes:'确认',cancel:'取消'},
				 fn:function(btn){
					 if(btn =="yes"){
						 Ext.MessageBox.prompt('任务核查', '请输入核查任务:', function(e,text) {
								if(e == "ok") {
									if(''==text||null==text){
										Ext.Msg.alert('提示','请输入核查任务');
										return;
									}
									var list={
											CUID:records[0].data.CUID,
											OBJECTID:records[0].data.OBJECTID,
											RELATED_DISTRICT_CUID : records[0].data.RELATED_SPACE_CUID.CUID
									};
							        setTimeout(function(){
							        	CheckTaskNameAction.lookTaskNameIsHave(text,list,function(data){
							        		if(data=='true'){
							        			Ext.Msg.alert('提示','[任务名称] 已占用！');
							        			return;
							        		}
											Ext.Msg.alert('执行结果',data);
										});
							        }, 100);
								}
								Ext.MessageBox.show({
									msg: '任务核查中, 请等待...',
									progressText: '执行中...',
									width:300,
									wait:true,
									waitConfig: {interval:200},
									icon:'ext-mb-download'
								});
						 }
						 );
					 }
				 }
			});
		},
		/*
		 * 光缆预留信息
		 */
		_wireRemainView : function() {
			var records=this.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
		    	return;
		    }
			var cuid=records[0].data['CUID'];
			var labelcn=records[0].data['LABEL_CN'];
			var wireRemainPanel = new Frame.wire.WireRemainPanel({
				cuid : cuid,
				labelcn : labelcn
			});
			
			var scope = this;
			var win = WindowHelper.openExtWin(wireRemainPanel, {
				title : '光缆预留设置',
				   width : window.screen.availWidth*0.5,
				   height : window.screen.availHeight*0.6,
				buttons : [{
					text : '关闭',
					scope : this,
					handler : function() {
						win.hide();
					}
				}]
			});
		},
		/*
		*生成光纤
		*/
		_methodGenerateFiber : function() {
							var records=this.getSelectionModel().getSelections();
							if(Ext.isEmpty(records)||records.length==0){
								Ext.Msg.alert('温馨提示', '请选择一条记录.');
								return;
							};
							if(records.length>1){
								Ext.Msg.alert('温馨提示', '目前只支持单条记录生成.');
								return;
							};
							var cuid = records[0].data['CUID'];
							var labelCn = records[0].data['LABEL_CN'];
							if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
								Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
								return;
							}
							var list={
									CUID:records[0].data.CUID,
									OBJECTID:records[0].data.OBJECTID
							};
							Ext.MessageBox.show({
								title:'注意！',
								msg:'选择此条记录生成光纤',
								buttons:{yes:'生成光纤',cancel:'取消'},
								 fn:function(btn){
									 if(btn =="yes"){
										 GenerateOpticalAction.doDealOptical(list,function(data){
											 if(data){
												 Ext.MessageBox.show({
														title:'提示！',
														msg:'生成光纤成功,是否立即进行光纤核查',
														buttons:{yes:'确定',cancel:'取消'},
														fn: function(e) {
													 if(e=='yes'){
														 var time =new Date().format('Y-m-d H:i:s');
														 var text = "光纤生成核查"+"-纤芯管理"+time;
														 setTimeout(function(){
													        	CheckTaskNameAction.lookTaskNameIsHave(text,list,function(data){
													        		if(data=='true'){
													        			Ext.Msg.alert('提示','[任务名称] 已占用！');
													        			return;
													        		}
																	Ext.Msg.alert('执行结果',data);
																});
													        }, 100);
													        Ext.MessageBox.show({
													        	msg: '任务核查中, 请等待...',
													        	progressText: '执行中...',
													        	width:300,
													        	wait:true,
													        	waitConfig: {interval:200},
													        	icon:'ext-mb-download'
													        });
												   };
												}
											 });
											};
									 });
									};
							 }
						});
		},
	
		/*
		 * 查看光纤列表实现
		 */
		_getOpticalLists : function() {
			var records = this.getSelectionModel().getSelections();
			
			if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
				Ext.Msg.alert('温馨提示', '请选择一条光缆系统数据操作。');
				return;
			} else {
				this.formPanel = new Ext.form.formPamel({});
				var win = WindowHelper.openExtWin(this.formPanel, {
					title : '查看光纤列表',
					width : 850,
					height : 400,
					frame : true,
					buttons : [ {
						text : '关闭',
						handler : function() {
							win.close();
						}
					} ]
				});
				
				// 与后台数据交互
				var scope=this.formPanel;
				var recordss = new Array();
				var WireSystemCUID = records[0].data.CUID;
				MaskHelper.mask(scope.getEl(),"读取中,请稍候...");
				OpticalViewAction.getOpticalLists(WireSystemCUID,function(data){
					for(var i=0;i<data.length;i++){
						var gridData =data[i];
						var record = new Ext.data.Record({
							LABEL_CN : gridData.LABEL_CN,
							MAKE_FLAG : gridData.MAKE_FLAG,
							ORIG_SITE_CUID : gridData.ORIG_SITE_CUID,
							ORIG_ROOM_CUID : gridData.ORIG_ROOM_CUID,
							ORIG_EQP_CUID : gridData.ORIG_EQP_CUID,
							ORIG_POINT_CUID : gridData.ORIG_POINT_CUID,
							DEST_SITE_CUID : gridData.DEST_SITE_CUID,
							DEST_ROOM_CUID : gridData.DEST_ROOM_CUID,
							DEST_EQP_CUID : gridData.DEST_EQP_CUID,
							DEST_POINT_CUID : gridData.DEST_POINT_CUID,
							ROUTE_DESCIPTION : gridData.ROUTE_DESCIPTION,
							OWNERSHIP : gridData.OWNERSHIP,
							FIBER_LEVEL : gridData.FIBER_LEVEL,
							FIBER_STATE : gridData.FIBER_STATE,
							USER_NAME : gridData.USER_NAME,
							FIBER_TYPE : gridData.FIBER_TYPE,
							FIBER_DIR : gridData.FIBER_DIR,
							PURPOSE : gridData.PURPOSE,
							LENGTH : gridData.LENGTH,
							SUM_ATTENU_1310 : gridData.SUM_ATTENU_1310,
			        		SUM_ATTENU_1550 : gridData.SUM_ATTENU_1550,
			        		APPLY_TYPE : gridData.APPLY_TYPE
						});
						recordss.push(record);
					}
					var store = scope.getStore();
					store.add(recordss);
					MaskHelper.unmask(scope.getEl());
				});
			}
		},
		
		_routeManage : function() {
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0) {
				Ext.Msg.alert('温馨提示', '请选择光缆系统数据.');
				return;
			}
			if (records.length > 1) {
				Ext.Msg.alert('温馨提示', '目前只支持单条光缆系统的路由管理.');
				return;
			}
			var cuid = records[0].data['CUID'];
			var labelCn = records[0].data['LABEL_CN'];
			var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_WIRE_SEG&hasQuery=false';
			FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
					labelCn + '路由管理');
		},	
		_fiberManage : function() {
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0) {
				Ext.Msg.alert('温馨提示', '请选择光缆系统数据.');
				return;
			}
			if (records.length > 1) {
				Ext.Msg.alert('温馨提示', '目前只支持单条光缆系统的纤芯管理.');
				return;
			}
			var url = '/rms/dm/wire/fibermanagepanel.jsp?x=x&type=SYSTEM&key=DUCT_BRANCH';
			var cuid = records[0].data['CUID'];
			var labelCn = records[0].data['LABEL_CN'];
			var objectId=records[0].data['OBJECTID'];
			if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
					labelCn + '纤芯管理');
		},
		_methodwirePass : function() {
			var records=this.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
		    	return;
		    }
			var cuid=records[0].data['CUID'];
			var labelcn=records[0].data['LABEL_CN'];
			var wirePassListPanel= new Frame.wire.WirePassListPanelExt({
				cuid : cuid,
				labelcn : labelcn
			});
			
			var scope = this;
			var win = WindowHelper.openExtWin(wirePassListPanel, {
				title : '光缆经过的承载对象查询',
				   width : window.screen.availWidth*0.60,
				   height : window.screen.availHeight*0.5,
				buttons : [{
					text : '关闭',
					scope : this,
					handler : function() {
						win.hide();
					}
				}]
			});
		},
		/**
		 * 光缆系统路由明细
		 */
		_wireRouteLists : function() {
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
				Ext.Msg.alert('温馨提示', '请选择一条光缆系统数据.');
				return;
			}
			var cuid = records[0].data['CUID'];
			var labelCn = records[0].data['LABEL_CN'];
			var objectId=records[0].data['OBJECTID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_WIREROUTEEXPORT&hasQuery=false';
			FrameHelper.openUrl(ctx + url+ '&cuid=' + cuid + '&labelCn=' + labelCn,
					labelCn + '光缆系统路由明细');
		
		},
		/**
		 * 光缆系统接头盒明细
		 */
		_fiberJointBoxLists : function() {
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
				Ext.Msg.alert('温馨提示', '请选择一条光缆系统数据.');
				return;
			}
			var cuid = records[0].data['CUID'];
			var labelCn = records[0].data['LABEL_CN'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_WIRESYSTEMDIRCTION&hasQuery=false';
			FrameHelper.openUrl(ctx + url + '&cuid=' + cuid +"&labelCn="+labelCn,labelCn +'光缆中涉及到的方向');
		    
		},
		deleteWireSystem : function() {
			var records=this.getSelectionModel().getSelections();
			var scope = this;
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择一条记录。。。');
		    	return;
		    }
		    
			var cuid = records[0].data['CUID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据不包含必需字段,请检查配置。。。');
				return;
			}
			var labelcn=records[0].data['LABEL_CN'];
			
			var cuids = [];
		    for(var i=0;i<records.length;i++){
		    	var cid = records[i].data['CUID'];
		    	cuids.push(cid);
		    }
			var deleteWireSystemPanel = new Frame.wire.WireSystemDeletePanel({
				cuid : cuid,
				labelcn : labelcn,
				cuids : cuids
			});
			
			var win = WindowHelper.openExtWin(deleteWireSystemPanel, {
				title : '光缆删除操作',
				width : window.screen.availWidth*0.5,
				height : window.screen.availHeight*0.5
			});
			deleteWireSystemPanel._win = function(){
				win.hide();
				scope.store.reload();
				return true;
			};
		},
		//导出  Add at 2015-4-15
		exportFile : function() {
			var GridDataExport= new Frame.grid.plugins.tbar.DmGridDataExport({
				grid : this.grid
			});
			GridDataExport.exportData('code=service_dict_export_dm.DM_EXPORT_WIRE_SEG&c_paramKey=RELATED_SYSTEM_CUID');
		},
		
		_uploadAttachment : function() {
			var records = this.grid.getSelectionModel().getSelections();
			if (records == null || records.length == 0 || records.length > 1) {
				Ext.Msg.alert("温馨提示", "请选择要一条记录.");
				return;
			}

			var winCfg = {
				width : 700,
				height : 350
			};

			this.filePanel = new IRMS.dm.common.FilePanel({
				title : '附件管理',
		    	width : 100,
		    	height : 350,
		    	relatedServiceCuid : records[0].data['CUID'],
		    	type : 1,
		    	readOnly : false,
		    	preview : false,
		    	gridPanel : this.grid
		    });
			var fileWin = WindowHelper.openExtWin(this.filePanel, winCfg);
		}
});