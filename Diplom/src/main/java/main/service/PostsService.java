package main.service;

import main.api.response.posts.CountPostsResponse;
import main.api.response.posts.PostsResponse;
import main.api.response.posts.UserResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final int LIKE = 1;
    private static final int DISLIKE = -1;

    private static final String MODERATION_NEW = "NEW";
    private static final String MODERATION_ACCEPTED = "ACCEPTED";
    private static final String MODERATION_DECLINED = "DECLINED";

    private static final int ACTIVE_POST = 1;

    private static final String STATUS_NEW = "new";
    private static final String STATUS_DECLINED = "declined";
    private static final String STATUS_ACCEPTED = "accepted";


    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostCommentsRepository postCommentsRepository;

    @Autowired
    private TagsRepository tagsRepository;


    public CountPostsResponse getPosts(int offset, int limit, String mode) {
        CountPostsResponse countPostsResponse = new CountPostsResponse();

        List<PostsResponse> postsList = new ArrayList<>();

        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {
            if (post.getIsActive() == ACTIVE_POST & post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                PostsResponse postsResponse = new PostsResponse();
                postsList.add(getPostsResponse(post, postsResponse));
            }
        }
        countPostsResponse.setCount(postsList.size());
        int pageSize = limit;
        if (mode.equals(MODE_POPULAR)) {
            countPostsResponse.setPosts(postsList.stream()
                    .sorted(Comparator.comparingLong(PostsResponse::getCommentCount).reversed())
                    .skip(offset)
                    .limit(pageSize)
                    .collect(Collectors.toList()));
            return countPostsResponse;
        } else if (mode.equals(MODE_BEST)) {
            countPostsResponse.setPosts(postsList.stream()
                    .sorted(Comparator.comparingLong(PostsResponse::getLikeCount).reversed())
                    .skip(offset)
                    .limit(pageSize)
                    .collect(Collectors.toList()));
            return countPostsResponse;
        } else if (mode.equals(MODE_EARLY)) {
            countPostsResponse.setPosts(postsList.stream()
                    .sorted(Comparator.comparingLong(PostsResponse::getTimestamp))
                    .skip(offset)
                    .limit(pageSize)
                    .collect(Collectors.toList()));
            return countPostsResponse;
        }
        countPostsResponse.setPosts(postsList.stream()
                .sorted(Comparator.comparingLong(PostsResponse::getTimestamp).reversed())
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList()));
        return countPostsResponse;

    }

    public CountPostsResponse searchTags(int offset, int limit, String tag) {

        List<PostsResponse> postsList = new ArrayList<>();
        HashSet<Posts> postsL = new HashSet<>();
        if (tag.contains(",")) {
            String[] tagsFragment = tag.split(",");
            Iterable<Tags> tagsIterable = tagsRepository.findAll();
            for (Tags f : tagsIterable) {
                for (String q : tagsFragment) {
                    if (q.equals(f.getName())) {
                        postsL.addAll(f.getPosts());
                    }
                }
            }
        } else {
            Iterable<Tags> tagsIterable = tagsRepository.findAll();
            for (Tags f : tagsIterable) {
                if (tag.equals(f.getName())) {
                    postsL.addAll(f.getPosts());
                }
            }
        }
        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {
            if (post.getIsActive() == ACTIVE_POST & post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                PostsResponse postsResponse = new PostsResponse();
                for (Posts s : postsL) {
                    if (post.getId() == s.getId()) {
                        postsList.add(getPostsResponse(post, postsResponse));
                    }
                }
            }
        }
        return getCountPostsResponse(postsList, offset, limit);
    }

    public CountPostsResponse searchByDate(int offset, int limit, String dateString) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<PostsResponse> postsList = new ArrayList<>();
        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {
            if (post.getIsActive() == ACTIVE_POST & post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {

                PostsResponse postsResponse = new PostsResponse();

                if (dateString.equals(dateFormat.format(post.getTime()))) {
                    postsList.add(getPostsResponse(post, postsResponse));
                }
            }
        }
        return getCountPostsResponse(postsList, offset, limit);
    }

    public CountPostsResponse getModerationPosts(Optional<Users> optionalModer, int offset, int limit, String status) {

        int idModer = optionalModer.get().getId();

        List<PostsResponse> postsList = new ArrayList<>();

        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {
            PostsResponse postsResponse = new PostsResponse();
            if (status.equals(STATUS_NEW)) {
//                Optional<Posts> optionalPosts = postsRepository.findById(post.getId());
                if (post.getIsActive() == ACTIVE_POST & post.getModerationStatus().toString().equals(MODERATION_NEW)) {
                    postsList.add(getPostsResponse(post, postsResponse));
                }
            } else if (status.equals(STATUS_DECLINED)) {
//                Optional<Posts> optionalPosts = postsRepository.findById(post.getId());

                if (idModer == post.getModeratorId()) {
                    if (post.getModerationStatus().toString().equals(MODERATION_DECLINED)) {
                        postsList.add(getPostsResponse(post, postsResponse));
                    }
                }
            } else if (status.equals(STATUS_ACCEPTED)) {
//                Optional<Posts> optionalPosts = postsRepository.findById(post.getId());
                if (idModer == post.getModeratorId()) {
                    if (post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                        postsList.add(getPostsResponse(post, postsResponse));
                    }
                }
            }
        }
        return getCountPostsResponse(postsList, offset, limit);
    }

    public CountPostsResponse getMyPosts(Optional<Users> optionalUser, int offset, int limit, String status) {

        int idUser = optionalUser.get().getId();

        List<PostsResponse> postsList = new ArrayList<>();

        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {
            if (idUser == post.getUser().getId()) {
                PostsResponse postsResponse = new PostsResponse();

                if (status.equals(STATUS_MYPOST_INACTIVE)) {
                    if (post.getIsActive() == 0) {
                        postsList.add(getPostsResponse(post, postsResponse));
                    }
                } else if (status.equals(STATUS_MYPOST_PENDING)) {
                    if (post.getIsActive() == 1) {
                        if (post.getModerationStatus().toString().equals(MODERATION_NEW)) {
                            postsList.add(getPostsResponse(post, postsResponse));
                        }
                    }
                } else if (status.equals(STATUS_MYPOST_DECLINED)) {
                    if (post.getIsActive() == 1) {
                        if (post.getModerationStatus().toString().equals(MODERATION_DECLINED)) {
                            postsList.add(getPostsResponse(post, postsResponse));
                        }
                    }
                } else if (status.equals(STATUS_MYPOST_PUBLISHED)) {
                    if (post.getIsActive() == 1) {
                        if (post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                            postsList.add(getPostsResponse(post, postsResponse));
                        }
                    }
                }
            }
        }
        return getCountPostsResponse(postsList, offset, limit);
    }

    public CountPostsResponse searchPosts(int offset, int limit, String query) {

        List<PostsResponse> postsList = new ArrayList<>();
        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable) {

            if (post.getIsActive() == ACTIVE_POST & post.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                String cleanText = Jsoup.clean(post.getText(), Whitelist.none());
                if (post.getTitle().toLowerCase().contains(query.toLowerCase()) || cleanText.toLowerCase().contains(query.toLowerCase())) {
                    PostsResponse postsResponse = new PostsResponse();
                    postsList.add(getPostsResponse(post, postsResponse));
                }
            }
        }
        return getCountPostsResponse(postsList, offset, limit);
    }


    private CountPostsResponse getCountPostsResponse(List<PostsResponse> postsList, int offset, int limit) {
        CountPostsResponse countPostsResponse = new CountPostsResponse();

        countPostsResponse.setCount(postsList.size());
        countPostsResponse.setPosts(postsList.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList()));

        return countPostsResponse;
    }


    private PostsResponse getPostsResponse(Posts post, PostsResponse postsResponse) {

        postsResponse.setId(post.getId());
        Date date = post.getTime();
        long timestamp = date.getTime() / 1000;
        postsResponse.setTimestamp(timestamp);
        UserResponse userResponse = new UserResponse();
        Optional<Users> optionalUser = usersRepository.findById(post.getUser().getId());
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

        List likeList = new ArrayList();
        List dislikeList = new ArrayList();
        Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
        for (PostVotes pv : postVotesIterable) {
            if (post.getId() == pv.getPostId()) {
                if (pv.getValue() == LIKE) {
                    likeList.add(pv.getId());
                } else if (pv.getValue() == DISLIKE) {
                    dislikeList.add(pv.getId());
                }
            }
        }
        postsResponse.setLikeCount(likeList.size());
        postsResponse.setDislikeCount(dislikeList.size());

        List commentList = new ArrayList();
        Iterable<PostComments> postCommentsIterable = postCommentsRepository.findAll();
        for (PostComments com : postCommentsIterable) {
            if (post.getId() == com.getPostId()) {
                commentList.add(com.getId());
            }
        }
        postsResponse.setCommentCount(commentList.size());
        postsResponse.setViewCount(post.getViewCount());

        return postsResponse;
    }
}
