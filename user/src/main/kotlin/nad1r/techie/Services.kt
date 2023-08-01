package nad1r.techie

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.management.relation.RoleNotFoundException


interface UserService {
    fun create(dto: UserDto)
    fun getById(): UserGetDto
    fun isUserActive(username: String): Boolean
    fun findByUsername(username: String): UserAuthDto
    fun existById(id: Long): Boolean
}

@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : UserService {
    override fun create(dto: UserDto) {
        dto.run {
            repository.existsByUsername(username).runIfTrue { throw UserNameAlreadyExistsException(username) }
            val role = roleRepository.findByIdAndDeletedFalse(roleId)
            role?.let {
                val permissionsList = permissionRepository.findAllByIdsAAndDeletedFalse(permissions)
                val user = dto.toEntity(role, permissionsList)
                user.password = passwordEncoder.encode(password)
                repository.save(user)
            }?: throw RoleNotFoundException()
        }
    }

    override fun getById(): UserGetDto = repository.findByIdAndDeletedFalse(userId())?.run { UserGetDto.toDto(this) }
        ?: throw UserNotFoundException( )

    override fun isUserActive(username: String): Boolean {
        return repository.existsByUsernameAndActiveTrueAndDeletedFalse(username)
    }

    override fun findByUsername(username: String): UserAuthDto {
        return repository.findByUsernameAndDeletedFalse(username)?.run { UserAuthDto.toDto(this) }
            ?: throw UserNotFoundException()
    }

    override fun existById(id: Long): Boolean {
        return repository.existsByIdAndDeletedFalse(id)
    }

}