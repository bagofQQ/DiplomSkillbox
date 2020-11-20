package main.api.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLoginResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("user")
    private UserLoginInfoResponse user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserLoginInfoResponse getUser() {
        return user;
    }

    public void setUser(UserLoginInfoResponse user) {
        this.user = user;
    }
}
