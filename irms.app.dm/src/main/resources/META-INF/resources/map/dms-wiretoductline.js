(function(window,object,undefined){
	dms.utils.createWireToDuctLinePanel = function(code, dialog, useName){
		var self = this;
		var c = self.createPanel(code, dialog, useName);
		return c;
	};
	
	ht.Default.def('dms.utils.createWireToDuctLinePanel',ht.widget.FormPane,{
		createPanel : function(code, dialog, useName){
			var dataModel = new ht.DataModel(),
			basicPane = new ht.widget.FormPane();
			var centerPanel = new dms.utils.createWireToDuctLineCenterPanel(dataModel, code);
			var bottomPanel = new dms.utils.createWireToDuctLineBottomPanel(dataModel,dialog, useName);
			if (useName && useName == "glaywire") {// 代表图形化敷设
				basicPane.addRow([ centerPanel, bottomPanel ], [ 0.85, 0.15 ], 210);
			} else {
				basicPane.addRow([ centerPanel ], [ 1 ], 320);
				basicPane.addRow([ bottomPanel ], [ 0.8 ], 50);
			}
			basicPane.getLineDataModel = function() {
				return centerPanel.getLineDataModel();
			};
			return basicPane;
		},
	});
})(this,Object);

(function(window,object,undefined){
	dms.utils.createWireToDuctLineCenterPanel = function(dataModel, code, dialog) {
		var self = this;
		var c = self.createPanel(dataModel, code, dialog);
		return c;
	};
	
	ht.Default.def('dms.utils.createWireToDuctLineCenterPanel',ht.widget.FormPane,{
		createPanel : function(dataModel, code, dialog){
			// 光缆段具体路由管理panel
			var basicFormPane = new ht.widget.FormPane();
			tp.utils.lock(basicFormPane);
			var treeTable = new ht.widget.TreeTablePane(dataModel);
			dms.utils.addColumns(treeTable, code);
			basicFormPane.addRow([ treeTable ], [ 0.2 ], [ 0.1 ]);

			treeTable.getTableView().setLoader(
			{
				load : function(data) {
					var systemCuid = data._id;
					var indexInRouteBegin = data.getAttr("indexInRouteBegin");
					var indexInRouteEnd = data.getAttr("indexInRouteEnd");
					var segCuid = code;
					tp.utils.lock(basicFormPane);
					$.ajax({
						url : ctx + "/rest/MapRestService/getSegsAndWire2duct/"+ systemCuid + "/" + segCuid + "/" + indexInRouteBegin + "/" + indexInRouteEnd + "?time=" + new Date().getTime(),
						success : function(data) {
							if (data && data.systemList) {
								dms.utils.getChildBySystem(data,dataModel);
							}
							dataModel.getDataById(systemCuid).needToLoad = false;
							tp.utils.unlock(basicFormPane);
						},
						error : function(e) {
							tp.utils.optionDialog("错误提示信息", " " + e);
							tp.utils.unlock(basicFormPane);
						},
						dataType : 'json',
						type : 'POST'
			
					});
				},
				isLoaded : function(data) {
					return !data.needToLoad;
				}
			});

			// onDataDoubleClicked,onExpanded
			// 用于延遲加載
			$.ajax({url : ctx + "/rest/MapRestService/getWireSegsRoute/" + code + "?time=" + new Date().getTime(),
				success : function(data) {
					if (data && data.systemList) {
						var nodes = data.systemList;
						for (var i = 0; i < nodes.length; i++) {
							var node = nodes[i], 
								cuid = node.CUID, 
								labelCn = node.LABEL_CN, 
								indexInRouteBegin = node.indexInRouteBegin, 
								indexInRouteEnd = node.indexInRouteEnd, 
								sysClassName = node.CLASS_NAME;
							var sNode = dataModel.getDataById(cuid);
							if (!sNode) {
								sNode = dms.utils.newHtNode(cuid, sysClassName, labelCn);
							}
							sNode.a("indexInRouteBegin", indexInRouteBegin);
							sNode.a("indexInRouteEnd", indexInRouteEnd);
							sNode.needToLoad = true;
							dms.utils.addData(dataModel, sNode, '');
						}
					}
					tp.utils.unlock(basicFormPane);
				},
				error : function(e) {
					tp.utils.unlock(basicFormPane);
					tp.utils.optionDialog("错误提示信息", " " + e);
				},
				dataType : 'json',
				type : 'POST'

			});
			basicFormPane.getLineDataModel = function() {
				return dataModel;
			};
			return basicFormPane;
		}
	});
})(this,Object);

