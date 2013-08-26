package com.jku.bpmn.model.json;

import java.io.Serializable;
import java.util.Set;

import com.jku.bpmn.model.Pair;

public class JSONNode implements Serializable {

	private static final long serialVersionUID = 6170234944869564095L;
	private double x;
	private double y;
	private String name;
	private String description;
	private JSONUser userAssigned;
	private Set<String> options;
	private String id;
	private String type;

	public final String getId() {
		return this.id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final String getType() {
		return this.type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final double getX() {
		return this.x;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final double getY() {
		return this.y;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getDescription() {
		return this.description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final Pair<String, String> getPairUserAssigned() {
		if (this.userAssigned == null)
			return null;
		return new Pair<String, String>(this.userAssigned.getName(), this.userAssigned.getEmail());
	}

	public final JSONUser getUserAssigned() {
		return this.userAssigned;
	}

	public final void setUserAssigned(JSONUser userAssigned) {
		this.userAssigned = userAssigned;
	}

	public final void setPairUserAssigned(Pair<String, String> userAssigned) {
		if (userAssigned == null)
			this.userAssigned = null;
		this.userAssigned = new JSONUser();
		this.userAssigned.setName(userAssigned.getKey());
		this.userAssigned.setEmail(userAssigned.getValue());
	}

	public final Set<String> getOptions() {
		return this.options;
	}

	public final void setOptions(Set<String> options) {
		this.options = options;
	}

}
