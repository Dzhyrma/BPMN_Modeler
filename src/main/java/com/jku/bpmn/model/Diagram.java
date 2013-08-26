package com.jku.bpmn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.jku.bpmn.model.flowobject.AbstractFlowObject;
import com.jku.bpmn.model.flowobject.EndEvent;
import com.jku.bpmn.model.flowobject.ExclusiveGateway;
import com.jku.bpmn.model.flowobject.ParallelGateway;
import com.jku.bpmn.model.flowobject.StartEvent;
import com.jku.bpmn.model.flowobject.UserTask;
import com.jku.bpmn.model.json.JSONCondition;
import com.jku.bpmn.model.json.JSONConnection;
import com.jku.bpmn.model.json.JSONDiagram;
import com.jku.bpmn.model.json.JSONNode;
import com.jku.bpmn.util.ProcessManager;
import com.jku.bpmn.util.ProcessThread;

public class Diagram {

	public static Diagram convertFromJSONDiagram(JSONDiagram diagram) {
		Diagram result = new Diagram();
		result.setName(diagram.getName());
		result.setId(diagram.getId());
		
		Map<String, AbstractFlowObject> idMap = new HashMap<String, AbstractFlowObject>();

		for (JSONNode node : diagram.getNodes()) {
			if (node == null)
				continue;
			if (node.getType().equals("StartEvent")) {
				StartEvent startEvent = new StartEvent();
				startEvent.setName(node.getName());
				startEvent.setX(node.getX());
				startEvent.setY(node.getY());
				idMap.put(node.getId(), startEvent);
				result.flowObjects.add(startEvent);
			} else if (node.getType().equals("EndEvent")) {
				EndEvent endEvent = new EndEvent();
				endEvent.setName(node.getName());
				endEvent.setX(node.getX());
				endEvent.setY(node.getY());
				idMap.put(node.getId(), endEvent);
				result.flowObjects.add(endEvent);
			} else if (node.getType().equals("ParallelGateway")) {
				ParallelGateway parallelGateway = new ParallelGateway();
				parallelGateway.setName(node.getName());
				parallelGateway.setX(node.getX());
				parallelGateway.setY(node.getY());
				idMap.put(node.getId(), parallelGateway);
				result.flowObjects.add(parallelGateway);
			} else if (node.getType().equals("ExclusiveGateway")) {
				ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
				exclusiveGateway.setName(node.getName());
				exclusiveGateway.setX(node.getX());
				exclusiveGateway.setY(node.getY());
				idMap.put(node.getId(), exclusiveGateway);
				result.flowObjects.add(exclusiveGateway);
			} else if (node.getType().equals("UserTask")) {
				UserTask userTask = new UserTask(result.conditionHolder);
				userTask.setName(node.getName());
				userTask.setX(node.getX());
				userTask.setY(node.getY());
				userTask.setDescription(node.getDescription());
				userTask.setUser(node.getPairUserAssigned());
				if (node.getOptions() != null)
					for (String option : node.getOptions())
						userTask.addOption(option);
				idMap.put(node.getId(), userTask);
				result.flowObjects.add(userTask);
			}
		}

		for (JSONConnection connection : diagram.getConnections()) {
			if (connection == null
					|| !idMap.containsKey(connection.getSourceId())
					|| !idMap.containsKey(connection.getTargetId()))
				continue;
			if (connection.getSourceId().indexOf("ExclusiveGateway") >= 0) {
				ExclusiveGateway exclusiveGateway = (ExclusiveGateway) idMap
						.get(connection.getSourceId());
				JSONCondition condition = connection.getCondition();
				if (condition == null || condition.getUserTaskId() == null
						|| !idMap.containsKey(condition.getUserTaskId()))
					if (exclusiveGateway.hasDefaultOutgoingSequenceFlow())
						System.out
								.println("More than one default sequence flows were uploaded from client!");
					else
						exclusiveGateway.addDefaultOutgoingSequenceFlow(idMap
								.get(connection.getTargetId()));
				else
					exclusiveGateway.addOutgoingSequenceFlowWithCondition(
							idMap.get(connection.getTargetId()),
							(UserTask) idMap.get(condition.getUserTaskId()),
							condition.getOption());
			} else
				idMap.get(connection.getSourceId()).addOutgoingSequenceFlow(
						idMap.get(connection.getTargetId()));
		}
		return result;
	}
	private int id = 0;
	private String name = null;
	private Vector<AbstractFlowObject> flowObjects = new Vector<AbstractFlowObject>();

