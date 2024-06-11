package data.remote

import domain.model.Message
import kotlinx.coroutines.flow.Flow
import util.Resource

interface ChatSocketService {

    suspend fun initSession(username: String): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    //suspend fun isActive(): Flow<Boolean>

    companion object {
        // const val HTTP_BASE_URL = "ws://192.168.56.1:8080"
        const val HTTP_BASE_URL = "ws://indus-chat-kmp-server.onrender.com"
    }

    sealed class Endpoints(val url: String) {
        data object ChatSocket : Endpoints("$HTTP_BASE_URL/chat-socket")
    }
}