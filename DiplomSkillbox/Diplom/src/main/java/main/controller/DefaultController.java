package main.controller;

import main.model.Users;
import main.model.UsersRepository;
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
    private UsersRepository usersRepository;

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
        Users users = new Users();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        users.setIsModerator(1);
        users.setRegTime(date);
        users.setName("Модер");
        users.setEmail("moder@mail.ru");
        users.setPassword("moder123");
        users.setCode(getGeneratedSecretCode());
        usersRepository.save(users);
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
