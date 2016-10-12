/**
 * 通达站点光缆分析
 */

$importjs(ctx+'/dwr/interface/SiteWireAnalysisAction.js');
dms.sitewireanalysis = {};
dms.sitewireanalysis.getSiteWireAnalysis = function(cuid){
	var c = dms.sitewireanalysis.createSiteWirePanel(cuid);
	tp.Default.DrawObject._drawState = 0;
	var panel = new ht.widget.Panel(
			{
				title : "通达站点光缆分析",
				width : 500,
				exclusive : false,
				titleColor : "yellow",
				minimizable : true,
				minimize : false,//控制打开时界面是不是最小化
				expand : true,
				narrowWhenCollapse : true,
				contentHeight : 230,
				buttons:['minimize',{
					name : '关闭',
					toolTip:'关闭',
					icon:'close.png',
					action:function(){
						document.body.removeChild(panel.getView());
						clearSiteWireUnknown();
					}
				}],
				content : c
			});

	panel.setPosition(200, 10);
	document.body.appendChild(panel.getView());

};

dms.sitewireanalysis.createSiteWirePanel = function(cuid){
	var swPanel = new ht.widget.TabView();
	var selectSitePanel = dms.sitewireanalysis.createSelectSitePanel(cuid);
	var wireSegPanel = dms.sitewireanalysis.createWireSegPane();
	
	swPanel.add('光缆段',selectSitePanel);
	swPanel.add('光缆段路由',wireSegPanel);
	swPanel.select(0);
	dms.sitewireanalysis.spanel = swPanel;
	return swPanel;
};

dms.sitewireanalysis.createWireSegPane = function(){
	var wireSegFormPane = new ht.widget.FormPane(),
	wireSegDataModel = new ht.DataModel(),
	wireSegTablePane = new ht.widget.TablePane(wireSegDataModel);
	addWireSegColumn(wireSegTablePane);
	wireSegFormPane.addRow([wireSegTablePane],[0.1],1);
	dms.sitewireanalysis.wiresegdm = wireSegDataModel;
	return wireSegFormPane;
};
dms.sitewireanalysis.createSelectSitePanel = function(cuid){

	var formPane = new ht.widget.FormPane(),
	dataModel = new ht.DataModel(),                       
    tablePane = new ht.widget.TablePane(dataModel);
	tp.utils.lock(formPane);
	var queryBtn = tp.utils.createButton('',"查看光缆段路由信息");
	
	queryBtn.onclick = function(e){
		var ld = dataModel.sm().ld();
		if(!ld){
			tp.utils.optionDialog("错误提示", "没有选择光缆段数据!");
			return;
		}
		dms.sitewireanalysis.wiresegdm.clear();
		var cid = ld.getAttr("CID");
		dms.sitewireanalysis.spanel.select(1);
		var wireList = dms.sitewireanalysis.wireList;
		var count = 1;
		for(var i=0;i<wireList.length;i++){
			var data = wireList[i];
			var did = data.CID;
			if(did == cid){
				var wireSegCuid = data.WIRE_SEG_CUID,
				wireSegName = data.WIRE_SEG_LABEL_CN,
				origPointCuid = data.ORIG_POINT_CUID,
				origPointName = data.ORIG_POINT_NAME,
				destPointCuid = data.DEST_POINT_CUID,
				destPointName = data.DEST_POINT_NAME,
				fiberCount = data.FIBER_COUNT,
				wireSystemName = data.SYSTEM_LABEL_CN;
				
				var node = new ht.Node();
				node.a("NID",count);
				node.a("CID",did);
				node.a("WIRE_SEG_CUID",wireSegCuid);
				node.a("WIRE_SEG_NAME",wireSegName);
				node.a("ORIG_POINT_CUID",origPointCuid);
				node.a("ORIG_POINT_NAME",origPointName);
				node.a("DEST_POINT_CUID",destPointCuid);
				node.a("DEST_POINT_NAME",destPointName);
				node.a("FIBER_COUNT",fiberCount);
				node.a("WIRE_SYSTEM_NAME",wireSystemName);
				dms.sitewireanalysis.wiresegdm.add(node);
				count++;
			}
		}
	};
	var exportBtn = tp.utils.createButton('',"导出");
	exportBtn.onclick = function(e){
		
	};
	addSiteWireColumn(tablePane);
	formPane.addRow([tablePane],[0.1],160);
	formPane.addRow([null,queryBtn],[0.1,150],24);
	
	SiteWireAnalysisAction.getSiteWireAnalysis(cuid,function(datas){
		if(datas){
			var siteWireList = datas.siteWirelist;
			dms.sitewireanalysis.wireList = datas.wirelist;
			for(var i=0;i<siteWireList.length;i++){
				var data = siteWireList[i];
				var origSite = data.ORIG_SITE,
				origSiteLongi = data.ORIG_LONGITUDE,
				origSiteLati = data.ORIG_LATITUDE,
				destSite = data.DEST_SITE,
				destSiteLongi = data.DEST_LONGITUDE,
				destSiteLati = data.DEST_LATITUDE,
				routeCount = data.COUNT,
				mark = data.MARK;
				
				var node = new ht.Node();
				node.a("NID",i+1);
				node.a("CID",mark);
				node.a("ORIG_POINT_SITE",origSite);
				node.a("ORIG_POINT_LONGITUDE",origSiteLongi);
				node.a("ORIG_POINT_LATITUDE",origSiteLati);
				node.a("DEST_POINT_SITE",destSite);
				node.a("DEST_POINT_LONGITUDE",destSiteLongi);
				node.a("DEST_POINT_LATITUDE",destSiteLati);
				node.a("ROUTE_COUNT",routeCount);
				dataModel.add(node);
				
				addMapSiteWire(origSiteLati,origSiteLongi,destSiteLati,destSiteLongi);
			}
		}
		tp.utils.unlock(formPane);
	});
//	formPane.getItemBorderColor = function(){return 'gray';};
	return formPane;
};

