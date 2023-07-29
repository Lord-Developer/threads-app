package nad1r.techie

data class BaseMessage(val code: Int? = null, val message: String?)

data class FollowerDto(
    val followerId: Long,
    val followingId: Long
)