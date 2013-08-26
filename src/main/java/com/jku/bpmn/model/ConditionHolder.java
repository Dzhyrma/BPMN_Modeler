package com.jku.bpmn.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jku.bpmn.model.flowobject.UserTask;

public class ConditionHolder {

	private final Map<UserTask, Set<String>> conditions = new HashMap<UserTask, Set<String>>();

	public void addUserTask(UserTask userTask) {
		if (userTask == null)
			return;
		Set<String> options = new HashSet<String>();
		this.conditions.put(userTask, options);
	}

	public void addOptionToTask(UserTask userTask, String option) {
		if (option == null || option.equals("") || userTask == null
				|| !this.conditions.containsKey(userTask))
			return;
		this.conditions.get(userTask).add(option);
	}

	public Set<String> getOptionsForTask(UserTask userTask) {
		if (userTask == null || this.conditions.get(userTask) == null)
			return Collections.<String> emptySet();
		return Collections.unmodifiableSet(this.conditions.get(userTask));
	}

	public void removeOptionFromTask(UserTask userTask, String option) {
		if (option == null || option.equals("") || userTask == null
				|| !this.conditions.containsKey(userTask))
			return;
		this.conditions.get(userTask).remove(option);
	}

}
