$importjs(ctx + "/map/ux/tp-dropdownlist.js");
$importjs(ctx + "/dwr/interface/DmResExplorerAction.js");
//集客勘查
(function(window,Object,undefined){
	"use strict";
	
	Dms.widget.ResSearchPanel = function(){
		var self = this;
		
		//绘制组件
		var formPane = self._formPane =  new ht.widget.FormPane();
		
		var geoPositionRadio = self._geoPositionRadio = new ht.widget.RadioButton();
		geoPositionRadio.setLabel('地理位置');
		geoPositionRadio.setGroupId('manualInput');
		geoPositionRadio.setSelected(true);
		
		var customerNameRadio = self._customerNameRadio = new ht.widget.RadioButton(); 
		customerNameRadio.setLabel('客户名称');
		customerNameRadio.setGroupId('manualInput');
		
		var siteNameRadio = self._siteNameRadio = new ht.widget.RadioButton(); 
		siteNameRadio.setLabel('站点名称');
		siteNameRadio.setGroupId('manualInput');
		
		var addrComboBox = self._addrComboBox = new ht.widget.MultiComboBox();
		addrComboBox.setEditable(true);
		addrComboBox.setDropDownComponent(tp.widget.DropDownList);
		//addrComboBox.open();
		addrComboBox.callback = self.handleCallback;
		addrComboBox.parentPanel = self;
		
		var queryButton = new ht.widget.Button();
		queryButton.setLabel('查询');
		
		var locateButton = new ht.widget.Button();
		locateButton.setLabel('定位');
		
		var lngInput = self._lngInput = new ht.widget.TextField();
		lngInput.setType('number');
		lngInput.setEditable(false);
		var latInput = self._latInput = new ht.widget.TextField();
		latInput.setType('number');
		latInput.setEditable(false);
		self.setLngLat('','');
		
		
		var selectButton = new ht.widget.Button();
		selectButton.setIcon('hand');
		selectButton.enableToolTip();
		selectButton.setToolTip('点击地图拾取经纬度');
		selectButton.setBackground('white');
		
		var shortDistanceRadio = self._shortDistanceRadio = new ht.widget.RadioButton();
		shortDistanceRadio.setLabel('<300m');
		shortDistanceRadio.setGroupId('coverRange');
		
		var mediumDistanceRadio = self._mediumDistanceRadio = new ht.widget.RadioButton();
		mediumDistanceRadio.setLabel('<500m');
		mediumDistanceRadio.setGroupId('coverRange');
		mediumDistanceRadio.setSelected(true);
		self.queryDistance = 500;
		
		var longDistanceRadio = self._longDistanceRadio = new ht.widget.RadioButton();
		longDistanceRadio.setLabel('<1000m');
		longDistanceRadio.setGroupId('coverRange');
		
		var businessType = self._businessType =  new ht.widget.CheckBox();
		businessType.setLabel("集客业务");
		businessType.setSelected(true);
		
		
		var searchButton = self._searchButton = new ht.widget.Button();
		searchButton.setLabel("勘查");
		
		formPane.addRow(['人工输入',geoPositionRadio,customerNameRadio,siteNameRadio],[60,0.1,0.1,0.1],22);
		formPane.addRow([addrComboBox,queryButton,locateButton],[0.1,60,60],22);
		formPane.addRow(['地图拾取','经度',lngInput,'纬度',latInput,selectButton],[80,30,0.1,30,0.1,22],22);
		formPane.addRow(['勘查范围',shortDistanceRadio,mediumDistanceRadio,longDistanceRadio],[60,0.1,0.1,0.1],22);
		formPane.addRow(['业务类型',businessType],[60,0.1],22);
		formPane.addRow(['',searchButton],[0.1,60],22);
		
		var tabView = self._tabView = self.createTabView();
		formPane.addRow([tabView],[0.1],300);
		
		Dms.widget.ResSearchPanel.superClass.constructor.apply(this,[{
			title : "集客勘查",
			width : 620,
			exclusive : true,
			titleColor : "white",
			minimizable : true,
			expand : true,
			narrowWhenCollapse : true,
			contentHeight : 500,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
				action:function(){
					self.close();
				}
			}],
			content : formPane.getView()
		}]);
		
		//处理事件
		self.addEventListener(function(e) {
			if (e.kind === "beginRestore" || e.kind === "betweenResize" || 
					(e.kind === "endToggle" && self._config.expand === true)){
				formPane.invalidate();
			}
		});
		
		var accNumcolumn = self._accessPointPanel._tableView.getColumnModel().getDataByTag('ACCESSPOINT_NUM');
		accNumcolumn.drawCell = function (g, data, selected, column, x, y, w, h) {
			var a = document.createElement('a');
			a.innerHTML = data.a('ACCESSPOINT_NUM');
			a.onclick = function(e){
				self.openAccessPointDetailPanel(data);
				return false;
			};
			a.setAttribute('style','color:blue;cursor:hand;text-decoration:underline;');
			return a;
		};
		
		var freePortColumn = self._accessResDetailPanel._tableView.getColumnModel().getDataByTag('FREE_PORT_NUM');
		freePortColumn.drawCell = function (g, data, selected, column, x, y, w, h) {
			var a = document.createElement('a');
			a.innerHTML = data.a('FREE_PORT_NUM');
			a.onclick = function(e){
				self.openPortDetailPanel(data);
				return false;
			};
			a.setAttribute('style','color:blue;cursor:hand;text-decoration:underline;');
			return a;
		};
		
		
		shortDistanceRadio.onClicked = function(){
			var scope = this;
			self.setDistance(scope);
		};
		
		mediumDistanceRadio.onClicked = function(){
			var scope = this;
			self.setDistance(scope);
		};
		
		longDistanceRadio.onClicked = function(){
			var scope = this;
			self.setDistance(scope);
		};
		
		selectButton.onClicked = function(){
			tp.Default.DrawObject._movePointState = 0;
			tp.Default.DrawObject._drawState = 10018;
			var tpmap = Dms.Default.tpmap;
			tpmap.getGraphView().getView().style.cursor = 'default';
			var onclick = function(e) {
				if (tp.Default.DrawObject._drawState == 10018){ //选取经纬度
					tp.Default.DrawObject._drawState = 0;
					self.setLngLat(e.latlng.lng,e.latlng.lat);
					self._addrComboBox.setValue("");
					self._addrComboBox.listDatas = null;
					if(self._addrComboBox.listView){
						self._addrComboBox.listView.dm().clear();
					}
					//self.queryByDistance();
					tpmap.getMap().off('click',onclick);
	            }
			};
			tpmap.getMap().on('click',onclick);
		},
		
		queryButton.onClicked = function(){
			if(geoPositionRadio.isSelected()){
				self.geoQuery();
			}else if(customerNameRadio.isSelected()){
				self.queryAddrByCustomerName();
			}else if(siteNameRadio.isSelected()){
				self.queryAddrBySiteName();
			}
			//addrComboBox.open();
		};
		
		searchButton.onClicked = function(){
			self.queryByDistance();
		};
		
		locateButton.onClicked = function(){
			var listDatas = addrComboBox.listDatas;
			if(!listDatas || (listDatas && !listDatas.sm().ld()) ){
				tp.utils.optionDialog("温馨提示",'经纬度信息不正确，无法定位！');
				return;
			}
			var data = addrComboBox.listDatas.sm().ld();
			data.a('className','COMMAND');
			if(data.a("graphic")){
				var graphic = data.a("graphic");
				var tpmap = Dms.Default.tpmap;
				self.setLngLat(graphic.x,graphic.y);
		    	var graphic = {
		    			geometryType : 'esriGeometryPoint',
		    			attributes : data._attrObject,
		    			geometry : {x:graphic.x,y:graphic.y}
		    	};
		    	tpmap.locateOnMap([graphic],'esriGeometryPoint',true);
			}else{
				var longitude = data.a("LONGITUDE");
				var latitude = data.a("LATITUDE");
				
				if(longitude && latitude && latitude > 0 && latitude> 0)
				{
					var tpmap = Dms.Default.tpmap;
			    	var graphic = {
			    			geometryType : 'esriGeometryPoint',
			    			attributes : data._attrObject,
			    			geometry : {x:longitude,y:latitude}
			    	};
			    	tpmap.locateOnMap([graphic],'esriGeometryPoint',true);
			    	self.setLngLat(longitude,latitude);
				}else{
					self.setLngLat("","");
					tp.utils.optionDialog("温馨提示",'经纬度信息不正确，无法定位！');
					
					return;
				}
			}
		};
		
	};
	ht.Default.def('Dms.widget.ResSearchPanel',ht.widget.Panel,{
		resType : ['SITE','FIBER_CAB','FIBER_DP','FIBER_JOINT_BOX'],	
		searchRes : [],
				
		createTabView : function(){
			var self = this;
			var tabView = new ht.widget.TabView();
			var accesspointPanel = self._accessPointPanel = self.createResPanel("accesspoint");
			var accessResDetailPanel = self._accessResDetailPanel = self.createResPanel("accessresdetail");
			var portDetailPanel = self._portDetailPanel = self.createResPanel("portdetail");
			
			tabView.add('可接入点',accesspointPanel);
			tabView.add('可接入资源明细',accessResDetailPanel);
			tabView.add('设备端口明细',portDetailPanel);
			tabView.select(0);
			tabView.setSelectBackground('#D26911');
			return tabView;
		},
		
		openAccessPointDetailPanel : function(data){
			var self = this;
			var accesspointNum = data.a('ACCESSPOINT_NUM');
			if(accesspointNum && Number(accesspointNum) > 0) {
				self._accessResDetailPanel._tableView.dm().clear();
				var accesspoints = data.a('DATA');
				for(var i = 0,len = accesspoints.length; i < len; i++){
					var data = new ht.Data();
					data._attrObject = accesspoints[i];
					self._accessResDetailPanel._tableView.dm().add(data);
				}
				self._tabView.select(1);
			}
		},
		
		openPortDetailPanel : function(data){
			var self = this;
			var roomCuid = data.a('RELATED_ROOM_CUID');
			if(roomCuid){
				self._portDetailPanel._tableView.dm().clear();
				tp.utils.lock(self._formPane);
				DmResExplorerAction.getPortDetail(roomCuid,function(result){
					if(result){
						for(var i = 0,len = result.length; i < len; i++){
							var data = new ht.Data();
							data._attrObject = result[i];
							self._portDetailPanel._tableView.dm().add(data);
						}
					}
					tp.utils.unlock(self._formPane);
					self._tabView.select(2);
				});
			}
		},
		
		createResPanel : function(type){
			var tablePanel = new ht.widget.TablePane(),
			tableView = tablePanel.getTableView();
			
			var url = ctx + "/map/columnCfg/" + type + ".json";
			$.ajaxSettings.async = false;
			$.getJSON(url,{},function(data){
				tableView.addColumns(data);
				tablePanel.iv();
			});
			$.ajaxSettings.async = true;
			return tablePanel;
		},
		
		setLngLat : function(lng,lat){
			var self = this;
			if(lng){
				self._lngInput.setText(Number(lng).toFixed(6));
			}else{
				self._lngInput.setText("");
			}
			if(lat){
				self._latInput.setText(Number(lat).toFixed(6));
			}else{
				self._latInput.setText("");
			}
		},
		
		setDistance : function(scope){
			var self = this;
			var label = scope.getLabel();
			self.queryDistance = label.substr(1,label.length - 2);
		},
		
		queryByDistance : function(){
			var self = this;
			if(self._lngInput.getText() === "" || self._latInput.getText() === ""){
				tp.utils.optionDialog("温馨提示","请先输入正确的经纬度或手动在地图上定位！");
				return;
			}
			if(!self._businessType.isSelected()){
				tp.utils.optionDialog("温馨提示","请选择业务类型！");
				return;
			}
			self.startBuffer = new Date().getTime();
			
			self._accessPointPanel._tableView.dm().clear();
			self._accessResDetailPanel._tableView.dm().clear();
			self._portDetailPanel._tableView.dm().clear();
			
			var longitude = Number(self._lngInput.getText());
			var latitude = Number(self._latInput.getText());
			var distance = Number(self.queryDistance);
			
			tp.utils.lock(self._formPane);
			Dms.Utils.bufferFeature(longitude, latitude, distance, function(geometries)
			{
				if(!geometries)
					return ;
				self.endBuffer = new Date().getTime();
				console.info("缓冲区分析时间(毫秒):"+ (self.endBuffer - self.startBuffer));
				
				var map = Dms.Default.tpmap.getMap();
				var graphView = Dms.Default.tpmap.getGraphView();
				graphView.dm().clear();
				
				for(var i = 0,len = geometries.length;i < len ;i++)
				{
					var geometry = geometries[i];
					var paths = geometry.rings;
					var shape = new ht.Shape();
					var points = new ht.List();
					var latLngPoints = new ht.List();
					var segments = new ht.List();
					for(var j=0;paths && j<paths.length;j++)
					{
						var path = paths[j];
						for(var k=0;k<path.length;k++)
						{
							latLngPoints.add(L.latLng(path[k][1],path[k][0]));
							var point = map.latLngToContainerPoint(L.latLng(path[k][1],path[k][0]));
							points.add(point);
							if(k == 0){
								segments.add(1);
							}else{
								segments.add(2);
							}
						}
					}
					shape.setPoints(points);
					shape.setSegments(segments);
					shape.a('latLng',latLngPoints);
					shape.s('shape.background', null);
					shape.s('shape.border.width', 1);
					shape.s('shape.pattern',[1,1]);
					shape.s('shape.border.color', 'blue');
//						shape.a("note", "查询缓存区域:周边"+distance+"米！");
//						shape.setName("查询缓存区域:周边"+distance+"米！");
					
					graphView.dm().add(shape);
					self.doPointQuery(geometry,longitude,latitude);
					self.loadExtentByGraphic(geometry);
					//不能选中
					graphView.dm().sm().setFilterFunc(function(data){
						if(data == shape){
							return false;
						}
						return true;
					});
				}
			});
		},
		
		//根据缓冲区查询资源
		doPointQuery : function(geometry,longitude,latitude){
			var self = this;
			var types = self.resType;
			var times = types.length;
			self.searchRes = [];
			
			for(var i = 0,len = types.length; i < len; i++){
				Dms.Utils.queryByGeometry([types[i]], geometry,null,Dms.Utils.addCallbackArgs(callback,[types[i]]));
			}
			function callback(features,type)
			{
				times -- ;
				for(var i = 0,len = features.length; i < len; i++){
					var attr = features[i].attributes;
					var obj = {};
					obj.CUID = attr.CUID;
					obj.NAME = attr.LABEL_CN;
					obj.TYPE = attr.CUID.split('-')[0];
					
					var p1 = new L.LatLng(self._latInput.getText(),self._lngInput.getText());
					var p2 = new L.LatLng(features[i].geometry.y,features[i].geometry.x);
					obj.DISTANCE = p1.distanceTo(p2).toFixed(2);
					self.searchRes.push(obj);
				}
				if(times == 0)
				{
					Dms.Utils.graphicLocateOnMap(self.searchRes);
					self.endGeometryQuery = new Date().getTime();
					console.info("缓冲区查询时间（毫秒）:"+(self.endGeometryQuery - self.endBuffer ));
					DmResExplorerAction.dealResExplore(self.searchRes,function(result){
						self.endResultQuery = new Date().getTime();
						console.info("资源查询时间（毫秒）:"+(self.endResultQuery - self.endGeometryQuery ));
						console.info("集客勘察总时间（毫秒）:"+(self.endResultQuery - self.startBuffer ));
						if(result){
							self._accessPointPanel._tableView.dm().clear();
							self._accessResDetailPanel._tableView.dm().clear();
							self._portDetailPanel._tableView.dm().clear();
							for(var i = 0; i < result.length; i++){
								if(result[i]){
									var data = new ht.Data();
									data._attrObject = result[i];
									self._accessPointPanel._tableView.dm().add(data);
								}
							}
						}
						tp.utils.unlock(self._formPane);
						self._tabView.select(0);
						
						var map = Dms.Default.tpmap.getMap();
						var paths = geometry.rings;
						var shape = new ht.Shape();
						var points = new ht.List();
						var latLngPoints = new ht.List();
						var segments = new ht.List();
						for(var j=0;paths && j<paths.length;j++)
						{
							var path = paths[j];
							for(var k=0;k<path.length;k++)
							{
								latLngPoints.add(L.latLng(path[k][1],path[k][0]));
								var point = map.latLngToContainerPoint(L.latLng(path[k][1],path[k][0]));
								points.add(point);
								if(k == 0){
									segments.add(1);
								}else{
									segments.add(2);
								}
							}
						}
						shape.setPoints(points);
						shape.setSegments(segments);
						shape.a('latLng',latLngPoints);
						shape.s('shape.background', null);
						shape.s('shape.border.width', 1);
						shape.s('shape.pattern',[1,1]);
						shape.s('shape.border.color', 'blue');
						
						var center = new ht.Node();
						center.setImage('COMMAND');
						center.a('latLng', new L.latLng(latitude, longitude));
                        center.s('body.color', 'red');
                        center.setPosition(map.latLngToContainerPoint(L.latLng(latitude, longitude)));
						
						var graphView = Dms.Default.tpmap.getGraphView();
						graphView.dm().add(center);
						graphView.dm().add(shape);
						//不能选中
						graphView.dm().sm().setFilterFunc(function(data){
							if(data == shape || data == center){
								return false;
							}
							return true;
						});
					});
				}
			}
		},
		
		loadExtentByGraphic : function(geometry){
			var tpmap = Dms.Default.tpmap,
				map = tpmap.getMap();
			var json = Terraformer.ArcGIS.parse(geometry);
			var latLngBounds = tpmap.getMaxBound([json]);
			map.fitBounds(latLngBounds);
		},
		
		geoQuery : function(){
			var self = this;
			self._addrComboBox.listDatas = new ht.DataModel();
			var value = self._addrComboBox.getValue();
			if(!value || !value.trim()){
				tp.utils.optionDialog("温馨提示", '请填写地理位置!');
				return;
			}
			tp.utils.lock(self._formPane);
			Dms.Utils.queryBaseMap(value, queryHandler);
			function queryHandler(results)
			{
				if(results.length > 0){
					for (var i = 0; i <results.length; i++){
						var obj = {};
						obj.CUID = (i+1)+"";
						obj.NAME = results[i].value;
						obj.graphic = results[i].geometry;
						
						var node = new ht.Data();
						node._attrObject = obj;
						node.setName(results[i].value);
						self._addrComboBox.listDatas.add(node);
					}
				}else{
					self._addrComboBox.listDatas.clear();
				}
				tp.utils.unlock(self._formPane);
				if(results.length > 0){
					self._addrComboBox.open();
				}
			}
		},
		
		queryAddrByCustomerName : function(){
			var self = this;
			var value = self._addrComboBox.getValue();
			if(!value || !value.trim()){
				tp.utils.optionDialog("温馨提示", '请填写客户名称!');
				return;
			}
			self._addrComboBox.listDatas = new ht.DataModel();
			tp.utils.lock(self._formPane);
			DmResExplorerAction.getUsersByName(value,function(results){
				if(results.length > 0){
					for (var i = 0; i <results.length; i++){
						var obj = results[i];
						var node = new ht.Data();
						node._attrObject = obj;
						node.setName(results[i].NAME);
						self._addrComboBox.listDatas.add(node);
					}
				}else{
					self._addrComboBox.listDatas.clear();
				}
				tp.utils.unlock(self._formPane);
				if(results.length > 0){
					self._addrComboBox.open();
				}
			});
		},
		
		queryAddrBySiteName : function(){
			var self = this;
			var value = self._addrComboBox.getValue();
			if(!value || !value.trim()){
				tp.utils.optionDialog("温馨提示", '请填写站点名称!');
				return;
			}
			//self._tablePane._tableView.dm().clear();
			self._addrComboBox.listDatas = new ht.DataModel();
			tp.utils.lock(self._formPane);
			Dms.Utils.queryByParams(["SITE"],"LABEL_CN LIKE '%" + value+ "%'",queryHandler);
			function queryHandler(features){
				if(features.length > 0){
					for(var i = 0; i < features.length; i++){
						var feature = features[i];
						if(feature){
							var obj = {
									CUID : feature.attributes.CUID,
									NAME:feature.attributes.LABEL_CN,
									LATITUDE:feature.geometry.y,
									LONGITUDE:feature.geometry.x
								};
								var node = new ht.Data();
								node._attrObject = obj;
								node.setName(obj.NAME);
								self._addrComboBox.listDatas.add(node);
						}
					}
				}else{
					self._addrComboBox.listDatas.clear();
				}
				tp.utils.unlock(self._formPane);
				if(features.length > 0){
					self._addrComboBox.open();
				}
			}
		},
		
		handleCallback : function(data){
			/*var self = this;
			if(data.a('graphic')){
				var graphic = data.a('graphic');
				self.setLngLat(graphic.x,graphic.y);
			}*/
		},
		
		show : function(){
			var self = this;
			self.setPosition(350, 20);
			document.body.appendChild(self.getView());
		},
		close : function(){
			var self = this,
				tpmap = Dms.Default.tpmap;
			document.body.removeChild(self.getView());
			tpmap.reset();
		}
	});
})(this,Object);