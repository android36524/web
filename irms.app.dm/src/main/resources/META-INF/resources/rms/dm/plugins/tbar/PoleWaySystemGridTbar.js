Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx + "/rms/dm/ductseg/LayingWireListPanelExt.js");
$importjs(ctx+'/dwr/interface/GetSystemParaAction.js');
$importjs(ctx + '/rms/dm/plugins/tbar/DmGridDataExport.js');
$importjs(ctx+'/dwr/interface/CalculateSystemLengthAction.js');
$importjs(ctx + "/rms/common/DmFilePanel.js");

Frame.grid.plugins.tbar.PoleWaySystemGridTbar = Ext.extend(Object,{
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.PoleWaySystemGridTbar.superclass.constructor.call(this);
		var isShowCAD = true;
		DWREngine.setAsync(false);
		GetSystemParaAction.getValueByParaName("TNMS_RUN_TIME_CFG","IS_SHOW_CAD",function(result){
			isShowCAD = (result=="1"?false:true);
		});
		DWREngine.setAsync(true);
		return [ '-', {
			text : '定位',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._doMapLocate
		},'-', {
			text : '路由管理',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._roteManage
		}, '-', {
			text : '杆路路由图',
			iconCls : 'c_page_white_magnify',
			scope : this.grid,
			handler : this._roteTopo
		}, '-', {
			text : '计算长度',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._method1
		}, '-', {
			text : '查询敷设的光缆',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._layingWire
		},'-',{
			xtype : 'splitbutton',
			text : '导入',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '杆路系统导入',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._import
			},{
				text : '杆路系统模板下载',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._export
			},'-', {
				text : '杆路段导入 ',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._importseg
			},{
				text : '杆路段模板下载',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._exportseg
			}]
		}, '-', {
			text : '附件管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._uploadAttachment 
		},'-',{
			xtype : 'splitbutton',
			text : '更多功能',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{								
				text : '导出CAD图纸',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				hidden : isShowCAD,
				handler : this._exportCAD
			},{
				text : '导出杆路段',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this.exportFile
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
			Ext.Msg.alert('温馨提示', '请选择系统数据.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单条系统的CAD导出.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var url = ctx+"/exportcad.do?cuid="+cuid;
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
	    	 key:'POLEWAY_SYSTEM'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	_export:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("杆路系统导入"));
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
	    	 key:'POLEWAY_SEG'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	_exportseg:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("杆路段"));
		window.open(url);
	},
	
	_roteManage : function() {
		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择杆路系统数据.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单条杆路系统的路由管理.');
			return;
		}
		var url ='/rms/dm/poleway/polewaypanel.jsp?x=x&type=SYSTEM&key=POLEWAY_BRANCH';
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var objectId=records[0].data['OBJECTID'];
		if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
			return;
		}
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
				labelCn + '路由管理');

	},
	_roteTopo : function() {
	    var records=this.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		 var cuid=records[0].data['CUID'];
		  var url = ctx+"/topo/index.do?code=DuctRouteSectionTopo&resId="+cuid+"&resType=POLEWAY_SYSTEM&clientType=html5";
		 
		 FrameHelper.openUrl(url,'杆路路由图-'+records[0].data['LABEL_CN']);
/*						 GetServiceParamAction.getUrlByServerName("TOPO",function(data){
							  var url = data+"/topo/index.do?code=DuctRouteSectionTopo&resId="+cuid+"&resType=POLEWAY_SYSTEM&clientType=html5";
							  var win = new Ext.Window({
							   title : '杆路路由图',
							   maximizable : true,
							   width : window.screen.availWidth*0.80,
							   height : window.screen.availHeight*0.75,
							   isTopContainer : true,
							   modal : true,
							   resizable : false,
							   contentEl : Ext.DomHelper.append(document.body, {
							    tag : 'iframe',
							    style : "border 0px none;scrollbar:true",
							    src : url,
							    height : "100%",
							    width : "100%"
							   })
							  });
							  win.show();
						 });*/
	},
	_method1 : function() {
		var records=this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records)||records.length==0){
			Ext.Msg.alert('温馨提示', '请选择管道系统数据.');
			return;
		}
		if(records.length>1){
			Ext.Msg.alert('温馨提示', '目前只支持单条计算统计长度.');
			return;
		}
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_MANHLE';
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
			return;
		}
		var scope = this;
		Ext.MessageBox
				.show({
					title : '注意！',
					msg : '此操作将重新计算现有系统长度，请根据具体需要进行选择<br />长度求和：根据现有段长求和，不更改段的长度<br />重新计算：此操作将根据系统下对应点的实际经纬度重新计算各段的长度，覆盖现有的长度<br />修改调线长度并求段长度和，当段的起止点的实际经纬度为非法值时，段的长度不修改<br />取消：取消当前操作',
					buttons : {
						ok : '长度求和',
						yes : '重新计算',
						cancel : '取消'
					},
					fn : function(btn) {
						if (btn == "ok") {
							CalculateSystemLengthAction.doPwsCalculateSystemLength(cuid);
							Ext.MessageBox.show({
								msg : '计算中,请等待..',
								progressText : '计算中...',
								width : 300,
								wait : true,
								waitConfig : {
									interval : 300
								}
							});
							setTimeout(function() {
								Ext.Msg.alert("计算完成");
								Ext.MessageBox.hide();
								scope.store.load();
							}, 3000);
						}

						else if (btn == "yes") {
							CalculateSystemLengthAction
									.modifyCalculateSystemLength(cuid);
							// CalculateSystemLengthAction.queryCalculateSystem(cuid);
							Ext.MessageBox.show({
								msg : '计算中,请等待..',
								progressText : '计算中...',
								width : 300,
								wait : true,
								waitConfig : {
									interval : 300
								}
							});
							setTimeout(function() {
								Ext.Msg.alert("计算完成");
								Ext.MessageBox.hide();
								scope.store.load();
							}, 3000);

						}
					}

				});
		},
		_layingWire : function() {
			var records=this.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
		    	return;
		    }
			var cuid=records[0].data['CUID'];
			var labelcn=records[0].data['LABEL_CN'];
			var layingWirePanel = new Frame.wire.LayingWireListPanelExt({
				cuid : cuid,
				labelcn : labelcn
			});
			
			var scope = this;
			var win = WindowHelper.openExtWin(layingWirePanel, {
				title : '查询承载对象敷设光缆信息',
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
		//导出  Add at 2015-4-15
		exportFile : function() {
			var GridDataExport= new Frame.grid.plugins.tbar.DmGridDataExport({
				grid : this.grid
			});
			GridDataExport.exportData('code=service_dict_export_dm.DM_EXPORT_POLEWAY_SEG&c_paramKey=RELATED_SYSTEM_CUID');
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