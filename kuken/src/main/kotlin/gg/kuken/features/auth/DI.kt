package gg.kuken.features.auth

import com.auth0.jwt.interfaces.JWTVerifier
import gg.kuken.features.auth.jwt.JWTAuthServiceImpl
import gg.kuken.features.auth.jwt.JWTVerifierImpl
import org.koin.dsl.module

val AuthDI = module {
    single<AuthService> { JWTAuthServiceImpl(get(), get()) }
    single<JWTVerifier> { JWTVerifierImpl() }
}