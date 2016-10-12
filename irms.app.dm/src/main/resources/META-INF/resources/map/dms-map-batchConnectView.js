/**
 * 批量接续
 */
$importjs(ctx + '/dwr/interface/WireRemainAction.js');

(function(window, object, undefined) {
	dms.Default.batchConnectView = function(pointCuid, dialog) {
		var self = this;
		self.dialog = dialog;
		var c = self.createPanel();
		return c;
	};

	ht.Default.def('dms.Default.batchConnectView', ht.widget.FormPane, {
		dm : null,
		dialog : null,
		fcount : null,
		jnum : null,
		awireseg : null,
		zwireseg : null,
		afiber : null,
		zfiber : null,
		pname : null,
		check : [],

		createCenterPanel : function() {
			var self = this;
			var formPane = new ht.widget.FormPane(), tablePanel = new ht.widget.TablePane(), tableView = self._tableView = tablePanel
					.getTableView();
			dataModel = self.dm = tableView.getDataModel();

			seqnumber = self.fcount, jointCoreNum = self.jnum, jointComboBoxa = self.awireseg, jointComboBoxb = self.zwireseg, aStartIndexa = self.afiber, aStartIndexb = self.zfiber;

			var attributes = [{
						name : 'seqnumber',
						displayName : '序号',
						align : 'center',
						accessType : 'attr'
					}, {
						name : 'jointCoreNum',
						displayName : '接续芯数',
						editable : true,
						accessType : 'attr'
					}, {
						name : 'jointComboBoxa',
						displayName : 'A光缆段名称',
						editable : true,
						accessType : 'attr',
						tag : 'awire'
					}, {
						name : 'aStartIndexa',
						displayName : '起始芯序',
						editable : true,
						accessType : 'attr'
					}, {
						name : 'jointComboBoxb',
						displayName : 'z光缆段名称',
						editable : true,
						accessType : 'attr',
						tag : 'zwire'
					}, {
						name : 'aStartIndexb',
						displayName : '起始芯序',
						editable : true,
						accessType : 'attr'
					}];

			tableView.addColumns(attributes);
			tablePanel.invalidate();
			formPane.addRow([tablePanel], [0.1], 0.1);
			return formPane;
		},

		createTopPanel : function() {
			var self = this;
			var formPane = new ht.widget.FormPane();
			var fiberJointBoxName = new ht.widget.TextField();
			var selectButton = new ht.widget.Button();
			selectButton.setIcon('grid_icon');

			fiberJointBoxName = self.pname = new ht.widget.ComboBox(),

			selectButton.onClicked = function(e) {
				var code = 'service_dict_dm.DM_FIBER_JOINT_BOX';

				tp.utils.createQueryDialog(code, function(data) {
							var labelCn = data.labelCn, cuid = data.cuid;
							var labelArray = [], cuidArray = [];

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
			formPane.addRow(['光接头盒名称', fiberJointBoxName, selectButton], [80,
							0.1, 20], 23);
			return formPane;
		},

		createPanel : function() {
			var self = this;
			var borderPane = new ht.widget.BorderPane();

			var topPanel = self.createTopPanel();
			var centerPanel = self.createCenterPanel();
			var bottomPanel = self.createBottomPanel();

			borderPane.setTopView(topPanel, 40);
			borderPane.setCenterView(centerPanel, 280);
			borderPane.setBottomView(bottomPanel, 40);
			borderPane.getResult = function() {
				return self;
			};
			return borderPane;

		},
		createBottomPanel : function() {
			var self = this;
			var btnPanel = new ht.widget.FormPane();
			var applyBtn = new ht.widget.Button();
			applyBtn.setLabel('应用');
			self.setButton(applyBtn);
			applyBtn.onClicked = function(e) {
				if (self.doBatchConn()) {
					tp.utils.optionDialog('温馨提示', '所选择的AB光缆段接续成功！');
				}
			};

			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('确定');
			self.setButton(confirmBtn);
			confirmBtn.onClicked = function(e) {
				if (self.doBatchConn()) {
					tp.utils.optionDialog('温馨提示', '所选择的AB光缆段接续成功！');
					self._win.closePanel();
				}
			};

			var closeBtn = new ht.widget.Button();
			closeBtn.setLabel('取消');
			self.setButton(closeBtn);
			closeBtn.onClicked = function(e) {
				self._win.closePanel();
			};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth = '0';
			btnPanel.getView().style.borderRightWidth = '0';
			btnPanel.getView().style.borderBottomWidth = '0';

			btnPanel.addRow([null, applyBtn, null, confirmBtn, null, closeBtn,
							null], [0.1, 80, 10, 80, 10, 80, 0.1], [23]);

			return btnPanel;
		},

		doBatchConn : function() {
			var self = this;
			var handlerresult = false;
			var time = 10;

			list = self.dm.getDatas();
			for (var i = 0; i < 10; i++) {

				data = list.get(i);
				var jointBoxCuid = self.pname.getValue();
				if(typeof(data.a('jointCoreNum')) == 'string')
				   connNumStr = data.a('jointCoreNum');
				else
			       connNumStr = '';
			    aSegCuid = data.a('jointComboBoxa');
				bSegCuid = data.a('jointComboBoxa');
				if(typeof(data.a('aStartIndexa')) == 'string')
				   aStartStr = data.a('aStartIndexa');
				else
					aStartStr = '';
				if(typeof(data.a('aStartIndexb')) == 'string')
					bStartStr = data.a('aStartIndexb');
				else
				    bStartStr = '';
				
				
				if (connNumStr != '' && aSegCuid != '' && bSegCuid != ''
						&& aStartStr != '' && bStartStr != '') {
					SpotFixHandlerAction.batchConn(jointBoxCuid, aSegCuid,
							bSegCuid, connNumStr, aStartStr, bStartStr, i + 1,
							function(data) {
								time--;
								if (time == 0) {
									tp.utils.optionDialog('温馨提示',
											'所选择的AB光缆段接续成功！');
								}
							});
				} else {
					var temp = i + 1;
					if(connNumStr == '' ||  aStartStr == '' || bStartStr == ''|| aSegCuid == '未选择' || bSegCuid == '未选择')
						if(connNumStr != '' ||  aStartStr != '' || bStartStr != ''|| aSegCuid != '未选择' || bSegCuid != '未选择')
					        tp.utils.optionDialog('温馨提示', '请填写完整第'+temp+'条接续纤数、A端起始芯序和Z段起始芯序！');
					break;
				}
			}
			return handlerresult;

		},
		setButton : function(button) {
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},

		getWireSegsByPoint : function() {
			var self = this;
			try {
				var segCuidArray = [];
				var segNameArray = [];
				var dm = self._tableView.getDataModel();
				var cl = self._tableView.getColumnModel();

				var pointCuid = self.pname.getValue();
				SpotFixHandlerAction.getWireSegsByPoint(pointCuid, {
					callback : function(datas) {
						if (datas && datas.length > 0) {
							datas.forEach(function(point) {

								var wCuid = point.CUID, wLabelCn = point.LABEL_CN;
								segCuidArray.push(wCuid);
								segNameArray.push(wLabelCn);
							});

							for (var i = 1; i <= 10; i++) {

								var data = new ht.Node();
								data.a('seqnumber', i);
								data.a('jointCoreNum', '');
								data.a('jointComboBoxa', '未选择');
								data.a('aStartIndexa', '');
								data.a('jointComboBoxb', '未选择');
								data.a('aStartIndexb', '');
								dm.add(data);
							}
							var column = cl.getDataByTag('awire');
							column.setEnum({
										values : segCuidArray,
										labels : segNameArray
									});

							var column1 = cl.getDataByTag('zwire');
							column1.setEnum({
										values : segCuidArray,
										labels : segNameArray
									});

							tp.utils.wholeUnLock();
						} else {
							tp.utils
									.optionDialog("温馨提示",
											'<div style="text-align:center;font-size:12px;color:red">根据接头盒得到焊点出错！</div>');
						}
					},
					errorHandler : function(errorString) {
						tp.utils.optionDialog("温馨提示",
								'<div style="text-align:center;font-size:12px;color:red">'
										+ errorString + '</div>');
						tp.utils.wholeUnLock();
						return;
					},
					async : false
				});
			} catch (e) {
				tp.utils.wholeUnLock();
			}
		},

		closeEditPanel : function() {

		}

	});
})(this, Object);