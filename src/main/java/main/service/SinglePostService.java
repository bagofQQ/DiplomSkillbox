package main.service;

import main.api.response.posts.UserResponse;
import main.api.response.singlepost.*;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public ResponseEntity<SinglePostResponse> getSinglePost(int id, String identifier, HashMap<String, Integer> identifierMap) {

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            if (optionalPost.get().getIsActive() == ACTIVE_POST & optionalPost.get().getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                return new ResponseEntity(singlPostRecording(identifierMap, identifier,optionalPost, true), HttpStatus.OK);
            }
            return new ResponseEntity(singlPostRecording(identifierMap, identifier,optionalPost, true), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }

    private SinglePostResponse singlPostRecording(HashMap<String, Integer> identifierMap, String identifier, Optional<Post> optionalPost, boolean active) {
        SinglePostResponse singlePostResponse = new SinglePostResponse();
        UserResponse userResponse = new UserResponse();
        Optional<User> optionalUserAuthor = userRepository.findById(optionalPost.get().getUser().getId());
        userResponse.setName(optionalUserAuthor.get().getName());
        userResponse.setId(optionalUserAuthor.get().getId());


        if (identifierMap.containsKey(identifier)) {
            int q = identifierMap.get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            if (optionalUser.get().getIsModerator() == USER) {
                if (optionalPost.get().getUser().getId() != optionalUser.get().getId()) {
                    optionalPost.get().setViewCount(optionalPost.get().getViewCount() + 1);
                    postRepository.save(optionalPost.get());
                }
            }
        } else if (!identifierMap.containsKey(identifier)) {
            optionalPost.get().setViewCount(optionalPost.get().getViewCount() + 1);
            postRepository.save(optionalPost.get());
        }


        singlePostResponse.setId(optionalPost.get().getId());

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Date dateSinglePost = optionalPost.get().getTime();

        if (dateSinglePost.before(date)) {
            long timestamp = dateSinglePost.getTime() / 1000;
            singlePostResponse.setTimestamp(timestamp);
        } else {
            long timestamp = date.getTime() / 1000;
            singlePostResponse.setTimestamp(timestamp);
        }


        singlePostResponse.setActive(active);
        singlePostResponse.setUser(userResponse);

        singlePostResponse.setTitle(optionalPost.get().getTitle());
        singlePostResponse.setText(optionalPost.get().getText());

        List<PostVotes> likeList = postVotesRepository.findLike(optionalPost.get().getId());
        List<PostVotes> dislikeList = postVotesRepository.findDislike(optionalPost.get().getId());
        singlePostResponse.setLikeCount(likeList.size());
        singlePostResponse.setDislikeCount(dislikeList.size());
        singlePostResponse.setViewCount(optionalPost.get().getViewCount());


        List<SingleCommentInfoResponse> sCommentList = new ArrayList<>();

        List<PostComment> commentList = postCommentRepository.findComment(optionalPost.get().getId());
        for(PostComment f : commentList){
            if (optionalPost.get().getId() == f.getPostId()) {
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

        SinglePostTagsResponse tags = new SinglePostTagsResponse();
        List<String> tagsList = new ArrayList();
        optionalPost.get().getTags().forEach(tags1 -> {
            tagsList.add(tags1.getName());
        });
        tags.setTags(tagsList);
        singlePostResponse.setTags(tags);

        return singlePostResponse;


    }
}
