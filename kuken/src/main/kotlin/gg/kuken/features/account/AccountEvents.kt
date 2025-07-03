@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.features.account

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class AccountCreatedEvent(val accountId: Uuid)

@Serializable
data class AccountDeletedEvent(val accountId: Uuid)
