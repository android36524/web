//Ext.ns('Frame.op');
$importjs(ctx+'/dwr/interface/FiberLinkAction.js');

ht.Default.setImage('/resources/topo/alarm/c.png',ctx+'/resources/topo/alarm/c.png');
ht.Default.setImage('/resources/topo/alarm/y.png',ctx+'/resources/topo/alarm/y.png');
ht.Default.setImage('/resources/topo/alarm/w.png',ctx+'/resources/topo/alarm/w.png');
ht.Default.setImage('/resources/topo/alarm/m.png',ctx+'/resources/topo/alarm/m.png');
ht.Default.setImage('/resources/topo/alarm/u.png',ctx+'/resources/topo/alarm/u.png');
ht.Default.setImage('/resources/topo/dm/fixed.png',ctx+'/resources/topo/dm/fixed.png');
ht.Default.setImage('/resources/topo/dm/isfixed.gif',ctx+'/resources/topo/dm/isfixed.gif');
ht.Default.setImage('/resources/topo/dm/Ddfmodule.gif',ctx+'/resources/map/FIBER_CAB.png');

// 光缆（段）
ht.Default.setImage('/resources/map/WIRE_SEG.png',ctx+'/resources/map/WIRE_SEG.png');
// 纤芯
ht.Default.setImage('/resources/topo/dm/isfixed.gif',ctx+'/resources/topo/dm/isfixed.gif');

/**
 * 光交接箱纤芯关联 ht panel
 */
FiberLinksMainView = function (title, dataModel, owner) {
    FiberLinksMainView.superClass.constructor.apply(this);

    var self = this;
    self.dataModel = dataModel;
    self.fiberDpName = "";
    self.owner = owner;

    var div_title = document.createElement("div");
    var _inner_div = document.createElement("div");
    _inner_div.innerHTML = title;
    _inner_div.style.borderBottom = '2px solid #ccc';
    //_inner_div.style.lineHeight = '24px';
    _inner_div.style.padding = "5px";
    div_title.appendChild(_inner_div);

    self.treeView = new ht.widget.TreeView(self.dataModel);
    self.treeView.onDataDoubleClicked = function (data) {
    	self.onDataDoubleClicked(data);
    };
    self.setTopView(div_title);
    self.setCenterView(self.treeView);

    window.addEventListener('resize', function (e) {
        self.invalidate();
    }, false);
};

