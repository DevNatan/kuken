package gg.kuken.http.modules.auth.dto

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @field:NotBlank(message = "Username must be provided")
    @SerialName("username")
    val username: String,

    @field:NotBlank(message = "Password must be provided")
    @SerialName("password")
    val password: String
)
