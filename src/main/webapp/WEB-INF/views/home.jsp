<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BPMN Modelel. Authorization</title>
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
	<div class="login" style="  color: gray; margin-right: auto; margin-left: 350px; " >
		<h2>Login with username/password</h2>
		<form class="loginform" name="f"
			action="<c:url value='j_spring_security_check' />" method="POST">
			<table class="loginform" style=" color: gray">
				<!-- 				<tr>
					<td colspan="2">
						<t:errors />
					</td>
				</tr> -->
				<tr>
					<td><label for="j_username" >Username</label></td>
					<td class="input"><input id="j_username" name="j_username"
						value="" /></td>
				</tr>
				<tr>
					<td><label for="j_password">Password</label></td>
					<td class="input"><input id="j_password" type="password"
						name="j_password" value="" /></td>
				</tr>
				<tr>
					<td></td>
					<td class="submit" ><input type="submit" name="submit"
						value="Login" /><a href="<c:url value="signup" />">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SignUp</a></td>
				</tr>
			</table>
		</form>
		<div class="errorblock" ${loginerror}>
			Invalid Username or Password <br> Please try again.
		</div>
		<!--<h2>Login with OpenID</h2>
		<form id="facebook_form" action="" method="POST">
			<input type="submit" name="facebook" value="Facebook login" />
		</form>-->
	</div>
	</section>
</body>
</html>
