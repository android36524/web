$importjs(ctx+'/dwr/interface/GenerateCuidAction.js');

//属性组件
(function(window,Object,undefined){
	"use strict";
	
	Dms.Panel.PropertyPane = function(dm,tabView,isSave){
		Dms.Panel.PropertyPane.superClass.constructor.apply(this);
		var self = this;
		self.dataModel = dm;
		self.tabView = tabView;
		self.createPropertyView(dm);
		dms.isSave = isSave;
		var data = self.dataModel.sm().ld(),
	    type = data.a('bmClassId');
		data.a("RELATED_DISTRICT_CUID_NAME",Dms.Default.user.distName);
		if(type && type === "SITE"){
			var district = data.a("RELATED_DISTRICT_CUID");
			if(district){
				
			}else{
				data.a("RELATED_DISTRICT_CUID",data.a("RELATED_SPACE_CUID"));
	    		data.a("RELATED_DISTRICT_CUID_NAME",data.a("RELATED_SPACE_CUID_NAME"));
			}
			self.createRoomPropertyView(dm);
		}
		self.loadColumns();
		window.addEventListener('resize', function(e) {
			self.invalidate();
		}, false);
		
	};
	
	
	ht.Default.def("Dms.Panel.PropertyPane", ht.widget.BorderPane, {
		dataModel: null,
		propertyView: null,
		saveBtn: null,
		cancelBtn: null,
		selectedData: null,
		tabView : null,
		roomCBox : null,
		roomText : null,
		lock: function() {
			this.setDisabled(true,ctx+"/resources/icons/loading.gif");
		},
		unlock: function() {
			this.setDisabled(false);
		},
		
		createPropertyView : function(dataModel){
			var self = this;
			self.propertyView = new ht.widget.PropertyView(dataModel);
			self.propertyView.setEditable(true);
			self.propertyView.expandAll();
			self.propertyView.setColumnPosition(0.3);
			self.setCenterView(self.propertyView);
		},
		
		createRoomPropertyView : function(){
			var self = this;
			var bottomPanel = self.createBotnPanel();
			self.setBottomView(bottomPanel,30);
		},
		
		createBotnPanel : function(){
			var self = this;
			var botnPanel = new ht.widget.FormPane();
			
			var roomCBox = self.roomCBox=  new ht.widget.CheckBox();
			roomCBox.setLabel("机房名称");
			roomCBox.setLabelColor('red');
			roomCBox.setSelected(true);//复选框默认选中
			
			var roomText = self.roomText = new ht.widget.TextField();
//			roomText.setValue("测试机房1");
			
			botnPanel.addRow([null,roomCBox,roomText,null],[0.1,100,150,0.1],[23]);
			botnPanel.getView().style.border = '1px solid rgb(7,61,86)';
			botnPanel.getView().style.borderLeftWidth ='0';
			botnPanel.getView().style.borderBottomWidth ='0';
			botnPanel.getView().style.borderRightWidth ='0';
			return botnPanel;
		},
		
		getSelectedData : function(index){
			var self = this;
			var node = {};
			var data = self.dataModel.sm().ld();
			for(var p in data._attrObject){
				node[p] = data.a(p);
			}
			if(index){ //批量编辑线设施时保留名称
				var arr = [];
				self.dataModel.sm().each(function(data){
					var obj = {};
					for(var p in node){
						if(p === 'LENGTH'){
							obj[p] = data.a('LENGTH')
						}else{
							obj[p] = node[p];
						}
//						obj[p] = node[p];
					}
					obj.CUID = data.a('CUID');
					obj.LABEL_CN = data.a('LABEL_CN');
					obj.ORIG_POINT_CUID = data.a('ORIG_POINT_CUID');
					obj.DEST_POINT_CUID = data.a('DEST_POINT_CUID');
					arr.push(obj);
				});
				return arr;
			}
			return node;
		},
		
		//批量编辑
		getBatchDatas : function(data){
			var self = this;
			var nameArr = [];
			var namesArr = [];
			var prefix = data.a('PREFIX');
			var suffix = data.a('SUFFIX');
			var serivalNumber = data.a('SERIALNUMBER');
			var origNumber = data.a('ORIGNUMBER');
			var selections = self.dataModel.sm().getSelection();
			if(data.a('COUNT')){
				for(var i=0; i<data.a('COUNT'); i++){
					var origNumLength = (origNumber+"").length;
					var name = "";
					if(serivalNumber <= origNumLength){
						name = name.concat(prefix+origNumber+suffix);
					}else{
						name = name.concat(prefix);
						for(var j=0; j<(serivalNumber-origNumLength); j++){
							name = name.concat('0');
						}
						name = name.concat(origNumber+suffix);
					}
					origNumber++;
					nameArr.push(name);
				}
			}else{
				for(var i=0; i<selections.size(); i++){
					var cuid = selections.get(i).a('CUID'),
					    labelCn = selections.get(i).a('LABEL_CN');
					var mapAttr = {};
					mapAttr[i] = {
							cuid : cuid,
							labelCn : labelCn
					};
					namesArr.push(mapAttr);
				}
			}
			
			var arr = ["PREFIX","SUFFIX","SERIALNUMBER","ORIGNUMBER","COUNT","LONGITUDE","LATITUDE","CUID"];
			var resutArr = [];
			var attrsObj = data._attrObject;
			for(var i=0; i<selections.size(); i++){
				var obj = {};
				var attrObject = selections.get(i)._attrObject;
				for(var p in attrsObj){
					if(arr.indexOf(p) === -1){
						if(p === 'LENGTH'){
							obj[p] = attrObject[p];
						}else{
							if(p=='DEST_POINT_CUID'|| p=='ORIG_POINT_CUID'){
							    obj[p] = selections.get(i).a(p);
						    }else{
						    	 obj[p] = data.a(p);
						    }
						}
//						obj[p] = data.a(p);
					}
				}
				obj.CUID = attrObject['CUID'];
				if(nameArr.length > 0){
					obj.LABEL_CN = nameArr[i];
				}else{
					for(var j =0; j < namesArr.length; j++){
						var attrs = namesArr[j];
						if(attrObject['CUID'] == attrs[j].cuid){
							obj.LABEL_CN = attrs[j].labelCn;
						}
					}
				}
				obj.LONGITUDE = attrObject['LONGITUDE'];
				obj.LATITUDE = attrObject['LATITUDE'];
				resutArr.push(obj);
			}
			return resutArr;
		},
		
		loadColumns : function(){
			var self = this;
			self.selectedData = self.getSelectedData(null);
			var data = self.dataModel.sm().ld();
			self.propertyView.getPropertyModel().clear();
			var pointClass = data.a('bmClassId');
			if(data && pointClass){
				var suffix = "";
				var selectCount = self.dataModel.sm().size();
				if(selectCount > 1){
					suffix = "_BATCH.json";
					if(dms.isPoint[pointClass]){
						data.setAttr("SERIALNUMBER",5);
						data.setAttr("SUFFIX","#");
						data.setAttr("ORIGNUMBER",1);
						data.setAttr("COUNT",selectCount);
					}
				}else{
					suffix = ".json";
				}
				var kind = tp.Default.DrawObject._kind;
	            if(pointClass == "FIBER_JOINT_BOX" && kind == '2'){
	            	pointClass = "FIBER_TERMINAL_BOX";
	            }
	            if(data.a('type')){
	            	pointClass = data.a('type');
	            }
				var url = ctx + "/map/property/" + pointClass + suffix;
				
				if(dms.designer){
					if(dms.designer.isShanxi){
						url = ctx + "/map/property/" + pointClass + '_PLAN' + suffix;
					}
				}
				var roomName = data.a('roomName');
				if(roomName){
					self.roomText.setValue(roomName);
				}
				$.getJSON(url, {}, function(data) {
					self.propertyView.getPropertyModel().clear();
					self.propertyView.addProperties(data);
					self.propertyView.invalidate();
				});
			}
			self.propertyView.invalidate();
		},
		
		show : function(){
			var self = this;
			var dialog = new ht.widget.Dialog(); 
			var width = 300;
			var height = 400;
			if(dms.designer){
				if(dms.designer.isShanxi){
					height = 280;
				}
			}
			dialog.setConfig({
	            title: "<html><font size=2>属性编辑</font></html>",
	            width: width,
	            height: height,
	            titleAlign: "left", 
	            draggable: true,
	            closable: false,
	            content: self.getView(),
	            buttons : [
	            	{
	            		label : '保存',
	            		action : function(){
	            			var selectCount = self.dataModel.sm().size();
	            			var selectIndex= 0 ;
	            			if(self.tabView){
	            				var tv = self.tabView,
								selectTab = tv.getCurrentTab();
								var datas = tv.getTabModel().getDatas();
								selectIndex = datas.indexOf(selectTab);
	            			}
	            			var editData = self.dataModel.sm().ld();
							
	            			if(selectCount == 1){  //单个编辑
		            			var propertyDataModel = self.propertyView.getPropertyModel();
								var flag = false;
								var nullColumnName = null;
								
								propertyDataModel.each(function(property){
									var value = editData.a(property.getName());
									if((value===undefined || value===null || value.toString().trim()==="" || value.length == 0) && !property.isNullable()){
										flag = true;
										nullColumnName = property.getDisplayName();
									}
								});
								if(flag){
									tp.utils.optionDialog("温馨提示",'【<font color="red">'+ nullColumnName +'</font>】缺少属性值，请填写完整！');
									return;
								}
								var data = self.dataModel.sm().ld(),
							    type = data.a('bmClassId');
								if(type === 'SITE'){
									var selected = self.roomCBox._selected;
									var roomName = self.roomText.getValue();
									data.a('select',selected);
									if(selected){
										if(roomName===undefined || roomName.toString().trim()==="" || roomName.length == 0){
											tp.utils.optionDialog("温馨提示",'缺少机房名称，请填写完整！');
										}else{
											data.a('roomName',roomName);
										}
									}
								}
	            				//dialog.hide();
	            				//self.dataModel.sm().cs();
	            			}else{  //批量编辑
	            				var propertyDataModel = self.propertyView.getPropertyModel();
								var flag = false;
								var nullColumnName = null;
								propertyDataModel.each(function(property){
									var value = editData.a(property.getName());
									if((value===undefined || value==null || value.toString().trim()==="" || value.length == 0) && !property.isNullable()){
										flag = true;
										nullColumnName = property.getDisplayName();
									}
								});
								if(flag){
									tp.utils.optionDialog("温馨提示",'【<font color="red">'+ nullColumnName +'</font>】缺少属性值，请填写完整！');
									return;
								}
								
								var pointArr = self.getBatchDatas(editData);
								var lineArr = self.getSelectedData(selectIndex);
								
								var selections = self.dataModel.sm().getSelection();
								for(var i=0; i<selections.size(); i++){
									if(selectIndex == 0){
										selections.get(i)._attrObject = pointArr[i];
									}
									if(selectIndex == 2){
		            					selections.get(i)._attrObject = lineArr[i];
									}
								}
	            			}
	            			if(dms.isSave == true){
	            				var datas = self.dataModel.sm().getSelection();
	            				self.saveDatas(datas);
	            			}
	            			dialog.hide();
	            			self.dataModel.sm().cs();
	            		}
	            	},
	            	{
						label : '取消',
						action : function(){
							var selectCount = self.dataModel.sm().size();
							var lastData = self.dataModel.sm().ld();
							if(selectCount == 1){
								lastData._attrObject = self.selectedData;
							}else{
								self.dataModel.sm().each(function(data){
									if(data == lastData){
										data._attrObject = self.selectedData;
									}
								});
							}
							dialog.hide();
							if(self.tabView){
								self.tabView.iv();
							}
						}
	            	}
	            ],
	            buttonsAlign : 'center'
	        });
	        dialog.show();
		},
		
		saveDatas : function(datas){
			//zhengwei start 
			var self = this;
			var lastData = self.dataModel.sm().ld();
			//zhengwei end
			var objArr = new Array();
			for(var i=0;i<datas.size();i++){
				var node = datas.get(i);
				var obj = new Object();
					obj.bmClassId = node.a("bmClassId");
					obj.CUID = node.a("CUID");
					if(node.a("LABEL_CN")){
						obj.LABEL_CN = node.a("LABEL_CN");//名称
					}
					obj.OWNERSHIP = node.a("OWNERSHIP");//产权
					obj.PURPOSE = node.a("PURPOSE");//用途
					obj.REMARK = node.a("REMARK");//备注
					obj.MAINT_DEP = node.a("MAINT_DEP");//维护单位
					obj.RES_OWNER = node.a("RES_OWNER");//产权单位
					obj.USERNAME = node.a("USERNAME");//使用单位
					obj.USER_NAME = node.a("USER_NAME");//使用单位
					
					obj.STONE_TYPE = node.a("STONE_TYPE");//标石类型
					
					obj.WELL_KIND = node.a("WELL_KIND");//人手井类型
					obj.WELL_TYPE = node.a("WELL_TYPE");//具体类型
					obj.MODEL = node.a("MODEL");//型号
					
					if(node.a("FIBERCAB_NO")){
						obj.FIBERCAB_NO = node.a("FIBERCAB_NO");//编号
					}
					if(node.a("FACE_COUNT")){
						obj.FACE_COUNT = node.a("FACE_COUNT");//面数
					}
					if(node.a("FACE_COL_COUNT")){
						obj.FACE_COL_COUNT = node.a("FACE_COL_COUNT");//每面列数
					}
					if(node.a("TIER_PORT_COUNT")){
						obj.TIER_PORT_COUNT = node.a("TIER_PORT_COUNT");//每排行数
					}
					if(node.a("RELATED_VENDOR_CUID")){
						obj.RELATED_VENDOR_CUID = node.a("RELATED_VENDOR_CUID");//设备供应商
					}
					
					if(node.a("FIBERDP_NO")){
						obj.FIBERDP_NO = node.a("FIBERDP_NO");//编号
					}
					if(node.a("COL_COUNT")){
						obj.COL_COUNT = node.a("COL_COUNT");//列数
					}
					if(node.a("COL_ROW_COUNT")){
						obj.COL_ROW_COUNT = node.a("COL_ROW_COUNT");//每列行数
					}
					if(node.a("TIER_COL_COUNT")){
						obj.TIER_COL_COUNT = node.a("TIER_COL_COUNT");//列数
					}
					if(node.a("TIER_ROW_COUNT")){
						obj.TIER_ROW_COUNT = node.a("TIER_ROW_COUNT");//每小排列数
					}
					
					obj.KIND = node.a("KIND");//设备类型
					obj.JUNCTION_TYPE = node.a("JUNCTION_TYPE");//接头形式
					obj.CAPACITY = node.a("CAPACITY");//接头盒容量
					
					obj.ALIAS = node.a("ALIAS");//别名
					obj.ABBREVIATION = node.a("ABBREVIATION");//缩写
					obj.SERVICE_LEVEL = node.a("SERVICE_LEVEL");//网元业务级别
					obj.SITE_TYPE = node.a("SITE_TYPE");//站点类型
					obj.LOCATION = node.a("LOCATION");//位置
					obj.REAL_LONGITUDE = node.a("REAL_LONGITUDE");//实际经度
					obj.REAL_LATITUDE = node.a("REAL_LATITUDE");//实际纬度
					obj.CONTACTOR = node.a("CONTACTOR");//联系人
					obj.CONTACT_ADDRESS = node.a("CONTACT_ADDRESS");//联系地址
					obj.TELEPHONE = node.a("TELEPHONE");//联系电话
					
					obj.FIBER_COUNT = node.a("FIBER_COUNT");//纤芯总数
					obj.VENDOR = node.a("VENDOR");////具体类型
					obj.WIRE_TYPE = node.a("WIRE_TYPE");//光缆类型
					obj.PRO_NAME = node.a("PRO_NAME");//工程名称
					
					obj.DUCT_SEG_TYPE = node.a("DUCT_SEG_TYPE");//管道段类型
					obj.DEPTH = node.a("DEPTH");//深度
					obj.MULTI_BUILD_USER = node.a("MULTI_BUILD_USER");//共建单位
					obj.SHARED_USER = node.a("SHARED_USER");//共享单位
					
					obj.HEIGHT = node.a("HEIGHT");//高度
					
					obj.LENGTH = node.a("LENGTH");//长度
					
					objArr[i] = obj;
			}
			DWREngine.setAsync(false);
			GenerateCuidAction.updateResAttr(objArr,function(result){
				if(result != "修改成功"){
					tp.utils.optionDialog("温馨提示",result);
					lastData._attrObject = self.selectedData;
					self.isClose='false';
					return;
				}
				tp.utils.optionDialog("温馨提示",result);
			});
			DWREngine.setAsync(true);
		}
		
	});
})(this,Object);