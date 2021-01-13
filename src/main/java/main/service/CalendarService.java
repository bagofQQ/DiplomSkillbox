package main.service;

import main.api.response.calendar.CalendarResponse;
import main.model.Post;
import main.model.PostRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CalendarService {

    @Autowired
    private PostRepository postRepository;

    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();

        TreeSet<Integer> years = new TreeSet<>();
        Calendar calendar = Calendar.getInstance();
        int yearCalendar = calendar.get(1);

        years.add(yearCalendar);
        years.add(yearCalendar - 1);
        TreeMap<String, Integer> posts = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Post> postList = postRepository.findCalendarYear(year);
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
