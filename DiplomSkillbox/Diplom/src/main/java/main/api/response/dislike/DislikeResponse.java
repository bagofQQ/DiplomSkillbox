package main.api.response.dislike;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DislikeResponse {

    @JsonProperty("result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
