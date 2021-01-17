package main.service;

import main.api.response.calendar.CalendarResponse;
import main.model.Post;
import main.model.PostRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Service
public class CalendarService {

    private final PostRepository postRepository;

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Integer> years = postRepository.findCalendarYears();

        TreeMap<String, Integer> posts = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Post> postList = postRepository.findPostsYear(year);
        for (Post f : postList) {
            Date date = f.getTime();
            int countDate = postRepository.countDate(dateFormat.format(date));
            posts.put(dateFormat.format(date), countDate);
        }

        calendarResponse.setYears(years);
        calendarResponse.setPosts(new JSONObject(posts));
        return calendarResponse;
    }

}
