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
        TreeMap<String, Integer> posts = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Iterable<Post> postsIterable = postRepository.findAll();
        for (Post f : postsIterable) {
            Calendar postDate = new GregorianCalendar();
            postDate.setTime(f.getTime());
            years.add(postDate.get(1));

            Date date = f.getTime();
            List<Integer> countDateList = new ArrayList<>();
            for (Post q : postsIterable) {
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
