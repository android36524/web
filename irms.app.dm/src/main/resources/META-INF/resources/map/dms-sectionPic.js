Dms.sectionPic={};
Dms.sectionPic.createDuctSectionPicDialog = function(segCuid,callback){
		var dialog = new ht.widget.Dialog(); 
		var c = Dms.sectionPic.createSectionPicPanel(segCuid,dialog);
		dialog.setConfig({
            title: "<html><font size=3>"+"选择管孔子孔</font></html>",
            width: 600,
            height: 400,
            titleAlign: "left", 
            draggable: true,
            closable: true,
            content: c,
            buttons: [
                      {
                    	  label: "确定",
                          action: function(){
                        	  callback(c.getResult());
                        	  this.hide();
                          }
                      },
                      {
                          label: "取消",
                          action: function(){
                          	this.hide();
                          }
                      }
                  ]
        });
	    dialog.onShown = function(operation) {
	        
	    };
	    dialog.onHidden = function(operation) {
//	    	callback(c.getResult());
	    };
	    //在前面用了99999，所以这里用999不行
	    dialog.getView().style.zIndex = 99999;
	    dialog.show();
		return c;
	};
	
Dms.sectionPic.createSectionPicPanel = function(ductSegCuid,dialog)
{
//	var dataModel = new ht.DataModel();
    var basicFormPane = new ht.widget.FormPane(); 
    
	var gv = new ht.graph.GraphView();
	gv.setEditable(false);
	gv.isMovable = function(data)
	{
		return false;
	};

	Dms.sectionPic.getHolePicBySegCuid(ductSegCuid,gv,basicFormPane);
    basicFormPane.addRow([gv],[0.1],[0.1]);

    basicFormPane.getResult = function(){
    	var result = Dms.sectionPic.getSelectPicResult(ductSegCuid,gv);
    	return result;
    };
    return basicFormPane;
};
Dms.sectionPic.getSelectPicResult = function(ductSegCuid,gv){
	var data = gv.dm().sm().ld();
	var _parent = data._parent;
	var result = {cuid:'',labelCn:'',related_seg_cuid:ductSegCuid,parentCuid:'',parentName:''};
	if(data){
		var dataCuid = data._id,dataName = data._name;
		var _parentCuid='',_parentName='';
		if(_parent){
			_parentCuid = _parent._id;
			_parentName = _parent._name;
    	}
		result = {cuid:dataCuid,labelCn:dataName,related_seg_cuid:ductSegCuid,parentCuid:_parentCuid,parentName:_parentName};
	}
	return result;
};
Dms.sectionPic.getHolePicBySegCuid = function(ductSegCuid,gv,basicFormPane){
	//图形化敷设时用到，初始化不传id不查询
	if(ductSegCuid){
		if(basicFormPane){
			tp.utils.lock(basicFormPane);
		}
		var topoUrl = ctx+"/topo/index.do?code=DuctSectionTopo&resId="+ ductSegCuid +"&resType=DUCT_SEG&clientType=html5&result=json";
		$.ajax({
				url : topoUrl,
				success : function(data){
					gv.dm().clear();
					gv.dm().deserialize(data,null,true);
					if(basicFormPane){
						tp.utils.unlock(basicFormPane);
					}
				},
				error : function(data){
					if(basicFormPane){
						tp.utils.unlock(basicFormPane);
					}
				},
				type : 'POST',
				dataType : 'json'
		});
	}
};