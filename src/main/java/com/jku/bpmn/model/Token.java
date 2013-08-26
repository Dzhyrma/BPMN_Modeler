package com.jku.bpmn.model;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.views.AbstractView;

import com.jku.bpmn.model.flowobject.UserTask;

public class Token{

	private static long idGenerator = 0;

	private long id = idGenerator++;
	private final Long processId;
	private Map<UserTask, String> selectedOptions;

	public Token(Long processId) {
		this.processId = processId;
		this.selectedOptions = new HashMap<UserTask, String>();
	}

	private Token(Token invokerToken) {
		this.processId = invokerToken.processId;
		if (invokerToken.selectedOptions == null)
			this.selectedOptions = new HashMap<UserTask, String>();
		else
			this.selectedOptions = new HashMap<UserTask, String>(
					invokerToken.selectedOptions);
	}

	public void addOption(UserTask userTask, String option) {
		if (userTask == null)
			return;
		this.selectedOptions.put(userTask, option);
	}

	public final long getId() {
		return this.id;
	}

	public String getOption(UserTask userTask) {
		return this.selectedOptions.get(userTask);
	}

	public Token getParallelToken() {
		return new Token(this);
	}

	public Long getProcessId() {
		return this.processId;
	}

}
