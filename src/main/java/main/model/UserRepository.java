package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    String QUERY_CHECK_TAG = "select count(id) from user where email = :eml";

    @Query(value = QUERY_CHECK_TAG,
            nativeQuery = true)
    int countEmail(@Param("eml") String email);

    String QUERY_USER_CHECK_EMAIL = "select count(*) from user where email = :eml";

    @Query(value = QUERY_USER_CHECK_EMAIL,
            nativeQuery = true)
    int countFindUser(@Param("eml") String email);

    String QUERY_USER_CHECK_EMAIL_PASSWORD = "select * from user where email = :eml and password = :pass";

    @Query(value = QUERY_USER_CHECK_EMAIL_PASSWORD,
            nativeQuery = true)
    List<User> findUser(@Param("eml") String email, @Param("pass") String password);

    String QUERY_USER_CHECK_EMAIL_RESTORE = "select * from user where email = :eml";

    @Query(value = QUERY_USER_CHECK_EMAIL_RESTORE,
            nativeQuery = true)
    List<User> findUserRestore(@Param("eml") String email);

    String QUERY_USER_CHECK_CODE = "select * from user where code = :cd";

    @Query(value = QUERY_USER_CHECK_CODE,
            nativeQuery = true)
    List<User> findUserCode(@Param("cd") String code);
}
