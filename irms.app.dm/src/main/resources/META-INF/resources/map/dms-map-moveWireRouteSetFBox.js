/**
 * 光缆改迁
 */
$importjs(ctx+'/dwr/interface/MapMoveWireRouteAction.js');

(function(window,object,undefined){
	"use strict";
	
	dms.move.moveWireRouteFBoxPanel = function(){
		var self = this;
		var c = self.createRouteFBoxPanel();
		Dms.widget.GridResListPanel.superClass.constructor.apply(this,[{
			title : "光缆改迁-在路由点上设置接头盒",
			titleAlign :'center',
			width : 600,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 400,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
				action:function(){
//					self.clearMoveWireRoute();
				}
			}],
			content : c
		
		}]);
		self.fp = function(){};
		var tpmap = Dms.Default.tpmap,
	  	map = tpmap.getMap();
//		dms.move.wireseglist
//		dms.move.isChanged = true;//用于判断上一步时有没有修改选择的段
		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 
		dms.move.setFiberJointBoxData = function() {
			try {
				tp.utils.wholeLock();
				var cuids = [];
				var lists = dms.move.wireseglist;
				for(var i=0;i<lists.size();i++){
					var list = lists.get(i);
					var cid = list.a('CUID');
					cuids.push(cid);
				}
				MapMoveWireRouteAction.setFiberJointBox(cuids,{
					callback : function(datas){
						if(datas){
							for(var i=0;i<datas.length;i++){
								var data = datas[i];
								var wid = data.CUID;
								var points = data.points;
								setPointValue(points);
							}
						}
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
						tp.utils.wholeUnLock();
					}
				});
			} catch (e) {
				tp.utils.wholeUnLock();
			}
		};
		dms.move.setFiberJointBoxData();
		
		function setPointValue(points){
			if(points){
				for(var i=0;i<points.length;i++){
					var point = points[i],
						cuid = point.CUID,
						labelCn = point.LABEL_CN;
					self.setDatas(dms.move.leftDm,cuid,labelCn);
				}
			}
		};
	};
	
	ht.Default.def('dms.move.moveWireRouteFBoxPanel',ht.widget.Panel,{
		combox : null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x, y);
			dms.move.moveWireRouteSetFBoxView = self.getView();
			document.body.appendChild(self.getView());
		},
		createRouteFBoxPanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var topPanel = self.createTopPanel();
			var bottomPanel = self.createBtnPanel();
			var leftPanel = self.createLeftPanel();
			var centerPanel = self.createCenterPanel();
			var rightPanel = self.createRightPanel();
			
			borderPane.setTopView(topPanel,40);
			borderPane.setBottomView(bottomPanel,40);
			borderPane.setLeftView(leftPanel,200);
			borderPane.setCenterView(centerPanel,30);
			borderPane.setRightView(rightPanel,300);
//			borderPane.getView().style.border = '2px solid red';

