@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.features.account

import gg.kuken.core.EventDispatcher
import gg.kuken.core.security.Hash
import gg.kuken.features.account.entity.toDomain
import kotlinx.datetime.Clock
import gg.kuken.features.account.model.Account
import gg.kuken.features.account.repository.AccountRepository
import gg.kuken.features.identity.IdentityGeneratorService
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AccountService {

    suspend fun listAccounts(): List<Account>

    suspend fun getAccount(id: Uuid): Account?

    suspend fun getAccountByUsername(username: String): Account?

    suspend fun getAccountAndHash(username: String): Pair<Account, String>?

    suspend fun createAccount(username: String, displayName: String?, email: String, password: String): Account

    suspend fun deleteAccount(id: Uuid)
}

internal class AccountServiceImpl(
    private val identityGeneratorService: IdentityGeneratorService,
    private val accountsRepository: AccountRepository,
    private val hashAlgorithm: Hash,
    private val eventDispatcher: EventDispatcher
) : AccountService {

    override suspend fun listAccounts(): List<Account> {
        return accountsRepository.findAll().map { entity -> entity.toDomain() }
    }

    override suspend fun getAccount(id: Uuid): Account? {
        return accountsRepository.findById(id)?.toDomain()
    }

    override suspend fun getAccountByUsername(username: String): Account? {
        return accountsRepository.findByUsername(username)?.toDomain()
    }

    override suspend fun getAccountAndHash(username: String): Pair<Account, String>? {
        // TODO optimize it
        val account = accountsRepository.findByUsername(username)?.toDomain() ?: return null
        val hash = accountsRepository.findHashByUsername(username) ?: return null

        return account to hash
    }

    override suspend fun createAccount(
        username: String,
        displayName: String?,
        email: String,
        password: String
    ): Account {
        if (accountsRepository.existsByUsername(username)) {
            error("Conflict") // TODO Conflict exception
        }

        val now = Clock.System.now()
        val account = Account(
            id = identityGeneratorService.generate(),
            displayName = displayName,
            username = username,
            email = email,
            createdAt = now,
            updatedAt = now,
            lastLoggedInAt = null,
            avatar = null
        )

        val hash = hashAlgorithm.hash(password.toCharArray())
        accountsRepository.addAccount(account, hash)
        eventDispatcher.dispatch(AccountCreatedEvent(account.id))
        return account
    }

    override suspend fun deleteAccount(id: Uuid) {
        accountsRepository.deleteAccount(id)
        eventDispatcher.dispatch(AccountDeletedEvent(id))
    }
}
