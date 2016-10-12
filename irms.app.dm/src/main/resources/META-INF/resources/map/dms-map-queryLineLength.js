/**
 * 查询管线长度
 */
$importjs(ctx+'/dwr/interface/DmQueryLengthAction.js');

(function(window,object,undefined){
	"use strict";
	
	dms.queryLineLengthPanel = function(systemCuid){
		var self = this;
		self.sysCuid = systemCuid;
		var c = dms.queryLineLengthPanel.lengthQueryPanel = self.createQueryPanel();
		tp.utils.lock(c);
		dms.queryLineLengthPanel.superClass.constructor.apply(this,[{
			title : "查询两点间管线长度：",
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
					self.closePanel();
				}
			}],
			content : c
		
		}]);
		self.fp = function(){};

		self.getDatas();
		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 

	};
	
	ht.Default.def('dms.queryLineLengthPanel',ht.widget.Panel,{
		sysCuid : null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight - self._config.contentHeight)/2;
			self.setPosition(x-100, y-120);
			document.body.appendChild(self.getView());
		},
		closePanel : function(){
			var self = this;
			tp.utils.unlock(dms.queryLineLengthPanel.lengthQueryPanel);
			Dms.Default.tpmap.reset();
			document.body.removeChild(self.getView());
		},
		createQueryPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var topPanel = self.createTopPanel();
			var centerPanel = self.createCenterPanel();
			var bottomPanel = self.createBtnPanel();
			
			borderPane.setTopView(topPanel,180);
			borderPane.setCenterView(centerPanel,100);
			borderPane.setBottomView(bottomPanel,40);
			
			return borderPane;
		},
		createTopPanel : function(){
			var self = this;
			dms.queryLineLengthPanel.origPoint = null,
			dms.queryLineLengthPanel.destPoint = null;
			var formPanel = new ht.widget.FormPane();
			//2px solid rgb(7,61,86)---1px solid #FFFFE0
			formPanel.getView().style.border = '1px solid rgb(7,61,86)';
			formPanel.getView().style.borderLeftWidth ='0';
			formPanel.getView().style.borderTopWidth ='0';
			formPanel.getView().style.borderRightWidth ='0';
			dms.queryLineLengthPanel.dataModel = new ht.DataModel();
			var lineTypeComBox = dms.queryLineLengthPanel.lineTypeCbox = new ht.widget.ComboBox();
			var lineNameComBox = dms.queryLineLengthPanel.lineNameCbox = new ht.widget.TextField();
			var lineBranchComBox = dms.queryLineLengthPanel.lineBranchCbox = new ht.widget.ComboBox();
			var origComBox = dms.queryLineLengthPanel.origCbox = new ht.widget.ComboBox();
			origComBox.addPropertyChangeListener(function(e){
				if (e.property === 'value') {
					var name = e.newValue;
					dms.queryLineLengthPanel.origPoint = dms.queryLineLengthPanel.dataModel.getDataByTag(name);
				}
			});
			var destComBox = dms.queryLineLengthPanel.destCbox = new ht.widget.ComboBox();
			destComBox.addPropertyChangeListener(function(e){
				if (e.property === 'value') {
					var name = e.newValue;
					dms.queryLineLengthPanel.destPoint = dms.queryLineLengthPanel.dataModel.getDataByTag(name);
				}
			});
			lineBranchComBox.addPropertyChangeListener(function(e){
				if (e.property === 'value') {
					var name = e.newValue;
					
					for(var i=0;i<dms.queryLineLengthPanel.branchs.length;i++){
						var data = dms.queryLineLengthPanel.branchs[i];
						var branchLabelCn = data.LABEL_CN;
						if(branchLabelCn == name){
							var pointsAndSegs = data.SEGS;
							var points = pointsAndSegs.POINTS;
							
							dms.queryLineLengthPanel.points = points;
							dms.queryLineLengthPanel.segs = pointsAndSegs.SEGS;
							var pointsArray = [];
							points.forEach(function(point){
								var pname = point.LABEL_CN;
								pointsArray.push(pname);
							});
							dms.queryLineLengthPanel.origCbox.setValues(pointsArray);
							dms.queryLineLengthPanel.destCbox.setValues(pointsArray);
							
							dms.queryLineLengthPanel.origCbox.setValue(pointsArray[0]);
							dms.queryLineLengthPanel.destCbox.setValue(pointsArray[1]);
							return;
						}
					}
				}
			});
			
			formPanel.addRow(["管线类型",lineTypeComBox],[0.1,0.7],25);
			formPanel.addRow(["管线名称",lineNameComBox],[0.1,0.7],25);
			formPanel.addRow(["分支名称",lineBranchComBox],[0.1,0.7],25);
			formPanel.addRow(["起点",origComBox],[0.1,0.7],25);
			formPanel.addRow(["终点",destComBox],[0.1,0.7],25);
			
			return formPanel;
		},
		addDataModel : function(data){
			var node = dms.queryLineLengthPanel.dataModel.getDataById(data.CUID);
			if(!node){
				node = new ht.Node();
				node.setId(data.CUID);
				node.setName(data.LABEL_CN);
				node.setTag(data.LABEL_CN);
				dms.queryLineLengthPanel.dataModel.add(node);
			}
		},
		createCenterPanel : function(){
			var formPanel = new ht.widget.FormPane();
			var resultArea = dms.queryLineLengthPanel.resultArea = new ht.widget.TextArea();
			formPanel.addRow(["查询结果"],[0.1],23);
			formPanel.addRow([resultArea],[0.1],60);
			return formPanel;
		},
		createBtnPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();
			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('查询');
			self.setButton(confirmBtn);
			confirmBtn.onClicked = function(){
				self.doQuery();
			};
			
			var cancelBtn = new ht.widget.Button();
			cancelBtn.setLabel('关闭');
			self.setButton(cancelBtn);
			
			cancelBtn.onClicked = function(e){
				self.closePanel();
			};
			
			btnPanel.addRow([null,confirmBtn,cancelBtn,null],[0.2,0.1,0.1,0.2],[23]);
