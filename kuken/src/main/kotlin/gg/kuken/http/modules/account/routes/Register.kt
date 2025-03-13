package gg.kuken.http.modules.account.routes

import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import gg.kuken.features.account.AccountService
import gg.kuken.http.modules.account.dto.RegisterRequest
import gg.kuken.http.modules.account.dto.RegisterResponse
import gg.kuken.http.util.receiveValidating
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

fun Route.register() {
    val accountService by inject<AccountService>()
    val validator by inject<Validator>()

    post<AccountRoutes.Register> {
        val payload = call.receiveValidating<RegisterRequest>(validator)
        val account = accountService.createAccount(
            username = payload.username,
            displayName = payload.displayName,
            email = payload.email,
            password = payload.password
        )

        call.respond(RegisterResponse(account))
    }
}
