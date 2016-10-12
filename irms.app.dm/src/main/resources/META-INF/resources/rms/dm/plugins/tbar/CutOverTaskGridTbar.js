Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx + '/map/map-inc.js');
$importjs(ctx+'/jslib/tp/topo-all.js');
$importjs(ctx+'/jslib/tp/tp-form.js');
$importjs(ctx + "/jslib/jquery/jquery-1.7.1.min.js");
$importjs(ctx + '/rms/common/FilePanel.js');
$importjs(ctx + "/rms/dm/fiber/LayingFiberListPanelExt.js");
$importjs(ctx + "/rms/dm/fiber/CutOverPointSelectExt.js");
$importjs(ctx + "/rms/dm/fiber/CutOverRouteManageExt.js");
$importjs(ctx+'/dwr/interface/WireFinishCutAction.js');

Frame.grid.plugins.tbar.CutOverTaskGridTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.CutOverTaskGridTbar.superclass.constructor.call(this);
		


    return [ '-', {
			text : '修改路由信息',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._changeRouteManage
		}, '-', {
			text : '方案设计',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._doCutScheme
		}, '-', {
			text : '执行切割',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._doCutSchemeExecute
		}];
	},
	_changeRouteManage : function() {
		var records = this.getSelectionModel().getSelections();
		if (Ext.isEmpty(records) || records.length == 0) {
			Ext.Msg.alert('温馨提示', '请选择割接任务数据.');
			return;
		}
		if (records.length > 1) {
			Ext.Msg.alert('温馨提示', '目前只支持单条割接任务的具体路由.');
			return;
		}
		var cuid = records[0].data['CUID'];
		var labelCn = records[0].data['LABEL_CN'];
		var cutOverState = records[0].data['CUTOVER_STATE'].LABEL_CN;
		if(cutOverState == '割接成功'){
			Ext.Msg.alert('温馨提示', '割接成功的不能修改具体路由');
			return;
		}
		var RouteManagePanel = new Frame.wire.CutOverRouteManageExt(
				{
					cuid : cuid,
					labelcn : labelCn
				});
		var win = WindowHelper.openExtWin(RouteManagePanel, {
			title : '修改具体路由',
			width : window.screen.availWidth * 0.50,
			height : window.screen.availHeight * 0.5
		});
		RouteManagePanel._win = function(){
			win.hide();
			return true;
		};
	},
	_doCutScheme:function(){
		var records=this.getSelectionModel().getSelections();
		var scope = this;
	    if(Ext.isEmpty(records) || records.length==0||records.length > 1){
	    	Ext.Msg.alert('温馨提示','请选择一条割接任务。。。');
	    	return;
	    }
	    
	    var cuid = records[0].data['CUID'];
		var labelcn = records[0].data['LABEL_CN'];
		var cutOverType = records[0].data['CUTOVER_TYPE'].CUID;

		var scope = this;
		if(cutOverType == '1'){
			var layingFiberPanel = new Frame.wire.LayingFiberListPanelExt(
					{
						cuid : cuid,
						labelcn : labelcn
					});
			var win = WindowHelper.openExtWin(layingFiberPanel, {
				title : '光缆成端割接',
				width : window.screen.availWidth * 0.60,
				height : window.screen.availHeight * 0.5
			});
		}
		else{
			var cutOverState = records[0].data['CUTOVER_STATE'].LABEL_CN;
			if(cutOverState == '割接成功'){
				Ext.Msg.alert('温馨提示','执行割接成功的割接任务不能进行方案设计!!!!');
		    	return;
			}
			Ext.Msg.confirm("提示", "中间光缆段割接在设计时会重新生成所有的割接信息，\n如果要修改现有的割接方案的路由信息，请选择"+"\"修改具体路由\""+"按钮！\n确定要继续吗？", function (btnId) {
	            if (btnId == 'yes') {
	            	var cutOverPointPanel = new Frame.wire.CutOverPointSelectExt(
	    					{
	    						cuid : cuid,
	    						labelcn : labelcn
	    					});
	    			WindowHelper.openExtWin(cutOverPointPanel, {
	    				title : '中间光缆割接',
	    				width : window.screen.availWidth * 0.40,
	    				height : window.screen.availHeight * 0.5
	    			});
	            }
	        },this);
		}
	},
	_doCutSchemeExecute:function(){
		var records=this.getSelectionModel().getSelections();
		var scope = this;
	    if(Ext.isEmpty(records) || records.length==0||records.length > 1){
	    	Ext.Msg.alert('温馨提示','请选择一条割接任务。。。');
	    	return;
	    }
	    var cuid = records[0].data['CUID'];
	    $.ajax({
	    	url:ctx + "/rest/WireCutSchemeExecuteAction/doGetWireFinishCut/" + cuid + "?time=" + new Date().getTime(),
	    	success:function(data){
	    		var data = $.parseJSON(data);
	    		Ext.Msg.alert('温馨提示',data.s);
		    	return;
	    	},
	       dataType: 'text',
		   type: 'GET'
	    });
	}
});