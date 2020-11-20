package main.api.response.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.registration.ErrorsRegistrationResponse;

public class UserRegistrationResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("errors")
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
