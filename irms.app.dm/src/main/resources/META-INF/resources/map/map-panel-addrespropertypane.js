$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.AddResPropertyPane = function(bmClassId,tabView,treedata,flag,resdata){
		Dms.Panel.AddResPropertyPane.superClass.constructor.apply(this);
		var self = this;
		self.dataModel = new ht.DataModel();
		self.tabView = tabView;
		self.resdata = resdata;
		self.treedata = treedata;
		self.bmClassId=bmClassId;
		self.flag = flag;
		self.createPropertyView(self.dataModel);
		self.loadColumns();
		var node = new ht.Node();
		if("T_ROFH_FULL_ADDRESS" == bmClassId)
		{
			if(Dms.Default.user.distCuid)
			{
				node.a("PROVINCE", dms.Default.user.distCuid.substring(0, 20));
				self.changeAddressEnum('', 'PROVINCE');
				if(node.a("PROVINCE"))
					self.changeAddressEnum(dms.Default.user.distCuid.substring(0, 20), 'CITY');
			}
		}
		if(self.tabView.dm().sm().ld()!=null){
			var tdata = self.tabView.dm().sm().ld();
			var s = "_NAME";
			var idstr = "";
			for(var p in tdata._attrObject){
				if(p.substring(p.length-s.length)==s){
					var obj = new Object;
					obj.id = tdata.a(p.substring(0,(p.length-s.length)));
					obj.labelCn = tdata.a(p);
					node.a(p.substring(0,(p.length-s.length)),obj);
					idstr += p.substring(0,(p.length-s.length));
				}else if(idstr.indexOf(p)>0){
					continue;
				}else{
					node.a(p,tdata.a(p));
				}
			}
		}
		self.dataModel.add(node);
		self.dataModel.sm().ss(node);
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
		self.initListener();
		if("T_ROFH_FULL_ADDRESS" == bmClassId)
		{
			if(Dms.Default.user.distCuid)
			{
				if(node.a("CITY"))
					self.changeAddressEnum(node.a("CITY"), 'COUNTY');
				if(node.a("COUNTY"))
					self.changeAddressEnum(node.a("COUNTY"), 'TOWN');
			}
		}
	};
	
	
	ht.Default.def("Dms.Panel.AddResPropertyPane", ht.widget.BorderPane, {
		dataModel: null,
		propertyView: null,
		saveBtn: null,
		cancelBtn: null,
		selectedData: null,
		tabView : null,
		roomCBox : null,
		roomText : null,
		lock: function() {
			this.setDisabled(true,ctx+"/resources/icons/loading.gif");
		},
		unlock: function() {
			this.setDisabled(false);
		},
		initListener: function()
		{
			var self = this;
			var data = self.dataModel.sm().ld();
			self.dataModel.addDataPropertyChangeListener(function(e){
				if(self.bmClassId == 'T_ROFH_FULL_ADDRESS')
				{
					if(e.property == 'a:PROVINCE')
					{
						if(e.newValue)
						{
							data.a('CITY', '');
							data.a('COUNTY', '');
							data.a('TOWN', '');
							self.changeAddressEnum(e.newValue, 'CITY');
						}
					}
					else if(e.property == 'a:CITY')
					{
						if(e.newValue)
						{
							if(!data.a('PROVINCE'))
							{
								data.a('CITY', '');
								tp.utils.optionDialog("温馨提示", "请先填写[省份]！");
								return;
							}
							else
							{
								data.a('COUNTY', '');
								data.a('TOWN', '');
								self.changeAddressEnum(e.newValue, 'COUNTY');
							}
						}
					}
					else if(e.property == 'a:COUNTY')
					{
						if(e.newValue)
						{
							if(!data.a('CITY'))
							{
								data.a('COUNTY', '');
								tp.utils.optionDialog("温馨提示", "请先填写[地市]！");
								return;
							}
							else
							{
								data.a('TOWN', '');
								self.changeAddressEnum(e.newValue, 'TOWN');
							}
						}
					}
					else if(e.property == 'a:TOWN')
					{
						if(e.newValue)
						{
							if(!data.a('COUNTY'))
							{
								data.a('TOWN', '');
								tp.utils.optionDialog("温馨提示", "请先填写[区县]！");
								return;
							}
						}
					}
					else if(e.property == 'a:ROAD')
					{
						if(e.newValue)
						{
							if(!data.a('TOWN'))
							{
								data.a('ROAD', '');
								tp.utils.optionDialog("温馨提示", "请先填写[街道办]！");
								return;
							}
						}
					}
					else if(e.property == 'a:VILLAGES')
					{
						if(e.newValue)
						{
							if(!data.a('ROAD'))
							{
								data.a('VILLAGES', '');
								tp.utils.optionDialog("温馨提示", "请先填写[路/巷/街]！");
								return;
							}
						}
					}
					else if(e.property == 'a:ROAD_NUMBER')
					{
						if(e.newValue)
						{
							if(!data.a('VILLAGES'))
							{
								data.a('ROAD_NUMBER', '');
								tp.utils.optionDialog("温馨提示", "请先填写[小区/单位/学校]！");
								return;
							}
						}
					}
					var labelCn = '';
					if(data.a('PROVINCE'))
						labelCn += self.getNameByDistrictcuid(data.a('PROVINCE'), 'PROVINCE');
					if(data.a('CITY'))
						labelCn += self.getNameByDistrictcuid(data.a('CITY'), 'CITY');
					if(data.a('COUNTY'))
						labelCn += self.getNameByDistrictcuid(data.a('COUNTY'), 'COUNTY');
					if(data.a('TOWN'))
						labelCn += self.getNameByDistrictcuid(data.a('TOWN'), 'TOWN');
					if(data.a('COMMUNITY'))
						labelCn += data.a('COMMUNITY');
					if(data.a('ROAD'))
						labelCn += data.a('ROAD');
					if(data.a('VILLAGES'))
						labelCn += data.a('VILLAGES');
					if(data.a('ROAD_NUMBER'))
						labelCn += data.a('ROAD_NUMBER');
					if(data.a('BUILDING'))
						labelCn += data.a('BUILDING') + '号楼';
					if(data.a('UNIT_NO'))
						labelCn += data.a('UNIT_NO') + '单元';
					if(data.a('FLOOR_NO'))
						labelCn += data.a('FLOOR_NO') + '层';
					if(data.a('ROOM_NO'))
						labelCn += data.a('ROOM_NO') + '户';
					data.a("LABEL_CN", labelCn);
				}
			});
		},
		getNameByDistrictcuid : function(value, flag)
		{
			var self = this;
			var enumObj = self.enumArr[flag];
			if(enumObj.values && enumObj.labels)
			{
				for(var i = 0; i < enumObj.values.length; i++)
				{
					if(enumObj.values[i] == value)
					{
						return enumObj.labels[i];
					}
				}
			}
		},
		changeAddressEnum : function(cuid, flag){
			var self = this;
			QuerySeggroupResAction.changeAddressEnum(cuid, function(datas){
				console.info(datas);
				var modelEnum = eval("("+datas[0].MODELENUM+")");
				if(!self.enumArr)
					self.enumArr = new Array();
				self.enumArr[flag] = modelEnum;
				var dataMap = self.propertyView.getPropertyModel()._dataMap;
				for(var key in dataMap)
				{
					if(dataMap[key].getName() == flag)
					{
						dataMap[key].setEnum(modelEnum);
					}
				}
			});
		},
		createPropertyView : function(dataModel){
			var self = this;
			self.propertyView = new ht.widget.PropertyView(dataModel);
			self.propertyView.setEditable(true);
			self.propertyView.expandAll();
			self.propertyView.setColumnPosition(0.3);
			self.setCenterView(self.propertyView);
		},
		
		loadColumns : function(){
			var self = this;
			var url = ctx + "/map/column/"+self.bmClassId+"_P.json" ;
			$.ajaxSettings.async = false;
			$.getJSON(url, {}, function(data) {
				self.propertyView.getPropertyModel().clear();
				self.propertyView.addProperties(data);
				self.propertyView.invalidate();
			});
			$.ajaxSettings.async = true;
		},
		
		show : function(){
			var self = this;
			var dialog = new ht.widget.Dialog(); 
			var width = 300;
			var height = 400;
			if(dms.designer){
				if(dms.designer.isShanxi){
					height = 280;
				}
			}
			dialog.setConfig({
	            title: "<html><font size=2>属性编辑</font></html>",
	            width: width,
	            height: height,
	            titleAlign: "left", 
	            draggable: true,
	            closable: false,
	            content: self.getView(),
	            buttons : [
	            	{
	            		label : '保存',
	            		action : function(){
	            			var data = self.dataModel.getDatas().get(0);
            				var flag = self.saveDatas(data);
            				if(!flag)
            					dialog.hide();
	            		}
	            			
	            	},
	            	{
						label : '取消',
						action : function(){
							self.dataModel.clear();
							dialog.hide();
						}
	            	}
	            ],
	            buttonsAlign : 'center'
	        });
	        dialog.show();
		},
		
		saveDatas : function(data){
			var self = this;
			var objArr = new Array();
			var obj = new Object();
			obj.bmClassId = self.bmClassId;
			if(obj.bmClassId == 'T_ROFH_FULL_ADDRESS')
			{
				if(!data.a('PROVINCE'))
				{
					tp.utils.optionDialog("温馨提示", "[省份]不能为空！");
					return true;
				}
				else
				{
					data.a('PROVINCE_LABEL', self.getEnumLabel('PROVINCE', data.a('PROVINCE')));
				}
				if(!data.a('CITY'))
				{
					tp.utils.optionDialog("温馨提示", "[地市]不能为空！");
					return true;
				}
				else
				{
					data.a('CITY_LABEL', self.getEnumLabel('CITY', data.a('CITY')));
				}
				if(!data.a('COUNTY'))
				{
					tp.utils.optionDialog("温馨提示", "[区县]不能为空！");
					return true;
				}
				else
				{
					data.a('COUNTY_LABEL', self.getEnumLabel('COUNTY', data.a('COUNTY')));
				}
				if(!data.a('TOWN'))
				{
					tp.utils.optionDialog("温馨提示", "[街道办]不能为空！");
					return true;
				}
				else
				{
					data.a('TOWN_LABEL', self.getEnumLabel('TOWN', data.a('TOWN')));
				}
				if(!data.a('ROAD'))
				{
					tp.utils.optionDialog("温馨提示", "[路/巷/街]不能为空！");
					return true;
				}
				if(!data.a('VILLAGES'))
				{
					tp.utils.optionDialog("温馨提示", "[小区/单位/学校]不能为空！");
					return true;
				}
				if(!data.a('ROAD_NUMBER'))
				{
					tp.utils.optionDialog("温馨提示", "[门牌号]不能为空！");
					return true;
				}
			}
			var addnode = new ht.Node();
			for(var p in data._attrObject){
				if(p!="CLASS" && p!="PROJECT_STATE"){
					if(p=="CUID" && data.a(p)!=null){
						obj["CUID"]=data.a("CUID");
						addnode.a("CUID",data.a("CUID"));
					}else{
						if(data.a(p)!=null){
							if("object" == typeof(data.a(p))){
								obj[p] = data.a(p).id;
								addnode.a(p,data.a(p));
								addnode.a(p+"_NAME",data.a(p).labelCn);
							}else{
								obj[p] = data.a(p);
								addnode.a(p,data.a(p));
							}
						}
					}
				}
			}	
			objArr.push(obj);
			QuerySeggroupResAction.saveEditResAttr(objArr,self.flag,dms.designer.segGroupCuid,function(result){
				if(result)
				{
					if(result[0] && result[0]['温馨提示']){
						tp.utils.optionDialog("温馨提示",result[0]['温馨提示']);
						return true;
					}else{
						for(var i = 0; i < result.length; i++)
						{
							addnode.a('CUID', result[i].CUID);
						}						
					}
				}
				if(self.flag!="true"){
					
					if(self.treedata == undefined && self.resdata == undefined)
					{
						var data = self.tabView.dm().sm().ld();
						for(var p in addnode._attrObject){
							data.a(p, addnode.a(p));
						}
						return;
					}
					else
					{
						self.tabView.dm().remove(self.tabView.dm().sm().ld());
					}
				
				}else{
					var bmClassId = self.treedata.a("bmClassId");
					addnode.a('bmClassId', bmClassId);
					var rescount = self.treedata.a('rescount')+1;
					if("T_ROFH_FULL_ADDRESS"==bmClassId){
						self.treedata.setName("标准地址  ("+rescount+")");
					}else if("AN_POS"==bmClassId){
						self.treedata.setName("POS  ("+rescount+")");
					}else if("GPON_COVER"==bmClassId){
						self.treedata.setName("覆盖地址  ("+rescount+")");
					}
				}
				self.tabView.dm().add(addnode);
				if(self.resdata)
					self.resdata.push(addnode);
			});
		},
		getEnumLabel : function(flag, value){
			var self = this;
			var labels = self.enumArr[flag].labels;
			var values = self.enumArr[flag].values;
			if(labels && values)
			{
				for(var i = 0; i < values.length; i++)
				{
					if(value == values[i])
					{
						return labels[i];
					}
				}
			}
		}
	});
})(this,Object);