$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.AddOnuPropertyPane = function(bmClassId,tabView,labelCn,treedata,flag){
		Dms.Panel.AddOnuPropertyPane.superClass.constructor.apply(this);
		var self = this;
		self.dataModel = new ht.DataModel();
		self.tabView = tabView;
		self.treedata = treedata;
		self.bmClassId=bmClassId;
		self.createPropertyView(self.dataModel);
		self.flag = flag;
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
		node.a("LABEL_CN",labelCn);
		self.dataModel.add(node);
		self.dataModel.sm().ss(node);
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
		
	};
	
	
	ht.Default.def("Dms.Panel.AddOnuPropertyPane", ht.widget.BorderPane, {
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
			var url = ctx + "/map/column/AN_ADDONU.json" ;
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
            				self.saveDatas(data);
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
			var addnode = new ht.Node();
			for(var p in data._attrObject){
				if(p!="PROJECT_STATE" && p!="CLASS" && p!="LABEL_CN_L9" && p!="LABEL_CN_L10" && p!="LABEL_CN_L11" && p!="LABEL_CN_L12"){
					if(p=="CUID" && data.a(p)!=null){
						obj["CUID"]=data.a("CUID");
						addnode.a("CUID",data.a("CUID"));
					}else if(p=="LABEL_CN" && data.a(p)!=null){
						obj["LABEL_CN"]=data.a("LABEL_CN")+data.a("LABEL_CN_L9")+data.a("LABEL_CN_L10")+data.a("LABEL_CN_L11")+data.a("LABEL_CN_L12");//名称
						addnode.a(p,data.a("LABEL_CN")+data.a("LABEL_CN_L9")+data.a("LABEL_CN_L10")+data.a("LABEL_CN_L11")+data.a("LABEL_CN_L12"));
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
				}else{
					continue;
				}
			}	
			objArr.push(obj);
			DWREngine.setAsync(false);
			GenerateCuidAction.saveEditResAttr(objArr,self.flag,dms.designer.segGroupCuid,function(result){
				if(self.flag!="true"){
					self.tabView.dm().remove(self.tabView.dm().sm().ld());
				}else{
					var rescount = self.treedata.a('rescount')+1;
					self.treedata.setName("ONU  ("+rescount+")");
				}
				self.tabView.dm().add(addnode);
			});
			DWREngine.setAsync(true);
		}
		
	});
})(this,Object);