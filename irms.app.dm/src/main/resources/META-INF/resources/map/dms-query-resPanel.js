/**
 * 通过名称过滤查询资源数据
 */
(function(window,object,undefined){
	
	dms.Default.createQueryResPanel = function(pageTable){
		var self = this;
		self.pageTable = pageTable;
		var c = self.createPanel();
		return c;
	};
	
	ht.Default.def('dms.Default.createQueryResPanel',ht.widget.FormPane,{
		pageTable : null,
		createPanel : function(){
			var self = this;
	    	var basicFormPanel = new ht.widget.FormPane();
	    	var queryNode = new ht.Node();
	    	
	    	var labelCn = new ht.widget.TextField();
			
	    	var queryBtn = tp.utils.createButton('','查询');
	    	queryBtn.onclick = function(e){
	    		if(!labelCn || labelCn.getText().trim().length <=0){
	    			tp.utils.optionDialog("错误提示","名称不能为空！");
	    			return;
	    		}
	    		
	    		queryNode.a('LABEL_CN',labelCn.getText().trim());
	    		
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
				}
			};
			return param;
		}
	
	});
})(this,Object);


