package palantir.controller;

import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import palantir.controller.entity.WatchedChannel;
import palantir.controller.WatchedChannelRepository;

import java.util.List;

@Slf4j
@Singleton
public class ChannelsWatcher {
    private final WatchedChannelRepository watchedChannelRepository;

    public ChannelsWatcher(WatchedChannelRepository watchedChannelRepository) {
        this.watchedChannelRepository = watchedChannelRepository;
    }

    /**
     * Adds a channel to the watchlist by its channel ID.
     * It is the last part of the link 'https://www.youtube.com/channel/{channel_id}'
     *
     * @param channelId The ID of the channel to add.
     * @return True if the channel was added, false if it was already being watched.
     */
    public boolean addChannelToWatchById(@NotEmpty String channelId) {
        log.debug("Request to watch channel with id: {}", channelId);
        if (watchedChannelRepository.existsByChannelId(channelId)) {
            log.debug("Channel {} is already being watched", channelId);
            return false;
        } else {
            log.debug("Channel {} is not being watched, adding to watchlist", channelId);
            watchedChannelRepository.save(WatchedChannel.builder().channelId(channelId).build());
            return true;
        }
    }

    /**
     * Adds a channel to the watchlist by its username.
     * It is the last part of the link 'https://www.youtube.com/c/{username}'
     * @param username The username of the channel to add.
     * @return True if the channel was added, false if it was already being watched.
     */
    public boolean addChannelToWatchByUsername(@NotEmpty String username) {
        log.debug("Request to watch channel with username: {}", username);
        if (watchedChannelRepository.existsByUserName(username)) {
            log.debug("Channel {} is already being watched", username);
            return false;
        } else {
            log.debug("Channel {} is not being watched, adding to watchlist", username);
            watchedChannelRepository.save(WatchedChannel.builder().userName(username).build());
            return true;
        }
    }

    /**
     * Adds a channel to the watchlist by its custom URL.
     * @param customUrl The custom URL of the channel to add.
     * @return True if the channel was added, false if it was already being watched.
     */
    public boolean addChannelToWatchByCustomUrl(@NotEmpty String customUrl) {
        log.debug("Request to watch channel with custom url: {}", customUrl);
        if (watchedChannelRepository.existsByCustomUrl(customUrl)) {
            log.debug("Channel {} is already being watched", customUrl);
            return false;
        } else {
            log.debug("Channel {} is not being watched, adding to watchlist", customUrl);
            watchedChannelRepository.save(WatchedChannel.builder().customUrl(customUrl).build());
            return true;
        }
    }

    public List<WatchedChannel> getAllWatchedChannels(){
        return watchedChannelRepository.findAll();
    }
}
