package com.jku.bpmn.model.flowobject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jku.bpmn.model.Token;

public abstract class AbstractFlowObject {

	private static int idGenerator = 0;
	
	private final Set<AbstractFlowObject> incomingFlowObjects = new HashSet<AbstractFlowObject>();
	private final Set<AbstractFlowObject> outgoingFlowObjects = new HashSet<AbstractFlowObject>();
	private int id = idGenerator++;
	private double X;
	private double Y;
	protected String name;

	protected AbstractFlowObject addIncomingSequenceFlow(AbstractFlowObject flowObject) {
		this.incomingFlowObjects.add(flowObject);
		return this;
	}

	protected AbstractFlowObject removeIncomingSequenceFlow(AbstractFlowObject flowObject) {
		this.incomingFlowObjects.remove(flowObject);
		return this;
	}

	public AbstractFlowObject addOutgoingSequenceFlow(AbstractFlowObject flowObject) {
		if (flowObject == null)
			return this;
		this.outgoingFlowObjects.add(flowObject.addIncomingSequenceFlow(this));
		return this;
	}

	public abstract List<String> getErrors();

	public final Set<AbstractFlowObject> getIncomingFlowObjects() {
		return Collections.unmodifiableSet(this.incomingFlowObjects);
	}

	public final int getIncomingSize() {
		return this.incomingFlowObjects.size();
	}

	public final String getName() {
		return this.name;
	}

	public final Set<AbstractFlowObject> getOutgoingFlowObjects() {
		return Collections.unmodifiableSet(this.outgoingFlowObjects);
	}

	public final int getOutgoingSize() {
		return this.outgoingFlowObjects.size();
	}

	public abstract List<String> getWarnings();

	public final double getX() {
		return this.X;
	}

	public final double getY() {
		return this.Y;
	}

	public abstract void invoke(AbstractFlowObject invokerFlowObject, Token token);

	public final boolean isIncomingFlowObject(AbstractFlowObject flowObject) {
		return this.incomingFlowObjects.contains(flowObject);
	}

	public final boolean isOutgoingFlowObject(AbstractFlowObject flowObject) {
		return this.outgoingFlowObjects.contains(flowObject);
	}

	public final void removeAllSequenceFlows() {
		for (AbstractFlowObject flowObject : this.outgoingFlowObjects)
			this.removeOutgoingSequenceFlow(flowObject);
		for (AbstractFlowObject flowObject : this.incomingFlowObjects)
			flowObject.removeOutgoingSequenceFlow(this);
	}

	public void removeOutgoingSequenceFlow(AbstractFlowObject flowObject) {
		if (flowObject == null)
			return;
		this.outgoingFlowObjects.remove(flowObject.removeIncomingSequenceFlow(this));
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setX(double x) {
		this.X = x;
	}

	public final void setY(double y) {
		this.Y = y;
	}
	
	public final String getStringId() {
		return this.getClass().getSimpleName() + this.id;
	}
}
