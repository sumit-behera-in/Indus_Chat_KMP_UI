package presantation.chat

import domain.model.Message
import domain.model.Users

data class ChatState(
    val messages: List<Message> = emptyList(),
    var activeChatId: MutableList<Users> = mutableListOf(),
    val isLoading: Boolean = false,
)
