package main.api.response.singlepost;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SinglePostTagsResponse {

    @JsonProperty("tags")
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
