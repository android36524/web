//资源详情（for henan)
(function(window,Object,undefined){
	"use strict";
		
	Dms.widget.GridResourcePanel = function(gridCuid){
		var self = this;
		
		self._gridCuid = gridCuid;
		//绘制组件
		var formPane = self._formPane =  new ht.widget.FormPane();
		
		var headTablePane = self._headTablePane = new ht.widget.TablePane();
		var resTablePane = self._resTablePane = new ht.widget.TablePane();
		
		var saveButton = tp.utils.createButton("","保存");
		saveButton.toolTip = "建立关联！";
		var locateButton = tp.utils.createButton("","定位");
		locateButton.toolTip = "将资源全部定位！";
		formPane.addRow([headTablePane],[0.1],100);
		formPane.addRow([resTablePane],[0.1],320);
		formPane.addRow(['',saveButton,locateButton],[0.1,60,60],24);
		
		Dms.widget.GridResourcePanel.superClass.constructor.apply(this,[{
			title : "资源详情",
			width : 250,
			exclusive : true,
			titleColor : "white",
			id : 1,
			minimizable : true,
			expand : true,
			narrowWhenCollapse : true,
			contentHeight : 480,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
				action:function(){
					self.close();
				}
			}],
			content : formPane.getView()
		}]);
		//处理事件
		self._initHeadColumn();
		self._initResColumn();
		self._loadData();
		saveButton.onclick = saveButton.onClicked = function(){
			var dataList = JSON.stringify(self._dataList);
			MapGridAction.bindGridResource(gridCuid,dataList,function(result){
				if(result && result == 'TRUE')
				{
					self._headTablePane._tableView.dm().each(function(data){
						if(data.a("labelCn") == "站点"){
							data.a("count",self.siteCount);
							data.a("countOut",0);
						}else if(data.a("labelCn") == "FIBER_CAB"){
							data.a("count",self.cabCount);
							data.a("countOut",0);
						}else if(data.a("labelCn") == "FIBER_DP"){
							data.a("count",self.dpCount);
							data.a("countOut",0);
						}
					});
					self._headTablePane._tableView.iv();
					headTablePane._tableView.iv();
					tp.utils.optionDialog('温馨提示','保存成功！');
				}else
				{
					tp.utils.optionDialog('温馨提示','操作业务区失败:'+result);
				}
			});
		};
		locateButton.onclick = locateButton.onClicked = function(){
			var cuidArr = [];
			resTablePane._tableView.dm().sm().each(function(data){
				cuidArr.push(data._attrObject);
			});
			Dms.Tools.graphicLocateOnMap(cuidArr);
		};
		//点击表头
		self._headTablePane._tableView.onColumnClicked = function(column){
			console.info(column);
			if(column._name == 'labelCn')
			{
				self._refreshList(self._dataList);
			}
			if(column._name == 'count')
			{
				self._refreshList(self._inList);
			}
			if(column._name == 'countOut')
			{
				self._refreshList(self._outList);
			}
		};
		//双击定位
		resTablePane._tableView.onDataDoubleClicked = function(data){
			var cuid = data.getId();
			Dms.Tools.graphicLocateOnMap(cuid);
		};
	};
	ht.Default.def("Dms.widget.GridResourcePanel",ht.widget.Panel,{
		_loadData : function(){
			var self = this;
			//tp.utils.lock(self._resTablePane._tableView);
			MapGridAction.findResourceByGridShape(this._gridCuid,function(result){
				
				if(result){
					self._dataList = [];
					self.siteCount = result.siteCount;
					self.cabCount = result.cabCount;
					self.dpCount = result.dpCount;
					self._headTablePane._tableView.dm().clear();
					self._resTablePane._tableView.dm().clear();
					
					var site = new ht.Node();
					site.a("labelCn",'站点');
					site.a("count",result.siteCount);
					site.a("countOut",0);
					
					var fiberCab = new ht.Node();
					fiberCab.a("labelCn",'光交接箱');
					fiberCab.a("count",result.cabCount);
					fiberCab.a("countOut",0);
					
					var fiberDp = new ht.Node();
					fiberDp.a("labelCn",'光分纤箱');
					fiberDp.a("count",result.dpCount);
					fiberDp.a("countOut",0);
					
					self._headTablePane._tableView.dm().add(site);
					self._headTablePane._tableView.dm().add(fiberCab);
					self._headTablePane._tableView.dm().add(fiberDp);
					
					self._dataList = result.resultList;
					self._refreshList(self._dataList);
					self._loadBindRes();
				}
			});
		},
		_loadBindRes : function(){
			var self = this;
			MapGridAction.findResourceByGridShape(this._gridCuid,function(result){
				if(result)
				{
					self._inList = [];
					self._outList = [];
					
					var siteCountOut = 0;
					var cabCountOut = 0;
					var dpCountOut = 0;
					
					for(var i=0;i<result.resultList.length;i++)
					{
						var cuid = result.resultList[i].CUID;
						var name = result.resultList[i].LABEL_CN;
						var type = result.resultList[i].RES_TYPE;
						var res = self._resTablePane._tableView.dm().getDataById(cuid);
						if(res){
							self._inList.push(res);
						}else{
							self._outList.push(res);
							if(type == "SITE"){
								siteCountOut++;
							}else if(type == "FIBER_CAB"){
								cabCountOut++;
							}else if(type == "FIBER_DP"){
								dpCountOut++;
							}
						}
					}
					
					self._headTablePane._tableView.dm().each(function(data){
						if(data.a("labelCn") == "站点"){
							data.a("count",result.siteCount);
							data.a("countOut",siteCountOut);
						}else if(data.a("labelCn") == "光交接箱"){
							data.a("count",result.cabCount);
							data.a("countOut",cabCountOut);
						}else if(data.a("labelCn") == "光分纤箱"){
							data.a("count",result.dpCount);
							data.a("countOut",dpCountOut);
						}
					});
					self._headTablePane._tableView.iv();
					
					//tp.utils.unlock(self._resTablePane._tableView);
				}
			});
		},
		_refreshList : function(list)
		{
			var self = this;
			self._resTablePane._tableView.dm().clear();
			for(var i=0;i<list.length;i++)
			{
				var site = list[i];
				var sNode = new ht.Node();
				sNode.setId(site['CUID']);
				sNode._attrObject = site;
				sNode.a('no_fuc',i+1);
				sNode.a('TYPE',self._getResType(site.RES_TYPE));
				self._resTablePane._tableView.dm().add(sNode);
			}
		},
		_initHeadColumn : function(){
			var self = this;
			self._headTablePane.addColumns([{
		        name: 'labelCn',
		        displayName :'类型(全部)',
		        width: 70,
		        accessType : 'attr'
		    },
		    {
		        name : 'count',
		        displayName : '数量(网格内)',
		        width: 80,
		        accessType : 'attr'
		    },
		    {
		        name : 'countOut',
		        displayName : '数量(网格外)',
		        width: 80,
		        accessType : 'attr'
			}
			]);
		},
		
		
		_initResColumn : function(){
			var self = this;
			self._resTablePane.addColumns([{
		        name: 'no_fuc',
		        displayName :'序号',
		        width: 35,
		        accessType : 'attr'
		    },
		    {
		        name : 'LABEL_CN',
		        displayName : '名称',
		        width: 140,
		        accessType : 'attr'
		    },
		    {
		        name : 'TYPE',
		        displayName : '类型',
		        width: 60,
		        accessType : 'attr'
		    }]);
		},
		
		_getResType : function(resType){
			if(resType === "SITE"){
				return "站点";
			}else if(resType === "FIBER_CAB"){
				return "光交接箱";
			}else if(resType === "FIBER_DP"){
				return "光分纤箱";
			}
		},
		
		show : function(){
			var self = this;
			self.setPosition(400, 30);
			document.body.appendChild(self.getView());
		},
		close : function(){
			var self = this;
			document.body.removeChild(self.getView());
		}
	});
})(this,Object);