Ext.ns('Frame.wire');
Frame.wire.WireSystemDeletePanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.WireSystemDeletePanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.WireSystemDeletePanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.WireSystemDeletePanel.superclass.afterRender.call(this);
		try{
			var wireSystemDeletePanel = new dms.deleteResourcePanel("光缆删除操作","解除敷设关系、删除光缆、纤芯及其承载的光纤？",this.cuids,this);
			var jv = wireSystemDeletePanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.cuid);
			div.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

