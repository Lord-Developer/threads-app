package nad1r.techie

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class BaseMessage(val code: Int, val message: String?)

data class UserCreateDto(

    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val username: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:ValidPassword(" Ensure password is at least 8 characters long and contains at least one uppercase letter and one digit")
    val password: String
) {
    fun toEntity() = User(name, email, username, password)
}

data class UserDto(
    val id: Long,
    val name: String,
    val username: String,
    val bio: String?
){
    companion object {
        fun from(user: User) = UserDto(
            user.id!!,
            user.name!!,
            user.username!!,
            user.bio
        )
    }
}