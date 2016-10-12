Ext.ns('Frame.wire');
//光交接箱端子直熔
Frame.wire.FibercabPortFusionPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = "FC_" + config.cuid;
		Frame.wire.FibercabPortFusionPanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.FibercabPortFusionPanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.FibercabPortFusionPanel.superclass.afterRender.call(this);
//		try{
			var cabPortFusionPanel = new dms.cabPortFusion(this.cuid,this);
			var jv = cabPortFusionPanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.id);
			div.firstChild.firstChild.appendChild(jv);
//		}catch(e){
//			Ext.Msg.alert('错误提示信息：',e); 
//		}
	}
});

