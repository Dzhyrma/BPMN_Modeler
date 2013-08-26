<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BPMN Modeler. Task completed</title>
<style type="text/css" >
@CHARSET "UTF-8";
header {
	width: 80%;
	background-color: #66cccc;
	color: white;
	margin-right: auto;
	margin-left: auto;
	padding: 10px;
	overflow: hidden;
	border-top-color: white;
	border-bottom-color: white ;
	border-top-style: solid;
	border-bottom-style: solid;
	border-top-width: 5px;
	border-bottom-width: 5px;
	text-align: center;
}

section {
	width: 80%;
	background-color: white;
	margin-right: auto;
	margin-left: auto;
	border-left: #d4d4d4 solid 3px;
	border-right: #d4d4d4 solid 3px;
	padding: 5px;
	height: 700px;
}
hr{
	border:0px;
	 color: #d4d4d4;
	background-color: #d4d4d4;
	height: 5px;
  width: 82%;
}

body {
	font-family: Arial, Helvettica, sans-serif;
	background-color: white;
}

a:link {
	text-decoration: none;
	color: gray;
}

a:VISITED {
	text-decoration: none;
	color: gray;
}
a:HOVER {
	text-decoration: none;
	color: #66cccc;
}


</style>
</head>
<body>
<hr>
	<header> <hgroup>
	<h1>Welcome to BPMN modeler</h1>
	</hgroup> </header>
<hr>
<section>
<div style=" color: gray; margin-left: 350px;">
	<h1>${taskName}</h1>
	<h3>was successfully completed!</h3>
	<a href="<c:url value='/welcome' />">Go home...</a>
	</div>
	</section>
</body>
</html>