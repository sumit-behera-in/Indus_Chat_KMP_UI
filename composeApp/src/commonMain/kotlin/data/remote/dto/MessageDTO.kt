package data.remote.dto

import domain.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat

@Serializable
data class MessageDTO(
    val _id: String,
    val text: String,
    val user: String,
    val timeStamp: Long,
) {
    fun toMessage(): Message {
        return Message(
            text = text,
            user = user,
            timeStamp = DateFormat.getDateTimeInstance()
                .format(timeStamp)
        )
    }
}
