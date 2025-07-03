package gg.kuken.http.exception

import gg.kuken.http.HttpError
import io.ktor.http.HttpStatusCode

open class ResourceNotFoundException(error: HttpError) : ResourceException(error, HttpStatusCode.NotFound) {

    constructor() : this(HttpError.NotFound)
}