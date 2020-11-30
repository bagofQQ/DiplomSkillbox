package main.service;

import main.api.response.StatisticsResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class AllStatisticsService {

    private static final int LIKE = 1;
    private static final int DISLIKE = -1;
    private static final String SIP_CODE = "SIP";
    private static final String VALUE_YES = "YES";
    private static final String MODERATION_ACCEPTED = "ACCEPTED";
    private static final int ACTIVE_POST = 1;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<StatisticsResponse> getStatistics(HashMap<String, Integer> identifierMap) {
        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();

        if (checkSettings(SIP_CODE, globalSettingsIterable)) {
            return new ResponseEntity(getAllStat(), HttpStatus.OK);
        } else {
            String identifier = httpSession.getId();
            if (identifierMap.containsKey(identifier)) {
                int q = identifierMap.get(identifier);
                Optional<User> optionalUser = userRepository.findById(q);
                if (optionalUser.get().getIsModerator() == 1) {
                    return new ResponseEntity(getAllStat(), HttpStatus.OK);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    public StatisticsResponse getAllStat() {
        StatisticsResponse statisticsResponse = new StatisticsResponse();

        List postList = new ArrayList();
        List<Integer> viewsList = new ArrayList();
        List<Long> publicationTimeList = new ArrayList();
        List likeList = new ArrayList();
        List dislikeList = new ArrayList();

        Iterable<Post> postsIterable = postRepository.findAll();
        if (postsIterable.iterator().hasNext()) {
            for (Post f : postsIterable) {
                if (f.getIsActive() == ACTIVE_POST & f.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                    postList.add(f.getId());
                    viewsList.add(f.getViewCount());
                    Date date = f.getTime();
                    long timestamp = date.getTime() / 1000;
                    publicationTimeList.add(timestamp);
                }
            }
            if (viewsList.size() > 0 & publicationTimeList.size() > 0) {
                int viewsCount = viewsList.stream().mapToInt(Integer::intValue).sum();
                long firstPublication = publicationTimeList.stream().sorted().findFirst().get();
                statisticsResponse.setViewsCount(viewsCount);
                statisticsResponse.setFirstPublication(firstPublication);
            } else {
                statisticsResponse.setViewsCount(0);
            }

            Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
            for (PostVotes pv : postVotesIterable) {
                if (pv.getValue() == LIKE) {
                    likeList.add(pv.getId());
                } else if (pv.getValue() == DISLIKE) {
                    dislikeList.add(pv.getId());
                }
            }

            statisticsResponse.setPostsCount(postList.size());
            statisticsResponse.setLikesCount(likeList.size());
            statisticsResponse.setDislikesCount(dislikeList.size());

            return statisticsResponse;
        }

        statisticsResponse.setPostsCount(postList.size());
        statisticsResponse.setLikesCount(likeList.size());
        statisticsResponse.setDislikesCount(dislikeList.size());
        statisticsResponse.setViewsCount(0);
        return statisticsResponse;
    }

    public StatisticsResponse getUserStat(Optional<User> optionalUser) {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        int idUser = optionalUser.get().getId();

        List postList = new ArrayList();
        List<Integer> viewsList = new ArrayList();
        List<Long> publicationTimeList = new ArrayList();
        List likeList = new ArrayList();
        List dislikeList = new ArrayList();

        Iterable<Post> postsIterable = postRepository.findAll();
        if (postsIterable.iterator().hasNext()) {
            for (Post f : postsIterable) {
                if (f.getIsActive() == ACTIVE_POST & f.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                    if (idUser == f.getUser().getId()) {
                        postList.add(f.getId());
                        viewsList.add(f.getViewCount());
                        Date date = f.getTime();
                        long timestamp = date.getTime() / 1000;
                        publicationTimeList.add(timestamp);
                    }
                }
            }
            if (viewsList.size() > 0 & publicationTimeList.size() > 0) {
                int viewsCount = viewsList.stream().mapToInt(Integer::intValue).sum();
                long firstPublication = publicationTimeList.stream().sorted().findFirst().get();
                statisticsResponse.setViewsCount(viewsCount);
                statisticsResponse.setFirstPublication(firstPublication);
            } else {
                statisticsResponse.setViewsCount(0);
            }

            Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
            for (PostVotes pv : postVotesIterable) {
                if (idUser == pv.getUser().getId()) {
                    if (pv.getValue() == LIKE) {
                        likeList.add(pv.getId());
                    } else if (pv.getValue() == DISLIKE) {
                        dislikeList.add(pv.getId());
                    }
                }
            }

            statisticsResponse.setPostsCount(postList.size());
            statisticsResponse.setLikesCount(likeList.size());
            statisticsResponse.setDislikesCount(dislikeList.size());

            return statisticsResponse;
        }

        statisticsResponse.setPostsCount(postList.size());
        statisticsResponse.setLikesCount(likeList.size());
        statisticsResponse.setDislikesCount(dislikeList.size());
        statisticsResponse.setViewsCount(0);

        return statisticsResponse;
    }

    private boolean checkSettings(String code, Iterable<GlobalSettings> globalSettingsIterable) {
        for (GlobalSettings setting : globalSettingsIterable) {
            if (setting.getCode().equals(code)) {
                if (setting.getValue().equals(VALUE_YES)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
