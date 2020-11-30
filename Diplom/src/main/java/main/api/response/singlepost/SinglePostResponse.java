package main.api.response.singlepost;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.posts.UserResponse;

import java.util.List;

public class SinglePostResponse {


    @JsonProperty("id")
    private int id;
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("user")
    private UserResponse user;
    @JsonProperty("title")
    private String title;
    @JsonProperty("text")
    private String text;
    @JsonProperty("likeCount")
    private int likeCount;
    @JsonProperty("dislikeCount")
    private int dislikeCount;
    @JsonProperty("viewCount")
    private int viewCount;
    @JsonProperty("comments")
    private List<SingleCommentInfoResponse> comments;
    @JsonProperty("tags")
    private SinglePostTagsResponse tags;






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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<SingleCommentInfoResponse> getComments() {
        return comments;
    }

    public void setComments(List<SingleCommentInfoResponse> comments) {
        this.comments = comments;
    }


    public SinglePostTagsResponse getTags() {
        return tags;
    }

    public void setTags(SinglePostTagsResponse tags) {
        this.tags = tags;
    }

}
