/**
 * 方法工具类
 */

// 选择管孔子孔
dms.utils.setSelectHoleAndChildHole = function(picData, dataModel, data) {
	var sCuid = picData.related_seg_cuid;
	var cuid = picData.cuid, 
		labelCn = picData.labelCn, 
		pCuid = picData.parentCuid, 
		pLabelCn = picData.parentName;

	var pdbo = dataModel.getDataById(sCuid);
	if (pdbo) {
		var cname = sCuid.split("-")[0];
		if (cname === "DUCT_SEG") {
			if (pCuid) {// 有parent代表选择的是子孔
				pdbo.a("CHILD_HOLE_NUM", labelCn);
				pdbo.a("CHILD_HOLE_CUID", cuid);
				pdbo.a("HOLE_CUID", pCuid);
				pdbo.a("HOLE_NUM", pLabelCn);
			} else {
				pdbo.a("HOLE_CUID", cuid);
				pdbo.a("HOLE_NUM", labelCn);
			}
		} else if (cname === "POLEWAY_SEG") {
			pdbo.a("CARRYING_CUID", cuid);
			pdbo.a("CARRYING_NUM", labelCn);
		}
	}
};

dms.utils.newHtNode = function(cuid, className, name, origPointName, destPointName, 
		holeCuid, ductHoleNum, childHoleCuid, childHoleNum,
		carryingCuid, carryingNum, direction, directionNum, 
		wireSegCuid, wireSystemCuid, origPointCuid, destPointCuid, 
		lineSystemCuid, lineBranchCuid) {
	var node = new ht.Node();
	node.setId(cuid);
	node.setName(name);
	node.a("CUID", cuid);
	node.a("CLASS_NAME", className);
	node.a("LABEL_CN", name);
	node.a("ORIG_POINT_NAME", origPointName);
	node.a("DEST_POINT_NAME", destPointName);
	node.a("HOLE_CUID", holeCuid);
	node.a("CHILD_HOLE_CUID", childHoleCuid);
	node.a("HOLE_NUM", ductHoleNum);
	node.a("CHILD_HOLE_NUM", childHoleNum);
	node.a("CARRYING_CUID", carryingCuid);
	node.a("CARRYING_NUM", carryingNum);
	node.a("DIRECTION", direction);
	node.a("DIRECTION_NUM", directionNum);
	node.a("WIRE_SEG_CUID", wireSegCuid);
	node.a("WIRE_SYSTEM_CUID", wireSystemCuid);
	node.a("ORIG_POINT_CUID", origPointCuid);
	node.a("DEST_POINT_CUID", destPointCuid);
	node.a("LINE_SYSTEM_CUID", lineSystemCuid);
	node.a("LINE_BRANCH_CUID", lineBranchCuid);
	node.a("LINE_SEG_CUID", cuid);
	return node;
};
// 设置选择的管孔子孔
dms.utils.setHoleAndChildHoleOrCarryingCable = function(dataModel, columnName, e) {

	var sm = dataModel.sm();
	var selectNodes = sm.getSelection();
	var ld = sm.ld();
	if (!ld || !ld.a("LINE_SYSTEM_CUID")) {
		alert("请选择管道段！");
		return
	}
	var sysCuid = ld.getAttr("LINE_SYSTEM_CUID");
	// var columnName = "HOLE_NUM";
	var result = "{\"systemList\":[";
	result = result + "{";
	result = result + "\"CUID\":" + "\"" + sysCuid + "\"";
	var length = selectNodes.size();
	result = result + ",\"segList\":[";
	for (var i = 0; i < length; i++) {
		var data = selectNodes.get(i),
		// var data = sm.get(i),
			cuid = data.getAttr("CUID"), 
			labelCn = data.getAttr("LABEL_CN"), 
			holeCuid = data.getAttr("HOLE_CUID"), 
			holeNum = data.getAttr("HOLE_NUM"), 
			childholeCuid = data.getAttr("CHILD_HOLE_CUID"), 
			childholeNum = data.getAttr("CHILD_HOLE_NUM"), 
			carryingCuid = data.getAttr("CARRYING_CUID"), 
			carryingNum = data.getAttr("CARRYING_NUM");

		result = result + "{";
		result = result + "\"SEG_CUID\":" + "\"" + cuid + "\",";
		result = result + "\"SEG_LABEL_CN\":" + "\"" + labelCn + "\",";
		result = result + "\"HOLE_CUID\":" + "\"" + holeCuid + "\",";
		result = result + "\"HOLE_NUM\":" + "\"" + holeNum + "\",";
		result = result + "\"CHILD_HOLE_CUID\":" + "\"" + childholeCuid + "\",";
		result = result + "\"CHILD_HOLE_NUM\":" + "\"" + childholeNum + "\",";
		result = result + "\"CARRYING_CUID\":" + "\"" + carryingCuid + "\",";
		result = result + "\"CARRYING_NUM\":" + "\"" + carryingNum + "\"";

		result = result + "}";
		if (i < length - 1) {
			result = result + ",";
		}
	}
	result = result + "]";
	result = result + "}]}";

	var holeJson = new Object();
	holeJson['segs'] = result;
	$.ajax({
		url : ctx + '/rest/MapRestService/getHoleAndChildHoleOrCarryingCable/' + sysCuid + "/" + columnName + "?time=" + new Date().getTime(),
		type : 'POST',
		data : holeJson,
		dataType : 'json',
		success : function(data) {
			if (data && data.error) {
				tp.utils.optionDialog('温馨提示', data.error);
				tp.utils.wholeUnLock();
				return;
			}
			if (data && data.holeList && data.holeList.length > 0) {
				var holes = data.holeList, menuItems = [];
				for (var i = 0; i < holes.length; i++) {
					var hole = holes[i];
					var cuid = hole.CUID, 
						origNo = hole.ORIG_NO, 
						destNo = hole.DEST_NO, 
						relatedSegCuid = hole.RELATED_SEG_CUID;
					var item = {
						cuid : cuid,
						label : origNo,
						relatedSegCuid : relatedSegCuid,
						action : function(item) {
							var contextObject = {
								'cuid' : item.cuid,
								'label' : item.label,
								'relatedSegCuid' : item.relatedSegCuid
							};
							sm.each(function(data) {
								if (data) {
									var cid = data.getAttr("CUID");// contextObject.relatedSegCuid;
									var dnode = dataModel.getDataById(cid);
									if (dnode) {
										if (columnName === "HOLE_NUM") {
											dnode.a("HOLE_CUID",contextObject.cuid);
											dnode.a("HOLE_NUM",contextObject.label);
										} else if (columnName === "CHILD_HOLE_NUM") {
											dnode.a("CHILD_HOLE_CUID",contextObject.cuid);
											dnode.a("CHILD_HOLE_NUM",contextObject.label);
										} else if (columnName === "CARRYING_NUM") {
											dnode.a("CARRYING_CUID",contextObject.cuid);
											dnode.a("CARRYING_NUM",contextObject.label);
										}
									}
								}
							}, sm);
						}
					};
					if (item) {
						menuItems.push(item);
					}
				}
				window.htconfig.Default.baseZIndex = 1000;
				var holeMenu = new ht.widget.ContextMenu();
				holeMenu.setItems(menuItems);

				if (!holeMenu.isShowing()) {
					holeMenu.show(e.x, e.y);
				}
			}
			if (data && data.holeList && data.holeList.length == 0) {
				tp.utils.optionDialog('温馨提示', '无管孔子孔!');
			}
			tp.utils.wholeUnLock();
		},
		error : function(e) {
			tp.utils.wholeUnLock();
			tp.utils.optionDialog('温馨提示', '获取管孔子孔失败!');
		}
	});

};

