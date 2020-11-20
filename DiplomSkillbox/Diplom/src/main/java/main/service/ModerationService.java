package main.service;

import main.api.response.moderation.ModerationResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ModerationService {

    private static final String ACCEPT = "accept";
    private static final String DECLINE = "decline";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private HttpSession httpSession;

    public ModerationResponse getMod(int postId, String decision, Optional<Users> optionalUser) {
        ModerationResponse moderationResponse = new ModerationResponse();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        int id = optionalUser.get().getId();

        if (decision.equals(ACCEPT)) {
            optionalPosts.get().setModerationStatus(ModerationStatus.ACCEPTED);

            optionalPosts.get().setModeratorId(id);

            Calendar calendar = Calendar.getInstance();
            Date dateMod = calendar.getTime();
            optionalPosts.get().setTime(dateMod);

            postsRepository.save(optionalPosts.get());
            moderationResponse.setResult(true);
            return moderationResponse;
        } else if (decision.equals(DECLINE)) {
            optionalPosts.get().setModerationStatus(ModerationStatus.DECLINED);

            optionalPosts.get().setModeratorId(id);

            Calendar calendar = Calendar.getInstance();
            Date dateMod = calendar.getTime();
            optionalPosts.get().setTime(dateMod);

            postsRepository.save(optionalPosts.get());
            moderationResponse.setResult(true);
            return moderationResponse;
        }

        moderationResponse.setResult(false);
        return moderationResponse;
    }
}