(function(window,object,undefined){
	dms.utils.createWireToDuctLineBottomPanel = function(dataModel, dialog, useName) {
		var self = this;
		var c = self.createPanel(dataModel, dialog, useName);
		return c;
	};
	ht.Default.def('dms.utils.createWireToDuctLineBottomPanel',ht.widget.FormPane,{
		createPanel : function(dataModel, dialog, useName){
			// useName用于判断是图形化敷设还是具体路由管理，按钮数和位置各不相同
			var bottomFormPane = new ht.widget.FormPane();
			var selectBtn = tp.utils.createButton('', '选择');
			selectBtn.onclick = function(e) {
				tp.Default.DrawObject._drawState = 202;
			};

			var addBtn = tp.utils.createButton('', '增加');
			var contextmenu = null;

			addBtn.onclick = function(e) {
				if (!contextmenu) {
					contextmenu = new ht.widget.ContextMenu(Dms.Default.contextMenu.SELECT_WIRE_SEG_ROUTE);
					contextmenu._dm = dataModel;
				}
				if (!contextmenu.isShowing()) {
					contextmenu.show(e.x, e.y);
				}
			};
			var modifyBtn = tp.utils.createButton('', '修改');
			modifyBtn.onclick = function(e) {
				var systemDbo = dataModel.sm().ld();
				if (systemDbo) {
					var cuid = systemDbo.getAttr("CUID"), 
						wireSegcuid = tp.Default.OperateObject.contextObject.cuid;
					var selCname = cuid.split("-")[0];
					if (selCname == "DUCT_SYSTEM" || selCname == "POLEWAY_SYSTEM" || selCname == "STONEWAY_SYSTEM" || selCname == "UP_LINE"
							|| selCname == "HANG_WALL") {
						if (!systemDbo.needToLoad || systemDbo.needToLoad == false) {
							tp.utils.createWireToDuctlineDialog(cuid, wireSegcuid,systemDbo, function(data) {
								if (data) {
									dataModel.remove(systemDbo);
									dataModel.deserialize(data, null, true);
								}
							});
						} else {
							tp.utils.optionDialog("温馨提示", "请首先展开要修改的系统！");
						}
					}else {
						tp.utils.optionDialog("温馨提示", "请选择系统修改");
					}
				} else {
					tp.utils.optionDialog("温馨提示", "请选择系统修改");
				}
			};
			var deleteBtn = tp.utils.createButton('', '删除');
			deleteBtn.onclick = function(e) {
				dms.Tools.deleteDatas(dataModel);
			};
			var upMoveBtn = tp.utils.createButton('', '上移');
			upMoveBtn.onclick = function(e) {
				dms.Tools.moveDataToUp(dataModel);
			};
			var downMoveBtn = tp.utils.createButton('', '下移');
			downMoveBtn.onclick = function(e) {
				dms.Tools.moveDataToDown(dataModel);
			};
			var directionBtn = tp.utils.createButton('', '正向/反向');
			directionBtn.onclick = function(e) {
				dms.Tools.changeDirection(dataModel);
			};
			var berachTrunButton = tp.utils.createButton('', '翻转');
			berachTrunButton.onclick = function(e) {
				dms.Tools.berachTrun(dataModel);
			};
			var applyBtn = tp.utils.createButton('', '应用');
			applyBtn.onclick = function(e) {
				// var cdm = contextmenu._dm;
				var cuid = tp.Default.OperateObject.contextObject.cuid;
				dms.utils.initSegInfo(dataModel, cuid);
			};

			var confirmBtn = tp.utils.createButton('', '确定');
			confirmBtn.onclick = function(e) {
				// var cdm = contextmenu._dm;
				var cuid = tp.Default.OperateObject.contextObject.cuid;
				dms.utils.initSegInfo(dataModel, cuid);
				if (dialog) {
					dialog.hide();
				}
			};
			var cancleBtn = tp.utils.createButton('', '取消');
			cancleBtn.onclick = function(e) {
				if (dialog) {
					dialog.hide();
				}
			};

			if (useName && useName == "glaywire") {// 代表图形化敷设
				bottomFormPane.addRow([ selectBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ addBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ modifyBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ deleteBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ upMoveBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ downMoveBtn ], [ 0.1 ], 20);
				bottomFormPane.addRow([ berachTrunButton ], [ 0.1 ], 20);
			} else {
				bottomFormPane.addRow([ null, addBtn, modifyBtn, deleteBtn, upMoveBtn, downMoveBtn, directionBtn, berachTrunButton, applyBtn,
						confirmBtn, cancleBtn ], [ 0.1, 60, 60, 60, 60, 60, 80, 60, 60, 60, 60 ], [ 24 ]);
			}
			return bottomFormPane;
		}
	});
})(this,Object);

