//站点详情
(function(window,Object,undefined){
	"use strict";
		
	Dms.widget.GridSiteListPanel = function(gridCuid){
		var self = this;
		
		self._gridCuid = gridCuid;
		//绘制组件
		var formPane = self._formPane =  new ht.widget.FormPane();
		
		var headTablePane = self._headTablePane = new ht.widget.TablePane();
		var siteTablePane = self._siteTablePane = new ht.widget.TablePane();
		
		var saveButton = tp.utils.createButton("","保存");
		saveButton.toolTip = "建立关联！";
		var locateButton = tp.utils.createButton("","定位");
		locateButton.toolTip = "将站点全部定位！";
		formPane.addRow([headTablePane],[0.1],60);
		formPane.addRow([siteTablePane],[0.1],340);
		formPane.addRow(['',saveButton,locateButton],[0.1,60,60],24);
		
		Dms.widget.GridSiteListPanel.superClass.constructor.apply(this,[{
			title : "站点详情",
			width : 280,
			exclusive : true,
			titleColor : "white",
			id : 1,
			minimizable : true,
			expand : true,
			narrowWhenCollapse : true,
			contentHeight : 450,
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
		self._initSiteColumn();
		self._loadData();
		saveButton.onclick = saveButton.onClicked = function(){
			var cuidArr = [];
			if(self._dataList == null || self._dataList.length == 0){
				return;
			}
			for(var i=0;i<self._dataList.length;i++)
			{
				cuidArr.push(self._dataList[i].CUID);
			}
			var cuids = cuidArr.join(',');
			MapGridAction.bindGridSite(gridCuid,cuids,function(result){
				if(result && result == 'TRUE')
				{
					//网格内
					var head = headTablePane._tableView.dm().getDatas().get(0);
					head.a("count",self._dataList.length);
					head.a("countOut",0);
					headTablePane._tableView.iv();
					tp.utils.optionDialog('温馨提示','保存成功!');
				}else
				{
					tp.utils.optionDialog('温馨提示',"操作业务区失败:"+result);
				}
			});
		};
		locateButton.onclick = locateButton.onClicked = function(){
			var cuidArr = [];
			siteTablePane._tableView.dm().sm().each(function(data){
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
		siteTablePane._tableView.onDataDoubleClicked = function(data){
			var cuid = data.getId();
			Dms.Tools.graphicLocateOnMap(cuid);
		};
	};
	ht.Default.def("Dms.widget.GridSiteListPanel",ht.widget.Panel,{
		_loadData : function(){
			var self = this;
			MapGridAction.findSiteListByGridShape(this._gridCuid,function(siteList){
				self._dataList = [];
				if(siteList)
				{
					self._headTablePane._tableView.dm().clear();
					self._siteTablePane._tableView.dm().clear();
					
					var head = new ht.Node();
					head.a("labelCn",'站点');
					head.a("count",siteList.length);
					head.a("countOut",0);
					self._headTablePane._tableView.dm().add(head);
					self._dataList = siteList;
					self._refreshList(self._dataList);
					
					self._loadBindSite(head);
				}
			});
		},
		_loadBindSite : function(head){
			var self = this;
			MapGridAction.findSiteListByGrid(this._gridCuid,function(siteList){
				if(siteList)
				{
					self._inList = [];
					self._outList = [];
					
					for(var i=0;i<siteList.length;i++)
					{
						var cuid = siteList[i].CUID;
						var name = siteList[i].LABEL_CN;
						var site = self._siteTablePane._tableView.dm().getDataById(cuid);
						if(site)
						{
							self._inList.push(siteList[i]);
						}else
						{
							self._outList.push(siteList[i]);
						}
					}
					head.a("count",self._inList.length);
					head.a("countOut",self._outList.length);
					self._headTablePane._tableView.iv();
				}
			});
		},
		_refreshList : function(list)
		{
			var self = this;
			self._siteTablePane._tableView.dm().clear();
			for(var i=0;i<list.length;i++)
			{
				var site = list[i];
				var sNode = new ht.Node();
				sNode.setId(site['CUID']);
				sNode._attrObject = site;
				sNode.a('no_fuc',i+1);
				self._siteTablePane._tableView.dm().add(sNode);
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
		        displayName : '数量(业务区内)',
		        width: 100,
		        accessType : 'attr'
		    },
		    {
		        name : 'countOut',
		        displayName : '数量(业务区外)',
		        width: 100,
		        accessType : 'attr'
			}]);
		},
		_initSiteColumn : function(){
			var self = this;
			self._siteTablePane.addColumns([{
		        name: 'no_fuc',
		        displayName :'序号',
		        width: 35,
		        accessType : 'attr'
		    },
		    {
		        name : 'LABEL_CN',
		        displayName : '名称',
		        width: 220,
		        accessType : 'attr'
		    }]);
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