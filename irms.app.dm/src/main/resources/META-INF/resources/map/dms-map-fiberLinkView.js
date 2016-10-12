/**
 * 查询管线长度
 */
$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');

(function(window,object,undefined){
	"use strict";
	
	dms.fiberLinkViewPanel = function(cuid,bmId,isRoom,attrName){
		var self = this;
		var pCuid = null;
		DWREngine.setAsync(false);
		GenerateCuidAction.getOrigAndDestPointByWireSegCuid(cuid,attrName,function(data){
			if(data){
				pCuid = data[0].POINT_CUID;
			}
		});
		DWREngine.setAsync(true);
		if(pCuid){
			var className = pCuid.split("-")[0];
			if(className !='SITE'){
				if(attrName == 'ORIG_POINT_CUID'){
					tp.utils.optionDialog("错误提示","该光缆段左端不是站点,不能进行左端纤芯上架");
				}else{
					tp.utils.optionDialog("错误提示","该光缆段右端不是站点，不能进行右端纤芯上架");
				}
				return;
			}
		}
		var c = dms.fiberLinkViewPanel.linkpanel = new Topo.x.FiberLinkView('SITE',pCuid,true);
//		tp.utils.lock(c);
		dms.fiberLinkViewPanel.superClass.constructor.apply(this,[{
			title : "纤芯上架：",
			titleAlign :'center',
			width : 700,
			exclusive : false,
			titleColor : "white",
			minimizable : true,
			minimized  : false,//控制打开时界面是不是最小化
			expanded : true,
			narrowWhenCollapse : true,
			contentHeight : 450,
			buttons:['minimize',{
				name : '关闭',
				toolTip:'关闭',
				icon:'close.png',
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
	
	ht.Default.def('dms.fiberLinkViewPanel',ht.widget.Panel,{
		show : function(){
			var self = this;
			var x = (window.screen.availWidth - self._config.width)/2;
			var y = (window.screen.availHeight - self._config.contentHeight)/2;
			self.setPosition(x-100, y-120);
			document.body.appendChild(self.getView());
		},
		closePanel : function(){
			var self = this;
			tp.utils.unlock(dms.fiberLinkViewPanel.linkpanel);
			Dms.Default.tpmap.reset();
			document.body.removeChild(self.getView());
		}
	});

})(this,Object);