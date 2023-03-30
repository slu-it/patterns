package patterns.inbox

import java.util.UUID

interface Inbox<T> {

    fun add(payload: T): UUID

    fun setHandling(id: UUID)
    fun setDone(id: UUID)
    fun setFailed(id: UUID, exception: Throwable?)

    fun get(id: UUID): InboxEntry?
    fun getByStatus(status: Status): List<InboxEntry>

    fun getDeserializedPayload(id: UUID): T?

}
