package data.remote

import domain.model.Message
import domain.model.Users

interface MessageService {
    suspend fun getAllMessages(): List<Message>

    suspend fun getActiveUsers(): List<Users>

    companion object {
        const val HTTP_BASE_URL = "https://indus-chat-kmp-server.onrender.com"
        //const val HTTP_BASE_URL = "http://192.168.56.1:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMessages : Endpoints("$HTTP_BASE_URL/messages")
        data object GetActiveUsers : Endpoints("$HTTP_BASE_URL/users")
    }
}