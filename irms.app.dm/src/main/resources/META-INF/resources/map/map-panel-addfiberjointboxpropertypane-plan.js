$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.AddFiberjointboxPropertyPanePlan = function(dm){
		Dms.Panel.AddFiberjointboxPropertyPanePlan.superClass.constructor.apply(this);
		var self = this;
		self._dm = dm;
		self.createPropertyView(self._dm);
		self.loadColumns();
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
	};
	
	ht.Default.def("Dms.Panel.AddFiberjointboxPropertyPanePlan", ht.widget.BorderPane, {
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
			var url = ctx + "/map/property/FIBER_JOINT_BOX.json" ;
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
	            			var data = self._dm.sm().ld();
	            			var propertyDataModel = self.propertyView.getPropertyModel();
	            			var flag = true;
	            			var nullColumnName = null;
	            			propertyDataModel.each(function(property){
	            				var value = data.a(property.getName());
	            				if((value===undefined || value.toString().trim()==="" || value.length == 0) && !property.isNullable()){
	            					flag = false;
	            					nullColumnName = property.getDisplayName();
	            				}
	            			});
	            			if(!flag){
	            				tp.utils.optionDialog("温馨提示",'【<font color="red">'+ nullColumnName +'</font>】缺少属性值，请填写完整！');
	            				return;
	            			}
							dialog.hide();
	            		}
	            			
	            	},
	            	{
						label : '取消',
						action : function(){
							var data = self._dm.sm().ld();
							self._dm.remove(data);
							dialog.hide();
						}
	            	}
	            ],
	            buttonsAlign : 'center'
	        });
	        dialog.show();
		}
		
	});
})(this,Object);