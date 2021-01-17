package main.api.response.moderation;

public class ModerationResponse {

    private boolean result;

    public ModerationResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
