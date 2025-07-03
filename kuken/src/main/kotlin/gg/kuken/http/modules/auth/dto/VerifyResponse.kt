@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.http.modules.auth.dto

import gg.kuken.features.account.model.Account
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class VerifyResponse(
    @SerialName("accountId") val id: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("createdAt") val createdAt: Instant,
    @SerialName("updatedAt") val updatedAt: Instant,
    @SerialName("lastLoggedInAt") val lastLoggedInAt: Instant?
) {

    constructor(account: Account) : this(
        id = account.id.toString(),
        username = account.username,
        email = account.email,
        createdAt = account.createdAt,
        updatedAt = account.createdAt,
        lastLoggedInAt = account.lastLoggedInAt
    )
}
