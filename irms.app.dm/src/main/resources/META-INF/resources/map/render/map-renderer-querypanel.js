//查询框组件
(function(window,Object,undefined){
	"use strict";
	
	Renderer.QueryPanelRenderer = function() {
		Renderer.QueryPanelRenderer.superClass.constructor.call(this);
	};
	ht.Default.def("Renderer.QueryPanelRenderer", ht.Property, {
	  
	  drawPropertyValue: function(g, property, value, rowIndex, x, y, w, h, data, view) {
	    var self = this;
	    if (!self.wrapper) {
	      var wrapper = self.wrapper = document.createElement("div"),
	      div = '<div style="display: inline-block;float:left"></div>'+
	  		'<img src="' + ctx + '/resources/tool/zoom.png" width="16" height="20" align="right" style="cursor:pointer" />';
	      wrapper.innerHTML = div;
	      wrapper.style.width = "100%";
	      wrapper.style.height = "100%";
	      wrapper.style.font = "12px arial, sans-serif";
	    };
	    
	    var lineClass = tp.Default.DrawObject._drawLineClass;
	    var systemClass = dms.designer.systemType[lineClass];
	    var code = Dms.selectSystemNameResName[lineClass];
	    
	   /* var attrs="",boclassName="",boname="",formName="";
  		attrs = Dms.selectSystemTypeResName[code];
	    if(attrs){
		  var attr = attrs.split(",");
		  boclassName = attr[0];
		  boname = attr[1];
		  formName = attr[2];
	    }*/
	    
	    var queryDiv = self.wrapper.children[0],
	   	queryImg = self.wrapper.children[1];
   	    if(data.a('isDesignRes') !== undefined){
      		queryImg.style.display = "none";
        }
	    queryImg.onclick = function(){
	    	  var dialog = new ht.widget.Dialog();
			  var c = tp.utils.createQueryPanel(code, dialog);
			  dialog.setConfig({
			    title: "<html><font size=3>查询</font></html>",
			    width: 800,
			    height: 500,
			    titleAlign: "left",
			    draggable: true,
			    closable: true,
			    content: c
			  });
			  
			  
			  document.body.onclick = function(e){
			  	var event = e || window.event; 
				var target = event.target || event.srcElement;
				if(target.tagName.toLowerCase() === "button" && target.type === "submit" && (target.innerHTML === "选择"||target.innerHTML === "确认" || target.innerHTML === "确定")){
					var splitView = c.getCenterView();
			  		var pageTable = splitView.getLeftView();
			  		var selectData = pageTable.getTableView().dm().sm().fd();
			  		if(selectData){
			  			self.convertEnum(selectData);
			  			data._attrObject = selectData._attrObject;
			  			data.a('RELATED_SPACE_CUID_NAME',Dms.Default.user.distName);
			  			data.a('bmClassId',systemClass);
			  			data.setTag('systemData');
			  			data.setId(data.a('CUID'));
			  			data.invalidate();
			  			dms.designer.panel.systemPropertyPanel.invalidate();
			  		}
				}
			  };
		  
		  	dialog.show();
	    };
	    
	    queryDiv.style.width = w - 20 + "px"; 
	    queryDiv.innerHTML = data.a(property.getName()) || "&nbsp;";
	    return self.wrapper;
	  },
	  
	  
	  convertEnum : function(data){
	  	var propertyEnum = {
	  		'OWNERSHIP' : {'未知':0,'自建':1,'共建':2,'合建':3,'租用':4,'购买':5,'置换':6},
	  		'SYSTEM_LEVEL' : {'未知':0,'省际':1,'省内':2,'本地骨干':3,'本地汇聚':4,'本地接入':5},
	  		'MAINT_MODE' : {'未知':0,'自维':1,'代维':2},
	  		'STATE' : {'未知':0,'设计':1,'施工/在建':2,'竣工':3,'废弃/退网':4,'维护':5,'规划':6,'一级设计':7,'待审批':9}
	  	};
	  	
	  	for(var p in data._attrObject){
	  		if(propertyEnum[p]){
	  			var value = data.a(p);
	  			data.a(p,propertyEnum[p][value]);
	  		}
	  	}
	  }
	  
	});
})(this,Object);