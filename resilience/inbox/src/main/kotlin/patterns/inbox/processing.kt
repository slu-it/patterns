package patterns.inbox

import patterns.inbox.ProcessResult.Failure
import patterns.inbox.ProcessResult.Success
import java.util.UUID

fun <T> Inbox<T>.process(payload: T, block: (T) -> Unit): ProcessResult {
    val inboxId = add(payload)
    return process(inboxId, payload, block)
}

fun <T> Inbox<T>.process(inboxId: UUID, payload: T, block: (T) -> Unit): ProcessResult =
    try {
        setHandling(inboxId)
        block(payload)
        setDone(inboxId)
        Success(inboxId)
    } catch (e: Exception) {
        setFailed(inboxId, e)
        Failure(inboxId, e)
    }

sealed interface ProcessResult {
    data class Success(val id: UUID) : ProcessResult
    data class Failure(val id: UUID, val ex: Throwable) : ProcessResult
}
