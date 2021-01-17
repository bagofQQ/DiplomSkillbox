package main.api.response.vote;

public class VoteResponse {

    private boolean result;

    public VoteResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