dms.utils.addColumns = function(table, code) {

	var className = null;
	if (code) {
		className = code.split("-")[0];
	}
	var cm = table.getColumnModel();
	table.getTableView().getTreeColumn().setDisplayName('路由');
	table.getTableView().getTreeColumn().setWidth(150);

	column = new ht.Column();
	column.setAccessType('attr');
	column.setName('ORIG_POINT_NAME');
	column.setWidth(100);
	column.setDisplayName('起点名称');
	cm.add(column);

	column = new ht.Column();
	column.setAccessType('attr');
	column.setName('DEST_POINT_NAME');
	column.setWidth(100);
	column.setDisplayName('终点名称');
	cm.add(column);

	if (className == "DUCT_SYSTEM" || className == "WIRE_SEG") {
		column = new ht.Column();
		column.setAccessType('attr');
		column.setName('HOLE_NUM');
		column.setWidth(100);
		column.setDisplayName('管孔编号');
		cm.add(column);
	}

	if (className == "DUCT_SYSTEM" || className == "WIRE_SEG") {
		column = new ht.Column();
		column.setAccessType('attr');
		column.setName('CHILD_HOLE_NUM');
		column.setWidth(100);
		column.setDisplayName('子孔编号');
		cm.add(column);
	}
	if (className == "POLEWAY_SYSTEM" || className == "WIRE_SEG") {
		column = new ht.Column();
		column.setAccessType('attr');
		column.setName('CARRYING_NUM');
		column.setWidth(100);
		column.setDisplayName('吊线编号');
		cm.add(column);
	}
	column = new ht.Column();
	column.setAccessType('attr');
	column.setName('DIRECTION_NUM');
	column.setWidth(100);
	column.setDisplayName('敷设方向');
	cm.add(column);
};

