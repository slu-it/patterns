package patterns.inbox

import java.time.Instant
import java.util.UUID

interface InboxEntry {
    val id: UUID
    var payload: String?
    var status: Status
    val created: Instant
    var lastUpdated: Instant
    var error: ErrorData?
}

data class ErrorData(
    val exceptionType: String?,
    val message: String?,
    val stackTrace: String
)

fun Throwable.toErrorData(): ErrorData =
    ErrorData(
        exceptionType = this::class.qualifiedName ?: "unknown",
        message = this.message,
        stackTrace = this.stackTraceToString()
    )

enum class Status { Received, Handling, Done, Failed }
enum class FinalizationMode { Keep, Prune, Delete }
