package gg.kuken.http.modules.auth.routes

import gg.kuken.http.modules.account.AccountPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import gg.kuken.http.modules.auth.dto.VerifyResponse
import io.ktor.server.response.respond

fun Route.verify() {
    get<AuthRoutes.Verify> {
        // TODO handle null AccountPrincipal
        val account = call.principal<AccountPrincipal>()!!.account

        call.respond(VerifyResponse(account))
    }
}
