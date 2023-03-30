package patterns.example

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import org.springframework.util.IdGenerator
import patterns.inbox.FinalizationMode.Keep
import patterns.inbox.mongodb.AbstractMongoDbInbox
import java.time.Clock

@Component
class EventInbox(
    override val objectMapper: ObjectMapper,
    override val mongoTemplate: MongoTemplate,
    override val idGenerator: IdGenerator,
    override val clock: Clock,
) : AbstractMongoDbInbox<Event>() {
    override val payloadType = Event::class.java
    override val collectionName = "event-inbox"
    override val finalizationMode = Keep
}