function addMapSiteWire(origSiteLati,origSiteLongi,destSiteLati,destSiteLongi){
	var origNode = new ht.Node(),
	destNode = new ht.Node(),
	origLatlng = L.latLng(origSiteLati,origSiteLongi),
	destLatlng = L.latLng(destSiteLati,destSiteLongi);
	
	var origPoint = Dms.Default.tpmap.getMap().latLngToContainerPoint(L.latLng(origSiteLati,origSiteLongi)),
	destPoint = Dms.Default.tpmap.getMap().latLngToContainerPoint(L.latLng(destSiteLati,destSiteLongi));
	
	origNode.setPosition(origPoint);
	origNode.setImage("SITE");
	origNode.a("latLng",origLatlng);
	destNode.a("latLng",destLatlng);
	destNode.setPosition(destPoint);
	destNode.setImage("SITE");
	Dms.Default.tpmap.getGraphView().dm().add(origNode);
	Dms.Default.tpmap.getGraphView().dm().add(destNode);
	var edge = new ht.Edge();
	edge.setStyle('edge.color', "green");
	edge.setStyle('edge.width', 3);
	edge.setStyle('edge.center', true);
	edge.setStyle('edge.pattern',[3,3]);
	edge.setSource(origNode);
	edge.setTarget(destNode);
	Dms.Default.tpmap.getGraphView().dm().add(edge);
};

function addWireSegColumn(tablePane){
	var cm = tablePane.getColumnModel();
    var column = new ht.Column();
    column.setAccessType('attr');
    column.setName('NID');
    column.setWidth(50);
    column.setDisplayName('序号');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('WIRE_SEG_NAME');
    column.setWidth(200);
    column.setDisplayName('光缆段名称');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('ORIG_POINT_NAME');
    column.setWidth(150);
    column.setDisplayName('起点名称');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('DEST_POINT_NAME');
    column.setWidth(150);
    column.setDisplayName('终点名称');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('FIBER_COUNT');
    column.setWidth(50);
    column.setDisplayName('纤芯数');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('WIRE_SYSTEM_NAME');
    column.setWidth(200);
    column.setDisplayName('光缆系统名称');
    cm.add(column);
};

function addSiteWireColumn(tablePane){
	var cm = tablePane.getColumnModel();
    var column = new ht.Column();
    column.setAccessType('attr');
    column.setName('NID');
    column.setWidth(50);
    column.setDisplayName('序号');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('ORIG_POINT_SITE');
    column.setWidth(150);
    column.setDisplayName('起点站点');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('DEST_POINT_SITE');
    column.setWidth(150);
    column.setDisplayName('通达站点');
    cm.add(column);
    
    column = new ht.Column();
    column.setAccessType('attr');
    column.setName('ROUTE_COUNT');
    column.setWidth(50);
    column.setDisplayName('路由段');
    cm.add(column);
};

function clearSiteWireUnknown(){
	dms.sitewireanalysis.wiresegdm = {};
	dms.sitewireanalysis.spanel = null;
	Dms.Default.tpmap.reset();
};
