Ext.ns('Frame.wire');
//光缆（段）
Frame.wire.PointWireRemainPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.PointWireRemainPanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.PointWireRemainPanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.PointWireRemainPanel.superclass.afterRender.call(this);
		try{
			var pointWireRemainPanel = new dms.queryWireRemain(this.cuid,this);
			var jv = pointWireRemainPanel.getView();
			jv.className = 'graphView';
			var div = document.getElementById(this.cuid);
			div.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});

