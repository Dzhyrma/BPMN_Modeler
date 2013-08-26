package com.jku.bpmn.model.json;

import java.io.Serializable;

public class JSONUser implements Serializable {

	private static final long serialVersionUID = -7679969709058443967L;
	private String name;
	private String email;

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getEmail() {
		return this.email;
	}

	public final void setEmail(String email) {
		this.email = email;
	}
}
