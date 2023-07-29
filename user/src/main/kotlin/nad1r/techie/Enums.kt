package nad1r.techie

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    USER_NAME_ALREADY_EXISTS(101),
    EMAIL_ALREADY_EXISTS(102),
}