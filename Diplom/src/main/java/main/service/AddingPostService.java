package main.service;

import main.api.request.addingpost.AddingPostRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.addingpost.ErrorsAddingPostResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AddingPostService {

    private static final String ERROR_TITLE = "Заголовок не установлен или слишком короткий";
    private static final String ERROR_TEXT = "Текст публикации слишком короткий";

    private static final String MM_CODE = "MM";
    private static final String PP_CODE = "PP";
    private static final String VALUE_YES = "YES";

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;

    public ResponseEntity<AddingPostResponse> add(AddingPostRequest addingPostRequest, Optional<User> optionalUser) {
        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        if (checkSettings(PP_CODE, globalSettingsIterable)) {
            if (checkSettings(MM_CODE, globalSettingsIterable)) {
                return new ResponseEntity(addPost(addingPostRequest, optionalUser), HttpStatus.OK);
            } else {
                if (optionalUser.get().getIsModerator() == 1) {
                    return new ResponseEntity(addPostModeratorOnly(addingPostRequest, optionalUser), HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
        } else {
            if (checkSettings(MM_CODE, globalSettingsIterable)) {
                return new ResponseEntity(addPostWithoutPremoderation(addingPostRequest, optionalUser), HttpStatus.OK);
            } else {
                if (optionalUser.get().getIsModerator() == 1) {
                    return new ResponseEntity(addPostModeratorOnly(addingPostRequest, optionalUser), HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
        }
    }

    private AddingPostResponse addPost(AddingPostRequest addingPostRequest, Optional<User> optionalUser) {

        if (addingPostRequest.getTitle().length() > 1) {
            String cleanText = Jsoup.clean(addingPostRequest.getText(), Whitelist.none());
            if (cleanText.length() > 50) {
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                Date datePost = new Date(addingPostRequest.getTimestamp() * 1000);
                if (datePost.before(date)) {
                    return postRecording(addingPostRequest, optionalUser, date, ModerationStatus.NEW, 0);
                } else {
                    return postRecording(addingPostRequest, optionalUser, datePost, ModerationStatus.NEW, 0);
                }
            } else {
                return postErrors(ERROR_TEXT);
            }
        }
        return postErrors(ERROR_TITLE);
    }

    private AddingPostResponse addPostModeratorOnly(AddingPostRequest addingPostRequest, Optional<User> optionalUser) {

        if (addingPostRequest.getTitle().length() > 1) {
            String cleanText = Jsoup.clean(addingPostRequest.getText(), Whitelist.none());
            if (cleanText.length() > 50) {
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                Date datePost = new Date(addingPostRequest.getTimestamp() * 1000);
                if (datePost.before(date)) {
                    return postRecording(addingPostRequest, optionalUser, date, ModerationStatus.ACCEPTED, optionalUser.get().getId());
                } else {
                    return postRecording(addingPostRequest, optionalUser, datePost, ModerationStatus.NEW, optionalUser.get().getId());
                }
            } else {
                return postErrors(ERROR_TEXT);
            }
        }
        return postErrors(ERROR_TITLE);
    }

    private AddingPostResponse addPostWithoutPremoderation(AddingPostRequest addingPostRequest, Optional<User> optionalUser) {
        if (addingPostRequest.getTitle().length() > 1) {
            String cleanText = Jsoup.clean(addingPostRequest.getText(), Whitelist.none());
            if (cleanText.length() > 50) {
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                Date datePost = new Date(addingPostRequest.getTimestamp() * 1000);
                if (datePost.before(date)) {
                    return postRecording(addingPostRequest, optionalUser, date, ModerationStatus.ACCEPTED, 0);
                } else {
                    return postRecording(addingPostRequest, optionalUser, datePost, ModerationStatus.ACCEPTED, 0);
                }
            } else {
                return postErrors(ERROR_TEXT);
            }
        }
        return postErrors(ERROR_TITLE);
    }

    private AddingPostResponse postRecording(
            AddingPostRequest addingPostRequest,
            Optional<User> optionalUser,
            Date date, ModerationStatus status, int moderatorId) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();
        Post post = new Post();

        post.setIsActive(addingPostRequest.getActive());
        post.setModerationStatus(status);
        post.setModeratorId(moderatorId);

        post.setUser(optionalUser.get());

        post.setTime(date);
        post.setTitle(addingPostRequest.getTitle());
        post.setText(addingPostRequest.getText());
        post.setViewCount(0);
        postRepository.save(post);

        Iterable<Tag> tagsIterable = tagRepository.findAll();

        for (String tagFromArray : addingPostRequest.getTags()) {
            Tag tagBase = new Tag();
            TagToPost tagToPostBase = new TagToPost();
            HashMap<String, Integer> tMap = new HashMap<>();

            String tagLowerCase = tagFromArray.toLowerCase();

            for (Tag tagFromBase : tagsIterable) {
                if (tagFromBase.getName().equals(tagLowerCase)) {
                    tMap.put(tagLowerCase, tagFromBase.getId());
                }
            }
            if (tMap.containsKey(tagLowerCase)) {
                tagToPostBase.setTagId(tMap.get(tagLowerCase));
                tagToPostBase.setPostId(post.getId());
                tagToPostRepository.save(tagToPostBase);
            } else {
                tagBase.setName(tagLowerCase);
                tagRepository.save(tagBase);
                tagToPostBase.setTagId(tagBase.getId());
                tagToPostBase.setPostId(post.getId());
                tagToPostRepository.save(tagToPostBase);
            }
        }
        addingPostResponse.setResult(true);
        return addingPostResponse;
    }

    private AddingPostResponse postErrors(String error) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();
        ErrorsAddingPostResponse errorsAddingPostResponse = new ErrorsAddingPostResponse();
        errorsAddingPostResponse.setTitle(error);
        addingPostResponse.setResult(false);
        addingPostResponse.setErrors(errorsAddingPostResponse);
        return addingPostResponse;
    }

    private boolean checkSettings(String code, Iterable<GlobalSettings> globalSettingsIterable) {
        for (GlobalSettings setting : globalSettingsIterable) {
            if (setting.getCode().equals(code)) {
                if (setting.getValue().equals(VALUE_YES)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
