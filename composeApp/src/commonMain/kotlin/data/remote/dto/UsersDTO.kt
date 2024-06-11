package data.remote.dto

import domain.model.Users
import kotlinx.serialization.Serializable

@Serializable
data class UsersDTO(
    val name: String,
    val sessionId: String,
) {
    fun toUsers(): Users {
        return Users(name)
    }
}