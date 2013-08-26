package com.jku.bpmn.model.flowobject;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity extends AbstractFlowObject {

	private static final String NO_NAME_ERROR = "Activity should have name specified.";
	private static final String RECOMMENDED_MAX_IN_WARNING = "It is recommended to use gateways instead of more than one incoming sequence flows.";
	private static final String NO_IN_WARNING = "It is recommended to add a start event if an activity has no incoming sequence flows.";
	private static final String NO_OUT_WARNING = "It is recommended to add an end event if an activity has no outgoing sequence flows.";

	@Override
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		if (this.name == null || this.name.trim().equals(""))
			errors.add(NO_NAME_ERROR);
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = new ArrayList<String>();
		if (this.getIncomingSize() == 0)
			warnings.add(NO_IN_WARNING);
		if (this.getOutgoingSize() == 0)
			warnings.add(NO_OUT_WARNING);
		if (this.getIncomingSize() > 1)
			warnings.add(RECOMMENDED_MAX_IN_WARNING);
		return warnings;
	}

}
