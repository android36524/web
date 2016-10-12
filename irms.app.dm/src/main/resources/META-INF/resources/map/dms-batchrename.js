
//批量命名界面
Dms.BatchNameView = function(topoType)
{
	Dms.BatchNameView.superClass.constructor.apply(this);	
	var self = this;
	var classname = tp.Default.DrawObject._drawPointClass;
	if(classname === 'FIBER_JOINT_BOX' && tp.Default.DrawObject._kind === '2'){
		topoType ='光终端盒';
	}
	self.topoType = topoType;
	
	self.data = new ht.Data();
	self.dm().add(self.data);
	self.getSelectionModel().setSelection(self.data);  
	self.data.suffix = '号'+topoType;
	self.data.serialNumber = '000';
	self.data.origNumber = 1;
	self.data.count = 1;
	var popertyModel = self.getPropertyModel();
	
	self.prefixProperty = new ht.Property();             
	self.prefixProperty.setName('prefix');
	self.prefixProperty.setDisplayName('前缀');
	self.prefixProperty.setAccessType('field');
	self.prefixProperty.setEditable(true);
	self.prefixProperty.setCategoryName('基本属性');
	popertyModel.add(self.prefixProperty); 
	
	self.suffixProperty = new ht.Property();             
	self.suffixProperty.setName('suffix');
	self.suffixProperty.setDisplayName('后缀');
	self.suffixProperty.setAccessType('field');
	self.suffixProperty.setEnum({values:['号'+topoType,'#',''], labels:['号'+topoType,'#','']});
	self.suffixProperty.setEditable(true);
	self.suffixProperty.setCategoryName('基本属性');
	popertyModel.add(self.suffixProperty);

	self.serialNumberProperty = new ht.Property();
	self.serialNumberProperty.setName('serialNumber');		
	self.serialNumberProperty.setDisplayName('编号位数');
	self.serialNumberProperty.setAccessType('field');	
	self.serialNumberProperty.setEditable(true);	
	self.serialNumberProperty.setEnum({values:['0','00','000','0000','00000'], labels:['0','00','000','0000','00000']});
	self.serialNumberProperty.setCategoryName('基本属性');
	popertyModel.add(self.serialNumberProperty);
	
	self.origNumberProperty = new ht.Property();
	self.origNumberProperty.setName('origNumber');		
	self.origNumberProperty.setDisplayName('起始编号');
	self.origNumberProperty.setAccessType('field');
	self.origNumberProperty.setEditable(true);
	self.origNumberProperty.setCategoryName('基本属性');
	popertyModel.add(self.origNumberProperty);

	self.countProperty = new ht.Property();
	self.countProperty.setName('count');		
	self.countProperty.setDisplayName('数量');
	self.countProperty.setAccessType('field');
	self.countProperty.setEditable(true);
	self.countProperty.setCategoryName('基本属性');
	popertyModel.add(self.countProperty);

};
ht.Default.def('Dms.BatchNameView',ht.widget.PropertyView, {
	topoType:null,
	prefixProperty:null,
	suffixProperty:null,
	serialNumberProperty:null,
	origNumberProperty:null,
	countProperty:null,
	getBatchNames:function()
	{
		this.reqValues = {"prefix":this.data.prefix,
							  "suffix":this.data.suffix,
							  "serialNumber":this.data.serialNumber,
							  "origNumber":this.data.origNumber,
							  "count":this.data.count};
		return this.reqValues;
	}
});




