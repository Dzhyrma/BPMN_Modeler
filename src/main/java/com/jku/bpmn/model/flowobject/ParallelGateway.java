package com.jku.bpmn.model.flowobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jku.bpmn.model.Token;
import com.jku.bpmn.util.ProcessManager;
import com.jku.bpmn.util.ProcessThread;

public class ParallelGateway extends AbstractGateway {

	private final Map<AbstractFlowObject, Integer> tokenCounter = new HashMap<AbstractFlowObject, Integer>();

	@Override
	protected AbstractFlowObject removeIncomingSequenceFlow(AbstractFlowObject flowObject) {
		if (flowObject == null)
			return this;
		this.tokenCounter.remove(flowObject);
		return super.removeIncomingSequenceFlow(flowObject);
	}

	@Override
	protected AbstractFlowObject addIncomingSequenceFlow(AbstractFlowObject flowObject) {
		if (flowObject == null)
			return this;
		this.tokenCounter.put(flowObject, Integer.valueOf(0));
		return super.addIncomingSequenceFlow(flowObject);
	}

	@Override
	public List<String> getErrors() {
		List<String> errors = super.getErrors();
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

		if (invokerFlowObject == null || !this.isIncomingFlowObject(invokerFlowObject)) {
			ProcessManager.getInstance().deleteToken(token);
			return;
		}
		Integer counter = this.tokenCounter.get(invokerFlowObject);
		if (counter == null)
			counter = Integer.valueOf(1);
		else
			counter++;
		this.tokenCounter.put(invokerFlowObject, counter);

		Set<AbstractFlowObject> inputFlowObjects = this.getIncomingFlowObjects();

		for (AbstractFlowObject flowObject : inputFlowObjects)
			if (flowObject == null || this.tokenCounter.get(flowObject) == null || this.tokenCounter.get(flowObject) <= 0) {
				ProcessManager.getInstance().deleteToken(token);
				return;
			}

		for (AbstractFlowObject flowObject : inputFlowObjects)
			this.tokenCounter.put(flowObject, Integer.valueOf(this.tokenCounter.get(flowObject).intValue() - 1));

		Set<AbstractFlowObject> outputFlowObjects = this.getOutgoingFlowObjects();

		int i = 1;
		for (AbstractFlowObject flowObject : outputFlowObjects)
			if (i++ < outputFlowObjects.size())
				new ProcessThread(this, flowObject, token).start();
			else
				flowObject.invoke(this, token);
	}

}
