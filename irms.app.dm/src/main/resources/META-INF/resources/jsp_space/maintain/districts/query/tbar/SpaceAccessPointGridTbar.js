Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx + "/commons/utils/FrameHelper.js");
$importjs(ctx + "/commons/utils/WindowHelper.js");
$importjs(ctx + "/jsp_space/maintain/districts/form/AccessPointForm.js");
$importjs(ctx + "/jsp_space/maintain/districts/form/RoomForm.js");
$importjs(ctx + "/dwr/interface/AccessPointDwrAction.js");
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx+'/jslib/tp/topo-all.js');
$importjs(ctx+'/jslib/tp/tp-form.js');
$importjs(ctx + "/jslib/ext/ux/fileuploadfield/FileUploadField.js");
$importjs(ctx + "/jsp_space/maintain/excel/ExportModel.js");
$importjs(ctx + "/jsp_space/maintain/excel/ExcelImport.js");
$importjs(ctx + "/jslib/ext/ux/statusbar/StatusBar.js");
$importjs(ctx + "/dwr/interface/ExcelAction.js");
$importjs(ctx + "/jsp_space/maintain/excel/GridDataExport.js");
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');
$importcss(ctx + "/rms/dm/fiberbox/JumpLinkMainView.css");
$importjs(ctx + "/rms/dm/fiberbox/JumpLinkMainView.js");
$importjs(ctx + "/map/dms-map-accesspointFBox.js");
$importjs(ctx + "/rms/dm/common/AccesspointAddFboxPanel.js");
$importjs(ctx + "/rms/dm/common/AccesspointAddFiberCabPanel.js");
$importjs(ctx + "/rms/dm/common/AccesspointAddFiberDpPanel.js");
$importjs(ctx + "/rms/dm/wire/AccesspointFboxView.js");
$importjs(ctx + "/rms/common/DmFilePanel.js");
$importjs(ctx + "/jsp_space/maintain/districts/query/tbar/FiberLinksMainView.js");
$importjs(ctx+'/rms/common/FilePanel.js');

