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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import util.Resource
import kotlin.coroutines.cancellation.CancellationException

class ChatSocketServiceImpl(private val client: HttpClient) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish a connection.")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String): Resource<Unit> {
        return try {
            if (socket?.isActive != true) {
                val initResult = initSession("defaultUsername")
                if (initResult is Resource.Error) {
                    return initResult
                }
            }
            socket?.send(Frame.Text(message))
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            e.printStackTrace()
            Resource.Error("Message send cancelled")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override fun observeMessages(): Flow<Message> {
        return (socket?.incoming?.receiveAsFlow() ?: flow { })
            .filter { it is Frame.Text }
            .map {
                val json = (it as Frame.Text).readText()
                val messageDto = Json.decodeFromString<MessageDTO>(json)
                println("ChatSocketServiceImpl Received message: $messageDto")
                messageDto.toMessage()
            }
            .catch { e ->
                println("Error in flow: ${e.message}")
                e.printStackTrace()
            }
            .onCompletion { cause ->
                if (cause != null) {
                    println("Flow completed with exception: $cause")
                } else {
                    println("Flow completed successfully")
                }
            }
    }


    override suspend fun closeSession() {
        try {
            socket?.close()
            println("WebSocket session closed")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket = null
        }
    }
}
