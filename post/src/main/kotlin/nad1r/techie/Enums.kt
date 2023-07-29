package nad1r.techie

enum class LikeType{
    TO_REPLY, TO_THREAD
}

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    THREAD_NOT_FOUND(200),
    REPLY_NOT_FOUND(300),
    GENERAL_API_EXCEPTION(202)
}