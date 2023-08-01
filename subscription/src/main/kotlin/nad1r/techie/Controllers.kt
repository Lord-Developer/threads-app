package nad1r.techie

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(SubscriptionServiceException::class)
    fun handleException(exception: SubscriptionServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId)
            )
            is UnfollowNotPossibleException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId, exception.followerId)
            )
            is FeignErrorException -> ResponseEntity.badRequest().body(
                BaseMessage(exception.code, exception.errorMessage)
            )
            is GeneralApiException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.msg)
            )
            is FollowerCannotFollowSelfException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.userId)
            )
        }
    }
}

@RestController
class SubscriptionController(
    private val followerService: FollowerService
){

    @PostMapping("/{id}")
    fun subscribe(@PathVariable id: Long) = followerService.follow(id)

    @PutMapping("/{id}")
    fun unsubscribe(@PathVariable id: Long) = followerService.unfollow(id)
}


@RestController
@RequestMapping("internal")
class SubscriptionInternalController(
    private val service: FollowerService
) {

    @GetMapping("/{userId}")
    fun getConnections(@PathVariable userId: Long) = service.getUserConnections(userId)

}


