package palantir.controller.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
@MappedEntity("videos")
public record Channel(
        @Id
        String channelId,
        @Nullable
        ZonedDateTime lastIndexation,
        Integer videosCount,
        @DateCreated
        ZonedDateTime rowCreated,

        @DateUpdated
        @Nullable
        ZonedDateTime rowUpdated
) {
}
