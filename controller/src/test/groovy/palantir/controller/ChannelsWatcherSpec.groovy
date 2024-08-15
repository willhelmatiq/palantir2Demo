package palantir.controller

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolationException
import spock.lang.Specification

@MicronautTest(transactional = false)
class ChannelsWatcherSpec extends Specification {

    @Inject
    ChannelsWatcher channelsWatcher

    def "Should not accept empty or null channel IDs "() {
        when:
        channelsWatcher.addChannelToWatchById(null)
        then:
        thrown(ConstraintViolationException)

        and:
        when:
        channelsWatcher.addChannelToWatchById("")
        then:
        thrown(ConstraintViolationException)
    }

    def "Should not accept empty or null usernames"() {
        when:
        channelsWatcher.addChannelToWatchByUsername(null)
        then:
        thrown(ConstraintViolationException)

        and:
        when:
        channelsWatcher.addChannelToWatchByUsername("")
        then:
        thrown(ConstraintViolationException)
    }

    def "Should not accept empty or null custom URLs"() {
        when:
        channelsWatcher.addChannelToWatchByCustomUrl(null)
        then:
        thrown(ConstraintViolationException)

        and:
        when:
        channelsWatcher.addChannelToWatchByCustomUrl("")
        then:
        thrown(ConstraintViolationException)
    }
}