//			btnPanel.getItemBackground = function(){return '#DAECF4';};
			btnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			return btnPanel;
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		doQuery : function(){
	        var startCuid = dms.queryLineLengthPanel.origPoint.getId();
	        var endCuid = dms.queryLineLengthPanel.destPoint.getId();

	        if (startCuid == endCuid) {
	        	dms.queryLineLengthPanel.resultArea.setText("0米");
	            return;
	        }
	        var a = dms.queryLineLengthPanel.resultArea;
	        var counting = false,
	        	shouldBreak = false;
	        var length = 0.0;
//	        dms.queryLineLengthPanel.resultArea
	        var provincePointCuid = null,
	        	pointCuidList = dms.queryLineLengthPanel.points,
	        	segsList = dms.queryLineLengthPanel.segs;
	        for(var i=0;i<pointCuidList.length;i++){
	        	var point = pointCuidList[i];
	        	var pCuid = point.CUID;
	        	if (pCuid == startCuid || pCuid == endCuid) {
	                //找到起点,开始计算
	                if (!counting) {
	                    provincePointCuid = pCuid;
	                    counting = true;
	                } else {
	                    //找到终点,停止计算
	                    shouldBreak = true;
	                }
	            }
	        	
	        	if (counting) {
	                if (provincePointCuid) {
	                	segsList.forEach(function(seg){
	                		var oCuid = seg.ORIG_POINT_CUID,
	                			dCuid = seg.DEST_POINT_CUID;
	                			if ((dCuid == provincePointCuid && oCuid == pCuid) ||
	   	                            (dCuid == pCuid && oCuid == provincePointCuid)) {
	                				
	   	                            length += seg.SEG_LENGTH;
	   	                            provincePointCuid = pCuid;
	   	                        }
	                	});
	                }
	            }
	        
	        	if (shouldBreak) {
	                break;
	            }
	        }
	        dms.queryLineLengthPanel.resultArea.setText(length+"米");
	        
		},
		getDatas : function(){
			var self = this;
			if(self.sysCuid){
				DmQueryLengthAction.getQueryLineLength(self.sysCuid,
				{
					callback : function(datas) {
						if (datas) {
							var system = datas[0],
								sysCuid = system.CUID,
								sysLabelCn = system.LABEL_CN,
								branchs = system.BRANCHS,
								bid = sysCuid.split("-")[0];
							//此类型当前只用选取的这一个，所以只插入这一个值
							var btype = dms.systemClassNameResName[bid];
							dms.queryLineLengthPanel.lineTypeCbox.setValue(btype);
							dms.queryLineLengthPanel.lineNameCbox.setValue(sysLabelCn);
							if(branchs){
								dms.queryLineLengthPanel.branchs = branchs;
								var branch = branchs[0];
								var branchLabelCn = branch.LABEL_CN;
								var branchNames = [];
								branchs.forEach(function(data){
									var bcn = data.LABEL_CN;
									branchNames.push(bcn);
									self.addDataModel(data);
									
									var bch = data.SEGS,
										ps = bch.POINTS,
										ss = bch.SEGS;
									ps.forEach(function(linePoint){
										self.addDataModel(linePoint);
									});
									
									ss.forEach(function(lineSeg){
										self.addDataModel(lineSeg);
									});
								});
								//点和线
								var pointsAndSegs = branch.SEGS;
								
								dms.queryLineLengthPanel.points = pointsAndSegs.POINTS;
								dms.queryLineLengthPanel.segs = pointsAndSegs.SEGS;
								
								dms.queryLineLengthPanel.lineBranchCbox.setValues(branchNames);
								dms.queryLineLengthPanel.lineBranchCbox.setValue(branchLabelCn);
							}else{
								var pointsAndSegs = system.SEGS;
								var pointsArray = [];
								var linePoints = dms.queryLineLengthPanel.points = pointsAndSegs.POINTS;
								linePoints.forEach(function(poi){
									var name = poi.LABEL_CN;
									pointsArray.push(name);
									self.addDataModel(poi);
								});
								
								var lineSegs = dms.queryLineLengthPanel.segs = pointsAndSegs.SEGS;
								lineSegs.forEach(function(ls){
									self.addDataModel(ls);
								});
								
								dms.queryLineLengthPanel.origCbox.setValues(pointsArray);
								dms.queryLineLengthPanel.destCbox.setValues(pointsArray);
								
								dms.queryLineLengthPanel.origCbox.setValue(pointsArray[0]);
								dms.queryLineLengthPanel.destCbox.setValue(pointsArray[1]);
							}
						}
						tp.utils.unlock(dms.queryLineLengthPanel.lengthQueryPanel);
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
					}
				});
			}
		}
	});

})(this,Object);