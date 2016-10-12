//系统菜单组件
Dms.initToolbar = (function(graphView)
{
	return function(graphView){
		var tpmap = Dms.Default.tpmap;
	var addPointMenu = new ht.widget.Menu(
				[{label:"新增点设施",
					items: [
//						{label:"站点",action:function(item){Dms.addMapPoint('SITE');}},
						{label:"接入点",action:function(item){tpmap.addMapPoint('ACCESSPOINT');}},
						{label:"人手井",action:function(item){tpmap.addMapPoint('MANHLE');}},
						{label:"电杆",action:function(item){tpmap.addMapPoint('POLE');}},
						{label:"标石",action:function(item){tpmap.addMapPoint('STONE');}},
						{label:"拐点",action:function(item){tpmap.addMapPoint('INFLEXION');}},
						{label:"光交接箱",action:function(item){tpmap.addMapPoint('FIBER_CAB');}},
						{label:"光分纤箱",action:function(item){tpmap.addMapPoint('FIBER_DP');}},
						{label:"光接头盒",action:function(item){tpmap.addMapPoint('FIBER_JOINT_BOX');}}
					]
				}]
	);
	addPointMenu.getView().style.display = "inline-block";
	var addLineMenu = new ht.widget.Menu(
				[{label:"新增线设施",
					items: [
						{label:"光缆段",action:function(item){tpmap.addMapLine('WIRE_SEG','FIBER_JOINT_BOX');}},
						{label:"管道分支",action:function(item){tpmap.addMapLine('DUCT_SEG','MANHLE');}},
						{label:"杆路分支",action:function(item){tpmap.addMapLine('POLEWAY_SEG','POLE');}},
						{label:"标石路由分支",action:function(item){tpmap.addMapLine('STONEWAY_SEG','STONE');}},
						{label:"引上系统",action:function(item){tpmap.addMapLine('UP_LINE_SEG','INFLEXION');}},
						{label:"挂墙系统",action:function(item){tpmap.addMapLine('HANG_WALL_SEG','INFLEXION');}}
					]
				}]
	);
	addLineMenu.getView().style.display = "inline-block";
	var editToolbar = new ht.widget.Toolbar([{
							label : '平移',
							selected : true,
							groupId: 'state',
							action : function(item) {
								if(item.selected)
								{
									tp.Default.DrawObject._movePointState = 0;
									tp.Default.DrawObject._drawState = 0;
									graphView.getView().style.cursor = 'hand';
								}
							}
						},				
						{
							label : '选择',
							groupId: 'state',
							action : function(item) {
								if(item.selected)
								{
									tp.Default.DrawObject._movePointState = 0;
									tp.Default.DrawObject._drawState = 1;
									graphView.getView().style.cursor = 'default';
								}
							}
						},
						{
							label : '移动点设施',
							groupId: 'state',
							action : function(item) {
								if(item.selected)
								{
									tp.Default.DrawObject._movePointState = 1;
									tp.Default.DrawObject._drawState = 1;
									graphView.getView().style.cursor = 'default';
								}else
								{
									tp.Default.DrawObject._movePointState = 0;
									tp.Default.DrawObject._drawState = 0;
									graphView.getView().style.cursor = 'hand';
								}
								
							}
						},
						{
		                    label : '全图',
		                    action: function(){
		                    	map.setZoom(0);
		                    }
		                },
						{
							label : '放大',
							action : function() {
								if(map.getZoom() < map.getMaxZoom())
									map.zoomIn();
							}
						},
						"separator",
	                    {
							label : '缩小',
							action : function() {
								if(map.getZoom() > map.getMinZoom())
									map.zoomOut();
							}
						},"separator",
						{
							label : '测距',
							action : function() {
								tpmap.measureLine();
							}
						},
						{
							label : '测面积',
							action : function() {
								tpmap.measurePloygon();
							}
						},
						{
							element: addPointMenu.getView()
						},
		                {
							element: addLineMenu.getView()
		                },
		                {
		                    label: '清除所有选择',  
		                    action: function(){
		                    	tpmap.reset();
		                    }
					}]);
	var	view = editToolbar.getView();
	view.style.position = "static";
    view.style.background = "lightblue";
	view.style.width = 600;
    
    var editWindow = new ht.widget.FloatingWindow(editToolbar);
    editWindow.getView().style.left = "45px";
    editWindow.getView().style.top = "20px";
    editWindow.getView().className = 'floatingWindow'; 

    editWindow.setBackground("lightblue");
    
    editWindow.setContent(editToolbar);
	editWindow.addTo(graphView.getView());
	//取消事件传递
	editWindow.getView().addEventListener("click", function(e){e.stopPropagation();});
	editWindow.getView().addEventListener("dblclick", function(e){e.stopPropagation();});
    editWindow.show();	
	};
})();

