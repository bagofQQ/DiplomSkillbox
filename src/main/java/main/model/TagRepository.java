package main.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TagRepository extends CrudRepository<Tag, Integer> {

    String QUERY_CHECK_TAG = "select count(id) from tag where name = :tag";

    @Query(value = QUERY_CHECK_TAG,
            nativeQuery = true)
    int countCheck(@Param("tag") String tag);

    String QUERY_TAG = "select id from tag where name = :tag";

    @Query(value = QUERY_TAG,
            nativeQuery = true)
    int idTag(@Param("tag") String tag);


    String QUERY_DELETE = "delete from tag where id = :tagId";

    @Modifying
    @Query(value = QUERY_DELETE,
            nativeQuery = true)
    void deleteTag(@Param("tagId") int tagId);
}
