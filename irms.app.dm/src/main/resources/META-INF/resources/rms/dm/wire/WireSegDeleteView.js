Ext.ns('Frame.wire');
//光缆（段）
Frame.wire.WireSegDeletePanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.WireSegDeletePanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.WireSegDeletePanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.WireSegDeletePanel.superclass.afterRender.call(this);
		try{
			var wireSegDeletePanel = new dms.deleteResourcePanel("光缆段删除操作","解除敷设关系、删除光缆段、纤芯及其承载的光纤？",this.cuids,this);
			var jv = wireSegDeletePanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.cuid);
			div.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

