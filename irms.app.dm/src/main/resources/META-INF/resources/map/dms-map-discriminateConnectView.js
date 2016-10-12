/**
 * 分岐接续
 */
$importjs(ctx+'/dwr/interface/SpotFixHandlerAction.js');

(function(window,object,undefined){
	dms.Default.discriminateConnectView = function(dialog){
		var self = this;
		self.dialog = dialog;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('dms.Default.discriminateConnectView',ht.widget.FormPane,{
		dialog : null,
		awireseg : null,
		zwireseg : null,
		afiber : null,
		zfiber : null,
		pname : null,
		fcount : null,
		createCenterPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane(),
				fiberJointBoxName = self.pname = new ht.widget.ComboBox(),
				fiberCount = self.fcount = new ht.widget.TextField(),
				aWireSegName = self.awireseg = new ht.widget.ComboBox(),
				zwireSegName = self.zwireseg = new ht.widget.ComboBox(),
				origFiber = self.afiber = new ht.widget.TextField(),
				destFiber = self.zfiber = new ht.widget.TextField(),
				queryBtn = new ht.widget.Button();
			
			queryBtn.setLabel('...');
			queryBtn.onClicked = function() {
				var code = 'service_dict_dm.DM_FIBER_JOINT_BOX';
				tp.utils.createQueryDialog(code, function(data) {
					var labelCn = data.labelCn,
						cuid = data.cuid;
					var labelArray = [],
						cuidArray = [];
					
					cuidArray.push(cuid);
					labelArray.push(labelCn);
					if (labelCn) {
						self.pname.setValues(cuidArray);
						self.pname.setLabels(labelArray);
						self.pname.setValue(cuidArray[0]);
						
						self.getWireSegsByPoint(self.pname.getValue());
					}
				});
			};

			fiberJointBoxName.enableToolTip();
			aWireSegName.enableToolTip();
			zwireSegName.enableToolTip();
			formPane.addRow(['光接头盒名称',fiberJointBoxName,queryBtn,'接续芯数',fiberCount],[80,185,20,60,0.1],30);
			formPane.addRow(['A光缆段名称',aWireSegName,'起始芯序',origFiber],[80,0.1,60,0.1],30);
			formPane.addRow(['B光缆段名称',zwireSegName,'起始芯序',destFiber],[80,0.1,60,0.1],30);
			return formPane;
		},

		createPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var centerPanel = self.createCenterPanel();
			var bottomPanel = self.createBottomPanel();
			
			borderPane.setCenterView(centerPanel,100);
			borderPane.setBottomView(bottomPanel,40);
			borderPane.getResult = function(){
				return self;
			};
			return borderPane;
		},
		createBottomPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();
			var applyBtn = new ht.widget.Button();
			applyBtn.setLabel('应用');
			self.setButton(applyBtn);
			applyBtn.onClicked = function(e) {
				//jointBoxCuid, aSegCuid, bSegCuid, points, connNumStr, aStartStr, bStartStr
				self.doDiscriminateConn();
			};
			
			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('确定');
			self.setButton(confirmBtn);
			confirmBtn.onClicked = function(e) {
				self.doDiscriminateConn(true);
			};
			
			var closeBtn = new ht.widget.Button();
			closeBtn.setLabel('取消');
			self.setButton(closeBtn);
			closeBtn.onClicked = function(e) {
				self._win.closePanel();
			};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			
			btnPanel.addRow([null,applyBtn,null,confirmBtn,null,closeBtn,null],[0.1,80,10,80,10,80,0.1],[23]);
			
			return btnPanel;
		},
		doDiscriminateConn : function(flag){
			var self = this;
			var jointBoxCuid = self.pname.getValue(),
				aSegCuid = self.awireseg.getValue(),
				bSegCuid = self.zwireseg.getValue(),
				connNumStr = self.fcount.getText(),
				aStartStr = self.afiber.getText(),
				bStartStr = self.zfiber.getText();
			
			SpotFixHandlerAction.discriminateConn(jointBoxCuid,aSegCuid,bSegCuid,connNumStr,aStartStr,bStartStr,{
				callback : function(datas) {
					if(datas){
						tp.utils.optionDialog('温馨提示','分岐接续成功！');
						tp.utils.wholeUnLock();
						if(flag){
							//_win使用dms.Default.openHtPanel用于关闭父窗口（必须）
							self._win.closePanel();
						}
					}
				},
				errorHandler : function(errorString) {
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
					tp.utils.wholeUnLock();
					return;
				}
			});
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		setComboxValues : function(labels,values){
			var self = this;

			self.awireseg.setLabels([]);
			self.awireseg.setValues([]);
			
			self.awireseg.setLabels(labels);
			self.awireseg.setValues(values);
			self.awireseg.setValue(values[0]);
			
			self.zwireseg.setLabels([]);
			self.zwireseg.setValues([]);
			
			self.zwireseg.setLabels(labels);
			self.zwireseg.setValues(values);
			self.zwireseg.setValue(values[0]);
		},
/*		getFiberJointPointsByJointBox : function(){
			var self = this;
			try {
				var pointCuid = self.pname.getValue();
				SpotFixHandlerAction.getFiberJointPointsByJointBox(pointCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(point){
								var cuid = point.CUID,
								labelCn = point.LABEL_CN;
								self.fiberJointPoints.push({"CUID":cuid,"LABEL_CN":labelCn});
							}); 
							tp.utils.wholeUnLock();
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有终端盒！</div>');
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
//				self.closeEditPanel();
				tp.utils.wholeUnLock();
			}
		},*/
		getWireSegsByPoint : function(){
			var self = this;
			try {
				var segCuidArray = [];
				segNameArray = [];
				var pointCuid = self.pname.getValue();
				SpotFixHandlerAction.getWireSegsByPoint(pointCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							datas.forEach(function(point){
								var wCuid = point.CUID,
								wLabelCn = point.LABEL_CN;
								segCuidArray.push(wCuid);
								segNameArray.push(wLabelCn);
							}); 
							
							self.setComboxValues(segNameArray,segCuidArray);
							
							tp.utils.wholeUnLock();
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">根据接头盒得到焊点出错！</div>');
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
		
		closeEditPanel : function(){
			
		}
	});
})(this,Object);