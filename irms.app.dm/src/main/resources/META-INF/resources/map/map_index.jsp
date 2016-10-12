<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<%@ include file="/commons/common.jsp"%>
<%@ include file="/commons/dwr.jsp"%>
<%@ include file="/commons/ext.jsp"%>
<%@ include file="/cmp_include/form.jsp"%>
<%@ include file="/cmp_include/grid.jsp" %>
<link rel="stylesheet" href="${ctx}/jslib/tp/tp-map.css">

<script src="${ctx}/jsp/framework/FlexEvent.js"></script>
<script	src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"></script>
<script>
	htconfig = {
		Default : {
			toolTipDelay : 100,
			toolTipContinual : true,
			toolTipLabelFont : '16px arial, sans-serif'
		}
	};
</script>
<script src="${ctx}/map/map-inc.js"></script>
<script src="${ctx}/map/dms-init.js"></script>
<script src="${ctx}/map/dms-components.js"></script>
<script src="${ctx}/map/dms-querypanel.js"></script>
<script src="${ctx}/map/dms-tools.js"></script>
<script src="${ctx}/map/dms-tools-ux.js"></script>
<script src="${ctx}/map/dms-utils.js"></script>
<script src="${ctx}/map/dms-wiretoductline.js"></script>
<script src="${ctx}/map/dms-sectionPic.js"></script>
<script src="${ctx}/map/dms-glaywire.js"></script>
<script src="${ctx}/map/dms-carryingcable.js"></script>
<script src="${ctx}/map/dms-wireFailurePosition.js"></script>
<script src="${ctx}/map/dms-movepoint.js"></script>
<script src="${ctx}/map/dms-sitewireanalysis.js"></script>
<script src="${ctx}/map/dms-ductline.js"></script>
<script src="${ctx}/map/dms-conf-layername.js"></script>
<!-- <script src="${ctx}/template/template-addRow.js"></script>
<script src="${ctx}/template/template-modifyCols.js"></script> -->
<script src="${ctx}/map/dms-map.js"></script>
<script src="${ctx}/map/dms-mapbounds.js"></script>
<script src="${ctx}/commons/utils/SynDataHelper.js"></script>
<!-- <script type="text/javascript" src="${ctx}/dwr/interface/GridViewAction.js"></script> -->
<script src="${ctx}/map/dms-toolpanel.js"></script>
<script src="${ctx}/map/dms-map-contextmenu.js"></script>
<script src="${ctx}/jslib/tp/topo-all.js"></script>
<script src="${ctx}/jslib/tp/tp-form.js"></script>
<script src="${ctx}/jslib/ht/ht-historymanager.js"></script>
<script src="${ctx}/jslib/tp/component/dialoggridpanel.js"></script>
<script src="${ctx}/dwr/interface/MapConfigAction.js"></script>
<script src="${ctx}/dwr/interface/DistNameAction.js"></script>
<script src="${ctx}/dwr/interface/PropertyAction.js"></script>
<script src="${ctx}/map/dms-batchrename.js"></script>
<script src="${ctx}/map/dms-map-inc.js"></script>
<script src="${ctx}/map/dms-map-deleteLines.js"></script>
<script src="${ctx}/map/dms-map-queryLineLength.js"></script>
<script src="${ctx}/map/dms-map-wireRemain.js"></script>
<script src="${ctx}/map/dms-map-wireRemainPanel.js"></script>
<script src="${ctx}/map/dms-map-branchinfo.js"></script>
<script src="${ctx}/map/dms-map-accesspointFBox.js"></script>
<script src="${ctx}/map/dms-map-accesspointFBoxPanel.js"></script>
<script src="${ctx}/map/dms-map-fiberLinkView.js"></script>
<script src="${ctx}/map/dms-map-cabPortFusion.js"></script>
<script src="${ctx}/map/dms-map-cabPortFusionPanel.js"></script>
<script src="${ctx}/map/dms-queryJXFiber.js"></script>
<script src="${ctx}/map/dms-querySiteConnctionAndAnalyse.js"></script>
<script src="${ctx}/map/dms-util-selectDeviceByTypeComp.js"></script>
<script src="${ctx}/map/dms-map-discriminateConnectView.js"></script>
<script src="${ctx}/map/dms-map-directConnectView.js"></script>
<script src="${ctx}/map/dms-map-batchConnectView.js"></script>
<script src="${ctx}/rms/common/DmFilePanel.js"></script>


