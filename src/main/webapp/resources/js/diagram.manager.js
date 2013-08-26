function objToString(obj) {
	var str = '';
	for ( var p in obj) {
		if (obj.hasOwnProperty(p)) {
			str += p + '::' + obj[p] + '\n';
		}
	}
	return str;
}

var obj = null;
var clickedItem = null;
var index = {
	"StartEvent" : 1,
	"EndEvent" : 1,
	"ExclusiveGateway" : 1,
	"ParallelGateway" : 1,
	"UserTask" : 1
};
var optionHolder = {};
var exclusiveHolder = {};
var optionIndex = 0;
var labelOverlay = [ "Label", {
	location : 0.1,
	label : "/",
	id : "defaultLabel",
	cssClass : "defaultArrowText"
} ];
var arrowOverlay = [ "Arrow", {
	width : 10,
	location : 1,
	id : "arrow",
	length : 10,
	foldback : 0.8
} ];

function getTextOverlay(text) {
	return [ "Label", {
		label : text,
		location : 0.5,
		id : "condition",
		cssClass : "condition"
	} ];
}

function clearAll() {
	index = {
		"StartEvent" : 1,
		"EndEvent" : 1,
		"ExclusiveGateway" : 1,
		"ParallelGateway" : 1,
		"UserTask" : 1
	};
	optionHolder = {};
	exclusiveHolder = {};
	var connections = jsPlumb.getConnections();
	for ( var i in connections)
		jsPlumb.detach(connections[i]);
	$("div.draggable").remove(":not([id^='new'])");
}

function isString(obj) {
	return toString.call(obj) == '[object String]';
}

function loadDiagramList() {
	$.getJSON('getDiagramList', function(data) {
		if (data == null)
			return;
		var diagramList = $("#diagrams");
		for ( var p in data) {
			diagramList.append("<option value='" + data[p].key + "'>"
					+ data[p].value + "</option>");
		}
	});
}

function diagramSelected() {
	var id = $("#diagrams").val();
	if (id != "default") {
		loadDiagram(id);
		$("#deleteButton").prop('disabled', false);
		$("#changeNameButton").prop('disabled', false);
	} else {
		clearAll();
		$("#deleteButton").prop('disabled', true);
		$("#changeNameButton").prop('disabled', true);
	}
}

function loadDiagram(id) {
	clearAll();
	$
			.getJSON(
					'getjson?id=' + id,
					function(data) {
						if (data == null) {
							alert("Diagram with this name no longer exists.");
							return;
						}
						for ( var p in data.nodes) {
							$("div.droppable").append(
									cloneItem($("[id^=new" + data.nodes[p].type
											+ "]"), data.nodes[p].id,
											data.nodes[p].x, data.nodes[p].y,
											data.nodes[p].name));
							if (data.nodes[p].type == "UserTask") {
								if (data.nodes[p].options != null) {
									optionHolder[data.nodes[p].id] = data.nodes[p].options;
								}
								var userTask = $("#" + data.nodes[p].id);
								if (data.nodes[p].userAssigned != null) {
									userTask.attr("username",
											data.nodes[p].userAssigned.name);
									userTask.attr("email",
											data.nodes[p].userAssigned.email);
								}
								if (data.nodes[p].description != null) {
									userTask.attr("title",
											data.nodes[p].description);
								}
							}
						}
						for ( var p in data.connections) {
							jsPlumb.connect({
								source : data.connections[p].sourceId,
								target : data.connections[p].targetId
							});
							if (data.connections[p].sourceId
									.indexOf("ExclusiveGateway") >= 0
									&& data.connections[p].condition != null) {
								if (exclusiveHolder[data.connections[p].sourceId] == null)
									exclusiveHolder[data.connections[p].sourceId] = {};
								exclusiveHolder[data.connections[p].sourceId][data.connections[p].targetId] = {
									userTask : data.connections[p].condition.userTaskId,
									option : data.connections[p].condition.option
								};
								repaintConnection(data.connections[p].sourceId,
										data.connections[p].targetId);
							}
						}
					});
};

