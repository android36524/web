$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');
$importjs(ctx+'/dwr/interface/SplitMapDuctSegAction.js');

(function(window,Object,undefined){
	dms.split.splitTools.AddSplitSegPanel = function (){
		tp.utils.wholeLock();
		var c = createSplitPanel();
		var panel = new ht.widget.Panel(
				{
					title : "选择的拆分点",
					width : 500,
					exclusive : false,
					titleColor : "white",
					minimizable : true,
					minimized  : false,//控制打开时界面是不是最小化
					expanded : true,
					narrowWhenCollapse : true,
					contentHeight : 280,
					buttons:['minimize',{
						name : '关闭',
						toolTip:'关闭',
						icon:'close.png',
						action:function(){
							dms.split.splitTools.clearSplitSegUnkown();
						}
					}],
					content : c
				});
	
		var x = (window.screen.availWidth - panel._config.width)/2;
		var y = (window.screen.availHeight- panel._config.contentHeight)/2;
		panel.setPosition(x, y);
		
		//锁屏有问题，加个fp
		panel.fp = function(){};
		dms.split.splitTools.panel = panel;
		dms.split.splitTools.splitSegView = panel.getView();
		document.body.appendChild(dms.split.splitTools.splitSegView);
		panel.setSelectValue = function(){
			c.setSelectValue();
		};
		
		window.addEventListener('resize', function (e) {
			panel.invalidate();
        }, false); 
		
		return panel;
	};
	
	dms.split.splitTools.AddSplitSeg = function(){
		var pointsize = tp.Default.DrawObject._drawPointList.length;
		if (pointsize <= 2) {
			tp.utils.optionDialog("温馨提示", "请选择拆分点！");
            return;
        }
		if(tp.Default.DrawObject._drawLineClass == 'WIRE_SEG'){
			//根据id判断list中的选择的承载点，是不是在这个光缆原有的具体路由点中
			var splitWsPoint = dms.split.wireSegSplitPoint,
				routePoint = dms.split.wireSegRoutePoint,
				list = tp.Default.DrawObject._drawPointList;
			
			var routeDtoCuid = "";
			var isFlag = false;//false代表拆分的路由点不是这个光缆段的具体路由点
			for(var i=0;i<routePoint.length;i++){
				var splitpoint = routePoint[i],
				splitpointCuid = splitpoint.a('CUID');
				if(!isFlag){
					for(var j=0; j<list.length; j++){
						var point = list[j],
						pointCuid = point.a('CUID');
						if(splitpointCuid === pointCuid){
							isFlag = true;
							routeDtoCuid = splitpointCuid;
							break;
						}else{
							isFlag = false;
						}
					}
				}
			}
			
			//有具体路由的，没有选择具体路由，只选择了光缆段的光交接拆分点
			var isSelectRoute=false,
			isSelectNotRoute=false;
			var notRouteDtoCuid = "";
    		for(var i=0;i<splitWsPoint.length;i++){
    			var spoint = splitWsPoint[i],
    			spCuid = spoint.a('CUID');
    			if(spCuid == routeDtoCuid){
    				isSelectRoute=true;
    			}else{
    				isSelectNotRoute=true;
    				notRouteDtoCuid=spCuid;
    			}
    		}

    		var isFpoint = null;
    		if(notRouteDtoCuid){
    			var notRouteName = notRouteDtoCuid.split('-')[0];
    			//非具体路由点必须是光交点
    			isFpoint = dms.isFiberPoint[notRouteName];
    		}
			if(routePoint.length == 0){
    			if(splitWsPoint.length !=1 || !isFpoint){
    				tp.utils.optionDialog("错误提示", "满足以下条件之一的才能进行拆分：" +
	    					"\n1.无具体路由，选择一个拆分点" +
	    					"\n2.有具体路由，选择其中一个具体路由点和一个拆分点");
					dms.split.splitTools.clearSplitSegUnkown();
					return;
    			}
    		}else{
    			if(!(isSelectRoute && isSelectNotRoute&&splitWsPoint.length==2 && isFpoint)){
	    			tp.utils.optionDialog("错误提示", "满足以下条件之一的才能进行拆分：" +
	    					"\n1.无具体路由，选择一个拆分点" +
	    					"\n2.有具体路由，选择其中一个具体路由点和一个拆分点");
					dms.split.splitTools.clearSplitSegUnkown();
					return;
	    		}
    		}
		}
		dms.split.splitTools.AddSplitSegPanel();
	};
	dms.split.splitTools.clearSplitSegUnkown = function(){
		//此处不再刷新地图，只在保存完成后刷新
//		dms.Default.tpmap.refreshMap();
//		tp.utils.unlock(dms.split.splitTools.splitSegView);
		tp.utils.unlock(dms.split.splitTools.panel);
		tp.utils.wholeUnLock();
		dms.Default.tpmap.reset();
		dms.split.splitTools.dataModel = null;
		dms.split.splitTools.splitSegCuid = "";
		dms.split.splitTools.bClassName = "";
		dms.split.wireSegRoutePoint = [];
		dms.split.wireSegSplitPoint = [];
		if(dms.split.splitTools.splitSegView){
			document.body.removeChild(dms.split.splitTools.splitSegView);
			dms.split.splitTools.splitSegView = null;
		}

		if(tp.Default.OperateObject.curInterator){
			tp.Default.OperateObject.curInterator.reset();
		}
		if(Dms.Default.floatPane)
        	Dms.Default.floatPane.close();
		
		if(dms.split.splitTools.panel){
			dms.split.splitTools.panel = null;
		}
		
		if(dms.split.splitTools.isDesigner){
			dms.split.splitTools.isDesigner = false;
		}
	};
	
	function createSplitPanel(){
		var splitPanel = new ht.widget.FormPane(),
		tablePanel = new ht.widget.TablePane(),
		tableView = tablePanel.getTableView();
		
		var serializeString = dms.Default.tpmap.getGraphView().dm().serialize();
		var dm = dms.split.splitTools.dataModel = new ht.DataModel();
		//dms.split.splitTools.panel.dataModel是clone过来的，前面的数据封装如果是在gv的dm中，在此界面修改时需要用dms.split.splitTools.panel.dataModel查一下
		dms.split.splitTools.dataModel.deserialize(serializeString);
		tableView.setDataModel(dm);
		tableView.setVisibleFunc(function(data){    
		    var bmClassId = data.a('bmClassId');
		    var isPoint = dms.isPoint[bmClassId];
		    if(isPoint){
		    	var obj = data.a('OBJECT_STATE');
		    	if(obj && obj === 1){
		    		return true;
		    	}
		    }
		    return false;
		});
		
		tableView.setCheckMode(true);
		var bmClassId = "SPLITPOINT";
		var url = ctx + "/map/column/" + bmClassId + ".json";
		$.getJSON(url, {}, function(data) {
			tableView.addColumns(data);
			tablePanel.invalidate();
//			column.formatValue = function(v) {return v;}ht表格数字给自动截取给两个小数，v代表不截取
		});
		var splitSegBtnPanel = createSplitSegButtonPanel();
		splitPanel.addRow([tablePanel],[0.1],225);
		splitPanel.addRow([splitSegBtnPanel],[0.1],28);
		return splitPanel;
	};
	
	function createSplitSegButtonPanel(){
		var btnPanel = new ht.widget.FormPane();
		btnPanel.setVPadding(1);
		btnPanel.setHPadding(1);
		
		var cancelBtn = new ht.widget.Button();
		cancelBtn.setLabel('取消');
		
		cancelBtn.onClicked = function(e){
			dms.split.splitTools.clearSplitSegUnkown();
		};
		var confirmBtn = new ht.widget.Button();
		confirmBtn.setLabel('确定');

		var editBtn = new ht.widget.Button();
		editBtn.setLabel('属性编辑');
		confirmBtn.onClicked = function(){
			try {
				tp.utils.lock(dms.split.splitTools.panel);
				var points = [];
				var list = tp.Default.DrawObject._drawPointList;
				//刷新下_drawPointList修改的值
				var dm = dms.split.splitTools.dataModel;
				for (var i = 0; i < list.length; i++) {
					var point = list[i];
					var cnode = dm.getDataByTag(point.a('CUID'));
					if (cnode) {
						var attrObject = cnode.getAttrObject();
						//p代表key
						for ( var p in attrObject) {
							point.a(p, cnode.a(p));
						}
					}
				}
				for (var i = 0; i < list.length; i++) {
					var point = list[i];
					var p = point.getAttrObject();
					points.push(p);
				}
				if (points.length > 0) {
					var segCuid = dms.split.splitTools.splitSegCuid;
					var segGroupCuid = "";
					var scene = null;
					if(dms.designer){
						scene = dms.designer.scene;
						segGroupCuid = dms.designer.segGroupCuid
					}
					if(scene==null || scene==""){
						segGroupCuid = Dms.Default.segGroupCuid;//勘误的单位工程
						scene = Dms.Default.scene;//勘误场景
					}
					if (tp.Default.DrawObject._drawLineClass == 'WIRE_SEG') {
						//过滤传递后台的具体路由点和光交点
						var filterPoint = dms.split.wireSegSplitPoint;
						var filterPoints = [];
						for (var i = 0; i < filterPoint.length; i++) {
							var fpt = filterPoint[i], fptCuid = fpt.a('CUID');
							for (var j = 0; j < points.length; j++) {
								var pt = points[j], 
									ptCuid = pt.CUID;
								if (fptCuid === ptCuid) {
									filterPoints.push(pt);
								}
							}
						}
						//如果是交接箱或者分纤箱，需要处理模块和端子
						if(filterPoints && filterPoints.length>0){
							for (var i = 0; i < filterPoints.length; i++) {
								var pt = filterPoints[i];
								var moduleCfg = pt.MODULE_CFG;
								if(moduleCfg){
									var stringify=JSON.stringify(moduleCfg);
									pt.MODULE_CFG = stringify;
								}
							}
						}
						SplitMapWireSegAction.splitWireSegRes(segCuid,filterPoints, segGroupCuid,scene, function(data) {
							if (data) {
								tp.utils.optionDialog("温馨提示",
										'<div style="text-align:center;font-size:12px">' + data + '</div>');
							}
							dms.split.splitTools.clearSplitSegUnkown();
							dms.Default.tpmap.refreshMap();
						});
					} else {
						SplitMapDuctSegAction.splitSegRes(segCuid, points,segGroupCuid,scene, function(data){//陕西拆分使用方法
							if (data) {
								tp.utils.optionDialog("温馨提示",
										'<div style="text-align:center;font-size:12px">' + data + '</div>');
							}
							dms.split.splitTools.clearSplitSegUnkown();
							dms.Default.tpmap.refreshMap();
						});
					}
				}
			} catch (e) {
				tp.utils.optionDialog("错误提示",'<div style="text-align:center;font-size:12px">拆分出错！</div>');
				dms.split.splitTools.clearSplitSegUnkown();
			}
		};
		editBtn.onClicked = function(e){
			var dm = dms.split.splitTools.dataModel;
			if(dm.sm().size() == 0){
				tp.utils.optionDialog("温馨提示",'请选择一条记录！');
				return;
			}
			var lastData = dm.sm().ld();
			var bmClassId = lastData.a('bmClassId');
			
			//1、判断只能选同种资源
			var typeArr = [];
			typeArr.push(bmClassId);
			var flag = false;
			dm.sm().each(function(data){
				var type = data.a('bmClassId');
				if(data != lastData && typeArr.indexOf(type) == -1){
					flag = true;
				}
			});
			if(flag){
				tp.utils.optionDialog('温馨提示','请选择同种类型的资源！');
				return;
			}
			
			//2、判断已有点不可编辑
			var message = '以下已有点不可编辑 </br>';
			dm.sm().each(function(data){
				if(data.a('ISOLD')){
					flag = true;
					message += '【<font color="red">'+data.a('LABEL_CN')+'</font>】';
				}
			});
			if(flag){
				tp.utils.optionDialog('温馨提示',message);
				return;
			}
			
			//3、打开属性编辑界面
			if(bmClassId && (bmClassId === 'FIBER_CAB' ||  bmClassId === "FIBER_DP")){ 
				new Dms.Panel.FcabEditPanel(dm).show();
			}else{
				new Dms.Panel.PropertyPane(dm).show();
			}
			
		};
		
		btnPanel.addRow([null,editBtn,null,confirmBtn,null,cancelBtn],[0.1,0.2,0.1,0.2,0.1,0.2],23);
		return btnPanel;
	}
})(this,Object);