package main.api.response.addingpost;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorsAddingPostResponse {

    @JsonProperty("title")
    private String title;
    @JsonProperty("text")
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
