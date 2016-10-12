//根据ID获取名称组件
(function(window,Object,undefined){
	"use strict";
	
	Renderer.DMActionTableCellRenderer = function() {
		Renderer.DMActionTableCellRenderer.superClass.constructor.call(this);
	};
	ht.Default.def("Renderer.DMActionTableCellRenderer", ht.Property, {
	  drawPropertyValue: function(g, property, value, rowIndex, x, y, w, h,data,view) {
	    var self = this;
	    var proName = property.getName() + "_NAME";
	    if (proName && data) {
	      var proValue = data.getAttr(proName);
	      if (proValue)
	        ht.Default.drawText(g, proValue, null, null, x, y, w, h, 'left');
	    }
	  }	  
	});
})(this,Object);