function getStandartNode(item) {
	var node = {};
	node["name"] = item.title;
	node["id"] = item.id;
	node["x"] = item.offsetLeft;
	node["y"] = item.offsetTop;
	return node;
}

function getCurrentDiagram() {
	var diagram = {
		nodes : [],
		connections : [],
		name : $("#diagrams").find(":selected").text()
	};
	var nodes = $("[id^=StartEvent]");
	for ( var index = 0; index < nodes.length; index++) {
		var node = getStandartNode(nodes[index]);
		node["type"] = "StartEvent";
		diagram.nodes[diagram.nodes.length] = node;
	}
	var nodes = $("[id^=EndEvent]");
	for ( var index = 0; index < nodes.length; index++) {
		var node = getStandartNode(nodes[index]);
		node["type"] = "EndEvent";
		diagram.nodes[diagram.nodes.length] = node;
	}
	var nodes = $("[id^=ParallelGateway]");
	for ( var index = 0; index < nodes.length; index++) {
		var node = getStandartNode(nodes[index]);
		node["type"] = "ParallelGateway";
		diagram.nodes[diagram.nodes.length] = node;
	}
	var nodes = $("[id^=ExclusiveGateway]");
	for ( var index = 0; index < nodes.length; index++) {
		var node = getStandartNode(nodes[index]);
		node["type"] = "ExclusiveGateway";
		diagram.nodes[diagram.nodes.length] = node;
	}
	var nodes = $("[id^=UserTask]");
	for ( var index = 0; index < nodes.length; index++) {
		var node = {};
		var item = nodes[index];
		node["name"] = $("#" + item.id + " #text").text();
		node["type"] = "UserTask";
		node["id"] = item.id;
		node["x"] = item.offsetLeft;
		node["y"] = item.offsetTop;
		node["description"] = item.title;
		if (item.hasAttribute("username") && item.hasAttribute("email"))
			node["userAssigned"] = {
				"name" : item.getAttribute("username"),
				"email" : item.getAttribute("email")
			};
		if (optionHolder[item.id] != null)
			node["options"] = optionHolder[item.id];
		diagram.nodes[diagram.nodes.length] = node;
	}

	var connections = jsPlumb.getConnections();
	for ( var i in connections) {
		var connection = {};
		connection["sourceId"] = connections[i].sourceId;
		connection["targetId"] = connections[i].targetId;
		if (exclusiveHolder[connections[i].sourceId] != null
				&& exclusiveHolder[connections[i].sourceId][connections[i].targetId] != null)
			connection["condition"] = {
				"userTaskId" : exclusiveHolder[connections[i].sourceId][connections[i].targetId].userTask,
				"option" : exclusiveHolder[connections[i].sourceId][connections[i].targetId].option
			};
		diagram.connections[diagram.connections.length] = connection;
	}
	return diagram;
}

function isCorrectDiagram() {
	var exclusiveGateways = $("[id^=ExclusiveGateway]");
	for ( var index = 0; index < exclusiveGateways.length; index++) {
		var outgoingConnections = jsPlumb.getConnections({
			source : exclusiveGateways[index].id
		});
		var incomingConnections = jsPlumb.getConnections({
			target : exclusiveGateways[index].id
		});
		if (outgoingConnections.length == 0) {
			alert("Could not save the diagram!\n\nExclusive gateway '"
					+ exclusiveGateways[index].title
					+ "' should have at least one outgoing sequence flow.");
			return false;
		}
		if (incomingConnections.length == 0) {
			alert("Could not save the diagram!\n\nExclusive gateway '"
					+ exclusiveGateways[index].title
					+ "' should have at least one incoming sequence flow.");
			return false;
		}
		var defaultCounter = 0;
		for ( var i in outgoingConnections) {
			if (exclusiveHolder[exclusiveGateways[index].id] == null
					|| exclusiveHolder[exclusiveGateways[index].id][outgoingConnections[i].targetId] == null)
				defaultCounter++;
		}
		if (defaultCounter > 1
				|| (defaultCounter == 0 && outgoingConnections.length > 1)) {
			alert("Could not save the diagram!\n\nExclusive gateway '"
					+ exclusiveGateways[index].title
					+ "' should have one and only one default outgoing sequence flow.");
			return false;
		}
	}
	var parallelGateways = $("[id^=ParallelGateway]");
	for ( var index = 0; index < parallelGateways.length; index++) {
		var outgoingConnections = jsPlumb.getConnections({
			source : parallelGateways[index].id
		});
		var incomingConnections = jsPlumb.getConnections({
			target : parallelGateways[index].id
		});
		if (outgoingConnections.length == 0) {
			alert("Could not save the diagram!\n\nParallel gateway '"
					+ parallelGateways[index].title
					+ "' should have at least one outgoing sequence flow.");
			return false;
		}
		if (incomingConnections.length == 0) {
			alert("Could not save the diagram!\n\nParallel gateway '"
					+ parallelGateways[index].title
					+ "' should have at least one incoming sequence flow.");
			return false;
		}
	}
	return true;
}

