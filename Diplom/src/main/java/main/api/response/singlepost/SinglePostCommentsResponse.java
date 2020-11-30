package main.api.response.singlepost;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SinglePostCommentsResponse {

    @JsonProperty("comments")
    private List<SingleCommentInfoResponse> comments;

    public List<SingleCommentInfoResponse> getComments() {
        return comments;
    }

    public void setComments(List<SingleCommentInfoResponse> comments) {
        this.comments = comments;
    }
}
