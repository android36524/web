Ext.ns('Frame.op');
$importjs(ctx + "/rms/dm/fiberbox/JumpLinkMainViewHt.js"); 
$importjs(ctx+'/dwr/interface/FiberManagerAction.js');

//设备内跳纤 ext Panel
Frame.op.JumpLinkPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = "JL_" + config.cuid+ new Date().getTime();
		Frame.op.JumpLinkPanel.superclass.constructor.call(this,
				config);
		this.bmClassId = config.bmClassId;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.op.JumpLinkPanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.op.JumpLinkPanel.superclass.afterRender.call(this);
		try{
			var jumpLinkMainView = new JumpLinkMainView(this.bmClassId,this.cuid);
			var jv = jumpLinkMainView.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.id);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',"请使用Chrome浏览器或安装IE Chome插件，谢谢！"); 
		}
	}
	
});