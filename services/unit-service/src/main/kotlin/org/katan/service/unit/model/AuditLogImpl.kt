package org.katan.service.unit.model

import kotlinx.datetime.Instant
import org.katan.model.Snowflake
import org.katan.model.account.Account
import org.katan.model.unit.auditlog.AuditLog
import org.katan.model.unit.auditlog.AuditLogChange
import org.katan.model.unit.auditlog.AuditLogEntry
import org.katan.model.unit.auditlog.AuditLogEvent

internal data class AuditLogImpl(
    override val entries: List<AuditLogEntry>,
    override val actors: List<Account>
) : AuditLog

internal data class AuditLogEntryImpl(
    override val id: Snowflake,
    override val targetId: Snowflake,
    override val actorId: Snowflake?,
    override val event: AuditLogEvent,
    override val reason: String?,
    override val changes: List<AuditLogChange>,
    override val additionalData: String?,
    override val createdAt: Instant
) : AuditLogEntry

internal data class AuditLogChangeImpl(
    override val key: String,
    override val oldValue: String?,
    override val newValue: String?
) : AuditLogChange
