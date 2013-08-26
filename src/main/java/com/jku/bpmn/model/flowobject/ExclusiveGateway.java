package com.jku.bpmn.model.flowobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.Token;

public class ExclusiveGateway extends AbstractGateway {

	private static final String NO_DEFAULT_ERROR = "Exclusive gateway should contain one default outgoing sequence flow.";
	private static final String NO_CONDITION_ERROR = "Exclusive gateway should have conditions on each non-default outgoing sequence flow.";

	private final Map<AbstractFlowObject, Pair<UserTask, String>> conditions = new HashMap<AbstractFlowObject, Pair<UserTask, String>>();
	private AbstractFlowObject defaultConnection = null;

	public ExclusiveGateway addOutgoingSequenceFlowWithCondition(AbstractFlowObject flowObject, UserTask userTask, String selectedOption) {
		if (flowObject == null)
			return this;
		super.addOutgoingSequenceFlow(flowObject);
		this.conditions.put(flowObject, new Pair<UserTask, String>(userTask, selectedOption));
		if (this.defaultConnection == flowObject)
			this.defaultConnection = null;
		return this;
	}

	public Pair<UserTask, String> getCondition(AbstractFlowObject flowObject) {
		if (flowObject == null || this.defaultConnection == flowObject)
			return null;
		Pair<UserTask, String> condition = this.conditions.get(flowObject);
		if (condition == null)
			return null;
		return new Pair<UserTask, String>(condition.getKey(), condition.getValue());
	}

	public boolean hasDefaultOutgoingSequenceFlow() {
		return this.defaultConnection != null;
	}

	public ExclusiveGateway addDefaultOutgoingSequenceFlow(AbstractFlowObject flowObject) {
		if (flowObject == null)
			return this;
		super.addOutgoingSequenceFlow(flowObject);
		this.defaultConnection = flowObject;
		this.conditions.remove(flowObject);
		return this;
	}

	@Override
	public void removeOutgoingSequenceFlow(AbstractFlowObject flowObject) {
		super.removeOutgoingSequenceFlow(flowObject);
		if (this.defaultConnection == flowObject)
			this.defaultConnection = null;
		this.conditions.remove(flowObject);
	}

	@Override
	public List<String> getErrors() {
		List<String> errors = super.getErrors();
		if (this.getOutgoingSize() > 1 && this.defaultConnection == null)
			errors.add(NO_DEFAULT_ERROR);
		for (AbstractFlowObject flowObject : this.conditions.keySet()) {
			Pair<UserTask, String> condition = this.conditions.get(flowObject);
			if (condition == null || condition.getKey() == null || condition.getValue() == null) {
				errors.add(NO_CONDITION_ERROR);
				break;
			}
		}
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = super.getWarnings();
		return warnings;
	}

	@Override
	public synchronized void invoke(AbstractFlowObject invokerFlowObject, Token token) {
		if (this.getErrors().size() > 0) {
			for (String error : this.getErrors()) {
				System.out.println(error);
			}
			return;
		}

		if (token == null)
			return;
		for (AbstractFlowObject flowObject : this.conditions.keySet()) {
			Pair<UserTask, String> condition = this.conditions.get(flowObject);
			if (condition == null)
				continue;
			String selectedOption = token.getOption(condition.getKey());
			if (selectedOption == null || condition.getValue() == null)
				continue;
			if (selectedOption.equalsIgnoreCase(condition.getValue())) {
				flowObject.invoke(this, token);
				return;
			}
		}
		if (this.defaultConnection == null)
			return;
		this.defaultConnection.invoke(this, token);
	}

}
