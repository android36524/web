Ext.ns('Frame.grid.plugins.tbar');
Frame.grid.plugins.tbar.OpticalErrorAlarmGridTbar = Ext.extend(Object,
		{

			constructor : function(grid) {
				this.grid = grid;
				Frame.grid.plugins.tbar.OpticalErrorAlarmGridTbar.superclass.constructor.call(this);
				return [ 
				{
					text : '查看关联光缆',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this.viewWireSeg
				}
				];
			},

			viewWireSeg : function() {
				var records = this.getSelectionModel().getSelections();
				if (Ext.isEmpty(records) || records.length == 0) {
					Ext.Msg.alert('温馨提示', '请选择告警数据.');
					return;
				}
				if (records.length > 1) {
					Ext.Msg.alert('温馨提示', '目前只支持选择单条数据.');
					return;
				}
				var labelCn = records[0].data['ALARM_NAME'];
				var RELATED_PORT_A =records[0].data['RELATED_PORT_A'];
				var RELATED_PORT_Z =records[0].data['RELATED_PORT_Z'];
				if(null!=RELATED_PORT_A && null!=RELATED_PORT_Z){
					var url = '/rms/dm/optical/opticalErrorAlarmPanel.jsp?code=service_dict_dm.DM_WIRE_SEG_ALARM&hasQuery=false';
					FrameHelper.openUrl(ctx + url + '&RELATED_PORT_A=' + RELATED_PORT_A+ '&RELATED_PORT_Z=' + RELATED_PORT_Z,labelCn + '告警关联光缆信息');
				}
				else {
					Ext.Msg.alert('温馨提示', '没有查询到关联的光缆信息');
				}
			}
		});
