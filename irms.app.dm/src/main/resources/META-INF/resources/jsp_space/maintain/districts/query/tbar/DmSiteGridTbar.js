Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx + '/dwr/interface/GetServiceParamAction.js');
$importjs(ctx + "/rms/common/DmFilePanel.js");
$importjs(ctx + "/rms/common/jumpFiberListPanel.js");

Frame.grid.plugins.tbar.DmSiteGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.DmSiteGridTbar.superclass.constructor.call(this);
		return [ '-', {
			text : '定位',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._doMapLocate
		},'-', {
			text : '站点剖面图',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._method1
		},'-',{
			text : '图片管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._uploadAttachment
		},'-',{
			text : '跳纤列表',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._jumpFiberList
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
	_method1 : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择要一条记录.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var url = ctx
		+ "/topo/index.do?code=SiteSectionTopo&resId="
		+ cuid + "&resType=SITE&clientType=html5";
		FrameHelper.openUrl(url,'站点剖面图-'+records[0].data['LABEL_CN']);
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