Frame.grid.ResEditorGridPanel = Ext.extend(Frame.grid.MaintainGridPanel,{
	constructor : function(config){
		Frame.grid.ResEditorGridPanel.superclass.constructor.call(this,config);
	},
	
	initComponent : function(){
		Frame.grid.ResEditorGridPanel.superclass.initComponent.call(this);
		var scope = this;
		this.maintainPanel.changeBatchMeta=function(){
			var editorMeta = {
					cuid : 'IRMS.RMS.FIBER_INTER'
				};
			var scope = this;
			DWREngine.setAsync(false);
			EditorPanelAction.getEditorMeta(editorMeta,function(result){
				if(result) {
					scope.propertyMeta = result;
				}
			});
			DWREngine.setAsync(true);
			
			this.batchEdit=true;
			var editorColumnMetas=[]; 
			this.sinStore=this.getSource();
			for(var i=0;i<this.propertyMeta.editorColumnMetas.length;i++){
				if(this.propertyMeta.editorColumnMetas[i].cuid!='WIRE_NO'){
					editorColumnMetas.push(this.propertyMeta.editorColumnMetas[i]);
				}else{
					var no={
							categoryName: "系统属性",
							code: null,
							cuid: "NO_BATCH",
							customEditor: null,
							customRender: null,
							editable: true,
							group: "系统属性",
							hidden: false,
							labelCn: "数量",
							name: "数量",
							nullable: true,
							type: "long",
							value: '1',
							width: 100,
							xtype: "long"
					};
					editorColumnMetas.push(no);
				}
			}
			this.propertyMeta.editorColumnMetas=editorColumnMetas;
			this._buildCustomEdit();
			this._buildCustomRender();
			var array = new Array();
			if(this.parentNodeId != undefined){
				this.parentNodeId = this.parentNodeId.split('@')[0];
				if(this.CUID != undefined){
					this.parentNodeId +="@"+this.CUID;	
				}
			}
			array.push(this.getDataBySource());
			this.refreshData(array);
		};
		this.maintainPanel.changeSignalMeta=function(){
			this.batchEdit=false;
			var editorColumnMetas=[];  
			for(var i=0;i<this.propertyMeta.editorColumnMetas.length;i++){
				if(this.propertyMeta.editorColumnMetas[i].cuid!='NO_BATCH'){
					editorColumnMetas.push(this.propertyMeta.editorColumnMetas[i]);
				}else{
					var labelCn={
							categoryName: "系统属性",
							code: null,
							cuid: "WIRE_NO",
							customEditor: null,
							customRender: null,
							editable: true,
							group: "系统属性",
							hidden: false,
							labelCn: "编号",
							name: "labelCn",
							nullable: true,
							type: "string",
							value: undefined,
							width: 100,
							xtype: "string"
					};
					editorColumnMetas.push(labelCn);
				}
			}
			this.propertyMeta.editorColumnMetas=editorColumnMetas;
			this._buildCustomEdit();
			this._buildCustomRender();
			this.setSource(this.sinStore);
		};
	}
	
});