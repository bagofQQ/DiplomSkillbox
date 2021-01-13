package main.api.response.addingpost;

public class AddingPostResponse {

    private boolean result;
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
