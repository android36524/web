Ext.ns('Frame.grid.plugins.tbar');
/*
 * 分光器列表
 */
$importjs(ctx + '/rms/dm/wire/PosDeleteView.js');
$importjs(ctx + '/map/dms-map-deleteResource.js');
$importjs(ctx + '/map/map-inc.js');
$importjs(ctx + "/rms/dm/common/BatchNamesPanel.js");
$importjs(ctx + '/rms/common/FilePanel.js');
$importjs(ctx + '/jslib/jquery/jquery-1.7.1.min.js');
$importjs(ctx + '/dwr/interface/PosAction.js');
Frame.grid.plugins.tbar.PosListGridTbar = Ext
		.extend(
				Object,
				{

					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.PosListGridTbar.superclass.constructor
								.call(this);

						var addButton = new Ext.Button({
							text : '添加',
							iconCls : 'c_page_white_link',
							scope : this,
							handler : this._addPos
						});

						var deleteBtn = new Ext.Button({
							text : '删除',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._deletePos
						});
						this.changeButtonArray = new Array();
						this.changeButtonArray.push(addButton);
						return [ addButton, '-', deleteBtn ];

					},

					// 添加分光器并弹出查询列表
					_addPos : function() {

						var url = ctx
								+ "/cmp_res/grid/ResGridPanel.jsp?hasMaintan=false&code=service_dict_dm.DM_POS_QUERY";
						var demension = "left=200,top=100,width=966,height=528";
						var grid = this.grid;
						window.setShowModalDialogValue = function(value) {
							var d = value;

							var relatedCabCuid = grid.cuid;
							var arr = [];
							for (var i = 0; i < d.length; i++) {
								arr[i] = d[i].data;
							}

							PosAction.doAdd(arr, relatedCabCuid, function() {

								if (!Ext.isEmpty(d)) {
									for (var j = 0; j < d.length; j++) {
										var record = new Ext.data.Record(
												d[j].data);
										grid.getStore().add(record);
									}
								}
								;
							});

						};
						window.open(url, "selectRecord", demension);

					},

					_deletePos : function() {

						var scope = this;
						var records = this.getSelectionModel().getSelections();
						if (Ext.isEmpty(records) || records.length == 0) {
							Ext.Msg.alert('温馨提示', '请选要删除的数据.');
							return;
						}
						var cuid = records[0].data['CUID'];
						if (Ext.isEmpty(cuid)) {
							Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
							return;
						}
						Ext.MessageBox.show({
							title : '温馨提示！',
							msg : ' 请确定是否要删除选中数据 ',
							buttons : {
								yes : '确定',
								cancel : '取消'
							},
							fn : function(btn) {
								if (btn == "yes") {
									var resources = [];
									for (var i = 0; i < records.length; i++) {
										// var data=records[i].data.cuid;
										var data = records[i].data;
										data.relatedCabCuid = scope.cuid;

										resources.push(data);
									}
									// resources.push(res);
								}
								PosAction.doDelete(resources, function() {
									scope.doQuery();
								});
							}
						});
					}

				});
