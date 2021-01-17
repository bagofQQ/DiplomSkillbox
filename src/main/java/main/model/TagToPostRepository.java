package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {

    String QUERY_COUNT_CONNECTION = "select count(*) from tag2post where tag_id = :tag";

    @Query(value = QUERY_COUNT_CONNECTION,
            nativeQuery = true)
    int countConnection(@Param("tag") int tag);

    String QUERY_CHECK_TAG_TO_POST = "select count(*) from tag2post where tag_id = :tagId and post_id = :postId";

    @Query(value = QUERY_CHECK_TAG_TO_POST,
            nativeQuery = true)
    int countCheckTagToPost(@Param("tagId") int tagId, @Param("postId") int postId);

    String QUERY_ID_TAG_TO_POST = "select tag2post.id from tag2post where tag_id = :tagId and post_id = :postId";

    @Query(value = QUERY_ID_TAG_TO_POST,
            nativeQuery = true)
    int idTagToPost(@Param("tagId") int tagId, @Param("postId") int postId);

}
