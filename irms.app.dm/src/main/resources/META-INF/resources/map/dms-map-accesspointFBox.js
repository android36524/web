/**
 * 接入点查看终端盒列表
 */
$importjs(ctx+'/dwr/interface/WireRemainAction.js');

(function(window,object,undefined){
	dms.accesspointFbox = function(pointCuid,dialog){
		var self = this;
		self.dialog = dialog;
		var c = self.createPanel();
		self.getFiberjointbox(pointCuid);
		return c;
	};
	
	ht.Default.def('dms.accesspointFbox',ht.widget.FormPane,{
		dm : null,
		dialog : null,
		createCenterPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane(),
			tablePanel = new ht.widget.TablePane(),
			tableView = tablePanel.getTableView();
			self.dm = tableView.getDataModel();
			var bmClassId = "DUCTFIBERJOINTBOX";
			var url = ctx + "/map/column/" + bmClassId + ".json";
			$.ajaxSettings.async = false;
			$.getJSON(url, {}, function(data) {
				tableView.addColumns(data);
				tablePanel.invalidate();
			});
			$.ajaxSettings.async = true;
			formPane.addRow([tablePanel],[0.1],0.1);
			return formPane;
		},

		createPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var centerPanel = self.createCenterPanel();
			var bottomPanel = self.createBottomPanel();
			
			borderPane.setCenterView(centerPanel,300);
			borderPane.setBottomView(bottomPanel,40);
			return borderPane;
		},
		createBottomPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();
			var delBtn = new ht.widget.Button();
			delBtn.setLabel('删除');
			self.setButton(delBtn);
			delBtn.onClicked = function(e) {
				self.deleteFiberjointBoxs();
			};
			
			var exportBtn = new ht.widget.Button();
			exportBtn.setLabel('导出');
			self.setButton(exportBtn);
			exportBtn.onClicked = function(e) {
				
			};
			
			var closeBtn = new ht.widget.Button();
			closeBtn.setLabel('关闭');
			self.setButton(closeBtn);
			closeBtn.onClicked = function(e) {
				self.dialog.closePanel();
			};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			
			btnPanel.addRow([null,delBtn,null],[0.1,60,0.1],[23]);
			
			return btnPanel;
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		deleteFiberjointBoxs : function(){
			var self = this;
			var selectFboxs = self.dm.sm().getSelection();
			if(!selectFboxs || selectFboxs.size() == 0){
				tp.utils.optionDialog("温馨提示", "没有选择数据！");
				return;
			}
			var arrays = [];
			if(selectFboxs){
				selectFboxs.each(function(data){
					var cuid = data.a('CUID');
					arrays.push(cuid);
				});
			}
			WireRemainAction.deleteFBoxForAccesspoint(arrays,function(data){
				if(data){
					self.dm.sm().toSelection().each(self.dm.remove, self.dm); 
				}
			});
		},
		//保存CAD导入的识别后的数据
		getFiberjointbox : function(pointCuid){
			var self = this;
			try {
				WireRemainAction.getFiberjointbox(pointCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(point){
								var labelCn = point.LABEL_CN,
									relatedDistrictCuid = point.RELATED_DISTRICT_CUID,
									juncttonType = point.JUNCTION_TYPE,
									kind = point.KIND,
									connectType = point.CONNECT_TYPE,
									cuid = point.CUID;
								
								
								var node = new ht.Node();
								node.a('CUID',cuid);
								node.a('LABEL_CN',labelCn);
								node.a('RELATED_DISTRICT_CUID',relatedDistrictCuid);
								node.a('JUNCTION_TYPE',juncttonType);
								node.a('KIND',kind);
								node.a('CONNECT_TYPE',connectType);
								node.setId(cuid);
								self.dm.add(node);
								
							}); 
							tp.utils.wholeUnLock();
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有终端盒！</div>');
						}
//						self.closeEditPanel();
					},
					errorHandler : function(errorString) {
						tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
//						self.closeEditPanel();
						tp.utils.wholeUnLock();
						return;
					},
					async : false
				});
			} catch (e) {
//				self.closeEditPanel();
				tp.utils.wholeUnLock();
			}
		},
		addNode : function(){
			
		},
		closeEditPanel : function(){
			
		}
	});
})(this,Object);