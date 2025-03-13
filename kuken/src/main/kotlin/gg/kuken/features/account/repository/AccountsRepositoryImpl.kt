@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.features.account.repository

import gg.kuken.features.account.entity.AccountEntity
import gg.kuken.features.account.entity.AccountTable
import gg.kuken.features.account.model.Account
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

internal class AccountsRepositoryImpl(private val database: Database) : AccountRepository {

    init {
        transaction(db = database) {
            SchemaUtils.create(AccountTable)
        }
    }

    override suspend fun findAll(): List<AccountEntity> = newSuspendedTransaction(db = database) {
        AccountEntity.all().notForUpdate().toList()
    }

    override suspend fun findById(id: Uuid): AccountEntity? = newSuspendedTransaction(db = database) {
        AccountEntity.findById(id.toJavaUuid())
    }

    override suspend fun findByUsername(username: String): AccountEntity? =
        newSuspendedTransaction(db = database) {
            AccountEntity.find {
                AccountTable.username eq username
            }.firstOrNull()
        }

    override suspend fun findHashByUsername(username: String): String? = newSuspendedTransaction(db = database) {
        AccountEntity.find {
            AccountTable.username eq username
        }.firstOrNull()?.hash
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addAccount(account: Account, hash: String) {
        newSuspendedTransaction(db = database) {
            AccountEntity.new(account.id.toJavaUuid()) {
                this.username = account.username
                this.email = account.email
                this.hash = hash
                this.createdAt = account.createdAt
                this.updatedAt = account.updatedAt
                this.lastLoggedInAt = account.lastLoggedInAt
            }
        }
    }

    override suspend fun deleteAccount(id: Uuid) {
        newSuspendedTransaction(db = database) {
            AccountEntity.findById(id.toJavaUuid())?.delete()
        }
    }

    override suspend fun existsByUsername(username: String): Boolean =
        newSuspendedTransaction(db = database) {
            !AccountEntity.find { AccountTable.username eq username }.empty()
        }
}
