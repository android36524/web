Ext.ns("Frame.grid.plugins.tbar");

$importcss(ctx + '/jslib/ext/ux/statusbar/css/statusbar.css');
$importjs(ctx + "/jslib/ext/ux/statusbar/StatusBar.js");

Frame.grid.plugins.tbar.DmGridDataExport = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		this.code = '';
		Frame.grid.plugins.tbar.DmGridDataExport.superclass.constructor.call(this);
	},
	exportData : function(code) {
		this.code = code;
		var scope = this;
		var sels = this.grid.grid.getSelectionModel().getSelections();
		
		if(!this.exportPanel) {
			this.exportPanel = new Ext.Panel({
				layout : 'fit',
				border : false,
				items : []
			});
			this.exportWin = new Ext.Window({
				title : '导出数据',
				closeAction: 'hide',
				layout : 'fit',
				frame : true,
				modal : true,
				width : 330,
				height : 80,
				items : [this.exportPanel],
				buttons : [{
					iconCls : 'c_page_excel',
					text : '导出',
					scope : this,
					handler : this.exportBySelected
				},{
					iconCls : 'c_door_in',
					text : '取消',
					scope : this,
					handler : function() {
						this.exportWin.hide();
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
			var statusBar = this.exportWin.getBottomToolbar();
			statusBar.clearStatus();
			statusBar.setStatus('选中数据：<font color="red">'+sels.length+'</font>条');
			this.exportWin.setHeight(80);
		}
		this.exportPanel.sels = sels;
		this.exportWin.show();
		
		
	},
	
	exportBySelected : function(){
		var param = this.getGridCfgByCode(this.code);
		//获取需要导出的Grid cfg
		var cfg = Frame.grid.BaseGridPanel.initParamsByUrl(param);
		var gridcfg =  cfg.gridCfg;
		if(!gridcfg.queryParams) {
			gridcfg.queryParams = {};
		}
		if(!gridcfg.extParams) {
			gridcfg.extParams = {};
		}
		var gridMeta = '';
		DWREngine.setAsync(false);
		GridViewAction.getGridMeta(gridcfg,function(resultData){
			if(resultData){
				gridMeta = resultData;
			}
		});
		DWREngine.setAsync(true);
		var columns = gridMeta.columns;
		var cols = [];
		Ext.each(columns, function(column) {
			cols.push(column.dataIndex);
		});
		
		var sels = this.grid.grid.getSelectionModel().getSelections();
		var cuidList = [];
		var v = "";
		Ext.each(sels, function(sel){
			cuidList.push(sel.json.CUID);
			v += "'"+sel.json.CUID + "',";
		});
		v = v.substring(0, v.length-1);
		var item = {};
		item.key = "RELATED_SYSTEM_CUID";
		item.relation = "IN";
		item.type = "string";
		item.value = v;
		gridcfg.queryParams = {'CUID':item};
		
		if(cuidList.length > 0) {
			var statusBar = this.exportWin.getBottomToolbar();
			statusBar.showBusy();
			GridViewAction.exportGridData(gridcfg, cols, cuidList, function(results){
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
		}else {
			Ext.Msg.alert('温馨提示', '请先选择要导出的记录！');
		}
		
	},
	
	getGridCfgByCode : function(params){
		var pluginsStack = {};
		var param = UrlHelper.getUrlObj(params);
		if(param && !Ext.isEmpty(param.code)) {
			var plugins = [];
			var pluginPath = '/cmp_plugins/grid';
			var dictMeta, dictRoot, code;
			if(param.code.indexOf('.') != -1) {
				var s = param.code.split('.');
				dictRoot = s[0];
				code = s[1];
			}else {
				dictRoot = 'service_dict_maintain';
				code = param.code;
			}
			try{
				var serviceDictRoot = Frame.SERVICE_DICT_URL;
				if(Ext.isEmpty(serviceDictRoot)) {
					serviceDictRoot = ctx + '/service_dict';
				}
				dictMeta = SynDataHelper.load(serviceDictRoot+'/'+dictRoot+'.json');
			}catch(e){
				alert('获取服务目录发生异常：'+e+'，请检查配置是否正确！');
				return;
			}
			if(!Ext.isEmpty(dictMeta)) {
				var dict = dictMeta[code];
				if(Ext.isEmpty(dict)) {
					alert('未找到“code='+param.code+'”对应的服务目录！');
					return;
				}else {
					if(dict.gridwrap === true) {
						$importcss(ctx + '/jslib/ext/resources/css/gridwrap.css');
					}
					if(dict.pluginPath) {
						pluginPath = dict.pluginPath;
					}
					Ext.applyIf(param, dict);
				}
			}else {
				return;
			}
		}
		return param;
	}
});