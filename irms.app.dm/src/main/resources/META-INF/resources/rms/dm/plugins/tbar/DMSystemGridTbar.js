Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/dwr/interface/DMSystemAction.js');
$importjs(ctx+'/rms/dm/ductseg/tree-editor.js');

$importjs(ctx+'/dwr/interface/CalculateSystemLengthAction.js');

$importjs(ctx+'/rms/dm/plugins/queryform/ManhleQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/StoneQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/PoleQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/InflexionQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/FiberCabQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/FiberDpQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/FiberJointBoxQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/SiteQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/AccesspointQueryPanel.js');
$importjs(ctx+'/rms/dm/plugins/queryform/OnuboxQueryPanel.js');
$importjs(ctx+'/dwr/interface/GetServiceParamAction.js');
$importjs(ctx+'/dwr/interface/UniteDuctSegAction.js');
$importjs(ctx+'/dwr/interface/UnitePWSegAction');
$importjs(ctx+'/dwr/interface/UniteSUHSegAction');
Frame.grid.plugins.tbar.DMSystemGridTbar = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		
		this.grid.on('click',function(node){
			if(node.attributes != null && node.attributes['BMCLASSTYPE'] == 'BRANCH'){
				Ext.each(this.changeButtonArray,function(button){
					if(button.getId() == "ductSegSectionButton"){
						button.disable();
					}
					else{
						button.enable();
					}
				});
			}else{
				Ext.each(this.changeButtonArray,function(button){
					if(button.getId() == "ductSegSectionButton"){
						button.enable();
					}
					else{
						button.disable();
					}
				});
			}
		},this);
		
		Frame.grid.plugins.tbar.DMSystemGridTbar.superclass.constructor.call(this);
		return this.createButtons();
		
	},
	
	createButtons : function(){
		var addBranchButton = new Ext.Button({
			text:'添加分支',
			hidden: (this.grid.gridConfig.key=='UP_LINE') || (this.grid.gridConfig.key=='HANG_WALL'),
			handler:function(){
				this._createBranch('save','SYSTEM');
			},
			iconCls : 'c_link_add',
			scope:this
		});
		
		var updateBranchButton = new Ext.Button({
			text:'修改分支',
			hidden: (this.grid.gridConfig.key=='UP_LINE') || (this.grid.gridConfig.key=='HANG_WALL'),
			//disabled : true,
			handler:function(){
				this._createBranch('update','SYSTEM');
			},
			iconCls : 'c_link_edit',
			scope:this
		});
		
		var calcLengthButton = new Ext.Button({
			text:'计算段长度',
			handler:this._method1,
			iconCls : 'c_page_white_link',
			scope:this
		});
		var delBranchButton = new Ext.Button({
			text:'删除分支',
			hidden: (this.grid.gridConfig.key=='UP_LINE') || (this.grid.gridConfig.key=='HANG_WALL'),
			disabled : true,
			handler:this._deleteBranch,
			iconCls : 'c_link_break',
			scope:this
		});
		
		var ductSegSectionButton = new Ext.Button({
			id : "ductSegSectionButton",
			text:'管道截面图',
			hidden: (this.grid.gridConfig.key != 'DUCT_BRANCH'),
			iconCls : 'c_cog_edit',
			scope:this,
			handler:this._ductsegSectionView
		});
		
		var updateRouteButton = new Ext.Button({
			text:'修改路由',
			hidden: (this.grid.gridConfig.key !='UP_LINE') && (this.grid.gridConfig.key != 'HANG_WALL'),
			//disabled : true,
			handler:function(){
				this._createBranch('update','SEG');
			},
			iconCls : 'c_link_edit',
			scope:this
		});
		//合并段
		var mergeSegsButton = new Ext.Button({
			text:'合并管线段',
			handler:this._mergeSegsView,
			iconCls : 'c_link_edit',
			scope:this
			
		});
		
		this.changeButtonArray = new Array();
		this.changeButtonArray.push(updateBranchButton);
		this.changeButtonArray.push(delBranchButton);
		this.changeButtonArray.push(ductSegSectionButton);
				return [
			'-',
			addBranchButton,
			updateBranchButton,
			updateRouteButton,
			delBranchButton,
			ductSegSectionButton,
			calcLengthButton,
			mergeSegsButton,
			'-'];
	},
	
	//合并管线系统段实现
	_mergeSegsView : function(){
		var scope=this;
		var segRecords = this.grid.getSelectionModel().selNodes; //选择记录
		var className = segRecords[0].attributes.className; //表名
		var type = className.split("_")[1];
		//判断选择记录是否为分支数据
		if (type == 'BRANCH'){
			Ext.Msg.alert('温馨提示', '请选择分支下的多个段进行合并.');
			return;
	    };
	    var segLists = [];
		//判断选择的段数据数量是否大于1
		if(segRecords.length > 1){
			var firsrOrigPointCuid = null, firstDestPointCuid = null,
			    nextorigPointCuid =null, nextDestPointCuid = null,
			    relatedBranchCuid = null, relatedBranchCuidNext =null;
			//判断选择的段是否有合并点
			for(var i=0; i<segRecords.length; i++ ){
				if((i+1) < segRecords.length){
					//判定是否在同一分支下
					relatedBranchCuid = segRecords[i].attributes.data.RELATED_BRANCH_CUID;
					relatedBranchCuidNext = segRecords[i+1].attributes.data.RELATED_BRANCH_CUID;
					if(relatedBranchCuid != relatedBranchCuidNext){
						Ext.Msg.alert('温馨提示',"选择的段不在同一分支下,不能进行合并段!");
						return;
					}
					//判定是否有合并点
					firsrOrigPointCuid = segRecords[i].attributes.data.ORIG_POINT_CUID;//前一段的起点
					firstDestPointCuid = segRecords[i].attributes.data.DEST_POINT_CUID;//前一段的终点
					nextorigPointCuid = segRecords[i+1].attributes.data.ORIG_POINT_CUID;//后一段的起点
					nextDestPointCuid = segRecords[i+1].attributes.data.DEST_POINT_CUID;//后一段的终点
					if(firstDestPointCuid != nextorigPointCuid && firsrOrigPointCuid != nextDestPointCuid ){
						Ext.Msg.alert('温馨提示',"选择的段没有合并点,不能进行合并光缆段!");
						return;
					}
				}
				var data = segRecords[i].attributes.data;
				var segObjArr = {};
				for(var key in data){
					var value=data[key];
					if(value!=null){
						if(typeof value=='object' && !Ext.isDate(value)){
							segObjArr[key]= (value.CUID==null)?null:value.CUID;										
					    }else if(typeof value=='object' && Ext.isDate(value)){
							segObjArr[key]=value.dateFormat('Y-m-d h:i:s');
						}else{
							segObjArr[key]=value;
						}	
					}else{
						segObjArr[key]=value;
					}
			    }
				segLists.push(segObjArr);
			}
			//提示要合并的段
			var startPoint = null, endPoint = null;
			if(firsrOrigPointCuid == nextDestPointCuid){
				startPoint = segRecords[segRecords.length-1].attributes.data.ORIG_POINT_CUID;//最后一条记录的起点
				endPoint = segRecords[0].attributes.data.DEST_POINT_CUID;//第一条记录的终点
			}
			if(firstDestPointCuid == nextorigPointCuid){
				startPoint = segRecords[0].attributes.data.ORIG_POINT_CUID;//第一条记录的起点
				endPoint = segRecords[segRecords.length-1].attributes.data.DEST_POINT_CUID;//最后一条记录的终点
			}
			Ext.MessageBox.show({
				title:'温馨提示！',
				msg:"是否合并'"+startPoint+"'到'"+endPoint+"'之间的段",
				buttons:{ok:'是',cancel:'否'},
			    fn:function(btn){
			    	if(btn =="ok"){
			    		MaskHelper.mask(scope.grid.getEl(),"数据合并中,请稍候...");
			    		if(className == "DUCT_SEG"){
			    			UniteDuctSegAction.ductUniteSegs(segLists,function(data){
			    				MaskHelper.unmask(scope.grid.getEl());
								scope.grid.reloadNode(scope.grid.getRootNode());//刷新列表
							});
			    		}else if(className == "POLEWAY_SEG"){
			    			UnitePWSegAction.polewayUniteSegs(segLists,function(data){
			    				MaskHelper.unmask(scope.grid.getEl());
								scope.grid.reloadNode(scope.grid.getRootNode());//刷新列表
							});
			    		}else if(className == "STONEWAY_SEG" || className == "UP_LINE_SEG"
			    			|| className == "HANG_WALL_SEG"){
			    			UniteSUHSegAction.uniteSegs(segLists,function(data){
			    				MaskHelper.unmask(scope.grid.getEl());
			    				scope.grid.reloadNode(scope.grid.getRootNode());//刷新列表
			    			});
			    		}
			    	}
			    }
			});
		}else{
			Ext.Msg.alert('温馨提示', '请选择多个段进行合并.');
			return;
		}  	
	},
		_ductsegSectionView : function() {
			var scope=this;
			var records=this.grid.getSelectionModel().selNodes;
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择一条记录!');
		    	return;
		    }
			var record = records[0].attributes.data;
			var cuid=record['CUID'];	
			var url = ctx+"/topo/index.do?code=DuctSectionTopo&resId="+cuid+"&resType=DUCT_SEG&clientType=html5";
			FrameHelper.openUrl(url,'管道截面图-'+record['LABEL_CN']);
