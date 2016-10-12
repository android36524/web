/**
 * 光缆分段保存使用选择起止点
 */
(function(window,object,undefined){
	
	dms.Default.createQueryPointPanel = function(pageTable){
		var self = this;
		self.pageTable = pageTable;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('dms.Default.createQueryPointPanel',ht.widget.FormPane,{
		pageTable : null,
		createPanel : function(){
			var self = this;
	    	var basicFormPanel = new ht.widget.FormPane();
	    	var queryNode = new ht.Node();
	    	
	    	var labelCn = new ht.widget.TextField();
	    	//设备类型
			var configType = tp.utils.createComboBox({
			      values: ['SITE','ACCESSPOINT','FIBER_CAB','FIBER_DP','FIBER_JOINT_BOX'],
			      labels: ['站点', '接入点','光交接箱','光分纤箱','光接头盒']
			});
			configType.setValue('SITE');
			//数据库类型
			var dataBaseType = tp.utils.createComboBox({
			      values: ['DESIGN','RESOURCE'],
			      labels: ['设计数据', '现网数据']
			});
			dataBaseType.setValue('DESIGN');
			
	    	var queryBtn = tp.utils.createButton('','查询');
	    	queryBtn.onclick = function(e){
	    		if(!labelCn || labelCn.getText().trim().length <=0){
	    			tp.utils.optionDialog("错误提示","名称不能为空！");
	    			return;
	    		}
	    		
	    		queryNode.a('LABEL_CN',labelCn.getText().trim());
	    		queryNode.a('CONFIG_TYPE',configType.getValue());
	    		queryNode.a('DATA_BASE_TYPE',dataBaseType.getValue());
	    		
	    		var queryParams = {};
	    		var attrs = queryNode._attrObject;
	    		var param = self.queryFormParam();
	    		for(var p in attrs){
	    			param[p](queryParams, queryNode);
	    		}
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
			}; 
			
			basicFormPanel.addRow(['名称',labelCn,null,queryBtn],[60,0.1,10,80],25);
			
			basicFormPanel.addRow(['类型',configType,null,'数据来源',dataBaseType,null,clearBtn],[60,0.1,10,60,0.1,10,80],25);
			
	        return basicFormPanel;
		},
		queryFormParam : function(){
			var param = {
				'LABEL_CN' : function(_para, resNode,code) {
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
				'CONFIG_TYPE' : function(_para, resNode,code) {
					var configType = resNode.getAttr("CONFIG_TYPE");
					if (configType && configType != '0') {
						_para.CONFIG_TYPE = {
							"alias": null,
							"baseVaule": null,
							"key": "CONFIG_TYPE",
							"relation": "=",
							"type": "string",
							"value": configType
						};
					};
				},
				'DATA_BASE_TYPE' : function(_para, resNode,code) {
					var baseType = resNode.getAttr("DATA_BASE_TYPE");
					if (baseType && baseType != '0') {
						_para.DATA_BASE_TYPE = {
							"alias": null,
							"baseVaule": null,
							"key": "DATA_BASE_TYPE",
							"relation": "=",
							"type": "string",
							"value": baseType
						};
					};
				}
			};
			return param;
		}
	
	});
})(this,Object);


