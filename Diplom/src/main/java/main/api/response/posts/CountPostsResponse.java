package main.api.response.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountPostsResponse {

    @JsonProperty("count")
    private int count;
    @JsonProperty("posts")
    private List<PostsResponse> posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostsResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsResponse> posts) {
        this.posts = posts;
    }
}
