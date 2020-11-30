package main.api.request.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLoginRequest {

    @JsonProperty("e_mail")
    private String email;
    @JsonProperty("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
