Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx + "/map/dms-map-wireRemain.js");
$importjs(ctx + "/rms/dm/wire/PointWireRemainView.js");
$importjs(ctx + "/rms/common/DmFilePanel.js");

Frame.grid.plugins.tbar.PoleGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.PoleGridTbar.superclass.constructor.call(this);
		return [ '-', {
			text : '定位',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._doMapLocate
		},'-', {
			text : '批量命名',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._batchNames
		} ,'-',{
			text : '查看关联线设施',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._relatedSystem
		} ,'-',{
			text : '预留信息',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this.queryPointWireMain
		} ,'-',{
			xtype : 'splitbutton',
			text : '导入',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : '电杆导入',
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
			text : '图片管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._uploadAttachment
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
				return; // 加上好像不起作用。是不是Ext.Msg.alert就是不运行下面的代码了呢？
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
			this.batchNamesPanel = new Frame.grid.edit.BatchNamesPanel({postfix:'号杆'});
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
					this.batchNamesPanel._save(this.ObjArr);
					win.hide();
					this.batchNamesPanel=null;
					this.grid.doQuery();
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
	    	 key:'POLE'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	_export:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("电杆导入"));
		window.open(url);
	},
	/**
	 * 查看关联线设施
	 */
	_relatedSystem : function(){
		var records=this.grid.getSelectionModel().getSelections();
	    if(Ext.isEmpty(records) || records.length==0 || records.length>1){
	    	Ext.Msg.alert('温馨提示','请选择一条杆路记录.');
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
	queryPointWireMain : function() {
		var records = this.getSelectionModel().getSelected();
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
	    
		var pointWireRemainPanel = new Frame.wire.PointWireRemainPanel({
			cuid : cuid,
			labelcn : labelcn,
			cuids : cuids
		});

		var win = WindowHelper.openExtWin(pointWireRemainPanel, {
			title : labelcn + '>>>光缆预留信息',
			width : 400,
			height : 300
		});
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
	}
});