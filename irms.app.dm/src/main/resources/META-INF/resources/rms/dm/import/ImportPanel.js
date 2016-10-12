Ext.namespace('IRMS.dm');
$importjs(ctx+'/rms/common/FilePanel.js');
$importjs(ctx+'/rms/common/JumpFiberPanel.js');

IRMS.dm.ImportPanel = Ext.extend(Object, {
	constructor : function() {
		IRMS.dm.ImportPanel.superclass.constructor.call(this);
		var columnwidth = .25, anchor = '-20', labelwidth = 100;
		var panel = new Ext.Panel({  
	        layout:"form",  
	        height : 100,
	        labelAlign:"right",	        
	        border:false,  
	        frame:true,  
	        bodyStyle: 'padding:40px 0 50 90px;',
	        items: [{//第一行  
	            layout:"column", 
	            bodyStyle: 'padding:0px 0 40 0px;',
	            items:[{  
	                layout:"form",  
	                items:[{
	                	fieldLabel:"预留点导入", 
	                    name: 'contractId', 
	                    width:30
	                    }]  
	            },{  
	                layout:"form",  
	                defaults : {
						anchor : anchor
					},
	                items:[{  
	                    xtype:"button",  
	                    text:"模板下载",
	                    name: 'contractId1', 
	                    width:150,
	                    scope: this,
	                    handler: this._remainModel
	                    }]  
	            },{  
	                layout:"form",  
	                defaults : {
						anchor : anchor
					},
	                items:[{  
	                    xtype:"button",  
	        	        style: 'padding:0px 0 0 50px;',
	                    text: "导入",  
	                    name: 'contractId2',  
	                    width:290,
	                    scope: this,
	                    handler: this._remainImport
	                    }]  
	            },
	            {
		   	         items: [{ xtype: 'displayfield', value: '<hr width=850,height:1/>' }]
		   	    }
	        ]},//第一行结束  	            	            

	            {//第二行  
	            layout:"column",
	            bodyStyle: 'padding:0px 0 40 0px;',
	            items:[{  
	                layout:"form",  
	                defaults : {
						anchor : anchor
					},
					labelWidth : labelwidth,
	                items:[{  
	                	fieldLabel:"ODF导入",  
	                    name: 'contractId',  
	                    width:50  
	                    }]  
	            },{  	             
	                layout:"form",  
	                defaults : {
						anchor : anchor
					},
	                items:[{  
	                    xtype:"button",  
	                    text:"模板下载",
	                    name: 'contractId1',  
	                    width:150,
	                    scope: this,
	                    handler: this._ODFModel
	                   }]  
	           },{  
	               layout:"form",  
	                defaults : {
						anchor : anchor
					},
	               items:[{  
	                    xtype:"button",  
	        	        style: 'padding:0px 0 0 50px;',
	                    text: '导入',  
	                    name: 'contractId2',  
	                    width:290,
	                    scope: this,
	                    handler: this._ODFImport
	                   }]  
	           },
	            {
		   	         items: [{ xtype: 'displayfield', value: '<hr width=850,height:1/>' }]
		   	    }
	           ]},//第二行结束  
	            {//第三行  
		            layout:"column",
		            bodyStyle: 'padding:0px 0 40 0px;',
		            items:[{  
		                layout:"form",  
		                defaults : {
							anchor : anchor
						},
		                items:[{  
		                	fieldLabel:"经纬度导入",  
		                    name: 'contractId',  
		                    width:50  
		                    }]  
		            },{  
		                layout:"form",  
		                defaults : {
							anchor : anchor
						},
		                items:[{  
		                    xtype:"button",  
		                    text:"模板下载",
		                    name: 'contractId1',  
		                    width:150,
		                    scope: this,
		                    handler: this._LONGModel
		                   }]  
		           },{  
		               layout:"form",  
		                defaults : {
							anchor : anchor
						},
		               items:[{  
		                    xtype:"button",  
		                    style: 'padding:0px 0 0 50px;',
		                    text: '导入',  
		                    name: 'contractId2',  
		                    width: 290,
		                    scope: this,
		                    handler: this._LONGImport
		                   }]  
		           },
		            {
			   	         items: [{ xtype: 'displayfield', value: '<hr width=850,height:1/>' }]
			   	    }		           
		           ]},//第三行结束 
		           {//第四行  
			            layout:"column",
			            bodyStyle: 'padding:0px 0 40 0px;',
			            items:[{  
			                layout:"form",  
			                defaults : {
								anchor : anchor
							},
			                items:[{  
			                	fieldLabel:"POS归属关系导入",  
			                    name: 'contractId',  
			                    width:50  
			                    }]  
			            },{  
			                layout:"form",  
			                defaults : {
								anchor : anchor
							},
			                items:[{  
			                    xtype:"button",  
			                    text:"模板下载",
			                    name: 'contractId1',  
			                    width:150,
			                    scope: this,
			                    handler: this._PosModel
			                   }]  
			           },{  
			               layout:"form",  
			                defaults : {
								anchor : anchor
							},
			               items:[{  
			                    xtype:"button",  
			                    style: 'padding:0px 0 0 50px;',
			                    text: '导入',  
			                    name: 'contractId2',  
			                    width: 290,
			                    scope: this,
			                    handler: this._PosImport
			                   }]  
			           },
			            {
				   	         items: [{ xtype: 'displayfield', value: '<hr width=850,height:1/>' }]
				   	    }		           
			           ]},//第四行结束
			           {//第五行  
				            layout:"column",
				            bodyStyle: 'padding:0px 0 40 0px;',
				            items:[{  
				                layout:"form",  
				                defaults : {
									anchor : anchor
								},
				                items:[{  
				                	fieldLabel:"ONU归属关系导入",  
				                    name: 'contractId',  
				                    width:50  
				                    }]  
				            },{  
				                layout:"form",  
				                defaults : {
									anchor : anchor
								},
				                items:[{  
				                    xtype:"button",  
				                    text:"模板下载",
				                    name: 'contractId1',  
				                    width:150,
				                    scope: this,
				                    handler: this._OnuModel
				                   }]  
				           },{  
				               layout:"form",  
				                defaults : {
									anchor : anchor
								},
				               items:[{  
				                    xtype:"button",  
				                    style: 'padding:0px 0 0 50px;',
				                    text: '导入',  
				                    name: 'contractId2',  
				                    width: 290,
				                    scope: this,
				                    handler: this._OnuImport
				                   }]  
				           },
				            {
					   	         items: [{ xtype: 'displayfield', value: '<hr width=850,height:1/>' }]
					   	    }		           
				           ]},//第五行结束 
		            {//第六行  
			            layout:"form",
			            xtype:'fieldset',
			            bodyStyle: 'padding:0px 0 40 0px;',
			            width: 850,
			            title:'跳纤纤芯',
			            items:[
			              {layout:'column',
			               items:[{
			            	   layout:'form',
					           labelWidth:220,	             
				               width:260,
			            	   defaults:{
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            		   fieldLabel:"ODF端子与端子跳纤导入"
			            	   }]
			               },{
			            	   layout:"form",
			            	   width:200,
			            	   defaults:{
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            	      xtype:'button',
			            	      text:"模板下载",
			            	      scope: this,
			            	      handler: this._ODFPortModel
			            	   }]
			               },{
			            	   layout:'form',
			            	   defaults : {
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            		   xtype: "button",
			            		   style: 'padding:0px 0 0 50px;',
			            		   text: '导入',
			            		   width: 290,
			            		   scope: this,
			            		   handler: this._ODFPortImport
			            	   }]
			               },
			          
			               ]
			            	   
			            	   
			               
			            },
			              {
			            	layout:'column',
			            	items:[{
			            	   layout:'form',
					           labelWidth:220,	             
				               width:260,
			            	   defaults:{
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            		   fieldLabel:"ODF端子与光缆纤芯连接关系导入"
			            	   }]
			               },{
			            	   layout:"form",
			            	   width:200,
			            	   defaults:{
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            	      xtype:'button',
			            	      text:"模板下载",
			            	      scope: this,
			            	      handler: this._ODFWireModel
			            	   }]
			               },{
			            	   layout:'form',
			            	   defaults : {
			            		   anchor : anchor
			            	   },
			            	   items:[{
			            		   xtype: "button",
			            		   style: 'padding:0px 0 0 50px;',
			            		   text: '导入',
			            		   width: 290,
			            		   scope: this,
			            		   handler: this._ODFWireImport
			            	   }]
			               },
			          
			               ]
			            	},
			              {
			            		layout:'column',
				            	items:[{
				            	   layout:'form',
						           labelWidth:220,	             
					               width:260,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   fieldLabel:"ODF架端子光纤链接属性导入"
				            	   }]
				               },{
				            	   layout:"form",
				            	   width:200,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            	      xtype:'button',
				            	      text:"模板下载",
				            	      scope: this,
				            	      handler: this._ODFFiberModel
				            	   }]
				               },{
				            	   layout:'form',
				            	   defaults : {
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   xtype: "button",
				            		   style: 'padding:0px 0 0 50px;',
				            		   text: '导入',
				            		   width: 290,
				            		   scope: this,
				            		   handler: this._ODFFiberImport
				            	   }]
				               },
				          
				               ]
			              },
			              {
			            		layout:'column',
				            	items:[{
				            	   layout:'form',
						           labelWidth:220,	             
					               width:260,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   fieldLabel:"接头盒纤芯接续导入"
				            	   }]
				               },{
				            	   layout:"form",
				            	   width:200,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            	      xtype:'button',
				            	      text:"模板下载",
				            	      scope: this,
				            	      handler: this._JointBoxFiberModel
				            	   }]
				               },{
				            	   layout:'form',
				            	   defaults : {
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   xtype: "button",
				            		   style: 'padding:0px 0 0 50px;',
				            		   text: '导入',
				            		   width: 290,
				            		   scope: this,
				            		   handler: this._JointBoxFiberImport
				            	   }]
				               },
				          
				               ]
			              },
			              {
			            		layout:'column',
				            	items:[{
				            	   layout:'form',
						           labelWidth:220,	             
					               width:260,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   fieldLabel:"光交接箱、分纤箱跳纤关系导入"
				            	   }]
				               },{
				            	   layout:"form",
				            	   width:200,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            	      xtype:'button',
				            	      text:"模板下载",
				            	      scope: this,
				            	      handler: this._FiberCabFiberDpModel
				            	   }]
				               },{
				            	   layout:'form',
				            	   defaults : {
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   xtype: "button",
				            		   style: 'padding:0px 0 0 50px;',
				            		   text: '导入',
				            		   width: 290,
				            		   scope: this,
				            		   handler: this._FiberCabFiberDpImport
				            	   }]
				               },
				          
				               ]
			              },
			              {
			            		layout:'column',
				            	items:[{
				            	   layout:'form',
						           labelWidth:220,	             
					               width:260,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   fieldLabel:"光交接箱、分纤箱与内置pos跳纤关系导入"
				            	   }]
				               },{
				            	   layout:"form",
				            	   width:200,
				            	   defaults:{
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            	      xtype:'button',
				            	      text:"模板下载",
				            	      scope: this,
				            	      handler: this._FiberCabFiberDpModel
				            	   }]
				               },{
				            	   layout:'form',
				            	   defaults : {
				            		   anchor : anchor
				            	   },
				            	   items:[{
				            		   xtype: "button",
				            		   style: 'padding:0px 0 0 50px;',
				            		   text: '导入',
				            		   width: 290,
				            		   scope: this,
				            		   handler: this._FiberCabFiberDpPosImport
				            	   }]
				               },
				          
				               ]
			              }
			               ]
			            
			           },
	                ]  
	            }); 
      
		return panel;		
	},
	    _remainModel:function(){
 	         var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("预留点"));
 	         window.open(url);
               },
        _remainImport:function(){
			 var winCfg={
			 width:600,
			 height:200
			   };
			this.filePanel=new IRMS.dm.FilePanel({
				title: '导入',
				width: 100,
				height:20,
				inputParams : this.inputParams,
				key:'WIRE_REMAIN'
				});
				var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
		      },

	    _ODFModel:function(){
	        	   var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("ODF导入"));
	        	   window.open(url);
	           },
	    _ODFImport:function(){
					var winCfg={
					width:600,
					height:200
					};
					this.filePanel=new IRMS.dm.FilePanel({
						title: '导入',
						width: 100,
						height:20,
						inputParams : this.inputParams,
						key:'ODF'
						});
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
				},
		 _LONGModel:function(){ 
		        	   var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("经纬度数据"));
		        	   window.open(url);
		           },
		 _LONGImport:function(){
						var winCfg={
						width:600,
						height:200
						};
						this.filePanel=new IRMS.dm.FilePanel({
							title: '导入',
							width: 100,
							height:20,
							inputParams : this.inputParams,
							key:'LongtitudeLatitude'
							});
							var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
					},	
			_ODFPortModel:function(){
	 	         var url = ctx+"/dm/modelexport.do?name="+encodeURI(encodeURI("DDF(ODF)端子与端子跳线（跳纤）"));
	 	         window.open(url);
	               },
            _ODFPortImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'ODFPortImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			      },
	        _ODFWireModel:function(){
	 	         var url = ctx+"/dm/modelexport.do?name="+encodeURI(encodeURI("ODF端子与光缆纤芯连接关系导入"));
	 	         window.open(url);
	               },
            _ODFWireImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'ODFWireImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			      },
	        _ODFFiberModel:function(){
	 	         var url = ctx+"/dm/modelexport.do?name="+encodeURI(encodeURI("ODF架端子光纤链接属性(备注信息)"));
	 	         window.open(url);
	               },
            _ODFFiberImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'ODFFiberImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			      },
	       _JointBoxFiberModel:function(){
	 	         var url = ctx+"/dm/modelexport.do?name="+encodeURI(encodeURI("接头盒纤芯接续导入"));
	 	         window.open(url);
	               },
           _JointBoxFiberImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'JointBoxFiberImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			      },
	      _FiberCabFiberDpModel:function(){
	 	         var url = ctx+"/dm/modelexport.do?name="+encodeURI(encodeURI("光交接箱、分纤箱跳纤关系导入"));
	 	         window.open(url);
	               },
          _FiberCabFiberDpImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'FiberCabFiberDpImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			  },
		  _FiberCabFiberDpPosImport:function(){
				 var winCfg={
				 width:600,
				 height:200
				   };
				this.filePanel=new IRMS.dm.JumpFiberPanel({
					title: '导入',
					width: 100,
					height:20,
					inputParams : this.inputParams,
					key:'FiberCabFiberDpPosImport'
					});
					var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
			  },
			  _PosModel:function(){ 
	        	   var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("POS归属关系"));
	        	   window.open(url);
	           },
	          _PosImport:function(){
					var winCfg={
					width:600,
					height:200
					};
					this.filePanel=new IRMS.dm.FilePanel({
						title: '导入',
						width: 100,
						height:20,
						inputParams : this.inputParams,
						key:'AN_POS'
						});
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
				},
			  _OnuModel:function(){ 
	        	   var url = ctx+"/dm/export.do?name="+encodeURI(encodeURI("ONU归属关系"));
	        	   window.open(url);
	           },
	          _OnuImport:function(){
					var winCfg={
					width:600,
					height:200
					};
					this.filePanel=new IRMS.dm.FilePanel({
						title: '导入',
						width: 100,
						height:20,
						inputParams : this.inputParams,
						key:'AN_ONU'
						});
						var fileWin=WindowHelper.openExtWin(this.filePanel,winCfg);	
				}
});
