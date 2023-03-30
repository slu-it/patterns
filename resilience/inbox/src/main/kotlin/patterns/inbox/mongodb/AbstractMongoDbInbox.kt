package patterns.inbox.mongodb

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import patterns.inbox.AbstractInbox
import patterns.inbox.InboxEntry
import patterns.inbox.Status
import java.time.Instant
import java.util.UUID

abstract class AbstractMongoDbInbox<T> : AbstractInbox<T>() {

    protected abstract val mongoTemplate: MongoTemplate
    protected abstract val collectionName: String

    override fun insert(id: UUID, payload: String, timestamp: Instant) {
        val document = InboxEntryDocument(
            id = id,
            payload = payload,
            status = Status.Received,
            created = timestamp,
            lastUpdated = timestamp,
            error = null
        )
        mongoTemplate.insert(document, collectionName)
    }

    override fun update(id: UUID, block: InboxEntry.() -> Unit) {
        val document = mongoTemplate.findById<InboxEntryDocument>(id, collectionName)
        if (document != null) {
            val updatedDocument = document.apply(block).also { it.lastUpdated = now() }
            mongoTemplate.save(updatedDocument, collectionName)
        }
    }

    override fun delete(id: UUID) {
        mongoTemplate.remove(byId(id), collectionName)
    }

    override fun get(id: UUID): InboxEntry? =
        mongoTemplate.findById<InboxEntryDocument>(id, collectionName)

    override fun getByStatus(status: Status): List<InboxEntry> =
        mongoTemplate.find<InboxEntryDocument>(byStatus(status), collectionName)

    private fun byId(id: UUID): Query =
        Query.query(Criteria.where("_id").isEqualTo(id))

    private fun byStatus(status: Status): Query =
        Query.query(Criteria.where("status").isEqualTo(status))

}
