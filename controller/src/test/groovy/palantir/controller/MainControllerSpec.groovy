package palantir.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import palantir.controller.repository.WatchedChannelRepository
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest(transactional = false)
class MainControllerSpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    @Inject
    WatchedChannelRepository repo

    @Inject
    @Client("/")
    HttpClient client

    void 'test it works'() {
        expect:
        application.running
    }

    @Unroll
    void "Watch channel by ID is added to the DB only once"() {
        given:
        def channelId = 'ABCD1234'
        def customUrl = 'CustomURL'
        def userName = '@UserName'

        when:
        client.toBlocking().retrieve("/watch/channel/by_id/$channelId")

        then:
        repo.existsByChannelId(channelId)

        and:
        client.toBlocking().retrieve("/watch/channel/by_custom_url/$customUrl")

        then:
        repo.existsByCustomUrl(customUrl)

        and:
        when:
        client.toBlocking().retrieve("/watch/channel/by_username/$userName")

        then:
        repo.existsByUserName(userName)

        cleanup:
        repo.deleteAll()
    }

    void "Accept multiple channels to watch removing duplicates"() {
        given:
        def channel1 = "XYZ1234"
        def channel2 = "EFGH5678"
        def channels = [channel1, channel2, channel1]

        when:
        client.toBlocking().exchange(HttpRequest.POST("/watch/channel", [ids: channels]))

        then:
        repo.findAll().size() == 2
        repo.existsByChannelId(channel1)
        repo.existsByChannelId(channel2)

        cleanup:
        repo.delete(repo.findByChannelId(channel1))
        repo.delete(repo.findByChannelId(channel2))
    }
}