function isEmpty(map) {
	for ( var key in map) {
		if (map.hasOwnProperty(key)) {
			return false;
		}
	}
	return true;
}

function validateDiagram() {
	if (!isCorrectDiagram())
		return false;
	var id = $("#diagrams").val();
	if (id == "default") {
		alert("You should save new diagram first.");
		return false;
	}
	$.ajax({
		url : 'welcome?id=' + $("#diagrams").val(),
		type : 'POST',
		// dataType: 'json',
		data : {
			json : JSON.stringify(getCurrentDiagram())
		},
		// contentType: 'application/json',
		success : function(result) {
			$.getJSON('validate?id=' + $("#diagrams").val(), function(data) {
				if (data == null) {
					return;
					alert("Server doesn't respond.");
				}
				var alertString = "Validation result:";
				if (!isEmpty(data.errors))
					alertString += "\n\nErrors:\n" + objToString(data.errors);
				if (!isEmpty(data.warnings))
					alertString += "\n\nWarnings:\n"
							+ objToString(data.warnings);
				if (isEmpty(data.errors) && isEmpty(data.warnings))
					alertString += "\n\nNo errors and warnings were found!";
				alert(alertString);
			});
		}
	});
	return true;
}

function changeDiagramName() {
	$(".validateTips").text("Set name for the diagram.");
	$("#diagram_name").val($("#diagrams").find(":selected").text());
	$("#dialog-diagram-name-form").dialog("open");
}

function deleteDiagram() {
	var id = $("#diagrams").val();
	if (id == "default") {
		return;
	}
	if (!confirm('Do you realy want to delete this diagram?'))
		return;
	$.blockUI();
	$.ajax({
		url : 'delete?id=' + $("#diagrams").val(),
		type : 'GET',
		// dataType: 'json',
		// contentType: 'application/json',
		success : function(result) {
			$.unblockUI();
			if (result == "ok") {
				alert("Diagram was successfully deleted!");
				$("#diagrams").find(":selected").remove();
				clearAll();
			}
		}
	});
}

function saveDiagram(run) {
	if (!isCorrectDiagram())
		return false;
	var id = $("#diagrams").val();
	if (id == "default") {
		if (!run) {
			changeDiagramName();
		} else {
			alert("You should save new diagram first");
		}
		return false;
	}
	$.blockUI();
	$.ajax({
		url : 'welcome?id=' + $("#diagrams").val(),
		type : 'POST',
		// dataType: 'json',
		data : {
			json : JSON.stringify(getCurrentDiagram())
		},
		// contentType: 'application/json',
		success : function(result) {
			if (run) {
				$.getJSON('run?id=' + $("#diagrams").val(), function(data) {
					$.unblockUI();
					if (data == null) {
						return;
						alert("Server doesn't respond.");
					}
					var alertString = data.isRunning ? "Diagram is running!"
							: "Diagram has some errors!";
					if (!isEmpty(data.errors))
						alertString += "\n\nErrors:\n"
								+ objToString(data.errors);
					if (!isEmpty(data.warnings))
						alertString += "\n\nWarnings:\n"
								+ objToString(data.warnings);
					alert(alertString);
				});
			} else {
				$.unblockUI();
				if (result != null)
					$("#diagrams").find(":selected").val(result);
			}
		}

	});
	return true;
};

