package main.api.response.tags;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;

public class TagsResponse {

//    @JsonProperty("tags")
//    private List<TagsInfoResponse> tags;

    @JsonProperty("tags")
    private HashSet<TagsInfoResponse> tags;

    public HashSet<TagsInfoResponse> getTags() {
        return tags;
    }

    public void setTags(HashSet<TagsInfoResponse> tags) {
        this.tags = tags;
    }

//    public List<TagsInfoResponse> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<TagsInfoResponse> tags) {
//        this.tags = tags;
//    }
}
