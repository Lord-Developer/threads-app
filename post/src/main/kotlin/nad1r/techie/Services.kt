package nad1r.techie

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.persistence.EntityManager

@FeignClient(name = "user", configuration = [Auth2TokenConfiguration::class])
interface UserService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean

    @GetMapping
    fun getUserById(): UserDto
}

@FeignClient(name = "subscription")
interface SubscriptionService {
    @GetMapping("internal/{userId}")
    fun getUserConnections(@PathVariable userId: Long): List<Long>
}


interface ThreadService {
    fun createThread(dto: ThreadDto)
    fun getThreads( pageable: Pageable): Page<ThreadDto>
    fun existsById(id: Long): Boolean
}

interface LikeService {
    fun like(dto: LikeDto)
    fun unlike(likeId: Long)
    fun getLikeCount(likeId: Long, likeType: LikeType): Int
}

interface ReplyService {
    fun createReply(dto: ReplyDto)
    fun editReply(replyId: Long, content: String)
    fun deleteReply(replyId: Long)
    fun getReplies(threadId: Long, pageable: Pageable): Page<ReplyDto>
}


@Service
class ThreadServiceImpl(
    private val repository: ThreadRepository,
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,
    private val userThreadReadRepository: UserThreadReadRepository
) : ThreadService {
    override fun createThread(dto: ThreadDto) {
        repository.save(dto.toEntity())
    }

    override fun existsById(id: Long) = repository.existsByIdAndDeletedFalse(id)

    override fun getThreads( pageable: Pageable): Page<ThreadDto> {
        val connections = subscriptionService.getUserConnections(userId())

        lateinit var userThreads: MutableList<UserThreadRead>
        val threadDtoPage = repository.findAllUnreadTweet(connections, userId(), pageable).map { thread ->
            userThreads.add(UserThreadRead(userId(), thread))
            ThreadDto(
                thread.id,
                thread.text,
                thread.authorId
            )
        }
        userThreadReadRepository.saveAll(userThreads)
        return threadDtoPage
    }
}


@Service
class LikeServiceImpl(
    private val repository: LikeRepository,
    private val replyRepository: ReplyRepository,
    private val threadRepository: ThreadRepository
) : LikeService {
    override fun like(dto: LikeDto) {
        dto.run {
           when(likeType) {
                LikeType.TO_REPLY -> replyRepository.existsByIdAndDeletedFalse(toLikeId)
                    .runIfFalse { throw ReplyNotFoundException(toLikeId) }


                LikeType.TO_THREAD -> threadRepository.existsByIdAndDeletedFalse(toLikeId)
                    .runIfFalse { throw ThreadNotFoundException(toLikeId) }

            }
            val like = repository.findByAuthorIdAndAndToLikeIdAndLikeType(userId(), toLikeId,  likeType)
            like?.let {
                it.deleted = false
                repository.save(it)
            }?: repository.save(dto.toEntity())

        }
    }

    override fun unlike(likeId: Long) {
        repository.trash(likeId)
    }

    override fun getLikeCount(likeId: Long, likeType: LikeType): Int {
        return repository.countByToLikeIdAndLikeTypeAndDeletedFalse(likeId, likeType)
    }
}

@Service
class ReplyServiceImpl(
    private val repository: ReplyRepository,
    private val threadRepository: ThreadRepository,
    private val entityManager: EntityManager
) : ReplyService {
    override fun createReply(dto: ReplyDto) {
        dto.run {
            threadRepository.existsByIdAndDeletedFalse(threadId).runIfFalse { throw ThreadNotFoundException(threadId) }
            val thread = entityManager.getReference(Thread::class.java, threadId)
            repository.save(dto.toEntity(thread))
        }
    }

    override fun editReply(replyId: Long, content: String) {
        val reply = repository.findByIdAndDeletedFalse(replyId)
        reply?.let {
            it.text = content
            repository.save(it)
        }?: throw ReplyNotFoundException(replyId)
    }

    override fun deleteReply(replyId: Long) {
        repository.trash(replyId)
    }

    override fun getReplies(threadId: Long, pageable: Pageable): Page<ReplyDto> {
        return repository.findAllByThreadIdAndDeletedFalse(threadId, pageable).map {
            ReplyDto(
                it.id,
                it.text,
                it.authorId,
                it.thread.id!!
            )
        }
    }
}