function getUserTasksWithOptions(selectedItem) {
	var result = "";
	var userTasks = $("[id^='UserTask']");
	for ( var i = 0; i < userTasks.length; i++) {
		if (optionHolder[userTasks[i].id] != null
				&& optionHolder[userTasks[i].id].length > 0)
			result += "<option "
					+ (selectedItem == userTasks[i].id ? "selected value='"
							: "value='") + userTasks[i].id + "'>"
					+ $("#" + userTasks[i].id + " #text").text() + "</option>";
	}
	return result;
}

function deleteOption(i) {
	$("#optionHolder" + i).remove();
}

function setOptions(item) {
	$('#options tbody').empty();
	if (optionHolder[item.attr("id")] != null
			&& optionHolder[item.attr("id")].length > 0)
		for ( var i in optionHolder[item.attr("id")]) {
			$('#options tbody')
					.append(
							"<tr id='optionHolder"
									+ i
									+ "'><td><input type='text' name='option' id='optionText' style=\"width:100%;\" class='text ui-widget-content ui-corner-all' value='"
									+ optionHolder[item.attr("id")][i]
									+ "'/></td><td><button onclick='deleteOption("
									+ i + ");'>Delete</button></td></tr>");
		}
}

function setSpecificOptions(i, selectedItem) {
	var item = $('#optionSelection' + i);
	item.empty();
	var selectedUserTask = $("#selection" + i + " :selected").val();
	if (selectedUserTask == "default")
		item.append("<option disabled>None</option>");
	else
		for ( var i in optionHolder[selectedUserTask])
			item
					.append("<option "
							+ (selectedItem == optionHolder[selectedUserTask][i] ? "selected value='"
									: "value='")
							+ optionHolder[selectedUserTask][i] + "'>"
							+ optionHolder[selectedUserTask][i] + "</option>");
}

function setConditions(item) {
	var connections = jsPlumb.getConnections({
		source : item.attr("id")
	});
	if (connections.length <= 0)
		return false;
	$('#connections tbody').empty();
	for ( var i in connections) {
		var targetId = connections[i].targetId;
		var name = "";// targetId + ":";
		if (targetId.indexOf("UserTask") >= 0)
			name += connections[i].target.children("#text").text();
		else
			name += connections[i].target.attr("title");
		if (exclusiveHolder[item.attr("id")] != null
				&& exclusiveHolder[item.attr("id")][targetId] != null) {
			$('#connections tbody')
					.append(
							"<tr><td>"
									+ name
									+ "</td><td><select onchange='setSpecificOptions(\""
									+ targetId
									+ "\", null);' id='selection"
									+ targetId
									+ "'><option value='default'>Default</option>"
									+ getUserTasksWithOptions(exclusiveHolder[item
											.attr("id")][targetId].userTask)
									+ "</select></td><td><select onchange='' id='optionSelection"
									+ targetId + "'></select></td></tr>");
			setSpecificOptions(targetId,
					exclusiveHolder[item.attr("id")][targetId].option);
		} else {
			$('#connections tbody')
					.append(
							"<tr><td>"
									+ name
									+ "</td><td><select onchange='setSpecificOptions(\""
									+ targetId
									+ "\", null);' id='selection"
									+ targetId
									+ "'><option value='default'>Default</option>"
									+ getUserTasksWithOptions(null)
									+ "</select></td><td><select onchange='' id='optionSelection"
									+ targetId + "'></select></td></tr>");
			setSpecificOptions(targetId, null);
		}
	}
	return true;
}

