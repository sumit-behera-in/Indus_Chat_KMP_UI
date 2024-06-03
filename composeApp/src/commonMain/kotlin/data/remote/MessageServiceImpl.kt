package data.remote

import data.remote.dto.MessageDTO
import domain.model.Message
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
}