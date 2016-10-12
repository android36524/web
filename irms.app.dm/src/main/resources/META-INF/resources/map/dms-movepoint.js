/**
 * 
 */
$importjs(ctx+'/dwr/interface/MovePointResourceAction.js');
dms.movepoint = {};
dms.movepoint.movePoint = function(){
	var mgv = Dms.Default.tpmap.getGraphView();
	
	tp.Default.DrawObject._drawState = 401;
	tp.Default.DrawObject._movePointState=1;
	
//	_movePointList 移动点列表
//	var ld = tp.Default.OperateObject.contextObject;,geometry.x,geometry.y
//	tp.Default.OperateObject.contextObject;
	mgv.addInteractorListener(function (e) {
		if(tp.Default.DrawObject._drawState == 401 && e.kind === 'endMove'){
	        console.log('结束移动图元');
	        tp.Default.DrawObject._drawState = 1;
	    	//不移动点
	    	tp.Default.DrawObject._movePointState = 0;
	        //移动完成使用这个
	        var newData =[];
	        for (var i = 0; i < tp.Default.DrawObject._movePointList.length; i++) {
	        	var item = tp.Default.DrawObject._movePointList[i];
	        	var cuid = item._id;
	        	var latlng = item.getAttr("latLng");
	        	var longiLati = latlng.lng+","+latlng.lat;
	        	newData.push({"CUID":cuid,"LONGILATI":longiLati});
    		}
	        doConfirm(newData);
	    }
	});
    
	function doConfirm(newData){
	
		var dialog = new ht.widget.Dialog();
			dialog.setConfig({
			title: "确认信息",
		    titleAlign: "left",
		    closable: true,
		    draggable: true,
		    content: "<p>确定移动资源位置？</p>",
		    buttons: [
		         {
		             label: "确定",
		             className: "button-yes"
		         },{
		             label: "取消",
		             className: "button-no"
		         }
		     ],
		     buttonsAlign: "center",
		     action: function(item, e) {
		    	 if(item.label==="确定"){
		    		 dialog.hide();
		    		 tp.utils.wholeLock();
		    		 MovePointResourceAction.modifyPointLocation(newData,function(datas){
		    			 if(datas){
		    				 
		    			 }
		    			 tp.utils.wholeUnLock();
		    			 Dms.Default.tpmap.refreshMap();
		    			 Dms.Default.tpmap.reset();
		    		 });
		    		 
		    	 }else{
		    		 var geometry = tp.Default.OperateObject.contextObject.geometry;
		    		 if(geometry){//将移动后的退回
		    			 //暂直接清空，geometry加上后再修改
		    			 Dms.Default.tpmap.reset();
		    		 }else{
		    			 Dms.Default.tpmap.reset();
		    		 }
		    		 dialog.hide();
		    		 Dms.Default.tpmap.refreshMap();
		    		 tp.utils.wholeUnLock();
		    	 }
		     }
		});
		dialog.show();
	}
};