Frame.grid.plugins.tbar.SpaceAccessPointGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.SpaceAccessPointGridTbar.superclass.constructor.call(this);
		return ['-', {
			text : '定位',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._doMapLocate
		},'-',
//		{
//			text : '新增',
//			iconCls : 'c_drive_add',
//			scope : this,
//			handler : this.addDistrict
//		},'-',{
//			text : '修改',
//			iconCls : 'c_drive_edit',
//			scope : this,
//			handler : this.edit
//		},'-',{
//			text : '删除',
//			iconCls : 'c_drive_delete',
//			scope : this,
//			handler : this.del
//		},'-',
		{
			text : '模板下载',
			iconCls : 'c_page_excel',
			scope : this,
			//handler : this.exportModel
			handler : this.exportModel_allcols
		},'-',
		{
			text : '数据导入',
			iconCls : 'c_page_white_excel',
			scope : this,
			handler : this.importData
		},'-',
		{
			text : '数据导出',
			iconCls : 'c_page_white_excel',
			scope : this,
			handler : this.exportDatas
		},'-',{
			text : '跳纤跳线管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._jumpLinkView
		},'-',{
			text : '查看关联线设施',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._relatedSystem
		},'-',{
			xtype : 'splitbutton',
			text : '更多功能',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '增加终端盒',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._batchNames
			},'-',
			{
	            text : '终端盒列表',
	            iconCls : 'c_page_white_link',
	            scope : this,
	            handler : this._getFiberJointBox
	        },'-',{
				text : '增加交接箱',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._addFiberCab
			},'-',
			{
	            text : '交接箱列表',
	            iconCls : 'c_page_white_link',
	            scope : this,
	            handler : this._getFiberCab
	        },'-',{
				text : '增加分纤箱',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._addFiberDp
			},'-',
			{
	            text : '分纤箱列表',
	            iconCls : 'c_page_white_link',
	            scope : this,
	            handler : this._getFiberDp
	        },'-',{
				text : '图片管理',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._uploadAttachment
			}
          ]},'-',{
			text : '纤芯关联',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._joinLinkView
		},'-',{
			text : '层间光缆列表',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._interwireView
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
	_getFiberJointBox : function(){

		var records = this.grid.getSelectionModel().getSelected();
		var scope = this;
	    if(!records){
	    	Ext.Msg.alert('温馨提示','请选择一条记录。。。');
	    	return;
	    }
	    
		var cuid = records.get('CUID');
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据不包含必需字段,请检查配置。。。');
			return;
		}
		var labelcn=records.get('LABEL_CN');
		
		var cuids = [];
		cuids.push(cuid);
	    
		var accesspointFboxPanel = new Frame.wire.AccessPointShowFboxPanel({
			cuid : cuid,
			labelcn : labelcn,
			cuids : cuids
		});

		var win = this.openExtWin(accesspointFboxPanel, {
			title : labelcn + '>>>终端盒列表',
			width : 500,
			height : 300
		},{
			closeAction : 'close'
		});
		
	
	},
	_getFiberCab : function() {
		var records=this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要增加光交接箱的接入点.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '只能选择一条数据！');
			return;
		}
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_FIBER_CAB_IN_ACCESSPOINT&hasQuery=false';
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
				labelCn + '光交接箱管理');
	},
	//分纤箱列表
	_getFiberDp : function() {
		var records=this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要增加光分纤箱的接入点.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '只能选择一条数据！');
			return;
		}
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_FIBER_DP_IN_ACCESSPOINT&hasQuery=false';
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
				labelCn + '光分纤箱管理');
	},

	_batchNames : function() {
	    var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要增加终端盒的接入点！');
	    	return;
	    }
	    if(records.length > 1){
	    	Ext.Msg.alert('温馨提示','只能选择一条数据！');
	    	return;
	    }
	    this.ObjArr = [];
	    for(var i=0;i<records.length;i++){
	    	var obj=records[i];
	    	if(Ext.isEmpty(obj)){
	    		Ext.Msg.alert('温馨提示','当前选中数据,不包含CUID字段,请检查配置');
	    		return;   //加上好像不起作用。是不是Ext.Msg.alert就是不运行下面的代码了呢？
	    	}
	    	this.ObjArr[i] = obj;
	    }
	    for(var j=0;j<records.length;j++){
	    	var labelCn=records[j].data['LABEL_CN'];
	    	if(Ext.isEmpty(labelCn)){
	    		Ext.Msg.alert('温馨提示','当前选中数据,不包含LABEL_CN字段,请检查配置');
	    		return;
	    	}						    	
	    }
	    if(this.batchNamesPanel==null){
	    	this.batchNamesPanel=new Frame.grid.edit.AccessPointAddFboxPanel({postfix:'号终端盒',number:records.length});
	    }
	    var scope=this;
	    var win=WindowHelper.openExtWin(this.batchNamesPanel,{
	    	 title: '批量命名',
	    	 width: 300,
		     height:221,
	    	 buttons: [{
	    		  text: '确定',
	    		  scope:this,
	    		  handler: function(){
	    			  this.batchNamesPanel._save(this.ObjArr);
			    	  win.hide();
			    	  this.batchNamesPanel=null;
			    	  this.grid.doQuery();
	    		  }
	    	 },{
	    		 text: '关闭',
	    		 scope : this,
	    		 handler: function() {
	    			 win.hide();
	    			 this.batchNamesPanel=null;
	    		 }						    	     
	    	 }]
	  });					    
  },
		_addFiberCab : function(){
		    var records=this.grid.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择要增加交接箱的接入点！');
		    	return;
		    }
		    if(records.length > 1){
		    	Ext.Msg.alert('温馨提示','只能选择一条数据！');
		    	return;
		    }
		    this.ObjArr = [];
		    for(var i=0;i<records.length;i++){
		    	var obj=records[i];
		    	if(Ext.isEmpty(obj)){
		    		Ext.Msg.alert('温馨提示','当前选中数据,不包含CUID字段,请检查配置');
		    		return;   //加上好像不起作用。是不是Ext.Msg.alert就是不运行下面的代码了呢？
		    	}
		    	this.ObjArr[i] = obj;
		    }
		    for(var j=0;j<records.length;j++){
		    	var labelCn=records[j].data['LABEL_CN'];
		    	if(Ext.isEmpty(labelCn)){
		    		Ext.Msg.alert('温馨提示','当前选中数据,不包含LABEL_CN字段,请检查配置');
		    		return;
		    	}						    	
		    }
		    if(this.batchNamesPanel==null){
		    	this.batchNamesPanel=new Frame.grid.edit.AccesspointAddFiberCabPanel({postfix:'号交接箱',number:records.length});
		    }
		    var scope=this;
		    var win=WindowHelper.openExtWin(this.batchNamesPanel,{
		    	 title: '批量命名',
		    	 width: 300,
			     height:221,
		    	 buttons: [{
		    		  text: '确定',
		    		  scope:this,
		    		  handler: function(){
		    			  this.batchNamesPanel._save(this.ObjArr);
				    	  win.hide();
				    	  this.batchNamesPanel=null;
				    	  this.grid.doQuery();
		    		  }
		    	 },{
		    		 text: '关闭',
		    		 scope : this,
		    		 handler: function() {
		    			 win.hide();
		    			 this.batchNamesPanel=null;
		    		 }						    	     
		    	 }]
		  });					    	
       },
   	_addFiberDp : function(){

	    var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要增加光分纤箱的接入点！');
	    	return;
	    }
	    if(records.length > 1){
	    	Ext.Msg.alert('温馨提示','只能选择一条数据！');
	    	return;
	    }
	    this.ObjArr = [];
	    for(var i=0;i<records.length;i++){
	    	var obj=records[i];
	    	if(Ext.isEmpty(obj)){
	    		Ext.Msg.alert('温馨提示','当前选中数据,不包含CUID字段,请检查配置');
	    		return;   //加上好像不起作用。是不是Ext.Msg.alert就是不运行下面的代码了呢？
	    	}
	    	this.ObjArr[i] = obj;
	    }
	    for(var j=0;j<records.length;j++){
	    	var labelCn=records[j].data['LABEL_CN'];
	    	if(Ext.isEmpty(labelCn)){
	    		Ext.Msg.alert('温馨提示','当前选中数据,不包含LABEL_CN字段,请检查配置');
	    		return;
	    	}						    	
	    }
	    if(this.batchNamesPanel==null){
	    	this.batchNamesPanel=new Frame.grid.edit.AccesspointAddFiberDpPanel({postfix:'号分纤箱',number:records.length});
	    }
	    var scope=this;
	    var win=WindowHelper.openExtWin(this.batchNamesPanel,{
	    	 title: '批量命名',
	    	 width: 300,
		     height:221,
	    	 buttons: [{
	    		  text: '确定',
	    		  scope:this,
	    		  handler: function(){
	    			  this.batchNamesPanel._save(this.ObjArr);
			    	  win.hide();
			    	  this.batchNamesPanel=null;
			    	  this.grid.doQuery();
	    		  }
	    	 },{
	    		 text: '关闭',
	    		 scope : this,
	    		 handler: function() {
	    			 win.hide();
	    			 this.batchNamesPanel=null;
	    		 }						    	     
	    	 }]
	  });					    	
   	},
	_jumpLinkView : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0){
	    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
	    	return;
	    }
		var cuid=records[0].data['CUID'];
		var jumpLinkMainView = new Topo.x.JumpLinkMainView('ACCESSPOINT',cuid);
		
		var dialog = new ht.widget.Dialog({
            title: "<html><font size=2>跳纤跳线管理</font></html>",
            width: 800,
            height: 400,
            titleAlign: "left", 
            draggable: true,
            closable: true,
            maximizable: true,
            resizeMode: "wh",
            /*
            buttons: [
                {
                    label: "确定"
                },
                {
                    label: "取消"
                }
            ],*/
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
		/*var jumpLinkPanel = new Frame.op.JumpLinkPanel({
			cuid : cuid,
			bmClassId : 'ACCESSPOINT'
		});
		
		var scope = this;
		var win = WindowHelper.openExtWin(jumpLinkPanel, {
			title : '跳纤管理',
			width : 800,
			height : 550,
			buttons : [{
				text : '关闭',
				scope : this,
				handler : function() {
					win.hide();
				}
			}]
		});*/
	},
	/**
	 * 查看关联线设施
	 */
	_relatedSystem : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0 || records.length>1){
	    	Ext.Msg.alert('温馨提示','请选择一条记录.');
	    	return;
	    }
		var cuid=records[0].data['CUID'];
		var labelcn=records[0].data['LABEL_CN'];
		var relatedSystemPanel = new Frame.com.RelatedSystemListPanelExt({
			cuid : cuid,
			labelcn : labelcn
		});
		
		var scope = this;
		var win = WindowHelper.openExtWin(relatedSystemPanel, {
			title : '查看关联线设施',
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
	addDistrict:function(){
		this.addForm = new GDTNMS.maintain.districts.form.AccessPointForm(this);
		var win = WindowHelper.openExtWin(this.addForm, {
			title:'新增资源点',
			width : "50%",
			buttons : [{
				text : '确定',
				iconCls : 'c_accept',
				scope : this,
				handler : function() {
					var scope = this;
					if(this.addForm.getForm().isValid()) {	
						var values = scope.addForm.getForm().getValues();
						Frame.MaskHelper.mask(this.addForm.getForm().getEl(),"数据新增中..."); 
						AccessPointDwrAction.add(values, function() {
							scope.grid.store.reload();
							Frame.MaskHelper.unmask(scope.addForm.getForm().getEl());
							win.hide();
						});
					}
				}
			},{
				text : '取消',
				iconCls : 'c_door_open',
				scope : this.editAttempTraphPanel,
				handler : function(){
					win.hide();
				}
			}],
			listeners : {
				scope : this,
				show : function() {
					this.addForm.doRefresh();
				}
			}
		});
	},
	edit : function() {
		var sels = this.grid.getSelectionModel().getSelections();
		var cuid = null;
		if(sels.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要修改的资源点！');
			return;
		}else if(sels.length > 1) {
			Ext.Msg.alert('温馨提示', '一次只能修改一个机房！');
			return;
		}else {
			cuid = sels[0].get('CUID');
		}
		this.editForm = new GDTNMS.maintain.districts.form.AccessPointForm(this);
		this.editForm.cuid = cuid;
		var win = WindowHelper.openExtWin(this.editForm, {
			title:'修改资源点',
			width : "50%",
			buttons : [{
				text : '确定',
				iconCls : 'c_accept',
				scope : this,
				handler : function() {
					var scope = this;
					if(this.editForm.getForm().isValid()) {
						var values = scope.editForm.getForm().getValues();
						Ext.apply(values, {CUID : cuid});
						Frame.MaskHelper.mask(this.editForm.getForm().getEl(),"数据更新中..."); 
						AccessPointDwrAction.update(values, function() {
							scope.grid.store.reload();
							Frame.MaskHelper.unmask(scope.editForm.getForm().getEl());
							win.hide();
						});
					}
				}
			},{
				text : '取消',
				iconCls : 'c_door_open',
				scope : this.editAttempTraphPanel,
				handler : function(){
					win.hide();
				}
			}],
			listeners : {
				scope : this,
				show : function() {
					this.editForm.doRefresh();
				}
			}
		});
	},
	
	del: function (){
		var sels = this.grid.getSelectionModel().getSelections();
		var cuid = null;
		if(sels.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要删除的资源点！');
			return;
		}else if(sels.length > 1) {
			Ext.Msg.alert('温馨提示', '一次只能 删除一个资源点！');
			return;
		}else {
			cuid = sels[0].get('CUID');
		}
		var scope = this;
		Ext.Msg.confirm("提示", "你确定要删除此记录吗?", function (btnId) {
            if (btnId == 'yes') {
            	 Frame.MaskHelper.mask(this.grid.getEl(),"数据删除中..."); 
            	AccessPointDwrAction.delDistrict(cuid, function() {            		
        			scope.grid.store.reload();
        			Frame.MaskHelper.unmask(scope.grid.getEl());
        		});
        		DWREngine.setAsync(true);
            }
        },this);
		
	},
	
	exportModel_allcols : function(){
//		var code	  = 'ACCESSPOINT';
//		var bmClassId = 'ACCESSPOINT';
//		var grid 	  = this.grid;
//		ExcelAction.getXmlInfor(code, function(colList){
//			ExcelAction.exportModel(grid.gridCfg, colList, bmClassId, function(results){
//				var time = results.seconds;
//				var files = results.files;
//				if(files && files.length > 0) {
//					var file = files[0];
//					var path = file.filePath;
//					Ext.Msg.alert("下载模板","导出完成。【<a href='" + ctx + "/download.do?file="+path+"&fileName=" + bmClassId + ".xls'>点击下载</a>】");
//				}
//			});
//		});
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("资源点导入"));
		window.open(url);
	},
	
	importData:function(){
		var winCfg={
		width:600,
		height:200
		};
		this.filePanel=new IRMS.dm.FilePanel({
		title: '导入',
		width: 100,
		height:20,
		inputParams : this.inputParams,
		key:'ACCESSPOINT'
		});
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg); 
	},
	exportDatas: function(){
		var bar = new IRMS.GridDataExport({
			bmclassId:'ACCESSPOINT',
			grid:this.grid
		});
		bar.scope.exportData();
	},
	
	exportModel : function(){
		var panel = new IRMS.ExportModel({
			grid  : this.grid,
			excel : '资源点',
			bmclassId:'ACCESSPOINT'
		});
		var win = WindowHelper.openExtWin(panel,{
			width : "50%",

			buttons : [{
				text : '确定',
				iconCls : 'c_accept',
				scope : this,
				handler : function() {
					var scope = this;		
					panel.exportExcel();
				}
			},{
				text : '取消',
				iconCls : 'c_door_open',
				scope : this,
				handler : function(){
					win.hide();
				}
			}]
		
		});
	},
	
	openExtWin : function(panel, winCfg, winParams) {
		var win ;
		var w = Ext.getBody().getWidth()-50;
		var h = Ext.getBody().getHeight()-50;
		if(Ext.isObject(winCfg)) {
			if(Ext.isEmpty(winCfg.width)) {
				winCfg.width = w;
			}
			if(Ext.isEmpty(winCfg.height)) {
				winCfg.height = h;
			}
		}else {
			winCfg = {
				width : w,
				height : h
			}
		}
		win = new Ext.Window(Ext.applyIf({
			closeAction: 'close',
			layout : 'fit',
			border : false,
			modal : winCfg.modal == false?false:true,
			items : [panel]
		}, winCfg));
		if(!Ext.isEmpty(winParams) && Ext.isObject(winParams)) {
			Ext.apply(win, winParams);
		}
		win.show();
		return win;
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
	/**
	 * 纤芯关联
	 */
	_joinLinkView : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0 || records.length > 1){
	    	Ext.Msg.alert('温馨提示','请选择一条记录.');
	    	return;
	    }
	    var cuid=records[0].data['CUID'];
		var name = records[0].data['LABEL_CN'];
		var fiberLinkPanel = new Frame.op.FiberLinksPanel({
			cuid : cuid,
			name : name,
			bmClassId : 'ACCESS_POINT'
		});//FiberLinksMainView.js
		
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
	/**
	 * 层间光缆列表
	 */
	_interwireView : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择一条记录.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单个接入点的层间光缆管理.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_INTERWIRE_APOINT&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
				labelCn + '层间光缆管理');
	}
});