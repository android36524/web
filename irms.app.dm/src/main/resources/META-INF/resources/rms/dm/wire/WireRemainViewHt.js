//Ext.ns('Frame.wire');
$importjs(ctx+'/dwr/interface/WireRemainAction.js');
$importjs(ctx+'/rms/dm/common/dm-base.js');
//光缆（段）
ht.Default.setImage('/resources/map/WIRE_SEG.png',ctx+'/resources/map/WIRE_SEG.png');

createWireRemainPanel = function(param){
	var aDataModel = new ht.DataModel();
	var zDataModel = new ht.DataModel();
    var basicFormPane = new ht.widget.FormPane(); 
    
    var topFormPane = new ht.widget.FormPane();
    var leftTreeView = new ht.widget.TreeView(aDataModel);

    
    var leftFormPane = new ht.widget.FormPane();
    
    var rightTable = new ht.widget.TablePane(zDataModel);
    var rightFormPane = new ht.widget.FormPane();
    var _data = [];
    leftTreeView.onDataClicked= function (data) {
    	tp.utils.lock(basicFormPane);
    	loadRemainData(rightTable,_data,data,basicFormPane);
    };
    
    //增加从服务端取值
    topFormPane.getItemBorderColor = function(){return 'red';};
    topFormPane.getRowBorderColor = function(){return 'yellow';};
    basicFormPane.addRow([topFormPane],[0.1],[0.1]);
    var addBtn = document.createElement('button');
    addBtn.innerHTML = "保存";
    addBtn.style.position = 'absolute';
    addBtn.onclick=function(e){
    	save(basicFormPane,_data,zDataModel);
    };
    basicFormPane.addRow([null,addBtn],[0.1,80],24);
    
    topFormPane.addRow([leftFormPane, rightFormPane],[0.1,0.1], 0.1);   
    leftFormPane.addRow([leftTreeView], [0.1], 0.1);
    
    rightFormPane.addRow([rightTable], [0.1], 0.1);
    addColumns(rightTable);
    loadWireSegs(leftTreeView,param,basicFormPane);
    
    function addColumns(table,code) {
    	if(code){
    		className=code.split("-")[0];
    	}
        var cm = table.getColumnModel();
       
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('CUID');
        column.setWidth(100);
        column.setDisplayName('CUID');
        column.setVisible(false);
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('OBJECTID');
        column.setWidth(100);
        column.setDisplayName('OBJECTID');
        column.setVisible(false);
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('OWNERSHIP');
        column.setWidth(100);
        column.setDisplayName('OWNERSHIP');
        column.setVisible(false);
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('RELATED_DISTRICT_CUID');
        column.setWidth(100);
        column.setDisplayName('RELATED_DISTRICT_CUID');
        column.setVisible(false);
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('LABEL_CN');
        column.setWidth(150);
        column.setDisplayName('点设施');
        cm.add(column);
        
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('LENGTH');
        column.setWidth(100);
        column.setDisplayName('长度(M)');
        column.setEditable(true);
        cm.add(column);
        
    };

    function loadWireSegs (tree,wireSystem,basicFormPane){
    	WireRemainAction.getWireSegs(wireSystem.CUID,function(data){
    		tp.utils.lock(basicFormPane);
    		var parent = new ht.Node(wireSystem.CUID);
    		parent.setName(wireSystem.LABEL_CN);
    		parent.setIcon("/resources/map/WIRE_SEG.png");
    		tree.dm().add(parent);
    		if(data && data.length > 0)
    		{
    			for (var i = 0; i < data.length; i++) {
    				var child = data[i];
    				var htdata = tree.dm().getDataById(child.CUID);
    				if(htdata == null)
    				{	
    					htdata = new ht.Node(child.CUID);
    					htdata.setId(child.CUID);
    					htdata.setAttr("cuid",child.CUID);
    					htdata.setName(child.LABEL_CN);
    					htdata.setIcon("/resources/map/WIRE_SEG.png");
    					parent.addChild(htdata);					
    					tree.dm().add(htdata);
    				}
    			}
    			parent.setAttr("expanded",true);
    			tree.expandAll();

    		}
    		tp.utils.unlock(basicFormPane);
    	});
    };

    function loadRemainData(table,olddata,data){
    	olddata.splice(0);
    	if (!data) {
    		return	;
    	}
    	WireRemainAction.getRemainInfo(data._id,function(data){
    		var dm = table.getTableView().dm();
    		if(dm)
    			dm.clear();
    		if(data && data.length > 0)
    		{
    			for (var i = 0; i < data.length; i++) {
    				var point = data[i];
    				var htdata = dm.getDataById(point.CUID);
    				if(htdata == null)
    				{	
    					htdata = new ht.Node(point.CUID);
    					htdata.setId(point.CUID);
    					htdata.a("CUID",point.CUID);
    					htdata.a("LABEL_CN",point.LABEL_CN);
    					htdata.a("OWNERSHIP",point.OWNERSHIP);
    					htdata.a("IS_REMAIN",point.IS_REMAIN);
    					htdata.a("RELATED_DISTRICT_CUID",point.RELATED_DISTRICT_CUID);
    					htdata.a("RELATED_WIRE_SEG_CUID",point.RELATED_WIRE_SEG_CUID);
    					htdata.setName(point.LABEL_CN);
    					htdata.a("LENGTH",point.LENGTH);
    					htdata.a("REMAIN_INFO",point.REMAIN_INFO);
    					dm.add(htdata);
    					if(point.IS_REMAIN == true){
    						olddata.push({"CUID":point.CUID,"IS_REMAIN":point.IS_REMAIN,"LABEL_CN":point.LABEL_CN,
    							"LENGTH":point.LENGTH,"OWNERSHIP":point.OWNERSHIP,
    							"RELATED_DISTRICT_CUID":point.RELATED_DISTRICT_CUID,"REMAIN_INFO":point.REMAIN_INFO});
    					}
    				}
    			}

    		}
    		tp.utils.unlock(basicFormPane);
    	});
    };

    function validate(tableModel){
    	var datas = tableModel.getDatas(),
    	length = datas.size();
    	if(length >0){
    		for(var i = 0;i < length;i++){
    			var data =datas.get(i)._attrObject;
    			var strLength = data.LENGTH.trim();
    			if(strLength != "" && !/^[\d\.]+$/.test(strLength)){
    				return false;
    			}
	    	}
	
	    }
    	return true;
    };
    function save(panel,oldData,tableModel){
    	if(!validate(tableModel)){
    		tp.utils.optionDialog('温馨提示','预留长度中存在非法字符!');
    		return ;
    	}
		tp.utils.lock(panel);
    	var newData =[];
    	if(tableModel._datas.size() >0){
    		for(var i = 0;i < tableModel._datas.size();i++){
    			var data = tableModel._datas.get(i)._attrObject;
    			if(data.LENGTH.trim() != ""){
	    			newData.push({"CUID":data.CUID,"LABEL_CN":data.LABEL_CN,"LENGTH":data.LENGTH,
	    				"OWNERSHIP":data.OWNERSHIP,"RELATED_DISTRICT_CUID":data.RELATED_DISTRICT_CUID,"RELATED_WIRE_SEG_CUID":data.RELATED_WIRE_SEG_CUID});
	    			}
    			}
    		WireRemainAction.saveWireRemain(oldData,newData, function(data){
    			tp.utils.optionDialog('温馨提示','光缆预留信息保存成功!');
    	   		tp.utils.unlock(panel);
    		});
    	}
    };
    return basicFormPane;
};

