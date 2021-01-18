package main.service;

import main.PostsException;
import main.api.request.password.PasswordRequest;
import main.api.response.password.ErrorsPasswordResponse;
import main.api.response.password.PasswordResponse;
import main.model.CaptchaCodesRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PasswordService {

    @Value("${blog.constants.errorCode}")
    private String ERROR_CODE;
    @Value("${blog.constants.errorPassword}")
    private String ERROR_PASSWORD;
    @Value("${blog.constants.errorCaptcha}")
    private String ERROR_CAPTCHA;

    private final UserRepository userRepository;
    private final CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    public PasswordService(UserRepository userRepository, CaptchaCodesRepository captchaCodesRepository) {
        this.userRepository = userRepository;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    public PasswordResponse updatePassword(PasswordRequest password) {
        List<User> findUserCode = userRepository.findUserCode(password.getCode());
        HashMap<String, String> errors = checkPasswordErrors(password, findUserCode);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        PasswordResponse passwordResponse = new PasswordResponse();
        User user = findUserCode.get(0);
        user.setCode(password.getCaptchaSecret());
        user.setPassword(password.getPassword());
        userRepository.save(user);

        passwordResponse.setResult(true);
        return passwordResponse;
    }

    private HashMap<String, String> checkPasswordErrors(PasswordRequest password, List<User> findUserCode) {
        HashMap<String, String> errors = new HashMap<>();
        if(findUserCode.size() != 1){
            errors.put("code", ERROR_CODE);
        }
        if (captchaCodesRepository.countCaptcha(password.getCaptchaSecret(), password.getCaptcha()) < 1) {
            errors.put("captcha", ERROR_CAPTCHA);
        }
        if (password.getPassword().length() < 6) {
            errors.put("password", ERROR_PASSWORD);
        }
        return errors;
    }

    private PasswordResponse setErrors(HashMap<String, String> errors) {
        PasswordResponse passwordResponse = new PasswordResponse();
        ErrorsPasswordResponse errorsPasswordResponse = new ErrorsPasswordResponse();
        errorsPasswordResponse.setCode(errors.get("code"));
        errorsPasswordResponse.setCaptcha(errors.get("captcha"));
        errorsPasswordResponse.setPassword(errors.get("password"));
        passwordResponse.setErrors(errorsPasswordResponse);
        passwordResponse.setResult(false);
        return passwordResponse;
    }
}
