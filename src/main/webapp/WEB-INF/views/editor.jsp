<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BPMN Modeler</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<link rel="stylesheet" href="./css/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css" href="styles.css" >
<!-- script src="http://code.jquery.com/jquery-1.9.1.js"></script -->
<script src="./js/jquery-2.0.0.min.js"></script>
<script src="./js/jquery-ui.min.js"></script>
<script src="./js/jquery.jsPlumb-1.4.0-all-min.js"></script>
<script src="./js/diagram.manager.js"></script>
<script src="./js/jquery.contextmenu.r2.js"></script>
<script src="./js/jquery.blockUI.js"></script>
<style type="text/css" >
@CHARSET "UTF-8";
@CHARSET "UTF-8";
.ui-loader-background {
	width: 100%;
	height: 100%;
	top: 0;
	padding: 0;
	margin: 0;
	background: rgba(0, 0, 0, 0.3);
	display: none;
	position: fixed;
	z-index: 100;
}

.ui-loading .ui-loader-background {
	display: block;
}

.defaultArrowText {
	font: bold 20px courier;
}

.condition {
	text-shadow: -2px 0 white, 0 2px white, 2px 0 white, 0 -2px white;
}

div.draggable {
	position: relative;
	cursor: move;
	min-width: 32px;
	min-height: 32px;
	max-width: 300px;
	max-height: 200px;
}

.ep {
	display: none;
	position: absolute;
	top: 0px;
	left: 0px;
	width: 0.7em;
	height: 0.7em;
	border: 2px solid;
	border-color: #000000;
	border-radius: 0.2em;
	background-color: #999999;
	cursor: pointer;
	display: none;
	z-index: 1;
}

div.draggable:hover .ep {
	display: block;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
}

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

nav {
	width: 30%;
	float: right;
}

body {
	font-family: Arial, Helvettica, sans-serif;
	background-color: white;
}

a:link {
	text-decoration: none;
	color: white;
}

a:VISITED {
	text-decoration: none;
	color: white;
}
a:HOVER {
	text-decoration: underline;
	color: white;
}


button {
background-color:E6E6E6;
color:#2E2E2E;
padding:2px;
text-decoration:none;
}

