package palantir.controller.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.Builder;

import java.util.Date;

@Builder(toBuilder=true)
@MappedEntity("watched_channels_queue")
public record WatchedChannel(
        @Id @GeneratedValue @Nullable Long id,
        @Nullable String channelId,
        @Nullable String customUrl,
        @Nullable String userName,
        @Nullable Boolean isWatched,
        @Nullable Boolean isInvalid,
        @Nullable Integer retryCount,
        @DateCreated @Nullable Date rowCreated,
        @DateCreated @Nullable Date rowUpdated
) {
}

