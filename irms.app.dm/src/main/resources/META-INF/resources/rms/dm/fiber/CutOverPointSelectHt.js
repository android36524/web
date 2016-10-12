/**
*中段光缆选择
*/
$importjs(ctx + '/map/map-inc.js');
$importjs(ctx + "/jslib/jquery/jquery-1.7.1.min.js");
$importjs(ctx+'/dwr/interface/CutOverPointSelectAction.js');
$importjs(ctx+"/cmp_res/grid/EditorGridPanel.js");
$importjs(ctx+'/rms/dm/plugins/queryform/FiberJointBoxQueryPanel');
//拆分点选择界面
(function(window,object,undefined){
	
	dms.Default.CutOverPointSelectHt = function(cuid){
		dms.Default.CutOverPointSelectHt.superClass.constructor.apply(this);
		var self = this;
		self.cuid = cuid;
		
		var c =self.createPanel();
		
		self.initPointTree();
		return c;
	};
	
	ht.Default.def('dms.Default.CutOverPointSelectHt', ht.widget.FormPane,{
		cuid:null,
		pointDm:null,
		rightTree:null,
		tabView:null,
		fiberJointBoxForm:null,
		buttonToolbar:null,
		
		createPanel:function(){
			var self = this;
			self.pointDm = new ht.DataModel();
            var treeView = self.rightTree = new ht.widget.TreeView(self.pointDm);
			var buttomView = self.createBottomPanel();
			var formPane = new ht.widget.FormPane();
			formPane.addRow([treeView],[0.1],0.1);
			formPane.addRow([buttomView],[0.1],30);
			var tabView = self.tabView = new ht.widget.TabView();
			tabView.add('拆分点选择', formPane, '#1ABC9C');
			tabView.select(0);
			return tabView;
		},
		createBottomPanel : function(boolean){
			//底层按钮操作区
			var self = this;
			var buttonItems = [
			{
				type: 'button',
				label: '增加接头盒',
				disabled: false,
				action: function() {
					self.addFiberJointBox();
				}
			}, {
				type: 'button',
				label: '已有接头盒',
				disabled: false,
				action: function() {
					self.newFiberJointBox();
				}
			}, {
				type: 'button',
				label: '删除接头盒',
				disabled: false,
				action: function() {
					self.deleteFiberJointBox();
				}
			}, {
				type: 'button',
				label: '熔接关系图',
				disabled: false,
				action: function() {
					self.weldRelation();
				}
			}, {
				type: 'button',
				label: '完成',
				disabled: false,
				action: function() {
					self.doNextDrop();
				}
			}];
			
			var btnToolbar = self.buttonToolbar = new ht.widget.Toolbar(buttonItems);
			btnToolbar.setStickToRight(true);
		
			return btnToolbar;
		},
		initPointTree:function(){
			var self = this;
			CutOverPointSelectAction.getPointTree(self.cuid,{
				callback:function(datas){
					datas.forEach(function(data){
						if(!data.PARENTCUID){
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
							self.pointDm.add(node);
				        }
						
						if(data.PARENTCUID){
							node = new ht.Node();
							node.a('CUID', data.CUID);
							node.setId(data.CUID);
							node.setName(data.LABEL_CN);
							node.setTag(data.CUID);
							var cName = data.CUID.split('-')[0];
							node.a('bmClassId', cName);
							node.setIcon(data.ICON);
							var parentNode = self.pointDm.getDataByTag(data.PARENTCUID);
							node.setParent(parentNode);
							self.pointDm.add(node);
						}
					});
				},
				errorHandler:function(errorMessage){}
			});
		},
		addFiberJointBox:function(){
			var self = this;
			var selectPoint = self.pointDm.sm().getSelection();
			if(selectPoint.size() != 1){
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择一个路有点</div>');
				return;
			}
				
			var pcuid = selectPoint.get(0).getId();
			var uri = "/rest/CutOverPointSelectAction/addNewFiberjointboxAction/";
			$.ajax({
				url: ctx + uri + pcuid + "?time=" + new Date().getTime(),
				success: function(data) {
					var data = $.parseJSON(data);
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">新增接头盒：'+data.LABEL_CN+' 完成！</div>');
					var node = new ht.Node();
					node.setId(data.CUID);
					node.setName(data.LABEL_CN);
					node.setIcon("/resources/topo/dm/FiberJointBox.png");
					node.setParent(selectPoint.get(0));
					self.pointDm.add(node);
					return;
				},
				dataType: 'text',
				type: 'GET'
			});
		},
		newFiberJointBox:function(){
			var self = this;
			var selectPoint = self.pointDm.sm().getSelection();
			var pointCuid = selectPoint.get(0).getId();
			var boxCuid = null;
			if (window.ActiveXObject) {
				var d = window.showModalDialog(c,"selectRecord", this.wincfg.winArgs);
				if (!Ext.isEmpty(d)) {
					var node = new ht.Node();
					node.setId(d[0].data.CUID);
					boxCuid = d[0].data.CUID;
					node.setName(d[0].data.LABEL_CN);
					node.setIcon("/resources/topo/dm/FiberJointBox.png");
					node.setParent(selectPoint.get(0));
					var uri = "/rest/CutOverPointSelectAction/addFiberJointPointBoxAction/";
					$.ajax({
						url: ctx + uri + pointCuid + boxCuid + "?time=" + new Date().getTime(),
						success: function(data) {
							tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">'+data+'</div>');
							return;
						},
						dataType: 'text',
						type: 'GET'
					});
					self.pointDm.add(node);
				}
			} else {
				var url = ctx+ "/cmp_res/grid/EditorGridPanel.jsp?&hasMaintan=false&code=service_dict_dm.CUT_FIBER_JOINT_BOX";
				window.setShowModalDialogValue = function(value) {
					var d = value;
					if (!Ext.isEmpty(d)) {
						var node = new ht.Node();
						node.setId(d[0].data.CUID);
						boxCuid = d[0].data.CUID;
						node.setName(d[0].data.LABEL_CN);
						node.setIcon("/resources/topo/dm/FiberJointBox.png");
						node.setParent(selectPoint.get(0));
						var uri = "/rest/CutOverPointSelectAction/addFiberJointPointBoxAction/";
						$.ajax({
							url: ctx + uri + pointCuid + boxCuid + "?time=" + new Date().getTime(),
							success: function(data) {
								tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">'+data+'</div>');
								return;
							},
							dataType: 'text',
							type: 'GET'
						});
						self.pointDm.add(node);
					}
				}
				var b = window.screen.width - 150;
				var f = window.screen.height - 150;
				var winOption = "location=no,top=25,left=75,height=" + f + ",width=" + b;
				window.open(url, "selectRecord", winOption);
			}
		},
		deleteFiberJointBox:function(){
			var self = this;
			var uri = "/rest/CutOverPointSelectAction/deleteFiberjointboxAction/";
			self.myAjax(uri);
		},
		weldRelation:function(){
			var self = this;
			var selectbox = self.pointDm.sm().getSelection();
			if(selectbox == null || selectbox.size() != 1){
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择一个接头盒</div>');
				return;
			}
			var boxCuid = selectbox.get(0).getId();
			var viewParam={
    	        	   'cuid':boxCuid,
    	        	   'mapType':"FiberJointBoxWeldSectionTopo",
    	        	   'mapName':"接头盒熔接关系图"
    	        	};
			var url = 'topoHtml5Client/topo_fiber_weld_section.jsp?code=FiberJointBoxWeldSectionTopo&resId='+boxCuid+'&resType=FIBER_JOINT_BOX&clientType=html5';
			FrameHelper.openUrl(url,'接头盒熔接关系图');
		},
		myAjax:function(uri){
			var self = this;
			var selectPoint = self.pointDm.sm().getSelection();
			if(selectPoint.size() != 1){
				tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">请选择一个路有点</div>');
				return;
			}
				
			var pcuid = selectPoint.get(0).getId();
			$.ajax({
				url: ctx + uri + pcuid + "?time=" + new Date().getTime(),
				success: function(data) {
					tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">'+data+'</div>');
					var fData = selectPoint.get(0);
					var pData = fData._children;
					var deleteData = new Array();
					for (var j = 0; j < pData.size(); j++) {
						var nData = pData.get(j);
							deleteData.push(nData);
					}
					if (deleteData && deleteData.length > 0) {
						for (var k = 0; k < deleteData.length; k++) {
							var dData = deleteData[k];
							pData.remove(dData);
							self.pointDm.remove(dData);
						}
					}
					return;
				},
				error:function(message){},
				dataType: 'text',
				type: 'GET'
			});
		},
		doNextDrop:function(){
			CutOverPointSelectAction.saveInfos({
				callback:function(data){
					if(data){
						tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">方案设计完成</div>');
						return;
					}
				},
			    errorHandler:function(errorMessage){
			    	tp.utils.optionDialog("温馨提示",'<div style="text-align:center;font-size:12px;color:red">'+errorMessage+'</div>');
			    }
			});
		    
		}
	});
})(this,Object);