<%-- <script src="${ctx}/map/jquery-plugins.js"></script> --%>
<script type="text/javascript">

	var isTouchable = "ontouchend" in document;
	seajs.use('tp_component',function(){
		$(document).ready(function(){
			//var graphView = Dms.Default.graphView = new ht.graph.GraphView(),
				//读取图层配置文件
			MapConfigAction.getMapConfig(function(data){
				window.htconfig.Default.baseZIndex = 999;
				Dms.initMap(data,function(){
					//加载保存的位置
					var tpmap = Dms.Default.tpmap;
					var map = tpmap.getMap();
					map.on("metadataLoaded",function(){
				        Dms.MapBounds.loadMapBound(map);
						Dms.initToolPane();
						var urlParam = UrlHelper.getUrlObj(window.location.search.substring(1));
						Dms.Default.scene = urlParam['scene'];
	  	        	    Dms.Default.segGroupCuid = urlParam['segGroupCuid'];//单位工程
	  	        	    
						var cuid = dms.Default.user.distCuid = ac.relatedDistrictCuid;
						DWREngine.setAsync(false);
						DistNameAction.distNameByDistCuid(cuid,function(data){
							if(data){
								dms.Default.user.distName = data;
							}
						}); 
						DWREngine.setAsync(true);
						
						DistNameAction.getRunTimeDistrict(function(data){
							if(data){
								var disLabel = data.LABEL_CN;
								dms.Default.user.systemDistrictName = disLabel;
							}
						});
						
						window.setTimeout(function(){
					        	var map = Dms.Default.tpmap;
								var resLayers =  map._resourceLayers;//动态图层
								var planLayer = null;
								for(var i = 0; i < resLayers.length; i++)
								{
									var resLayer = resLayers[i];
									if(resLayer._layerParams.resid === 'plan')
									{
										planLayer  = resLayer;
										break;
									}
								}
								if(planLayer)
								{
									var delLayer = new ht.List();
									Dms.Default.layerPanel.dm().getRoots().each(function(root)
									{
										var layer = root.a("layer");
										if(layer == planLayer)
										{
											delLayer.add(root);
											delLayer.addAll(root.getChildren());
										}
									});
									
									Dms.Default.layerPanel.dm().sm().rs(delLayer);
									
								}
					        },5000);
					});
				    var selectMenu={
				    			'SITE' : function() {
				    				return (Dms.Default.contextMenu.SITE_RIGHT_MENU);
				    			},
				    			'ACCESSPOINT' : function() {
				    				return (Dms.Default.contextMenu.ACCESSPOINT_RIGHT_MENU);
				    			},
				    			'FIBER_JOINT_BOX':function(){
				    				return (Dms.Default.contextMenu.FIBER_JOINT_BOX_RIGHT_MENU);
				    			},
				    			'FIBER_DP' : function() {
				    				return (Dms.Default.contextMenu.FIBER_DP_RIGHT_MENU);
				    			},
				    			'FIBER_CAB' : function() {
				    				return (Dms.Default.contextMenu.FIBER_CAB_RIGHT_MENU);
				    			},
				    			'PON' : function() {
				    				return (Dms.Default.contextMenu.PON_RIGHT_MENU);
				    			},
				    			'MANHLE' : function() {
				    				return (Dms.Default.contextMenu.MANHLE_RIGHT_MENU);
				    			},
				    			'POLE' : function() {
				    				return (Dms.Default.contextMenu.POLE_RIGHT_MENU);
				    			},
				    			'STONE' : function() {
				    				return (Dms.Default.contextMenu.STONE_RIGHT_MENU);
				    			},
				    			'INFLEXION' : function() {
				    				return (Dms.Default.contextMenu.INFLEXION_RIGHT_MENU);
				    			},
				    			'WIRE_SEG' : function() {
				    				//return (Dms.Default.contextMenu.SELECT_WIRE_SEG_POINT);
				    				return (Dms.Default.contextMenu.WIRE_SEG_RIGHT_MENU);
				    			},
				    			'DUCT_SEG' : function() {
				    				return (Dms.Default.contextMenu.DUCT_SEG_RIGHT_MENU);
				    			},
				    			'POLEWAY_SEG' : function() {
				    				return (Dms.Default.contextMenu.POLE_SEG_RIGHT_MENU);
				    			},
				    			'STONEWAY_SEG' : function() {
				    				return (Dms.Default.contextMenu.STONE_SEG_RIGHT_MENU);
				    			},
				    			'UP_LINE_SEG' : function() {
				    				return (Dms.Default.contextMenu.UPLINE_SEG_RIGHT_MENU);
				    			},
				    			'HANG_WALL_SEG' : function() {
				    				return (Dms.Default.contextMenu.HANGWALL_SEG_RIGHT_MENU);
				    			},
				    			'GRID' : function(){
				    				return (Dms.Default.contextMenu.GRID);
				    			},
				    			'FIBER_TERMINAL_BOX':function(){
				    				return (Dms.Default.contextMenu.FIBER_TERMINAL_BOX_RIGHT_MENU);
				    			}
				    	};
				    	//系统右键菜单
				    	var selectSystemMenu={
				    			'WIRE_SYSTEM' : function() {
				    				return (Dms.Default.contextMenu.WIRE_SYSTEM_RIGHT_MENU);
				    			},
				    			'DUCT_SYSTEM' : function() {
				    				return (Dms.Default.contextMenu.DUCT_SYSTEM_RIGHT_MENU);
				    			},
				    			'POLEWAY_SYSTEM' : function() {
				    				return (Dms.Default.contextMenu.POLE_SYSTEM_RIGHT_MENU);
				    			},
				    			'STONEWAY_SYSTEM' : function() {
				    				return (Dms.Default.contextMenu.STONE_SYSTEM_RIGHT_MENU);
				    			},
				    			'UP_LINE' : function() {
				    				return (Dms.Default.contextMenu.UPLINE_SYSTEM_RIGHT_MENU);
				    			},
				    			'HANG_WALL' : function() {
				    				return (Dms.Default.contextMenu.HANGWALL_SYSTEM_RIGHT_MENU);
				    			}
				    	};
				    	//分支右键菜单
				    	var selectBranchMenu={
							'DUCT_BRANCH' : function() {
								return (Dms.Default.contextMenu.DUCT_BRANCH_RIGHT_MENU);
							},
							'POLEWAY_BRANCH' : function() {
								return (Dms.Default.contextMenu.POLE_BRANCH_RIGHT_MENU);
							},
							'STONEWAY_BRANCH' : function() {
								return (Dms.Default.contextMenu.STONE_BRANCH_RIGHT_MENU);
							}
				    	};
				    	var laySelectMenu={
							'WIRE_SEG' : function() {
								return (Dms.Default.contextMenu.LAY_SELECT_RIGHT_MENU);
							}
				    	};
				    	
				    	var grayLaySelectMenu={
							'WIRE_SEG' : function() {
								return (Dms.Default.contextMenu.GLAY_WIRE_RIGHT_MENU);
							}
				    	};		    	
				    	
				    	var rightClickBackground={
				    			'1': function(){
				    				return (Dms.Default.contextMenu.POINT_RIGHTCLICK_MENU);
				    			},
				        		'2':	function() {//tp.Default.DrawObject._drawState == 2 //新增点时
				        			return (Dms.Default.contextMenu.ADD_POINT);
				        		},
				        		'3':	function() {//新增线时
				        			return (Dms.Default.contextMenu.ADD_LINE);
				        		},
				        		'6':	function() {//承载段拆分
				        			return (dms.Default.contextMenu.SPLIT_SEG_CONTEXTMENU);
				        		},

				        		'7': function(e){//光缆改迁
				        			 if (dms.splitContextmenu){
				        				 //此处hide()方法不灵
//				                     	contextmenu.hide();
				        				 dms.splitContextmenu.setItems(null);
				                     }
				        			return (dms.Default.contextMenu.MOVE_WIREROUTE_RIGHT_MENU);
				        		},
				        		
				        		
				        		'8': function(e){//承载段合并
				        			return (dms.Default.contextMenu.MERGE_SEG_CONTEXTMENU);
				        		},
				        		
				        		'9': function(e){//承载分支合并
				        			return (dms.Default.contextMenu.MERGE_BRANCH_CONTEXTMENU);
				        		},
				        		
				        		'101':	function() {//点选段 右键菜单
				        			var contextObject = tp.Default.OperateObject.contextObject;
				        			if(contextObject == null){
				        				return;
				        			}
				        			var cuid = contextObject.cuid;
				                	if (cuid == null){
				                		return;
				                	}
				                	var className = cuid.split('-')[0];
				                	var kind = tp.Default.DrawObject._kind;
					        		if(className == "FIBER_JOINT_BOX"){
					        			if(kind && kind == '2'){
						        			className = "FIBER_TERMINAL_BOX";//光终端盒
						        		}
					        		}
				                	return selectMenu[className](cuid);
				        		},
				        		'102':	function() {//点选系统 右键菜单
				        			var contextObject = tp.Default.OperateObject.contextObject;
				        			if(contextObject == null){
				        				return;
				        			}
				        			var cuid = contextObject.cuid;
				                	if (cuid == null){
				                		return;
				                	}
					            	var className = cuid.split('-')[0];
					            	return selectSystemMenu[className](cuid);
				        		},
				        		'103':	function() {//点选分支 右键菜单
				        			var contextObject = tp.Default.OperateObject.contextObject;
				        			if(contextObject == null){
				        				return;
				        			}
				        			var cuid = contextObject.cuid;
				                	if (cuid == null){
				                		return;
				                	}
				    	     	    var className = cuid.split('-')[0];
				    	        	return selectBranchMenu[className]();
				        		},
				        		'201':	function() {//图形化自动敷设时选择要敷设的承载段用
				        			return laySelectMenu["WIRE_SEG"]();
				        		},
				        		'202':function(){//图形化敷设时“选择”分支用
				        			return grayLaySelectMenu["WIRE_SEG"]();
				        		}    		
				        	};
				    map.on("locateFinished",function(){
		//				Dms.Tools.showResOperateView();	
						var fn = rightClickBackground[tp.Default.DrawObject._drawState] ; 
				    	if (fn !=null){
		            		var contextMenu = fn() || [];
							if(tp.Default.DrawObject._drawState === 102 || tp.Default.DrawObject._drawState === 103)
							{
					    		
					    	}else
					    	{
					    		contextMenu = [{
					    			icon : ctx +"/resources/map/menu/JOINT_BOX_LIST.png", 
					    	        label: "查看属性",
					    	        action: function(){
					    	        	Dms.Tools.showPropertyView();
					    	        },
					    	        scope: "" 
					    	    }].concat(contextMenu);
					    	}
							new tp.widget.PopupPanel(tpmap,contextMenu).show();
		            	}
			        });
				});
				//Dms.initToolbar(graphView);
			});
		    $(window).bind("beforeunload",function(){
		    	var msg;
		    	/*
		    	var msg = '您确定要退出该界面么！';
		    	var e = e || window.event;
				if (e) {
					e.returnValue = msg;
				}*/
				Dms.MapBounds.saveMapBound();
				if(window.ActiveXObject){
					window.returnValue = "";
				}else if(window.opener){
					window.opener.setShowModalDialogValue("");
				}
				return msg;
		    });
		});
	});
</script>
</head>
<body>
<!-- 	<div id="map"></div> -->
</body>
</html>
