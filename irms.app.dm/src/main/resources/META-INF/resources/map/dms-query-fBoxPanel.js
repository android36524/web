/**
 * 选择光接头盒
 */
(function(window,object,undefined){
	
	tp.utils.createQueryFBoxPanel = function(pageTable){
		var self = this;
		self.pageTable = pageTable;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('tp.utils.createQueryFBoxPanel',ht.widget.FormPane,{
		pageTable : null,
		createPanel : function(){
			var self = this;
	    	var basicFormPanel = new ht.widget.FormPane();
	    	var queryNode = new ht.Node();
	    	
			var labelCn = new ht.widget.TextField();
			var creator = new ht.widget.TextField();
			var location = new ht.widget.TextField();
			//熔接方式
			var connectType = tp.utils.createComboBox({
			      values: [-1, 0, 1, 2],
			      labels: ['全部', '未知', '熔接', '压接']
			});
			connectType.setValue(-1);
			//接头形式
			var junctionType = tp.utils.createComboBox({
			      values: [-1, 0, 1, 2],
			      labels: ['全部','未知', '直通', '分歧']
			});
			junctionType.setValue(-1);
			//维护方式
			var maintMode = tp.utils.createComboBox({
			      values: [-1, 0, 1, 2],
			      labels: ['全部','未知', '自维', '代维']
			});
			maintMode.setValue(-1);
			//设备类型
			var kind = tp.utils.createComboBox({
			      values: [-1, 0, 1, 2],
			      labels: ['全部','未知', '光接头盒', '光终端盒']
			});
			kind.setValue(-1);
			var districtCuid = new tp.widget.form.Combo({
				name : 'OWNERSHIP',
				label : '产权',
				values: [0,1,2,3,4,5,6],
                labels: ['未知','自建','共建','合建','租用','购买','置换']
			
			})
			
	    	var queryBtn = tp.utils.createButton('','查询');
	    	queryBtn.onclick = function(e){
				//CREATOR,CONNECT_TYPE,JUNCTION_TYPE,RELATED_DISTRICT_CUID,LOCATION,MAINT_MODE,KIND
	    		queryNode.a('LABEL_CN',labelCn.getText().trim());
	    		queryNode.a('OWNERSHIP',districtCuid.getValue());
	    		queryNode.a('CREATOR',creator.getText().trim());
	    		queryNode.a('LOCATION',location.getText().trim());
	    		queryNode.a('CONNECT_TYPE',connectType.getValue());
	    		queryNode.a('MAINT_MODE',maintMode.getValue());
	    		queryNode.a('JUNCTION_TYPE',junctionType.getValue());
	    		queryNode.a('KIND',kind.getValue());
	    		
	    		var queryParams = {};
	    		var attrs = queryNode._attrObject;
	    		var param = self.queryFormParam();
	    		for(var p in attrs){
	    			//tp.Default.PropertyQueryParam
	    			param[p](queryParams, queryNode);
	    		}
	    		
	    		//var gridCfg = pageTable.getGridCfg();
	    		var baseParams = {
	    			count : true,
	    			start : 0,
	    			limit : 20,
	    			totalNum : 20
	    		};
	    		self.pageTable.loadGridData(queryParams, baseParams);
			};       
			
	    	var clearBtn = tp.utils.createButton('','清空');
	    	
	    	clearBtn.onclick = function(e){
				self.pageTable.getTableView().dm().clear();
				labelCn.setText('');
				location.setText('');
				connectType.setValue(-1);
				maintMode.setValue(-1);
				junctionType.setValue(-1);
				kind.setValue(-1);
				districtCuid.reset();
				creator.setText('');
			}; 
			
			basicFormPanel.addRow(['名称',labelCn,'录入人',creator,null,queryBtn],[60,0.1,60,0.1,10,80],25);
			
			basicFormPanel.addRow(['产权归属',districtCuid,'地址',location,null,clearBtn],[60,0.1,60,0.1,10,80],25);
			
	        return basicFormPanel;
		},
		queryFormParam : function(){
			var param = {
				'LABEL_CN': function(_para, resNode,code) {
					var labelcn = resNode.getAttr("LABEL_CN");
					if (labelcn && labelcn != null) {
						_para.LABEL_CN = {
							"alias": null,
							"baseVaule": null,
							"key": "LABEL_CN",
							"relation": "like",
							"type": "string",
							"value": "%" + labelcn + "%"
						};
					};
				},
				'OWNERSHIP': function(_para, resNode,code) {
					var relatedDistrictCuid = resNode.getAttr("OWNERSHIP");
					if (relatedDistrictCuid && relatedDistrictCuid != '0') {
						_para.OWNERSHIP = {
							"alias": null,
							"baseVaule": null,
							"key": "OWNERSHIP",
							"relation": "=",
							"type": "string",
							"value": relatedDistrictCuid
						};
					};
				},
				'MAINT_MODE': function(_para, resNode,code) {
					if (resNode.getAttr("MAINT_MODE") != null) {
						if (resNode.getAttr("MAINT_MODE") != "-1") {
							_para.MAINT_MODE = {
								"alias": null,
								"baseVaule": null,
								"key": "MAINT_MODE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("MAINT_MODE")
							};
						};
					};
				},
				'CONNECT_TYPE': function(_para, resNode,code) {
					if (resNode.getAttr("CONNECT_TYPE") != null) {
						if (resNode.getAttr("CONNECT_TYPE") != "-1") {
							_para.CONNECT_TYPE = {
								"alias": null,
								"baseVaule": null,
								"key": "CONNECT_TYPE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("CONNECT_TYPE")
							};
						};
					};
				},
				'JUNCTION_TYPE': function(_para, resNode,code) {
					if (resNode.getAttr("JUNCTION_TYPE") != null) {
						if (resNode.getAttr("JUNCTION_TYPE") != "-1") {
							_para.JUNCTION_TYPE = {
								"alias": null,
								"baseVaule": null,
								"key": "JUNCTION_TYPE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("JUNCTION_TYPE")
							};
						};
					};
				},
				'KIND': function(_para, resNode,code) {
					if (resNode.getAttr("KIND") != null) {
						if (resNode.getAttr("KIND") != "-1") {
							_para.KIND = {
								"alias": null,
								"baseVaule": null,
								"key": "KIND",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("KIND")
							};
						};
					};
				},
				'LOCATION': function(_para, resNode,code) {
					if (resNode.getAttr("LOCATION") != '') {
						_para.LOCATION = {
							"alias": null,
							"baseVaule": null,
							"key": "LOCATION",
							"relation": "like",
							"type": "string",
							"value": resNode.getAttr("LOCATION") + "%"
						};
					}
				},
				'CREATOR': function(_para, resNode,code) {
					if (resNode.getAttr("CREATOR") != '') {
						_para.CREATOR = {
							"alias": null,
							"baseVaule": null,
							"key": "CREATOR",
							"relation": "like",
							"type": "string",
							"value": resNode.getAttr("CREATOR") + "%"
						};
					}
				},
			};
			return param;
		}
	
	});
	
	//光缆系统
	tp.utils.createQueryWireSystemPanel = function(pageTable){
		var self = this;
		self.pageTable = pageTable;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('tp.utils.createQueryWireSystemPanel',ht.widget.FormPane,{
		pageTable : null,
		createPanel : function(){
			var self = this;
	    	var basicFormPanel = new ht.widget.FormPane();
	    	var queryNode = new ht.Node();
	    	
			var labelCn = new ht.widget.TextField();
			var creator = new ht.widget.TextField();			
			var districtCuid = new tp.widget.form.Combo({
				name : 'OWNERSHIP',
				label : '产权',
                values: [0,1,2,3,4,5,6],
                labels: ['未知','自建','共建','合建','租用','购买','置换']
			
			})

	    	var queryBtn = tp.utils.createButton('','查询');
	    	queryBtn.onclick = function(e){
				//CREATOR,CONNECT_TYPE,JUNCTION_TYPE,RELATED_DISTRICT_CUID,LOCATION,MAINT_MODE,KIND
	    		queryNode.a('LABEL_CN',labelCn.getText().trim());
	    		queryNode.a('OWNERSHIP',districtCuid.getValue());
	    		queryNode.a('CREATOR',creator.getText().trim());
	    		
	    		var queryParams = {};
	    		var attrs = queryNode._attrObject;
	    		var param = self.queryFormParam();
	    		for(var p in attrs){
	    			//tp.Default.PropertyQueryParam
	    			param[p](queryParams, queryNode);
	    		}
	    		
	    		//var gridCfg = pageTable.getGridCfg();
	    		var baseParams = {
	    			count : true,
	    			start : 0,
	    			limit : 20,
	    			totalNum : 20
	    		};
	    		self.pageTable.loadGridData(queryParams, baseParams);
			};       
			
	    	var clearBtn = tp.utils.createButton('','清空');
	    	
	    	clearBtn.onclick = function(e){
				self.pageTable.getTableView().dm().clear();
				labelCn.setText('');	
				/*maintMode.setValue(-1);*/
				districtCuid.reset();
				creator.setText('');
			}; 
			
			basicFormPanel.addRow(['名称',labelCn,'录入人',creator,null,queryBtn],[60,0.1,60,0.1,10,80],25);
			
			basicFormPanel.addRow(['产权归属',districtCuid,null,null,null,clearBtn],[60,0.1,60,0.1,10,80],25);
			
	        return basicFormPanel;
		},
		queryFormParam : function(){
			var param = {
				'LABEL_CN': function(_para, resNode,code) {
					var labelcn = resNode.getAttr("LABEL_CN");
					if (labelcn && labelcn != null) {
						_para.LABEL_CN = {
							"alias": null,
							"baseVaule": null,
							"key": "LABEL_CN",
							"relation": "like",
							"type": "string",
							"value": "%" + labelcn + "%"
						};
					};
				},
				'OWNERSHIP': function(_para, resNode,code) {
					var relatedDistrictCuid = resNode.getAttr("OWNERSHIP");
					if (relatedDistrictCuid && relatedDistrictCuid != '-1') {
						_para.OWNERSHIP = {
							"alias": null,
							"baseVaule": null,
							"key": "OWNERSHIP",
							"relation": "=",
							"type": "string",
							"value": relatedDistrictCuid
						};
					};
				},
				'MAINT_MODE': function(_para, resNode,code) {
					if (resNode.getAttr("MAINT_MODE") != null) {
						if (resNode.getAttr("MAINT_MODE") != "-1") {
							_para.MAINT_MODE = {
								"alias": null,
								"baseVaule": null,
								"key": "MAINT_MODE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("MAINT_MODE")
							};
						};
					};
				},
				'CONNECT_TYPE': function(_para, resNode,code) {
					if (resNode.getAttr("CONNECT_TYPE") != null) {
						if (resNode.getAttr("CONNECT_TYPE") != "-1") {
							_para.CONNECT_TYPE = {
								"alias": null,
								"baseVaule": null,
								"key": "CONNECT_TYPE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("CONNECT_TYPE")
							};
						};
					};
				},
				'JUNCTION_TYPE': function(_para, resNode,code) {
					if (resNode.getAttr("JUNCTION_TYPE") != null) {
						if (resNode.getAttr("JUNCTION_TYPE") != "-1") {
							_para.JUNCTION_TYPE = {
								"alias": null,
								"baseVaule": null,
								"key": "JUNCTION_TYPE",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("JUNCTION_TYPE")
							};
						};
					};
				},
				'KIND': function(_para, resNode,code) {
					if (resNode.getAttr("KIND") != null) {
						if (resNode.getAttr("KIND") != "-1") {
							_para.KIND = {
								"alias": null,
								"baseVaule": null,
								"key": "KIND",
								"relation": "=",
								"type": "string",
								"value": resNode.getAttr("KIND")
							};
						};
					};
				},
				'LOCATION': function(_para, resNode,code) {
					if (resNode.getAttr("LOCATION") != '') {
						_para.LOCATION = {
							"alias": null,
							"baseVaule": null,
							"key": "LOCATION",
							"relation": "like",
							"type": "string",
							"value": resNode.getAttr("LOCATION") + "%"
						};
					}
				},
				'CREATOR': function(_para, resNode,code) {
					if (resNode.getAttr("CREATOR") != '') {
						_para.CREATOR = {
							"alias": null,
							"baseVaule": null,
							"key": "CREATOR",
							"relation": "like",
							"type": "string",
							"value": resNode.getAttr("CREATOR") + "%"
						};
					}
				},
			};
			return param;
		}
	
	});
})(this,Object);


