package gg.kuken.http.modules.account.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import gg.kuken.features.account.AccountService
import gg.kuken.http.modules.account.dto.AccountResponse
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

fun Route.listAccounts() {
    val accountService by inject<AccountService>()

    get<AccountRoutes.List> {
        val accounts = accountService.listAccounts()
        call.respond(accounts.map(::AccountResponse))
    }
}
