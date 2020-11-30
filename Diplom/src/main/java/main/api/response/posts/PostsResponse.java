package main.api.response.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostsResponse {

    @JsonProperty("id")
    private int id;
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("user")
    private UserResponse user;
    @JsonProperty("title")
    private String title;
    @JsonProperty("announce")
    private String announce;
    @JsonProperty("likeCount")
    private int likeCount;
    @JsonProperty("dislikeCount")
    private int dislikeCount;
    @JsonProperty("commentCount")
    private int commentCount;
    @JsonProperty("viewCount")
    private int viewCount;

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

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
