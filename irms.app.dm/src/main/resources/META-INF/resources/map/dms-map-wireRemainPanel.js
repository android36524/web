/**
 * 点设施查看预留信息
 */
(function(window,object,undefined){
	"use strict";
	
	dms.wireRemainPanel = function(tip,pointCuid){
		var self = this;
		var c = new dms.queryWireRemain(pointCuid,self);
		dms.wireRemainPanel.superClass.constructor.apply(this,[{
			title : tip + '>>>光缆预留信息',
			titleAlign :'center',
			width : 500,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 300,
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
	
	ht.Default.def('dms.wireRemainPanel',ht.widget.Panel,{
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x, y-110);
			document.body.appendChild(self.getView());
		},
		closePanel : function(){
			var self = this;
			document.body.removeChild(self.getView());
			tp.utils.wholeUnLock();
		}
	});

})(this,Object);