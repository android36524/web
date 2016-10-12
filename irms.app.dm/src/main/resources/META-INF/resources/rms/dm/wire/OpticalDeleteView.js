Ext.ns('Frame.wire');
//光缆（段）
Frame.wire.OpticalDeletePanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.OpticalDeletePanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.OpticalDeletePanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.OpticalDeletePanel.superclass.afterRender.call(this);
		try{
			var opticalDeletePanel = new dms.deleteResourcePanel("光纤删除操作","删除光纤？",this.cuids,this);
			var jv = opticalDeletePanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.cuid);
			div.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

