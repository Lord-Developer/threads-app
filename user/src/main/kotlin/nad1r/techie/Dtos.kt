package nad1r.techie

import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseMessage(var code: Int? = null, var message: String? = null)


data class UserDto(
    val id: Long?,

    @field:Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
    var username: String,

    @field:Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
    var name: String,

    @field:NotNull(message = "Role is required")
    var roleId: Long,

    @field:ValidPassword(" Ensure password is at least 8 characters long and contains at least one uppercase letter and one digit")
    var password: String,
    var permissions: List<Long>,
    var organizationId: Long?,
    var pinfl: String?
) {
    fun toEntity(role: Role, permissions: MutableSet<Permission>?) = User(username, password, role, name, permissions)
}


data class UserGetDto(
    val id: Long?,
    val fullName: String,
    val phoneNumber: String
) {
    companion object {
        fun toDto(user: User) = user.run { UserGetDto(id, name, username) }
    }
}

data class UserAuthDto(
    var id: Long,
    var username: String,
    var name: String? = null,
    var password: String,
    var role: String,
    var active: Boolean,
    var permissions: List<String>?
) {
    companion object {
        fun toDto(user: User) = user.run {
            UserAuthDto(
                id!!,
                username,
                name,
                password,
                role.name.name,
                active,
                permissions?.map { it.name })
        }
    }
}