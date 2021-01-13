package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer> {

    String QUERY_LIKE = "select * from post_votes where post_id = :postId and value = 1";

    @Query(value = QUERY_LIKE,
            nativeQuery = true)
    List<PostVotes> findLike(@Param("postId") int postId);

    String QUERY_DISLIKE = "select * from post_votes where post_id = :postId and value = -1";

    @Query(value = QUERY_DISLIKE,
            nativeQuery = true)
    List<PostVotes> findDislike(@Param("postId") int postId);

    String QUERY_VOTE = "select * from post_votes where post_id = :postId";

    @Query(value = QUERY_VOTE,
            nativeQuery = true)
    List<PostVotes> findPostVotes(@Param("postId") int postId);

    String QUERY_COUNT_LIKE = "select count(*) from post_votes where value = 1";

    @Query(value = QUERY_COUNT_LIKE,
            nativeQuery = true)
    int countLike();

    String QUERY_COUNT_DISLIKE = "select count(*) from post_votes where value = -1";

    @Query(value = QUERY_COUNT_DISLIKE,
            nativeQuery = true)
    int countDislike();

    String QUERY_COUNT_LIKE_USER = "select count(*) from post join post_votes on post_id = post.id where post.user_id = :userId and is_active = 1 and moderation_status = 'ACCEPTED' and value = 1";

    @Query(value = QUERY_COUNT_LIKE_USER,
            nativeQuery = true)
    int countLikeUser(@Param("userId") int userId);

    String QUERY_COUNT_DISLIKE_USER = "select count(*) from post join post_votes on post_id = post.id where post.user_id = :userId and is_active = 1 and moderation_status = 'ACCEPTED' and value = -1";

    @Query(value = QUERY_COUNT_DISLIKE_USER,
            nativeQuery = true)
    int countDislikeUser(@Param("userId") int userId);
}
