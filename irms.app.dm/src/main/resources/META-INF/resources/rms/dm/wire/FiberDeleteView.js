Ext.ns('Frame.wire');
//光缆（段）
Frame.wire.FiberDeletePanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.FiberDeletePanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
		this.grid = config.grid;
	},
	initComponent : function() {
		Frame.wire.FiberDeletePanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.FiberDeletePanel.superclass.afterRender.call(this);
		try{
			var fiberDeletePanel = new dms.deleteResourcePanel("纤芯删除操作","删除纤芯及其承载的光纤？",this.cuids,this);
			var jv = fiberDeletePanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.cuid);
			div.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

