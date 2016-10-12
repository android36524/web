Ext.ns('Frame.wire');
$importjs(ctx+'/rms/dm/common/dm-base.js');
$importjs(ctx + "/rms/dm/fiber/CutOverRouteManageHt.js");

//中间光缆割接选择
Frame.wire.CutOverRouteManageExt = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.CutOverRouteManageExt.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.wire.CutOverRouteManageExt.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.CutOverRouteManageExt.superclass.afterRender.call(this);
		try{
			var fiberList = new dms.Default.CutOverRouteManageHt(this.cuid,this);
			var jv = fiberList.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.cuid);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});