ht.Default.def("FiberLinksMainView", ht.widget.BorderPane, {
	/**
	 * 清空列表数据
	 */
	clearDataModel: function() {
		var scope = this;
		scope.dataModel.clear();
	},
	
	_addNode: function(dataModel, parent, cuid, name, icon) {
		var node = new ht.Node(); // 光缆节点
		node.setName(name);
		if (cuid) {
			node.setId(cuid);
		}
		if (icon) {
			node.setIcon(icon);
		}
		if (parent){
			parent.addChild(node);
		}
		dataModel.add(node);
		
		return node;
	},
	
	// 双击事件
	onDataDoubleClicked: function(data) {
		var self = this;
		var id = data.getId();
		if (!data || !id) {
			return false;
		}
		var nodeType = (id + "").split("-")[0];
		
		if ("FCABPORT" == nodeType || "FIBER_DP_PORT" == nodeType
				|| "ODFPORT" == nodeType || "PTP" == nodeType || nodeType == "FIBER_JOINT_POINT") {
            self.owner.doQuery();
		} else if ("WIRE_SEG" == nodeType) {
			self.loadFiberLinkData(data);
		} else if ("FIBER" == nodeType) {
//			self.loadPortsByFiber(data);
			self.owner.doQueryPort();
		}
	},

    /**
     * 根据光分纤箱加载端子数据
     * @param fiberDpCuid 光分纤箱CUID
     */
    loadPortData: function (box,portCuid) {
    	 var scope = this;
         // 渲染数据
         var renderData = function (data) {
             // 端子数据
             var ports = data;
             for (var i = 0; i < ports.length; i++) {
                 var port = ports[i];
                 var id = port['CUID'];
                 var name = port['LABEL_CN'];
                 var parentId = port['PARENT_CUID'];
                 var bmClassId = port['BM_CLASS_ID'];
                 var relatedNeCuid = port['RELATED_NE_CUID'];
            	 if(bmClassId == 'AN_ONU')
             	 {
                     var pos = scope._addNode(scope.dataModel, null, id, name, rootIcon);
                     posDevices.push(pos);
             	 }
            	 else if(bmClassId == 'PTP')
            	 {
            		 var parentPos = scope.dataModel.getDataById(relatedNeCuid);
            		 scope._addNode(scope.dataModel, parentPos, id, name, port['ICON']);
            	 }
                 else if(bmClassId != 'PTP')
             	 {
                 	var parent = scope.dataModel.getDataById(parentId);
                 	scope._addNode(scope.dataModel, parent, id, name, port['ICON']);
                 	// 加载端子
                 	var portNodes = parent.getChildren();
                 	portNodes.each(function(data){
                 		scope.loadPortNodesData(data,box,portCuid);
                 	}, portNodes);
             	 }
             }
         };
         if(box.cuid.indexOf('ACCESSPOINT_') != -1)
    	 {
        	 //查询接入点下各设备（交接箱、分纤箱、终端盒、ONU、POS、ODF）
        	 FiberLinkAction.getAccesspointChildren(box.cuid, function(datas){
        		 scope.clearDataModel();
        		 if (!datas || !datas.length) {
        			 return;
        		 }
        		 var cuids = [];
        		 var node = new ht.Node();
        		 node.setId("FIBER_CAB");
        		 node.setName("光交箱");
        		 scope.dataModel.add(node);
        		 node = new ht.Node();
        		 node.setId("FIBER_DP");
        		 node.setName("光分纤箱");
        		 scope.dataModel.add(node);
        		 node = new ht.Node();
        		 node.setId("FIBER_JOINT_BOX");
        		 node.setName("终端盒");
        		 scope.dataModel.add(node);
        		 node = new ht.Node();
        		 node.setId("ONU");
        		 node.setName("ONU");
        		 scope.dataModel.add(node);
        		 node = new ht.Node();
        		 node.setId("POS");
        		 node.setName("POS");
        		 scope.dataModel.add(node);
        		 node = new ht.Node();
        		 node.setId("ODF");
        		 node.setName("ODF");
        		 scope.dataModel.add(node);
        		 for(var i = 0; i < datas.length; i++)
        		 {
        			 var data = datas[i];
        			 cuids.push(data.CUID);
        			 scope._addNode(scope.dataModel, null, data.CUID, data.LABEL_CN, null);
        			 var parent = scope.dataModel.getDataById(data.DEVICE_TABLE_TYPE);
        			 scope.dataModel.getDataById(data.CUID).setParent(parent);
        		 }
        		 //查询模块
        		 FiberLinkAction.getChildrenByParentCuids(cuids,renderData);
        	 });
    	 }
         else
    	 {
        	 FiberLinkAction.getChildrenByParentCuid(box.cuid,renderData);
    	 }
         
    },
    
    // 加载光缆数据
    loadWireData: function(fiberDpCuid) {
    	var scope = this;
        // 渲染数据
    	if(fiberDpCuid.indexOf('INTER_WIRE') != -1)
		{
    		var renderData = function (data) {
    			scope.clearDataModel();
    			if (!data || !data.length || data.length != 1) {
    				return;
    			}
    			var wireSegList = data[0];   // 光缆段
    			var dm = scope.dataModel;
    			for (var i=0;i<wireSegList.length;i++){
    				var sys = wireSegList[i];
    				var sysCuid = sys['CUID'];
    				// 光缆节点
    				var sysNode = scope._addNode(dm, null, sysCuid,sys['LABEL_CN'],sys['ICON'], {type: 'WIRE_SEG'});
    				// 加载纤芯
    				scope.loadFiberLinkData(sysNode);
    			}
    		};
    		
    		// 加载数据
    		FiberLinkAction.getWireInfoByInterwire(fiberDpCuid, renderData);
		}
    	else
		{
    		var renderData = function (data) {
    			scope.clearDataModel();
    			if (!data || !data.length || data.length != 2) {
    				return;
    			}
    			var wireSystemList = data[0];   // 光缆
    			var wireSegList = data[1];   // 光缆段
    			var dm = scope.dataModel;
    			for (var i=0;i<wireSystemList.length;i++){
    				var sys = wireSystemList[i];
    				var sysCuid = sys['CUID'];
    				// 光缆节点
    				var sysNode = scope._addNode(dm, null, sysCuid,sys['LABEL_CN'],sys['ICON'], {type: 'WIRE'});
    				for (var j = 0; j < wireSegList.length; j++) {
    					var seg = wireSegList[j];
    					if (seg['RELATED_SYSTEM_CUID'] != sysCuid) {
    						continue;
    					}
    					// 光缆段节点
    					var segNode = scope._addNode(dm, sysNode, seg['CUID'], seg['LABEL_CN'], seg['ICON'], {type: 'WIRE_SEG'});
    				}
    				if (scope._centerView.isExpanded(sysNode) == false) {
    					scope._centerView.expand(sysNode);
    				}
    				// 加载纤芯
    				var segNodes = sysNode.getChildren();
    				segNodes.each(function(data){
    					scope.loadFiberLinkData(data);
    				}, segNodes);
    			}
    		};
    		
    		// 加载数据
    		FiberLinkAction.getWireInfoByFCab(fiberDpCuid, renderData);
		}
    },
    
    // 根据光缆段， 加载纤芯数据
    loadFiberLinkData: function (segData) {
    	var scope = this;
        var renderData = function (data) {
        	var dm = scope.dataModel;

        	// 先清空当前所选节点下的数据，再添加
        	var childList = segData.getChildren();
        	for (var i = childList.size() - 1; i >= 0; i--) {
        		dm.remove(childList.get(i));
        	}
        	
        	if (!data || !data.length) {
                return;
            }
        	for (var i = 0; i< data.length; i++){
        		var fiber = data[i];
        		var _node = scope._addNode(dm, segData, fiber['CUID'], fiber['LABEL_CN'], fiber['ICON']);
        		if (fiber['ORIG_POINT_CUID'] || fiber['DEST_POINT_CUID']) {
        			scope.loadPortsByFiber(_node);
        		}
        	}
        };
        var segCuid = segData.getId();
        FiberLinkAction.getFiberByWireSeg(segCuid, renderData);
    },
    
   // 查询端子
    loadPortNodesData: function (segData,box,portCuid) {
    	var scope = this;
        var renderData = function (data) {
        	var dm = scope.dataModel;

        	// 先清空当前所选节点下的数据，再添加
        	var childList = segData.getChildren();
        	for (var i = childList.size() - 1; i >= 0; i--) {
        		dm.remove(childList.get(i));
        	}
        	
        	if (!data || !data.length) {
                return;
            }
        	for (var i = 0; i< data.length; i++){
        		var fiber = data[i];
        		var _node = scope._addNode(dm, segData, fiber['CUID'], fiber['LABEL_CN'], fiber['ICON']);
        		if (fiber['ORIG_POINT_CUID'] || fiber['DEST_POINT_CUID']) {
        			scope.loadPortsByFiber(_node);
        		}
        	}
        	if(portCuid)
            {
            	var selectedPort = dm.getDataById(portCuid);
            	if(selectedPort)
            	{
            		dm.sm().setSelection(selectedPort);
            		setTimeout(function(){box.doQuery();},2000);
            	}
            }
        };
        var segCuid = segData.getId();
        FiberLinkAction.getPortByMoudle(segCuid, renderData);
    },
    // 根据纤芯查找相关的端子
    loadPortsByFiber: function (fiberData) {
    	var scope = this;
    	var renderData = function(data) {
    		var dm = scope.dataModel;
    		
        	// 先清空当前所选节点下的数据，再添加
        	var childList = fiberData.getChildren();
        	for (var i = childList.size() - 1; i >= 0; i--) {
        		dm.remove(childList.get(i));
        	}
        	
        	if (!data || !data.length) {
                return;
            }
            // 获取当前所选纤芯的扩展属性
        	for (var i = 0; i < data.length; i++) {
        		var port = data[i];
                var id = fiberCuid + ":" + port['CUID'];
        		scope._addNode(dm, fiberData, id, port['LABEL_CN'], port['ICON']);
        	}
    	};
    	var fiberCuid = fiberData.getId();
    	FiberLinkAction.getSidePointsByFiberCuid(fiberCuid, renderData);
    }
});

