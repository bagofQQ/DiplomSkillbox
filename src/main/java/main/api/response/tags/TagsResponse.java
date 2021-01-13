package main.api.response.tags;

import java.util.HashSet;

public class TagsResponse {

    private HashSet<TagsInfoResponse> tags;

    public HashSet<TagsInfoResponse> getTags() {
        return tags;
    }

    public void setTags(HashSet<TagsInfoResponse> tags) {
        this.tags = tags;
    }

}