//			var moveSplitView = new ht.widget.SplitView(tView,btnPanel,'v',0.88);
//			moveSplitView.setDraggable(false);
			return borderPane;
		},
		
		createLeftPanel : function(){
			var self = this;
			var leftPanel = new ht.widget.FormPane();
			var dm = dms.move.leftDm = new ht.DataModel();
			var listView = new ht.widget.ListView(dm);
			listView.setCheckMode(true);  
//            listView.setSelectionModelShared(false);
//            listView.getSelectionModel().setSelectionMode('single'); 
			leftPanel.addRow([listView],[0.1],100);

			return leftPanel;
		},
		
		createRightPanel : function(){
			var self = this;
			var rightPanel = new ht.widget.FormPane();
			var dm = dms.move.tableDm = new ht.DataModel();
			var tablePanel = new ht.widget.TablePane(dm);
			rightPanel.addRow([tablePanel],[0.1],300);
			self.addRightColumn(tablePanel);
			return rightPanel;
		},
		
		addRightColumn : function(table){
			var cm = table.getColumnModel();
		    var column = new ht.Column();
		    column.setAccessType('attr');
		    column.setName('WIRE_SEG_LABEL_CN');
		    column.setWidth(200);
		    column.setDisplayName('光缆段名称');
		    cm.add(column);
		    
		    column = new ht.Column();
		    column.setAccessType('attr');
		    column.setName('RELATE_LOCATION_NAME');
		    column.setWidth(100);
		    column.setDisplayName('新增接头盒位置');
		    cm.add(column);
		    
		    /*column = new ht.Column();
		    column.setAccessType('attr');
		    column.setName('LABEL_CN');
		    column.setWidth(100);
		    column.setDisplayName('新增接头盒名称');
		    cm.add(column);*/
		},
		createTopPanel : function(){
			var self = this;
			var comBoxPanel = new ht.widget.FormPane();
			var comBox = new ht.widget.ComboBox();
			
			var cuids = [],labels = [];
			var lists = dms.move.wireseglist;
			for(var i=0;i<lists.size();i++){
				var list = lists.get(i);
				var cid = list.a('CUID'),
					labelCn = list.a('LABEL_CN');
				cuids.push(cid);
				labels.push(labelCn);
			}
			comBox.setValues(cuids);
			comBox.setLabels(labels);
			comBox.setValue(cuids[0]);
			self.combox = comBox;
			comBoxPanel.addRow([comBox],[0.1],30);
			self.setPanelBorerder(comBoxPanel);
			comBoxPanel.getView().style.borderTopWidth ='0';
			comBoxPanel.getView().style.borderRightWidth ='0';
			return comBoxPanel;
		},

		createBtnPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();

			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('确定');
			self.setButton(confirmBtn);
			confirmBtn.onClicked = function(e) {
				
			};
			var cancelBtn = new ht.widget.Button();
			cancelBtn.setLabel('取消');
			self.setButton(cancelBtn);
			
			cancelBtn.onClicked = function(e){
				self.clearMoveWireRouteFBox();
			};
			
			btnPanel.addRow([null,confirmBtn,cancelBtn,null],[0.1,0.1,0.1,0.1],[23]);
			self.setPanelBorerder(btnPanel);
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			return btnPanel;
		},

		createCenterPanel : function(){
			var self = this;
			var centerPanel = new ht.widget.FormPane();
			var leftToRightBtn = new ht.widget.Button();
			leftToRightBtn.setLabel('>');
			leftToRightBtn.onClicked = function(){
//				dms.move.leftDm
//				dms.move.tableDm
				var leftSelection = dms.move.leftDm.sm().getSelection();
				for (var i = 0; i < leftSelection.size(); i++) {
					var wireseg = leftSelection.get(i),
						cuid = wireseg.a('CUID'),
						labelCn = wireseg.a('LABEL_CN');
					var node = self.setDatas(dms.move.tableDm,cuid,labelCn);
					node.a('WIRE_SEG_LABEL_CN',self.combox._value);
					node.a('RELATE_LOCATION_NAME',labelCn);
				}
			};
			var rightToLeftBtn = new ht.widget.Button();
			rightToLeftBtn.setLabel('<');
			rightToLeftBtn.onClicked = function(){
				var tableSelection = dms.move.tableDm.getSelection();
				for(var i=0;i<tableSelection.size();i++){
					
				}
			};
			
			centerPanel.addRow([null],[0.1],25);
			centerPanel.addRow([leftToRightBtn],[0.1],25);
			centerPanel.addRow([rightToLeftBtn],[0.1],25);
			centerPanel.addRow([null],[0.1],25);
			self.setPanelBorerder(centerPanel);
			centerPanel.getView().style.borderTopWidth ='0';
			centerPanel.getView().style.borderBottomWidth ='0';
			return centerPanel;
		},
		
		setPanelBorerder : function(panel){
			panel.getView().style.border = '1px solid rgb(7,61,86)';
//			panel.getView().style.borderTopWidth ='0';
//			panel.getView().style.borderBottomWidth ='0';
//			panel.getView().style.borderRightWidth ='0';
		},
		
		clearMoveWireRouteFBox : function(){
			tp.utils.wholeUnLock();
			Dms.Default.tpmap.reset();
			if(dms.move.moveWireRouteView){
				document.body.removeChild(dms.move.moveWireRouteView);
				dms.move.moveWireRouteView = null;
			}
			if(tp.Default.OperateObject.curInterator){
				tp.Default.OperateObject.curInterator.reset();
			}
			if(dms.move.leftDm){
				
			}
			if(dms.move.tableDm){
				
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
	    setDatas : function(dm,cuid,labelCn){
	    	var oldNode = dm.getDataById(cuid);
	    	if(!oldNode){
	    		oldNode = new ht.Node();
	    		oldNode.a('CUID',cuid);
	    		oldNode.a('LABEL_CN',labelCn);
	    		oldNode.setId(cuid);
	    		oldNode.setName(labelCn);
	    		dm.add(oldNode);
	    	}
	    	return oldNode;
	    }
	});

})(this,Object);