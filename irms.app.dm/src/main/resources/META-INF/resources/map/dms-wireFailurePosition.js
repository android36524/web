/**
 * 
 */
$importjs(ctx+'/dwr/interface/WireInterruptAction.js');

(function(){
	"use strict";
	//选择一个光缆系统
	dms.wireFailure.createSelectWirePane = function(cuid){
		tp.Default.DrawObject._drawState = 0;
		var c = createWireInterRuptPane(cuid);
		var panel = new ht.widget.Panel(
				{
					title : "光缆故障定位",
					width : 600,
					exclusive : false,
					titleColor : "yellow",
					minimizable : true,
					minimize : false,//控制打开时界面是不是最小化
					expand : true,
					narrowWhenCollapse : true,
					contentHeight : 310,
					buttons:['minimize',{
						name : '关闭',
						toolTip:'关闭',
						icon:'close.png',
						action:function(){
							document.body.removeChild(panel.getView());
							clearDmsWireFailUnkown();
						}
					}],
					content : c
				});

		panel.setPosition(200, 10);
		document.body.appendChild(panel.getView());
	};
	
	function createWireInterRuptPane(cuid){
		var interruptPane = new ht.widget.TabView();
		var wireSystemPane = createWireSystemPanel(cuid);
		var conditionPanel = createWFQueryConditionPane(cuid),
		remainPanel = createInterRuptAndRemainPane();
		interruptPane.add('选择光缆系统',wireSystemPane);
		interruptPane.add('输入查询条件',conditionPanel);
		interruptPane.add('查看断点信息',remainPanel);
		interruptPane.select(0);
		dms.wireFailure.interrupt = interruptPane;
		return interruptPane;
	};
	
	function createWireSystemPanel(cuid){
		var formPane = new ht.widget.FormPane(),
		dataModel = new ht.DataModel(),                       
	    tablePane = new ht.widget.TablePane(dataModel);
		var queryBtn = tp.utils.createButton('',"选择光缆系统");
		tp.utils.lock(formPane);
		queryBtn.onclick = function(e){
			var ld = dataModel.sm().ld();
			if(!ld){
				tp.utils.optionDialog("错误提示", "没有选择光缆系统!");
				return;
			}
			var wireSystemCuid = ld.getAttr("CUID");
			dms.wireFailure.wsCuid = wireSystemCuid;
			WireInterruptAction.getSystemPointsByWireSystemCuid(wireSystemCuid,function(datas){
				if(datas){
					var combox = dms.wireFailure.pointCombox;
					var cuids = new Array(),
					names = new Array();
					for(var i=0;i<datas.length;i++){
						var data = datas[i];
						var origPointCuid = data.ORIG_POINT_CUID,
						origPointName = data.ORIG_POINT_NAME,
						destPointCuid = data.DEST_POINT_CUID,
						destPointName = data.DEST_POINT_NAME;
						cuids.push(origPointCuid);
						cuids.push(destPointCuid);
						names.push(origPointName);
						names.push(destPointName);
					}
					if(cuids && names){
						var newCuids = tp.utils.unique(cuids);
						combox.setValues(newCuids);
						var newNames = tp.utils.unique(names);
						combox.setLabels(newNames);
						combox.setValue(newCuids[0]);
					}
					dms.wireFailure.interrupt.select(1);
				}
			});
		};
		addWireSystemColumn(tablePane);
		formPane.addRow([tablePane],[0.1],230);
		formPane.addRow([null,queryBtn],[0.1,100],24);
		
		WireInterruptAction.getWireSystemByCuid(cuid,function(datas){
			if(datas){
				for(var i=0;i<datas.length;i++){
					var data = datas[i];
					var cuid = data.CUID,
					labelCn = data.LABEL_CN;
					var node = new ht.Node();
					node.setId(cuid);
					node.setName(labelCn);
					node.a("CUID",cuid);
					node.a("LABEL_CN",labelCn);
					node.a("nid",i+1);
					node.a("WIRESYSTEMNAME",labelCn);
					dataModel.add(node);
				}
				dms.wireFailure.dm = dataModel;
			}
			tp.utils.unlock(formPane);
		});
//		formPane.getItemBorderColor = function(){return 'gray';};
		return formPane;
	};
	
	//输入查询条件并查询断点
	function createWFQueryConditionPane(){
		var queryPane = new ht.widget.FormPane(),
		fiberNo = tp.utils.createInput('','input'),
		measureDistance = tp.utils.createInput('','input'),
		measureError = tp.utils.createInput('','input'),
		queryBtn = tp.utils.createButton('',"查询断点");
		fiberNo.value=0;
		measureDistance.value=100;
		measureError.value = "15.0";
		
		var measurePointCombox = new ht.widget.ComboBox();
		measurePointCombox.getView().style.border = 'solid 1px black';
		measurePointCombox.setHeight(22);
		measurePointCombox.setValue("");
		dms.wireFailure.pointCombox = measurePointCombox;
		var measureWireCombox = new ht.widget.ComboBox();
		measureWireCombox.getView().style.border = 'solid 1px black';
		measureWireCombox.setHeight(22);
		measureWireCombox.setValue("");
		
		queryPane.addRow(["测量点",measurePointCombox],[0.1,0.5],25);
		queryPane.addRow(["测量光缆段",measureWireCombox],[0.1,0.5],25);
		queryPane.addRow(["纤芯编号",fiberNo],[0.1,0.5],25);
		queryPane.addRow(["测量距离",measureDistance],[0.1,0.5],25);
		queryPane.addRow(["误差",measureError],[0.1,0.5],25);
		queryPane.addRow([null],[0.1],25);
		queryPane.addRow([null],[0.1],25);
		queryPane.addRow([null],[0.1],25);
		
		queryPane.addRow([null,queryBtn],[0.1,80],25);

		measurePointCombox.onValueChanged = function(){
			if(measurePointCombox._value==""){
				return;
			}
			var pointCuid = measurePointCombox._value;
			processComboBoxChange(measureWireCombox,pointCuid);
		};
		
		queryBtn.onclick = function(e){
			Dms.Default.tpmap.reset();
			var wireSegCuid = measureWireCombox._value,
			wireNo = fiberNo.value,
			interruptDistance = measureDistance.value,
			wirePointCuid = measurePointCombox._value;
			
			if(!wireSegCuid || !wireNo || !interruptDistance || !wirePointCuid){
				tp.utils.optionDialog("错误提示", "条件不能为空!");
				return;
			}
			WireInterruptAction.getWireInterruptPoint(wireSegCuid,wireNo,interruptDistance,wirePointCuid,function(datas){
				if(datas){
					var mapNode = new ht.Node();
					var lat="",lng="";
					for(var i=0;i<datas.length;i++){
						var data = datas[i];
						if(data.ERROR_INFO){
							var error = data.ERROR_INFO[0];
							tp.utils.optionDialog("温馨提示", error);
							return;
						}
						var origPointName=data.ORIG_POINT_CUID,
						disOrigInter = data.ORIG_POINT_POSITION,
						destPointName = data.DEST_POINT_CUID,
						disDestInter = data.DEST_POINT_POSITION,
						longitude = data.LONGITUDE,
						latitude =data.LATITUDE,
						remainOneName =data.ROTE_ORIG_POINT_NAME,
						remainOneDistance =data.ROTE_ORIG_POINT_LONG,
						disOneToInterrupt =data.ORIG_POINT_LONGTH,
						remainTwoName = data.ROTE_DEST_POINT_NAME,
						remainTwoDistance = data.ROTE_DEST_POINT_LONG,
						disTwoToInterrupt =data.DEST_POINT_LONGTH;
						lat = latitude;
						lng = longitude;
						var node = dms.wireFailure.interruptDm.getDataById("INTERRUPT_POINT");//new ht.Node();
						node.a("origPointName",origPointName);
						node.a("disOrigInter",disOrigInter);
						node.a("destPointName",destPointName);
						node.a("disDestInter",disDestInter);
						node.a("longitude",longitude);
						node.a("latitude",latitude);
						node.a("remainOneName",remainOneName);
						node.a("remainOneDistance",remainOneDistance);
						node.a("disOneToInterrupt",disOneToInterrupt);
						node.a("remainTwoName",remainTwoName);
						node.a("remainTwoDistance",remainTwoDistance);
						node.a("disTwoToInterrupt",disTwoToInterrupt);
					}
					dms.wireFailure.interrupt.select(2);
					if(lat && lng){
						var point = Dms.Default.tpmap.getMap().latLngToContainerPoint(L.latLng(lat,lng));
						mapNode.setPosition(point);
						//mapNode.setStyle('shape', 'circle');
						//mapNode.setStyle('shape.border.width', 1);
						//mapNode.setStyle('shape.border.color', 'yellow');
						mapNode.setImage(ctx + '/map/close.png');
						//需要设置latlng，不然移动时restposition会把坐标清掉
						var latLng = new L.LatLng(lat,lng);                            
						mapNode.a('latLng',latLng);
						mapNode.setSize(15,15);
		                
						var mGv = Dms.Default.tpmap.getGraphView();
						mGv.dm().add(mapNode);
						mapNode.a('latLng',latLng);
						//加上点后不可移动
						mGv.dm().sm().setFilterFunc(function(data){
							if(data == mapNode)
								return false;
							return true;
						});
					}
				}
			});
		};
		return queryPane;
	};
	
	//断点信息、预留信息
	function createInterRuptAndRemainPane(){
		var formPanel = new ht.widget.FormPane();
		var dataModel = new ht.DataModel(),
		propertyView = new ht.widget.PropertyView(dataModel);
		var propertyModel = propertyView.getPropertyModel();
		var data = getInterRuptHtData();
		dataModel.add(data);
		addProperty('origPointName','设备1名称',"断点信息",propertyModel);
		addProperty('disOrigInter','距离断点位置',"断点信息",propertyModel);
		addProperty('destPointName','设备2名称',"断点信息",propertyModel);
		addProperty('disDestInter','距离断点位置',"断点信息",propertyModel);
		addProperty('longitude','经度',"断点信息",propertyModel);
		addProperty('latitude','纬度',"断点信息",propertyModel);
		addProperty('remainOneName','预留1名称',"预留信息",propertyModel);
		addProperty('remainOneDistance','预留1长度',"预留信息",propertyModel);
		addProperty('disOneToInterrupt','距离断点长度',"预留信息",propertyModel);
		addProperty('remainTwoName','预留2名称',"预留信息",propertyModel);
		addProperty('remainTwoDistance','预留2长度',"预留信息",propertyModel);
		addProperty('disTwoToInterrupt','距离断点长度',"预留信息",propertyModel);
		
		formPanel.addRow([propertyView],[0.1],220);
		formPanel.addRow([null],[0.1],10);
//		var smsBtn = tp.utils.createButton('',"发送短信");
//		formPanel.addRow([null,smsBtn],[0.1,80],22);
		dataModel.sm().ss(data);
		propertyView.collapse("预留信息");
		dms.wireFailure.interruptDm = dataModel;
		return formPanel;
	};
	
	function addWireSystemColumn(tablePane){
		var cm = tablePane.getColumnModel();
	    var column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('nid');
	    column.setWidth(50);
	    column.setDisplayName('序号');
	    cm.add(column);
	    
	    column = new ht.Column();
	    column.setAccessType('attr');
	    column.setName('WIRESYSTEMNAME');
	    column.setWidth(600);
	    column.setDisplayName('光缆系统');
	    cm.add(column);
	}
	
	function processComboBoxChange(measureWireCombox,pointCuid){
		var systemCuid = dms.wireFailure.wsCuid;
		WireInterruptAction.getWireSegsBySystemAndPoint(systemCuid,pointCuid,function(datas){
			if(datas){
				var cuids = new Array(),
				names = new Array();
				for(var i=0;i<datas.length;i++){
					var data = datas[i];
					var cuid = data.CUID,
					labelCn = data.LABEL_CN;
					
					cuids.push(cuid);
					names.push(labelCn);
				}
				if(cuids && names){
					var newCuids = tp.utils.unique(cuids);
					measureWireCombox.setValues(newCuids);
					var newNames = tp.utils.unique(names);
					measureWireCombox.setLabels(newNames);
					measureWireCombox.setValue(newCuids[0]);
				}
			}
		});
	};

	function addProperty(name, displayName,categoryName,propertyModel) {
	    var property = new ht.Property();
	    property.setName(name);
	    property.setDisplayName(displayName);
	    property.setAccessType('attr');
	    property.setCategoryName(categoryName);
	    
	    if(name.indexOf("LONGITUDE") > -1 || name.indexOf("LATITUDE") > -1)
		{	property.formatValue = function(value)
			{
				if(value)
					return new Number(value).toFixed(6);
				else
					return "";
			};
		}
	    propertyModel.add(property);
	};

	function getInterRuptHtData(){
		var data = new ht.Data();
		data.setId("INTERRUPT_POINT");
	    return data;
	};
	
	function clearDmsWireFailUnkown(){
		dms.wireFailure.dm = {};
		dms.wireFailure.pointCombox = {};
		dms.wireFailure.interruptDm = {};
		dms.wireFailure.interrupt = null;
		dms.wireFailure.wsCuid = null;
		Dms.Default.tpmap.reset();
	};
	
})();