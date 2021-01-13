package main.api.response.singlepost;

import java.util.List;

public class SinglePostCommentsResponse {

    private List<SingleCommentInfoResponse> comments;

    public List<SingleCommentInfoResponse> getComments() {
        return comments;
    }

    public void setComments(List<SingleCommentInfoResponse> comments) {
        this.comments = comments;
    }
}
