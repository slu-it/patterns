package patterns.inbox.mongodb

import org.springframework.data.annotation.Id
import patterns.inbox.ErrorData
import patterns.inbox.InboxEntry
import patterns.inbox.Status
import java.time.Instant
import java.util.UUID

data class InboxEntryDocument(
    @Id override var id: UUID,
    override var payload: String?,
    override var status: Status,
    override var created: Instant,
    override var lastUpdated: Instant,
    override var error: ErrorData?,
) : InboxEntry
