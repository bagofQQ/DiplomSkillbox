package main.service;

import main.api.response.moderation.ModerationResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostRepository;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class ModerationService {

    private static final String ACCEPT = "accept";
    private static final String DECLINE = "decline";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HttpSession httpSession;

    public ModerationResponse getMod(int postId, String decision, int idUser) {
        ModerationResponse moderationResponse = new ModerationResponse();
        Optional<Post> optionalPosts = postRepository.findById(postId);

        if (decision.equals(ACCEPT)) {
            optionalPosts.get().setModerationStatus(ModerationStatus.ACCEPTED);

            optionalPosts.get().setModeratorId(idUser);

            Calendar calendar = Calendar.getInstance();
            Date dateMod = calendar.getTime();
            optionalPosts.get().setTime(dateMod);

            postRepository.save(optionalPosts.get());
            moderationResponse.setResult(true);
            return moderationResponse;
        } else if (decision.equals(DECLINE)) {
            optionalPosts.get().setModerationStatus(ModerationStatus.DECLINED);

            optionalPosts.get().setModeratorId(idUser);

            Calendar calendar = Calendar.getInstance();
            Date dateMod = calendar.getTime();
            optionalPosts.get().setTime(dateMod);

            postRepository.save(optionalPosts.get());
            moderationResponse.setResult(true);
            return moderationResponse;
        }

        moderationResponse.setResult(false);
        return moderationResponse;
    }
}
