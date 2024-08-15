package palantir.controller.repository

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import palantir.controller.repository.WatchedChannelRepository
import spock.lang.Specification


@MicronautTest(transactional = false)
class WatchedChannelRepositorySpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    @Inject
    WatchedChannelRepository repo

    @Inject
    @Client("/")
    HttpClient client

    void 'Should be able to retrieve channels without channel id'() {
        given:
        def username = "ABCD"
        when:
        client.toBlocking().retrieve("/watch/channel/by_username/$username")
        then:
        repo.existsByUserName(username)
        repo.findByChannelIdIsNull().find { it.userName() == username }
    }
}
