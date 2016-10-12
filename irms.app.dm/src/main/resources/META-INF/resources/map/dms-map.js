//初始化地图，加入底图和动态图层
Dms.initMap = (function(config, calllback) {

    return function(config, calllback) {
        var tpmap = Dms.Default.tpmap = new tp.widget.TPMap(config, calllback);
        var view = tpmap.getView();
        document.body.appendChild(view);
        
        var graphView = tpmap.getGraphView();
        
        if(!dms.splitContextmenu){
        	 var contextmenu = dms.splitContextmenu = new ht.widget.ContextMenu();
             contextmenu.addTo(graphView.getView());
        }
        
    	var selectMenu={
    			'SITE' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.SITE_RIGHT_MENU);
    			},
    			'ACCESSPOINT' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.ACCESSPOINT_RIGHT_MENU);
    			},
    			'FIBER_JOINT_BOX':function(){
    				tpmap.buildContextMenu(Dms.Default.contextMenu.FIBER_JOINT_BOX_RIGHT_MENU);
    			},
    			'FIBER_DP' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.FIBER_DP_RIGHT_MENU);
    			},
    			'FIBER_CAB' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.FIBER_CAB_RIGHT_MENU);
    			},
    			'PON' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.PON_RIGHT_MENU);
    			},
    			'MANHLE' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.MANHLE_RIGHT_MENU);
    			},
    			'POLE' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.POLE_RIGHT_MENU);
    			},
    			'STONE' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.STONE_RIGHT_MENU);
    			},
    			'INFLEXION' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.INFLEXION_RIGHT_MENU);
    			},
    			'WIRE_SEG' : function() {
    				//tpmap.buildContextMenu(Dms.Default.contextMenu.SELECT_WIRE_SEG_POINT);
    				tpmap.buildContextMenu(Dms.Default.contextMenu.WIRE_SEG_RIGHT_MENU);
    			},
    			'DUCT_SEG' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.DUCT_SEG_RIGHT_MENU);
    			},
    			'POLEWAY_SEG' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.POLE_SEG_RIGHT_MENU);
    			},
    			'STONEWAY_SEG' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.STONE_SEG_RIGHT_MENU);
    			},
    			'UP_LINE_SEG' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.UPLINE_SEG_RIGHT_MENU);
    			},
    			'HANG_WALL_SEG' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.HANGWALL_SEG_RIGHT_MENU);
    			},
    			'GRID' : function(){
    				tpmap.buildContextMenu(Dms.Default.contextMenu.GRID);
    			},
    			'FIBER_TERMINAL_BOX':function(){
    				tpmap.buildContextMenu(Dms.Default.contextMenu.FIBER_TERMINAL_BOX_RIGHT_MENU);
    			}
    	};
    	//系统右键菜单
    	var selectSystemMenu={
    			'WIRE_SYSTEM' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.WIRE_SYSTEM_RIGHT_MENU);
    			},
    			'DUCT_SYSTEM' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.DUCT_SYSTEM_RIGHT_MENU);
    			},
    			'POLEWAY_SYSTEM' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.POLE_SYSTEM_RIGHT_MENU);
    			},
    			'STONEWAY_SYSTEM' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.STONE_SYSTEM_RIGHT_MENU);
    			},
    			'UP_LINE' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.UPLINE_SYSTEM_RIGHT_MENU);
    			},
    			'HANG_WALL' : function() {
    				tpmap.buildContextMenu(Dms.Default.contextMenu.HANGWALL_SYSTEM_RIGHT_MENU);
    			}
    	};
    	//分支右键菜单
    	var selectBranchMenu={
			'DUCT_BRANCH' : function() {
				tpmap.buildContextMenu(Dms.Default.contextMenu.DUCT_BRANCH_RIGHT_MENU);
			},
			'POLEWAY_BRANCH' : function() {
				tpmap.buildContextMenu(Dms.Default.contextMenu.POLE_BRANCH_RIGHT_MENU);
			},
			'STONEWAY_BRANCH' : function() {
				tpmap.buildContextMenu(Dms.Default.contextMenu.STONE_BRANCH_RIGHT_MENU);
			}
    	};
    	var laySelectMenu={
			'WIRE_SEG' : function() {
				tpmap.buildContextMenu(Dms.Default.contextMenu.LAY_SELECT_RIGHT_MENU);
			}
    	};
    	var ductLineGlaylaySelectMenu = {
    		'WIRE_SEG' : function() {
        		tpmap.buildContextMenu(Dms.Default.contextMenu.DUCT_LINE_GLAY_LAY_SELECT_RIGHT_MENU);
        	}	
    	};
    	var ductLineLaySelectMenu = {
    		'WIRE_SEG' : function() {
    			tpmap.buildContextMenu(Dms.Default.contextMenu.DUCT_LINE_LAY_SELECT_RIGHT_MENU);
    		}
        };
    	
    	var grayLaySelectMenu={
			'WIRE_SEG' : function() {
				tpmap.buildContextMenu(Dms.Default.contextMenu.GLAY_WIRE_RIGHT_MENU);
			}
    	};
    	var rightClickData={
    		'2':	function(e) {//tp.Default.DrawObject._drawState == 2 //新增点时
    			tpmap.buildContextMenu(Dms.Default.contextMenu.ADD_POINT);
    		},
    		'3':	function(e) {//新增线时
                if (e.data instanceof ht.Node) //选择点
                	tpmap.buildContextMenu(Dms.Default.contextMenu.POINT_CONTEXTMENU, e.data);
                if (e.data instanceof ht.Edge) //选择线
                	tpmap.buildContextMenu(Dms.Default.contextMenu.LINE_CONTEXTMENU, e.data, e.event.x, e.event.y);
    		},
    		'6': function(e){//管道段拆分
    			tpmap.buildContextMenu(dms.Default.contextMenu.SPLIT_SEG_CONTEXTMENU);
    		},

    		'7': function(e){//光缆改迁
                if (dms.splitContextmenu){
                	dms.splitContextmenu.setItems(null);
                }
    			tpmap.buildContextMenu(dms.Default.contextMenu.MOVE_WIREROUTE_RIGHT_MENU);
    		},
    		
    		'8': function(e){//承载段合并
    			tpmap.buildContextMenu(dms.Default.contextMenu.MERGE_SEG_CONTEXTMENU);
    		},
    		
    		'9': function(e){//承载分支合并
    			tpmap.buildContextMenu(dms.Default.contextMenu.MERGE_BRANCH_CONTEXTMENU);
    		},
    		
    		'101':	function(e) {//点选段 右键菜单
    			var cuid=e.data.getId(),
        	        className = cuid.split('-')[0];
        	    
        	    var contextObject = tp.Default.OperateObject.contextObject;
    			if(contextObject == null){
    				return;
    			}
    			var cuid1 = contextObject.cuid;
            	if (cuid == null){
            		return;
            	}
        	    
	        	if (cuid == cuid1){
	        		var kind = tp.Default.DrawObject._kind;
	        		if(className == "FIBER_JOINT_BOX"){
	        			if(kind && kind == '2'){
		        			className = "FIBER_TERMINAL_BOX";//光终端盒
		        		}
	        		}
	        		selectMenu[className](cuid);
	        	}
    		},
    		'102':	function(e) {//点选系统 右键菜单
    			var contextObject = tp.Default.OperateObject.contextObject;
    			if(contextObject == null){
    				return;
    			}
    			var cuid = contextObject.cuid;
            	if (cuid == null){
            		return;
            	}
     	        var	className = cuid.split('-')[0];
    			selectSystemMenu[className](cuid);
    		},
    		'103':	function(e) {//点选分支 右键菜单
    			var contextObject = tp.Default.OperateObject.contextObject;
    			if(contextObject == null){
    				return;
    			}
    			var cuid = contextObject.cuid;
            	if (cuid == null){
            		return;
            	}
	     	    var className = cuid.split('-')[0];
	        	selectBranchMenu[className]();
    		},
    		'201':	function(e) {//图形化自动敷设时选择要敷设的承载段用
    			laySelectMenu["WIRE_SEG"]();
    		},
    		'202':function(){//图形化敷设时“选择”分支用
    			grayLaySelectMenu["WIRE_SEG"]();
    		},
    		'203' : function(e){
    			ductLineGlaylaySelectMenu["WIRE_SEG"]();
    		},
    		'204' : function(e){
    			ductLineLaySelectMenu["WIRE_SEG"]();
    		}
    	};
    	
    	var rightClickBackground={
    			'1': function(){
    				tpmap.buildContextMenu(Dms.Default.contextMenu.POINT_RIGHTCLICK_MENU);
    			},
        		'2':	function() {//tp.Default.DrawObject._drawState == 2 //新增点时
        			tpmap.buildContextMenu(Dms.Default.contextMenu.ADD_POINT);
        		},
        		'3':	function() {//新增线时
        			tpmap.buildContextMenu(Dms.Default.contextMenu.ADD_LINE);
        		},
        		'6':	function() {//承载段拆分
        			tpmap.buildContextMenu(dms.Default.contextMenu.SPLIT_SEG_CONTEXTMENU);
        		},

        		'7': function(e){//光缆改迁
        			 if (dms.splitContextmenu){
        				 //此处hide()方法不灵
//                     	contextmenu.hide();
        				 dms.splitContextmenu.setItems(null);
                     }
        			tpmap.buildContextMenu(dms.Default.contextMenu.MOVE_WIREROUTE_RIGHT_MENU);
        		},
        		
        		
        		'8': function(e){//承载段合并
        			tpmap.buildContextMenu(dms.Default.contextMenu.MERGE_SEG_CONTEXTMENU);
        		},
        		
        		'9': function(e){//承载分支合并
        			tpmap.buildContextMenu(dms.Default.contextMenu.MERGE_BRANCH_CONTEXTMENU);
        		},
        		
        		'101':	function() {//点选段 右键菜单
        			var contextObject = tp.Default.OperateObject.contextObject;
        			if(contextObject == null){
        				return;
        			}
        			var cuid = contextObject.cuid;
                	if (cuid == null){
                		return;
                	}
                	var className = cuid.split('-')[0];
                	var kind = tp.Default.DrawObject._kind;
	        		if(className == "FIBER_JOINT_BOX"){
	        			if(kind && kind == '2'){
		        			className = "FIBER_TERMINAL_BOX";//光终端盒
		        		}
	        		}
                	selectMenu[className](cuid);
        		},
        		'102':	function() {//点选系统 右键菜单
        			var contextObject = tp.Default.OperateObject.contextObject;
        			if(contextObject == null){
        				return;
        			}
        			var cuid = contextObject.cuid;
                	if (cuid == null){
                		return;
                	}
	            	var className = cuid.split('-')[0];
	            	selectSystemMenu[className](cuid);
        		},
        		'103':	function() {//点选分支 右键菜单
        			var contextObject = tp.Default.OperateObject.contextObject;
        			if(contextObject == null){
        				return;
        			}
        			var cuid = contextObject.cuid;
                	if (cuid == null){
                		return;
                	}
    	     	    var className = cuid.split('-')[0];
    	        	selectBranchMenu[className]();
        		},
        		'201':	function() {//图形化自动敷设时选择要敷设的承载段用
        			laySelectMenu["WIRE_SEG"]();
        		},
        		'202':function(){//图形化敷设时“选择”分支用
        			grayLaySelectMenu["WIRE_SEG"]();
        		},
        		'203' : function(e){
        			ductLineGlaylaySelectMenu["WIRE_SEG"]();
        		},
        		'204' : function(e){
        			ductLineLaySelectMenu["WIRE_SEG"]();
        		}
        	};
    	
        graphView.mi(function(e) {
            //监控在节点上右键
            if (e.kind === 'clickData' && e.event.button === 2) {
                  rightClickData[tp.Default.DrawObject._drawState](e);
            };
            //监控空白处右键
            if (e.kind === 'clickBackground' && e.event.button === 2) {
//            	var locate = tp.Default.DrawObject._drawState == 101 || tp.Default.DrawObject._drawState == 102 || tp.Default.DrawObject._drawState == 103;
            	if (rightClickBackground[tp.Default.DrawObject._drawState]!=null){
            		rightClickBackground[tp.Default.DrawObject._drawState]();
            	}else{
            		tpmap.buildContextMenu([]);
                	tpmap.reset();
            	}
                 
            }
            if (e.kind === 'endEditPoint') {
                console.log('结束编辑多边形或者线的编辑');
                graphView.dm().each(function(data) {
                    if (data instanceof ht.Shape) {
                        //if(tp.Default.DrawObject._movePointState === 5){
                    	tpmap.refreshPolygon(data);
                        //}
                    } else if (data instanceof ht.Node) {
                        var position = data.getPosition(),
                            x = position.x + graphView.tx(),
                            y = position.y + graphView.ty(),
                            latLng = tpmap.containerPointToLatLng(new L.Point(x, y));
                        data.a('latLng', latLng);

                        //增加一个线的距离计算方法
                        tpmap.toDistance(data._id);
                        if (tp.Default.DrawObject._movePointState === 1) {
                            var exists = false;
                            for (var i = 0; i < tp.Default.DrawObject._movePointList.length; i++) {
                                var item = tp.Default.DrawObject._movePointList[i];
                                if (item.getId() === data.getId())
                                    exists = true;
                            }
                            if (!exists)
                                tp.Default.DrawObject._movePointList.push(data);
                        }
                    }
                });

            }
            if(e.kind === 'betweenMove')
            {
            	var ld = graphView.dm().sm().ld();
            	if(ld && ld instanceof ht.Node)
            	{
            		var position = ld.getPosition(),
            		x = position.x + graphView.tx(),
            		y = position.y + graphView.ty(),
            		map = tpmap.getMap(),
            		latLng = map.containerPointToLatLng(new L.Point(x, y));
            		ld.a('latLng', latLng);
            		
            		//增加一个线的距离计算方法
            		tpmap.toDistance(ld.getId());
            		var edges = ld.getEdges();
            		if(edges){
            			edges.each(function(edge){
                			var targetNode = edge.getTarget(),
                			sourceNode = edge.getSource();
                			if (targetNode && sourceNode) {
                				//起点经纬度
                				var startLatlng = sourceNode.getAttr('latLng');
                				//止点经纬度
                				var endLatlng = targetNode.getAttr('latLng');
                				var distance = startLatlng.distanceTo(endLatlng);
                				distance = L.Util.formatNum(distance, 2);
                				edge.a("LENGTH",distance+"");
                				edge.setName(distance + "米");
                			}
                		});
            		}
            	}	
            }
            if (e.kind === 'endMove') { //双击完移动点计算距离
            	var data = graphView.dm().sm().ld();
            	if(data && data instanceof ht.Node)
            	{
                    var position = data.getPosition(),
                        x = position.x + graphView.tx(),
                        y = position.y + graphView.ty(),
                        map = tpmap.getMap(),
                        latLng = map.containerPointToLatLng(new L.Point(x, y));
                    data.a('latLng', latLng);

                    //增加一个线的距离计算方法
                    tpmap.toDistance(data.getId());
                    var edges = data.getEdges();
                    if(edges){
                    	edges.each(function(edge){
                       	 var targetNode = edge.getTarget(),
                            	sourceNode = edge.getSource();
                       	 if (targetNode && sourceNode) {
                       		//起点经纬度
                                var startLatlng = sourceNode.getAttr('latLng');
                                //止点经纬度
                                var endLatlng = targetNode.getAttr('latLng');
                                var distance = startLatlng.distanceTo(endLatlng);
                                distance = L.Util.formatNum(distance, 2);
                                edge.a("LENGTH",distance+"");
                                edge.setName(distance + "米");
                       	 }
                       });
                    }
                    
                    if (tp.Default.DrawObject._movePointState === 1) {
                        var exists = false;
                        for (var i = 0; i < tp.Default.DrawObject._movePointList.length; i++) {
                            var item = tp.Default.DrawObject._movePointList[i];
                            if (item.getId() === data.getId())
                                exists = true;
                        }
                        if (!exists)
                            tp.Default.DrawObject._movePointList.push(data);
                    }
                }
            }

            if (e.kind === 'endEditRect') {
                console.log('结束编辑图元大小和位置');
                graphView.dm().each(function(data) {
                    if (data instanceof ht.Shape) {
                    	tpmap.refreshPolygon(data);
                        //tp.Default.DrawObject._drawPloygonList.push(data);
                    }
                });

            }
        });
        
        return tpmap;
    };
})();