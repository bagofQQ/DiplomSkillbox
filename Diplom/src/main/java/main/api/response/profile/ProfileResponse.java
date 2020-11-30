package main.api.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("errors")
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