(function(window,object,undefined){
	dms.utils.createWireToDuctLineSelectPanel = function(code, segCuid, node){
		var self = this;
		var c=self.createPanel(code, segCuid, node);
		return c;
	};
	ht.Default.def('dms.utils.createWireToDuctLineSelectPanel',ht.widget.FormPane,{
		createPanel : function(code, segCuid, node){
			var self = this;
			// 选择管线段panel
			var aDataModel = new ht.DataModel(), 
				zDataModel = new ht.DataModel(), 
				basicFormPane = new ht.widget.FormPane(), 
				topFormPane = new ht.widget.FormPane(), 
				leftTreeView = new ht.widget.TreeView(aDataModel),
				centerFormPane = new ht.widget.FormPane(),
				leftFormPane = new ht.widget.FormPane(),
				rightTreeTable = new ht.widget.TreeTablePane(zDataModel),
				rightFormPane = new ht.widget.FormPane();
				var table = rightTreeTable.getTableView();
			leftTreeView.enableToolTip(), 
			table.getView().addEventListener("dblclick",
				function(e) {
					var column = rightTreeTable.getTableView().getColumnAt(e), 
					columnName = column._name;
					var resData = zDataModel.sm().ld();
					if(!resData)
						return;
					var resId = resData.getAttr("CUID");
					if(!resId)
						return;
					var resClassName = resId.split("-")[0];
					if (resClassName === "DUCT_SEG") {
						if (columnName === "HOLE_NUM") {
							dms.sectionPic.createDuctSectionPicDialog(resId, function(picData) {
								if (picData) {
									dms.utils.setSelectHoleAndChildHole(picData, zDataModel);
								}
							});
						}
					} else if (resClassName === "POLEWAY_SEG") {
						if (columnName === "CARRYING_NUM") {
							dms.carryingcabPic.createSelectCarryingCableDialog(resData,
							function(picData) {
								if (picData) {
									dms.utils.setSelectHoleAndChildHole(picData,zDataModel);
								}
							});
						}
					}
				});
			tp.utils.lock(basicFormPane);
			// 增加从服务端取值
			$.ajax({
				url : ctx + "/rest/MapRestService/getWireSegRoute/" + code + "/" + segCuid + "?time=" + new Date().getTime(),
				success : function(data) {
					if (data && data.systemList) {
						dms.utils.getChildBySystem(data, aDataModel);
					}
					tp.utils.unlock(basicFormPane);
				},
				error : function(e) {
					tp.utils.unlock(basicFormPane);
					tp.utils.optionDialog("错误提示信息", " " + e);
				},
				dataType : 'json',
				type : 'POST'
			});
			// 如果有这个json代表是从光缆段具体路由点击“修改”跳转到此界面，右面表格中需要将传回来的值加上
			if (node) {
				var jsonDm = node.getDataModel();
				var jsonSerializer = new ht.JSONSerializer(jsonDm), jsonDeserialize = new ht.JSONSerializer(zDataModel);
				// 序列化时重载是否序列化，可以将node序列化
				jsonSerializer.isSerializable = function(data) {
					// 此处只有三级，暂时写死，后续需要改成递归判断，只要是父类是这个要转的node，都得加上，以保证子类不会丢失
					return data === node || data._parent === node || data._parent._parent == node;
				};

				jsonDeserialize.deserialize(jsonSerializer.serialize(), null, true);
				rightTreeTable.getTableView().expandAll();
			}
			topFormPane.getItemBorderColor = function() {
				return 'gray';
			};
			// topFormPane.getRowBorderColor = function(){return 'yellow';};
			basicFormPane.addRow([ topFormPane ], [ 0.1 ], 0.1);

			topFormPane.addRow([ leftFormPane, centerFormPane, rightFormPane ], [ 0.1, 60, 0.1 ], 0.1);

			var leftBtn = tp.utils.createButton("", ">");
			leftBtn.onclick = function(e) {
				var elements = aDataModel.sm();
				elements.each(function(data) {
					var _children = data._children;
					if (_children && _children.size() > 0) {
						var parent = data._parent;
						if (parent) {
							self.addParentSeg(aDataModel, zDataModel, data);
						}
						self.addChildren(aDataModel, zDataModel, data);
					} else {
						self.addParentSeg(aDataModel, zDataModel, data);
					}
				}, elements);
			};
			var rightBtn = tp.utils.createButton("", "<");
			rightBtn.onclick = function(e) {
				zDataModel.sm().toSelection().each(zDataModel.remove, zDataModel);
			};
			var rightDoubleBtn = tp.utils.createButton("", ">>");
			rightDoubleBtn.onclick = function(e) {
				var elements = aDataModel.getDatas();
				for (var i = 0; i < elements.size(); i++) {
					var node = elements.get(i);
					self.addChildren(aDataModel, zDataModel, node);
				}
			};
			var leftDoubleleftBtn = tp.utils.createButton("", "<<");
			leftDoubleleftBtn.onclick = function(e) {
				zDataModel.clear();
			};

			centerFormPane.addRow([ null ], [ 40 ], 20);
			centerFormPane.addRow([ null ], [ 40 ], 20);
			centerFormPane.addRow([ null ], [ 40 ], 20);
			centerFormPane.addRow([ null ], [ 40 ], 20);
			centerFormPane.addRow([ null ], [ 40 ], 20);
			centerFormPane.addRow([ null, leftBtn, null ], [ 0.1, 40, 0.1 ], 20);
			centerFormPane.addRow([ null, rightBtn, null ], [ 0.1, 40, 0.1 ], 20);
			centerFormPane.addRow([ null, rightDoubleBtn, null ], [ 0.1, 40, 0.1 ], 20);
			centerFormPane.addRow([ null, leftDoubleleftBtn, null ], [ 0.1, 40, 0.1 ],20);
			centerFormPane.setPadding(0);
			leftFormPane.addRow([ leftTreeView ], [ 0.1 ], 0.1);
			var batchHoles = tp.utils.createButton('', '管孔');
			batchHoles.onclick = function(e) {
				var columnName = "HOLE_NUM";
				dms.utils.setHoleAndChildHoleOrCarryingCable(zDataModel, columnName, e);
			};
			var batchChildHoles = tp.utils.createButton('', '子孔');
			batchChildHoles.onclick = function(e) {
				var columnName = "CHILD_HOLE_NUM";
				dms.utils.setHoleAndChildHoleOrCarryingCable(zDataModel, columnName, e);
			};

			var upMoveBtn = tp.utils.createButton('', '上移');
			upMoveBtn.onclick = function(e) {
				// var dm = zDataModel;
				dms.Tools.moveDataToUp(zDataModel);
			};
			var downMoveBtn = tp.utils.createButton('', '下移');
			downMoveBtn.onclick = function(e) {
				dms.Tools.moveDataToDown(zDataModel);
			};
			var directionBtn = tp.utils.createButton('', '正向/反向');
			directionBtn.onclick = function(e) {
				dms.Tools.changeDirection(zDataModel);
			};
			var berachTrunButton = tp.utils.createButton('', '翻转');
			berachTrunButton.onclick = function(e) {
				dms.Tools.berachTrun(zDataModel);
				//上面翻转，为页面假操作，实际datamodel顺序为变
				var newDataModel = new ht.DataModel();
				newDataModel.deserialize(JSON.stringify(zDataModel.toJSON(), null, 1));
				var list = newDataModel.getDatas();
				zDataModel.clear();
				for(var i = 0;i < list.size(); i++)
				{
					var data = list.get(list.size() - i - 1);
					var newData = new ht.Data();
					if(zDataModel.getDataById(data.getId()))
						newData = zDataModel.getDataById(data.getId());
					newData.setId(data.getId());
					newData.setName(data.getName());
					for(var j in data.getAttrObject())
					{
						newData.a(j, data.a(j));
					}
					if(data.getParent())
					{
						var parent = zDataModel.getDataById(data.getParent().getId());
						if(!parent)
						{
							parent = new ht.Data();
							parent.setId(data.getParent().getId());
							zDataModel.add(parent);
						}
						newData.setParent(parent);
					}
					if(!zDataModel.contains(newData))
						zDataModel.add(newData);
				}
			};
			rightFormPane.addRow([ rightTreeTable ], [ 0.1 ], 0.1);
			var resClassName = code.split("-")[0];

			if (resClassName === "DUCT_SYSTEM") {
				rightFormPane.addRow([ batchHoles, batchChildHoles, upMoveBtn, downMoveBtn, directionBtn, berachTrunButton ], 
						[ 0.1, 0.1, 0.1, 0.1, 0.2, 0.1 ], 22);
			} else if (resClassName === "POLEWAY_SYSTEM") {
				rightFormPane.addRow([upMoveBtn, downMoveBtn, directionBtn, berachTrunButton ], [0.1, 0.1, 0.2, 0.1 ],22);
			}

			dms.utils.addColumns(rightTreeTable, code);

			basicFormPane.getResult = function() {
				var json = JSON.stringify(zDataModel.toJSON(), null, 1);
				return json;
			};
			return basicFormPane;
		},
		addParentSeg : function(aDataModel, zDataModel, node) {
			var self = this;
			var parent = node._parent;
			var className = node.getAttr("CLASS_NAME");
			;// 取当前类型名称
			if (parent) {
				var parentNode = zDataModel.getDataById(parent._id);
				if (!parentNode) {
					self.addParentSeg(aDataModel, zDataModel, parent);
				}
				var rootdbo = node;
				if (("DUCT_SEG" == className) || ("POLEWAY_SEG" == className)
						|| ("STONEWAY_SEG" == className)
						|| ("HANG_WALL_SEG" == className)
						|| ("UP_LINE_SEG" == className)) {
					var zDbo = zDataModel.getDataById(rootdbo._id);
					if (zDbo) {
						return zDbo;
					}
				}
				var rootNode = zDataModel.getDataById(rootdbo._id);
				
				if (!rootNode) {
					
					var origPointName = rootdbo.getAttr("ORIG_POINT_NAME"), 
						destPointName = rootdbo.getAttr("DEST_POINT_NAME"), 
						holeCuid = node.getAttr("HOLE_CUID"), 
						ductHoleNum = rootdbo.getAttr("HOLE_NUM"), 
						childHoleCuid = node.getAttr("CHILD_HOLE_CUID"), 
						ductChileHoleNum = rootdbo.getAttr("CHILD_HOLE_NUM"), 
						carryingCuid = node.getAttr("CARRYING_CUID"), 
						carryingNum = node.getAttr("CARRYING_NUM"), 
						//direction = node.getAttr("DIRECTION"),
						direction = 1,
						directionNum = "正向";
					if (!direction) {
						direction = "1";
					}
					if (direction == "1") {
						directionNum = "正向";
					} else {
						directionNum = "反向";
					}

					var wireSegCuid = rootdbo.getAttr("WIRE_SEG_CUID"), 
						wireSystemCuid = rootdbo.getAttr("WIRE_SYSTEM_CUID"), 
						disPointCuid = rootdbo.getAttr("ORIG_POINT_CUID"), 
						endPointCuid = rootdbo.getAttr("DEST_POINT_CUID"), 
						lineSystemCuid = node.getAttr("LINE_SYSTEM_CUID"), 
						lineBranchCuid = node.getAttr("LINE_BRANCH_CUID");
					rootNode = dms.utils.newHtNode(rootdbo._id, className, rootdbo._name,
							origPointName, destPointName, holeCuid, ductHoleNum,
							childHoleCuid, ductChileHoleNum, carryingCuid, carryingNum,
							direction, directionNum, wireSegCuid, wireSystemCuid,
							disPointCuid, endPointCuid, lineSystemCuid, lineBranchCuid);
					rootNode.setParent(zDataModel.getDataById(parent._id));
					zDataModel.add(rootNode);
				}
				return rootNode;
			} else {
				var rootdbo = node;
				if (("DUCT_SEG" == className) || ("POLEWAY_SEG" == className)
						|| ("STONEWAY_SEG" == className)
						|| ("HANG_WALL_SEG" == className)
						|| ("UP_LINE_SEG" == className)) {
					var zDbo = zDataModel.getDataById(rootdbo._id);
					if (zDbo) {
						return zDbo;
					}
				}
				var rootNode;
				var zroot = zDataModel.getDataById(rootdbo._id);
				if (zroot) {
					rootNode = rootdbo;
				} else {
					rootNode = dms.utils.newHtNode(rootdbo._id, className, rootdbo._name);
					zDataModel.add(rootNode);
				}
				return rootNode;
			}
		},
		addChildren : function(aDataModel, zDataModel, node) {
			var self = this;
			// node代表当前选择的值
			var did = node._id, 
				name = node._name,
				className = node.getAttr("CLASS_NAME"), 
				origPointName = node.getAttr("ORIG_POINT_NAME"), 
				destPointName = node.getAttr("DEST_POINT_NAME"), 
				holeCuid = node.getAttr("HOLE_CUID"), 
				ductHoleNum = node.getAttr("HOLE_NUM"), 
				childHoleCuid = node.getAttr("CHILD_HOLE_CUID"), 
				ductChileHoleNum = node.getAttr("CHILD_HOLE_NUM"), 
				carryingCuid = node.getAttr("CARRYING_CUID"), 
				carryingNum = node.getAttr("CARRYING_NUM"), 
				wireSegCuid = node.getAttr("WIRE_SEG_CUID"), 
				wireSystemCuid = node.getAttr("WIRE_SYSTEM_CUID"), 
				disPointCuid = node.getAttr("ORIG_POINT_CUID"), 
				endPointCuid = node.getAttr("DEST_POINT_CUID"), 
				lineSystemCuid = node.getAttr("LINE_SYSTEM_CUID"), 
				lineBranchCuid = node.getAttr("LINE_BRANCH_CUID"), 
				//direction = node.getAttr("DIRECTION"),
				direction = 1,
				directionNum = "正向";
			if (!direction) {
				direction = "1";
			}
			if (direction == "1") {
				directionNum = "正向";
			} else {
				directionNum = "反向";
			}
			var dataById = zDataModel.getDataById(did);
			if (("DUCT_SEG" == className) || ("POLEWAY_SEG" == className)
					|| ("STONEWAY_SEG" == className) || ("HANG_WALL_SEG" == className)
					|| ("UP_LINE_SEG" == className)) {
				if (dataById != null) {
					return;
				}
			}

			if (!dataById) {
				// var cnode = new ht.Node();
				var cnode = dms.utils.newHtNode(did, className, name, origPointName,
						destPointName, holeCuid, ductHoleNum, childHoleCuid,
						ductChileHoleNum, carryingCuid, carryingNum, direction,
						directionNum, wireSegCuid, wireSystemCuid, disPointCuid,
						endPointCuid, lineSystemCuid, lineBranchCuid);
				// cnode.setId(did);
				// cnode.setName(name);
				if (node._parent) {
					var parent = zDataModel.getDataById(node._parent._id);
					if (parent) {
						cnode.setParent(parent);
					}
				}
				zDataModel.add(cnode);
			}
			var _children = node._children;
			if (_children) {
				for (var i = 0; i < _children.size(); i++) {
					var child = _children.get(i);
					self.addChildren(aDataModel, zDataModel, child);
				}
			}
		}
	});
})(this,Object);

