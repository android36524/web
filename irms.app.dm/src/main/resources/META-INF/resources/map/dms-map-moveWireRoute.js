/**
 * 光缆改迁
 */
$importjs(ctx+'/dwr/interface/MapMoveWireRouteAction.js');

(function(window,object,undefined){
	"use strict";
	
	dms.move.moveWireRoutePanel = function(isDesigner){
		var self = this;
		var c = self.createRoutePanel();
		dms.move.moveWireRoutePanel.superClass.constructor.apply(this,[{
			title : "光缆改迁",
			titleAlign :'center',
			width : 600,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 350,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
				action:function(){
					self.clearMoveWireRoute();
				}
			}],
			content : c
		
		}]);
		self.fp = function(){};
		var tpmap = Dms.Default.tpmap,
	  	map = tpmap.getMap();
		
		dms.move.movePanel = c;
		dms.move.isDesigner = isDesigner;
//		dms.move.isChanged = true;//用于判断上一步时有没有修改选择的段
		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 
//		self.isChanged;
		setMapOnClick();
	    function create(cuid,name, parent, description){
	    	var dataModel = null;
	    	var seltabIndex = self.getTabelViewSelectIndex();
	    	if(seltabIndex === 0){
	    		dataModel = dms.move.selectLineDataModel;
	    	}else if(seltabIndex === 2){
	    		dataModel = dms.move.selectToLineDataModel;
	    	}
	    	var data=dataModel.getDataByTag(cuid);
	    	if(!data){
	    		 var data = new ht.Data();
	 	         data.setName(name);
	 	         data.a('description', description);
	 	         var bmClassId = cuid.split('-')[0];
	 	         data.a('bmClassId',bmClassId);
	 	         data.setParent(parent);
	 	         data.setTag(cuid);
	 	         data.a('CUID',cuid);
	        	 dataModel.add(data);
	        	 self.isChanged = true;
	        }
	        return data;
	    };
	    
	    function setSegNode(cuid,origPointCuid,origPointName, destPointCuid, destPointName,direction){
	    	var dataModel = null;
	    	var seltabIndex = self.getTabelViewSelectIndex();
	    	if(seltabIndex === 0){
	    		dataModel = dms.move.selectLineDataModel;
	    	}else if(seltabIndex === 2){
	    		dataModel = dms.move.selectToLineDataModel;
	    	}
	    	var data=dataModel.getDataByTag(cuid);
	    	if(data){
	    		//用于分支反转
				data.a('DIRECTION',direction);
				data.a('ORIG_POINT_CUID',origPointCuid);
				data.a('ORIG_POINT_NAME',origPointName);
				data.a('DEST_POINT_CUID',destPointCuid);
				data.a('DEST_POINT_NAME',destPointName);
	        }
	        return data;
	    };
		dms.move.addData = function(data) {
			try {
				if (data) {
					tp.utils.wholeLock();
					var branchCuid = data.relatedBranchCuid,	
						bmClassId = data.className, 
						cuid = data.cuid;
				var isFlag = false;//false代表不是段，选择的是分支或者系统
				var isSeg = dms.branchClassName2ResName[bmClassId];
				if(isSeg){//代表是段
					isFlag = true;
				}
				MapMoveWireRouteAction.getRelatedSystemAndBranchByCuid(branchCuid,cuid,isFlag,
				{
					callback : function(datas) {
						if (datas) {
							var bid = datas.BRANCH_CUID, 
								blabel = datas.BRANCH_LABEL_CN, 
								sid = datas.SYSTEM_CUID, 
								slabel = datas.SYSTEM_LABEL_CN;
							var sysData = create(sid,slabel, null, slabel);
							var branchData = null;
							if (sysData) {
								if (bid) {
									branchData = create(bid, blabel,sysData, blabel);
								}
							}
							var parent = sysData;
							if (branchData) {
								parent = branchData;
							}
							var segs = datas.segs;
							if(segs){
								for(var i=0;i<segs.length;i++){
									var seg = segs[i];
									var cid = seg.CUID,
										name = seg.LABEL_CN,
										ocuid = seg.ORIG_POINT_CUID,
										oname = seg.ORIG_POINT_NAME,
										dcuid = seg.DEST_POINT_CUID,
										dname = seg.DEST_POINT_NAME,
										direction = seg.direction;
									create(cid, name, parent,name);
									setSegNode(cid, ocuid,oname,dcuid,dname,direction);
								}
							}
							self.movetableview.expandAll();
						}
						tp.utils.wholeUnLock();
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
						tp.utils.wholeUnLock();
					}
				});
			}
		} catch (e) {
			tp.utils.wholeUnLock();
		}
	};

	function setMapOnClick(){
//			var self = this;
		var tpmap = Dms.Default.tpmap,
		  	graphView = tpmap.getGraphView();
		
		   tp.Default.DrawObject._drawState = 7;
	        graphView.setEditableFunc(function(data)
			{
	        	return false;
			});
//		        var districtCuid = dms.Default.user.distCuid;
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
	        curInterator.setSeriesTip('请选择管线承载资源');
	        graphView.getView().style.cursor = 'url(' + ctx + '/resources/cursor/cursor_black.cur), pointer';
			map.off('click', createMoveRouteClick);
	        map.on('click', createMoveRouteClick/*dms.Tools.addCallbackArgs(self.createMoveRouteClick,[self])*/);
	};
		
	function createMoveRouteClick(e){
        console.info('move wireroute click');
        if (tp.Default.DrawObject._drawState == 7) { //点选,多个动态图层
        	var reslayers = dms.Default.tpmap.getConfig().map.reslayers;
            var times = reslayers.length;
//            var selectPoints=dms.getSplitPointTypeByResName[tp.Default.DrawObject._drawLineClass];
            var latlng = e.latlng;
            var results = [];

            var reslay = dms.getDuctLineTypeByResName['WIRE_SEG'],
            	layerIds = Dms.Tools.getLayerIdsByTypeName(reslay);
            if(dms.move.isDesigner){
            	for (var i = 0; i < reslayers.length; i++) {
                    var url = reslayers[i].url;
                    tp.utils.identify(url, {
                        geometry: e.latlng.lng + ',' + e.latlng.lat,
                    	layers : layerIds
                    }, dms.Tools.addCallbackArgs(identifyHandler,[url]));
                }
            }else{
            	times = 1;
            	 var url = reslayers[0].url;
                 tp.utils.identify(url, {
                     geometry: e.latlng.lng + ',' + e.latlng.lat,
                 	layers : layerIds
                 }, dms.Tools.addCallbackArgs(identifyHandler,[url]));
            }
        }
        
        function identifyHandler(response) {
	        times--;
	        if (response.error)
	            tp.utils.optionDialog("错误", response.error.message);
	        else {
	        	results = results.concat(response.results);
	        }
	        if (times == 0) {
	            var contextmenu = dms.splitContextmenu;
	            if (contextmenu)
	                contextmenu.hide();
	            if(!contextmenu){
	            	contextmenu = dms.designer.splitContextmenu;
	            }
	            if (results.length == 0) {
	            	
	            }else{
	            	if(dms.designer && dms.designer.segGroupCuid){
	            		var segGroupCuid = dms.designer.segGroupCuid;	            		
	            		if("陕西" === dms.Default.user.systemDistrictName){
	            			for (var i = 0; i < results.length; i++) {
			                    var data = results[i];
			                    var cuid = data.attributes["唯一标识"] || data.attributes["CUID"] || data.attributes["OBJECTID"];
			                 	QuerySeggroupResAction.getResListByCuid(cuid,segGroupCuid,{
			                		callback : function(datas){
			                			if(datas == "true"){
			                				locateLineData(data, contextmenu, latlng);
			                			}else{
			                				tp.utils.optionDialog('温馨提示','资源不在该工单下，不可操作！');
			                			}
			                		}
			                 	});
			            	}
	            		}else{
		            		locateLineDataMenu(results, contextmenu, latlng);
		            	}
	            	}else{
	            		locateLineDataMenu(results, contextmenu, latlng);
	            	}
	            }
	        }
        }
	};
        //定位多数据的选择菜单
    function locateLineDataMenu(results, contextmenu, latlng) {
        var menuItems = [];
        for (var i = 0; i < results.length; i++) {
        	var resultGraphic = results[i];
        	menuItems = locateLineDataOnMap(resultGraphic);
        }
        contextmenu.setItems(menuItems);
        var point = map.latLngToContainerPoint(latlng);
        contextmenu.show(point.x, point.y);
    };
    
    function locateLineData(resultGraphic, contextmenu, latlng){
    	var menuItems = [];
    	menuItems = locateLineDataOnMap(resultGraphic);
        contextmenu.setItems(menuItems);
        var point = map.latLngToContainerPoint(latlng);
        contextmenu.show(point.x, point.y);
    }
    
    function locateLineDataOnMap(resultGraphic){
    	var menuItems = [];
        var cuid = resultGraphic.attributes["唯一标识"] || resultGraphic.attributes["CUID"] || resultGraphic.attributes["OBJECTID"];
        var label = resultGraphic.attributes["中文标识"] || resultGraphic.attributes["LABEL_CN"] || resultGraphic.attributes["GRID_NAME"];
        var className = resultGraphic.attributes.RELATED_BMCLASSTYPE_CUID || cuid.split('-')[0];
        if (className != '') {
        	var systemCuid = resultGraphic.attributes["线路系统ID"] || resultGraphic.attributes["RELATED_SYSTEM_CUID"];
            var branchCuid = resultGraphic.attributes["线路分支ID"] || resultGraphic.attributes["RELATED_BRANCH_CUID"];
            dms.systemCuid = systemCuid;
            dms.branchCuid = branchCuid;
            var item = null;
            if ('UP_LINE_SEG' === className || 'HANG_WALL_SEG' === className) {
                //光缆,引上，挂墙
                item = {
                    label: label,
                    icon: '',
                    items: [{
                        label: "系统",
                        locateSql: "RELATED_SYSTEM_CUID='" + systemCuid + "'",
                        systemCuid: systemCuid,
                        locateClassName: className,
                        labelCn: label,
                        cuid: cuid,
                        action: function(item) {
                        	dms.Default.tpmap.locateByCondition(item.locateClassName, item.locateSql, true);
                        	var cname = systemCuid.split('-')[0];
                            var contextObject = {
                                'cuid': item.cuid,
                                'label': item.labelCn,
                                'className': cname,
                                'relatedBranchCuid' : item.systemCuid,
                                'relatedSystemCuid': item.systemCuid
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            tp.Default.DrawObject._drawState = 7;
                        }
                    }, {
                        label: "段",
                        locateObject: resultGraphic,
                        cuid: cuid,
                        labelCn: label,
                        className: className,
                        action: function(item) {
                        	dms.Default.tpmap.locateOnMap([item.locateObject]);
                        	var locateObject = item.locateObject;
                        	var attr = locateObject.getAttributes();
                        		bCuid = attr.RELATED_BRANCH_CUID || attr.线路分支ID,
                        		sCuid = attr.RELATED_SYSTEM_CUID || attr.线路系统ID;
                            var contextObject = {
                                'cuid': item.cuid,
                                'label': item.labelCn,
                                'className': item.className,
                                'relatedBranchCuid':bCuid,
                                'relatedSystemCuid':sCuid
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            tp.Default.DrawObject._drawState = 7;
                        }
                    }]
                };
            } else if (('DUCT_SEG' === className || 'STONEWAY_SEG' === className || 'POLEWAY_SEG' === className)) {
                //管道段，标石段，杆路段
                item = {
                    label: label,
                    icon: '',
                    items: [{
                        label: "系统",
                        locateSql: "RELATED_SYSTEM_CUID='" + systemCuid + "'",
                        systemCuid: systemCuid,
                        labelCn: label,
                        cuid:cuid,
                        locateClassName: className,
                        action: function(item) {
                        	dms.Default.tpmap.locateByCondition(item.locateClassName, item.locateSql, true);
                        	var cname = systemCuid.split('-')[0];
                            var contextObject = {
                                'cuid': item.cuid,
                                'label': item.labelCn,
                                'className': cname,
                                'relatedBranchCuid':systemCuid,
                                'relatedSystemCuid':systemCuid
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            tp.Default.DrawObject._drawState = 7;
                        }
                    }, {
                        label: "分支",
                        locateSql: "RELATED_BRANCH_CUID='" + branchCuid + "'",
                        locateObject: branchCuid,
                        locateClassName: className,
                        labelCn: label,
                        cuid: cuid,
                        branchCuid: branchCuid,
                        systemCuid: systemCuid,
                        action: function(item) {
                        	dms.Default.tpmap.locateByCondition(item.locateClassName, item.locateSql);
                        	var cname = branchCuid.split('-')[0];
                            var contextObject = {
                                'cuid': item.cuid,
                                'label': item.labelCn,
                                'className': cname,
                                'relatedBranchCuid' : item.branchCuid,
                                'relatedSystemCuid': item.systemCuid
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            tp.Default.DrawObject._drawState = 7;
                        }
                    }, {
                        label: "段",
                        locateObject: resultGraphic,
                        cuid: cuid,
                        className: className,
                        labelCn: label,
                        action: function(item) {
                        	dms.Default.tpmap.locateOnMap([item.locateObject]);
                        	var locateObject = item.locateObject,
                        		attr = locateObject.attributes,
                        	    bCuid = attr.RELATED_BRANCH_CUID || attr.线路分支ID,
                    		    sCuid = attr.RELATED_SYSTEM_CUID || attr.线路系统ID;
                            var contextObject = {
                                'cuid': item.cuid,
                                'label': item.labelCn,
                                'className': item.className,
                                'relatedBranchCuid':bCuid,
                                'relatedSystemCuid':sCuid
                            };
                            tp.Default.OperateObject.contextObject = contextObject;
                            tp.Default.DrawObject._drawState = 7;
                        }
                    }]
                };
            }
        } //是否有格式类型的资源
        
        if (item !== null) {
            menuItems.push(item);
        }
        return menuItems;
    }
};
	
	ht.Default.def('dms.move.moveWireRoutePanel',ht.widget.Panel,{
		moveMap:null,
		importPoint:null,
		tip:'光缆改迁步骤：',
		isChanged:true,
		movetableview:null,
		deleteCBox:null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight - self._config.contentHeight)/2;
			self.setPosition(x-100, y-120);
			dms.move.moveWireRouteView = self.getView();
			document.body.appendChild(self.getView());
		},
		createRoutePanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var tView = self.createWireRouteTabView();
			var bottomPanel = self.createBtnPanel();
			var topPanel = self.createTopPanel();
			var rightPanel = self.createRightPanel();
			
			borderPane.setTopView(topPanel,0);
			borderPane.setBottomView(bottomPanel,40);
			borderPane.setCenterView(tView,300);
			borderPane.setRightView(rightPanel,80);
//			borderPane.getView().style.border = '2px solid red';

//			var moveSplitView = new ht.widget.SplitView(tView,btnPanel,'v',0.88);
//			moveSplitView.setDraggable(false);
			return borderPane;
		},
		createTopPanel : function(){
			var self = this;
			var formPanel = new ht.widget.FormPane();
			//2px solid rgb(7,61,86)---1px solid #FFFFE0
			formPanel.getView().style.border = '1px solid rgb(7,61,86)';
			formPanel.getView().style.borderLeftWidth ='0';
			formPanel.getView().style.borderTopWidth ='0';
			formPanel.getView().style.borderRightWidth ='0';
			
			formPanel.addRow([self.tip],[0.1],23);
			return formPanel;
		},
		createRightPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();

			var upMoveBtn = new ht.widget.Button();
			upMoveBtn.setLabel('上移');
			self.setButton(upMoveBtn);
			upMoveBtn.onClicked = function(){
				var seltabIndex = self.getTabelViewSelectIndex();
		    	if(seltabIndex === 0){
		    		dms.Tools.moveDataToUp(dms.move.selectLineDataModel);
		    	}else if(seltabIndex === 2){
		    		dms.Tools.moveDataToUp(dms.move.selectToLineDataModel);
		    	}
			};
			
			var nextMoveBtn = new ht.widget.Button();
			nextMoveBtn.setLabel('下移');
			self.setButton(nextMoveBtn);
			nextMoveBtn.onClicked = function(){
				var seltabIndex = self.getTabelViewSelectIndex();
		    	if(seltabIndex === 0){
		    		dms.Tools.moveDataToDown(dms.move.selectLineDataModel);
		    	}else if(seltabIndex === 2){
		    		dms.Tools.moveDataToDown(dms.move.selectToLineDataModel);
		    	}
			};
			
			var reverseBtn = new ht.widget.Button();
			reverseBtn.setLabel('反转');
			self.setButton(reverseBtn);
			reverseBtn.onClicked = function(){
				var seltabIndex = self.getTabelViewSelectIndex();
		    	if(seltabIndex === 0){
		    		dms.Tools.berachTrun(dms.move.selectLineDataModel);
		    	}else if(seltabIndex === 2){
		    		dms.Tools.berachTrun(dms.move.selectToLineDataModel);
		    	}
			};
			
			var delBtn = new ht.widget.Button();
			delBtn.setLabel('删除');
			self.setButton(delBtn);
			delBtn.onClicked = function(){
				var seltabIndex = self.getTabelViewSelectIndex();
		    	if(seltabIndex === 0){
		    		dms.Tools.deleteDatas(dms.move.selectLineDataModel);
		    	}else if(seltabIndex === 2){
		    		dms.Tools.deleteDatas(dms.move.selectToLineDataModel);
		    	}
		    	self.isChanged = true;
			};
			
			var importRouteBtn = new ht.widget.Button();
			importRouteBtn.setLabel('导入路由点');
			self.setButton(importRouteBtn);
			importRouteBtn._view.hidden = true;
			self.importPoint = importRouteBtn;
			
			btnPanel.addRow([null],[0.1],25);
			btnPanel.addRow([null],[0.1],25);
			btnPanel.addRow([null],[0.1],25);
			btnPanel.addRow([upMoveBtn],[0.1,0.3],25);
			btnPanel.addRow([nextMoveBtn],[0.1,0.3],25);
			btnPanel.addRow([reverseBtn],[0.1,0.3],25);
			btnPanel.addRow([delBtn],[0.1,0.3],25);
			btnPanel.addRow([importRouteBtn],[0.1,0.3],25);
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';//
			btnPanel.getView().style.borderTopWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			return btnPanel;
		},
		createBtnPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();
			var upBtn = new ht.widget.Button();
			upBtn.setLabel('上一步');
			upBtn.setDisabled(true);
			self.setButton(upBtn);
			
			var nextBtn = new ht.widget.Button();
			nextBtn.setLabel('下一步');
			nextBtn.setDisabled(false);
			self.setButton(nextBtn);
			
			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('确定');
			confirmBtn.setDisabled(true);
			self.setButton(confirmBtn);
			
			/*
			 * 改迁后光缆先不加
			 * var fiberBoxBtn = new ht.widget.Button();
			fiberBoxBtn.setLabel('设置接头盒');
			self.setButton(fiberBoxBtn);
			fiberBoxBtn.onClicked = function(e){
				//将改迁后的光缆设置接头盒
				//写死一个用来测试
				dms.move.wireseglist = dms.move.selectWireSegDataModel.getDatas();
				new dms.move.moveWireRouteFBoxPanel(dms.move.wireseglist).show();
			};*/
			try {
				confirmBtn.onClicked = function(e) {
					//传一个map进去
					var oldDuctlines = [], oldSystemCuids = [], wireSegCuids = [], 
					wireSystemCuids = [], newLayingOutObjs = [], jointBoxCuids = [], 
					isImport = false, isDelete = false;
					var linesMap = {
						'oldLayingOutCuids' : oldDuctlines,
						oldSystemCuids : oldSystemCuids,
						wireSegCuids : wireSegCuids,
						wireSystemCuids : wireSystemCuids,
						newLayingOutObjs : newLayingOutObjs,
						jointBoxCuids : jointBoxCuids
					};

					var oldlinedm = dms.move.selectLineDataModel.getDatas();
					if (oldlinedm) {
						for (var i = 0; i < oldlinedm.size(); i++) {
							var data = oldlinedm.get(i), attrs = data.getAttrObject(), 
							cuid = attrs.CUID, 
							bid = attrs.bmClassId;
							var isSeg = dms.systemClassName2ResName[bid];
							if (isSeg) {//是段
								self.setMoveNodeValue(cuid, oldDuctlines);
							} else {//是系统
								if (cuid.indexOf("BRANCH") == -1) {
									self.setMoveNodeValue(cuid, oldSystemCuids);
								}
							}
						}
					}
					var newlinedm = dms.move.selectToLineDataModel.getDatas();
					if (newlinedm && newlinedm.size()>0) {
						for (var i = 0; i < newlinedm.size(); i++) {
							var data = newlinedm.get(i), 
								attrs = data.getAttrObject(), 
								cuid = attrs.CUID, 
								bid = attrs.bmClassId;
							var isSeg = dms.systemClassName2ResName[bid];
							if (isSeg) {//是段
								self.setMoveNodeValue(cuid, newLayingOutObjs);
							}
						}
					}else{
						tp.utils.optionDialog("错误提示", "没有选择目的承载资源！");
						return;
					}
					var wiresegdm = dms.move.selectWireSegDataModel.sm();
					if (wiresegdm) {
						var selectWireSegs = wiresegdm.getSelection();
						for (var i = 0; i < selectWireSegs.size(); i++) {
							var data = selectWireSegs.get(i),  
								cuid = data.a('CUID'), 
								bid = data.a('bmClassId'), 
								relatedSystemCuid = data.a('RELATED_SYSTEM_CUID');
							self.setMoveNodeValue(cuid, wireSegCuids);
							self.setMoveNodeValue(relatedSystemCuid,wireSystemCuids);
						}
					}

					if(self.deleteCBox.isSelected()){
						isDelete = true;
					}
					MapMoveWireRouteAction.saveMoveWireSeg(linesMap, isImport,isDelete, {
						callback : function(datas) {
							if (datas) {
								//用于设置改迁后增加接头盒
//								dms.move.wireseglist = datas;
								self.clearMoveWireRoute();
								Dms.Default.tpmap.refreshMap();
							}
						},
						errorHandler : function(error){
							tp.utils.optionDialog("错误提示：", error);
							tp.utils.wholeUnLock();
						}
					});
				};
			} catch (e) {
				tp.utils.wholeUnLock();
			}
			var cancelBtn = new ht.widget.Button();
			cancelBtn.setLabel('取消');
			cancelBtn.setDisabled(true);
			self.setButton(cancelBtn);
			
			cancelBtn.onClicked = function(e){
				self.clearMoveWireRoute();
			};
			
			upBtn.onClicked = function(e){
				var tv = dms.move.tabeView,
					selectTab = tv.getCurrentTab();
				var datas = tv.getTabModel().getDatas();
				var selectIndex = datas.indexOf(selectTab);
				var nextTabIndex = 0;
				
				if(selectIndex == 1){
					tv.select(0);
					nextTabIndex = 0;
					upBtn.setDisabled(true);
					nextBtn.setDisabled(false);
					confirmBtn.setDisabled(true);
					cancelBtn.setDisabled(true);
//					self.importPoint._view.hidden = true;//导入路由点暂不需要
					self.deleteCBox._view.hidden = true;
					dms.move.movePanel.setRightWidth(80);
					self.movetableview = dms.move.tablePane.getTableView();
				}else if(selectIndex ==2){
					tv.select(1);
					nextTabIndex = 1;
					upBtn.setDisabled(false);
					nextBtn.setDisabled(false);
					confirmBtn.setDisabled(true);
					cancelBtn.setDisabled(true);
					dms.move.movePanel.setRightWidth(0);
					self.deleteCBox._view.hidden = true;
				}
				self.setTabViewSelectState(nextTabIndex);
			};
			
			nextBtn.onClicked = function(e){
				var tv = dms.move.tabeView,
					selectTab = tv.getCurrentTab();
				var datas = tv.getTabModel().getDatas();
				var selectIndex = datas.indexOf(selectTab);
//				if(!validateData(selectIndex)){
//					return;
//				}
				
				var nextTabIndex = 0;
				if(selectIndex == 0){
					if(self.isChanged){
						
						var dm = dms.move.selectLineDataModel;
						var selLinesegs=dm.getDatas();
						if(!selLinesegs || selLinesegs.size() == 0){
							tp.utils.optionDialog("错误提示", '没有选择要改迁其上光缆的承载资源！');
							return null;
						}
						var list1 = [];
						dm.each(function(data){
							if(data){
								var bid = data.a('bmClassId'),
									cuid = data.a('CUID');
								if(self.contains(bid)){
									list1.push(cuid);
								}
							}
						});
						if(list1.length == 0){
							tp.utils.optionDialog("错误提示", '没有选择要改迁其上光缆的承载资源！');
							return null;
						}
						var isConect = true;
						//用于判断有没有连续的段
						MapMoveWireRouteAction.getIsSkipNext(list1,{
							errorHandler : function(error){
								tp.utils.optionDialog("错误提示：", error);
								tp.utils.wholeUnLock();
								isConect = false;
							},
							async : false
						});
						
						if(!isConect){
							return;
						}
						var list = self.doNext();
						if(!list || list.length == 0){
							tp.utils.optionDialog("温馨提示", '没有选择承载段！');
							return;
						}
					}
					tv.select(1);
					nextTabIndex = 1;
					upBtn.setDisabled(false);
					nextBtn.setDisabled(false);
					confirmBtn.setDisabled(true);
					cancelBtn.setDisabled(true);
					dms.move.movePanel.setRightWidth(0);
					
				}else if(selectIndex == 1){
					var isSelectSeg = self.doSelectWireSeg();
					if(!isSelectSeg){
						tp.utils.optionDialog("温馨提示", '请选择要改迁的光缆！');
						return;
					}
					tv.select(2);
					nextTabIndex = 2;
					upBtn.setDisabled(false);
					nextBtn.setDisabled(true);
					confirmBtn.setDisabled(false);
					cancelBtn.setDisabled(false);
//					self.importPoint._view.hidden = false;
					self.deleteCBox._view.hidden = false;
					dms.move.movePanel.setRightWidth(80);
					self.movetableview = dms.move.tolinetablePane.getTableView();
				}
				self.setTabViewSelectState(nextTabIndex);
			};
			var deleteOldSegCBox = new ht.widget.CheckBox();
			deleteOldSegCBox.setLabel("删除原有路由");
			self.deleteCBox = deleteOldSegCBox;
			self.deleteCBox._view.hidden = true;
			
			btnPanel.addRow([null,deleteOldSegCBox,upBtn,nextBtn,confirmBtn,cancelBtn,null],[0.1,100,0.1,0.1,0.1,0.1,0.1,0.1],[23]);
//			btnPanel.getItemBackground = function(){return '#DAECF4';};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			return btnPanel;
		},
		setMoveNodeValue : function(cuid,arrylist){
			if(arrylist.indexOf(cuid) == -1){
				var m = {
						CUID:cuid
				};
				arrylist.push(m);
			}		
		},
		doNext : function(){
			var self = this;
			var dm = dms.move.selectLineDataModel;
			var selLinesegs=dm.getDatas();
			if(!selLinesegs || selLinesegs.size == 0){
//				tp.utils.optionDialog("错误提示", '没有选择要改迁其上光缆的承载资源！');
				return null;
			}
			var list = [];
			dm.each(function(data){
				if(data){
					var bid = data.a('bmClassId'),
						cuid = data.a('CUID');
					if(self.contains(bid)){
						list.push(cuid);
					}
				}
			});
			if(list.length == 0){
//				tp.utils.optionDialog("错误提示", '没有承载段！');
				return null;
			}
			if(self.isChanged){
				tp.utils.wholeLock();
				try {
					MapMoveWireRouteAction.doSycWireSeg(list, {
						callback : function(datas) {
							if (datas) {
								var arrays = datas.WIRE_SEG;
								for (var i = 0; i < arrays.length; i++) {
									var data = arrays[i];
									var cuid = data.CUID, 
										labelCn = data.LABEL_CN,
										relatedSystemCuid = data.RELATED_SYSTEM_CUID;
									var data = new ht.Data();
									data.setName(labelCn);
									data.a('description', labelCn);
									var bmClassId = cuid.split('-')[0];
									data.a('bmClassId', bmClassId);
									//				 	         data.setParent(parent);
									data.setTag(cuid);
									data.a('CUID', cuid);
									data.a('RELATED_SYSTEM_CUID',relatedSystemCuid);
									dms.move.selectWireSegDataModel.add(data);
								}
								self.isChanged = false;
								//dms.move.selectWireSegDataModel
							}
							tp.utils.wholeUnLock();
						},
						errorHandler : function(error){
							tp.utils.optionDialog("错误提示：", error);
							tp.utils.wholeUnLock();
						},
//						async : true
					});
				} catch (e) {
					tp.utils.wholeUnLock();
				}
			}
			return list;
		},
		doSelectWireSeg : function(){
			var ld = dms.move.selectWireSegDataModel.sm().ld();
			if(ld){
				return ld;
			}
			return null;
		},
		setTabViewSelectState : function(nextTabIndex){
			var tv = dms.move.tabeView,
				tvList = tv.getTabModel().getDatas();
			for(var i=0;i<tvList.size();i++){
				var tab = tvList.get(i),
				tabindex = tvList.indexOf(tab);
				if(tabindex == nextTabIndex){
					tab.setDisabled(false);
				}else{
					tab.setDisabled(true);
				}
			}
		},
		
		createWireRouteTabView : function(){
			var self = this;
			var tabView = new ht.widget.TabView();
			dms.move.tabeView = tabView;
			var ductLinePanel = self.createSelectDuctLinePanel(),
				wireSegPanel = self.createSelectWireSegPanel(),
				routeLinePanel = self.createRouteDuctLinePanel();
			tabView.setSelectBackground('#D26911');
	        
			tabView.add('选择原有管线段',ductLinePanel);
	        tabView.add('选择改迁光缆',wireSegPanel).setDisabled(true);
	        tabView.add('选择改迁管线段',routeLinePanel).setDisabled(true);
	        
	        tabView.select(0);
	        return tabView;
		},
		
		createRouteDuctLinePanel : function(){
			var dm = dms.move.selectToLineDataModel = new ht.DataModel();
			var treeTablePane = new ht.widget.TreeTablePane(dm); 

	        dms.move.tolinetablePane = treeTablePane;
	        var treeTableView = treeTablePane.getTableView(),
	        treeColumn = treeTableView.getTreeColumn();
	        treeColumn.setDisplayName('将光缆迁移到的管线资源名称');
	        treeColumn.setAlign('center');
	        treeColumn.setWidth(600);

			return treeTablePane;
		},
		
		createSelectWireSegPanel : function(){
			var wireSegPanel = new ht.widget.FormPane();
			var dm = dms.move.selectWireSegDataModel = new ht.DataModel();
			var listView = new ht.widget.ListView(dm);
			listView.setCheckMode(true);  
//            listView.setSelectionModelShared(false);
//            listView.getSelectionModel().setSelectionMode('single'); 
			wireSegPanel.addRow([listView],[0.1],200);
			
			return wireSegPanel;
		},
		
		createSelectDuctLinePanel : function(){
			var self = this;
			var dm = dms.move.selectLineDataModel = new ht.DataModel();
			var treeTablePane = new ht.widget.TreeTablePane(dm); 
	        dms.move.tablePane = treeTablePane;
	        var treeTableView = treeTablePane.getTableView(),
	        treeColumn = treeTableView.getTreeColumn();
	        treeColumn.setDisplayName('需要迁移光缆的承载管线资源名称');
	        treeColumn.setAlign('center');
	        treeColumn.setWidth(600);
	        self.movetableview = dms.move.tablePane.getTableView();
			return treeTablePane;
		},

		clearMoveWireRoute : function(){
			tp.utils.wholeUnLock();
			Dms.Default.tpmap.reset();
			if(dms.move.moveWireRouteView){
				document.body.removeChild(dms.move.moveWireRouteView);
				dms.move.moveWireRouteView = null;
			}
			if(tp.Default.OperateObject.curInterator){
				tp.Default.OperateObject.curInterator.reset();
			}
			if(dms.move.movePanel){
				dms.move.movePanel = null;
			}
			if(dms.move.selectLineDataModel){
				dms.move.selectLineDataModel = null;
			}
			if(dms.move.selectToLineDataModel){
				dms.move.selectToLineDataModel = null;
			}
			if(dms.move.selectWireSegDataModel){
				dms.move.selectWireSegDataModel = null;
			}
			if(dms.move.tablePane){
				dms.move.tablePane = null;
			}
			if(dms.move.tolinetablePane){
				dms.move.tolinetablePane = null;
			}
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
	    contains : function(bid)
	    {
	    	var bclassname = dms.branchClassName2ResName[bid];
    		if(bclassname)
    			return true;
	    	return false;
	    },
		getTabelViewSelectIndex : function(){
			var tv = dms.move.tabeView,
			selectTab = tv.getCurrentTab();
			var tvdatas = tv.getTabModel().getDatas();
			var selectIndex = tvdatas.indexOf(selectTab);
			return selectIndex;
		}
	});

})(this,Object);