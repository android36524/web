/**
 * 光缆割接任务管理
 */
$importjs(ctx+'/dwr/interface/CutOverTaskAction.js');
$importjs(ctx+'/dwr/interface/WireFinishCutAction.js');
$importjs(ctx + "/rms/dm/fiber/CutSchemeFinishHt.js");

(function(window,object,undefined){
	
	dms.Default.createFiberListPanelHT = function(cuid,labelcn,dialog){
		dms.Default.createFiberListPanelHT.superClass.constructor.apply(this);
		var self = this;
		self.cuid = cuid;
		self.lab_cn = labelcn;
		self.dialog = dialog;
		var c = self.createPanel();
		self.loadDatasByCutOverTaskCuid(cuid);
	    return c;
	};
	
	ht.Default.def('dms.Default.createFiberListPanelHT', ht.widget.FormPane, {
		cuid : null,
		sheet : null,
		lab_cn: null,
		dialog : null,
		fiberDm : null,
		rightTree : null,
		fusionBtn : null,
		deleteBtn :null,
		confirmBtn : null,
		leftpanel : null,
		createPanel:function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			var fiberDm = self.fiberDm = new ht.DataModel();
            var treeView = self.rightTree = new ht.widget.TreeView(fiberDm);
            treeView.setCheckMode('all');
            treeView.expandAll();
			
			var bottomPanel = self.createButtonPanel();
			borderPane.setCenterView(treeView,500);
			borderPane.setBottomView(bottomPanel,40);
			
			var CutFinishPanel = self.leftpanel = new dms.Default.CutSchemeFinish();
			
			var interruptPane= self.sheet = new ht.widget.TabView();
				interruptPane.add('割接纤芯列表',borderPane);
				interruptPane.add('割接方案设计',CutFinishPanel).setDisabled(true);
				interruptPane.select(0);
			return interruptPane;
		},
		createButtonPanel:function(){
			var self = this;
			var bottomPanel = new ht.widget.FormPane();
			bottomPanel.getView().style.border = '1px solid rgb(7,61,86)';
			var fBtn = self.fusionBtn = self.createButton('下一步');
			
			fBtn.onClicked = function(e){
				var selects = self.fiberDm.sm().getSelection();
				var records = new Array();
				for(var i = 0;i < selects.size();i++){
					if(selects.get(i).a('CUID').split('-')[0] == 'FIBER')
						records.push(selects.get(i));
				}
				//var records = this.getSelectionModel().getSelections();
				if(records == null || records.length < 1){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择纤芯</div>');
				    return;
				}
				var flag = true;
				for(var i = 0;i < records.length - 1;i++){
					var node1 = records[i];
					var node2 = records[i+1];
					var cuid1 = node1.getParent().a('CUID');
					var cuid2 = node2.getParent().a('CUID');
					var cuid3 = node1.getParent().getParent().a('CUID');
					var cuid4 = node2.getParent().getParent().a('CUID');
					if(!(cuid1 == cuid2 && cuid3 == cuid4))
						flag = false;
				}
				
				if(!flag){
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择同一光缆段同一设备下的纤芯</div>');
				    return;
				}
				
				self.doNextStep(records);
			};
			
			bottomPanel.getView().style.borderTopWidth ='1';
			bottomPanel.getView().style.borderLeftWidth ='0';
			bottomPanel.getView().style.borderRightWidth ='0';
			bottomPanel.getView().style.borderBottomWidth ='0';

			bottomPanel.addRow([null,fBtn],[0.2,80,5,80,5,80,5,80],23);
			return bottomPanel;
		},
		createButton : function(label){
			var self = this;
			var btn = new ht.widget.Button();
			btn.setLabel(label);
			self.setButton(btn);
			return btn;
		},
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
		getTree : function(datas,dm){
			var self = this;
			datas.forEach(function(data){
				
		        if(!data.PARENTCIUD){
		        	node = new ht.Node();
		        	node.setTag(data.CUID);
					node.a('CUID',data.CUID);
					node.setId(data.CUID);
					node.setName(data.LABEL_CN);
					node.setTag(data.CUID);
					var cName = data.CUID.split('-')[0];
					node.a('bmClassId',cName);
					node.setAttr('ICON',data.ICON);
					node.setIcon(data.ICON);
					dm.add(node);
		        }
			});
			
			datas.forEach(function(data) {
                if(data.PARENTCIUD){
					if (data.CUID.split("-")[0] != "FIBER") {
						self.setNode(data, dm);
					}
                }
			});
			
			datas.forEach(function(data) {
                if(data.PARENTCIUD){
					if (data.CUID.split("-")[0] == "FIBER") {
						self.setNode(data, dm);
					}
                }
			});
		},
		setNode:function(data,dm){
			node = new ht.Node();
			node.a('CUID', data.CUID);
			node.setId(data.CUID);
			node.setName(data.LABEL_CN);
			node.setTag(data.CUID);
			var cName = data.CUID.split('-')[0];
			node.a('bmClassId', cName);
			node.setIcon(data.ICON);
			var parentNode = dm.getDataByTag(data.PARENTCIUD);
			node.setParent(parentNode);
			dm.add(node);
		},		
		loadDatasByCutOverTaskCuid:function(Tcuid){
			var self = this;
			try {
				CutOverTaskAction.doGetWireFinishCut(Tcuid,{
					callback : function(datas) {
						if (datas && datas.length>0) {
							self.getTree(datas,self.fiberDm);
						}else{
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">没有相关的纤芯列表！</div>');
						}
					},
					errorHandler : function(errorString) {
						tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">' + errorString + '</div>');
						tp.utils.wholeUnLock();
						return;
					}
				});
			} catch (e) {
				tp.utils.wholeUnLock();
			}
		},
		doNextStep:function(fibers){
			var self = this;
			self.sheet.get(0).setDisabled(true);
			self.sheet.get(1).setDisabled(false);
			self.leftpanel.getResult(fibers,self.cuid);
			self.sheet.select(1);
		}
	});
})(this,Object);