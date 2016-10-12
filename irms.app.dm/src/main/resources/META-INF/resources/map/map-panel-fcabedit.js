//光交接箱、光分纤箱编辑窗口
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.FcabEditPanel = function(dm,tabView,func){
		var self=this;
		self._dm = dm;
		self._func = func;
		var data = self._dm.sm().ld();
		if(data){
			self.selBmclassId = data.a('bmClassId');
		}
		//属性页窗口
		var propertyPane = new Dms.Panel.PropertyPane(dm);
		var save = tp.utils.createButton('','保存');
		var cancel = tp.utils.createButton('','取消');
		var items = [{
			element: save,
			unfocusable: true,
			width: 30
		}, {
			element: cancel,
			unfocusable: true,
			width: 30
		}];
		
		var toolbar = new ht.widget.Toolbar(items);
		toolbar.setStickToRight(true);
		propertyPane.setBottomView(toolbar);
		//编辑模块
		var formPane = new ht.widget.BorderPane();
		var modulePane = self._modulePane =  new ht.widget.FormPane();
		var posPane = self._posPane =  new ht.widget.FormPane();
		var add = tp.utils.createButton('','增加模块');
		var del = tp.utils.createButton('','删除模块');
		var addpos = tp.utils.createButton('','增加POS');
		var delpos = tp.utils.createButton('','删除POS');
		
		var buttonItems = [{
			element: add,
			unfocusable: true,
			width: 20
		}, {
			element: del,
			unfocusable: true,
			width: 20
		},{
			element: addpos,
			unfocusable: true,
			width: 20
		}, {
			element: delpos,
			unfocusable: true,
			width: 20
		}];
		var isHaveModule = true;//代表光交接箱
		var selelement = dm.sm().ld();
		if(selelement){
			var bid = selelement.a('bmClassId');
			if(bid === 'FIBER_DP'){
				isHaveModule = false;
			}
		}
		if(!isHaveModule){
			if(self._func)
			{
				buttonItems = [{
					element: addpos,
					unfocusable: true,
					width: 20
				}, {
					element: delpos,
					unfocusable: true,
					width: 20
				}];
			}
			else
			{
				buttonItems = [];
			}
		}
		else
		{
			if(self._func)
			{
				buttonItems = [{
					element: add,
					unfocusable: true,
					width: 20
				}, {
					element: del,
					unfocusable: true,
					width: 20
				},{
					element: addpos,
					unfocusable: true,
					width: 20
				}, {
					element: delpos,
					unfocusable: true,
					width: 20
				}];
			}
			else
			{
				buttonItems = [{
					element: add,
					unfocusable: true,
					width: 20
				}, {
					element: del,
					unfocusable: true,
					width: 20
				}];
			}
		}
		var buttonToolbar = new ht.widget.Toolbar(buttonItems);
		buttonToolbar.setStickToRight(true);
		
		formPane.setTopView(buttonToolbar);
		var tabView = new ht.widget.TabView();
		tabView.add("模块", modulePane, true);
		if(self._func)
			tabView.add("POS", posPane, false);
		formPane.setCenterView(tabView);
		
		var viewbutton = tp.utils.createButton('','面板图预览');
		var viewButtonItems = [{
			element: viewbutton,
			unfocusable: true,
			width: 20
		}];
		var viewButtonToolbar = new ht.widget.Toolbar(viewButtonItems);
		viewButtonToolbar.setStickToRight(true);
		formPane.setBottomView(viewButtonToolbar);
		var gvDataModel = new ht.DataModel();
		var gv = self._gv = new ht.graph.GraphView(gvDataModel);
		gv.setEditable(false);
		gv.isMovable = function(data) {
			return false;
		};
		var splitView = new ht.widget.SplitView(formPane,gv.getView(),'h','0.5');
		Dms.Panel.FcabEditPanel.superClass.constructor.apply(this,[propertyPane,splitView,'h','0.30']);
		
		add.onclick = function(){
			tabView.select(0);
			var moduleCfg = {};
			var data = self._dm.sm().ld();
			var bmClassId = data.a('bmClassId');
			if(bmClassId === "FIBER_CAB"){
				moduleCfg.ROW_NUM = '24';
				moduleCfg.COL_NUM = '12';
			}else if(bmClassId === 'FIBER_DP'){
				moduleCfg.ROW_NUM = '2';
				moduleCfg.COL_NUM = '12';
			}
			modulePane.addRow([self.buildModule(modulePane,moduleCfg)],[0.1],130);
		};
		del.onclick = function(){
			tabView.select(0);
			if(modulePane._rows.length > 0)
				modulePane.removeRow(modulePane._rows.length - 1);
		};
		addpos.onclick = function(){
			tabView.select(1);
			var posCfg = {};
			posCfg.ROW_NUM = '1';
			posCfg.COL_NUM = '16';
			posPane.addRow([self.buildPos(posPane,posCfg)],[0.1],80);
		};
		delpos.onclick = function(){
			tabView.select(1);
			if(posPane._rows.length > 0)
				posPane.removeRow(posPane._rows.length - 1);
		};
		save.onclick = function(){
			//1、属性值合法性验证
			var data = propertyPane.propertyView.dm().sm().ld();
			var propertyDataModel = propertyPane.propertyView.getPropertyModel();
			var flag = true;
			var nullColumnName = null;
			propertyDataModel.each(function(property){
				var value = data.a(property.getName());
				if((value===undefined || value.toString().trim()==="" || value.length == 0) && !property.isNullable()){
					flag = false;
					nullColumnName = property.getDisplayName();
				}
			});
			if(!flag){
				tp.utils.optionDialog("温馨提示",'【<font color="red">'+ nullColumnName +'</font>】缺少属性值，请填写完整！');
				return;
			}
			if(self.saveModuleCfg()){
				//zhengwei start 把拼好的名称赋给选择模型中的data
				//2、批量保存
				if(self._dm.sm().size() > 1){
					var pointArr = propertyPane.getBatchDatas(data);
					var lastDataIndex = pointArr.length-1;
					var lastDataLabelCn = pointArr[lastDataIndex].LABEL_CN;
					if(lastDataLabelCn.length > 100) {
						tp.utils.optionDialog("温馨提示",'名称过长');
						return;
					}
					var selections = self._dm.sm().getSelection();
					for(var i=0; i<selections.size(); i++){
						selections.get(i)._attrObject = pointArr[i];
					}
				} else {
					if(data.a('LABEL_CN').length > 100) {
						tp.utils.optionDialog("温馨提示",'名称过长');
						return;
					}
				}
				//zhengwei end
				self.close();
				self._dm.sm().cs();
			}
			if(self._func)
			{
				if(self.saveAndCreateData())
					self.close();
				return;
			}
		};
		cancel.onclick = function(){
			if(self._func)
			{
				self._func();
				self.close();
				return;
			}
			var selectCount = self._dm.sm().size();
			var lastData = self._dm.sm().ld();
			if(selectCount == 1){
				lastData._attrObject = propertyPane.selectedData;
			}else{
				self._dm.sm().each(function(data){
					if(data == lastData){
						data._attrObject = propertyPane.selectedData;
					}
				});
			}
			self.close();
			if(tabView){
				tabView.iv();
//				dms.designer.panel.tabeView.iv();
			}
		};
		viewbutton.onclick = function(){
			var portWidth = 12,
				portHeight = 30,
				innerWidth = 6,
				innerHeight = 14;
			ht.Default.setImage('cab_dp_pos_port', {
				width : portWidth,
				height : portHeight,
				clip : true,
				comps : [{
					type : 'rect',
					rect : [ 0, 0, portWidth, portHeight ],
					background : 'rgb(97,96,96)'
				}, {
					type : 'rect',
					rect : [ (portWidth-innerWidth)/2, (portHeight-innerHeight)/2, innerWidth, innerHeight ],
					background : '#D4D4D4'
				}]
			});
			self._gv.dm().clear();
			if(tabView.getCurrentTab().getName() == 'POS')
			{
				var dm = self._dm,
				formPane = self._posPane;
			
				var length = formPane._rows.length;
				if(length > 0)
				{
					var rowHeight = 0;
					for(var i = 0; i < length ; i++)
					{
						var row = formPane._rows[i].items[0].element;
						var ration = row.v('RATION');
						var rowCount = ration.substring(0, ration.indexOf(':'));
						var columnCount = parseInt(ration.substring(ration.indexOf(':')+1)) + parseInt(rowCount);
						for(var m = 0; m < rowCount; m++)
						{
							for(var n = 0; n < columnCount; n++)
							{
								var node = new ht.Node();
								node.setPosition({x:(portWidth+4)*n,y:rowHeight + portHeight*m});
								node.setImage('cab_dp_pos_port');
								self._gv.dm().add(node);
							}
							rowHeight = rowHeight+3;
						}
						rowHeight = rowHeight+45;
					}
				}
			}
			else
			{
				var dm = self._dm,
				formPane = self._modulePane;
			
				var length = formPane._rows.length;
				if(length > 0)
				{
					var rowHeight = 0;
					for(var i = 0; i < length ; i++)
					{
						var row = formPane._rows[i].items[0].element;
						var rowCount = row.v('ROW_NUM');
						var columnCount = row.v('COL_NUM');
						if(rowCount > 0 && columnCount > 0)
						{
							var parentNode = new ht.Node();
							parentNode.setPosition({x:(portWidth+4)*columnCount/2 - (portWidth+4)/2,y:rowHeight + (portHeight+3)*rowCount/2 - portHeight/2});
							parentNode.setWidth((portWidth+4) * columnCount + 8);
							parentNode.setHeight((portHeight+3) * rowCount + 8);
							parentNode.setStyle('shape', 'rect');
							parentNode.setStyle('shape.background', '#D4D4D4');
							self._gv.dm().add(parentNode);
							for(var m = 0; m < rowCount; m++)
							{
								for(var n = 0; n < columnCount; n++)
								{
									var node = new ht.Node();
									node.setPosition({x:(portWidth+4)*n,y:rowHeight + portHeight*m});
									node.setImage('cab_dp_pos_port');
									self._gv.dm().add(node);
									node.setParent(parentNode);
								}
								rowHeight = rowHeight+3;
							}
							rowHeight = rowHeight + portHeight*rowCount + 45;
						}
					}
				}
			}
		};
		//初始化模块配置View
		self.initModuleCfgView();
		self.initPosCfgView();
	};
	ht.Default.def('Dms.Panel.FcabEditPanel',ht.widget.SplitView, {
		saveModuleCfg : function(){
			var self = this,
				dm = self._dm,
				formPane = self._modulePane;
			
			var length = formPane._rows.length;
			if(length > 0)
			{
				var moduleCfg = [];
				for(var i = 0; i < length ; i++)
				{
					var row = formPane._rows[i].items[0].element;
					if(row.v('LABEL_CN').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块名称不能为空！");
						return false;
					}
					if(row.v('ROW_NUM').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块端子行数不能为空！");
						return false;
					}
					if(row.v('COL_NUM').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块端子列数不能为空！");
						return false;
					}
					moduleCfg.push({
						'LABEL_CN' : row.v('LABEL_CN'),
						'ROW_NUM' : row.v('ROW_NUM'),
						'COL_NUM' : row.v('COL_NUM'),
						'START_CODE' : row.v('START_CODE'),
						'PREFIX' : row.v('PREFIX'),
						'SUFFIX' : row.v('SUFFIX')
					});
				}
				dm.sm().each(function(data){
					data.a('MODULE_CFG',moduleCfg);					
				});
			}
			
			formPane = self._posPane;
			var length = formPane._rows.length;
			if(length > 0)
			{
				var moduleCfg = [];
				for(var i = 0; i < length ; i++)
				{
					var row = formPane._rows[i].items[0].element;
					if(row.v('LABEL_CN').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个分光器名称不能为空！");
						return false;
					}
					if(row.v('RATION').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个分光比不能为空！");
						return false;
					}
					var ration = row.v('RATION');
					var rowCount = ration.substring(0, ration.indexOf(':'));
					var columnCount = ration.substring(ration.indexOf(':')+1);
					moduleCfg.push({
						'LABEL_CN' : row.v('LABEL_CN'),
						'ROW_NUM' : rowCount,
						'COL_NUM' : columnCount
					});
				}
				dm.sm().each(function(data){
					data.a('POS_CFG',moduleCfg);					
				});
			}
			return true;
		},
		initModuleCfgView : function(){
			var self = this;
			var dm = self._dm;
			var ld = self._dm.sm().ld();
			if(ld)
			{
				var moduleCfg = ld.a('MODULE_CFG');
				if(moduleCfg && moduleCfg.length > 0)
				{
					for(var i=0 ; i < moduleCfg.length ; i++)
					{
						self._modulePane.addRow([self.buildModule(self._modulePane,moduleCfg[i])],[0.1],130);
					}
				}else
				{
					var cfg = {};
					var bmClassId = ld.a('bmClassId');
					if(bmClassId === "FIBER_CAB"){
						cfg.ROW_NUM = '24';
						cfg.COL_NUM = '12';
					}else if(bmClassId === 'FIBER_DP'){
						cfg.ROW_NUM = '2';
						cfg.COL_NUM = '12';
					}
					self._modulePane.addRow([self.buildModule(self._modulePane,cfg)],[0.1],130);
				}
			}
		},
		initPosCfgView : function(){
			var self = this;
			var dm = self._dm;
			var ld = self._dm.sm().ld();
			if(ld)
			{
				var posCfg = ld.a('POS_CFG');
				if(posCfg && posCfg.length > 0)
				{
					for(var i=0 ; i < posCfg.length ; i++)
					{
						self._posPane.addRow([self.buildPos(self._posPane,posCfg[i])],[0.1],80);
					}
				}
			}
		},
		show : function(){
			var self = this;
			var width = 1000;
			var height = 460;
			if(dms.designer){
				if(dms.designer.isShanxi){
					height = 300;
				}
			}
			var panel = self.panel =  new ht.widget.Panel(
				{
					id : 'propertyEdit',
					title : "属性编辑",
					width : width,
					exclusive : false,
					titleColor : "white",
					minimizable : true,
					minimize : false,//控制打开时界面是不是最小化
					expand : true,
					resizeMode : 'none',
					contentHeight : height,
					buttons:['minimize'],
					content : self.getView()
				});
	
			panel.setPosition(100,40);
			panel.getView().style.zIndex=999;
			document.body.appendChild(panel.getView());
		},
		close : function(){
			document.body.removeChild(this.panel.getView());
		},
		saveAndCreateData : function(){
			var self = this,
				dm = self._dm,
				formPane = self._modulePane;
			
			var length = formPane._rows.length;
			if(length > 0)
			{
				var moduleCfg = [];
				for(var i = 0; i < length ; i++)
				{
					var row = formPane._rows[i].items[0].element;
					if(row.v('LABEL_CN').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块名称不能为空！");
						return false;
					}
					if(row.v('ROW_NUM').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块端子行数不能为空！");
						return false;
					}
					if(row.v('COL_NUM').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个模块端子列数不能为空！");
						return false;
					}
					moduleCfg.push({
						'LABEL_CN' : row.v('LABEL_CN'),
						'ROW_NUM' : row.v('ROW_NUM'),
						'COL_NUM' : row.v('COL_NUM'),
						'START_CODE' : row.v('START_CODE'),
						'PREFIX' : row.v('PREFIX'),
						'SUFFIX' : row.v('SUFFIX')
					});
					var lastData = self._dm.sm().ld();
					var portBmClassId = 'FIBER_DP_PORT';
					var data = new ht.Data();
					data.a('LABEL_CN', row.v('LABEL_CN'));
					data.a('RELATED_DEVICE_CUID', lastData.a('CUID'));
					if(lastData.a('bmClassId') == 'FIBER_CAB')
					{
						data.a('bmClassId', 'FIBERCABMODULE');
						portBmClassId = 'FCABPORT';
					}
					self._dm.add(data);
					data.setParent(lastData);
					var portCount = 1;
					for(var m = 0; m < row.v('ROW_NUM'); m++)
					{
						for(var n = 0; n < row.v('COL_NUM'); n++)
						{
							var port = new ht.Data();
							port.a('bmClassId', portBmClassId);
							port.a('RELATED_DEVICE_CUID', lastData.a('CUID'));
							if(lastData.a('bmClassId') == 'FIBER_CAB')
							{
								port.a('RELATED_MODULE_CUID', data.a('CUID'));
							}
							port.a('NUM_IN_MROW', m + 1);
							port.a('NUM_IN_MCOL', n + 1);
							port.a('LABEL_CN', portCount++);
							self._dm.add(port);
							port.setParent(data);
						}
					}
				}
				dm.sm().each(function(data){
					data.a('MODULE_CFG',moduleCfg);					
				});
			}
			
			formPane = self._posPane;
			var length = formPane._rows.length;
			if(length > 0)
			{
				for(var i = 0; i < length ; i++)
				{
					var row = formPane._rows[i].items[0].element;
					if(row.v('LABEL_CN').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个分光器名称不能为空！");
						return false;
					}
					if(row.v('RATION').length == 0)
					{
						tp.utils.optionDialog("温馨提示","第"+(i+1)+"个分光比不能为空！");
						return false;
					}
					var pos = new ht.Data();
					pos.a('bmClassId', 'AN_POS');
					pos.a('LABEL_CN', row.v('LABEL_CN'));
					pos.a('RATION', row.v('RATION'));
					pos.a('RELATED_CAB_CUID', dm.sm().ld().a('CUID'));
					pos.a('RELATED_ACCESS_POINT', dm.sm().ld().a('RELATED_SITE_CUID'));
					pos.a('RELATED_ACCESS_POINT_NAME', dm.sm().ld().a('RELATED_SITE_CUID_NAME'));
					pos.setParent(dm.sm().ld());
					dm.add(pos);
				}
			}
			return true;
		},
		buildModule : function(parent,moduleCfg){
			var self = this;
			var index = parent._rows.length;
			var last = {v : function(){return null;}};
			if(index > 0)
				last = parent._rows[index - 1].items[0].element;
			
			var formPane = new ht.widget.FormPane();
			formPane.addRow(['模块名称',{element:'*',color:'red'},{id: 'LABEL_CN',textField: {text:moduleCfg['LABEL_CN']|| last.v('LABEL_CN')||''}}],[40,14,0.1],22);
			formPane.addRow(['端子起始编号',{id: 'START_CODE',textField: {text: moduleCfg['START_CODE']||last.v('START_CODE')||'1',type:'number'}}],[80,0.1],22);
			formPane.addRow(['端子行数',{element:'*',color:'red'},{id: 'ROW_NUM',textField: {text:moduleCfg['ROW_NUM']||last.v('ROW_NUM')||'2',type:'number'}},
			                 '端子列数',{element:'*',color:'red'},{id: 'COL_NUM',textField: {text: moduleCfg['COL_NUM']||last.v('COL_NUM')||'12',type:'number'}}],[60,14,0.1,60,14,0.1],22);
			formPane.addRow(['端子前缀',{id: 'PREFIX',textField: {text: moduleCfg['PREFIX']||last.v('PREFIX')||''}},
			                 '端子后缀',{id: 'SUFFIX',textField: {text: moduleCfg['SUFFIX']||last.v('SUFFIX')||''}}],[60,0.1,60,0.1],22);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			var idTextfield = formPane._rows[1].items[1].element;
			formPane._rows[1].items[1].element._element.onchange = function(a){
				var idvalue = idTextfield.getValue();
				if(idvalue < 0)
				{
					tp.utils.optionDialog("温馨提示","编号不能为负数！");
					idTextfield.setValue(0);
				}
			};
			var rowTextfield = formPane._rows[2].items[2].element;
			rowTextfield._element.onchange = function(a){
				var value = rowTextfield.getValue();
				if(value < 1)
				{
					tp.utils.optionDialog("温馨提示","端子行数不能小于1！");
					rowTextfield.setValue(1);
					return;
				}
				else if(value > 24)
				{
					tp.utils.optionDialog("温馨提示","端子行数不能大于24！");
					rowTextfield.setValue(24);
					return;
				}
			};
			var colTextfield = formPane._rows[2].items[5].element;
			colTextfield._element.onchange = function(a){
				var value = colTextfield.getValue();
				if(value < 1)
				{
					tp.utils.optionDialog("温馨提示","端子列数不能小于1！");
					colTextfield.setValue(1);
					return;
				}
				else if(value > 12)
				{
					tp.utils.optionDialog("温馨提示","端子列数不能大于12！");
					colTextfield.setValue(12);
					return;
				}
			};
			
			return formPane;
		},
		
		buildPos : function(parent,posCfg){
			var index = parent._rows.length;
			var last = {v : function(){return null;}};
			if(index > 0)
				last = parent._rows[index - 1].items[0].element;
			
			var formPane = new ht.widget.FormPane();
			formPane.addRow(['分光器名称',{element:'*',color:'red'},{id: 'LABEL_CN',textField: {text:posCfg['LABEL_CN']|| last.v('LABEL_CN')||''}}],[40,14,0.1],22);
			var ration = new ht.widget.ComboBox();
			ration.setLabels(['1:4','1:8','1:16','1:32','1:64']);
			ration.setValues(['1:4','1:8','1:16','1:32','1:64']);
			ration.setValue('1:16');
			formPane.addRow(['分光比',{element:'*',color:'red'},{id: 'RATION', element:ration}],[40,14,0.1],22);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			
			return formPane;
		}
	});
})(this,Object);	
