package main.service;

import main.PostsException;
import main.api.response.posts.UserResponse;
import main.api.response.singlepost.SingleCommentInfoResponse;
import main.api.response.singlepost.SinglePostResponse;
import main.api.response.singlepost.UserPhotoResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SinglePostService {

    private static final String MODERATION_ACCEPTED = "ACCEPTED";
    private static final int ACTIVE_POST = 1;
    private static final int MODERATOR = 1;
    private static final int USER = 0;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Transactional
    public ResponseEntity<SinglePostResponse> getSinglePost(int id, String identifier, HashMap<String, Integer> identifierMap) {
        Post post = postRepository.findById(id).orElseThrow(PostsException::new);
        return new ResponseEntity<>(singlPostRecording(identifierMap, identifier, post), HttpStatus.OK);
    }

    private SinglePostResponse singlPostRecording(HashMap<String, Integer> identifierMap, String identifier, Post post) {
        SinglePostResponse singlePostResponse = new SinglePostResponse();
        UserResponse userResponse = new UserResponse();
        Optional<User> optionalUserAuthor = userRepository.findById(post.getUser().getId());
        userResponse.setName(optionalUserAuthor.get().getName());
        userResponse.setId(optionalUserAuthor.get().getId());


        if (identifierMap.containsKey(identifier)) {
            int q = identifierMap.get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            if (optionalUser.get().getIsModerator() == USER) {
                if (post.getUser().getId() != optionalUser.get().getId()) {
                    post.setViewCount(post.getViewCount() + 1);
                    postRepository.save(post);
                }
            }
        } else if (!identifierMap.containsKey(identifier)) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }

        singlePostResponse.setId(post.getId());

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Date dateSinglePost = post.getTime();

        if (dateSinglePost.before(date)) {
            long timestamp = dateSinglePost.getTime() / 1000;
            singlePostResponse.setTimestamp(timestamp);
        } else {
            long timestamp = date.getTime() / 1000;
            singlePostResponse.setTimestamp(timestamp);
        }
        singlePostResponse.setActive(true);
        singlePostResponse.setUser(userResponse);

        singlePostResponse.setTitle(post.getTitle());
        singlePostResponse.setText(post.getText());

        List<PostVotes> likeList = postVotesRepository.findLike(post.getId());
        List<PostVotes> dislikeList = postVotesRepository.findDislike(post.getId());

        singlePostResponse.setLikeCount(likeList.size());
        singlePostResponse.setDislikeCount(dislikeList.size());

        singlePostResponse.setViewCount(post.getViewCount());

        List<SingleCommentInfoResponse> sCommentList = new ArrayList<>();

        List<PostComment> commentList = postCommentRepository.findComment(post.getId());
        for (PostComment f : commentList) {
            if (post.getId() == f.getPostId()) {
                Optional<User> optionalUsersComment = userRepository.findById(f.getUser().getId());
                UserPhotoResponse us = new UserPhotoResponse();
                us.setId(optionalUsersComment.get().getId());
                us.setName(optionalUsersComment.get().getName());
                us.setPhoto(optionalUsersComment.get().getPhoto());

                SingleCommentInfoResponse sComment = new SingleCommentInfoResponse();
                sComment.setId(f.getId());
                sComment.setText(f.getText());

                Date dateComment = f.getTime();
                long timestampComment = dateComment.getTime() / 1000;
                sComment.setTimestamp(timestampComment);
                sComment.setUser(us);
                sCommentList.add(sComment);
            }
        }
        singlePostResponse.setComments(sCommentList);

        List<String> tagsList = new ArrayList();

        post.getTags().forEach(tags1 -> {
            tagsList.add(tags1.getName());
        });
        singlePostResponse.setTags(tagsList);

        return singlePostResponse;
    }
}
