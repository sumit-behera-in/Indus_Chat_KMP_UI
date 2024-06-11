package data.remote

import data.remote.dto.MessageDTO
import data.remote.dto.UsersDTO
import domain.model.Message
import domain.model.Users
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class MessageServiceImpl(
    private val client: HttpClient,
) : MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            val response: HttpResponse = client.get(MessageService.Endpoints.GetAllMessages.url)
            val responseBody: String = response.bodyAsText()
            Json.decodeFromString(ListSerializer(MessageDTO.serializer()), responseBody)
                .map { it.toMessage() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getActiveUsers(): List<Users> {
        return try {
            val response: HttpResponse = client.get(MessageService.Endpoints.GetActiveUsers.url)
            val responseBody: String = response.bodyAsText()
            Json.decodeFromString(ListSerializer(UsersDTO.serializer()), responseBody)
                .map { it.toUsers() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}