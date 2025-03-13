package gg.kuken.http

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class HttpException(
    val code: Int,
    message: String?,
    val details: String?,
    val status: HttpStatusCode,
    cause: Throwable?
) : RuntimeException(message, cause)

@Serializable
data class HttpError(
    val code: Int,
    val message: String,
    val details: String?
) {

    companion object {

        val Conflict = error(0, "Conflict")
        val NotFound = error(0, "Not Found")
        val Generic: (String) -> HttpError = { message -> error(0, message) }
        val UnknownAccount = error(1001, "Unknown account")
        val UnknownUnit = error(1002, "Unknown unit")
        val UnknownInstance = error(1003, "Unknown instance")
        val UnknownFSBucket = error(1004, "Unknown file system bucket")
        val UnknownFSFile = error(1005, "Unknown file")
        val InstanceRuntimeNotAvailable = error(1006, "Instance runtime not available")
        val ResourceNotAccessible = error(1007, "Resource not accessible")
        val FileIsNotDirectory = error(1008, "File is not a directory")
        val RequestedResourceIsNotAFile = error(1009, "The requested resource is not a file")
        val UnavailableFileSystem = error(1010, "Unavailable file system")
        val UnknownBlueprint = error(1011, "Unknown blueprint")
        val BlueprintParse: (String) -> HttpError =
            { error(1012, "Failed to parse blueprint file", it) }
        val InvalidAccessToken = error(2001, "Invalid or missing access token")
        val AccountInvalidCredentials = error(2002, "Invalid account credentials")
        val AccountLoginConflict = error(
            2003,
            "An account with that username or email already exists"
        )
        val InvalidInstanceUpdateCode = error(3001, "Invalid instance update code")
        val FailedToParseRequestBody = error(3002, "Failed to handle request")
        val InvalidRequestBody = error(3003, "Invalid request body")

        fun error(code: Int, message: String, details: String? = null) =
            HttpError(code, message, details)
    }
}