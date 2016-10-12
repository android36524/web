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
		        		{name : 'TYPE'}]
		        })
			}),
			
			colModel : new Ext.grid.ColumnModel({
				defaults : {
					width : 390,
					shortable : false
				},
				columns : [
				{
					id : 'LABEL_CN',
					header : '名称',
					dataIndex : 'LABEL_CN'
				}, {
					id : 'TYPE',
					header : '类型',
					dataIndex : 'TYPE'
				}]
			})
		});
		return gridPanel;
	}
});