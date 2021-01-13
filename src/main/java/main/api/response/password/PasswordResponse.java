package main.api.response.password;

public class PasswordResponse {

    private boolean result;
    private ErrorsPasswordResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsPasswordResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsPasswordResponse errors) {
        this.errors = errors;
    }
}
