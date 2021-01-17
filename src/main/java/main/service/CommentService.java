package main.service;

import main.PostsException;
import main.api.request.comment.CommentRequest;
import main.api.response.comment.CommentResponse;
import main.api.response.comment.ErrorsCommentResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class CommentService {

    @Value("${blog.constants.errorText2}")
    private String ERROR_TEXT;

    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(UserRepository userRepository, PostCommentRepository postCommentRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
    }


    public ResponseEntity<CommentResponse> postComment(CommentRequest commentRequest, int idUser) {
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);
        Optional<Post> optionalPosts = postRepository.findById(commentRequest.getPostId());
        Optional<PostComment> optionalPostComments = postCommentRepository.findById(commentRequest.getParentId());
        if (optionalPosts.isPresent() || optionalPostComments.isPresent()) {
            return new ResponseEntity(recordingOrSetErrors(commentRequest, user), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private CommentResponse recordingOrSetErrors(CommentRequest commentRequest, User user) {
        HashMap<String, String> errors = checkCommentErrors(commentRequest);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        return commentRecording(commentRequest, user);
    }

    private HashMap<String, String> checkCommentErrors(CommentRequest commentRequest) {
        HashMap<String, String> errors = new HashMap<>();
        String cleanText = Jsoup.clean(commentRequest.getText(), Whitelist.none());
        if (cleanText.length() < 3) {
            errors.put("text", ERROR_TEXT);
        }
        return errors;
    }

    private CommentResponse setErrors(HashMap<String, String> errors) {
        CommentResponse commentResponse = new CommentResponse();
        ErrorsCommentResponse errorsCommentResponse = new ErrorsCommentResponse();
        errorsCommentResponse.setText(errors.get("text"));
        commentResponse.setResult(false);
        commentResponse.setErrors(errorsCommentResponse);
        return commentResponse;
    }

    private CommentResponse commentRecording(CommentRequest commentRequest, User user) {
        CommentResponse commentResponse = new CommentResponse();
        PostComment postComment = new PostComment();
        postComment.setParentId(commentRequest.getParentId());
        postComment.setPostId(commentRequest.getPostId());
        postComment.setUser(user);
        Date date = Calendar.getInstance().getTime();
        postComment.setTime(date);
        postComment.setText(commentRequest.getText());
        postCommentRepository.save(postComment);
        commentResponse.setId(postCommentRepository.idCommentUser(user.getId(), date));
        commentResponse.setResult(true);
        return commentResponse;
    }

}
