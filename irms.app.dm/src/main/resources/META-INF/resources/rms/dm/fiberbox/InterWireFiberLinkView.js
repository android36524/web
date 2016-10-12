Ext.ns('Frame.op');
$importjs(ctx+'/dwr/interface/InterFiberLinkAction.js');

Frame.op.InterWireFiberLinkPanel = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		config = config || {};
		config.id = config.cuid;
		Frame.op.InterWireFiberLinkPanel.superclass.constructor.call(this,
				config);
		this.bmClassId = config.bmClassId;
		this.cuid = config.cuid;
	},
	initComponent : function() {
		Frame.op.InterWireFiberLinkPanel.superclass.initComponent.call(this);
	},
	afterRender: function(){
		Frame.op.InterWireFiberLinkPanel.superclass.afterRender.call(this);
		
		var fiberLinkMainView = new Frame.op.InterWireFiberLinkView(this.bmClassId,this.cuid);
		var jv = fiberLinkMainView.getView();
		jv.className = 'graphView';   
		var fiberPanelDiv = document.getElementById(this.cuid);
		fiberPanelDiv.firstChild.firstChild.appendChild(jv);
	}
});

ht.Default.setImage('/resources/topo/alarm/c.png',ctx+'/resources/topo/alarm/c.png');
ht.Default.setImage('/resources/topo/alarm/y.png',ctx+'/resources/topo/alarm/y.png');
ht.Default.setImage('/resources/topo/alarm/w.png',ctx+'/resources/topo/alarm/w.png');
ht.Default.setImage('/resources/topo/alarm/m.png',ctx+'/resources/topo/alarm/m.png');
ht.Default.setImage('/resources/topo/alarm/u.png',ctx+'/resources/topo/alarm/u.png');
ht.Default.setImage('/resources/topo/dm/fixed.png',ctx+'/resources/topo/dm/fixed.png');
ht.Default.setImage('/resources/topo/dm/isfixed.gif',ctx+'/resources/topo/dm/isfixed.gif');
ht.Default.setImage('/resources/topo/dm/Ddfmodule.gif',ctx+'/resources/map/FIBER_CAB.png');
ht.Default.setImage('/resources/topo/dm/HangWall.gif',ctx+'/resources/topo/dm/HangWall.gif');
ht.Default.setImage('/resources/topo/dm/WireSystem.gif',ctx+'/resources/topo/dm/WireSystem.gif');