hr{
	border:0px;
	 color: #d4d4d4;
	background-color: #d4d4d4;
	height: 5px;
  width: 82%;
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
	<div id="topbar"
		style="  top: 136px; left: 20px; padding: 3px; background-color: #CCCCCC; z-index: 1;">
		<font color="#4C4C4C" >Diagram:</font> <select id="diagrams" style="width: 200px"
			onchange="diagramSelected();">
			<option value="default">New diagram...</option>
		</select>
		<button id="deleteButton" onclick="deleteDiagram();" disabled>Delete</button>
		<button id="changeNameButton" onclick="changeDiagramName();" disabled>Change
			name</button>
		<button onclick="saveDiagram(false, $('#diagrams').val());">Save</button>
		<button onclick="clearAll();">Clear all</button>
		<button onclick="validateDiagram();">Validate</button>
		<button onclick="saveDiagram(true, $('#diagrams').val());">Run</button>
		<a href="<c:url value='logout' />">Logout</a>
	</div>
	<div id="content" style="background-color: #EEEEEE; height: 100%;">
		<div id="sidebar"
			style="float: left; position: absolute; top: 210px; border: 4px solid; border-color: #d4d4d4; height: 120px; background-color: white; z-index: 1;">
			<table>
				<tr>
					<td><div id="newStartEvent" title="Add new start event"
							style="background-image: url(./images/node.event.start.svg); background-size: cover;"
							class="draggable"></div></td>
					<td><div id="newEndEvent" title="Add new end event"
							style="background-image: url(./images/node.event.end.svg); background-size: cover;"
							class="draggable"></div></td>
				</tr>
				<tr>
					<td><div id="newParallelGateway"
							title="Add new parallel gateway" class="draggable"
							style="background-image: url(./images/node.gateway.and.svg); background-size: cover;"></div></td>
					<td><div id="newExclusiveGateway"
							title="Add new exclusive gateway" class="draggable"
							style="background-image: url(./images/node.gateway.exclusive.svg); background-size: cover;"></div></td>
				</tr>
				<tr>
					<td><div id="newUserTask" title="Add new user task"
							class="draggable"
							style="border: 2px solid; border-radius: 5px; overflow: hidden; padding-right: 5px; min-width: 25px; min-height: 25px; text-align: center; background-color: #FFFFFF;">
							<div id="text"
								style="text-align: center; width: 100%; vertical-align: middle; position: absolute; top: 50%; margin-top: -10px; margin-right: -5px; z-index: 0;"></div>
						</div></td>
					<td>
						<!-- a id="newInclGateway" title="Add new inclusive gateway"
					class="draggable"><embed
							src="images/connection.svg"
							style="position: relative;"></a -->
					</td>

				</tr>
			</table>
		</div>
		<div id="canvas" class="droppable"
			style="position: relative; top: 18px; margin-left: 90px; min-width: 200px; margin-right: auto; height: 90%; width: 90%; z-index: 0; overflow: hidden; background-color: white">
		</div>

	</div>
	<div id="dialog-name-form" title="Set name">
		<p class="validateTips">Set name for the selected flow object.</p>
		<form>
			<fieldset>
				<label for="name">Name</label> <input type="text" name="name"
					id="name" class="text ui-widget-content ui-corner-all" />
			</fieldset>
		</form>
	</div>
	<div id="dialog-diagram-name-form" title="Set diagram name">
		<p class="validateTips">Set name for the diagram.</p>
		<form>
			<fieldset>
				<label for="diagram_name">Name</label> <input type="text"
					name="diagram_name" id="diagram_name"
					class="text ui-widget-content ui-corner-all" />
			</fieldset>
		</form>
	</div>
	<div id="dialog-description-form" title="Set description">
		<p class="validateTips">Set description for the selected user
			task.</p>
		<form>
			<fieldset>
				<label for="description">Description</label> <input type="text"
					name="description" id="description"
					class="text ui-widget-content ui-corner-all" />
			</fieldset>
		</form>
	</div>
	<div id="dialog-user-form" title="Set new user">
		<p class="validateTips">All form fields are required.</p>
		<form>
			<fieldset>
				<label for="userName">User name</label> <input type="text"
					name="userName" id="userName"
					class="text ui-widget-content ui-corner-all" style="width: 100%;" />
				<label for="email">Email</label> <input type="text" name="email"
					id="email" value="" class="text ui-widget-content ui-corner-all"
					style="width: 100%;" />
			</fieldset>
		</form>
	</div>
	<div id="dialog-condition-form" title="Set conditions">
		<p class="validateTips">Set conditions for the sequence flows.</p>
		<table id="connections" class="ui-widget ui-widget-content"
			style="width: 100%;">
			<thead>
				<tr class="ui-widget-header ">
					<th>FlowObject name</th>
					<th>UserTask</th>
					<th>Option</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>John Doe</td>
					<td><select>
							<option disabled>Default</option>
							<option value="value1">Value1</option>
							<option value="value2">Value2</option>
							<option value="value3">Value3</option>
							<option value="value4">Value4</option>
					</select></td>
					<td><button id="create-user">Create new user</button></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="dialog-options-form" title="Set options">
		<p class="validateTips">Set possible options for the user task.</p>
		<table id="options" class="ui-widget ui-widget-content"
			style="width: 100%;">
			<thead>
				<tr class="ui-widget-header ">
					<th style="width: 100%;">Options</th>
					<th style="min-width: 80px;"></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<button id="add-option">Add option</button>
	</div>
	<div class="contextMenu" id="flowObjectMenu">
		<ul>
			<li id="setName">Set name</li>
			<li id="setDescription">Set description</li>
			<li id="setUser">Set user</li>
			<li id="setOptions">Options</li>
			<li id="setConditions">Conditions</li>
			<li id="delete">Delete</li>
		</ul>
	</div>
	<div class="ui-loader-background"></div>

	</section>
	>
</body>
</html>