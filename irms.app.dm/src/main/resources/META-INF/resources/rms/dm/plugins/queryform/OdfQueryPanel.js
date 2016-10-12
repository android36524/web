Ext.namespace('Frame.grid.plugins.query');
Frame.grid.plugins.query.OdfQueryPanel=Ext.extend(Object,{constructor:function(grid){
	this.grid=grid;
	Frame.grid.plugins.query.OdfQueryPanel.superclass.constructor.call(this);
	var columnWidth=80;
	var panel=new Ext.Panel({
		layout:'form',
		height:80,
		style : 'margin:5 0 0 10',
		items:[{
			layout:'column',
			items:[{
		        layout:'form',
		        columnWidth:.3,
		        defaults:{
					anchor:'-20'
				},
				labelWidth:columnWidth,
		        items:[{
					xtype:'dmcombox',
				    fieldLabel:'所属机房',
				    name:'RELATED_ROOM_CUID',
				    templateId: 'DM_ROOM',
				    queryCfg:{
				    	  type:"string",
				    	  relation:"="
				    } }]
				},{
					layout:'form',
					columnWidth:.3,
					defaults:{
						anchor:'-20'
					},
					labelWidth: columnWidth,
				
				    items:[{
					   xtype:'textfield',
					   fieldLabel:'名称',
					   name:'LABEL_CN',					
					   queryCfg:{
						type:"string",
						relation:'like',
						blurMatch : 'both'
				}}]
		}]
		  }]
		
	});
	return panel;
	}
});
	
