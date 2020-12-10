package main.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
}
