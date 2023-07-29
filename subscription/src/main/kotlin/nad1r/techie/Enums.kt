package nad1r.techie

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    UNFOLLOW_NOT_POSSIBLE(101),
    GENERAL_API_EXCEPTION(202),
    FOLLOWER_CANNOT_FOLLOW_SELF(203),
}