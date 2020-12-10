package main.api.response.restore;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestoreResponse {

    @JsonProperty("result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