FiberLinksView = function (fiberDpCuid, fiberDpName,portCuid) {
    var self = this;
    self.cuid = fiberDpCuid;
    self.fiberDpName = fiberDpName;

    FiberLinksView.superClass.constructor.apply(this);
    self.getItemBorderColor = function(){return 'gray';};
    
    var buttonItems = [
        {
            type: 'button', label: '查询端子关联的纤芯', disabled: false,
            action: function (item) {
                self.doQuery();
            }
        },
        {
            type: 'button', label: '关联', disabled: false,
            action: function (item) {
                self.doLink();
            }
        },
        {
            type: 'button', label: '断开', disabled: false,
            action: function (item) {
                self.disconnect();
            }
        },
        {
            type: 'button', label: '确定', disabled: true,
            action: function (item) {
                self.doConfirm();
            }
        },
        {
            type: 'button', label: '取消', disabled: true,
            action: function (item) {
                self.doCancel();
            }
        }
    ];

    var aModel = new ht.DataModel();
    var zModel = new ht.DataModel();
    self.aListView = new FiberLinksMainView("端子列表", aModel, self);
    self.zListView = new FiberLinksMainView("纤芯列表", zModel, self);
    self.buttonToolbar = new ht.widget.Toolbar(buttonItems);
    self.setBottomView(self.buttonToolbar);
    self.buttonToolbar.setStickToRight(true);
    var splitView = new ht.widget.SplitView(self.aListView, self.zListView, 'horizontal', 0.4);
    self.setCenterView(splitView);

    // 初始化数据
    self.initDataModel(portCuid);
};

