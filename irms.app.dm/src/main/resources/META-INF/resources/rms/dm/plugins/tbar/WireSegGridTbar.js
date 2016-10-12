Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/map/dms-map-deleteResource.js');
$importjs(ctx + '/rms/dm/wire/WireSegDeleteView.js');
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx+'/jslib/tp/topo-all.js');
$importjs(ctx+'/jslib/tp/tp-form.js');
$importjs(ctx+'/map/dms-tools.js');
$importjs(ctx+'/map/dms-utils.js');
$importjs(ctx+'/map/dms-wiretoductline.js');
$importjs(ctx+'/map/dms-sectionPic.js');
$importjs(ctx+'/map/dms-map-contextmenu.js');

$importjs(ctx+'/jslib/tp/component/gridpanel');
$importjs(ctx+'/jslib/tp/component/dialoggridpanel.js');
$importjs(ctx+'/map/dms-ductline.js');


/**
 * 
 */
Frame.grid.plugins.tbar.WireSegGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.WireSegGridTbar.superclass.constructor.call(this);
		this.grid.on('click',function(node){
			var records=this.grid.getSelectionModel().getSelections();
			Ext.each(this.changeButtonArray,function(button){
				if(Ext.isEmpty(records) || records.length==0 || records.length>1){
					button.disable();
			    }else{
			    	button.enable();
			    }
			});
		},this);
		
		var wireSegRouteButton = new Ext.Button({
			text : '具体路由',
			disabled : true,
			iconCls : 'c_page_white_magnify',
			scope : this,
			handler : this.wireSegRouteManage
		});
		
		var deleteBtn = new Ext.Button({
			text : '删除',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this.deleteWireSeg
		});
		this.changeButtonArray = new Array();
		this.changeButtonArray.push(wireSegRouteButton);
		return [
		        wireSegRouteButton,
		        '-',
		        deleteBtn
		];
	},
	
	wireSegRouteManage : function(){
		var records=this.grid.getSelectionModel().getSelections();
		var cuid=records[0].data['CUID'];
		var dialog = new ht.widget.Dialog(); 
		var labelcn=records[0].data['LABEL_CN'];
		
		dms.Tools.wireToLayDuctLine(cuid);
		
		/*var wireSegRoutePanel = new Frame.com.WireSegRoutePanelExt({
			cuid : cuid,
			labelcn : labelcn
		});
		Dms.Default.tpmap.reset();
		var scope = this;
		var win = WindowHelper.openExtWin(wireSegRoutePanel, {
			   title : '光缆段具体路由管理',
			 
		});
		dialog=win;*/
	
	},
	deleteWireSeg : function() {
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
	    
		var deleteWireSegPanel = new Frame.wire.WireSegDeletePanel({
			cuid : cuid,
			labelcn : labelcn,
			cuids : cuids
		});

		var win = WindowHelper.openExtWin(deleteWireSegPanel, {
			title : '光缆段删除操作',
			width : window.screen.availWidth*0.5,
			height : window.screen.availHeight*0.5
		});
		deleteWireSegPanel._win = function(){
			win.hide();
			scope.store.reload();
			return true;
		};
	}
});
