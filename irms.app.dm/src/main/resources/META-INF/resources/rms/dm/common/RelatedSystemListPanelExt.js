Ext.ns('Frame.com');
$importjs(ctx+'/rms/dm/common/dm-base.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelHT.js');

Frame.com.RelatedSystemListPanelExt = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = "SL"+config.cuid;
		Frame.com.RelatedSystemListPanelExt.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.com.RelatedSystemListPanelExt.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.com.RelatedSystemListPanelExt.superclass.afterRender.call(this);
//		try{
			var param = {
					LABEL_CN:this.labelcn,
					CUID:this.cuid
			};
			var relatedSystemListHT = new dms.createRelatedSystemListPanel(param);
			var jv = relatedSystemListHT.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.id);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
//		}catch(e){
//			Ext.Msg.alert('错误提示信息：',e); 
//		}
	}
});