// 光缆路由管理中承载段系统展开
dms.utils.getChildBySystem = function(data, dataModel) {
	if (data && data.systemList) {
		//
		var systemDbo = data.systemList[0], 
			systemCuid = systemDbo.CUID, 
			systemLabenCn = systemDbo.LABEL_CN, 
			sysClassName = systemDbo.SYS_CLASS_NAME, 
			indexInRouteBegin = systemDbo.indexInRouteBegin, 
			indexInRouteEnd = systemDbo.indexInRouteEnd, 
			branchs = systemDbo.branchList;

		var sNode = dataModel.getDataById(systemCuid);
		if (!sNode) {
			sNode = dms.utils.newHtNode(systemCuid, sysClassName, systemLabenCn);
		}
		sNode.setToolTip(sNode._name);
		sNode.a("indexInRouteBegin", indexInRouteBegin);
		sNode.a("indexInRouteEnd", indexInRouteEnd);
		var sysNode = dms.utils.addData(dataModel, sNode, '');
		// var sysNode = addData(aDataModel,systemLabenCn,'',sysClassName);
		if (sysClassName === "UP_LINE" || sysClassName === "HANG_WALL") {
			var segs = systemDbo.segList;
			if (segs) {
				for (var j = 0; j < segs.length; j++) {
					var seg = segs[j];
					var segCuid = seg.SEG_CUID, 
						segClassName = seg.SEG_CLASS_NAME, 
						segName = seg.SEG_LABEL_CN, 
						origPointName = seg.ORIG_POINT_NAME, 
						destPointName = seg.DEST_POINT_NAME, 
						holeCuid = seg.HOLE_CUID, 
						ductHoleNum = seg.HOLE_NUM, 
						childHoleCuid = seg.CHILD_HOLE_CUID, 
						childHoleNum = seg.CHILD_HOLE_NUM, 
						carryingCuid = seg.CARRYING_CUID, 
						carryingNum = seg.CARRYING_NUM, 
						direction = seg.DIRECTION,
					// 方向设为正向
					directionNum = "正向";
					if (!direction) {
						direction = "1";
					}
					if (direction == "1") {
						directionNum = "正向";
					} else {
						directionNum = "反向";
					}

					var wireSegCuid = seg.WIRE_SEG_CUID, wireSystemCuid = seg.WIRE_SYSTEM_CUID, origPointCuid = seg.ORIG_POINT_CUID, destPointCuid = seg.DEST_POINT_CUID, lineSystemCuid = seg.LINE_SYSTEM_CUID;
					var segNode = dms.utils.newHtNode(segCuid, segClassName,segName, origPointName, destPointName, holeCuid,
							ductHoleNum, childHoleCuid, childHoleNum,carryingCuid, carryingNum, direction, directionNum,
							wireSegCuid, wireSystemCuid, origPointCuid,destPointCuid, lineSystemCuid, '');
					dms.utils.addData(dataModel, segNode, sysNode);
				}
			}
		} else {
			if (branchs) {
				for (var i = 0; i < branchs.length; i++) {
					var branch = branchs[i];
					var bCuid = branch.BRANCH_CUID, 
						bClassName = branch.BRANCH_CLASS_NAME, 
						branchName = branch.BRANCH_LABEL_CN;
					var bNode = dms.utils.newHtNode(bCuid, bClassName,branchName);

					var branchNode = dms.utils.addData(dataModel, bNode, sysNode);
					var segs = branch.segList;
					if (segs) {
						for (var j = 0; j < segs.length; j++) {
							var seg = segs[j];
							var segCuid = seg.SEG_CUID, 
								segClassName = seg.SEG_CLASS_NAME, 
								segName = seg.SEG_LABEL_CN, 
								origPointName = seg.ORIG_POINT_NAME, 
								destPointName = seg.DEST_POINT_NAME, 
								holeCuid = seg.HOLE_CUID, 
								ductHoleNum = seg.HOLE_NUM, 
								childHoleCuid = seg.CHILD_HOLE_CUID, 
								childHoleNum = seg.CHILD_HOLE_NUM, 
								carryingCuid = seg.CARRYING_CUID, 
								carryingNum = seg.CARRYING_NUM, 
								direction = seg.DIRECTION, 
								directionNum = "正向";
							if (!direction) {
								direction = "1";
							}
							if (direction == "1") {
								directionNum = "正向";
							} else {
								directionNum = "反向";
							}
							var wireSegCuid = seg.WIRE_SEG_CUID, wireSystemCuid = seg.WIRE_SYSTEM_CUID, origPointCuid = seg.ORIG_POINT_CUID, destPointCuid = seg.DEST_POINT_CUID, lineSystemCuid = seg.LINE_SYSTEM_CUID, lineBranchCuid = seg.LINE_BRANCH_CUID;
							var segNode = dms.utils.newHtNode(segCuid,segClassName, segName, origPointName,
									destPointName, holeCuid, ductHoleNum,childHoleCuid, childHoleNum, carryingCuid,
									carryingNum, direction, directionNum,wireSegCuid, wireSystemCuid, origPointCuid,
									destPointCuid, lineSystemCuid,lineBranchCuid);
							dms.utils.addData(dataModel, segNode, branchNode);
						}
					}
				}
			}
		}
	}
};
// titleTip代表完成后弹出的提示框信息
dms.utils.initSegInfo = function(dataModel, wsCuid, titleTip) {
	tp.utils.wholeLock();
	var roots = dataModel.getRoots();
	var result = "{\"systemList\":[";
	for (var i = 0; i < roots.size(); i++) {
		var root = roots.get(i), systemCuid = root.getAttr("CUID");

		result = result + "{";
		var indexInRouteBegin = null;
		if (root.getAttr("indexInRouteBegin")) {
			indexInRouteBegin = root.getAttr("indexInRouteBegin");
			root.a("indexInRouteBegin", indexInRouteBegin);
		}
		var indexInRouteEnd = null;
		if (root.getAttr("indexInRouteEnd")) {
			indexInRouteEnd = root.getAttr("indexInRouteEnd");
			root.a("indexInRouteEnd", indexInRouteEnd);
		}
		result = result + "\"CUID\":" + "\"" + systemCuid + "\",";
		result = result + "\"indexInRouteBegin\":" + "\"" + indexInRouteBegin + "\",";
		result = result + "\"indexInRouteEnd\":" + "\"" + indexInRouteEnd + "\"";

		var sysClassName = systemCuid.split("-")[0];
		if (sysClassName === "UP_LINE" || sysClassName === "HANG_WALL") {
			var segs = root._children;
			if (segs && segs.size() > 0) {
				var length = segs.size();
				result = result + ",\"segList\":[";
				for (var k = 0; k < length; k++) {
					var node = segs.get(k);
					var cuid = node.getAttr("CUID"), 
						labelCn = node.getAttr("LABEL_CN"), 
						lineSystemCuid = node.getAttr("LINE_SYSTEM_CUID"), 
						wireSystemCuid = node.getAttr("WIRE_SYSTEM_CUID"), 
							direction = node.getA.getAttr("WIRE_SEG_CUID"), 
							disPointCuid = node.getAttr("ORIG_POINT_CUID"), 
							endPointCuid = node.getAttr("DEST_POINT_CUID"), 
							holeCuid = node.getAttr("HOLE_CUID"), 
							childHoleCuid = node.getAttr("CHILD_HOLE_CUID"), 
							carryingCuid = node.getAttr("CARRYING_CUID");

					result = result + "{";
					result = result + "\"CUID\":" + "\"" + cuid + "\",";
					result = result + "\"LABEL_CN\":" + "\"" + labelCn + "\",";
					result = result + "\"DUCTLINE_CUID\":" + "\"" + cuid + "\",";
					result = result + "\"LINE_SEG_CUID\":" + "\"" + cuid + "\",";
					result = result + "\"LINE_SYSTEM_CUID\":" + "\"" + lineSystemCuid + "\",";
					result = result + "\"WIRE_SYSTEM_CUID\":" + "\"" + wireSystemCuid + "\",";

					result = result + "\"DIRECTION\":" + "\"" + direction + "\",";
					result = result + "\"WIRE_SEG_CUID\":" + "\"" + wireSegCuid + "\",";
					result = result + "\"HOLE_CUID\":" + "\"" + holeCuid + "\",";
					result = result + "\"CHILD_HOLE_CUID\":" + "\"" + childHoleCuid + "\",";
					result = result + "\"CARRYING_CUID\":" + "\"" + carryingCuid + "\",";
					result = result + "\"DIS_POINT_CUID\":" + "\"" + disPointCuid + "\",";
					result = result + "\"END_POINT_CUID\":" + "\"" + endPointCuid + "\"";
					result = result + "}";
					if (k < segs.size() - 1) {
						result = result + ",";
					}
				}
				result = result + "]";
			}
		} else {
			var branchs = root._children;
			if (branchs && branchs.size() > 0) {
				var length = branchs.size();
				result = result + ",\"branchList\":[";
				for (var j = 0; j < length; j++) {
					result = result + "{";
					var branch = branchs.get(j), 
					branchCuid = branch.getAttr("CUID");
					result = result + "\"CUID\":" + "\"" + branchCuid + "\"";
					var segs = branch._children;
					result = result + ",\"segList\":[";
					for (var k = 0; k < segs.size(); k++) {
						var node = segs.get(k);
						var cuid = node.getAttr("CUID"), 
							labelCn = node.getAttr("LABEL_CN"), 
							lineSystemCuid = node.getAttr("LINE_SYSTEM_CUID"), 
							wireSystemCuid = node.getAttr("WIRE_SYSTEM_CUID"), 
							lineBranchCuid = node.getAttr("LINE_BRANCH_CUID"), 
							direction = node.getAttr("DIRECTION"), 
							wireSegCuid = node.getAttr("WIRE_SEG_CUID"), 
							disPointCuid = node.getAttr("ORIG_POINT_CUID"), 
							endPointCuid = node.getAttr("DEST_POINT_CUID"), 
							holeCuid = node.getAttr("HOLE_CUID"), 
							childHoleCuid = node.getAttr("CHILD_HOLE_CUID"), 
							carryingCuid = node.getAttr("CARRYING_CUID");
						result = result + "{";
						result = result + "\"CUID\":" + "\"" + cuid + "\",";
						result = result + "\"LABEL_CN\":" + "\"" + labelCn + "\",";
						result = result + "\"DUCTLINE_CUID\":" + "\"" + cuid + "\",";
						result = result + "\"LINE_SEG_CUID\":" + "\"" + cuid + "\",";
						result = result + "\"LINE_SYSTEM_CUID\":" + "\"" + lineSystemCuid + "\",";
						result = result + "\"WIRE_SYSTEM_CUID\":" + "\"" + wireSystemCuid + "\",";
						result = result + "\"LINE_BRANCH_CUID\":" + "\"" + lineBranchCuid + "\",";
						result = result + "\"DIRECTION\":" + "\"" + direction + "\",";
						result = result + "\"WIRE_SEG_CUID\":" + "\"" + wireSegCuid + "\",";
						result = result + "\"HOLE_CUID\":" + "\"" + holeCuid + "\",";
						result = result + "\"CHILD_HOLE_CUID\":" + "\"" + childHoleCuid + "\",";
						result = result + "\"CARRYING_CUID\":" + "\"" + carryingCuid + "\",";
						result = result + "\"DIS_POINT_CUID\":" + "\"" + disPointCuid + "\",";
						result = result + "\"END_POINT_CUID\":" + "\"" + endPointCuid + "\"";
						result = result + "}";
						if (k < segs.size() - 1)
							result = result + ",";

					}
					result = result + "]";

					result = result + "}";
					if (j < branchs.size() - 1)
						result = result + ",";
				}
				// 分支
				result = result + "]";
			}
		}

		// 系统
		result = result + "}";
		if (i < roots.size() - 1) {
			result = result + ",";
		}
	}
	result = result + "]}";
	var wiretoductlineJson = new Object();
	wiretoductlineJson['wireductlinelist'] = result;

	$.ajax({
				url : ctx + '/rest/MapRestService/modifyWireTuDuctLine/' + wsCuid + "?time=" + new Date().getTime(),
				type : 'POST',
				data : wiretoductlineJson,
				dataType : 'json',
				success : function(data) {
					if (data && data.success) {
						Dms.Default.tpmap.refreshMap();
						Dms.Default.tpmap.reset();
						if (titleTip) {
							tp.utils.optionDialog('温馨提示', titleTip);
						} else {
							tp.utils.optionDialog('温馨提示', '具体路由修改成功!');
						}
					} else if (data && data.error) {
						tp.utils.optionDialog('温馨提示', '路由修改失败!');
					}
					tp.utils.wholeUnLock();
				},
				error : function(e) {
					tp.utils.wholeUnLock();
					tp.utils.optionDialog('温馨提示', '路由修改失败!');
				}
			});
};

