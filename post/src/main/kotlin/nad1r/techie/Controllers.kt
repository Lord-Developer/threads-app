package nad1r.techie

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(PostServiceException::class)
    fun handleException(exception: PostServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.authorId)
            )
            is ReplyNotFoundException ->  ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.replyId)
            )
            is ThreadNotFoundException ->  ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.threadId)
            )
            is FeignErrorException -> ResponseEntity.badRequest().body(
                BaseMessage(exception.code, exception.errorMessage)
            )
            is GeneralApiException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.msg)
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
class ThreadController(
    private val threadService: ThreadService
) {

    @PostMapping("/thread")
    fun createThread(@RequestBody  @Valid dto:  ThreadDto) = threadService.createThread(dto)

    @GetMapping("/thread/{id}")
    fun existsById(@PathVariable id: Long) = threadService.existsById(id)

    @GetMapping("{id}")
    fun getThreadstoUser(@PathVariable id: Long, @PageableDefault(size = 2) pageable: Pageable)
        = threadService.getThreads(id, pageable)

}

@RestController
@Validated
class ReplyController(
    private val replyService: ReplyService
) {
    @PostMapping("/reply")
    fun createReply(@RequestBody @Valid dto: ReplyDto) = replyService.createReply(dto)

    @PutMapping("/reply/{id}")
    fun updateReply(@PathVariable id: Long, @RequestParam(name = "text") text: String) = replyService.editReply(id, text)

    @DeleteMapping("/reply/{id}")
    fun deleteReply(@PathVariable id: Long) = replyService.deleteReply(id)

    @GetMapping("/reply/thread/{id}")
    fun getReplies(@PathVariable id: Long, @PageableDefault(size = 2) pageable: Pageable)
        = replyService.getReplies(id, pageable)

}

@RestController
@Validated
class LikeController(
    private val likeService: LikeService
) {
    @PostMapping("/like")
    fun createLike(@RequestBody @Valid dto: LikeDto) = likeService.like(dto)

    @DeleteMapping("/like/{id}")
    fun deleteLike(@PathVariable id: Long) = likeService.unlike(id)

    @GetMapping("/like/count/{id}")
    fun getLikeCount(@PathVariable id: Long, @RequestParam(name = "likeType") likeType: LikeType)
        = likeService.getLikeCount(id,  likeType)
}