Frame.op.InterWireFiberLinkView=function(c,id) {
	var self = this;
	self.bmClassId = c;
	self.cuid = id;
						
	Frame.op.InterWireFiberLinkView.superClass.constructor.apply(this);
	
//	var bmClassType = "";
//	if(self.bmClassId ==='FIBER_CAB')
//		bmClassType = "光交接箱";  
//	if(self.bmClassId ==='FIBER_DP')
//		bmClassType = "光分纤箱";
	
	//A端
	var aSelectView = new ht.widget.TabView();
//	var aToolbar = new ht.widget.Toolbar([{
//						label : bmClassType
//                    }]);
	
    var aBorderPane = new ht.widget.BorderPane();
//    aBorderPane.setTopView(aToolbar);
    
    //端口端子列表
    self.aDataModel = new ht.DataModel();
    self.aPortListView = new Frame.op.aPortListView("端子列表",self.aDataModel);
    
	aBorderPane.setCenterView(self.aPortListView);
	
	aSelectView.add("A端",aBorderPane,true);
	aSelectView.setTabBackground("#6FB1DD");
	aSelectView.setSelectBackground("rgba(0,0,0,0)");
	
	//Z端
	var zSelectView = new ht.widget.TabView();
//	
//	var zToolbar = new ht.widget.Toolbar([{
//		label : bmClassType
//    }]);
	
    var zBorderPane = new ht.widget.BorderPane();
//    zBorderPane.setTopView(zToolbar);
    
    //端口端子列表
    self.zDataModel = new ht.DataModel();
    self.zPortListView = new Frame.op.zPortListView("纤芯列表",self.zDataModel);
    
	zBorderPane.setCenterView(self.zPortListView);
	
	zSelectView.add("光缆",zBorderPane,true);
	zSelectView.setTabBackground("#6FB1DD");
	zSelectView.setSelectBackground("rgba(0,0,0,0)");
	
	//上层选择区
	var splitView = new ht.widget.SplitView(aSelectView,zSelectView,'horizontal', 0.5);

   //按钮操作区域
   var buttonItems = [
        
{
	   type:'button',
	   label:'关联',
	   action:function(item){
			self.doRelevancy();
     	item.selected=false;
	   }
	   },{type: 'button',
        label: '断开', 
        disabled : false,
        action: function(item) {
        	self.doDisconnect();
        	item.selected=false;
        }
    },{
        type: 'button',
        label: '确定',   
        disabled : true,
        action: function(item) {
        	self.doConfim();
        }
    },{
        type: 'button',
        label: '取消',   
        disabled : true,
        action: function(item) {
        	self.doCancel();
        	item.selected=false;
            }
} ]; 
self.buttonToolbar = new ht.widget.Toolbar(buttonItems);
self.buttonToolbar.setStickToRight(true);

self.setCenterView(splitView);
self.setBottomView(self.buttonToolbar);
//初始化数据
self.initDataModel();
self.aPortListView.treeView.onDataDoubleClicked = function (data) {
	self.onDataDoubleClicked(data,"a");
};
self.zPortListView.treeView.onDataDoubleClicked = function (data) {
	self.onDataDoubleClicked(data);
};
};
ht.Default.def('Frame.op.InterWireFiberLinkView', ht.widget.BorderPane, {
	bmClassId : null,
	cuid : null,
	aDataModel:null,
	zDataModel:null,
	aPortListView:null,
	zPortListViewListView:null,
	buttonToolbar:null,
	azCancelData:[],
	fiberCancelData:[],
	isConnectTofiber:0,
	action_type : "add",
	initDataModel : function(){
		var scope = this;
		scope.aPortListView.getChildrenByParentCuid(scope.cuid);
		scope.zPortListView.getMsgChildrenByParentCuid(scope.cuid);
	},
	doCancel:function(){
		var self = this;
		var fiberData=self.fiberCancelData;
		if(fiberData){
			for(var i=0;i<fiberData.length;i++){
				var fData=fiberData[i];
				var pData=fData._children;
				var deleteData=new Array();
				for(var j=0;j<pData.size();j++){
					var nData=pData.get(j);
					var newData=nData.getAttr("newnode");
					if(newData){
						deleteData.push(nData);
					}
				}
				if(deleteData && deleteData.length>0){
					for(var k=0;k<deleteData.length;k++){
						var dData = deleteData[k];
						pData.remove(dData);
						self.zDataModel.remove(dData);
					}
				}
			}
		}
		self.aPortListView.unlock();
		self.zPortListView.unlock();
		var items = self.buttonToolbar.getItems();
		if(items){
			for(var i=0;i<items.length;i++){
				var item = items[i];
				var label = item.label;
				if(label != "确定" && label != "取消"){
					item.disabled = false;
				}else{
					item.disabled = true;
				}
				item.selected = false;
			}
		}
	},
	doDisconnect: function(){
		var self = this;
		self.fiberCancelData.splice(0);
		self.action_type = "delete";
		try{
			var selectFibers=self.zDataModel.sm();
			if(selectFibers==null || selectFibers.size() ==0){
				tp.utils.optionDialog("错误提示信息","请选择需要断开的纤芯！");
				return;
			}
			self.aPortListView.lock();
			self.zPortListView.lock();
			var items=self.buttonToolbar.getItems();
			if(items){
				for(var i=0;i<items.length;i++){
					var item = items[i];
					var label = item.label;
					if(label != "确定" && label != "取消"){
						item.disabled = true;
					}else{
						item.disabled = false;
					}
					item.selected = false;
				}
			}

		} catch (e) {

		} finally {

		}

	},
		queryFiberLink : function(){
			var self = this;
			var cuid = "";
			var azportselect = "";
			self.fiberCancelData.splice(0);
			var azports= self.aDataModel.sm();
			var portCuid ="";
			azports.each(function(data){
				portCuid = data.getAttr("cuid");
				azportselect = azportselect+portCuid+",";
			},azports);
			azportselect = azportselect.substring(0,azportselect.length-1);
			
			if(azportselect == null || azportselect.length==0){
				tp.utils.optionDialog("错误提示信息","请选择左侧端子！"); 
				return;
			}
			FiberLinkAction.getportFibersystemChildren(portCuid,function(data){
				
			   if(data && data.length>0)
					{
						for(var i=0;i<data.length;i++){
							var arr = data[i];
							var fCuid = arr.CUID;
							var fiberData = self.zDataModel.getDataById(fCuid);
							var wire=fiberData._parent;
							var wire=fiberData._parent;
							var wiresegId=wire._id;
							var wireSystemData = self.zDataModel.getDataById(wiresegId);
						    var wsExpend = wireSystemData.getAttr("expanded");
							if(wsExpend){//如果光缆系统展开，查光缆段
							var wireSegData = self.zDataModel.getDataById(wiresegId);
							var segExpend = wireSegData.getAttr("expanded");
							if(segExpend){//如果光缆段展开，找纤芯
								var fiberData = self.zDataModel.getDataById(fCuid);
								var fiberExpend = fiberData.getAttr("expanded");
								if(fiberExpend){//已经展开的话，直接选中端子
									var ports = fiberData._children;
									azports.each(function(data){
										var portCuid = data.getAttr("cuid");
										ports.each(function(pdata){
											var pid = pdata.getAttr("cuid");
											if(pid == portCuid){
												var pd = self.zDataModel.getDataById(pid);
												if(pd){
													self.zDataModel.getSelectionModel().setSelection(pd);
												}
											}
										},ports);
									},azports);
								}
						}
					}
				}
			}
		});
	},
								
doRelevancy : function (){
	var self = this;
	self.azCancelData.splice(0);
	self.fiberCancelData.splice(0);
	self.action_type = "add";
	var selectPorts = self.aDataModel.sm();
	var selectFibers = self.zDataModel.sm();
	if(selectPorts == null || selectFibers == null || selectPorts.size() == 0 || selectFibers.size() == 0){
		        	//请选择需要关联的纤芯和端子
	tp.utils.optionDialog("错误提示信息","没有选择需要关联的纤芯和端子,请选择后再关联！"); 
	return;
	}
	if (selectPorts.size() != selectFibers.size()) {
		        	//纤芯关联以纤芯列表为准,选择的纤芯数量必须小于或者等于选中的端子数量
	tp.utils.optionDialog("错误提示信息","所选择的端子数据必须和选择的纤芯数量相同！"); 
	return;
	} 		
	var portselect = "";
	var checked = true;
	selectPorts.each(function(data){
		var portCuid = data.getAttr("cuid");
		var portBm = data.getAttr("BM_CLASS_ID");
		if(!(portBm=="ODFPORT" || portBm=="FIBER_JOINT_POINT" || portBm=="PTP"||portBm=="FCABPORT")){
			//做关联时端子列表必须选择端子
			//var dialog = Topo.x.showTopoDialog("错误提示信息","做关联时端子列表中选择的必须是端子！");
			tp.utils.optionDialog("错误提示信息","做关联时端子列表中选择的必须是端子！"); 
			checked = false;
			return;
		}
		
		portselect = portselect+portCuid+",";
	},selectPorts);
	portselect = portselect.substring(0,portselect.length-1);
	
	var fiberselect = "";
	selectFibers.each(function(data){
		var fiberCuid = data._id;
		var fiberBm = data.getAttr("BM_CLASS_ID");
		if (!(fiberBm=="FIBER")) {
			//判断光缆列表选中元素类型,做关联时纤芯列表中必须选择纤芯
			tp.utils.optionDialog("错误提示信息","做关联时纤芯列表中必须选择纤芯！"); 
			//var dialog = Topo.x.showTopoDialog("错误提示信息","做关联时纤芯列表中必须选择纤芯！");
			checked = false;
			return;
        }
        var expanded = data.getAttr("expanded");
    	if(!expanded)
		{
			self.zPortListView.getMsgChildrenByParentCuid(fiberCuid,self.zDataModel);
		}
		fiberselect = fiberselect+fiberCuid+",";
	},selectFibers);
	if(!checked)
		return;
	fiberselect = fiberselect.substring(0,fiberselect.length-1);
	
	self.aPortListView.lock();
	self.zPortListView.lock();
	var items = self.buttonToolbar.getItems();
	if(items){
		for(var i=0;i<items.length;i++){
			var item = items[i];
			var label = item.label;
			if(label != "确定" && label != "取消"){
				item.disabled = true;
			}else{
				item.disabled = false;
			}
			item.selected = false;
		}
	}
	
	var fibers = fiberselect.split(",");
	var ports = portselect.split(",");
	
	for(var i=0;i<fibers.length;i++){
		var fiberCuid = fibers[i];
		var portCuid = ports[i];
		var hfiber = self.zDataModel.getDataById(fiberCuid);
		var hfiberName = hfiber.getName();
		var pt = self.aDataModel.getDataById(portCuid);
		var ptName = pt.getName();
		var ptBm = pt.getAttr("BM_CLASS_ID");
		
		var htdata = new ht.Node(portCuid);
		htdata.setId(portCuid);
		htdata.setAttr("BM_CLASS_ID",ptBm);
		htdata.setAttr("cuid",portCuid);
		htdata.setAttr("newnode","newnode");
		htdata.setName(ptName);
		htdata.setIcon("/resources/topo/alarm/c.png");
		
		self.zDataModel.add(htdata);
		if(hfiber){
			hfiber.addChild(htdata);
			//加上下面注释掉的这句后，右侧不能显示node,原因是：树上的元素只能有一个父类
			//self.zCancelData.addChild(hfiber);
			
			self.fiberCancelData.push(hfiber);
		}
		self.zPortListView.treeView.expand(hfiber);
	}
},
	doConfim : function(){
	 	var self = this;
	 	try{
      	var selectPorts = self.aDataModel.sm();
		var selectFibers = self.zDataModel.sm();
		var portselect = "";
		var items = self.buttonToolbar.getItems();
		if(items){
			for(var i=0;i<items.length;i++){
				var item = items[i];
				var label = item.label;
				if(label != "确定" && label != "取消"){
					item.disabled = false;
				}else{
					item.disabled = true;
				}
				item.selected = false;
			}
		}
		if(self.action_type=="add"){
			var portCuid="";
			selectPorts.each(function(data){
				 portCuid = data.getAttr("cuid");
				portselect = portselect+portCuid+",";
			},selectPorts);
			portselect = portselect.substring(0,portselect.length-1);
			
			var fiberselect = "";
			var fiberCuid="";
			selectFibers.each(function(data){
				 fiberCuid = data.getAttr("cuid");
				fiberselect = fiberselect+fiberCuid+",";
			},selectFibers);
			fiberselect = fiberselect.substring(0,fiberselect.length-1);
			InterFiberLinkAction.queryFiberLink(portCuid,fiberCuid, function(data){
				if(data)
					{
					 for(var a=0;a<data.length;a++){
							var da=data[a];
							if(da.ERROR){
								Ext.Msg.alert("error",'错误提示信息:'+da.ERROR);  
								self.doCancel();
								return;
							}}
						var array = data;
						for(var i=0;i<array.length;i++){
							var arr = array[i];
							var parentCuid = arr.CUID;
							var bmClassId = arr.BM_CLASS_ID;
							var afiberdata = self.zDataModel.getDataById(portCuid+"_"+fiberCuid);
								//新增加一次后，继续关联第二个端子（同样的纤芯）时，如果点击取消，需要在此清掉原来那个newnode属性
							if(afiberdata){
								afiberdata.setAttr("cuid",arr.CUID);
								
								afiberdata.setAttr("BM_CLASS_ID",arr.BM_CLASS_ID);
								
								afiberdata.setAttr("ORIG_POINT_CUID",arr.ORIG_POINT_CUID);
								afiberdata.setAttr("DEST_POINT_CUID",arr.DEST_POINT_CUID);
								afiberdata.setIcon(arr.ICON);
								self.zPortListView.treeView.expand(fiberselect);
								
								}
							}
						}	
						
				});
			}if(self.action_type=="delete"){
				var portselect ="port-";
				var fiberselect ="";
				var checked = true;
				//断开时后面需要加上选择端子断开的功能
				var portCuid="";
				selectPorts.each(function(data){
					 portCuid = data.getAttr("cuid");
					portselect = portselect+portCuid+",";
				},selectPorts);
				portselect = portselect.substring(0,portselect.length-1);
				    var fiber="";
				selectFibers.each(function(data){
					 fiber = data.getAttr("cuid");
					var portBm = data.getAttr("BM_CLASS_ID");
					//此处根据右侧选择纤芯判断
					if(!(portBm=="FIBER")){
						//做关联时端子列表必须选择端子
						checked = false;
						tp.utils.optionDialog("错误提示信息","断开时右侧纤芯列表中需要选择的类型必须是纤芯！"); 
						return;
					}
					fiberselect = fiberselect+fiber+",";
				},selectFibers);
				if(!checked)
					return;
				
				fiberselect = fiberselect.substring(0,fiberselect.length-1);
				InterFiberLinkAction.deleteFiberLink(portCuid,fiber,function(data){
				if(data)
						{
							if(data.error){
								alert('错误提示信息:'+data.error);
								self.doCancel();
								return;
							}
							var array = data;
							var azFiberPort = self.aDataModel.getDataById(portCuid);
							var zFiberPort = self.zDataModel.getDataById(portCuid);
							var fiberFiber = self.zDataModel.getDataById(fiber);
							if(fiberFiber&&zFiberPort&&azFiberPort){
	                               var fibers=fiberFiber.getChildren()._as;

	                                for(var i=0;i<fibers.length;i++){

	                                		var zfiber = fibers[i];
	                                		if(zFiberPort._id==zfiber._id){
	                                			fiberFiber.removeChild(zFiberPort);
                                      }
                                      else{
                                      	tp.utils.optionDialog("错误提示信息","请选择对应的纤芯进行断开操作！"); 
                                      	return;
                                          }

	                                }

								}
						/*	if(azFiberPort)
								self.aDataModel.remove(azFiberPort);*/
							/*if(fiberFiber)
								self.zDataModel.remove(azFiberPort);
								fiberFiber.removeChild(zFiberPort);*/
							self.zPortListView.treeView.expand(fiberselect);
					}
					
				});
			}
	      	
		 	}catch(err){
		 		
		 	}finally{
		 		self.aPortListView.unlock();
				self.zPortListView.unlock();
				var items = self.buttonToolbar.getItems();
				if(items){
					for(var i=0;i<items.length;i++){
						var item = items[i];
						var label = item.label;
						if(label != "确定" && label != "取消"){
							item.disabled = false;
						}else{
							item.disabled = true;
						}
						item.selected = false;
					}
				}
		 	}
	     },	
	     onDataDoubleClicked: function(data,model) {
	    	 var self = this;
	 		var id = data.getId();
	 		if (!data || !id) {
	 			return false;
	 		}
	 		var nodeType = (id + "").split("-")[0];
	 		
	 		if ("ODFPORT" == nodeType || "FIBER_DP_PORT" == nodeType) {
	 			self.loadPortsByFiber(id,model);
	 		}
	 		else
	 		{
	 			return;
	 		}
	 	},
	 	loadPortsByFiber : function(data,model)
		{
			var self = this;
			self.aDataModel.sm().clearSelection();
			self.zDataModel.sm().clearSelection();
			var acuid = "";
			var zcuid = "";
			if(model == "a")
			{
				acuid = data;
				zcuid = data.split(";")[0];
			}
			else
			{
				acuid = data.split(";")[1] +";"+ data.split(";")[0];
				zcuid = data;
			}
			var anode = self.aDataModel.getDataById(acuid);
			var znode = self.zDataModel.getDataById(zcuid);
			self.aDataModel.sm().appendSelection(anode);
			self.zDataModel.sm().appendSelection(znode);
		},
});


