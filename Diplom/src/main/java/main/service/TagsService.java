package main.service;

import main.api.response.tags.TagsInfoResponse;
import main.api.response.tags.TagsResponse;
import main.model.Post;
import main.model.PostRepository;
import main.model.Tag;
import main.model.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class TagsService {

    private static final String MODERATION_ACCEPTED = "ACCEPTED";

    private static final int ACTIVE_POST = 1;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    public TagsResponse getAllTags() {
        TagsResponse tagsResponse = new TagsResponse();


        HashSet<TagsInfoResponse> tagsInfoResponseSet = new HashSet<>();
        List<Post> postList = new ArrayList<>();
        Iterable<Post> postsIterable = postRepository.findAll();

        if (postsIterable.iterator().hasNext()) {

            for (Post f : postsIterable) {
                if (f.getIsActive() == ACTIVE_POST & f.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                    postList.add(f);
                }
            }
            int postsSize = postList.size();

            Iterable<Tag> tagsIterable = tagRepository.findAll();
            for (Tag f : tagsIterable) {
                TagsInfoResponse tagsInfoResponse = new TagsInfoResponse();

                f.getPosts().forEach(posts -> {
                    if (posts.getIsActive() == ACTIVE_POST & posts.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                        int i = f.getPosts().size();
                        double weight = (double) i / postsSize * 2;
                        tagsInfoResponse.setName(f.getName());
                        tagsInfoResponse.setWeight(weight);
                    }
                });
                tagsInfoResponseSet.add(tagsInfoResponse);

            }
            tagsResponse.setTags(tagsInfoResponseSet);
            return tagsResponse;
        }
        tagsResponse.setTags(tagsInfoResponseSet);
        return tagsResponse;
    }

    public TagsResponse getTag(String tag) {
        TagsResponse tagsResponse = new TagsResponse();

        HashSet<TagsInfoResponse> tagsInfoResponseSet = new HashSet<>();
        List<Post> postList = new ArrayList<>();
        Iterable<Post> postsIterable = postRepository.findAll();

        if (postsIterable.iterator().hasNext()) {
            for (Post f : postsIterable) {
                postList.add(f);
            }
            int postsSize = postList.size();
            Iterable<Tag> tagsIterable = tagRepository.findAll();
            for (Tag f : tagsIterable) {
                if (tag.equals(f.getName())) {
                    int i = f.getPosts().size();
                    double weight = (double) i / postsSize * 2;
                    TagsInfoResponse tagsInfoResponse = new TagsInfoResponse();
                    tagsInfoResponse.setName(f.getName());
                    tagsInfoResponse.setWeight(weight);
                    tagsInfoResponseSet.add(tagsInfoResponse);
                    tagsResponse.setTags(tagsInfoResponseSet);
                    return tagsResponse;
                }
            }
        }
        tagsResponse.setTags(tagsInfoResponseSet);
        return tagsResponse;
    }
}
