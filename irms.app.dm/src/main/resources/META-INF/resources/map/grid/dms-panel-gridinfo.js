//综合业务区属性
$importjs(ctx+'/map/ux/tp-inputcombox.js');
(function(window,Object,undefined){
	"use strict";
	
	Dms.widget.GridInfoPanel = function(cuid,districtCuid,shape){
		var self = this;
		var gridInfo = {};
	
		//绘制组件
		var formPane = self._formPane =  new ht.widget.FormPane();
		var nameInput = tp.utils.createInput('','input');
		var numberInput = tp.utils.createInput('','input');
		var districtInput = self._districtInput = new tp.widget.InputCombox("IbatisTransDAO",
		"select cuid as VALUE,label_cn as  LABEL from district where label_cn like '%PARAMETER%'");
		
		var maintanceTypeInput = self._maintanceTypeInput = new ht.widget.ComboBox();
		maintanceTypeInput.setValues(["自维","代维"]);
		maintanceTypeInput.setLabels(["自维","代维"]);
		maintanceTypeInput.setValue("自维");
		
		/*var stateInput = self._stateInput = new ht.widget.ComboBox();
		stateInput.setValues([1,0,-1]);
		stateInput.setLabels(["有效","无效","删除"]);
		stateInput.setValue(1);*/
		
		var remarkInput = tp.utils.createInput('','input');
		
		var confirmBtn = tp.utils.createButton('','保存');
		var cancelBtn = tp.utils.createButton('','取消');
		
		formPane.addRow(['业务区名称',nameInput],[80,0.1],22);
		formPane.addRow(['业务区编号',numberInput],[80,0.1],22);
		//formPane.addRow(['业务区状态',stateInput],[80,0.1],22);
		if(districtCuid)
		{
			districtInput.setValue(districtCuid);
		}else
		{
			formPane.addRow(['所属区域',districtInput],[80,0.1],22);
		}
		formPane.addRow(['维护方式 ',maintanceTypeInput],[80,0.1],22);
		formPane.addRow(['备注信息',remarkInput],[80,0.1],22);
		formPane.addRow(['',confirmBtn,cancelBtn,''],[0.1,60,60,0.1],22);
		
		
		Dms.widget.GridInfoPanel.superClass.constructor.apply(this,[{
			title : "综合业务区信息",
			width : 350,
			exclusive : true,
			titleColor : "white",
			id : 1,
			minimizable : true,
			expand : true,
			narrowWhenCollapse : true,
			contentHeight : 200,
			buttons:['minimize'],
			content : formPane.getView()
		}]);
		//处理事件
		self.addEventListener(function(e) {
			if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
					(e.kind === "endToggle" && self._config.expand === true)){
				formPane.invalidate();
			}
		});
		
		var districtView = districtInput.getView();
		districtView.onblur = function(){
			if(districtInput.getValue() && districtInput.getValue().length < 32){
				tp.utils.optionDialog("温馨提示","请选择县级区域！");
				return;
			}
		};
		
		confirmBtn.onclick = function(){
			if(nameInput.value.trim() == ""){
				tp.utils.optionDialog("温馨提示","业务区名称不能为空！");
				return;
			}
			if(numberInput.value.trim() == ""){
				tp.utils.optionDialog("温馨提示","业务区编号不能为空！");
				return;
			}
			/*if(stateInput.getValue() == ""){
				tp.utils.optionDialog("温馨提示","业务区状态不能为空！");
				return;
			}*/
			if(districtInput.getValue() == ""){
				tp.utils.optionDialog("温馨提示","所属区域不能为空！");
				return;
			}
			/*if(maintanceTypeInput.getValue() == ""){
				tp.utils.optionDialog("温馨提示","维护方式不能为空！");
				return;
			}*/
			
			gridInfo["GRID_NAME"] = nameInput.value;
			gridInfo["GRIDNUMBER"] = numberInput.value;
			gridInfo["CITY"] = districtInput.getValue();
			gridInfo["MAINTANCE_TYPE"] = maintanceTypeInput.getValue();
			//gridInfo["STATE"] = stateInput.getValue();
			gridInfo["REMARK"] = remarkInput.value;
			
			if(gridInfo["OBJECTID"]){
				self.updateGrid(gridInfo);
			}else{
				var tpmap = Dms.Default.tpmap,
					map = tpmap.getMap(),
					gv = tpmap.getGraphView(),
					latlngs = new ht.List(),
					pxpoints = shape.getPoints(),
					geometry = {'rings':[[]]};
				for ( var j=0;j<pxpoints.size();j++) {
					var pp=pxpoints.get(j);
					var x = gv.tx();
					var y = gv.ty();
					var point = map.containerPointToLatLng( new L.Point(x+pp.x, y+pp.y));
					geometry.rings[0].push([point.lng,point.lat]);
					latlngs.add(point);
	            };
	            geometry.rings[0].push(geometry.rings[0][0]);
				shape.a('latLng',latlngs);
				
				gridInfo['SHAPE'] = JSON.stringify(geometry);
				self.addGrid(gridInfo);
			}
		};
		cancelBtn.onclick = function(){
			Dms.Default.tpmap.reset();
			self.close();
		};
		
		if(cuid && cuid.length > 0){
			MapGridAction.getQueryListByCuid(cuid,function(result){
				if(result && result.length > 0){
					gridInfo = result[0];
					nameInput.value = result[0]["GRID_NAME"];
					numberInput.value = result[0]["GRIDNUMBER"];
					districtInput.setValues([result[0]["CITY"]]);
					districtInput.setLabels([result[0]["DISTRICT_NAME"]]);
					districtInput.setValue(result[0]["CITY"]);
					maintanceTypeInput.setValue(result[0]["MAINTANCE_TYPE"]);
					//stateInput.setValue(result[0]["STATE"]);
					remarkInput.value = result[0]["REMARK"];
				}
			});
		}
		
	};
	ht.Default.def("Dms.widget.GridInfoPanel",ht.widget.Panel,{
		show : function(){
			var self = this;
			self.setPosition(500, 200);
			document.body.appendChild(self.getView());
		},
		close : function(){
			var self = this;
			document.body.removeChild(self.getView());
		},
		updateGrid : function(gridInfo){
			var self = this;
			MapGridAction.updateGrid(gridInfo,function(result){
				if(result && result == "TRUE"){
					tp.utils.optionDialog("温馨提示","保存成功！");
					self.close();
					Dms.Default.gridPanel.refreshTree();
				}else{
					tp.utils.optionDialog("温馨提示","保存失败："+result);
					return;
				}
			});
		},
		addGrid : function(gridInfo){
			var self = this;
			MapGridAction.addGrid(gridInfo,function(result){
				if(result && result == "TRUE"){
					tp.utils.optionDialog("温馨提示","新增成功！");
					self.close();
					Dms.Default.gridPanel.refreshTree();
					Dms.Default.tpmap.refreshMap();
					Dms.Default.tpmap.reset();
				}else{
					tp.utils.optionDialog("温馨提示","新增失败："+result);
					Dms.Default.tpmap.reset();
				}
			});
		}
	});
})(this,Object);