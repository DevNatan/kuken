package gg.kuken.features.auth

import com.auth0.jwt.interfaces.JWTVerifier
import gg.kuken.features.account.model.Account
import gg.kuken.features.auth.jwt.JWTVerifierImpl
import org.koin.dsl.module

interface AuthService {

    suspend fun auth(username: String, password: String): String

    suspend fun verify(subject: String?): Account?
}
