Ext.ns('Frame.grid.plugins.tbar');
/**
 * 光缆隐患管理
 */
//$importjs(ctx + "/rms/dm/common/DistrictTaskOpticalPanel.js");
//$importjs(ctx + "/rms/dm/common/OpticalCheckReboot.js");

Frame.grid.plugins.tbar.WireTroubleGridTbar = Ext.extend(Object,
		{
			constructor : function(grid) {
				this.grid = grid;
				Frame.grid.plugins.tbar.WireTroubleGridTbar.superclass.constructor.call(this);
				return [ 
				{
//					text : '区域定时任务初始化',
//					iconCls : 'c_page_white_link',
//					scope : this,
//					handler : this.areaTaskInit
				} ,'-', {
//					text : '核查详细信息',
//					iconCls : 'c_page_white_link',
//					scope : this,
//					handler : this._investigate
				},'-', {
//					text : '重新启动',
//					iconCls : 'c_page_white_link',
//					scope : this,
//					handler : this.reStart
				}
				];
			},
//			_investigate : function() {
//				var scope = this;
//				var records = this.grid.getSelectionModel().getSelections();
//				if (Ext.isEmpty(records) || records.length == 0) {
//					Ext.Msg.alert('温馨提示', '请选择核查计划数据.');
//					return;
//				}
//				if (records.length > 1) {
//					Ext.Msg.alert('温馨提示', '目前只支持核查单条详细信息.');
//					return;
//				}
//				
//				if (scope.investigatePanel == null) {
//					scope.investigatePanel = new Ext.form.FormPanel({
//						border : false,
//						layout : 'form',
//						items : [{
//							border : false,
//							hideBorders : true,
//							layout : 'form',
//							labelWidth : 60,
//							style : 'margin:20px 5px 5px 20px',
//							items : [{
//								xtype : 'asyncombox',
//								fieldLabel : '任务状态',
//								name:'TASK_STATUS',
//								id:'TASK_STATUS',
//								multSel:false,
//								comboxCfg :{
//									boName:'EnumTemplateComboxBO',
//									cfgParams:{
//										code:"OpticalTaskType"
//									}
//								},
//								queryCfg:{
//									type : "string",
//									relation : "in"
//								}
//							}]
//						}]
//					});
//				}
//				var win1 = WindowHelper.openExtWin(scope.investigatePanel, {
//					title : '核查详细信息',
//					width: 300,
//			    	height:150,
//					buttons : [ {
//						text : '确定',
//						scope : this,
//						handler : function() {
//							var records = this.grid.getSelectionModel().getSelections();
//							var cuid = records[0].data['CUID'];
//							var taskName = records[0].data['TASK_NAME'];
//							var url = '/cmp_res/grid/EditorGridPanel.jsp?';
//							var taskStatus = Ext.getCmp('TASK_STATUS').getValue();
//							if(taskStatus=="1"){
//								url += 'code=service_dict_dm.DM_OPTICAL_INVESTIGATE&hasQuery=false';
//							}else if(taskStatus=="2"){
//								url += 'code=service_dict_dm.DM_OPTICAL_WAY_INVESTIGATE&hasQuery=false';
//							}
//							FrameHelper.openUrl(ctx + url + '&cuid=' + cuid + '&c_taskName=' + taskName, '核查结果');
//							Ext.getCmp('TASK_STATUS').setValue('');
//							win1.hide();
//							
//						}
//					}, {
//						text : '取消',
//						scope : this,
//						handler : function() {
//							Ext.getCmp('TASK_STATUS').setValue('');
//							win1.hide();
//							
//						}
//					} ]
//				});
//			},
//			areaTaskInit : function() {
//				if (this.districtTaskOpticalPanel == null) {
//					this.districtTaskOpticalPanel = new Frame.grid.edit.DistrictTaskOpticalPanel();
//				}
//				var scope = this;
//				var win = WindowHelper.openExtWin(this.districtTaskOpticalPanel, {
//					title : '区域定时任务初始化',
//					width: 700,
//			    	height:250,
//					buttons : [ {
//						text : '确定',
//						scope : this,
//						handler : function() {
//							this.districtTaskOpticalPanel._save();
//							win.hide();
//							this.districtTaskOpticalPanel.initCheck();
//							this.grid.doQuery();
//						}
//					}, {
//						text : '清除',
//						scope : this,
//						handler : function() {
//							this.districtTaskOpticalPanel._clean();
//						}
//					}, {
//						text : '取消',
//						scope : this,
//						handler : function() {
//							win.close();
//							this.districtTaskOpticalPanel=null;
//						}
//					} ]
//				});
//			},
//			reStart : function() {
//				var records = this.grid.getSelectionModel().getSelections();
//				if (Ext.isEmpty(records) || records.length == 0) {
//					Ext.Msg.alert('温馨提示', '请选择一条数据.');
//					return;
//				}
//				if (records.length > 1) {
//					Ext.Msg.alert('温馨提示', '目前只支持至多一条数据.');
//					return;
//				}
//				var scope = this;
//				var records = this.grid.getSelectionModel().getSelections();
//				var cuid = records[0].data['CUID'];
//				var taskName = records[0].data['TASK_NAME'];
//				var district = records[0].data['RELATED_WIRE_SYSTEM_CUID'];
//				if (this.OpticalCheckReboot == null) {
//					this.OpticalCheckReboot = new Frame.grid.edit.OpticalCheckReboot({
//						cuid : cuid,
//						taskName : taskName,
//						district : district
//					});
//				}
//				var win = WindowHelper.openExtWin(this.OpticalCheckReboot, {
//					title : '重启光纤核查任务',
//					width: 700,
//			    	height:250,
//					buttons : [ {
//						text : '确定',
//						scope : this,
//						handler : function() {
//							this.OpticalCheckReboot._save();
//							win.hide();
//							this.OpticalCheckReboot.initCheck();
//							this.grid.doQuery();
//						}
//					}, {
//						text : '清除',
//						scope : this,
//						handler : function() {
//							this.OpticalCheckReboot._clean();
//						}
//					}, {
//						text : '取消',
//						scope : this,
//						handler : function() {
//							win.close();
//							this.OpticalCheckReboot=null;
//						}
//					} ]
//				});
//			},
		});
