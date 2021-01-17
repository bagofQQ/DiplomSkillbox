package main.service;

import main.api.request.registration.UserRegistrationRequest;
import main.api.response.registration.ErrorsRegistrationResponse;
import main.api.response.registration.UserRegistrationResponse;
import main.model.CaptchaCodesRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;

@Service
public class UserRegistrationService {

    @Value("${blog.constants.checkName}")
    private String CHECK_NAME;

    @Value("${blog.constants.errorEmail}")
    private String ERROR_EMAIL;
    @Value("${blog.constants.errorCaptcha}")
    private String ERROR_CAPTCHA;
    @Value("${blog.constants.errorName}")
    private String ERROR_NAME;
    @Value("${blog.constants.errorPassword}")
    private String ERROR_PASSWORD;

    private final UserRepository userRepository;
    private final CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    public UserRegistrationService(UserRepository userRepository, CaptchaCodesRepository captchaCodesRepository) {
        this.userRepository = userRepository;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    public UserRegistrationResponse getUserRegistrationInfo(UserRegistrationRequest userRequest) {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        HashMap<String, String> errors = checkRegistrationErrors(userRequest);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        setUser(userRequest);
        userRegistrationResponse.setResult(true);
        return userRegistrationResponse;
    }

    private HashMap<String, String> checkRegistrationErrors(UserRegistrationRequest user) {
        HashMap<String, String> errors = new HashMap<>();
        if (userRepository.countFindUser(user.getEmail()) > 0) {
            errors.put("email", ERROR_EMAIL);
        }
        if (!user.getName().matches(CHECK_NAME)) {
            errors.put("name", ERROR_NAME);
        }
        if (captchaCodesRepository.countCaptcha(user.getCaptchaSecret(), user.getCaptcha()) < 1) {
            errors.put("captcha", ERROR_CAPTCHA);
        }
        if (user.getPassword().length() < 6) {
            errors.put("password", ERROR_PASSWORD);
        }
        return errors;
    }

    private UserRegistrationResponse setErrors(HashMap<String, String> errors) {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        ErrorsRegistrationResponse errorsRegistrationResponse = new ErrorsRegistrationResponse();

        errorsRegistrationResponse.setEmail(errors.get("email"));
        errorsRegistrationResponse.setName(errors.get("name"));
        errorsRegistrationResponse.setCaptcha(errors.get("captcha"));
        errorsRegistrationResponse.setPassword(errors.get("password"));


        userRegistrationResponse.setErrors(errorsRegistrationResponse);
        userRegistrationResponse.setResult(false);
        return userRegistrationResponse;
    }

    private void setUser(UserRegistrationRequest userRequest) {
        User user = new User();
        user.setRegTime(Calendar.getInstance().getTime());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setCode(userRequest.getCaptchaSecret());
        userRepository.save(user);
    }

}
