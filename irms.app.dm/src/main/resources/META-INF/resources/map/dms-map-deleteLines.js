/**
 * 删除光缆
 */
(function(window,object,undefined){
	"use strict";
	
	dms.deleteLinesPanel = function(title,tip){
		var self = this;
		self.tip = tip;
		var wireSystemCuid = self.systemCuid= tp.Default.OperateObject.contextObject.cuid;
		var c = new dms.deleteResourcePanel(title,tip,wireSystemCuid,self);
		dms.deleteLinesPanel.superClass.constructor.apply(this,[{
			title : title,
			titleAlign :'center',
			width : 600,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 380,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon: ctx+'/map/close.png',
				action:function(){
//					self.clearDeleteDefault();
					document.body.removeChild(self.getView());
				}
			}],
			content : c
		
		}]);
		self.fp = function(){};

		window.addEventListener('resize', function (e) {
			self.invalidate();
        }, false); 
	};
	
	ht.Default.def('dms.deleteLinesPanel',ht.widget.Panel,{
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x-120, y-110);
			document.body.appendChild(self.getView());
		},
		_win : function(){
			return false;
		}
	});

})(this,Object);