
Dms.Default.contextMenu ={
		'ADD_LINE':[
		{
	        label: "完成",
	        action: function(){
	        	Dms.Tools.drawLineComplete();
	        },
	        scope: "" 
	    },
	    "separator", //分割线
	    {
	        label: "使用已有点",
	        type: 'check', //单选菜单项
	        action: function(item,event){
	        	if(item && item.selected)
	        		tp.Default.DrawObject._reuseMapPoint = true;
	        	else
	        		tp.Default.DrawObject._reuseMapPoint = false;
	        },
	        selected:true,
	        scope: "" 
	    },
	    "separator", //分割线
	    {
	        label: "新增类型",
	        items: [
	            {
	                label: "人手井",
	                icon: '',
	                type: 'radio', //单选菜单项
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'MANHLE';
	                },
	                groupId: 1 //菜单项分组
	            },
	            {
	                label: "电杆",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'POLE';
	                },
	                groupId: 1
	            },
	            {
	                label: "标石",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'STONE';
	                },
	                groupId: 1
	            },
	            {
	                label: "拐点",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'INFLEXION';
	                },
	                groupId: 1
	            },
	            {
	                label: "接入点",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'ACCESSPOINT';
	                },
	                groupId: 1
	            },
	            {
	                label: "光交接箱",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_CAB';
	                },
	                groupId: 1
	            },
	            {
	                label: "光分纤箱",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_DP';
	                },
	                groupId: 1
	            },
	            {
	                label: "光接头盒",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_JOINT_BOX';
	                	tp.Default.DrawObject._kind = '1';
	                },
	                groupId: 1
	            },
	            {
	                label: "光终端盒",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_JOINT_BOX';
	                	tp.Default.DrawObject._kind = '2';
	                },
	                groupId: 1
	            },
	            {
	                label: "分光器",
	                icon: '',
	                type: 'radio',
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'AN_POS';
	                },
	                groupId: 1
	            }
	        ]
	    },
	    {
	        label: "批量命名",
	        action:Dms.batchName,
	        icon: '',
	        items: [
	            {
	                label: "选中点", 
	            },
	             "separator", //分割线
	            {
	                label: "人手井",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'MANHLE';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "电杆",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'POLE';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "标石",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'STONE';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "拐点",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'INFLEXION';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "接入点",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'ACCESSPOINT';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "光交接箱",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_CAB';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "光分纤箱",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_DP';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "光接头盒",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_JOINT_BOX';
	                	tp.Default.DrawObject._kind = '1';
	                	Dms.Tools.batchName();
	                }
	            },
	            {
	                label: "光终端盒",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'FIBER_JOINT_BOX';
	                	tp.Default.DrawObject._kind = '2';
	                	Dms.Tools.batchName();
	                }
	            
	            },
	            {
	                label: "分光器",
	                action:function(){
	                	tp.Default.DrawObject._drawPointClass = 'AN_POS';
	                	Dms.Tools.batchName();
	                }
	            
	            }
	            
	        ]
	    },
	    "separator", //分割线
	    {
	        label: "撤销",
	        action: Dms.Tools.editCancel,
	        scope: "" 
	    },
	    {
	        label: "放弃",
	        action:function(){
	        	Dms.Default.tpmap.reset();
	        },
	        scope: "" 
	    }
	],
	'ADD_POINT':[
		{
	        label: "完成",
	        action: function(){
	        	Dms.Tools.drawPointComplete();
	        },
	        scope: "" 
	    },
	    {
	        label: "批量命名",
	        action:function(){
	        	Dms.Tools.batchName();
	        },
	        icon: ''
	    },
	    "separator", //分割线
	    {
	        label: "放弃",
	        action: function(){
	        	Dms.Default.tpmap.reset();
	        },
	        scope: "" 
	    }
	],
	'LINE_CONTEXTMENU':[
		{label: "直接插入点",action:function(e){Dms.Tools.insertPointDirect(e);},scope: "" },
	    "separator", //分割线
	    {label: "等分插入点",action:function(e){Dms.Tools.insertPointAliquots(e);},icon: ''},
	    "separator", //分割线
	    {label: "等距插入点",action: function (e){Dms.Tools.insertPointIsometric(e);},scope: "" }
	],	
	'SELECT_WIRE_SEG_ROUTE':[
	{
	    label: "管道",
	    action: function(e){
	    	var ductDm = this._dm;
//	    	var wireSegcuid = tp.Default.OperateObject.contextObject.cuid;
	    	dms.openDuctSystemQueryPanel("IRMS.RMS.DUCT_SYSTEM","SystemGridTemplateProxyBO",'tp-querySystemForm',ductDm);
	    }
	},{
	    label: "杆路",
	    action: function(e){
	    	var poleWayDm = this._dm;
	    	dms.openDuctSystemQueryPanel("IRMS.RMS.POLEWAY_SYSTEM","SystemGridTemplateProxyBO",'tp-querySystemForm',poleWayDm);
	    },
	},{
		        label: "标石路由",
		        action: function(e){
			    	var stoneWayDm = this._dm;
			    	dms.openDuctSystemQueryPanel("IRMS.RMS.STONEWAY_SYSTEM","SystemGridTemplateProxyBO",'tp-querySystemForm',stoneWayDm);
			    },
		    },{
		        label: "引上",
		        action: function(e){
			    	var upLineDm = this._dm;
			    	dms.openDuctSystemQueryPanel("IRMS.RMS.UP_LINE","SystemGridTemplateProxyBO",'tp-querySystemForm',upLineDm);
			    },
		    },{
		        label: "挂墙",
		        action: function(e){
			    	var hangWallDm = this._dm;
			    	dms.openDuctSystemQueryPanel("IRMS.RMS.HANG_WALL","SystemGridTemplateProxyBO",'tp-querySystemForm',hangWallDm);
			    },
		    }
	 ],  	 
  	'WIRE_SEG_RIGHT_MENU'://光缆段右键功能
  		[{
  			icon : ctx +"/resources/map/menu/LIST_LAY.png",
 	        label: "敷设光缆",
 	        action: function(e){
 	        	//menu_090;
 	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
 	        	Dms.Tools.wireToLayDuctLine(cuid);
 	        },
 	        scope: "" 
 	    },{
 	    	icon : ctx +"/resources/map/menu/GRAPH_LAY.png",
  	        label: "图形化敷设",
  	        action: function(e){
  	        //menu_091;
  	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
  	        	var panel = dms.glaywire.createGlayWireSystemPane(cuid);
  	        	dms.Default.GlayWireSystemPane = panel;
  	        	tp.Default.DrawObject._drawState = 0;
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/QUICK_LAY.png",
  	        label: "快速敷设光缆",
  	        action: function(e){
  	        //menu_092;
  	        	tp.Default.DrawObject._drawState = 201;
  	        	alert("请选择承载段后，右键完成！");
  	         },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/FAULT.png",
	        label: "光缆故障定位",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.wireFailure.createSelectWirePane(cuid);
	         },
	        scope: "" 
	    },
  	  
//  	    {
//  	        label: "光缆段拆分?",
//  	        action: function(e){
//  	        //menu_093;
//  	         },
//  	        scope: "" 
//  	    },
	    {
	    	icon : ctx +"/resources/map/menu/LOADING_OBJECT.png",
	    	label: "承载对象",
            action: function(e){
        	    var viewParam={
 	        	     'cuid':tp.Default.OperateObject.contextObject.cuid,
 	        	     'labelCn':tp.Default.OperateObject.contextObject.label,
 	        	     'mapName':"光缆经过的承载对象查询"
 	        	};
        	    var wireRemainMainView = new WirePassListPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	        	viewParam.content=wireRemainMainView;
  	            tp.utils.showDialogView(viewParam);
            },
        scope: "" 
  	    },
//        	{
//  	        label: "光缆纤芯利用率统计?",
//  	        action: function(e){
//  	        //menu_096;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "光缆纤芯关联传输系统分析?",
//  	        action: function(e){
//  	        //menu_097;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "修改线设施?",
//  	        action: function(e){
//  	        //menu_098;
//  	         },
//  	        scope: "" 
//  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
  	        label: "拆除承载",
  	        action: function(e){
  	        //menu_099;
  	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
  	        	Dms.Tools.doDeleteLayedRelation(cuid);
  	         },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/DUCT_REMOVE.png",
  	        label: "通过段拆除承载",
  	        action: function(e){
  	        //menu_100;
  	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
  	        	Dms.Tools.deleteDuctLine(cuid);
  	         },
  	         scope: "" 
	  	 },{
	  		icon : ctx +"/resources/map/menu/SPLIT.png",
   	        label: "光缆段拆分",
  	        action: function(e){
  	        	dms.split.splitTools.splitseg('FIBER_JOINT_BOX');
  	         },
  	         scope: "" 
	  	 },{
	  		icon : ctx +"/resources/map/menu/LEFT_FIBER.png",
	        label: "左端纤芯上架",
	        action: function(e){
	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
	        	//当前光缆段起点
	        	new dms.fiberLinkViewPanel(cuid,'SITE',false,"ORIG_POINT_CUID").show();
	        	
	         },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/RIGHT_FIBER.png",
	        label: "右端纤芯上架",
	        action: function(e){
	        	var cuid=tp.Default.OperateObject.contextObject.cuid;
	        	//当前光缆段起点
	        	new dms.fiberLinkViewPanel(cuid,'SITE',false,"DEST_POINT_CUID").show();
	        	
	         },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
	    	label: "复制名称",
	    	action: function(e){
	    		var name=tp.Default.OperateObject.contextObject.label;
	    		Ext.Msg.alert('设备名称', name);
	    	},
	    	scope: "" 
	    }
	  		 /*, {//暂时此处加，用来测试
		      label: "光缆模板管理",
		      action: function(e){
		      //menu_101;
		    	  dms.template.createTemplatePanel();
		       },
		      scope: "" 
	  	    }*/
