package palantir.controller

import io.micronaut.context.annotation.Property
import io.micronaut.http.client.HttpClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import palantir.controller.entity.WatchedChannel
import palantir.controller.repository.WatchedChannelRepository
import reactor.core.publisher.Mono
import spock.lang.Specification

@MicronautTest(transactional = false)
@Property(name = "channels-resolve.maxRetries", value = "2")
class ChannelResolveServiceSpec extends Specification {
    @Inject
    WatchedChannelRepository repository

    @Inject
    YouTubeApiService apiService

    @Inject
    ChannelsResolveService service


    def "Tries to resolve channel if it's ID is not known and number of retries is not exceeded"() {
        given:
        def channelId = "1234567890"
        def username_new = "test"
        def username_invalid = "test retries"
        when:
        service.resolveIds()

        then:
        1 * repository.findByChannelIdIsNull() >> [WatchedChannel.builder().userName(username_new).build(),
                                                   WatchedChannel.builder().userName(username_invalid).retryCount(2).build()]
        then:
        1 * apiService.getChannelIdByUsername(username_new) >> Mono.just(channelId)
        then:
        1 * repository.update(WatchedChannel.builder().userName(username_new).channelId(channelId).build())
        then:
        0 * repository.update(WatchedChannel.builder().userName(username_invalid).retryCount(2).channelId(channelId).build())
    }

    def "If failed to resolve channel, retries count is incremented"() {
        given:
        def username_new = "test retries"
        when:
        service.resolveIds()

        then:
        1 * repository.findByChannelIdIsNull() >> [WatchedChannel.builder().userName(username_new).build()]
        then:
        1 * apiService.getChannelIdByUsername(username_new) >> Mono.empty()
        then:
        1 * repository.update(WatchedChannel.builder().userName(username_new).retryCount(1).build())
    }

    @MockBean(WatchedChannelRepository)
    WatchedChannelRepository repositoryMock() {
        Mock(WatchedChannelRepository)
    }

    @MockBean(HttpClient)
    HttpClient httpClientMock() {
        Mock(HttpClient)
    }

    @MockBean(FunctionClientProxy)
    FunctionClientProxy functionClientProxyMock() {
        Mock(FunctionClientProxy)
    }

    @MockBean(YouTubeApiService)
    YouTubeApiService apiMock() {
        return Mock(YouTubeApiService) {
            getChannelIdByUsername(_ as String) >> { String username ->
                if (username == "test") {
                    return Mono.just("1234567890")
                } else {
                    return Mono.empty()
                }
            }
        }
    }
}
