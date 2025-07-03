package gg.kuken.http.modules.account.exception

import gg.kuken.http.HttpError
import gg.kuken.http.exception.ResourceNotFoundException

class AccountNotFoundException : ResourceNotFoundException(HttpError.UnknownAccount) {
}