	private final ConditionHolder conditionHolder = new ConditionHolder();

	public JSONDiagram convertToJSONDiagram() {
		JSONDiagram result = new JSONDiagram(this.name);
		result.setId(this.id);

		for (AbstractFlowObject flowObject : this.flowObjects) {
			if (flowObject != null) {
				JSONNode node = new JSONNode();
				node.setX(flowObject.getX());
				node.setY(flowObject.getY());
				node.setId(flowObject.getStringId());
				node.setName(flowObject.getName());
				node.setType(flowObject.getClass().getSimpleName());
				if (flowObject.getClass() == UserTask.class) {
					UserTask userTask = (UserTask) flowObject;
					node.setDescription(userTask.getDescription());
					node.setOptions(userTask.getOptions());
					node.setPairUserAssigned(userTask.getUser());
				}
				result.addNode(node);
				String sourceId = flowObject.getStringId();

				for (AbstractFlowObject outgoingFlowObject : flowObject
						.getOutgoingFlowObjects()) {
					JSONConnection connection = new JSONConnection();
					connection.setSourceId(sourceId);
					connection.setTargetId(outgoingFlowObject.getStringId());
					if (flowObject.getClass() == ExclusiveGateway.class) {
						Pair<UserTask, String> condition = ((ExclusiveGateway) flowObject)
								.getCondition(outgoingFlowObject);
						if (condition != null)
							connection.setCondition(new JSONCondition(condition
									.getKey().getStringId(), condition
									.getValue()));
					}
					result.addConnection(connection);
				}
			}
		}
		return result;
	}

	public Map<String, List<String>> getErrors() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		int startEvents = 0;
		int endEvents = 0;
		for (AbstractFlowObject flowObject : this.flowObjects) {
			if (flowObject.getClass() == StartEvent.class)
				startEvents++;
			if (flowObject.getClass() == EndEvent.class)
				endEvents++;
		}
		List<String> diagramErrors = new ArrayList<String>();
		if (startEvents == 0)
			diagramErrors.add("No start events were found.");
		if (endEvents == 0)
			diagramErrors.add("No end events were found.");
		if (diagramErrors.size() > 0)
			result.put("Diagram", diagramErrors);
		for (AbstractFlowObject flowObject : this.flowObjects) {
			if (flowObject != null) {
				List<String> errors = flowObject.getErrors();
				if (errors.size() > 0)
					result.put("(" + flowObject.getClass().getSimpleName()
							+ ":" + flowObject.getName() + ")", errors);
			}
		}
		return result;
	}

	public final int getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public Map<String, List<String>> getWarnings() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (AbstractFlowObject flowObject : this.flowObjects) {
			if (flowObject != null) {
				List<String> warnings = flowObject.getWarnings();
				if (warnings.size() > 0)
					result.put("(" + flowObject.getClass().getSimpleName()
							+ ":" + flowObject.getName() + ")", warnings);
			}
		}
		return result;
	}

	public void run(User user) {
		Vector<AbstractFlowObject> startPoints = new Vector<AbstractFlowObject>();
		for (AbstractFlowObject flowObject : this.flowObjects) {
			if (flowObject.getIncomingSize() == 0)
				startPoints.add(flowObject);
		}

		Token token = ProcessManager.getInstance().getNewProcessToken(user);
		int i = 1;
		for (AbstractFlowObject flowObject : startPoints)
			if (i++ < startPoints.size())
				new ProcessThread(null, flowObject, token).start();
			else
				flowObject.invoke(null, token);
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final void setName(String name) {
		this.name = name;
	}
}
