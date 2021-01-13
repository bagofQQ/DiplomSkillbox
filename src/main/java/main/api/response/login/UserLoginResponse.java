package main.api.response.login;

public class UserLoginResponse {

    private boolean result;
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
