package palantir.controller;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import palantir.controller.entity.WatchedChannel;
import palantir.controller.WatchedChannelRepository;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Service to resolve channel IDs from usernames and custom URLs.
 */
@Slf4j
@Singleton
public class ChannelsResolveService {
    private final WatchedChannelRepository watchedChannelRepository;

    private final ChannelsResolveConfiguration channelsResolveConfiguration;

    private final YouTubeApiService youTubeApiService;

    public ChannelsResolveService(WatchedChannelRepository watchedChannelRepository, ChannelsResolveConfiguration channelsResolveConfiguration, YouTubeApiService youTubeApiService) {
        this.watchedChannelRepository = watchedChannelRepository;
        this.channelsResolveConfiguration = channelsResolveConfiguration;
        this.youTubeApiService = youTubeApiService;
    }

    public void resolveIds() {
        log.info("Starting to resolve channel IDs");
        List<WatchedChannel> channels = watchedChannelRepository.findByChannelIdIsNull();
        log.info("Found {} channels to resolve", channels.size());
        Flux.fromIterable(channels)
                .filter(c -> c.retryCount() == null || c.retryCount() < channelsResolveConfiguration.maxRetries())
                .flatMap(c -> {
                    log.info("Resolving channel ID for channel: {}", c);
                    return youTubeApiService.getChannelIdByUsername(c.userName())
                            .doOnError(e -> log.error("Error while resolving channel ID for channel {}", c, e))
                            .doOnSuccess(channelId -> {
                                log.info("Resolved channel ID for channel {}: {}", c, channelId);
                                if (channelId == null) {
                                    int newRetryCount = c.retryCount() == null ? 1 : c.retryCount() + 1;
                                    log.info("Channel ID for channel {} was not resolved, incrementing retry count from {} to {}, maxRetries: {}",
                                            c, c.retryCount(), newRetryCount,
                                            channelsResolveConfiguration.maxRetries());
                                    WatchedChannel newC = c.toBuilder().retryCount(newRetryCount).build();
                                    watchedChannelRepository.update(newC);
                                    log.debug("New retry count for channel {}: {}", c, newC.retryCount());
                                } else {
                                    log.info("Channel ID for channel {} was resolved, updating channel with ID", c);
                                    WatchedChannel newC = c.toBuilder().channelId(channelId).retryCount(null).build();
                                    watchedChannelRepository.update(newC);
                                }
                            });
                }).subscribe();
    }
}

