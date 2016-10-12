/**
 * 光交接箱端子直熔
 */
(function(window,object,undefined){
	"use strict";
	
	Topo.x.portFusionPanel = function(tip,pointCuid){
		var self = this;
		var c = new Topo.x.portFusion(pointCuid,self);
		Topo.x.portFusionPanel.superClass.constructor.apply(this,[{
			title : tip + '>>>端子直熔',
			titleAlign :'center',
			width : 600,
			exclusive : true,
			titleColor : "white",
			draggable: true,
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
	
	ht.Default.def('Topo.x.portFusionPanel',ht.widget.Panel,{
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - 1000)/2;
			var y = (window.screen.availHeight- self._config.contentHeight)/2;
			self.setPosition(x, y-110);
			document.body.appendChild(self.getView());
			tp.utils.lock(Topo.Default.graphView);
		},
		closePanel : function(){
			var self = this;
			tp.utils.unlock(Topo.Default.graphView);
			document.body.removeChild(self.getView());
		}
	});

})(this,Object);