//资源详情
(function(window,Object,undefined){
	"use strict";
		
	Dms.widget.GridResListPanel = function(gridCuid){
		var self = this;
		
		self._gridCuid = gridCuid;
		//绘制组件
		var formPane = self._formPane =  new ht.widget.FormPane();
		
		var headTablePane = self._headTablePane = new ht.widget.TablePane();
		var statTablePane = self._statTablePane = new ht.widget.TablePane();
		var resTablePane = self._resTablePane = new ht.widget.TablePane();
		
		var saveButton = tp.utils.createButton("","保存");
		saveButton.toolTip = "建立关联！";
		var locateButton = tp.utils.createButton("","定位");
		locateButton.toolTip = "将资源全部定位！";
		formPane.addRow([headTablePane],[0.1],100);
		formPane.addRow([statTablePane],[0.1],120);
		formPane.addRow([resTablePane],[0.1],300);
		formPane.addRow(['',saveButton,locateButton],[0.1,60,60],24);
		
		Dms.widget.GridResListPanel.superClass.constructor.apply(this,[{
			title : "资源详情",
			width : 250,
			exclusive : true,
			titleColor : "white",
			id : 1,
			minimizable : true,
			expand : true,
			narrowWhenCollapse : true,
			contentHeight : 580,
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
		self._initStatColumn();
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
						}else if(data.a("labelCn") == "ONU"){
							data.a("count",self.onuCount);
							data.a("countOut",0);
						}else if(data.a("labelCn") == "OLT"){
							data.a("count",self.oltCount);
							data.a("countOut",0);
						}else{
							data.a("count",self.posCount);
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
			Dms.Utils.graphicLocateOnMap(cuidArr);
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
			Dms.Utils.graphicLocateOnMap(cuid);
		};
	};
	ht.Default.def("Dms.widget.GridResListPanel",ht.widget.Panel,{
		_loadData : function(){
			var self = this;
			//tp.utils.lock(self._resTablePane._tableView);
			MapGridAction.findResourceByGridShape(this._gridCuid,function(result){
				
				var resObj = tp.Default.OperateObject.contextObject;
				if(!resObj){
					return ;
				}
				var attributes = resObj.attributes;
					
				var fttbPortRate = new ht.Node();
				fttbPortRate.a("labelCn","FTTB利用率");
				fttbPortRate.a("count",attributes["ONU_FTTB_PORT_RATE"]+"%");
				
				var ftthPortRate = new ht.Node();
				ftthPortRate.a("labelCn","FTTH利用率");
				ftthPortRate.a("count",attributes["ONU_FTTH_PORT_RATE"]+"%");
				
				var fttbUserCount = new ht.Node();
				fttbUserCount.a("labelCn","FTTB用户数");
				fttbUserCount.a("count",attributes["ONU_FTTB_USER_COUNT"]);
				
				var ftthUserCount = new ht.Node();
				ftthUserCount.a("labelCn","FTTH用户数");
				ftthUserCount.a("count",attributes["ONU_FTTH_USER_COUNT"]);
				
				var freePortCount = new ht.Node();
				freePortCount.a("labelCn","空闲端口数");
				freePortCount.a("count",attributes["ONU_FREE_PORT_COUNT"]);
				
				self._statTablePane._tableView.dm().add(fttbPortRate);
				self._statTablePane._tableView.dm().add(ftthPortRate);
				self._statTablePane._tableView.dm().add(fttbUserCount);
				self._statTablePane._tableView.dm().add(ftthUserCount);
				self._statTablePane._tableView.dm().add(freePortCount);
				
				if(result){
					self._dataList = [];
					self.siteCount = result.siteCount;
					self.onuCount = result.onuCount;
					self.oltCount = result.oltCount;
					self.posCount = result.posCount;
					self._headTablePane._tableView.dm().clear();
					self._resTablePane._tableView.dm().clear();
					
					var site = new ht.Node();
					site.a("labelCn",'站点');
					site.a("count",result.siteCount);
					site.a("countOut",0);
					
					var onu = new ht.Node();
					onu.a("labelCn",'ONU');
					onu.a("count",result.onuCount);
					onu.a("countOut",0);
					
					var olt = new ht.Node();
					olt.a("labelCn",'OLT');
					olt.a("count",result.oltCount);
					olt.a("countOut",0);
					
					var pos = new ht.Node();
					pos.a("labelCn",'POS');
					pos.a("count",result.posCount);
					pos.a("countOut",0);
					
					self._headTablePane._tableView.dm().add(site);
					self._headTablePane._tableView.dm().add(onu);
					self._headTablePane._tableView.dm().add(olt);
					self._headTablePane._tableView.dm().add(pos);
					
					self._dataList = result.resultList;
					self._refreshList(self._dataList);
					self._loadBindRes();
				}
			});
		},
		_loadBindRes : function(){
			var self = this;
			MapGridAction.findResourceByGrid(this._gridCuid,function(result){
				if(result)
				{
					self._inList = [];
					self._outList = [];
					
					var siteCountOut = 0;
					var onuCountOut = 0;
					var oltCountOut = 0;
					var posCountOut = 0;
					
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
							}else if(type == "ONU"){
								onuCountOut++;
							}else if(type == "OLT"){
								oltCountOut++;
							}else if(type == "POS"){
								posCountOut++;
							}
						}
					}
					
					self._headTablePane._tableView.dm().each(function(data){
						if(data.a("labelCn") == "站点"){
							data.a("count",result.siteCount);
							data.a("countOut",siteCountOut);
						}else if(data.a("labelCn") == "ONU"){
							data.a("count",result.onuCount);
							data.a("countOut",onuCountOut);
						}else if(data.a("labelCn") == "OLT"){
							data.a("count",result.oltCount);
							data.a("countOut",oltCountOut);
						}else{
							data.a("count",result.posCount);
							data.a("countOut",posCountOut);
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
		
		_initStatColumn : function(){
			var self = this;
			self._statTablePane.addColumns([
				{
					name : 'labelCn',
					displayName : '指标',
					width : 100,
					accessType : 'attr'
				},
				{
					name : 'count',
					displayName : '数值',
					width : 130,
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