package com.jku.bpmn.models;

import java.io.Serializable;
import java.lang.reflect.Field;

public class User implements Serializable{

	private static final long serialVersionUID = 3227145775970532302L;
	private String email;
	private int enabled = 1;
	private String password;
	private int userID;
	private String userName;
	private String authority = "ROLE_USER";

	public synchronized String getAuthority() {
		return authority;
	}

	public synchronized void setAuthority(String authority) {
		this.authority = authority;
	}

	public synchronized String getEmail() {
		return email;
	}

	public synchronized int getEnabled() {
		return enabled;
	}

	public synchronized String getPassword() {
		return password;
	}

	public synchronized int getUserID() {
		return userID;
	}

	public synchronized String getUserName() {
		return userName;
	}

	public synchronized void setEmail(String email) {
		this.email = email;
	}

	public synchronized void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public synchronized void setPassword(String password) {
		this.password = password;
	}

	public synchronized void setUserID(int userID) {
		this.userID = userID;
	}

	public synchronized void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);
		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();
		// print field names paired with their values
		for (Field field : fields) {
			result.append(" ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");
		return result.toString();
	}
}