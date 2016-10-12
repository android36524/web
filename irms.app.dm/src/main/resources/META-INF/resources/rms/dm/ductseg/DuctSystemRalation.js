Ext.ns('Frame.op');
$importjs(ctx + "/map/dms-tools.js"); 
$importjs(ctx + "/resources/rms/dm/plugins/tbar/DuctSystemGridTbar.js"); 
$importjs(ctx + "/map/dms-tools.js"); 
$importjs(ctx+'/dwr/interface/ReviewWireViewAction.js');


Frame.op.DuctSystemRePanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.op.DuctSystemRePanel.superclass.constructor.call(this,config);
		this.labelcn = config.labelcn;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.op.DuctSystemRePanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.op.DuctSystemRePanel.superclass.afterRender.call(this);
		try{
			var wireToRemainMainView = new Frame.op.getWireToLinePanel({LABEL_CN:this.labelcn,CUID:this.cuid});
			var jv = wireToRemainMainView.getView();
			jv.className = 'graphView';
			var jumpPanelDiv = document.getElementById(this.cuid);
			jumpPanelDiv.firstChild.firstChild.appendChild(jv);
		}catch(e){
			Ext.Msg.alert('错误提示信息：',e); 
		}
	}
});


Frame.op.getWireToLinePanel=function(param){

var dataModel=new ht.DataModel();
var basicFormPane=new ht.widget.FormPane();
var treeTablePanel=new ht.widget.TreeTablePane(dataModel);
basicFormPane.addRow([treeTablePanel],[0.2],[0.1]);
treeTablePanel.getTableView().getTreeColumn().setVisible(false);
addColumns(treeTablePanel);
treeTablePanel.getTableView().setLoader({
	load: function(data){
		var systemCuid = data._id;
		var indexInRouteBegin = data.getAttr("indexInRouteBegin");;
		var indexInRouteEnd = data.getAttr("indexInRouteEnd");
		var segCuid = code;
		var sNode=dataModel.getDataById(param.CUID);
		
	},
	isLoaded: function(data){
		return !data.needToLoad;
	}
});

var node = new ht.Node();
node.a("LABEL_CN",'aaaaaaaaaaaaa');
node.setIcon('');
node.needToLoad = true;
node.setParent(''); // or parent.addChild(data);
dataModel.add(node); 
/*ReviewWireViewAction.getRemainInfo(param.CUID,function(data){
	for(var i=0;i<data.length;i++){
		var gridData =data[i];
		var lineSystemName=gridData.LINE_SYSTEM_NAME;
		var wireSegOrigPoint=gridData.WIRE_SEG_ORIG_POINT;
		var wireSegDestPoint=gridData.WIRE_SEG_DEST_POINT;
		var node = new ht.Node();
		node.a("LABEL_CN",lineSystemName);
		node.a("WIRE_SEG_ORIG_POINT",wireSegOrigPoint);
		node.a("WIRE_SEG_DEST_POINT",wireSegDestPoint);
		dataModel.add(node);
		node.needToLoad = true;
//		var parent =dataModel.getDataById(param.CUID);
//		var child = data[i];
//	  
//		var htdata =dataModel.getDataById(child.CUID);
//		if(htdata == null)
//		{	
//			htdata = new ht.Node(child.CUID);
//			htdata.setId(child.CUID);
//			htdata.setAttr("cuid",child.CUID);
//			if(param.CUID.indexOf("FIBER-")>-1)
//			{
//				var name = child.LABEL_CN;
//			
//				htdata.setName(name);
//			}else
//			{
//				htdata.setName(child.WIRE_SYSTEM_NAME);
//			}
//		
//			if(parent)
//			parent.addChild(htdata);
//			htdata.setAttr("ORIG_POINT_CUID",htdata.ORIG_POINT_CUID);
//			htdata.setAttr("DEST_POINT_CUID",htdata.DEST_POINT_CUID);
//			dataModel.add(htdata);
//			
//			
//		}
//	
//	if(parent)
//		parent.setAttr("expanded",false);
	}	

});*/


return basicFormPane;
};

function addColumns(table,code){

	var cm=table.getColumnModel();
	//table.getTableView().getTreeColumn().setDisplayName('')
	 column = new ht.Column();
     column.setAccessType('attr');
     column.setName('LABEL_CN');
     column.setWidth(100);
     column.setDisplayName('承载对象');
     cm.add(column);
     
     column = new ht.Column();
     column.setAccessType('attr');
     column.setName('WIRE_SEG_ORIG_POINT');
     column.setWidth(100);
     column.setDisplayName('管道起点');
     cm.add(column);
//     WIRE_SEG_DEST_POINT
//  
//     WIRE_SEG_ORIG_POINT
     column = new ht.Column();
     column.setAccessType('attr');
     column.setName('WIRE_SEG_DEST_POINT');
     column.setWidth(100);
     column.setDisplayName('管道终点');
     cm.add(column);
    
//     
     column = new ht.Column();
     column.setAccessType('attr');
     column.setName('ORIG_POINT_NAME');
     column.setWidth(100);
     column.setDisplayName('管道编号');
     cm.add(column);
     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DEST_POINT_NAME');
//     column.setWidth(100);
//     column.setDisplayName('子孔编号');
//     cm.add(column);
//     
//  
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DIRECTION');
//     column.setWidth(100);
//     column.setDisplayName('光缆名称');
//     cm.add(column);
//     
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('ORIG_POINT_NAME');
//     column.setWidth(100);
//     column.setDisplayName('光缆级别');
//     cm.add(column);
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DEST_POINT_NAME');
//     column.setWidth(100);
//     column.setDisplayName('光缆段起点');
//     cm.add(column);
//     
//  
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DIRECTION');
//     column.setWidth(100);
//     column.setDisplayName('光缆段终点');
//     cm.add(column);
//     
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DEST_POINT_NAME');
//     column.setWidth(100);
//     column.setDisplayName('光缆段芯数');
//     cm.add(column);
//     
//  
//     
//     column = new ht.Column();
//     column.setAccessType('attr');
//     column.setName('DIRECTION');
//     column.setWidth(100);
//     column.setDisplayName('敷设对象');
//     cm.add(column);
//     
     
     
     
     
 };
