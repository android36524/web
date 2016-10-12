$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.AddGponCoverPropertyPane = function(bmClassId,tabView,treedata,flag,resdata){
		Dms.Panel.AddGponCoverPropertyPane.superClass.constructor.apply(this);
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
	};
	
	
	ht.Default.def("Dms.Panel.AddGponCoverPropertyPane", ht.widget.BorderPane, {
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
					if(e.property == 'a:CITY')
					{
						if(e.newValue)
						{
							if(!data.a('PROVINCE'))
							{
								data.a('CITY', '');
								tp.utils.optionDialog("温馨提示", "请先填写[省份]！");
								return;
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
						labelCn += data.a('PROVINCE');
					if(data.a('CITY'))
						labelCn += '|' + data.a('CITY');
					if(data.a('COUNTY'))
						labelCn += '|' + data.a('COUNTY');
					if(data.a('TOWN'))
						labelCn += '|' + data.a('TOWN');
					if(data.a('COMMUNITY'))
						labelCn += '|' + data.a('COMMUNITY');
					if(data.a('ROAD'))
						labelCn += '|' + data.a('ROAD');
					if(data.a('VILLAGES'))
						labelCn += '|' + data.a('VILLAGES');
					if(data.a('ROAD_NUMBER'))
						labelCn += '|' + data.a('ROAD_NUMBER');
					if(data.a('BUILDING'))
						labelCn += '|' + data.a('BUILDING');
					if(data.a('UNIT_NO'))
						labelCn += '|' + data.a('UNIT_NO');
					if(data.a('FLOOR_NO'))
						labelCn += '|' + data.a('FLOOR_NO');
					if(data.a('ROOM_NO'))
						labelCn += '|' + data.a('ROOM_NO');
					data.a("LABEL_CN", labelCn);
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
			var nodeArr = new Array();
			if(!data.a('RELATED_NE_CUID'))
			{
				tp.utils.optionDialog("温馨提示", "[设备名称]不能为空！");
				return true;
			}
			if(!data.a('STANDARD_ADDR'))
			{
				tp.utils.optionDialog("温馨提示", "[标准地址]不能为空！");
				return true;
			}
			var standard = data.a('STANDARD_ADDR');
			var cuidArr = standard.id.split(',');
			var labelCnArr =  standard.labelCn.split(',');
			for(var i = 0; i < cuidArr.length; i++)
			{
				var obj = new Object();
				obj.bmClassId = self.bmClassId;
				var addnode = new ht.Node();
				addnode.a("bmClassId","GPON_COVER");
				obj['STANDARD_ADDR'] = cuidArr[i];
				addnode.a('STANDARD_ADDR', cuidArr[i]);
				addnode.a('STANDARD_ADDR_NAME', labelCnArr[i]);
				
				var neId = data.a('RELATED_NE_CUID').id;
				var neLabel = data.a('RELATED_NE_CUID').labelCn;				
				obj['RELATED_NE_CUID'] = neId;
				addnode.a('RELATED_NE_CUID', neId);
				addnode.a('DEVICE_NAME', neLabel);
				obj['DEVICE_NAME'] = neLabel;
				var deviceType = neId.indexOf('ONU') != -1 ? 'ONU' : 'POS';
				addnode.a('DEVICE_TYPE', deviceType);
				obj['DEVICE_TYPE'] = deviceType;
				
				
				obj['COVER_RANGE'] = data.a('COVER_RANGE');
				addnode.a('COVER_RANGE', data.a('COVER_RANGE'));
				objArr.push(obj);
				nodeArr.push(addnode);
			}
			DWREngine.setAsync(false);
			QuerySeggroupResAction.saveEditResAttr(objArr,self.flag,dms.designer.segGroupCuid,function(result){
				if(result){
					if(result[0] && result[0]['温馨提示']){
						tp.utils.optionDialog("温馨提示",result[0]['温馨提示']);
						return true;
					}				
					else{
						for(var i = 0; i < nodeArr.length; i++)
						{
							nodeArr[i].a('CUID', result[i].CUID);
						}
					}
					if(self.flag!="true"){
						self.tabView.dm().remove(self.tabView.dm().sm().ld());
					}else{
						var bmClassId = self.treedata.a("bmClassId");
						var rescount = self.treedata.a('rescount')+nodeArr.length;
						if("GPON_COVER"==bmClassId){
							self.treedata.setName("覆盖地址  ("+rescount+")");
						}
					}
					for(var i = 0; i < nodeArr.length; i++)
					{	
						
						self.tabView.dm().add(nodeArr[i]);
						if(self.resdata)
							self.resdata.push(nodeArr[i]);
					}
				}
			});
			DWREngine.setAsync(true);
		}
		
	});
})(this,Object);