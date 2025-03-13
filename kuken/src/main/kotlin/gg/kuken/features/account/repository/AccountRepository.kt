@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.features.account.repository

import gg.kuken.features.account.entity.AccountEntity
import gg.kuken.features.account.model.Account
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal interface AccountRepository {

    suspend fun findAll(): List<AccountEntity>

    suspend fun findById(id: Uuid): AccountEntity?

    suspend fun findByUsername(username: String): AccountEntity?

    suspend fun findHashByUsername(username: String): String?

    suspend fun addAccount(account: Account, hash: String)

    suspend fun deleteAccount(id: Uuid)

    suspend fun existsByUsername(username: String): Boolean
}
