Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/dwr/interface/WireStatAction.js');

Frame.grid.plugins.tbar.FaultSegGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.FaultSegGridTbar.superclass.constructor.call(this);
		return [  '-',  {
							text : '定位',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._doLocate
						}, 
						'-', 
						{
							text : '核减',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._doReduce
						}
	    ];
	},
	
	_doLocate : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert("温馨提示","请选择一条记录.");
			return;
		}
		var data = records[0].data;
		var cuid = data['SEG_CUID'];
		var scope = window;
		var explorer = undefined;
		for(var i = 0; i < 5; i++) {
			if(scope.Ext) {
				explorer = scope.Ext.getCmp("explorer_frame_panel");
				if(explorer) {
					explorer.windowScope = scope;
					break;
				}
			}
			scope = scope.parent;
		}
		
		if(explorer){
			var indexTab = explorer.centerPanel.items.items[0];
			explorer.centerPanel.activate(indexTab);
			var contentWindow = explorer.el.child('iframe').dom.contentWindow;
			contentWindow.Dms.Tools.graphicLocateOnMap(cuid);
		}
	},
	
	_doReduce : function(){
		var self = this;
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1){
			Ext.Msg.alert("温馨提示","请选择一条记录.");
			return;
		}
		var data = records[0].data;
		var cuid = data['CUID'];
		var tableName = cuid.split("-")[0];
		var isWire = tableName === "WIRE_SEG_FAULT";
		var checkState = data['CHECK_STATE'];
		var isSub = data['RESULT_TYPE'] === 2;
		if(checkState === "成功"){
			Ext.Msg.alert("温馨提示","成功状态不允许核减.");
			return;
		}else{
			Ext.Msg.confirm("温馨提示","确认要进行核减操作吗？",function(btn){
				if(btn == "yes"){
					WireStatAction.checkFaultSeg(cuid,isWire,isSub,{
						callback : function(result){
							if(data){
								if(result){
									self.doQuery();
								}
								return;
							}
						},
						errorHandler : function(error){
						},
						async : false
					});
				}else{
					return;
				}
			});
		}
	}
	
});

