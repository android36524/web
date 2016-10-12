Ext.namespace('Frame.grid.plugins.query');
Frame.grid.plugins.query.InterCableQueryPanel=Ext.extend(Object,{constructor:function(grid){
	this.grid=grid;
Frame.grid.plugins.query.InterCableQueryPanel.superclass.constructor.call(this);
var columnWidth=80;
var panel=new Ext.Panel({
	layout:'form',
	height:60,
	style:'margin:5 0 0 10'
		,
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
	        	  
	        	  xtype:'textfield',
	        	  fieldLabel:'电缆名称',
	        	  name:'LABEL_CN',
	        	    queryCfg:{
	        	    	type:"string",
	        	    	relation:'like',
						blurMatch : 'both'
	        	    }
	        	  }]},
	                  {
	        		  layout:'form',
	        		  columnWidth:.3,
	        		  defaults:{
	        			  anchor:'-20'
	        		  },
	        		  labelWidth:columnWidth,
	        		  items:[{
		  					xtype:'dmcombox',
						    fieldLabel:'A端机房',
						    name:'ORIG_POINT_CUID',
						    templateId: 'ROOM',
						    queryCfg:{
						    	  type:"string",
						    	  relation:"="
						    } }]
	        		  },
	        		  {
	        			  layout:'form',
		        		  columnWidth:.3,
		        		  defaults:{
		        			  anchor:'-20'
		        		  },
		        		  labelWidth:columnWidth,
		        		  items:[{
			  					xtype:'dmcombox',
							    fieldLabel:'Z端机房',
							    name:'DEST_POINT_CUID',
							    templateId: 'ROOM',
							    queryCfg:{
							    	  type:"string",
							    	  relation:"="
							    } }]
	        		  }]
	}]
});
return panel;
}
});       		  
