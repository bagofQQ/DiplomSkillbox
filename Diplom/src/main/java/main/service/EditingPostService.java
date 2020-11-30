package main.service;

import main.api.request.addingpost.AddingPostRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.addingpost.ErrorsAddingPostResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EditingPostService {

    private static final String ERROR_TITLE = "Заголовок слишком короткий";
    private static final String ERROR_TEXT = "Текст публикации слишком короткий";

    private static final int MODERATOR = 1;
    private static final int USER = 0;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;

    public AddingPostResponse editPost(AddingPostRequest editPostRequest, Optional<User> optionalUser, int postId) {

        Optional<Post> optionalPost = postRepository.findById(postId);

        if (editPostRequest.getTitle().length() > 1) {
            String cleanText = Jsoup.clean(editPostRequest.getText(), Whitelist.none());
            if (cleanText.length() > 50) {
                if (optionalUser.get().getIsModerator() == USER) {
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    Date datePost = new Date(editPostRequest.getTimestamp() * 1000);
                    if (datePost.before(date)) {
                        return editPostRecording(editPostRequest, ModerationStatus.NEW, optionalPost, date);
                    } else {
                        return editPostRecording(editPostRequest, ModerationStatus.NEW, optionalPost, datePost);
                    }
                } else if (optionalUser.get().getIsModerator() == MODERATOR) {
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    Date datePost = new Date(editPostRequest.getTimestamp() * 1000);
                    if (datePost.before(date)) {
                        return editPostRecording(editPostRequest, optionalPost.get().getModerationStatus(), optionalPost, date);
                    } else {
                        return editPostRecording(editPostRequest, optionalPost.get().getModerationStatus(), optionalPost, datePost);
                    }
                }
            } else {
                return postErrors(ERROR_TEXT);
            }
        }
        return postErrors(ERROR_TITLE);
    }

    private AddingPostResponse editPostRecording(AddingPostRequest editPostRequest, ModerationStatus status, Optional<Post> optionalPost, Date date) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();

        optionalPost.get().setIsActive(editPostRequest.getActive());
        optionalPost.get().setModerationStatus(status);
        optionalPost.get().setTime(date);
        optionalPost.get().setTitle(editPostRequest.getTitle());

        optionalPost.get().setText(editPostRequest.getText());
        postRepository.save(optionalPost.get());

        Iterable<Tag> tagsIterable = tagRepository.findAll();

        for (String tagFromArray : editPostRequest.getTags()) {
            Tag tagBase = new Tag();
            TagToPost tagToPostBase = new TagToPost();
            HashMap<String, Integer> tMap = new HashMap<>();

            String tagLowerCase = tagFromArray.toLowerCase();

            for (Tag tagFromBase : tagsIterable) {
                if (tagFromBase.getName().equals(tagLowerCase)) {
                    tMap.put(tagLowerCase, tagFromBase.getId());
                }
            }
            if (!tMap.containsKey(tagLowerCase)) {
                tagBase.setName(tagLowerCase);
                tagRepository.save(tagBase);
                tagToPostBase.setTagId(tagBase.getId());
                tagToPostBase.setPostId(optionalPost.get().getId());
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
}
