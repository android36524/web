$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.AddPosPropertyPanePlan = function(dm){
		Dms.Panel.AddPosPropertyPanePlan.superClass.constructor.apply(this);
		var self = this;
		self._dm = dm;
		self.createPropertyView(self._dm);
		self.loadColumns();
		self.initListener();
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
	};
	
	ht.Default.def("Dms.Panel.AddPosPropertyPanePlan", ht.widget.BorderPane, {
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
		
		createPropertyView : function(dm){
			var self = this;
			self.propertyView = new ht.widget.PropertyView(dm);
			self.propertyView.setEditable(true);
			self.propertyView.expandAll();
			self.propertyView.setColumnPosition(0.3);
			self.setCenterView(self.propertyView);
		},
		
		loadColumns : function(){
			var self = this;
			var url = ctx + "/map/property/homeCustemProperty/AN_POS.json" ;
			$.ajaxSettings.async = false;
			$.getJSON(url, {}, function(data) {
				self.propertyView.getPropertyModel().clear();
				self.propertyView.addProperties(data);
				self.propertyView.invalidate();
			});
			$.ajaxSettings.async = true;
		},
		
		initListener : function(){
			var self = this;
			self._dm.md(function(e){
				var flag = true;
				if(e.property && e.property == 'a:RATION' && e.data.a('bmClassId') == 'AN_POS')
				{
					var newValue = e.newValue;
					if(newValue && newValue.indexOf(':') != -1)
					{
						var beforeValue = newValue.substring(0, newValue.indexOf(':'));
						var afterValue = newValue.substring(newValue.indexOf(':') + 1);
						var reg = /^[1-9][0-9]*$/;
						if(reg.test(beforeValue) && reg.test(afterValue))
						{
							flag = false;
						}
					}
					if(flag)
					{
						if(self._dm.sm().ld())
							self._dm.sm().ld().a('RATION', e.oldValue);
					}
				}
			});
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
	            			var data = self._dm.sm().ld();
	            			var propertyDataModel = self.propertyView.getPropertyModel();
	            			var flag = true;
	            			var nullColumnName = null;
	            			propertyDataModel.each(function(property){
	            				var value = data.a(property.getName());
	            				if((value==undefined || (value+'').trim()=="" || (value+'').length == 0) && !property.isNullable()){
	            					flag = false;
	            					nullColumnName = property.getDisplayName();
	            				}
	            			});
	            			if(!flag){
	            				tp.utils.optionDialog("温馨提示",'【<font color="red">'+ nullColumnName +'</font>】缺少属性值，请填写完整！');
	            				return;
	            			}
	            			self.saveDatas();
	            			tp.utils.optionDialog("温馨提示",'保存成功');
							dialog.hide();
	            		}
	            			
	            	},
	            	{
						label : '取消',
						action : function(){
							var data = self._dm.sm().ld();
							//self._dm.remove(data);
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
			var data = self._dm.sm().ld();
			
			if(data)
			{
				var obj = data.a('RELATED_OLT_CUID');
				if(typeof(obj) == 'object')
				{	
					
					data.a('RELATED_OLT_CUID', obj.id);
					data.a('RELATED_OLT_CUID_NAME', obj.labelCn);
				}
				obj = data.a('RELATED_PORT_CUID');
				if(typeof(obj) == 'object')
				{
					data.a('RELATED_PORT_CUID', obj.id);
					data.a('RELATED_PORT_CUID_NAME', obj.labelCn);
				}
			}
		}
		
	});
})(this,Object);