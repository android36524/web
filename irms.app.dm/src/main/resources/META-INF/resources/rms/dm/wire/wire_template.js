$importjs(ctx+'/dwr/interface/GetServiceParamAction.js');
$importjs(ctx+'/jslib/jquery/jquery-1.7.1.min.js');

//打开光缆模板
function openWireTemplate(grid)
{
	var records = grid.getSelectionModel().getSelectedNodes();
	var resId = records[0].attributes.CUID;
	var dm = new ht.DataModel();
	var gv = new ht.graph.GraphView(dm);
	gv.setEditable(false);
	gv.isMovable = function(data)
	{
		return false;
	};
	var topoUrl = ctx+"/topo/index.do?code=TemplateSectionTopo&resId=7&resType=TEMPLATE_TYPE&clientType=html5&result=json";
	$.ajax({
		url : topoUrl,
		success : function(data){
			dm.clear();
			dm.deserialize(data,null,true);
			},error :function(e){alert(e);} 
		});
	var treeView = new ht.widget.TreeView(dm);
	treeView.isVisible = function(data)
	{
		if(data instanceof ht.Node)
			return false;
		return true;
	};
	treeView.onDataClicked = function(data)
	{
		var cuid = data.a("cuid");
		var bmClassId = data.a("bmClassId");
		if(bmClassId == 'TEMPLATE')
		{
			//清空原有的模板
			var removeList = [];
			dm.each(function(data){
				if(data instanceof ht.Node)
					removeList.push(data);
			});
			for(var i=0;i<removeList.length;i++)
				dm.remove(removeList[i]);
			
			$.ajax({
				url : ctx+'/rest/TemplateSerivce/getTemplateJson/'+cuid+'?time='+new Date().getTime(),
				type : 'post',
				dataType : 'json',
				success : function(result) {
					if(result && result.error)
					{
						alert(result.error);
					}else if(result)
					{
						dm.deserialize(result,null,true);
						data.a("JSON",result);
					}else
					{
						Ext.Msg.alert('温馨提示', '未找到模板，请先新增模板！');
					}
				}
			});
		}
	};
	var splitView = new ht.widget.SplitView(treeView,gv,'h', 0.3);
	
	var dialog = new ht.widget.Dialog({
        title: "<html><font size=2>选择模板</font></html>",
        width: 800,
        height: 400,
        titleAlign: "left", 
        draggable: true,
        closable: true,
        buttons: [
              {
                  label: "确定"
              },
              {
                  label: "取消"
              }
          ],
        content: splitView.getView(),
        action: function(button, e) {
        	if (button.label === "确定") {
        		var data = dm.sm().ld();
        		if(data == null)
        		{
        			Ext.Msg.alert("信息","请选择模板！");
        			return;
        		}else
        		{
        			var bmClassId = data.a("bmClassId");
            		if(bmClassId === 'TEMPLATE' )
            		{
            			try{
//    	        			var json = data.a("JSON");
            				var json = data._dataModel.serialize();
    	        			console.info(json);
    	        			$.ajax({
    	        				url : ctx+'/rest/WireSegService/addWireSegFibers?time='+new Date().getTime(),
    	        				type : 'post',
    	        				data : {
    	        					wireSegCuid:resId,
    	        					json : json
    	        				},
    	        				success : function(data) {
    	        					if(data.error)
    	        					{
    	        						Ext.Msg.alert("error",data.error);
    	        					}else
    	        					{
    	        						Ext.Msg.alert('温馨提示', '生成纤芯成功！');
    	        						grid.reloadNode(null);
//    	        						graphView.loadTopoData(graphView.topoUrl);
    	        					}
    	        				}
    	        			});
    	        			dialog.hide();
    	        			Ext.Msg.alert('温馨提示', '模板选择成功！');
            			}catch(e)
    					{
            				Ext.Msg.alert('温馨提示', '增加纤芯失败！');
    					}
            		}
            		else
            		{
            			Ext.Msg.alert('温馨提示', "请选择模板！");
    					return;
            		}
        		}
        	}
        	dialog.hide();
        }
	});
	dialog.onTransitionEnd = function(operation) {
	    if (operation === "show") {
	    } else {
	        dialog.dispose();
	    }
	};
	dialog.show();
}
