package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ImportBlueprintResponse(
    val blueprint: BlueprintResponse,
    val spec: BlueprintSpecResponse
)
