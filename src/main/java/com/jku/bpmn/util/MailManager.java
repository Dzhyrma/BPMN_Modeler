package com.jku.bpmn.util;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailManager {

	private static volatile MailManager instance = new MailManager();

	public synchronized static MailManager getInstance() {
		if (instance == null)
			instance = new MailManager();
		return instance;
	}

	private final static String FROM = "no-reply@bpmn-modeler.com";
	private final static String HOST = "smtp.gmail.com";
	private final static String PORT = "587";
	private final static String USERNAME = "bpmnmodeler@gmail.com";
	private final static String PASSWORD = "bpmnmodeler13";

	private MailManager() {}

	public void send(String to, String subject, String text) {
		Properties properties = new Properties();

		properties.setProperty("mail.smtp.host", HOST);
		properties.setProperty("mail.smtp.port", PORT);
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(FROM));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}