package main.api.response.comment;

public class CommentResponse {

    private int id;
    private boolean result;
    private ErrorsCommentResponse errors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsCommentResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsCommentResponse errors) {
        this.errors = errors;
    }
}
