package main.service;

import main.PostsException;
import main.api.response.posts.UserResponse;
import main.api.response.singlepost.SingleCommentInfoResponse;
import main.api.response.singlepost.SinglePostResponse;
import main.api.response.singlepost.UserPhotoResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SinglePostService {

    @Value("${blog.constants.user}")
    private int USER;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostCommentRepository postCommentRepository;

    @Autowired
    public SinglePostService(UserRepository userRepository, PostRepository postRepository, PostVotesRepository postVotesRepository, PostCommentRepository postCommentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Transactional
    public ResponseEntity<SinglePostResponse> getSinglePost(int id, String identifier, HashMap<String, Integer> identifierMap) {
        Post post = postRepository.findById(id).orElseThrow(PostsException::new);
        setView(identifierMap, identifier, post);
        return new ResponseEntity<>(setSinglPost(post), HttpStatus.OK);
    }

    private void setView(HashMap<String, Integer> identifierMap, String identifier, Post post) {
        if (identifierMap.containsKey(identifier)) {
            User user = userRepository.findById(identifierMap.get(identifier)).orElseThrow(PostsException::new);
            if (user.getIsModerator() == USER & post.getUser().getId() != user.getId()) {
                post.setViewCount(post.getViewCount() + 1);
                postRepository.save(post);
            }
        } else if (!identifierMap.containsKey(identifier)) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
    }

    private SinglePostResponse setSinglPost(Post post) {
        SinglePostResponse singlePostResponse = new SinglePostResponse();
        UserResponse userResponse = new UserResponse();

        userResponse.setName(post.getUser().getName());
        userResponse.setId(post.getUser().getId());
        singlePostResponse.setId(post.getId());
        singlePostResponse.setTimestamp(getDate(Calendar.getInstance().getTime(), post.getTime()));
        singlePostResponse.setActive(true);
        singlePostResponse.setUser(userResponse);
        singlePostResponse.setTitle(post.getTitle());
        singlePostResponse.setText(post.getText());
        singlePostResponse.setLikeCount(postVotesRepository.findLike(post.getId()).size());
        singlePostResponse.setDislikeCount(postVotesRepository.findDislike(post.getId()).size());
        singlePostResponse.setViewCount(post.getViewCount());
        singlePostResponse.setComments(getCommentList(post));
        singlePostResponse.setTags(getTagList(post));

        return singlePostResponse;
    }

    private List<String> getTagList(Post post) {
        List<String> tagsList = new ArrayList();

        post.getTags().forEach(tags1 -> {
            tagsList.add(tags1.getName());
        });
        return tagsList;
    }

    private List<SingleCommentInfoResponse> getCommentList(Post post) {
        List<SingleCommentInfoResponse> singlePostCommentList = new ArrayList<>();
        List<PostComment> postCommentList = postCommentRepository.findComment(post.getId());
        for (PostComment postComment : postCommentList) {
            if (post.getId() == postComment.getPostId()) {
                User user = userRepository.findById(postComment.getUser().getId()).orElseThrow(PostsException::new);
                UserPhotoResponse ups = new UserPhotoResponse();
                ups.setId(user.getId());
                ups.setName(user.getName());
                ups.setPhoto(user.getPhoto());

                SingleCommentInfoResponse singlePostComment = new SingleCommentInfoResponse();
                singlePostComment.setId(postComment.getId());
                singlePostComment.setText(postComment.getText());

                Date dateComment = postComment.getTime();
                long timestampComment = dateComment.getTime() / 1000;
                singlePostComment.setTimestamp(timestampComment);
                singlePostComment.setUser(ups);
                singlePostCommentList.add(singlePostComment);
            }
        }
        return singlePostCommentList;
    }

    private long getDate(Date date, Date dateSinglePost) {
        if (dateSinglePost.before(date)) {
            return dateSinglePost.getTime() / 1000;
        } else {
            return date.getTime() / 1000;
        }
    }
}
