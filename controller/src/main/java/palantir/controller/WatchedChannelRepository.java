package palantir.controller;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;
import palantir.controller.entity.WatchedChannel;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WatchedChannelRepository extends CrudRepository<WatchedChannel, Long> {
    WatchedChannel findByChannelId(@NotBlank String channelId);

    WatchedChannel findByUserName(@NotBlank String userName);

    WatchedChannel findByCustomUrl(@NotBlank String customUrl);

    List<WatchedChannel> findByChannelIdIsNull();

    boolean existsByChannelId(@NotBlank String channelId);

    boolean existsByUserName(@NotBlank String userName);

    boolean existsByCustomUrl(@NotBlank String customUrl);
}

