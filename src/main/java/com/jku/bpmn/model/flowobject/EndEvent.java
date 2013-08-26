package com.jku.bpmn.model.flowobject;

import java.util.ArrayList;
import java.util.List;

import com.jku.bpmn.model.Token;
import com.jku.bpmn.util.ProcessManager;

public class EndEvent extends AbstractFlowObject {

	private static final String MAX_OUT_ERROR = "End event should not containt outgoing sequence flows.";
	private static final String MIN_IN_ERROR = "End event should containt at least one incoming sequence flow.";

	@Override
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		if (this.getOutgoingSize() > 0)
			errors.add(MAX_OUT_ERROR);
		if (this.getIncomingSize() < 1)
			errors.add(MIN_IN_ERROR);
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = new ArrayList<String>();
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

		ProcessManager.getInstance().deleteToken(token);
	}
}
