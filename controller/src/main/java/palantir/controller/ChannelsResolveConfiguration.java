package palantir.controller;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties("channels-resolve")
public record ChannelsResolveConfiguration(@NotNull int maxRetries) {
}