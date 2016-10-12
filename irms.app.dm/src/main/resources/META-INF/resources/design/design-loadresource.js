/**
 * 绘制背景地理信息类
 */
tp.Default.design = {};
tp.Default.design.Tools = {
	//加载地理信息
	loadGeoInfo:function(tpmap,layerIds){
		var self=this;
		self.tpmap = tpmap;
		var config=self.tpmap.getConfig();
		var identifyUrl = config.map.basemaps[0].url;
	    var  latLngBounds=self.tpmap.getMap().getBounds();
		var graphView = tpmap.getGraphView();
		
		var param={};
		param.text='';
		param.geometryType='esriGeometryEnvelope';
		param.geometry="{"+"xmin\:" + latLngBounds.getSouthWest().lng
		+ "\,ymin\:"+ latLngBounds.getSouthWest().lat
		+"\,xmax\:"+ latLngBounds.getNorthEast().lng
		+ "\,ymax\:" +latLngBounds.getNorthEast().lat+"\}";
		param.layers='all:'+layerIds.join(',');
		param.sr=4326;
		
		tp.utils.identify(identifyUrl, param, function (response) {
            if (response.error)
                alert("错误：" + response.error.message);
            else {
                var results = response.results;
                if (results.length > 0){
                	self._drawGeoOnMap(results);
                }
            };
        });
	},
	//绘制获取的geo对象 
	_drawGeoOnMap:function(resArr,geometryType)
    {
    	var tpmap=Dms.Default.tpmap,
    	    graphView = tpmap.getGraphView(),
    		map = tpmap.getMap();
    	graphView.dm().clear();
    	for(var i = 0; i<resArr.length; i++){
    		var resultGraphic = resArr[i];
    		var cuid = resultGraphic.attributes["唯一标识"] 
		    		|| resultGraphic.attributes["CUID"]
		    		|| resultGraphic.attributes["OBJECTID"];
    		var name = resultGraphic.attributes["中文标识"] 
		          ||resultGraphic.attributes["NAME"] 
		     	  ||resultGraphic.attributes["CNAME"]
		          ||resultGraphic.attributes["CAMNE"] 
	              || resultGraphic.attributes["LABEL_CN"]
	              || resultGraphic.attributes["GRID_NAME"];
    		var resultGeometryType = resultGraphic.geometryType || geometryType;
    		var layerId=resultGraphic.layerId;
    		var layerName=resultGraphic.layerName;
    		cuid=layerId+'-'+cuid;
    		if(resultGeometryType === "esriGeometryPoint")//如果是点
    		{
    			var point = map.latLngToContainerPoint(L.latLng(resultGraphic.geometry.y,resultGraphic.geometry.x));
    			//屏幕坐标需转换为ht的坐标
    			var node =  graphView.dm().getDataById(cuid);
    			if(node == null)
    			{
    				var latLng=new L.latLng(resultGraphic.geometry.y,resultGraphic.geometry.x);
    				node =  new ht.Node();
    				node.setId(cuid);
    				node.setName(name);
    				node.setPosition(point);
    				node.a('latLng',latLng);
    				node.a('layerId',layerId);
    				node.a('layerName',layerName);
    				node.s('bmClassId','GEO');
    				node.s('shape', 'circle');
    				node.s('shape.background', 'red');
    				node.s('shape.border.width', 1);		
    				node.setSize(2,2);
    				
    				graphView.dm().add(node);				

    			}
    		}else if(resultGeometryType === "esriGeometryPolyline")
    		{
    			var node =  graphView.dm().getDataById(cuid);
    			if (node==null){
	    			var paths = resultGraphic.geometry.paths;
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
	    					if(k==0)
	    						segments.add(1);
	    					else
	    						segments.add(2);
	    				}
	    			}
	    			shape.setId(cuid);
	    			shape.setName(name);
	    			shape.setPoints(points);
	    			shape.setSegments(segments);
	    			shape.a('latLng',latLngPoints);
	    			if(resultGeometryType === "esriGeometryPolyline")
	    				shape.s('shape.background', null);
	    			else
	    			shape.s('shape.background', 'red');
	    			shape.s('shape.border.width', 1);
	    			shape.s('shape.border.color', 'red');
	    			shape.a('layerId',layerId);
	    			shape.a('layerName',layerName);
	    			graphView.dm().add(shape);
    			}
    		}else if(resultGeometryType === "esriGeometryPolygon"){
    			var paths =resultGraphic.geometry.rings;
    			var shape = new ht.Shape();
    			var points = new ht.List();
    			var latLngPoints = new ht.List();
    			var segments = new ht.List();
    			for(var j=0;j<paths.length;j++)
    			{
    				var path = paths[j];
    				for(var k=0;k<path.length;k++)
    				{
    					latLngPoints.add(L.latLng(path[k][1],path[k][0]));
    					var point = map.latLngToContainerPoint(L.latLng(path[k][1],path[k][0]));
    					points.add(point);
    					if(k==0){
    						segments.add(1);
    					}else if (k=== (path.length -1)) {
    						segments.add(5);//closePath
    					} else {
    						segments.add(2);
    					}	
    				}
    			}
    			shape.setId(cuid);
    			//shape.setName(name);
    			shape.setPoints(points);
    			shape.setSegments(segments);
    			shape.a('latLng',latLngPoints);
    			shape.s('shape.background', null);//'rgba(255,0,0,0.9)');
    			shape.s('shape.border.width', 1);
    			shape.s('shape.border.color', 'red');//'yellow');  	
    			shape.a('layerId',layerId);
    			shape.a('layerName',layerName);
    			graphView.dm().add(shape);
    		}
    	}
    }		
		/*
	//query空间查询背景图层数据  范围
	queryGeoInfo:function(tpmap,layerIds){
		var self=this;
		self.tpmap = tpmap;
		var config=self.tpmap.getConfig();
		var _dynamicUrl = config.map.basemaps[0].url;
		//var layerIds=["2","1","19","20","21","22","23","24","25","26","28","29","30","31","32","33","34","35","36","37","38"];
		//layerIds=["20","21","22","23","24","25","26","28","29","30","31","32","33","34","35","36","37","38"];//面状测试, ,"34" "35","36"
	    var  latLngBounds=self.tpmap.getMap().getBounds();
		var graphView = tpmap.getGraphView();
		graphView.dm().clear();
		//获取图层对应objectIds
		for (var i=0;i<layerIds.length;i++){
			var layerid=layerIds[i];
			var url1=_dynamicUrl+"/"+layerIds[i]+'/query';
			var param={};
			param.text='';
			param.geometry="{"+"xmin\:" + latLngBounds.getSouthWest().lng
			+ "\,ymin\:"+ latLngBounds.getSouthWest().lat
			+"\,xmax\:"+ latLngBounds.getNorthEast().lng
			+ "\,ymax\:" +latLngBounds.getNorthEast().lat+"\}";
			
			param.inSR=4326;
			param.spatialRel="esriSpatialRelIntersects";//esriSpatialRelContains
			param.relationParam='';
			param.objectIds='';
			//param.outFields='CUID,OBJECTID,LABEL_CN';
			param.outFields='';
			param.maxAllowableOffset=1;
			param.returnIdsOnly=true;
			param.returnGeometry=true;
			param.outSR=4326;
			var types = ["esriGeometryPoint","esriGeometryMultipoint","esriGeometryPolyline","esriGeometryPolygon","esriGeometryEnvelope"];
			
			for(var j=0;j<types.length;j++){
			param.geometryType= types[j];
			L.esri.get(url1, param, function(response){
				if(response.error && (response.error.code === 499 || response.error.code === 498)) {
					console.info('地图服务'+url1+'无法访问！');
				}else{
					console.info(response);
					self.drawLayerGeometry(layerid,response.objectIds);
				}
		    });
		}
		}
	},	
	//暂时定为 面
	drawQueryResult:function(results){
    	if (results.feature==null){
			return 
		};		
		var geoType=results.geometryType;
		var labelCn=results.displayFieldName;
		if (results.feature.geometry.rings!=null){
			geoType="esriGeometryPolygon";
			
		}else{
			if(results.feature.geometry.paths!=null){
				geoType="esriGeometryPolyline";
			}else{
				geoType="esriGeometryPoint";
			}
		}
		//geoType="esriGeometryPolygon";
		//geoType="esriGeometryPolyline";
		//geoType="esriGeometryPoint";
		labelCn="CNAME";
		
		//this.drawOnMap(geoType,results.feature,labelCn);
		this.draw(geoType,results.feature);
    },
	//根据图层id,objectIds绘制
	drawLayerGeometry:function(layerId,objectIds){
		var self=this;
		var config= self.tpmap.getConfig();
		var _dynamicUrl = config.map.basemaps[0].url;
		var url=_dynamicUrl+"/"+layerId+'/';
		var self=this;
		console.info('layerId='+layerId+',objectIds.length='+objectIds);
		if(objectIds)
		for (var i=0;i<objectIds.length;i++){
			var url1=url+objectIds[i];
			var param={};
			L.esri.get(url1, param, function(response){
				if(response.error && (response.error.code === 499 || response.error.code === 498)) {
					console.info('地图服务'+url1+'无法访问！');
				}else{
					console.info(response);
					self.drawQueryResult(response);
				}
		    });		
		}
	},
	
	//绘制点
	drawPoint:function(resArr,dm){
		var self=this;
		var map = self.tpmap.getMap();
		var point = map.latLngToContainerPoint(L.latLng(resArr.geometry.y,resArr.geometry.x));
		//屏幕坐标需转换为ht的坐标
		var cuid = resArr.attributes["唯一标识"] || resArr.attributes["CUID"]|| resArr.attributes["OBJECTID"];
		var name=resArr.attributes["CNAME"] ||resArr.attributes["CAMNE"] ||resArr.attributes["NAME"] ;
		var node =  dm.getDataById(cuid);
		if(node == null)
		{
			var latLng=new L.latLng(resArr.geometry.y,resArr.geometry.x);
			node =  new ht.Node();
			node.setId(cuid);
			node.setName(name);
			//node.setImage(className);
			node.s('shape', 'circle');
			node.s('shape.background', 'red');
			node.setSize(8,8);    				
			node.setPosition(point);
			//node.setAttr('bmClassId',className);
			node.a('latLng',latLng);    				
			dm.add(node);			
		 }
	},	
	//绘制线
	drawPolyLine:function(resArr,dm){
		var self=this;
		var map = self.tpmap.getMap();
		var cuid = resArr.attributes["唯一标识"] || resArr.attributes["CUID"]|| resArr.attributes["OBJECTID"];
		var name=resArr.attributes["NAME"]||resArr.attributes["CNAME"] ||resArr.attributes["CAMNE"]  ;
		var paths = resArr.geometry.paths;
		var shape = new ht.Shape();
		var points = new ht.List();
		var latLngPoints = new ht.List();
		var segments = new ht.List();
		for(var j=0;j<paths.length;j++)
		{
			var path = paths[j];
			for(var k=0;k<path.length;k++)
			{
				latLngPoints.add(L.latLng(path[k][1],path[k][0]));
				var point = map.latLngToContainerPoint(L.latLng(path[k][1],path[k][0]));
				points.add(point);
				if(k==0){
					segments.add(1);
				}else {
					segments.add(2);
				}	
			}
		}
		//shape.setId(cuid);
		shape.setPoints(points);
		shape.setSegments(segments);
		shape.a('latLng',latLngPoints);
		shape.s('shape.background', null);
		shape.s('shape.border.width', 1);
		shape.s('shape.border.color', 'red');//'yellow');    			
		dm.add(shape);
	},
	
	//绘制面
	drawPolygon:function(resArr,dm){
		var self=this;
		var map = self.tpmap.getMap();
		var cuid = resArr.attributes["唯一标识"] || resArr.attributes["CUID"]|| resArr.attributes["OBJECTID"];
		var name=resArr.attributes["CNAME"] ||resArr.attributes["CAMNE"] ||resArr.attributes[fieldName] ;
		var paths =resArr.geometry.rings;
		var shape = new ht.Shape();
		var points = new ht.List();
		var latLngPoints = new ht.List();
		var segments = new ht.List();
		for(var j=0;j<paths.length;j++)
		{
			var path = paths[j];
			for(var k=0;k<path.length;k++)
			{
				latLngPoints.add(L.latLng(path[k][1],path[k][0]));
				var point = map.latLngToContainerPoint(L.latLng(path[k][1],path[k][0]));
				points.add(point);
				if(k==0){
					segments.add(1);
				}else if (k=== (path.length -1)) {
					segments.add(5);//closePath
				} else {
					segments.add(2);
				}	
			}
		}
		shape.setId(cuid);
		shape.setPoints(points);
		shape.setSegments(segments);
		shape.a('latLng',latLngPoints);
		shape.s('shape.background', null);//'rgba(255,0,0,0.9)');
		shape.s('shape.border.width', 1);
		shape.s('shape.border.color', 'red');//'yellow');  			
		dm.add(shape);
	},
	
	//具体绘制函数选择
	draw :function(geoType,data){ 
		var self=this;
		var dm = self.tpmap.getGraphView().dm();
		var drawObject={
	    	'esriGeometryPoint':function(data,dm) {
	    	self.drawPoint(data,dm);
		},    		
		'esriGeometryPolyline':function(data,dm) {
			self.drawPolyLine(data,dm);
		},
		'esriGeometryPolygon':function(data,dm) {
			self.drawPolygon(data,dm);
		}  	
	 };	
		drawObject[geoType](data,dm);
		tp.Default.DrawObject._drawState = 100;
	},
	*/

    
};