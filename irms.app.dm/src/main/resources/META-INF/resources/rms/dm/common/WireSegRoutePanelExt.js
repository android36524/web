Ext.ns('Frame.com');
$importjs(ctx+'/rms/dm/common/dm-base.js');
$importjs(ctx+'/rms/dm/common/WireSegRoutePanelHT.js');

Frame.com.WireSegRoutePanelExt = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.com.WireSegRoutePanelExt.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.com.WireSegRoutePanelExt.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.com.WireSegRoutePanelExt.superclass.afterRender.call(this);
		try{
			var wireSegRouteHT = new createWireSegPanelHT({
				LABEL_CN:this.labelcn,
				CUID:this.cuid
			});
			var jv = wireSegRouteHT.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.cuid);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