//  	 , {
//  	        label: "纤芯管理?",
//  	        action: function(e){
//  	        //menu_101;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "右端纤芯上架?",
//  	        action: function(e){
//  	        //menu_103;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "自动搜索路由?",
//  	        action: function(e){
//  	        //menu_104;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "光缆成端割接?",
//  	        action: function(e){
//  	        //menu_105;
//  	         },
//  	        scope: "" 
//  	    }
  	    ],
  	 'WIRE_SYSTEM_RIGHT_MENU'://光缆系统右键功能
    		[{
    			icon : ctx +"/resources/map/menu/LOADING_OBJECT.png",
    	        label: "承载对象",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"光缆经过的承载对象查询"
         	        	};
    	        	var wireRemainMainView = new WirePassListPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    
    		},
    		{
    			icon : ctx +"/resources/map/menu/SECTION.png",
    	        label: "纤芯接续图",
    	        action: function(e){
     	        	var viewParam={
 	       	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
 	       	        	   'mapType':"WireSystemLinkSectionTopo",
 	       	        	   'mapName':"纤芯接续图"
 	       	        	};
 	    	        Dms.Tools.showSectionView(viewParam);
    	        },
    	  	    scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/REMAIN.png",
    	        label: "光缆预留信息",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"光缆预留信息"
         	        	};
    	        	var wireRemainMainView = new createWireRemainPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE.png",
    	        label: "光缆路由图",
    	        action: function(e){
    	        	var viewParam={
    	     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
    	     	        	   'mapType':"WireRouteSectionTopo",
    	     	        	   'mapName':"光缆具体路由图"
    	     	    };
    	      	    Dms.Tools.showSectionView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
    	        label: "路由管理",
    	        action: function(e){
    	        	var viewParam={
    	        		   'cuid':tp.Default.OperateObject.contextObject.cuid,
           	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
          	        	   'mapName':"光缆系统路由管理"
          	        	};
 	   	        	var url = '/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.DM_WIRE_SEG&hasQuery=false';
 	   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
 	   	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName );
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/FIBER_MANAGE.png",
    	        label: "纤芯管理",
    	        action: function(e){
    	        	var viewParam={
     	        		   'cuid':tp.Default.OperateObject.contextObject.cuid,
            	           'labelCn':tp.Default.OperateObject.contextObject.label,
           	        	   'mapName':"光缆系统纤芯管理"
           	        	};
    	        	var url = '/rms/dm/wire/fibermanagepanel.jsp?x=x&type=SYSTEM&key=DUCT_BRANCH';
    	        	url=ctx + url + '&cuid=' + viewParam.cuid;
    	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
    	        	//'目前只支持单条光缆系统的纤芯管理.'
    	        	//window.open(url, "", "");
    	         },
    	        scope: "" 
    	    },
