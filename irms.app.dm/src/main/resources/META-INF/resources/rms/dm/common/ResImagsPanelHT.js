//Ext.ns('Frame.wire');
$importjs(ctx+'/rms/dm/common/ResImagsAction.js');
$importjs(ctx+'/rms/dm/common/dm-base.js');
createResImagsPanel = function(param){
	//布局
	var basicFormPane = new ht.widget.FormPane();
	
	var dataModel = new ht.DataModel();
	var tabView = new ht.widget.TabView(dataModel);
	basicFormPane.addRow([tabView],[0.1],0.1);
	
	var gv = new ht.graph.GraphView();
	var view = gv.getView();
	view.className = 'main';
	
	if(param && param.CUID){
		tabView.setDisabled(true,ctx+"/resources/icons/loading.gif");
		//根据资源ID获取图片信息
		IRMS.ViewHelper.request.call(this,{
			url : ctx + '/dm/getSysFileByServiceId.do',
			method : 'POST',
			params : {
				relatedServiceCuid : param.CUID
			},
			async : true,
			success : function(response){
				if(response.responseText!=""){
					var values = Ext.decode(response.responseText);
					callBackGetImgs(values);
				}
			},
			failure : function(){
				Ext.Msg.alert("操作提示", "操作失败，请确认信息后重新提交！");
			}
		});
		//ResImagsAction.getImgsByResCuid(param.CUID,callBackGetImgs);
	}
	
	//获取图片回调
	function callBackGetImgs(data){
		if(data){
			tabView.getTabModel().clear();
			for(var i=0 ; i < data.length ; i ++)
			{
				var attachFilename = data[i].ATTACH_FILENAME;
				var attachAddress = data[i].ATTACH_ADDRESS;
				//var indexOf = attachFilename.lastIndexOf(".");
				//if(indexOf > 4){
				//	attachFilename = attachFilename.substring(indexOf-4,indexOf);
				//}else{
				//	attachFilename = attachFilename.substring(0,indexOf);
				//}
				var img = createImage(ctx +"/dm/readImg.do?file="+attachAddress);
				tabView.add((i+1)+"."+attachFilename,img,true);
			}
			if(data.length > 0){
				tabView.select(0);
			}
			tabView.iv();
		}
		tabView.setDisabled(false);
	}
	function createImage(src){
			var gv = new ht.graph.GraphView();
			var view = gv.getView();
			view.className = 'main';
			
			var image = new ht.Node();
			image.setImage(src);
			image.setPosition(200,100);
			image.a('image.orig.rotation', image.getRotation());
			gv.dm().add(image);
			
			gv.onLayouted = function(x, y, width, height){
				gv.fitContent(true);
            };
            var items = [{
				unfocusable: true,
				button: {
					width: 60,
					label: '向左90°',
					onClicked: function(){
						image.setRotation(image.getRotation() - (Math.PI/2));
						gv.fitContent(true);
					}
            	}},{
				unfocusable: true,
				button: {
					width: 60,
					label: '向右90°',
					onClicked: function(){
						image.setRotation(image.getRotation() + (Math.PI/2));
						gv.fitContent(true);
					}
            	}},{
				unfocusable: true,
				button: {
					width: 60,
					label: '向左10°',
					onClicked: function(){
						image.setRotation(image.getRotation() - (Math.PI/18));
						gv.fitContent(true);
					}
            	}},{
				unfocusable: true,
				button: {
					width: 60,
					label: '向右10°',
					onClicked: function(){
						image.setRotation(image.getRotation() + (Math.PI/18));
						gv.fitContent(true);
					}
            	}},{
   				unfocusable: true,
   				button: {
   					width: 60,
   					label: '原始角度',
   					onClicked: function(){
   						image.setRotation(image.a('image.orig.rotation'));
   						gv.fitContent(true);
   					}
                }}
            ];
			var toolbar = new ht.widget.Toolbar(items);
			var borderPane = new ht.widget.BorderPane();
			borderPane.setTopView(toolbar);
            borderPane.setCenterView(gv);
            
		    return borderPane;
        }
    return basicFormPane;
};


