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
		        		{name : 'LABEL_CN'},
		        		{name : 'NO'},
		        		{name : 'OPERATOR'},
		        		{name : 'OPERATION_NAME'},
		        		{name : 'DESCRIPTION'},
		        		{name : 'OPERATION_DATE'}
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
					header : '项目名称',
					dataIndex : 'LABEL_CN'
				}, {//枚举类型
					id : 'NO',
					header : '项目编号',
					dataIndex : 'NO'
				}, {
					id : 'OPERATOR',
					header : '操作员账号',
					dataIndex : 'OPERATOR'
				}, {
					id : 'OPERATION_NAME',
					header : '项目状态',
					dataIndex : 'OPERATION_NAME'
				}, {
					id : 'DESCRIPTION',
					header : '说明',
					dataIndex : 'DESCRIPTION'
				}, {
					id : 'OPERATION_DATE',
					header : '操作时间',
					dataIndex : 'OPERATION_DATE'
				}]
			})
		});
		return gridPanel;
	}
});