function cloneItem(itemToClone, id, x, y, name) {
	var item = itemToClone.clone();
	item.attr("class", "draggable");
	item.attr("id", id);
	item.attr("oncontextmenu", "return false;");
	if (item.attr("id").indexOf("UserTask") >= 0) {
		item.children("#text").text(name);
		item.attr("title", "");
		item.resizable({
			aspectRatio : 3 / 2,
			handles : "se",
			resize : function(e, ui) {
				jsPlumb.repaint(ui.helper);
			}
		});
		item.css({
			"position" : "absolute",
			"top" : y + "px",
			"left" : x + "px",
			"width" : "75px",
			"height" : "50px"
		});
	} else {
		item.attr("title", (name == null) ? id : name);
		item.css({
			"position" : "absolute",
			"top" : y + "px",
			"left" : x + "px"
		});
	}
	item
			.contextMenu(
					'flowObjectMenu',
					{
						bindings : {
							'setName' : function(t) {
								if (clickedItem.attr("id").indexOf("UserTask") >= 0)
									$("#name").val(
											clickedItem.children("#text")
													.text());
								else
									$("#name").val(clickedItem.attr("title"));
								$(".validateTips")
										.text(
												"Set name for the selected flow object.");
								$("#dialog-name-form").dialog("open");
							},
							'setUser' : function(t) {
								$("#userName")
										.val(clickedItem.attr("username"));
								$("#email").val(clickedItem.attr("email"));
								$(".validateTips").text(
										"All form fields are required.");
								$("#dialog-user-form").dialog("open");
							},
							'setDescription' : function(t) {
								$("#description")
										.val(clickedItem.attr("title"));
								$(".validateTips")
										.text(
												"Set description for the selected user task.");
								$("#dialog-description-form").dialog("open");
							},
							'setConditions' : function(t) {
								if (setConditions(item)) {
									$(".validateTips")
											.text(
													"Set conditions for the sequence flows.");
									$("#dialog-condition-form").dialog("open");
								} else
									alert("This exclusive gateway has no outgoing sequence flows.");
							},
							'setOptions' : function(t) {
								setOptions(item);
								$(".validateTips")
										.text(
												"Set possible options for the user task.");
								$("#dialog-options-form").dialog("open");
							},
							'delete' : function(t) {
								if (clickedItem.attr("id").indexOf("UserTask") >= 0
										&& optionHolder[clickedItem.attr("id")] != null
										&& optionHolder[clickedItem.attr("id")].length > 0) {
									for ( var i in exclusiveHolder)
										for ( var j in exclusiveHolder[i])
											if (exclusiveHolder[i][j] != null
													&& exclusiveHolder[i][j].userTask == clickedItem
															.attr("id")) {
												exclusiveHolder[i][j] = null;
												repaintConnection(i, j);
											}
									optionHolder[clickedItem.attr("id")] = null;
								} else if (clickedItem.attr("id").indexOf(
										"ExclusiveGateway") >= 0)
									exclusiveHolder[clickedItem.attr("id")] = null;
								for ( var i in exclusiveHolder) {
									if (exclusiveHolder[i] != null
											&& exclusiveHolder[i][clickedItem
													.attr("id")] != null)
										exclusiveHolder[i][clickedItem
												.attr("id")] = null;
								}
								jsPlumb.detachAllConnections(t.id);
								clickedItem.remove();
							}
						},
						onShowMenu : function(e, menu) {
							if ($(e.target).attr('id') == 'text'
									|| $(e.target).attr('id') == 'jsPlumb_1_1')
								clickedItem = $(e.target).parent();
							else
								clickedItem = $(e.target);
							if (clickedItem.attr('id').indexOf(
									"ExclusiveGateway") >= 0) {
								$('#setDescription, #setOptions, #setUser',
										menu).remove();
							} else if (clickedItem.attr('id').indexOf(
									"UserTask") >= 0) {
								$('#setConditions', menu).remove();
							} else {
								$(
										'#setDescription, #setOptions, #setConditions, #setUser',
										menu).remove();
							}
							return menu;
						}
					});
	var dot = $("<div class='ep' id='jsPlumb_1_1'></div>");
	item.append(dot);
	jsPlumb.makeSource(dot, {
		parent : item,
		anchor : "Continuous",
		connector : [ "Flowchart", {
			stub : 2
		} ],
		connectorStyle : {
			strokeStyle : "#000",
			lineWidth : 2,
			outlineColor : "white",
			outlineWidth : 1
		}
	});
	jsPlumb.makeTarget(item, {
		anchor : "Continuous",
		isSource : false
	});
	jsPlumb.draggable(item, {
		containment : "parent"
	});
	return item;
}

