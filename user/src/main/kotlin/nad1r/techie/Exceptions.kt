package nad1r.techie

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class UserServiceException(message: String? = null) : RuntimeException(message) {
    abstract fun errorType(): ErrorCode

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage {
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                array,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class UserNotFoundException() : UserServiceException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class UserNameAlreadyExistsException(val userName: String) : UserServiceException() {
    override fun errorType() = ErrorCode.USER_NAME_ALREADY_EXISTS
}

class EmailAlreadyExistsException(val email: String) : UserServiceException() {
    override fun errorType() = ErrorCode.EMAIL_ALREADY_EXISTS
}

