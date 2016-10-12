Ext.ns('Frame.grid.plugins.tbar');
/*
 * 光交接箱管理
 */
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/jslib/tp/topo-all.js');
$importjs(ctx+'/jslib/tp/tp-form.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');
$importjs(ctx + "/rms/dm/fiberbox/JumpLinkMainView.js");
$importjs(ctx + "/rms/dm/fiberbox/FiberLinkMainView.js");
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
//$importjs(ctx + "/map/dms-map-cabPortFusion.js");
//$importjs(ctx + "/rms/dm/wire/FibercabPortFusionView.js");
$importjs(ctx + "/rms/common/DmFilePanel.js");
$importjs(ctx + "/map/dms-map-portFusionPanel.js");
$importjs(ctx + "/map/dms-map-portFusion.js");
$importjs(ctx + "/rms/common/jumpFiberListPanel.js");

Frame.grid.plugins.tbar.FiberCabGridTbar = Ext.extend(Object,{
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.FiberCabGridTbar.superclass.constructor.call(this);
		return [ '-', {
			text : '定位',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._doMapLocate
		}, '-',{
			text : '端子管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._portManager
		}, '-',{
			text : '跳纤管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._jumpLinkView
		}, '-',{
			text : '纤芯关联',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._joinLinkView
		} , '-',{
			text : '端子直熔',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._connectPort
		},'-',{
			xtype : 'splitbutton',
			text : '导入',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '光交接箱导入',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._import
			},{
				text : '模板下载',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._export
			}]
		},'-',{
			xtype : 'splitbutton',
			text : '更多',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '批量命名',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._batchNames
			},{
				text : '面板图',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._panelView
			},{
				text : '查看关联线设施',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._relatedSystem
			},{
				text : '分光器列表',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._RelatedPos
			},{
				text : '图片管理',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._uploadAttachment
			},{
				text : '跳纤列表',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._jumpFiberList
			}]
		}
		];
	},
	_doMapLocate : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0 || records.length > 1){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		 var cuid=records[0].data['CUID'];
		 var labelCn = records[0].data['LABEL_CN'];
		 var lng = records[0].data['LONGITUDE'];
		 var lat = records[0].data['LATITUDE'];
		 
		 var hdata = new ht.Data();
		 hdata.a('CUID',cuid);
		 hdata.a('LABEL_CN',labelCn);
		 hdata.a('LONGITUDE',lng);
		 hdata.a('LATITUDE',lat);
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
			//contentWindow.Dms.Tools.graphicLocateOnMap(cuid);
			contentWindow.dms.Default.tpmap.locateResouce(datas);
		  }
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
	    	 key:'FIBER_CAB'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	_export:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("光交接箱"));
		window.open(url);
	},
	_openSelectView: function(){
		var url = ctx +"/cmp_res/grid/EditorGridPanel.jsp?hasMaintan=false&code=service_dict_dm.DM_PROJECT_MANAGEMENT";
    	var demension = "left=200,top=100,width=966,height=528";
    	var grid = this.grid;
		window.setShowModalDialogValue = function(value){
    		 var d = value;
    		 if (!Ext.isEmpty(d)) {
    		    if (!Ext.isEmpty(d)) {
					for (var j = 0; j < d.length; j++) {
					   var record = new Ext.data.Record(d[j].data); 
        			   grid.getStore().add(record);
					}
                };
            };
    	 };
    	 window.open(url, "selectRecord", demension);
	},
	
	
	_batchNames : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要批量命名的行.');
			return;
		}
		this.ObjArr = [];
		for (var i = 0; i < records.length; i++) {
			var obj = records[i];
			if (Ext.isEmpty(obj)) {
				Ext.Msg.alert('温馨提示', '当前选中数据,不包含CUID字段,请检查配置');
				return; 
			}
			this.ObjArr[i] = obj;
		}
		for (var j = 0; j < records.length; j++) {
			var labelCn = records[j].data['LABEL_CN'];
			if (Ext.isEmpty(labelCn)) {
				Ext.Msg.alert('温馨提示', '当前选中数据,不包含LABEL_CN字段,请检查配置');
				return;
			}
		}
		if (this.batchNamesPanel == null) {
			this.batchNamesPanel = new Frame.grid.edit.BatchNamesPanel({postfix:'号光交接箱'});
		}
		var scope = this;
		var win = WindowHelper.openExtWin(this.batchNamesPanel, {
			title : '批量命名',
			width: 300,
	    	height:221,
			buttons : [ {
				text : '确定',
				scope : this,
				handler : function() {
//					this.batchNamesPanel._save(this.ObjArr,function(){
//						scope.fireEvent('metaloaded');
//						MaskHelper.unmaskAll();
//						win.close();
//						batchNamesPanel = null;
//						scope.doQuery();
//					});
					DWREngine.setAsync(false);
					this.batchNamesPanel._save(this.ObjArr);
					win.hide();
					this.batchNamesPanel = null;
					this.grid.doQuery();
					DWREngine.setAsync(true);
				}

			}, {
				text : '关闭',
				scope : this,
				handler : function() {
					win.hide();
					this.batchNamesPanel=null;
				}
			} ]
		});
	},
	_portManager : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择一个交接箱.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '只可以选择一个交接箱.');
			return;
		}
		var url = '/rms/dm/port/FiberCabPortManager.jsp?code=service_dict_dm.DM_FIBERCAB_PORT';
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var objectId=records[0].data['OBJECTID'];
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含CUID字段,请检查配置');
			return;
		}
		if (Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含LABEL_CN字段,请检查配置');
			return;
		}
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
				labelCn + '路由管理');
	},
	/* 跳纤管理 */
	_jumpLinkView : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		var cuid=records[0].data['CUID'];
		var jumpLinkMainView = new Topo.x.JumpLinkMainView('FIBER_CAB',cuid);
		
		var dialog = new ht.widget.Dialog({
            title: "<html><font size=2>跳纤跳线管理</font></html>",
            width: 800,
            height: 400,
            titleAlign: "left", 
            draggable: true,
            closable: true,
            maximizable: true,
            resizeMode: "wh",
            content: jumpLinkMainView.getView(),
            action: function(button, e) {
                if (button.label === "确定") {
                	//后台进行保存
                }
                dialog.hide();
            }
        });
		dialog.onTransitionEnd = function(operation) {
			if (operation === "show") {
			} else {
				dialog.dispose();
			}
		};
		dialog.addEventListener(function(e) {
			jumpLinkMainView.invalidate();      
		});
		dialog.show();
	},
	/* 纤芯关联 */
	_joinLinkView : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
	    var cuid=records[0].data['CUID'];
		var name = records[0].data['LABEL_CN'];
        var fiberLinkPanel = new Frame.op.FiberLinkPanel({
            cuid : cuid,
			name : name,
            bmClassId : 'FIBER_CAB'
        });//FiberLinkMainView.js

        var scope = this;
        var win = WindowHelper.openExtWin(fiberLinkPanel, {
            title : '纤芯关联',
            width : 800,
            height : 450,
            buttons : [{
                text : '关闭',
                scope : this,
                handler : function() {
                    win.hide();
                }
            }]
        });
	},
	/* 画板图 */
	_panelView : function() {
	    var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		 var cuid=records[0].data['CUID'];	
		  var url = ctx+"/topo/index.do?code=FiberCabSectionTopo&resId="+cuid+"&resType=FIBER_CAB&clientType=html5";
		 FrameHelper.openUrl(url,'交接箱面板图-'+records[0].data['LABEL_CN']);
	},
	/* 端子直熔  */
	_connectPort : function() {
		var records=this.grid.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length==0 ){
			Ext.Msg.alert('温馨提示','请选择要一条记录.');
			return;
		}
		var cuid=records[0].data['CUID'];
		var labelcn = records[0].data['LABEL_CN'];
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据不包含必需字段,请检查配置。。。');
			return;
		}
		var cuids = [];
		cuids.push(cuid);
		var portFusion = new Topo.x.portFusionPanel(labelcn,cuid);
		portFusion.show();
