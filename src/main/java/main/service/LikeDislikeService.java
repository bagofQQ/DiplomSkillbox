package main.service;

import main.PostsException;
import main.api.response.vote.VoteResponse;
import main.model.PostVotes;
import main.model.PostVotesRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class LikeDislikeService {

    private final UserRepository userRepository;
    private final PostVotesRepository postVotesRepository;

    @Autowired
    public LikeDislikeService(UserRepository userRepository, PostVotesRepository postVotesRepository) {
        this.userRepository = userRepository;
        this.postVotesRepository = postVotesRepository;
    }

    public VoteResponse postVote(int idUser, int postId, int voteAmount) {
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);

        List<PostVotes> postVotesList = postVotesRepository.findOnePostVotes(postId, user.getId());

        if (postVotesList.isEmpty()) {
            return getVoteResponse(postId, voteAmount, user);
        }

        if (voteAmount == postVotesList.get(0).getValue()) {
            return new VoteResponse(false);
        }

        return getVoteResponse(postId, voteAmount, user, changePostVote(voteAmount, postVotesList.get(0)));
    }

    private VoteResponse getVoteResponse(int postId, int voteAmount, User user) {
        return getVoteResponse(postId, voteAmount, user, getNewPostVote(postId, user, voteAmount));
    }

    private VoteResponse getVoteResponse(int postId, int voteAmount, User user, PostVotes postVote) {
        VoteResponse voteResponse = new VoteResponse(true);
        postVotesRepository.save(postVote);
        return voteResponse;
    }

    private PostVotes getNewPostVote(int postId, User user, int vote) {
        PostVotes postVote = new PostVotes();
        postVote.setUser(user);
        postVote.setPostId(postId);
        postVote.setTime(Calendar.getInstance().getTime());
        postVote.setValue(vote);
        return postVote;
    }

    private PostVotes changePostVote(int vote, PostVotes postVote) {
        postVote.setValue(vote);
        postVote.setTime(Calendar.getInstance().getTime());
        return postVote;
    }
}
