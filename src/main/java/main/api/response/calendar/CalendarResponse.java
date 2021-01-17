package main.api.response.calendar;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.TreeSet;

public class CalendarResponse {

    private List<Integer> years;
    private JSONObject posts;

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public JSONObject getPosts() {
        return posts;
    }

    public void setPosts(JSONObject posts) {
        this.posts = posts;
    }
}
