/**
 * 接入点查看交接箱列表 liuyumiao
 */
$importjs(ctx+'/dwr/interface/WireRemainAction.js');

(function(window,object,undefined){
	dms.accesspointFiberCab = function(pointCuid,dialog){
		var self = this;
		self.dialog = dialog;
		var c = self.createPanel();
		self.getFiberCab(pointCuid);
		return c;
	};
	
	ht.Default.def('dms.accesspointFiberCab',ht.widget.FormPane,{
		dm : null,
		dialog : null,
		createCenterPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane(),
			tablePanel = new ht.widget.TablePane(),
			tableView = tablePanel.getTableView();
			self.dm = tableView.getDataModel();
			var bmClassId = "DUCTFIBERCAB";
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
				self.deleteFiberCab();
			};
			
			var fiberBtn = new ht.widget.Button();
			fiberBtn.setLabel('纤芯关联');
			self.setButton(fiberBtn);
			fiberBtn.onClicked = function(e) {
				self.fiberLink();
	  	        
			};
			
			var jumpBtn = new ht.widget.Button();
			jumpBtn.setLabel('跳纤跳线管理');
			self.setButton(jumpBtn);
			jumpBtn.onClicked = function(e) {
				
			};
			var portBtn = new ht.widget.Button();
			portBtn.setLabel('端子管理');
			self.setButton(portBtn);
			portBtn.onClicked = function(e) {
				
			};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			btnPanel.addRow([null,portBtn,null,fiberBtn,null,jumpBtn,null,delBtn,null],[30,80,5,80,5,120,5,60,5],[23]);
			return btnPanel;
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		//纤芯关联
		fiberLink : function(){
			var self = this;
			var selectFboxs = self.dm.sm().getSelection();
			if(!selectFboxs || selectFboxs.size() == 0){
				tp.utils.optionDialog("温馨提示", "没有选择数据！");
				return;
			}
			var arrays = [];
			var cuid = null;
			var labelCn = null;
			if(selectFboxs){
				selectFboxs.each(function(data){
					 cuid = data.a('CUID');
					 labelCn = data.a('LABEL_CN');
					arrays.push(cuid);
					arrays.push(labelCn);
				});
			}
			var viewParam={	 	        	   
	 	        	   'labelCn':arrays.pop(labelCn),
	 	        	   'cuid':arrays.pop(cuid),
	 	        	   'mapName':"光交接箱纤芯关联"
	     	        };
        	var fiberLinkView = new FiberLinkView(viewParam.cuid, viewParam.labelCn);
        	viewParam.content=fiberLinkView;
	        tp.utils.showDialogView(viewParam);

		},
		//删除光交接箱
		deleteFiberCab : function(){
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
			WireRemainAction.deleteFiberCabForAccesspoint(arrays,function(data){
				if(data){
					self.dm.sm().toSelection().each(self.dm.remove, self.dm); 
				}
			});
		},
		//得到光接箱数据
		getFiberCab : function(pointCuid){
			var self = this;
			try {
				WireRemainAction.getFiberCab(pointCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(point){
								var labelCn = point.LABEL_CN,
									relatedDistrictCuid = point.RELATED_DISTRICT_CUID,
									cuid = point.CUID;								
								
								var node = new ht.Node();
								node.a('CUID',cuid);
								node.a('LABEL_CN',labelCn);
								node.a('RELATED_DISTRICT_CUID',relatedDistrictCuid);
								node.setId(cuid);
								self.dm.add(node);
								
							}); 
							tp.utils.wholeUnLock();
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有交接箱！</div>');
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
		},
		addNode : function(){
			
		},
		closeEditPanel : function(){
			
		}
	});
})(this,Object);