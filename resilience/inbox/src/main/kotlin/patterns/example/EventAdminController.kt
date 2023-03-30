package patterns.example

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import patterns.inbox.ErrorData
import patterns.inbox.Status.Failed
import java.util.UUID

@RestController
@RequestMapping("/admin/events/inbox")
class EventAdminController(
    private val inbox: EventInbox,
    private val processor: EventProcessor,
) {

    @GetMapping("/failed")
    fun getFailed(): List<FailedInboxEntry> =
        inbox.getByStatus(Failed)
            .map { FailedInboxEntry(it.id, it.error) }

    @PostMapping("/failed/{id}")
    fun retryFailed(@PathVariable id: UUID) {
        val event = inbox.getDeserializedPayload(id)
        if (event != null) {
            processor.reprocess(id, event)
        }
    }

    data class FailedInboxEntry(val id: UUID, var error: ErrorData?)

}
