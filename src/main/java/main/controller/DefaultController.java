package main.controller;

import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Controller
public class DefaultController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index() {

//        insertModer();
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }


    public void insertModer() {
        User user = new User();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        user.setIsModerator(1);
        user.setRegTime(date);
        user.setName("Модер");
        user.setEmail("moder@mail.ru");
        user.setPassword("moder123");
        user.setCode(getGeneratedSecretCode());
        userRepository.save(user);
    }

    public String getGeneratedSecretCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 22;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}
