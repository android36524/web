Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/dwr/interface/GetServiceParamAction.js');
$importjs(ctx+'/dwr/interface/OpticalWayAction.js');
Frame.grid.plugins.tbar.OpticalWayGridTbar = Ext
		.extend(
				Object,
				{

					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.OpticalWayGridTbar.superclass.constructor
								.call(this);
						return [ '-', {
							text : '光路路由图',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._method1
						},'-', {
							text : '光路完整路由',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._opticalWayRoute
						},'-',{
							text : '关联光缆纤芯信息',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._wireFiberInfoLists
						},'-',{
							text : '删除光路',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._deleteOpticalWayLists
						}];
					},
					_method1 : function() {
						 var records=this.getSelectionModel().getSelections();
						    if(Ext.isEmpty(records) || records.length==0){
						    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
						    	return;
						    }
							 var cuid=records[0].data['CUID'];	
							  var url = ctx+"/topo/index.do?code=OpticalWaySectionTopo&resId="+cuid+"&resType=OPTICAL_WAY&clientType=html5";
							 
							 FrameHelper.openUrl(url,'光路路由图-'+records[0].data['LABEL_CN']);
/*							 GetServiceParamAction.getUrlByServerName("TOPO",function(data){
								  var url = data+"/topo/index.do?code=OpticalWaySectionTopo&resId="+cuid+"&resType=OPTICAL_WAY&clientType=html5";
								  var win = new Ext.Window({
								   title : '光路路由图',
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
							 });*/	
					},
					_wireFiberInfoLists : function(){
						var records = this.getSelectionModel().getSelections();
						if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
							Ext.Msg.alert('温馨提示', '请选一条光路管理数据.');
							return;
						}
						var cuid = records[0].data['CUID'];
						var labelCn = records[0].data['LABEL_CN'];
						if (Ext.isEmpty(cuid)) {
							Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
							return;
						}
						var cfg= {openType : 'extwin',
							      width : 700,
							      height :400};
						var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_WIRE_FIBER_INFO&hasQuery=false';
						FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,
								labelCn + '关联光缆纤芯信息',null,cfg);
					},
					/**
					 * 删除光路
					 */
					_deleteOpticalWayLists : function(){
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
							msg:' 请确定是否要删除选中光路数据 ',
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
					//光路完整路由
					_opticalWayRoute : function(){
						var records = this.getSelectionModel().getSelections();
						if (Ext.isEmpty(records) || records.length == 0 || records.length > 1) {
							Ext.Msg.alert('温馨提示', '请选择一条光路数据.');
							return;
						}
						var makeFlag = records[0].data['MAKE_FLAG'];
						if(!Ext.isEmpty(records) && records.length > 0 ){
							var makeFlagId = makeFlag['CUID'];
							if(makeFlagId != 2){
								Ext.Msg.alert('温馨提示', '该光路不在核查成功状态,无法查看导出其完整路由.');
								return;
							}
						}
						
						var cuid = records[0].data['CUID'];
						var labelCn = records[0].data['LABEL_CN'];
						if (Ext.isEmpty(cuid)) {
							Ext.Msg.alert('系统错误', '当前选中数据,不包含必需字段,请检查配置');
							return;
						}
						var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_OPTICALWAY_ROUTE&hasQuery=false';
						FrameHelper.openUrl(ctx + url + '&cuid=' + cuid + '&labelCn=' + labelCn,'光路完整路由');
					}
					
			});
