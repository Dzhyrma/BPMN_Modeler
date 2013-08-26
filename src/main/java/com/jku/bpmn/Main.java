package com.jku.bpmn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jku.bpmn.model.ConditionHolder;
import com.jku.bpmn.model.Diagram;
import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.flowobject.EndEvent;
import com.jku.bpmn.model.flowobject.ExclusiveGateway;
import com.jku.bpmn.model.flowobject.ParallelGateway;
import com.jku.bpmn.model.flowobject.StartEvent;
import com.jku.bpmn.model.flowobject.UserTask;
import com.jku.bpmn.util.ProcessManager;
import com.jku.bpmn.util.ProcessThread;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new GsonBuilder().create().toJson(new Diagram().convertToJSONDiagram()));
	}
}
