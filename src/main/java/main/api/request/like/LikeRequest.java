package main.api.request.like;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeRequest {

    @JsonProperty("post_id")
    private int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
