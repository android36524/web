
$importjs(ctx+'/dwr/interface/SiteConncetionQueryAction.js');
tp.utils.createSiteConnectionQueryConPanel = function(treeTableView){
    	var basicFormPane = new ht.widget.FormPane();
    	var queryNode = new ht.Node();
    	
    	var queryBtn = tp.utils.createButton('','查询');
    	queryBtn.onclick = function(e){
    		var queryParam={'ORIG_DEVICE':origDevice.getResult().id,
    		                'DEST_DEVICE':destDevice.getResult().id,
    		                'ORIG_DEVICE_LABELCN':origDevice.getResult().value,
    		                'DEST_DEVICE_LABELCN':destDevice.getResult().value,
                            'POINT_COUNT':transitionPointCountCombo.getValue(),
                            'INCLUDE_POINT':includePoint.getResult().id,
                            'EXCLUDE_POINT':excludePoint.getResult().id};
    		if(origDevice
    				&&origDevice.getResult()
    				&&origDevice.getResult().id
    				&&origDevice.getResult().id.split(',').length>1){
    			Ext.Msg.alert('温馨提示','起点设备不支持多选');
    			return;
    		}
    		if(!origDevice
    				||!origDevice.getResult()
    				||!origDevice.getResult().id){
    			Ext.Msg.alert('温馨提示','起点设备不能为空');
    			return;
    		}
    		if(destDevice
    				&&destDevice.getResult()
    				&&destDevice.getResult().id
    				&&destDevice.getResult().id.split(',').length>1){
    			Ext.Msg.alert('温馨提示','终点设备不支持多选');
    			return;
    		}
    		if(!destDevice
    				||!destDevice.getResult()
    				||!destDevice.getResult().id){
    			Ext.Msg.alert('温馨提示','终点设备不能为空');
    			return;
    		}
    		tp.utils.doQuery(treeTableView,queryParam);  		
		};        
    	var clearBtn = tp.utils.createButton('','清空');
    	clearBtn.onclick = function(e){// 此处有问题，清除当前值后，已经选择过的值还存在param中。
    		origDevice.cleanAction();
    		destDevice.cleanAction();
    		includePoint.cleanAction();
    		excludePoint.cleanAction();
			fiberStateCombo.setValue(-1);
			transitionPointCountCombo.setValue(0);
		}; 

        var origDevice = tp.utils.selectDeviceByTypeComp();
        var destDevice = tp.utils.selectDeviceByTypeComp();
        var includePoint = tp.utils.selectDeviceByTypeComp();
        var excludePoint = tp.utils.selectDeviceByTypeComp();

		var transitionPointCount = {values:[0,1,2,3,4,5],labels:['0','1','2','3','4','5']};
		var transitionPointCountCombo = new tp.utils.createComboBox(transitionPointCount);
		transitionPointCountCombo.setValue(0);        

		basicFormPane.addRow([{element:'起点设备：',align:'left'},{element:origDevice,align:'left'},
		                      {element:'转接点个数：',align:'left'},transitionPointCountCombo,null,queryBtn],
		                     [0.1,0.2,0.1,0.2,0.2,80]);
		basicFormPane.addRow([{element:'终点设备：',align:'left'},{element:destDevice,align:'left'},null,null,null,clearBtn],
				             [0.1,0.2,0.1,0.2,0.2,80]);
		
		transitionPointCountCombo.onValueChanged = function() {
			if(!transitionPointCountCombo||transitionPointCountCombo._value==0){
				basicFormPane.clear();
				basicFormPane.addRow([{element:'起点设备：',align:'left'},{element:origDevice,align:'left'},
				                      {element:'转接点个数：',align:'left'},transitionPointCountCombo,null,queryBtn],
				                     [0.1,0.2,0.1,0.2,0.2,80]);
				basicFormPane.addRow([{element:'终点设备：',align:'left'},{element:destDevice,align:'left'},null,null,null,clearBtn],
						             [0.1,0.2,0.1,0.2,0.2,80]);
				return;
			}
			basicFormPane.clear();
		    basicFormPane.addRow([ {element : '起点设备：', align : 'left'}, {element:origDevice,align:'left'}, 
		                           {element : '转接点个数：',align : 'left'}, transitionPointCountCombo, 
		                           {element : '不能经过的转接点：', align : 'left'}, {element:excludePoint,align:'left'},queryBtn  ],
		                           [0.1,0.2,0.15,0.2,0.15,0.2,80]);
		    basicFormPane.addRow([ {element : '终点设备：',align : 'left'}, {element:destDevice,align:'left'},
		                           {element : '必须经过的转接点：',align : 'left'}, {element:includePoint,align:'left'},null,clearBtn], 
		                           [0.1,0.2,0.15,0.2,0.35,80]);
	    };
	    

	    
	    basicFormPane._view.onclick=function(e){
	    	if(e
	    		&&e.target
	    		&&e.target.parentNode
	    		&&e.target.parentNode.id
	    		&&e.target.parentNode.id=='subQueryButton'
	    		&&e.target.outerHTML
	    		&&e.target.outerHTML.indexOf('/resources/tool/zoom.png')>0){
	    		return;
	    	}else{
		        origDevice.hide();
		        destDevice.hide();
		        includePoint.hide();
		        excludePoint.hide();
	    	}
	        
	    };
	    
        return basicFormPane;
    };
    
