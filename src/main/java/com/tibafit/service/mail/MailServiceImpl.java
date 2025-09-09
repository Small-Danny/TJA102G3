package com.tibafit.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;

	@Autowired
	public MailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void sendMail(String to, String subject, String messageText) {
		try {
			// 建立一張空白的訊息
			SimpleMailMessage message = new SimpleMailMessage();

			// 填上收件人、主旨、內容
			// (寄件人會自動使用我們在 application.properties 裡設定的 username
			message.setTo(to);
			message.setSubject(subject);
			message.setText(messageText);

			//javaMailSender 幫我們寄出去，原本是Transport.send
			javaMailSender.send(message);

			System.out.println("傳送成功!");

		} catch (MailException e) {
			System.out.println("傳送失敗!");
			e.printStackTrace();
			// 實務上這裡也應該拋出一個自訂例外，讓呼叫者知道失敗了
		}
	}

}