dms.utils.addData = function(dataModel, node, parent, icon) {
	var dataById = dataModel.getDataById(node._id);
	if (!dataById) {
		node.setIcon(icon);
		node.setParent(parent); // or parent.addChild(data);
		dataModel.add(node);
	}
	node.setToolTip(node._name);
	return node;
};

//光缆分段保存选择起止点
dms.utils.createPointSelectInput = function(code, value, inputName, callback) {
	var input = ht.Default.createElement('input');
	input.name = inputName;
	input.readOnly = 'readOnly';
	if (value) {
		input.value = value;
	}
	var clearButton = document.createElement('button');
	clearButton.style.padding = 0;
	clearButton.style.position = 'absolute';
	clearButton.innerHTML = '<img src="' + ctx
			+ '/resources/tool/cancel.png" style="margin:0;padding0;">';
	clearButton.onclick = function(e) {// 此处有问题，清除当前值后，已经选择过的值还存在param中。
		input.value = '';
	};

	var queryButton = document.createElement('button');
	queryButton.style.padding = 0;
	queryButton.style.position = 'absolute';
	queryButton.innerHTML = '<img src="' + ctx + '/resources/tool/zoom.png">';
	queryButton.onclick = function(e) {

		var dialog = new ht.widget.Dialog();
		var c = dms.utils.createWithOutPropertyQueryPanel(code, dialog, input, dms.Default.createQueryPointPanel);
		dialog.setConfig({
					title : "<html><font size=3>查询</font></html>",
					width : 500,
					height : 350,
					titleAlign : "left",
					draggable : true,
					closable : true,
					content : c
				});
		dialog.onHidden = function(operation) {
			if (input) {
				var inputId = input.id, 
				inputName = input.value,
				inputLongitude = input.longitude,
				intputLati = input.latitude;
				var inputData = {
					'CUID' : inputId,
					'LABEL_CN' : inputName,
					'LONGITUDE' : inputLongitude,
					'LATITUDE' : intputLati
				};
				callback(inputData);
			}
		};
		dialog.getView().style.zIndex = 999;
		dialog.show();
	};
	var inputForm = new ht.widget.FormPane();
	inputForm.addRow([input, clearButton, queryButton], [0.1, 20, 20]);
	inputForm.setVGap(0);
	inputForm.setHGap(0);
	inputForm.setPadding(0);
	inputForm.getResult = function(){
		return input;
	};
	return inputForm;
};

