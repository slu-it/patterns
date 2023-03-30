package patterns.inbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory.getLogger
import org.springframework.util.IdGenerator
import java.time.Clock
import java.time.Instant
import java.util.UUID

abstract class AbstractInbox<T> : Inbox<T> {

    private val log = getLogger(javaClass)

    protected abstract val payloadType: Class<T>
    protected abstract val finalizationMode: FinalizationMode
    protected abstract val objectMapper: ObjectMapper
    protected abstract val idGenerator: IdGenerator
    protected abstract val clock: Clock

    override fun add(payload: T): UUID {
        val id = generateId()
        insert(id = id, payload = serialize(payload), timestamp = now())
        return id
    }

    override fun setHandling(id: UUID) =
        tryTo {
            update(id) { status = Status.Handling; error = null }
        }

    override fun setDone(id: UUID) =
        tryTo {
            when (finalizationMode) {
                FinalizationMode.Keep -> update(id) { status = Status.Done; error = null; }
                FinalizationMode.Prune -> update(id) { status = Status.Done; payload = null; error = null }
                FinalizationMode.Delete -> delete(id)
            }
        }

    override fun setFailed(id: UUID, exception: Throwable?) =
        tryTo {
            update(id) { status = Status.Failed; error = exception?.toErrorData() }
        }

    private fun tryTo(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            log.error("failed to update inbox entry: ${e.message}", e)
        }
    }

    override fun getDeserializedPayload(id: UUID): T? =
        get(id)?.payload?.let(::deserialize)

    protected fun now(): Instant = clock.instant()
    private fun generateId(): UUID = idGenerator.generateId()
    private fun serialize(payload: T): String = objectMapper.writeValueAsString(payload)
    private fun deserialize(payload: String): T = objectMapper.readValue(payload, payloadType)

    protected abstract fun insert(id: UUID, payload: String, timestamp: Instant)
    protected abstract fun update(id: UUID, block: InboxEntry.() -> Unit)
    protected abstract fun delete(id: UUID)

}
