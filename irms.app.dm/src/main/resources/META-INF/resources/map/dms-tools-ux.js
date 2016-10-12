$importjs(ctx+'/map/grid/dms-panel-gridinfo.js');
$importjs(ctx+'/map/grid/dms-panel-gridresource.js');
$importjs(ctx+'/map/grid/dms-panel-gridsitelist.js');
/**
 * 不规则网格工具：liuchao
 */
(function(window,Object,undefined){
	"use strict";
	
	Dms.Tools.Grid = {
			editingShape : null,
			drawGrid : function(){
				//县节点
				var resObj = tp.Default.OperateObject.contextObject;
				if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}
				if(!resObj){
					return ;
				}
				
				//绘制图形
				var tpmap = Dms.Default.tpmap,
				gv = tpmap.getGraphView(),
				map = tpmap.getMap();
			
				tpmap.clearContextMenu();
				tpmap.reset();
		    	tp.Default.DrawObject._drawState = 10018;
		    	tpmap.getGraphView().getView().style.cursor = 'url('+ctx+'/resources/cursor/cursor_black.cur), pointer';
		    	var curInterator = new CreateVectorInteractor(gv, tpmap.getView());
		    	gv.setInteractors(new ht.List([
		    		new ht.graph.SelectInteractor(gv),
		    		new ht.graph.EditInteractor(gv),
		    		new ht.graph.MoveInteractor(gv),
		    		new ht.graph.DefaultInteractor(gv),
		    		curInterator
		    	]));
		    	
		    	curInterator._vectorType = 'shape';
		    	//绘制时的颜色
		    	curInterator._fillStyle = 'rgba(0,255,0,0.3)';
		    	curInterator.onCreateStarted = function(node) {
		    	};
		    	curInterator.onCreateCompleted = function(node,index) {
		    		tp.Default.DrawObject._drawState = 1;
		    		tpmap.getGraphView().getView().style.cursor = 'default';
		    		if(node instanceof ht.Shape)
		    		{
		    			var latlngs = new ht.List();
						var pxpoints = node.getPoints();
						for ( var j=0;j<pxpoints.size();j++) {
							var pp=pxpoints.get(j);
							var x = gv.tx();
							var y = gv.ty();
							var point = map.containerPointToLatLng( new L.Point(x+pp.x, y+pp.y));
							latlngs.add(point);
		                };
						node.a('latLng',latlngs);
						node.setClosePath(true);
						node.s('shape.background','rgba(0,255,0,0.3)');
						node.onPropertyChanged = function(e){
							if(e.property === 'points')
							{
								var latlngs = new ht.List();
								var pxpoints = node.getPoints();
								for ( var j=0;j<pxpoints.size();j++) {
									var pp = pxpoints.get(j);
									var x = gv.tx();
									var y = gv.ty();
									var point = map.containerPointToLatLng( new L.Point(x+pp.x, y+pp.y));
									latlngs.add(point);
				                };
								node.a('latLng',latlngs);
							}
						};
						var panel = new Dms.widget.GridInfoPanel(null,resObj.cuid,node);
						panel.show();
		    		}
		    	};
			},
			modifyGridInfo : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				/*if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}*/
				if(!resObj){
					return ;
				}
				
				var panel = new Dms.widget.GridInfoPanel(resObj.cuid);
				panel.show();
			},
			startModifyShape : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!resObj){
					return ;
				}
				Dms.Tools.Grid.editingShape = null;
				var geometry = resObj.geometry;
				var map = Dms.Default.tpmap.getMap(),
					graphView = Dms.Default.tpmap.getGraphView();
				
				Dms.Default.tpmap.reset();//停止闪烁
				//绘制shape
				var paths = geometry.paths || geometry.rings;
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
				shape.setPoints(points);
				shape.setSegments(segments);
				shape.a('latLng',latLngPoints);
				graphView.dm().add(shape);
				graphView.setEditable(true);
				graphView.dm().sm().setSelection(shape);
				//使其可编辑
				graphView.setEditableFunc(function(data){
					if(data == shape)
						return true;
					return false;
				});
				Dms.Tools.Grid.editingShape = {
						cuid:resObj.cuid,
						shape:shape};
			},
			saveModifyShape : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!resObj){
					return ;
				}
				
				if(!Dms.Tools.Grid.editingShape)
				{
					tp.utils.optionDialog('温馨提示','请先开始编辑形状！');
					return;
				}else
				{
					var cuid = Dms.Tools.Grid.editingShape.cuid;
					var shape = Dms.Tools.Grid.editingShape.shape;
					if(cuid != resObj.cuid)
					{
						tp.utils.optionDialog('温馨提示','请先开始编辑形状！');
						return;
					}
					var tpmap = Dms.Default.tpmap,
						map = tpmap.getMap(),
						gv = tpmap.getGraphView(),
						latlngs = new ht.List(),
						pxpoints = shape.getPoints(),
						geometry = {'rings':[[]]};
					gv.setEditable(false);
					for ( var j=0;j< pxpoints.size();j++) {
						var pp= pxpoints.get(j);
						var x = gv.tx();
						var y = gv.ty();
						var point = map.containerPointToLatLng( new L.Point(x+pp.x, y+pp.y));
						geometry.rings[0].push([point.lng,point.lat]);
						latlngs.add(point);
		            };
		            geometry.rings[0].push(geometry.rings[0][0]);
					shape.a('latLng',latlngs);
					//后台保存
					MapGridAction.updateGeometry({"CUID":cuid,"SHAPE":JSON.stringify(geometry)},false,function(result){
						if(result && result == "TRUE"){
							tp.utils.optionDialog('温馨提示','修改成功！');
							//刷新地图
							Dms.Default.tpmap.refreshMap();
							Dms.Default.tpmap.reset();
						}else{
							tp.utils.optionDialog('温馨提示','修改失败！');
							Dms.Default.tpmap.reset();
						}
					});
				}
			},
			replaceRegion : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}
				if(!resObj){
					return ;
				}
				
				Dms.Tools.showConfirmDialog({
					content : '确定要将此业务区形状替换为行政区域边界吗?',
					action : function(item,e){
						if(item.id === 'ok'){
							MapGridAction.replaceRegionShape(resObj.cuid,function(result){
								if(result == "TRUE"){
									tp.utils.optionDialog('温馨提示','替换成功！');
									//刷新网格树
									Dms.Default.gridPanel.refreshTree();
									
									//刷新地图
									Dms.Default.tpmap.refreshMap();
									Dms.Default.tpmap.reset();
								}else{
									tp.utils.optionDialog('温馨提示','替换失败！');
								}
							});
						}
						this.hide();
					}
				});
				
			},
			importRegion : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}
				if(!resObj){
					return ;
				}
				
				Dms.Tools.showConfirmDialog({
					content : '确定要导入行政区域边界生成业务区吗?',
					action : function(item,e){
						if(item.id === 'ok'){
							MapGridAction.importRegionShape(resObj.cuid,resObj.label,function(data){
									if(data["result"] == "TRUE"){
										var gridInfo = data["grid"];
										var panel = new Dms.widget.GridInfoPanel(gridInfo["CUID"]);
										panel.show();
									}else{
										tp.utils.optionDialog('温馨提示','生成业务区失败！');
										return;
									}
							});
						}
						this.hide();
					}
				});
				
			},
			deleteGrid : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				/*if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}*/
				if(!resObj){
					return ;
				}
				
				Dms.Tools.showConfirmDialog({
					content : '确定要删除该业务区吗?',
					action : function(item,e){
						if(item.id === 'ok'){
			            		MapGridAction.deleteGrid(resObj.cuid,function(result){
									if(result === "TRUE"){
										tp.utils.optionDialog('温馨提示','删除成功！');
										//刷新网格树
										Dms.Default.gridPanel.refreshTree();
										//刷新地图
										Dms.Default.tpmap.refreshMap();
										Dms.Default.tpmap.reset();
									}else{
										tp.utils.optionDialog('温馨提示','删除失败: '+result);
									}
								});
			            	}
			            this.hide();
					}
				});
				
			},
			splitGrid : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!resObj){
					return ;
				}
				//被切图形
				var geometry = resObj.geometry;
				//绘制直线
				var tpmap = Dms.Default.tpmap,
					gv = tpmap.getGraphView(),
					map = tpmap.getMap();
				
				tpmap.clearContextMenu();
		    	tp.Default.DrawObject._drawState = 10086;
		    	var curInterator = new CreateVectorInteractor(gv, tpmap.getView());
		    	gv.setInteractors(new ht.List([
		    		new ht.graph.SelectInteractor(gv),
		    		new ht.graph.EditInteractor(gv),
		    		new ht.graph.MoveInteractor(gv),
		    		new ht.graph.DefaultInteractor(gv),
		    		curInterator
		    	]));
		    	
		    	curInterator._vectorType = 'shape';
		    	curInterator.onCreateStarted = function(node) {
		    	};
		    	curInterator.onCreateCompleted = function(node,index) {
		    		if(node instanceof ht.Shape)
		    		{
		    			var latlngs = new ht.List();
						var pxpoints = node.getPoints();
						var cutter = {'paths':[[]]};
						for ( var j=0;j<pxpoints.size();j++) {
							var pp=pxpoints.get(j);
							var x = gv.tx();
							var y = gv.ty();
							var point = map.containerPointToLatLng( new L.Point(x+pp.x, y+pp.y));
							cutter.paths[0].push([point.lng,point.lat]);
							latlngs.add(point);
		                };
						node.a('latLng',latlngs);
						
						Dms.Tools.cutFeature([geometry],cutter,callback);
		    		}
		    		tp.Default.DrawObject._drawState = 1;
		    	};
				function callback(geometries,geometryType)
				{
					if(geometries && geometries.length > 0)
					{
						Dms.Tools.showConfirmDialog({
							content : '确定将业务区拆分吗？',
							action : function(item,e){
								if(item.label === '确定'){
									var geoArr = [];
									for(var i=0 ;i < geometries.length ; i++)
									{
										geoArr.push(JSON.stringify(geometries[i]));
									}
									MapGridAction.splitGrid(resObj.cuid,geoArr,function(result){ 
										if(result === "TRUE"){
											tp.utils.optionDialog('温馨提示','业务区拆分成功!');
											//刷新网格树
											Dms.Default.gridPanel.refreshTree();
											//刷新地图
											Dms.Default.tpmap.refreshMap();
											Dms.Default.tpmap.reset();
										}else{
										
											tp.utils.optionDialog('温馨提示','业务区拆分失败：'+result);
										}
									});
								}else{
									Dms.Default.tpmap.reset();
								}
								this.hide();
							}
						});
					}
				}
			},
			combineGrid : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				if(!resObj){
					return ;
				}
				//被合并图形
				var geometry = resObj.geometry;
				//选取经纬度
				tp.Default.DrawObject._movePointState = 0;
				tp.Default.DrawObject._drawState = 10016;
				var tpmap = Dms.Default.tpmap;
				tpmap.getGraphView().getView().style.cursor = 'url('+ctx+'/resources/cursor/cursor_black.cur), pointer';
				tpmap.getMap().on('click', function(e) {
					if (tp.Default.DrawObject._drawState == 10016){ 
						tp.Default.DrawObject._drawState = 0;
						//identify查询网格
						Dms.Tools.indentifyByLatLng('grid', e.latlng.lng, e.latlng.lat, identifyHandler);
		            }
				});
				function identifyHandler(results){
					tpmap.getGraphView().getView().style.cursor = 'default';
					if(results && results.length > 0)
					{
						var feature = results[0];
						var targetCuid = feature.attributes.CUID;
						var targetLabel = feature.attributes.GRID_NAME;
						var targetGeometry = feature.geometry;
						if(targetCuid == resObj.cuid)
						{
							tp.utils.optionDialog('温馨提示','合并业务区不能选择相同业务区！');
						}else
						{
							//网格定位
							Dms.Tools.graphicLocateOnMap([resObj.cuid,targetCuid]);
							//确认合并
							Dms.Tools.showConfirmDialog({
								content : '确定要合并业务区【' + resObj.label + '】和【' + targetLabel + '】吗?',
								contentPadding : 25,
								action : function(item,e){
									if(item.id === 'ok'){
										MapGridAction.combineGrid(resObj.cuid,targetCuid,function(result){
											if(result === "TRUE"){
												tp.utils.optionDialog('温馨提示','业务区合并成功！');
												//刷新网格树
												Dms.Default.gridPanel.refreshTree();
												//刷新地图
												Dms.Default.tpmap.refreshMap();
												Dms.Default.tpmap.reset();
											}else{
												tp.utils.optionDialog('温馨提示','业务区合并失败：'+result);
											}
										});
									}else{
										Dms.Default.tpmap.reset();
									}
									this.hide();
								}
							});
						}
						
					}
				}
			},
			getRelatedSite : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				/*if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}*/
				if(!resObj){
					return ;
				}
				
				var panel = new Dms.widget.GridSiteListPanel(resObj.cuid);
				panel.show();
			},
			
			getRelatedRes : function(){
				var resObj = tp.Default.OperateObject.contextObject;
				/*if(!(Dms.Default.ContextMenuTarget instanceof ht.graph.GraphView)){
					resObj = Dms.Default.ListContextObject;
				}*/
				if(!resObj){
					return ;
				}
				
				var panel = new Dms.widget.GridResourcePanel(resObj.cuid);
				panel.show();
			}
			
	};
})(this,Object);