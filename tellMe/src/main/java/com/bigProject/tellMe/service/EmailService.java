package com.bigProject.tellMe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String senderEmail) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
    }

    // 인증 코드 생성
    public String generateAuthCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // 6자리 난수
    }

    // 이메일 전송
    public void sendVerificationEmail(String toEmail) throws MessagingException {
        String authCode = generateAuthCode();
        verificationCodes.put(toEmail, authCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(toEmail);
        helper.setSubject("이메일 인증 코드");
        helper.setText("인증 코드: " + authCode, false);

        mailSender.send(message);
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        return verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code);
    }
}
