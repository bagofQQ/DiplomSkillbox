package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer> {

    String QUERY_VALUE_POST_PREMODERATION = "select global_settings.value from global_settings where code = 'PP'";

    @Query(value = QUERY_VALUE_POST_PREMODERATION,
            nativeQuery = true)
    String findValuePostPremoderation();

    String QUERY_VALUE_STATISTICS_IS_PUBLIC = "select global_settings.value from global_settings where code = 'SIP'";

    @Query(value = QUERY_VALUE_STATISTICS_IS_PUBLIC,
            nativeQuery = true)
    String findValueStatisticsIsPublic();

    String QUERY_VALUE_MULTIUSER_MODE = "select global_settings.value from global_settings where code = 'MM'";

    @Query(value = QUERY_VALUE_MULTIUSER_MODE,
            nativeQuery = true)
    String findValueMultiuserMode();

}
