package main.api.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorsCommentResponse {

    @JsonProperty("text")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
