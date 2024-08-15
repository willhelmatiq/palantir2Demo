package palantir.controller.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;
import palantir.controller.entity.Video;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VideoRepository extends CrudRepository<Video, String> {
    // Find videos by their channel ID
    List<Video> findByChannelId(@NotBlank String channelId);
}
