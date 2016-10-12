/**
 * 点设施查看预留信息
 */
$importjs(ctx+'/dwr/interface/WireRemainAction.js');

(function(window,object,undefined){
	dms.queryWireRemain = function(pointCuid,dialog){
		var self = this;
		self.dialog = dialog;
		var c = self.createPanel();
		self.getWireRemain(pointCuid);
		return c;
	};
	
	ht.Default.def('dms.queryWireRemain',ht.widget.FormPane,{
		dm : null,
		dialog : null,
		createPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane(),
			tablePanel = new ht.widget.TablePane(),
			tableView = tablePanel.getTableView();
			self.dm = tableView.getDataModel();
			var bmClassId = "POINT_REMAIN";
			var url = ctx + "/map/column/" + bmClassId + ".json";
			$.ajaxSettings.async = false;
			$.getJSON(url, {}, function(data) {
				tableView.addColumns(data);
				tablePanel.invalidate();
//				column.formatValue = function(v) {return v;}ht表格数字给自动截取给两个小数，v代表不截取
			});
			$.ajaxSettings.async = true;
			formPane.addRow([tablePanel],[0.1],0.1);
			return formPane;
		},

		//保存CAD导入的识别后的数据
		getWireRemain : function(pointCuid){
			var self = this;
			try {
				WireRemainAction.getWireRemainByPointCuid(pointCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(point){
								var endName = point.WIRE_END_NAME,
									remainLength = point.WIRE_REMAIN_LENGTH,
									remainName = point.WIRE_REMAIN_NAME,
									startName = point.WIRE_START_NAME;
								
								var node = new ht.Node();
								node.a('WIRE_END_NAME',endName);
								node.a('WIRE_REMAIN_LENGTH',remainLength);
								node.a('WIRE_REMAIN_NAME',remainName);
								node.a('WIRE_START_NAME',startName);
								self.dm.add(node);
								
							}); 
							tp.utils.wholeUnLock();
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有相关预留信息！</div>');
						}
					},
					errorHandler : function(errorString) {
						tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
						tp.utils.wholeUnLock();
						return;
					},
					async : false
				});
			} catch (e) {
				tp.utils.wholeUnLock();
			}
		}
	});
})(this,Object);