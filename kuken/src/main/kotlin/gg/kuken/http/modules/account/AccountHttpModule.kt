package gg.kuken.http.modules.account

import gg.kuken.http.HttpModule
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import gg.kuken.http.modules.account.routes.listAccounts
import gg.kuken.http.modules.account.routes.register

object AccountHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.routing {
            authenticate {
                listAccounts()
            }
            register()
        }
    }
}
