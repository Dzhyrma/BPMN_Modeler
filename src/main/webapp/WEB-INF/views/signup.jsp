<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>BPMN Modeler. Registration</title>
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
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an outdated browser. <a href="http://browsehappy.com/">Upgrade your browser today</a> or <a href="http://www.google.com/chromeframe/?redirect=true">install Google Chrome Frame</a> to better experience this site.</p>
        <![endif]-->

	<form:form method="POST" commandName="user" modelAttribute="user"
		id="signup">
		<div class="row-fluid"  style=" color: gray; margin-left: 350px;">
			<div class="span10">
				<h3>Sign Up Now !</h3>
				<table  style=" color: gray">
					<tr>
						<td><form:label path="userName">User Name &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</form:label> <form:input
								path="userName" required="true" /></td>
					</tr>
					<tr>
						<td><form:label path="password">Password&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</form:label> <form:input
								type="password" path="password" required="true" /></td>
					</tr>
					<tr>
						<td><label for="retype">Retype Password</label> <input
							type="password" required id="retype" /></td>
					</tr>
					<tr>
						<td><form:label path="email">Email&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</form:label> <form:input
								path="email" required="true" type="email" /></td>
					</tr>
					<tr>
						<td><input type="submit" value="SignUp" class="btn btn-large" />
						</td>
					</tr>
				</table>
			</div>
			<div ${error}>
				Invalid Username or Password <br> Please try again.
			</div>
		</div>
	</form:form>
	</section>
</body>
</html>