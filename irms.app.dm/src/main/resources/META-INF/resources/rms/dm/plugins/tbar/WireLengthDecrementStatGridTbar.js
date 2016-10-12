Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');

Frame.grid.plugins.tbar.WireLengthDecrementStatGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.WireLengthDecrementStatGridTbar.superclass.constructor.call(this);
		return [  		
				'-',  
				{
					text : '查看详细信息',
					iconCls : 'c_page_white_link',
					scope : this.grid,
					handler : this._showDetail
				}
	    ];
	},
	
	_showDetail : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1 || !records[0].data['CUID']){
			Ext.Msg.alert("温馨提示","请选择一条记录.");
			return;
		}
		var data = records[0].data;
		var cuid = data['CUID'];
		var districtName = data['DISTRICT_NAME']?data['DISTRICT_NAME']:"";
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_WIRE_DECREMENT_INFO&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,districtName + '-光缆减量详细信息');
	}
});

