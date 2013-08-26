package com.jku.bpmn.model.flowobject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGateway extends AbstractFlowObject {

	private static final String MIN_OUT_ERROR = "Gateway should containt at least one outgoing sequence flow.";
	private static final String MIN_IN_ERROR = "Gateway should containt at least one incoming sequence flow.";
	private static final String SPLIT_JOIN_WARNING = "Gateway should join incoming or split outgoing sequence flows, or do both.";

	@Override
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		if (this.getIncomingSize() < 1)
			errors.add(MIN_IN_ERROR);
		if (this.getOutgoingSize() < 1)
			errors.add(MIN_OUT_ERROR);
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = new ArrayList<String>();
		if (this.getIncomingSize() == 1 && this.getOutgoingSize() == 1)
			warnings.add(SPLIT_JOIN_WARNING);
		return warnings;
	}
}
