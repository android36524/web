Ext.ns('Frame.op');
$importjs(ctx+'/dwr/interface/ReviewWireViewAction.js');


InterWireListPanel=function(accessPointCuid){
		
	var dataModel=new ht.DataModel();
	var gv = new ht.graph.GraphView(dataModel);
	loadInterWires(accessPointCuid, dataModel);
  	var dialog = new ht.widget.Dialog({
        title: "<html><font size=2>层间光缆展示</font></html>",
        width: 800,
        height: 500,
        titleAlign: "center",
        draggable: true,
        closable: true,
        buttons: [{
            label: "确定"
        }, {
            label: "取消"
        }],
        content: gv.getView(),
        action: function(button, e) {
        	dialog.hide();
        }
  	});
	function loadInterWires (accessPointCuid, dataModel){
		tp.utils.lock(dialog);
		ReviewWireViewAction.getInterWires(accessPointCuid, function(data) {
			var icon = ctx +"/resources/map/images/16/";
			for (var i = 0; i < data.length; i++) {
				var gridData = data[i];
				if(gridData){
					var count = 0;
					if(i%2 == 0)
					{
						count = 0 - i/2;
					}
					else
					{
						count = i/2;
					}
					var cuid = gridData.CUID;
					var name = gridData.NAME;
					var origCuid = gridData.ORIG_POINT_CUID;
					var origName = gridData.ORIG_POINT_NAME;
					var destCuid = gridData.DEST_POINT_CUID;
					var destName = gridData.DEST_POINT_NAME;
					var origNode;
					if(!dataModel.getDataById(origCuid))
					{
						origNode = new ht.Node();
						origNode.setId(origCuid);
						origNode.setName(origName);
						origNode.setPosition(200, 200 + count*100);
						var origIcon = icon + origCuid.substring(0, origCuid.indexOf('-')).toLowerCase() + '.png';
						origNode.setIcon(origIcon);
						origNode.setImage(origIcon);
						dataModel.add(origNode);
					}
					var destNode;
					if(!dataModel.getDataById(destCuid))
					{
						destNode = new ht.Node();
						destNode.setId(destCuid);
						destNode.setName(destName);
						destNode.setPosition(600, 200 + count*100);
						var destIcon = icon + destCuid.substring(0, destCuid.indexOf('-')).toLowerCase() + '.png';
						destNode.setIcon(destIcon);
						destNode.setImage(destIcon);
						dataModel.add(destNode);
					}
					var edge = new ht.Edge();
					edge.setId(cuid);
					edge.setName(name);
					edge.setSource(origNode);
					edge.setTarget(destNode);
					dataModel.add(edge);
				}
			}
			tp.utils.unlock(dialog);
		});
	}

	return dialog;
};
