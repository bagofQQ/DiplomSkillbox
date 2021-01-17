package main.service;

import main.PostsException;
import main.api.response.moderation.ModerationResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class ModerationService {

    @Value("${blog.constants.accept}")
    private String ACCEPT;
    @Value("${blog.constants.decline}")
    private String DECLINE;

    private final PostRepository postRepository;

    @Autowired
    public ModerationService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ModerationResponse getMod(int postId, String decision, int idModer) {
        Post post = postRepository.findById(postId).orElseThrow(PostsException::new);
        if (decision.equals(ACCEPT)) {
            return getModerationResponse(post, idModer, ModerationStatus.ACCEPTED);
        }
        if (decision.equals(DECLINE)) {
            return getModerationResponse(post, idModer, ModerationStatus.DECLINED);
        }
        return new ModerationResponse(false);
    }

    private ModerationResponse getModerationResponse(Post post, int idModer, ModerationStatus status) {
        setPost(post, idModer, status);
        return new ModerationResponse(true);
    }

    private void setPost(Post post, int idModer, ModerationStatus status) {
        post.setModerationStatus(status);
        post.setModeratorId(idModer);
        post.setTime(Calendar.getInstance().getTime());
        postRepository.save(post);
    }
}
