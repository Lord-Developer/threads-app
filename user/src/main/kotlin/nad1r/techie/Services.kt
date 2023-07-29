package nad1r.techie

import org.springframework.stereotype.Service


interface UserService {
    fun create(dto: UserCreateDto)
    fun getById(id: Long): UserDto
    fun existById(id: Long): Boolean
}

@Service
class UserServiceImpl(
    private val repository: UserRepository
) : UserService {
    override fun create(dto: UserCreateDto) {
        dto.run {
            repository.existsByEmail(email).runIfTrue { throw EmailAlreadyExistsException(email) }
            repository.existsByUsername(username).runIfTrue { throw UserNameAlreadyExistsException(username) }
            repository.save(dto.toEntity())
        }
    }

    override fun getById(id: Long) = repository.findByIdAndDeletedFalse(id)?.run { UserDto.from(this) }
        ?: throw UserNotFoundException(id)

    override fun existById(id: Long): Boolean {
        return repository.existsByIdAndDeletedFalse(id)
    }

}