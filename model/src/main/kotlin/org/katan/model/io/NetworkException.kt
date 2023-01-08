package org.katan.model.io

import org.katan.model.KatanException

open class NetworkException(message: String?, cause: Throwable? = null) : KatanException(message, cause)

class InvalidNetworkAssignmentException(message: String) : NetworkException(message)

class UnknownNetworkException(val network: String) : NetworkException(network)

class NetworkConnectionFailed(val network: String, cause: Throwable? = null) :
    NetworkException(network, cause)
