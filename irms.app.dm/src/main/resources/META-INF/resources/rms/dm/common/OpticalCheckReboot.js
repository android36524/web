Ext.ns('Frame.grid.edit');
$importjs(ctx+'/dwr/interface/OpticalCheckAction.js');

Frame.grid.edit.OpticalCheckReboot = Ext.extend(Ext.Panel, {
	constructor : function(config) {
		Frame.grid.edit.OpticalCheckReboot.superclass.constructor.call(this, config);
	},
	initComponent : function() {
		this._initView();
		Frame.grid.edit.OpticalCheckReboot.superclass.initComponent.call(this);
	},

	_initView : function() {
		this.items = [ this.taskInit() ];
		this.initCheck();
	},
	
	initCheck : function() {
		var scope = this;
		OpticalCheckAction.getOpticalCheckTaskByCuid({cuid : this.cuid},function(data){
			if(data!=null){
				scope.form.getForm().findField("taskName").setValue(scope.taskName); //任务说明
				scope.form.getForm().findField("gatheringDesc").setValue(data.TASK_REMARK); //任务说明
				scope.form.getForm().findField("district").setValue(data.RELATED_WIRE_SYSTEM_CUID);//区域
				scope.form.getForm().findField("taskWorkState").setValue(data.TASK_WORK_STATUE);//任务状态
				scope.form.getForm().findField("taskType").setValue(data.TASK_TYPE);//任务类型
				var taskCircle = data.TASK_CIRCLE;
				var taskStartWay = data.TASK_START_WAY;
				if(taskCircle==0 && taskStartWay==1){//立即执行
					scope.form.getForm().findField("exeType").setValue('0');//执行方式
					scope.form.getForm().findField("spinnerDate").hide();
					scope.form.getForm().findField("monthDoMonth").hide();
					scope.form.getForm().findField("monthDoDay").hide();
					scope.form.getForm().findField("taskWorkState").disable();
					scope.form.getForm().findField("gatheringCelue").disable();
					scope.form.getForm().findField("weekDo").hide();
					scope.form.getForm().findField("monthDo").hide();
					
				}else if(taskCircle==0 && taskStartWay==2){//定时执行
					scope.form.getForm().findField("exeType").setValue('1');//执行方式
					scope.form.getForm().findField("spinnerDate").setValue(data.timeRe);//时间
					scope.form.getForm().findField("monthDoDay").setValue(data.dayRe);//执行月号
					scope.form.getForm().findField("monthDoMonth").setValue(data.monthRe);//执行月份
					scope.form.getForm().findField("spinnerDate").show();
					scope.form.getForm().findField("monthDoMonth").show();
					scope.form.getForm().findField("monthDoDay").show();
					scope.form.getForm().findField("weekDo").hide();
					scope.form.getForm().findField("monthDo").hide();
					scope.form.getForm().findField("taskWorkState").enable();
					scope.form.getForm().findField("gatheringCelue").disable();
					
				}else if(taskCircle==1){//周期执行 每日
					scope.form.getForm().findField("exeType").setValue('2');//执行方式
					scope.form.getForm().findField("gatheringCelue").setValue('1');//任务频度
					scope.form.getForm().findField("spinnerDate").setValue(data.timeRe);//时间
					scope.form.getForm().findField("spinnerDate").show();
					scope.form.getForm().findField("monthDoMonth").hide();
					scope.form.getForm().findField("monthDoDay").hide();
					scope.form.getForm().findField("weekDo").hide();
					scope.form.getForm().findField("monthDo").hide();
					scope.form.getForm().findField("taskWorkState").enable();
					scope.form.getForm().findField("gatheringCelue").enable();
					
				}else if(taskCircle==2){//周期执行 每月
					scope.form.getForm().findField("exeType").setValue('2');//执行方式
					scope.form.getForm().findField("gatheringCelue").setValue('2');//任务频度
					scope.form.getForm().findField("spinnerDate").setValue(data.timeRe);//时间
					scope.form.getForm().findField("monthDo").setValue(data.TASK_CIRCLE_POINT);//执行月号(周期)
					scope.form.getForm().findField("spinnerDate").show();
					scope.form.getForm().findField("monthDoMonth").hide();
					scope.form.getForm().findField("monthDoDay").hide();
					scope.form.getForm().findField("weekDo").hide();
					scope.form.getForm().findField("monthDo").show();
    				scope.form.getForm().findField("taskWorkState").enable();
    				scope.form.getForm().findField("gatheringCelue").enable();
					
				}else if(taskCircle==3){//周期执行 每周
					scope.form.getForm().findField("exeType").setValue('2');//执行方式
					scope.form.getForm().findField("gatheringCelue").setValue('3');//任务频度
					scope.form.getForm().findField("spinnerDate").setValue(data.timeRe);//时间
					scope.form.getForm().findField("weekDo").setValue(data.TASK_CIRCLE_POINT);//执行星期(周期)
					scope.form.getForm().findField("spinnerDate").show();
					scope.form.getForm().findField("monthDoMonth").hide();
					scope.form.getForm().findField("monthDoDay").hide();
					scope.form.getForm().findField("weekDo").show();
    				scope.form.getForm().findField("monthDo").hide();
    				scope.form.getForm().findField("taskWorkState").enable();
    				scope.form.getForm().findField("gatheringCelue").enable();
					
				}else{
					scope.form.getForm().findField("spinnerDate").show();
					scope.form.getForm().findField("monthDoMonth").show();
					scope.form.getForm().findField("monthDoDay").show();
					scope.form.getForm().findField("taskWorkState").enable();
					scope.form.getForm().findField("gatheringCelue").disable();
					scope.form.getForm().findField("weekDo").hide();
					scope.form.getForm().findField("monthDo").hide();
				}
			}
		});
		
		
		
	},
	
	taskInit : function() {
		
		var labelWidth = 40;
		this.form = new Ext.form.FormPanel({
			border : false,
			style : 'margin:10px 5px 5px 10px',
			layout : 'form',
			items : [{
				border : false,
				hideBorders : true,
				items : [{
					layout : 'column',
					hideBorders : true,
					items : [{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'textfield',
							fieldLabel : '*任务名称',
							labelWidth : labelWidth,
							allowBlank : true,
							readOnly : false,
							name : 'taskName',
							id : 'taskName'
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype:'asyncombox',
						    fieldLabel:'执行方式',
						    labelWidth : labelWidth,
						    name:'exeType',
						    id:'exeType',
						    multSel:false,
						    minListWidth:200,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"OpticalTaskExeType"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		},
				    		listeners : {
				    			scope:this,
					    		select : function(combo,record,index) {
					    			if(combo.getValue()=='0'){//立即执行
					    				this.form.getForm().findField("spinnerDate").hide();
					    				this.form.getForm().findField("monthDoMonth").hide();
					    				this.form.getForm().findField("monthDoDay").hide();
					    				this.form.getForm().findField("weekDo").hide();
					    				this.form.getForm().findField("monthDo").hide();
					    				this.form.getForm().findField("taskWorkState").disable();
					    				this.form.getForm().findField("gatheringCelue").disable();
					    				this.form.getForm().findField("gatheringCelue").setValue();
					    			}else if(combo.getValue()=='2'){//周期执行
					    				this.form.getForm().findField("spinnerDate").show();
					    				this.form.getForm().findField("monthDoMonth").hide();
					    				this.form.getForm().findField("monthDoDay").hide();
					    				this.form.getForm().findField("weekDo").hide();
					    				this.form.getForm().findField("monthDo").hide();
					    				this.form.getForm().findField("taskWorkState").enable();
					    				this.form.getForm().findField("gatheringCelue").enable();
					    				this.form.getForm().findField("gatheringCelue").setValue();
					    			}else{//定时执行
					    				this.form.getForm().findField("spinnerDate").show();
					    				this.form.getForm().findField("monthDoMonth").show();
					    				this.form.getForm().findField("monthDoDay").show();
					    				this.form.getForm().findField("weekDo").hide();
					    				this.form.getForm().findField("monthDo").hide();
					    				this.form.getForm().findField("taskWorkState").enable();
					    				this.form.getForm().findField("gatheringCelue").disable();
					    				this.form.getForm().findField("gatheringCelue").setValue();
					    			}
								}
				    		}
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype:'asyncombox',
						    fieldLabel:'任务类型',
						    labelWidth : labelWidth,
						    name:'taskType',
						    id:'taskType',
						    multSel:false,
						    minListWidth:200,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"CheckTaskType"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						}]
					}]
				},{
					layout : 'column',
					hideBorders : true,
					items : [{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'textfield',
							fieldLabel : '任务说明',
							labelWidth : labelWidth,
							allowBlank : true,
							readOnly : false,
							name : 'gatheringDesc',
							id : 'gatheringDesc'
						}]
						
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'asyncombox',
							fieldLabel : '任务状态',
							labelWidth : labelWidth,
							name:'taskWorkState',
							id:'taskWorkState',
						    multSel:false,
						    minListWidth:200,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"OpticalTaskState"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'textfield',
							fieldLabel : '占位的',
							labelWidth : labelWidth,
							allowBlank : true,
							hidden : true,
							readOnly : true
						}]
					}]
				},{
					layout : 'column',
					hideBorders : true,
					items : [{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'asyncombox',
							fieldLabel : '区域',
							labelWidth : labelWidth,
							name:'district',
							id:'district',
						    multSel:false,
						    minListWidth:200,
						    value:'0',
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
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype:'asyncombox',
						    fieldLabel:'任务频度',
						    labelWidth : labelWidth,
						    name:'gatheringCelue',
						    id:'gatheringCelue',
						    multSel:false,
						    minListWidth:200,
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"Frequency"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		},
				    		listeners : {
				    			scope:this,
					    		select : function(combo,record,index) {
					    			if(combo.getValue()=='2'){//每周
					    				this.form.getForm().findField("spinnerDate").show();
					    				this.form.getForm().findField("monthDoMonth").hide();
					    				this.form.getForm().findField("monthDoDay").hide();
					    				this.form.getForm().findField("weekDo").show();
					    				this.form.getForm().findField("monthDo").hide();
					    			}else if(combo.getValue()=='3'){//每月
					    				this.form.getForm().findField("spinnerDate").show();
					    				this.form.getForm().findField("monthDoMonth").hide();
					    				this.form.getForm().findField("monthDoDay").hide();
					    				this.form.getForm().findField("weekDo").hide();
					    				this.form.getForm().findField("monthDo").show();
					    			}else{//每日
					    				this.form.getForm().findField("spinnerDate").show();
					    				this.form.getForm().findField("monthDoMonth").hide();
					    				this.form.getForm().findField("monthDoDay").hide();
					    				this.form.getForm().findField("weekDo").hide();
					    				this.form.getForm().findField("monthDo").hide();
					    			}
								}
				    		}
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'textfield',
							fieldLabel : '占位的',
							labelWidth : labelWidth,
							allowBlank : true,
							hidden : true,
							readOnly : true
						}]
					}]
				},{
					layout : 'column',
					hideBorders : true,
					items : [{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'asyncombox',
							fieldLabel : '时间',
							name:'spinnerDate',
							id:'spinnerDate',
						    multSel:false,
						    minListWidth:100,
						    value : '1:00',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"OpticalTaskTime"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .33,
						defaults : {
							anchor : '-30'
						},
						items : [{
							xtype : 'asyncombox',
							fieldLabel : '执行月份',
							name:'monthDoMonth',
							id:'monthDoMonth',
						    multSel:false,
						    minListWidth:100,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"CheckMonth"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						},{
							xtype : 'asyncombox',
							fieldLabel : '执行月号',
							name:'monthDo',
							id:'monthDo',
						    multSel:false,
						    minListWidth:100,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"CheckDate"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						},{
							xtype : 'asyncombox',
							fieldLabel : '执行星期',
							name:'weekDo',
							id:'weekDo',
						    multSel:false,
						    minListWidth:100,
						    value : '2',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"OpticalWeekTime"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						}]
					},{
						layout : 'form',
						labelWidth : 60,
						columnWidth : .34,
						defaults : {
							anchor : '-20'
						},
						items : [{
							xtype:'asyncombox',
						    fieldLabel:'执行月号',
						    name:'monthDoDay',
						    id:'monthDoDay',
						    multSel:false,
						    minListWidth:100,
						    value : '1',
						    comboxCfg :{
						    	boName:'EnumTemplateComboxBO',
						    	cfgParams:{
						    		code:"CheckDate"
				    			}
						    },
						    queryCfg:{
						    	type : "string",
						    	relation : "in"
				    		}
						}]
						
					}]
				}]
			}]
		});
		return this.form;
	},
	//保存操作
	_save : function() {
		var scope = this;
		this._dateCheck();
		var param = {
				cuid : this.cuid, //任务CUID
				taskName : this.taskName, //任务名称 
				gatheringDesc : this.form.getForm().findField("gatheringDesc").getValue(),	//任务说明 
				gatheringCelue : this.form.getForm().findField("gatheringCelue").getValue(),//任务频度
				exeType : this.form.getForm().findField("exeType").getValue(),				//执行方式
				district : this.form.getForm().findField("district").getValue(),			//区域CUID
				districtName : this.form.getForm().findField("district").lastSelectionText,	//区域名称
				taskWorkState : this.form.getForm().findField("taskWorkState").getValue(),	//任务状态
				taskType : this.form.getForm().findField("taskType").getValue(),			//任务类型
				spinnerDate : this.form.getForm().findField("spinnerDate").getValue(),		//时间
				monthDoMonth : this.form.getForm().findField("monthDoMonth").getValue(),	//月份
				monthDoDay : this.form.getForm().findField("monthDoDay").getValue(), 		//月号
				weekDo : this.form.getForm().findField("weekDo").getValue(),				//执行星期(频度每周)
				monthDo : this.form.getForm().findField("monthDo").getValue()				//执行月号
		};
//		OpticalCheckAction.OpticalCheckReboot(param,function(data){
//			if(data!=null && data=='0'){
//				Ext.Msg.alert('温馨提示', '重启光纤核查任务成功!');
//			}else if(data!=null && data!=""){
//				Ext.Msg.alert('温馨提示', data);
//			}
//		});
	},
	//清除操作
	_clean : function() {
		this.form.getForm().findField("taskName").setValue();  //任务名称 
		this.form.getForm().findField("gatheringDesc").setValue();  //任务说明 
		this.form.getForm().findField("gatheringCelue").setValue(); //任务频度
		this.form.getForm().findField("exeType").setValue(); 	    //执行方式
		this.form.getForm().findField("district").setValue("0"); 	//区域
		this.form.getForm().findField("taskWorkState").setValue();  //任务状态
		this.form.getForm().findField("taskType").setValue();		//任务类型
		this.form.getForm().findField("spinnerDate").setValue("1:00");//时间
		this.form.getForm().findField("monthDoMonth").setValue("1");//月份
		this.form.getForm().findField("monthDoDay").setValue("1");	//月号
		this.form.getForm().findField("spinnerDate").hide();		//时间
		this.form.getForm().findField("monthDoMonth").hide();   	//月份
		this.form.getForm().findField("monthDoDay").hide();			//月号
		this.form.getForm().findField("weekDo").setValue();			//执行星期
		this.form.getForm().findField("monthDo").setValue();		//执行月号
		this.form.getForm().findField("weekDo").hide();				//执行星期(频度每周)
		this.form.getForm().findField("monthDo").hide();			//执行月号
	},
	//数据校验
	_dateCheck : function() {
		if(this.form.getForm().findField("exeType").getValue()=='' || this.form.getForm().findField("exeType").getValue()==null){
			Ext.Msg.alert('温馨提示', '执行方式不能为空');
			return;
		}
		if(this.form.getForm().findField("taskType").getValue()=='' || this.form.getForm().findField("taskType").getValue()==null){
			Ext.Msg.alert('温馨提示', '任务类型不能为空');
			return;
		}
		if(this.form.getForm().findField("exeType").getValue()=='1'){
			if(this.form.getForm().findField("taskWorkState").getValue()=='' || this.form.getForm().findField("taskWorkState").getValue()==null){
				Ext.Msg.alert('温馨提示', '任务状态不能为空');
				return;
			}
		}else if(this.form.getForm().findField("exeType").getValue()=='2'){
			if(this.form.getForm().findField("taskWorkState").getValue()=='' || this.form.getForm().findField("taskWorkState").getValue()==null){
				Ext.Msg.alert('温馨提示', '任务状态不能为空');
				return;
			}
			if(this.form.getForm().findField("gatheringCelue").getValue()=='' || this.form.getForm().findField("gatheringCelue").getValue()==null){
				Ext.Msg.alert('温馨提示', '任务频度不能为空');
				return;
			}
		}
		
	}
});
