Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx+'/dwr/interface/ProjectManageAction.js');
$importjs(ctx+'/rms/dm/plugins/queryform/HangWallQueryPanel.js');


Frame.grid.plugins.tbar.ProHangWallSegGridTbar = Ext
		.extend(
				Object,
				{
					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.ProHangWallSegGridTbar.superclass.constructor
								.call(this);
						return ['-',{
							text: '添加',
							iconCls: 'c_page_white_link',
							scope: this,
							handler: this._add
						},'-',{
							text: '删除',
							iconCls: 'c_page_white_link',
							scope: this,
							handler: this._delete						
						}
						];
					},
					_add : function(){
						var grid = new Frame.grid.MaintainGridPanel({
							hasMaintan:false,
							isTree:false,
							queryPlugin : 'HangWallQueryPanel',
							gridCfg : {
								cfgParams : {
									templateId : 'IRMS.RMS.HANG_WALL'
								},
								boName:'GridTemplateProxyBO'
							}
						});
						var scope = this;
						var win = WindowHelper.openExtWin(grid,
							{
								width : 650,
								height : 450,
								frame : true,
								buttons : [{
									text:'选中',handler : function() {
										this._select(grid);
										win.close();
									}
								,scope : this
								},{
									text:'取消',
									handler : function(){
										win.close();
									}
							}],
								scope : this
							});
					},
					_select : function(scope) {
						
						var hangwallsegGrid=this.grid;
						
						var projectCuid=this.grid.gridCfg.cfgParams.cuid;
						
						var selections=scope.grid.getSelectionModel().getSelections();
						
						if(selections==null || selections.length==0){
							Ext.Msg.alert('温馨提示','请选择至少一条记录');
						}

						var resources=[];
						for(var i=0;i<selections.length;i++){
							var data=selections[i].json;
							var res={};
							for(var key in data){
								var value=data[key];
								if(value!=null){
									if(typeof value=='object' && !Ext.isDate(value)){
										res[key]=(value.CUID==null)?null:value.CUID;
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
						
						var project={
							CUID: projectCuid
						};
						
						ProjectManageAction.updateRelatedProject(project,resources,{
			    		    callback: function(result){
			    		    	if(Ext.isEmpty(result)){
			    		    		Ext.Msg.alert('系统异常','调用服务器功能发生异常!');
			    		    	}
			    		    	hangwallsegGrid.fireEvent('metaloaded');
			    		    },
			    		    exceptionHandler: function exceptionHandler(exceptionString,exception){
			    		    	Ext.Msg.alert('系统异常',exceptionString);
			    		    }
			    	   });
						
					},
					_delete: function() {
					    var records=this.grid.getSelectionModel().getSelections();
					    if(Ext.isEmpty(records) || records.length==0){
					    	Ext.Msg.alert('温馨提示','请选择至少一条资源');
					    	return;
					    }
					    var scope=this.grid;
					    Ext.MessageBox.confirm('注意','您确定删除所选择的资源记录么?',function(btn,text){
					        if(btn=='yes'){
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
					    	   //增加DWR调用逻辑
					    	   ProjectManageAction.deleteRelatedProject(resources,{
					    		    callback: function(result){
					    		    	if(Ext.isEmpty(result)){
					    		    		Ext.Msg.alert('系统异常','调用服务器功能发生异常!');
					    		    	}
					    		    	scope.fireEvent('metaloaded');
					    		    },
					    		    exceptionHandler: function exceptionHandler(exceptionString,exception){
					    		    	Ext.Msg.alert('系统异常',exceptionString);
					    		    }
					    	   });
					        }					    
					   });					    
					}					
			});
