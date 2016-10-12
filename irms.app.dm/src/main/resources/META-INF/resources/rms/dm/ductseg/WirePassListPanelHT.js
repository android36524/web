Ext.ns('Frame.op');
$importjs(ctx+'/dwr/interface/ReviewWireViewAction.js');


WirePassListPanel=function(param){
		
	var dataModel=new ht.DataModel();
	var basicFormPane=new ht.widget.FormPane();
	var treeTablePanel=new ht.widget.TreeTablePane(dataModel);
	treeTablePanel.getTableView().enableToolTip();
	basicFormPane.addRow([treeTablePanel],[0.1],0.1);
	treeTablePanel.getTableView().getTreeColumn().setDisplayName('光缆段名称');
	addRouteColumns(treeTablePanel);
	loadLayingWires(treeTablePanel,param);
	function loadLayingWires (tree,wireSystem){
		tp.utils.lock(basicFormPane);
		ReviewWireViewAction.getWireRoute(param.CUID, function(data) {
			var parentNode = null;
			for (var i = 0; i < data.length; i++) {
				var gridData = data[i];
				if(gridData){
					var cuid = gridData.CUID,
					lineSystemName = gridData.LABEL_CN,
					wireSegOrigPoint = gridData.WIRE_SEG_LUYOU,
					LAYTYPE = gridData.LAY_TYPE;
					if(cuid){
						parentNode = dataModel.getDataById(cuid);
						if(!parentNode){
							parentNode = new ht.Node();
							parentNode.a("CUID",cuid);
							parentNode.setId(cuid);
							parentNode.setName(lineSystemName);
							dataModel.add(parentNode);
						}
					}else{
						var node = new ht.Node();
						node.setName(lineSystemName);
						node.setAttr("LAYTYPE",LAYTYPE);
						node.setAttr("WIRE_SEG_LUYOU",wireSegOrigPoint);
						if(parentNode){
							node.setParent(parentNode);
						}
						dataModel.add(node);
					}
				}
			}
			treeTablePanel.getTableView().expandAll();
			tp.utils.unlock(basicFormPane);
		});
	}

	return basicFormPane;
};

function addRouteColumns(table,code){

	var cm=table.getColumnModel();
	//table.getTableView().getTreeColumn().setDisplayName('')
	 column = new ht.Column();
     column.setAccessType('attr');
     column.setName('LABEL_CN');
     column.setWidth(500);
     column.setVisible(false);
     column.setDisplayName('光缆段名称');
     cm.add(column);
     
     column = new ht.Column();
     column.setAccessType('attr');
     column.setName('LAYTYPE');
     column.setWidth(100);
     column.setDisplayName('敷设方式');
     cm.add(column);

     column = new ht.Column();
     column.setAccessType('attr');
     column.setName('WIRE_SEG_LUYOU');
     column.setWidth(600);
     column.setDisplayName('光缆段路由');
     column.getToolTip = function(data, tableView){
    	 return data.a('WIRE_SEG_LUYOU');
     }
     cm.add(column);
     
 };
