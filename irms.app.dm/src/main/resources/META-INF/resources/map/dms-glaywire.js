/** * 
 */

(function(){
	//图形化敷设光缆
	dms.glaywire.createGlayWireSystemPane = function(code){
		var c = createGlayWirePane(code, null, "glaywire");
		var panel = new ht.widget.Panel(
				{
					title : "图形化敷设光缆",
					width : 600,
					exclusive : false,
					titleColor : "white",
					minimizable : true,
					minimize : false,//控制打开时界面是不是最小化
					expand : true,
					narrowWhenCollapse : true,
					contentHeight : 530,
					buttons:['minimize',{
						name : '关闭',
						toolTip:'关闭',
						icon:'close.png',
						action:function(){
							clearDmsGlayWireUnkown();
						}
					}],
					content : c
				});
	
		panel.setPosition(200, 10);
		dms.Default.GlayPaneView = panel.getView();
		document.body.appendChild(panel.getView());
		panel.setSelectValue = function(){
			c.setSelectValue();
		};
		return panel;
	};
	
	function createGlayWirePane(code,dialog,useName){
		var glaywirePane = new ht.widget.FormPane();
		//给FormPane设置进度条
	//	glaywirePane.setAutoHideScrollBar(false);
		glaywirePane.getItemBorderColor = function(){return 'gray';};
		var ductlinePanel = new dms.utils.createWireToDuctLinePanel(code, null, "glaywire");
		var selectPanel = createSelectLinePane();
		var picPane = createPicPane();
	//	var bottomPane = dms.glaywire.createBottomPane();
		glaywirePane.addRow([ductlinePanel],[0.1],210);
		glaywirePane.addRow([selectPanel],[0.1],170);
		glaywirePane.addRow([picPane],[0.1],100);
	//	glaywirePane.addRow([bottomPane],[0.1],30);
		var confirmBtn = tp.utils.createButton('', '保存'),
		cancelBtn = tp.utils.createButton('', '取消');
		
		confirmBtn.onclick = function(e){
			var treeDataModel = ductlinePanel.getLineDataModel(),
			tableDataModel = selectPanel.getLineDataModel();
			
			getModifySystems(treeDataModel,tableDataModel);
		};
		    
		cancelBtn.onclick = function(e){
			clearDmsGlayWireUnkown();
	    };
	    glaywirePane.addRow([null,confirmBtn,cancelBtn],[0.1, 60,60],[24]);
	    
		glaywirePane.setPadding(0);
		glaywirePane.setSelectValue = function(){
			selectPanel.setSelectValue();
		};
		return glaywirePane;
	};
	//图形化敷设光缆选择管线段和管孔子孔表单
	function createSelectLinePane(){
		var selectPane = new ht.widget.FormPane();
		var _sysInput = tp.utils.createInput('','input');
	//	_sysInput.value = '';
		var _branchInput = tp.utils.createInput('','input');
		
		var startComboBox = new ht.widget.ComboBox();
		startComboBox.getView().style.border = 'solid 1px black';
		startComboBox.setHeight(22);
		startComboBox.setValue("");
		    
		var endComboBox = new ht.widget.ComboBox();
		endComboBox.getView().style.border = 'solid 1px black';
		endComboBox.setHeight(22);
		endComboBox.setValue("");
		
	//	endComboBox.setEditable(true);
	//	endComboBox.setIcons([ 'WIRE_SEG', 'TRANS_ELEMENT', 'SITE' ]);
		selectPane.addRow(["管线系统名称",_sysInput,"管道分支名称",_branchInput],[0.2,0.5,0.2,0.5],22);
		selectPane.addRow(["管道段起始点",startComboBox,"管道段终止点",endComboBox],[0.2,0.5,0.2,0.5],22);
	
		var dataModel = new ht.DataModel(),                       
		selectTablePane = new ht.widget.TablePane(dataModel);
		selectPane.addRow([selectTablePane],[1],100);
		addColumns(selectTablePane);
		
		startComboBox.onValueChanged = function(){
			processComboBoxChange(startComboBox,endComboBox,dataModel);
		};
		
		endComboBox.onValueChanged = function(){
			processComboBoxChange(startComboBox,endComboBox,dataModel);  
		};
		
		selectTablePane.getTableView().onDataDoubleClicked = function(data){
	    	var gdo = dataModel.sm().ld();
	    	var segCuid = gdo.getAttr("CUID");
	    	Dms.sectionPic.getHolePicBySegCuid(segCuid,dms.Default.picGv,null);
	    	//dms.Default.picGv.setTranslate(-300, -210);
	    	dms.Default.picGv.fitContent();
	//    	var data = dms.Default.picGv.getDataAt(dbo);
	    	
	    	dms.Default.picGv.addInteractorListener(function (e) {
	    		if(e.kind === 'doubleClickData'){
	    			var dbo = dataModel.sm().ld();//gdo;
	    			var edata = e.data;
	    			var dataCuid = edata._id;
	    			var cname = dataCuid.split("-")[0];
	    			
	    			if(cname === "DUCT_HOLE"){
	    				var holeNum = edata._name;
	    				
	    				dbo.a("HOLE_CUID",dataCuid);
	        			dbo.a("HOLE_NUM",holeNum);
	        			var childHoleCuid = dbo.getAttr("CHILD_HOLE_CUID"),
	        			childHoleNum = dbo.getAttr("CHILD_HOLE_NUM");
	        			if(childHoleCuid){
	        				dbo.a("CHILD_HOLE_CUID","");
	            			dbo.a("CHILD_HOLE_NUM","");
	        			}
	    			}else if(cname === "DUCT_CHILD_HOLE"){
	    				var childHoleNum = edata.getAttr("DUCT_CHILD_HOLD_NUM"),
	    				holeCuid = edata.getAttr("RELATED_HOLE_CUID"),
	    				holeNum = edata.getAttr("RELATED_HOLE_NUM");//此处需要拓扑增加管孔的名称，此处先暂时设置为1
	    				dbo.a("CHILD_HOLE_CUID",dataCuid);
	        			dbo.a("CHILD_HOLE_NUM",childHoleNum);
	        			dbo.a("HOLE_CUID",holeCuid);
	    				dbo.a("HOLE_NUM",holeNum);
	    			}
	    	    }  
	    	});
	
	    };
	    selectPane.setSelectValue = function(){
	    	var wireSegCuid = tp.Default.OperateObject.contextObject.cuid,
	   	 	lineCuid = tp.Default.OperateObject.contextDuctLineObject.cuid;
	    	
	    	var lineUrl = ctx+'/rest/MapRestService/getGlayLayWireSelectLine/'+lineCuid+"?time="+new Date().getTime();
	    	$.ajax({
	    		url : lineUrl,
	    		success : function(data){
	    			if(data && data.selectList){
	    				var datas = data.selectList;
	    				//datas是选择的系统或者分支，只有一个值
						for(var i=0;i<datas.length;i++){
							var data = datas[i];
							var systemCuid = data.SYSTEM_CUID,
							systemName = data.SYSTEM_NAME;
							_sysInput.value = systemName;
							var branchCuid = data.BRANCH_CUID;
							if(branchCuid){
								var branchName = data.BRANCH_NAME;
								_branchInput.value = branchName;
							}else{
								_branchInput.value = "";
							}
							var points = data.pointList;
							if(points){
								var cuids = new Array(),
								names = new Array();
								for(var j=0;j<points.length;j++)
								{
									var pointData = points[j];
									var pointCuid = pointData.POINT_CUID,
									pointName = pointData.POINT_NAME;
									cuids.push(pointCuid);
									names.push(pointName);
								}
								if(cuids){
									startComboBox.setValues(cuids);
									endComboBox.setValues(cuids);
								}
								if(names){
									startComboBox.setLabels(names);
									endComboBox.setLabels(names);
								}
							}
							var lineSegs = data.lineSegList;
							if(lineSegs){
								dms.Default.GlayWireSelectLineSegs = lineSegs;
							}
						}
	    			}else if(data && data.error){
	    				tp.utils.optionDialog('温馨提示',data.error);
	    				return;
	    			}
	    		},
	    		error : function(e){
	    			
	    		},
	    		dataType : 'json',
				type : 'POST'
				
	    	});
	    };
	    selectPane.getLineDataModel = function(){
	    	return dataModel;
	    };
		return selectPane;
	};
	
	function createPicPane(segCuid){
	//	var picPane = Dms.sectionPic.createSectionPicPanel(segCuid);
		var picPane = new ht.widget.FormPane(); 
	    
		var gv = this._gv = new ht.graph.GraphView();
		gv.setEditable(false);
		gv.isMovable = function(data)
		{
			return false;
		};
	
	//	Dms.sectionPic.getHolePicBySegCuid(segCuid,dms.Default.picGv,picPane);
		picPane.addRow([gv],[0.1],[0.1]);
		dms.Default.picGv = gv;
		return picPane;
	};
	
	addColumns = function(table){
	    var cm = table.getColumnModel();
	   
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('ORIG_POINT_NAME');
	    column.setWidth(100);
	    column.setDisplayName('起点名称');
	    cm.add(column);
	    
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('DEST_POINT_NAME');
	    column.setWidth(100);
	    column.setDisplayName('终点名称');
	    cm.add(column);
	    
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('HOLE_NUM');
	    column.setWidth(100);
	    column.setDisplayName('管孔编号');
	    cm.add(column);
	    
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('CHILD_HOLE_NUM');
	    column.setWidth(100);
	    column.setDisplayName('子孔编号');
	    cm.add(column);
	  
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('CARRYING_NUM');
	    column.setWidth(100);
	    column.setDisplayName('吊线编号');
	    cm.add(column);
	    
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('DIRECTION_NUM');
	    column.setWidth(100);
	    column.setDisplayName('敷设方向');
	    cm.add(column);
	};
	
	dms.glaywire.selectLine = function(wireSegCuid,lineCuid){
		 dms.Default.GlayWireSystemPane.setSelectValue();
	};
	
	processComboBoxChange = function(startComboBox,endComboBox,dataModel){
		if(startComboBox._value=="" || endComboBox._value == ""){
			return;
		}
	//	startComboBox.getValues().indexOf(startComboBox._value)
		//startComboBox.toLabel(startComboBox._value);
	    var startInx = startComboBox.getValues().indexOf(startComboBox._value);
	    var endInx = endComboBox.getValues().indexOf(endComboBox._value);
	    var lineSegs = dms.Default.GlayWireSelectLineSegs;
	    if(startInx>=0 && endInx>=0){
	        if (startInx == endInx) {
	        	dataModel.clear();
	        } else if (startInx < endInx) {
	        	dataModel.clear();
	            for (var i = startInx; i < endInx; i++) {
	            	var dbo = lineSegs[i]; 
	            	var node = addNewNode(dbo,true);
	            	var data = dataModel.getDataById(node._id);
	            	if(!data){
	            		dataModel.add(node);
	            	}
	            }
	        } else {
	        	dataModel.clear();
	            for (var i = startInx - 1; i >= endInx; i--) {
	            	var dbo = lineSegs[i]; 
	            	var node = addNewNode(dbo,false);
	            	var data = dataModel.getDataById(node._id);
	            	if(!data){
	            		dataModel.add(node);
	            	}
	            }
	        }
	    }
	};
	
	addNewNode = function(dbo,ispositiveOrientation){
	
		var node = new ht.Node();
		var segCuid = dbo.SEG_CUID,
		segName = dbo.SEG_NAME,
		origPointCuid = dbo.ORIG_POINT_CUID,
		origPointName = dbo.ORIG_POINT_NAME,
		destPointCuid = dbo.DEST_POINT_CUID,
		destPointNmae = dbo.DEST_POINT_NAME,
		direction = dbo.DIRECTION,
		systemCuid = dbo.SYSTEM_CUID,
		branchCuid = dbo.BRANCH_CUID;
		if(ispositiveOrientation){//代表正方向
			direction = "1";
		}
		var directionNum = "正向";
		if(!direction){
			direction = "1";
		}
		if(direction =="1"){
			directionNum ="正向";
		}else{
			directionNum = "反向";
		}
		node.setId(segCuid);
		node.setName(segName);
		node.a("CUID",segCuid);
		node.a("LABEL_CN", segName);
		node.a("ORIG_POINT_CUID",origPointCuid);
		node.a("ORIG_POINT_NAME",origPointName);
		node.a("DEST_POINT_CUID",destPointCuid);
		node.a("DEST_POINT_NAME",destPointNmae);
		node.a("HOLE_CUID","");
		node.a("HOLE_NUM","");
		node.a("CHILD_HOLE_CUID","");
		node.a("CHILD_HOLE_NUM","");
		node.a("CARRYING_CUID","");
		node.a("CARRYING_NUM","");
		node.a("DIRECTION",direction);
		node.a("DIRECTION_NUM",directionNum);
		node.a("LINE_SYSTEM_CUID",systemCuid);
		node.a("LINE_BRANCH_CUID",branchCuid);
		node.a("LINE_SEG_CUID",segCuid);
		return node;
	
	};
	
	clearDmsGlayWireUnkown = function(){
	
		tp.Default.DrawObject._drawState = 1;
		dms.Default.GlayWireSelectLineSegs={};
		dms.Default.picGv = null;
		Dms.Default.tpmap.refreshMap();
		Dms.Default.tpmap.reset();
		dms.Default.GlayWireSystemPane = null;
		if(dms.Default.GlayPaneView){
			document.body.removeChild(dms.Default.GlayPaneView);
			dms.Default.GlayPaneView = null;
		}
	};
	
	getModifySystems = function(treeDataModel,tableDataModel){
	
		//将两个datamodel中的值放到一个新的datamodel中，然后修改
		var dm = new ht.DataModel();
		
		getDataModels(treeDataModel,dm);
		getDataModels(tableDataModel,dm);
		var elements = dm.getDatas();
		var result = "{\"segList\":[";
		var count = elements.size();
	    for (var i = 0; i < count; i++) {
	    	var node = elements.get(i);
	    	
	    	var cuid = node.getAttr("CUID");
	    	var cname = cuid.split("-")[0];
	    	if(cname == "DUCT_SYSTEM" || cname == "POLEWAY_SYSTEM" || cname == "STONEWAY_SYSTEM" || cname == "UP_LINE" || cname == "HANG_WALL"){
	    		var indexInRouteBegin = node.getAttr("indexInRouteBegin"),
	    		indexInRouteEnd = node.getAttr("indexInRouteEnd");
	    		result = result+"{";
	    		result = result+"\"CUID\":"+"\""+cuid+"\",";
	    		result = result+"\"indexInRouteBegin\":"+"\""+indexInRouteBegin+"\",";
	    		result = result+"\"indexInRouteEnd\":"+"\""+indexInRouteEnd+"\"";
	    		result = result+"}";
	    	}else{
	    		result = result+"{";
	    		var lineSystemCuid = node.getAttr("LINE_SYSTEM_CUID"),
	        	lineBranchCuid = node.getAttr("LINE_BRANCH_CUID"),
	        	lineSegCuid = node.getAttr("LINE_SEG_CUID"),
	        	origPointCuid = node.getAttr("ORIG_POINT_CUID"),
	        	destPointCuid = node.getAttr("DEST_POINT_CUID"),
	        	holeCuid = node.getAttr("HOLE_CUID"),
	        	childHoleCuid = node.getAttr("CHILD_HOLE_CUID"),
	        	carryingCuid = node.getAttr("CARRYING_CUID"),
	        	direction = node.getAttr("DIRECTION");
	
	        	result = result+"\"CUID\":"+"\""+cuid+"\",";
	    		result = result+"\"LINE_SYSTEM_CUID\":"+"\""+lineSystemCuid+"\",";
	    		result = result+"\"LINE_BRANCH_CUID\":"+"\""+lineBranchCuid+"\",";
	    		result = result+"\"LINE_SEG_CUID\":"+"\""+lineSegCuid+"\",";
	    		result = result+"\"ORIG_POINT_CUID\":"+"\""+origPointCuid+"\",";
	    		result = result+"\"DEST_POINT_CUID\":"+"\""+destPointCuid+"\",";
	    		result = result+"\"HOLE_CUID\":"+"\""+holeCuid+"\",";
	    		result = result+"\"CHILD_HOLE_CUID\":"+"\""+childHoleCuid+"\",";
	    		result = result+"\"CARRYING_CUID\":"+"\""+carryingCuid+"\",";
	    		result = result+"\"DIRECTION\":"+"\""+direction+"\"";
	    		result = result+"}";
	    	}
			if(i<count-1){
				result = result+",";
			}
	    }
	    result = result+"]}";
	    var wiretoductlineJson = new Object();
		wiretoductlineJson['wireductlinelist'] = result;
	//    modifyGlayWireTuDuctLine
	    var wireSegCuid = tp.Default.OperateObject.contextObject.cuid;
		var lineUrl = ctx+'/rest/MapRestService/modifyGlayWireTuDuctLine/'+wireSegCuid+"?time="+new Date().getTime();
		$.ajax({
			url : lineUrl,
			success : function(data){
				if(data && data.success){
					tp.utils.optionDialog('温馨提示','光缆敷设完成!');
				}else{
					tp.utils.optionDialog('错误','光缆敷设失败!');
				}
				clearDmsGlayWireUnkown();
			},
			error : function(e){
				tp.utils.optionDialog('错误','光缆敷设失败!');
				clearDmsGlayWireUnkown();
			},
			data : wiretoductlineJson,
			dataType : 'json',
			type : 'POST'
			
		});
		return dm;
	
	};
	function getDataModels(dataModel,dm){
		var elements = dataModel.getDatas();
		for(var i=0;i<elements.size();i++){
			var oldNode = elements.get(i);
			var jsonDm = oldNode.getDataModel(); 
			var jsonSerializer = new ht.JSONSerializer(jsonDm),
			jsonDeserialize = new ht.JSONSerializer(dm);
			//序列化时重载是否序列化，可以将node序列化
			jsonSerializer.isSerializable  = function(data)
			{
				var nodeId = data.getAttr("CUID"),
				nodeName = data.getAttr("LABEL_CN");
				data.setId(nodeId);
				data.setName(nodeName);
		    	var nodeClassName = nodeId.split("-")[0];
		    	if(nodeClassName == "DUCT_SYSTEM" || nodeClassName == "POLEWAY_SYSTEM" || nodeClassName == "STONEWAY_SYSTEM" || nodeClassName == "UP_LINE" || nodeClassName == "HANG_WALL"){
		    		var needToLoad = data.needToLoad;
		    		var node = dm.getDataById(nodeId);
		    		if(needToLoad==true && !node){
		    			return true;
		    		}
		    		return false;
		    	}else if(nodeClassName == "DUCT_BRANCH" || nodeClassName == "POLEWAY_BRANCH" || nodeClassName == "STONEWAY_BRANCH"){
		    		return false;
		    	}else if(nodeClassName == "DUCT_SEG" || nodeClassName == "POLEWAY_SEG" || nodeClassName == "STONEWAY_SEG" || nodeClassName == "UP_LINE_SEG" || nodeClassName == "HANG_WALL_SEG"){
		    		var node = dm.getDataById(nodeId);
		    		if(!node){
		    			return true;
		    		}
		        	return false;
		    	}
			}; 
			jsonDeserialize.deserialize(jsonSerializer.serialize(),null,true);
		}
	}
})();
