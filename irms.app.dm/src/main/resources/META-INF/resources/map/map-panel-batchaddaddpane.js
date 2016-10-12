$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');
$importjs(ctx+'/dwr/interface/DistNameAction.js');
//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.BatchAddAddrPropertyPane = function(bmClassId,tabView,treedata,resdata){
		Dms.Panel.BatchAddAddrPropertyPane.superClass.constructor.apply(this);
		var self = this;
		self.tabView = tabView;
		self.resdata = resdata;
		self.bmClassId = bmClassId;
		self.treedata = treedata;
		self.createFormView();
		self.index = 0;
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
		
	};
	
	
	ht.Default.def("Dms.Panel.BatchAddAddrPropertyPane", ht.widget.BorderPane, {
		formView: null,
		unitFormView: null,
		tabView : null,
		lock: function() {
			this.setDisabled(true,ctx+"/resources/icons/loading.gif");
		},
		unlock: function() {
			this.setDisabled(false);
		},
		
		createFormView : function(){
			var self = this;
			self.formView = new ht.widget.FormPane();
			self.provience = new ht.widget.ComboBox();
			self.provience.setDisabled(true);
			self.city = new ht.widget.ComboBox();
			self.county = new ht.widget.ComboBox();
			self.town = new ht.widget.ComboBox();
			self.initListener();
			if(dms.Default.user.distCuid)
				self.provience.setValue(dms.Default.user.distCuid.substring(0, 20));
			self.community = new ht.widget.TextField();
			self.road = new ht.widget.TextField();
			self.roadnumber = new ht.widget.TextField();
			self.villages = new ht.widget.TextField();
			self.villagesalias = new ht.widget.TextField();
			self.longitude = new ht.widget.TextField();
			self.pinyin = new ht.widget.TextField();
			self.latitude = new ht.widget.TextField();
			self.abbreviation = new ht.widget.TextField();
			self.postcode = new ht.widget.TextField();
			self.businesscommunity = new ht.widget.ComboBox();
			var provinceObj = {
					'element': '省份', 
					'color': 'red'
			};
			var cityObj = {
				'element': '地市', 
				'color': 'red'
			};
			var countyObj = {
				'element': '区/县', 
				'color': 'red'
			};
			var townObj = {
				'element': '街道办', 
				'color': 'red'
			};
			var roadObj = {
				'element': '路/巷/街', 
				'color': 'red'
			};
			var villagesObj = {
				'element': '小区/单位/学校', 
				'color': 'red'
			};
			var roadnumberObj = {
				'element': '门牌号码', 
				'color': 'red'
			};
			self.formView.addRow([provinceObj,self.provience,null,cityObj,self.city],[100,0.1,10,100,0.1]);
			self.formView.addRow([countyObj,self.county,null,townObj,self.town],[100,0.1,10,100,0.1]);
			self.formView.addRow(["社区",self.community,null,roadObj,self.road],[100,0.1,10,100,0.1]);
			self.formView.addRow([villagesObj,self.villages,null,roadnumberObj,self.roadnumber],[100,0.1,10,100,0.1]);
			self.formView.addRow(["小区别名",self.villagesalias,null,"经度",self.longitude],[100,0.1,10,100,0.1]);
			self.formView.addRow(["地址拼音",self.pinyin,null,"纬度",self.latitude],[100,0.1,10,100,0.1]);
			self.formView.addRow(["地址简称",self.abbreviation],[100,0.1]);
			self.formView.addRow(["邮政编码",self.postcode,null,"所属业务区",self.businesscommunity],[100,0.1,10,100,0.1]);
			self.setCenterView(self.formView);
			self.unitFormView = new ht.widget.FormPane();
			self.delUnit = new ht.widget.Button();
			self.delUnit.setLabel("删除楼宇");
			self.delUnit.onClicked = function(e){
				if(self.index>=1){
					self.unitFormView.removeRow(self.index);
					self.index = self.index-1;
				}
			};
			self.addUnit = new ht.widget.Button();
			self.addUnit.setLabel("增加楼宇");
			self.addUnit.onClicked = function(e){
				self.unitFormView.addRow(["楼号",{
					id:"unit"+self.index,
					textField:{
						text:""
					}
				},"","单元数",{
					id:"unitnum"+self.index,
					textField:{
						text:""
					} 
				},"","楼层数",{
					id:"floornum"+self.index,
					textField:{
						text:""
					}
				},"","每梯户数",{
					id:"roomnum"+self.index,
					textField:{
						text:""
					}
				},""],[30,0.1,10,50,0.1,10,50,0.1,10,50,0.1,10]);
				self.index = self.index+1;
			};
			self.unitFormView.addRow([null,self.addUnit,null,self.delUnit],[0.1,50,20,50]);
			self.setBottomView(self.unitFormView);
			
		},
		initListener : function(){
			var self = this;
			self.changeAddressEnum('', 'PROVINCE', self.provience);
			self.provience.addPropertyChangeListener(function(e)
			{
				if(e.newValue && typeof(e.newValue) == 'string')
				{
					self.changeAddressEnum(e.newValue, 'CITY', self.city);
					self.city.setValue('');
					self.county.setValue('');
					self.town.setValue('');
				}
			});
			self.city.addPropertyChangeListener(function(e)
			{
				if(e.newValue && typeof(e.newValue) == 'string')
				{
					self.changeAddressEnum(e.newValue, 'COUNTY', self.county);
					self.county.setValue('');
					self.town.setValue('');
				}
			});
			self.county.addPropertyChangeListener(function(e)
			{
				if(e.newValue && typeof(e.newValue) == 'string')
				{
					self.changeAddressEnum(e.newValue, 'TOWN', self.town);
					self.town.setValue('');
				}
			});
		},
		changeAddressEnum : function(cuid, flag, combo){
			var self = this;
			QuerySeggroupResAction.changeAddressEnum(cuid, function(datas){
				console.info(datas);
				var modelEnum = eval("("+datas[0].MODELENUM+")");
				if(!self.enumArr)
					self.enumArr = new Array();
				self.enumArr[flag] = modelEnum;
				combo.setLabels(modelEnum.labels);
				combo.setValues(modelEnum.values);
			});
		},
		show : function(){
			var self = this;
			var dialog = new ht.widget.Dialog(); 
			var width = 700;
			var height = 400;
			dialog.setConfig({
	            title: "<html><font size=2>标准地址批量新增</font></html>",
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
	            			var flag = self.saveDatas();
	            			if(!flag)
	            				dialog.hide();
	            		}
	            			
	            	},
	            	{
						label : '取消',
						action : function(){
							dialog.hide();
						}
	            	}
	            ],
	            buttonsAlign : 'center'
	        });
	        dialog.show();
		},
		
		saveDatas : function(){
			var self = this;
			var reg = /^(\d+,?)+$/;
			var numreg = /^\+?[1-9][0-9]*$/;
			var errormsg = "";
			var addrs = [];
			var nodearr = [];
			for(var i=0;i<self.index;i++){
				if(self.unitFormView.getItemById("unit"+i)!=null){
					var unit = self.unitFormView.getItemById("unit"+i);
					if(!reg.test(unit.element.getValue())){
						errormsg +="第"+i+"行楼号输入格式错误,请输入单个数字或英文逗号分隔的数字 "+"/n";
					}
					var unitnum = self.unitFormView.getItemById("unitnum"+i);
					if(!numreg.test(unitnum.element.getValue())){
						errormsg +="第"+i+"行单元数输入格式错误,请输入大于0的整数 "+"/n";
					}
					var floornum = self.unitFormView.getItemById("floornum"+i);
					if(!numreg.test(floornum.element.getValue())){
						errormsg +="第"+i+"行楼层数输入格式错误,请输入大于0的整数 "+"/n";
					}
					var roomnum = self.unitFormView.getItemById("roomnum"+i);
					if(!numreg.test(roomnum.element.getValue())){
						errormsg +="第"+i+"行每梯户数数输入格式错误,请输入大于0的整数 "+"/n";
					}
				}
			}
			if(errormsg.length>1){
				alert(errormsg);
			}else{
				var provinceLabel = self.getSelectLable(self.provience);
				var cityLabel = self.getSelectLable(self.city);
				var countyLabel = self.getSelectLable(self.county);
				var townLabel = self.getSelectLable(self.town);
				for(var i=0;i<self.index;i++){
					if(self.unitFormView.getItemById("unit"+i)!=null){
						var unit = self.unitFormView.getItemById("unit"+i);
						var builds = unit.element.getValue().split(",");
						var unitnum = self.unitFormView.getItemById("unitnum"+i).element.getValue();
						var floornum = self.unitFormView.getItemById("floornum"+i).element.getValue();
						var roomnum = self.unitFormView.getItemById("roomnum"+i).element.getValue();
						for(var j=0;j<builds.length;j++){
							var building = builds[j];
							for(var a=0;a<unitnum;a++){
								var unitindex = a+1;
								for(var b=0;b<floornum;b++){
									var floorindex = b+1;
									for(var c=0;c<roomnum;c++){
										var roomindex=c+1;
										var node = new ht.Node();
										var obj = new Object();
										var labelcn = provinceLabel+cityLabel+countyLabel
										+townLabel+self.community.getValue()+self.road.getValue()+self.villages.getValue()
										+self.roadnumber.getValue()+self.villagesalias.getValue()+building+"号楼"+unitindex+"单元"
										+floorindex+"层"+roomindex+"户";
										node.a('LABEL_CN',labelcn);
										obj.LABEL_CN=labelcn;
										node.a('PROVINCE',self.provience.getValue());
										obj.PROVINCE=self.provience.getValue();
										node.a('PROVINCE_LABEL',provinceLabel);
										obj.PROVINCE_NAME=provinceLabel;
										node.a('CITY',self.city.getValue());
										obj.CITY=self.city.getValue();
										node.a('CITY_LABEL',cityLabel);
										obj.CITY_NAME=cityLabel;
										node.a('COUNTY',self.county.getValue());
										obj.COUNTY=self.county.getValue();
										node.a('COUNTY_LABEL',countyLabel);
										obj.COUNTY_NAME=countyLabel;
										node.a('TOWN',self.town.getValue());
										obj.TOWN=self.town.getValue();
										node.a('TOWN_LABEL',townLabel);
										obj.TOWN_NAME=townLabel;
										node.a('VILLAGES',self.villages.getValue());
										obj.VILLAGES=self.villages.getValue();
										node.a('ROAD',self.road.getValue());
										obj.ROAD=self.road.getValue();
										node.a('VILLAGES_ALIAS',self.villagesalias.getValue());
										obj.VILLAGES_ALIAS=self.villagesalias.getValue();
										node.a('ROAD_NUMBER',self.roadnumber.getValue());
										obj.ROAD_NUMBER=self.roadnumber.getValue();
										node.a('LONGITUDE',self.longitude.getValue());
										obj.LONGITUDE=self.longitude.getValue();
										node.a('PINYIN',self.pinyin.getValue());
										obj.PINYIN=self.pinyin.getValue();
										node.a('LATITUDE',self.latitude.getValue());
										obj.LATITUDE=self.latitude.getValue();
										node.a('ABBREVIATION',self.abbreviation.getValue());
										obj.ABBREVIATION=self.abbreviation.getValue();
										node.a('POSTCODE',self.postcode.getValue());
										obj.POSTCODE=self.postcode.getValue();
										node.a('RELATED_COMMUNITY_CUID',self.businesscommunity.getValue());
										obj.RELATED_COMMUNITY_CUID=self.businesscommunity.getValue();
										node.a('BUILDING',building);
										obj.BUILDING=building;
										node.a('UNIT_NO',unitindex);
										obj.UNIT_NO=unitindex;
										node.a('FLOOR_NO',floorindex);
										obj.FLOOR_NO=floorindex;
										node.a('ROOM_NO',roomindex);
										obj.ROOM_NO=roomindex;
										node.a('bmClassId', 'T_ROFH_FULL_ADDRESS');
										obj.bmClassId = 'T_ROFH_FULL_ADDRESS';
										addrs.push(obj);
										nodearr.push(node);
									}
								}
							}
						}
					}
				}
			}
			if(addrs.length>0){
				DWREngine.setAsync(false);
				QuerySeggroupResAction.saveEditResAttr(addrs,"true",dms.designer.segGroupCuid,function(result){
					if(result && result[0] && result[0]['温馨提示']){
						tp.utils.optionDialog("温馨提示",result[0]['温馨提示']);
						return true;
					}
					for(var i=0;i<nodearr.length;i++){
						if(result && result[i] && typeof(result[i]) == 'object')
							nodearr[i].a('CUID', result[i].CUID);
						self.tabView.dm().add(nodearr[i]);
						if(self.resdata)
							self.resdata.push(nodearr[i]);
					}
					var rescount = self.treedata.a('rescount')+addrs.length;
					self.treedata.setName("标准地址  ("+rescount+")");
				});
				DWREngine.setAsync(true);
			}
		},
		getSelectLable : function(combox){
			var value = combox.getValue();
			for(var i = 0; i < combox.getValues().length; i++)
			{
				if(combox.getValues()[i] == value)
				{
					return combox.getLabels()[i];
				}
			}
		}
	});
})(this,Object);