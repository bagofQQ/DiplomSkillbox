package main.controller;

import main.api.request.login.UserLoginRequest;
import main.api.request.password.PasswordRequest;
import main.api.request.registration.UserRegistrationRequest;
import main.api.response.LogoutResponse;
import main.api.response.login.UserLoginResponse;
import main.api.response.password.PasswordResponse;
import main.api.response.registration.UserRegistrationResponse;
import main.api.response.restore.EmailResponse;
import main.api.response.restore.RestoreResponse;
import main.api.response.СaptchaResponse;
import main.model.GlobalSettingsRepository;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ApiAuthController {

    @Value("${blog.constants.valueYes}")
    private String VALUE_YES;

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;


    private final СaptchaService сaptchaService;
    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final RestoreService restoreService;
    private final PasswordService passwordService;

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
        return userLoginService.checkUser();
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<UserRegistrationResponse> getUserRegistrationResponse(@RequestBody UserRegistrationRequest user){
        if(globalSettingsRepository.findValueMultiuserMode().equals(VALUE_YES)){
            return new ResponseEntity(userRegistrationService.getUserRegistrationInfo(user), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/api/auth/captcha")
    public СaptchaResponse getCaptchaResponse() throws IOException {
        return сaptchaService.generateCaptcha();
    }

    @PostMapping("/api/auth/login")
    public UserLoginResponse getUserLoginResponse(@RequestBody UserLoginRequest user){
        return userLoginService.getUserLoginInfo(user.getEmail(), user.getPassword());
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity<LogoutResponse> logout(){
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(userLoginService.logoutUser(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/api/auth/restore")
    public RestoreResponse restore(@RequestBody EmailResponse email){
        return restoreService.restoreEmail(email.getEmail());
    }

    @PostMapping("/api/auth/password")
    public PasswordResponse password(@RequestBody PasswordRequest password){
        return passwordService.updatePassword(password);
    }


}
