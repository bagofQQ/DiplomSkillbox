package main.service;

import main.PostsException;
import main.api.response.posts.CountPostsResponse;
import main.api.response.posts.PostsResponse;
import main.api.response.posts.UserResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PostsService {

    private static final String MODE_POPULAR = "popular";
    private static final String MODE_BEST = "best";
    private static final String MODE_EARLY = "early";

    @Value("${blog.constants.statusMypostInactive}")
    private String STATUS_MYPOST_INACTIVE;
    @Value("${blog.constants.statusMypostPending}")
    private String STATUS_MYPOST_PENDING;
    @Value("${blog.constants.statusMypostDeclined}")
    private String STATUS_MYPOST_DECLINED;
    @Value("${blog.constants.statusMypostPublished}")
    private String STATUS_MYPOST_PUBLISHED;

    @Value("${blog.constants.moderationNew}")
    private String MODERATION_NEW;
    @Value("${blog.constants.moderationAccepted}")
    private String MODERATION_ACCEPTED;
    @Value("${blog.constants.moderationDeclined}")
    private String MODERATION_DECLINED;

    @Value("${blog.constants.activePost}")
    private int ACTIVE_POST;
    @Value("${blog.constants.inactivePost}")
    private int INACTIVE_POST;

    @Value("${blog.constants.statusNew}")
    private String STATUS_NEW;
    @Value("${blog.constants.statusDeclined}")
    private String STATUS_DECLINED;
    @Value("${blog.constants.statusAccepted}")
    private String STATUS_ACCEPTED;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostCommentRepository postCommentRepository;
    private final TagRepository tagRepository;
    private final HttpSession httpSession;
    private final UserLoginService userLoginService;

    @Autowired
    public PostsService(UserRepository userRepository,
                        PostRepository postRepository,
                        PostVotesRepository postVotesRepository,
                        PostCommentRepository postCommentRepository,
                        TagRepository tagRepository, HttpSession httpSession, UserLoginService userLoginService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
        this.httpSession = httpSession;
        this.userLoginService = userLoginService;
    }


    public CountPostsResponse getPosts(int offset, int limit, String mode) {
        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
        int offsetPageable = offset / 10;

        Page<Post> page;

        switch (mode) {
            case MODE_EARLY:
                page = postRepository.findEarlyPosts(getDateNow(), getPageable(offsetPageable, limit));
                break;
            case MODE_POPULAR:
                page = postRepository.findPopularPosts(getDateNow(), getPageable(offsetPageable, limit));
                break;
            case MODE_BEST:
                page = postRepository.findBestPosts(getDateNow(), getPageable(offsetPageable, limit));
                break;
            default:
                page = postRepository.findResentPosts(getDateNow(), getPageable(offsetPageable, limit));
                break;
        }

        addPostsList(page, postsList);

        countPostsResponse.setCount(postRepository.countActivePosts());
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;

    }

    public CountPostsResponse searchTags(int offset, int limit, String tag) {

        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();

        int offsetPageable = offset / 10;

        int idTag = postRepository.idTag(tag);
        Page<Post> page = postRepository.findTagPosts(getDateNow(), idTag, getPageable(offsetPageable, limit));
        addPostsList(page, postsList);

        countPostsResponse.setCount(postRepository.countTagPosts(idTag));
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;

    }

    public CountPostsResponse searchByDate(int offset, int limit, String dateString) {

        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
        int offsetPageable = offset / 10;
        Page<Post> page = postRepository.findDatePosts(getDateNow(), dateString, getPageable(offsetPageable, limit));
        addPostsList(page, postsList);

        countPostsResponse.setCount(postRepository.countDate(dateString));
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;
    }

    public CountPostsResponse getModerationPosts(int offset, int limit, String status) {
        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
        int idModer = userLoginService.getIdentifierMap().get(httpSession.getId());
        int offsetPageable = offset / 10;

        if (status.equals(STATUS_NEW)) {
            Page<Post> page = postRepository.findModerationPostsNew(getDateNow(), MODERATION_NEW, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatus(MODERATION_NEW));
        } else if (status.equals(STATUS_DECLINED)) {
            Page<Post> page = postRepository.findModerationPosts(getDateNow(), idModer, MODERATION_DECLINED, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatus(MODERATION_DECLINED));
        } else if (status.equals(STATUS_ACCEPTED)) {
            Page<Post> page = postRepository.findModerationPosts(getDateNow(), idModer, MODERATION_ACCEPTED, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatus(MODERATION_ACCEPTED));
        }
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;

    }

    public CountPostsResponse getMyPosts(int offset, int limit, String status) {
        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
        int idUser = userLoginService.getIdentifierMap().get(httpSession.getId());
        int offsetPageable = offset / 10;

        if (status.equals(STATUS_MYPOST_INACTIVE)) {
            Page<Post> page = postRepository.findMyPosts(getDateNow(), idUser, INACTIVE_POST, MODERATION_NEW, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatusUser(MODERATION_NEW, INACTIVE_POST, idUser));
        } else if (status.equals(STATUS_MYPOST_DECLINED)) {
            Page<Post> page = postRepository.findMyPosts(getDateNow(), idUser, ACTIVE_POST, MODERATION_DECLINED, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatusUser(MODERATION_DECLINED, ACTIVE_POST, idUser));
        } else if (status.equals(STATUS_MYPOST_PUBLISHED)) {
            Page<Post> page = postRepository.findMyPosts(getDateNow(), idUser, ACTIVE_POST, MODERATION_ACCEPTED, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatusUser(MODERATION_ACCEPTED, ACTIVE_POST, idUser));
        } else if (status.equals(STATUS_MYPOST_PENDING)) {
            Page<Post> page = postRepository.findMyPosts(getDateNow(), idUser, ACTIVE_POST, MODERATION_NEW, getPageable(offsetPageable, limit));
            addPostsList(page, postsList);
            countPostsResponse.setCount(postRepository.countModStatusUser(MODERATION_NEW, ACTIVE_POST, idUser));
        }
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;
    }

    public CountPostsResponse searchPosts(int offset, int limit, String query) {

        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
        int offsetPageable = offset / 10;
        Page<Post> page = postRepository.findSearchPosts(getDateNow(), query, getPageable(offsetPageable, limit));
        addPostsList(page, postsList);
        countPostsResponse.setCount(postRepository.countSearchCount(getDateNow(), query));
        countPostsResponse.setPosts(postsList);
        return countPostsResponse;

    }

    private void addPostsList(Page<Post> page, List<PostsResponse> postsList) {
        for (Post post : page) {
            PostsResponse postsResponse = new PostsResponse();
            postsList.add(getPostsResponse(post, postsResponse));
        }
    }

    private PostsResponse getPostsResponse(Post post, PostsResponse postsResponse) {

        postsResponse.setId(post.getId());
        Date date = post.getTime();
        long timestamp = date.getTime() / 1000;
        postsResponse.setTimestamp(timestamp);
        UserResponse userResponse = new UserResponse();
        User user = userRepository.findById(post.getUser().getId()).orElseThrow(PostsException::new);
        userResponse.setName(user.getName());
        userResponse.setId(user.getId());
        postsResponse.setUser(userResponse);
        postsResponse.setTitle(post.getTitle());
        postsResponse.setAnnounce(getAnnounce(post.getText()));
        postsResponse.setLikeCount(postVotesRepository.findLike(post.getId()).size());
        postsResponse.setDislikeCount(postVotesRepository.findDislike(post.getId()).size());
        postsResponse.setCommentCount(postCommentRepository.findComment(post.getId()).size());
        postsResponse.setViewCount(post.getViewCount());

        return postsResponse;
    }

    private String getAnnounce(String text) {
        String cleanText = Jsoup.clean(text, Whitelist.none());
        if (cleanText.substring(40, 50).contains(" ")) {
            String announce = cleanText.replaceAll("(\\A.{40}\\S+)(.+)", "$1");
            return announce + "...";
        } else {
            String announce = cleanText.replaceAll("(\\A.{40})(.+)", "$1");
            return announce + "...";
        }
    }

    private Date getDateNow() {
        return Calendar.getInstance().getTime();
    }

    private Pageable getPageable(int offsetPageable, int limit) {
        return PageRequest.of(offsetPageable, limit);
    }

}
