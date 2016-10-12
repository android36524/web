Ext.ns('Frame.grid.plugins.tbar');

$importjs(ctx+'/map/map-inc.js');

Frame.grid.plugins.tbar.DuctLineLengthStatGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.DuctLineLengthStatGridTbar.superclass.constructor.call(this);
		return [  '-',  {
							text : '查看县级结果',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._showCountyResult
						}, 
						'-', 
						{
							text : '查看问题明细',
							iconCls : 'c_page_white_link',
							scope : this.grid,
							handler : this._showQuestionDetail
						}
	    ];
	},
	
	_showCountyResult : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1 || !records[0].data['CUID']){
			Ext.Msg.alert("温馨提示","请选择一条记录.");
			return;
		}
		var data = records[0].data;
		var cuid = data['CUID'];
		var districtName = data['DISTRICT_NAME']?data['DISTRICT_NAME']:"";
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_DUCTLINE_LENGTH_STAT_COUNTY&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,districtName + '-县级统计情况');
	},
	
	_showQuestionDetail : function(){
		var records = this.getSelectionModel().getSelections();
		if(Ext.isEmpty(records) || records.length > 1 || !records[0].data['CUID']){
			Ext.Msg.alert("温馨提示","请选择一条记录.");
			return;
		}
		var data = records[0].data;
		var cuid = data['CUID'];
		var districtName = data['DISTRICT_NAME']?data['DISTRICT_NAME']:"";
		var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_FAULT_SEG&hasQuery=false';
		FrameHelper.openUrl(ctx + url + '&cuid=' + cuid,districtName + '-问题段情况');
	}
});

