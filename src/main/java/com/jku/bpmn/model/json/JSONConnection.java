package com.jku.bpmn.model.json;

import java.io.Serializable;

public class JSONConnection implements Serializable {

	private static final long serialVersionUID = -1383406755270231422L;
	private String sourceId;
	private String targetId;
	private JSONCondition condition;

	public final String getSourceId() {
		return this.sourceId;
	}

	public final void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public final String getTargetId() {
		return this.targetId;
	}

	public final void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public final JSONCondition getCondition() {
		return this.condition;
	}

	public final void setCondition(JSONCondition condition) {
		this.condition = condition;
	}
}