/*			 GetServiceParamAction.getUrlByServerName("TOPO",function(data){
				  var url = data+"/topo/index.do?code=DuctSectionTopo&resId="+cuid+"&resType=DUCT_SEG&clientType=html5";
				  var win = new Ext.Window({
				   title : '管道截面图',
				   maximizable : true,
				   width : window.screen.availWidth*0.80,
				   height : window.screen.availHeight*0.75,
				   isTopContainer : true,
				   modal : true,
				   resizable : false,
				   contentEl : Ext.DomHelper.append(document.body, {
				    tag : 'iframe',
				    style : "border 0px none;scrollbar:true",
				    src : url,
				    height : "100%",
				    width : "100%"
				   })
				  });
				  win.show();
			 });	*/
	},
	
	_createBranch : function(action,type){

		this.formPanel = new Ext.form.FormPanel({
			region : 'north',
			frame : true,
			height : 100,
			border : false,
			defaults : {
				anchor : '-20'
			},
			items : [{
				xtype : 'textfield',
				hidden : true,
				fieldLabel : 'CUID',
				name : 'CUID'
			},{
				xtype : 'textfield',
				hidden : true,
				fieldLabel : 'OBJECTID',
				name : 'OBJECTID'
			},{
				xtype : 'textfield',
				fieldLabel : '分支名称',
				name : 'LABEL_CN',
				allowBlank : false
			},{
				xtype : 'textarea',
				name : 'REMARK',
				height : 40,
				fieldLabel : '备注'
			}]
		});
		this.gridPanel = new Ext.grid.GridPanel({
			border : false,
			frame : false,
			region : 'center',
			tbar : [{
				text : '添加',
				iconCls : 'c_link_add',
				menu : [{
					text:'标石',
					hidden: this._isStoneDisplay(),
					iconCls : 'c_bin_closed',
					handler : function(){
						this.selectPoint('IRMS.RMS.STONE');
					},
					scope : this
				},{
					text:'人手井',
					hidden: this._isManhleDisplay(),
					iconCls : 'c_bin_closed',
					handler : function(){
						this.selectPoint('IRMS.RMS.MANHLE');
					},
					scope : this
				},{
					text:'电杆',
					hidden: this._isPoleDisplay(),
					iconCls : 'c_bin_closed',
					handler : function(){
						this.selectPoint('IRMS.RMS.POLE');
					},
					scope : this
				},{
					text:'拐点',
					hidden: this._isInflexionDisplay(),
					iconCls : 'c_bin_closed',
					handler : function(){
						this.selectPoint('IRMS.RMS.INFLEXION');
					},
					scope : this
				},{
					text:'光交接箱',
					hidden: this._isFiberCabDisplay(),
					iconCls : 'c_application_home',
					handler : function(){
						this.selectPoint('IRMS.RMS.FIBER_CAB');
					},
					scope : this
				},{
					text:'光分纤箱',
					hidden: this._isFiberDpDisplay(),
					iconCls : 'c_application_home',
					handler : function(){
						this.selectPoint('IRMS.RMS.FIBER_DP');
					},
					scope : this
				},{
					text:'光接头盒',
					hidden: this._isFiberJointBoxDisplay(),
					iconCls : 'c_application_home',
					handler : function(){
						this.selectPoint('IRMS.RMS.FIBERJOINTBOX');
					},
					scope : this
				},{
					text:'站点',
					hidden: this._isSiteDisplay(),
					iconCls : 'c_house',
					handler: function() {
						this.selectPoint('IRMS.RMS.SITE');
					},
					scope : this
				
				},{
					text:'接入点',
					hidden: this._isAccesspointDisplay(),
					iconCls : 'c_house_link',
					handler: function() {
						this.selectPoint('IRMS.RMS.ACCESSPOINT');
					},
					scope: this
				}],
				scope : this
			},{ 
				text : '删除',
				iconCls : 'c_link_delete',
				handler : this._deletePoint,
				scope : this
			},
			'->',
			{
				text : '上移',
				iconCls : 'c_bullet_arrow_up',
				handler : this._upPoint,
				scope : this
			},{
				text : '下移',
				iconCls : 'c_bullet_arrow_down',
				handler : this._downPoint,
				scope : this
			},{
				text : '至顶',
				iconCls : 'c_bullet_arrow_top',
				handler : this._upPointToFirst,
				scope : this
			},{
				text : '至底',
				iconCls : 'c_bullet_arrow_bottom',
				handler : this._downPointToLast,
				scope : this
			}],
			store: new Ext.data.Store({
		        autoDestroy: true,
		        reader: new Ext.data.DataReader({
		        	fields : [
		        		{name : 'LABEL_CN'},
		        		{name : 'RELATED_BMCLASSTYPE_CUID'}
		        	]
		        })
		    }),
		    colModel: new Ext.grid.ColumnModel({
		    	defaults : {
		    		width : 260,
		    		shortable : false
		    	},
		    	columns : [
		    		new Ext.grid.CheckColumn({header : '是否显示段',width : 120,dataIndex:'IS_SELECT'}),
		    		{id:'LABEL_CN',header:'名称',dataIndex:'LABEL_CN'},
		    		{id:'RELATED_BMCLASSTYPE_CUID',header:'所属类型',dataIndex:'RELATED_BMCLASSTYPE_CUID'}
		    	]
		    })
		});
		
		if(action == 'update'){
			//
			var sysCuid;
			if(this.grid.gridConfig.type=='SYSTEM'){
				var dbo = this.grid.getSelectionModel().selNodes[0].attributes.data;
				//this.grid.getSelectionModel().selNode.attributes.data;  
				this.formPanel.getForm().setValues({
					CUID : dbo['CUID'],
					LABEL_CN : dbo['LABEL_CN'],
					REMARK:dbo['REMARK'],
					OBJECTID:this.grid.gridConfig.objectId
				});
				sysCuid=dbo['CUID'];
			}else{
				sysCuid=this.grid.gridConfig.cuid;
			}
			var records = new Array();
			var scope=this;
			DMSystemAction.getDisplayPoints(sysCuid,function(data){
				Ext.each(data,function(child){
					var record = new Ext.data.Record({
						CUID : child.cuid,
						LABEL_CN : child.labelCn,
						IS_SELECT : child.selected,
						RELATED_BMCLASSTYPE_CUID:child.classtype,
						OBJECTID:child.objectId
					});
					records.push(record);
				});
				var store = scope.gridPanel.getStore();
				store.add(records);
			});
		}
		
		var panel;
		if(type=='SYSTEM'){
			panel=new Ext.Panel({
				border : false,
				hideBorders : false,
				frame : false,
				layout : 'border',
				items : [this.formPanel,this.gridPanel]
			});
		}else{
			panel=new Ext.Panel({
				border : false,
				hideBorders : false,
				frame : false,
				layout : 'border',
				items : [this.gridPanel]
			});
		}

		var win = WindowHelper.openExtWin(panel,
			{
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() * 0.95,
				frame : true,
				buttons : [{
					text:'确定',
					handler : function(){
						this.save(win);
					},
					scope : this
				},{
					text:'取消',
					handler : function(){
						win.close();
					}
				}]
			}
		);
	},
	_isStoneDisplay : function(key) {
		 if(this.grid.gridConfig.key=='STONEWAY_BRANCH' || this.grid.gridConfig.key=='UP_LINE'){
			 return false;
		 }
		 return true;
	},
	_isManhleDisplay: function(key) {
		if(this.grid.gridConfig.key=='DUCT_BRANCH' || this.grid.gridConfig.key=='STONEWAY_BRANCH' 
		|| this.grid.gridConfig.key=='UP_LINE' ){
			return false;
		}
		return true;
	},
	_isPoleDisplay: function(key) {
		if(this.grid.gridConfig.key=='POLEWAY_BRANCH' || this.grid.gridConfig.key=='UP_LINE'
	 || this.grid.gridConfig.key=='HANG_WALL'){
			return false;
		}
		return true;
	},
	_isInflexionDisplay:function(key) {
		if(this.grid.gridConfig.key=='POLEWAY_BRANCH' || this.grid.gridConfig.key=='UP_LINE'
			|| this.grid.gridConfig.key=='HANG_WALL'){
			return false;
		}
		return true;
	},
	_isFiberCabDisplay:function(key){
		return false;
	},
	_isFiberDpDisplay:function(key){
		return false;
	},
	_isFiberJointBoxDisplay : function(key){
		if(this.grid.gridConfig.key=='UP_LINE'){
			return false;
		}
		return true;
	},
	_isSiteDisplay:function(key){
		return false;
	},
	_isAccesspointDisplay:function(key){
		return false;
	},
	_deleteBranch : function(){
		var dbo = this.grid.getSelectionModel().selNodes;//.attributes.data;
		var scope=this;
		if(dbo != null){
			for(var i=0; i<dbo.length; i++){
				if(dbo[i].attributes['BMCLASSTYPE'] == 'BRANCH'){
					Ext.Msg.confirm("温馨提示","该分支准备删除，请确认.",function(btn){
						if(btn == 'yes'){
							DMSystemAction.delObjByCuid(dbo[i].attributes.CUID,dbo[i].attributes.OBJECTID,function(data){
								scope.grid.reloadNode(scope.grid.getRootNode());//刷新列表
								Ext.Msg.alert("删除成功！");
							});
						}else{
							return;
						}
					},this);
					return;
				}
			}
		}
		
	},
	_method1: function(){
		
	var records=this.grid.getSelectionModel().selNodes[0].attributes.data;
		 if (records['BMCLASSTYPE']=='BRANCH'){
		   Ext.Msg.alert('温馨提示', '请选择分支下层进行计算.');
			return;
	   }
		 var scope=this;
		 Ext.MessageBox.show({
		  title:'注意！',
		msg:"此操作根据起止端点的经纬度计算段的长度，并修改管孔，子孔长度 <br/>当段的起止点的实际经纬度为非法值时，段的长度不修改，是否继续？<br/>选择是将继续该操作，选择否将取消该操作.",
		buttons:{ok:'是',cancel:'否'},
	    fn:function(btn){
	    	if(btn =="ok"){
	    		CalculateSystemLengthAction.doDMCalculateSystemLength(records.CUID,function(data){
	    			Ext.Msg.alert('温馨提示', '计算段长度完成.');
					scope.grid.reloadNode(scope.grid.getRootNode());//刷新列表
				});
//	    		CalculateSystemLengthAction.doDMCalculateSystemLength(records.CUID);
//	    		Ext.Msg.alert('温馨提示', '计算段长度完成.');
    		}
	      }
	});
	},
	save : function(win){
		var grid = this.gridPanel;
		var form = this.formPanel;
		var store = grid.getStore();
		var scope=this;
		if(!form.getForm().isValid()&&this.grid.gridConfig.type.indexOf("SYSTEM")>=0){
			Ext.Msg.alert('温馨提示','信息不完整.');
			return;
		}
		if(store == null || store.getCount() < 2){
			Ext.Msg.alert('温馨提示','路由点数量不能小于2.');
			return;
		}
		var rountPoints = new Array();
		var firstRecord = store.getAt(0);
		var lastRecord = store.getAt(store.getCount()-1);
		
		if(firstRecord.data['IS_SELECT'] != true || lastRecord.data['IS_SELECT'] != true){
			Ext.Msg.alert('温馨提示','起止点路由必须为显示路由.');
			return;
		}
		
		for(var i=0;i<store.getCount();i++){
			var record = store.getAt(i);
			var dbo = {
				CUID : record.data['CUID'],
				LABEL_CN : record.data['LABEL_CN'],
				IS_SELECT :  record.data['IS_SELECT'] == null ? false : record.data['IS_SELECT'],
				OBJECTID:record.data['OBJECTID'].toString(),
				BM_CLASS_ID: record.data['CUID'].split("-")[0]
			};
			rountPoints.push(dbo);
		}
		
		var branchList = new Array();
		var branch=new Object();
		if(this.grid.gridConfig.type.indexOf("SYSTEM")>=0 && form){
			branch=form.getForm().getValues();
		}
		var param = {
			branch : branch,
			systemCuid:this.grid.gridConfig.cuid,
			type:this.grid.gridConfig.key,
			rountPoints : rountPoints
		};
		MaskHelper.mask(scope.grid.getEl(),"读取中,请稍候...");
		DMSystemAction.save(branch,this.grid.gridConfig.cuid,this.grid.gridConfig.key,rountPoints,function(data){
			MaskHelper.unmask(scope.grid.getEl());
			win.close();
			scope.grid.reloadNode(scope.grid.getRootNode());
		});
	}
	,
	_deletePoint : function(){
		var grid = this.gridPanel;
		var store = this.gridPanel.getStore();
		var selections = grid.getSelectionModel().getSelections();
		if(selections == null || selections.length == 0){
			Ext.Msg.alert('温馨提示','请选择需要删除的内容.');
			return;
		}
		store.remove(selections);
	},
	
	_upPoint : function(){
		var grid = this.gridPanel;
		var store = this.gridPanel.getStore();
		var selections = grid.getSelectionModel().getSelections();
		if(selections == null || selections.length == 0){
			Ext.Msg.alert('温馨提示','请选择需要上移的内容.');
			return;
		}
		
		Ext.each(selections,function(record){
			var index = store.indexOf(record);
			if(index > 0){
				store.removeAt(index);
				store.insert(index-1,record);
				grid.getView().refresh();
				grid.getSelectionModel().selectRow(index-1);
			}
		});
	},
	
	_downPoint : function(){
		var grid = this.gridPanel;
		var store = this.gridPanel.getStore();
		var selections = grid.getSelectionModel().getSelections();
		if(selections == null || selections.length == 0){
			Ext.Msg.alert('温馨提示','请选择需要上移的内容.');
			return;
		}
		
		Ext.each(selections,function(record){
			var index = store.indexOf(record);
			if(index < (store.getCount()-1)){
				store.removeAt(index);
				store.insert(index+1,record);
				grid.getView().refresh();
				grid.getSelectionModel().selectRow(index+1);
			}
		});
	},
	_upPointToFirst : function(){
		
		var grid = this.gridPanel;
		var store = this.gridPanel.getStore();
		var selections = grid.getSelectionModel().getSelections();
		if(selections == null || selections.length == 0){
			Ext.Msg.alert('温馨提示','请选择需要上移的内容.');
			return;
		}
		
		Ext.each(selections,function(record){
			var index = store.indexOf(record);
			if(index > 0){
				store.removeAt(index);
				store.insert(0,record);
				grid.getView().refresh();
				grid.getSelectionModel().selectRow(0);
			}
		});
	},
	_downPointToLast : function(){
		
		var grid = this.gridPanel;
		var store = this.gridPanel.getStore();
		var selections = grid.getSelectionModel().getSelections();
		if(selections == null || selections.length == 0){
			Ext.Msg.alert('温馨提示','请选择需要上移的内容.');
			return;
		}
		Ext.each(selections,function(record){
			var index = store.indexOf(record);
			var lastNum = store.getCount()-1;
			if(index < lastNum){
				store.removeAt(index);
				store.insert(lastNum,record);
				grid.getView().refresh();
				grid.getSelectionModel().selectRow(lastNum);
			}
		});
	},
	
	selectPoint : function(templateId){
		var grid = new Frame.grid.MaintainGridPanel({
			hasMaintan:false,
			isTree:false,
			hasPageBar : true,
			pageSize : 20,
			totalNum : 20,
			queryPlugin : this._queryPanelType(templateId),
			gridCfg : {
				cfgParams : {
					templateId : templateId
				},
				boName:'GridTemplateProxyBO'
			}
		});
		var scope = this;
		var win = WindowHelper.openExtWin(grid,
			{
				width : 900,
				height : 400,
				frame : true,
				buttons : [{
					text:'选择',handler : function(scope){
						var classNames = {"MANHLE":"人手井","POLE":"电杆","SITE":"站点","FIBER_DP":"分纤箱","FIBER_CAB":"交接箱",
								"FIBER_JOINT_BOX":"接头盒","ACCESSPOINT":"接入点","INFLEXION":"拐点"};
						var innerGrid = win.items.items[0].grid;
						var selections = innerGrid.getSelectionModel().getSelections();
						if(selections == null || selections.length == 0){
							Ext.Msg.alert('温馨提示','请选择至少一条记录.');
							return;
						}
						for(var i = 0; i < selections.length; i++){							
							var cuid = selections[i].data.CUID;
							var className = cuid.substring(0,cuid.indexOf('-'));
							selections[i].data.RELATED_BMCLASSTYPE_CUID=classNames[className];
						}
						var grid = this.gridPanel;
						var store = grid.getStore();
						store.add(selections);
						grid.getView().refresh();
						win.close();
					},scope : this
				},{
					text:'取消',
					handler : function(){
						win.close();
					}
			}],
				scope : this
			});
	},
	_queryPanelType : function(templateId){
		if(templateId=='IRMS.RMS.STONE'){
			return 'StoneQueryPanel';
		}
		else if(templateId=='IRMS.RMS.MANHLE'){
			return 'ManhleQueryPanel';
		}
		else if(templateId=='IRMS.RMS.POLE'){
			return 'PoleQueryPanel';
		}
		else if(templateId=='IRMS.RMS.INFLEXION'){
			return 'InflexionQueryPanel';
		}
		else if(templateId=='IRMS.RMS.FIBER_CAB'){
			return 'FiberCabQueryPanel';
		}
		else if(templateId=='IRMS.RMS.FIBER_DP'){
			return 'FiberDpQueryPanel';
		}
		else if(templateId=='IRMS.RMS.FIBERJOINTBOX'){
			return 'FiberJointBoxQueryPanel';
		}
		else if(templateId=='IRMS.RMS.SITE'){
			return 'SiteQueryPanel';
		}
		else if(templateId=='IRMS.RMS.ACCESSPOINT'){
			return 'AccesspointQueryPanel';
		}
		else if(templateId=='IRMS.RMS.ONUBOX'){
			return 'OnuboxQueryPanel';
		}
	}
	
});