Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx + "/jslib/ht/ht-all.js");
$importcss(ctx + "/rms/dm/fiberbox/JumpLinkMainView.css");
$importjs(ctx+'/dwr/interface/InterFiberLinkAction.js');
$importjs(ctx + "/rms/dm/fiberbox/InterWireFiberLinkView.js");


Frame.grid.plugins.tbar.InterFiberGridTbar = Ext
		.extend(
				Object,
				{
					constructor : function(grid) {
						this.grid = grid;
						Frame.grid.plugins.tbar.InterFiberGridTbar.superclass.constructor
								.call(this);
						return [ '-', {
							text : 'A端关联',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._regenerateOpticalsMethod
						}, '-',{
							text : 'Z端关联',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._opticalRoute
						} 
						];
					},
					_regenerateOpticalsMethod : function() {
						var records=this.getSelectionModel().getSelections();
					    if(Ext.isEmpty(records) || records.length==0){
					    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
					    	return;
					    }
					    var result; 
					    DWREngine.setAsync(false);
					    InterFiberLinkAction.queryARoom(this.cuid,function(datas){
					    	result=datas;
					    });  
					    DWREngine.setAsync(true); 
					    var cuid=result;
				        var interWireFiberLinkPanel = new Frame.op.InterWireFiberLinkPanel({
				        	cuid : result,
				        	bmClassId : 'ROOM',
				        });
				        var scope = this;
				        var win = WindowHelper.openExtWin(interWireFiberLinkPanel, {
				            title : '楼内纤芯上架',
				            width : 800,
				            height : 550,
				            buttons : [{
				                text : '关闭',
				                scope : this,
				                handler : function() {
				                    win.hide();
				                }
				            }]
				        });
				     
					},
					
					_opticalRoute: function() {
						var records=this.getSelectionModel().getSelections();
					    if(Ext.isEmpty(records) || records.length==0){
					    	Ext.Msg.alert('温馨提示','请选择要一条记录.');
					    	return;
					    }
					    var result; 
					    DWREngine.setAsync(false);
					    InterFiberLinkAction.queryZRoom(this.cuid,function(datas){
					    	result=datas;
					    });  
					    DWREngine.setAsync(true); 
					    var cuid=result;
				        var interWireFiberLinkPanel = new Frame.op.InterWireFiberLinkPanel({
				        	cuid : result,
				        	bmClassId : 'ROOM',
				        });
				        var scope = this;
				        var win = WindowHelper.openExtWin(interWireFiberLinkPanel, {
				            title : '楼内纤芯上架',
				            width : 800,
				            height : 550,
				            buttons : [{
				                text : '关闭',
				                scope : this,
				                handler : function() {
				                    win.hide();
				                }
				            }]
				        });
					},
			});
