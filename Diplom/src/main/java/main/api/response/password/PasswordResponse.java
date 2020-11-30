package main.api.response.password;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("errors")
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
