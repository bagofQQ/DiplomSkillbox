package main.service;

import main.api.response.dislike.DislikeResponse;
import main.api.response.like.LikeResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LikeDislikeService {

    private static final int LIKE = 1;
    private static final int DISLIKE = -1;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;


    public LikeResponse postLike(int idUser, int postId) {
        LikeResponse likeResponse = new LikeResponse();
        PostVotes postVotes = new PostVotes();
        Optional<User> optionalUser = userRepository.findById(idUser);

        List<PostVotes> postVotesList = postVotesRepository.findPostVotes(postId);
        if(postVotesList.size() > 0){
            for(PostVotes votes : postVotesList){
                if(idUser == votes.getUser().getId()){
                    if(LIKE == votes.getValue()){
                        likeResponse.setResult(false);
                        return likeResponse;
                    } else if(DISLIKE == votes.getValue()){
                        Optional<PostVotes> optionalPostVotes = postVotesRepository.findById(votes.getId());
                        Calendar calendar = Calendar.getInstance();
                        Date dateLike = calendar.getTime();
                        optionalPostVotes.get().setValue(LIKE);
                        optionalPostVotes.get().setTime(dateLike);
                        postVotesRepository.save(optionalPostVotes.get());
                        likeResponse.setResult(true);

                        return likeResponse;
                    }
                }
            }
        }

//        Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
//        if (postVotesIterable.iterator().hasNext()) {
//            for (PostVotes f : postVotesIterable) {
//                if (idUser == f.getUser().getId()) {
//                    if (postId == f.getPostId()) {
//                        if (LIKE == f.getValue()) {
//                            likeResponse.setResult(false);
//                            return likeResponse;
//                        } else if (DISLIKE == f.getValue()) {
//                            Optional<PostVotes> optionalPostVotes = postVotesRepository.findById(f.getId());
//                            Calendar calendar = Calendar.getInstance();
//                            Date dateLike = calendar.getTime();
//                            optionalPostVotes.get().setValue(LIKE);
//                            optionalPostVotes.get().setTime(dateLike);
//                            postVotesRepository.save(optionalPostVotes.get());
//                            likeResponse.setResult(true);
//
//                            return likeResponse;
//                        }
//                    }
//                }
//            }
//        }
        Calendar calendar = Calendar.getInstance();
        Date dateLike = calendar.getTime();
        postVotes.setUser(optionalUser.get());
        postVotes.setPostId(postId);
        postVotes.setTime(dateLike);
        postVotes.setValue(LIKE);
        postVotesRepository.save(postVotes);
        likeResponse.setResult(true);

        return likeResponse;
    }

    public DislikeResponse postDislike(int idUser, int postId) {
        DislikeResponse dislikeResponse = new DislikeResponse();
        PostVotes postVotes = new PostVotes();
        Optional<User> optionalUser = userRepository.findById(idUser);

        List<PostVotes> postVotesList = postVotesRepository.findPostVotes(postId);
        if(postVotesList.size() > 0){
            for(PostVotes votes : postVotesList){
                if(idUser == votes.getUser().getId()){
                    if (DISLIKE == votes.getValue()) {
                        dislikeResponse.setResult(false);
                        return dislikeResponse;
                    } else if (LIKE == votes.getValue()) {
                        Optional<PostVotes> optionalPostVotes = postVotesRepository.findById(votes.getId());
                        Calendar calendar = Calendar.getInstance();
                        Date dateDislike = calendar.getTime();
                        optionalPostVotes.get().setValue(DISLIKE);
                        optionalPostVotes.get().setTime(dateDislike);
                        postVotesRepository.save(optionalPostVotes.get());
                        dislikeResponse.setResult(true);

                        return dislikeResponse;
                    }
                }
            }
        }


//        Iterable<PostVotes> postVotesIterable = postVotesRepository.findAll();
//        if (postVotesIterable.iterator().hasNext()) {
//            for (PostVotes f : postVotesIterable) {
//                if (idUser == f.getUser().getId()) {
//                    if (postId == f.getPostId()) {
//                        if (DISLIKE == f.getValue()) {
//                            dislikeResponse.setResult(false);
//                            return dislikeResponse;
//                        } else if (LIKE == f.getValue()) {
//                            Optional<PostVotes> optionalPostVotes = postVotesRepository.findById(f.getId());
//                            Calendar calendar = Calendar.getInstance();
//                            Date dateDislike = calendar.getTime();
//                            optionalPostVotes.get().setValue(DISLIKE);
//                            optionalPostVotes.get().setTime(dateDislike);
//                            postVotesRepository.save(optionalPostVotes.get());
//                            dislikeResponse.setResult(true);
//
//                            return dislikeResponse;
//                        }
//                    }
//                }
//            }
//        }
        Calendar calendar = Calendar.getInstance();
        Date dateDislike = calendar.getTime();
        postVotes.setUser(optionalUser.get());
        postVotes.setPostId(postId);
        postVotes.setTime(dateDislike);
        postVotes.setValue(DISLIKE);
        postVotesRepository.save(postVotes);
        dislikeResponse.setResult(true);

        return dislikeResponse;
    }
}
