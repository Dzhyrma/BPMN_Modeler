package com.jku.bpmn.util;

import com.jku.bpmn.model.Token;
import com.jku.bpmn.model.flowobject.AbstractFlowObject;

public class ProcessThread extends Thread {

	private AbstractFlowObject invokerFlowObject;
	private AbstractFlowObject flowObject;
	private Token token;

	public ProcessThread(AbstractFlowObject invokerFlowObject, AbstractFlowObject flowObject, Token token) {
		this.invokerFlowObject = invokerFlowObject;
		this.flowObject = flowObject;
		this.token = ProcessManager.getInstance().getNewParallelToken(token);
	}

	@Override
	public void run() {
		if (this.flowObject != null)
			this.flowObject.invoke(this.invokerFlowObject, this.token);
	}

}
