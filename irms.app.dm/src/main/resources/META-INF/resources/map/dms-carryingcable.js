/**
 * 
 */
Dms.carryingcabPic = {};
Dms.carryingcabPic.createSelectCarryingCableDialog = function(data, callback) {
	var dialog = new ht.widget.Dialog();
	var pane = Dms.carryingcabPic.createSelectCarryingCablePane(data);
	dialog.setConfig({
		title : "选择吊线编号",
		width : 300,
		height : 200,
		titleAlign : "left",
		draggable : true,
		closable : true,
		content : pane,
		buttons : [{
					label : "确定",
					action : function() {
						callback(pane.getResult());
						this.hide();
					}
				}, {
					label : "取消",
					action : function() {
						this.hide();
					}
				}]
			});

	dialog.show();
};

Dms.carryingcabPic.createSelectCarryingCablePane = function(data) {
	var formPane = new ht.widget.FormPane(), 
	dataModel = new ht.DataModel(),
	listview = new ht.widget.ListView(dataModel);
	formPane.getItemBorderColor = function() {
		return 'gray';
	};
	formPane.addRow([listview], [0.1], 160);

	dms.carryingcabPic.setCarryingCable(dataModel, data);
	formPane.getResult = function() {
		var dt = dataModel.sm().ld();
		var result = Dms.carryingcabPic.getSelectCarrayPicResult(dt, formPane);
		return result;
	};
	return formPane;
};
Dms.carryingcabPic.getSelectCarrayPicResult = function(data) {
	var _parent = data._parent, 
	segCuid = data.getAttr("related_seg_cuid");

	var result = {
		cuid : '',
		labelCn : '',
		related_seg_cuid : segCuid,
		parentCuid : '',
		parentName : ''
	};
	if (data) {
		var dataCuid = data._id, 
		dataName = data._name,
		_parentCuid = '', 
		_parentName = '';
		if (_parent) {
			_parentCuid = _parent._id;
			_parentName = _parent._name;
		}
		result = {
			cuid : dataCuid,
			labelCn : dataName,
			related_seg_cuid : segCuid,
			parentCuid : _parentCuid,
			parentName : _parentName
		};
	}
	return result;
};

dms.carryingcabPic.setCarryingCable = function(dataModel, carryData) {
	// var sm=dataModel.sm();
	// var ld = sm.ld();
	var sysCuid = carryData.getAttr("LINE_SYSTEM_CUID");
	var result = "{\"systemList\":[";
	result = result + "{";
	result = result + "\"CUID\":" + "\"" + sysCuid + "\"";
	result = result + ",\"segList\":[";

	var cuid = carryData.getAttr("CUID"), 
	labelCn = carryData.getAttr("LABEL_CN"), 
	holeCuid = carryData.getAttr("HOLE_CUID"), 
	holeNum = carryData.getAttr("HOLE_NUM"), 
	childholeCuid = carryData.getAttr("CHILD_HOLE_CUID"), 
	childholeNum = carryData.getAttr("CHILD_HOLE_NUM"), 
	carryingCuid = carryData.getAttr("CARRYING_CUID"), 
	carryingNum = carryData.getAttr("CARRYING_NUM");

	result = result + "{";
	result = result + "\"SEG_CUID\":" + "\"" + cuid + "\",";
	result = result + "\"SEG_LABEL_CN\":" + "\"" + labelCn + "\",";
	result = result + "\"HOLE_CUID\":" + "\"" + holeCuid + "\",";
	result = result + "\"HOLE_NUM\":" + "\"" + holeNum + "\",";
	result = result + "\"CHILD_HOLE_CUID\":" + "\"" + childholeCuid + "\",";
	result = result + "\"CHILD_HOLE_NUM\":" + "\"" + childholeNum + "\",";
	result = result + "\"CARRYING_CUID\":" + "\"" + carryingCuid + "\",";
	result = result + "\"CARRYING_NUM\":" + "\"" + carryingNum + "\"";

	result = result + "}]";
	result = result + "}]}";

	var holeJson = new Object();
	holeJson['segs'] = result;
	var columnName = "CARRYING_NUM";
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
				var holes = data.holeList;
				for (var i = 0; i < holes.length; i++) {
					var hole = holes[i];
					var cuid = hole.CUID, 
					origNo = hole.ORIG_NO, 
					destNo = hole.DEST_NO, 
					relatedSegCuid = hole.RELATED_SEG_CUID;
					var node = new ht.Node();
					node.setId(cuid);
					node.setName(origNo);
					node.a("CUID", cuid);
					node.a("related_seg_cuid", relatedSegCuid);
					dataModel.add(node);
				}
			}
		},
		error : function(e) {
			tp.utils.optionDialog('温馨提示', '获取吊线信息失败!');
		}
	});
};