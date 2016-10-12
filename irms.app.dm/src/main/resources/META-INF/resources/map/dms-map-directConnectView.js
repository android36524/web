/**
 * 直通接续
 */
$importjs(ctx+'/dwr/interface/SpotFixHandlerAction.js');

(function(window,object,undefined){
	dms.Default.directConnectView = function(pointCuid){
		var self = this;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('dms.Default.directConnectView',ht.widget.FormPane,{
		dm : null,
		awireseg : null,
		zwireseg : null,
		pname : null,
		createCenterPanel : function(){
			var self = this;
			var formPane = new ht.widget.FormPane();
			var fiberJointBoxName = self.pname = new ht.widget.ComboBox();
			var aWireSegName = self.awireseg = new ht.widget.ComboBox();
			var zwireSegName = self.zwireseg = new ht.widget.ComboBox();
			
			var selectButton = new ht.widget.Button();
			selectButton.setIcon('grid_icon');			
			selectButton.onClicked = function(e) {
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
			
			formPane.addRow(['光接头盒名称',fiberJointBoxName,selectButton],[80,0.1,20],30);
			formPane.addRow(['A光缆段名称',aWireSegName],[80,0.1],30);
			formPane.addRow(['B光缆段名称',zwireSegName],[80,0.1],30);
			return formPane;
		},

		createPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var centerPanel = self.createCenterPanel();
			var bottomPanel = self.createBottomPanel();
			
			borderPane.setCenterView(centerPanel,300);
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
				self.directConn();
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
			
			btnPanel.addRow([null,applyBtn,null,null,closeBtn,null],[0.1,80,10,10,80,0.1],[23]);
			
			return btnPanel;
		},
		directConn : function(){
			var self = this;
			var jointBoxCuid = self.pname.getValue(),
				aSegCuid = self.awireseg.getValue(),
				bSegCuid = self.zwireseg.getValue();
			
			SpotFixHandlerAction.directConn(jointBoxCuid,aSegCuid,bSegCuid,{
				callback : function(datas){
					if(datas){
						tp.utils.optionDialog("温馨提示",'直通接续成功！');
						tp.utils.wholeUnLock();
					}
				},
				errorHandler : function(errorString){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
					tp.utils.wholeUnLock();
					return;
				},
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
		//保存CAD导入的识别后的数据
/*		getFiberjointbox : function(pointCuid){
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
									connectType = point.CONNECT_TYPE;
								
								var node = new ht.Node();
								node.a('LABEL_CN',labelCn);
								node.a('RELATED_DISTRICT_CUID',relatedDistrictCuid);
								node.a('JUNCTION_TYPE',juncttonType);
								node.a('KIND',kind);
								node.a('CONNECT_TYPE',connectType);
								
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
		
//		addNode : function(){
//			
//		},
		closeEditPanel : function(){
			
		}
	});
})(this,Object);