Ext.ns('Frame.grid.plugins.tbar');
$importjs(ctx + "/commons/utils/FrameHelper.js");
$importjs(ctx+'/rms/dm/plugins/queryform/ProplanForm.js');
$importjs(ctx + "/commons/utils/WindowHelper.js");

$importjs(ctx + "/jsp_space/maintain/districts/form/AccessPointForm.js");
$importjs(ctx + "/jsp_space/maintain/districts/form/RoomForm.js");
$importjs(ctx + "/dwr/interface/AccessPointDwrAction.js");

$importjs(ctx + "/jslib/ext/ux/fileuploadfield/FileUploadField.js");
$importjs(ctx + "/jsp_space/maintain/excel/ExportModel.js");
$importjs(ctx + "/jsp_space/maintain/excel/ExcelImport.js");
$importjs(ctx + "/jslib/ext/ux/statusbar/StatusBar.js");
$importjs(ctx + "/dwr/interface/ExcelAction.js");
$importjs(ctx + "/jsp_space/maintain/excel/GridDataExport.js");
Frame.grid.plugins.tbar.ProjectPlanPeriodGridTbar = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.tbar.ProjectPlanPeriodGridTbar.superclass.constructor
				.call(this);
		return [{
			text : '新增',
			iconCls : 'c_drive_add',
			scope : this,
			handler : this.addDistrict
		},{
			text : '修改',
			iconCls : 'c_drive_edit',
			scope : this,
			handler : this.edit
		},{
			text : '删除',
			iconCls : 'c_drive_delete',
			scope : this,
			handler : this.del
		}
		
		];
	},
	addDistrict:function(){},
	edit : function() {},
	
	del: function (){},
	

	
});