package com.tibafit.service.mail;

public interface MailService {
	public abstract void sendMail(String to, String subject, String messageText);
}
