/**
 * 移动点
 */
$importjs(ctx+'/dwr/interface/MovePointResourceAction.js');
(function(window,object,undefined){
	"use strict";
	dms.move.movePointsPanel = function(){
		var tpmap = Dms.Default.tpmap,
	  	map = tpmap.getMap();
		Dms.Default.tpmap.reset();
		
		var self = this;
		self.dm = dms.Default.tpmap.getGraphView().dm();
		var c = self.createEditPanel();
		dms.move.movePointsPanel.superClass.constructor.apply(this,[{
			title : '移动点信息',
			titleAlign :'center',
			width : 450,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 200,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon: ctx+'/map/close.png',
				action:function(){
					self.clearMovePoints();
					map.off('click',self.onMoveclick);
				}
			}],
			content : c
		}]);
		self.fp = function(){};

		dms.move.createEditPanel = c;
		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 
		setMapOnClick();
		movePoints();
	function setMapOnClick(){
		var tpmap = Dms.Default.tpmap,
	  	graphView = tpmap.getGraphView();
		tp.Default.DrawObject._drawState = 10;
        graphView.setEditableFunc(function(data)
		{
        	return false;
		});
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
        	if (tp.Default.DrawObject._drawState == 10) {
        		return true;
        	}
        };
        //在鼠标移动时提供一个提示标注
        curInterator.setSeriesTip('请选择点资源');
        graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
        var onMoveclick = self.onMoveclick = function(e) {
	       	 console.info('move point click');
	         if (tp.Default.DrawObject._drawState == 10) { //点选,多个动态图层
	         	var reslayers = dms.Default.tpmap.getConfig().map.reslayers;
	            var times = 1;
	            var results = [];
	            var url = null;
	            var reslay = dms.getMovePointTypeByResName['POINT'];
	            var layerIds = Dms.Tools.getLayerIdsByTypeName(reslay);
	            if(reslayers && reslayers.length > 2){
	            	var scene = dms.designer.scene;
	            	if(scene && (scene == 'DesignScene' || scene == 'ConstructScene' || scene == 'ViewpointScene')){
	                	url = reslayers[1].url;
	                }else if(scene && scene == 'planScene'){
	                	url = reslayers[2].url;
	                }else{
	                	url = reslayers[0].url;
	                }
	            }else{
	            	if(reslayers[1]!=null &&  reslayers[1].resid=='designer'){
	            		url = reslayers[1].url;
	                }else{
	            	    url = reslayers[0].url;
	                }
	            }
	            tp.utils.identify(url, {
	            	geometry: e.latlng.lng + ',' + e.latlng.lat,
	            	layers : layerIds
	            }, identifyHandler);
	         }
	         function identifyHandler(response) {
	 	        times--;
	 	        if (response.error)
	 	            tp.utils.optionDialog("错误", response.error.message);
	 	        if (times == 0) {
	 	        	results = results.concat(response.results);
	 	            if (results.length == 1){ //找到一个资源
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
	                        locateOnMap([oneObj]);
	                    }
	                }else if(results.length>1){
	                	var contextmenu = tpmap._contextmenu;
	                	_locatePointResouceMenu(results, contextmenu,e.latlng);
	                }
	 	        }
	         }
	    };
        map.on('click', onMoveclick);
        function setPointImage(node){
        	var imageName = node.a("CUID").split("-")[0];
        	node.setImage(imageName);
        	var resType = dms.isPoint[imageName];
        	node.a('RES_TYPE',resType);
        	node.a('bmClassId',imageName);
        };
        function _locatePointResouceMenu(results, contextmenu,latlng){
            var tpmap = Dms.Default.tpmap;
            var map = tpmap.getMap();
            var menuItems = [];
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
                    item = {
                        label: label,
                        locateObject: resultGraphic,
                        cuid: cuid,
                        icon : className,
                        className: className,
                        action: function(item) {
                            locateOnMap([item.locateObject]);
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
            }
        };
        //定位至地图上高亮显示
        function locateOnMap(resArr, geometryType, zoomFlag) {
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
                        var oldlng = L.Util.formatNum(latLng.lng, 6);
    	    	        var oldlat = L.Util.formatNum(latLng.lat, 6);
    	    	        
                        node.a('LONGITUDE',oldlng);
                        node.a('LATITUDE',oldlat);
                        node.a('LABEL_CN',name);
                        node.a('RELATED_DISTRICT_CUID',districtId);
                        node.a('oldlatLng', latLng);
                        node.a('OLDLONGITUDE',oldlat);
                        node.a('OLDLATITUDE',oldlat);
                        setPointImage(node);
                        graphView.dm().add(node);
                        
                        var tableNode = self.nodeDm.getDataByTag(cuid);
                        if(!tableNode){
                        	tableNode = new ht.Node();
                        	for(var p in node._attrObject){
                            	tableNode.a(p,node.a(p));
                			}
                            //地图上第一次点击时新旧值设置为一样
        	    	        var oldlongiLati = oldlng+","+oldlat;
        	    	        tableNode.a("OLD",oldlongiLati);
        	    	        //node.a("NEW",newlongiLati);
        	    	        //tableNode.a("NEW_LONGITUDE",oldlng+"");
        	    	        //tableNode.a("NEW_LATITUDE",oldlng+"");
        	    	        
                            tableNode.setTag(cuid);
                            tableNode.setId(cuid);
                            self.nodeDm.add(tableNode);
                        }
                    }
                }
            }
        }
    };
	function movePoints(){
		var mgv = Dms.Default.tpmap.getGraphView();
		tp.Default.DrawObject._movePointState = 1;
	    mgv.addInteractorListener(function (e) {
	    	if(tp.Default.DrawObject._drawState == 10 && e.kind === 'endMove'){
	    		console.log('结束移动图元');
	    	    //移动完成使用这个
	    	    for (var i = 0; i < tp.Default.DrawObject._movePointList.length; i++) {
	    	    	var item = tp.Default.DrawObject._movePointList[i];
	    	        var cuid = item._id;
	    	        var latlng = item.getAttr("latLng");
	    	        var newlng = L.Util.formatNum(latlng.lng, 6);
	    	        var newlat = L.Util.formatNum(latlng.lat, 6);
	    	        //var newlongiLati = newlng+","+newlat;
	    	        var node = mgv.dm().getDataById(cuid);
	    	        var oldlatlng = node.getAttr("oldlatLng");
	    	        var oldlng = L.Util.formatNum(oldlatlng.lng, 6);
	    	        var oldlat = L.Util.formatNum(oldlatlng.lat, 6);
	    	        var oldlongiLati = oldlng+","+oldlat;
	    	        node.a("OLD",oldlongiLati);
	    	        //node.a("NEW",newlongiLati);
	    	        node.a("NEW_LONGITUDE",newlng+"");
	    	        node.a("NEW_LATITUDE",newlat+"");
	    	        
	    	        var dataByTag=self.nodeDm.getDataByTag(cuid);
	    	        if(dataByTag){
	    	        	for(var p in node._attrObject){
	    	        		dataByTag.a(p,node.a(p));
            			}
	    	        	/*for(var p in node){
	    	        		dataByTag.a(p,node.a(p));
	    				}*/
	    	        }else{
	    	        	var tableNode = new ht.Node();
                        for(var p in node._attrObject){
                        	tableNode.a(p,node.a(p));
            			}
                        /*for(var p in node){
                        	tableNode.a(p,node.a(p));
	    				}*/
                        tableNode.setTag(cuid);
                        tableNode.setId(cuid);
                        self.nodeDm.add(tableNode);
	    	        }
	    	    }
	    	}
	    });
	  }
	};
	
	ht.Default.def('dms.move.movePointsPanel',ht.widget.Panel,{
		dm : null,
		nodeDm : null,
		saveBtn : null,
		deleteBtn : null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x-450, y+90);
			document.body.appendChild(self.getView());
		},
		addColumns : function(){
			var attritudes = [{
				name: 'LABEL_CN',
                displayName: '点设施名称',
                editable: false,
                accessType :'attr',
                width:120
			},{
				name: 'OLD',
                displayName: '原经纬度',
                editable: false,
                accessType :'attr',
                width:130
			},{
				name: 'NEW_LONGITUDE',
                displayName: '移动后经度',
                editable: true,
                tag: 'LONGI_TYPE',
                accessType :'attr',
                width:80
			},{
				name: 'NEW_LATITUDE',
                displayName: '移动后纬度',
                editable: true,
                tag: 'LATI_TYPE',
                accessType :'attr',
                width:80
			}];
			return attritudes;
		},
		createEditPanel : function(){
			var self = this;
			var formpanel = new ht.widget.FormPane();
			var tablePanel = self.createPointsInfoPanel();
			var bottompanel = self.createBottomPanel();
			formpanel.addRow([tablePanel],[0.1],0.1);
			formpanel.addRow([bottompanel],[0.1],30);
			return formpanel;
		},
		createPointsInfoPanel : function(){
			var self = this;
			var pointsPanel = new ht.widget.FormPane();
			pointsPanel.setVPadding(1);
			pointsPanel.setHPadding(1);
			var tablePanel = new ht.widget.TablePane(),
			tableView = tablePanel.getTableView();
			self.nodeDm = new ht.DataModel();
			tableView.setDataModel(self.nodeDm);
			
			tableView.addColumns(self.addColumns());
			self.tableView = tableView;
			var longiColumn = tableView.getColumnModel().getDataByTag('LONGI_TYPE');
			longiColumn.getValue = function(data){
				var longi = data.a('NEW_LONGITUDE'),
				lati = data.a('NEW_LATITUDE'),
				cid = data.getId();
				if(!longi){
					longi = data.a('LONGITUDE');
					lati = data.a('LATITUDE');
				}
				var node=self.dm.getDataByTag(cid);
				if(node){
					node.a('NEW_LONGITUDE',longi);
					var lt = new L.LatLng(lati,longi);
					node.a('latLng',lt);
					
					var latLngToContainerPoint = dms.Default.tpmap.getMap().latLngToContainerPoint(lt);
			        node.setPosition(latLngToContainerPoint);
				}
//		        self.tableView.invalidateModel(data);
				return longi;
			};
			
			var latiColumn = tableView.getColumnModel().getDataByTag('LATI_TYPE');
			latiColumn.getValue = function(data){
				var lati = data.a('NEW_LATITUDE'),
				longi = data.a('NEW_LONGITUDE'),
				cid = data.getId();
				if(!lati){
					longi = data.a('LONGITUDE');
					lati = data.a('LATITUDE');
				}
				var node=self.dm.getDataByTag(cid);
				if(node){
					node.a('NEW_LATITUDE',lati);
					var lt = new L.LatLng(lati,longi);
					node.a('latLng',lt);
					var latLngToContainerPoint = dms.Default.tpmap.getMap().latLngToContainerPoint(lt);
			        node.setPosition(latLngToContainerPoint);
				}
//		        self.tableView.invalidateModel(data);
				return lati;
			};
			
			tableView.setCheckMode(true);
			pointsPanel.addRow([tablePanel],[0.1],0.1);
			return pointsPanel;
		},
		createBottomPanel : function(){
			var self = this;
			var bottompanel = new ht.widget.FormPane();
			bottompanel.setVPadding(1);
			bottompanel.setHPadding(1);
			
			var selectBtn = new ht.widget.Button();
			selectBtn.setLabel('框选');
			selectBtn.onClicked = function(e){
				self.createShape(dms.Default.tpmap.getGraphView(), 'rect', '矩形', 'TRECTANGLE');
			};
			
			var deleteBtn = self.deleteBtn = new ht.widget.Button();
			deleteBtn.setLabel('撤销移动点');
			deleteBtn.onClicked = function(e){
				//dms.Tools.deleteDatas(dms.Default.tpmap.getGraphView().dm());
				
				var selectionModel = self.nodeDm.sm();
				while(selectionModel.size() > 0){
					var ld = selectionModel.ld(),
					tagCuid = ld.a('CUID');
					self.nodeDm.removeDataByTag(tagCuid);
					self.dm.removeDataByTag(tagCuid);
					
					if(tp.Default.DrawObject._movePointList){
						var delIndex=tp.Default.DrawObject._movePointList.indexOf(ld);
						if(delIndex && delIndex != -1){
							tp.Default.DrawObject._movePointList.splice(delIndex,1);
						}
					}
                }
				//tp.Default.DrawObject._movePointList =[];
			};
			
			var saveBtn = self.saveBtn = new ht.widget.Button();
			saveBtn.setLabel('确定');
			saveBtn.onClicked = function(e){
				self.saveBtn.setDisabled(true);
				self.deleteBtn.setDisabled(true);
				self.savePoints(self.dm);
			};
			bottompanel.addRow([null,deleteBtn,null,saveBtn,null],[0.2,80,0.1,80,0.2],23);
			return bottompanel;
		},
		clearMovePoints : function(){
			var self = this;
			tp.utils.wholeUnLock();
			self.saveBtn.setDisabled(false);
			self.deleteBtn.setDisabled(false);
			self.nodeDm.clear();
			document.body.removeChild(self.getView());
			Dms.Default.tpmap.reset();
			if(dms.move.createEditPanel){
				dms.move.createEditPanel = null;
			}
		},
		savePoints : function(datas){
			var self = this;
			var newData =[];
			var dataArr = datas.getDatas();
			if(dataArr && dataArr.size() > 0){
				for(var i = 0; i < dataArr.size(); i++ ){
					var data = dataArr.get(i);
					var cuid = data._id;
		        	var latlng = data.getAttr("latLng");
		        	var oldLatlng = data.getAttr("oldlatLng");
		        	if(latlng.lng==oldLatlng.lng && latlng.lat==oldLatlng.lat){
		        		tp.utils.optionDialog("温馨提示", '请移动点!');
		        		return;
		        	}
		        	var longiLati = latlng.lng+","+latlng.lat;
		        	newData.push({"CUID":cuid,"LONGILATI":longiLati});
				}
			}else{
				tp.utils.optionDialog("温馨提示", '请选中要移动的点!');
        		return;
			}
			tp.utils.wholeLock();
			MovePointResourceAction.modifyPointLocation(newData,function(datas){
				if(datas){
					
				}
				tp.utils.wholeUnLock();
				self.clearMovePoints();
				Dms.Default.tpmap.refreshMap();//地图刷新
				Dms.Default.tpmap.reset();//
				tp.utils.optionDialog("温馨提示", '点移动完成！');
			});
		},
		
		//创建图形,目前没有用到
		createShape : function(){
			return function(gv, vectorType, vectorName, bmClassId) {
				var createVectorInteractor  = new CreateVectorInteractor(gv, document.getElementById('main'));
				gv.setInteractors(new ht.List([
					new ht.graph.SelectInteractor(gv),
					Topo.Default.editInteractor,
					new Topo.widget.CustomMoveInteractor(gv),
					new ht.graph.DefaultInteractor(gv),
					createVectorInteractor
				]));
				createVectorInteractor._vectorType = vectorType;
				createVectorInteractor.onCreateStarted = function(node) {
					console.info("onCreateStarted");
					node.s("shape.background", "#5CACEE");
					node.s("shape.border.color", "black");
					node.s("shape.border.width", 2);
					node.setName(vectorName);
				};
				createVectorInteractor.onCreateCompleted = function(node) {
					console.info("onCreateCompleted");
					node.a("bmClassId", bmClassId);
					node.s("shape.background", "#5CACEE");
					node.s("shape.border.color", "black");
					node.s("shape.border.width", 2);
				};
			};
		}
	});
	
})(this,Object);