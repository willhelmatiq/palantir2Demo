package palantir.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.server.exceptions.InternalServerException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
public class YouTubeApiService {
    private final HttpClient httpClient;
    private final FunctionClientProxy functionClientProxy;

    @Inject
    public YouTubeApiService(@Client("${functions.url}") HttpClient httpClient, FunctionClientProxy functionClientProxy) {
        this.httpClient = httpClient;
        this.functionClientProxy = functionClientProxy;
    }

    public Mono<String> getChannelIdByUsername(String username) {
        log.info("Resolving channel ID for username {}", username);
        return Mono.from(functionClientProxy.getChannelIdForUser(username))
                .flatMap(response -> {
                    switch (response.getStatus()) {
                        case OK:
                            String channelId = response.body();
                            log.info("Found channel ID {} for username {}", channelId, username);
                            return Mono.just(channelId);
                        case NOT_FOUND:
                            log.warn("Not found channel ID for username {}", username);
                            return Mono.empty();
                        default:
                            log.error("Unexpected response from upstream function: status = {}, body = {}",
                                    response.getStatus(), response.getBody().orElse("No body"));
                            return Mono.error(new InternalServerException("Unexpected response from upstream function"));
                    }
                });
    }
}
