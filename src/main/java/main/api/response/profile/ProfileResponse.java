package main.api.response.profile;

public class ProfileResponse {

    private boolean result;
    private ErrorsProfileResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsProfileResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsProfileResponse errors) {
        this.errors = errors;
    }
}
