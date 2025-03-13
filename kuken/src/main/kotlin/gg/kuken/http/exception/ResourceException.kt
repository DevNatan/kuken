package gg.kuken.http.exception

import gg.kuken.http.HttpError
import io.ktor.http.HttpStatusCode

open class ResourceException(val error: HttpError, val code: HttpStatusCode) : RuntimeException()