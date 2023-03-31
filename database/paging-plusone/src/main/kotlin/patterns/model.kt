package patterns

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("books")
data class BookRecord(
    @Id val id: UUID,
    val data: BookData
)

data class BookData(
    val isbn: String,
    val title: String
)

data class Page<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
)
