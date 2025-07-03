package gg.kuken.http.modules.auth

import com.auth0.jwt.interfaces.JWTVerifier
import gg.kuken.KukenConfig
import gg.kuken.features.auth.AuthService
import gg.kuken.http.HttpError
import gg.kuken.http.HttpModule
import gg.kuken.http.modules.account.AccountKey
import gg.kuken.http.modules.account.AccountPrincipal
import gg.kuken.http.modules.auth.exception.InvalidAccessTokenException
import gg.kuken.http.modules.auth.routes.login
import gg.kuken.http.modules.auth.routes.verify
import gg.kuken.http.util.respondError
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.routing.Routing
import io.ktor.server.routing.intercept
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import kotlin.getValue

object AuthHttpModule : HttpModule() {

    // Needed to Ktor's [Authentication] plugin to be installed before services try to hook on it
    override val priority: Int get() = 1

    override fun install(app: Application): Unit = with(app) {
        installAuthentication()
        routing {
            login()
            authenticate { verify() }
            addAccountAttributeIfNeeded()
        }
    }

    private fun Routing.addAccountAttributeIfNeeded() {
        intercept(ApplicationCallPipeline.Call) {
            val account = call.principal<AccountPrincipal>()?.account
                ?: return@intercept
            call.attributes.put(AccountKey, account)
        }
    }

    private fun Application.installAuthentication() {
        val appConfig by inject<KukenConfig>()
        val authService by inject<AuthService>()
        val jwtVerifier by inject<JWTVerifier>()

        install(Authentication) {
            jwt {
                verifier(jwtVerifier)

                challenge { _, _ ->
                    throw InvalidAccessTokenException()
                }

                validate { credentials ->
                    val account = runCatching { authService.verify(credentials.subject) }
                        .onFailure { exception ->
                            if (appConfig.devMode) exception.printStackTrace()
                        }.getOrNull() ?: respondError(HttpError.NotFound)

                    AccountPrincipal(account)
                }
            }
        }
    }
}