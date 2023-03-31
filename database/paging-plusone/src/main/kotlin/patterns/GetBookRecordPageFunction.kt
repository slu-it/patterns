package patterns

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

internal const val TITLE = "data.title"

@Component
class GetBookRecordPageFunction(
    private val mongoTemplate: MongoTemplate
) {

    private val defaultSort = Sort.by(ASC, TITLE)

    operator fun invoke(number: Int, size: Int, sort: Sort = defaultSort): Page<BookRecord> {
        val query = Query()
            .with(sort)
            .limit(size + 1) // one more than necessary to see if there is a next page
            .skip(offset(number, size))

        val content = mongoTemplate.find(query, BookRecord::class.java)

        return page(content, number, size)
    }

    private fun offset(number: Int, size: Int): Long = number.toLong() * size

    private fun page(content: List<BookRecord>, number: Int, size: Int) =
        Page(
            content = content.take(size),
            number = number,
            size = size,
            hasPrevious = number > 0,
            hasNext = content.size > size
        )

}
