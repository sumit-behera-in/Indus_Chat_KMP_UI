package data.remote

import domain.model.Message

interface MessageService {
    suspend fun getAllMessages(): List<Message>

    companion object {
        const val HTTP_BASE_URL = "http://192.168.56.1:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMessages : Endpoints("$HTTP_BASE_URL/messages")
    }
}