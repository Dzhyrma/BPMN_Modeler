package com.jku.bpmn.model.json;

import java.util.List;
import java.util.Map;

public class JSONDiagramRunResponse {

	private boolean isRunning;
	private Map<String, List<String>> errors;
	private Map<String, List<String>> warnings;
	private String response;

	public final String getResponse() {
		return this.response;
	}

	public final void setResponse(String response) {
		this.response = response;
	}

	public final boolean isRunning() {
		return this.isRunning;
	}

	public final void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public final Map<String, List<String>> getErrors() {
		return this.errors;
	}

	public final void setErrors(Map<String, List<String>> errors) {
		this.errors = errors;
	}

	public final Map<String, List<String>> getWarnings() {
		return this.warnings;
	}

	public final void setWarnings(Map<String, List<String>> warnings) {
		this.warnings = warnings;
	}
}
