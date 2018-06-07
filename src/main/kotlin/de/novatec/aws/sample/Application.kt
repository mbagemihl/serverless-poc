package de.novatec.aws.sample

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import java.time.LocalDateTime

class Application : RequestHandler<Input, List<Action>> {
    override fun handleRequest(input: Input, context: Context?): List<Action> {
        return handler(input)
    }

    fun handler(input: Input): List<Action> {

        val result = GooglePlusAccessor().get()

        val startDate: LocalDateTime
        val endDate: LocalDateTime
        when (input.quarter) {
            1 -> {
                startDate = LocalDateTime.of(input.year, 1, 1, 0, 0, 0)
                endDate = LocalDateTime.of(input.year, 4, 1, 0, 0, 0).minusDays(1)
            }
            2 -> {
                startDate = LocalDateTime.of(input.year, 4, 1, 0, 0, 0)
                endDate = LocalDateTime.of(input.year, 7, 1, 0, 0, 0).minusDays(1)
            }
            3 -> {
                startDate = LocalDateTime.of(input.year, 7, 1, 0, 0, 0)
                endDate = LocalDateTime.of(input.year, 10, 1, 0, 0, 0).minusDays(1)
            }
            4 -> {
                startDate = LocalDateTime.of(input.year, 10, 1, 0, 0, 0)
                endDate = LocalDateTime.of(input.year + 1, 1, 1, 0, 0, 0).minusDays(1)
            }
            else -> {
                throw IndexOutOfBoundsException("Quarter must be between 1 and 4!")
            }
        }
        var list:List<Action> = ArrayList()
//        var nextPageStr = ""
//        do {
//            var response = get("https://www.googleapis.com/plus/v1/people/+novatecgmbhdeutschland/activities/public?key=$key&maxResults=100$nextPageStr")
//            var result: Result = Mapper().deserialize(response.jsonObject)
//            if (result.nextPageToken != "") {
//                nextPageStr = "&pageToken=${result.nextPageToken}"
//            } else {
//                nextPageStr = ""
//            }

            list += result.items.filter {
                it.published.isAfter(startDate) && it.published.isBefore(endDate)
            }
//        } while (!result.nextPageToken.isNullOrBlank() && result.items.last().published.isAfter(startDate))
//
        return list
    }
}

data class Action(
        val id: String,
        val verb: String,
        val published: LocalDateTime
)

data class Result(
        val nextPageToken: String?,
        val items: List<Action>
)

data class Input(
        val year: Int = LocalDateTime.now().year,
        val quarter: Int = LocalDateTime.now().monthValue / 4
)
