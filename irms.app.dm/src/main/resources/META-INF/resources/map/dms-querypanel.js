tp.utils.createQueryConPanel = function(pageTable){
    	var basicFormPane = new ht.widget.FormPane();
    	
    	var queryNode = new ht.Node();
    	
    	var queryBtn = tp.utils.createButton('','查询');
    	queryBtn.onclick = function(e){
    		
    		queryNode.a('LABEL_CN',nameInput.value.trim());
    		queryNode.a('OWNERSHIP',ownershipCombo.getValue());
    		queryNode.a('SYSTEM_LEVEL',systemLevelCombo.getValue());
    		queryNode.a('MAINT_MODE',maintModeCombo.getValue());
    		queryNode.a('PROJECT',projectInput.value.trim());
    		queryNode.a('BUILDER',builderInput.value.trim());
    		
    		var queryParams = {};
    		var attrs = queryNode._attrObject;
    		for(var p in attrs){
    			tp.Default.PropertyQueryParam[p](queryParams, queryNode);
    		}
    		
    		var gridCfg = pageTable.getGridCfg();
    		var baseParams = {
    			count : true,
    			start : 0,
    			limit : 20,
    			totalNum : 20
    		};
    		pageTable.loadGridData(queryParams, baseParams);
		};        
    	var clearBtn = tp.utils.createButton('','清空');
    	clearBtn.onclick = function(e){
			pageTable.getTableView().dm().clear();
			nameInput.value = '';
			ownershipCombo.setValue(-1);
			systemLevelCombo.setValue(-1);
			maintModeCombo.setValue(-1);
			projectInput.value = '';
			builderInput.value = '';
		}; 
		
		var nameInput = tp.utils.createInput('','input','');

		var ownershipEnum = {values:[-1, 0, 1, 2, 3, 4, 5, 6, 7],labels:['全部', '未知', '自建', '共建', '合建', '附挂/附穿','租用', '购买', '置换']};
		var ownershipCombo = tp.utils.createComboBox(ownershipEnum);
		ownershipCombo.setValue(-1);

		var systemLevelEnum = {values:[-1, 0, 1, 2, 3, 4, 5],labels:['全部', '未知', '省际', '省内', '本地骨干', '本地汇聚','本地接入']};
		var systemLevelCombo = tp.utils.createComboBox(systemLevelEnum);
		systemLevelCombo.setValue(-1);

		var maintModeEnum = {values:[-1, 0, 1, 2],labels:['全部', '未知', '自维', '代维']};
		var maintModeCombo = tp.utils.createComboBox(maintModeEnum);
		maintModeCombo.setValue(-1);
		
		var projectInput = tp.utils.createInput('','input','');
		
		var builderInput = tp.utils.createInput('','input','');
		
		basicFormPane.addRow([{element: '名称', align: 'left'},nameInput,{element: '系统级别', align: 'left'},systemLevelCombo,
		{element:'工程名称',align:'left'},projectInput,null,queryBtn],[0.1,0.2,0.1,0.2,0.1,0.2,10,80]);
		
		basicFormPane.addRow([{element:'产权',align:'left'},ownershipCombo,{element:'维护方式',align:'left'},maintModeCombo,
		{element:'施工单位',align:'left'},builderInput,null,clearBtn],[0.1,0.2,0.1,0.2,0.1,0.2,10,80]);
		
        return basicFormPane;
    };
    
tp.utils.createQueryPanel = function (code, dialog,callBackInput,queryPanel){
	var dataModel = new ht.DataModel();
	var pageTable = new tp.widget.PageTable(dataModel);
	var propertyPane = new ht.widget.PropertyPane(dataModel);  
	propertyPane.setHeaderLabels(['属性', '值']);
	var propertyView = propertyPane.getPropertyView();

	var param = tp.getGridCfgByCode(code);
	var gridConfig = initParamsByUrl(param);
	pageTable.setGridCfg(gridConfig);
	
	GridViewAction.getGridMeta(gridConfig.gridCfg, function(data) {
		if (Ext.isEmpty(data)) {
			Ext.Msg.alert('系统异常', '无法获取表格定义，请检查表格配置！');
		}
		pageTable.setPropertyMeta(data);
		tp.utils.addProperties(propertyView, data);
	});
	//TODO 根据配置动态加载查询面板
	var basicFormPane = new (queryPanel ||  tp.utils.createQueryFBoxPanel)(pageTable);//tp.utils.createQueryConPanel(pageTable);	
        
        var toolbarPanel = new ht.widget.FormPane();
//        toolbarPanel.addRow([null,
//                             tp.utils.createButton('','增加'), tp.utils.createButton('','修改'), tp.utils.createButton('','删除'),
//                             null,
//                             tp.utils.createButton('','保存'), tp.utils.createButton('','取消')],
//        				[0.9,60, 60, 60,0.1,60,60]);
        var selectBtn = tp.utils.createButton('','确定');
        var cancleBtn = tp.utils.createButton('','取消');
        toolbarPanel.addRow([null,
                             selectBtn, cancleBtn],
        				[0.1,60,60]);
        selectBtn.onclick = function(){
//        	var data = pageTable.getTableView().dm().sm().getFirstData();
//        	data.getAttr('CUID');
//        	data.getAttr('LABEL_CN');
        	if(callBackInput){
            	var a =  borderPane.getResult();
            	callBackInput.value = a.labelCn;
            	callBackInput.id = a.cuid;
        	}
        	dialog.hide();
        };
        cancleBtn.onclick = function(){
        	dialog.hide();
        };
        
        var mainSplit = new ht.widget.SplitView(pageTable, propertyPane, 'horizontal', 0.7);  
        
        var borderPane = new ht.widget.BorderPane(); 
        borderPane.setTopView(basicFormPane);
        borderPane.setTopHeight(80);
        borderPane.setBottomView(toolbarPanel);
        borderPane.setBottomHeight(40);
        borderPane.setCenterView(mainSplit);
        borderPane.getResult = function(){
        	var datas = pageTable.getTableView().dm().sm().getSelection();
        	var cuids = "";
        	var labelCns = "";
        	var result = {cuid:'',labelCn:''};
        	for(var i=0;i< datas.size();i++){
        		var data = datas.get(i);
        		if(Ext.isEmpty(cuids)){
        			cuids = data.getAttr('CUID');
        		}else{
        			cuids =cuids+","+data.getAttr('CUID');
        		}
        		
        		if(Ext.isEmpty(labelCns)){
        			labelCns = data.getAttr('LABEL_CN');
        		}else{
        			labelCns =labelCns+","+data.getAttr('LABEL_CN');
        		}
        	}
        	result = {cuid:cuids,labelCn:labelCns};
        	return result;
        };
        return 	borderPane;
    };
    
	
tp.utils.createQueryDialog = function(code, callback){
		var dialog = new ht.widget.Dialog(); 
		var c = tp.utils.createQueryPanel(code, dialog);
		dialog.setConfig({
            title: "<html><font size=3>"+"查询</font></html>",
            width: 800,
            height: 400,
            titleAlign: "left", 
            draggable: true,
            closable: true,
            content: c
        });
	    dialog.onShown = function(operation) {
	        
	    };
	    dialog.onHidden = function(operation) {
	    	callback(c.getResult());
	    };
	    dialog.show();
		return c;
	};

