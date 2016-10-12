$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
$importjs(ctx+'/dwr/interface/SplitMapWireSegAction.js');
$importjs(ctx+'/map/uuid.js');
dms.split.splitTools = {
	splitseg : function(type,isDesigner){
		var tpmap = Dms.Default.tpmap,
      	graphView = tpmap.getGraphView();
      	graphView.dm().clear();
      	dms.split.splitTools.isDesigner = isDesigner;
      	var cuid = tp.Default.OperateObject.contextObject.cuid;
     	if(isDesigner){
			var isFlag = dms.Tools.getIsTnmsDatas(cuid);
            if(!isFlag){
           	 tp.utils.optionDialog("错误提示", '非设计数据，不能拆分！');
           	 return ;
            } 
		}
     	var isflag = Dms.Tools.getIsOperate(isDesigner, cuid);
		if(isflag){
			dms.split.splitTools.splitLineSeg(cuid,type,isDesigner);
		}
	},
    //拆分段
    splitLineSeg: function(cuid, pointType,isDesigner) {
    	
        var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView(),
		config = tpmap._config;
//        graphView.getView().style.cursor = 'default';
        //清空绘制的点
        tp.Default.DrawObject._drawPointList.length = 0;
        //清空绘制的线
        tp.Default.DrawObject._drawLineList.length = 0;
        //绘制的节点类型及图标
        tp.Default.DrawObject._drawPointClass = pointType;
        //绘制的线类型及图标
        tp.Default.DrawObject._drawLineClass = '';
//        graphView.enableDashFlow();
        var bClassName = "";
        if(cuid){
        	dms.split.splitTools.splitSegCuid = cuid;
        	dms.split.splitTools.bClassName = bClassName = cuid.split("-")[0];
        	tp.Default.DrawObject._drawLineClass = bClassName;
        	if(tp.Default.DrawObject._drawPointClass.length == 0){
        		tp.Default.DrawObject._drawPointClass = dms.getSplitPointTypeByResName[bClassName][0];
        	}
        }
        if(bClassName == 'WIRE_SEG'){
        	DWREngine.setAsync(false);
        	//得到具体路由点
        	SplitMapWireSegAction.getRoutePointsByWireSegCuid(cuid,function(datas){
        		if(datas){
        			for(var i=0;i<datas.length;i++){
        				var data = datas[i];
        				var node = new ht.Node();
        				node.a('CUID',data);
        				dms.split.wireSegRoutePoint.push(node);
        			}
        		}
        	});
        	DWREngine.setAsync(true);
        }
        tp.Default.DrawObject._drawState = 6;
        graphView.setEditableFunc(function(data)
		{
        	return false;
		});
//        var districtCuid = dms.Default.user.distCuid;

        if (!graphView) return;
        var map = tpmap.getMap();
        var curInterator = tp.Default.OperateObject.curInterator = new CreateVectorInteractor(graphView);
        graphView.setInteractors(new ht.List([
              new ht.graph.SelectInteractor(graphView),
              new ht.graph.EditInteractor(graphView),
              new ht.graph.MoveInteractor(graphView),
              new ht.graph.DefaultInteractor(graphView),
              curInterator
          ]));

        graphView.isMovable = function(data) {
        	 if (tp.Default.DrawObject._drawState == 6) {
        		 return false;
        	 }
        };
        //在鼠标移动时提供一个提示标注
    	var pointClassName = dms.className2ResName[pointType];
        curInterator.setSeriesTip('插入拆分点:'+pointClassName);
        graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
        if(Dms.Default.floatPane)
        	Dms.Default.floatPane.close();
        else
        	Dms.Default.floatPane =  new Dms.Panel.FloatPane();
        if(isDesigner){
        	Dms.Default.floatPane.initOperateView('插入拆分点:',true);
        }else{
        	Dms.Default.floatPane.initOperateView('插入拆分点:');
        }
        Dms.Default.floatPane.show();
        
        graphView.enableDashFlow();
        DWREngine.setAsync(false);
        
        if(dms.split.splitTools.isDesigner){
        	DuctLineDatasAction.getIsNotDesignerDatas(cuid,function(data){
        		//可能会有垃圾数据，起止点什么的无值
        		var hasProp = false;      
        		for (var prop in data){
        			hasProp = true;
        			break;
        		}
        		if(hasProp){
        			var istnms = data.ISTNMS;
            		if(istnms === "TRUE"){
            			//上面已经判断，此处无用，但是getIsNotDesignerDatas里面查询了数据
            			//此处不会进
            		}else{
            			dms.split.splitTools.setOrigAndDestPointNode(data,map,graphView);
            		}
        		}else{
        			tp.utils.optionDialog("错误提示", '有问题数据，请检查数据！');
        			dms.Default.tpmap.reset();
                  	return ;
        		}
            });
        }else{
        	GenerateCuidAction.getSplitPointDatas(cuid,function(data){
        		var hasProp = false;      
        		for (var prop in data){
        			hasProp = true;
        			break;
        		}
        		if(hasProp){
        			dms.split.splitTools.setOrigAndDestPointNode(data,map,graphView);
        		}else{
        			tp.utils.optionDialog("错误提示", '有问题数据，请检查数据！');
        			dms.Default.tpmap.reset();
                  	return ;
        		}
        	});
        }

        map.off('click', dms.split.splitTools.createClick);
        map.on('click', dms.split.splitTools.createClick);
    },
    createClick : function(e) {
        var tpmap = dms.Default.tpmap,
		graphView = tpmap.getGraphView(),
		map = tpmap.getMap();
        console.info('split line click');
        if (tp.Default.DrawObject._drawState == 6) { //点选,多个动态图层
        	var reslayers = dms.Default.tpmap.getConfig().map.reslayers;
            var times = reslayers.length;
            var selectPoints=dms.getSplitPointTypeByResName[tp.Default.DrawObject._drawLineClass];
            
            var results = [];
            //
            var reslay = dms.Tools.getLayerUrlsByType(selectPoints);
            reslay = dms.split.splitTools.getLayersId(tp.Default.DrawObject._drawLineClass);
            
            if(dms.split.splitTools.isDesigner){
            	for (var i = 0; i < reslayers.length; i++) {
                    var url = reslayers[i].url;
                    tp.utils.identify(url, {
                        geometry: e.latlng.lng + ',' + e.latlng.lat,
                    	layers : reslay
                    }, Dms.Utils.addCallbackArgs(identifyHandler,[url]));
                }
            }else{
            	times = 1;
            	 var url = reslayers[0].url;
                 tp.utils.identify(url, {
                     geometry: e.latlng.lng + ',' + e.latlng.lat,
                 	layers : reslay
                 }, Dms.Utils.addCallbackArgs(identifyHandler,[url]));
            }

            function identifyHandler(response,url) {
                times--;
                if (response.error)
                    tp.utils.optionDialog("错误", response.error.message);
                else {
                	var queryResults = response.results;
                	if(queryResults){
                		dms.designer.tools.getResInterpose(queryResults,url);
                    	results = results.concat(queryResults);
                    }
                }
                if (times == 0) {
                    var contextmenu = dms.splitContextmenu;
                    if(dms.split.splitTools.isDesigner){
                    	contextmenu = dms.designer.splitContextmenu;
                    }
                    if (contextmenu)
                        contextmenu.hide();
                    var latlng = e.latlng;
                    if (results.length == 0) { //点选左键如果空数据,需要在地图上新增加点
                    	var newNode = new ht.Node();
                    	var cArray = [];
            			cArray.push(newNode);
            			dms.split.splitTools.setNodeCuid(cArray,tp.Default.DrawObject._drawPointClass);
            			var position = map.latLngToContainerPoint(latlng);
            			newNode.a('latLng',latlng);
            			newNode.setPosition(position);
            			newNode.a('OBJECT_STATE',1);
            			var loni = L.Util.formatNum(latlng.lng, 6);
            			var lati = L.Util.formatNum(latlng.lat, 6);
            			newNode.a('LONGITUDE',loni+"");
            			newNode.a('LATITUDE',lati+"");
//            			newNode.a('RELATED_DISTRICT_NAME',dms.Default.user.distName);
            			//需要设置新增加点的名称
            			graphView.dm().add(newNode);
            			dms.split.splitTools.locateNewOnMap([newNode]);
            			if(tp.Default.DrawObject._drawLineClass === 'WIRE_SEG'){
            				var hnode = new ht.Node();
            				hnode.a('CUID',newNode.a('CUID'));
            				dms.split.wireSegSplitPoint.push(hnode);
            			}
//                        return;
                    } else if (results.length == 1) //找到一个资源
                    {
                        var oneObj = results[0];
                        if (oneObj.geometryType != "esriGeometryPolyline") { //点或面	
                            var cuid = oneObj.attributes["唯一标识"] || oneObj.attributes["CUID"] || oneObj.attributes["OBJECTID"];
                            var label = oneObj.attributes["中文标识"] || oneObj.attributes["LABEL_CN"] || oneObj.attributes["GRID_NAME"];
                            var className = oneObj.attributes.RELATED_BMCLASSTYPE_CUID || cuid.split('-')[0];
                            if (className == null) {
                                //非资源数据，返回
                                return;
                            }
                            var contextObject = {
                                'cuid': cuid,
                                'label': label,
                                'className': className,
                                'geometry': oneObj.geometry
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            dms.split.splitTools.locateOnMap([oneObj]);
                			if(tp.Default.DrawObject._drawLineClass === 'WIRE_SEG'){
                				var hnode = new ht.Node();
                				hnode.a('CUID',cuid);
                				dms.split.wireSegSplitPoint.push(hnode);
                			}
                        } else { //线
//                            self._locateMutiDataMenu(results, contextmenu, latlng);
                        };
                    } else //找到多个资源，则弹出菜单供用户选择定位的资源
                    {
                    	dms.split.splitTools.locatePointMenu(results, contextmenu, latlng);
                    };
                }
            }
        }
    },
    setOrigAndDestPointNode : function(data,map,graphView){
    	
    	if(data){
        	var origPointNode = new ht.Node();
            var destPointNode = new ht.Node();
            
    		var origPointCuid = data.ORIG_POINT_CUID,
    			origPointName = data.ORIG_POINT_NAME,
    			origPointLati = data.ORIG_POINT_LATITUDE,
    			origPointLongi = data.ORIG_POINT_LONGITUDE,
    			origDisCuid = data.ORIG_POINT_DISTRICT;
    		
    		if(origPointCuid){
    			//data可能为空的object对象，如果起止点为空，认为此数据有问题
    			origPointNode.a('CUID',origPointCuid);
        		origPointNode.a('LABEL_CN',origPointName);
        		origPointNode.a('LONGITUDE',origPointLongi);
        		origPointNode.a('LATITUDE',origPointLati);
        		origPointNode.a('RELATED_DISTRICT_CUID',origDisCuid);
        		DWREngine.setAsync(false);
        		DistNameAction.distNameByDistCuid(origDisCuid,function(data){
        			if(data){
        				origPointNode.a("RELATED_DISTRICT_CUID_NAME",data);
        			}
        		}); 
        		DWREngine.setAsync(true);
        		origPointNode.setId(origPointCuid);
        		origPointNode.setTag(origPointCuid);
        		dms.split.splitTools.setPointImage(origPointNode);
        		var origLatLng = new L.latLng(origPointLati, origPointLongi);
        		origPointNode.a('latLng',origLatLng);
        		var origPosition = map.latLngToContainerPoint(origLatLng);
        		origPointNode.setPosition(origPosition);
        		tp.Default.DrawObject._drawPointList.push(origPointNode);
        		graphView.dm().add(origPointNode);
        		
        		var destPointCuid = data.DEST_POINT_CUID,
        			destPointName = data.DEST_POINT_NAME,
        			destPointLati = data.DEST_POINT_LATITUDE,
        			destPointLongi = data.DEST_POINT_LONGITUDE,
        			destDisCuid = data.DEST_POINT_DISTRICT;
        			
        		destPointNode.a('CUID',destPointCuid);
        		destPointNode.a('LABEL_CN',destPointName);
        		destPointNode.a('LONGITUDE',destPointLongi);
        		destPointNode.a('LATITUDE',destPointLati);
        		destPointNode.a('RELATED_DISTRICT_CUID',destDisCuid);
        		DWREngine.setAsync(false);
        		DistNameAction.distNameByDistCuid(destDisCuid,function(data){
        			if(data){
        				destPointNode.a("RELATED_DISTRICT_CUID_NAME",data);
        			}
        		}); 
        		DWREngine.setAsync(true);
        		destPointNode.setId(destPointCuid);
        		destPointNode.setTag(destPointCuid);

        		var destLatLng = new L.latLng(destPointLati, destPointLongi);
        		destPointNode.a('latLng',destLatLng);
        		var destPosition = map.latLngToContainerPoint(destLatLng);
        		destPointNode.setPosition(destPosition);
        		tp.Default.DrawObject._drawPointList.push(destPointNode);
        		dms.split.splitTools.setPointImage(destPointNode);
        		graphView.dm().add(destPointNode);
    		}
    	}
    },
    //判定空间查询结果集中是否有点资源
    locatePointMenu: function(results, contextmenu, latlng) {
        var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView(),
		map = tpmap.getMap();
        
        var menuItems = [],
            pointObject = null, //点资源
            tmpContextObject = {}; //临时context变量   	
        var pointNum = 0; //点资源计数
        for (var i = 0; i < results.length; i++) {
            var resultGraphic = results[i];
            var cuid = resultGraphic.attributes["唯一标识"] || resultGraphic.attributes["CUID"] || resultGraphic.attributes["OBJECTID"];
            var className = resultGraphic.attributes.RELATED_BMCLASSTYPE_CUID || cuid.split('-')[0];
            var label = resultGraphic.attributes["中文标识"] || resultGraphic.attributes["LABEL_CN"] || resultGraphic.attributes["GRID_NAME"];
            var item = null;
            if (className == '') {
                continue;
            } //是否有格式类型的资源
            if ((resultGraphic.geometryType == "esriGeometryPoint") &&
                ('ACCESSPOINT' === className || 'MANHLE' === className || 'POLE' === className || 'STONE' === className || 'INFLEXION' === className || 'FIBER_DP' === className || 'FIBER_CAB' === className || 'FIBER_JOINT_BOX' === className || 'SITE' === className)) {
                pointNum++;
                pointObject = resultGraphic;
                
                var resid = resultGraphic.resid,
				resUrl = resultGraphic.url,
				cname = "DESIGN_"+className;
                //暂不考虑规划的
				if(resid && resid != 'designer'){
					cname = className;
				}
				
                tmpContextObject = {
                    'cuid': cuid,
                    'label': label,
                    'className': className
                };
                item = {
                    label: label,
                    locateObject: resultGraphic,
                    cuid: cuid,
                    icon: cname,
                    className: className,
                    action: function(item) {
                        var contextObject = {
                            'cuid': item.cuid,
                            'label': item.label,
                            'className': item.className
                        };
                        dms.split.splitTools.locateOnMap([item.locateObject]);
                        if(tp.Default.DrawObject._drawLineClass === 'WIRE_SEG'){
            				var hnode = new ht.Node();
            				hnode.a('CUID',cuid);
            				dms.split.wireSegSplitPoint.push(hnode);
            			}
                        tp.Default.OperateObject.contextObject = contextObject;
                    }
                };
                if (item !== null) {
                    menuItems.push(item);
                }
            };

        }
        if (pointNum > 1) { //多点资源显示菜单	    	
            contextmenu.setItems(menuItems);
            var point = map.latLngToContainerPoint(latlng);
            contextmenu.show(point.x, point.y);
        } else if (pointNum == 1) { //单点资源，不用提示菜单
            dms.split.splitTools.locateOnMap([pointObject]);
            tp.Default.OperateObject.contextObject = tmpContextObject;
//            tp.Default.DrawObject._drawState = 101;
        }

        return pointNum;
    },
    //定位至地图上高亮显示
    locateOnMap: function(resArr, geometryType, zoomFlag) {
    	var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView(),
		map = tpmap.getMap();

        for (var i = 0; i < resArr.length; i++) {
            var resultGraphic = resArr[i];
            var cuid = resultGraphic.attributes["唯一标识"] || resultGraphic.attributes["CUID"] || resultGraphic.attributes["OBJECTID"];
            var name = resultGraphic.attributes["中文标识"] || resultGraphic.attributes["LABEL_CN"] || resultGraphic.attributes["GRID_NAME"];
            var className = resultGraphic.attributes.className || resultGraphic.attributes.RELATED_BMCLASSTYPE_CUID || cuid.split('-')[0];
            var resultGeometryType = resultGraphic.geometry.y ? 'esriGeometryPoint' : (resultGraphic.geometry.paths ? 'esriGeometryPolyline' : 'esriGeometryPolygon');
            var districtId = resultGraphic.attributes["RELATED_DISTRICT_CUID"];
            if (resultGeometryType === "esriGeometryPoint") //如果是点
            {
                var point = map.latLngToContainerPoint(L.latLng(resultGraphic.geometry.y, resultGraphic.geometry.x));
                //屏幕坐标需转换为ht的坐标
                var node = graphView.dm().getDataById(cuid);
                if (node == null) {
                    var latLng = new L.latLng(resultGraphic.geometry.y, resultGraphic.geometry.x);
                    node = new ht.Node();
                    node.setId(cuid);
                    node.setTag(cuid);
                    node.a('CUID',cuid);
                    node.setImage(className);
                    node.setName(name);
                    node.setPosition(point);
//                    node.setAttr('bmClassId', className);
                    node.a('latLng', latLng);
                    node.s('body.color', 'red');
                    node.a('LONGITUDE',latLng.lng);
                    node.a('LATITUDE',latLng.lat);
                    node.a('LABEL_CN',name);
                    node.a('RELATED_DISTRICT_CUID',districtId);
					DWREngine.setAsync(false);
					DistNameAction.distNameByDistCuid(districtId,function(data){
						if(data){
							 node.a("RELATED_DISTRICT_CUID_NAME",data);
						}
					}); 
					DWREngine.setAsync(true);
                    dms.split.splitTools.setPointImage(node);
                    node.a('ISOLD','0');
                    var addLength = tp.Default.DrawObject._drawPointList.length;
                	//将新增加的点加入到倒数第二个位置上
                	tp.Default.DrawObject._drawPointList.splice(addLength-1,0,node);

//                    geoType = "esriGeometryPoint";
                    graphView.dm().add(node);
                    dms.split.splitTools.addSplitSeg();
                }
            }
        }
    },
    
    //定位至地图上高亮显示
    locateNewOnMap: function(resArr, geometryType, zoomFlag) {
    	var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView(),
		map = tpmap.getMap();

        for (var i = 0; i < resArr.length; i++) {
            var resultGraphic = resArr[i];
            var cuid = resultGraphic.a('CUID');
            var name = resultGraphic.a('NAME');
            var className = resultGraphic.a('bmClassId');
            var longilati = resultGraphic.a('latLng');
            //屏幕坐标需转换为ht的坐标
            var node = graphView.dm().getDataById(cuid);
            if (node) {
                node.s('body.color', 'red');
                dms.split.splitTools.setPointImage(node);
                if(dms.split.splitTools.isDesigner){
                	 node.a('RELATED_PROJECT_CUID',dms.designer.projectId);
                     node.a('RELATED_SEG_GROUP_CUID',dms.designer.segGroupCuid);
                }
                node.a('RELATED_DISTRICT_CUID',dms.Default.user.distCuid);
                node.a('RELATED_DISTRICT_CUID_NAME',dms.Default.user.distName);
                node.a('SPLITREMARK','新增点');
                var rstype = node.a('RES_TYPE');
                var resName = rstype+(tp.Default.DrawObject._drawPointList.length+1);
                node.setName(resName);
                node.a('LABEL_CN',resName);

                var addLength = tp.Default.DrawObject._drawPointList.length;
            	//将新增加的点加入到倒数第二个位置上
            	tp.Default.DrawObject._drawPointList.splice(addLength-1,0,node);
                dms.split.splitTools.addSplitSeg();
                
            }
        
        }
    },
    addSplitSeg : function(){
    	var bid = tp.Default.DrawObject._drawLineClass;
    	var pointList = tp.Default.DrawObject._drawPointList;
    	var lineList = tp.Default.DrawObject._drawLineList;
    	var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView();
    	var lineLength = tp.Default.DrawObject._drawLineList.length;
    	if(pointList && pointList.length>0){
    		if(lineList.length == 0){
    			//第一个段使用原来的段的cuid
    			var edge = new ht.Edge();
    			edge.setStyle('edge.center', true);
    			edge.setStyle('edge.gap', 2);
    			edge.setSource(pointList[0]);
    			edge.setTarget(pointList[1]);
    			edge.setId(dms.split.splitTools.splitSegCuid);
    			edge.setTag(dms.split.splitTools.splitSegCuid);
    			edge.a('bmClassId',dms.split.splitTools.bClassName);
    			dms.split.splitTools.setEdgeDashFlow(edge);
    			tp.Default.DrawObject._drawLineList.push(edge);
    			graphView.dm().add(edge);
    			
    			var nextEdge = new ht.Edge();
    			nextEdge.setSource(pointList[1]);
    			nextEdge.setTarget(pointList[2]);
    			nextEdge.setStyle('edge.center', true);
    			nextEdge.setStyle('edge.gap', 2);
//    			nextEdge.a('bmClassId',dms.split.splitTools.splitSeg.bClassName);
    			tp.Default.DrawObject._drawLineList.push(nextEdge);
    			
    			var cArray = [];
//    			cArray.push(edge);
    			cArray.push(nextEdge);
    			dms.split.splitTools.setNodeCuid(cArray,bid);
    			dms.split.splitTools.setEdgeDashFlow(nextEdge);
    			graphView.dm().add(nextEdge);
    		}else{
    			//点第二个及以后的点的时候，最后一段的起点要修改，中间新增加一段
    			//新增加的段起点是新增加的拆分点的前一个，止点为新增的拆分点
    			var lastEdge = lineList[lineList.length-1];
    			lastEdge.setSource(pointList[pointList.length-2]);
    			
    			var aedge = new ht.Edge();
    			aedge.setStyle('edge.center', true);
    			aedge.setStyle('edge.gap', 2);
    			aedge.setSource(pointList[pointList.length-3]);
    			aedge.setTarget(pointList[pointList.length-2]);
//    			aedge.a('bmClassId',dms.split.splitTools.splitSeg.bClassName);
    			tp.Default.DrawObject._drawLineList.splice(lineLength-1,0,aedge);
    			
    			var cArray = [];
    			cArray.push(aedge);
    			dms.split.splitTools.setNodeCuid(cArray,bid);
    			dms.split.splitTools.setEdgeDashFlow(aedge);
    			graphView.dm().add(aedge);
    			
    		}
    	}
    },
    setEdgeDashFlow : function(link){
    	link.s({
			 'edge.dash': true,
			 'edge.dash.flow':true,
			 'edge.width': 4,
			 'edge.dash.color': 'yellow',
			 'edge.dash.flow.step':1
		 });
    },

    setPointImage : function(node){
    	var imageName = node.a("CUID").split("-")[0];
    	node.setImage(imageName);
    	var resType = dms.isPoint[imageName];
    	node.a('RES_TYPE',resType);
    	node.a('bmClassId',imageName);
    },
    
    setNodeCuid : function(cuidArray,bmCid){
        var length = cuidArray.length;
         
  		for(var i=0;i< length;i++){
  			var resCuid = Math.generateCUID(bmCid);
  			var objElement = cuidArray[i];
  			objElement.a('CUID',resCuid);
  			objElement.setTag(resCuid);
  			objElement.setId(resCuid);
  			var bmId = resCuid.split("-")[0];
  			objElement.a('bmClassId',bmId);
  		}
          	
    },
    getDataByCuid : function(cuid){
    	 var tpmap = Dms.Default.tpmap,
			gv = tpmap.getGraphView();
    	 
    	 var result = null;
    	 gv.dm().each(function(d){
    		 if(d.a('CUID') === cuid){
    			 result = d;
     		 	 return; 
    		 }
    	 });
    	 return result;
    },
    getLayersId : function(bid){
		var layers = dms.Default.tpmap.getResouceLayers();//Dms.Default.tpmap._getMapMetaData();
//		var layers = metadata.layers;
		var selectPoints=dms.getSplitPointTypeByResName[bid];
		var ids=new Array();
		for(var i=0;i<layers.length;i++){
			var layer = layers[i],
			layerName = layer.name;
			if(dms.split.splitTools.contains(selectPoints,layerName)){
				ids.push(layer.id);
			}
		}
		return ids.join(',');
	},
	contains : function(cArray, n) {
		var c = false;
		cArray.forEach(function(data) {
			if(data == n){
				c = true;
			}
		});
		return c;
	}
    
};