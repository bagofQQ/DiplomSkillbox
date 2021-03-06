package main.service;

import main.api.response.restore.RestoreResponse;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;


@Service
public class RestoreService {

    @Value("${blog.constants.usernameGoogleMail}")
    private String username;
    @Value("${blog.constants.passwordGoogleMail}")
    private String password;

    private final UserRepository userRepository;

    @Autowired
    public RestoreService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RestoreResponse restoreEmail(String email) {
        RestoreResponse restoreResponse = new RestoreResponse();

        List<User> findUserRestore = userRepository.findUserRestore(email);
        if (findUserRestore.size() == 1) {
            for (User f : findUserRestore) {
                String code = f.getCode();
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