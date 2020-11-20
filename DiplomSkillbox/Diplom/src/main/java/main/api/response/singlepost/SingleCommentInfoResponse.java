package main.api.response.singlepost;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleCommentInfoResponse {

    @JsonProperty("id")
    private int id;
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("text")
    private String text;
    @JsonProperty("user")
    private UserPhotoResponse user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserPhotoResponse getUser() {
        return user;
    }

    public void setUser(UserPhotoResponse user) {
        this.user = user;
    }
}
