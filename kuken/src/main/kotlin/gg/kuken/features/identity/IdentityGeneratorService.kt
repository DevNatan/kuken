package gg.kuken.features.identity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IdentityGeneratorService {

    fun generate(): Uuid = Uuid.random()
}
