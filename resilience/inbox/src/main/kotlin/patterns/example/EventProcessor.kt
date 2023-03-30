package patterns.example

import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import patterns.inbox.ProcessResult.Failure
import patterns.inbox.ProcessResult.Success
import patterns.inbox.process
import java.util.UUID

@Component
class EventProcessor(
    private val inbox: EventInbox
) {

    private val log = getLogger(javaClass)

    fun process(event: Event) {
        when (val result = inbox.process(event, ::doSomethingWithEvent)) {
            is Success -> log.info("Successfully processed event [${result.id}]")
            is Failure -> log.error("Failed to process event [${result.id}]: ${result.ex.message}", result.ex)
        }
    }

    fun reprocess(id: UUID, event: Event) {
        when (val result = inbox.process(id, event, ::doSomethingWithEvent)) {
            is Success -> log.info("Successfully re-processed event [${result.id}]")
            is Failure -> log.error("Failed to re-process event [${result.id}]: ${result.ex.message}", result.ex)
        }
    }

    private fun doSomethingWithEvent(event: Event) {
        println(event)
    }

}
