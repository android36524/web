//保存当前地图范围(本地缓存和数据库???)
Dms.MapBounds =  {
	saveMapBound:function (){
		var map=Dms.Default.tpmap.getMap();
		if (map==null){return;}
		var mapcenter=map.getCenter();
		var mapzoom=map.getZoom();
		if (mapcenter){		
			localStorage.mapcenter=JSON.stringify(mapcenter);
		}
		if (mapzoom){		
			localStorage.mapzoom=mapzoom;
		}
		/*
		//db
		if (ac==null){return;}
		if (ac.userId==null){return;}
		if (ac.userId==""){return;}
		var usermapview={
				'userid':ac.userId,
				'longitude':mapcenter.lng,
				'latitude':mapcenter.lat,
				'mapzoom':mapzoom };
		
		$.ajax({
			url : ctx+'/rest/MapRestService/UserMapView?do=save'+"&time="+new Date().getTime(),
			type : 'POST',
			data : usermapview,	
			dataType : 'json',				
			success : function(data) {
				alert('保存成功!');
			},
			error:function(e)
			{
				alert('保存失败!');
			}
		});	 
		*/
		
	},
	//加载地图范围
	loadMapBound:function (map){
		var suceesLoad=false;
		var mapcenter=localStorage.mapcenter;
		var mapzoom  =localStorage.mapzoom;
		if ((mapcenter!=null) && (mapzoom!=null)){
			map.setView(JSON.parse(mapcenter) , mapzoom);
			suceesLoad=true;
		}
		return suceesLoad;
	},
	//加载地图范围????数据库来源
	loadMapBoundDb : function (map){
		var suceesLoad=false;
		//db
		if (ac==null){return suceesLoad;}
		if (ac.userId==null){return suceesLoad;}
		if (ac.userId==""){return suceesLoad;}
		var userid={
				'userid':ac.userid};
		$.ajax({
			url : ctx+'/rest/MapRestService/UserMapView?do=get?userid='+ac.user+"&time="+new Date().getTime(),
			type : 'POST',
			data : userid,	
			dataType : 'json',				
			success : function(data) {
				alert('保存成功!');
				suceesLoad=true;
				var longitude=data.longitude;
				var latitude=data.latitude;
				var mapzoom  =data.mapzoom;
				if ((longitude!=null)&&(latitude!=null) && (mapzoom!=null)){
					map.setView(JSON.parse(mapcenter) , mapzoom);
					suceesLoad=true;
				}
			},
			error:function(e)
			{
				//alert('获取数据库数据失败!');
			}
		});	 
		return suceesLoad;
	}};
