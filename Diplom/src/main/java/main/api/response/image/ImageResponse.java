package main.api.response.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.image.ErrorsImageResponse;

public class ImageResponse {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("errors")
    private ErrorsImageResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsImageResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsImageResponse errors) {
        this.errors = errors;
    }
}