(function(window,object,undefined){
	dms.utils.deleteDuctLineRelationPanel = function(code, dialog) {
		var self = this;
		var c = self.createPanel(code, dialog);
		return c;
	};
	ht.Default.def('dms.utils.deleteDuctLineRelationPanel',ht.widget.FormPane,{
		createPanel : function(code, dialog){
			// 光缆段右键通过段拆除承载
			var dataModel = new ht.DataModel();
			var basicFormPane = new ht.widget.FormPane();
			tp.utils.lock(basicFormPane);
			var treeTable = new ht.widget.TreeTablePane(dataModel);
			dms.utils.addColumns(treeTable, code);
			basicFormPane.addRow([ treeTable ], [ 0.2 ], [ 0.1 ]);

			treeTable.getTableView().setLoader(
			{
				load : function(data) {
					var systemCuid = data._id;
					var indexInRouteBegin = data.getAttr("indexInRouteBegin");
					
					var indexInRouteEnd = data.getAttr("indexInRouteEnd");
					var segCuid = code;
					tp.utils.lock(basicFormPane);
					$.ajax({
							url : ctx + "/rest/MapRestService/getSegsAndWire2duct/" + systemCuid + "/" + segCuid + "/" + indexInRouteBegin + "/" + indexInRouteEnd + "?time=" + new Date().getTime(),
							success : function(data) {
								if (data && data.systemList) {
									dms.utils.getChildBySystem(data,dataModel);
								}
								dataModel.getDataById(systemCuid).needToLoad = false;
								tp.utils.unlock(basicFormPane);
							},
							error : function(e) {
								tp.utils.unlock(basicFormPane);
								tp.utils.optionDialog("错误提示信息", " " + e);
							},
							dataType : 'json',
							type : 'POST'
				
						});
				},
				isLoaded : function(data) {
					return !data.needToLoad;
				}
			});

			var dm = treeTable.getDataModel();

			var deleteBtn = tp.utils.createButton('', '删除');
			deleteBtn.onclick = function(e) {
				dataModel.sm().toSelection().each(dataModel.remove, dataModel);
			};

			// onDataDoubleClicked,onExpanded
			// 用于延遲加載
			$.ajax({
				url : ctx + "/rest/MapRestService/getWireSegsRoute/" + code + "?time=" + new Date().getTime(),
				success : function(data) {
					if (data && data.systemList) {
						var nodes = data.systemList;
						for (var i = 0; i < nodes.length; i++) {
							var node = nodes[i], 
								cuid = node.CUID, 
								labelCn = node.LABEL_CN, 
								indexInRouteBegin = node.indexInRouteBegin, 
								indexInRouteEnd = node.indexInRouteEnd, 
								sysClassName = node.CLASS_NAME;
							var sNode = dataModel.getDataById(cuid);
							if (!sNode) {
								sNode = dms.utils.newHtNode(cuid, sysClassName, labelCn);
							}
							sNode.a("indexInRouteBegin", indexInRouteBegin);
							sNode.a("indexInRouteEnd", indexInRouteEnd);
							sNode.needToLoad = true;
							dms.utils.addData(dataModel, sNode, '');
						}
					}
					tp.utils.unlock(basicFormPane);
				},
				error : function(e) {
					tp.utils.unlock(basicFormPane);
					tp.utils.optionDialog("错误提示信息", " " + e);
				},
				dataType : 'json',
				type : 'POST'

			});

			var confirmBtn = tp.utils.createButton('', '确定');
			confirmBtn.onclick = function(e) {
				var cuid = tp.Default.OperateObject.contextObject.cuid;
				dms.utils.initSegInfo(dm, cuid, '通过段拆除承载成功!');
				dialog.hide();
			};
			var cancleBtn = tp.utils.createButton('', '取消');
			cancleBtn.onclick = function(e) {
				dialog.hide();
			};
			basicFormPane.addRow([ null, deleteBtn, confirmBtn, cancleBtn ], [ 0.1, 60, 60, 60 ], [ 24 ]);
			return basicFormPane;
		}
	});
})(this,Object);

