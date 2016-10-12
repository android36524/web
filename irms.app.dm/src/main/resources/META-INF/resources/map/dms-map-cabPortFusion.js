/**
 * 光交接箱端子直熔
 */
$importjs(ctx+'/map/uuid.js');
$importjs(ctx+'/dwr/interface/FiberCabFusionAction.js');
$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
(function(window,object,undefined){
	dms.cabPortFusion = function(pointCuid,dialog){
		var self = this;
		self.dialog = dialog;
		self.fcabCuid = pointCuid;
		self.cancelArray = [];
		self.deleteArray = [];
		var c = self.createPanel();
		self.loadWireDataByFiberCabCuid(pointCuid);
		return c;
	};
	
	ht.Default.def('dms.cabPortFusion',ht.widget.FormPane,{
		leftdm : null,
		rightdm : null,
		dialog : null,
		fcabCuid : null,
		leftTree:null,
		rightTree: null,
		fcabPortList : null,
		leftFiberList : null,
		rightFiberList : null,
		fusionBtn : null,
		deleteBtn : null,
		confirmBtn : null,
		cancelBtn : null,
		isConnect : true,
		cancelArray : null,
		leftCancelArray : null,
		rightCancelArray : null,
		deleteArray : null,
		createPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			var leftDm = self.leftdm =  new ht.DataModel(),
				rightDm = self.rightdm = new ht.DataModel();
			var leftTreeView = self.leftTree = new ht.widget.TreeView(leftDm);
			var rightTreeView = self.rightTree = new ht.widget.TreeView(rightDm);
			
			var leftFiberPanel = self.createFiberPanel(leftTreeView);
			var rightFiberPanel = self.createFiberPanel(rightTreeView);
			var splitView = new ht.widget.SplitView(leftFiberPanel, rightFiberPanel, 'horizontal', 0.5);
			var bottomPanel = self.createBottomPanel();
			borderPane.setCenterView(splitView,500);
			borderPane.setBottomView(bottomPanel,40);

			return borderPane;
		},
		clearDefaultValues : function(){
			self.cancelArray = [];
			self.leftCancelArray = [];
			self.rightCancelArray =[];
			self.deleteArray = [];
		},
		createFiberPanel : function(treeView){
			var self = this;
			var tabView = new ht.widget.TabView();
			
			treeView.onDataDoubleClicked = function(data) {
				var parentCuid = data.getId();
				var pname = parentCuid.split('-')[0];
				if(pname == 'FCABPORT' || pname == 'WIRE_SYSTEM' || pname == 'FIBER_DP_PORT' || pname == 'ODFPORT'){
					return;
				}
				var expanded = data.getAttr("expanded");
				if(!expanded){
					self.getChildrenByParentCuid(parentCuid, treeView.getDataModel());
				}
			};
			tabView.add('纤芯列表', treeView, '#1ABC9C');
			return tabView;
		},
		createBottomPanel : function(){
			var self = this;
			var bottomPanel = new ht.widget.FormPane();
			bottomPanel.getView().style.border = '1px solid rgb(7,61,86)';
			var fBtn = self.fusionBtn = self.createButton('端子直熔'),
				dBtn = self.deleteBtn = self.createButton('断开直熔'),
				cBtn  = self.confirmBtn = self.createButton('确定'),
				celBtn  = self.cancelBtn = self.createButton('取消');
			
			self.setButtonDisabled(false);
			fBtn.onClicked = function(e){
				self.doRelevancy();
				self.isConnect = true;
			};
			dBtn.onClicked = function(e){
				var leftWireFiberList = self.leftdm.sm().getSelection();
				var rightWireFiberList = self.rightdm.sm().getSelection();
				if ((leftWireFiberList == null || leftWireFiberList.size() == 0) &&
						(rightWireFiberList == null || rightWireFiberList.size() == 0)) {
					tp.utils.optionDialog("温馨提示","请选择需要断开直熔的纤芯！");
					return;
				}
				
				self.setButtonDisabled(true);
				var leftCelArray = self.leftCancelArray = self.doDisConnection(self.leftdm);
				if(leftCelArray){
					var deleteLeftList = self.getDeleteDisconnection(leftWireFiberList);
					if(deleteLeftList){
						self.deleteArray = self.deleteArray.concat(deleteLeftList);
					}
					self.removeSelection(leftCelArray,self.leftdm);
				}
				
				var rightCelArray = self.rightCancelArray = self.doDisConnection(self.rightdm);
				
				if(rightCelArray){
					var deleteRightList = self.getDeleteDisconnection(rightWireFiberList);
					if(deleteRightList){
						self.deleteArray = self.deleteArray.concat(deleteRightList);
					}
					self.removeSelection(rightCelArray,self.rightdm);
				}
				
				self.isConnect = false;
			};
			cBtn.onClicked = function(e){
				var bid = 'FIBER_CAB';
				if(self.fcabCuid){
					bid = self.fcabCuid.split("-")[0];
				}
				if(self.isConnect){
					FiberCabFusionAction.addFibersConnect(self.leftFiberList,self.fcabPortList,self.rightFiberList,function(data){
						var flag = data.split("-")[0],
							msg = data.substring(6);
						
						if(flag=='true'){
							tp.utils.optionDialog("温馨提示","增加端子直熔成功！");
						}else{
							tp.utils.optionDialog("温馨提示","增加端子直熔失败:"+msg);
							self.clearAttrs();
						}
					});
				}else{
					var dels = [];
					if(self.deleteArray.length>0){
						self.deleteArray.forEach(function(data){
							var cid = data.a('CUID');
							dels.push(cid);
						});
					}
					FiberCabFusionAction.deleteFcabPortFibersConnect(dels,bid,function(data){
						if(data){
							tp.utils.optionDialog("温馨提示","删除端子直熔成功！");
							self.deleteArray=[];
						}else{
							tp.utils.optionDialog("温馨提示","删除端子直熔出错！");
						}
					});
				}
				
				self.setButtonDisabled(false);
				self.clearDefaultValues();
			};
			celBtn.onClicked = function(e){
				self.setButtonDisabled(false);
				self.clearAttrs();
			};
			bottomPanel.getView().style.borderTopWidth ='1';
			bottomPanel.getView().style.borderLeftWidth ='0';
			bottomPanel.getView().style.borderRightWidth ='0';
			bottomPanel.getView().style.borderBottomWidth ='0';

			bottomPanel.addRow([null,fBtn,null,dBtn,null,cBtn,null,celBtn],[0.2,80,5,80,5,80,5,80],23);
			return bottomPanel;
		},
		createButton : function(label){
			var self = this;
			var btn = new ht.widget.Button();
			btn.setLabel(label);
			self.setButton(btn);
			return btn;
		},
		setButtonDisabled : function(disable){
			var self = this;
			self.fusionBtn.setDisabled(disable);
			self.deleteBtn.setDisabled(disable);
			self.confirmBtn.setDisabled(!disable);
			self.cancelBtn.setDisabled(!disable);
		},
		clearAttrs : function(){
			var self = this;
			if(self.isConnect){
				self.removeNodes(self.leftdm);
				self.removeNodes(self.rightdm);
			}else{
				self.addCancelNodes(self.leftCancelArray,self.leftdm);
				self.addCancelNodes(self.rightCancelArray,self.rightdm);
			}
		},
		getDeleteDisconnection : function(list){
			var self = this;
			var deleteList = [];
			list.each(function(data){
				var cid = data.a('CUID'),
					cName = cid.split("-")[0];
				
				if(cName == 'FIBER'){
					if(!self.contains(deleteList, data)){
						deleteList.push(data);
					}
				}else if(cName == 'FCABPORT'||cName == 'FIBER_DP_PORT'||cName == 'ODFPORT'){
					var parent = data.getParent();
					if(parent){
						if(!self.contains(deleteList, parent)){
							deleteList.push(parent);
						}
					}
				}
				
			});
			return deleteList;
		},
		addCancelNodes : function(attrArray,dm){
			if(attrArray.length>0){
				attrArray.forEach(function(cancelData){
					var cancelCuid = cancelData.a('CUID');
					var old = dm.getDataById(cancelCuid);
					if(!old){
						var parent = dm.getDataById(cancelData.a('RELATED_FIBER_CUID'));
						if(parent){
							cancelData.setParent(parent);
						}
						dm.add(cancelData);
					}
				});
			}
		},
		removeNodes : function(dm){
			var self = this;
			var attrArray = self.cancelArray;
			var datas = [];
			dm.each(function(data){
				var cuid = data.a('CUID');
				if(attrArray.length>0){
					attrArray.forEach(function(cancelData){
						var cancelCuid = cancelData.a('CUID');
						if(cuid == cancelCuid){
							datas.push(data);
						}
					});
				}
			});
			//端子直熔时取消
			if(datas.length>0){
				for(var i=0;i<datas.length;i++){
					dm.remove(datas[i]);
				}
			}
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		addNode : function(dataModel,cid,label){
			var node = dataModel.getDataById(cid);
			if(!node){
				node = new ht.Node();
				node.a('CUID',cid);
				node.setId(cid);
				node.setName(label);
				var cName = cid.split('-')[0];
				node.a('bmClassId',cName);
				node.setImage(cName);
				dataModel.add(node);
			}
			return node;
		},
		getNode : function(datas,dm){
			var self = this;
			datas.forEach(function(data){
				var sysCuid = data.CUID,
					sysLabel = data.LABEL_CN;
				var segs = data.SEGS;
				var node = self.addNode(dm, sysCuid, sysLabel);
				if(segs){
					segs.forEach(function(seg){
						var segCuid = seg.CUID,
							seglabel = seg.LABEL_CN;
						var segNode = self.addNode(dm, segCuid, seglabel);
						if(node){
							segNode.setParent(node);
						}
					});
				}
			});
		},
		getChildrenByParentCuid: function(parentCuid, dm) {
			var self = this;
			var pname = parentCuid.split("-")[0];
			var bid = 'FIBER_CAB';
			if(self.fcabCuid){
				bid = self.fcabCuid.split("-")[0];
			}
			if(pname == 'FIBER'){
				FiberCabFusionAction.getChildrenByParentCuid(parentCuid,bid,function(data){
					if (data) {
						var cuid = data.CUID;
						var children = data.PORTS;
						var parent = dm.getDataById(cuid);
						if(children){
							for (var i = 0; i < children.length; i++) {
								var child = children[i];
								var icon = child.ICON;
								var bmClassId = child.BM_CLASS_ID;
								var htdata = new ht.Node(child.CUID);
								htdata.setId(child.CUID);
								htdata.a("CUID", child.CUID);
								if (parentCuid.indexOf("FIBER-") > -1) {
									var name = child.PORTNAME;
									htdata.setName(name);
								} else {
									htdata.setName(child.LABEL_CN);
								}

								if (icon)
									htdata.setIcon(icon);
								if (bmClassId)
									htdata.setAttr("BM_CLASS_ID", bmClassId);
								
								if (parent)
									parent.addChild(htdata);

								dm.add(htdata);
							}
						}
						if (parent)
							parent.a("expanded", true);
					}
				});
			}else{
				$.ajax({
				url: ctx + "/rest/JumpLinkService/getChildrenByParentCuid/" + parentCuid + "?time=" + new Date().getTime(),
				success: function(data) {
					if (data) {
						var cuid = data.cuid;
						var children = data.children;
						var parent = dm.getDataById(cuid);
						if(children){
							for (var i = 0; i < children.length; i++) {
								var child = children[i];
								var icon = child.ICON;
								var bmClassId = child.BM_CLASS_ID;
								var htdata = new ht.Node(child.CUID);
								htdata.setId(child.CUID);
								htdata.a("CUID", child.CUID);
								if (parentCuid.indexOf("FIBER-") > -1) {
									var name = child.PORTNAME;
									htdata.setName(name);
								} else {
									htdata.setName(child.LABEL_CN);
								}
		
								if (icon)
									htdata.setIcon(icon);
								if (bmClassId)
									htdata.setAttr("BM_CLASS_ID", bmClassId);
								
								if (parent)
									parent.addChild(htdata);

								dm.add(htdata);
							}
						}
						
						if (parent)
							parent.a("expanded", true);
					}
				},
				dataType: 'json',
				type: 'GET'
			});
			}
		},
		//根据光交接箱加载光缆数据
		loadWireDataByFiberCabCuid : function(fCuid){
			var self = this;
			try {
				FiberCabFusionAction.getWireSegAndWireSystemByCuid(fCuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							self.getNode(datas,self.leftdm);
							self.getNode(datas,self.rightdm);
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有相关的纤芯列表！</div>');
						}
					},
					errorHandler : function(errorString) {
						tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
						tp.utils.wholeUnLock();
						return;
					}
				});
			} catch (e) {
				tp.utils.wholeUnLock();
			}
		},
		doDisConnection : function(dm){
			var self = this;
			var celArray = [];
			var sections=dm.sm().getSelection();
			for(var i=0;i<sections.size();i++){
				var section = sections.get(i);
				var cuid = section.a('CUID'),
					className = cuid.split('-')[0];
				if(className == 'FIBER'){
					var children = section.getChildren();
					if(children){
						for(var j=0;j<children.size();j++){
							var child = children.get(j);
							if(!self.contains(celArray, child)){
								celArray.push(child);
							}
						}
					}
				}else if(className == 'FCABPORT' || className == 'FIBER_DP_PORT' || className == 'ODFPORT'){
					var parent = section.getParent();
					var children = parent.getChildren();
					if(children){
						for(var i=0;i<children.size();i++){
							var child = children.get(i);
							if(!self.contains(celArray, child)){
								celArray.push(child);
							}
						}
					}
				}
			}
			return celArray;
		},
		removeSelection : function(celArray,dm){
			if(celArray.length>0){
				for(var i=0;i<celArray.length;i++){
					var delNode = celArray[i];
					var relatedFiberCuid = delNode.a('RELATED_FIBER_CUID');
					if(!relatedFiberCuid){
						var parentCuid = delNode.getParent().a('CUID');
						delNode.a('RELATED_FIBER_CUID',parentCuid);
					}
					dm.remove(delNode);
				}
			}
		},
	    contains : function(children,child)
	    {
	    	var childCuid = child.a('CUID');
	    	for(var i=0;i<children.length;i++){
	    		var old = children[i],
	    			cuid = old.a('CUID');
	    		if(childCuid == cuid){
	    			return true;
	    		}
	    		return false;
	    	}
	    },
		doRelevancy : function(){
			var self = this;
			var leftSections = self.leftdm.sm().getSelection();
			var rightSections = self.rightdm.sm().getSelection();
			if(leftSections.size() == 0 || rightSections.size() == 0){
				tp.utils.optionDialog('温馨提示','请选择需要直熔的纤芯！');
				return;
			}
			if(leftSections.size() != rightSections.size()){
				tp.utils.optionDialog('温馨提示','两端选择的纤芯数不一致！');
				return;
			}
			var leftChildren = leftSections.get(0).getChildren();
			var rightChildren = rightSections.get(0).getChildren();
			
			if(leftChildren.size() > 0 || rightChildren.size() > 0)
			{
				tp.utils.optionDialog('温馨提示','已经直熔，不能继续直熔！');
				return;
			}
			self.fcabPortList = [];
			self.leftFiberList = [];
			self.rightFiberList =[];
			parentClass = self.fcabCuid.split("-")[0];
			if(parentClass=="ODF"){
				var relatedModuleCuid = Math.generateCUID("ODFMODULE");
				var count = Math.ceil(leftSections.size()/12);
				for(var i=0;i<count;i++){
					var leftSection = leftSections.get(i),
						rightSection = rightSections.get(i);
					var leftId = leftSection.a('CUID'),
					rightId = rightSection.a('CUID');
					self.leftFiberList.push(leftId);
					self.rightFiberList.push(rightId);
					var leftBid = leftId.split('-')[0],
						rightBid = rightId.split('-')[0];
					if(!(leftBid == 'FIBER' && rightBid == 'FIBER')){
						tp.utils.optionDialog('温馨提示','做关联时纤芯列表中必须选择纤芯！');
						return;
					}
					var leftParentId = leftSection.getParent().a('CUID'),
						rightParentId = rightSection.getParent().a('CUID');
					if(leftParentId == rightParentId){
						tp.utils.optionDialog('温馨提示','请选择不同的光缆段！');
						return;
					}
					var name = leftSection.getName()+"-"+rightSection.getName();
					self.leftTree.expand(leftSection);
					self.rightTree.expand(rightSection); 
					
					
	          		for(var i=0;i < 12;i++){
	          			var resCuid = Math.generateCUID("ODFPORT");													
	          			var fport = {"CUID":resCuid,"RELATED_DEVICE_CUID" : self.fcabCuid,"LABEL_CN":name,"RELATED_MODULE_CUID" : relatedModuleCuid};
	          			self.fcabPortList.push(fport);
	          		}
			          	
					}
				for(var i=0;i<leftSections.size();i++){
					var leftSection = leftSections.get(i),
					rightSection = rightSections.get(i);
					var name = leftSection.getName()+"-"+rightSection.getName();
					var leftNode = self.setZRNodeValue(leftSection,self.leftdm,name,true);
					//右侧和左侧应该是一个node
					var rightNode = self.setZRNodeValue(rightSection,self.rightdm,name);
					rightNode.a('CUID',fportId);
					//创建端子，并存到cancelArray（remove端子时使用）
					self.cancelArray.push(leftNode);
					self.setButtonDisabled(true);
				}
			}
			else{
				for(var i=0;i<leftSections.size();i++){
					var leftSection = leftSections.get(i),
						rightSection = rightSections.get(i);
					var leftId = leftSection.a('CUID'),
						rightId = rightSection.a('CUID');
					self.leftFiberList.push(leftId);
					self.rightFiberList.push(rightId);
					var leftBid = leftId.split('-')[0],
						rightBid = rightId.split('-')[0];
					if(!(leftBid == 'FIBER' && rightBid == 'FIBER')){
						tp.utils.optionDialog('温馨提示','做关联时纤芯列表中必须选择纤芯！');
						return;
					}
					var leftParentId = leftSection.getParent().a('CUID'),
						rightParentId = rightSection.getParent().a('CUID');
					if(leftParentId == rightParentId){
						tp.utils.optionDialog('温馨提示','请选择不同的光缆段！');
						return;
					}
					var name = leftSection.getName()+"-"+rightSection.getName();
					self.leftTree.expand(leftSection);
					self.rightTree.expand(rightSection);
					var leftNode = self.setZRNodeValue(leftSection,self.leftdm,name,true);
					var fportId = leftNode.a('CUID'),
						fportName = leftNode.getName(),
						relatedDeviceCuid = leftNode.a('RELATED_DEVICE_CUID');
					//右侧和左侧应该是一个node
					var rightNode = self.setZRNodeValue(rightSection,self.rightdm,name);
					rightNode.a('CUID',fportId);
					//创建端子，并存到cancelArray（remove端子时使用）
					self.cancelArray.push(leftNode);
					var fport = {"CUID":fportId,"LABEL_CN":fportName,"RELATED_DEVICE_CUID" : relatedDeviceCuid};
					self.fcabPortList.push(fport);
					self.setButtonDisabled(true);
				}
			}
		},
		setZRNodeValue : function(parent,dm,name,isLeft){
			var self = this;
			var node = new ht.Node();
			var bmCid = '';
			node.setParent(parent);
			node.setName('ZR-'+name);
			parentClass = self.fcabCuid.split("-")[0];
			if(parentClass=='FIBER_CAB'){
				bmCid = 'FCABPORT';
			}
			else if(parentClass=='FIBER_DP'){
				bmCid = 'FIBER_DP_PORT';
			}
			else{
				bmCid = 'ODFPORT';
			}
			if(isLeft && bmCid!='ODFPORT'){				
      			var resCuid = Math.generateCUID(bmCid);
      			node.a('CUID',resCuid);   
			}
			node.a('RELATED_DEVICE_CUID',self.fcabCuid);
			dm.add(node);
			return node;
		}
	});
})(this,Object);