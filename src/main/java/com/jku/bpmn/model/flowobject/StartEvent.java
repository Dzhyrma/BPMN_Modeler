package com.jku.bpmn.model.flowobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jku.bpmn.model.Token;
import com.jku.bpmn.util.ProcessThread;

public class StartEvent extends AbstractFlowObject {

	private static final String MAX_IN_ERROR = "Start event should not containt incoming sequence flows.";
	private static final String OUT_ERROR = "Start event should containt at least one outgoing sequence flow.";
	private static final String OUT_WARNING = "It is recommended to have only one outgoing sequence flow."; //?

	@Override
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		if (this.getIncomingSize() > 0)
			errors.add(MAX_IN_ERROR);
		if (this.getOutgoingSize() < 1)
			errors.add(OUT_ERROR);
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = new ArrayList<String>();
		if (this.getOutgoingSize() > 1 || this.getOutgoingSize() < 1)
			warnings.add(OUT_WARNING);
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

		Set<AbstractFlowObject> outputFlowObjects = this.getOutgoingFlowObjects();

		int i = 1;
		for (AbstractFlowObject flowObject : outputFlowObjects)
			if (i++ < outputFlowObjects.size())
				new ProcessThread(this, flowObject, token).start();
			else
				flowObject.invoke(this, token);
	}
}
