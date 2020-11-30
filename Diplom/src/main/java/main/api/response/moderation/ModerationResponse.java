package main.api.response.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModerationResponse {

    @JsonProperty("result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
