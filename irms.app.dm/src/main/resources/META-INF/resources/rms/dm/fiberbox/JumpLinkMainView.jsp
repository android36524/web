<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
<%@ include file="/commons/common.jsp"%>
<%@ include file="/commons/dwr.jsp"%>
<%@ include file="/commons/ext.jsp"%>
<%@ include file="/cmp_include/form.jsp"%>
<script type="text/javascript" src="${ctx}/jsp/framework/FlexEvent.js"></script>

<style>
body { margin: 0px; overflow:hidden; }
body {-moz-user-select:none; -khtml-user-select:none; user-select:none;}
</style>
<title>Index</title>
		<style>
            html, body {
                padding: 0px;
                margin: 0px;
                font-family: serif;
            }            
            .main {
                margin: 0px;
                padding: 0px;
                background: #ffffff;
                position: absolute;
                position: fixed;
                top: 0px;
                bottom: 0px;
                left: 0px;
                right: 0px;
            }
            .graphView {
                margin: 0px;
                padding: 0px;
                background: #ffffff;
                position: absolute;
                position: fixed;
                top: 0px;
                bottom: 0px;
                left: 0px;
                right: 0px;
            }
            .topo {
                position: absolute;
                top: 0px;
                bottom: 0px;
                left: 0px;
                right: 0px;
            }
            .overview{
                position:absolute;
                right:0px;
                bottom:0px;
                width:200px;
                height:150px;
                background:rgb(67,75,94);
            }
            .animation{
                -webkit-transition: width .3s,height .3s;
                -moz-transition: width .3s,height .3s;
                -o-transition: width .3s,height .3s;
                transition: width .3s,height .3s;
            }
            .mask{
                width:100%;
                height:100%;
                background: rgba(0,0,0,0.2);
                position: absolute;
                display: table;
            }
            .mask div{
                display: table-cell;
                vertical-align: middle;
                text-align: center;
            }
            .ht-widget-contextmenu{
                font-size: 14px;
            }
            .ht-widget-contextmenu ul{
                border-radius: 5px;
            }
            .ht-widget-contextmenu .top-menu-item{
                border-radius: 5px 5px 0 0;
            }
            .ht-widget-contextmenu .bottom-menu-item{
                border-radius: 0 0  5px 5px;
            }
            
            .ht-widget-menu {
    			font-size: 14px; /*自定义根菜单文字尺寸，所有的图标资源自适应文字尺寸*/
    			/*自定义根菜单背景*/
    			background: -webkit-linear-gradient(top, rgb(250,252,253), rgb(232,241,251) 40%,rgb(220,230,243) 40%,rgb(220,231,245));
    			background: -moz-linear-gradient(top, rgb(250,252,253), rgb(232,241,251) 40%,rgb(220,230,243) 40%,rgb(220,231,245));
    			background: -ms-linear-gradient(top, rgb(250,252,253), rgb(232,241,251) 40%,rgb(220,230,243) 40%,rgb(220,231,245));
    			background: linear-gradient(to bottom, rgb(250,252,253), rgb(232,241,251) 40%,rgb(220,230,243) 40%,rgb(220,231,245));
			}
        </style>
        <script type="text/javascript" src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"> </script>
		<script type="text/javascript" src="${ctx}/jslib/ht/ht-all.js"> </script>
		<script type="text/javascript" src="${ctx}/rms/dm/fiberbox/JumpLinkMainView.js"> </script>
	
<script type="text/javascript">
	
	var dataModel = null;
	try{
		dataModel = new ht.DataModel();
	}catch(Exception){
		alert('错误信息：'+"请使用Chrome浏览器或安装IE Chome插件，谢谢！"); 
	}
	Ext.onReady(function(){
		var jumpPanel = new Ext.Panel({
			 title:'跳纤管理',//标题
			 collapsible:true,//右上角的收缩按钮，设为false则不显示
			 id:'jumpPanel',//panel是显示在html中，id为container中的
			 renderTo:Ext.getBody(),
			 width:800,
			 dragable:true,
			 height:400//,
			});
		
		var jumpLinkMainView = new Frame.op.JumpLinkMainView("FIBER_CAB","FIBER_CAB-8a9f82f235eff5970135eff5ad540002");
		var jv = jumpLinkMainView.getView();
		jv.className = 'graphView';
		var jumpPanelDiv = document.getElementById('jumpPanel');
		jumpPanelDiv.firstChild.nextSibling.firstChild.appendChild(jv);
	});
	
</script>
</head>
<body>	
</body>
</html>
