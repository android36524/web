<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'prompt.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
		<style type="text/css">
<!--

body {
	background-color: #f4fafe;
}

.con_div {
	width: 300px;
	height: 53px;
	border: 1px #DEE9F5;
	text-align: center;
	display: table-cell;
	vertical-align: middle;
	background-image: url(img/waitbj.gif);
	color: #7790AF;
	font-size: 14px;
	line-height: 25px;
}

.tdbj {
	background-image: url(img/bj_14.gif);
	background-repeat: no-repeat;
	background-position: bottom right;
}
-->
</style>

  </head>
  
  <body>
    <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
						   <tr>
							<td align="center" valign="middle" class="tdbj">
								<table width="400" height="76" border="0" cellpadding="0"
									cellspacing="0" class="con_div" >
									<tr>
										<td>
											+ + 无此类型的资源，请检查配置 + +
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
   
   
   
   
  </body>
</html>
