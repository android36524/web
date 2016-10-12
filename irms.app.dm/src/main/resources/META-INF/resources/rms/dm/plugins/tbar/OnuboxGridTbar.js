Ext.ns('Frame.grid.plugins.tbar');
/*
 * 光交接箱管理
 */
$importjs(ctx+'/map/map-inc.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelExt.js');
$importjs(ctx + "/rms/dm/fiberbox/JumpLinkMainView.js");
$importjs(ctx + "/rms/dm/fiberbox/FiberLinkMainView.js");
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx + "/map/dms-map-cabPortFusion.js");
$importjs(ctx + "/rms/dm/wire/FibercabPortFusionView.js");
$importjs(ctx + "/rms/common/DmFilePanel.js");
$importjs(ctx + "/rms/dm/fiberbox/FiberLinkMainView.js");

Frame.grid.plugins.tbar.OnuboxGridTbar = Ext.extend(Object,{
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.OnuboxGridTbar.superclass.constructor.call(this);
		return [ '-',{
			text : '纤芯关联',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this._joinLinkView
		}, '-', {
			xtype : 'splitbutton',
			text : '导入',
			iconCls : 'c_page_white_link',
			scope : this,
			menu : [{
				text : 'ONU综合箱导入',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._import
			},{
				text : '模板下载',
				iconCls : 'c_page_white_link',
				scope : this,
				handler : this._export
			}]
		}];
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
	    	 key:'Onubox'
	  });
		var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
	},
	_export:function(){
		var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("ONU综合箱"));
		window.open(url);
	},
	_joinLinkView:function()
	{
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
	}
});