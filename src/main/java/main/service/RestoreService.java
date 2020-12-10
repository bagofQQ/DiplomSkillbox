package main.service;

import main.api.response.restore.RestoreResponse;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Properties;


@Service
public class RestoreService {

    private String username = "";
    private String password = "";

    @Autowired
    private UserRepository userRepository;

    public RestoreResponse get(String email) {
        RestoreResponse restoreResponse = new RestoreResponse();

        Iterable<User> usersIterable = userRepository.findAll();
        for (User f : usersIterable) {
            Optional<User> optionalUser = userRepository.findById(f.getId());
            if (email.equals(optionalUser.get().getEmail())) {
                String code = optionalUser.get().getCode();
                sendEmail(email, code);
                restoreResponse.setResult(true);
                return restoreResponse;
            }
        }
        restoreResponse.setResult(false);
        return restoreResponse;
    }

    private void sendEmail(String email, String code) {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Восстановление пароля");
            String text = "http://localhost:8080/login/change-password/" + code;
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}