package main.api.response.singlepost;

public class SingleCommentInfoResponse {

    private int id;
    private long timestamp;
    private String text;
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
