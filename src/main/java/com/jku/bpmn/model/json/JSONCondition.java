package com.jku.bpmn.model.json;

import java.io.Serializable;

public class JSONCondition implements Serializable {

	private static final long serialVersionUID = -2304564463097849252L;
	private String userTaskId;
	private String option;
	
	public JSONCondition() {
		this.userTaskId = null;
		this.option = null;
	}

	public JSONCondition(String userTaskId, String option) {
		this.userTaskId = userTaskId;
		this.option = option;
	}

	public final String getUserTaskId() {
		return this.userTaskId;
	}

	public final void setUserTaskId(String userTaskId) {
		this.userTaskId = userTaskId;
	}

	public final String getOption() {
		return this.option;
	}

	public final void setOption(String option) {
		this.option = option;
	}
}
