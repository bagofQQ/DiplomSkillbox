package main.service;

import main.api.request.registration.UserRegistrationRequest;
import main.api.response.registration.ErrorsRegistrationResponse;
import main.api.response.registration.UserRegistrationResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserRegistrationService {

    private static final String CHECK_NAME = "([а-яА-Я]*)";
    private static final String ERROR_EMAIL = "Этот e-mail уже зарегистрирован";
    private static final String ERROR_CAPTCHA = "Код с картинки введён неверно";
    private static final String ERROR_NAME = "Имя указано неверно";
    private static final String ERROR_PASSWORD = "Пароль короче 6-ти символов";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    public UserRegistrationResponse getUserRegistrationInfo(UserRegistrationRequest user) {

        UserRegistrationResponse userReg = new UserRegistrationResponse();
        ErrorsRegistrationResponse errors = new ErrorsRegistrationResponse();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        User users = new User();
        if (checkEmail(user.getEmail())) {
            if (user.getName().matches(CHECK_NAME)) {
                if (checkCaptcha(user.getCaptchaSecret(), user.getCaptcha())) {
                    if (user.getPassword().length() >= 6) {
                        users.setRegTime(date);
                        users.setName(user.getName());
                        users.setEmail(user.getEmail());
                        users.setPassword(user.getPassword());
                        users.setCode(user.getCaptchaSecret());
                        userRepository.save(users);
                        userReg.setResult(true);
                        return userReg;
                    } else {
                        errors.setCaptcha(ERROR_PASSWORD);
                        userReg.setErrors(errors);
                        userReg.setResult(false);
                        return userReg;
                    }
                } else {
                    errors.setCaptcha(ERROR_CAPTCHA);
                    userReg.setErrors(errors);
                    userReg.setResult(false);
                    return userReg;
                }
            } else {
                errors.setName(ERROR_NAME);
                userReg.setErrors(errors);
                userReg.setResult(false);
                return userReg;
            }
        } else {
            errors.setEmail(ERROR_EMAIL);
            userReg.setErrors(errors);
            userReg.setResult(false);
            return userReg;
        }
    }

    private boolean checkCaptcha(String captchaSecret, String captcha) {
        Iterable<CaptchaCodes> captchaCodesIterable = captchaCodesRepository.findAll();
        for (CaptchaCodes f : captchaCodesIterable) {
            Optional<CaptchaCodes> optionalCaptchaCodes = captchaCodesRepository.findById(f.getId());
            if (captchaSecret.equals(optionalCaptchaCodes.get().getSecretCode())) {
                if (captcha.equals(optionalCaptchaCodes.get().getCode())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkEmail(String email) {
        Iterable<User> usersIterable = userRepository.findAll();
        if (usersIterable.iterator().hasNext()) {
            for (User q : usersIterable) {
                Optional<User> optionalUsers = userRepository.findById(q.getId());
                if (email.equals(optionalUsers.get().getEmail())) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
