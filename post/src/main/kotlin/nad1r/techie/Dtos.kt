package nad1r.techie

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class BaseMessage(val code: Int? = null, val message: String?)


data class ThreadDto(

    val id: Long? = null,

    @field:NotBlank(message = "Text is required")
    @field:Size(min = 3, max = 100, message = "Content must be between 3 and 100 characters")
    val text: String,

    val authorId: Long
) {
    fun toEntity() = Thread(text, userId())
}


data class LikeDto(

    @field:NotNull(message = "ToLike ID cannot be null")
    val toLikeId: Long,

    val authorId: Long?,

    val likeType: LikeType
){
    fun toEntity() = Like(userId(),  toLikeId, likeType)
}

data class ReplyDto(

    val id: Long? = null,

    @field:Size(min = 1, max = 100, message = "Content must be between 1 and 100 characters")
    @field:NotBlank(message = "Text is required")
    val text: String,

    val authorId: Long?,

    @field:NotNull(message = "Thread ID cannot be null")
    val threadId: Long
){
    fun toEntity(thread: Thread) = Reply(text, userId(), thread)
}

data class UserDto(
    val id: Long?,
    val name: String,
    val username: String
)