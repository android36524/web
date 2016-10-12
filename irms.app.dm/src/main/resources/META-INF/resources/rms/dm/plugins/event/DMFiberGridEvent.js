Ext.ns("Frame.grid.plugins.event");

Frame.grid.plugins.event.DMFiberGridEvent = Ext.extend(Object, {
	_treeLevelBoNameCfg: {},
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.event.DMFiberGridEvent.superclass.constructor.call(this);
		return {
			scope : this,
			rowdblclick : this.onRowDblClick,
			click:this.onRowclick
		};
	},
	onRowDblClick : function(grid, rowIndex, e) {
	},
	onRowclick	:	function(node, e){
		
		if(!node){
			return;
		}
		this.grid.maintainPanel.setCanUpdate();
		if(node.attributes.className == 'WIRE_SEG'){
			this.grid.maintainPanel.modifyButton.disable();
			this.grid.maintainPanel.removeButton.disable();
			this.grid.maintainPanel.addBatch.enable();
		}else{
			this.grid.maintainPanel.modifyButton.enable();
			this.grid.maintainPanel.removeButton.enable();
			this.grid.maintainPanel.addBatch.disable();
		}
		var level = node.getDepth()-1;
		var classname = this.grid.gridConfig.treeLevelBoName[level];

		if (this._treeLevelBoNameCfg && this._treeLevelBoNameCfg[classname]) {
			this.grid.maintainPanel.propertyMeta = this._treeLevelBoNameCfg[classname];
		}else {
			var editorMeta = {
					cuid : classname
				};
			var scope = this;
			DWREngine.setAsync(false);
			EditorPanelAction.getEditorMeta(editorMeta,function(result){
				if(result) {
					scope.grid.maintainPanel.propertyMeta = result;
					scope._treeLevelBoNameCfg[classname] = result;
				}
			});
			DWREngine.setAsync(true);
			//this.grid.maintainPanel.initMeta(classname);
		}
		this.grid.maintainPanel._buildCustomEdit();
		this.grid.maintainPanel._buildCustomRender();
		this.grid.maintainPanel.refreshGridData(node.attributes,node.attributes);
	}
});