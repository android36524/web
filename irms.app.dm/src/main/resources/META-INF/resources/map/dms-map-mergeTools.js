$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
$importjs(ctx+'/dwr/interface/MergeDuctLineSegsAction.js');
$importjs(ctx+'/map/uuid.js');

dms.merge.mergeTools = {
	mergeSeg : function(isDesigner){
		var tpmap = Dms.Default.tpmap,
      	graphView = tpmap.getGraphView();
      	graphView.dm().clear();
      	dms.merge.mergeTools.isDesigner = isDesigner;
      	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	if(isDesigner){
			var isFlag = dms.Tools.getIsTnmsDatas(cuid);
            if(!isFlag){
           	 tp.utils.optionDialog("错误提示", '非设计数据，不能执行合并操作！');
           	 return ;
            } 
		}
      	var isflag = Dms.Tools.getIsOperate(isDesigner,cuid);
		if(isflag){
			dms.merge.mergeTools.mergeLineSeg(cuid,isDesigner);
		}
	},
    //合并管线段
	mergeLineSeg: function(cuid) {
        var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView();
        //清空绘制的点
        tp.Default.DrawObject._drawPointList.length = 0;
        //清空绘制的线
        tp.Default.DrawObject._drawLineList.length = 0;
        //绘制的节点类型及图标
        tp.Default.DrawObject._drawPointClass = '';
        //绘制的线类型及图标
        tp.Default.DrawObject._drawLineClass = '';
        var bClassName = "";
        if(cuid){
        	dms.merge.mergeTools.mergeSegCuid = cuid;
        	dms.merge.mergeTools.bClassName = bClassName = cuid.split("-")[0];
        }
        tp.Default.DrawObject._drawState = 8;
        graphView.setEditableFunc(function(data)
		{
        	return false;
		});
        if (!graphView) return;
        var map = tpmap.getMap();
        graphView.isMovable = function(data) {
        	 if (tp.Default.DrawObject._drawState == 8) {
        		 return false;
        	 }
        };
        var curInterator = tp.Default.OperateObject.curInterator = new CreateVectorInteractor(graphView);
        graphView.setInteractors(new ht.List([
              new ht.graph.SelectInteractor(graphView),
              new ht.graph.EditInteractor(graphView),
              new ht.graph.MoveInteractor(graphView),
              new ht.graph.DefaultInteractor(graphView),
              curInterator
          ]));
        //在鼠标移动时提供一个提示标注
        curInterator.setSeriesTip('请选择两个合并的点');
        if(Dms.Default.floatPane)
        	Dms.Default.floatPane.close();
        else
        	Dms.Default.floatPane =  new Dms.Panel.FloatPane();
        
        graphView.enableDashFlow();
        //选择点
        map.on('click', function(e) {
            console.info('merge line click');
            if (tp.Default.DrawObject._drawState == 8) { //点选,多个动态图层
                var reslayers = Dms.Default.tpmap.getConfig().map.reslayers;
                var times = reslayers.length;
                var selectPoints=dms.getPointTypeByBraResName[bClassName];
                var results = [];
                var reslay = dms.Tools.getLayerUrlsByType(selectPoints);
                reslay = dms.merge.mergeTools.getLayersId(bClassName);
                for (var i = 0; i < reslayers.length; i++) {
                    var url = reslayers[i].url;
                    tp.utils.identify(url, {
                        geometry: e.latlng.lng + ',' + e.latlng.lat,
                    	layers : reslay
                    }, identifyHandler);
                }
                
                function identifyHandler(response) {
                    times--;
                    if (response.error)
                        tp.utils.optionDialog("错误", response.error.message);
                    else {
                    	results = results.concat(response.results);
                    }
                    if (times == 0) {
                        var contextmenu = dms.mergeContextmenu;
                        if (contextmenu)
                            contextmenu.hide();
                        var latlng = e.latlng;
                        if (results.length == 0) {
                        	var newNode = new ht.Node();
                        	var cArray = [];
                			cArray.push(newNode);
                			dms.merge.mergeTools.setNodeCuid(cArray,tp.Default.DrawObject._drawPointClass);
                			var position = map.latLngToContainerPoint(latlng);
                			newNode.a('latLng',latlng);
                			newNode.setPosition(position);
                			graphView.dm().add(newNode);
                			dms.merge.mergeTools.locateNewOnMap([newNode]);
                        } else if (results.length == 1){ //找到一个资源
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
                                dms.merge.mergeTools.locateOnMap([oneObj]);
                            } else { //线
                            	
                            };
                        } else { //找到多个资源，则弹出菜单供用户选择定位的资源
                        	dms.merge.mergeTools.locatePointMenu(results, contextmenu, latlng);
                        };
                    }
                }
            }
        });
    },
    //判定空间查询结果集中是否有点资源
    locatePointMenu: function(results, contextmenu, latlng) {
        var tpmap = Dms.Default.tpmap,
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
                tmpContextObject = {
                    'cuid': cuid,
                    'label': label,
                    'className': className
                };
                item = {
                    label: label,
                    locateObject: resultGraphic,
                    cuid: cuid,
                    className: className,
                    action: function(item) {
                        var contextObject = {
                            'cuid': item.cuid,
                            'label': item.label,
                            'className': item.className
                        };
                        dms.merge.mergeTools.locateOnMap([item.locateObject]);
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
            dms.merge.mergeTools.locateOnMap([pointObject]);
            tp.Default.OperateObject.contextObject = tmpContextObject;
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
            if (resultGeometryType === "esriGeometryPoint") { //如果是点
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
                    node.a('latLng', latLng);
                    node.s('body.color', 'red');
                    node.a('LONGITUDE',latLng.lng);
                    node.a('LATITUDE',latLng.lat);
                    node.a('LABEL_CN',name);
                    node.a('RELATED_DISTRICT_CUID',districtId);
                    dms.merge.mergeTools.setPointImage(node);
                    
                    var addLength = tp.Default.DrawObject._drawPointList.length;
                	//将新增加的点加入到倒数第一个位置上
                	tp.Default.DrawObject._drawPointList.splice(addLength,0,node);

                    geoType = "esriGeometryPoint";
                    graphView.dm().add(node);
                    dms.merge.mergeTools.addMergeSeg();
                }
            }
        }
    },
    
    //定位至地图上高亮显示
    locateNewOnMap: function(resArr, geometryType, zoomFlag) {
    	var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView();
        for (var i = 0; i < resArr.length; i++) {
            var resultGraphic = resArr[i];
            var cuid = resultGraphic.a('CUID');
            //屏幕坐标需转换为ht的坐标
            var node = graphView.dm().getDataById(cuid);
            if (node) {
                node.s('body.color', 'red');
                dms.merge.mergeTools.setPointImage(node);
                if(dms.designer){
                	  node.a('RELATED_PROJECT_CUID',dms.designer.projectId);
                      node.a('RELATED_SEG_GROUP_CUID',dms.designer.segGroupCuid);
                }
                node.a('RELATED_DISTRICT_CUID',dms.Default.user.distCuid);
                node.a('MERGEREMARK','新增点');
    			
                var addLength = tp.Default.DrawObject._drawPointList.length;
            	tp.Default.DrawObject._drawPointList.splice(addLength,0,node);
                geoType = "esriGeometryPoint";
                dms.merge.mergeTools.addMergeSeg();
            }
        }
    },
    addMergeSeg : function(){
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
    			edge.setId(dms.merge.mergeTools.mergeSegCuid);
    			edge.setTag(dms.merge.mergeTools.mergeSegCuid);
    			edge.a('bmClassId',dms.merge.mergeTools.bClassName);
    			dms.merge.mergeTools.setEdgeDashFlow(edge);
    			tp.Default.DrawObject._drawLineList.push(edge);
    			graphView.dm().add(edge);
    			
    			var nextEdge = new ht.Edge();
    			nextEdge.setSource(pointList[1]);
    			nextEdge.setTarget(pointList[2]);
    			nextEdge.setStyle('edge.center', true);
    			nextEdge.setStyle('edge.gap', 2);
    			tp.Default.DrawObject._drawLineList.push(nextEdge);
    			
    			var cArray = [];
    			cArray.push(nextEdge);
    			dms.merge.mergeTools.setNodeCuid(cArray,bid);
    			dms.merge.mergeTools.setEdgeDashFlow(nextEdge);
    			graphView.dm().add(nextEdge);
    		}else{
    			//选中点间拉高亮线预览
    			var lastEdge = lineList[lineList.length-1];
    			lastEdge.setSource(pointList[pointList.length-1]);
    			
    			var aedge = new ht.Edge();
    			aedge.setStyle('edge.center', true);
    			aedge.setStyle('edge.gap', 2);
    			aedge.setSource(pointList[pointList.length-2]);
    			aedge.setTarget(pointList[pointList.length-1]);
    			tp.Default.DrawObject._drawLineList.splice(lineLength-1,0,aedge);
    			
    			var cArray = [];
    			cArray.push(aedge);
    			dms.merge.mergeTools.setNodeCuid(cArray,bid);
    			dms.merge.mergeTools.setEdgeDashFlow(aedge);
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
    getLayersId : function(bid){
		var layers = dms.Default.tpmap.getResouceLayers();
		var selectPoints=dms.getPointTypeByBraResName[bid];
		var ids=new Array();
		for(var i=0;i<layers.length;i++){
			var layer = layers[i],
			layerName = layer.name;
			if(dms.merge.mergeTools.contains(selectPoints,layerName)){
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