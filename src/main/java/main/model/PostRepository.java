package main.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    String QUERY_RESENT = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date group by id order by time desc";

    @Query(value = QUERY_RESENT,
            nativeQuery = true)
    Page<Post> findResentPosts(@Param("date") Date date, Pageable pageable);

    String QUERY_EARLY = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date group by id order by time asc";

    @Query(value = QUERY_EARLY,
            nativeQuery = true)
    Page<Post> findEarlyPosts(@Param("date") Date date, Pageable pageable);

    String QUERY_POPULAR = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date group by id order by (select count(*) from post_comment where post_id = post.id) desc";

    @Query(value = QUERY_POPULAR,
            nativeQuery = true)
    Page<Post> findPopularPosts(@Param("date") Date date, Pageable pageable);

    String QUERY_BEST = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date group by id order by (select sum(case when post_votes.value = 1 then 1 else 0 end) from post_votes where post_id = post.id) desc";

    @Query(value = QUERY_BEST,
            nativeQuery = true)
    Page<Post> findBestPosts(@Param("date") Date date, Pageable pageable);

    String QUERY_ACTIVE_POSTS = "select count(*) from post where post.is_active = 1 and post.moderation_status = 'ACCEPTED'";

    @Query(value = QUERY_ACTIVE_POSTS,
            nativeQuery = true)
    int countActivePosts();

    String QUERY_ACTIVE_POSTS_USER = "select count(*) from post where user_id = :userId and post.is_active = 1 and post.moderation_status = 'ACCEPTED'";

    @Query(value = QUERY_ACTIVE_POSTS_USER,
            nativeQuery = true)
    int countActivePostsUser(@Param("userId") int userId);


    String QUERY_VIEWCOUNT = "select sum(post.view_count) from post where is_active = 1 and moderation_status = 'ACCEPTED'";
    @Query(value = QUERY_VIEWCOUNT,
            nativeQuery = true)
    int viewCount();

    String QUERY_VIEWCOUNT_USER = "select sum(post.view_count) from post where user_id = :userId and is_active = 1 and moderation_status = 'ACCEPTED'";
    @Query(value = QUERY_VIEWCOUNT_USER,
            nativeQuery = true)
    int viewCountUser(@Param("userId") int userId);

    String QUERY_EARLY_DATE = "select post.time from post where is_active = 1 and moderation_status = 'ACCEPTED' order by time asc limit 1";
    @Query(value = QUERY_EARLY_DATE,
            nativeQuery = true)
    Date earlyDate();

    String QUERY_EARLY_DATE_USER = "select post.time from post where user_id = :userId and is_active = 1 and moderation_status = 'ACCEPTED' order by time asc limit 1";
    @Query(value = QUERY_EARLY_DATE_USER,
            nativeQuery = true)
    Date earlyDateUser(@Param("userId") int userId);

    String QUERY_TAG = "select id from tag where name = :tag";

    @Query(value = QUERY_TAG,
            nativeQuery = true)
    int idTag(@Param("tag") String tag);

    String QUERY_TAG_POSTS = "select * from post join tag2post on post_id = post.id where tag_id = :idTag and is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date order by time desc";

    @Query(value = QUERY_TAG_POSTS,
            nativeQuery = true)
    Page<Post> findTagPosts(@Param("date") Date date, @Param("idTag") int idTag, Pageable pageable);

    String QUERY_COUNT_TAG_POSTS = "select count(*) from post join tag2post on post_id = post.id where tag_id = :idTag and is_active = 1 and moderation_status = 'ACCEPTED'";

    @Query(value = QUERY_COUNT_TAG_POSTS,
            nativeQuery = true)
    int countTagPosts(@Param("idTag") int idTag);

    String QUERY_DATE = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date and date_format(post.time, '%Y-%m-%d') = (:date_requested) order by time desc";

    @Query(value = QUERY_DATE,
            nativeQuery = true)
    Page<Post> findDatePosts(@Param("date") Date date, @Param("date_requested") String dateRequested, Pageable pageable);

    String QUERY_SEARCH = "select * from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date and post.title like %:query% or post.text like %:query% order by time desc";

    @Query(value = QUERY_SEARCH,
            nativeQuery = true)
    Page<Post> findSearchPosts(@Param("date") Date date, @Param("query") String query, Pageable pageable);

    String QUERY_SEARCH_COUNT = "select count(*) from post where is_active = 1 and moderation_status = 'ACCEPTED' and time <= :date and post.title like %:query% or post.text like %:query% order by time desc";

    @Query(value = QUERY_SEARCH_COUNT,
            nativeQuery = true)
    int countSearchCount(@Param("date") Date date, @Param("query") String query);

    String QUERY_MY_POST = "select * from post where user_id = :idUser and is_active = :activeCount and moderation_status = :statusString and time <= :date group by id order by time desc";

    @Query(value = QUERY_MY_POST,
            nativeQuery = true)
    Page<Post> findMyPosts(@Param("date") Date date, @Param("idUser") int idUser, @Param("activeCount") int activeCount, @Param("statusString") String statusString, Pageable pageable);

    String QUERY_MODERATION_POST_NEW = "select * from post where is_active = 1 and moderation_status = :statusString and time <= :date group by id order by time desc";

    @Query(value = QUERY_MODERATION_POST_NEW,
            nativeQuery = true)
    Page<Post> findModerationPostsNew(@Param("date") Date date, @Param("statusString") String statusString, Pageable pageable);

    String QUERY_MODERATION_POST = "select * from post where moderator_id = :isModerator and is_active = 1 and moderation_status = :statusString and time <= :date group by id order by time desc";

    @Query(value = QUERY_MODERATION_POST,
            nativeQuery = true)
    Page<Post> findModerationPosts(@Param("date") Date date, @Param("isModerator") int isModerator, @Param("statusString") String statusString, Pageable pageable);

    String QUERY_CALENDAR = "select * from post where date_format(post.time, '%Y') = (:year)";

    @Query(value = QUERY_CALENDAR,
            nativeQuery = true)
    List<Post> findCalendarYear(@Param("year") int year);

    String QUERY_COUNT_DATE = "select count(*) from post where post.is_active = 1 and post.moderation_status = 'ACCEPTED' and date_format(post.time, '%Y-%m-%d') = (:date_requested)";

    @Query(value = QUERY_COUNT_DATE,
            nativeQuery = true)
    int countDate(@Param("date_requested") String dateRequested);

    String QUERY_COUNT_MOD_STATUS = "select count(*) from post where is_active = 1 and moderation_status = :status";

    @Query(value = QUERY_COUNT_MOD_STATUS,
            nativeQuery = true)
    int countModStatus(@Param("status") String status);

    String QUERY_COUNT_MOD_STATUS_USER = "select count(*) from post where is_active = :active and moderation_status = :status and user_id = :userId";

    @Query(value = QUERY_COUNT_MOD_STATUS_USER,
            nativeQuery = true)
    int countModStatusUser(@Param("status") String status, @Param("active") int active, @Param("userId") int userId);
}
