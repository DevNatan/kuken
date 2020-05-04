package me.devnatan.katan.core.sql

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import java.util.*

internal object AccountsTable : UUIDTable("katan_accounts") {

    val username    = varchar("username", 32)
    val password    = varchar("password", 255)
    val permissions = integer("permissions")

}

class AccountEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AccountEntity>(AccountsTable)

    var username    by AccountsTable.username
    var password    by AccountsTable.password
    var permissions by AccountsTable.permissions

}