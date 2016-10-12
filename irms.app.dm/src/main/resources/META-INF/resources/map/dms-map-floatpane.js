//悬浮的操作按钮
$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');
$importjs(ctx+'/dwr/interface/DuctLineAction.js');
$importjs(ctx+'/dwr/interface/DuctLineDatasAction.js');
(function(){
	"use strict";
	
	Dms.Panel.FloatPane = function(){
		var formPane = this;
		Dms.Panel.FloatPane.superClass.constructor.apply(formPane);
		
        formPane.setPadding(2);
        formPane.setHGap(2);
        formPane.setWidth(70);
        formPane.setHeight(150);
        formPane.getView().style.top = '100px';
        formPane.getView().style.right = '200px';
        formPane.getView().style.background = 'rgba(255, 255, 255, 0.5)';
        
        formPane._lines = [];
	};
	ht.Default.def('Dms.Panel.FloatPane',ht.widget.FormPane,{
		selectedRes : {},
		initSegGroupResdata : function(){
			var self = this;
			if(dms.designer){
				var segGroupCuid = dms.designer.segGroupCuid;
				if(segGroupCuid){
					var isSplit = dms.designer.isSplit,
					scene = dms.designer.scene;
					QuerySeggroupResAction.getResBySeggroupCuid(segGroupCuid,isSplit,scene,function(data){
						if(data && data.length > 0)
			    		{
			    			for (var i = 0; i < data.length; i++) {
			    				var child = data[i];
			    				var className = child['CLASS_NAME'];
			    				var	htdata = new ht.Node(child.CUID);
			    				var length = child.LENGTH;
			    				if(length)
			    				{
			    					htdata.setId(child.CUID);
			    					htdata.a('bmClassId',className);
			    					htdata.a('LENGTH',length);
			    					self._lines.push(htdata);		
			    				}
			    			};  
			    			self.initStaticsView();
			    		}
					});
				}
			}
		},
		initStaticsView : function(){
			var formPane = this;
			console.info(formPane._lines);
			formPane.clear();
			formPane.addRow(["资源统计："],[0.1],16);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			var lines  = tp.Default.DrawObject._drawLineList;
			var existsLines =  self._lines;
			//{'MANHLE':{'COUNT':1,'LENGTH':2},'WIRE_SEG':{}}
			var result = {};
			var newlines = lines.concat(formPane._lines);
			for(var i = 0 ;i < newlines.length ; i++)
			{
				var line = newlines[i];
				var length = parseFloat(line.a('LENGTH'));
				var bmClassId = line.a('bmClassId');
				var isold = line.a('ISOLD');
				if(isold !== '0')
				{
					if(result[bmClassId])
					{
						result[bmClassId].COUNT ++;
						result[bmClassId].LENGTH += length;
					}else
					{
						result[bmClassId]={};
						result[bmClassId].COUNT = 1;
						result[bmClassId].LENGTH = length;
					}
				}
			}
			var i = 0;
			for(var p in result)
			{
				var className = Dms.layernameConfig[p];
				var count = result[p].COUNT;
				var length = result[p].LENGTH;
				length = L.Util.formatNum(length, 2);
				if(className)
				{	
					if(p === 'WIRE_SEG')
						formPane.addRow([className+" 长度:"+length+"米"],[0.1],20);
					else
						formPane.addRow([className+count+"段 长度:"+length+"米"],[0.1],20);
					i++;
				}
			}
			if(i === 0)
			{
				formPane.addRow(["光缆段 长度:0米"],[0.1],20);
				formPane.addRow(["管道段 长度:0米"],[0.1],20);
				formPane.addRow(["杆路段 长度:0米"],[0.1],20);
				formPane.addRow(["直埋段 长度:0米"],[0.1],20);
				formPane.addRow(["引上段 长度:0米"],[0.1],20);
				formPane.addRow(["挂墙段 长度:0米"],[0.1],20);
			}
		},
		initOperateView : function(tip,isDesigner){
			var formPane = this;
			formPane.clear();
			formPane.addRow(["新增类型："],[0.1],16);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			
			var lineClass = tp.Default.DrawObject._drawLineClass,
				lineType = dms.isHideContextMenu[lineClass],
				lineItems = lineType.split(",");
			var height = 35;
			var items = [];
			if(isDesigner){
				items = dms.designer.contextMenu['ADD_LINE'][4].items;
			}else{
				items = dms.Default.contextMenu['ADD_LINE'][4].items;
			}
	    	for(var i = 0 ; i < items.length ; i++)
	    	{
	    		if(lineItems.indexOf(items[i].lineId) !== -1)
	    		{	
		    		formPane.addRow([{
		                unfocusable: true,
		                button: {
		                    label: items[i].label,
		                    width: 70,
		                    action : items[i].action,
		                    togglable:true,
		                    selected: tp.Default.DrawObject._drawPointClass == items[i].lineId,
		                    groupId: 'A',
		                    icon : items[i].icon,
		                    onClicked: function(e){
		                    	this.action.call(this,tip);
		                    }
		                }
		            }],[0.1], 20);
		    		height += 25;
	    		}
	    	}
	    	formPane.setHeight(height);
		},
		
		isAllowEdit : function(seg){
			var allowEditSegs = dms.designer.segs;
			for(var cuid in seg){
				if(allowEditSegs.indexOf(cuid) === -1){
					return false;
				}
			}
			return true;
		},
		
		showDeleteDialog : function(resObject,tip,button){
			var self = this;
			var dialog = new ht.widget.Dialog();
                dialog.setConfig({
                    title: "资源删除确认",
                titleAlign: "left",
                closable: false,
                draggable: true,
                height : 150,
                width : 230,
                contentPadding: 35,
                content: "<p>"+ tip +"</p>",
                buttons : button,
                buttonsAlign: "center",
                action: function(item, e) {
                	if(item.label === '是' || item.label === '确定'){
                		//判断如果是线，是不是要删除点
                		self.deleteResFlow(resObject.CUID,true);
                	}else if(item.label ==='否'){
                		self.deleteResFlow(resObject.CUID,false);
                	}
                	tp.Default.OperateObject.contextObject = null;
					Dms.Default.tpmap.reset();
					Dms.Default.tpmap.refreshMap();
					self.close();
                	dialog.hide();
                }
            });
            dialog.show();
		},
		deleteResFlow : function(cuid,flag){
			var self = this;
			//添加单位工程id参数，判断要删除的资源是否属于本单位工程
			var segGroupCuid=dms.designer.segGroupCuid;
			var scene = dms.designer.scene;
			QuerySeggroupResAction.deleteResFlow(cuid,flag,segGroupCuid,scene,{
				callback : function(data){
					if(data){
						var dataModels = dms.selectData;
						if(dataModels){
							dataModels.remove(dataModels.sm().ld());
						}
						tp.utils.optionDialog('温馨提示','删除数据成功！');
					}else{
						tp.utils.optionDialog('温馨提示','删除数据失败！');
					}
					self.close();
				},
				errorHandler : function(error){
					tp.utils.optionDialog('温馨提示','删除失败：' + error);
					self.close();
					return;
				},
				async : true
			});
		},
		showViewProperty : function(isDesignRes,resObject){
			var scene = dms.designer.scene;
			if(scene === "backoutScene"){//退网流程时控制查看属性面板无编辑操作
				isDesignRes = false;
			}
			var self = this;
			var dataModel = new ht.DataModel();
        	var data = new ht.Node();
        	
        	for(var p in resObject){
			    self.selectedRes[p] = resObject[p];
			    if(resObject[p] instanceof Date){
			    	 data.a(p,Dms.Utils.dateToLabel(resObject[p].toString()));
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
        	if(dms.designer){
        		var isRead = dms.designer.isRead;
        		if(isRead == "1"){
        			isDesignRes = false;
        		}
        	}
			dialog.setConfig({
	            title: "<html><font size=2>查看属性</font></html>",
	            width: 600,
	            height: 350,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            content: propertyPane.getView(),
	            buttons:isDesignRes?buttomBtns:null,
	            buttonsAlign : 'center',
	            action : function(item,e){
	            	if(item.label === '保存'){
	            		var cuid = data._attrObject.CUID;
	            		var isFlag = Dms.Tools.getIsOperate(isDesignRes, cuid);
	            		var length=data._attrObject.LENGTH.length;
	            		if(length>13){
	            			tp.utils.optionDialog('温馨提示','长度大于为此列指定的允许精度！');
	            			return;
	            		}
	                  	if(isFlag){
            				var param = {
            						paras : '['+JSON.stringify(data._attrObject)+']'
        	            		};
            				DuctLineDatasAction.saveResource(param,{//DuctLineAction.saveRes
            					callback : function(result){
            						if(result && result.resObject){
            							self._resObject = result.resObject;
            							tp.utils.optionDialog('温馨提示','保存成功！');
            							dialog.hide();
            							Dms.Default.tpmap.reset();
            							var resName = self._resObject.LABEL_CN;
            							if(scene && scene ==="ViewpointScene"){
            								//指定需求点时，保存后刷新需求点的名称
            								if(dms.Default.pointSelectDM){
            									var datas=dms.Default.pointSelectDM.getDatas();
            									if(datas){
            										var pointSelect=datas.get(0);
            										pointSelect.a('LABEL_CN',resName);
            									}
            								}
            							}
            						}
            					},
        	            		errorHandler : function(error){
        	            			tp.utils.optionDialog('温馨提示','保存失败：'+error);
        	            			dialog.hide();
        	            		},
        	            		async : false
        	            	});
//        	            	DuctLineAction.updateRes(param,{
//        	            		callback : function(result){
//        	            			if(result && result.resObject){
//        	            				self._resObject = result.resObject;
//        	            				tp.utils.optionDialog('温馨提示','保存成功！');
//        	            				dialog.hide();
//        	            			}
//        	            		},
//        	            		errorHandler : function(error){
//        	            			tp.utils.optionDialog('温馨提示','保存失败：'+error);
//        	            			dialog.hide();
//        	            		},
//        	            		async : false
//        	            	});
	                  	}
	            	}else{
	            		data._attrObject = self.selectedRes;
	            		dialog.hide();
	            	}
	            }
			});
        	dialog.show();
		},
		
		initResOperateView : function(isDesignRes,resObject){
			var formPane = this;
			formPane._resObject = resObject;
			formPane.setWidth(100);
			formPane.clear();
			formPane.addRow(["资源操作："],[0.1],16);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			var height = 35;
			var cid = resObject.CUID,
			cname = cid.split('-')[0];
			
			var isLine = dms.systemClassNameResName[cname],
				isPoint = dms.designer.isResourcePoint[cname];
			var delBtn = [{
				unfocusable: true,
				button: {
					label : '删除',
					width : 70,
					togglable: true,
					onClicked : function(){
		        		if(!isDesignRes){
		        			tp.utils.optionDialog('温馨提示','不允许删除现网库资源！');
		        		}else{
		        			var button = [{
		                        label: "是"
		                    }, {
		                        label: "否"
		                    },{
		                        label: "取消"
		                    }];
		        			var tip = '删除线后是否删除独立点?';
		        			if(!isLine){
		        				tip = '是否确定删除？';
		        				button = [{
			                        label: "确定"
			                    },{
			                        label: "取消"
			                    }];
		        			}
		            		var isFlag = Dms.Tools.getIsOperate(isDesignRes, cid);
		                  	if(isFlag){
                				formPane.showDeleteDialog(formPane._resObject,tip,button);
		                  	}
		        		}
					}
				}
			}];
			
			var viewBtn = [{
				unfocusable: true,
				button: {
					label : '查看属性',
					width : 70,
					togglable:false,
					onClicked : function(){
			        	if(resObject){
							if(resObject.className ==="SITE" && dms.designer.scene ==="ViewpointScene"){
								var self = this;
								var dataModel = new ht.DataModel();
								var data = new ht.Node();
								for(var p in resObject){
									if(resObject[p] instanceof Date){
										 data.a(p,Dms.Utils.dateToLabel(resObject[p].toString()));
									}else{
										if(p ==='LONGITUDE' || p === 'LATITUDE'){
											data.a(p,resObject[p]+"");
										}else{
											data.a(p,resObject[p]);
										}
									}
								}
								data.a('bmClassId',resObject.className);
								dataModel.add(data);
								dataModel.sm().ss(data);
								new Dms.Panel.PropertyPane(dataModel, "", true).show();
							}else{
			        		formPane.showViewProperty(isDesignRes,formPane._resObject);
							}
			        	}
					}
				}
			}];
			var isflag = false;
			if(cname){ //&& cname != 'SITE'
				isflag = true;
			}
			var isRead = dms.designer.isRead;//0 可操作，!0 可查看
			if(isLine || isPoint){
				//系统和点有删除按钮，段不显示
				if(isRead == '0' && isflag){
					formPane.addRow(delBtn,[0.1],20);
				}
			}
			
			if(isflag){
				formPane.addRow(viewBtn,[0.1],20);
			}
			
			var fiberLinkBtn = [{
				unfocusable: true,
				button: {
					label : '纤芯接续图',
					width : 70,
					togglable:false,
					onClicked : function(){
			        	if(resObject){
			        		var viewParam={
	 	       	        	   'cuid':resObject.CUID,
	 	       	        	   'mapType':"WireSystemLinkSectionTopo",
	 	       	        	   'mapName':"纤芯接续图"
			 	       	    };
			 	    	    Dms.Tools.showSectionView(viewParam);
			        	}
					}
				}
			}];
			
			formPane.setHeight(85);
			if(cname === 'WIRE_SYSTEM' || cname === 'WIRE_SEG'){
				formPane.addRow(fiberLinkBtn,[0.1],20);
				formPane.setHeight(105);
			}
//			viewBtn._view.hidden = !(dms.designer.scene=='backoutScene'? true:false);
	    	formPane.setHeight(85);
		},
		
		show : function(){
			window.document.body.appendChild(this.getView());
		},
		close : function(){
			try{
				window.document.body.removeChild(this.getView());
			}catch(e){}
		}
	});
})();