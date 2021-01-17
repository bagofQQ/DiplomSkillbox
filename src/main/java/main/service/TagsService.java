package main.service;

import main.api.response.tags.TagsInfoResponse;
import main.api.response.tags.TagsResponse;
import main.model.PostRepository;
import main.model.Tag;
import main.model.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TagsService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Autowired
    public TagsService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public TagsResponse getAllTags() {
        TagsResponse tagsResponse = new TagsResponse();
        HashSet<TagsInfoResponse> tagsInfoResponseSet = new HashSet<>();
        int activePostsSize = postRepository.countActivePosts();
        Iterable<Tag> tagsIterable = tagRepository.findAll();
        for (Tag tag : tagsIterable) {
            TagsInfoResponse tagsInfoResponse = new TagsInfoResponse();
            int countTagPosts = postRepository.countTagPosts(tag.getId());
            double weight = (double) countTagPosts / activePostsSize * 2;
            if (countTagPosts > 0) {
                tagsInfoResponse.setName(tag.getName());
                tagsInfoResponse.setWeight(weight * 2);
                tagsInfoResponseSet.add(tagsInfoResponse);
            }
        }
        tagsResponse.setTags(tagsInfoResponseSet);
        return tagsResponse;
    }
}
