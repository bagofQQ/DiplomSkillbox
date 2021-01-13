package main.api.response.image;

public class ImageResponse {

    private boolean result;
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
