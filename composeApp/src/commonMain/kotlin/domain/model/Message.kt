package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val text: String,
    val user: String,
    val timeStamp: String,
)
