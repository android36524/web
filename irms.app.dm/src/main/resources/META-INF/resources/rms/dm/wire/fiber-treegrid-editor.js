Ext.ns('DM.fiber');

$importjs(ctx+'/rms/dm/plugins/tbar/FiberGridTbar.js');
$importjs(ctx+'/rms/dm/plugins/event/DMFiberGridEvent.js');

DM.fiber.TreeEditorGrid = Ext.extend(Frame.grid.MaintainGridPanel,{
	hasMaintan:true,
	isTree:true,
	constructor : function(config){
		DM.fiber.TreeEditorGrid.superclass.constructor.call(this,config);
	},
	initComponent : function(){
		//this.grid.selModel = new Ext.grid.CheckSelectionModel();
		this.gridConfig = this.getGridConfig();
		this.editorId='IRMS.RMS.WIRE_SEG';
		DM.fiber.TreeEditorGrid.superclass.initComponent.call(this);
		var scope = this;
		this.grid.singleSelect = false;
		var selectedWireSeg = new Array();
		this._doQuery = function(){
			this.grid.reloadNode(null);
		},
		this.maintainPanel.changeBatchMeta=function(){
			var wireseg = this.getDataBySource();
			wireseg.ORIG_POINT_CUID = null;
			wireseg.DEST_POINT_CUID = null;
			wireseg.SIGNAL_DIRECTION = {CUID:1,LABEL_CN:'无'};
			var editorMeta = {
					cuid : 'IRMS.RMS.FIBER'
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

			if(this.parentNodeId != undefined){
				this.parentNodeId = this.parentNodeId.split('@')[0];
				if(this.CUID != undefined){
					this.parentNodeId +="@"+this.CUID;	
				}
			}
			var array = new Array();
			array.push(wireseg);
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
			var array = new Array();
			this.refreshData(array);
			scope.maintainPanel.modifyButton.disable();
			scope.maintainPanel.removeButton.disable();
			scope.maintainPanel.addBatch.disable();
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
		if(dms.designer){
			gridConfig.tbarPluginKeys = ['designerFiberGridTbar'];
		}else{
			gridConfig.tbarPluginKeys = ['FiberGridTbar'];
		}
		gridConfig.eventPluginKeys=['DMFiberGridEvent'];
		gridConfig.gridCfg.boName = 'FiberTreeGridBO';
		gridConfig.treeLevelBoName = this.treeLevelBoName;
		Ext.applyIf(gridConfig, this.inputParam);
		return gridConfig;
	},	
	_buildTreeGridPanel:function(){
		this.grid = new Frame.tree.AsynTreeGridPanel({
			region : 'center',
			border : false,
			frame : false,
			autoScroll : false,
			expanded : false,
			autoLoad : true,
			singleSelect : false,
			checkVisible : false,
			multSel:false,
			header : false,
			checkModel: 'single',
			onlyLeafCheckable:true,
			gridConfig:this.gridConfig,
			tbarPluginKeys:this.gridConfig.tbarPluginKeys,
			eventPluginKeys:this.gridConfig.eventPluginKeys,
			maintainPanel:this.maintainPanel,
			columns : [{
				dataIndex : 'text',
				header : '全部光缆段',
				width : 200
			},{
				dataIndex : 'WIRE_NO',
				header : '编号',
				width : 60
			},{
				dataIndex : 'SIGNAL_DIRECTION',
				header : '信号方向',
				width : 80
			},{
				dataIndex : 'SUM_ATTENU_1310',
				header : '1310总纤芯衰耗值(dB)',
				width : 160
			},{
				dataIndex : 'SUM_ATTENU_1550',
				header : '1550总纤芯衰耗值(dB)',
				width : 160
			},{
				dataIndex : 'AVE_ATTENU_1310',
				header : '1310平均衰耗值(dB)',
				width : 160
			},{
				dataIndex : 'AVE_ATTENU_1550',
				header : '1550平均衰耗值(dB)',
				width : 160
			},{
				dataIndex : 'LENGTH',
				header : '长度(M)',
				width : 80
			},{
				dataIndex : 'ORIG_POINT_CUID',
				header : 'A端端子',
				width : 120
			},{
				dataIndex : 'DEST_POINT_CUID',
				header : 'Z端端子',
				width : 120
			},{
				dataIndex : 'SPECTRUM',
				header : '纤芯色散',
				width : 80
			},{
				dataIndex : 'FIBER_LEVEL',
				header : '纤芯级别',
				width : 80
			},{
				dataIndex : 'OWNERSHIP',
				header : '产权',
				width : 60
			},{
				dataIndex : 'MAINT_MODE',
				header : '维护方式',
				width : 100
			},{
				dataIndex : 'PURPOSE',
				header : '用途',
				width : 60
			},{
				dataIndex : 'USAGE_STATE',
				header : '使用状态',
				width : 80
			},{
				dataIndex : 'BUILDER',
				header : '施工单位',
				width : 100
			}],
			root : {
				id : 'root',
				cuid : 'root',
				text : 'tree',
				expanded : true,
				boName : this.gridConfig.gridCfg.boName,
				treeName : 'FIBER',
				params : {
					pageCount : -1,
					templateIds : 'ROLE_ROLESELECT_LV0',
					cuid : this.cuid
				},
				treeParams : {
					relId : this.relId,
					user : '',
					relation : this.relation,
					district : this.district
				},
				queryParams : {
				}
			},
			getSelesionNodes : function() {
				var nodeList = [];
				var node = this.getSelectionModel().getSelectedNode();
				    if (node.isLeaf()) {
						nodeList.push(node);
				    }else{
				    	msg='请选择一个叶子节点的施工队！';
				    	Ext.Msg.alert('温馨提示', msg);
				    }
				return nodeList;
			}
		});
		
		this.items.push(this.grid);
	}
});