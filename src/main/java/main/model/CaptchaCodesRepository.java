package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {

    String QUERY_CAPTCHA_COUNT = "select count(*) from captcha_codes where secret_code = :secretCode and code = :code";

    @Query(value = QUERY_CAPTCHA_COUNT,
            nativeQuery = true)
    int countCaptcha(@Param("secretCode") String secretCode, @Param("code") String code);
}