//    	    {
//    	        label: "直通接头信息?",
//    	        action: function(e){
//    	        //menu_117;
//    	         },
//    	        scope: "" 
//    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/FAULT.png",
    	        label: "光缆故障定位",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	dms.wireFailure.createSelectWirePane(cuid);
    	         },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/MODIFY.png",
    	        label: "修改线设施",
    	        action: function(e){
    	        	Dms.Tools.openModifyExtWin();
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
    	        label: "删除线设施",
    	        action: function(e){
//    	        var cuid = tp.Default.OperateObject.contextObject.cuid;
//      	        	var array = new Array();
//      	        	array.push(cuid);
//      	        	Dms.Tools.deleteSystemsAction(array);
//      	        	new dms.deleteResourcePanel("光缆删除操作","解除敷设关系、删除光缆、纤芯及其承载的光纤？").show();
    	        	new dms.deleteLinesPanel("光缆删除操作","请确认是否解除光缆敷设关系，同时删除其关联的纤芯和承载的光纤？").show();
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
    	        label: "拆除承载",
    	        action: function(e){
    	        //menu_121;
    	        	//系统 用systemCuid
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.doDeleteLayedRelation(cuid);
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "光缆段合并",
    	        action: function(e){
    	        	dms.merge.mergeTools.mergeSeg(false);
    	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
      	        label: "附件管理",
      	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
      	        action: function(e) {
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var filePanel = new IRMS.dm.common.FilePanel({
      	        		title : '附件管理',
    			    	width : 100,
    			    	height : 350,
    			    	relatedServiceCuid : cuid,
    			    	type : 1,
    			    	readOnly : true,
    			    	preview : false
    			    });

      	        	var winCfg = {
      	      			width : 700,
      	      			height : 350
      	      		};
      	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
      	        },
      	        scope: "" 
      	    }*/
    	   ],  	    
  	'DUCT_SEG_RIGHT_MENU'://管道段右键功能
 		[   {
 				icon : ctx +"/resources/map/menu/SECTION.png",
     	        label: "截面图",
     	        action: function(e){
     	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'mapType':"DuctSectionTopo",
     	        	   'mapName':"管道截面图",
     	        	   'singleProjectCuid': Dms.Default.scene,
     	        	   'segGroupCuid' : Dms.Default.segGroupCuid
     	        	};
      	        	Dms.Tools.showSectionView(viewParam);
     	        },
     	        scope: "" 
     	    },
      	    {
     	    	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doDMCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/SPLIT.png",
      	        label: "管道段拆分",
      	        action: function(e){
      	        	dms.split.splitTools.splitseg('MANHLE',false);
      	         },
      	        scope: "" 
      	    }
//      	    {
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_005;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	        //menu_006;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	        //menu_007;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "修改线设施?",
//      	        action: function(e){
//      	        //menu_008;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "管孔子孔利用率统计?",
//      	        action: function(e){
//      	        //menu_009;
//      	         },
//      	        scope: "" 
//      	    },
//    	        {
//      	        label: "管道段长度准确性分析?",
//      	        action: function(e){
//      	        //menu_010;
//      	         },
//      	        scope: "" 
//      	    }
      	    ,{
      	    icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    }
    	    ],
  	'DUCT_BRANCH_RIGHT_MENU': //管道分支右键功能
 		 [
// 		  {
//     	        label: "路由管理?",
//     	        action: function(e){
//     	        	;
//     	        },
//     	        scope: "" 
//     	  },
          {
        	   icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
      	       label: "查询敷设光缆",
               action: function(e){
            	   var viewParam={
            			   'cuid':tp.Default.OperateObject.contextObject.cuid,
             			   'labelCn':tp.Default.OperateObject.contextObject.label,
            			   'mapName':"查询承载对象敷设光缆"
            	   };
            	   var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	               viewParam.content=wireRemainMainView;
                   tp.utils.showDialogView(viewParam);
               },
               scope: "" 
      	  },
      	  {
      		   icon : ctx +"/resources/map/menu/REMOVE.png",
      	       label: "拆除敷设",
      	       action: function(e){
      	    	   var cuid = tp.Default.OperateObject.contextObject.cuid;
      	    	   Dms.Tools.deleteLayRelationAction(cuid);
      	       },
      	       scope: "" 
      	   },
     	   {
      		    icon : ctx +"/resources/map/menu/COMBIN.png",
     	        label: "管道段合并",
     	        action: function(e){
     	        	dms.merge.mergeTools.mergeSeg(false);
       	        },
     	        scope: "" 
     	    },{
     	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            //Ext.Msg.alert('设备名称', name);
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
//      	    ,{
//      	        label: "拆分管道分支?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_005;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "修改线设施?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "管孔子孔利用率统计?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "管道段长度准确性分析?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    }
      	    ],
      	  'DUCT_SYSTEM_RIGHT_MENU'://管道系统右键功能
      		   [
      	           {
      	        	    icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
      	    	        label: "查询敷设光缆",
      	    	        action: function(e){
      	    	        	var viewParam={
      	         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
      	         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
      	         	        	   'mapName':"查询承载对象敷设光缆"
      	         	        	};
      	    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
      		   	        	viewParam.content=wireRemainMainView;
      		      	        tp.utils.showDialogView(viewParam);
      	    	        },
      	    	        scope: "" 
      	    	    },
//      	          {
//      		        label: "查看光缆?",
//      		        action: function(e){
//      		        //menu_124;
//      		        },
//      		        scope: "" 
//      		        },
      	    	    {
      	    	    	icon : ctx +"/resources/map/menu/ROUTE.png",
      	    	        label: "管道路由图",
      	    	        action: function(e){
      	    	        	var viewParam={
      	     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
      	     	        	   'mapType':"DuctRouteSectionTopo",
      	     	        	   'mapName':"管道路由图"
      	     	        	};
      	      	        	Dms.Tools.showSectionView(viewParam);
      	    	         },
      	    	        scope: "" 
      	    	    },{
      	    	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
      	    	        label: "路由管理",
      	    	        action: function(e){
      	    	        //menu_126;
      	    	        	var viewParam={
      	    	        		   'cuid':tp.Default.OperateObject.contextObject.cuid,
      	           	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
      	         	        	   'mapName':"管道系统路由管理"
      	         	        	};
      		   	        	var url =  '/rms/dm/ductseg/ductsegpanel.jsp?x=x&type=SYSTEM&key=DUCT_BRANCH';
      		   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
      		   	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);	        	
      	    	         },
      	    	        scope: "" 
      	    	    },
      	    	    {
      	    	    	icon : ctx +"/resources/map/menu/MODIFY.png",
      	    	        label: "修改线设施",
      	    	        action: function(e){
      	    	        	Dms.Tools.openModifyExtWin();
      	    	         },
      	    	        scope: "" 
      	    	    },{
      	    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	    	        label: "删除线设施",
      	    	        action: function(e){
      	      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	      	        	var array = new Array();
      	      	        	array.push(cuid);
      	      	        	Dms.Tools.deleteSystemsAction(array);
      	    	         },
      	    	        scope: "" 
      	    	    },
//      	    	    {
//      	    	        label: "查询管线长度?",
//      	    	        action: function(e){
//      	    	        //menu_129;
//      	    	         },
//      	    	        scope: "" 
//      	    	    },
      	    	    {
      	    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
      	    	        label: "计算长度",
      	    	        action: function(e){
      	    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	    	        	Dms.Tools.doCalculateSystemLengthAction(cuid);
      	    	        },
      	    	        scope: "" 
      	    	    },
//      	    	    {
//      	    	        label: "管孔子孔利用率统计?",
//      	    	        action: function(e){
//      	    	        //menu_131;
//      	    	         },
//      	    	        scope: "" 
//      	    	    },{
//      	    	        label: "管道段长度准确性分析?",
//      	    	        action: function(e){
//      	    	        //menu_132;
//      	    	         },
//      	    	        scope: "" 
//      	    	    },
      	    	    {
      	    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	    	        label: "拆除敷设",
      	    	        action: function(e){
      	    	        	//系统 用systemCuid
      	      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	    	         },
      	    	        scope: "" 
      	    	    },
      	      	    {
      	    	    	icon : ctx +"/resources/map/menu/COMBIN.png",
      	      	        label: "管道分支合并",
      	      	        action: function(e){
      	      	        	dms.merge.mergeBranchTools.mergeBranch(false);
      	        	        },
      	      	        scope: "" 
      	      	    },
      	    	    {
      	    	        label: "查询",
      	    	        items: [
      							{
      							    label: "查询管线长度",
      							    action: function(e){
      							    	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
      							    	new dms.queryLineLengthPanel(systemCuid).show();
      							    },
      							    scope: "" 
      							},{
      		    	    	        label: "查询包含管道分支",
      		    	    	        action: function(e){
      		    	    	        	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
      		    	    	        	new dms.querDuctLineBranchPanel(systemCuid).show();
      		    	    	         },
      		    	    	        scope: "" 
      		    	    	    }
      	    	        ]
      	    	    },{
      	    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	      	        label: "复制名称",
      	      	        action: function(e){
      	      	        //menu_091;
      	      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	      	            var name=tp.Default.OperateObject.contextObject.label;
      	      	            Ext.Msg.alert('设备名称', name);
      	      	        },
      	      	        scope: "" 
      	      	    },{
      	    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
      	    	        label: "查看图片",
      	    	        action: function(e){
      	    	        	var viewParam={
      	         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
      	         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
      	         	        	   'mapName':"查看图片",
      	         	        	   'widthScale' : 0.6,
      	         	        	   'hightScale' : 0.6
      	         	        	};
      	    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
      		   	        	viewParam.content=resImagsPanel;
      		      	        tp.utils.showDialogView(viewParam);
      	    	        },
      	    	        scope: "" 
      	    	    }/*,{
      	       	        label: "附件管理",
      	       	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
      	       	        action: function(e) {
      	       	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	       	        	var filePanel = new IRMS.dm.common.FilePanel({
      	       	        		title : '附件管理',
      	     			    	width : 100,
      	     			    	height : 350,
      	     			    	relatedServiceCuid : cuid,
      	     			    	type : 1,
      	     			    	readOnly : true,
      	     			    	preview : false
      	     			    });

      	       	        	var winCfg = {
      	       	      			width : 700,
      	       	      			height : 350
      	       	      		};
      	       	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
      	       	        },
      	       	        scope: "" 
      	       	    }*/
      	    	    ],
    'POLE_SEG_RIGHT_MENU': //杆路段右键功能
	   	[  
	   	   {
	   		icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
   	        label: "查询敷设光缆",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查询承载对象敷设光缆"
     	        	};
	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=wireRemainMainView;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	      },
    	    {
	    	    icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doDMCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
  	    {
    	    icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "拆除敷设",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	Dms.Tools.deleteLayRelationAction(cuid);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/SPLIT.png",
  	        label: "杆路段拆分",
  	        action: function(e){
  	        	dms.split.splitTools.splitseg('POLE',false);
  	         },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    }
//  	    ,{
//	        label: "杆路段拆分?",
//  	        action: function(e){
//  	        //menu_012;
//  	        },
//  	        scope: "" 
//  	    },{
//	        label: "图形化承载新增光缆段?",
//  	        action: function(e){
//  	        //menu_013;
//  	        },
//  	        scope: "" 
//  	    },{
//  	        label: "图形化承载已有光缆段（自动）?",
//  	        action: function(e){
//  	        //menu_014;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "图形化承载已有光缆段（手动）?",
//  	        action: function(e){
//  	        //menu_015;
//  	         },
//  	        scope: "" 
//  	    },{
//  	        label: "修改线设施?",
//  	        action: function(e){
//  	        //menu_016;
//  	         },
//  	        scope: "" 
//  	    }
  	    ],
  	'POLE_BRANCH_RIGHT_MENU'://杆路分支右键功能
 		 [ 
// 		   {
//      	        label: "查询承载对象敷设光缆?",
//      	        action: function(e){
//      	        },
//      	        scope: "" 
//      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },
     	    {
      	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "杆路段合并",
    	        action: function(e){
    	        	dms.merge.mergeTools.mergeSeg(false);
      	        },
    	        scope: ""
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
//      	    ,{
//      	        label: "修改线设施?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },
//      	    {
//      	        label: "拆分杆路分支?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_005;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    }
      	    ],  	    
  	'POLE_SYSTEM_RIGHT_MENU'://杆路系统右键功能
	   [ 	
//	     	{
//    	        label: "查看光缆?",
//    	        action: function(e){
//    	        //menu_136
//    	        },
//    	        scope: "" 
//    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE.png",
    	        label: "杆路路由图",
    	        action: function(e){
    	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'mapType':"DuctRouteSectionTopo",
     	        	   'mapName':"杆路路由图"
     	        	};
      	        	Dms.Tools.showSectionView(viewParam);
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
    	        label: "路由管理",
    	        action: function(e){
    	        	var viewParam={
          	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
          	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
          	        	   'mapName':"杆路系统路由管理"
          	        	};
 	   	        	var url = '/rms/dm/poleway/polewaypanel.jsp?x=x&type=SYSTEM&key=POLEWAY_BRANCH';
 	   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
 	   	            FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/MODIFY.png",
    	        label: "修改线设施",
    	        action: function(e){
    	        	Dms.Tools.openModifyExtWin();
    	         },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
    	        label: "删除线设施",
    	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var array = new Array();
      	        	array.push(cuid);
      	        	Dms.Tools.deleteSystemsAction(array);
    	         },
    	        scope: "" 
    	    },
//    	    {
//    	        label: "查询管线长度?",
//    	        action: function(e){
//    	        //menu_141;
//    	         },
//    	        scope: "" 
//    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
    	        label: "拆除敷设",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
    	         },
    	        scope: "" 
    	    },
     	    {
    	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	    	label: "杆路分支合并",
   	            action: function(e){
   	            	dms.merge.mergeBranchTools.mergeBranch(false);
     	        },
   	            scope: "" 
   	        },{
    	        label: "查询管线长度",
    	        action: function(e){
    	        	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
    	        	new dms.queryLineLengthPanel(systemCuid).show();
    	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
      	        label: "附件管理",
      	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
      	        action: function(e) {
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var filePanel = new IRMS.dm.common.FilePanel({
      	        		title : '附件管理',
    			    	width : 100,
    			    	height : 350,
    			    	relatedServiceCuid : cuid,
    			    	type : 1,
    			    	readOnly : true,
    			    	preview : false
    			    });

      	        	var winCfg = {
      	      			width : 700,
      	      			height : 350
      	      		};
      	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
      	        },
      	        scope: "" 
      	    }*/
    	    ],    
   'SITE_RIGHT_MENU': //站点右键功能
	  [  {
		    icon : ctx +"/resources/map/menu/SITE_SECTION.png",
	        label: "剖面图",
  	        action: function(e){
  	        	var viewParam={
  	        			'cuid':tp.Default.OperateObject.contextObject.cuid,
  	        			'mapType':"SiteSectionTopo",
  	        			'mapName':"站点剖面图",
  	        			'singleProjectCuid': Dms.Default.scene,
  	        			'segGroupCuid' : Dms.Default.segGroupCuid
  	        	};
   	        	Dms.Tools.showSectionView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/FAULT.png",
	        label: "光缆故障定位",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.wireFailure.createSelectWirePane(cuid);
	        },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/SITE_MOVE.png",
	        label: "站点移动",
	        action: function(e){
	        	dms.movepoint.movePoint();
	        },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/ACCESS_WIRESEG.png",
	        label: "通达分析",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.sitewireanalysis.getSiteWireAnalysis(cuid);
	        },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/
//	  ,{
//	        label: "局向光纤查询分析?",
//  	        action: function(e){
//  	        //menu_107;
//  	        },
//  	        scope: "" 
//  	    },{
//	        label: "局向光缆纤芯查询?",
//  	        action: function(e){
//  	        //menu_108;
//  	        },
//  	        scope: "" 
//  	    },{
//  	        label: "重置点设施位置?",
//  	        action: function(e){
//  	        //menu_110;
//  	         },
//  	        scope: "" 
//  	    }
  	    ],
  	'STONE_BRANCH_RIGHT_MENU': //标石路由（直埋）分支右键功能
 		 [{
 			 	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "直埋段合并",
    	        action: function(e){
    	        	dms.merge.mergeTools.mergeSeg(false);
      	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
// 		 ,{
//      	        label: "修改线设施?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "拆分标石分支?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_005;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	         },
//      	        scope: "" 
//      	    }
      	    ],  	    
    'STONE_SEG_RIGHT_MENU'://标石路由（直埋）段右键功能
  		[
            {
            	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doDMCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
  	  	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
  		        label: "拆除敷设",
  	  	        action: function(e){
  	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	  	        	Dms.Tools.deleteLayRelationAction(cuid);
  	  	        },
  	  	        scope: "" 
  	  	    },{
  	  	    	icon : ctx +"/resources/map/menu/SPLIT.png",
      	        label: "直埋段拆分",
      	        action: function(e){
      	        	dms.split.splitTools.splitseg('STONE',false);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
//  	  	    ,{
//  		        label: "图形化承载新增光缆段?",
//  	  	        action: function(e){
//  	  	        //menu_020;
//  	  	        },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（自动）?",
//  	  	        action: function(e){
//  	  	        //menu_021;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（手动）?",
//  	  	        action: function(e){
//  	  	        //menu_022;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "修改线设施?",
//  	  	        action: function(e){
//  	  	        //menu_023;
//  	  	         },
//  	  	        scope: "" 
//  	  	    }
  	  	    ],
  	'STONE_SYSTEM_RIGHT_MENU'://标石路由（直埋）系统右键功能
  	   [
   	       {
   	    	    icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
//    	    {
//	  	        label: "查看光缆?",
//	  	        action: function(e){
//	  	        //menu_146
//  	        },
//  	        scope: "" 
//      	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE.png",
      	        label: "直埋路由图",
      	        action: function(e){
    	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'mapType':"DuctRouteSectionTopo",
     	        	   'mapName':"直埋路由图"
     	        	};
      	        	Dms.Tools.showSectionView(viewParam);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
      	        label: "路由管理",
      	        action: function(e){
    	        	var viewParam={
    	        		   'cuid':tp.Default.OperateObject.contextObject.cuid,
           	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
           	        	   'mapName':"标石系统路由管理"
           	        	};
  	   	        	var url = '/rms/dm/stoneway/stonewaypanel.jsp?x=x&type=SYSTEM&key=STONEWAY_BRANCH';
  	   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
  	   	            FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/MODIFY.png",
      	        label: "修改线设施",
      	        action: function(e){
      	        	Dms.Tools.openModifyExtWin();
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "删除线设施",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var array = new Array();
      	        	array.push(cuid);
      	        	Dms.Tools.deleteSystemsAction(array);
      	         },
      	        scope: "" 
      	    },
//      	    {
//      	        label: "查询管线长度?",
//      	        action: function(e){
//      	        //menu_151;
//      	         },
//      	        scope: "" 
//      	    },
    	    {
      	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        //系统 用systemCuid
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "直埋分支合并",
    	        action: function(e){
    	        	dms.merge.mergeBranchTools.mergeBranch(false);
      	        },
    	        scope: "" 
    	    },{
    	        label: "查询管线长度",
    	        action: function(e){
    	        	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
    	        	new dms.queryLineLengthPanel(systemCuid).show();
    	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
       	        label: "附件管理",
       	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
       	        action: function(e) {
       	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
       	        	var filePanel = new IRMS.dm.common.FilePanel({
       	        		title : '附件管理',
     			    	width : 100,
     			    	height : 350,
     			    	relatedServiceCuid : cuid,
     			    	type : 1,
     			    	readOnly : true,
     			    	preview : false
     			    });

       	        	var winCfg = {
       	      			width : 700,
       	      			height : 350
       	      		};
       	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
       	        },
       	        scope: "" 
       	    }*/
      	    ],  	    
  	'UPLINE_SEG_RIGHT_MENU': //引上段右键功能
  		[
   	         {
   	        	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doDMCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
  	  	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
  		        label: "拆除敷设",
  	  	        action: function(e){
  	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	  	        	Dms.Tools.deleteLayRelationAction(cuid);
  	  	        },
  	  	        scope: "" 
  	  	    },{
  	  	    	icon : ctx +"/resources/map/menu/SPLIT.png",
      	        label: "引上段拆分",
      	        action: function(e){
      	        	dms.split.splitTools.splitseg('INFLEXION',false);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
//  	  	    ,{
//  		        label: "图形化承载新增光缆段?",
//  	  	        action: function(e){
//  	  	        //menu_027;
//  	  	        },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（自动）?",
//  	  	        action: function(e){
//  	  	        //menu_028;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（手动）?",
//  	  	        action: function(e){
//  	  	        //menu_029;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "修改线设施?",
//  	  	        action: function(e){
//  	  	        //menu_030;
//  	  	         },
//  	  	        scope: "" 
//  	  	    }
  	  	],
    'UPLINE_SYSTEM_RIGHT_MENU'://引上右键功能
  	   [ {
  		   		icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
//    	    {
//	  	        label: "查看光缆?",
//	  	        action: function(e){
//	  	        //menu_156
//	  	        },
//  	        	scope: "" 
//      	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
      	        label: "路由管理",
      	        action: function(e){
    	        	var viewParam={
    	        		'cuid':tp.Default.OperateObject.contextObject.cuid,
           	        	 'labelCn':tp.Default.OperateObject.contextObject.label,
            	         'mapName':"引上系统路由管理"
            	       };
   	   	        	var url = '/rms/dm/upline/uplinepanel.jsp?x=x&type=SEG&key=UP_LINE';
   	   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
   	   	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/MODIFY.png",
      	        label: "修改线设施",
      	        action: function(e){
      	        	Dms.Tools.openModifyExtWin();
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "删除线设施",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var array = new Array();
      	        	array.push(cuid);
      	        	Dms.Tools.deleteSystemsAction(array);
      	         },
      	        scope: "" 
      	    },
    	    {
      	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
//      	    {
//      	        label: "查询管线长度?",
//      	        action: function(e){
//      	        //menu_160;
//      	         },
//      	        scope: "" 
//      	    }{
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_163;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	        //menu_164;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	        //menu_165;
//      	         },
//      	        scope: "" 
//      	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "引上段合并",
    	        action: function(e){
    	        	dms.merge.mergeTools.mergeSeg(false);
      	        },
    	        scope: "" 
    	    },{
    	        label: "查询管线长度",
    	        action: function(e){
    	        	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
    	        	new dms.queryLineLengthPanel(systemCuid).show();
    	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
       	        label: "附件管理",
       	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
       	        action: function(e) {
       	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
       	        	var filePanel = new IRMS.dm.common.FilePanel({
       	        		title : '附件管理',
     			    	width : 100,
     			    	height : 350,
     			    	relatedServiceCuid : cuid,
     			    	type : 1,
     			    	readOnly : true,
     			    	preview : false
     			    });

       	        	var winCfg = {
       	      			width : 700,
       	      			height : 350
       	      		};
       	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
       	        },
       	        scope: "" 
       	    }*/],   	
  	'HANGWALL_SEG_RIGHT_MENU': //挂墙段右键功能
  		[
   	         {
   	        	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doDMCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
  	  	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
  		        label: "拆除敷设",
  	  	        action: function(e){
  	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	  	        	Dms.Tools.deleteLayRelationAction(cuid);
  	  	        },
  	  	        scope: "" 
  	  	    },{
  	  	    	icon : ctx +"/resources/map/menu/SPLIT.png",
      	        label: "挂墙段拆分",
      	        action: function(e){
      	        	dms.split.splitTools.splitseg('INFLEXION',false);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    }
//  	  	    ,{
//  		        label: "图形化承载新增光缆段?",
//  	  	        action: function(e){
//  	  	        //menu_034;
//  	  	        },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（自动）?",
//  	  	        action: function(e){
//  	  	        //menu_035;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "图形化承载已有光缆段（手动）?",
//  	  	        action: function(e){
//  	  	        //menu_036;
//  	  	         },
//  	  	        scope: "" 
//  	  	    },{
//  	  	        label: "修改线设施?",
//  	  	        action: function(e){
//  	  	        //menu_037;
//  	  	         },
//  	  	        scope: "" 
//  	  	    }
  	  	],  	  	    
   'HANGWALL_SYSTEM_RIGHT_MENU'://挂墙右键功能
  	   [
//  	       {
//	  	        label: "查看光缆?",
//	  	        action: function(e){
//	  	        //menu_168
//	  	        },
//  	        	scope: "" 
//      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/LAY_WIRESEG.png",
    	        label: "查询敷设光缆",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查询承载对象敷设光缆"
         	        	};
    	        	var wireRemainMainView = new createLayingWireListPanelHT({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=wireRemainMainView;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    },
    	    {
    	    	icon : ctx +"/resources/map/menu/ROUTE_MANAGE.png",
      	        label: "路由管理",
      	        action: function(e){
    	        	var viewParam={
    	        		   'cuid':tp.Default.OperateObject.contextObject.cuid,
           	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"挂墙系统路由管理"
         	        	};
	   	        	var url = '/rms/dm/hangwall/hangwallpanel.jsp?x=x&type=SEG&key=HANG_WALL';
	   	        	url=ctx + url + '&cuid=' + viewParam.cuid;
	   	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/MODIFY.png",
      	        label: "修改线设施",
      	        action: function(e){
      	        	Dms.Tools.openModifyExtWin();
      	         },
      	        scope: "" 
      	    },{
      	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "删除线设施",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	var array = new Array();
      	        	array.push(cuid);
      	        	Dms.Tools.deleteSystemsAction(array);
      	         },
      	        scope: "" 
      	    },
    	    {
      	    	icon : ctx +"/resources/map/menu/CALCULATE.png",
    	        label: "计算长度",
    	        action: function(e){
    	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
    	        	Dms.Tools.doCalculateSystemLengthAction(cuid);
    	        },
    	        scope: "" 
    	    },
//      	    {
//      	        label: "查询管线长度?",
//      	        action: function(e){
//      	        //menu_172;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载新增光缆段?",
//      	        action: function(e){
//      	        //menu_175;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（自动）?",
//      	        action: function(e){
//      	        //menu_176;
//      	         },
//      	        scope: "" 
//      	    },{
//      	        label: "图形化承载已有光缆段（手动）?",
//      	        action: function(e){
//      	        //menu_177;
//      	         },
//      	        scope: "" 
//      	    },
      	    {
    	    	icon : ctx +"/resources/map/menu/REMOVE.png",
      	        label: "拆除敷设",
      	        action: function(e){
      	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
      	        	Dms.Tools.deleteLayRelationAction(cuid);
      	         },
      	        scope: "" 
      	    },
      	    {
      	    	icon : ctx +"/resources/map/menu/COMBIN.png",
    	        label: "挂墙段合并",
    	        action: function(e){
    	        	dms.merge.mergeTools.mergeSeg(false);
      	        },
    	        scope: "" 
    	    },{
    	        label: "查询管线长度",
    	        action: function(e){
    	        	var systemCuid = tp.Default.OperateObject.contextObject.cuid;
    	        	new dms.queryLineLengthPanel(systemCuid).show();
    	        },
    	        scope: "" 
    	    },{
    	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
      	        label: "复制名称",
      	        action: function(e){
      	        //menu_091;
      	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
      	            var name=tp.Default.OperateObject.contextObject.label;
      	            Ext.Msg.alert('设备名称', name);
      	        },
      	        scope: "" 
      	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
       	        label: "附件管理",
       	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
       	        action: function(e) {
       	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
       	        	var filePanel = new IRMS.dm.common.FilePanel({
       	        		title : '附件管理',
     			    	width : 100,
     			    	height : 350,
     			    	relatedServiceCuid : cuid,
     			    	type : 1,
     			    	readOnly : true,
     			    	preview : false
     			    });

       	        	var winCfg = {
       	      			width : 700,
       	      			height : 350
       	      		};
       	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
       	        },
       	        scope: "" 
       	    }*/],	
  	 'ACCESSPOINT_RIGHT_MENU': //接入点右键功能
		[   
		    {
				icon : ctx +"/resources/map/menu/REMOVE.png",
		        label: "删除点设施",
	  	        action: function(e){
	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	  	        	var array = new Array();
	  	        	array.push(cuid);
	  	        	Dms.Tools.deletePointsAction(array);
	  	        },
	  	        scope: "" 
	  	    },
	  	    {
	  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
	  	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},scope: "" },
	  	    {
	  	    	icon : ctx +"/resources/map/menu/RELATE_LINE.png",
		        label: "关联线设施",
	  	        action: function(e){
	  	        	dms.Tools.getRelationLineByPoint();
	       	        
	  	        },
	  	        scope: "" 
	  	    },{
	  	    	icon : ctx +"/resources/map/menu/JOINT_BOX_LIST.png",
		        label: "终端盒列表",
	  	        action: function(e){
	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	  	        	var labelCn = tp.Default.OperateObject.contextObject.label;
	  	        	new dms.accesspointFboxPanel(labelCn,cuid).show();
	  	        },
	  	        scope: "" 
	  	    },{
	  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
	  	        label: "复制名称",
	  	        action: function(e){
	  	        //menu_091;
	  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
	  	            var name=tp.Default.OperateObject.contextObject.label;
	  	            Ext.Msg.alert('设备名称', name);
	  	        },
	  	        scope: "" 
	  	    },{
    	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
    	        label: "查看图片",
    	        action: function(e){
    	        	var viewParam={
         	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
         	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
         	        	   'mapName':"查看图片",
         	        	   'widthScale' : 0.6,
         	        	   'hightScale' : 0.6
         	        	};
    	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
	   	        	viewParam.content=resImagsPanel;
	      	        tp.utils.showDialogView(viewParam);
    	        },
    	        scope: "" 
    	    }/*,{
	  	        label: "图片管理",
	  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	  	        action: function(e) {
	  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	  	        	var filePanel = new IRMS.dm.common.FilePanel({
	  	        		title : '图片管理',
				    	width : 100,
				    	height : 350,
				    	relatedServiceCuid : cuid,
				    	type : 1,
				    	readOnly : true,
				    	preview : true
				    });

	  	        	var winCfg = {
	  	      			width : 700,
	  	      			height : 350
	  	      		};
	  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
	  	        },
	  	        scope: "" 
	  	    }*/, {
		        label: "层间光缆展示",
	  	        icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	  	        action: function(e){
	  	        	var dialog = InterWireListPanel(tp.Default.OperateObject.contextObject.cuid);
	  	        	dialog.show();
	  	        },
	  	        scope: "" 
	  	    }
//	  	    ,{
//		        label: "开通能力分析?",
//	  	        action: function(e){
//	  	        //menu_041;
//	  	        },
//	  	        scope: "" 
//	  	    }
	  	],  	  	    
	'FIBER_JOINT_BOX_RIGHT_MENU': //接头盒右键功能
		[  
		   {
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/WELD_SECTION.png",
	        label: "熔接关系图",
  	        action: function(e){
  	        	var viewParam={
       	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
       	        	   'mapType':"FiberJointBoxWeldSectionTopo",
       	        	   'mapName':"接头盒熔接关系图"
       	        	};
    	        Dms.Tools.showSectionView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},scope: "" },
  	    {
  	    	icon : ctx +"/resources/map/menu/PORT_MANAGE.png",
	        label: "焊点管理",
  	        action: function(e){
  	        	var viewParam={
  	        			'cuid':tp.Default.OperateObject.contextObject.cuid,
  	        			'labelCn':tp.Default.OperateObject.contextObject.label,
  	        			'mapName':"光接头盒焊点管理",
  	        			'scene': Dms.Default.scene,
  	        			'segGroupCuid' : Dms.Default.segGroupCuid
  	        	};
	        	var url = '/rms/dm/port/FiberJointPointManager.jsp?code=service_dict_dm.DM_FIBERJOINTPOINT';
	        	url=ctx + url + '&cuid=' + viewParam.cuid+'&scene='+viewParam.scene+'&segGroupCuid='+viewParam.segGroupCuid;
	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/FAULT.png",
	        label: "光缆故障定位",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.wireFailure.createSelectWirePane(cuid);
	         },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/
  	    
  	],
  	
  	'FIBER_TERMINAL_BOX_RIGHT_MENU': //终端盒右键功能
		[
		 {
			icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",
  	    	action: function(e){
  	    		Dms.Tools.openModifyExtWin();
  	    	},
  	    	scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JUMP_MANAGE.png",
	        label: "跳纤管理",
  	        action: function(e){
  	        	var viewParam={
     	        	  'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	  'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	  'mapName':"光终端盒跳纤管理"
  	        	};
  	        	var jumpLinkMainView = new JumpLinkMainView(viewParam.cuid.split('-')[0],viewParam.cuid);
  	        	viewParam.content=jumpLinkMainView;
     	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/SECTION.png",
	        label: "面版图",
  	        action: function(e){
  	        	var viewParam={
	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
	        	   'mapType':"FiberJointBoxSectionTopo",
	        	   'mapName':"光终端盒面版图"
        	    };
     	        Dms.Tools.showSectionView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/PORT_MANAGE.png",
	        label: "端子管理",
  	        action: function(e){
  	        	var viewParam={
  	        			'cuid':tp.Default.OperateObject.contextObject.cuid,
  	        			'labelCn':tp.Default.OperateObject.contextObject.label,
  	        			'mapName':"光终端盒端子管理",
  	        			'scene': Dms.Default.scene,
  	        			'segGroupCuid' : Dms.Default.segGroupCuid
  	        	};
	        	var url = '/rms/dm/port/FiberJointPointManager.jsp?code=service_dict_dm.DM_FIBER_JOINT_POINT';
	        	url=ctx + url + '&cuid=' + viewParam.cuid+'&scene='+viewParam.scene+'&segGroupCuid='+viewParam.segGroupCuid;
	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/RELATE_FIBER.png",
	        label: "纤芯关联",
  	        action: function(e){
  	        //menu_049;
  	        	var viewParam={
        	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
        	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
        	        	   'mapName':"光终端盒纤芯关联"
        	        	};
  	        	var fiberDpLinkView = new FiberDpLinkView(viewParam.cuid, viewParam.labelCn);
  	        	viewParam.content=fiberDpLinkView;
     	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/
  	],
  	
	'FIBER_DP_RIGHT_MENU'://光分纤箱右键功能
		[  {
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "查看关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/RELATE_FIBER.png",
	        label: "纤芯关联",
  	        action: function(e){
  	        //menu_049;
  	        	var viewParam={
        	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
        	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
        	        	   'mapName':"光分纤箱纤芯关联"
        	        	};
  	        	var fiberDpLinkView = new FiberDpLinkView(viewParam.cuid, viewParam.labelCn);
  	        	viewParam.content=fiberDpLinkView;
     	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/PORT_MANAGE.png",
	        label: "端子管理",
  	        action: function(e){
  	        	var viewParam={
        	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
        	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
        	        	   'mapName':"光分纤箱端子管理",
        	        	   'scene': Dms.Default.scene,
     	        		   'segGroupCuid' : Dms.Default.segGroupCuid
        	        	};
  	        	
  	        	var url = '/rms/dm/port/FiberDpPortManager.jsp?code=service_dict_dm.DM_FIBERDP_PORT';
  	        	url=ctx + url + '&cuid=' + viewParam.cuid+'&scene='+viewParam.scene+'&segGroupCuid='+viewParam.segGroupCuid;
  	        	FrameHelper.openUrl(url,viewParam.labelCn+viewParam.mapName);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JUMP_MANAGE.png",
	        label: "跳纤管理",
  	      action: function(e){
	        	var viewParam={
	        			 'cuid':tp.Default.OperateObject.contextObject.cuid,
	     	        	  'labelCn':tp.Default.OperateObject.contextObject.label,
	     	        	  'mapName':"光分纤箱跳纤管理"
  	        	};
	        	var jumpLinkMainView =  new Topo.x.JumpLinkMainView('FIBER_DP',viewParam.cuid);
  	        	viewParam.content=jumpLinkMainView;
     	        tp.utils.showDialogView(viewParam);
	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/SECTION.png",
	        label: "面版图",
  	        action: function(e){
  	        	var viewParam={
	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
	        	   'mapType':"FiberDpSectionTopo",
	        	   'mapName':"光分纤箱面版图"
        	    };
     	        Dms.Tools.showSectionView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/FAULT.png",
	        label: "光缆故障定位",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.wireFailure.createSelectWirePane(cuid);
	         },
	        scope: "" 
	    },
  	    {
	    	icon : ctx +"/resources/map/menu/MODIFY.png",
	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},scope: ""},
  	    {
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/
  	],
	'FIBER_CAB_RIGHT_MENU'://光交接箱右键功能
		[  {
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/SECTION.png",
	        label: "面版图",
  	        action: function(e){
  	        	var viewParam={
 	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
 	        	   'mapType':"FiberCabSectionTopo",
 	        	   'mapName':"光交接箱面版图"
	     	    };
	  	        Dms.Tools.showSectionView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/PORT_MANAGE.png",
	        label: "端子管理",
  	        action: function(e){
  	        //menu_058;
  	        	var viewParam={
  	        			'cuid':tp.Default.OperateObject.contextObject.cuid,
  	        			'labelCn':tp.Default.OperateObject.contextObject.label,
  	        			'mapName':"光交接箱端子管理",
  	        			'scene': Dms.Default.scene,
  	        			'segGroupCuid' : Dms.Default.segGroupCuid
  	        	};
  	        	var url = '/rms/dm/port/FiberCabPortManager.jsp?code=service_dict_dm.DM_FIBERCAB_PORT';
	        	url=ctx + url + '&cuid=' + viewParam.cuid+'&scene='+viewParam.scene+'&segGroupCuid='+viewParam.segGroupCuid;
	        	FrameHelper.openUrl(url,viewParam.labelCn+ viewParam.mapName);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JUMP_MANAGE.png",
	        label: "跳纤管理",
  	        action: function(e){
  	        //menu_059;
  	        	var viewParam={
       	        	  'cuid':tp.Default.OperateObject.contextObject.cuid,
       	        	  'labelCn':tp.Default.OperateObject.contextObject.label,
       	        	  'mapName':"光交接箱跳纤管理"
    	        	};
    	        	//var jumpLinkMainView = new JumpLinkMainView(viewParam.cuid.split('-')[0],viewParam.cuid);
  	        	    var jumpLinkMainView =  new Topo.x.JumpLinkMainView('FIBER_CAB',viewParam.cuid);
    	        	viewParam.content=jumpLinkMainView;
       	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/RELATE_FIBER.png",
	        label: "纤芯关联",
  	        action: function(e){
  	        //menu_060;
  	        	var viewParam={
 	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
 	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
 	        	   'mapName':"光交接箱纤芯关联"
     	        };
	        	var fiberLinkView = new FiberLinkView(viewParam.cuid, viewParam.labelCn);
	        	viewParam.content=fiberLinkView;
  	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/FAULT.png",
	        label: "光缆故障定位",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
	        	dms.wireFailure.createSelectWirePane(cuid);
	         },
	        scope: "" 
	    },
  	    {
	    	icon : ctx +"/resources/map/menu/MODIFY.png",
	    	label: "修改点设施",action: function(e)
	    	{
  	    		Dms.Tools.openModifyExtWin();
  	    	},scope: ""
  	    },{
  	    	icon : ctx +"/resources/map/menu/MELT.png",
	        label: "端子直熔",
	        action: function(e){
	        	var cuid = tp.Default.OperateObject.contextObject.cuid,
	        		label = tp.Default.OperateObject.contextObject.label;
	        	new dms.cabPortFusionPanel(label,cuid).show();
	         },
	        scope: "" 
	    },{
	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/]
  	,
	'MANHLE_RIGHT_MENU': //人手井右键功能
		[  {
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	      
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/MANHLE.png",
	        label: "展开图",
  	        action: function(e){
  	        	var viewParam={
  	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
  	        	   'mapType':"ManhleSectionTopo",
  	        	   'mapName':"人井展开图",
  	        	   'widthScale' : 0.8,
  	        	   'hightScale' : 0.8
 	     	    };
 	  	        Dms.Tools.showSectionView(viewParam);
  	        },
  	        //scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",
  	    	action: function(e){
  	    		Dms.Tools.openModifyExtWin();
  	    	},
  	    	scope: ""
  	    },
//  	    {
//	        label: "重置点设施位置?",
//  	        action: function(e){
//  	        //menu_068;
//  	        },
//  	        scope: "" 
//  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/JOINT_BOX_INFO.png",
	        label: "接头盒信息",
  	        action: function(e){
	        	var viewParam={
      	        	   'mapName':"查看接头盒信息",
      	        	};
	        	var param={
	      	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
	      	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
	      	        	   'code': 'service_dict_dm.DM_FIBER_JOINT_BOX',
	      	        	   'condition':[
	      	        		   {'key':'RELATED_LOCATION_CUID', 'relation':'=','value':tp.Default.OperateObject.contextObject.cuid,'type':'string'}
	      	        		   ]
	      	        	};
 	        	var  getFiberJointBoxView = new createResourceTablePanelHT(param);
	   	        	viewParam.content=getFiberJointBoxView;
	      	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/REMAIN.png",
  	    	label: "查看预留信息",
  	    	action: function(e){
  	    		var pointCuid = tp.Default.OperateObject.contextObject.cuid,
  	    			pointName = tp.Default.OperateObject.contextObject.label;
  	    		new dms.wireRemainPanel(pointName,pointCuid).show();
  	    	},
  	    	scope: ""
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/]
  	,
	'POLE_RIGHT_MENU': //电杆右键功能
		[{
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JOINT_BOX_INFO.png",
	        label: "接头盒信息",
  	        action: function(e){
	        	var viewParam={
	      	        	   'mapName':"查看接头盒信息",
	      	        	};
		        	var param={
		      	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
		      	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
		      	        	   'code': 'service_dict_dm.DM_FIBER_JOINT_BOX',
		      	        	   'condition':[
		      	        		   {'key':'RELATED_LOCATION_CUID', 'relation':'=','value':tp.Default.OperateObject.contextObject.cuid,'type':'string'}
		      	        		   ]
		      	        	};
	 	        	var  getFiberJointBoxView = new createResourceTablePanelHT(param);
		   	        	viewParam.content=getFiberJointBoxView;
		      	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },
//  	    {
//	        label: "重置点设施位置?",
//  	        action: function(e){
//  	        //menu_073;
//  	        },
//  	        scope: "" 
//  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},scope: "" },
  	    {
  	    	icon : ctx +"/resources/map/menu/REMAIN.png",
  	    	label: "查看预留信息",
  	    	action: function(e){
  	    		var pointCuid = tp.Default.OperateObject.contextObject.cuid,
  	    			pointName = tp.Default.OperateObject.contextObject.label;
  	    		new dms.wireRemainPanel(pointName,pointCuid).show();
  	    	},
  	    	scope: ""
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/]
  	,
	'STONE_RIGHT_MENU': //标石右键功能
		[{
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JOINT_BOX_INFO.png",
	        label: "接头盒信息",
  	        action: function(e){
	        	var viewParam={
	      	        	   'mapName':"查看接头盒信息",
	      	        	};
		        	var param={
		      	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
		      	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
		      	        	   'code': 'service_dict_dm.DM_FIBER_JOINT_BOX',
		      	        	   'condition':[
		      	        		   {'key':'RELATED_LOCATION_CUID', 'relation':'=','value':tp.Default.OperateObject.contextObject.cuid,'type':'string'}
		      	        		   ]
		      	        	};
	 	        	var  getFiberJointBoxView = new createResourceTablePanelHT(param);
		   	        	viewParam.content=getFiberJointBoxView;
		      	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },
//  	    {
//	        label: "重置点设施位置?",
//  	        action: function(e){
//  	        //menu_078;
//  	        },
//  	        scope: "" 
//  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},scope: ""},
  	    {
  	    	icon : ctx +"/resources/map/menu/REMAIN.png",
  	    	label: "查看预留信息",
  	    	action: function(e){
  	    		var pointCuid = tp.Default.OperateObject.contextObject.cuid,
  	    			pointName = tp.Default.OperateObject.contextObject.label;
  	    		new dms.wireRemainPanel(pointName,pointCuid).show();
  	    	},
  	    	scope: ""
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/]
  	,
	'INFLEXION_RIGHT_MENU': //拐点右键功能
		[{
			icon : ctx +"/resources/map/menu/RELATE_LINE.png",
	        label: "关联线设施",
  	        action: function(e){
  	        	dms.Tools.getRelationLineByPoint();
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/JOINT_BOX_INFO.png",
	        label: "接头盒信息",
  	        action: function(e){
	        	var viewParam={
	      	        	   'mapName':"查看接头盒信息",
	      	        	};
		        	var param={
		      	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
		      	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
		      	        	   'code': 'service_dict_dm.DM_FIBER_JOINT_BOX',
		      	        	   'condition':[
		      	        		   {'key':'RELATED_LOCATION_CUID', 'relation':'=','value':tp.Default.OperateObject.contextObject.cuid,'type':'string'}
		      	        		   ]
		      	        	};
	 	        	var  getFiberJointBoxView = new createResourceTablePanelHT(param);
		   	        	viewParam.content=getFiberJointBoxView;
		      	        tp.utils.showDialogView(viewParam);
  	        },
  	        scope: "" 
  	    },{
  	    	icon : ctx +"/resources/map/menu/REMOVE.png",
	        label: "删除点设施",
  	        action: function(e){
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var array = new Array();
  	        	array.push(cuid);
  	        	Dms.Tools.deletePointsAction(array);
  	        },
  	        scope: "" 
  	    },
//  	    {
//	        label: "重置点设施位置?",
//  	        action: function(e){
//  	        //menu_088;
//  	        },
//  	        scope: "" 
//  	    },
  	    {
  	    	icon : ctx +"/resources/map/menu/MODIFY.png",
  	    	label: "修改点设施",action: function(e){Dms.Tools.openModifyExtWin();},cope: "" },
  	    {
  	    	icon : ctx +"/resources/map/menu/REMAIN.png",
  	    	label: "查看预留信息",
  	    	action: function(e){
  	    		var pointCuid = tp.Default.OperateObject.contextObject.cuid,
  	    			pointName = tp.Default.OperateObject.contextObject.label;
  	    		new dms.wireRemainPanel(pointName,pointCuid).show();
  	    	},
  	    	scope: ""
  	    },{
  	    	icon : ctx +"/resources/map/menu/COPY_NAME.png",
  	        label: "复制名称",
  	        action: function(e){
  	        //menu_091;
  	        	//var cuid=tp.Default.OperateObject.contextObject.cuid;
  	            var name=tp.Default.OperateObject.contextObject.label;
  	            Ext.Msg.alert('设备名称', name);
  	        },
  	        scope: "" 
  	    },{
	    	icon : ctx +"/resources/map/menu/ATTACHMENT.png",
	        label: "查看图片",
	        action: function(e){
	        	var viewParam={
     	        	   'cuid':tp.Default.OperateObject.contextObject.cuid,
     	        	   'labelCn':tp.Default.OperateObject.contextObject.label,
     	        	   'mapName':"查看图片",
     	        	   'widthScale' : 0.6,
     	        	   'hightScale' : 0.6
     	        	};
	        	var resImagsPanel =  createResImagsPanel({LABEL_CN:viewParam.labelCn,CUID:viewParam.cuid});
   	        	viewParam.content=resImagsPanel;
      	        tp.utils.showDialogView(viewParam);
	        },
	        scope: "" 
	    }/*,{
  	        label: "图片管理",
  	        icon : ctx +"/resources/map/menu/ATTACHMENT.png",
  	        action: function(e) {
  	        	var cuid = tp.Default.OperateObject.contextObject.cuid;
  	        	var filePanel = new IRMS.dm.common.FilePanel({
  	        		title : '图片管理',
			    	width : 100,
			    	height : 350,
			    	relatedServiceCuid : cuid,
			    	type : 1,
			    	readOnly : true,
			    	preview : true
			    });

  	        	var winCfg = {
  	      			width : 700,
  	      			height : 350
  	      		};
  	        	var fileWin = WindowHelper.openExtWin(filePanel, winCfg);
  	        },
  	        scope: "" 
  	    }*/]
  	 ,
  'POINT_RIGHTCLICK_MENU':
	[{
        label: "快速增加点设施",
        items: [
			{label: "增加交接箱",action: function(item) {Dms.Tools.addMapPoint('FIBER_CAB');}},
			{label: "增加分纤箱",action: function(item) {Dms.Tools.addMapPoint('FIBER_DP');}},
			{label: "增加接头盒",action: function(item) {
				tp.Default.DrawObject._kind = '1';
				Dms.Tools.addMapPoint('FIBER_JOINT_BOX');
				}
			},
			{label: "增加终端盒",action: function(item) {
				tp.Default.DrawObject._kind = '2';
				Dms.Tools.addMapPoint('FIBER_JOINT_BOX');
				}
			},
			{label: "增加人手井",action: function(item) {Dms.Tools.addMapPoint('MANHLE');}},
			{label: "增加电杆",action: function(item) {Dms.Tools.addMapPoint('POLE');}},
			{label: "增加标石",action: function(item) {Dms.Tools.addMapPoint('STONE');}},
			{label: "增加拐点",action: function(item) {Dms.Tools.addMapPoint('INFLEXION');}},
			{label: "增加接入点",action: function(item) {Dms.Tools.addMapPoint('ACCESSPOINT');}},
			{label: "增加分光器",action: function(item) {Dms.Tools.addMapPoint('AN_POS');}}
	      ],
	        scope: "" 
	    },{
        label: "快速增加线设施",
        items: [
			{label:"增加光缆段",action:function(item){
					Dms.Tools.addMapLine('WIRE_SEG','FIBER_JOINT_BOX');
					tp.Default.DrawObject._kind = '1';
				}
			},
			{label:"增加管道分支",action:function(item) {Dms.Tools.addMapLine('DUCT_SEG','MANHLE');}},
			{label:"增加杆路分支",action:function(item) {Dms.Tools.addMapLine('POLEWAY_SEG','POLE');}},
			{label:"增加标石路由分支",action:function(item){Dms.Tools.addMapLine('STONEWAY_SEG','STONE');}},
			{label:"增加引上系统",action:function(item){Dms.Tools.addMapLine('UP_LINE_SEG','INFLEXION');}},
			{label:"增加挂墙系统",action:function(item){Dms.Tools.addMapLine('HANG_WALL_SEG','INFLEXION');}}
	      ],
	        scope: "" 
	    },
	    {
      	  label: "光缆改迁",
      	  action: function(e){
      		new dms.move.moveWireRoutePanel().show();
      	  },
      	  scope: "" 
        },
        {
          label: "场景固化",
          items: [{

              label: "分岐接续",
              action: function(e){
                var panel = new dms.Default.discriminateConnectView();
                new dms.Default.openHtPanel(panel,'分岐接续',600,160).show();
              },
              scope: "" 
            
          },{

              label: "直通接续",
              action: function(e){
                var panel = new dms.Default.directConnectView();
                new dms.Default.openHtPanel(panel,'直通接续',400,160).show();
              },
              scope: "" 
            
          },{

              label: "批量接续",
              action: function(e){
                var panel = new dms.Default.batchConnectView();
                new dms.Default.openHtPanel(panel,'批量接续',600,380).show();
              },
              scope: "" 
            
          }
          ]
        }
//	    ,{
//        	  label: "全图",
//        	  action: function(e){
//        		var map = dms.Default.tpmap.getMap();
//        		var southWest = L.latLng(30.5025259,114.0010071),
//        	    northEast = L.latLng(30.6562249,114.5853424),
//        	    bounds = L.latLngBounds(southWest, northEast);
//        		map.fitBounds(bounds);
//        	  },
//        	  scope: "" 
//          }
//	    ,{
//        label: "区块选择?",
//	        action: function(e){
//	        },
//	        scope: "" 
//	    },{
//        label: "光缆断点查询?",
//	        action: function(e){
//	        },
//	        scope: "" 
//	    }
	],
	'LAY_SELECT_RIGHT_MENU':[
          {
        	  label: "完成",
        	  action: function(e){
        		  var wireSegCuid = tp.Default.OperateObject.contextObject.cuid,
        		  lineCuid = tp.Default.OperateObject.contextDuctLineObject.cuid;
        		  if(!lineCuid){
        			  tp.utils.optionDialog("温馨提示", "请选择承载段！");
        		  }else{
        			  Dms.Tools.autoLayDuctLine(wireSegCuid,lineCuid);
        		  }
        	  },
        	  scope: "" 
          },{
        	  label: "放弃",
        	  action: function(e){
        		  Dms.Default.tpmap.reset();
        	  },
        	  scope: "" 
          }                
	],
	'GLAY_WIRE_RIGHT_MENU':[
           {
         	  label: "选中",
         	  action: function(e){
         		  var wireSegCuid = tp.Default.OperateObject.contextObject.cuid,
         		  lineCuid = tp.Default.OperateObject.contextDuctLineObject.cuid;
         		 dms.glaywire.selectLine(wireSegCuid,lineCuid);
         	  },
         	  scope: "" 
           }              
 	],
 	'SPLIT_SEG_CONTEXTMENU':[
		{
			label: "完成",
			action:function(e){
				dms.split.splitTools.AddSplitSeg();
			},
			scope: "" 
		},
	    "separator", //分割线
	    {
			label: "放弃",
			action:function(e){
				dms.split.splitTools.clearSplitSegUnkown();
			},
			icon: ''
		}
	],
	'GRID':[
		{
			icon : ctx +"/resources/map/menu/MODIFY.png",
			label:"修改属性",
			action: function(){
				Dms.Tools.Grid.modifyGridInfo();
			}
		},
		{
			label:"编辑形状",
			items :[
			        {
			        	label : '开始',
			        	action: function(){
							Dms.Tools.Grid.startModifyShape();
						}
			        },
			        {
			        	label : '保存',
			        	action : function(){
			        		Dms.Tools.Grid.saveModifyShape();
			        	}
			        }
			]
			
		}/*,
		{
			label:"替换行政边界",
			action: function(){
				Dms.Tools.Grid.replaceRegion();
			}
		}*/,
		{
			icon : ctx +"/resources/map/menu/REMOVE.png",
			label:"删除业务区",
			action: function(){
				Dms.Tools.Grid.deleteGrid();
			}
		},
		{
			icon : ctx +"/resources/map/menu/SPLIT.png",
			label:"拆分业务区",
			action: function(){
				Dms.Tools.Grid.splitGrid();
			}
		},
		{
			icon : ctx +"/resources/map/menu/COMBIN.png",
			label:"合并业务区",
			action: function(){
				Dms.Tools.Grid.combineGrid();
			}
		},
		{
			label:"站点详情",
			action: function(){
				Dms.Tools.Grid.getRelatedSite();
			}
		},
		{
			label:"资源详情",
			action: function(){
				Dms.Tools.Grid.getRelatedRes();
			}
		}
	],
	"G_DISTRICT" : [
		{
			label:"新增业务区",
			action: function(){
				Dms.Tools.Grid.drawGrid();
			}
		}/*,
		{
			label:"导入行政边界",
			action: function(){
				Dms.Tools.Grid.importRegion();
			}
		}*/
	],
	'MERGE_SEG_CONTEXTMENU' : [
	    {
			label : "完成",
			action : function(e) {
				var pointsize = tp.Default.DrawObject._drawPointList.length;
				if (pointsize < 2) {
					tp.utils.optionDialog("温馨提示", "请选择要合并的点！");
					return;
				}
				var mergeCuid = dms.merge.mergeTools.mergeSegCuid;
				var firstPointCuid = tp.Default.DrawObject._drawPointList[0]._id;
				var lastPointCuid = tp.Default.DrawObject._drawPointList[pointsize - 1]._id;
				var className = mergeCuid.split("-")[0];
				dms.merge.mergeTools.AddMergeSegPanel(mergeCuid,firstPointCuid, lastPointCuid);
			},
			scope : ""
		}, "separator", //分割线
		{
			label : "放弃",
			action : function(e) {
				dms.merge.mergeTools.clearMergeSegUnkown();
			},
			icon : ''
		} 
	],
	'MERGE_BRANCH_CONTEXTMENU' : [
			{
				label : "完成",
				action : function(e) {
					
					var size = dms.merge.mergeBranchTools.mergeBranchCuids.length;
					if (size < 2) {
						tp.utils.optionDialog("温馨提示", "请选择要合并的分支！");
						return;
					}
					var mergeCuid = dms.merge.mergeBranchTools.mergeBranchCuid;//系统cuid
					var firstBranchCuid = dms.merge.mergeBranchTools.mergeBranchCuids[0];
					var lastBranchCuid = dms.merge.mergeBranchTools.mergeBranchCuids[size-1];
					
					dms.merge.mergeBranchTools.AddMergeBranchPanel(mergeCuid,firstBranchCuid, lastBranchCuid);
				},
				scope : ""
			}, "separator", //分割线
			{
				label : "放弃",
				action : function(e) {
					dms.merge.mergeTools.clearMergeSegUnkown();
				},
				icon : ''
			} 
		],
		'MOVE_WIREROUTE_RIGHT_MENU':[
	         {
	       	  label: "选中",
	       	  action: function(e){
	       		 var con = tp.Default.OperateObject.contextObject;
	       		 if(con){
	       			var relatedBranchCuid = con.relatedBranchCuid,
	       				classname = relatedBranchCuid.split("-")[0];
	       			var isSystem = dms.systemClassNameResName[classname];
	       			var isFlag = false;
	       			if(isSystem){
	       				if(classname === 'UP_LINE' || classname === 'HANG_WALL'){
	       					isFlag = true;
	       				}
	       			}else{
	       				isFlag = true;
	       			}
	       			if(!isFlag){
	       				tp.utils.optionDialog('温馨提示', '只有引上、挂墙可选系统！');
	       				return;
	       			}
	       			dms.move.addData(con);
	       		 }
	       	  },
	       	  scope: "" 
	     }
	],
	

	'SELECT_DEVICE_TYPE' : [
		{
			label : "站点",
			action : function(e) {
			      var scope = this;
				  var code = 'service_dict_dm.DM_SITE';
				  tp.utils.createMenuActionQueryDialog(code,scope);
			  }
		},
		{
			label : "接入点",
			action : function(e) {
			      var scope = this;
				  var code = 'service_dict_dm.DM_ACCESSPOINT';
				  tp.utils.createMenuActionQueryDialog(code,scope);
			  },
		},
		{
			label : "光交接箱",
			action : function(e) {
			      var scope = this;
				  var code = 'service_dict_dm.DM_FIBER_CAB';
				  tp.utils.createMenuActionQueryDialog(code,scope);
			  },
		},
		{
			label : "光分纤箱",
			action : function(e) {
			      var scope = this;
				  var code = 'service_dict_dm.DM_FIBER_DP';
				  tp.utils.createMenuActionQueryDialog(code,scope);
			  },
		},
		{
			label : "光接头盒",
			action : function(e) {
			      var scope = this;
				  var code = 'service_dict_dm.DM_FIBER_JOINT_BOX';
				  tp.utils.createMenuActionQueryDialog(code,scope);
			  },
		} ],

};
