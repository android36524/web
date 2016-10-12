/**
 * 用于打开htpanel界面
 */
(function(window,object,undefined){
	"use strict";
	
	dms.Default.openHtPanel = function(panel,tip,width,height,icon){
		var self = this;
		//用于panel关闭此父窗口
		var childPanel = panel.getResult();
		childPanel._win = self;
		
		self.openPanel = panel;
		var c = self.getPanel();
		self.width = width;
		self.height = height;
		if(icon){
			self.icon = icon;
		}else{
			self.icon = 'mobile';
		}
		if(!self.width){
			self.width = 500;
		}
		
		if(!self.height){
			self.height = 300;
		}
		
		dms.Default.openHtPanel.superClass.constructor.apply(this,[{
			title : tip,
			titleAlign :'center',
			width : self.width,
			titleIcon : self.icon,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : self.height,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon: ctx+'/map/close.png',
				action:function(){
					self.closePanel();
				}
			}],
			content : c
		}]);
		self.fp = function(){};

		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 
	};
	
	ht.Default.def('dms.Default.openHtPanel',ht.widget.Panel,{
		width : null,
		height : null,
		icon: null,
		openPanel : null,
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x, y-110);
			document.body.appendChild(self.getView());
			self.getView().style.zIndex = 999;
		},
		closePanel : function(){
			var self = this;
			document.body.removeChild(self.getView());
			dms.Default.tpmap.reset();
			tp.utils.wholeUnLock();
		},
		getPanel : function(){
			var self = this;
			var panel = self.openPanel; 
		  if(!panel){
			  panel = new ht.widget.FormPane();
		  }
		  return panel;
		}
	});

})(this,Object);