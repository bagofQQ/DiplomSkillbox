package main.service;

import main.api.response.StatisticsResponse;
import main.model.GlobalSettingsRepository;
import main.model.PostRepository;
import main.model.PostVotesRepository;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;

@Service
public class AllStatisticsService {

    @Value("${blog.constants.valueYes}")
    private String VALUE_YES;
    @Value("${blog.constants.moderator}")
    private int MODERATOR;

    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final PostVotesRepository postVotesRepository;
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Autowired
    public AllStatisticsService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, PostVotesRepository postVotesRepository, UserRepository userRepository, HttpSession httpSession) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.userRepository = userRepository;
        this.httpSession = httpSession;
    }


    public ResponseEntity<StatisticsResponse> getStatistics(HashMap<String, Integer> identifierMap) {
        if (!globalSettingsRepository.findValueStatisticsIsPublic().equals(VALUE_YES)) {
            if (!identifierMap.containsKey(httpSession.getId()) && userRepository.findById(identifierMap.get(httpSession.getId())).get().getIsModerator() != MODERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return new ResponseEntity(getAllStat(), HttpStatus.OK);
        }
        return new ResponseEntity(getAllStat(), HttpStatus.OK);
    }

    public StatisticsResponse getAllStat() {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        int countActivePosts = postRepository.countActivePosts();
        if (countActivePosts > 0) {
            setStat(statisticsResponse,
                    countActivePosts,
                    postVotesRepository.countLike(),
                    postVotesRepository.countDislike(),
                    postRepository.earlyDate(),
                    postRepository.viewCount());
        }
        return statisticsResponse;
    }

    public StatisticsResponse getUserStat(int idUser) {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        int countActivePosts = postRepository.countActivePostsUser(idUser);
        if (countActivePosts > 0) {
            setStat(statisticsResponse,
                    countActivePosts,
                    postVotesRepository.countLikeUser(idUser),
                    postVotesRepository.countDislikeUser(idUser),
                    postRepository.earlyDateUser(idUser),
                    postRepository.viewCountUser(idUser));
        }
        return statisticsResponse;
    }

    private void setStat(StatisticsResponse statisticsResponse, int countActivePosts, int like, int dislike, Date firstDate, int viewsCount) {
        long firstPublication = firstDate.getTime() / 1000;
        statisticsResponse.setPostsCount(countActivePosts);
        statisticsResponse.setLikesCount(like);
        statisticsResponse.setDislikesCount(dislike);
        statisticsResponse.setViewsCount(viewsCount);
        statisticsResponse.setFirstPublication(firstPublication);
    }

}
