package main.service;

import main.api.response.posts.UserResponse;
import main.api.response.singlepost.*;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SinglePostService {

    private static final int LIKE = 1;
    private static final int DISLIKE = -1;

    private static final String MODERATION_ACCEPTED = "ACCEPTED";
    private static final int ACTIVE_POST = 1;
    private static final int MODERATOR = 1;
    private static final int USER = 0;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostCommentsRepository postCommentsRepository;


    public SinglePostResponse getSinglePost(Optional<Posts> optionalPost, String identifier, HashMap<String, Integer> identifierMap) {
        if (optionalPost.get().getIsActive() == ACTIVE_POST & optionalPost.get().getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
            return singlPostRecording(identifierMap, identifier, optionalPost, true);
        }
        return singlPostRecording(identifierMap, identifier, optionalPost, false);
    }

    private SinglePostResponse singlPostRecording(HashMap<String, Integer> identifierMap, String identifier, Optional<Posts> optionalPost, boolean active) {
        SinglePostResponse singlePostResponse = new SinglePostResponse();
        UserResponse userResponse = new UserResponse();
        Optional<Users> optionalUserAuthor = usersRepository.findById(optionalPost.get().getUser().getId());
        userResponse.setName(optionalUserAuthor.get().getName());
        userResponse.setId(optionalUserAuthor.get().getId());


        if (identifierMap.containsKey(identifier)) {
            int q = identifierMap.get(identifier);
            Optional<Users> optionalUser = usersRepository.findById(q);
            if (optionalUser.get().getIsModerator() == USER) {
                if (optionalPost.get().getUser().getId() != optionalUser.get().getId()) {
                    optionalPost.get().setViewCount(optionalPost.get().getViewCount() + 1);
                    postsRepository.save(optionalPost.get());
                }
            }
        } else if (!identifierMap.containsKey(identifier)) {
            optionalPost.get().setViewCount(optionalPost.get().getViewCount() + 1);
            postsRepository.save(optionalPost.get());
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

        List likeList = new ArrayList();
        List dislikeList = new ArrayList();
        Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
        for (PostVotes pv : postVotesIterable) {
            if (optionalPost.get().getId() == pv.getPostId()) {
                if (pv.getValue() == LIKE) {
                    likeList.add(pv.getId());
                } else if (pv.getValue() == DISLIKE) {
                    dislikeList.add(pv.getId());
                }
            }
        }
        singlePostResponse.setLikeCount(likeList.size());
        singlePostResponse.setDislikeCount(dislikeList.size());
        singlePostResponse.setViewCount(optionalPost.get().getViewCount());


        List<SingleCommentInfoResponse> sCommentList = new ArrayList<>();
        Iterable<PostComments> postCommentsIterable = postCommentsRepository.findAll();
        for (PostComments f : postCommentsIterable) {
            if (optionalPost.get().getId() == f.getPostId()) {
                Optional<Users> optionalUsersComment = usersRepository.findById(f.getUser().getId());
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
