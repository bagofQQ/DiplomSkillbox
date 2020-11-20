package main.controller;

import main.api.response.*;
import main.api.request.password.PasswordRequest;
import main.api.response.password.PasswordResponse;
import main.api.request.login.UserLoginRequest;
import main.api.response.login.UserLoginResponse;
import main.api.request.registration.UserRegistrationRequest;
import main.api.response.registration.UserRegistrationResponse;
import main.api.response.restore.EmailResponse;
import main.api.response.restore.RestoreResponse;
import main.model.GlobalSettings;
import main.model.GlobalSettingsRepository;
import main.model.Users;
import main.model.UsersRepository;
import main.service.*;
import main.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RestController
public class ApiAuthController {

    private final СaptchaService сaptchaService;
    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final RestoreService restoreService;
    private final PasswordService passwordService;

    private static final String MM_CODE = "MM";
    private static final String VALUE_YES = "YES";

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UsersRepository usersRepository;

    public ApiAuthController(СaptchaService сaptchaService,
                             UserRegistrationService userRegistrationService,
                             UserLoginService userLoginService,
                             RestoreService restoreService,
                             PasswordService passwordService) {
        this.сaptchaService = сaptchaService;
        this.userRegistrationService = userRegistrationService;
        this.userLoginService = userLoginService;
        this.restoreService = restoreService;
        this.passwordService = passwordService;
    }

    @GetMapping("/api/auth/check")
    public UserLoginResponse check(){
        return userLoginService.getUserCheck();
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<UserRegistrationResponse> getUserRegistrationResponse(@RequestBody UserRegistrationRequest user){
        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        for(GlobalSettings f : globalSettingsIterable){
            if(f.getCode().equals(MM_CODE)){
                if(f.getValue().equals(VALUE_YES)){
                    return new ResponseEntity(userRegistrationService.getUserRegistrationInfo(user), HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/api/auth/captcha")
    public СaptchaResponse getCaptchaResponse() throws IOException {
        return сaptchaService.getCaptcha();
    }

    @PostMapping("/api/auth/login")
    public UserLoginResponse getUserLoginResponse(@RequestBody UserLoginRequest user){
        return userLoginService.getUserLoginInfo(user.getEmail(), user.getPassword());
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity<LogoutResponse> logout(){

        String identifier = httpSession.getId();
        if(userLoginService.getIdentifierMap().containsKey(identifier)){
            return new ResponseEntity(userLoginService.logoutUser(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @PostMapping("/api/auth/restore")
    public RestoreResponse restore(@RequestBody EmailResponse email){
        return restoreService.get(email.getEmail());
    }

    @PostMapping("/api/auth/password")
    public PasswordResponse password(@RequestBody PasswordRequest password){
        return passwordService.getPassword(password);
    }


}
