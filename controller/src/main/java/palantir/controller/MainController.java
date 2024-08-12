package palantir.controller;

import io.micrometer.core.annotation.Timed;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.validation.Validated;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import palantir.controller.entity.WatchedChannel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller()
@Validated
@Slf4j
public class MainController {
    private final ChannelsWatcher channelsWatcher;
    private final YouTubeApiService youTubeApiService;
    private final ChannelsWatcher channelsWatcherService;

    public MainController(ChannelsWatcher channelsWatcher, YouTubeApiService youTubeApiService, ChannelsWatcher channelsWatcherService) {
        this.channelsWatcher = channelsWatcher;
        this.youTubeApiService = youTubeApiService;
        this.channelsWatcherService = channelsWatcherService;
    }

    /**
     * Adds a channel to the watchlist by its channel ID.
     * It is the last part of the link 'https://www.youtube.com/channel/{channel_id}'
     *
     * @param channelId The ID of the channel to add.
     * @return HTTP "OK" (200) if the channel was added or HTTP "Not Modified" (304) if it was already being watched.
     */
    @Get("/watch/channel/by_id/{channelId}")
    @Timed("watchChannel.time")
    public HttpResponse<String> addChannelToWatchById(@PathVariable("channelId") String channelId) {
        log.info("Adding channel to watch: channelId={}", channelId);
        if (channelsWatcher.addChannelToWatchById(channelId)) {
            log.debug("Channel added to watch: channelId={}", channelId);
            return HttpResponse.ok(channelId);
        } else {
            log.debug("Channel is already being watched: channelId={}", channelId);
            return HttpResponse.notModified();
        }
    }

    /**
     * Adds a channel to the watchlist by its username.
     * It is the last part of the link 'https://www.youtube.com/c/{username}'
     *
     * @param username The username of the channel to add.
     * @return HTTP "OK" (200) if the channel was added or HTTP "Not Modified" (304) if it was already being watched.
     */
    @Get("/watch/channel/by_username/{username}")
    @Timed("watchChannel.time")
    public HttpResponse<String> addChannelToWatchByUsername(@PathVariable("username") String username) {
        log.info("Adding channel to watch: username={}", username);
        if (channelsWatcher.addChannelToWatchByUsername(username)) {
            log.debug("Channel added to watch: username={}", username);
            return HttpResponse.ok(username);
        } else {
            log.debug("Channel is already being watched: username={}", username);
            return HttpResponse.notModified();
        }
    }

    /**
     * Adds a channel to the watchlist by its custom URL.
     *
     * @param customUrl The custom URL of the channel to add.
     * @return HTTP "OK" (200) if the channel was added or HTTP "Not Modified" (304) if it was already being watched.
     */
    @Get("/watch/channel/by_custom_url/{customUrl}")
    @Timed("watchChannel.time")
    public HttpResponse<String> addChannelToWatchByCustomUrl(@PathVariable("customUrl") String customUrl) {
        log.info("Adding channel to watch: customUrl={}", customUrl);
        if (channelsWatcher.addChannelToWatchByCustomUrl(customUrl)) {
            log.debug("Channel added to watch: customUrl={}", customUrl);
            return HttpResponse.ok(customUrl);
        } else {
            log.debug("Channel is already being watched: customUrl={}", customUrl);
            return HttpResponse.notModified();
        }
    }

    /**
     * Adds multiple channels to watch
     *
     * @param channelIds The channel IDs to add
     * @param usernames  The usernames to add
     * @param customUrls The custom URLs to add
     * @return A map of the channels actually added skipping the ones that were already being watched.
     */
    @Post(value = "/watch/channel", produces = MediaType.APPLICATION_JSON)
    @Timed("watchChannelMany.time")
    public Map<String, List<String>> addChannelsToWatch(@Nullable @Body("ids") List<String> channelIds,
                                                        @Nullable @Body("usernames") List<String> usernames,
                                                        @Nullable @Body("customUrls") List<String> customUrls) {
        log.info("Adding channels to watch: channelIds={}, usernames={}, customUrls={}", channelIds, usernames, customUrls);
        List<String> channelIdsAdded = List.of();
        if (channelIds != null && !channelIds.isEmpty()) {
            channelIdsAdded = channelIds.stream().filter(channelsWatcher::addChannelToWatchById).toList();
            log.debug("Channel ids added to watch: channelIdsAdded={}", channelIdsAdded);
        } else {
            log.debug("No channel ids provided");
        }

        List<String> usernamesAdded = List.of();
        if (usernames != null && !usernames.isEmpty()) {
            usernamesAdded = usernames.stream().filter(channelsWatcher::addChannelToWatchByUsername).toList();
            log.debug("Username added to watch: usernamesAdded={}", usernamesAdded);

        } else {
            log.debug("No usernames provided");
        }

        List<String> customUrlsAdded = List.of();
        if (customUrls != null && !customUrls.isEmpty()) {
            customUrlsAdded = customUrls.stream().filter(channelsWatcher::addChannelToWatchByCustomUrl).toList();
            log.debug("Custom urls added to watch: customUrlsAdded={}", customUrlsAdded);
        } else {
            log.debug("No custom urls provided");
        }
        return Map.of("channel_ids", channelIdsAdded,
                "usernames", usernamesAdded,
                "custom_urls", customUrlsAdded);
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/watch_channel_of_user/{user}")
    public HttpResponse<String> addChannelUserToWatch(@PathVariable @NotEmpty String user) {
        log.info("Adding channel for user {} to watch list", user);
        String channelId = youTubeApiService.getChannelIdByUsername(user).block();
        List<WatchedChannel> watchedChannels = channelsWatcherService.getAllWatchedChannels();
        Set<String> watchedChannelIds = watchedChannels.stream().map(WatchedChannel::channelId).collect(Collectors.toSet());
        if (watchedChannelIds.contains(channelId)) {
            log.info("Channel with ID {} for user {} is already being watched", channelId, user);
            return HttpResponse.notModified();
        }
        channelsWatcherService.addChannelToWatchById(channelId);
        return HttpResponse.ok();
    }
}
