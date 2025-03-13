package gg.kuken.http.modules.auth.routes

import gg.kuken.features.auth.AuthService
import gg.kuken.http.HttpError
import gg.kuken.http.modules.auth.dto.LoginRequest
import gg.kuken.http.modules.auth.dto.LoginResponse
import gg.kuken.http.util.receiveValid
import gg.kuken.http.util.receiveValidating
import gg.kuken.http.util.respondError
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import javax.security.auth.login.AccountNotFoundException

fun Route.login() {
    val authService by inject<AuthService>()

    post<AuthRoutes.Login> {
        val req = call.receiveValid<LoginRequest>()
        val token = authService.auth(
            username = req.username,
            password = req.password,
        )

        call.respond(LoginResponse(token))
    }
}
