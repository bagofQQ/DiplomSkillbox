package main.api.request.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModerationRequest {

    @JsonProperty("post_id")
    private int postId;
    @JsonProperty("decision")
    private String decision;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
