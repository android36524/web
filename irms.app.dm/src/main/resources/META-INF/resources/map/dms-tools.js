$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');
$importjs(ctx+'/map/uuid.js');
Dms.Tools = {
    //新增点
    addMapPoint: function(bmClassId) {
        var self = this,
            graphView = Dms.Default.tpmap.getGraphView();
        if (!graphView) return;
        Dms.Default.tpmap.reset();
        graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
        tp.Default.DrawObject._drawState = 2;
        tp.Default.DrawObject._drawPointClass = bmClassId;
        graphView.setEditable(false);
        Dms.Default.tpmap.clearContextMenu();
    },
    //新增线
    addMapLine: function(bmClassId, pointType) {
        var self = this,
            tpmap = Dms.Default.tpmap,
            config = Dms.Default.tpmap._config;
        var graphView = Dms.Default.tpmap.getGraphView();
        if (!graphView) return;
        var map = Dms.Default.tpmap.getMap();
        var oldArray = new Array();
        Dms.Default.tpmap.clearContextMenu();
        Dms.Default.tpmap.reset();
        tp.Default.DrawObject._drawState = 3;
        tp.Default.DrawObject._drawPointClass = pointType;
        tp.Default.DrawObject._drawLineClass = bmClassId;
        var curInterator = tp.Default.OperateObject.curInterator = new CreateVectorInteractor(graphView, Dms.Default.tpmap.getView());
        graphView.setInteractors(new ht.List([
            new ht.graph.SelectInteractor(graphView),
            new ht.graph.EditInteractor(graphView),
            new ht.graph.MoveInteractor(graphView),
            new ht.graph.DefaultInteractor(graphView),
            curInterator
        ]));
        graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
        curInterator._vectorType = 'series';
        //var lineClassName = Dms.className2ResName[bmClassId];
        curInterator.onCreateStarted = function(node) {
            graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
            if (node instanceof ht.Node) {
                node.setImage(tp.Default.DrawObject._drawPointClass);
                var position = node.getPosition(),
                    x = position.x + graphView.tx(),
                    y = position.y + graphView.ty(),
                    latLng = map.containerPointToLatLng(new L.Point(x, y));
                node.a('latLng', latLng);
            }
            if (node instanceof ht.Edge) {
                // node.setName(lineClassName+(tp.Default.DrawObject._drawLineList.length+1));
            }
        };
        curInterator.onMove = function(e){
        	var edge = curInterator._edge;
        	if(edge)
        	{
        		edge.s('edge.pattern',[5,5]);
        		var targetNode = edge.getTarget(),
    				sourceNode = edge.getSource();
    			if (targetNode && sourceNode) {
    				//起点经纬度
    				var startLatlng = map.containerPointToLatLng(new L.Point(sourceNode.getPosition().x, sourceNode.getPosition().y));
    				//止点经纬度
    				var endLatlng = map.containerPointToLatLng(new L.Point(targetNode.getPosition().x, targetNode.getPosition().y));
    				var distance = startLatlng.distanceTo(endLatlng);
    				distance = L.Util.formatNum(distance, 2);
    				edge.a("LENGTH",distance+"");
    				edge.setName(distance + "米");
    			}
        	}
        };
        curInterator.onCreateCompleted = function(node, index) {
            var pointClass = tp.Default.DrawObject._drawPointClass;
            var pointClassName = Dms.className2ResName[pointClass];

            console.info("onCreateCompleted");
            node.s("shape.border.color", "#8ab5ed");
            node.s("shape.background", null);
            if (node && node instanceof ht.Node) {
                var position = node.getPosition(),
                    x = position.x + graphView.tx(),
                    y = position.y + graphView.ty(),
                    latLng = map.containerPointToLatLng(new L.Point(x, y));
                if(pointClass === 'FIBER_JOINT_BOX'){
                	var kind = tp.Default.DrawObject._kind;
                	node.a('KIND',kind);
                	if(kind === '2'){
                		pointClassName = '光终端盒';
                	}
                }
                
                node.setImage(pointClass);
                node.a("bmClassId", pointClass);
                node.setName(pointClassName+(tp.Default.DrawObject._drawPointList.length+1));
                node.a('latLng', latLng);
                node.a('LONGITUDE',latLng.lng+"");
                node.a('LATITUDE',latLng.lat+"");
                node.setSize(12, 12);
                if (node) {
                    //在些还需要加上类型判断，比如管道只支持人手井、光交接箱、光分纤箱、接入点等类型
                    var latLng = node.getAttr("latLng");
                    var identifyUrl = config.map.reslayers[0].url;
                    var lineClass = tp.Default.DrawObject._drawLineClass,
                        pointLayers = Dms.getPointTypeByResName[lineClass],
                        layerIds = Dms.Tools.getLayerIdsByTypeName(pointLayers);
                    var times = 1;
                    //如果使用已有点
                    if(tp.Default.DrawObject._reuseMapPoint){
                        tp.utils.identify(identifyUrl, {
                        	geometry: latLng.lng + ',' + latLng.lat,
        	            	layers : layerIds
        	            }, identifyHandler);
                    }
                    function identifyHandler(response) {
                    	times--;
                        if (response.error)
                            tp.utils.optionDialog("错误", response.error.message);
                        if(times == 0){
                            var loc = response.results;
                            if (loc) {
                                if (loc.length == 0) {
                                    node.setName(pointClassName + (tp.Default.DrawObject._drawPointList.length - oldArray.length));
                                } else if(loc.length == 1){
                            		var resultGraphic = loc[0];
                            		var cuid = resultGraphic.attributes["唯一标识"] || resultGraphic.attributes["CUID"] || resultGraphic.attributes["OBJECTID"];
                                    var name = resultGraphic.attributes["中文标识"] || resultGraphic.attributes["LABEL_CN"] || resultGraphic.attributes["GRID_NAME"];
                                    var lt = new L.LatLng(resultGraphic.geometry.y, resultGraphic.geometry.x);
                                    var bId = cuid.split("-")[0];
                                    node.setName(name);
                    	            node.a('bmClassId',bId);
                    	            node.a('CUID', cuid);
                    	            node.setId(cuid);
                    	            node.setTag(cuid);
                    	            node.a('LABEL_CN',name);
                    	            node.a('latLng', lt);
                    	            node.a('LONGITUDE',lt.lng+"");
                    				node.a('LATITUDE',lt.lat+"");
                    				node.s('body.color', 'red');
                    	            //需要将他的ht上的位置刷到和原来的一样，不然创建时有两个点
                    	            var latLngToContainerPoint = map.latLngToContainerPoint(lt);
                    	            node.setPosition(latLngToContainerPoint);
                    	            node.a("RELATED_DISTRICT_CUID", resultGraphic.attributes["RELATED_DISTRICT_CUID"]);
                    	            node.a("ISOLD", "0"); //0代表是老数据
                    	            node.setImage(resultGraphic.layerName); //已有点使用已有点的图片
                                    oldArray.push(node);
                                } else if(loc.length > 1){
                                	for (var i = 0; i < loc.length; i++) {
                                		var contextmenu = tpmap._contextmenu;
                                    	self.locatePointResouceMenu(loc, contextmenu, latLng,node);
                                    	oldArray.push(node);
                                	}
                                }
                            } else {
                                node.setName(pointClassName + (tp.Default.DrawObject._drawPointList.length - oldArray.length));
                            }
                        }
                    }
                }
                tp.Default.DrawObject._drawPointList.push(node);
            }
            if (node && node instanceof ht.Edge) {
                node.a("bmClassId", bmClassId);
                var targetNode = node.getTarget(),
                    sourceNode = node.getSource();
                if (targetNode && sourceNode) {
                    //在新增加线更换两头点设施类型时，前面的点不能换图片
                    // sourceNode.setImage(pointClass);
                    targetNode.setImage(pointClass);
                    //起点经纬度
                    var startLatlng = sourceNode.getAttr('latLng');
                    //止点经纬度
                    var endLatlng = targetNode.getAttr('latLng');
                    var distance = startLatlng.distanceTo(endLatlng);
                    distance = L.Util.formatNum(distance, 2);
                    node.setName(distance + "米");
                }
                tp.Default.DrawObject._drawLineList.push(node);
            }
            graphView.getView().style.cursor = 'default';

        };
        curInterator.onSeriesCreateCompleted = function(nodes, edges) {
            graphView.setEditable(false);
        };
    },
    
    locatePointResouceMenu : function(results, contextmenu, latlng, node){
		var self = this;
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
	        } 
	        //是否有格式类型的资源
	        if (resultGraphic.geometryType == "esriGeometryPoint") {
	        	pointNum++;
	        	item = {
	        			label: label,
	        			locateObject: resultGraphic,
	                    cuid: cuid,
	                    icon : className, //图标展示
	                    className: className,
	                    action: function(item) {
	                        self.locateOnMap([item.locateObject],node);
	                    }
	        	};
	        	if (item !== null) {
	        		menuItems.push(item);
	        	}
	        };
	    }
	    tp.utils.wholeUnLock();
	    if (pointNum > 1) { //多点资源显示菜单	
	    	contextmenu.setItems(menuItems);
	        var point = map.latLngToContainerPoint(latlng);
	        contextmenu.show(point.x, point.y);
	    }
	},
    //定位至地图上高亮显示
    locateOnMap : function (resArr, node) {
    	var tpmap = Dms.Default.tpmap,
			map = tpmap.getMap();
        for (var i = 0; i < resArr.length; i++) {
            var resultGraphic = resArr[i];
            var cuid = resultGraphic.attributes["唯一标识"] || resultGraphic.attributes["CUID"] || resultGraphic.attributes["OBJECTID"];
            var name = resultGraphic.attributes["中文标识"] || resultGraphic.attributes["LABEL_CN"] || resultGraphic.attributes["GRID_NAME"];
            var resultGeometryType = resultGraphic.geometry.y ? 'esriGeometryPoint' : (resultGraphic.geometry.paths ? 'esriGeometryPolyline' : 'esriGeometryPolygon');
            if (resultGeometryType === "esriGeometryPoint") { //如果是点
            	if (node) {
            		//屏幕坐标需转换为ht的坐标
                    var latLng = new L.latLng(resultGraphic.geometry.y, resultGraphic.geometry.x);
    				var bId = cuid.split("-")[0];
    				
    				node.setName(name);
    	            node.a('bmClassId',bId);
    	            node.a('CUID', cuid);
    	            node.setId(cuid);
    	            node.setTag(cuid);
    	            node.a('LABEL_CN',name);
    	            node.a('latLng', latLng);
    	            node.a('LONGITUDE',latLng.lng+"");
    				node.a('LATITUDE',latLng.lat+"");
    				node.s('body.color', 'red');
    	            //需要将他的ht上的位置刷到和原来的一样，不然创建时有两个点
    	            var latLngToContainerPoint = map.latLngToContainerPoint(latLng);
    	            node.setPosition(latLngToContainerPoint);
    	            node.a("RELATED_DISTRICT_CUID", resultGraphic.attributes["RELATED_DISTRICT_CUID"]);
    	            node.a("ISOLD", "0"); //0代表是老数据
    	            node.setImage(resultGraphic.layerName); //已有点使用已有点的图片
            	}
            }
        }
    },
    
    //完成绘制点
    drawPointComplete: function() {
        var self = this;
        //增加点设施方法，在增加线设施时，类型可能是多个，className可能需要处理（node中有bmclassid属性）
        var dialog = new ht.widget.Dialog();
        dialog.setConfig({
            title: "温馨提示",
            titleAlign: "left",
            height: 120,
            width : 160,
            closable: true,
            draggable: true,
            content: "<p>要保存新增的点资源吗？</p><p>确定：进行数据保存！</p><p>重命名：进行重新命名！</p>",
            buttons: [{
                label: "确定",
                className: "button-yes"
            }, {
                label: "重命名",
                className: "button-cancel"
            }],
            buttonsAlign: "center",
            action: function(item, e) {
                if (item.label === "确定") {
                    console.info('保存新增加的点设施！');
                    dialog.hide();
                    tp.utils.wholeLock();
                    self._addPoints();
                } else {
                	self.batchName();
                }

            }
        });
        dialog.show();
    },
    _addPoints: function() {
        var className = tp.Default.DrawObject._drawPointClass;
        var dataList = tp.Default.DrawObject._drawPointList;
        var count = dataList.length;
        var disCuid = dms.Default.user.distCuid;
        var obj = "[";
        for (var i = 0; i < dataList.length; i++) {
            var labelCn = dataList[i]._name;
            var pointBmClassId = dataList[i].getAttr("bmClassId");
            var longitude = dataList[i].getAttr("latLng").lng;
            var latitude = dataList[i].getAttr("latLng").lat;
            obj = obj + "{";
            obj = obj + "\"BM_CLASS_ID\":" + "\"" + pointBmClassId + "\",";
            if(pointBmClassId === 'FIBER_JOINT_BOX'){
            	var kind = tp.Default.DrawObject._kind;
            	if(kind){
            		obj = obj + "\"KIND\":" + "\"" + kind + "\",";
            	}
            }
            obj = obj + "\"LABEL_CN\":" + "\"" + labelCn + "\",";
            obj = obj + "\"RELATED_DISTRICT_CUID\":" + "\"" + disCuid + "\",";
            if (className != "ACCESSPOINT") {
                obj = obj + "\"REAL_LONGITUDE\":" + "\"" + longitude + "\",";
                obj = obj + "\"REAL_LATITUDE\":" + "\"" + latitude + "\",";
            }
            obj = obj + "\"LONGITUDE\":" + "\"" + longitude + "\",";
            obj = obj + "\"LATITUDE\":" + "\"" + latitude + "\"";
            obj = obj + "}";
            if (i < count - 1)
                obj = obj + ",";
        }
        obj = obj + "]";
        var p = new Object();
        p['points'] = obj;
        //后台进行保存
        var bmClassId = className;
        var scene = Dms.Default.scene;//场景名
        var segGroupCuid = Dms.Default.segGroupCuid;//单位工程

        $.ajax({
            url: ctx + '/rest/MapRestService/addPoints/' + bmClassId + "/" + scene + "/"+segGroupCuid + "?time=" + new Date().getTime(),
            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
            type: 'POST',
            data: p,
            dataType: 'json',
            success: function(data) {
            	Dms.Default.tpmap.refreshMap();
            	Dms.Default.tpmap.reset();
            	if(data){
            		var error = data.error;
            		if(error){
            			tp.utils.optionDialog("错误提示", error);
            		}else{
            			tp.utils.optionDialog("温馨提示", '点设施增加成功!');
            		}
            	}
                tp.utils.wholeUnLock();
            },
            error: function(e) {
            	tp.utils.optionDialog("错误提示", '资源名称重复，增加点资源失败!<br/>请先批量命名！');
            	Dms.Default.tpmap.reset();
                tp.utils.wholeUnLock();
            }
        });
    },
	//完成绘制线
    drawLineComplete: function() {
        var self = this;
        var pointList = tp.Default.DrawObject._drawPointList;
        if (pointList) {
            var pointCount = pointList.length;
            if (pointCount > 1) { //只有一个点时，不允许保存
                self._createLinePropertyInputView(function(result) {
                    self._addDuctLines(result);
                });
            } else {
                tp.utils.optionDialog("错误提示", "保存线设施至少需要两个点!");
            }
        } else {
            tp.utils.optionDialog("错误提示", "保存线设施至少需要两个点!");
        }
    },
    _createLinePropertyInputView: function(callback) {

        var param = {},
            lineClass = tp.Default.DrawObject._drawLineClass,
            lineClassName = Dms.branchClassName2ResName[lineClass],
            lineSystemClassName = Dms.systemClassName2ResName[lineClass],
            lineArray = tp.Default.DrawObject._drawLineList,
            branchName = "",
            systemName = "";
        	
        if (lineArray) {
            var sourceNode = lineArray[0].getSource();
            var origName = sourceNode._name;

            var targetNode = lineArray[lineArray.length - 1].getTarget();
            var destName = targetNode._name;
            branchName = origName + "--" + destName + lineClassName;
            systemName = origName + "--" + destName + lineSystemClassName;
        }
        //根据增加的不同类型弹出不同的对话框
        var dm_system = Dms.selectSystemNameResName[lineClass];
        var systemFormPane = new ht.widget.FormPane();
        var branchNameInput = tp.utils.createInput('branchName', 'input', branchName);
        param.branchName = branchNameInput.value;
//        var systemInput = tp.utils.createActionInput(dm_system, systemName, function(data) {
//            param.relatedSystemCuid = data.cuid;
//            param.relatedSystemLabelCn = data.labelCn;
//        });
        param.relatedSystemLabelCn = systemName;
        var systemInput = dms.Tools.createAddTextSelectInput(dm_system, systemName,"related_system", function(data) {
            if(data){
            	param.relatedSystemCuid = data.CUID;
                param.relatedSystemLabelCn = data.LABEL_CN;
            }
        });
        var templateInput = dms.Tools.createAddTextSelectInput('service_dict_dm.DM_TEMPLATE', '','related_template', function(data) {
            if(data){
            	param.relatedTemplateCuid = data.CUID;
            	param.relatedTemplateLabelCn = data.LABEL_CN;
            }
        });
        if(lineClass && lineClass === 'WIRE_SEG'){
        	
        		var purComboBox = new ht.widget.ComboBox();
                purComboBox.setLabels(['未知','集客','家客','集客家客']);
                purComboBox.setValues([0,1,2,3]);
                purComboBox.setValue(0);
                param.specialPurpose=0;
                purComboBox.onValueChanged = function(){
                	param.specialPurpose = purComboBox.getValue();
                };
                
                var olevelComboBox = new ht.widget.ComboBox();
                olevelComboBox.setLabels(['未知','一干','二干','本地一级','本地二级','本地三级','金牌','银牌','铜牌','标准','国干']);
                olevelComboBox.setValues([0,1,2,3,4,5,6,7,8,9,10]);
                olevelComboBox.setValue(5);
                param.olevel=5;
                olevelComboBox.onValueChanged = function(){
                	param.olevel = olevelComboBox.getValue();
                };
        	
        }
//        var projectInput = tp.utils.createActionInput('service_dict_dm.DM_PROJECT_MANAGEMENT', '', function(data) {
//            param.relatedProjectCuid = data.cuid;
//        });
        var projectInput = dms.Tools.createAddTextSelectInput('service_dict_dm.DM_PROJECT_MANAGEMENT', '','related_project', function(data) {
            if(data){
            	param.relatedProjectCuid = data.CUID;
            	param.relatedProjectLabelCn = data.LABEL_CN;
            }
        });
        systemFormPane.addRow(['名称', branchNameInput], [60, 0.1]);
        systemFormPane.addRow(['所属系统', systemInput], [60, 0.1]);
        if(lineClass && lineClass === 'WIRE_SEG'){
        	systemFormPane.addRow(['专线用途', purComboBox], [60, 0.1]);
            systemFormPane.addRow(['重要性', olevelComboBox], [60, 0.1]);
        }
        //先把模板引用去掉
 //     systemFormPane.addRow(['模板名称:', templateInput], [60, 0.1]);
        systemFormPane.addRow(['所属工程:', projectInput], [60, 0.1]);
        systemFormPane.getResult = function() {
            var result = param;
            return result;
        };
        dms.params = param;
        var dialog = new ht.widget.Dialog({
            width: 400,
            height: 200,
            draggable: true,
            closable: true,
            title: "选择",
            content: systemFormPane,
            buttons: [{
                label: "确定",
                action: function() {
                	
                }
            }, {
                label: "取消",
                action: function() {
                    this.hide();
                }
            }],
            action: function(item, e) {
                if (item.label == '确定') {
                    callback(systemFormPane.getResult());
                    tp.utils.wholeLock();
                } else {
                }
                this.hide();
            }
        });
        dialog.onHidden = function() {
        	dms.params = {};
        };
        //    dialog.onShown = function() {
        ////        dialog.getView().querySelector('.PickInput').focus();
        //    };
        dialog.show();
        return systemFormPane;
    },
    //增加线设施
    _addDuctLines: function(param) {
        var linlist = tp.Default.DrawObject._drawLineList,
        count = linlist.length,
        lineBmClassId = null,
        projectCuid = param.relatedProjectCuid,
        relatedSystemCuid = param.relatedSystemCuid,
        relatedBranchName = param.branchName,
        disCuid = dms.Default.user.distCuid,
        relatedSystemLabelCn = param.relatedSystemLabelCn;
        
        if (count > 0) {
            lineBmClassId = linlist[0].getAttr('bmClassId');
        }
        var specialPurpose = null,
        olevel = null;
        if(lineBmClassId && lineBmClassId ==='WIRE_SEG'){
        	specialPurpose = param.specialPurpose;
        	olevel = param.olevel;
        	var systemLevel = 5;
            var str=Dms.Tools.getSpecialPurposeAndOlevel(systemLevel,specialPurpose,olevel);
            if(str){
            	tp.utils.optionDialog("温馨提示",str);
            	tp.utils.wholeUnLock();
            	return;        	
            }
        }
        var lineJson = "[";
        for (var i = 0; i < linlist.length; i++) {
        	var segLength = 0.0;
            var edgeNode = linlist[i];
            var edgeName = edgeNode._name;
            var sourceNode = edgeNode.getSource();
            var sourceLatLng = sourceNode.getAttr('latLng');
            var sLongitude = sourceLatLng.lng;
            var slatitude = sourceLatLng.lat;
            var sourceName = sourceNode._name;
            var sourceType = sourceNode.getAttr('bmClassId');
            var sourceIsOld = sourceNode.getAttr('ISOLD');
            var sourceCuid = sourceNode.getAttr('CUID');
            var origPointKind = null;
            if(sourceType === 'FIBER_JOINT_BOX'){
            	origPointKind = sourceNode.getAttr('KIND');
            }
            var targetNode = edgeNode.getTarget();
            var targetLatLng = targetNode.getAttr('latLng');
            var tLongitude = targetLatLng.lng;
            var tlatitude = targetLatLng.lat;
            var targetName = targetNode._name;
            var targetType = targetNode.getAttr('bmClassId');
            var targetIsOld = targetNode.getAttr('ISOLD');
            var targetCuid = targetNode.getAttr('CUID');
            var destPointKind = null;
            if(targetType === 'FIBER_JOINT_BOX'){
            	destPointKind = targetNode.getAttr('KIND');
            }
            segLength = edgeNode.getAttr('LENGTH');
            edgeName = sourceName + "--" + targetName;
            lineJson = lineJson + "{";
            if (relatedSystemCuid) {
                lineJson = lineJson + "\"RELATED_SYSTEM_CUID\":" + "\"" + relatedSystemCuid + "\",";
            }
            lineJson = lineJson + "\"RELATED_SYSTEM_LABEL_CN\":" + "\"" + relatedSystemLabelCn + "\",";
            if (projectCuid) {
                lineJson = lineJson + "\"RELATED_PROJECT_CUID\":" + "\"" + projectCuid + "\",";
            }
            lineJson = lineJson + "\"RELATED_DISTRICT_CUID\":" + "\"" + disCuid + "\",";
            if(lineBmClassId ==='WIRE_SEG'){
            	if(specialPurpose || specialPurpose===0){
                	 lineJson = lineJson + "\"SPECIAL_PURPOSE\":" + "\"" + specialPurpose + "\",";
                }
                if(olevel || olevel ===0 ){
                	 lineJson = lineJson + "\"OLEVEL\":" + "\"" + olevel + "\",";
                }
            }
            
            lineJson = lineJson + "\"LENGTH\":" + "\"" + segLength + "\",";
            lineJson = lineJson + "\"BRANCH_NAME\":" + "\"" + relatedBranchName + "\",";
            lineJson = lineJson + "\"LABEL_CN\":" + "\"" + edgeName + "\",";
            lineJson = lineJson + "\"ORIG_POINT_NAME\":" + "\"" + sourceName + "\",";
            lineJson = lineJson + "\"ORIG_POINT_TYPE\":" + "\"" + sourceType + "\",";
            lineJson = lineJson + "\"ORIG_POINT_KIND\":" + "\"" + origPointKind + "\",";
            
            lineJson = lineJson + "\"ORIG_POINT_LONGITUDE\":" + "\"" + sLongitude + "\",";
            lineJson = lineJson + "\"ORIG_POINT_LATITUDE\":" + "\"" + slatitude + "\",";
            lineJson = lineJson + "\"ORIG_POINT_ISOLD\":" + "\"" + sourceIsOld + "\",";
            if (sourceCuid) {
                lineJson = lineJson + "\"ORIG_POINT_CUID\":" + "\"" + sourceCuid + "\",";
            }

            lineJson = lineJson + "\"DEST_POINT_NAME\":" + "\"" + targetName + "\",";
            lineJson = lineJson + "\"DEST_POINT_TYPE\":" + "\"" + targetType + "\",";
            lineJson = lineJson + "\"DEST_POINT_KIND\":" + "\"" + destPointKind + "\",";
            lineJson = lineJson + "\"DEST_POINT_LONGITUDE\":" + "\"" + tLongitude + "\",";
            lineJson = lineJson + "\"DEST_POINT_LATITUDE\":" + "\"" + tlatitude + "\",";
            lineJson = lineJson + "\"DEST_POINT_ISOLD\":" + "\"" + targetIsOld + "\",";
            if (targetCuid) {
                lineJson = lineJson + "\"DEST_POINT_CUID\":" + "\"" + targetCuid + "\",";
            }
            lineJson = lineJson + "\"INDEX_IN_BRANCH\":" + "\"" + (i + 1) + "\"";

            lineJson = lineJson + "}";
            if (i < count - 1)
                lineJson = lineJson + ",";
        }
        lineJson = lineJson + "]";
        var ductLine = new Object();
        ductLine['ductlines'] = lineJson;
        var scene = Dms.Default.scene;//场景名
        var segGroupCuid = Dms.Default.segGroupCuid;//单位工程
        
        //后台进行保存
        //var bmClassId = className;
        $.ajax({
            url: ctx + '/rest/MapRestService/addDuctLines/' + lineBmClassId +"/"+scene+"/"+segGroupCuid+"?time=" + new Date().getTime(),
            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
            type: 'POST',
            data: ductLine,
            dataType: 'json',
            success: function(data) {
            	Dms.Default.tpmap.refreshMap();
            	Dms.Default.tpmap.reset();
            	if(data){
            		var error = data.error;
            		if(error){
            			tp.utils.optionDialog("错误提示", '增加线资源失败!');
            		}else{
            			tp.utils.optionDialog("温馨提示", '线设施增加成功!');
            		}
            	}
                tp.utils.wholeUnLock();
            },
            error: function(e) {
            	tp.utils.optionDialog("温馨提示", '线设施增加失败!');
                tp.utils.wholeUnLock();
                Dms.Default.tpmap.reset();
            }
        });
    },
    getSpecialPurposeAndOlevel : function(systemLevel,specialPurpose,olevel){  	
    	var str=null;
    	if(systemLevel == 5){//系统级别是本地接入
		if(specialPurpose==0){//如果专线用途是未知，重要性只能是本地三级或者三级
			if(olevel !=4 && olevel !=5){
				//提示
				str = "系统级别是本地接入，专线用途是未知时，重要性只能是本地二级或本地三级!";
			}
		}else if(specialPurpose==1 || specialPurpose==3){//如果专线用途是集客，重要性只能是金牌、银牌、铜牌、标准
			if(olevel !=6 && olevel !=7 && olevel !=8 && olevel !=9){
				//提示
				str = "系统级别是本地接入，专线用途是集客或者集客家客时，重要性只能是金牌、银牌、铜牌、标准!";
			}
		}else if(specialPurpose==2){//如果专线用途是家客，重要性只能是未知
			if(olevel !=0){
				//提示
				str = "系统级别是本地接入，专线用途是家客时，重要性只能是未知!";
			}
		}
	}else{//系统级别是非本地接入，专线用途只能是未知
		if(specialPurpose != 0){
			//提示
			str = "系统级别是非本地接入时，专线用途只能是未知!";
		}else{
			if(systemLevel==0){//如果系统级别是未知，重要性也只能是未知
				if(olevel !=0){
					//提示
					str = "系统级别是未知时，重要性只能是未知!";
				}
			}else if(systemLevel==1){//如果系统级别是省际，重要性也只能是一干
				if(olevel !=1){
					//提示
					str = "系统级别是省际时，重要性只能是一干!";
				}
			}else if(systemLevel==2){//如果系统级别是省内，重要性也只能是二干
				if(olevel !=2){
					//提示
					str = "系统级别是省际时，重要性只能是二干!";
				}
			}else if(systemLevel==3 || systemLevel==4){//如果系统级别是本地骨干、本地汇聚，重要性也只能是本地一级
				if(olevel !=3){
					//提示
					str = "系统级别是本地骨干、本地汇聚时，重要性只能是本地一级!";
				}
			}
		}
	}
    return str;
},
	//批量命名
	batchName : function(){
		//取当前绘制的点类型
		var className = tp.Default.DrawObject._drawPointClass;
		var resName = Dms.className2ResName[className];
		var batchNameView = new Dms.BatchNameView(resName);
		var topoType = batchNameView.topoType;
		var pointList = tp.Default.DrawObject._drawPointList;
		var map = null;
		if(pointList){
			map=dms.Tools.map(pointList);
		}
	    // console.info(changeNameView.getView());    
		var dialog = new ht.widget.Dialog({
	            title: "<html><font size=2>"+topoType+"_批量命名</font></html>",
	            width: 300,
	            height: 180,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            buttons: [
	                {
	                    label: "确定"
	                },
	                {
	                    label: "取消"
	                }
	            ],
	            content: batchNameView,
	            action: function(button, e) {
	                if (button.label === "确定") {
	                	//弹出来的界面输入后得到的值
	                	var obj = batchNameView.getBatchNames();
	                	var pName = obj.prefix;
	                	var startIndex = Number(obj.origNumber);
	                	var suffix = obj.suffix;
	                	var serialNumber = obj.serialNumber;
	                	if(map){
	                		var pList = map[className];
	                		if(pList){
	                			//var count = 0;
	                			var countMap ={};
	    	                	for(var i=0;i<pList.length;i++){
	    	                		var point = pList[i];
	    	                		var isOldNode = point.getAttr("ISOLD");
	    	                		if(isOldNode != "0"){//0代表是老数据不重新命名
	    	                			var kind = point.a('KIND');
	    	                			var count = countMap[kind];
	    	                			if(count == null){
	    	                				count = 0;
	    	                				countMap[kind] = count;
	    	                			}
    	                    			DWREngine.setAsync(false);//同步
    	                    			var inputNum = startIndex+count,returnName = serialNumber;
    	                    			GenerateCuidAction.doCompareName(serialNumber,inputNum,function(data){
    	                    				if(data){
    	                    					returnName = data;
    	                    				}
    	                    			});
    	                    			DWREngine.setAsync(true);//同步
    	                    			var newname = pName+returnName+suffix;
    	                    			if(className === 'FIBER_JOINT_BOX'){
    	                    				var kindType = "光终端盒";
    	                    				if(kind === '2'){
    	                    					newname = pName+returnName+kindType;
    	                    				}
    	                    				//tp.Default.DrawObject._kind = '2';这个是批量命名选择接头盒还是终端盒
    	                    				if(kind == tp.Default.DrawObject._kind){
    	                    					point.setName(newname);//设置到当前node上
    	                    				}
    	                    			}else{
    	                    				point.setName(newname);//设置到当前node上
    	                    			}
    	                    			//var newname = pName + (obj.serialNumber+(startIndex+count))+obj.suffix;
    	                    			countMap[kind]++;
	    	                		}
	    	                	}
	                		}
	                	}
	                }
	                dialog.hide();
	            }
	        });
	   dialog.onTransitionEnd = function(operation) {
	        if (operation === "show") {
	        } else {
	            dialog.dispose();
	        }
	    };
	    dialog.show();
		
	},
	//封装map
	map : function(pointList){
		var map = {};
		for(var i=0;i<pointList.length;i++){
    		var point = pointList[i];
    		var pClassName = point.getAttr('bmClassId');
			var ls = map[pClassName];
			if(ls == null){
				ls = new Array();
				map[pClassName] = ls;
			}
			ls.push(point);
		}
		return map;
	},
	//撤销
	editCancel : function(){
		var curInterator = tp.Default.OperateObject.curInterator;
		if(curInterator){
			curInterator.undo();
			tp.Default.DrawObject._drawPointList.pop();
			if(tp.Default.DrawObject._drawLineList)
				tp.Default.DrawObject._drawLineList.pop();
		}
	},
	//选择同类点
	selectSamePoint : function(obj){
		var tpmap = Dms.Default.tpmap,
			graphView = tpmap.getGraphView();
		var bmClassId = obj.bmClassId;
		graphView.dm().each(function(data){
			if(bmClassId === data.getAttr('bmClassId'))
				graphView.dm().sm().appendSelection(data);
		});
	},
	setSeriesNode : function(sid){
		var selectIndex = 0;
		var seriesNodes = tp.Default.OperateObject.curInterator._seriesEdges;
		seriesNodes.each(function(serie){
			if(sid ==serie.getId()){
				selectIndex = seriesNodes.indexOf(serie);
			}
		});
		return selectIndex;
	},
	//直接插入点
	insertPointDirect:function(e){
		var lineselected = tp.Default.OperateObject.contextObject,
			id = lineselected._id,
		    className = tp.Default.DrawObject._drawPointClass,
		    resName = Dms.className2ResName[className],
		    latlng = tp.Default.OperateObject.contextMenuLocate;
		var selectIndex = 0;
		if(lineselected instanceof ht.Edge){
			selectIndex = dms.Tools.setSeriesNode(id);
		}
		var middleNode = new ht.Node();
		middleNode.setName(resName+(tp.Default.DrawObject._drawPointList.length+1));
		middleNode.setImage(className);
		var tpmap = Dms.Default.tpmap,
			graphView = tpmap.getGraphView(),
			map = tpmap.getMap();
		var point = map.latLngToContainerPoint(latlng);
		middleNode.setPosition(point);
		middleNode.a('bmClassId',className);
		middleNode.a('latLng',latlng);
		middleNode.a('LONGITUDE',latlng.lng+"");
		middleNode.a('LATITUDE',latlng.lat+"");
		graphView.dm().add(middleNode);
		// tp.Default.DrawObject._drawPointList.push(middleNode);
		var linlist = tp.Default.DrawObject._drawLineList;
		var lineClassName = tp.Default.DrawObject._drawLineClass;
		var index = 0;
		var middleEdgeNode = new ht.Edge();
		var sPointNodeId = null;
		//lineselected此处可以直接用这个，需要取出其所在集合中的索引位置
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var edgeID = edgeNode._id;
			if(id == edgeID){
				index = i;
				break;
			}
		}
		var sourceNode = lineselected._source;
		var sourceName = sourceNode._name;
		sPointNodeId = sourceNode._id;
		middleEdgeNode.setSource(sourceNode);
		middleEdgeNode.setTarget(middleNode);
		
		var result = Math.generateCUID(lineClassName);
		middleEdgeNode.a('CUID', result);
		middleEdgeNode.setId(result);
		middleEdgeNode.setTag(result);
				
		middleEdgeNode.s('edge.offset',2);
		middleEdgeNode.a('bmClassId',lineClassName);
		// middleEdgeNode.setName(sourceName+"--"+middleNode.getName());
		lineselected.setSource(middleNode);
		// var targetNode = lineselected._target;
		// var targetName = targetNode._name;
		// edgeNode.setName(middleNode.getName()+"--"+targetName);
		graphView.dm().add(middleEdgeNode);
		linlist.splice(index,0,middleEdgeNode);
		var pointList = tp.Default.DrawObject._drawPointList;
		var pointIndex = 0;
		for(var i=0;i<pointList.length;i++){
			var pointNode = pointList[i];
			var pid = pointNode._id;
			if(sPointNodeId == pid){
				pointIndex = i;
				break;
			}
		}
		pointList.splice(pointIndex+1,0,middleNode);
		tp.Default.OperateObject.curInterator._seriesEdges.add(middleEdgeNode,selectIndex);
		//为了直接插入排序，在此新加一遍
		dms.Tools.dataMoveTo(pointList,graphView.dm(),true);
		dms.Tools.dataMoveTo(linlist,graphView.dm());
		/*graphView.dm().clear();
		Dms.Tools.setSorePointAndLine(pointList,graphView.dm());
		Dms.Tools.setSorePointAndLine(linlist,graphView.dm());*/
		
		//需要增加距离
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var sNode = edgeNode._source;
			var eNode = edgeNode._target;
			var sLatlng = sNode.getAttr('latLng');
			var eLatlng = eNode.getAttr('latLng');
			
			var distance = sLatlng.distanceTo(eLatlng);
			distance = L.Util.formatNum(distance, 2);
	    	edgeNode.setName(distance+"米");
	    	edgeNode.a('LENGTH',distance+"");
		}
	},
	
	//等分插入点
	insertPointAliquots:function (e){
		var tpmap = Dms.Default.tpmap,
			graphView = tpmap.getGraphView(),
			map = tpmap.getMap();      	
		var lineselected = tp.Default.OperateObject.contextObject;
		var id = lineselected._id;
		var selectIndex = 0;
		if(lineselected instanceof ht.Edge){
			selectIndex = dms.Tools.setSeriesNode(id);
		}
		var className = tp.Default.DrawObject._drawPointClass;
		var resName = Dms.className2ResName[className];
		
		var inputCrossCount = 1;
		var str=prompt("等分插入点设施:"+resName,"请在此输入要插入的点的个数");
		var re = /^[1-9]+[0-9]*]*$/;
		if(str == null){
			alert("未输入数值,请输入正整数");
			return;
		}
		if (!re.test(str))  
		{  
	       	alert("输入的值必须是正整数");
	       	return;
		}else{
			inputCrossCount = parseInt(str);
		}
		
		var linlist = tp.Default.DrawObject._drawLineList;
		var lineClassName = tp.Default.DrawObject._drawLineClass;
		var index = 0;
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var edgeID = edgeNode._id;
			if(id == edgeID){
				index = i;
				break;
			}
		}
		
		var sourceNode = lineselected._source;
		var targetNode = lineselected._target;
		//起点经纬度
		var startLatlng = sourceNode.getAttr('latLng');
		var startLongitude = startLatlng.lng;
		var startLatitude = startLatlng.lat;
		
		//止点经纬度
		var endLatlng = targetNode.getAttr('latLng');
		var endLongitude = endLatlng.lng;
		var endLatitude = endLatlng.lat;
		
		var pointList = new Array();
		for(var i=0;i<inputCrossCount;i++){
			var middleEdgeNode = new ht.Edge();
			var middleNode = new ht.Node();
			middleNode.setImage(className);
			middleNode.setName(resName+(tp.Default.DrawObject._drawPointList.length+1+i));
			middleNode.a('bmClassId',className);
			
			middleEdgeNode.setSource(sourceNode);
			middleEdgeNode.setTarget(middleNode);
			
			var result = Math.generateCUID(lineClassName);
			middleEdgeNode.a('CUID', result);
			middleEdgeNode.setId(result);
			middleEdgeNode.setTag(result);
					
			middleEdgeNode.s('edge.offset',2);
			middleEdgeNode.a('bmClassId',lineClassName);
			
			sourceNode = middleNode;
			//将新段从选择的段的起点的索引位置增加新加的段
			linlist.splice(index+i,0,middleEdgeNode);
			//原来的选择这个段的起点设为最后一个新加点的位置
			lineselected.setSource(sourceNode);
			
			graphView.dm().add(middleEdgeNode);
			tp.Default.OperateObject.curInterator._seriesEdges.add(middleEdgeNode,selectIndex+i);
			pointList.push(middleNode);
		}
		// var targetName = targetNode._name;
		
		var segs = pointList.length + 1;
		var latStep = (endLatitude - startLatitude) / segs;
		var lonStep = (endLongitude - startLongitude) / segs;
		
		for (var i = 0; i < pointList.length; i++) {
	        var point = pointList[i];
	        var newPointLati = startLatitude + latStep * (i + 1);
	        var newPointLongi = startLongitude + lonStep * (i + 1);
	        var pointLatlng = new L.latLng(newPointLati,newPointLongi);
	        var pointPostion = map.latLngToContainerPoint(pointLatlng);
	        
	        point.a('latLng',pointLatlng);
	        point.a('LONGITUDE',newPointLongi+"");
	        point.a('LATITUDE',newPointLati+"");
	        point.setPosition(pointPostion);
	        graphView.dm().add(point);
		}
		
		var points = tp.Default.DrawObject._drawPointList;
		for(var i=0;i<pointList.length;i++){
			//新增点位置：当前选择的段的index+1+i
			points.splice(selectIndex+1+i,0,pointList[i]);
		}
		
		//为等分插入排序
		dms.Tools.dataMoveTo(points,graphView.dm(),true);
		dms.Tools.dataMoveTo(linlist,graphView.dm());
		//需要增加距离
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var sNode = edgeNode._source;
			var eNode = edgeNode._target;
			var sLatlng = sNode.getAttr('latLng');
			var eLatlng = eNode.getAttr('latLng');
			
			var distance = sLatlng.distanceTo(eLatlng);
			distance = L.Util.formatNum(distance, 2);
	    	edgeNode.setName(distance+"米");
	    	edgeNode.a('LENGTH',distance+"");
		}
	},
	dataMoveTo : function(arr,dm,isPoint){
		var len = arr.length;
		for(var i=0;i<len-1;i++){
			var dmone,dmtwo;
			if(isPoint){
				dmone = dm.getDataById(arr[i].getId());
				dmtwo = dm.getDataById(arr[i+1].getId());
			}else{
				//线可能是在加到dm中后修改的setId设置为cuid，可能无效，需要通过
				dmone = dm.getDataByTag(arr[i].getId());
				dmtwo = dm.getDataByTag(arr[i+1].getId());
			}
			//dm.getDatas()用这个取索引不正确，因为这个只是个链接，并不变顺序
			var dindex = dm.getSiblings(dm.getDatas().get(0)).indexOf(dmone);
//			var dindex = dm.getDatas().indexOf(dmone);
			if(dmone && dmtwo){
				dm.moveTo(dmtwo,dindex+1);
			}
		}
	},
	//"等距插入点",
	insertPointIsometric: function (e){
		var lineselected = tp.Default.OperateObject.contextObject;
		var id = lineselected._id;
		var selectIndex = 0;
		if(lineselected instanceof ht.Edge){
			selectIndex = dms.Tools.setSeriesNode(id);
		}
		var tpmap = Dms.Default.tpmap,
			graphView = tpmap.getGraphView(),
			map = tpmap.getMap();
		var className = tp.Default.DrawObject._drawPointClass;
		var resName = Dms.className2ResName[className];
		var inputCrossCount = 50;
		//输入的距离，单位是米
		var str=prompt("等距插入点设施:"+resName+"\n"+"间距单位米(M)","请在此输入");
		var re = /^[1-9]+[0-9]*]*$/;
		if(str == null){
			alert("未输入数值,请输入正整数");
			return;
		}
		if (!re.test(str))  
		{  
	       	alert("输入的值必须是正整数");
	       	return;
		}else{
			inputCrossCount = parseInt(str);
		}
		if(inputCrossCount<20){
			tp.utils.optionDialog("温馨提示","输入距离不能小于20！");
			return;
		}
	    //对弹出的对话框返回的值，需要增加整型判断
		var linlist = tp.Default.DrawObject._drawLineList;
		var lineClassName = tp.Default.DrawObject._drawLineClass;
		var index = 0;
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var edgeID = edgeNode._id;
			if(id == edgeID){
				index = i;
				break;
			}
		}
		
		var sourceNode = lineselected._source;
		var targetNode = lineselected._target;
		//起点经纬度
		var startLatlng = sourceNode.getAttr('latLng');
		var startLongitude = startLatlng.lng;
		var startLatitude = startLatlng.lat;
		
		//止点经纬度
		var endLatlng = targetNode.getAttr('latLng');
		var endLongitude = endLatlng.lng;
		var endLatitude = endLatlng.lat;
		
		var distance = startLatlng.distanceTo(endLatlng);
		distance = L.Util.formatNum(distance, 2);
		var fixedRatio = 1 / (distance / inputCrossCount);
		var count = parseInt(distance / inputCrossCount);
	    var pointList = new Array();
	    
	    var points = tp.Default.DrawObject._drawPointList;
	    for(var i=0;i<count;i++){
			var middleEdgeNode = new ht.Edge();
			var middleNode = new ht.Node();
			middleNode.setImage(className);
			middleNode.setName(resName+(points.length+1+i));
			middleNode.a('bmClassId',className);
			
			var lonStep = (endLongitude - startLongitude);
			var latStep = (endLatitude - startLatitude);
			var x = startLongitude + lonStep * fixedRatio * (i + 1);
			var y = startLatitude + latStep * fixedRatio * (i + 1);
	
			var pointLatlng = new L.latLng(y,x);
	        var pointPostion = map.latLngToContainerPoint(pointLatlng);
	        var distance = endLatlng.distanceTo(pointLatlng);
	        distance = L.Util.formatNum(distance, 2);
	        if(distance<=20){
				break;
			}
	        middleNode.a('latLng',pointLatlng);
	        middleNode.a('LONGITUDE',pointLatlng.lng+"");
	        middleNode.a('LATITUDE',pointLatlng.lat+"");
	        middleNode.setPosition(pointPostion);
	        
			middleEdgeNode.setSource(sourceNode);
			middleEdgeNode.setTarget(middleNode);
			
			var result = Math.generateCUID(lineClassName);
			middleEdgeNode.a('CUID', result);
			middleEdgeNode.setId(result);
			middleEdgeNode.setTag(result);
					
			middleEdgeNode.s('edge.offset',2);
			middleEdgeNode.a('bmClassId',lineClassName);
	    
			sourceNode = middleNode;
			//将新段从选择的段的起点的索引位置增加新加的段
			linlist.splice(index+i,0,middleEdgeNode);
			//原来的选择这个段的起点设为最后一个新加点的位置
			lineselected.setSource(sourceNode);
			graphView.dm().add(middleEdgeNode);
			graphView.dm().add(middleNode);
			pointList.push(middleNode);
			tp.Default.OperateObject.curInterator._seriesEdges.add(middleEdgeNode,selectIndex+i);
		}
		
		for(var i=0;i<pointList.length;i++){
			points.splice(selectIndex+1+i,0,pointList[i]);
		}
		
		//为了等距插入排序，在此新加一遍
		dms.Tools.dataMoveTo(points,graphView.dm(),true);
		dms.Tools.dataMoveTo(linlist,graphView.dm());
		/*graphView.dm().clear();
		Dms.Tools.setSorePointAndLine(points,graphView.dm());
		Dms.Tools.setSorePointAndLine(linlist,graphView.dm());*/
		//需要增加距离
		for(var i=0;i<linlist.length;i++){
			var edgeNode = linlist[i];
			var sNode = edgeNode._source;
			var eNode = edgeNode._target;
			var sLatlng = sNode.getAttr('latLng');
			var eLatlng = eNode.getAttr('latLng');
			
			var distance = sLatlng.distanceTo(eLatlng);
			distance = L.Util.formatNum(distance, 2);
	    	edgeNode.setName(distance+"米");
	    	edgeNode.a('LENGTH',distance+"");
		}
	},
	
	//打开Ext win 修改点和线设施属性页
	openModifyExtWin:function(){
					var viewParam={
							'cuid':tp.Default.OperateObject.contextObject.cuid,
					      	'labelCn':tp.Default.OperateObject.contextObject.label
					};
				    var className=viewParam.cuid.split('-')[0];
					if (className == 'UP_LINE'){
						className = 'UPLINE';
					}
					if(className =='FIBER_JOINT_BOX'){
						var kind = tp.Default.DrawObject._kind;
						if(kind && kind == '2'){
							className = 'FIBER_TERMINAL_BOX';
						}
					}
					var  url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_'+className+'&loadData=true&hasQuery=false';
					FrameHelper.openUrl(ctx + url + '&cuid=' + viewParam.cuid,viewParam.labelCn );
	},
	//TODO显示剖面及面板图等
	showSectionView:function(viewParam){
		//cuid,mapType,mapName
		var className = viewParam.cuid.split('-')[0];
		var url="code="+viewParam.mapType+"&resId="+viewParam.cuid+"&resType="+className+"&token="
				+viewParam.segGroupCuid+"/"+viewParam.singleProjectCuid+"/"+viewParam.districtCuid;
		
		//GetServiceParamAction.getUrlByServerName("TOPO",function(data){
			//var url = data+"/topo/index.do?code=ManhleSectionTopo&resId="+cuid+"&resType=MANHLE&clientType=html5";
			var url2=ctx+"/topo/index.do?"+url+"&clientType=html5";
//			var iframe = document.createElement('iframe');
//			iframe.src=url2;
//			iframe.id=viewParam.cuid+'_'+viewParam.mapType;
//			viewParam.content=iframe;
//			tp.utils.showDialogView(viewParam);
			FrameHelper.openUrl(url2,viewParam.mapName);
		//});
	},
	
	//Ht dialog中打开url
	showSectionUrlView:function(viewParam){
		var className = viewParam.cuid.split('-')[0];
		var url=viewParam.url;
		var iframe = document.createElement('iframe');
		iframe.src=url2;
		iframe.id=viewParam.cuid+'_'+viewParam.mapType;
		viewParam.content=iframe;
		tp.utils.showDialogView(viewParam);
	},
	
	//
	getIsOperate : function(isDesigner, cuid){
		var isFlag = true;//可操作
		if(isDesigner){
			if("陕西" === dms.Default.user.systemDistrictName){
	      		DWREngine.setAsync(false);
	      		var segGroupCuid = dms.designer.segGroupCuid;
	      		QuerySeggroupResAction.getResListByCuid(cuid,segGroupCuid,{
	        		callback : function(data){
	        			isFlag = data;
	        		}
	      		});
	      		DWREngine.setAsync(true);
	      		if(!isFlag){//false时跨单位操作了
	      			tp.utils.optionDialog('温馨提示','资源不在该工单下，不可操作！');
	      		}
	      	}
		}
		return isFlag;
    },
	
	//光缆段和光缆系统右键“拆除承载”,code代表当前选中的线cuid
	doDeleteLayedRelation : function(code,isDesigner){
      	var isFlag = Dms.Tools.getIsOperate(isDesigner, code);
      	if(isFlag){
    		var dialog = new ht.widget.Dialog();
    		  dialog.setConfig({
    		        title: "温馨提示",
    		        titleAlign: "left",
    		        closable: true,
    		        draggable: true,
    		        height: 80,
    		        content: "<p>此操作将会删除此资源下的所有承载设施信息，点击 “确定” 执行操作，点击 “取消” 放弃此操作!</p>",
    		        buttons: [
    		             {
    		                 label: "确定",
    		                 className: "button-yes"
    		             },{
    		                 label: "取消",
    		                 className: "button-no"
    		             }
    		         ],
    		         buttonsAlign: "center",
    		         action: function(item, e) {
    		        	 if(item.label==="确定"){
    		        		 dialog.hide();
    		        		 if(isDesigner){
    		        			 var isFlag = dms.Tools.getIsTnmsDatas(code);
    			                 if(!isFlag){
    			                	 tp.utils.optionDialog("错误提示", '非设计数据，不能拆分！');
    			                	 return ;
    			                 } 
    		        		 }
    		                 
    		        		 tp.utils.wholeLock();
    		        			$.ajax({
    		        				url : ctx+'/rest/MapRestService/doDeleteLayedRelation/'+code+"?time="+new Date().getTime(),
    		        				type : 'POST',
    		        				dataType : 'json',				
    		        				success : function(data) {
    		        					if(data && data.success){
    		        						Dms.Default.tpmap.refreshMap();
    		        						Dms.Default.tpmap.reset();
    		        						tp.utils.optionDialog('温馨提示',data.success);
    		        					}else if(data && data.error){
    		        						tp.utils.optionDialog('温馨提示','光缆拆除承载失败!');
    		        					}
    		        					tp.utils.wholeUnLock();
    		        				},
    		        				error:function(e)
    		        				{
    		        					tp.utils.optionDialog('温馨提示','光缆拆除承载失败!');
    		        					tp.utils.wholeUnLock();
    		        				}
    		        			});	
    		        	 }else{
    		        		 dialog.hide();
    		        		 tp.utils.wholeUnLock();
    		        	 }
    		         }
    		    });
    		    dialog.show();
      	}
	},
	
    //管线设施右键拆除敷设
	deleteLayRelationAction : function(code, isDesigner){
		var isFlag = Dms.Tools.getIsOperate(isDesigner, code);
		if(isFlag){
			var dialog = new ht.widget.Dialog(); 
			var c = new dms.utils.deleteLayRelationPanel(code, dialog);
			dialog.setConfig({
	            title: "<html><font size=3>"+"敷设信息</font></html>",
	            width: 315,
	            height: 300,
	            titleAlign: "center", 
	            draggable: true,
	            closable: true,
	            content: c
	        });
		    dialog.onShown = function(operation) {
		        
		    };
		    dialog.onHidden = function(operation) {
		    	
		    };
		    dialog.show();
		}
    },
    //光缆段右键“通过段拆除承载”
    deleteDuctLine : function(code,isDesigner){
    	var isFlag = Dms.Tools.getIsOperate(isDesigner,code);
      	if(isFlag){
			var dialog = new ht.widget.Dialog(); 
			if(isDesigner){
				var isFlag = dms.Tools.getIsTnmsDatas(code);
	            if(!isFlag){
	           	 tp.utils.optionDialog("错误提示", '非设计数据，不能拆分！');
	           	 return ;
	            } 
			 }
			var c = new dms.utils.deleteDuctLineRelationPanel(code, dialog);
			dialog.setConfig({
	            title: "<html><font size=3>"+"通过段拆除承载</font></html>",
	            width: 800,
	            height: 400,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            content: c
	        });
		    dialog.onShown = function(operation) {
		        
		    };
		    dialog.onHidden = function(operation) {
//		    	callback(c.getResult());
		    };
		    dialog.show();
      	}
    },
    deletePointsAction: function(pointCuids){//删除点设施
		  var dialog = new ht.widget.Dialog();
		  dialog.setConfig({
		        title: "温馨提示",
		        titleAlign: "left",
		        closable: true,
		        draggable: true,
		        content: "<p>确定删除选择的点资源吗？</p>",
		        buttons: [
		             {
		                 label: "确定",
		                 className: "button-yes"
		             },{
		                 label: "取消",
		                 className: "button-no"
		             }
		         ],
		         buttonsAlign: "center",
		         action: function(item, e) {
		        	 if(item.label==="确定"){
		        		 dialog.hide();
		        		 DeleteResInMapAction.deletePoints(pointCuids, function(data){
		        			 if(data){
		        				 tp.utils.optionDialog('删除提示', '数据删除成功!');
		        			 }
		        			 Dms.Default.tpmap.refreshMap();
		     				 Dms.Default.tpmap.reset();
		        		 });
		        	 }else{
		        		 dialog.hide();
		        	 }
		        	 
		             
		         }
		    });
		    dialog.show();
    },

    deleteSystemsAction: function(systemCuids){ //删除线设施
		  var dialog = new ht.widget.Dialog();
		  dialog.setConfig({
		        title: "温馨提示",
		        titleAlign: "left",
		        closable: true,
		        draggable: true,
		        content: "<p>是否删除线设施及其关联点设施?<br>是：删除线设施及其关联点设施<br>否：仅删除线设施<br>取消：取消删除操作</p>",
		        buttons: [
		             {
		                 label: "是",
		                 className: "button-yes"
		             },{
		                 label: "否",
		                 className: "button-no"
		             }
		             ,{
		                 label: "取消",
		                 className: "button-cancel"
		             }
		         ],
		         buttonsAlign: "center",
		         action: function(item, e) {
		        	 deleteAction(item, e);		             
		         }
		    });
		    dialog.show();
		    
		    function deleteAction(item, e){
		    	var isDeletePoint = false;
	        	 if(item.label==="是"){
	        		 isDeletePoint = true;
	        		 dialog.hide();
	        		 excuteDelete(isDeletePoint);
	        	 }else if(item.label==="否"){
	        		 isDeletePoint = false;
	        		 dialog.hide();
	        		 excuteDelete(isDeletePoint);
	        	 }else{	        		 
	        		 dialog.hide();
	        	 }
		    };
		    
		    function isExistOpticalway(){
		    	 var systemcuid = systemCuids[0];
		    	 if(systemcuid&&systemcuid.indexOf("WIRE_SYSTEM") > 0){
		    		 DWREngine.setAsync(false);
		       		 DeleteResInMapAction.isExistOpticalWay(systemCuids, function(data){
		    			 return data;
		    		 });
		       		DWREngine.setAsync(true);
		    	 }
		    	 return false;
		    };
		    
		    function excuteDelete(isdeletepoint){
	        	 if(isExistOpticalway()){
	       		  var dialog = new ht.widget.Dialog();
	    		  dialog.setConfig({
	    		        title: "温馨提示",
	    		        titleAlign: "left",
	    		        closable: true,
	    		        draggable: true,
	    		        content: "<p>删除的光缆系统下存在关联光路，是否继续操作?</p>",
	    		        buttons: [
	    		             {
	    		                 label: "确定",
	    		                 className: "button-yes"
	    		             },{
	    		                 label: "取消",
	    		                 className: "button-no"
	    		             }
	    		         ],
	    		         buttonsAlign: "center",
	    		         action: function(item, e) {
	    		        	 if(item.label==="确定"){
	    		        		 DeleteResInMapAction.deleteSystems(systemCuids,isdeletepoint, function(data){
	    		        			 tp.utils.optionDialog('删除提示', '数据删除成功!');
	    		        			 Dms.Default.tpmap.refreshMap();
	    		     				 Dms.Default.tpmap.reset();
	    		        		 });
	    		        		 dialog.hide();
	    		        	 }else{
	    		        		 dialog.hide();
	    		        	 }
	    		        	 
	    		             
	    		         }
	    		    });
	    		    dialog.show();
	        	 }else{
	        		 DeleteResInMapAction.deleteSystems(systemCuids,isdeletepoint, function(data){
	        			 tp.utils.optionDialog('删除提示', '数据删除成功!');
	        			 Dms.Default.tpmap.refreshMap();
	     				 Dms.Default.tpmap.reset();
	        		 });
	        	 }
		    }
    },
    
    //在过滤时根据特定条件过滤得到需要查询的图层的layerId
    getLayerIdsByTypeName : function(pointLayers){
		var resourceLayerObject=Dms.Default.tpmap.getResourceLayerObject(),
		layerIds = new Array();
		for(var i=0;i<pointLayers.length;i++){
			var pointLayer = pointLayers[i];
			//预置点目前sde中不加，先过滤
			if(pointLayer != "PRESET_POINT"){
				var layerObj = resourceLayerObject[pointLayer];
				if(layerObj){
					var ids = layerObj.layerIds;
					layerIds.push(ids);
				}
			}
		}
		return layerIds;
    },
    getLayerIdsByLayers : function(pointLayers,resourceLayerObject){
		var layerIds = new Array();
		for(var i=0;i<pointLayers.length;i++){
			var pointLayer = pointLayers[i];
			//预置点目前sde中不加，先过滤
			if(pointLayer != "PRESET_POINT"){
				for(var j=0;j<resourceLayerObject.length;j++){
					var resObject = resourceLayerObject[j];
					var name = resObject.name;
					if(name === pointLayer){
						var ids = resObject.id;
						layerIds.push(ids);
					}
				}
			}
		}
		return layerIds;
    },
	autoLayDuctLine: function(wireSegCuid,lineCuid){//图形化自动敷设
	  var dialog = new ht.widget.Dialog();
	  dialog.setConfig({
	    title: "温馨提示",
	    titleAlign: "left",
	    closable: true,
	    draggable: true,
	    width: 200,
        height: 100,
	    content: "<p>请选择敷设光缆方向</p>",
	    buttons: [
	         {
	             label: "正向",
	             className: "button-yes"
	         },{
	             label: "反向",
	             className: "button-no"
	         },{
	             label: "取消",
	             className: "button-cancel"
	         }
	     ],
	     buttonsAlign: "center",
	     action: function(item, e) {
	    	 //所选择的必须是以下两种情况中的一种
	    	 //管道、杆路、标石路由系统的分支或者段；
	    	 //挂墙、引上系统的系统或者段
	    	 tp.utils.wholeLock();
	    	 if(item.label === "取消"){
	    		 Dms.Default.tpmap.reset();
	    		 dialog.hide();
	    		 tp.utils.wholeUnLock();
	    		 return;
	    	 }else{
	    		 var cuidSplit = lineCuid.split("-")[0];
	        	 if(cuidSplit ==='DUCT_SYSTEM' || cuidSplit ==='POLEWAY_SYSTEM' || cuidSplit ==='STONEWAY_SYSTEM'){
	        		 dialog.hide();
	        		 tp.utils.optionDialog("温馨提示", "所选择的必须是以下两种情况中的一种:<br/> 1:管道、杆路、标石路由系统的分支或者段;<br/> 2:挂墙、引上系统的系统或者段.");
	        		 Dms.Default.tpmap.reset();
	        		 tp.utils.wholeUnLock();
	        		 return;
	        	 }
	        	 var wireType = wireSegCuid.split("-")[0];
	        	 if(wireType ==='WIRE_SYSTEM'){
	        		 dialog.hide();
	        		 tp.utils.optionDialog("温馨提示", "所选择的必须是光缆段.");
	        		 Dms.Default.tpmap.reset();
	        		 tp.utils.wholeUnLock();
	        		 return;
	        	 }
	        	 var direction = "0";
	        	 if(item.label === "正向"){
	        		 direction = "0";
	        	 }else if(item.label === "反向"){
	        		 direction = "1";
	        	 }
	        	 dialog.hide();
	        	 $.ajax({
	    				url : ctx+'/rest/MapRestService/graphWireLayToNewDuctLine/'+wireSegCuid+"/"+lineCuid+"/"+direction+"?time="+new Date().getTime(),
	    				type : 'POST',
	    				dataType : 'json',				
	    				success : function(data) {
	    					if(data && data.success){
	    						Dms.Default.tpmap.refreshMap();
	    						Dms.Default.tpmap.reset();
	    						tp.utils.optionDialog('温馨提示','光缆敷设成功!');
	    					}else if(data && data.error){
	    						tp.utils.optionDialog('温馨提示','光缆敷设失败!');
	    					}
	    					tp.utils.wholeUnLock();
	    				},
	    				error:function(e)
	    				{
	    					tp.utils.wholeUnLock();
	    					optionDialog('温馨提示','光缆敷设失败!');
		        		}
		        	});	
		        }
		    }
		});
		dialog.show();
	  },
	//光缆段具体路由管理
	    wireToLayDuctLine : function(code){
			var dialog = new ht.widget.Dialog(); 
			tp.Default.OperateObject.contextObject.cuid = code;
			var c = new dms.utils.createWireToDuctLinePanel(code, dialog);
			dialog.setConfig({
	            title: "<html><font size=3>"+"光缆段具体路由管理</font></html>",
	            width: 800,
	            height: 400,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            content: c
	        });
		    dialog.onShown = function(operation) {
		        
		    };
		    dialog.onHidden = function(operation) {
//		    	callback(c.getResult());
		    };
		    dialog.show();
	    },
	    //计算管线系统长度
	    doCalculateSystemLengthAction: function(cuid){
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含CUID字段,请检查配置');
				return;
			}
			var className = cuid.split("-")[0];
			var scop=this;
			Ext.MessageBox.show({
				title:'注意！',
				msg:'此操作将重新计算现有系统长度，请根据具体需要进行选择<br />长度求和：根据现有段长求和，不更改段的长度<br />重新计算：此操作将根据系统下对应点的实际经纬度重新计算各段的长度，覆盖现有的长度<br />修改调线长度并求段长度和，当段的起止点的实际经纬度为非法值时，段的长度不修改<br />取消：取消当前操作',
				buttons:{ok:'长度求和',yes:'重新计算',cancel:'取消'},
			    fn:function(btn){
			    	if(btn =="ok"){
			    		if(classname == "DUCT_SYSTEM" || className == "HANG_WALL"){
			    			CalculateSystemLengthAction.doCalculateSystemLength(cuid);
			    		}
			    		if(className == "POLEWAY_SYSTEM"){
				    		CalculateSystemLengthAction.doPwsCalculateSystemLength(cuid);
			    		}
			    		if(className == "STONEWAY_SYSTEM"){
			    			CalculateSystemLengthAction.doSwsCalculateSystemLength(cuid);
			    		}
			    		if(className == "UP_LINE"){
				    		CalculateSystemLengthAction.doUpCalculateSystemLength(cuid);
			    		}
			    		Ext.MessageBox.show({
			    			msg:'计算中,请等待..',
			    			progressText:'计算中...',
			    			width:300,
			    			wait:true,
			    			waitConfig:{interval:300}
			    		});
			    		setTimeout(function(){
			    	          Ext.MessageBox.hide();
			    	    }, 3000);
			    		 
			        }else if(btn =="yes"){
			        	CalculateSystemLengthAction.modifyCalculateSystemLength(cuid);
			        	Ext.MessageBox.show({
			    			msg:'计算中,请等待..',
			    			progressText:'计算中...',
			    			width:300,
			    			wait:true,
			    			waitConfig:{interval:300}
			    		});
			    		setTimeout(function(){
			    			Ext.MessageBox.hide();
			    	    }, 3000);
			        }
			    	 scop.store.load();
	              }
			});
	    },
	    //计算管线段长度
	    doDMCalculateSystemLengthAction :function(cuid){
			 Ext.MessageBox.show({
				title:'注意！',
				msg:"此操作根据起止端点的经纬度计算段的长度，并修改管孔，子孔长度 <br/>当段的起止点的实际经纬度为非法值时，段的长度不修改，是否继续？<br/>选择是将继续该操作，选择否将取消该操作.",
				buttons:{ok:'是',cancel:'否'},
			    fn:function(btn){
			    	if(btn =="ok"){
			    		CalculateSystemLengthAction.doDMCalculateSystemLength(cuid);
			    		Ext.MessageBox.show({
			    			msg:'计算中,请等待..',
			    			progressText:'计算中...',
			    			width:300,
			    			wait:true,
			    			waitConfig:{interval:300}
			    		});
			    		setTimeout(function(){
			    	          Ext.MessageBox.hide();
			    	    }, 3000);
			    	}
			    }
			});
	    },
	    
	    //根据CUID定位
		graphicLocateOnMap : function(cuids,where,bmClassId)
		{
			var whereCuid = "";
			var types = [];
			
			if('string' == typeof(cuids))
			{
				var aryCuid = cuids.split(",");
				if(aryCuid != null && aryCuid.length > 0){
					for(var x = 0; x < aryCuid.length; x++){
					
						var cuid = aryCuid[x] ;
						var className = bmClassId || cuid.split("-")[0];
						//综资资源
						className = Dms.layernameFilter(className);
						types.push(className);
						
						if(whereCuid.length > 0){
							whereCuid += ",";
						}
						whereCuid += "'" + aryCuid[x] + "'";
					}
				}
			}
			else if(cuids instanceof Array)
			{
				for(var x = 0; x < cuids.length; x++){
					
					var cuid = cuids[x].CUID;
					if(cuid != null){
						var className = bmClassId || cuid.split("-")[0];
						//综资资源
						className = Dms.layernameFilter(className);
						types.push(className);
						
						if(whereCuid.length > 0){
							whereCuid += ",";
						}
						whereCuid += "'" + cuids[x].CUID + "'";
					}
				}
			}
			//去重
			types = tp.utils.unique(types);
			if(where == null){
				where = "CUID IN(" + whereCuid + ")";
			}
			//查询类型对应的图层URL
			this.queryByParams(types, where, callback);
			function callback(features,geometryTypes){
				if(!features || features.length ==0)
				{
					var names = [];
					for(var i = 0 ;i< types.length ; i++)
					{
						names.push(Dms.layernameConfig[types[i]]);
					}
					tp.utils.optionDialog("温馨提示","空间库没有查询到符合条件的"+names.join('，')+"资源！");
				}
				else
				{
					var tpmap = Dms.Default.tpmap;
					var contextObject={
			    			'cuid':features[0].attributes.CUID || features[0].attributes.OBJECTID,
			    			'label': features[0].attributes.LABEL_CN || features[0].attributes.GRID_NAME,
			    			'className' :features[0].attributes.RELATED_BMCLASSTYPE_CUID || features[0].attributes.CUID.split("-")[0],
			    			'attributes':features[0].attributes,
			    			'geometry':features[0].geometry
			    	};
					tp.Default.OperateObject.contextObject = contextObject;
			    	tp.Default.DrawObject._drawState = 101;
			    	
			    	//仅支持纯点或纯线，不支持混合模式
					tpmap.locateOnMap(features,geometryTypes[0],true);
				}
			}
		},
		//根据条件查询动态资源
		queryByParams : function(types,where,callback){
			var urls = this.getLayerUrlsByType(types);
			urls = tp.utils.unique(urls);
			
			var times = urls.length;
			var tpmap = Dms.Default.tpmap;
			var features = [];
			var geometryTypes = [];
			
			for(var i=0 ; i < urls.length ; i++)
			{
				tpmap._query(urls[i],where||'1=1',queryHandler);
			}
			function queryHandler(response){
				times -- ;
				if(response.features)
					features = features.concat(response.features);
				if(response.geometryType)
					geometryTypes.push(response.geometryType);
    			//所有图层均返回结果
    			if(times == 0){
    				callback(features,geometryTypes);
    			}
			}
		},
		
		getLayerUrlsByType : function(type){
			var tpmap = Dms.Default.tpmap;
			var result = [];
			for(var i=0;i<type.length;i++)
			{
				var urls = tpmap._findLayerUrls(type[i]);
				if(urls.length > 0){
					for(var j=0;j<urls.length;j++)
						result.push(urls[j]);
				}
			}
			return result;
		},
		
		//切分图形
		cutFeature : function(geometries,cutterPolyline,callback){
			var url = Dms.Default.tpmap.getConfig().map.geometryService+"/cut";
			var param = {
					sr : 4326,
					target : JSON.stringify({
						geometryType : 'esriGeometryPolygon',
						geometries :geometries}),
					cutter : JSON.stringify(cutterPolyline)
			};
			L.esri.get(url, param, function(response){
				if(response.error && (response.error.code === 499 || response.error.code === 498)) {
					console.info('地图服务'+url+'无法访问！');
				}else{
					if(response.error)
					{
						tp.utils.optionDialog('温馨提示','切分图形失败，请联系管理员！');
						Dms.Default.tpmap.reset();
					}else{
						var geometries = response.geometries;
						var geometryType = response.geometryType;
						callback(geometries,geometryType);
					}
				}
		    });
		},
		
		_getMapServerUrl : function(type){
			var reslayers = Dms.Default.tpmap.getConfig().map.reslayers;
			for(var i =0 ;i< reslayers.length; i++)
			{
				var url = reslayers[i].url;
				if(url.indexOf(type) > -1)
					return url;
			}
			return null;
		},
		
		//根据经纬度查询某图层
		indentifyByLatLng : function(type,lng,lat,callback)
		{
			var url = this._getMapServerUrl(type);
			if(url == null)
			{
				tp.utils.optionDialog("温馨提示", "未配置"+type+"图层");
				return ; 
			}
			var map = Dms.Default.tpmap.getMap();
			var param = {
					geometryType :'esriGeometryPoint',
				    geometry : lng+','+lat,
				    tolerance : 5,
				    layers : 'visible',
				    returnGeometry:true,
				    sr : 4326,
				    mapExtent : map.getBounds().getSouthWest().lng+','+map.getBounds().getSouthWest().lat+','+map.getBounds().getNorthEast().lng+','+map.getBounds().getNorthEast().lat,
				    imageDisplay : '600,550,96'
			};
			L.esri.get(url+"/identify", param, function(response){
				if(response.error)
				{
					tp.utils.optionDialog("错误", response.error.message);
					Dms.Default.tpmap.reset();
				}
    			else{
    				var loc = response.results;
    				if(loc && callback){
    					callback(loc);
    				}
    			}
			});
		},
		//为回调函数增加参数
		addCallbackArgs : function(f,args){
			var F = false;
		   	var _f = function(e,_args)
		   	{
		   		_args = args;
		   		if(!F)
		   		{
		   			F=true;
		   			_args.unshift(e);
		   		}
		   		f.apply(null,_args);
			};
			return _f;
		},
		//下移
		moveDataToDown : function(dataModel){
			var ld=dataModel.sm().ld();
			dataModel.moveDown(ld); 
		},
		
		//上移moveToTop移动到顶上，moveToBottom移动到最下面，moveUp上移一个,moveDown下移一个
		moveDataToUp : function(dataModel){
			var ld=dataModel.sm().ld();
			dataModel.moveUp(ld); 
		},
		
		//改变方向
		changeDirection : function(dataModel){
			var child = dataModel.sm().ld();
			if(child)
			{
				var origPointCuid = child.getAttr("ORIG_POINT_CUID"),
					origPointName = child.getAttr("ORIG_POINT_NAME"),
					destPointCuid = child.getAttr("DEST_POINT_CUID"),
					destPointName = child.getAttr("DEST_POINT_NAME"),
					direction = child.getAttr("DIRECTION");
				if(origPointCuid && destPointCuid)
				{
					child.a("ORIG_POINT_CUID",destPointCuid);
					child.a("ORIG_POINT_NAME",destPointName);
					child.a("DEST_POINT_CUID",origPointCuid);
					child.a("DEST_POINT_NAME",origPointName);
					
					var name = destPointName+"--"+origPointName;
					child.setName(name);
					child.a("LABEL_CN",name);
					
					if(direction == "1"){
						child.a("DIRECTION",2);
						child.a("DIRECTION_NUM","反向");
					}else{
						child.a("DIRECTION",1);
						child.a("DIRECTION_NUM","正向");
					}
				}
			}else
				tp.utils.optionDialog("错误提示", "请选择资源!");
		},
		//分支翻转
		berachTrun : function(dataModel){

	    	var elements = dataModel.sm();
	    	for(var i=0;i<elements.size();i++){
	    		var data = elements.getSelection().get(i),
	    		attr=data.getAttr("CUID"),
	    		attrClassName = attr.split("-")[0];
	    		if(attrClassName.indexOf("BRANCH")==-1){
	    			tp.utils.optionDialog("错误提示", "要翻转的数据中有不是分支的类型,请重新选择!");
	    			return;
	    		}
	    	};
	    	elements.each(function(data){
	    		var children = data.getChildren()._as.slice(0); 
	    		children.forEach(
	    			function(child) {
	    				var origPointCuid = child.getAttr("ORIG_POINT_CUID"),
	    				origPointName = child.getAttr("ORIG_POINT_NAME"),
	    				destPointCuid = child.getAttr("DEST_POINT_CUID"),
	    				destPointName = child.getAttr("DEST_POINT_NAME"),
	    				direction = child.getAttr("DIRECTION");
	    				child.a("ORIG_POINT_CUID",destPointCuid);
	    				child.a("ORIG_POINT_NAME",destPointName);
	    				child.a("DEST_POINT_CUID",origPointCuid);
	    				child.a("DEST_POINT_NAME",origPointName);
	    				
	    				var name = destPointName+"--"+origPointName;
	    				child.setName(name);
	    				child.a("LABEL_CN",name);
	    				
	    				if(direction == "1"){
	    					child.a("DIRECTION",2);
	    					child.a("DIRECTION_NUM","反向");
	    				}else{
	    					child.a("DIRECTION",1);
	    					child.a("DIRECTION_NUM","正向");
	    				}
	    				dataModel.moveToTop(child); 
	    			}
	    		);
			},elements);
		},
		deleteDatas : function(dataModel){
			dataModel.sm().toSelection().each(dataModel.remove, dataModel); 
		},
		//地图右键快速添加线设施中所属系统等的查询组件修改------by wq--2015.03.03--
		//个value代表选择前的input里面的值，更换为新的弹出查询选择窗口
		createAddTextSelectInput : function(code, value, inputName,callback) {
		  var input = ht.Default.createElement('input');
		  input.name = inputName;
		  input.readOnly = 'readOnly';
		  if (value) {
		    input.value = value;
		  }
		  var attrs="",boclassName="",boname="",formName="";
		  attrs = Dms.selectSystemTypeResName[code];
		  if(attrs){
			  var attr = attrs.split(",");
			  boclassName = attr[0];
			  boname = attr[1];
			  formName = attr[2];
		  }
		  var clearButton = document.createElement('button');
		  clearButton.style.padding = 0;
		  clearButton.style.position = 'absolute';
		  clearButton.innerHTML = '<img src="' + ctx + '/resources/tool/cancel.png" style="margin:0;padding0;">';
		  clearButton.onclick = function(e) {//此处有问题，清除当前值后，已经选择过的值还存在param中。
		    input.value = '';
		    var param = dms.params;
		    var iname = input.name;
		    if(iname == "related_system"){
		    	if(param.relatedSystemCuid)
		    		param.relatedSystemCuid = "";
		    	if(param.relatedSystemLabelCn)
		    		param.relatedSystemLabelCn = "";
		    }else if(iname == "related_template"){
		    	if(param.relatedTemplateCuid)
		    		param.relatedTemplateCuid = "";
		    }else if(iname == "related_project"){
		    	if(param.relatedProjectCuid)
		    		param.relatedProjectCuid = "";
		    }
		  };

		  var queryButton = document.createElement('button');
		  queryButton.style.padding = 0;
		  queryButton.style.position = 'absolute';
		  queryButton.innerHTML = '<img src="' + ctx + '/resources/tool/zoom.png">';
		  queryButton.onclick = function(e) {
			  
	    	  var dialog = new ht.widget.Dialog();
	    	  var queryPanel = null;
	    	  if(code == 'service_dict_dm.DM_PROJECT_MANAGEMENT' || inputName ==='related_system')
	    		  queryPanel  = tp.utils.createQueryWireSystemPanel;
	    	  
			  var c = tp.utils.createQueryPanel(code, dialog,input,queryPanel);
			  dialog.setConfig({
			    title: "<html><font size=3>查询</font></html>",
			    width: 800,
			    height: 500,
			    titleAlign: "left",
			    draggable: true,
			    closable: true,
			    content: c
			  });
			  dialog.onHidden = function(operation) {
				  if(input){
					  var inputId = input.id,
					  inputName = input.value;
					  var inputData = {'CUID': inputId,'LABEL_CN' : inputName};
					  callback(inputData);
				  }
			  };
			  dialog.show();
		  };
		  var inputForm = new ht.widget.FormPane();
		  inputForm.addRow([input, clearButton, queryButton], [0.1, 20, 20]);
		  inputForm.setVGap(0);
		  inputForm.setHGap(0);
		  inputForm.setPadding(0);
		  return inputForm;
		},
		getIsTnmsDatas : function(cuid) {
			var isFlag = true;
			DWREngine.setAsync(false);
			DuctLineDatasAction.getDatasIsDesigner(cuid, function(data) {
				if (data) {
					var istnms = data.ISTNMS;
					if (istnms === "TRUE") {
						//是现网数据，不能进行拆分
						isFlag = false;
					}
				}
			});
			DWREngine.setAsync(true);
			return isFlag;
		},
		getRelationLineByPoint : function(viewParam){
			//点设施查看关联线设施
			if(!viewParam){
				//如果没有传值，默认为是地图上右键查看
				viewParam={
						'CUID':tp.Default.OperateObject.contextObject.cuid,
	       	        	'LABEL_CN':tp.Default.OperateObject.contextObject.label,
	       	        	'mapName':"查看关联线设施"
	       	    };
			}
			if(viewParam){
				var relatedLinesPanel = new dms.createRelatedSystemListPanel(viewParam);
				viewParam.content=relatedLinesPanel;
	   	        tp.utils.showDialogView(viewParam);
			}else{
				tp.utils.optionDialog("参数错误","查看关联线设施失败！");
			}
		}
		,
		showResOperateView : function(){
			//不处理地图上点选分支、系统
	    	if(tp.Default.DrawObject._drawState === 102 || tp.Default.DrawObject._drawState === 103){
	    		if(Dms.Default.dataAttrPane){
	            	Dms.Default.dataAttrPane.close();
	            }
	    		return;
	    	}
	    	var tpmap = Dms.Default.tpmap;
	    	var graphView = tpmap.getGraphView();
	    	
	    	var resCuids = [];
	    	var datas = graphView.dm().getDatas();
	    	if(datas.isEmpty()){
	    		return;
	    	}else{
	    		datas.each(function(data){
	    			resCuids.push(data.getId());
	    		});
	    	}
	    	
	    	if(resCuids.length > 0){
	    		GenerateCuidAction.getObjsByCuidsAndType(resCuids,{
	    			callback : function(result){
	    				if(result){
	    					var resObject = result.resObject;
	    					var isDesignRes = result.isDesignRes;
					    	if(Dms.Default.floatPane){
					    	    Dms.Default.floatPane.close();
					    	}else{
					    		Dms.Default.dataAttrPane = new Dms.Panel.DataAttrPane();
					    	}
					        Dms.Default.dataAttrPane.initResOperateView(isDesignRes,resObject);
					        Dms.Default.dataAttrPane.show();
	    				}
	    			},
	    			errorHandler : function(){},
	    			async : false
	    		});
	    	}else{
	    		return;
	    	}
	    },
	    showPropertyView : function(){
	    	var self = this;
	    	var data = tp.Default.OperateObject.contextObject ;
	    	if(!data)
	    		return;
	    	
			var cuid = data.cuid;
			GenerateCuidAction.getObjsByCuidsAndType([cuid],{
    			callback : function(result){
    				if(result){
    					var resObject = result.resObject;
    					var isDesignRes = result.isDesignRes;
				    	self.showAttrPane(isDesignRes,resObject);
    				}
    			},
    			errorHandler : function(){},
    			async : false
    		});
	    },
	    showAttrPane : function(isDesignRes,resObject)
	    {
	    	var self = this;
			var dataModel = new ht.DataModel();
        	var data = new ht.Node();
        	for(var p in resObject){
			    if(resObject[p] instanceof Date){
			    	 data.a(p,self.dateToLabel(resObject[p].toString()));
			    }else{
			    	if(p ==='LONGITUDE' || p === 'LATITUDE'){
			    		data.a(p,resObject[p]+"");
			    	}else{
			    		data.a(p,resObject[p]);
			    	}
			    }
			}
			
    		var origPoint = new ht.Node();
    		var destPoint = new ht.Node();
    		if(resObject.ORIG_POINT_CUID){
    			origPoint.setId(resObject.ORIG_POINT_CUID);
    			origPoint.setName(resObject.ORIG_POINT_CUID_NAME);
    			data.a('ORIG_POINT_CUID',origPoint);
    		}
    		if(resObject.DEST_POINT_CUID){
    			destPoint.setId(resObject.DEST_POINT_CUID);
    			destPoint.setName(resObject.DEST_POINT_CUID_NAME);
    			data.a('DEST_POINT_CUID',destPoint);
    		}
        		
        	data.a('bmClassId',resObject.className);
        	dataModel.add(data);
        	dataModel.sm().ss(data);
        	var propertyPane = new Dms.Panel.PropertyPane(dataModel);
        	propertyPane.propertyView.setEditable(isDesignRes);
        	
        	var dialog = new ht.widget.Dialog(); 
        	var buttomBtns = [{
		           label : '保存'
		    }, {
		           label : '取消'
		    }];
			dialog.setConfig({
	            title: "<html><font size=2>查看属性</font></html>",
	            width: 600,
	            height: 350,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            content: propertyPane.getView(),
	            buttonsAlign : 'center'
			});
        	dialog.show();
	    },
	    dateToLabel : function(dataValue,fmt){
			if(dataValue)
			{
				var date = new Date(dataValue);
				if(date){
					return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()
					+":"+date.getMinutes()+":"+date.getSeconds();
				}
			}
			return "";
		}
	   
};