(function(window,object,undefined){
	dms.utils.deleteLayRelationPanel = function(code, dialog) {
		var self = this;
		var c = self.createPanel(code,dialog);
		return c;
	};
	ht.Default.def('dms.utils.deleteLayRelationPanel',ht.widget.FormPane,{
		createPanel : function(code, dialog){

			// 管线段右键拆除敷设
			var dataModel = new ht.DataModel(), 
				basicFormPane = new ht.widget.FormPane(), 
				treeTablePanel = new ht.widget.TreeTablePane(dataModel);
			tp.utils.lock(basicFormPane);
			basicFormPane.addRow([ treeTablePanel ], [ 0.2 ], [ 0.1 ]);
			treeTablePanel.getTableView().getTreeColumn().setVisible(false);
		    $.ajax({
					url : ctx + "/rest/MapRestService/getWireSegByRelatedDuct/" + code + "?time=" + new Date().getTime(),
					success : function(data) {
						if (data && data.wireSegList) {
							var datas = data.wireSegList;
							for (var i = 0; i < datas.length; i++) {
								var data = datas[i], 
									cuid = data.CUID, 
									wireSegName = data.WIRE_SEG_NAME, 
									wireSystemName = data.WIRE_SYSTEM_NAME;

								var node = new ht.Node();
								node.setId(cuid);
								node.setName(wireSegName);
								node.a("CUID", cuid);
								node.a("WIRE_SEG_NAME", wireSegName);
								node.a("WIRE_SYSTEM_NAME", wireSystemName);
								dms.utils.addData(dataModel, node, '', '');
							}
						}
						tp.utils.unlock(basicFormPane);
					},
					error : function(e) {
						tp.utils.unlock(basicFormPane);
						tp.utils.optionDialog("错误提示信息", " " + e);
					},
					dataType : 'json',
					type : 'POST'

				});
			var confimBtn = tp.utils.createButton('', '确定');
			confimBtn.onclick = function(e) {
				var cuids = "";
				var sm = dataModel.sm();
				if (!sm || sm.size() == 0) {
					// 需要给出提示
					tp.utils.optionDialog('温馨提示', '没有选择要拆除的光缆!');
					return;
				}
				sm.each(function(data) {
					var cuid = data.getAttr("CUID");
					cuids = cuids + cuid + ",";
				}, sm);
				cuids = cuids.substring(0, cuids.length - 1);
			    tp.utils.lock(basicFormPane);
		    	$.ajax({
					url : ctx + "/rest/MapRestService/deleteDuctlineRelation/" + cuids + "/" + code + "?time=" + new Date().getTime(),
					success : function(data) {
						if (data && data.success) {
							dataModel.sm().toSelection().each(dataModel.remove,dataModel);
							dialog.hide();
							tp.utils.optionDialog('温馨提示', '拆除敷设信息成功!');
						} else if (data && data.error) {
							dialog.hide();
							tp.utils.optionDialog('温馨提示', '拆除敷设信息失败!');
						}
						Dms.Default.tpmap.refreshMap();
						Dms.Default.tpmap.reset();
						tp.utils.unlock(basicFormPane);
					},
					error : function(e) {
						tp.utils.unlock(basicFormPane);
						tp.utils.optionDialog('错误提示' + e);
					},
					dataType : 'json',
					type : 'POST'
		    	});
			};
			var cancelBtn = tp.utils.createButton('', '取消');
			cancelBtn.onclick = function(e) {
				dialog.hide();
			};
			basicFormPane.addRow([ null, confimBtn, cancelBtn ], [ 0.1, 60, 60 ],[ 24 ]);

			var cm = treeTablePanel.getColumnModel();
			column = new ht.Column();
			column.setAccessType('attr');
			column.setName('WIRE_SYSTEM_NAME');
			column.setWidth(150);
			column.setDisplayName('光缆系统');
			cm.add(column);

			column = new ht.Column();
			column.setAccessType('attr');
			column.setName('WIRE_SEG_NAME');
			column.setWidth(150);
			column.setDisplayName('光缆段');
			cm.add(column);
			return basicFormPane;
		}
	});
})(this,Object);

//node是在光缆具体路由图点击修改时用，其他时候请传空
tp.utils.createWireToDuctlineDialog = function(code, segCuid, node, callback) {
	var dialog = new ht.widget.Dialog();
	var c = new dms.utils.createWireToDuctLineSelectPanel(code, segCuid, node,dialog);
	dialog.setConfig({
		title : "<html><font size=3>" + "选择管线段</font></html>",
		width : 850,
		height : 400,
		titleAlign : "left",
		draggable : true,
		closable : true,
		content : c,
		buttons : [ {
			label : "确定",
			action : function() {
				callback(c.getResult());
				this.hide();
			}
		}, {
			label : "取消",
			action : function() {
				this.hide();
			}
		} ]
	});
	dialog.onShown = function(operation) {

	};
	dialog.onHidden = function(operation) {
		// callback(c.getResult());
	};
	dialog.getView().style.zIndex = 99999;
	dialog.show();
	return c;
};
