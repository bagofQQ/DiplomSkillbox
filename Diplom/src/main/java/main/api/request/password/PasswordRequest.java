package main.api.request.password;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordRequest {

    @JsonProperty("code")
    private String code;
    @JsonProperty("password")
    private String password;
    @JsonProperty("captcha")
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }

}
