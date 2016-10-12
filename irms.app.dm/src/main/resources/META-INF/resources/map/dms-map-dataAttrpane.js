//悬浮的操作按钮
$importjs(ctx+'/dwr/interface/QuerySeggroupResAction.js');
$importjs(ctx+'/dwr/interface/DuctLineAction.js');
$importjs(ctx+'/dwr/interface/DuctLineDatasAction.js');
(function(){
	"use strict";
	
	Dms.Panel.DataAttrPane = function(){
		var formPane = this;
		Dms.Panel.DataAttrPane.superClass.constructor.apply(formPane);
		
        formPane.setPadding(2);
        formPane.setHGap(2);
        formPane.setWidth(70);
        formPane.setHeight(150);
        formPane.getView().style.top = '100px';
        formPane.getView().style.right = '200px';
        formPane.getView().style.background = 'rgba(255, 255, 255, 0.5)';
        
        formPane._lines = [];
	};
	ht.Default.def('Dms.Panel.DataAttrPane',ht.widget.FormPane,{
		selectedRes : {},
		showDeleteDialog : function(resObject,tip,button){
			var self = this;
			var dialog = new ht.widget.Dialog();
                dialog.setConfig({
                    title: "资源删除确认",
                titleAlign: "left",
                closable: false,
                draggable: true,
                height : 150,
                width : 230,
                contentPadding: 35,
                content: "<p>"+ tip +"</p>",
                buttons : button,
                buttonsAlign: "center",
                action: function(item, e) {
                	if(item.label === '是' || item.label === '确定'){
                		//判断如果是线，是不是要删除点
                		self.deleteResFlow(resObject.CUID,true);
                	}else if(item.label ==='否'){
                		self.deleteResFlow(resObject.CUID,false);
                	}
                	tp.Default.OperateObject.contextObject = null;
					Dms.Default.tpmap.reset();
					Dms.Default.tpmap.refreshMap();
					self.close();
                	dialog.hide();
                }
            });
            dialog.show();
		},
		deleteResFlow : function(cuid,flag){
			var self = this;
			//添加单位工程id参数，判断要删除的资源是否属于本单位工程
			var segGroupCuid=dms.designer.segGroupCuid;
			var scene = dms.designer.scene;
			QuerySeggroupResAction.deleteResFlow(cuid,flag,segGroupCuid,scene,{
				callback : function(data){
					if(data){
						var dataModels = dms.selectData;
						if(dataModels){
							dataModels.remove(dataModels.sm().ld());
						}
						tp.utils.optionDialog('温馨提示','删除数据成功！');
					}else{
						tp.utils.optionDialog('温馨提示','删除数据失败！');
					}
					self.close();
				},
				errorHandler : function(error){
					tp.utils.optionDialog('温馨提示','删除失败：' + error);
					self.close();
					return;
				},
				async : false
			});
		},
		
		initResOperateView : function(isDesignRes,resObject){
			var formPane = this;
			formPane._resObject = resObject;
			formPane.setWidth(100);
			formPane.clear();
			formPane.addRow(["资源操作："],[0.1],16);
			formPane.addRow([], [0.1], 1.01, {background: '#43AFF1'});
			
			var viewBtn = [{
				unfocusable: true,
				button: {
					label : '查看属性',
					width : 70,
					togglable:false,
					onClicked : function(){
			        	if(resObject){
			        		formPane.showViewProperty(isDesignRes,formPane._resObject);
			        	}
					}
				}
			}];
			formPane.addRow(viewBtn,[0.1],20);
	    	formPane.setHeight(85);
		},
		
		showViewProperty : function(isDesignRes,resObject){
			var self = this;
			var dataModel = new ht.DataModel();
        	var data = new ht.Node();
        	for(var p in resObject){
			    self.selectedRes[p] = resObject[p];
			    if(resObject[p] instanceof Date){
			    	 data.a(p,self.dateToLabel(resObject[p].toString()));
			    }else{
			    	if(p ==='LONGITUDE' || p === 'LATITUDE'){
			    		data.a(p,resObject[p]+"");
			    	}else{
			    		data.a(p,resObject[p]);
			    	}
			    }
			}
			
    		var origPoint = new ht.Node();
    		var destPoint = new ht.Node();
    		if(resObject.ORIG_POINT_CUID){
    			origPoint.setId(resObject.ORIG_POINT_CUID);
    			origPoint.setName(resObject.ORIG_POINT_CUID_NAME);
    			data.a('ORIG_POINT_CUID',origPoint);
    		}
    		if(resObject.DEST_POINT_CUID){
    			destPoint.setId(resObject.DEST_POINT_CUID);
    			destPoint.setName(resObject.DEST_POINT_CUID_NAME);
    			data.a('DEST_POINT_CUID',destPoint);
    		}
        		
        	data.a('bmClassId',resObject.className);
        	dataModel.add(data);
        	dataModel.sm().ss(data);
        	var propertyPane = new Dms.Panel.PropertyPane(dataModel);
        	propertyPane.propertyView.setEditable(isDesignRes);
        	
        	var dialog = new ht.widget.Dialog(); 
        	var buttomBtns = [{
		           label : '保存'
		    }, {
		           label : '取消'
		    }];
			dialog.setConfig({
	            title: "<html><font size=2>查看属性</font></html>",
	            width: 600,
	            height: 350,
	            titleAlign: "left", 
	            draggable: true,
	            closable: true,
	            content: propertyPane.getView(),
	            buttonsAlign : 'center'
			});
        	dialog.show();
		},
		dateToLabel : function(dataValue,fmt){
			if(dataValue)
			{
				var date = new Date(dataValue);
				if(date){
					return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()
					+":"+date.getMinutes()+":"+date.getSeconds();
				}
			}
			return "";
		},
		show : function(){
			window.document.body.appendChild(this.getView());
		},
		close : function(){
			try{
				window.document.body.removeChild(this.getView());
			}catch(e){}
		}
	});
})();