$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
$importjs(ctx+'/dwr/interface/MergeDuctLineBranchsAction.js');

dms.merge.mergeBranchTools = {
	mergeBranch : function(isDesigner){
		var tpmap = Dms.Default.tpmap,
      	graphView = tpmap.getGraphView();
      	graphView.dm().clear();
      	dms.merge.mergeBranchTools.isDesigner = isDesigner;
      	tp.Default.DrawObject._drawLineList.length = 0;
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
			dms.merge.mergeBranchTools.mergeLineBranch(cuid);
		}
	},
    //合并管线分支
	mergeLineBranch: function(cuid) {
        var tpmap = Dms.Default.tpmap,
		graphView = tpmap.getGraphView();
        var bClassName = "";
        
        dms.merge.mergeBranchTools.mergeBranchCuids = [];
        
        if(cuid){
        	dms.merge.mergeBranchTools.mergeBranchCuid = cuid;//cuid是系统的cuid值
        	dms.merge.mergeBranchTools.bClassName = bClassName = cuid.split("-")[0];
        }
        tp.Default.DrawObject._drawState = 9;
        graphView.setEditableFunc(function(data)
		{
        	return false;
		});
        if (!graphView) return;
        var map = tpmap.getMap();
        graphView.isMovable = function(data) {
        	 if (tp.Default.DrawObject._drawState == 9) {
        		 return false;
        	 }
        };
        //在鼠标移动时提供一个提示标注
        if(Dms.Default.floatPane){
        	Dms.Default.floatPane.close();
        }else{
        	Dms.Default.floatPane =  new Dms.Panel.FloatPane();
        }
        graphView.enableDashFlow();
        DWREngine.setAsync(false);
        //选择分支
        map.on('click', function(e) {
            console.info('merge branch click');
            if (tp.Default.DrawObject._drawState == 9) { //点选,多个动态图层                
                var reslayers = Dms.Default.tpmap.getConfig().map.reslayers;
                var times = reslayers.length;
                var results = [];
                for (var i = 0; i < reslayers.length; i++) {
                    var url = reslayers[i].url;
                    tp.utils.identify(url, {
                        geometry: e.latlng.lng + ',' + e.latlng.lat,
                    }, identifyHandler);
                }
                
                function identifyHandler(response) {
                	times--;
                    if (response.error){
                    	tp.utils.optionDialog("错误", response.error.message);
                    }else {
                    	if(times == 0){
                    		results = results.concat(response.results);
                        	for (var i = 0; i < results.length; i++) {
                        		var branchCuid = response.results[i].attributes["RELATED_BRANCH_CUID"];
                        		dms.merge.mergeBranchTools.mergeBranchCuids.push(branchCuid);
                            	GenerateCuidAction.getAllSegsCuidByBranchCuid(branchCuid,function(data){
                            		if(data){
                            			for (var i = 0; i < data.length; i++){
                            				var segCuid = data[i];
                            				GenerateCuidAction.getPointsBySegCuid(segCuid,function(datas){
                                				dms.merge.mergeBranchTools.locateOnMap(datas);
                                			});
                            			}
                            		}
                            	});
                        	 }
                    	}
                    }
                }
            }
        });
    },
    
    //定位至地图上高亮显示
    locateOnMap: function(resArr, geometryType, zoomFlag) {
        var self = this,
        	tpmap = Dms.Default.tpmap,
            graphView = tpmap.getGraphView();
        var map = Dms.Default.tpmap.getMap();
        var geojsonArr = new Array();
        for (var i = 0; i < resArr.length - 1; i++) {
            var resultGeometryType = "esriGeometryPolyline";
            if (resultGeometryType === "esriGeometryPolyline") //如果是线或面
            {
            	var cuid1 = resArr[i].CUID;
            	var name1 = resArr[i].LABEL_CN;
            	var latitude1 = resArr[i].LATITUDE;
            	var longitude1 =  resArr[i].LONGITUDE;
            	var latlng1 = new L.LatLng(latitude1, longitude1);
            	var position1 = map.latLngToContainerPoint(latlng1);
            	var node1 = new ht.Node();
            	node1.setId(cuid1);
            	node1.setName(name1);
            	node1.setPosition(position1);
            	
            	var cuid2 = resArr[i + 1].CUID;
            	var name2 = resArr[i + 1].LABEL_CN;
            	var latitude2 = resArr[i + 1].LATITUDE;
            	var longitude2 =  resArr[i + 1].LONGITUDE;
            	var latlng2 = new L.LatLng(latitude2, longitude2);
            	var position2 = map.latLngToContainerPoint(latlng2);
            	var node2 = new ht.Node();
            	node2.setId(cuid2);
            	node2.setName(name2);
            	node2.setPosition(position2);
            	
                //绘制线
            	var edge = new ht.Edge();
    			edge.setStyle('edge.center', true);
    			edge.setStyle('edge.gap', 2);
    			edge.s('edge.width', 6);
    			edge.s('edge.color', 'red');
    			edge.setSource(node1);
    			edge.setTarget(node2);
    			edge.setAnimation({
                    hide: {
                        property: "opacity",
                        accessType: "style", 
                        from: 1, 
                        to: 0,
                        frames: 1,
                        next: "show"
                    },
                    show: {
                        property: "opacity",
                        accessType: "style", 
                        from: 0, 
                        to: 1,
                        frames: 1,
                        next: "hide"
                    },
                    start: ["hide"]
                });
    			tp.Default.DrawObject._drawLineList.push(edge);
    			graphView.dm().add(edge);
    			
                if (zoomFlag == true) {
                	var latLngBounds = self.getMaxBound(geojsonArr);
                	map.fitBounds(latLngBounds);
                	if (tp.Default.design) {
                		map.fitBounds(latLngBounds);
                	}
                }
            }
        }
        //触发定位完成的事件
        map.fire('locateFinished');
    }
};