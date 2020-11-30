package main.service;

import main.api.request.password.PasswordRequest;
import main.api.response.password.ErrorsPasswordResponse;
import main.api.response.password.PasswordResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import main.model.Users;
import main.model.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordService {

    private static final String ERROR_CODE = "Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">" +
            "Запросить ссылку снова</a>";
    private static final String ERROR_PASSWORD = "Пароль короче 6-ти символов";
    private static final String ERROR_CAPTCHA = "Код с картинки введён неверно";
    private int idUserChangePassword;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    public PasswordResponse getPassword(PasswordRequest password) {

        PasswordResponse passwordResponse = new PasswordResponse();
        ErrorsPasswordResponse errorsPasswordResponse = new ErrorsPasswordResponse();

        if (checkCode(password.getCode())) {
            if (checkCaptcha(password.getCaptchaSecret(), password.getCaptcha())) {
                if (password.getPassword().length() >= 6) {
                    int idUCP = getIdUserChangePassword();
                    Optional<Users> optionalUsers = usersRepository.findById(idUCP);

                    optionalUsers.get().setCode(password.getCaptchaSecret());
                    optionalUsers.get().setPassword(password.getPassword());
                    usersRepository.save(optionalUsers.get());

                    passwordResponse.setResult(true);
                    return passwordResponse;
                } else {
                    errorsPasswordResponse.setCode(ERROR_PASSWORD);
                    passwordResponse.setErrors(errorsPasswordResponse);
                    passwordResponse.setResult(false);
                    return passwordResponse;
                }
            } else {
                errorsPasswordResponse.setCode(ERROR_CAPTCHA);
                passwordResponse.setErrors(errorsPasswordResponse);
                passwordResponse.setResult(false);
                return passwordResponse;
            }
        } else {
            errorsPasswordResponse.setCode(ERROR_CODE);
            passwordResponse.setErrors(errorsPasswordResponse);
            passwordResponse.setResult(false);
            return passwordResponse;
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

    private boolean checkCode(String code) {
        Iterable<Users> usersIterable = usersRepository.findAll();
        for (Users q : usersIterable) {
            Optional<Users> optionalUsers = usersRepository.findById(q.getId());
            if (code.equals(optionalUsers.get().getCode())) {
                setIdUserChangePassword(optionalUsers.get().getId());
                return true;
            }
        }
        return false;
    }

    private int getIdUserChangePassword() {
        return idUserChangePassword;
    }

    private void setIdUserChangePassword(int idUserChangePassword) {
        this.idUserChangePassword = idUserChangePassword;
    }


}
