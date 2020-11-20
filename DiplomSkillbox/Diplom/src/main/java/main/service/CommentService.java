package main.service;

import main.api.request.comment.CommentRequest;
import main.api.response.comment.CommentResponse;
import main.api.response.comment.ErrorsCommentResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class CommentService {

    private static final String ERROR_TEXT = "Текст комментария не задан или слишком короткий";

    @Autowired
    private PostCommentsRepository postCommentsRepository;

    @Autowired
    private PostsRepository postsRepository;

    public ResponseEntity<CommentResponse> postComment(CommentRequest commentRequest, Optional<Users> optionalUser) {

        Optional<Posts> optionalPosts = postsRepository.findById(commentRequest.getPostId());
        Optional<PostComments> optionalPostComments = postCommentsRepository.findById(commentRequest.getParentId());

        if (optionalPosts.isPresent() || optionalPostComments.isPresent()) {
            String cleanText = Jsoup.clean(commentRequest.getText(), Whitelist.none());
            if (cleanText.length() > 3) {
                return new ResponseEntity(commentRecording(commentRequest, optionalUser), HttpStatus.OK);
            }
            return new ResponseEntity(commentErrors(ERROR_TEXT), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private CommentResponse commentRecording(CommentRequest commentRequest, Optional<Users> optionalUser) {
        CommentResponse commentResponse = new CommentResponse();
        PostComments postComments = new PostComments();
        int idUser = optionalUser.get().getId();

        postComments.setParentId(commentRequest.getParentId());
        postComments.setPostId(commentRequest.getPostId());
        postComments.setUser(optionalUser.get());
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        postComments.setTime(date);
        postComments.setText(commentRequest.getText());
        postCommentsRepository.save(postComments);

        Iterable<PostComments> postCommentsIterable = postCommentsRepository.findAll();
        for (PostComments f : postCommentsIterable) {
            if (f.getUser().getId() == idUser & f.getTime().equals(date)) {
                commentResponse.setId(f.getId());
            }
        }
        commentResponse.setResult(true);
        return commentResponse;
    }

    private CommentResponse commentErrors(String error) {
        CommentResponse commentResponse = new CommentResponse();
        ErrorsCommentResponse errorsCommentResponse = new ErrorsCommentResponse();
        errorsCommentResponse.setText(error);
        commentResponse.setResult(false);
        commentResponse.setErrors(errorsCommentResponse);
        return commentResponse;
    }
}
