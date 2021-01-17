package main.service;

import main.PostsException;
import main.api.request.addingpost.AddingPostRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.addingpost.ErrorsAddingPostResponse;
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

@Service
public class AddingPostService {

    @Value("${blog.constants.valueYes}")
    private String VALUE_YES;
    @Value("${blog.constants.errorTitle}")
    private String ERROR_TITLE;
    @Value("${blog.constants.errorText}")
    private String ERROR_TEXT;

    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagToPostRepository tagToPostRepository;
    private final UserRepository userRepository;

    @Autowired
    public AddingPostService(GlobalSettingsRepository globalSettingsRepository,
                             PostRepository postRepository,
                             TagRepository tagRepository,
                             TagToPostRepository tagToPostRepository,
                             UserRepository userRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<AddingPostResponse> addPost(AddingPostRequest addingPostRequest, int idUser) {
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);
        if (!globalSettingsRepository.findValuePostPremoderation().equals(VALUE_YES) & addingPostRequest.getActive() == 1) {
            return new ResponseEntity(recordingOrSetErrors(addingPostRequest, user, ModerationStatus.ACCEPTED), HttpStatus.OK);
        }
        return new ResponseEntity(recordingOrSetErrors(addingPostRequest, user, ModerationStatus.NEW), HttpStatus.OK);
    }

    private AddingPostResponse recordingOrSetErrors(AddingPostRequest addingPostRequest, User user, ModerationStatus status) {
        HashMap<String, String> errors = checkAddErrors(addingPostRequest);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        return postRecording(addingPostRequest, user, status);
    }

    private HashMap<String, String> checkAddErrors(AddingPostRequest addingPostRequest) {
        HashMap<String, String> errors = new HashMap<>();
        String cleanText = Jsoup.clean(addingPostRequest.getText(), Whitelist.none());
        if (addingPostRequest.getTitle().length() < 1) {
            errors.put("title", ERROR_TITLE);
        }
        if (cleanText.length() < 50) {
            errors.put("text", ERROR_TEXT);
        }
        return errors;
    }

    private AddingPostResponse setErrors(HashMap<String, String> errors) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();
        ErrorsAddingPostResponse errorsAddingPostResponse = new ErrorsAddingPostResponse();
        errorsAddingPostResponse.setTitle(errors.get("title"));
        errorsAddingPostResponse.setText(errors.get("text"));
        addingPostResponse.setResult(false);
        addingPostResponse.setErrors(errorsAddingPostResponse);
        return addingPostResponse;
    }

    private AddingPostResponse postRecording(
            AddingPostRequest addingPostRequest,
            User user, ModerationStatus status) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();
        Post post = new Post();
        post.setModerationStatus(status);
        post.setIsActive(addingPostRequest.getActive());
        post.setModeratorId(0);
        post.setUser(user);
        post.setTime(checkDate(addingPostRequest.getTimestamp()));
        post.setTitle(addingPostRequest.getTitle());
        post.setText(addingPostRequest.getText());
        post.setViewCount(0);
        postRepository.save(post);
        tagRecording(addingPostRequest, post);
        addingPostResponse.setResult(true);
        return addingPostResponse;
    }

    private Date checkDate(long timestamp) {
        Date date = Calendar.getInstance().getTime();
        Date datePost = new Date(timestamp * 1000);
        if (datePost.before(date)) {
            return date;
        }
        return datePost;
    }

    private void tagRecording(AddingPostRequest addingPostRequest, Post post) {
        for (String tagFromArray : addingPostRequest.getTags()) {
            Tag tagBase = new Tag();
            String tagLowerCase = tagFromArray.toLowerCase();
            if (tagRepository.countCheck(tagLowerCase) > 0) {
                int idTag = tagRepository.idTag(tagLowerCase);
                tagToPostRecording(idTag, post);
            } else {
                tagBase.setName(tagLowerCase);
                tagRepository.save(tagBase);
                tagToPostRecording(tagBase.getId(), post);
            }
        }
    }

    private void tagToPostRecording(int idTag, Post post) {
        TagToPost tagToPostBase = new TagToPost();
        if (tagToPostRepository.countCheckTagToPost(idTag, post.getId()) < 1) {
            tagToPostBase.setTagId(idTag);
            tagToPostBase.setPostId(post.getId());
            tagToPostRepository.save(tagToPostBase);
        }
    }
}
