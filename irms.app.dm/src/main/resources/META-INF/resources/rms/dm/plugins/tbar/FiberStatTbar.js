Ext.ns('Frame.grid.plugins.tbar');
Frame.grid.plugins.tbar.FiberStatTbar = Ext
		.extend(
				Object,
				{

					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.FiberStatTbar.superclass.constructor
								.call(this);
						return [ {
							text : '查看具体纤芯信息',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this.viewFiberDetailInfo
						} ];
					},

					viewFiberDetailInfo : function() {
						var records = this.getSelectionModel().getSelections();
						if (Ext.isEmpty(records) || records.length == 0) {
							Ext.Msg.alert('温馨提示', '请选择光缆段.');
							return;
						}
						if (records.length > 1) {
							Ext.Msg.alert('温馨提示', '目前只支持选择单条数据.');
							return;
						}
						var labelCn = records[0].data['WIRE_SEG_NAME'];
						var wireSegCuid = records[0].data['CUID'];
						var url = '/rms/dm/optical/fiberDetailInfo.jsp?code=service_dict_dm.DM_FIBER_DETAILINFO&hasQuery=false';
						FrameHelper.openUrl(ctx + url + '&CUID='+ wireSegCuid , labelCn + '关联光纤信息');
					}
				});
