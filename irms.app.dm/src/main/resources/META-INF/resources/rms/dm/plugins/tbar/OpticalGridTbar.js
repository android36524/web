Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/dwr/interface/GenerateOpticalAction.js');
$importjs(ctx+'/dwr/interface/GetServiceParamAction.js');
$importjs(ctx+'/dwr/interface/OpticalWayAction.js');
$importjs(ctx+'/map/dms-map-deleteResource.js');
$importjs(ctx + "/rms/dm/wire/OpticalDeleteView.js");

Frame.grid.plugins.tbar.OpticalGridTbar = Ext.extend(Object,
	{
		constructor : function(grid) {
			this.grid = grid;
			Frame.grid.plugins.tbar.OpticalGridTbar.superclass.constructor.call(this);
			return [ '-', {
				text : '重新生成光纤',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._regenerateOpticalsMethod
			}, '-',{
				text : '光纤路由图',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._opticalRoute
			},'-',{
				text : '查看光路列表',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._opticalWayLists
			},'-',{
				text : '删除光纤',
				iconCls : 'c_page_white_link',
				scope : this.grid,
				handler : this._deleteOpticals
				//_deleteOpticalLists
			}  
			];
		},
		_regenerateOpticalsMethod : function() {
			var records=this.getSelectionModel().getSelections();
			if(Ext.isEmpty(records)||records.length==0){
				Ext.Msg.alert('温馨提示', '请选择一条记录.');
				return;
			}
			var cuid = records[0].data['CUID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			Ext.MessageBox.show({
				title:'注意！',
				msg:'起止端子信息不全或者起止设备一端是光接头盒的光纤，<br />且该光纤是非占用或非预占用状态的将重新生成。',
				buttons:{yes:'生成光纤',cancel:'取消'},
				 fn:function(btn){
					 if(btn =="yes"){
						 var cuids="";
						 for(var i=0;i<records.length;i++){
							 cuids=cuids+records[i].data['CUID']+"&&";
						 }
						 GenerateOpticalAction.doRegenerateOpticals(cuids);
					 }
				 }
			});
		},
		_opticalRoute: function() {
			var records=this.getSelectionModel().getSelections();
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择一条记录.');
		    	return;
		    }
			 var cuid=records[0].data['CUID'];	
			 if (Ext.isEmpty(cuid)) {
				 Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				 return;
			 }
			  var url = ctx+"/topo/index.do?code=FiberRouteSectionTopo&resId="+cuid+"&resType=OPTICAL&clientType=html5";						 
			 FrameHelper.openUrl(url,'光纤路由图-'+records[0].data['LABEL_CN']);
		},
		/**
		 * 查看光路列表
		 */
		_opticalWayLists : function() {
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
				Ext.Msg.alert('温馨提示', '请选一条光纤管理数据.');
				return;
			}
			var cuid = records[0].data['CUID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			var cfg= {openType : 'extwin',
				      width : 700,
				      height :400};
			var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_OPTICAL_WAY_OPTICAL&hasQuery=false';
			FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
					 '查看光路信息列表',null,cfg);
		},
		/**
		 * 删除光纤
		 */
		_deleteOpticalLists : function(){
			var scope = this;
			var records = this.getSelectionModel().getSelections();
			if (Ext.isEmpty(records) || records.length == 0) {
				Ext.Msg.alert('温馨提示', '请选要删除的光纤数据.');
				return;
			}
			
			var cuid = records[0].data['CUID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
				return;
			}
			Ext.MessageBox.show({
				title:'温馨提示！',
				msg:' 请确定是否要删除选中光纤数据 ',
				buttons:{yes:'确定',cancel:'取消'},
				 fn:function(btn){
					 if(btn =="yes"){
						 var resources=[];
				    	   for(var i=0;i<records.length;i++){
				    		   var data=records[i].json;
				    		   var res={};
				    		   for(var key in data){
				    			   var value=data[key];
				    			   if(value!=null){
				    				   if(typeof value=='object' && !Ext.isDate(value)){
				    					    res[key]= (value.CUID==null)?null:value.CUID;										
									    }
										else if(typeof value=='object' && Ext.isDate(value)){
											res[key]=value.dateFormat('Y-m-d h:i:s');
										}
										else{
											res[key]=value;
										}	
				    			   }
				    			   else{
				    				   res[key]=value;
				    			   }
				    		   }
				    		   resources.push(res);
				    	   }
				    	   OpticalWayAction.doDeleteOpOrOpWays(resources,function(){
				    		   scope.doQuery();
				    	   });
					 }
				 }
			});
		},
		_deleteOpticals : function() {
			var records=this.getSelectionModel().getSelections();
			var scope = this;
		    if(Ext.isEmpty(records) || records.length==0){
		    	Ext.Msg.alert('温馨提示','请选择一条记录。。。');
		    	return;
		    }
		    
			var cuid = records[0].data['CUID'];
			if (Ext.isEmpty(cuid)) {
				Ext.Msg.alert('系统错误', '当前选中数据不包含必需字段,请检查配置。。。');
				return;
			}
			var labelcn=records[0].data['LABEL_CN'];
			
			var cuids = [];
		    for(var i=0;i<records.length;i++){
		    	var cid = records[i].data['CUID'];
		    	cuids.push(cid);
		    }
			
			var deleteOpticalsPanel = new Frame.wire.OpticalDeletePanel({
				cuid : cuid,
				labelcn : labelcn,
				cuids : cuids
			});
	
			var win = WindowHelper.openExtWin(deleteOpticalsPanel, {
				title : '光纤删除操作',
				width : window.screen.availWidth*0.5,
				height : window.screen.availHeight*0.5
			});
			deleteOpticalsPanel._win = function(){
				win.hide();
				scope.store.reload();
				return true;
			};
		}
	});
