package gg.kuken.http.util

import gg.kuken.http.HttpError.Companion.FailedToParseRequestBody
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.groupBy
import kotlin.collections.ifEmpty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

@Serializable
data class ValidationErrorResponse(
    val code: Int,
    val message: String,
    val details: Set<ValidationConstraintViolation>
)

@Serializable
data class ValidationConstraintViolation(
    val property: String,
    val info: List<String>
)

internal data class ValidationException(
    val data: ValidationErrorResponse
) : RuntimeException()

fun Validator.validateOrThrow(value: Any) {
    val violations = validate(value).ifEmpty { return }
    val mappedViolations = violations.groupBy { violation ->
        val propValue = violation.propertyPath.toString()
        val serialName =
            violation.rootBeanClass.kotlin.declaredMemberProperties.firstOrNull { property ->
                property.name.equals(propValue, ignoreCase = false)
            }?.findAnnotation<SerialName>()?.value

        serialName ?: propValue
    }.map { (path, violation) ->
        ValidationConstraintViolation(
            property = path,
            info = violation.map(ConstraintViolation<*>::getMessage)
        )
    }.toSet()

    throw ValidationException(
        ValidationErrorResponse(
            code = FailedToParseRequestBody.code,
            message = FailedToParseRequestBody.message,
            details = mappedViolations
        )
    )
}