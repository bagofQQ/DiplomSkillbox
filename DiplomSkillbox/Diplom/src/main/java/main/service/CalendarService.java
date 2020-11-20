package main.service;

import main.api.response.calendar.CalendarResponse;
import main.model.Posts;
import main.model.PostsRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CalendarService {

    @Autowired
    private PostsRepository postsRepository;

    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();

        TreeSet<Integer> years = new TreeSet<>();
        TreeMap<String, Integer> posts = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts f : postsIterable) {
            Calendar postDate = new GregorianCalendar();
            postDate.setTime(f.getTime());
            years.add(postDate.get(1));

            Date date = f.getTime();
            List<Integer> countDateList = new ArrayList<>();
            for (Posts q : postsIterable) {
                if (dateFormat.format(date).equals(dateFormat.format(q.getTime()))) {
                    countDateList.add(q.getId());
                }
            }

            posts.put(dateFormat.format(date), countDateList.size());
        }
        calendarResponse.setYears(years);
        calendarResponse.setPosts(new JSONObject(posts));
        return calendarResponse;
    }
}
