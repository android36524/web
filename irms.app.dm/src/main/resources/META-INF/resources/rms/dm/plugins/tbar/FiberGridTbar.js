Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/rms/dm/wire/wire_template.js');
$importjs(ctx+'/map/dms-map-deleteResource.js');
$importjs(ctx + "/rms/dm/wire/FiberDeleteView.js");
$importjs(ctx + "/rms/dm/wire/FiberDirectionPanel.js");
$importjs(ctx + "/dwr/interface/CheckTaskNameAction.js");

Frame.grid.plugins.tbar.FiberGridTbar = Ext.extend(Object,
		{

			constructor : function(grid) {
				this.grid = grid;
				Frame.grid.plugins.tbar.FiberGridTbar.superclass.constructor.call(this);
				return this.createButtons();
			},
			createButtons : function(){
				var fiberRouteViewButton = new Ext.Button({
					text : '纤芯完整路由图',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._routeview
				});
				
				var checkTaskButton = new Ext.Button({
					text : '设置核查任务',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._checktask
				});
				
				var templeteButton = new Ext.Button({
					text : '应用模板',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this._useTemplate
				});
				 
				var deleteBtn = new Ext.Button({
					text : '删除',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this.deleteFibers
				});
				
				var updateFiberBtn = new Ext.Button({
					text : '更新纤芯方向',
					iconCls : 'c_page_white_link',
					scope : this,
					handler : this.updateFibers
				});
				this.grid.on('click',function(node){
					if(node.attributes != null && node.attributes['BMCLASSTYPE'] == 'FIBER'){
						fiberRouteViewButton.enable();
						checkTaskButton.enable();
						templeteButton.disable();
						deleteBtn.enable();
					}else{
						fiberRouteViewButton.disable();
						checkTaskButton.disable();
						templeteButton.enable();
						deleteBtn.disable();
					};
				},this);
/*				this.changeButtonArray = new Array();
				this.changeButtonArray.push(fiberRouteViewButton);
				this.changeButtonArray.push(checkTaskButton);*/
				return [
					'-',
					fiberRouteViewButton,
					templeteButton];
			},
			
			_routeview : function() {
				var records = this.grid.getSelectionModel().getSelectedNodes();
				if (Ext.isEmpty(records) || records.length == 0) {
					Ext.Msg.alert('温馨提示', '请选择纤芯！');
					return;
				}
				var cuid = records[0].attributes.data.CUID;	
				var name = records[0].attributes.data.LABEL_CN;	
				var url = ctx+"/topo/index.do?code=FiberRouteSectionTopo&resId="+cuid+"&resType=FIBER&clientType=html5";
				FrameHelper.openUrl(url,'纤芯完整路由图-'+name);
			},
			
			_checktask : function() {
				var records = this.grid.getSelectionModel().getSelectedNodes();
				if(Ext.isEmpty(records)||records.length==0){
					Ext.Msg.alert('温馨提示', '请选择一条记录.');
					return;
				}
				if(records.length>1){
					Ext.Msg.alert('温馨提示', '目前只支持单条记录生成.');
					return;
				};
				Ext.MessageBox.show({
					title:'注意！',
					msg:'您确认要核查吗',
					buttons:{yes:'确认',cancel:'取消'},
					fn:function(btn){
						 if(btn =="yes"){
							 Ext.MessageBox.prompt('任务核查', '请输入核查任务:', function(e,text) {
									if(e == "ok") {
										if(Ext.isEmpty(text)){
											Ext.Msg.alert('提示','请输入核查任务');
										}else{
											var list={
													CUID:records[0].attributes.CUID,
													OBJECTID:records[0].attributes.OBJECTID
											};
									        setTimeout(function(){
									        	CheckTaskNameAction.lookTaskNameIsHave(text,list,function(data){
									        		if(data=='true'){
									        			Ext.Msg.alert('提示','[任务名称] 已占用！');
									        			return;
									        		}
													Ext.Msg.alert('执行结果',data);
												});
									        }, 100);
									        Ext.MessageBox.show({
									        	msg: '任务核查中, 请等待...',
									        	progressText: '执行中...',
									        	width:300,
									        	wait:true,
									        	waitConfig: {interval:200},
									        	icon:'ext-mb-download'
									        });
										}
									}
							 });
						 };
					}
				});
			},
			
		_useTemplate : function()
		{
			var records = this.grid.getSelectionModel().getSelectedNodes();
			if(Ext.isEmpty(records)||records.length==0){
				Ext.Msg.alert('温馨提示', '请选择一条记录.');
				return;
			}
			if(records[0].attributes.className == "WIRE_SEG" && records[0].attributes.children == null)
			{
				openWireTemplate(this.grid);
				return;
			}
			else
			{
				Ext.Msg.alert('温馨提示', '请选择一条没有纤芯的光缆段.');
				return;
			}
		},
		deleteFibers : function() 
		{
			var records = this.grid.getSelectionModel().getSelectedNodes();
			var scope = this;
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择一条记录.');
		    	return;
		    }
		    
		    var cuid = records[0].attributes.data.CUID;	
			var name = records[0].attributes.data.LABEL_CN;
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据不包含必需字段,请检查配置。。。');
				return;
			}
			
			var cuids = [];
			cuids.push(cuid);
//		    for(var i=0;i<records.length;i++){
//		    	var cid = records[i].attributes.CUID;//records[i].data['CUID'];
//		    	cuids.push(cid);
//		    }
		    
			var deleteFiberPanel = new Frame.wire.FiberDeletePanel({
				cuid : cuid,
				labelcn : name,
				cuids : cuids,
				grid : scope.grid
			});

			var win = WindowHelper.openExtWin(deleteFiberPanel, {
				title : '纤芯删除操作',
				width : window.screen.availWidth*0.5,
				height : window.screen.availHeight*0.5
			});
			deleteFiberPanel._win = function(){
				win.hide();
				return true;
			};
		},
		updateFibers : function(){
			var scope = this;
			var records = this.grid.getSelectionModel().getSelectedNodes();
			if(Ext.isEmpty(records) || records.length==0){
				Ext.Msg.alert('温馨提示','请选择一条记录.');
				return;
			}
			var fiberCuids = "";
			if(records!=null && records.length>0)
			{
				for(var i=0;i<records.length;i++){
					var record = records[i];
					var cuid = record.attributes.data.CUID;
					if(Ext.isEmpty(fiberCuids)){
						fiberCuids = cuid;
					}else{
						fiberCuids +=','+cuid;
					}
					}
			}
			this.panel = new Frame.wire.FiberDirectionPanel({
				cuids : fiberCuids
			});
			var win = WindowHelper.openExtWin(scope.panel, {
				title:'更新纤芯方向',
				width:450,
				height:150,
				layout : 'fit',
				border : false,
				modal : true,
				buttons : [{
					text : '确定',
					iconCls : 'c_accept',
					scope : this,
					handler : function(){
						var panel =this.panel;
						var text = panel.UpdateFiberInfo(fiberCuids);
						if(text=='OK'){
							Ext.Msg.alert('温馨提示', '保存成功！');
						}else{
							Ext.Msg.alert('温馨提示', text);
							return;
						}
						win.hide();
					}
				},{
					text : '取消',
					iconCls : 'c_door_open',
					scope : this,
					handler : function(){
						win.hide();
					}
				}]
			});
		}
	});
