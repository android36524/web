$importjs(ctx+'/dwr/interface/WireFinishCutAction.js');
$importjs(ctx + "/rms/dm/fiber/CutSchemeFinishHt.js");
//方案设计
(function(window,Object,undefined){
	"use strict";
	dms.Default.CutSchemeFinish = function() {
		dms.Default.CutSchemeFinish.superClass.constructor.apply(this);
		var self = this;
		var c = self.createPanel();
		
		self.zCancelData = new Array();
		self.selectDm = new ht.DataModel();
		c.getResult = function(fibers,task_cuid){
			self.tcuid = task_cuid;
			self.fibers = fibers;
			//初始化z端树
			self.initRTree(self.rightDm);
			//初始化A端树
            var deviceCuid = self.fibers[0].getParent().getParent().a('CUID');
            self.initRoomList(deviceCuid);
            self.initPortListModel();
		};
		return c;
	};
	
	ht.Default.def('dms.Default.CutSchemeFinish', ht.widget.BorderPane, {
		tcuid: null,
		fibers: null,
		rightTree: null,
		rightDm: null,
		leftTree: null,
		leftDm: null,
		dm : null,
		aRoomSelect: null,
		selectDm : null,
		zCancelData: null,
		aCategorySelect : null,
		buttonToolbar: null,
		action_type: null,
		aPortListView: null,
		zPortListView: null,
		fiberselect: null,
		portselect: null,
		
		bmClassId: null,
		
		createPanel : function(){
			//上层选择区
			var self = this;
			var formPanel = new ht.widget.FormPane();
			var zSelectView = self.zPortListView = self.createRightPanel();
			var aSelectView = self.aPortListView = self.createLeftPanel();
			self.createBottomPanel(self.disIndex);
			var splitView = new Topo.widget.SplitView(aSelectView, zSelectView, 'horizontal', 0.5);
			formPanel.addRow([splitView],[0.1],0.1);
			formPanel.addRow([self.buttonToolbar],[0.1],30);
			return formPanel;
		},
		createLeftPanel : function(){
			var self = this;
			var formPanel = new ht.widget.FormPane();
			
			//端口端子列表
			var room = self.aRoomSelect = new ht.widget.ComboBox();
			var leftDm = self.leftDm = new ht.DataModel();
			var ltreeView = self.leftTree = new ht.widget.TreeView(leftDm);
			ltreeView.setCheckMode('default');
			ltreeView.onDataDoubleClicked = function(data) {
			    var parentCuid = data.getId();
				if(parentCuid.split('-')[0] == "ODFPORT")
					return;
				WireFinishCutAction.getchildrenbyParentCuid(parentCuid,{
					callback:function(datas){
						datas.forEach(function(result){
							node = new ht.Node();
							node.a('CUID', result.CUID);
							node.setId(result.CUID);
							node.setName(result.LABEL_CN);
							node.setTag(result.CUID);
							if(result.ICON == "1")
								node.setIcon("/resources/topo/alarm/w.png");
							else if(result.ICON == "2")
								node.setIcon("/resources/topo/alarm/c.png");
							else
							    node.setIcon(result.ICON);
							var parentNode = self.leftDm.getDataByTag(result.PARENTCIUD);
							node.setParent(parentNode);
							self.leftDm.add(node);
						});
					},
					errorHandler : function(errorString) {
						return;
					}
				});
			};
			var tabView = new ht.widget.TabView();
            tabView.add('端子列表', ltreeView, '#1ABC9C');
			formPanel.addRow([room],[0.1],23);
			formPanel.addRow([tabView],[0.1],0.1);
			return formPanel;
		},
		createRightPanel : function(){
			var self = this;
			var formPanel = new ht.widget.FormPane();
			//Z端
			var rightDm = self.rightDm = new ht.DataModel();
			var rtreeView = self.rightTree = new ht.widget.TreeView(rightDm);
			rtreeView.setCheckMode('default');
			var tabView = new ht.widget.TabView();
            tabView.add('割接纤芯列表', rtreeView, '#1ABC9C');
			formPanel.addRow([tabView],[0.1],0.1);
			return formPanel;
		},
		initRTree : function(dm){
			var self = this;
			var fibers = self.fibers;
			if(fibers == null || fibers.length == 0){
				return;
			}
			var deviceNode = fibers[0].getParent().getParent();
			var wiresys = fibers[0].getParent();
			dm.add(c(deviceNode));
			dm.add(c(wiresys));
			for(var i = 0; i < fibers.length; i++){
				dm.add(c(fibers[i]));
				WireFinishCutAction.getChildrenByCutFibers(fibers[i].getId(),deviceNode.getId(),{
					callback:function(datas){
						datas.forEach(function(result){
							node = new ht.Node();
							node.a('CUID', result.CUID);
							node.setId(result.CUID);
							node.setName(result.LABEL_CN);
							node.setTag(result.CUID);
							if(result.ICON == "1")
							    node.setIcon("/resources/topo/alarm/w.png");
							else
								node.setIcon("/resources/topo/alarm/c.png");
							var parentNode = self.rightDm.getDataByTag(result.PARENTCIUD);
							node.setParent(parentNode);
							self.rightDm.add(node);
						});
					}
				});
			}
			
			function c(node){
				var newNode =  new ht.Node();
				newNode.a('CUID', node.a('CUID'));
				newNode.setId(node.a('CUID'));
				newNode.setName(node.getName());
				newNode.setTag(node.a('CUID'));
				var cName = node.a('CUID').split('-')[0];
				newNode.a('bmClassId', cName);
				newNode.setIcon(node.getIcon());
				if(cName == 'FIBER' || cName == 'WIRE_SEG'){
					var parentNode = dm.getDataByTag(node.getParent().a('CUID'));
					newNode.setParent(parentNode);
				}
				return newNode;
			}
		},
		createBottomPanel : function(boolean){
			//底层按钮操作区
			var self = this;
			var buttonItems = [
			{
				type: 'button',
				label: '关联',
				disabled: false,
				action: function(item) {
					self.doRelevancy();
					item.selected = false;
				}
			}, {
				type: 'button',
				label: '断开',
				disabled: false,
				action: function(item) {
					self.doDisconnect();
					item.selected = false;
				}
			}, {
				type: 'button',
				label: '确定',
				disabled: true,
				action: function(item) {
					self.doConfim();
				}
			}, {
				type: 'button',
				label: '取消',
				disabled: true,
				action: function(item) {
					self.doCancel();
					item.selected = false;
				}
			}];
			
			var btnToolbar = self.buttonToolbar = new ht.widget.Toolbar(buttonItems);
			btnToolbar.setStickToRight(true);
		
			return btnToolbar;
		},
		doCancel: function() {
			var self = this;
			
	        if(self.action_type === "delete"){
	        	self.zCancelData.splice(0);
	        	return;
	        }
	        self.removePrePort();
			var items = self.buttonToolbar.getItems();
			if (items) {
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					var label = item.label;
					if (label != "确定" && label != "取消") {
						item.disabled = false;
					} else {
						item.disabled = true;
					}
					item.selected = false;
				}
			}
		},
		doDisconnect: function() {
			var self = this;
			self.zCancelData.splice(0);
			//action_type用来判断在纤芯上架关联后确定时判断是新增还是删除
			self.action_type = "delete";
		
			//先不管原来的选择左侧端子断开，只支持右侧选择纤芯断开关联
			var selectFibers = self.rightDm.sm().getSelection();
			//List selWiresList = wireTreeBox.getSelectionModel().getAllSelectedElement();

			if (selectFibers == null || selectFibers.size() == 0) {
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择需要断开的纤芯！！！</div>');
				return;
			}
			
			var fiberCuids = "";
			for(var i = 0; i< selectFibers.size();i++){
				var flag = false;
				var fiber = selectFibers.get(i);
				var pdata = fiber._children;
				var fiberCuid = fiber.getAttr("CUID");
				var fiberBm = fiberCuid.split('-')[0];
				
				if(fiberBm != 'FIBER'){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择纤芯进行断开！！！</div>');
				    break;
				}
				
				for(var n = 0; n < pdata.size(); n++){
					var nData = pdata.get(n);
					var pName = nData.getName();
					var pCuid = nData.getId();
					if(pName.indexOf('预割接') > -1){
						flag = true;
						fiberCuids = fiberCuids + ","+fiberCuid + ","+pCuid;
						self.zCancelData.push(self.rightDm.getDataById(fiberCuid));
					}
				}
				
				if(!flag){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">所选纤芯中不包含预割接端子！！！</div>');
				    return;
				}
			}
			fiberCuids = self.fiberselect = fiberCuids.substring(1, fiberCuids.length);
			
			var items = self.buttonToolbar.getItems();
			if (items) {
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					var label = item.label;
					if (label != "确定" && label != "取消") {
						item.disabled = true;
					} else {
						item.disabled = false;
					}
					item.selected = false;
				}
			}
		},
		//function在后面时代表一个函数，定义给doRelevancy对象；
		doRelevancy: function() {
			var self = this;
			self.zCancelData.splice(0);
			self.action_type = "add";
			var selectPorts = self.leftDm.sm().getSelection();
			var selectFibers = self.rightDm.sm().getSelection();
			if (selectPorts == null || selectFibers == null || selectPorts.size() == 0 || selectFibers.size() == 0) {
				//请选择需要关联的纤芯和端子
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有选择需要关联的纤芯和端子,请选择后再关联！！！</div>');
				return;
			}
			//如果两端选的不一致，直接提示--原来的：纤芯关联以纤芯列表为准,选择的纤芯数量必须小于或者等于选中的端子数量
			if (selectPorts.size() != selectFibers.size()) {
				//纤芯关联以纤芯列表为准,选择的纤芯数量必须小于或者等于选中的端子数量
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">所选择的端子数据必须和选择的纤芯数量相同！！！</div>');
				return;
			}
	
			var portselect = "";
			var checked = true;
			selectPorts.each(function(data) {
				var portCuid = data.getAttr("CUID");
				var portBm = portCuid.split('-')[0];
				//纤芯上架中只有odf端子和焊点类型
				if (!(portBm == "ODFPORT" || portBm == "FIBER_JOINT_POINT" || portBm == "PTP")) {
					//做关联时端子列表必须选择端子
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">做关联时端子列表中选择的必须是端子！！！</div>');
					checked = false;
					return;
				}
	
				portselect = portselect + portCuid + ",";
			}, selectPorts);
			portselect = self.portselect = portselect.substring(0, portselect.length - 1);
	
			var fiberselect = "";
			var flag = true;
			selectFibers.each(function(data) {
				var fiberCuid = data.getAttr("CUID");
				var fiberBm = fiberCuid.split('-')[0];
				if (!(fiberBm == "FIBER")) {
					//判断光缆列表选中元素类型,做关联时纤芯列表中必须选择纤芯
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">做关联时纤芯列表中必须选择纤芯！！！</div>');
					checked = false;
					return;
				}
				var pdata = data._children;
				for(var n = 0; n < pdata.size(); n++){
					var nData = pdata.get(n);
					var pName = nData.getName();
					if(pName.indexOf('预割接') > -1){
						flag = false;
					}
				}
				if(!flag){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">所选纤芯中已包含预割接端子！！！</div>');
				    return;
				}
				fiberselect = fiberselect + fiberCuid + ",";
			}, selectFibers);
			if (!checked)
				return;
	
			fiberselect = self.fiberselect = fiberselect.substring(0, fiberselect.length - 1);
	
			var items = self.buttonToolbar.getItems();
			if (items) {
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					var label = item.label;
					if (label != "确定" && label != "取消") {
						item.disabled = true;
					} else {
						item.disabled = false;
					}
					item.selected = false;
				}
			}
	
			var fibers = fiberselect.split(",");
			var ports = portselect.split(",");
	        
			if(flag){
				for (var i = 0; i < fibers.length; i++) {
					var fiberCuid = fibers[i];
					var portCuid = ports[i];
					var hfiber = self.rightDm.getDataById(fiberCuid);
					var pt = self.leftDm.getDataById(portCuid);
					var ptName = pt.getName();
					var icon = pt.getIcon();
					
					var node = new ht.Node();
					node.setTag(portCuid);
					node.a('CUID',data.CUID);
					node.setId(portCuid);
					ptName = '预割接端子 ： ' + ptName;
					node.setName(ptName);
					node.setIcon(icon);
					var cName = data.CUID.split('-')[0];
					node.a('bmClassId',cName);
					node.setImage(cName);
					node.setAttr("newnode", "newnode");
					self.rightDm.add(node);
					
					if (hfiber) {
						hfiber.addChild(node);
						self.zCancelData.push(hfiber);
					}
				}
			}
		},
		doConfim: function() {
			var self = this;
			var cutoverTask = self.tcuid;
			var fibers = self.fiberselect;
			try {
				if (self.action_type == "add") {
					var device = self.fibers[0].getParent().getParent().getId();
					var wireSeg = self.fibers[0].getParent().getId();
					var ports = self.portselect;
					WireFinishCutAction.doRelevancy(cutoverTask,device,wireSeg,fibers,ports,{
						callback : function(){
						},
						errorHandler:function(errorMessage){
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorMessage + '</div>');
							self.doCancel();
							tp.utils.wholeUnLock();
							return;
						}
					});
				}
				if (self.action_type == "delete") {
					self.removePrePort();
					WireFinishCutAction.doDisConnect(fibers,cutoverTask,{
						callback : function(){
						},
						errorHandler:function(errorMessage){
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorMessage + '</div>');
							tp.utils.wholeUnLock();
							return;
						}
					});
				}
			} catch (err) {
			} finally {
				var items = self.buttonToolbar.getItems();
				if (items) {
					for (var i = 0; i < items.length; i++) {
						var item = items[i];
						var label = item.label;
						if (label != "确定" && label != "取消") {
							item.disabled = false;
						} else {
							item.disabled = true;
						}
						item.selected = false;
					}
				}
			}
		},
		initRoomList: function(cuid) {
			var self = this;
			var uri = "/rest/WireFinishCutAction/getRoomList/";
			$.ajax({
				url: ctx + uri + cuid + "?time=" + new Date().getTime(),
				success: function(data) {
					if (data && data.roomList) {
						var roomValues = [];
						var roomlabels = [];
						for (var i = 0; i < data.roomList.length; i++) {
							var room = data.roomList[i];
							var labelCn = room.LABEL_CN,
								rcuid = room.CUID;
							roomValues.push(rcuid);
							roomlabels.push(labelCn);
						}
						self.aRoomSelect.setValues(roomValues);
						self.aRoomSelect.setLabels(roomlabels);
						self.aRoomSelect.setValue(roomValues[0]);
					}
				},
				dataType: 'json',
				type: 'GET'
			});
		},
		initPortListModel: function() {
			var self = this;
			var deviceDto = self.fibers[0].getParent().getParent();
			WireFinishCutAction.initPostList(deviceDto.a('CUID'),{
				callback:function(results){
					results.forEach(function(result){
						node = new ht.Node();
						node.a('CUID', result.CUID);
						node.setId(result.CUID);
						node.setName(result.LABEL_CN);
						node.setTag(result.CUID);
						node.setIcon(result.ICON);
						var cName = result.CUID.split('-')[0];
						node.a('bmClassId', cName);
						self.leftDm.add(node);
					});
				}
			});
		},
		removePrePort:function(){
			var self = this;
			var zdata = self.zCancelData;
			if (zdata) {
				for (var i = 0; i < zdata.length; i++) {
					var fData = zdata[i];
					var pData = fData._children;
					var deleteData = new Array();
					for (var j = 0; j < pData.size(); j++) {
						var nData = pData.get(j);
						var newData = nData.getAttr("newnode");
						var nName = nData.getName();
						if (newData || nName.indexOf('预割接') >-1) {
							deleteData.push(nData);
						}
					}
					if (deleteData && deleteData.length > 0) {
						for (var k = 0; k < deleteData.length; k++) {
							var dData = deleteData[k];
							pData.remove(dData);
							self.rightDm.remove(dData);
						}
					}
				}
			}
		}
	});
})(this,Object);
