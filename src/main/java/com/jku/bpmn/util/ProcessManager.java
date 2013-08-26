package com.jku.bpmn.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.Token;
import com.jku.bpmn.model.User;
import com.jku.bpmn.model.flowobject.UserTask;

public class ProcessManager {

	private static volatile ProcessManager instance = new ProcessManager();
	private static long idGenerator = 0;

	public synchronized static ProcessManager getInstance() {
		if (instance == null)
			instance = new ProcessManager();
		return instance;
	}

	private final Map<Long, Pair<User, Set<Long>>> processes = new HashMap<Long, Pair<User, Set<Long>>>();
	private final Map<String, Pair<UserTask, Token>> validationMap = new HashMap<String, Pair<UserTask, Token>>();

	private ProcessManager() {}

	public synchronized Token getNewParallelToken(Token token) {
		if (token == null)
			return null;
		Token newToken = token.getParallelToken();
		Pair<User, Set<Long>> pair = this.processes.get(newToken.getProcessId());
		if (pair != null) {
			Set<Long> tokens = pair.getValue();
			if (tokens != null)
				tokens.add(Long.valueOf(newToken.getId()));
			else {
				tokens = new HashSet<Long>();
				tokens.add(Long.valueOf(newToken.getId()));
				pair.setValue(tokens);
			}
		} else {
			return null;
		}
		return newToken;
	}

	public synchronized Token getNewProcessToken(User user) {
		Token newToken = new Token(Long.valueOf(++idGenerator));
		Set<Long> tokens = new HashSet<Long>();
		tokens.add(Long.valueOf(newToken.getId()));
		this.processes.put(newToken.getProcessId(), new Pair<User, Set<Long>>(user, tokens));
		return newToken;
	}

	public synchronized void deleteToken(Token token) {
		if (token == null)
			return;
		Long processIdL = token.getProcessId();
		if (processIdL == null)
			return;
		Pair<User, Set<Long>> pair = this.processes.get(processIdL);
		if (pair == null)
			return;
		Set<Long> tokens = pair.getValue();
		if (tokens == null)
			return;
		tokens.remove(Long.valueOf(token.getId()));
		if (tokens.isEmpty()) {
			System.out.println("Process with id [" + processIdL + "] was finished!");
			if (pair.getKey() != null)
			MailManager.getInstance().send(pair.getKey().getEmail(), "Process was finished", "Congratulations, " + pair.getKey().getUserName() + "!\n\nProcess was finished.\n\nBest regards, BPMN Modeler team!");
		}
	}

	public synchronized void userTaskValidation(String uuid, UserTask userTask, Token token) {
		this.validationMap.put(uuid, new Pair<UserTask, Token>(userTask, token));
	}

	public synchronized UserTask getValidationTask(String uuid) {
		if (uuid == null || !this.validationMap.containsKey(uuid))
			return null;
		Pair<UserTask, Token> pair = this.validationMap.get(uuid);
		if (pair == null)
			return null;
		return pair.getKey();
	}

	public synchronized void checkValidation(String uuid) {
		if (uuid != null) {
			final Pair<UserTask, Token> pair = this.validationMap.get(uuid);

			if (pair != null && pair.getKey() != null && pair.getValue() != null) {
				Thread newThread = new Thread(new Runnable() {

					@Override
					public void run() {
						pair.getKey().continueInvoke(pair.getValue());

					}
				});
				newThread.start();
			}
		}
	}

	public synchronized void checkValidation(String uuid, String option) {
		if (uuid == null)
			return;
		Pair<UserTask, Token> pair = this.validationMap.get(uuid);
		if (pair != null && pair.getValue() != null)
			pair.getValue().addOption(pair.getKey(), option);
		this.checkValidation(uuid);
	}
}
