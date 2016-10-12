Ext.ns('Frame.grid');

$importjs(ctx+'/rms/dm/plugins/tbar/DMSystemGridTbar.js');
$importjs(ctx+'/rms/dm/plugins/event/DMSystemGridEvent.js');

Frame.grid.TreeEditorGrid = Ext.extend(Frame.grid.MaintainGridPanel,{
	hasMaintan:true,
	isTree:true,
	constructor : function(config){
		Frame.grid.TreeEditorGrid.superclass.constructor.call(this,config);
	},
	initComponent : function(){
		this.gridConfig = this.getGridConfig();
		this.editorId='IRMS.RMS.DUCT_BRANCH';
		Frame.grid.TreeEditorGrid.superclass.initComponent.call(this);
		var scope = this;
		this._doQuery = function(){
			this.grid.reloadNode(null);
		};
	},
	getGridConfig : function() {
		var gridConfig = {
				gridCfg:{},
				type:this.type,
				key:this.key,
				cuid:this.cuid,
				objectId:this.objectId
		};
		gridConfig.tbarPluginKeys = ['DMSystemGridTbar'];
		gridConfig.eventPluginKeys=['DMSystemGridEvent'];
		gridConfig.gridCfg.boName = 'DuctTreeGridBO';
		gridConfig.treeLevelBoName = this.treeLevelBoName;
		Ext.applyIf(gridConfig, this.inputParam);
		return gridConfig;
	}
});