//		var cabPortFusionPanel = new Frame.wire.FibercabPortFusionPanel({
//			cuid : cuid,
//			labelcn : labelcn,
//			bmClassId : 'FIBER_CAB',
//			cuids : cuids
//		});//FibercabPortFusionView.js
//		var win = WindowHelper.openExtWin(cabPortFusionPanel, {
//			title : labelcn + '>>>端子直熔',
//			width : window.screen.availWidth*0.60,
//			height : window.screen.availHeight*0.5
//		});
	},

	/* 查看关联线设施 */
	_relatedSystem : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
	    var cuid=records[0].data['CUID'];
		var name = records[0].data['LABEL_CN'];
        var relatedSystemPanel = new Frame.com.RelatedSystemListPanelExt({
            cuid : cuid,
			name : name,
            bmClassId : 'FIBER_CAB'
        });

        var scope = this;
        var win = WindowHelper.openExtWin(relatedSystemPanel, {
            title : '查看关联线设施',
            width : 800,
            height : 450,
//						   width : window.screen.availWidth*0.60,
//						   height : window.screen.availHeight*0.5,
            buttons : [{
                text : '关闭',
                scope : this,
                handler : function() {
                	win.hide();
                }
            }]
        });
	},

	_RelatedPos : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择一个交接箱.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '只可以选择一个交接箱.');
			return;
		}
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_POS_LIST&hasQuery=false';
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var objectId=records[0].data['OBJECTID'];
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含CUID字段,请检查配置');
			return;
		}
		if (Ext.isEmpty(labelCn)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含LABEL_CN字段,请检查配置');
			return;
		}
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid+"&objectId="+objectId,
				labelCn + '分光器管理');
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
			title : '图片管理',
	    	width : 100,
	    	height : 350,
	    	relatedServiceCuid : records[0].data['CUID'],
	    	type : 1,
	    	readOnly : false,
	    	preview : true,
	    	gridPanel : this.grid
	    });
		var fileWin = WindowHelper.openExtWin(this.filePanel, winCfg);
	},
	_jumpFiberList : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (records == null || records.length == 0 || records.length > 1) {
			Ext.Msg.alert("温馨提示", "请选择要一条记录.");
			return;
		}
		var cuid = records[0].data.CUID;
		var winCfg = {
				title : '跳纤列表',
				width : 800,
				height : 400
			};
		var jumpFiberListPanel = new IRMS.dm.common.jumpFiberListPanel({cuid : cuid});
		var fileWin = WindowHelper.openExtWin(jumpFiberListPanel, winCfg);
	}
});