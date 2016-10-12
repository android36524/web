Ext.ns('Ext.form');

Ext.form.formPamel = Ext.extend(Ext.form.FormPanel, {
	constructor : function() {
		var gridPanel = new Ext.grid.GridPanel({
			border : false,
			frame : false,
			region : 'center',
			
			store : new Ext.data.Store({
				autoDestroy: true,
		        reader: new Ext.data.DataReader({
		        	fields : [
		        		{name : 'LABEL_CN'},          {name : 'MAKE_FLAG'},
		        		{name : 'ORIG_SITE_CUID'},    {name : 'ORIG_ROOM_CUID'},
		        		{name : 'ORIG_EQP_CUID'},     {name : 'ORIG_POINT_CUID'},
		        		{name : 'DEST_SITE_CUID'},    {name : 'DEST_ROOM_CUID'},
		        		{name : 'DEST_EQP_CUID'},     {name : 'DEST_POINT_CUID'},
		        		{name : 'ROUTE_DESCIPTION'},  {name : 'OWNERSHIP'},
		        		{name : 'FIBER_LEVEL'},       {name : 'FIBER_STATE'},
		        		{name : 'USER_NAME'},         {name : 'FIBER_TYPE'},
		        		{name : 'FIBER_DIR'},         {name : 'PURPOSE'},
		        		{name : 'LENGTH'},            {name : 'SUM_ATTENU_1310'},
		        		{name : 'SUM_ATTENU_1550'},   {name : 'APPLY_TYPE'}
		        	]
		        })
			}),
			
			colModel : new Ext.grid.ColumnModel({
				defaults : {
					width : 120,
					shortable : false
				},
				columns : [
				{
					id : 'LABEL_CN',
					header : '光纤名称',
					dataIndex : 'LABEL_CN'
				}, {//枚举类型
					id : 'MAKE_FLAG',
					header : '核查状态',
					dataIndex : 'MAKE_FLAG'
				}, {
					id : 'ORIG_SITE_CUID',
					header : '起点站点',
					dataIndex : 'ORIG_SITE_CUID'
				}, {
					id : 'ORIG_ROOM_CUID',
					header : '起点机房',
					dataIndex : 'ORIG_ROOM_CUID'
				}, {
					id : 'ORIG_EQP_CUID',
					header : '起点设备',
					dataIndex : 'ORIG_EQP_CUID'
				}, {
					id : 'ORIG_POINT_CUID',
					header : '起点端子',
					dataIndex : 'ORIG_POINT_CUID'
				}, {
					id : 'DEST_SITE_CUID',
					header : '终点站点',
					dataIndex : 'DEST_SITE_CUID'
				}, {
					id : 'DEST_ROOM_CUID',
					header : '终点机房',
					dataIndex : 'DEST_ROOM_CUID'
				}, {
					id : 'DEST_EQP_CUID',
					header : '终点设备',
					dataIndex : 'DEST_EQP_CUID'
				}, {
					id : 'DEST_POINT_CUID',
					header : '终点端子',
					dataIndex : 'DEST_POINT_CUID'
				}, {
					id : 'ROUTE_DESCIPTION',
					header : '路由信息',
					dataIndex : 'ROUTE_DESCIPTION'
				}, {
					id : 'OWNERSHIP',
					header : '产权归属',
					dataIndex : 'OWNERSHIP'
				}, {
					id : 'FIBER_LEVEL',
					header : '级别',
					dataIndex : 'FIBER_LEVEL'
				}, {
					id : 'FIBER_STATE',
					header : '使用状态',
					dataIndex : 'FIBER_STATE'
				}, {
					id : 'USER_NAME',
					header : '用户名',
					dataIndex : 'USER_NAME'
				}, {
					id : 'FIBER_TYPE',
					header : '类型',
					dataIndex : 'FIBER_TYPE'
				}, {
					id : 'FIBER_DIR',
					header : '方向',
					dataIndex : 'FIBER_DIR'
				}, {
					id : 'PURPOSE',
					header : '用途',
					dataIndex : 'PURPOSE'
				}, {
					id : 'LENGTH',
					header : '总长度(M)',
					dataIndex : 'LENGTH'
				}, {
					id : 'SUM_ATTENU_1310',
					header : '1310总衰耗',
					dataIndex : 'SUM_ATTENU_1310'
				}, {
					id : 'SUM_ATTENU_1550',
					header : '1550总衰耗',
					dataIndex : 'SUM_ATTENU_1550'
				},{
					id : 'APPLY_TYPE',
					header : '适用类型',
					dataIndex : 'APPLY_TYPE'
				}]
			})
		});
		return gridPanel;
	}
});