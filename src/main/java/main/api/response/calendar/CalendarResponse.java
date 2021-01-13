package main.api.response.calendar;

import org.json.simple.JSONObject;

import java.util.TreeSet;

public class CalendarResponse {

    private TreeSet<Integer> years;
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
