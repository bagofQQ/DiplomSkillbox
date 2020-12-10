package main.api.response.like;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeResponse {

    @JsonProperty("result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
