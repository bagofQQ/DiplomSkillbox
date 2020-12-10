package main.api.response.addingpost;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddingPostResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("errors")
    private ErrorsAddingPostResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsAddingPostResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsAddingPostResponse errors) {
        this.errors = errors;
    }
}
