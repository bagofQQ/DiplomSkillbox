package main.service;

import main.api.response.posts.CountPostsResponse;
import main.api.response.posts.PostsResponse;
import main.api.response.posts.UserResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostsService {

    private static final String MODE_RECENT = "recent";
    private static final String MODE_POPULAR = "popular";
    private static final String MODE_BEST = "best";
    private static final String MODE_EARLY = "early";

    private static final String STATUS_MYPOST_INACTIVE = "inactive";
    private static final String STATUS_MYPOST_PENDING = "pending";
    private static final String STATUS_MYPOST_DECLINED = "declined";
    private static final String STATUS_MYPOST_PUBLISHED = "published";

    private static final String MODERATION_NEW = "NEW";
    private static final String MODERATION_ACCEPTED = "ACCEPTED";
    private static final String MODERATION_DECLINED = "DECLINED";

    private static final int ACTIVE_POST = 1;
    private static final int INACTIVE_POST = 0;

    private static final String STATUS_NEW = "new";
    private static final String STATUS_DECLINED = "declined";
    private static final String STATUS_ACCEPTED = "accepted";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private TagRepository tagRepository;

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

    public CountPostsResponse getModerationPosts(int idModer, int offset, int limit, String status) {

        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
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

    public CountPostsResponse getMyPosts(int idUser, int offset, int limit, String status) {

        CountPostsResponse countPostsResponse = new CountPostsResponse();
        List<PostsResponse> postsList = new ArrayList<>();
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

    private PostsResponse getPostsResponse(Post post, PostsResponse postsResponse) {

        postsResponse.setId(post.getId());
        Date date = post.getTime();
        long timestamp = date.getTime() / 1000;
        postsResponse.setTimestamp(timestamp);
        UserResponse userResponse = new UserResponse();
        Optional<User> optionalUser = userRepository.findById(post.getUser().getId());
        userResponse.setName(optionalUser.get().getName());
        userResponse.setId(optionalUser.get().getId());
        postsResponse.setUser(userResponse);
        postsResponse.setTitle(post.getTitle());
        String cleanText = Jsoup.clean(post.getText(), Whitelist.none());

        if (cleanText.substring(40, 50).contains(" ")) {
            String announce = cleanText.replaceAll("(\\A.{40}\\S+)(.+)", "$1");
            postsResponse.setAnnounce(announce + "...");
        } else {
            String announce = cleanText.replaceAll("(\\A.{40})(.+)", "$1");
            postsResponse.setAnnounce(announce + "...");
        }

        List<PostVotes> likeList = postVotesRepository.findLike(post.getId());
        List<PostVotes> dislikeList = postVotesRepository.findDislike(post.getId());
        postsResponse.setLikeCount(likeList.size());
        postsResponse.setDislikeCount(dislikeList.size());

        List<PostComment> commentList = postCommentRepository.findComment(post.getId());
        postsResponse.setCommentCount(commentList.size());
        postsResponse.setViewCount(post.getViewCount());

        return postsResponse;
    }

    private Date getDateNow() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return date;
    }

    private Pageable getPageable(int offsetPageable, int limit) {
        Pageable pageable = PageRequest.of(offsetPageable, limit);
        return pageable;
    }

    private void addPostsList(Page<Post> page, List<PostsResponse> postsList) {
        for (Post post : page) {
            PostsResponse postsResponse = new PostsResponse();
            postsList.add(getPostsResponse(post, postsResponse));
        }
    }
}