tp.utils.createSiteConnectionQueryPanel = function (){
	var dm = dms.move.selectToLineDataModel = new ht.DataModel();
	var treeTablePane = new ht.widget.TreeTablePane(dm); 

    var treeTableView = treeTablePane.getTableView();
	treeColumn = treeTableView.getTreeColumn();
	treeColumn.setDisplayName('');
	treeColumn.setWidth(260);

	treeTableView.addColumns([{name:"original",displayName:"起点",accessType:"attr"},
	                          {name:"destination",displayName:"终点",accessType:"attr"}]);
	
	//TODO 根据配置动态加载查询面板
	var basicFormPane = tp.utils.createSiteConnectionQueryConPanel(treeTableView);	
               
        var borderPane = new ht.widget.BorderPane(); 
        borderPane.setTopView(basicFormPane);
        borderPane.setCenterView(treeTablePane);
        borderPane.setTopHeight(80);
        borderPane.setBottomHeight(40);
        borderPane.getResult = function(){};
        
        return 	borderPane;
    };

tp.utils.doQuery= function (treeTableView,queryParam){
	SiteConncetionQueryAction.getResourceScheme(queryParam,function(data){
    		if(!data || data.length <1){
    			treeTableView.dm().clear();
            	treeTableView.redraw();
    			Ext.Msg.alert('温馨提示','没有满足条件的数据');
    			return;
    		}

			for (var i = 0; i < data.length; i++) {
				var child = data[i];
				var parentID = child['ID'];
				var parentLabelCN = child['LABEL_CN'];
				var routeList = child['ROUTE_LIST'];
				var parent = treeTableView.dm().getDataById(parentID);
				if(!parent){
					parentLabelCN = "方案"+(i+1)+": "+parentLabelCN;
					parent = new ht.Node(parentLabelCN);
					parent.setId(parentID);
					parent.setName(parentLabelCN);
					parent.a('supportDoubleClick',false);
					treeTableView.dm().add(parent);
				}
				for(var j=0;j<routeList.length;j++){
					var routeInfo = routeList[j];
					var routeID = routeInfo["ID"]; 
					var routeLabelCN = routeInfo["LABEL_CN"]; 
					var routeNode =  treeTableView.dm().getDataById(routeID);
					if(!routeNode){
						routeNode = new ht.Node(routeLabelCN);
						routeNode.setId(routeID);
						routeNode.setName(routeLabelCN);
						routeNode.a('supportDoubleClick',true);
						parent.addChild(routeNode);
						treeTableView.dm().add(routeNode);
					}
				}
				parent.setAttr("expanded",true);
			};
			
			var roots = treeTableView.getRootData();
			console.log(roots);
	        treeTableView.setRootVisible(true);
	        treeTableView.expandAll();
	        
	        treeTableView.getView().addEventListener('dblclick', function(e){
                var data = treeTableView.getDataAt(e);
                if(data&&data.a('supportDoubleClick')){
                	SiteConncetionQueryAction.getWireSegs(data._id,data._name,function(optials){
        				for(var k=0;k<optials.length;k++){
        					var optical = optials[k];
        					var opticalID = optical["ID"]; 
        					var opticalNode = treeTableView.dm().getDataById(opticalID);
        					if(!opticalNode){
            					var opticalLabelCN = optical["LABEL_CN"]; 
            					var original = optical["ORIG_SITE"];
            					var destination = optical["DEST_SITE"];
            					var level = optical["FIBER_LEVEL"];
            					var fiberRoute = optical["ROUTE_DESCRIPTION"];
            					opticalNode = new ht.Node(opticalID);
            					opticalNode.setId(opticalID);
            					opticalNode.setName(opticalLabelCN);
            					
            					opticalNode.setAttr("original",original);
            					opticalNode.setAttr("fiberRoute",fiberRoute);
            					opticalNode.setAttr("level",level);
            					opticalNode.setAttr("destination",destination);
            					opticalNode.a('supportDoubleClick',false);
            					data.addChild(opticalNode);
            					treeTableView.dm().add(opticalNode);
        					}
        				}
                	});
                	treeTableView.expand(data);
                	treeTableView.redraw();
                }
            });
		});

	};	
	
