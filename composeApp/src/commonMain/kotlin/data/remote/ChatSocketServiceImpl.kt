package data.remote

import data.remote.dto.MessageDTO
import domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import util.Resource

class ChatSocketServiceImpl(private val client: HttpClient) : ChatSocketService {

    private val socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {

            client.webSocketSession {
                url(ChatSocketService.Endpoints.ChatSocket.url)
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Couldn't establish a connection")
            }

        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")

        }
    }

    override suspend fun sendMessage(message: String): Resource<Unit> {
        return try {
            socket?.send(Frame.Text(message))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Message Send Failed")
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {

            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val message = Json.decodeFromString<MessageDTO>(json).toMessage()

                    message
                } ?: flow { }

        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}