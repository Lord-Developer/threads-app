package nad1r.techie

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(UserServiceException::class)
    fun handleException(exception: UserServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId)
            )
            is EmailAlreadyExistsException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, listOf(exception.email))
            )
            is UserNameAlreadyExistsException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, listOf(exception.userName))
            )
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        val validationErrors = ex.bindingResult.fieldErrors.map { it.defaultMessage }
        val response = mapOf("status" to "400 Bad Request", "errors" to validationErrors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}


@RestController
@Validated
class UserController(private val service: UserService) {

    @PostMapping
    fun create(@RequestBody @Valid dto: UserCreateDto) = service.create(dto)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)
}

@RestController
@RequestMapping("internal")
class UserInternalController(private val service: UserService) {

    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)
}