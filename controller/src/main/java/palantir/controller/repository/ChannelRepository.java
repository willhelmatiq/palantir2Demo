package palantir.controller.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;
import palantir.controller.entity.Channel;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ChannelRepository extends CrudRepository<Channel, String> {
    // Find a channel by its ID
    Channel findByChannelId(@NotBlank String channelId);
}
