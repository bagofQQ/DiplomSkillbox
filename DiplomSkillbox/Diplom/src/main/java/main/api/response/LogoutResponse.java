package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutResponse {

    @JsonProperty("result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
