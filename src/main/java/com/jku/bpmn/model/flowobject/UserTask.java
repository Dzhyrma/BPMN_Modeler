package com.jku.bpmn.model.flowobject;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.jku.bpmn.controllers.SignUpFormController;
import com.jku.bpmn.model.ConditionHolder;
import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.Token;
import com.jku.bpmn.util.MailManager;
import com.jku.bpmn.util.ProcessManager;
import com.jku.bpmn.util.ProcessThread;

public class UserTask extends Activity {

	private static final String NO_USER_ERROR = "User task should contain an assigned user.";
	private static final String NO_DESC_ERROR = "User task should have some description.";
	private Pair<String, String> assignedUser = null;
	private final ConditionHolder conditionHolder;
	private String description = "";

	public UserTask(ConditionHolder conditions) {
		this.conditionHolder = conditions;
		if (this.conditionHolder == null)
			return;
		this.conditionHolder.addUserTask(this);
	}

	public void addOption(String option) {
		if (this.conditionHolder == null)
			return;
		this.conditionHolder.addOptionToTask(this, option);
	}

	public void removeOption(String option) {
		if (this.conditionHolder == null)
			return;
		this.conditionHolder.removeOptionFromTask(this, option);
	}

	public Set<String> getOptions() {
		if (this.conditionHolder == null)
			return Collections.<String> emptySet();
		return this.conditionHolder.getOptionsForTask(this);
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		if (description == null)
			return;
		this.description = description;
	}

	public void setUser(Pair<String, String> userContacts) {
		if (userContacts != null)
			this.assignedUser = userContacts;
	}

	public Pair<String, String> getUser() {
		if (this.assignedUser == null)
			return new Pair<String, String>("", "");
		return new Pair<String, String>(this.assignedUser.getKey(), this.assignedUser.getValue());
	}

	@Override
	public List<String> getErrors() {
		List<String> errors = super.getErrors();
		if (this.assignedUser == null)
			errors.add(NO_USER_ERROR);
		if (this.description == null || this.description.trim().equals(""))
			errors.add(NO_DESC_ERROR);
		return errors;
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = super.getWarnings();
		return warnings;
	}

	public void removeUser() {
		this.assignedUser = null;
	}

	@Override
	public synchronized void invoke(AbstractFlowObject invokerFlowObject, Token token) {
		if (this.getErrors().size() > 0) {
			for (String error : this.getErrors()) {
				System.out.println(error);
			}
			return;
		}

		//System.out.println(this.assignedUser.getValue() + this.assignedUser.getKey() + this.name + this.description);
		if (this.assignedUser != null) {
			String uuid = UUID.randomUUID().toString();
			MailManager.getInstance().send(
				this.assignedUser.getValue(),
				"BPMN Modeler task",
				"Dear, " + this.assignedUser.getKey() + "!\n\nYou just received a new task '" + this.name + "':\n" + this.description
					+ "\nVisit this link to check the task: http://localhost:8080/bpmn/complete?id=" + uuid + " \n\nBest regards, BPMN Modeler team!");
			ProcessManager.getInstance().userTaskValidation(uuid, this, token);
		}

		/*
		 * System.out.println("[" + this.name + "] : " + this.description);
		 * Set<String> options = this.conditionHolder.getOptionsForTask(this);
		 * if (options.size() > 0) { String[] optionArray = new
		 * String[options.size()]; optionArray = options.toArray(optionArray);
		 * System.out.println("Choose an option for the user task '" + this.name
		 * + "':"); for (int i = 0; i < optionArray.length; i++)
		 * System.out.println("\t" + (i + 1) + ": " + optionArray[i]);
		 * 
		 * Scanner in = new Scanner(System.in); int num = in.nextInt(); while
		 * (num < 1 || num > optionArray.length) {
		 * System.out.println("Choose number between 1 and " +
		 * (optionArray.length - 1) + " inclusive:"); num = in.nextInt(); } //
		 * in.close();
		 * 
		 * token.addOption(this, optionArray[num - 1]); }
		 */

	}

	public synchronized void continueInvoke(Token token) {
		Set<AbstractFlowObject> outputFlowObjects = this.getOutgoingFlowObjects();
		
		if (outputFlowObjects.size() == 0) {
			ProcessManager.getInstance().deleteToken(token);
			return;
		}

		int i = 1;
		for (AbstractFlowObject flowObject : outputFlowObjects)
			if (i++ < outputFlowObjects.size())
				new ProcessThread(this, flowObject, token).start();
			else
				flowObject.invoke(this, token);
	}

}
