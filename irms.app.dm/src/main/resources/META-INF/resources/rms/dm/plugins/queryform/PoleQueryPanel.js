Ext.namespace('Frame.grid.plugins.query');

Frame.grid.plugins.query.PoleQueryPanel = Ext.extend(Object, {
	constructor : function(grid) {
		this.grid = grid;
		Frame.grid.plugins.query.PoleQueryPanel.superclass.constructor.call(this);
		var columnWidth = 80;
		
		var panel = new Ext.Panel({
			layout : 'form',
			height : 135,
			style : 'margin:5 0 0 5',
			items : [ {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '电杆名称',
						name : 'LABEL_CN',
						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '产权所属',
						name : 'OWNERSHIP',
						multSel : true,
						
						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMOwnerShip"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '电杆类型',
						name : 'POLE_KIND',
						multSel : true,
						
						
						comboxCfg : {
							boName : 'EnumTemplateComboxBO',
							cfgParams : {
								code : "DMPoleKind"
							}
						},
						queryCfg : {
							type : "string",
							relation : "in"
						}
					} ]
				} ]
			}, {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '维护单位',
						name : 'MAINT_DEP',
												
						queryCfg : {
							type : "string",
							relation : "like",
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '所属区域',
						name : 'RELATED_DISTRICT_CUID',
						comboxCfg : {
							cfgParams : {
								code : "DMDF.DISTRICT"
							}
						},
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'right'								
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						name : 'MAINT_MODE',
						fieldLabel : '维护方式',
						multSel: false,
						
						comboxCfg: {
      	    			  boName: 'EnumTemplateComboxBO',
      	    			  cfgParams: {
      	    				  code: "DMMaintMode"
      	    			  }
      	    		  },    	        	    		  
      	    		  queryCfg: {
      	    			  type: "string",
      	    			  relation:"in"
      	    		  }
					} ]
				} ]
			}, {
				layout : 'column',
				items : [ {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'textfield',
						fieldLabel : '所有权人',
						name : 'RES_OWNER',
						
						queryCfg : {
							type : "string",
							relation : 'like',
							blurMatch : 'both'
						}
					} ]
				}, {
					layout : 'form',
					columnWidth : .33,
					defaults : {
						anchor : '-20'
					},
					labelWidth : columnWidth,
					items : [ {
						xtype : 'asyncombox',
						fieldLabel : '用途',
						name : 'PURPOSE',
						multSel: false,
					   comboxCfg: {
      	    			  boName: 'EnumTemplateComboxBO',
      	    			  cfgParams: {
      	    				  code: "DMPurpose"
      	    			  }
      	    		   },    	        	    		  
      	    		   queryCfg: {
      	    			  type: "string",
      	    			  relation:"in"
      	    		   }
					} ]
				} ]
			} ]
		});
		return panel;
	}
});
