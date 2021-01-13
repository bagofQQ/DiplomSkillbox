package main.api.response.registration;

public class UserRegistrationResponse {

    private boolean result;
    private ErrorsRegistrationResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsRegistrationResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsRegistrationResponse errors) {
        this.errors = errors;
    }
}
