
$importjs(ctx+'/rms/dm/common/dm-base.js');
//光缆（段）
ht.Default.setImage('/map/WIRE_SEG.png',ctx+'/map/WIRE_SEG.png');
$importjs(ctx+'/dwr/interface/ReviewWireViewAction.js');
createLayingWireListPanelHT = function(param){
	var treeDataModel = new ht.DataModel();
    var basicFormPane = new ht.widget.FormPane(); 
    var treeTablePanel = new ht.widget.TreeTablePane(treeDataModel);
    treeTablePanel.getTableView().getTreeColumn().setDisplayName('承载对象');

    basicFormPane.addRow([treeTablePanel],[0.1], 0.1);   
    addColumns(treeTablePanel,param.CUID);
    loadLayingWires(treeTablePanel,param);
    
    function addColumns(table,code) {
    	if(code){
    		className=code.split("-")[0];
    	}
        var cm = table.getColumnModel();
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('LINE_SYSTEM_NAME');
        column.setWidth(200);
        column.setDisplayName('光缆名称');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('WIRE_SYSTEM_LEVEL');
        column.setWidth(80);
        column.setDisplayName('光缆级别');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('WIRE_SEG_ORIG_POINT');
        column.setWidth(150);
        column.setDisplayName('光缆段起点');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('WIRE_SEG_DEST_POINT');
        column.setWidth(150);
        column.setDisplayName('光缆段终点');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('FIBER_COUNT');
        column.setWidth(50);
        column.setDisplayName('光缆段芯数');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('LAY');
        column.setWidth(80);
        column.setDisplayName('敷设对象');
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('DIS_POINT_NAME');
        column.setWidth(200);
        if(className=="DUCT_SYSTEM" || className == "DUCT_BRANCH" || className == "DUCT_SEG"){
        	column.setDisplayName('管道起点');
        }else if(className=="POLEWAY_SYSTEM" || className == "POLEWAY_BRANCH" || className == "POLEWAY_SEG"){
        	column.setDisplayName('杆路起点');
        }else{
        	column.setDisplayName('管线起点');
        }
        cm.add(column);
        
        column = new ht.Column();
        column.setAccessType('attr');
        column.setName('END_POINT_NAME');
        column.setWidth(200);
        if(className=="DUCT_SYSTEM" || className == "DUCT_BRANCH" || className == "DUCT_SEG"){
        	column.setDisplayName('管道终点');
        }else if(className=="POLEWAY_SYSTEM" || className == "POLEWAY_BRANCH" || className == "POLEWAY_SEG"){
        	column.setDisplayName('杆路终点');
        }else{
        	column.setDisplayName('管线终点');
        }
        cm.add(column);
        
        if(className=="DUCT_SYSTEM" || className == "DUCT_BRANCH" || className == "DUCT_SEG"){
	        column = new ht.Column();
	        column.setAccessType('attr');
	        column.setName('DUCT_HOLE_NO');
	        column.setWidth(50);
	        column.setDisplayName('管孔编号');
	        cm.add(column);
	        
	        column = new ht.Column();
	        column.setAccessType('attr');
	        column.setName('DUCT_CHILD_HOLE_NUM');
	        column.setWidth(50);
	        column.setDisplayName('子孔编号');
	        cm.add(column);
        }else if(className=="POLEWAY_SYSTEM" || className == "POLEWAY_BRANCH" || className == "POLEWAY_SEG"){
            column = new ht.Column();
            column.setAccessType('attr');
            column.setName('INNER_NO');
            column.setWidth(50);
            column.setDisplayName('吊线段');
            cm.add(column);
         }
    };

    function loadLayingWires (wireSystem){
    	try{
    		tp.utils.lock(basicFormPane);
    		ReviewWireViewAction.getRemainInfo(param.CUID,function(data){
    			addLayWireNode(data,treeDataModel);
    			treeTablePanel.getTableView().expandAll();
    			tp.utils.unlock(basicFormPane);
    		});
    	}catch(e){
    		tp.utils.unlock(basicFormPane);
    		tp.utils.optionDialog("错误提示", "查询经过的光缆出错!");
    	}
		
    };
    return basicFormPane;
};

function addLayWireNode(datas,dm){
	for(var i=0;i<datas.length;i++){
		var data =datas[i],
		lineSystemCuid = data.LINE_SYSTEM_CUID,
		lineSystemName = data.LINE_SYSTEM_NAME,
		lineBranchCuid = data.LINE_BRANCH_CUID,
		lineBranchName = data.LINE_BRANCH_NAME,
		lineSegName = data.LINE_SEG_NAME,
		disPointName = data.DIS_POINT_NAME,
		endPointName = data.END_POINT_NAME,
		wireSegOrigPoint = data.WIRE_SEG_ORIG_POINT,
		wireSegDestPoint = data.WIRE_SEG_DEST_POINT,
		ductHoleNo = data.DUCT_HOLE_NO,
		ductChildHoleNum = data.DUCT_CHILD_HOLD_NUM,
		wireSystemName = data.WIRE_SYSTEM_NAME,
		wireSystemLevel=data.WIRE_SYSTEM_LEVEL,
		innerNo=data.INNER_NO,
		lay = data.LAY,
		fiberCount = data.FIBER_COUNT;
		var lineSysNode = null,
		lineBranchNode = null,
		lineSegNode = null;
		
		var lineClassName = null;
		if(lineSystemCuid){
			lineClassName = lineSystemCuid.split("-")[0];
			lineSysNode = dm.getDataById(lineSystemCuid);
			if(!lineSysNode){
				lineSysNode = new ht.Node();
				lineSysNode.setId(lineSystemCuid);
				lineSysNode.setName(lineSystemName);
//				lineSysNode.a("LABEL_CN",lineSystemName);
				dm.add(lineSysNode);
			}
		}
		if(lineBranchCuid){
			lineBranchNode = dm.getDataById(lineBranchCuid);
			if(!lineBranchNode){
				lineBranchNode = new ht.Node();
				lineBranchNode.setId(lineBranchCuid);
				lineBranchNode.setName(lineBranchName);
//				lineBranchNode.a("LABEL_CN",lineBranchName);
				if(lineSysNode){
					lineBranchNode.setParent(lineSysNode);
				}
				dm.add(lineBranchNode);
			}
		}
		lineSegNode = new ht.Node();
		lineSegNode.setName(lineSegName);
		lineSegNode.a("DIS_POINT_NAME",disPointName);
		lineSegNode.a("END_POINT_NAME",endPointName);
		lineSegNode.a("LINE_SYSTEM_NAME",wireSystemName);
		lineSegNode.a("WIRE_SYSTEM_LEVEL",wireSystemLevel);
		lineSegNode.a("WIRE_SEG_ORIG_POINT",wireSegOrigPoint);
		lineSegNode.a("WIRE_SEG_DEST_POINT",wireSegDestPoint);
		lineSegNode.a("FIBER_COUNT",fiberCount);
		lineSegNode.a("INNER_NO",innerNo);
		lineSegNode.a("DUCT_HOLE_NO",ductHoleNo);
		lineSegNode.a("DUCT_CHILD_HOLE_NUM",ductChildHoleNum);
		lineSegNode.a("LAY",lay);
		if(lineClassName == "DUCT_SYSTEM" || lineClassName =="POLEWAY_SYSTEM" || lineClassName == "STONEWAY_SYSTEM"){
			if(lineBranchNode){
				lineSegNode.setParent(lineBranchNode);
			}
		}else{
			if(lineSysNode){
				lineSegNode.setParent(lineSysNode);
			}
		}
		dm.add(lineSegNode);
	}
};