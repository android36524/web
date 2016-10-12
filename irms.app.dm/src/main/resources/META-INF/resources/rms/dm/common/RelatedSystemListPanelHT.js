$importjs(ctx+'/dwr/interface/RelatedSystemAction.js');
(function(window,object,undefined){
	"use strict";
	//查看关联线设施
	dms.createRelatedSystemListPanel = function(param){
		var self = this;
		self.param = param;
		var c=self.createPanel();
		return c;
	};
	ht.Default.def('dms.createRelatedSystemListPanel',ht.widget.FormPane,{
		param : null,
		treeTablePane : null,
		treeDataModel : null,
		basicFormPane : null,
		createPanel : function(){
			var self = this;
			var treeDataModel = self.treeDataModel = new ht.DataModel();
		    var basicFormPane = new ht.widget.FormPane(); 
		    
		    var treeTablePane = self.treeTablePane = new ht.widget.TreeTablePane(treeDataModel);
		    basicFormPane.addRow([treeTablePane],[0.1],[0.1]);
		    treeTablePane.getTableView().getTreeColumn().setDisplayName('关联的线设施名称');
		    
		    self.addColumns();
		    self.loadRelatedSystems();
		    return basicFormPane;
		},
		
		loadRelatedSystems : function(){
			var self = this;
	    	tp.utils.lock(self.basicFormPane);
	    	
	    	var parentNode = new ht.Node();
			parentNode.setName(self.param.LABEL_CN);
			self.treeDataModel.add(parentNode);
			
	    	RelatedSystemAction.getRelatedSystemList(self.param.CUID,{
				callback : function(data) {
					if (data) {
						for (var i = 0; i < data.length; i++) {
							var gridData = data[i];
							if(gridData){
								var labelCn = gridData.LABEL_CN,
								relatedSystem=gridData.RELATED_SYSTEM_CUID,
								relatedBranch=gridData.RELATED_BRANCH_CUID;
								
								var node = new ht.Node();
								node.setName(labelCn);
								node.setAttr("RELATED_SYSTEM_CUID",relatedSystem);
								node.setAttr("RELATED_BRANCH_CUID",relatedBranch);
								node.setAttr("LABEL_CN",labelCn);
								if(parentNode){
									node.setParent(parentNode);
								}
								self.treeDataModel.add(node);
							}
						}
						self.treeTablePane.getTableView().expandAll();
					}
					tp.utils.unlock(self.basicFormPane);
				},
				errorHandler : function(error) {
					tp.utils.optionDialog("错误提示：",error);
					tp.utils.unlock(self.basicFormPane);
				}
			});   
		},
	    addColumns : function() {
	    	var self = this;
	        var cm = self.treeTablePane.getColumnModel(),
	        column = new ht.Column();
	        column.setAccessType('attr');
	        column.setName('RELATED_SYSTEM_CUID');
	        column.setWidth(200);
	        column.setVisible(true);
	        column.setDisplayName('所属系统');
	        cm.add(column);
	        
	        
	        column = new ht.Column();
	        column.setAccessType('attr');
	        column.setName('RELATED_BRANCH_CUID');
	        column.setWidth(200);
	        column.setDisplayName('所属分支');
	        cm.add(column);
	        
	        column = new ht.Column();
	        column.setAccessType('attr');
	        column.setName('LABEL_CN');
	        column.setWidth(200);
	        column.setDisplayName('所属段');
	        cm.add(column);
	     }
	});
	
})(this,Object);