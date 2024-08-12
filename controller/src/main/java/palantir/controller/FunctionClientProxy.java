package palantir.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${functions.url}")
public interface FunctionClientProxy {

    @Get("/get_channel_id_for_user/{user}")
    Mono<HttpResponse<String>> getChannelIdForUser(@PathVariable String user);
}