Frame.op.aPortListView = function(n,dm){
	var self = this;
	self.name = n;
	self.dataModel = dm;
	Frame.op.aPortListView.superClass.constructor.apply(this);
	self.treeView = new ht.widget.TreeView(self.dataModel);

    self.add(self.name,self.treeView,true); 
	self.setTabBackground("#6FB1DD");
	self.setSelectBackground("rgba(0,0,0,0)");

};
ht.Default.def('Frame.op.aPortListView', ht.widget.TabView, {
	name:null,
	dataModel:null,
	treeView:null,
	mask:null,
	lock : function(){
		this.mask = document.createElement("div");
	    this.mask.className = 'mask';
	    this.mask.innerHTML = "<div></div>";
	    this.getView().appendChild(this.mask);
	},
	unlock : function(){
		if(this.mask)
		{
			this.getView().removeChild(this.mask);
			this.mask = null;
		}
	},
	getChildrenByParentCuid : function(parentCuid){
		var scope = this;
		InterFiberLinkAction.getChildrenByParentCuid(parentCuid,function(data){
			
			var parent = scope.dataModel.getDataById(parentCuid);
			
			if(data && data.length > 0)
			{
//				console.info(JSON.stringify(data));
				for (var i = 0; i < data.length; i++) {
					var child = data[i];
					var icon = child.ICON;
					var bmClassId = child.BM_CLASS_ID;
					var htdata = scope.dataModel.getDataById(child.CUID);
					if(htdata == null)
					{	
						htdata = new ht.Node(child.CUID);
						htdata.setId(child.CUID);
						htdata.setAttr("cuid",child.CUID);
						if(parentCuid.indexOf("FIBER-")>-1)
						{
							var name = child.PORTNAME;
							htdata.setName(name);
						}else
						{
							htdata.setName(child.LABEL_CN);
						}
						if(icon)
							htdata.setIcon(icon);
						if(bmClassId)
							htdata.setAttr("BM_CLASS_ID",bmClassId);
						if(parent)
							parent.addChild(htdata);
						htdata.setAttr("ORIG_POINT_CUID",htdata.ORIG_POINT_CUID);
						htdata.setAttr("DEST_POINT_CUID",htdata.DEST_POINT_CUID);
						
						scope.dataModel.add(htdata);
						
						scope.getChildrenByParentCuid(child.CUID);
					}
				}
				if(parent)
					parent.setAttr("expanded",false);
//				scope.treeView.expandAll();
			}
		});
	}
});
//设备内端子列表
Frame.op.zPortListView = function(n,dm){
	var self = this;
	self.name = n;
	self.dataModel = dm;
	Frame.op.zPortListView.superClass.constructor.apply(this);
	self.treeView = new ht.widget.TreeView(self.dataModel);
	
    self.add(self.name,self.treeView,true);
	self.setTabBackground("#6FB1DD");
	self.setSelectBackground("rgba(0,0,0,0)");
	
//    self.treeView.onDataDoubleClicked = function (data) {
//    	var expanded = data.getAttr("expanded");
//    	var dataid = data.getId();
//    	self.getChildrenByParentCuid(dataid);
//    };
	
	
//	window.addEventListener('resize', function (e) {
//		self.invalidate();
//    }, false);
};
ht.Default.def('Frame.op.zPortListView', ht.widget.TabView, {
	name:null,
	dataModel:null,
	treeView:null,
	mask:null,
	lock : function(){
		this.mask = document.createElement("div");
	    this.mask.className = 'mask';
	    this.mask.innerHTML = "<div></div>";
	    this.getView().appendChild(this.mask);
	},
	unlock : function(){
		if(this.mask)
		{
			this.getView().removeChild(this.mask);
			this.mask = null;
		}
	},
	getMsgChildrenByParentCuid : function(parentCuid){
		var scope = this;
		InterFiberLinkAction.getMsgChildrenByParentCuid(parentCuid,function(data){
			
			var parent = scope.dataModel.getDataById(parentCuid);
			
			if(data && data.length > 0)
			{
//				console.info(JSON.stringify(data));
				for (var i = 0; i < data.length; i++) {
					var child = data[i];
					var icon = child.ICON;
					var bmClassId = child.BM_CLASS_ID;
					var htdata = scope.dataModel.getDataById(child.CUID);
					if(htdata == null)
					{	
						htdata = new ht.Node(child.CUID);
						htdata.setId(child.CUID);
						htdata.setAttr("cuid",child.CUID);
						if(parentCuid.indexOf("FIBER-")>-1)
						{
							var name = child.LABEL_CN;
						
							htdata.setName(name);
						}else
						{
							htdata.setName(child.LABEL_CN);
						}
						if(icon)
							htdata.setIcon(icon);
						if(bmClassId)
							htdata.setAttr("BM_CLASS_ID",bmClassId);
						if(parent)
							parent.addChild(htdata);
						htdata.setAttr("ORIG_POINT_CUID",htdata.ORIG_POINT_CUID);
						htdata.setAttr("DEST_POINT_CUID",htdata.DEST_POINT_CUID);
						
						scope.dataModel.add(htdata);
						
						scope.getMsgChildrenByParentCuid(child.CUID);
					}
				}
				if(parent)
					parent.setAttr("expanded",false);
//				scope.treeView.expandAll();
			}
		});
	}
});

