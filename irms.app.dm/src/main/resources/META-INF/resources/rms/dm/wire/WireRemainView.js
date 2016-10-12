Ext.ns('Frame.wire');
$importjs(ctx+'/rms/dm/wire/WireRemainViewHt.js');
//光缆（段）
Frame.wire.WireRemainPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid + new Date().getTime();
		Frame.wire.WireRemainPanel.superclass.constructor.call(this,config);
		this.id = config.id;
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.wire.WireRemainPanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.WireRemainPanel.superclass.afterRender.call(this);
		try{
			var wireRemainMainView = new createWireRemainPanel({LABEL_CN:this.labelcn,CUID:this.cuid});
			var jv = wireRemainMainView.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.id);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

function lock(panel){
	panel.mask = document.createElement("div");
	panel.mask.className = 'mask';
	panel.mask.innerHTML = "<div><img src='"+ctx+"/resources/icons/loading.gif'></div>";
	panel.getView().appendChild(panel.mask);
};
function unlock(panel){
	if(panel.mask)
	{
		panel.getView().removeChild(panel.mask);
		panel.mask = null;
	}
};
