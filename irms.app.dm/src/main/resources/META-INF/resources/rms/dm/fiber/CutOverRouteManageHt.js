$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/map/dms-tools.js');
$importjs(ctx+'/map/dms-utils.js');
$importjs(ctx+'/map/dms-wiretoductline.js');

$importjs(ctx+'/dwr/interface/CutOverRouteManageAction.js');

(function(window,object,undefined){
	
	dms.Default.CutOverRouteManageHt = function(cuid,dialog){
		dms.Default.CutOverRouteManageHt.superClass.constructor.apply(this);
		var self = this;
		self.cuid = cuid;
		self.dialog = dialog;
		self.treeDataModel = new ht.DataModel();
		
		var c =self.createPanel();
		
		self.initPointTree();
		return c;
	};
	
	ht.Default.def('dms.Default.CutOverRouteManageHt', ht.widget.FormPane,{
		cuid:null,
		treeView:null,
		treeDataModel:null,
		dialog:null,
		
		createPanel:function(){
			var self = this;
			var basicFormPane = new ht.widget.FormPane();
			tp.utils.lock(basicFormPane);
			
			var buttomView = self.createBottomPanel();
			var treeView = self.treeView = new ht.widget.TreeTablePane(self.treeDataModel);
			treeView.getTableView().getTreeColumn().setDisplayName('光缆段');
		    self.addColumns(treeView);
		    
			basicFormPane.addRow([treeView],[0.1],0.1);
			basicFormPane.addRow([buttomView],[0.1],30);
			tp.utils.unlock(basicFormPane);
			return basicFormPane;
		},
		addColumns : function(table){
			 var cm = table.getColumnModel();
		        
		     var column = new ht.Column();
	         column.setAccessType('attr');
	         column.setName('WIRE_SEG_NAME');
	         column.setWidth(500);
	         column.setDisplayName('光缆段名称');
	         cm.add(column);
		},
		createBottomPanel : function(){
			//底层按钮操作区
			var self = this;
			var buttonItems = [
			{
				type: 'button',
				label: '具体路由',
				disabled: false,
				action: function() {
					var records=self.treeDataModel.sm().getSelection();
					var cuid=records.get(0).a('CUID');
//					var dialog = new ht.widget.Dialog(); 
//					var labelcn=records[0].data['LABEL_CN'];
					
					self.dialog._win();
					dms.Tools.wireToLayDuctLine(cuid);
				}
			}];
			
			var btnToolbar = self.buttonToolbar = new ht.widget.Toolbar(buttonItems);
			//btnToolbar.setStickToRight(true);
		
			return btnToolbar;
		},
		initPointTree:function(){
			var self = this;
			CutOverRouteManageAction.getWireSegs(self.cuid,{
				callback:function(datas){
					datas.forEach(function(data){
						lineSegNode = new ht.Node();
						lineSegNode.setId(data.CUID);
						lineSegNode.setName(data.WIRE_SEG_NAME);
						lineSegNode.a("CUID",data.CUID);
						lineSegNode.a("WIRE_SEG_NAME",data.WIRE_SEG_NAME);
						self.treeDataModel.add(lineSegNode);
					});
				},
				errorHandler:function(errorMessage){
					tp.utils.optionDialog("异常提示",'<div style="text-align:center;font-size:12px;color:red">'+errorMessage+'</div>');
					return;
				}
			});
		},
		doNextDrop:function(){
			var self = this;
			CutOverPointSelectAction.saveInfos({
				callback:function(data){
					if(!data)
						return;
				},
			    errorHandler:function(errorMessage){
			    	tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">'+errorMessage+'</div>');
			    }
			});
		    
		}
	});
})(this,Object);