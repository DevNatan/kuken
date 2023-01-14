package org.katan.service.blueprint.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.service.blueprint.http.routes.getBlueprint
import org.katan.service.blueprint.http.routes.importBlueprints
import org.katan.service.blueprint.http.routes.listBlueprints

internal class BlueprintHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.routing {
            getBlueprint()
            listBlueprints()
            importBlueprints()
        }
    }
}
