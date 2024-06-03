package data.remote

import domain.model.Message
import kotlinx.coroutines.flow.Flow
import util.Resource

interface ChatSocketService {

    suspend fun initSession(username: String): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        const val HTTP_BASE_URL = "ws://192.168.56.1:8080"
    }

    sealed class Endpoints(val url: String) {
        data object ChatSocket : Endpoints("$HTTP_BASE_URL/chat-socket")
    }
}