function repaintConnection(sourceId, targetId) {
	var connection = jsPlumb.getConnections({
		source : sourceId,
		target : targetId
	});
	if (exclusiveHolder[sourceId] != null
			&& exclusiveHolder[sourceId][targetId] != null) {
		if (connection.length > 0) {
			connection[0].removeAllOverlays();
			connection[0].addOverlay(arrowOverlay);
			connection[0]
					.addOverlay(getTextOverlay(exclusiveHolder[sourceId][targetId].option));
		}
	} else if (connection.length > 0) {
		connection[0].removeAllOverlays();
		connection[0].addOverlay(labelOverlay);
		connection[0].addOverlay(arrowOverlay);
	}
}

$(document)
		.ready(
				function() {
					var userName = $("#userName"), email = $("#email"), name = $("#name"), diagramName = $("#diagram_name"), description = $("#description"), allFields = $(
							[]).add(name).add(description).add(userName).add(
							email).add(diagramName), tips = $(".validateTips");
					function updateTips(t) {
						tips.text(t).addClass("ui-state-highlight");
						setTimeout(function() {
							tips.removeClass("ui-state-highlight", 1500);
						}, 500);
					}

					function checkLength(o, n, min, max) {
						if (o.val().length > max || o.val().length < min) {
							o.addClass("ui-state-error");
							updateTips("Length of " + n + " must be between "
									+ min + " and " + max + ".");
							return false;
						} else {
							return true;
						}
					}

					function checkRegexp(o, regexp, n) {
						if (!(regexp.test(o.val()))) {
							o.addClass("ui-state-error");
							updateTips(n);
							return false;
						} else {
							return true;
						}
					}
					$("#dialog-name-form")
							.dialog(
									{
										autoOpen : false,
										height : 400,
										width : 450,
										modal : true,
										buttons : {
											"Ok" : function() {
												var bValid = true;
												allFields
														.removeClass("ui-state-error");

												bValid = bValid
														&& checkLength(name,
																"name", 2, 16);
												bValid = bValid
														&& checkRegexp(
																name,
																/^[a-z]([0-9a-zA-Z_])+$/i,
																"Name may consist of a-z, 0-9, underscores and begin with a letter.");
												if (clickedItem.attr("id")
														.indexOf("UserTask") >= 0) {
													var userTasks = $("[id^='UserTask']");
													for ( var index = 0; index < userTasks.length; index++) {
														if (bValid
																&& userTasks[index].id != clickedItem
																		.attr("id")
																&& $(
																		"#"
																				+ userTasks[index].id
																				+ " #text")
																		.text() == name
																		.val()) {
															updateTips("User task with this name already exists. Chose another name.");
															bValid = false;
														}
													}
												}

												if (bValid) {
													if (clickedItem
															.attr("id")
															.indexOf("UserTask") >= 0) {
														clickedItem.children(
																"#text").text(
																name.val());
													} else {
														clickedItem.attr(
																"title", name
																		.val());
													}
													$(this).dialog("close");
												}
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											allFields.val("").removeClass(
													"ui-state-error");
										}
									});
					$("#dialog-diagram-name-form")
							.dialog(
									{
										autoOpen : false,
										height : 300,
										width : 400,
										modal : true,
										buttons : {
											"Ok" : function() {
												var bValid = true;
												allFields
														.removeClass("ui-state-error");

												bValid = bValid
														&& checkLength(
																diagramName,
																"name", 2, 16);
												bValid = bValid
														&& checkRegexp(
																diagramName,
																/^[a-z]([0-9a-zA-Z_])+$/i,
																"Name may consist of a-z, 0-9, underscores and begin with a letter.");

												if (bValid) {
													var list = $("#diagrams");
													if (list.val() == "default") {
														list
																.append("<option value='noid'>"
																		+ diagramName
																				.val()
																		+ "</option>");
														list.val('noid');
													} else {
														list
																.find(
																		":selected")
																.text(
																		diagramName
																				.val());
													}
													saveDiagram(false);
													$(this).dialog("close");
												}
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											allFields.val("").removeClass(
													"ui-state-error");
										}
									});
					$("#dialog-user-form")
							.dialog(
									{
										autoOpen : false,
										height : 350,
										width : 400,
										modal : true,
										buttons : {
											"Ok" : function() {
												var bValid = true;
												allFields
														.removeClass("ui-state-error");

												bValid = bValid
														&& checkLength(
																userName,
																"name", 2, 16);
												bValid = bValid
														&& checkLength(email,
																"email", 6, 80);
												bValid = bValid
														&& checkRegexp(
																userName,
																/^[a-zA-Z]([a-zA-Z])+$/i,
																"User name may consist of characters only.");
												bValid = bValid
														&& checkRegexp(
																email,
																/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i,
																"eg. ui@jquery.com");

												if (bValid) {
													clickedItem.attr(
															"username",
															userName.val());
													clickedItem.attr("email",
															email.val());
													$(this).dialog("close");
												}
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											allFields.val("").removeClass(
													"ui-state-error");
										}
									});
					$("#dialog-description-form")
							.dialog(
									{
										autoOpen : false,
										height : 300,
										width : 400,
										modal : true,
										buttons : {
											"Ok" : function() {
												var bValid = true;
												allFields
														.removeClass("ui-state-error");

												if (bValid) {
													clickedItem.attr("title",
															description.val());
													$(this).dialog("close");
												}
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											allFields.val("").removeClass(
													"ui-state-error");
										}
									});
					$("#dialog-condition-form")
							.dialog(
									{
										autoOpen : false,
										height : 350,
										width : 750,
										modal : true,
										buttons : {
											"Ok" : function() {
												var bValid = true;
												allFields
														.removeClass("ui-state-error");

												var selections = $("[id^='selection']");
												exclusiveHolder[clickedItem
														.attr('id')] = {};
												for ( var i = 0; i < selections.length; i++) {
													var targetId = selections[i].id
															.substr(9);
													var connection = jsPlumb
															.getConnections({
																source : clickedItem
																		.attr("id"),
																target : targetId
															});
													if (selections[i].value != "default") {
														exclusiveHolder[clickedItem
																.attr('id')][targetId] = {
															userTask : selections[i].value,
															option : $(
																	"#optionSelection"
																			+ targetId)
																	.val()
														};
														if (connection.length > 0) {
															connection[0]
																	.removeAllOverlays();
															connection[0]
																	.addOverlay(arrowOverlay);
															connection[0]
																	.addOverlay(getTextOverlay(exclusiveHolder[clickedItem
																			.attr('id')][targetId].option));
														}
													} else if (connection.length > 0) {
														connection[0]
																.removeAllOverlays();
														connection[0]
																.addOverlay(labelOverlay);
														connection[0]
																.addOverlay(arrowOverlay);
													}
												}

												if (bValid) {
													// clickedItem.attr("title",
													// description.val());
													$(this).dialog("close");
												}
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											$('#connections tbody').empty();
										}
									});
					$("#dialog-options-form")
							.dialog(
									{
										autoOpen : false,
										height : 350,
										width : 450,
										modal : true,
										buttons : {
											"Ok" : function() {
												allFields
														.removeClass("ui-state-error");
												var options = $('[id^="optionText"]');

												if (options.length == 1) {
													updateTips("There should be no options or more than one.");
													return;
												}

												for ( var i = 0; i < options.length; i++)
													if (options[i].value == "") {
														updateTips("Options should not be empty.");
														return;
													}

												for ( var i = 0; i < options.length - 1; i++)
													for ( var j = i + 1; j < options.length; j++)
														if (options[i].value == options[j].value) {
															updateTips("Options should not be equal");
															return;
														}

												if (optionHolder[clickedItem
														.attr("id")] == null)
													optionHolder[clickedItem
															.attr("id")] = new Array();

												for ( var i = 0; i < options.length; i++) {
													optionHolder[clickedItem
															.attr("id")][i] = options[i].value;
												}

												for ( var i in exclusiveHolder)
													for ( var j in exclusiveHolder[i])
														if (exclusiveHolder[i][j] != null
																&& exclusiveHolder[i][j].userTask == clickedItem
																		.attr("id")
																&& optionHolder[clickedItem
																		.attr("id")]
																		.indexOf(exclusiveHolder[i][j].option) < 0) {
															exclusiveHolder[i][j] = null;
															repaintConnection(
																	i, j);
														}

												$(this).dialog("close");
											},
											Cancel : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
											$('#options tbody').empty();
										}
									});
					$("#add-option")
							.button()
							.click(
									function() {
										if (optionIndex < $("[id^=optionHolder]").length)
											optionIndex = $("[id^=optionHolder]").length;
										var i = optionIndex++;
										$('#options tbody')
												.append(
														"<tr id='optionHolder"
																+ i
																+ "'><td><input type='text' name='option' id='optionText"
																+ i
																+ "' style=\"width:100%;\" class='text ui-widget-content ui-corner-all'/></td><td><button onclick='deleteOption("
																+ i
																+ ");'>Delete</button></td></tr>");
									});
					jsPlumb
							.bind(
									"connection",
									function(CurrentConnection) {
										var connections = jsPlumb
												.getConnections({
													source : CurrentConnection.connection.sourceId,
													target : CurrentConnection.connection.targetId
												});
										if (CurrentConnection.connection.targetId == CurrentConnection.connection.sourceId
												|| connections.length > 1)
											jsPlumb.detach(CurrentConnection);
										else if (CurrentConnection.connection.sourceId
												.indexOf("ExclusiveGateway") >= 0) {
											CurrentConnection.connection
													.addOverlay(labelOverlay);
										}
									});
					jsPlumb.importDefaults({
						Endpoint : [ "Blank", {
							radius : 3
						} ],
						HoverPaintStyle : {
							strokeStyle : "#42a62c",
							lineWidth : 5
						},
						ConnectionOverlays : [ arrowOverlay // , [ "Label", {
															// label:"/",
						// location:0.1,
						// id:"defaultLabel", cssClass:"defaultArrowText" } ]
						]
					});
					jsPlumb
							.bind(
									"click",
									function(CurrentConnection) {
										if (CurrentConnection.sourceId
												.indexOf("ExclusiveGateway") >= 0
												&& exclusiveHolder[CurrentConnection.sourceId] != null) {
											exclusiveHolder[CurrentConnection.sourceId][CurrentConnection.targetId] = null;
										}
										jsPlumb.detach(CurrentConnection);
									});
					$("div.draggable").draggable({
						cursor : "move",
						helper : "clone",
						revert : "invalid"
					});
					$("div.droppable")
							.droppable(
									{
										accept : "div.draggable",
										drop : function(event, ui) {
											var item;
											if (ui.draggable.attr("id")
													.indexOf("new") < 0) {
												item = ui.draggable;
											} else {
												var type = ui.draggable.attr(
														"id").substr(3);
												var id = type + index[type]++;
												while ($("#" + id).length > 1)
													id = type + index[type]++;
												item = cloneItem(ui.draggable,
														id,
														ui.position.left - 100,
														ui.position.top, id);
											}
											/*
											 * item.resizable({ aspectRatio : 1,
											 * handles : "all", maxHeight : 300,
											 * maxWidth : 300, minHeight : 10,
											 * minWidth : 10 });
											 */
											$(this).append(item);
										}
									});
					// $( "#sidebar" ).draggable();
					loadDiagramList();
				});