//可输入下拉框
(function(window,Object,undefined){
	"use strict";
	
	tp.widget.InputCombox = function(datasource,sql){
		var self = this;
		
		tp.widget.InputCombox.superClass.constructor.apply(this);
		this.setEditable(true);
		
		this.onInputCreated = function(input){
			input.oninput = function(){
				if(input.value && input.value.length > 0)
				{
					MapComboxAction.getEnumValue(datasource,sql.replace("PARAMETER",input.value),function(result){
						if(result && result.values)
						{
							self.setValues(result.values);
							self.setLabels(result.labels);
						}
						self.setValue("");
					});
				}
	//			self.setValues([0,1,2,3,4,5,6]);
	//			self.setLabels(["不限","省际","省内","省内+本地","本地骨干","本地汇聚","本地接入"]);
				//如何将下拉框展开
				
			};
		};
		
	};
	ht.Default.def('tp.widget.InputCombox',ht.widget.ComboBox,{
		
	});
})(this,Object);