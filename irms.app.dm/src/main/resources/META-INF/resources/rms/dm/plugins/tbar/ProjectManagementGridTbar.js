Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx + '/rms/dm/projectmanagement/projectmanaStatePanel.js');
$importjs(ctx + '/rms/dm/projectmanagement/resourcePanel.js');
$importjs(ctx + '/rms/dm/projectmanagement/projectProgressPanel.js');
$importjs(ctx+'/dwr/interface/GetSystemAuthorityAction.js');
$importjs(ctx+'/dwr/interface/ProjectManageAction.js');




Frame.grid.plugins.tbar.ProjectManagementGridTbar = Ext.extend(Object, {

	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.ProjectManagementGridTbar.superclass.constructor.call(this);
		
		var isHideen = true;
		DWREngine.setAsync(false);
		GetSystemAuthorityAction.hasApprovalAuthority(function(result){
			if(result != null){
				isHideen = !result;
			}
		});
		DWREngine.setAsync(true);
		
		return [{
			text : '关联资源管理',
	        iconCls : 'c_page_white_link',
	        scope : this.grid,
	        handler : this._relatedResources
		},{
			text : '设计->施工',
			tooltip : '提交施工',
			iconCls : 'c_page_white_link',
			scope:this.grid,
			handler : this._designToConstruct
		},{
			text : '施工->竣工',
			tooltip : '提交验收',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._constructToMaintain
		},{
			text : '竣工->维护',
			tooltip : '交维',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._maintainToAccept
		},{
			text : '工程审批',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			hidden : isHideen,
			handler : this._acceptProject
		},{
			text : '查看工程进展',
			iconCls : 'c_page_white_link',
			scope : this.grid,
			handler : this._showProjectProgress
		}];
	},
	
	_relatedResources: function() {
		var records=this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length==0 || records.length>1){
			 Ext.Msg.alert('温馨提示','请选择一条工程管理数据操作。');
			 return;
		}else{
             var url="/rms/dm/projectmanagement/ProjectManagement.jsp?x=x";
             var cuid=records[0].data['CUID'];
             var labelCn=records[0].data['LABEL_CN'];
             var objectId=records[0].data['OBJECTID'];
			 if (Ext.isEmpty(cuid) || Ext.isEmpty(labelCn)) {
				 Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				 return;
			 }
             FrameHelper.openUrl(ctx+url+"&cuid="+cuid+"&objectId="+objectId+"&labelCn="+labelCn,
              labelCn);
		}
	},
	
		/**
	 * 设计->施工
	 */
	_designToConstruct : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert('温馨提示','请选择一条工程状态为设计的数据操作');
			return;
		}else{
			var stateValue = records[0].data.STATE.CUID;
			if(Ext.isEmpty(stateValue) || stateValue != "1"){
				Ext.Msg.alert('温馨提示','工程状态不是设计，该按钮不可操作');
				return;
			}else{
				this.formPanel = new DM.form.formPamel({});
				var win = WindowHelper.openExtWin(this.formPanel,{
					title : '工程管理',
					width : 340,
					height : 180,
					frame : true,
					resizable: false, 
					buttons : [{
						text: '提交审批',
					    scope:this,
					    handler: function(){
					    	this.formPanel._saveSubProject(records,null);
					    	win.hide();
					    	this.doQuery();
					    }
					}, {
						text : '取消',
						handler : function() {
							win.close();
						}
					} ]
				});
			}
		}
	},
	
	/**
	 * 施工->交维
	 */
	_constructToMaintain : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert('温馨提示','请选择一条工程状态为施工的数据操作');
			return;
		}else{
			var stateValue = records[0].data.STATE.CUID;
			if(Ext.isEmpty(stateValue) || stateValue != "2"){
				Ext.Msg.alert('温馨提示','工程状态不是施工，该按钮不可操作');
				return;
			}else{
				this.formPanel = new DM.form.formPamel({});
				var win = WindowHelper.openExtWin(this.formPanel,{
					title : '工程管理',
					width : 340,
					height : 180,
					frame : true,
					resizable: false,
					buttons : [{
						text: '提交审批',
					    scope:this,
					    handler: function(){
					    	this.formPanel._saveSubProject(records,null);
					    	win.hide();
					    	this.doQuery();
					    }
					}, {
						text : '取消',
						handler : function() {
							win.close();
						}
					} ]
				});
			}
		}
	},
	
	/**
	 * 交维->验收
	 */
	_maintainToAccept : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert('温馨提示','请选择一条工程状态为交维的数据操作');
			return;
		}else{
			var stateValue = records[0].data.STATE.CUID;
			if(Ext.isEmpty(stateValue) || stateValue != "3"){
				Ext.Msg.alert('温馨提示','工程状态不是竣工，该按钮不可操作');
				return;
			}else{
				this.formPanel = new DM.form.formPamel({});
				var win = WindowHelper.openExtWin(this.formPanel,{
					title : '工程管理',
					width : 340,
					height : 180,
					frame : true,
					resizable: false,
					buttons : [{
						text: '提交审批',
					    scope:this,
					    handler: function(){
					    	this.formPanel._saveSubProject(records,null);
					    	win.hide();
					    	this.doQuery();
					    }
					}, {
						text : '取消',
						handler : function() {
							win.close();
						}
					} ]
				});
			}
		}
	},
	
	/**
	 * 工程审批
	 */
	_acceptProject : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert('温馨提示','请选择一条工程状态为待审批的数据操作');
			return;
		}else{
			var stateValue = records[0].data.STATE.CUID;
			if(Ext.isEmpty(stateValue) || stateValue != "5"){
				Ext.Msg.alert('温馨提示','工程状态不是待审批，该按钮不可操作');
				return;
			}else{
				var isApproved = false;
				this.formPanel = new DM.form.formPamel({});
				var win = WindowHelper.openExtWin(this.formPanel,{
					title : '工程审批',
					width : 340,
					height : 180,
					frame : true,
					resizable: false,
					buttons : [{
						text: '通过',
					    scope:this,
					    handler: function(){
					    	isApproved = true;
					    	this.formPanel._saveSubProject(records,isApproved);
					    	win.hide();
					    	this.doQuery();
					    }
					}, {
						text : '驳回',
						scope:this,
						handler : function() {
							this.formPanel._saveSubProject(records,isApproved);
							win.close();
							this.doQuery();
						}
					} ]
				});
			}
		}
	},
	
	/**
	 * 查看工程进展
	 */
	_showProjectProgress : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert('温馨提示','请选择一条工程管理数据操作');
			return;
		}else{
			this.formPanel = new Ext.form.formPamel({});
			var win = WindowHelper.openExtWin(this.formPanel, {
				title : '工程进展',
				width : 700,
				height : 400,
				frame : true,
				buttons : [ {
					text : '关闭',
					handler : function() {
						win.close();
					}
				} ]
			});
			
			//与后台数据交互
			var scope = this.formPanel;
			var datas = new Array();
			var projectCuid = records[0].data.CUID;
			MaskHelper.mask(scope.getEl(),"读取中,请稍候...");
			ProjectManageAction.getProjectProgressList(projectCuid,function(result){
				for(var i = 0; i < result.length; i++){
					var gridData = result[i];
					var record = new Ext.data.Record({
						CUID : gridData.CUID,
						LABEL_CN : gridData.LABEL_CN,
						NO : gridData.NO,
						OPERATOR : gridData.OPERATOR,
						OPERATION_NAME : gridData.OPERATION_NAME,
						DESCRIPTION : gridData.DESCRIPTION,
						OPERATION_DATE : gridData.OPERATION_DATE
					});
					datas.push(record);
				}
				var store = scope.getStore();
				store.add(datas);
				MaskHelper.unmask(scope.getEl());
			});
		}
	}
	
});