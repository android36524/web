Ext.ns('Frame.wire');
$importjs(ctx+'/rms/dm/common/dm-base.js');
$importjs(ctx+'/rms/dm/ductseg/LayingWireListPanelHT.js');
//光缆（段）
Frame.wire.LayingWireListPanelExt = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.LayingWireListPanelExt.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.wire.LayingWireListPanelExt.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.LayingWireListPanelExt.superclass.afterRender.call(this);
		try{
			var layingWireListHT = new createLayingWireListPanelHT({LABEL_CN:this.labelcn,CUID:this.cuid});
			var jv = layingWireListHT.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.cuid);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

