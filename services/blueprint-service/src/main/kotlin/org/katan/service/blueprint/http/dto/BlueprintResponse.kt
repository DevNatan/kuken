package org.katan.service.blueprint.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.Blueprint
import org.katan.service.blueprint.model.BlueprintSpecImpl

@Serializable
internal data class BlueprintResponse(
    val id: String,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("updated-at") val updatedAt: Instant,
    @SerialName("spec") val spec: BlueprintSpecImpl
) {

    constructor(blueprint: Blueprint) : this(
        id = blueprint.id.value.toString(),
        createdAt = blueprint.createdAt,
        updatedAt = blueprint.updatedAt,
        spec = blueprint.spec as BlueprintSpecImpl
    )
}
