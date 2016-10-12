$importjs(ctx + "/dwr/interface/MapComboxAction.js");
//可输入下拉框
(function(window,Object,undefined){
	"use strict";
	
	tp.widget.DropDownList = function(master){
		var self = this;
		tp.widget.DropDownList.superClass.constructor.call(self,master);
		self._master = master;
		var listView = self._listView = new ht.widget.ListView();
		
		listView.sm().ms(function(e){
			master.setValue(self.getValue());
		});
		
		listView.onDataClicked = function(data){
			master.callback.call(master.parentPanel,data);
			master.close();
		};
		
		listView.getView().style.background = 'white';
		
		self.bindingHandleInputValueChange = self.handleInputValueChange.bind(self);
		
	};
	ht.Default.def('tp.widget.DropDownList',ht.widget.BaseDropDownTemplate,{
		getView : function(){
			return this._listView.getView();
		},
		
		handleInputValueChange : function(){
			var self = this;
			if(self._listView.dm().sm().size() > 0){
				self._listView.dm().sm().cs();
			}
		},
		
		getValue : function(){
			var self = this;
			
			var currentValue = self._master._input.value;
			var names = "";
			var listView = self._master.listView = self._listView;
			if(listView.sm().size() === 0){
				return currentValue;
			}
			listView.sm().each(function(data){
				names += data.getName() + ",";
			});
			if(names !== ""){
				names = names.substr(0,names.length-1);
			}
			return names;
		},
		onClosed : function(){},
		onOpened : function(value){
			var self = this;
			
			var dm = self._master.listDatas;
			if(dm && dm.size() > 0){
				self._listView.setDataModel(dm);
			}
			//self.bindingHandleInputValueChange();
			self._master._input.addEventListener('keyup',self.bindingHandleInputValueChange);
		},
		
		getHeight : function(){
			return 200;
		}
		
	});
})(this,Object);