dms.utils.createWithOutPropertyQueryPanel = function (code, dialog,callBackInput,queryPanel){
	var dataModel = new ht.DataModel();
	//如果tp包已打，此处加一个参数，用来去掉定位按钮var pageTable = new tp.widget.PageTable(dataModel,false);
	var pageTable = new tp.widget.PageTable(dataModel);

	var param = tp.getGridCfgByCode(code);
	var gridConfig = initParamsByUrl(param);
	pageTable.setGridCfg(gridConfig);
	
	GridViewAction.getGridMeta(gridConfig.gridCfg, function(data) { 
		if(Ext.isEmpty(data)) { 
			Ext.Msg.alert('系统异常', '无法获取表格定义，请检查表格配置！'); 
		}
		pageTable.setPropertyMeta(data);
	});
	 
	// TODO 根据配置动态加载查询面板
	var basicFormPane = new queryPanel(pageTable);// tp.utils.createQueryConPanel(pageTable);
        
	var toolbarPanel = new ht.widget.FormPane();

    var selectBtn = tp.utils.createButton('','确定');
    var cancleBtn = tp.utils.createButton('','取消');
    toolbarPanel.addRow([null,selectBtn, cancleBtn],[0.1,60,60]);
    selectBtn.onclick = function(){
		if(callBackInput){
	    	var a =  borderPane.getResult();
	    	callBackInput.value = a.labelCn;
	    	callBackInput.id = a.cuid;
	    	callBackInput.longitude = a.longitude;
	    	callBackInput.latitude = a.latitude;
		}
	    dialog.hide();
    };
    cancleBtn.onclick = function(){
    	dialog.hide();
    };
    
    //var mainSplit = new ht.widget.SplitView(pageTable, propertyPane, 'horizontal', 0.7);  
    
    var borderPane = new ht.widget.BorderPane(); 
    borderPane.setTopView(basicFormPane);
    borderPane.setTopHeight(80);
    borderPane.setBottomView(toolbarPanel);
    borderPane.setBottomHeight(40);
    borderPane.setCenterView(pageTable);
    borderPane.getResult = function(){
    	var datas = pageTable.getTableView().dm().sm().getSelection();
    	var cuids = "";
    	var labelCns = "";
    	var longitudes = "";
    	var latitudes = "";
    	var result = {cuid:'',labelCn:'',longitude:'',latitude:''};
    	for(var i=0;i< datas.size();i++){
    		var data = datas.get(i);
    		if(Ext.isEmpty(cuids)){
    			cuids = data.getAttr('CUID');
    		}else{
    			cuids =cuids+","+data.getAttr('CUID');
    		}
    		
    		if(Ext.isEmpty(labelCns)){
    			labelCns = data.getAttr('LABEL_CN');
    		}else{
    			labelCns =labelCns+","+data.getAttr('LABEL_CN');
    		}
    		if(Ext.isEmpty(longitudes)){
    			longitudes = data.getAttr('LONGITUDE');
    		}else{
    			longitudes =longitudes+","+data.getAttr('LONGITUDE');
    		}
    		if(Ext.isEmpty(latitudes)){
    			latitudes = data.getAttr('LATITUDE');
    		}else{
    			latitudes =latitudes+","+data.getAttr('LATITUDE');
    		}
    	}
    	result = {cuid:cuids,labelCn:labelCns,longitude:longitudes,latitude:latitudes};
    	return result;
    };
    return 	borderPane;
};
