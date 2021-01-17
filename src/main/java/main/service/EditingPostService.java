package main.service;

import main.PostsException;
import main.api.request.addingpost.AddingPostRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.addingpost.ErrorsAddingPostResponse;
import main.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EditingPostService {

    @Value("${blog.constants.errorTitle2}")
    private String ERROR_TITLE;
    @Value("${blog.constants.errorText}")
    private String ERROR_TEXT;
    @Value("${blog.constants.user}")
    private int USER;

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagToPostRepository tagToPostRepository;
    private final UserRepository userRepository;

    public EditingPostService(PostRepository postRepository, TagRepository tagRepository, TagToPostRepository tagToPostRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.userRepository = userRepository;
    }


    public AddingPostResponse editPost(AddingPostRequest editPostRequest, int idUser, int postId) {
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostsException::new);

        HashMap<String, String> errors = checkAddErrors(editPostRequest);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        return postRecording(editPostRequest, user, post);
    }

    private AddingPostResponse postRecording(AddingPostRequest editPostRequest, User user, Post post) {
        AddingPostResponse addingPostResponse = new AddingPostResponse();
        post.setIsActive(editPostRequest.getActive());
        if (user.getIsModerator() == USER) {
            post.setModerationStatus(ModerationStatus.NEW);
        }
        post.setTime(checkDate(editPostRequest.getTimestamp()));
        post.setTitle(editPostRequest.getTitle());
        post.setText(editPostRequest.getText());
        postRepository.save(post);
        tagRecording(editPostRequest, post);
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


    private void tagRecording(AddingPostRequest editPostRequest, Post post) {
        List<String> oldTagsListString = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        List<String> newTagsListString = new ArrayList<>();
        for (String tagFromArray : editPostRequest.getTags()) {
            newTagsListString.add(tagFromArray);
            if (!oldTagsListString.contains(tagFromArray)) {
                String tagLowerCase = tagFromArray.toLowerCase();
                if (tagRepository.countCheck(tagLowerCase) == 0) {
                    Tag tagBase = new Tag();
                    tagBase.setName(tagLowerCase);
                    tagRepository.save(tagBase);
                    tagToPostRecording(tagBase.getId(), post);
                }
            }
        }
        tagСhange(post.getTags(), newTagsListString, post);
    }

    private void tagToPostRecording(int idTag, Post post) {
        TagToPost tagToPostBase = new TagToPost();
        if (tagToPostRepository.countCheckTagToPost(idTag, post.getId()) < 1) {
            tagToPostBase.setTagId(idTag);
            tagToPostBase.setPostId(post.getId());
            tagToPostRepository.save(tagToPostBase);
        }
    }

    private void tagСhange(List<Tag> oldTagsList, List<String> newTagsListString, Post post) {
        for (Tag tag : oldTagsList) {
            if (!newTagsListString.contains(tag.getName())) {
                if (tagToPostRepository.countConnection(tag.getId()) > 1) {
                    int idTagToPost = tagToPostRepository.idTagToPost(tag.getId(), post.getId());
                    tagToPostRepository.deleteById(idTagToPost);
                } else {
                    int idTagToPost = tagToPostRepository.idTagToPost(tag.getId(), post.getId());
                    tagToPostRepository.deleteById(idTagToPost);
                    tagRepository.deleteTag(tag.getId());
                }
            }
        }
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
}
