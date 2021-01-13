package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    String QUERY_COMMENT = "select * from post_comment where post_id = :postId";

    @Query(value = QUERY_COMMENT,
            nativeQuery = true)
    List<PostComment> findComment(@Param("postId") int postId);

    String QUERY_COMMENT_USER = "select * from post_comment where user_id = :userId and time <= :date";

    @Query(value = QUERY_COMMENT_USER,
            nativeQuery = true)
    List<PostComment> findCommentUser(@Param("userId") int userId, @Param("date") Date date);
}
