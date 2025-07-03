@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.http.modules.account.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import gg.kuken.features.account.model.Account
import kotlinx.serialization.SerialName
import kotlin.uuid.ExperimentalUuidApi

@Serializable
internal data class RegisterResponse(
    @SerialName("accountId") val id: String,
    @SerialName("username") val username: String,
    @SerialName("displayName") val displayName: String?,
    @SerialName("email") val email: String,
    @SerialName("createdAt") val createdAt: Instant
) {

    constructor(account: Account) : this(
        id = account.id.toString(),
        username = account.username,
        displayName = account.displayName,
        email = account.email,
        createdAt = account.createdAt
    )
}