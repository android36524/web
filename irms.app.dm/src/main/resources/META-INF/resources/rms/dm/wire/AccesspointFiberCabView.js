Ext.ns('Frame.wire');
//光缆（段）
Frame.wire.AccessPointShowFiberCabPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.AccessPointShowFiberCabPanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
		this.cuids = config.cuids;
	},
	initComponent : function() {
		Frame.wire.AccessPointShowFiberCabPanel.superclass.initComponent.call(this);
		this.on("render",function(){
			Frame.wire.AccessPointShowFiberCabPanel.superclass.afterRender.call(this);
			try{
				var accesspointFiberCabPanel = new dms.accesspointFiberCab(this.cuid,this);
				var jv = accesspointFiberCabPanel.getView();
				jv.className = 'graphView';
				var div = document.getElementById(this.cuid);
				div.firstChild.firstChild.appendChild(jv);
			}catch(e){
				Ext.Msg.alert('错误提示信息：',e); 
			}
		});
		
	}
//	afterRender: function(){
//		Frame.wire.AccessPointShowFboxPanel.superclass.afterRender.call(this);
//		try{
//			var accesspointFboxPanel = new dms.accesspointFbox(this.cuid,this);
//			var jv = accesspointFboxPanel.getView();
//			jv.className = 'graphView';
//			var div = document.getElementById(this.cuid);
//			div.firstChild.firstChild.appendChild(jv);
//		}catch(e){
//			Ext.Msg.alert('错误提示信息：',e); 
//		}
//	}
});

