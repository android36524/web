Ext.ns('DM.projectManagement');
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProManhleGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProPoleGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProStoneGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProInflexionGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProFiberCabGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProFiberDpGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProFiberJointBoxGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProWireSegGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProDuctSegGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProStoneWaySegGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProPoleWaySegGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProUpLineSegGridTbar.js");
$importjs(ctx + "/rms/dm/plugins/tbar/project/ProHangWallSegGridTbar.js");


DM.projectManagement = Ext.extend(Ext.Panel, {
	border : false,
	layout: 'border',
	bodyCfg: {
    	cls: 'x-panel-mc'
    },
	constructor : function(config) {
		DM.projectManagement.superclass.constructor.call(this,config);
	},
	
	initComponent : function() {
		var param = getGridCfgByCode();
		this.gridConfig = Frame.grid.BaseGridPanel.initParamsByUrl(param);

		this.items = [];
		this.items.push(this._buildProjectInfoPanel());
		this.items.push(this._buildProjectTabsPanel());
		DM.projectManagement.superclass.initComponent.call(this);
	},
	_buildProjectInfoPanel : function() {
		var panel = new Ext.form.FormPanel({
			border: false,
			region: 'north',
			height: 45,
			bodyStyle:'padding:10px',
			labelWidth: 70,
			items : [{
				xtype: 'textfield',
				readOnly: true,
				width: 300,
				fieldLabel: '工程名称',
				value: this.labelCn
			}]
		});
		return panel;
	},
	_buildProjectTabsPanel : function(){
		var tabpanel = new Ext.TabPanel({
			region: 'center'
		});
		var tabsCfg = [
		     {
		    	 id : 'IRMS.RMS.MANHLE',
		    	 text: '人手井',
		    	 tbar: 'ProManhleGridTbar'
		     },{
		    	 id : 'IRMS.RMS.POLE',
		    	 text: '电杆',
		    	 tbar: 'ProPoleGridTbar'
		     },{
		    	 id : 'IRMS.RMS.STONE',
		    	 text: '标石',
		    	 tbar: 'ProStoneGridTbar'
		     },{
		    	 id: 'IRMS.RMS.INFLEXION',
		    	 text: '拐点',
		    	 tbar: 'ProInflexionGridTbar',
		     },{
		    	 id: 'IRMS.RMS.FIBER_CAB',
		    	 text: '光交接箱',
		    	 tbar: 'ProFiberCabGridTbar'
		     },{
		    	 id: 'IRMS.RMS.FIBER_DP',
		    	 text: '光分纤箱',
		    	 tbar: 'ProFiberDpGridTbar'
		     },{
		    	 id: 'IRMS.RMS.FIBERJOINTBOX',
		    	 text: '光接头盒',
		    	 tbar: 'ProFiberJointBoxGridTbar',
		     },{
		    	 id: 'IRMS.RMS.WIRE_SEG',
		    	 text: '光缆段',
		    	 tbar: 'ProWireSegGridTbar'
		     },{
		    	 id: 'IRMS.RMS.DUCT_SEG',
		    	 text: '管道段',
		    	 tbar: 'ProDuctSegGridTbar',		    		 
		     },{
		    	 id: 'IRMS.RMS.STONEWAY_SEG',
		    	 text: '标石路由段',
		    	 tbar: 'ProStoneWaySegGridTbar'		    		 
		     },{
		    	 id: 'IRMS.RMS.POLEWAY_SEG',
		    	 text: '杆路段',
		    	 tbar: 'ProPoleWaySegGridTbar'
		     },{
		    	 id: 'IRMS.RMS.UPLINESEG',
		    	 text: '引上段',
		    	 tbar: 'ProUpLineSegGridTbar'
		     },{
		    	 id: 'IRMS.RMS.HANG_WALL_SEG',
		    	 text: '挂墙段',
		    	 tbar: 'ProHangWallSegGridTbar'
		     }
		     
		];
		for (var i=0; i<tabsCfg.length; i++) {
			tabpanel.add(this._createGridPanel(tabsCfg[i]));
		}
		tabpanel.setActiveTab(0);
		return tabpanel;
	},
	_createGridPanel : function(param) {
		var scope = this;
		var id = param.id;
		var gridCfg = {
				id: id,
				title: param.text,
				closeable: false,
				tbarPluginKeys: param.tbar,
				gridCfg : {
				boName: 'ProjectManagementTemplateProxyBO',
				cfgParams: {
					templateId : id,
					cuid: scope.cuid
				},
				queryParams: {
					CUID : {
						key: 'CUID',
						value: scope.cuid
					}	
				}
			}
		};
		var grid = new Frame.grid.DataGridPanel(Ext.applyIf({
			disableSystemBar : true
		}, gridCfg));
		
		
		grid.on('metaloaded', function() {
			grid.doQuery();
		});
		return grid;
	}
});