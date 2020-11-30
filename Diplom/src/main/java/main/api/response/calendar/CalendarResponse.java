package main.api.response.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.TreeSet;

public class CalendarResponse {

    @JsonProperty("years")
    private TreeSet<Integer> years;

    @JsonProperty("posts")
    private JSONObject posts;

    public TreeSet<Integer> getYears() {
        return years;
    }

    public void setYears(TreeSet<Integer> years) {
        this.years = years;
    }

    public JSONObject getPosts() {
        return posts;
    }

    public void setPosts(JSONObject posts) {
        this.posts = posts;
    }
}
