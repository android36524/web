/**
 * 管道系统查询包含管道分支
 */
$importjs(ctx+'/dwr/interface/DmQueryLengthAction.js');

(function(window,object,undefined){
	"use strict";
	
	dms.querDuctLineBranchPanel = function(systemCuid){
		var self = this;
		self.sysCuid = systemCuid;
		var c = dms.querDuctLineBranchPanel.lengthQueryPanel = self.createQueryPanel();
		tp.utils.lock(c);
		dms.querDuctLineBranchPanel.superClass.constructor.apply(this,[{
			title : "管道系统包含分支",
			titleAlign :'center',
			width : 600,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 350,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
				action:function(){
					self.closePanel();
				}
			}],
			content : c
		
		}]);
		self.fp = function(){};

		self.getDatas();
		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 

	};
	
	ht.Default.def('dms.querDuctLineBranchPanel',ht.widget.Panel,{
		sysCuid : null,
		dm : null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight - self._config.contentHeight)/2;
			self.setPosition(x-100, y-120);
			document.body.appendChild(self.getView());
		},
		closePanel : function(){
			var self = this;
			tp.utils.unlock(dms.querDuctLineBranchPanel.lengthQueryPanel);
			Dms.Default.tpmap.reset();
			document.body.removeChild(self.getView());
		},
		createQueryPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane(),
				tablePanel = new ht.widget.TablePane(),
				tableView = tablePanel.getTableView();
			self.dm = tableView.getDataModel();
			var bmClassId = "BRANCHINFO";
			var url = ctx + "/map/column/" + bmClassId + ".json";
			$.ajaxSettings.async = false;
			$.getJSON(url, {}, function(data) {
				tableView.addColumns(data);
				tablePanel.invalidate();
			});
			$.ajaxSettings.async = true;
			formPane.addRow([tablePanel],[0.1],300);
			
			return formPane;
		},
		addDataModel : function(data){
			var self = this;
			var node = self.dm.getDataByTag(data.LABEL_CN);
			if(!node){
				node = new ht.Node();
				node.a('DUCT_SYSTEM_NAME',data.DUCT_SYSTEM_NAME);
				node.a('LABEL_CN',data.LABEL_CN);
				node.a('ORIG_POINT_NAME',data.ORIG_POINT_NAME);
				node.a('DEST_POINT_NAME',data.DEST_POINT_NAME);
				node.setName(data.LABEL_CN);
				node.setTag(data.LABEL_CN);
				self.dm.add(node);
			}
		},
		getDatas : function(){
			var self = this;
			if(self.sysCuid){
				DmQueryLengthAction.getDuctBranchByDuctSystemCuid(self.sysCuid,
				{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(data){
								self.addDataModel(data);
							});
						}
						tp.utils.unlock(dms.querDuctLineBranchPanel.lengthQueryPanel);
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
					}
				});
			}
		}
	});

})(this,Object);