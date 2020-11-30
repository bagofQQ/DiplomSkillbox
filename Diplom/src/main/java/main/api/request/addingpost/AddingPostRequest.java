package main.api.request.addingpost;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddingPostRequest {

    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("active")
    private int active;
    @JsonProperty("title")
    private String title;
    @JsonProperty("tags")
    private String[] tags;
    @JsonProperty("text")
    private String text;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
