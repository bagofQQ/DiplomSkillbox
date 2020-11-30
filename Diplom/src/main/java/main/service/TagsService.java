package main.service;

import main.api.response.tags.TagsInfoResponse;
import main.api.response.tags.TagsResponse;
import main.model.Posts;
import main.model.PostsRepository;
import main.model.Tags;
import main.model.TagsRepository;
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
    private PostsRepository postsRepository;

    @Autowired
    private TagsRepository tagsRepository;

    public TagsResponse getAllTags() {
        TagsResponse tagsResponse = new TagsResponse();


        HashSet<TagsInfoResponse> tagsInfoResponseSet = new HashSet<>();
        List<Posts> postsList = new ArrayList<>();
        Iterable<Posts> postsIterable = postsRepository.findAll();

        if (postsIterable.iterator().hasNext()) {

            for (Posts f : postsIterable) {
                if (f.getIsActive() == ACTIVE_POST & f.getModerationStatus().toString().equals(MODERATION_ACCEPTED)) {
                    postsList.add(f);
                }
            }
            int postsSize = postsList.size();

            Iterable<Tags> tagsIterable = tagsRepository.findAll();
            for (Tags f : tagsIterable) {
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
        List<Posts> postsList = new ArrayList<>();
        Iterable<Posts> postsIterable = postsRepository.findAll();

        if (postsIterable.iterator().hasNext()) {
            for (Posts f : postsIterable) {
                postsList.add(f);
            }
            int postsSize = postsList.size();
            Iterable<Tags> tagsIterable = tagsRepository.findAll();
            for (Tags f : tagsIterable) {
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
