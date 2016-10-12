Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx + "/ponMaintain/panel/DeviceTabPanel.js");

/**
 * 家客设备工程 GridTbar
 */
Frame.grid.plugins.tbar.JkSegGroupGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.JkSegGroupGridTbar.superclass.constructor.call(this);
		return ['-',{
			text : '家客设备管理',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this.viewDeviceInfo
		},'-',{
			text : '提交审核',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this.submitAudit
		},'-',{
			text : '审核确认',
			iconCls : 'c_page_white_link',
			scope : this,
			handler : this.auditConfirm
		}];
	},

	viewDeviceInfo : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (records == null || records.length == 0 || records.length > 1) {
			Ext.Msg.alert("温馨提示", "请选择需要查看的一条单位工程数据.");
			return;
		}

		var scope = this;
		var cuid = records[0].data['CUID'];
		var projectNo = records[0].data['RELATED_PROJECT_CUID'].CUID.CUID;
		if (Ext.isEmpty(cuid)) {
			Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
			return;
		}

		var tbarBtns = 'AnIrmsOltTBar.add,AnIrmsOltTBar.modify,AnIrmsOltTBar.delete,AnIrmsOltTBar.relatedCard,AnIrmsOltTBar.relatedPtp,AnIrmsOltTBar.controlPanel,AnIrmsOltTBar.relatedTopo,AnIrmsOltTBar.netdomain,AnIrmsOltTBar.importOlt,AnIrmsPosTBar.add,AnIrmsPosTBar.modify,AnIrmsPosTBar.delete,AnIrmsPosTBar.relatedCard,AnIrmsPosTBar.relatedPtp,AnIrmsPosTBar.relatedTopo,AnIrmsPosTBar.import,AnIrmsOnuTBar.add,AnIrmsOnuTBar.modify,AnIrmsOnuTBar.delete,AnIrmsOnuTBar.relatedCard,AnIrmsOnuTBar.relatedPtp,AnIrmsOnuTBar.relatedTopo,AnIrmsOnuTBar.import,FullAddressGridTBar.add,FullAddressGridTBar.modify,FullAddressGridTBar.delete,FullAddressGridTBar.importAddress,FullAddressGridTBar.exportAddress,FullAddressGridTBar.businessManage,FullAddressGridTBar.batchInsertAddress,FullAddressGridTBar.showAddressTree,GponCoverCreateTBar.add,GponCoverCreateTBar.modify,GponCoverCreateTBar.delete,GponCoverCreateTBar.import,AnCard.add,AnCard.modify,AnCard.delete,AnCard.relatedPtp,AnPtp.add,AnPtp.modify,AnPtp.delete,AnPtp.import';
		var status = records[0].data['STATUS'].CUID;
		if (status != "1") {
			tbarBtns = 'AnIrmsOltTBar.relatedCard,AnIrmsOltTBar.relatedPtp,AnIrmsOltTBar.relatedTopo,AnIrmsOltTBar.netdomain,AnIrmsPosTBar.relatedCard,AnIrmsPosTBar.relatedPtp,AnIrmsPosTBar.relatedTopo,AnIrmsOnuTBar.relatedCard,AnIrmsOnuTBar.relatedPtp,AnIrmsOnuTBar.relatedTopo,FullAddressGridTBar.exportAddress,FullAddressGridTBar.showAddressTree,AnCard.relatedPtp';
		}

		this.deviceTabPanel = new DM.ponMaintain.common.DeviceTabPanel({
			inputParams : {
				segGroupCuid : cuid,
				projectNo : projectNo
			},
			codes : 'dm_dict.AN_IRMS_OLT_MANAGE,dm_dict.AN_IRMS_POS_MANAGE,dm_dict.AN_IRMS_ONU_MANAGE,dm_dict.IRMS_T_ROFH_FULL_ADDRESS_MANAGE,dm_dict.IRMS_GPON_COVER_MANAGE',
			menuIds : 'AN_IRMS_OLT_MANAGE,AN_IRMS_POS_MANAGE,AN_IRMS_ONU_MANAGE,IRMS_T_ROFH_FULL_ADDRESS_MANAGE,IRMS_GPON_COVER_MANAGE',
			tbarBtns : tbarBtns,
			source : 'WEBDM'
		});

		var win = new Ext.Window({
			width : Ext.getBody().getWidth() * 0.9,
			height : Ext.getBody().getHeight(),
			autoScroll : true,
			modal : true,
			items : [this.deviceTabPanel],
			buttons : [{
				text : '关闭',
				handler : function() {
					win.close();
				},
				scope : this
			}]
		});
		win.show();
	},

	submitAudit : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (records == null || records.length == 0 || records.length > 1) {
			Ext.Msg.alert("温馨提示", "请选择需要查看的一条单位工程数据.");
			return;
		}

		var scope = this;
		var creator = records[0].data['CREATOR'];
		if (creator == ac.userId) {
			var status = records[0].data['STATUS'].CUID;
			if (status == "1") {
				var scope = this;
				var cuid = records[0].data['CUID'];
				if (Ext.isEmpty(cuid)) {
					Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
					return;
				}

				var formMap = {
					segGroupCuid : cuid
				};

				Frame.MaskHelper.mask(scope.grid.getEl(), "提交审核中...");
				IRMS.ViewHelper.request.call(this, {
					url :  ctx + '/JkSnSegGroupAction/submit.do',
					method : 'POST',
					params : {
						formMapStr : Ext.encode(formMap)
					},
					async : true,
					success : function(response) {
						var messsage = "";
						var result = response.responseText;
						if (result == "true") {
							messsage = '提交审核成功';
							scope.grid.store.reload();
						} else {
							messsage = result;
						}
						Frame.MaskHelper.unmask(scope.grid.getEl());
						Ext.Msg.alert("操作提示", messsage);
						return;
					},
					failure : function(response) {
						Frame.MaskHelper.unmask(scope.grid.getEl());
						Ext.Msg.alert("操作提示", "提交审核出错:" + response.responseText);
						return;
					}
				});
			} else {
				Ext.Msg.alert("温馨提示", "请选择待审核的一条单位工程数据提交审核.");
				return;
			}
		} else {
			Ext.Msg.alert("温馨提示", "用户不具备当前单位工程的设计权限.");
			return;
		}
	},

	auditConfirm : function() {
		var records = this.grid.getSelectionModel().getSelections();
		if (records == null || records.length == 0 || records.length > 1) {
			Ext.Msg.alert("温馨提示", "请选择需要查看的一条单位工程数据.");
			return;
		}

		var scope = this;
		var accepterCuid = records[0].data['ACCEPTER_CUID'];
		if (accepterCuid == ac.userId) {
			var status = records[0].data['STATUS'].CUID;
			if (status == "6") {
				var scope = this;
				var cuid = records[0].data['CUID'];
				if (Ext.isEmpty(cuid)) {
					Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
					return;
				}

				var formMap = {
					segGroupCuid : cuid,
					deviceTypes : 'AN_OLT,AN_ONU,AN_POS,T_ROFH_FULL_ADDRESS,GPON_COVER',
					flag : false
				};

				Ext.MessageBox.show({
					title : '审核确认操作提示',
					msg : '审核提交：将单位工程状态改为维护，并迁移该单位工程保护的所有资源<br />审核驳回：将单位工程状态改为设计<br />取消：取消当前操作',
					buttons : {ok : '审核提交', yes : '审核驳回', cancel : '取消'},
				    fn : function(btn) {
						if (btn == 'ok') {
							Frame.MaskHelper.mask(scope.grid.getEl(), "迁移数据中...");
							IRMS.ViewHelper.request.call(this, {
								url :  ctx + '/JkSnSegGroupAction/migrateData.do',
								method : 'POST',
								params : {
									formMapStr : Ext.encode(formMap)
								},
								async : true,
								success : function(response) {
									var result = response.responseText;
									if (result == "true") {
										Ext.Msg.alert("操作提示", "迁移数据成功");
										scope.grid.store.reload();
										Frame.MaskHelper.unmask(scope.grid.getEl());
										return;
									} else {
										Ext.Msg.alert("操作提示", result);
										Frame.MaskHelper.unmask(scope.grid.getEl());
										return;
									}
								},
								failure : function(response) {
									Ext.Msg.alert("操作提示", "迁移数据出错:" + response.responseText);
									Frame.MaskHelper.unmask(scope.grid.getEl());
									return;
								}
							});
						} else if (btn == 'yes') {
					    	MaskHelper.mask(scope.grid.getEl(), '审核驳回中，请稍候...');
					    	IRMS.ViewHelper.request.call(this, {
								url :  ctx + '/JkSnSegGroupAction/reject.do',
								method : 'POST',
								params : {
									formMapStr : Ext.encode(formMap)
								},
								async : true,
								success : function(response) {
									var result = response.responseText;
									if (result == "true") {
										Ext.Msg.alert("操作提示", "审核驳回成功");
										scope.grid.store.reload();
										Frame.MaskHelper.unmask(scope.grid.getEl());
										return;
									} else {
										Ext.Msg.alert("操作提示", result);
										Frame.MaskHelper.unmask(scope.grid.getEl());
										return;
									}
								},
								failure : function(response) {
									Ext.Msg.alert("操作提示", "审核驳回出错:" + response.responseText);
									Frame.MaskHelper.unmask(scope.grid.getEl());
									return;
								}
							});
					    }
				    }
				});
			} else {
				Ext.Msg.alert("温馨提示", "请选择待审核的一条单位工程数据进行数据迁移.");
				return;
			}
		} else {
			Ext.Msg.alert("温馨提示", "用户不具备当前单位工程的审核权限.");
			return;
		}
	}
});