ht.Default.def('FiberLinksView', ht.widget.BorderPane, {
	
    // 页面数据初始化
    initDataModel: function (portCuid) {
        var scope = this;

        this.undoData = {
    		action: '', // add：表示关联, del：表示断开
    		datas: []   // 记录add或del操作，涉及到的数据 
    	}; 
        this.aTreeView = scope.aListView._centerView; // BorderPane.getCenterView()方法有bug
        this.zTreeView = scope.zListView._centerView;
        this.aDataModel = this.aTreeView.getDataModel();
        this.zDataModel = this.zTreeView.getDataModel();
        this.aSelections = this.aDataModel.getSelectionModel().getSelection(); // 左侧端子选择的数据
        this.zSelections = this.zDataModel.getSelectionModel().getSelection(); // 右侧纤芯选择的数据
        
    	scope.aListView.loadPortData(scope,portCuid);
        scope.zListView.loadWireData(scope.cuid);
    },
    // 查询端子关联的纤芯
    doQuery: function () {
        var scope = this;
        if (scope.aSelections.size() == 0) {
            return false;
        }
        var selectDatas = [];
        var dpPorts = scope.aSelections;
        dpPorts.each(function(dpPort) {
            var _dpPortId = dpPort.getId();
            scope.zDataModel.each(function(zdata) {
                var id = zdata.getId();
                if (id.indexOf(":") >= 0) { // 说明是纤芯下的端子节点
                    var portId = id.split(":")[1];
                    if (portId == _dpPortId) {
                        selectDatas.push(zdata);
                        if (scope.zTreeView.isExpanded(zdata.getParent()) == false) {
                            scope.zTreeView.expand(zdata.getParent());
                        }
                        if (scope.zTreeView.isExpanded(zdata.getParent().getParent()) == false) {
                            scope.zTreeView.expand(zdata.getParent().getParent());
                        }
                    }
                }
            }, scope.zDataModel);
        }, dpPorts);
        scope.zDataModel.sm().clearSelection();
        scope.zDataModel.sm().setSelection(new ht.List(selectDatas));
    },
    //根据纤芯定位端子
    doQueryPort : function() {
        var scope = this;
        if (scope.zSelections.size() == 0) {
            return false;
        }
        var selectDatas = [];
        var cabFibers = scope.zSelections;
        cabFibers.each(function(cabFiber) {
            var _cabFiberId = cabFiber.getId();
            if (_cabFiberId.indexOf(":") >= 0) {
            	var portId = _cabFiberId.split(":")[1];
            	scope.aDataModel.each(function(adata) {
                    var id = adata.getId();
                    // 说明是纤芯下的端子节点
                    if (portId == id) {
                        selectDatas.push(adata);
                        if (scope.aTreeView.isExpanded(adata.getParent()) == false) {
                            scope.aTreeView.expand(adata.getParent());
                        }
                    }
                }, scope.aDataModel);
            }
        }, cabFibers);
        scope.aDataModel.sm().clearSelection();
        scope.aDataModel.sm().setSelection(new ht.List(selectDatas));
    },
    // 关联操作
    doLink: function () {
    	var nodeType = "FCABPORT"; // 端子列表中的节点类型
    	var asls = this.aSelections,
    	zsls = this.zSelections,
    	alength = asls.size(),
    	zlength = zsls.size();
    	
    	for(var i = 0; i < alength; i++) {
        	var _d = asls.get(i);
        	nodeType = _d.getId().split("-")[0];
        	if (!nodeType || (nodeType != "FCABPORT" && nodeType != "FIBER_DP_PORT"
        		&& nodeType != "ODFPORT" && nodeType != "PTP" && nodeType != "FIBER_JOINT_POINT")) {
        		nodeType = null;
        		tp.utils.optionDialog('错误提示信息',"选择的不是端子类型！");
        		return;
        	}
    	}
		if (nodeType != 'FCABPORT' && nodeType != "FIBER_DP_PORT"
			&& nodeType != 'ODFPORT' && nodeType != "PTP" && nodeType != "FIBER_JOINT_POINT") {
    		tp.utils.optionDialog('错误提示信息',"请选择端子！"); 
            return;
    	}
		if(alength == 0){
			tp.utils.optionDialog('错误提示信息',"请选择端子！"); 
            return;
		}
    	if (zlength == 0){
    		tp.utils.optionDialog('错误提示信息',"请选择纤芯！"); 
            return;
    	}
    	if(alength !== zlength){
    		tp.utils.optionDialog('错误提示信息',"选择的端子和纤芯数量不一致！"); 
            return;
    	}
    	var i = 0;
    	nodeType = "FIBER"; // 纤芯列表中的节点类型
    	var isBatchOp = zlength > 1; // 是否批量关联操作
    	if (isBatchOp) {
        	for (i = 0; i < zlength; i++) {
        		var _d = zsls.get(i);
                var _id = _d.getId();
                if (_id.indexOf(":") != -1) { // 说明该节点是纤芯下的端子
                    nodeType = null;
                } else {
                    nodeType = (_id.split("-"))[0];
                }
                if (nodeType == null || nodeType != "FIBER") { // 说明所选的节点中包含了非纤芯的节点
                	tp.utils.optionDialog('错误提示信息',"选择的包含了非纤芯的节点！"); 
                    return;
                }
        	}
        	if ("FIBER" != nodeType) {
        		tp.utils.optionDialog('错误提示信息',"批量操作时,请选择未关联端子的纤芯！"); 
                return;
        	}
    	} else {
    		var _d =zsls.get(0);
            var _id = _d.getId();
            if (_id.indexOf(":") == -1) {
                nodeType = (_id.split("-"))[0];
            } else {
                nodeType = null;
            }
            if (nodeType != "FIBER") {
            	tp.utils.optionDialog('错误提示信息',"请选择纤芯！"); 
                return;
            }
    	}
    	
    	// 计算可匹配的端子或纤芯数量
    	var count = Math.min(alength, zlength);
    	var aData = null, zData = null; // A，Z分别表示左右侧所选的数据
    	for (i = 0; i < count; i++) {
    		aData = asls.get(i);
    		zData = zsls.get(i);
    		if (zData.getChildren().size() == 2) {
    			var zname = zData.getName();
    			tp.utils.optionDialog('错误提示信息',"纤芯["+zname+"]的两端都已经关联了端子，不能再做关联！");
    			return;
//    			continue;
    		}
    		// 把左侧端子和右侧的纤芯关联, 前提是纤芯未关联端子
            var id = zData.getId() + ":" + aData.getId();
    		var node = this.zListView._addNode(this.zDataModel, zData, id, aData.getName(), aData.getIcon());
    		this.undoData.datas.push(node);
    		if (this.zTreeView.isExpanded(zData) == false) {
    			this.zTreeView.expand(zData);
    		}
    	}
    	// 置按钮状态
    	if (this.undoData.datas.length > 0){
            this.undoData.action = 'add';

        	var buttons = this.buttonToolbar.getItems();
        	console.info(buttons);
        	for (i = 0; i < buttons.length; i++) {
        		var item = buttons[i];
    			var label = item.label;
    			item.disabled = !(label == "确定" || label == "取消");
        	}
    	}
    },
    // 断开操作
    disconnect: function () {
    	if (this.aSelections.size() == 0 && this.zSelections.size() == 0) {
    		tp.utils.optionDialog("错误提示","请选择纤芯！");
            return;
    	}

        var aDataMap = {}; // 左侧选择的端子ID
        this.aSelections.each(function(data) {
            var _id = data.getId();
            nodeType = ((_id + "").split("-"))[0];
            if (nodeType == "FCABPORT" || nodeType == "FIBER_DP_PORT"
            	|| nodeType == "ODFPORT" || nodeType == "PTP" || nodeType == "FIBER_JOINT_POINT") {
                aDataMap[_id] = _id;
            }
        }, this.aSelections);

        var zDataMap = {}; // 右侧选择的端子ID
        this.zSelections.each(function(data) {
            var id = data.getId();
            var isFiberPort = id.indexOf(":") >= 0; // 表示当前节点是纤芯下的端子
            var nodeType = (id.split("-"))[0];

            if (isFiberPort) {
                zDataMap[id] = id.split(":")[1];
            } else if (nodeType == "FIBER") {
                var childs = data.getChildren();
                childs.each(function(data) {
                    var id = data.getId();
                    var cuid = (id.split(":"))[1];
                    zDataMap[id] = cuid;
                }, childs);
            }
        }, this.zSelections);

        var aCount = 0, zCount = 0;
        for (var id in aDataMap) {
            aCount++;
        }
        for (var id in zDataMap) {
            zCount++;
        }
        var dataIds = [];
        if (aCount > 0 && zCount > 0) { // 左右两侧取交集
            for (var aid in aDataMap) {
                for (var zid in zDataMap) {
                    if (aid == (zid.split(":"))[1]) {
                        dataIds.push(zid);
                    }
                }
            }
        } else if (aCount > 0) { // 右侧未选择，以左侧为准
            this.zDataModel.each(function(data) {
                var id = data.getId();
                var isFiberPort = id.indexOf(":") != -1; // 表示当前节点是纤芯下的端子
                var nodeType = (id.split("-"))[0];
                if (isFiberPort) {
                    var id = data.getId();
                    var _cuid = (id.split(":"))[1];
                    if (_cuid in aDataMap) {
                        dataIds.push(id);
                    }
                } else if (nodeType == "FIBER") {
                    var childs = data.getChildren();
                    childs.each(function(data) {
                        var id = data.getId();
                        var _cuid = (id.split(":"))[1];
                        if (_cuid in aDataMap) {
                            dataIds.push(id);
                        }
                    }, childs);
                }
            }, this.zDataModel);
        } else if (zCount > 0) { // 左侧未选择，以右侧为准
            for (var zid in zDataMap) {
                dataIds.push(zid);
            }
        }

        if (dataIds.length == 0) {
        	tp.utils.optionDialog("错误提示","请选择要断开的纤芯或端子！");
            return;
        }
		
		var fiberCuids = '';
		for(var i = 0; i < dataIds.length; i++) {
            var data = this.zDataModel.getDataById(dataIds[i]);
            if (!data) {
                continue;
            }
            var cuid = data.getId();
			var fibercuid = (cuid.split(":"))[0];
			fiberCuids +=','+fibercuid;
        }
		
		var isConfirmDel = false;
		DWREngine.setAsync(false);
		FiberLinkAction.isFiberInOpticalwayRoute(fiberCuids.substring(1,fiberCuids.length),function(data){
			var  prompt ='';
			if(data){
				prompt = '纤芯上承载了光路，确定要断开选择的纤芯吗?';
				tp.utils.showConfirmDialog({
				content : prompt,
				contentPadding : 25,
				action : function(item,e){
					if(item.id === 'ok'){
						isConfirmDel = true;
					}
					this.hide();
				}
			});
			}else{
				isConfirmDel = true;
			}

		});
		DWREngine.setAsync(true);
		if(!isConfirmDel){
			return;
		}

        for(var i = 0; i < dataIds.length; i++) {
            var data = this.zDataModel.getDataById(dataIds[i]);
            if (!data) {
                continue;
            }
            var node = new ht.Node();
            node.setName(data.getName());
            node.setIcon(data.getIcon());
            node.setId(data.getId());

            this.undoData.datas.push(node);
            this.zDataModel.remove(data);
        }

    	// 置按钮状态
    	if (this.undoData.datas.length > 0) {
            this.undoData.action = 'del';
	    	var buttons = this.buttonToolbar.getItems();
	    	for (i = 0; i < buttons.length; i++) {
	    		var item = buttons[i];
				var label = item.label;
				item.disabled = !(label == "确定" || label == "取消");
	    	}
    	}
    },
    // 确认操作
    doConfirm: function () {
        var self = this;
    	
        var act = self.undoData.action;
        var workData = self.undoData.datas;

        if (act == 'add') { // 执行“关联”操作
            var fiberCuids = [];
            var portCuids = [];
            for (var i = 0; i < workData.length; i++) {
                var _portData = workData[i];
                var _fiberId = _portData.getParent().getId();
                portCuids.push((_portData.getId().split(":"))[1]);
                fiberCuids.push(_fiberId);
            }

            FiberLinkAction.addFibersConnectByAccesspoint(fiberCuids.join(","), portCuids.join(","), function(fibers){
                if (!fibers || fibers.length == 0) {
                    return;
                }
                if (fibers[0] != 'SUCCESS') {
                	tp.utils.optionDialog('错误提示信息', fibers[0]);
                    self.doCancel();
                    return;
                }
                // 操作成功处理按钮状态
                setButtonState(true);
                // 没有成功关联的纤芯
                if (fibers.length == 0) {
                    return;
                }
                for (var i = 0; i < fibers.length; i++) {
                    var fiberData = self.zDataModel.getDataById(fibers[i]);
                    if (fiberData) {
                        self.zListView.loadPortsByFiber(fiberData);
                    }
                }

                // 更新左侧端子图标状态
                self.aListView.loadPortData(self);
            });
        }

        if (act == 'del') { // 执行“断开”操作
            var params = new Object();
            for (var i = 0; i < workData.length; i++) {
                var _portData = workData[i];
                var _ids = _portData.getId().split(":");
                var _fiberId = _ids[0];
                var _portId = _ids[1];
                if (_fiberId in params) {
                    params[_fiberId] = params[_fiberId] + "," + _portId;
                } else {
                    params[_fiberId] = _portId;
                }
            }

            FiberLinkAction.deleteFibersConnect(params, function(resp){
                if (resp['_MESSAGE'] != 'SUCCESS') {
                	tp.utils.optionDialog('错误提示信息', resp['_MESSAGE']);
                    self.doCancel();
                    return;
                }

                var failData = resp['_FAIL_DATA'];

                // 解析失败的消息
                var _msg = "";
                for (var fiberCuid in failData) {
                    _msg += failData[fiberCuid] + "\n";
                    // 从undo缓存中清除已经成功“断开”的纤芯关联的端子
                    for (var i = self.undoData.datas.length - 1; i >= 0; i--) {
                        var _dpPort = self.undoData.datas[i]; // 纤芯关联的端子
                        var _id = _dpPort.getId(); // 端子节点的ID
                        if (_id.indexOf(fiberCuid) == 0) {
                            self.undoData.datas.splice(i, 1);
                        }
                    }
                }
                if (_msg) {
                	tp.utils.optionDialog('错误提示信息', _msg);
                    self.doCancel();
                } else {
                    setButtonState(true);
                }

                // 更新左侧端子图标状态
                self.aListView.loadPortData(self);
            });
        }

        function setButtonState(isOk) {
            if (!isOk) {
                return;
            }
            // 置按钮状态
            if (self.undoData.datas.length > 0) {
                self.resetButtonStatus();
            }

            // 清空操作缓存数据
            self.undoData.action = '';
            self.undoData.datas.length = 0;
        }
    },
    // 撤消关联或断开操作
    doCancel: function () {
    	var action = this.undoData.action;

    	var dataList = this.undoData.datas;
    	for(var i = 0; i < dataList.length; i++) {
    		var data = dataList[i];
    		if ("add" == action) { // 撤消“关联”操作
    			this.zDataModel.remove(data);
    		}
    		if ("del" == action) { // 撤消“断开”操作,撤消的数据一定是纤芯下的端子
                var fiberId = (data.getId().split(":"))[0];
                if (fiberId) {
                    data.setParent(this.zDataModel.getDataById(fiberId));
                }
    			this.zDataModel.add(data);
    		}
    	}
    	
    	// 清空操作缓存数据
    	this.undoData.action = '';
    	this.undoData.datas = [];
    	
    	// 置按钮状态
        this.resetButtonStatus();
    },
    resetButtonStatus: function() {
        var buttons = this.buttonToolbar.getItems();
        for (var i = 0; i < buttons.length; i++) {
            var item = buttons[i];
            item.disabled = (item.label == "取消" || item.label == "确定");
        }
    }
});