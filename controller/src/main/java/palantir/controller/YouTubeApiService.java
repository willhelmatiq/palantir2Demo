package palantir.controller;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
public class YouTubeApiService {
    public Mono<String> getChannelIdByUsername(String username) {
        return null;
    }
}
