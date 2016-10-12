Ext.ns('Frame.wire');
$importjs(ctx+'/rms/dm/common/dm-base.js');
$importjs(ctx + "/rms/dm/fiber/LayingFiberListPanelExtHt.js");

//光缆成端割接选择
Frame.wire.LayingFiberListPanelExt = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.wire.LayingFiberListPanelExt.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.wire.LayingFiberListPanelExt.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.wire.LayingFiberListPanelExt.superclass.afterRender.call(this);
		try{
			var fiberList = new dms.Default.createFiberListPanelHT(this.cuid,this.labelcn,this);
			var jv = fiberList.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.cuid);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});